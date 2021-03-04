from __future__ import absolute_import
from __future__ import division
from __future__ import print_function
import os
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import uuid
import random
import time
import six
import sys
import functools
import math
import paddle
import paddle.fluid as fluid
import paddle.dataset.flowers as flowers
import argparse
import functools
import subprocess
import codecs
import json
import distutils.util
from paddle.fluid import core
from paddle.fluid.initializer import MSRA
from paddle.fluid.param_attr import ParamAttr
from PIL import Image, ImageEnhance
import logging
# 定义网络结构 设定网络结构，此处定义了三个常用的网络结构
# resnet mobile-net vgg-net

class ResNet():
    def __init__(self, layers=50):
        self.layers = layers
        
    def name(self):
        return 'resnet'

    def net(self, input, class_dim=1000):
        layers = self.layers
        supported_layers = [50, 101, 152]
        assert layers in supported_layers, \
            "supported layers are {} but input layer is {}".format(supported_layers, layers)

        if layers == 50:
            depth = [3, 4, 6, 3]
        elif layers == 101:
            depth = [3, 4, 23, 3]
        elif layers == 152:
            depth = [3, 8, 36, 3]
        num_filters = [64, 128, 256, 512]

        conv = self.conv_bn_layer(
            input=input,
            num_filters=64,
            filter_size=7,
            stride=2,
            act='relu',
            name="conv1")
        conv = fluid.layers.pool2d(
            input=conv,
            pool_size=3,
            pool_stride=2,
            pool_padding=1,
            pool_type='max')

        for block in range(len(depth)):
            for i in range(depth[block]):
                if layers in [101, 152] and block == 2:
                    if i == 0:
                        conv_name = "res" + str(block + 2) + "a"
                    else:
                        conv_name = "res" + str(block + 2) + "b" + str(i)
                else:
                    conv_name = "res" + str(block + 2) + chr(97 + i)
                conv = self.bottleneck_block(
                    input=conv,
                    num_filters=num_filters[block],
                    stride=2 if i == 0 and block != 0 else 1,
                    name=conv_name)

        pool = fluid.layers.pool2d(
            input=conv, pool_size=7, pool_type='avg', global_pooling=True)
        stdv = 1.0 / math.sqrt(pool.shape[1] * 1.0)
        fc = fluid.layers.fc(
            input=pool,
            size=4096,
            act='relu',
            param_attr=fluid.param_attr.ParamAttr(
                initializer=fluid.initializer.Normal(scale=0.005)),
            bias_attr=fluid.param_attr.ParamAttr(
                initializer=fluid.initializer.Constant(value=0.1)))
        fc = fluid.layers.dropout(x=fc, dropout_prob=0.5)
        out = fluid.layers.fc(input=fc,
                              size=class_dim,
                              act='softmax', 
                              param_attr=fluid.param_attr.ParamAttr(
                                  initializer=fluid.initializer.Uniform(-stdv,
                                                                        stdv)))
        return out

    def conv_bn_layer(self,
                      input,
                      num_filters,
                      filter_size,
                      stride=1,
                      groups=1,
                      act=None,
                      name=None):
        conv = fluid.layers.conv2d(
            input=input,
            num_filters=num_filters,
            filter_size=filter_size,
            stride=stride,
            padding=(filter_size - 1) // 2,
            groups=groups,
            act=None,
            param_attr=ParamAttr(name=name + "_weights"),
            bias_attr=False,
            name=name + '.conv2d.output.1')
        if name == "conv1":
            bn_name = "bn_" + name
        else:
            bn_name = "bn" + name[3:]
        return fluid.layers.batch_norm(
            input=conv,
            act=act,
            name=bn_name + '.output.1',
            param_attr=ParamAttr(name=bn_name + '_scale'),
            bias_attr=ParamAttr(bn_name + '_offset'),
            moving_mean_name=bn_name + '_mean',
            moving_variance_name=bn_name + '_variance', )

    def shortcut(self, input, ch_out, stride, name):
        ch_in = input.shape[1]
        if ch_in != ch_out or stride != 1:
            return self.conv_bn_layer(input, ch_out, 1, stride, name=name)
        else:
            return input

    def bottleneck_block(self, input, num_filters, stride, name):
        conv0 = self.conv_bn_layer(
            input=input,
            num_filters=num_filters,
            filter_size=1,
            act='relu',
            name=name + "_branch2a")
        conv1 = self.conv_bn_layer(
            input=conv0,
            num_filters=num_filters,
            filter_size=3,
            stride=stride,
            act='relu',
            name=name + "_branch2b")
        conv2 = self.conv_bn_layer(
            input=conv1,
            num_filters=num_filters * 4,
            filter_size=1,
            act=None,
            name=name + "_branch2c")

        short = self.shortcut(
            input, num_filters * 4, stride, name=name + "_branch1")

        return fluid.layers.elementwise_add(
            x=short, y=conv2, act='relu', name=name + ".add.output.5")
            
class VGGNet():
    def __init__(self, layers=16):
        self.layers = layers
        
    def name(self):
        return 'vgg-net'

    def net(self, input, class_dim=1000):
        layers = self.layers
        vgg_spec = {
            11: ([1, 1, 2, 2, 2]),
            13: ([2, 2, 2, 2, 2]),
            16: ([2, 2, 3, 3, 3]),
            19: ([2, 2, 4, 4, 4])
        }
        assert layers in vgg_spec.keys(), \
            "supported layers are {} but input layer is {}".format(vgg_spec.keys(), layers)

        nums = vgg_spec[layers]
        conv1 = self.conv_block(input, 64, nums[0])
        conv2 = self.conv_block(conv1, 128, nums[1])
        conv3 = self.conv_block(conv2, 256, nums[2])
        conv4 = self.conv_block(conv3, 512, nums[3])
        conv5 = self.conv_block(conv4, 512, nums[4])

        fc_dim = 4096
        fc1 = fluid.layers.fc(
            input=conv5,
            size=fc_dim,
            act='relu',
            param_attr=fluid.param_attr.ParamAttr(
                initializer=fluid.initializer.Normal(scale=0.005)),
            bias_attr=fluid.param_attr.ParamAttr(
                initializer=fluid.initializer.Constant(value=0.1)))
        fc1 = fluid.layers.dropout(x=fc1, dropout_prob=0.5)
        fc2 = fluid.layers.fc(
            input=fc1,
            size=fc_dim,
            act='relu',
            param_attr=fluid.param_attr.ParamAttr(
                initializer=fluid.initializer.Normal(scale=0.005)),
            bias_attr=fluid.param_attr.ParamAttr(
                initializer=fluid.initializer.Constant(value=0.1)))
        fc2 = fluid.layers.dropout(x=fc2, dropout_prob=0.5)
        out = fluid.layers.fc(
            input=fc2,
            size=class_dim,
            act='softmax',
            param_attr=fluid.param_attr.ParamAttr(
                initializer=fluid.initializer.Normal(scale=0.005)),
            bias_attr=fluid.param_attr.ParamAttr(
                initializer=fluid.initializer.Constant(value=0.1)))

        return out

    def conv_block(self, input, num_filter, groups):
        conv = input
        for i in range(groups):
            if i == groups - 1:
                act = None
            else:
                act = 'relu'
            conv = fluid.layers.conv2d(
                input=conv,
                num_filters=num_filter,
                filter_size=3,
                stride=1,
                padding=1,
                act=act,
                param_attr=fluid.param_attr.ParamAttr(
                    initializer=fluid.initializer.Normal(scale=0.01)),
                bias_attr=fluid.param_attr.ParamAttr(
                    initializer=fluid.initializer.Constant(value=0.0)))
        conv = fluid.layers.batch_norm(input=conv, act='relu')
        return fluid.layers.pool2d(input=conv, pool_size=2, pool_type='max', pool_stride=2)
        
class MobileNet():
    def __init__(self):
        pass
    
    def name(self):
        return 'mobile-net'

    def net(self, input, class_dim=1000, scale=1.0):
        # conv1: 112x112
        input = self.conv_bn_layer(
            input,
            filter_size=3,
            num_filters=int(32 * scale),
            stride=2,
            padding=1)

        # 56x56
        input = self.depthwise_separable(
            input,
            num_filters1=32,
            num_filters2=64,
            num_groups=32,
            stride=1,
            scale=scale)

        input = self.depthwise_separable(
            input,
            num_filters1=64,
            num_filters2=128,
            num_groups=64,
            stride=2,
            scale=scale)

        # 28x28
        input = self.depthwise_separable(
            input,
            num_filters1=128,
            num_filters2=128,
            num_groups=128,
            stride=1,
            scale=scale)

        input = self.depthwise_separable(
            input,
            num_filters1=128,
            num_filters2=256,
            num_groups=128,
            stride=2,
            scale=scale)

        # 14x14
        input = self.depthwise_separable(
            input,
            num_filters1=256,
            num_filters2=256,
            num_groups=256,
            stride=1,
            scale=scale)

        input = self.depthwise_separable(
            input,
            num_filters1=256,
            num_filters2=512,
            num_groups=256,
            stride=2,
            scale=scale)

        # 14x14
        for i in range(5):
            input = self.depthwise_separable(
                input,
                num_filters1=512,
                num_filters2=512,
                num_groups=512,
                stride=1,
                scale=scale)
        module1 = input
        # 7x7
        input = self.depthwise_separable(
            input,
            num_filters1=512,
            num_filters2=1024,
            num_groups=512,
            stride=2,
            scale=scale)

        input = self.depthwise_separable(
            input,
            num_filters1=1024,
            num_filters2=1024,
            num_groups=1024,
            stride=1,
            scale=scale)

        # class_dim x 1
        input = paddle.fluid.layers.conv2d(
            input,
            num_filters=class_dim,
            filter_size=1,
            stride=1)

        pool = fluid.layers.pool2d(
            input=input,
            pool_size=0,
            pool_stride=1,
            pool_type='avg',
            global_pooling=True)

        output = fluid.layers.fc(input=pool,
                              size=class_dim,
                              act='softmax', 
                              param_attr=ParamAttr(initializer=MSRA()))
        
        return output

    def conv_bn_layer(self,
                      input,
                      filter_size,
                      num_filters,
                      stride,
                      padding,
                      num_groups=1,
                      act='relu',
                      use_cudnn=True):
        conv = fluid.layers.conv2d(
            input=input,
            num_filters=num_filters,
            filter_size=filter_size,
            stride=stride,
            padding=padding,
            groups=num_groups,
            act=None,
            use_cudnn=use_cudnn,
            param_attr=ParamAttr(initializer=MSRA()),
            bias_attr=False)
        return fluid.layers.batch_norm(input=conv, act=act)

    def depthwise_separable(self, input, num_filters1, num_filters2, num_groups,
                            stride, scale):
        depthwise_conv = self.conv_bn_layer(
            input=input,
            filter_size=3,
            num_filters=int(num_filters1 * scale),
            stride=stride,
            padding=1,
            num_groups=int(num_groups * scale),
            use_cudnn=True)

        pointwise_conv = self.conv_bn_layer(
            input=depthwise_conv,
            filter_size=1,
            num_filters=int(num_filters2 * scale),
            stride=1,
            padding=0)
        return pointwise_conv
        
class DenseNet(): 
    def __init__(self, layers, dropout_prob):
        self.layers = layers
        self.dropout_prob = dropout_prob
    def name(self):
        return 'dense-net'
    def bottleneck_layer(self, input, fliter_num, name):
        bn = fluid.layers.batch_norm(input=input, act='relu', name=name + '_bn1')
        conv1 = fluid.layers.conv2d(input=bn, num_filters=fliter_num * 4, filter_size=1, name=name + '_conv1')
        dropout = fluid.layers.dropout(x=conv1, dropout_prob=self.dropout_prob)

        bn = fluid.layers.batch_norm(input=dropout, act='relu', name=name + '_bn2')
        conv2 = fluid.layers.conv2d(input=bn, num_filters=fliter_num, filter_size=3, padding=1, name=name + '_conv2')
        dropout = fluid.layers.dropout(x=conv2, dropout_prob=self.dropout_prob)

        return dropout

    def dense_block(self, input, block_num, fliter_num, name):
        layers = []
        layers.append(input)#拼接到列表

        x = self.bottleneck_layer(input, fliter_num, name=name + '_bottle_' + str(0))
        layers.append(x)
        for i in range(block_num - 1):
            x = paddle.fluid.layers.concat(layers, axis=1)
            x = self.bottleneck_layer(x, fliter_num, name=name + '_bottle_' + str(i + 1))
            layers.append(x)

        return paddle.fluid.layers.concat(layers, axis=1)

    def transition_layer(self, input, fliter_num, name):
        bn = fluid.layers.batch_norm(input=input, act='relu', name=name + '_bn1')
        conv1 = fluid.layers.conv2d(input=bn, num_filters=fliter_num, filter_size=1, name=name + '_conv1') 
        dropout = fluid.layers.dropout(x=conv1, dropout_prob=self.dropout_prob)
        
        return fluid.layers.pool2d(input=dropout, pool_size=2, pool_type='avg', pool_stride=2)
 
    def net(self, input, class_dim=1000): 

        layer_count_dict = {
            121: (32, [6, 12, 24, 16]),
            169: (32, [6, 12, 32, 32]),
            201: (32, [6, 12, 48, 32]),
            161: (48, [6, 12, 36, 24])
        }
        layer_conf = layer_count_dict[self.layers]

        conv = fluid.layers.conv2d(input=input, num_filters=layer_conf[0] * 2, 
            filter_size=7, stride=2, padding=3, name='densenet_conv0')
        conv = fluid.layers.pool2d(input=conv, pool_size=3, pool_padding=1, pool_type='max', pool_stride=2)
        for i in range(len(layer_conf[1]) - 1):
            conv = self.dense_block(conv, layer_conf[1][i], layer_conf[0], 'dense_' + str(i))
            conv = self.transition_layer(conv, layer_conf[0], name='trans_' + str(i))

        conv = self.dense_block(conv, layer_conf[1][-1], layer_conf[0], 'dense_' + str(len(layer_conf[1])))
        conv = fluid.layers.pool2d(input=conv, global_pooling=True, pool_type='avg')
        out = fluid.layers.fc(conv, class_dim, act='softmax')
        # last fc layer is "out" 
        return out
        
class InceptionV4():
    def __init__(self):
        pass
    
    def name(self):
        return 'InceptionV4'

    def net(self, input, class_dim=1000):
        x = self.inception_stem(input)

        for i in range(4):
            x = self.inceptionA(x, name=str(i + 1))
        x = self.reductionA(x)

        for i in range(7):
            x = self.inceptionB(x, name=str(i + 1))
        x = self.reductionB(x)

        for i in range(3):
            x = self.inceptionC(x, name=str(i + 1))

        pool = fluid.layers.pool2d(
            input=x, pool_size=8, pool_type='avg', global_pooling=True)

        drop = fluid.layers.dropout(x=pool, dropout_prob=0.2)

        stdv = 1.0 / math.sqrt(drop.shape[1] * 1.0)
        out = fluid.layers.fc(
            input=drop,
            size=class_dim,
            act='softmax',
            param_attr=ParamAttr(
                initializer=fluid.initializer.Uniform(-stdv, stdv),
                name="final_fc_weights"),
            bias_attr=ParamAttr(
                initializer=fluid.initializer.Uniform(-stdv, stdv),
                name="final_fc_offset"))
        return out

    def conv_bn_layer(self,
                      data,
                      num_filters,
                      filter_size,
                      stride=1,
                      padding=0,
                      groups=1,
                      act='relu',
                      name=None):
        conv = fluid.layers.conv2d(
            input=data,
            num_filters=num_filters,
            filter_size=filter_size,
            stride=stride,
            padding=padding,
            groups=groups,
            act=None,
            param_attr=ParamAttr(name=name + "_weights"),
            bias_attr=False,
            name=name)
        bn_name = name + "_bn"
        return fluid.layers.batch_norm(
            input=conv,
            act=act,
            name=bn_name,
            param_attr=ParamAttr(name=bn_name + "_scale"),
            bias_attr=ParamAttr(name=bn_name + "_offset"),
            moving_mean_name=bn_name + '_mean',
            moving_variance_name=bn_name + '_variance')

    def inception_stem(self, data, name=None):
        conv = self.conv_bn_layer(
            data, 32, 3, stride=2, act='relu', name="conv1_3x3_s2")
        conv = self.conv_bn_layer(conv, 32, 3, act='relu', name="conv2_3x3_s1")
        conv = self.conv_bn_layer(
            conv, 64, 3, padding=1, act='relu', name="conv3_3x3_s1")

        pool1 = fluid.layers.pool2d(
            input=conv, pool_size=3, pool_stride=2, pool_type='max')
        conv2 = self.conv_bn_layer(
            conv, 96, 3, stride=2, act='relu', name="inception_stem1_3x3_s2")
        concat = fluid.layers.concat([pool1, conv2], axis=1)

        conv1 = self.conv_bn_layer(
            concat, 64, 1, act='relu', name="inception_stem2_3x3_reduce")
        conv1 = self.conv_bn_layer(
            conv1, 96, 3, act='relu', name="inception_stem2_3x3")

        conv2 = self.conv_bn_layer(
            concat, 64, 1, act='relu', name="inception_stem2_1x7_reduce")
        conv2 = self.conv_bn_layer(
            conv2,
            64, (7, 1),
            padding=(3, 0),
            act='relu',
            name="inception_stem2_1x7")
        conv2 = self.conv_bn_layer(
            conv2,
            64, (1, 7),
            padding=(0, 3),
            act='relu',
            name="inception_stem2_7x1")
        conv2 = self.conv_bn_layer(
            conv2, 96, 3, act='relu', name="inception_stem2_3x3_2")

        concat = fluid.layers.concat([conv1, conv2], axis=1)

        conv1 = self.conv_bn_layer(
            concat, 192, 3, stride=2, act='relu', name="inception_stem3_3x3_s2")
        pool1 = fluid.layers.pool2d(
            input=concat, pool_size=3, pool_stride=2, pool_type='max')

        concat = fluid.layers.concat([conv1, pool1], axis=1)

        return concat

    def inceptionA(self, data, name=None):
        pool1 = fluid.layers.pool2d(
            input=data, pool_size=3, pool_padding=1, pool_type='avg')
        conv1 = self.conv_bn_layer(
            pool1, 96, 1, act='relu', name="inception_a" + name + "_1x1")

        conv2 = self.conv_bn_layer(
            data, 96, 1, act='relu', name="inception_a" + name + "_1x1_2")

        conv3 = self.conv_bn_layer(
            data, 64, 1, act='relu', name="inception_a" + name + "_3x3_reduce")
        conv3 = self.conv_bn_layer(
            conv3,
            96,
            3,
            padding=1,
            act='relu',
            name="inception_a" + name + "_3x3")

        conv4 = self.conv_bn_layer(
            data,
            64,
            1,
            act='relu',
            name="inception_a" + name + "_3x3_2_reduce")
        conv4 = self.conv_bn_layer(
            conv4,
            96,
            3,
            padding=1,
            act='relu',
            name="inception_a" + name + "_3x3_2")
        conv4 = self.conv_bn_layer(
            conv4,
            96,
            3,
            padding=1,
            act='relu',
            name="inception_a" + name + "_3x3_3")

        concat = fluid.layers.concat([conv1, conv2, conv3, conv4], axis=1)

        return concat

    def reductionA(self, data, name=None):
        pool1 = fluid.layers.pool2d(
            input=data, pool_size=3, pool_stride=2, pool_type='max')

        conv2 = self.conv_bn_layer(
            data, 384, 3, stride=2, act='relu', name="reduction_a_3x3")

        conv3 = self.conv_bn_layer(
            data, 192, 1, act='relu', name="reduction_a_3x3_2_reduce")
        conv3 = self.conv_bn_layer(
            conv3, 224, 3, padding=1, act='relu', name="reduction_a_3x3_2")
        conv3 = self.conv_bn_layer(
            conv3, 256, 3, stride=2, act='relu', name="reduction_a_3x3_3")

        concat = fluid.layers.concat([pool1, conv2, conv3], axis=1)

        return concat

    def inceptionB(self, data, name=None):
        pool1 = fluid.layers.pool2d(
            input=data, pool_size=3, pool_padding=1, pool_type='avg')
        conv1 = self.conv_bn_layer(
            pool1, 128, 1, act='relu', name="inception_b" + name + "_1x1")

        conv2 = self.conv_bn_layer(
            data, 384, 1, act='relu', name="inception_b" + name + "_1x1_2")

        conv3 = self.conv_bn_layer(
            data, 192, 1, act='relu', name="inception_b" + name + "_1x7_reduce")
        conv3 = self.conv_bn_layer(
            conv3,
            224, (1, 7),
            padding=(0, 3),
            act='relu',
            name="inception_b" + name + "_1x7")
        conv3 = self.conv_bn_layer(
            conv3,
            256, (7, 1),
            padding=(3, 0),
            act='relu',
            name="inception_b" + name + "_7x1")

        conv4 = self.conv_bn_layer(
            data,
            192,
            1,
            act='relu',
            name="inception_b" + name + "_7x1_2_reduce")
        conv4 = self.conv_bn_layer(
            conv4,
            192, (1, 7),
            padding=(0, 3),
            act='relu',
            name="inception_b" + name + "_1x7_2")
        conv4 = self.conv_bn_layer(
            conv4,
            224, (7, 1),
            padding=(3, 0),
            act='relu',
            name="inception_b" + name + "_7x1_2")
        conv4 = self.conv_bn_layer(
            conv4,
            224, (1, 7),
            padding=(0, 3),
            act='relu',
            name="inception_b" + name + "_1x7_3")
        conv4 = self.conv_bn_layer(
            conv4,
            256, (7, 1),
            padding=(3, 0),
            act='relu',
            name="inception_b" + name + "_7x1_3")

        concat = fluid.layers.concat([conv1, conv2, conv3, conv4], axis=1)

        return concat

    def reductionB(self, data, name=None):
        pool1 = fluid.layers.pool2d(
            input=data, pool_size=3, pool_stride=2, pool_type='max')

        conv2 = self.conv_bn_layer(
            data, 192, 1, act='relu', name="reduction_b_3x3_reduce")
        conv2 = self.conv_bn_layer(
            conv2, 192, 3, stride=2, act='relu', name="reduction_b_3x3")

        conv3 = self.conv_bn_layer(
            data, 256, 1, act='relu', name="reduction_b_1x7_reduce")
        conv3 = self.conv_bn_layer(
            conv3,
            256, (1, 7),
            padding=(0, 3),
            act='relu',
            name="reduction_b_1x7")
        conv3 = self.conv_bn_layer(
            conv3,
            320, (7, 1),
            padding=(3, 0),
            act='relu',
            name="reduction_b_7x1")
        conv3 = self.conv_bn_layer(
            conv3, 320, 3, stride=2, act='relu', name="reduction_b_3x3_2")

        concat = fluid.layers.concat([pool1, conv2, conv3], axis=1)

        return concat

    def inceptionC(self, data, name=None):
        pool1 = fluid.layers.pool2d(
            input=data, pool_size=3, pool_padding=1, pool_type='avg')
        conv1 = self.conv_bn_layer(
            pool1, 256, 1, act='relu', name="inception_c" + name + "_1x1")

        conv2 = self.conv_bn_layer(
            data, 256, 1, act='relu', name="inception_c" + name + "_1x1_2")

        conv3 = self.conv_bn_layer(
            data, 384, 1, act='relu', name="inception_c" + name + "_1x1_3")
        conv3_1 = self.conv_bn_layer(
            conv3,
            256, (1, 3),
            padding=(0, 1),
            act='relu',
            name="inception_c" + name + "_1x3")
        conv3_2 = self.conv_bn_layer(
            conv3,
            256, (3, 1),
            padding=(1, 0),
            act='relu',
            name="inception_c" + name + "_3x1")

        conv4 = self.conv_bn_layer(
            data, 384, 1, act='relu', name="inception_c" + name + "_1x1_4")
        conv4 = self.conv_bn_layer(
            conv4,
            448, (1, 3),
            padding=(0, 1),
            act='relu',
            name="inception_c" + name + "_1x3_2")
        conv4 = self.conv_bn_layer(
            conv4,
            512, (3, 1),
            padding=(1, 0),
            act='relu',
            name="inception_c" + name + "_3x1_2")
        conv4_1 = self.conv_bn_layer(
            conv4,
            256, (1, 3),
            padding=(0, 1),
            act='relu',
            name="inception_c" + name + "_1x3_3")
        conv4_2 = self.conv_bn_layer(
            conv4,
            256, (3, 1),
            padding=(1, 0),
            act='relu',
            name="inception_c" + name + "_3x1_3")

        concat = fluid.layers.concat(
            [conv1, conv2, conv3_1, conv3_2, conv4_1, conv4_2], axis=1)

        return concat

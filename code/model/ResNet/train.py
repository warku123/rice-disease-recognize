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


def data_balance():
    lb_dir = 'data/data_new/train_list.txt'
    save_list = 'data/data_new/train_list_balance.txt'
    with open(lb_dir) as f:
        lines = [line.strip() for line in f]
        print(len(lines))
        np.random.shuffle(lines)
        lst = []
        for line in lines:
            dic = {}
            img_name, label = line.split()
            dic['img_name'] = img_name
            dic['label'] = int(label)
            lst.append(dic)
    df = pd.DataFrame(lst)
    file_list = []
    for i in range(train_parameters['_ASSUMED_CLASS_DIM']):
        df1 = df[df['label'] == i][:1400]
        for row in df1.itertuples(index=False):
            file_list.append('\t'.join([row[0], str(row[1])]))
    with open(save_list, 'w') as f:
        for line in file_list:
            f.write(line+'\n')


""" Net def """

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
            "supported layers are {} but input layer is {}".format(
                supported_layers, layers)

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
            "supported layers are {} but input layer is {}".format(
                vgg_spec.keys(), layers)

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
        bn = fluid.layers.batch_norm(
            input=input, act='relu', name=name + '_bn1')
        conv1 = fluid.layers.conv2d(
            input=bn, num_filters=fliter_num * 4, filter_size=1, name=name + '_conv1')
        dropout = fluid.layers.dropout(x=conv1, dropout_prob=self.dropout_prob)

        bn = fluid.layers.batch_norm(
            input=dropout, act='relu', name=name + '_bn2')
        conv2 = fluid.layers.conv2d(
            input=bn, num_filters=fliter_num, filter_size=3, padding=1, name=name + '_conv2')
        dropout = fluid.layers.dropout(x=conv2, dropout_prob=self.dropout_prob)

        return dropout

    def dense_block(self, input, block_num, fliter_num, name):
        layers = []
        layers.append(input)  # 拼接到列表

        x = self.bottleneck_layer(
            input, fliter_num, name=name + '_bottle_' + str(0))
        layers.append(x)
        for i in range(block_num - 1):
            x = paddle.fluid.layers.concat(layers, axis=1)
            x = self.bottleneck_layer(
                x, fliter_num, name=name + '_bottle_' + str(i + 1))
            layers.append(x)

        return paddle.fluid.layers.concat(layers, axis=1)

    def transition_layer(self, input, fliter_num, name):
        bn = fluid.layers.batch_norm(
            input=input, act='relu', name=name + '_bn1')
        conv1 = fluid.layers.conv2d(
            input=bn, num_filters=fliter_num, filter_size=1, name=name + '_conv1')
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
        conv = fluid.layers.pool2d(
            input=conv, pool_size=3, pool_padding=1, pool_type='max', pool_stride=2)
        for i in range(len(layer_conf[1]) - 1):
            conv = self.dense_block(
                conv, layer_conf[1][i], layer_conf[0], 'dense_' + str(i))
            conv = self.transition_layer(
                conv, layer_conf[0], name='trans_' + str(i))

        conv = self.dense_block(
            conv, layer_conf[1][-1], layer_conf[0], 'dense_' + str(len(layer_conf[1])))
        conv = fluid.layers.pool2d(
            input=conv, global_pooling=True, pool_type='avg')
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


""" Tools """

''' 初始化日志 '''


def init_log_config():
    global logger
    logger = logging.getLogger()
    logger.setLevel(logging.INFO)
    log_path = os.path.join(os.getcwd(), 'work/logs')
    if not os.path.exists(log_path):
        os.makedirs(log_path)
    log_name = os.path.join(log_path, 'train.log')
    fh = logging.FileHandler(log_name, mode='w')
    fh.setLevel(logging.DEBUG)
    formatter = logging.Formatter(
        "%(asctime)s - %(filename)s[line:%(lineno)d] - %(levelname)s: %(message)s")
    fh.setFormatter(formatter)
    logger.addHandler(fh)


''' 简单的图像增强函数 '''


def resize_img(img, target_size):
    percent_h = float(target_size[1]) / img.size[1]
    percent_w = float(target_size[2]) / img.size[0]
    percent = min(percent_h, percent_w)
    resized_width = int(round(img.size[0] * percent))
    resized_height = int(round(img.size[1] * percent))
    w_off = (target_size[1] - resized_width) / 2
    h_off = (target_size[2] - resized_height) / 2
    img = img.resize((resized_width, resized_height), Image.LANCZOS)
    array = np.ndarray(
        (target_size[1], target_size[2], target_size[0]), np.uint8)
    array[:, :, 0] = 127.5
    array[:, :, 1] = 127.5
    array[:, :, 2] = 127.5
    ret = Image.fromarray(array)
    ret.paste(img, (int(w_off), int(h_off)))
    return ret


def random_brightness(img):
    prob = np.random.uniform(0, 1)
    if prob < train_parameters['image_distort_strategy']['brightness_prob']:
        brightness_delta = train_parameters['image_distort_strategy']['brightness_delta']
        delta = np.random.uniform(-brightness_delta, brightness_delta) + 1
        img = ImageEnhance.Brightness(img).enhance(delta)
    return img


def random_contrast(img):
    prob = np.random.uniform(0, 1)
    if prob < train_parameters['image_distort_strategy']['contrast_prob']:
        contrast_delta = train_parameters['image_distort_strategy']['contrast_delta']
        delta = np.random.uniform(-contrast_delta, contrast_delta) + 1
        img = ImageEnhance.Contrast(img).enhance(delta)
    return img


def random_saturation(img):
    prob = np.random.uniform(0, 1)
    if prob < train_parameters['image_distort_strategy']['saturation_prob']:
        saturation_delta = train_parameters['image_distort_strategy']['saturation_delta']
        delta = np.random.uniform(-saturation_delta, saturation_delta) + 1
        img = ImageEnhance.Color(img).enhance(delta)
    return img


def random_hue(img):
    prob = np.random.uniform(0, 1)
    if prob < train_parameters['image_distort_strategy']['hue_prob']:
        hue_delta = train_parameters['image_distort_strategy']['hue_delta']
        delta = np.random.uniform(-hue_delta, hue_delta)
        img_hsv = np.array(img.convert('HSV'))
        img_hsv[:, :, 0] = img_hsv[:, :, 0] + delta
        img = Image.fromarray(img_hsv, mode='HSV').convert('RGB')
    return img


def distort_image(img):
    prob = np.random.uniform(0, 1)
    # Apply different distort order
    if prob < 0.25:
        img = random_brightness(img)
        img = random_contrast(img)
        img = random_saturation(img)
        img = random_hue(img)
    elif prob < 0.5:
        img = random_brightness(img)
        img = random_saturation(img)
        img = random_hue(img)
        img = random_contrast(img)
    return img


'''  自定义数据读取器  label_dir 是数据标签的地址 '''


def custom_image_reader(label_dir, data_dir, mode):
    # 获取图片的总张数和类别多少
    with codecs.open(label_dir) as flist:
        lines = [line.strip() for line in flist]
        train_parameters['image_count'] = len(lines)
        np.random.shuffle(lines)
        label_set = set()
        for line in lines:
            img_path, label = line.split()
            label_set.add(label)
        train_parameters['class_dim'] = len(label_set)
        print("class dim:{0} image count:{1}".format(
            train_parameters['class_dim'], train_parameters['image_count']))

    def reader():
        for line in lines:
            if mode == 'train' or mode == 'val':
                img_path, label = line.split()
                img_path = os.path.join(data_dir, img_path)
                img = Image.open(img_path)
                try:
                    if img.mode != 'RGB':
                        img = img.convert('RGB')
                    if train_parameters['image_distort_strategy']['need_distort'] == True:
                        img = distort_image(img)
                    mirror = int(np.random.uniform(0, 2))
                    if mirror == 1:
                        img = img.transpose(Image.FLIP_LEFT_RIGHT)
                    img = resize_img(img, train_parameters['input_size'])
                    # HWC--->CHW && normalized
                    img = np.array(img).astype('float32')
                    img -= train_parameters['mean_rgb']
                    img = img.transpose((2, 0, 1))  # HWC to CHW
                    img *= 0.007843
                    yield img, int(label)
                except Exception as e:
                    pass
            elif mode == 'test':
                img_path = os.path.join(data_dir, line)
                if img.mode != 'RGB':
                    img = img.convert('RGB')
                img = resize_img(img, train_parameters['input_size'])
                yield img

    return reader


# 优化器
def optimizer_momentum_setting():
    """
    阶梯型的学习率适合比较大规模的训练数据
    """
    learning_strategy = train_parameters['momentum_strategy']
    batch_size = train_parameters["train_batch_size"]
    iters = train_parameters["image_count"] // batch_size
    lr = learning_strategy['learning_rate']

    boundaries = [i * iters for i in learning_strategy["lr_epochs"]]
    values = [i * lr for i in learning_strategy["lr_decay"]]
    learning_rate = fluid.layers.piecewise_decay(boundaries, values)
    optimizer = fluid.optimizer.MomentumOptimizer(
        learning_rate=learning_rate, momentum=0.9)
    return optimizer


def optimizer_rms_setting():
    """
    阶梯型的学习率适合比较大规模的训练数据
    """
    batch_size = train_parameters["train_batch_size"]
    iters = train_parameters["image_count"] // batch_size
    learning_strategy = train_parameters['rsm_strategy']
    lr = learning_strategy['learning_rate']

    boundaries = [i * iters for i in learning_strategy["lr_epochs"]]
    values = [i * lr for i in learning_strategy["lr_decay"]]

    optimizer = fluid.optimizer.RMSProp(
        learning_rate=fluid.layers.piecewise_decay(boundaries, values),
        regularization=fluid.regularizer.L2Decay(0.00011))

    return optimizer


def optimizer_sgd_setting():
    """
    loss下降相对较慢，但是最终效果不错，阶梯型的学习率适合比较大规模的训练数据
    """
    learning_strategy = train_parameters['momentum_strategy']
    batch_size = train_parameters["train_batch_size"]
    iters = train_parameters["image_count"] // batch_size
    lr = learning_strategy['learning_rate']

    boundaries = [i * iters for i in learning_strategy["lr_epochs"]]
    values = [i * lr for i in learning_strategy["lr_decay"]]
    learning_rate = fluid.layers.piecewise_decay(boundaries, values)
    optimizer = fluid.optimizer.SGD(learning_rate=learning_rate,
                                    regularization=fluid.regularizer.L2Decay(regularization_coeff=0.1))
    return optimizer


def optimizer_adam_setting():
    """
    能够比较快速的降低 loss，但是相对后期乏力。对于小规模的数据，比较适合
    """
    optimizer = fluid.optimizer.Adam(learning_rate=0.01, regularization=fluid.regularizer.L2Decay(
        regularization_coeff=0.1))
    return optimizer


# 保存模型
def save_model(base_dir, base_name, feed_var_list, target_var_list, program, exe):
    fluid.io.save_persistables(dirname=base_dir,
                               filename=base_name + '-retrain',
                               main_program=program,
                               executor=exe)
    fluid.io.save_inference_model(dirname=base_dir,
                                  params_filename=base_name + '-params',
                                  model_filename=base_name + '-model',
                                  feeded_var_names=feed_var_list,
                                  target_vars=target_var_list,
                                  main_program=program,
                                  executor=exe)

# 调试打印函数，通过 py_func 实现打印参数值的 op，方便调试


def print_func(var):
    logger.info("in py func type: {0}".format(type(var)))
    print(np.array(var))


# 不带类别平衡的 focal loss，仅仅区分类别难易；猜测此时算出来的梯度有一个 gama 倍，所以学习率可以比以往更小一点
def focal_loss(pred, label, gama):
    # 使用打印函数查看当前 Tensor，
    # fluid.layers.py_func(func=print_func, x=pred, out=None)
    one_hot = paddle.fluid.layers.one_hot(label, train_parameters['class_dim'])
    prob = one_hot * pred
    cross_entropy = one_hot * fluid.layers.log(pred)
    cross_entropy = fluid.layers.reduce_sum(cross_entropy, dim=-1)
    sum = paddle.fluid.layers.sum(cross_entropy)
    weight = -1.0 * one_hot * paddle.fluid.layers.pow((1.0 - pred), gama)
    weight = fluid.layers.reduce_sum(weight, dim=-1)
    return weight * cross_entropy


# 用来预测的函数
def read_image(img_path):
    img = Image.open(img_path)
    if img.mode != 'RGB':
        img = img.convert('RGB')
    img = resize_img(img, train_parameters['input_size'])
    img = np.array(img).astype('float32')
    img -= train_parameters['mean_rgb']
    img = img.transpose((2, 0, 1))  # HWC to CHW
    img *= 0.007843
    img = img[np.newaxis, :]
    return img


def infer():
    t1 = time.time()
    # 进行测试
    test_accs = []
    test_costs = []
    batch_reader = fluid.io.batch(validation_reader, batch_size=100)
    for batch_id, data in enumerate(batch_reader()):  # 遍历test_reader
        test_cost, test_acc = exe.run(program=test_program,  # 执行训练程序
                                      feed=feeder.feed(data),  # 喂入数据
                                      fetch_list=[avg_cost, acc_top1])  # fetch 误差、准确率
        test_accs.append(test_acc[0])  # 每个batch的准确率
        test_costs.append(test_cost[0])  # 每个batch的误差
    # 求测试结果的平均值
    test_cost = (sum(test_costs) / len(test_costs))  # 每轮的平均误差
    test_acc = (sum(test_accs) / len(test_accs))  # 每轮的平均准确率
    period = time.time() - t1
    return period/1000, test_cost, test_acc


def valid_reader():
    with codecs.open(valid_label_dir) as f:
        valid_label_df = pd.read_json(f, orient='records')
        valid_label_df.disease_class = valid_label_df.disease_class.astype(
            np.uint8)
        valid_label_df = valid_label_df[valid_label_df.disease_class != 44]
        valid_label_df = valid_label_df[valid_label_df.disease_class != 45]
        valid_label_df.disease_class = valid_label_df.apply(
            lambda r: r['disease_class'] if r['disease_class'] < 44 else r['disease_class']-2,
            axis=1)
        train_parameters['valid_im_count'] = valid_label_df.disease_class.count()
        train_parameters['valid_lb_count'] = len(
            valid_label_df.disease_class.unique())

        items = valid_label_df.to_dict(orient='records')
        del valid_label_df
        np.random.shuffle(items)

    def reader():
        for item in items:
            label, img_path = item['disease_class'], item['image_id']
            img_path = os.path.join(valid_img_dir, img_path)
            img = read_image(img_path)
            yield img, int(label)
    return reader


""" Config """


# 参数设置 设置基础训练参数，例如
# 图片尺寸，注意是 chw 格式 训练数据路径 保存模型的输出路径 训练轮数、训练批次大小 是否使用GPU 学习率变化等 其中类别数量会在读取数据时提前计算，初始为-1，仅用作占位

train_parameters = {
    "_ASSUMED_CLASS_DIM":38,
    # "input_size": [3, 224, 224],
    "input_size": [3, 224, 224],
    "class_dim": -1,
    "data_dir": "data/data_new/train_data",
    "save_model_dir": "work/classification-model",
    "mode": "train",
    "num_epochs": 30,
    "image_count": -1,
    "train_batch_size": 64,
    "mean_rgb": [118, 124, 102],  # [127.5, 127.5, 127.5],
    "use_gpu": True,            # 根据自己的环境，选择适当的设备进行训练
    'valid_im_count': -1,
    'valid_lb_count': -1,
    "image_distort_strategy": {
        "need_distort": False,
        "expand_prob": 0.5,
        "expand_max_ratio": 4,
        "hue_prob": 0.5,
        "hue_delta": 18,
        "contrast_prob": 0.5,
        "contrast_delta": 0.5,
        "saturation_prob": 0.5,
        "saturation_delta": 0.5,
        "brightness_prob": 0.5,
        "brightness_delta": 0.125
    },
    "rsm_strategy": {
        "learning_rate": 0.0001,
        "lr_epochs": [1, 2, 6, 8, 16, 20, 100],
        "lr_decay": [1, 0.5, 0.25, 0.1, 0.05, 0.01, 0.006, 0.003]
    },
    "momentum_strategy": {
        "learning_rate": 0.005,
        "lr_epochs": [20, 40, 60, 80, 100],
        "lr_decay": [1, 0.5, 0.25, 0.1, 0.01, 0.002]
    }
}

init_log_config()
train_prog = fluid.Program()
train_startup = fluid.Program()
print("create prog success")
logger.info("create prog success")
logger.info("build input custom reader and data feeder")
file_list = 'data/data_new/train_list_balance.txt'
valid_img_dir = 'data/data17627/AgriculturalDisease_validationset/images'
valid_label_dir = 'data/data17627/AgriculturalDisease_validationset/AgriculturalDisease_validation_annotations.json'
mode = train_parameters['mode']
batch_reader = paddle.batch(custom_image_reader(file_list, train_parameters['data_dir'], mode),
                            batch_size=train_parameters['train_batch_size'],
                            drop_last=True)


place = fluid.CUDAPlace(0) if train_parameters['use_gpu'] else fluid.CPUPlace()


logger.info("train config:%s", train_parameters)
img = fluid.layers.data(
    name='img', shape=train_parameters['input_size'], dtype='float32')
label = fluid.layers.data(name='label', shape=[1], dtype='int64')
feeder = fluid.DataFeeder(feed_list=[img, label], place=place)

logger.info("build newwork")
# ~~~~~~替换模型在此~~~~~~
model = ResNet(layers=101)
# model = VGGNet(layers=16)
# model = MobileNet()
# model = DenseNet(121, dropout_prob=0.5)
# model = InceptionV4()
out = model.net(input=img, class_dim=train_parameters['class_dim'])

# ??????
one_hot_label = fluid.layers.one_hot(input=label, depth=train_parameters['_ASSUMED_CLASS_DIM'])


smooth_label = fluid.layers.label_smooth(
    label=one_hot_label, epsilon=0.2, dtype="float32")

cost = fluid.layers.cross_entropy(out, smooth_label, soft_label=True)
avg_cost = fluid.layers.mean(x=cost)
acc_top1 = fluid.layers.accuracy(input=out, label=label, k=1)
test_program = fluid.default_main_program().clone(for_test=True)

optimizer = optimizer_rms_setting()
# optimizer = optimizer_momentum_setting()
# optimizer = optimizer_sgd_setting()
# optimizer = optimizer_adam_setting()

optimizer.minimize(avg_cost)
exe = fluid.Executor(place)

# # 读取绘图数据
global all_valid_iters, all_train_iters, all_valid_accs, all_train_accs, all_train_costs

all_train_iter = 0
all_valid_iter = 0
all_train_iters = []
all_train_costs = []
all_train_accs = []
all_valid_accs = []
all_valid_iters = []
all_valid_costs = []

def save_draw_train_process(num_epoches, title, iters, costs, label_cost):
    plt.title(title, fontsize=24)
    plt.xlabel("iter", fontsize=20)
    plt.ylabel("cost", fontsize=20)
    plt.plot(iters, costs, color='red', label=label_cost)
    plt.legend()
    plt.grid()
    # plt.show()
    image_path = str(num_epoches)+title + '.jpg'
    plt.savefig(image_path)


def save_draw_train_valid_acc(num_epoches, title, train_iters, valid_iters, train_accs, valid_accs, label1, label2):
    plt.title(title, fontsize=24)
    plt.xlabel("iter", fontsize=20)
    plt.ylabel("acc", fontsize=20)
    plt.plot(train_iters, train_accs, color='green', label=label1)
    plt.plot(valid_iters, valid_accs, color='red', label=label2)
    plt.legend()
    plt.grid()
    # plt.show()
    image_path = str(num_epoches)+title + '.jpg'
    plt.savefig(image_path)


def read_draw_data():

    if os.path.exists('all_valid_iters.json'):
        with open('all_valid_iters.json', 'r') as f:
            all_valid_iters = json.loads(f.read())

    if os.path.exists('all_valid_iters.json'):
        with open('all_train_iters.json', 'r') as f:
            all_train_iters = json.loads(f.read())

    if os.path.exists('all_valid_iters.json'):
        with open('all_valid_accs.json', 'r') as f:
            all_valid_accs = json.loads(f.read())

    if os.path.exists('all_valid_iters.json'):
        with open('all_train_accs.json', 'r') as f:
            all_train_accs = json.loads(f.read())

    if os.path.exists('all_valid_iters.json'):
        with open('all_train_costs.json', 'r') as f:
            all_train_costs = json.loads(f.read())

    all_train_iter = all_train_iters[-1] if len(all_train_iters) > 0 else []
    all_valid_iters = all_valid_iters[-1] if len(all_valid_iters) > 0 else []


def save_draw_data(epoch_num):
    global all_valid_iters, all_train_iters, all_valid_accs, all_train_accs, all_train_costs
    all_train_accs = [np.float64(i) for i in all_train_accs]
    all_train_costs = [np.float64(i) for i in all_train_costs]

    # _prefix = str(epoch_num)+'_'
    _prefix = ''

    with open(_prefix+'all_valid_iters.json', 'w') as f:
        f.write(json.dumps(all_valid_iters))
    with open(_prefix+'all_train_iters.json', 'w') as f:
        f.write(json.dumps(all_train_iters))
    with open(_prefix+'all_valid_accs.json', 'w') as f:
        f.write(json.dumps(all_valid_accs))
    with open(_prefix+'all_train_accs.json', 'w') as f:
        f.write(json.dumps(all_train_accs))
    with open(_prefix+'all_train_costs.json', 'w') as f:
        f.write(json.dumps(all_train_costs))
    with open(_prefix+'all_valid_costs.json', 'w') as f:
        f.write(json.dumps(all_valid_costs))


main_program = fluid.default_main_program()
exe.run(fluid.default_startup_program())
# 如果有训练过的参数，可以通过打开这句话来加载接着训练

if os.path.exists(train_parameters['save_model_dir']):
    fluid.io.load_persistables(dirname=train_parameters['save_model_dir'], filename=model.name(
    ) + '-final-retrain', main_program=main_program, executor=exe)


""" Train """
train_fetch_list = [avg_cost.name, acc_top1.name, out.name]
validation_reader = valid_reader()
successive_count = 0
stop_train = False
total_batch_count = 0

for pass_id in range(train_parameters["num_epochs"]):
    # 每一次batch重新筛选，打乱
    data_balance()
    batch_reader = paddle.batch(custom_image_reader(
        file_list, train_parameters['data_dir'], mode), batch_size=train_parameters['train_batch_size'], drop_last=True)

    logger.info("current pass: %d, start read image", pass_id)
    batch_id = 0
    for step_id, data in enumerate(batch_reader()):
        t1 = time.time()
        loss, acc1, pred_ot = exe.run(main_program,
                                      feed=feeder.feed(data),
                                      fetch_list=train_fetch_list)
        t2 = time.time()
        batch_id += 1
        total_batch_count += 1
        period = t2 - t1
        loss = np.mean(np.array(loss))
        acc1 = np.mean(np.array(acc1))
        # 记录绘图数据
        all_train_iter = all_train_iter+1
        all_train_iters.append(all_train_iter)
        all_train_costs.append(loss)
        all_train_accs.append(acc1)

        if batch_id % 10 == 0:
            print("Pass {0}, trainbatch {1}, loss {2}, acc1 {3}, time {4}".format(
                pass_id, batch_id, loss, acc1, "%2.2f sec" % period))
            logger.info("Pass {0}, trainbatch {1}, loss {2}, acc1 {3}, time {4}".format(
                pass_id, batch_id, loss, acc1, "%2.2f sec" % period))
            #每训练10个batch, 进行一次测试
            period, valid_cost, valid_acc = infer()
            logger.info(
                f'validation:         loss={valid_cost},acc={valid_acc},time={period}')
            print(
                f'validation:         loss={valid_cost},acc={valid_acc},time={period}')
            all_valid_accs.append(valid_acc)
            all_valid_costs.append(valid_cost)
            all_valid_iter = all_valid_iter+10
            all_valid_iters.append(all_valid_iter)

        if acc1 >= 0.9:
            successive_count += 1
            fluid.io.save_inference_model(dirname=train_parameters['save_model_dir'],
                                          params_filename=model.name() + '-params',
                                          model_filename=model.name() + '-model',
                                          feeded_var_names=['img'],
                                          target_vars=[out],
                                          main_program=main_program,
                                          executor=exe)
            if successive_count >= 5:
                logger.info("end training")
                print("end training")
                stop_train = True
                break
        else:
            successive_count = 0
        if total_batch_count % 200 == 0:
            save_draw_train_valid_acc("Accuracy", all_train_iters, all_valid_iters,
                                      all_train_accs, all_valid_accs, "trainning acc", "validation acc")
            logger.info("temp save {0} batch train result".format(
                total_batch_count))
            print("temp save {0} batch train result".format(total_batch_count))

    if stop_train:
        break

    # 保存绘图数据
    save_draw_data()
    save_model(train_parameters['save_model_dir'], model.name(
    ) + '-final', ['img'], [out], main_program, exe)
    save_draw_train_process("training cost", all_train_iters,
                            all_train_costs, "trainning cost")
    save_draw_train_valid_acc("Accuracy", all_train_iters, all_valid_iters,
                              all_train_accs, all_valid_accs, "trainning acc", "validation acc")

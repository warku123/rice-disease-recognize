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

data_root = "all_data"

train_path = data_root+'/AgriculturalDisease_trainingset/images'
valid_path = data_root+"/AgriculturalDisease_validationset/images"
train_list = os.listdir(train_path)
valid_list = os.listdir(valid_path)
print('AgriculturalDisease_trainingset:', len(train_list))
print('AgriculturalDisease_validationset:', len(valid_list))

train_json_path = data_root + \
    '/AgriculturalDisease_trainingset/AgriculturalDisease_train_annotations.json'
valid_json_path = data_root + \
    '/AgriculturalDisease_validationset/AgriculturalDisease_validation_annotations.json'

with open(train_json_path, encoding='utf-8') as datafile1:
    train_label_df = pd.read_json(datafile1, orient='records')
with open(valid_json_path, encoding='utf-8') as datafile2:
    valid_label_df = pd.read_json(datafile2, orient='records')
# 查看有没有缺失值
print(train_label_df.isnull().sum())
print(valid_label_df.isnull().sum())

# plt.figure(figsize=(16, 8))
# plt.subplot(2, 1, 1)
# train_label_df['disease_class'].value_counts().plot(
#     kind='bar', grid=True, rot=45, alpha=0.8, title='trainset_label_distribute', legend=True)
# plt.subplot(2, 1, 2)
# valid_label_df['disease_class'].value_counts()[-10:-1].plot(kind='bar',
#                                                             grid=True, rot=45, alpha=0.8, title='validationset_label_distribute')
# train_label_df['disease_class'].value_counts()


data_root = "./all_data/"

data_path = data_root+"AgriculturalDisease_trainingset/images"
label_file = data_root + \
    "AgriculturalDisease_trainingset/AgriculturalDisease_train_annotations.json"

save_list = data_root+"aug_data/train_list.txt"
save_path = data_root+"aug_data/train_data"


if not os.path.exists(save_path):
    os.makedirs(save_path)
file_list = []
img_num = 0

with codecs.open(label_file) as f:
    train_label_df = pd.read_json(f, orient='records')
    train_label_df.disease_class = train_label_df.disease_class.astype(
        np.uint8)
    train_label_df = train_label_df[train_label_df.disease_class != 44]
    train_label_df = train_label_df[train_label_df.disease_class != 45]
    train_label_df.disease_class = train_label_df.apply(
        lambda r: r['disease_class'] if r['disease_class'] < 44 else r['disease_class']-2,
        axis=1)
    items = train_label_df.to_dict(orient='records')
    label_count = train_label_df['disease_class'].value_counts()
    del train_label_df


print(len(items))

# 数据增强

for item in items:
    img_list = []
    img_path, label = item['image_id'], item['disease_class']
    img_path = os.path.join(data_path, img_path)
    img = Image.open(img_path).convert('RGB')
    rn = [0.5, 0.8, 1.2, 1.5]
    img_list.append(img)
    # 这个看上去……有不同的权重是吗
    if(label_count[label] < 1000):
        for e in rn:
            img_list.append(ImageEnhance.Brightness(img).enhance(e))
            img_list.append(ImageEnhance.Contrast(img).enhance(e))
            img_list.append(ImageEnhance.Color(img).enhance(e))
            img_list.append(ImageEnhance.Sharpness(img).enhance(e))
        if(label_count[label] < 100):
            img = img.transpose(Image.FLIP_LEFT_RIGHT)
            img_list.append(img)
            for e in rn:
                img_list.append(ImageEnhance.Brightness(img).enhance(e))
                img_list.append(ImageEnhance.Contrast(img).enhance(e))
                img_list.append(ImageEnhance.Color(img).enhance(e))
                img_list.append(ImageEnhance.Sharpness(img).enhance(e))

    for step, image in enumerate(img_list):
        img_name = "-".join([str(img_num), str(step)]) + '.jpg'
        file_list.append('\t'.join([img_name, str(label)]))
        image.save(os.path.join(save_path, img_name))

    # print(len(img_list))

    img_num += 1

    if img_num % 20 == 0:
        print(img_num)

""" 数据处理 """

with open(save_list, 'w') as f:
    _count = 0
    for line in file_list:
        _count = _count + 1
        if _count % 1000 == 0:
            print(_count)
        f.write(line+'\n')

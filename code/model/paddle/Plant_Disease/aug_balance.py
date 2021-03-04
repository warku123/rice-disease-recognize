from __future__ import absolute_import, division, print_function

import argparse
import codecs
import distutils.util
import functools
import json
import logging
import math
import os
import random
import subprocess
import sys
import time
import uuid

import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
import six
from PIL import Image, ImageEnhance

import paddle
import paddle.dataset.flowers as flowers
import paddle.fluid as fluid
from paddle.fluid import core
from paddle.fluid.initializer import MSRA
from paddle.fluid.param_attr import ParamAttr


# 标签平衡,下采样,每个标签选出800张图片  生成新的train_list_balance
def data_balance(data_root):
    lb_dir = data_root+'train_list.txt'
    save_list = data_root+'train_list_balance.txt'
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
    for i in range(59):
        df1 = df[df['label'] == i][:1400]
        for row in df1.itertuples(index=False):
            file_list.append('\t'.join([row[0], str(row[1])]))
    with open(save_list, 'w') as f:
        for line in file_list:
            f.write(line+'\n')


if __name__ == "__main__":
    data_root = "./all_data/aug_data/"
    data_balance(data_root=data_root)

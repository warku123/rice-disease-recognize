import paddle.fluid as fluid
import paddle
import reader
from net import ResNet
import numpy as np
import os
from config import train_parameters, init_train_parameters
from PIL import Image


def resize_img(img, target_size):
    """
    强制缩放图片
    :param img:
    :param target_size:
    :return:
    """
    img = img.resize((target_size[1], target_size[2]), Image.BILINEAR)
    return img

def read_img(img_path = "./infer.path"):
    img = Image.open(img_path)
    if img.mode != 'RGB':
        img = img.convert('RGB')
    img = resize_img(img, train_parameters['input_size'])
    # HWC--->CHW && normalized
    img = np.array(img).astype('float32')
    img -= train_parameters['mean_rgb']
    img = img.transpose((2, 0, 1))  # HWC to CHW
    img *= 0.007843  # 像素值归一化
    img = np.array([img]).astype('float32')
    return img
    
def infer(image_path = None,image = None):
    if image is None:
        if image_path is not None:
            image = read_img(image_path)
        else:
            print("没有提供图片路径")

    with fluid.dygraph.guard():
        net = ResNet("resnet", class_dim = train_parameters['class_dim'])
        # load checkpoint
        model_dict, _ = fluid.dygraph.load_dygraph(train_parameters["save_persistable_dir"])
        net.load_dict(model_dict)
        print("checkpoint loaded")

        # start evaluate mode
        net.eval()
        
        label_dict = train_parameters["label_dict"]
        label_dict = {v: k for k, v in label_dict.items()}

        results = net(fluid.dygraph.to_variable(image)).numpy()[0]
        print("results",results)
        sorted_idx = np.argsort(results)[::-1][:-1]
        print("sorted_idx",sorted_idx)
        prob_dict = {
            label_dict[idx]:results[idx] for idx in sorted_idx
        }
        print(prob_dict)

        print("image {} Infer result is: {}".format(image_path,label_dict[sorted_idx[0]]))


if __name__ == "__main__":
    init_train_parameters()
    infer(image_path="./infer.jpg")
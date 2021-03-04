import codecs
import os
import random
import shutil
from PIL import Image

train_ratio = 4.0 / 5

raw_folders = './plant_data'
new_file_dir = './processed_data'

class_list = [c for c in os.listdir(raw_folders) if os.path.isdir(os.path.join(
    raw_folders, c)) and not c.endswith('Set') and not c.startswith('.')]
class_list.sort()
print(class_list)

train_image_dir = os.path.join(new_file_dir, "trainImageSet")
if not os.path.exists(train_image_dir):
    os.makedirs(train_image_dir)

eval_image_dir = os.path.join(new_file_dir, "evalImageSet")
if not os.path.exists(eval_image_dir):
    os.makedirs(eval_image_dir)

train_file = codecs.open(os.path.join(new_file_dir, "train.txt"), 'w')
eval_file = codecs.open(os.path.join(new_file_dir, "eval.txt"), 'w')

with codecs.open(os.path.join(new_file_dir, "label_list.txt"), "w") as label_list:
    label_id = 0
    image_count = 1
    for class_dir in class_list:
        # 打开每一个文件夹
        label_list.write("{0}\t{1}\n".format(label_id, class_dir))
        raw_image_dir = os.path.join(raw_folders, class_dir)
        for raw_image_name in os.listdir(raw_image_dir):
            # 每一个文件
            try:
                raw_img_file = os.path.join(raw_image_dir, raw_image_name)
                img = Image.open(raw_img_file)

                train_cat_file = os.path.join(train_image_dir, str(
                    label_id)+"_"+str(image_count) + ".jpg")
                eval_cat_file = os.path.join(eval_image_dir, str(
                    label_id)+"_"+str(image_count) + ".jpg")
                if random.uniform(0, 1) <= train_ratio:
                    shutil.copyfile(raw_img_file, train_cat_file)
                    train_file.write("{0}\t{1}\n".format(
                        train_cat_file, label_id))
                else:
                    shutil.copyfile(raw_img_file, eval_cat_file)
                    eval_file.write("{0}\t{1}\n".format(
                        eval_cat_file, label_id))
                image_count += 1
            except Exception as e:
                print(raw_img_file)
                # 存在一些文件打不开，此处需要稍作清洗
        label_id += 1
        

train_file.close()
eval_file.close()

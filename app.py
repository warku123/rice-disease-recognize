from flask import Flask
from flask import request
from werkzeug.utils import secure_filename
from PIL import Image
# import cv2
# import matplotlib.pyplot as plt

import os
from ResNet_Code import infer
from ResNet_Code.net import ResNet
from ResNet_Code.config import train_parameters, init_train_parameters
# from ResNet_Code.config import train_parameters

model = None

# init diease_dic 
label_info = {}

def get_info_from_mysql():
    # python操作mysql数据库
    import pymysql

    # 查询数据库中数据表中的信息
    # 1.连接mysql数据库
    conn = pymysql.connect(
        host="localhost", port=3306,
        user="root", password="123456",
        database="PreventionInformation", charset="utf8"
    )

    # 判断连接是否成功
    if not conn:
        print("连接数据库失败！")
    else:
        print("连接数据库成功！")

    # 2.获取cursor对象
    cursor = conn.cursor()
    # 借助cursor查询数据库
    sql = "select * from Prevention"
    count = cursor.execute(sql)
    print(f"查询到了{count}条病虫害数据！")
    # 获取查询到的数据的详细信息
    result = cursor.fetchall()
    # 元组类型的数据
    # print(result)

    diease_dic = {}

    # 循环读取所有数据
    for item in result:
        # 以####分割
        record = item[0] + "####" + item[1]
        # TODO 循环的次数根据数据表结构确定
        for i in range(len(item)-2):
            with open(item[i+2],mode = 'r',encoding = 'utf8') as f:
                record = record + "####" + f.read()
        # print(record)
        # 将数据存入字典
        diease_dic[item[0]] = record

    # print(diease_dic)

    # 资源释放
    cursor.close()
    conn.close()

    return diease_dic


app = Flask(__name__)

@app.route('/')
def index():
    return 'Index Page'

@app.route('/hello')
def hello():
    return 'Hello, World'


@app.route('/upload', methods=['GET', 'POST'])
def upload_file():
    if request.method == 'POST':
        # with open(r"/home/team4980/rice-disease-recognize/uploads/osdata.txt","w") as f:    
        #     f.write("request.form:\r\n")
        #     f.write(str(request.form))
        #     f.write("\r\nrequest.args:\r\n")
        #     f.write(str(request.args))
        #     f.write("\r\nrequest.values:\r\n")
        #     f.write(str(request.values))
        #     f.write("\r\nrequest.data:\r\n")
        #     f.write(str(request.data))
        #     f.write("\r\nrequest.json:\r\n")
        #     f.write(str(request.json))
        #     f.write("\r\nrequest.files:\r\n")
        #     f.write(str(request.files))
        img_obj = request.files['bitmap']
        # print(img_obj)
        save_path = './uploads/' + secure_filename(img_obj.filename)
        img_obj.save(save_path)
        img = Image.open(img_obj)
        # print(img)
        # print(infer.infer(image_path = save_path))
        print(label_info[infer.live_model_infer(model = model, image = save_path)])
        return label_info[infer.live_model_infer(model = model, image = save_path)]

if __name__ == '__main__':
    # infer.infer("data/infer.jpg")
    # init parameters
    init_train_parameters()
    # load live model
    model = infer.load_model()
    # TODO Load info from Database, although retrieving is quick enough.
    label_info = get_info_from_mysql()

    ## for test
    # result1 = infer.live_model_infer(model = model, image = "./uploads/bitmap.bmp")
    # print(result1)
    # result2 = infer.live_model_infer(model = model, image = "./data/infer2.jpg")
    # print(result2)

    # While
    app.run(debug=True,host='0.0.0.0',port=80)

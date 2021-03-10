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
init_train_parameters()

model = None

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
        print(img_obj)
        save_path = './uploads/' + secure_filename(img_obj.filename)
        img_obj.save(save_path)
        img = Image.open(img_obj)
        print(img)
        # print(infer.infer(image_path = save_path))
        return infer.live_model_infer(model = model, imagepath = save_path)

if __name__ == '__main__':
    # infer.infer("data/infer.jpg")
    model = infer.load_model()
    result1 = infer.live_model_infer(model = model, image = "./uploads/bitmap.bmp")
    print(result1)
    # result2 = infer.live_model_infer(model = model, image = "./data/infer2.jpg")
    # print(result2)
    app.run(debug=True,host='0.0.0.0',port=80)

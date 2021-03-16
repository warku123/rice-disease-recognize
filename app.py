from flask import Flask
from flask import request, Response, jsonify
from werkzeug.utils import secure_filename
from PIL import Image

from io import BytesIO, BufferedWriter
import json
# import cv2
# import matplotlib.pyplot as plt

import os
from ResNet_Code import infer
from ResNet_Code.net import ResNet
from ResNet_Code.config import train_parameters, init_train_parameters

# from utils.sql_helper import Sql_Helper

from tools import db_operations, app_tools

import base64

app = Flask(__name__)

@app.route('/')
def index():
    '''
    Index Page test
    :return:    string'Index Page'
    '''
    return 'Index Page'

@app.route('/hello')
def hello():
    '''
    hello test
    :return:
    '''
    return 'Hello, World'

@app.route('/get',methods=['GET', 'POST'])
def get():
    '''
    get test
    :return:    test result
    '''
    if request.method == 'GET':
        elem_list = {}
        elem_list['string'] = 'this is a get response.'
        elem_list['bitmap'] = Image.open('./uploads/infer.jpg')
        return elem_list
    else:
        return "get test failed!"

@app.route('/post',methods=['GET', 'POST'])
def post():
    '''
    post test
    :return:    request info/fail error
    '''
    if request.method == 'POST':
        # app_tools.get_request_info()
        print(request.files['bitmap'])
        print(request.form['string'])
        # response = Response(BytesIO(open('./uploads/infer.jpg','rb').read()),mimetype='BytesIO')
        # TODO need to transform filestroge into json
        response = jsonify({'bitmap':request.files['bitmap'],'string':request.form['string']})
        # print(BytesIO(open('./uploads/infer.jpg','rb').read()))
        return response
    else:
        return "post test failed!"

'''pg's four demand'''

@app.route('/user/fetch_one',methods=['GET', 'POST'])
def fetch_one_user():
    '''
    pg's demand 1:
    fetch one user's record from db.user
    :return:    success:    user's record(string, attribute split by '####')
                fail:       "insert user record failed!"
    '''
    if request.method == 'POST': 
        username = request.form['username']
        password = request.form['password']
        tel_number = request.form['tel_number']
        user_record = db_operations.get_one_user(username, password, tel_number)
        return user_record
    else:
        return "Fetch user record failed!"

@app.route('/user/insert',methods=['GET', 'POST'])
def insert_one_user():
    '''
    pg's demand 2:
    insert one user's record into db.user
    :return:    success:    influence rows
                fail:       "insert user record failed!"
    '''
    if request.method == 'POST':
        username = request.form['username']
        password = request.form['password']
        tel_number = request.form['tel_number']
        influence_row = db_operations.insert_one_user(username, password, tel_number)
        print(influence_row)
        return influence_row
    else:
        return "insert user record failed!"

@app.route('/user/fetch_by_tel',methods=['GET', 'POST'])
def fetch_by_tel():
    '''
    pg's demand 3:
    fetch one user's record by tel from db.user
    :return:    success:    user's record(string, attribute split by '####')
                fail:       "fetch user record by tel failed!"
    '''
    if request.method == 'POST':
        tel_number = request.form['tel_number']
        user_record = db_operations.get_user_by_tel(tel_number)
        return user_record
    else:
        return "fetch user record by tel failed!"

@app.route('/user/update_pw_by_tel',methods=['GET', 'POST'])
def update_pw_by_tel():
    '''
    pg's demand 4:
    update one user's record password in db.user
    :return:    success:    influence rows
                fail:       "update user password by tel failed!"
    '''
    if request.method == 'POST':
        tel_number = request.form['tel_number']
        password = request.form['password']
        influence_row = db_operations.update_pw_by_tel(tel_number, password)
        return influence_row
    else:
        return "update user password by tel failed!"

@app.route('/get_all_wiki',methods=['GET', 'POST'])
def get_all_wiki():
    '''
    frank's demand 1:
    get all wiki from db, return json(bytes) info 
    :return:    success:    wiki(bytes, json)
                fail:       "Get all wiki failed!"
    '''
    if request.method == 'POST': 
        # transfor list to json
        wiki_json_list = db_operations.wiki_list_to_json(list_label_info)
        wiki_json_result = json.dumps(wiki_json_list)
        print(type(bytes(wiki_json_result,encoding='utf8')))
        # print(json_result)
        return bytes(wiki_json_result,encoding='utf8')
    else:
        return "Get all wiki failed!"

@app.route('/record/get_all_records',methods=['GET', 'POST'])
def get_all_records():
    '''
    frank's demand 2:
    get all record from db.upload_records, return json(bytes) info with image(bytes) in
    :return:    success:    records(bytes, json)
                fail:       "Get all wiki failed!"
    '''
    if request.method == 'POST': 
        # transfor list to json
        # record_json_list = db_operations.record_list_to_json(xxx)
        # record_json_list = [{"record_id":"001","diease_name":"Two-spotted_spider_mite","image":str(open('./uploads/infer.jpg','rb').read())},\
        #     {"record_id":"002","diease_name":"DO NOT KNOW","image":str(open('./uploads/MyPic.jpg','rb').read())}]
        record_json_list = [{"record_id":"001","diease_name":"Two-spotted_spider_mite","image":str(open('./uploads/infer.jpg','rb').read())},\
            {"record_id":"002","diease_name":"DO NOT KNOW","image":str(open('./uploads/MyPic.jpg','rb').read())}]
        print(record_json_list)
        
        # record_json_result = bytes(json.dumps(record_json_list),encoding='utf8')
        record_json_result = base64.b64encode(record_json_result)#使用base64进行加密
        # print(type(bytes(json_result,encoding='utf8')))
        # print(json_result)
        # return record_json_result
    else:
        return "Get all records failed!"

@app.route('/record/get_all_records_base64',methods=['GET', 'POST'])
def get_all_records_base64():
    '''
    frank's demand 2:
    get all record from db.upload_records, return json(bytes) info with image(bytes) in
    :return:    success:    records(bytes, json)
                fail:       "Get all wiki failed!"
    '''
    if request.method == 'POST': 
      
        record_json_list = [{"record_id":"001","diease_name":"Two-spotted_spider_mite","image":base64.b64encode(open('./uploads/infer.jpg','rb').read()).decode('utf-8')},\
            {"record_id":"002","diease_name":"DO NOT KNOW","image":base64.b64encode(open('./uploads/MyPic.jpg','rb').read()).decode('utf-8')}]
        
        record_json_result_string = json.dumps(record_json_list)
        return record_json_result_string
    else:
        return "Get all records failed!"


@app.route('/upload', methods=['GET', 'POST'])
def upload_file():
    '''
    main function: 
    get a pic from app, call live-model to get a result, then give it back to app
    :method:    only post
    :return:    result diease's record(string, attribute split by '####')
    '''
    if request.method == 'POST':
        img_obj = request.files['bitmap']
        print(img_obj)
        save_path = './uploads/' + secure_filename(img_obj.filename)
        # TODO 数据库中记录好的路径
        # TODO 随机文件名 date_type.jpg
        img_obj.save(save_path)
        img = Image.open(img_obj)
        # print(img)
        # print(infer.infer(image_path = save_path))
        # Beautiful!
        result = label_info[infer.live_model_infer(model = model, image = save_path)]
        print(result)
        return result
    else:
        return "Upload file error!"

if __name__ == '__main__':

    # infer.infer("data/infer.jpg")
    # init parameters
    init_train_parameters()
    # load live model
    model = infer.load_model()
    # TODO Load all dieases' info from Database, although retrieving is quick enough.
    list_label_info = db_operations.get_all_info()
    string_abel_info = db_operations.list_to_string(list_label_info)

    ## for test
    # result1 = infer.live_model_infer(model = model, image = "./uploads/bitmap.bmp")
    # print(result1)
    # result2 = infer.live_model_infer(model = model, image = "./data/infer2.jpg")
    # print(result2)

    # While
    app.run(debug=True,host='0.0.0.0',port=80)


    """ 
    /get_all_wiki [无参数]
        防治信息
            权宜：#####拼接

[
    {
        "en_type_name":"...",
        "cn_type_name":"...",
        "disease_feature":"...",
        "agri_control":"...",
        "chem_control":"..."
    },
    {
        "en_type_name":"...",
        "cn_type_name":"...",
        "disease_feature":"...",
        "agri_control":"...",
        "chem_control":"..."
    },
]
            
    /get_all_records [username=?POST参数]
        查识别记录数据库
        【图】⭐
        
     """

    """ 
    json
    -> Bytes
    [
        {
            "en_name":1,
            "cn_name":2,
            "explian":3,
            "method":4,
            "prevention":5
        },
        {
            "date":asd,
            "type":
        },
        {
            "date":asd,
            "type":
        }
    ]

    json
    {
        "len":100,
        "dict_1":{

        },
        "dict_10":{

        }
    }

    for(int i = 0;i<10;i++){
        String key = dict+Integer(i).toString();
        // 灵活
    }
    """


""" get_all_records 

.json5(nested)

{
    "len":10,
    [
        {
            "date":"2021-3-16 19:03:25",
            "disease_type":"",
            "_base64
        }
    ]
}
"""
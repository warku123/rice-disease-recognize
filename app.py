# flask
from flask import Flask
from flask import request, Response, send_from_directory, send_file#, jsonify
# path
import os
from werkzeug.utils import secure_filename
# image
from PIL import Image
# import cv2
# import matplotlib.pyplot as plt

# common
import json
import time
import base64
from io import BytesIO #, BufferedWriter
from utils import common
from tools import db_operations, app_tools

# model
from ResNet_Code import infer
from ResNet_Code.net import ResNet
from ResNet_Code.config import train_parameters, init_train_parameters

# multiprocess
import multiprocessing
from multiprocessing import Pool

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
        print(user_record)
        return user_record
    else:
        return "Fetch user record failed!"

@app.route('/user/detetive_user_repeat',methods=['GET', 'POST'])
def detetive_user_repeat():
    '''
    pg's demand 1:
    fetch one user's record from db.user
    :return:    success:    user's record(string, attribute split by '####')
                fail:       "insert user record failed!"
    '''
    if request.method == 'POST': 
        username = request.form['username']
        tel_number = request.form['tel_number']
        result_row = db_operations.detetive_username_repeat(username,tel_number)
        print(result_row)
        return str(result_row)
    else:
        return "Fetch user_repeat record failed!"

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
        return str(influence_row)
    else:
        return "Insert user record failed!"

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
        print(user_record)
        return user_record
    else:
        return "Fetch user record by tel failed!"

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
        print(str(influence_row))
        return str(influence_row)
    else:
        return "Update user password by tel failed!"

'''frank's demand'''

@app.route('/get_all_wiki',methods=['GET', 'POST'])
def get_all_wiki():
    '''
    frank's demand 1:
    get all wiki from db, return json(bytes) info 
    :return:    success:    wiki(bytes, json)
                fail:       "Get all wiki failed!"
    '''
    if request.method == 'POST' or request.method == 'GET': 
        # transfor list to json
        wiki_json_list = db_operations.wiki_list_to_json(list_label_info)
        wiki_json_result = json.dumps(wiki_json_list)

        return bytes(wiki_json_result,encoding='utf8')
    else:
        return "Get all wiki failed!"

@app.route('/record/get_all_records',methods=['GET', 'POST'])
def get_all_records():
    '''
    frank's demand 2:
    get all record from db.upload_records, return json info with image_url in
    :return:    success:    records(json)
                fail:       "Get all wiki failed!"
    '''
    if request.method == 'POST':
        print(request)
        try:
            username = request.form['username']
            print(username)
        except:
            return "Cannot get a username, please post a username!"
        # username = "zpg"
        record_result = db_operations.get_records_by_username(username)
        record_json_result = json.dumps(record_result)
        
        # # record_json_result = bytes(json.dumps(record_json_list),encoding='utf8')
        # record_json_result = base64.b64encode(record_json_result)#使用base64进行加密
        # # print(type(bytes(json_result,encoding='utf8')))

        return bytes(record_json_result, encoding="utf8")
    else:
        return "Get all records failed!"

@app.route('/record/get_all_records_base64',methods=['GET', 'POST'])
def get_all_records_base64():
    '''
    frank's demand 2:
    get all record from db.upload_records, return json(bytes) info with image(bytes) in
    :return:    success:    records(bytes, json)
                fail:       "Get all records failed!"
    '''
    if request.method == 'POST': 
      
        record_json_list = [{"recordId":"001","date":"2021-08-18 19:03:25","diseaseName":"Two-spotted_spider_mite","image":base64.b64encode(open('./uploads/infer.jpg','rb').read()).decode('utf-8')},{"recordId":"002","date":"2021-08-18 19:03:25","diseaseName":"DO NOT KNOW","image":base64.b64encode(open('./uploads/MyPic.jpg','rb').read()).decode('utf-8')}]        
        record_json_result_string = json.dumps(record_json_list)
        return record_json_result_string
    else:
        return "Get all records failed!"

@app.route("/download/absolute/<path:path>", methods=['GET','POST'])
def download_file_absolute_path(path):
    '''
    frank's demand 3:
    get a image by url request(absolute path, not safety)
    :para path:     absolute path
    :return:        downliad file
    '''
    path = '/' + path
    # print(path)
    try:
        if os.path.isdir(path):
            return 'Cannot download folder!'
        else:
            name=path.split('/')[-1]#split file name
            filePath=path.replace(name,'')
            # print(name)
            # print(filePath)
            return send_from_directory(filePath,filename=name,as_attachment=True)
    except: 
        return 'The file cannot be found or downloaded!'

@app.route("/download/relative/<mode>/<path:path>", methods=['GET','POST'])
def download_file_relative_path(mode,path):
    '''
    frank's demand 3:
    get a image by url request(relative path, safety)
    :para mode:     determined begin folder
    :para path:     relative path
    :return:        downliad file
    '''
    # TODO 需要知道2个参数, 第1个参数mode是某个功能的相对访问的起始路径, 第2个参数是在起始路径下的相对路径
    if mode == 'record_image':
        path = '/home/team4980/rice-disease-recognize/uploads/images/'+ path
    elif mode == 'disease_info':
        path = '/home/team4980/PreventionInfo/Pictures/' + path
    else:
        return "Relative path did not define!"
    print(path)
    try:
        if os.path.isdir(path):
            return 'Cannot download folder!'
        else:
            name=path.split('/')[-1]#split file name
            filePath=path.replace(name,'')
            # print(name)
            # print(filePath)
            return send_from_directory(filePath,filename=name,as_attachment=True)
    except:
        return 'The file cannot be found or downloaded!'
    

@app.route('/upload', methods=['GET', 'POST'])
def upload_file():
    '''
    main function: 
    get a pic from app, call live-model to get a result, then give it back to app
    :method:    only post
    :return:    result diease's record(string, attribute split by '####')
    '''
    if request.method == 'POST':
        # read post influence_row
        img_obj = request.files['bitmap']
        print(img_obj)
        try:
            username = request.form['username']
            print(username)
        except:
            username = "TOURIST"

        # call live-model
        img = Image.open(img_obj)
        diease_result = string_label_info[infer.live_model_infer(model = model, image = img)]

        # load multiprocess_pool
        # po = None
        # global po
        # with Pool(2) as po:
        #     # apply_async process
        #     en_diease_name_process_res = po.apply_async(infer.live_model_infer,(model,img))
        #     # get result
        #     en_diease_name = en_diease_name_process_res.get(timeout=1)
        #     print(str(en_diease_name))
        #     diease_result = string_label_info[str(en_diease_name)]

        # print(diease_result)

        # generate img_save name
        # Beautiful!
        img_name = db_operations.generate_image_name_by_time(diease_result,secure_filename(img_obj.filename))

        # TODO generate img_save path, which needed to be added to db
        ticks = time.localtime(time.time())
        common.create_dir_not_exist('./uploads/images/'+ str(ticks.tm_year) +'-'+ str(ticks.tm_mon) + '/')
        save_path = '/home/team4980/rice-disease-recognize/uploads/images/' + str(ticks.tm_year) +'-'+ str(ticks.tm_mon) + '/' + img_name
        print(save_path)

        # save img
        img.save(save_path)

        # insert one record into db
        insert_result = db_operations.record_insert(diease_result,save_path,username)
        if insert_result != 1:
            print("insert record error!")
        else:
            print("insert 1 record!")
        
        # return string split by "####"
        return diease_result

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
    string_label_info = db_operations.list_to_string(list_label_info)
    
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
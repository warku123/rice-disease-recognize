from utils.sql_helper import Sql_Helper
import pymysql
import time

def get_all_info():
    '''
    获取数据库病虫害信息表的所有数据
    :return:    数据元组，每条数据为一个元组单元
    '''
    # 初始化对象
    conn = Sql_Helper('localhost', 'root', '123456', 'PreventionInformation')
    # 连接
    try:
        conn.connect()
    except:
        return 'Error when linking to database!'
    # 执行
    tuple_data = conn.fetchall("select * from Prevention")
    # # 判断
    # if tuple_data:
    #     for temp in tuple_data:
    #         print(temp)
    # else:  # None,False,0
    #     print('没有数据.')
    conn.close()
    
    # 处理元组类型的数据
    list_data = []

    # 循环读取所有数据
    for item in range(len(tuple_data)):
        list_data.append([])
        # TODO 循环的次数根据数据表结构确定
        for i in range(2):
            list_data[item].append(tuple_data[item][i])
        for i in range(len(tuple_data[item])-3):
            with open(tuple_data[item][i+2],mode = 'r',encoding = 'utf8') as f:
                list_data[item].append(f.read())
        # TODO 1 此处不一定全都是png，可能会产生bug; 2 [item][5]根据表结构确定，可能会有bug
        list_data[item].append("http://40.73.0.45/download/relative/disease_info/"+\
             tuple_data[item][5].split("/")[-1])

    # print(list_data)

    return list_data

def list_to_string(list_data):    
    '''
    将查询到的元组数据转换为####分割的数据
    :return:    数据字典:
                key:   英文病名
                value:  以####分隔的不同属性列查询结果
    '''
    diease_dic = {}

    for item in list_data:
        # 循环读取所有数据
        record = item[0] + "####" + item[1]
        # TODO 此处的item切片根据db中的表结构确定
        for elem in item[2:5]:
            # 以####分割
            record = record + "####" + elem
            # print(record)
            # 将数据存入字典
            diease_dic[item[0]] = record

    # print(diease_dic)

    return diease_dic

def wiki_list_to_json(list_data):    
    '''
    将查询到的元组数据转换为json数据
    :return:    数据字典:
                key:    属性
                value:  属性结果
    '''
    json_dic = []

    for item in list_data:
        # 循环读取所有数据
        json_item = {}
        json_item["en_type_name"] = item[0]
        json_item["cn_type_name"] = item[1]
        json_item["disease_feature"] = item[2]
        json_item["agri_control"] = item[3]
        json_item["chem_control"] = item[4]
        json_item["img_url"] = item[5]
        json_dic.append(json_item)
    
    print(json_dic)

    return json_dic

def get_one_user(username = None,password = None,tel_number = None):
    '''
    获取一条用户数据
    :param username:        用户名
    :param password:        密码
    :param tel_number:      电话号码
    :return:                字符串用户记录，以####分割
    '''
    # if username == None and password == None and tel_number == None:
    if password == None:
        return 'Require password to fetch!'
    
    # 初始化对象
    conn = Sql_Helper('localhost', 'root', '123456', 'PreventionInformation')
    # 连接
    try:
        conn.connect()
    except:
        return 'Error when linking to database!'
    
    data = None

    # SQL语句
    if password != None:
        sql = "select * from user where username=%s and password=%s UNION select * from user where tel_number=%s and password=%s"
        # 执行
        data = conn.fetchone(sql,[username, password, tel_number, password])
    # elif username == None:
    #     sql = "select * from user where tel_number=%s"
    #     # 执行
    #     data = conn.fetchone(sql,[tel_number])
    # elif tel_number ==None:
    #     sql = "select * from user where username=%s"
    #     # 执行
    #     data = conn.fetchone(sql,[username])
    # else:
    #     sql = "select * from user where username=%s and tel_number=%s"
    #     # 执行
    #     data = conn.fetchone(sql,[username, tel_number])
    
    # 关闭连接
    conn.close()

    if data == None:
        return "Cannot find record."
    else:
        # 将一条记录赋值为####分割的字符串
        record = data[0]
        for item in data[1:]:
            # 以####分割
            record = record + "####" + item
        return record

def insert_one_user(username,password,tel_number):
    '''
    插入一条用户数据
    :param username:        用户名
    :param password:        密码
    :param tel_number:      电话号码
    :return:                受影响的行数
    '''
    # 初始化对象
    conn = Sql_Helper('localhost', 'root', '123456', 'PreventionInformation')
    # 连接
    try:
        conn.connect()

    except:
        return 'Error when linking to database!'
    
    # 测试用户名或电话号码是否已存在
    data = conn.fetchall("select * from user where username=%s UNION select * from user where tel_number=%s",[username, tel_number])
    if data != None:
        return 'Username or telephone number already exists!'
    else:
        # 执行插入
        influence_row = conn.insert("insert into user values(%s,%s,%s)",[username,password,tel_number])
        # print(influence_row)
    # 关闭
    conn.close()

    return influence_row

def get_user_by_tel(tel_number,username=None,password=None):
    '''
    通过电话号码获取一条用户数据
    :param username:        用户名
    :param password:        密码
    :param tel_number:      电话号码
    :return:                字符串用户记录，以####分割
    '''
    if tel_number == None:
        return "telephone is None!"

    # 初始化对象
    conn = Sql_Helper('localhost', 'root', '123456', 'PreventionInformation')
    # 连接
    try:
        conn.connect()
    except:
        return 'Error when linking to database!'
    
    # SQL语句
    sql = "select * from user where tel_number=%s"

    # 执行
    data = conn.fetchone(sql,[tel_number])
    
    # 关闭
    conn.close()

    if data == None:
        return "Cannot find record."
    else:
        # 将一条记录赋值为####分割的字符串
        record = data[0]
        for item in data[1:]:
            # 以####分割
            record = record + "####" + item
        return record

def update_pw_by_tel(tel_number, password):
    '''
    通过电话号码更新密码
    :param username:        用户名
    :param password:        密码
    :param tel_number:      电话号码
    :return:                受影响的行数
    '''
    if tel_number == None or password == None:
        return "tel_number or password is none!"
    
    # 初始化对象
    conn = Sql_Helper('localhost', 'root', '123456', 'PreventionInformation')

    # 连接
    try:
        conn.connect()
    except:
        return 'Error when linking to database!'
    
    # 修改密码
    influence_row = conn.update("update user set password=%s where tel_number=%s",[password,tel_number])
    
    # 关闭
    conn.close()

    if influence_row == 0:
        return 'Update password failed!'
    else:
        return influence_row
    
def record_insert(result,image_path,username="TOURIST"):
    '''
    插入一条用户查询记录
    :param result:              病虫害识别结果
    :param image_path:          图片存储路径
    :param username:            查询的用户名（不存在的话就是TUORIST）
    :return:                    影响的行数
    '''
    # 获取%Y-%m-%d %H:%M:%S类型的时间字符串
    ticks = time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())
    # 初始化对象
    conn = Sql_Helper('localhost', 'root', '123456', 'PreventionInformation')
    # 连接
    try:
        conn.connect()

    except:
        return 'Error when linking to database!'
    
    # 执行插入
    try:
        sql = "insert into upload_records(username,record_time,record_result,record_image_path) values(%s,%s,%s,%s)"
        influence_row = conn.insert(sql,[username,str(ticks),result.split("####")[0],image_path])
    except:
        print('Error when inserting upload record to database!')
    # print(influence_row)
    # 关闭
    conn.close()

    return influence_row

def generate_image_name_by_time(result,secure_filename):
    '''
    根据时间自动生成图片名称
    :param result:              病虫害识别结果
    :param secure_filename:     原图片名称
    :return:                    生成图片名称
    '''
    ticks = time.localtime(time.time())
    name = "{}_{}_{}-{}_{}_{}-{}.{}".format(ticks.tm_year,ticks.tm_mon,ticks.tm_mday,\
    # TODO 注意这里secure_filename.split的"."
        ticks.tm_hour,ticks.tm_min,ticks.tm_sec,result.split("####")[0],secure_filename.split(".")[-1])
    return name

def get_records_by_username(username):
    '''
    通过username获取所有历史记录
    :param username:        用户名
    :return:                json数据
    '''
    if username == None or username == "TOURIST":
        return "username CANNOT be NONE or TOURIST!"

    # 初始化对象
    conn = Sql_Helper('localhost', 'root', '123456', 'PreventionInformation')
    # 连接
    try:
        conn.connect()
    except:
        return 'Error when linking to database!'
    
    # SQL语句
    sql = "select * from upload_records where username=%s"

    # 执行
    data = conn.fetchall(sql,[username])
    
    # 关闭
    conn.close()
 
    if data == None:
        return "Cannot find record."
    else:
        # 将所有记录写入json形式
        json_list = []
        for item in data:
            item_dic = {}
            item_dic["record_id"] = str(item[0])
            item_dic["record_time"] = str(item[2])
            item_dic["record_result"] = str(item[3])
            # TODO 最后两个路径叠加根据存储路径确定，可能会有bug
            item_dic["record_image_path"] = "http://40.73.0.45/download/relative/record_image/"+ \
                str(item[4]).split('/')[-2] + '/' + str(item[4]).split('/')[-1]
            json_list.append(item_dic)
        
        # print(json_list)

        return json_list
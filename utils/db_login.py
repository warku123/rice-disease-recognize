from sql_helper import Sql_Helper
import hashlib


def login():
    '''登录'''
    name = input('输入用户名:')
    pwd = input('输入密码:')
    #加密
    pwd = doPwd(pwd)

    helper = Sql_Helper('127.0.0.1', 'root', '123456', 'PreventionInformation')
    helper.connect()
    sql = 'select * from user where name=%s and pwd=%s'
    params = [name, pwd]
    data = helper.fetchone(sql, params)
    if data:
        print('登录成功.')
    else:  # None,False,0
        print('登录失败.')


def doPwd(pwd):
    '''sha1编码'''
    mysha1 = hashlib.sha1()
    mysha1.update(pwd.encode('utf-8'))
    pwd = mysha1.hexdigest()
    return pwd


def register():
    '''注册'''
    name = input('输入用户名:')
    pwd = input('输入密码:')
    # 加密
    pwd = doPwd(pwd)

    helper = Sql_Helper('127.0.0.1', 'root', '123456', 'PreventionInformation')
    helper.connect()
    sql = 'insert into user(name,pwd) values(%s,%s)'
    params = [name, pwd]
    count = helper.insert(sql, params)
    if count:
        print('操作成功.')
    else:  # None,False,0
        print('操作失败.')


if __name__ == '__main__':
    #register()
    login()
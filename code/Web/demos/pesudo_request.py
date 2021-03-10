1
2
3
4
5
6
7
8
9
10
11
12
13
14
15
16
17
18
19
20
21
22
23
24
25
26
from flask import Flask
from flask import request
from werkzeug.utils import secure_filename
from PIL import Image

from urllib import request, parse
import urllib
import requests
import json
 
def fun_httprequest(url):
    # logname='request_post'
    # print(url)
    # s = json.dumps(dict_qurey_data)
    # print(s)
    files = {'file': open(r'D:/Research/GraduationInternship/rice-disease-recognize/code/Web/infer.jpg','rb')}
    print(files)
    r = requests.post(url, files=files)
    respode=r.text
    print('响应报文为:',end='')
    print (respode)
    # ret_dict = json.loads(respode)
    return respode
# dict_qurey_data="{'client_name': 'TRUE,0', 'card_no': '6216610100008898222', 'partner_trans_time': '192552', 'partner_trans_date': '201705101', 'partner_serial_no': 'AutoTestJin20201201192552934659', 'cvv2': '987', 'mobile_tel': '15988179711', 'pay_bankacct_type': '0', 'id_kind': '0', 'partner_id': 'A0003123', 'valid_date': '0817', 'id_no': '320113196912021509', 'func_code': '1'}"
fun_httprequest('http://40.73.0.45:80/upload')
# fun_httprequest('http://localhost:5000/upload')
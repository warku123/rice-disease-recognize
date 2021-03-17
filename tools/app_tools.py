import os

def get_request_info():
    '''
    获取request请求报文body中的模块信息，写入至文件
    '''
    with open(r"/home/team4980/rice-disease-recognize/uploads/osdata.txt","w") as f:    
        f.write("request.form:\r\n")
        f.write(str(request.form))
        f.write("\r\nrequest.args:\r\n")
        f.write(str(request.args))
        f.write("\r\nrequest.values:\r\n")
        f.write(str(request.values))
        f.write("\r\nrequest.data:\r\n")
        f.write(str(request.data))
        f.write("\r\nrequest.json:\r\n")
        f.write(str(request.json))
        f.write("\r\nrequest.files:\r\n")
        f.write(str(request.files))
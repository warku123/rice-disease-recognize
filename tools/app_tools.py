import os
# multiprocess
import multiprocessing
from multiprocessing import Pool

# model
from ResNet_Code import infer
from ResNet_Code.net import ResNet
from ResNet_Code.config import train_parameters, init_train_parameters

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

def open_multiprocess_pool(num):
    '''
    定义一个进程池，最大进程数为num
    '''
    po = Pool(num) 
    return po

def do_a_detective(pool,model,img):
    '''
    开启一个进程调用模型
    '''
    p_res = pool.apply_async(infer.live_model_infer,(model,img))
    # p_res.start()
    # p_res.join()
    # p_res.close()
    print("nonMian-Process end.")

    print(p_res.get(timeout=1))

    return str(p_res.get(timeout=1))


def close_multiprocess_pool(pool,model,img):
    '''
    关闭进程池
    '''
    pool.close() #关闭进程池，关闭后po不再接收新的请求
    pool.join() #等待po中所有子进程执行完成，必须放在close语句之后
    return "Multiprocess pools closed!"
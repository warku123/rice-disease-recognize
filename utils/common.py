import os

def create_dir_not_exist(path):
    '''
    判断路径是都存在，不存在则创建
    '''
    if not os.path.exists(path):
        os.mkdir(path)
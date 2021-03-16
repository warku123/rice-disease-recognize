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
    print("连接失败！")
else:
    print("连接成功！")

# 2.获取cursor对象
cursor = conn.cursor()
# 借助cursor查询数据库
sql = "select * from Prevention"
count = cursor.execute(sql)
print(f"查询到了{count}条数据！")
# 获取查询到的数据的详细信息
result = cursor.fetchall()
# 元组类型的数据
# print(result)

diease_dic = {}

for item in result:
    record = item[0] + "####" + item[1]
    for i in range(len(item)-2):
        with open(item[i+2],mode = 'r',encoding = 'utf8') as f:
            record = record + "####" + f.read()
    print(record)
    diease_dic[item[0]] = record

print(diease_dic)

# 资源释放
cursor.close()
conn.close()
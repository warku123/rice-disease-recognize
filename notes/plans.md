- 每天写工时
- 写点每天的收获

2021-03-09

# Web

- **防治信息**

- [ ] <span style = "color:red">Git</span>
- [ ] HTTP 文件请求
  - [ ] 数据发送方式，还原
  - [ ] Java Byte -> IOStram -> Python Image

# DL

- 异常检测
- 小样本

# App

- 历史记录UI页面

## 数据库

```java
// conn = ("username","ip")
// conn.SQL("select" + name + "from  mainTable")

public class CRUDParameterized{
    private conn;
    CRUD(){
        // conn = ...
    } 
    public getRecord(String username,String originTable){
        // jdbc
        result = conn.SQL("select" + username + "from" + originTable)
        return result;
    }

}

// App
// CRUD.getRecord(username,password_table)
```
2021-03-09
- 董
  - 服务器 Git 仓库 dyb分支
- 董、张 
  - 实现图片上传，HTTP回送分类结果
- 陈
  - 优化第二版本原型
- 崔
  - UI（选项卡）
- 赵 
  - 确定异常检测、小样本学习可行性

2021-03-10

APP

识别之后实现历史记录持久化

- 图片
- 类别
- 经纬度



DL


- 特征聚类（无监督）实现异常分析

Web

- MySQL数据库

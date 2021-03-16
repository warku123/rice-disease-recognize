# Sprint3

## 主要需求 

- **注册，登录**
  - [ ] 数据库操作
  - [ ] 登录状态持久化 
  ```java
    public void login(String username){
        userStatus = StatusEnum.LOGGED_IN;
        this.userName = username;
    }

    public void logout(){
        userStatus = StatusEnum.LOGGED_OUT;
        this.userName = null;
    }

  ```

- **历史记录**
  - [ ] 服务器存储历史记录、**图片**
  - [ ] 服务器根据`get_history_records?username=ABC`返回复合数据结构

- **防治信息标签页**
  - [ ] 选项卡UI
  - [ ] 拉取历史记录
    - [ ] 需要Web返回📷图片

- **疑问及反馈**
  - [ ] APP调用SQLPost = `upload`上传一条记录
  - [ ] APP调用SQLPost **查询**、**显示**问题&反馈

~~- **预警**~~
~~- [ ] `get_warnings?username=ABC`~~

<span style = "color:red">广告图片，欢迎界面</span>

## 技术要点

**Web**

*总体来讲，基于URL的访问缺乏安全性(Cookies可能更好)，但我们不用在意💃*

<span style = "color:red">非常重要！</span>

- Java 
  - SQL -> Http **url请求** -> Flask -> MySQL -> Response
  ```java
    String httpurl = "http://40.73.0.45:80/login?username=【】&password=【】";
    // 创建远程url连接对象
    URL url = new URL(httpurl);
    // 通过远程url连接对象打开一个连接，强转成httpURLConnection类
    connection = (HttpURLConnection) url.openConnection();
    // 设置连接方式：get
    connection.setRequestMethod("GET");
  ```
  - 文件传输【尤其是图片】
    - `json5`
    ```json5
    // 第一个Response得到length
    // 之后for循环获取Response
    {
        "disease_type":"**",
        "record_date":"2021-3-15",
        "img_base64":"qotnfzvxcj..."
    }
    ```
    <span style = "color:red"><p style = "font-size:1.5em;font-family:Consolas">多个连续响应可能需要考虑阻塞问题，Socket有可能可行，但是Socket传输文件有包大小限制 
    </p></span>
    

**UI**

- 选项卡（基本完成）
- 预警页面

<span style = "color:orange"><b>UI优化（<span style = "color:red">面向发布</span>）</b></span>

**App Logic**

- Json解包
  - 接受不定量的`Response`
  - `阻塞`
- 【服务器端】
  - 预警信息预测
  - 需要**数据库遍历**
  ```
  给出(X,Y)经纬度
  计算识别记录附近的(X_1,Y_1),...,(X_n,Y_n)，并且打包返回病虫害种类及数量作为预警
  ```

通用逻辑 `SQLUrlHelper`

- 封装`Conn的维护`，`添加请求头`，`try-catch`等操作

## 人员分工

- 历史记录
  - 董、张、赵
- 预警 
  - 崔、董
- UI&测试
  - 陈 

## 其他

建议
- UI风格统一
- 方法封装

待办
- 置信度？
- 模型更新？

技术主管个人反思：
- 开发方向和需求不符
- 缺少沟通，或许关注太过细节

----


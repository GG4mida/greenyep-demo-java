## 绿点支付 Java 示例程序

### 说明

绿点支付为独立开发者提供免签即时到账收款接口，只需个人支付宝或微信账号即可收款，极低费率，快捷接入，安全稳定不漏单。详情请访问：[https://greenyep.com](https://greenyep.com)

### 运行

> 请修改 common/config.java 文件，替换商户 UID 和 Token。

- Eclipse 导入项目
- 启动 web 服务器，如 tomcat
- 浏览器访问 /index.html

### 示例截图

> 如截图无法加载请开启FQ软件。

订单页：

<img src="https://github.com/GG4mida/greenyep-demo-java/blob/main/screen/trans.png?raw=true" width="360" alt="订单页截图">

提交订单，会跳转至官方收银台（也可以使用接口返回的数据，自定义收银台）。

官方收银台：

<img src="https://github.com/GG4mida/greenyep-demo-java/blob/main/screen/cashier.png?raw=true" width="360" alt="收银台截图">

### 接入文档

请访问绿点支付官网，查阅详细接入文档：[https://greenyep.com/doc/dev_pre.html](https://greenyep.com/doc/dev_pre.html)
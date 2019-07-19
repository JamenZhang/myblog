#### 演示网站链接：[https://blog.iifxs.xyz](https://blog.iifxs.xyz)

## 页面展示

##### 首页展示
![](https://jamen-blog.oss-cn-shenzhen.aliyuncs.com/public/blogArticles/2019-07-19/1563525349.png)
##### 文章编辑
![](https://jamen-blog.oss-cn-shenzhen.aliyuncs.com/public/blogArticles/2019-07-19/1563525364.png)
##### 网站后台
![](https://jamen-blog.oss-cn-shenzhen.aliyuncs.com/public/blogArticles/2019-07-19/1563525376.png)
##### 用户个人中心
![](https://jamen-blog.oss-cn-shenzhen.aliyuncs.com/public/blogArticles/2019-07-19/1563525394.png)
##### 修改密码
![](https://jamen-blog.oss-cn-shenzhen.aliyuncs.com/public/blogArticles/2019-07-19/1563525407.png)
##### Druid监控
![](https://jamen-blog.oss-cn-shenzhen.aliyuncs.com/public/blogArticles/2019-07-19/1563526738.png)
![](https://jamen-blog.oss-cn-shenzhen.aliyuncs.com/public/blogArticles/2019-07-19/1563526749.png)
## 项目设计
![](https://jamen-blog.oss-cn-shenzhen.aliyuncs.com/public/blogArticles/2019-07-19/1563526786.png)

#### 总体设计
运行环境
jdk1.8+tomcat7+Mysql+IntelliJ IDEA工具+Redi缓存+PageHelper分页插件+SpringSecurity权限控制+Maven+Git

## 数据设计

###### 用户表：user
![](https://jamen-blog.oss-cn-shenzhen.aliyuncs.com/public/blogArticles/2019-07-19/1563527328.png)

###### 文章表：article
![](https://jamen-blog.oss-cn-shenzhen.aliyuncs.com/public/blogArticles/2019-07-19/1563527317.png)

###### 评论表：comment
![](https://jamen-blog.oss-cn-shenzhen.aliyuncs.com/public/blogArticles/2019-07-19/1563527305.png)

## 开发流程
###### 数据库操作部分
- 实现service层中的接口，并注入mapper层中的sql接口
- 使用Mybatis的JavaConfig（注解）方式编写Sql语句。
- 关于事务的实现，在启动类中开启事务，并在service层需要实现事务的业务接口上使用`@Transactional`注解。

###### 页面与展示
- 作为一名渣渣学生，对于css欠缺，所以使用了比较友好的妹子UI主题，
- 前后端的交互主要在controller和JavaScript，springboot比较配的Thymeleaf渲染页面。
- 自定义异常处理页面，重写`WebMvcConfigurerAdapter`实现自动跳转到404、403页面

###### 其他小功能
- 用了lazyload插件实现页面图片懒加载（已经实现）
- 后台实时记录当天访客量，用类似轮盘展示出来
- 分析访问量最多的数据，主要在于文章访问部分，将文章放入redis缓存。
- 使用了阿里云OSS、短信服务，实现验证码注册的功能。
- Druid监控

###### 网站
- 云服务器是搬瓦工上买的vps，顺带做个人服务器用系统是：centos7.4
- 阿里巴巴的免费SSL证书，实现https访问以及自动从http跳转到https



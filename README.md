# rpc
知乎文章地址：https://zhuanlan.zhihu.com/p/74098408

手写一个简单rpc框架，示意图如下：

![rpc框架示意图](https://raw.githubusercontent.com/DavidDingXu/rpc/master/images/rpc%E6%A1%86%E6%9E%B6%E7%A4%BA%E6%84%8F%E5%9B%BE.png)

实现的主要内容包括：

1. 基于netty实现了一套自定义远程调用；
2. 基于Zookeeper实现了服务的自动注册与发现；
3. 实现了服务的多版本支持与负载均衡。

<p align="center">
    <img src="https://cdn.lilu.org.cn/sunchaser-logo.png" alt="logo" />
    <h2 align="center">sunchaser-rpc</h2>
    <p align="center">
        sunchaser-rpc, A high performance RPC framework based on Netty.
        <br /><br />
        <img src="https://img.shields.io/circleci/project/github/badges/shields/master?color=%231ab1ad&label=master" alt="project" />
        <img src="https://img.shields.io/badge/JDK-8.0+-0e83c" alt="java-version" />
        <img src="https://img.shields.io/github/license/sunchaser-lilu/sunchaser-rpc?color=FF5531" alt="license" />
    </p>
</p>

# 背景

`RPC`：像调用本地方法一样调用远程服务。

# 介绍

`sunchaser-rpc`是一个分布式服务框架，提供高性能的`RPC`远程过程调用功能。拥有分布式、高性能、服务治理、注册中心、负载均衡及微内核架构等诸多特性。

# 特性

- [x] 基于`Netty`框架构建网络通讯层。高性能。 
- [x] 多序列化方式：支持`Hessian2`、`Json`、`XML`、`Protostuff`及`Kryo`等方式。 
- [x] 多压缩方式：支持`Snappy`、`DEFLATE`、`Gzip`、`bzip`、`LZ4`及`LZO`等方式。 
- [x] 多动态代理方式：支持`JDK`、`Cglib`、`Javassist`及`Byte Buddy`等方式。 
- [x] 注册中心：提供基于`Zookeeper`的服务注册与发现。 
- [x] 负载均衡/软负载：提供多种负载均衡算法，包括随机/加权随机、轮询/加权轮询、一致性哈希等。 
- [x] 多调用方式：支持同步`Sync`、异步`Future`、回调`Callback`和单向调用`Oneway`四种方式。 
- [x] 微内核架构：增强`JDK`原生`SPI`机制（参考了`Dubbo`框架），使用`SPI`加载插件，可以轻松替换每个组件的实现，包括序列化器、压缩器、动态代理、注册中心、负载均衡算法等。用户只需实现相关接口，然后在`/META-INF/extensions`目录下配置实现类的类路径即可替换框架的默认实现。
- [ ] 插件机制：通过插件实现高可扩展性，在`RPC`请求生命周期各个阶段注入插件，实现自定义的业务逻辑。
- [ ] 泛化调用：不依赖服务提供方的`API`接口，用`Map`封装参数直接调用。
- [ ] ......

# 快速开始

拉取代码

```shell
git clone https://github.com/sunchaser-lilu/sunchaser-rpc.git
```

待完善...

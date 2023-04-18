package com.rrtv.rpc.provider.service;

import com.rrtv.rpc.api.service.HelloWordService;
import com.rrtv.rpc.server.annotation.RpcService;

/**
 * @Classname HelloWordServiceImpl
 * @Description
 * @Date 2021/7/5 16:31
 * @Created by wangchangjiu
 */
//自定义注解是基于 @service 的，是一个复合注解，具备@service注解的功能，
//在@RpcService注解中指明服务接口和服务版本，发布服务到ZK上，会根据这个两个元数据注册
@RpcService(interfaceType = HelloWordService.class, version = "1.0")
public class HelloWordServiceImpl implements HelloWordService {
    @Override
    public String sayHello(String name) {
        return String.format("您好：%s, rpc 调用成功", name);
    }

}

package com.rrtv.rpc.server;
import com.rrtv.rpc.core.common.ServiceInfo;
import com.rrtv.rpc.core.common.ServiceUtil;
import com.rrtv.rpc.core.register.RegistryService;
import com.rrtv.rpc.server.annotation.RpcService;
import com.rrtv.rpc.server.config.RpcServerProperties;
import com.rrtv.rpc.server.store.LocalServerCache;
import com.rrtv.rpc.server.transport.RpcServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.CommandLineRunner;

import javax.annotation.PreDestroy;
import java.net.InetAddress;
/**
 * @Classname RpcServerProvider
 * @Description
 * @Date 2022/7/5 11:38
 * @Created by 闫港
 */
@Slf4j
public class RpcServerProvider implements BeanPostProcessor, CommandLineRunner {

    private RegistryService registryService;

    private RpcServerProperties properties;

    private RpcServer rpcServer;

    public RpcServerProvider(RegistryService registryService, RpcServer rpcServer, RpcServerProperties properties) {
        this.registryService = registryService;
        this.properties = properties;
        this.rpcServer = rpcServer;
    }


    /**
     * 所有bean 实例化之后处理
     * <p>
     * 暴露服务注册到注册中心
     * <p>
     * 容器启动后开启netty服务处理请求
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
//    服务提供者启动之后，根据springboot自动装配机制，server-starter的配置类就生效了，
//    在一个bean的后置处理器(RpcServerProvider)中获取被注解@RpcService修饰的bean,将注解的元数据注册到ZK上。
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
//      如果此类中存在这样的注释，则使用java.lang.Class类的getAnnotation()方法来获取指定注释类型的注释。
        RpcService rpcService = bean.getClass().getAnnotation(RpcService.class);
        if (rpcService != null) {
            try {
                String serviceName = rpcService.interfaceType().getName();
                String version = rpcService.version();
//               将bean缓存在本地，用于服务处理请求时反射调用
                LocalServerCache.store(ServiceUtil.serviceKey(serviceName, version), bean);

                ServiceInfo serviceInfo = new ServiceInfo();
                serviceInfo.setServiceName(ServiceUtil.serviceKey(serviceName, version));
                serviceInfo.setPort(properties.getPort());
                serviceInfo.setAddress(InetAddress.getLocalHost().getHostAddress());
                serviceInfo.setAppName(properties.getAppName());

                // 服务注册
                registryService.register(serviceInfo);
            } catch (Exception ex) {
                log.error("服务注册出错:{}", ex);
            }
        }
        return bean;
    }

    /**
     * 启动rpc服务 处理请求
     * @param args
     */
    @Override
    public void run(String... args) {
        new Thread(() -> rpcServer.start(properties.getPort())).start();
        log.info(" rpc server :{} start, appName :{} , port :{}", rpcServer, properties.getAppName(), properties.getPort());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                // 关闭之后把服务从ZK上清除
                registryService.destroy();
            }catch (Exception ex){
                log.error("", ex);
            }
        }));
    }

}

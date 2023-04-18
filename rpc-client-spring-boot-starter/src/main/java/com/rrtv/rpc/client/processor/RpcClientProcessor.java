package com.rrtv.rpc.client.processor;

import com.rrtv.rpc.client.annotation.RpcAutowired;
import com.rrtv.rpc.client.config.RpcClientProperties;
import com.rrtv.rpc.client.proxy.ClientStubProxyFactory;
import com.rrtv.rpc.core.discovery.DiscoveryService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/**
 * @Classname RpcClientProcessor
 * @Description bean 后置处理器 获取所有bean
 * 判断bean字段是否被 {@link com.rrtv.rpc.client.annotation.RpcAutowired } 注解修饰
 * 动态修改被修饰字段的值为代理对象 {@link ClientStubProxyFactory}
 * @Date 2022/10/3 16:06
 * @Created by yangang
 */
public class RpcClientProcessor implements BeanFactoryPostProcessor, ApplicationContextAware {

    private ClientStubProxyFactory clientStubProxyFactory;

    private DiscoveryService discoveryService;

    private RpcClientProperties properties;

    private ApplicationContext applicationContext;

    public RpcClientProcessor(ClientStubProxyFactory clientStubProxyFactory,
                              DiscoveryService discoveryService,
                              RpcClientProperties properties) {
        this.clientStubProxyFactory = clientStubProxyFactory;
        this.discoveryService = discoveryService;
        this.properties = properties;
    }
//要让客户端无感知的调用服务者，就要使用动态代理，
// HelloWordService没有实现类，需要给它赋值代理类
//在代理类中发起请求调用。基于springboot自动装配，服务消费者启动
//bean后置处理器RpcClientProcessor开始工作，它主要是遍历所有bean,
//判断每个bean中的属性是否有被@RpcAutowired注释修饰，有的话就把该属性动态赋值代理类，
//这个再调用时会调用代理类的invoke方法。
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
//    拿到所有的bean开始遍历
        for (String beanDefinitionName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
//     获取bean类名
            String beanClassName = beanDefinition.getBeanClassName();
            if (beanClassName != null) {
//      反射获取class
                Class<?> clazz = ClassUtils.resolveClassName(beanClassName, this.getClass().getClassLoader());
                ReflectionUtils.doWithFields(clazz, field -> {
//      判断这个bean中是否有被RpcAutowired注解修饰的属性
                    RpcAutowired rpcAutowired = AnnotationUtils.getAnnotation(field, RpcAutowired.class);
                    if (rpcAutowired != null) {
                        Object bean = applicationContext.getBean(clazz);
                        field.setAccessible(true);
                        // 修改为代理对象
                        ReflectionUtils.setField(field, bean, clientStubProxyFactory.getProxy(field.getType(),
                                rpcAutowired.version(), discoveryService, properties));
                    }
                });
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

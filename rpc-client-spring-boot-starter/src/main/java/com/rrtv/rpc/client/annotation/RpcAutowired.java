package com.rrtv.rpc.client.annotation;

import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.*;
//自定义注解
/**
 * @Classname RpcAutowired
 * @Description
 * @Date 2021/7/5 15:41
 * @Created by wangchangjiu
 */
//描述注解能够使用的位置
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.PARAMETER,
        ElementType.FIELD, ElementType.ANNOTATION_TYPE})
// @Retention(RetentionPolicy.RUNTIME)：当前被描述的注解，
// 会保留到class字节码文件中，并被JVM读取到
//@Documented：描述注解是否被抽取到api文档中
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Autowired
public @interface RpcAutowired {
//    如果定义属性时，使用default关键字给属性默认初始化值，
//    则使用注解时，可以不进行属性的赋值。
    String version() default "1.0";

}

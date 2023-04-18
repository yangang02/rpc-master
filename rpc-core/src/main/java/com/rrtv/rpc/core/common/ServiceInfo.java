package com.rrtv.rpc.core.common;

import lombok.Data;

import java.io.Serializable;
//Serializable是一个空接口，那怎样工作呢？Serializable仅仅作为一个标识符的作用，通过该接口让JVM识别，并将其进行序列化。
//为什么要序列化
//存储对象在存储介质中，以便在下次使用的时候，可以很快捷的重建一个副本。
//便于数据传输，尤其是在远程调用的时候！
@Data
public class ServiceInfo implements Serializable {

	/**
     *  应用名称
	 */
	private String appName;

    /**
     *  服务名称
	 */
	private String serviceName;

	/**
	 *  版本
	 */
	private String version;

	/**
     *  地址
	 */
	private String address;

    /**
     *  端口
	 */
	private Integer port;
}

package com.rrtv.rpc.server.store;


import com.rrtv.rpc.core.common.ServiceInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Classname LocalServerCache
 * @Description 将暴露的服务缓存到本地
 * 在处理 RPC 请求时可以直接通过 cache 拿到对应的服务进行调用。避免反射实例化服务开销
 * @Date 2021/7/5 11:44
 * @Created by yangang
 */
public final class LocalServerCache {

    private static final Map<String, Object> serverCacheMap = new HashMap<>();

    public static void store(String serverName, Object server) {
//        merge() 可以这么理解：它将新的值赋值到 key （如果不存在）或更新给定的key 值对应的 value
//        因为Map.merge()意味着我们可以原子地执行插入或更新操作，它是线程安全的。
        serverCacheMap.merge(serverName, server, (Object oldObj, Object newObj) -> newObj);
    }

    public static Object get(String serverName) {
        return serverCacheMap.get(serverName);
    }

    public static Map<String, Object> getAll(){
        return null;
    }
}

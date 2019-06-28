package com.aliware.tianchi;

import java.util.Arrays;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.LoadBalance;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author daofeng.xjf
 *
 * 负载均衡扩展接口 必选接口，核心接口 此类可以修改实现，不可以移动类或者修改包名 选手需要基于此类实现自己的负载均衡算法
 */
public class UserLoadBalance implements LoadBalance {

    @Override
    public <T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
        //数组中存储的分别是[interface com.aliware.tianchi.HashInterface -> dubbo://provider-small:20880/com.aliware.tianchi.HashInterface?async=true&heartbeat=0&loadbalance=user&reconnect=false,
        // interface com.aliware.tianchi.HashInterface -> dubbo://provider-medium:20870/com.aliware.tianchi.HashInterface?async=true&heartbeat=0&loadbalance=user&reconnect=false,
        // interface com.aliware.tianchi.HashInterface -> dubbo://provider-large:20890/com.aliware.tianchi.HashInterface?async=true&heartbeat=0&loadbalance=user&reconnect=false]
        // System.out.println("ZCL-DEBUG"+Arrays.toString(invokers.toArray()));
        return invokers.get(2);
    }
}

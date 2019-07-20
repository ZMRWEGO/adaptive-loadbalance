package com.aliware.tianchi;

import com.aliware.tianchi.util.GlobalConf;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
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
        //比较剩余可用线程数，大的优先分配
        int small = 200 - GlobalConf.smallActive;
        int medium = 450 - GlobalConf.mediumActive;
        int large =650 - GlobalConf.largeActive;
        if (large >= Math.max(small, medium)) {
            return invokers.get(2);
        } else if (medium >= small) {
            return invokers.get(1);
        } else {
            return invokers.get(0);
        }
    }
    private int randomOnWeight() {
        int[] weightArray = new int[]{150,500,650};
        TreeMap<Integer, Integer> treeMap = new TreeMap<>();
        Map<Integer, Integer> map = new HashMap<>();
        map.put(150,0);
        map.put(500,1);
        map.put(650,2);
        int key = 0;
        for (int weight : weightArray) {
            treeMap.put(key, weight);
            key += weight;
        }

        Random r = new Random();
        int num;
        num = r.nextInt(key);
        return map.get(treeMap.floorEntry(num).getValue());

    }
}

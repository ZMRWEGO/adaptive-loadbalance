package com.aliware.tianchi;

import com.aliware.tianchi.util.GlobalConf;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.utils.NamedThreadFactory;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.RpcStatus;
import org.apache.dubbo.rpc.cluster.LoadBalance;
import java.util.List;
import org.apache.dubbo.rpc.cluster.loadbalance.LeastActiveLoadBalance;

/**
 * @author daofeng.xjf
 *
 * 负载均衡扩展接口 必选接口，核心接口 此类可以修改实现，不可以移动类或者修改包名 选手需要基于此类实现自己的负载均衡算法
 */
public class UserLoadBalance implements LoadBalance {
    @Override
    public <T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
//        int small = GlobalConf.smallActive;
//        int medium = GlobalConf.mediumActive;
//        int large = GlobalConf.largeActive;
//        if (large <= Math.min(small, medium)) {
//            return invokers.get(2);
//        } else if (medium <= small) {
//            return invokers.get(1);
//        } else {
//            return invokers.get(0);
//        }
        return invokers.get(randomOnWeight());
    }

    public  String stampToDate(long s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(s);
        res = simpleDateFormat.format(date);
        return res;
    }
    private int getActive(int i){
        if (i == 2){
            return GlobalConf.largeActive;
        } else if (i == 1) {
            return GlobalConf.mediumActive;
        } else {
            return GlobalConf.smallActive;
        }
    }

    private int getWeight(int i) {
        if (i == 2) {
            return 13;
        } else if (i == 1) {
            return 9;
        } else {
            return 4;
        }
    }
    private int randomOnWeight() {
        int[] weightArray = new int[]{30, 85, 115};
        TreeMap<Integer, Integer> treeMap = new TreeMap<>();
        Map<Integer, Integer> map = new HashMap<>();
        map.put(30, 0);
        map.put(85, 1);
        map.put(115, 2);
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

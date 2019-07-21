package com.aliware.tianchi;

import com.aliware.tianchi.util.GlobalConf;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.LoadBalance;

import java.util.List;

/**
 * @author daofeng.xjf
 *
 * 负载均衡扩展接口 必选接口，核心接口 此类可以修改实现，不可以移动类或者修改包名 选手需要基于此类实现自己的负载均衡算法
 */
public class UserLoadBalance implements LoadBalance {

    private static final Logger logger = LoggerFactory.getLogger(UserLoadBalance.class);
    private Random random = new Random();

    @Override
    public <T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
        //比较剩余可用线程数，
//        if (GlobalConf.smallMax != 0 && GlobalConf.mediumMax != 0 && GlobalConf.largeMax != 0) {
            int small = GlobalConf.smallActive * 10000 / GlobalConf.smallMax;
            int medium = GlobalConf.mediumActive * 10000 / GlobalConf.mediumMax;
            int large = GlobalConf.largeActive * 10000 / GlobalConf.largeMax;
            if (large <= Math.min(small, medium)) {
                return invokers.get(2);
            } else if (medium <= small) {
                return invokers.get(1);
            } else {
                return invokers.get(0);
            }
//        }
//        //若开始时未获取到最大线程数，先用临时权重分配
//        else {
//            return invokers.get(randomOnWeight());
//        }
    }

    private int randomOnWeight() {
        int[] weightArray = new int[]{3, 8, 12};
        TreeMap<Integer, Integer> treeMap = new TreeMap<>();
        Map<Integer, Integer> map = new HashMap<>();
        map.put(3, 0);
        map.put(8, 1);
        map.put(12, 2);
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

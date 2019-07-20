package com.aliware.tianchi;

import com.aliware.tianchi.util.GlobalConf;
import java.util.Arrays;
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
import java.util.concurrent.ThreadLocalRandom;

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
        if (GlobalConf.smallMax != 0 && GlobalConf.mediumMax != 0 && GlobalConf.largeMax != 0) {
            int length = invokers.size();
            // 剩余活跃数
            int remainActive = -1;
            // 相同剩余活跃 数量
            int count = 0;
            // indexes 用于记录具有相同“活跃数”的 Invoker 在 invokers 列表中的下标信息
            int[] indexes = new int[length];
            int totalWeight = 0;
            // 第一个最小活跃数的 Invoker 权重值，用于与其他具有相同最小活跃数的 Invoker 的权重进行对比，
            // 以检测是否“所有具有相同最小活跃数的 Invoker 的权重”均相等
            int firstWeight = 0;
            boolean sameWeight = true;

            // 遍历 invokers 列表
            for (int i = 0; i < length; i++) {
                // 获取 Invoker 对应的活跃数
                int active = getActive(i);
                // 获取权重 - ⭐️
                int weight = getWeight(i);
                // 发现更大的活跃数，重新开始
                if (active>remainActive) {
                    // 使用当前活跃数 active 更新最大活跃数 remainActive
                    remainActive = active;
                    // 更新 count 为 1
                    count = 1;
                    // 记录当前下标值到 indexes 中
                    indexes[0] = i;
                    totalWeight = weight;
                    firstWeight = weight;
                    sameWeight = true;
                } else if (active == remainActive) {
                    // 在 indexes 中记录下当前 Invoker 在 invokers 集合中的下标
                    indexes[count++] = i;
                    // 累加权重
                    totalWeight += weight;
                    // 检测当前 Invoker 的权重与 firstWeight 是否相等，
                    // 不相等则将 sameWeight 置为 false
                    if (sameWeight && i > 0
                        && weight != firstWeight) {
                        sameWeight = false;
                    }
                }
            }
            if (count == 1) {
                return invokers.get(indexes[0]);
            }

            // 有多个 Invoker 具有相同的最大活跃数，但它们之间的权重不同
            if (!sameWeight && totalWeight > 0) {
                // 随机生成一个 [0, totalWeight) 之间的数字
                int offsetWeight = random.nextInt(totalWeight)+1;
                for (int i = 0; i < count; i++) {
                    int leastIndex = indexes[i];
                    // 获取权重值，并让随机数减去权重值 - ⭐️
                    offsetWeight -= getWeight(leastIndex);
                    if (offsetWeight <= 0)
                        return invokers.get(leastIndex);
                }
            }
            // 如果权重相同或权重为0时，随机返回一个 Invoker
            return invokers.get(indexes[random.nextInt(count)]);
        }
        //若开始时未获取到最大线程数，先用临时权重分配
        else {
            return invokers.get(randomOnWeight());
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

    private int getActive(int i) {
        if (i == 0) {
            return GlobalConf.smallMax - GlobalConf.smallActive;
        } else if (i == 1) {
            return GlobalConf.mediumMax - GlobalConf.mediumActive;
        } else {
            return GlobalConf.largeMax - GlobalConf.largeActive;
        }
    }

    private int getWeight(int i) {
        if (i == 0) {
            return GlobalConf.smallMax;
        } else if (i == 1) {
            return GlobalConf.mediumMax;
        } else {
            return GlobalConf.largeMax;
        }
    }
}

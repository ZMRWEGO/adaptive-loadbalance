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

    private final AtomicBoolean init = new AtomicBoolean(false);
    private final AtomicBoolean isFormal = new AtomicBoolean(false);
    private final Random random = new Random();
    private int x = 2;
    private LeastActiveLoadBalance leastActiveLoadBalance;
    @Override
    public <T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
        System.out.println("s:"+GlobalConf.smallActive+"m"+GlobalConf.mediumActive+"l"+GlobalConf.largeActive);
        int length = invokers.size();
        // 最小的活跃数
        int leastActive = -1;
        // 具有相同“最小活跃数”的服务者提供者（以下用 Invoker 代称）数量
        int leastCount = 0;
        // leastIndexs 用于记录具有相同“最小活跃数”的 Invoker 在 invokers 列表中的下标信息
        int[] leastIndexs = new int[length];
        int totalWeight = 0;
        // 第一个最小活跃数的 Invoker 权重值，用于与其他具有相同最小活跃数的 Invoker 的权重进行对比，
        // 以检测是否“所有具有相同最小活跃数的 Invoker 的权重”均相等
        int firstWeight = 0;
        boolean sameWeight = true;

        // 遍历 invokers 列表
        for (int i = 0; i < length; i++) {
            Invoker<T> invoker = invokers.get(i);
            // 获取 Invoker 对应的活跃数
            int active = getActive(i);
            // 获取权重 - ⭐️
            int weight = getWeight(i);
            // 发现更小的活跃数，重新开始
            if (leastActive == -1 || active < leastActive) {
                // 使用当前活跃数 active 更新最小活跃数 leastActive
                leastActive = active;
                // 更新 leastCount 为 1
                leastCount = 1;
                // 记录当前下标值到 leastIndexs 中
                leastIndexs[0] = i;
                totalWeight = weight;
                firstWeight = weight;
                sameWeight = true;

                // 当前 Invoker 的活跃数 active 与最小活跃数 leastActive 相同
            } else if (active == leastActive) {
                // 在 leastIndexs 中记录下当前 Invoker 在 invokers 集合中的下标
                leastIndexs[leastCount++] = i;
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

        // 当只有一个 Invoker 具有最小活跃数，此时直接返回该 Invoker 即可
        if (leastCount == 1) {
            return invokers.get(leastIndexs[0]);
        }

        // 有多个 Invoker 具有相同的最小活跃数，但它们之间的权重不同
        if (!sameWeight && totalWeight > 0) {
            // 随机生成一个 [0, totalWeight) 之间的数字
            int offsetWeight = random.nextInt(totalWeight)+1;
            // 循环让随机数减去具有最小活跃数的 Invoker 的权重值，
            // 当 offset 小于等于0时，返回相应的 Invoker
            for (int i = 0; i < leastCount; i++) {
                int leastIndex = leastIndexs[i];
                // 获取权重值，并让随机数减去权重值 - ⭐️
                offsetWeight -= getWeight(leastIndex);
                if (offsetWeight <= 0)
                    return invokers.get(leastIndex);
            }
        }
        // 如果权重相同或权重为0时，随机返回一个 Invoker
        return invokers.get(leastIndexs[random.nextInt(leastCount)]);
    }


    private int randomOnWeight() {
        int[] weightArray = new int[]{150, 500, 650};
        TreeMap<Integer, Integer> treeMap = new TreeMap<>();
        Map<Integer, Integer> map = new HashMap<>();
        map.put(150, 0);
        map.put(500, 1);
        map.put(650, 2);
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

    private void weighting(long current) {
        //System.out.println(Thread.currentThread().getId());
        if (!init.get()) {
            if (init.compareAndSet(false, true)) {
                GlobalConf.TIME = new AtomicLong(System.currentTimeMillis());
            }
        }

    }

    private int refresh(AtomicInteger index) {
        if (index.get() > 9) {
            index.set(9);
        }
        int small = Double.valueOf(GlobalConf.smallMC[index.get()]/GlobalConf.smallRT[index.get()]*1000).intValue();
        int medium = Double.valueOf(GlobalConf.mediumMC[index.get()]/GlobalConf.mediumRT[index.get()]*1000).intValue();
        int large = Double.valueOf(GlobalConf.largeMC[index.get()]/GlobalConf.largeRT[index.get()]*1000).intValue();
        int[] weightArray = {small, medium, large};
        TreeMap<Integer, Integer> treeMap = new TreeMap<>();
        Map<Integer, Integer> map = new HashMap<>();
        map.put(small, 0);
        map.put(medium, 1);
        map.put(large, 2);
        int key = 0;
        for (int weight : weightArray) {
            treeMap.put(key, weight);
            key += weight;
        }

        Random r = new Random();
        int num;
        num = r.nextInt(key);
        int result = map.get(treeMap.floorEntry(num).getValue());
        System.out.println("s:" + small + " m:" + medium + " l" + large + "weight" + result);
        return result;
    }
    public  String stampToDate(long s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(s);
        res = simpleDateFormat.format(date);
        return res;
    }
    private int getActive(int i){
        if (i == 0){
            return GlobalConf.smallActive*20;
        } else if (i == 1) {
            return GlobalConf.mediumActive*6;
        } else {
            return GlobalConf.largeActive*5;
        }
    }

    private int getWeight(int i) {
        if (i == 0) {
            return 150;
        } else if (i == 1) {
            return 500;
        } else {
            return 650;
        }
    }
}

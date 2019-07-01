package com.aliware.tianchi;

import com.aliware.tianchi.util.GlobalConf;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.utils.NamedThreadFactory;
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

    private final AtomicBoolean init = new AtomicBoolean(false);
    private final AtomicBoolean isFormal = new AtomicBoolean(false);
    private final AtomicInteger index = new AtomicInteger(0);
    private int x = 2;

    @Override
    public <T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
        long current = System.currentTimeMillis();
        weighting(current);
        if (!isFormal.get()) {
            if ((current - GlobalConf.TIME.get()) / 1000 >= 30) {
                if (isFormal.compareAndSet(false, true)) {

                    GlobalConf.TIME.set(current);
                    // index.getAndAdd(1);
                    // System.out.println("预热阶段结束，第一次更新最大并发数");
                    //x = refresh(index);
                }
            }
            x = randomOnWeight();

        } else {
            if (isFormal.get() && (current - GlobalConf.TIME.get()) / 1000 >= 6) {
                GlobalConf.TIME.compareAndSet(GlobalConf.TIME.get(), current);
                index.getAndAdd(1);
                int circle = index.get() + 1;
                System.out.println("第" + circle + "次更新最大并发数");
            }
            x = refresh(index);
        }
        // System.out.println("ZCL-DEBUG:" + x + isFormal.get());
        return invokers.get(x);
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
//        获取json
//        if (!init.get()) {
//            if (init.compareAndSet(false, true)) {
//                //这里不自行加载json，改为数组
//                //执行定时任务设置权重
//                int startTime = 30;
//                for ( index = 0; index <GlobalConf.smallMC.length ; index++) {
//
//                    scheduler.schedule(() -> refresh(GlobalConf.smallMC[index],
//                        GlobalConf.mediumMC[index], GlobalConf.largeMC[index]), startTime, TimeUnit.SECONDS);
//                    startTime += 6;
//                    System.out.println("执行第"+index+"次");
//                }
//            }
//
//        }
    }

    private int refresh(AtomicInteger index) {
        if (index.get() > 9) {
            index.set(9);
        }
        int small = GlobalConf.smallMC[index.get()];
        int medium = GlobalConf.mediumMC[index.get()];
        int large = GlobalConf.largeMC[index.get()];
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
        // System.out.println("s:" + small + " m:" + medium + " l" + large + "weight" + result);
        return result;
    }
}

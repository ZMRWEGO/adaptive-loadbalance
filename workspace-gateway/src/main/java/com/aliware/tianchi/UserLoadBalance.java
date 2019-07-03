package com.aliware.tianchi;

import com.aliware.tianchi.util.MyConf;
import com.aliware.tianchi.util.MyList;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.AsyncRpcResult;
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
            if ((current - MyConf.TIME.get()) / 1000 >= 30) {
                if (isFormal.compareAndSet(false, true)) {

                    MyConf.TIME.set(current);
                    // index.getAndAdd(1);
                    System.out.println(stampToDate(current) + ":预热阶段结束，第一次更新最大并发数");
                    //x = refresh(index);
                }
            }
            x = randomOnWeight();

        } else {
            if ((current - MyConf.TIME.get()) / 1000 >= 1) {
                MyConf.TIME.set(current);

                getIndex();
            }
            x = refresh();
        }
        //System.out.println("ZCL-DEBUG:" + x + isFormal.get());
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
                MyConf.TIME = new AtomicLong(System.currentTimeMillis());
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

    private int refresh() {
        int small = MyConf.smallMC[index.get()];
        int medium = MyConf.mediumMC[index.get()];
        int large = MyConf.largeMC[index.get()];
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
        //System.out.println("s:" + small + " m:" + medium + " l" + large + "weight" + result);
        return result;
    }

    public void getIndex() {
        long small = MyConf.smallSumTime.get() / MyConf.smallNUM.get();
        long medium = MyConf.mediumSumTime.get() / MyConf.mediumNUM.get();
        long large = MyConf.largeSumTime.get() / MyConf.largeNUM.get();
        System.out.println(small+" -"+medium+"-"+large);
        if (small > Math.max(large,medium)) {
            index.set(0);
            System.out.println("更新到small");
        } else if (medium > Math.max(large,small)) {
            index.set(1);
            System.out.println("更新到medium");
        } else if (large > Math.max(small,medium)) {
            index.set(2);
            System.out.println("更新到large");
        } else {

        }
        MyConf.smallSumTime.set(0);
        MyConf.largeSumTime.set(0);
        MyConf.mediumSumTime.set(0);
        MyConf.smallNUM.set(1);
        MyConf.mediumNUM.set(1);
        MyConf.largeNUM.set(1);
    }

    public String stampToDate(long s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(s);
        res = simpleDateFormat.format(date);
        return res;
    }
}

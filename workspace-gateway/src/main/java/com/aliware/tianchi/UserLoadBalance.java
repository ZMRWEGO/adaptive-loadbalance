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

     private  MyList circle ;
    private final AtomicBoolean init = new AtomicBoolean(false);
    private final AtomicInteger x = new AtomicInteger(0);
    @Override
    public <T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
        //refresh();
        if (!init.get()) {
            init.compareAndSet(false, true);
            setCircle();
        } else {
            if(MyConf.EXCEPTION.get()){
                if (MyConf.EXCEPTION.compareAndSet(true, false)) {
                    circle = circle.next;
                    x.set(circle.value);
                }
            }
        }
        System.out.println("zcl:weight:"+x);
        return invokers.get(x.get());
    }

    private int randomOnWeight(int index) {
        int small = MyConf.smallMC[index];
        int large = MyConf.largeMC[index];
        int medium = MyConf.mediumMC[index];
        int[] weightArray = new int[]{small, medium, large};
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
        return map.get(treeMap.floorEntry(num).getValue());

    }

    public String stampToDate(long s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(s);
        res = simpleDateFormat.format(date);
        return res;
    }

    public void setCircle(){
        circle = new MyList(0);
        circle.add(1).add(2);
        circle.next.next.next = circle;
    }

}



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
        if (GlobalConf.large >= Math.max(GlobalConf.medium, GlobalConf.small)) {
            return invokers.get(2);
        } else if (GlobalConf.medium >= GlobalConf.small) {
            return invokers.get(1);
        } else {
            return invokers.get(0);
        }
    }
}

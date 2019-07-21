package com.aliware.tianchi;

import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.common.store.DataStore;
import org.apache.dubbo.remoting.exchange.Request;
import org.apache.dubbo.remoting.transport.RequestLimiter;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author daofeng.xjf
 *
 * 服务端限流 可选接口 在提交给后端线程池之前的扩展，可以用于服务端控制拒绝请求
 */
public class TestRequestLimiter implements RequestLimiter {

    private static final Logger logger = LoggerFactory.getLogger(TestRequestLimiter.class);
    private AtomicBoolean init = new AtomicBoolean(false);
    /**
     * @param request 服务请求
     * @param activeTaskCount 服务端对应线程池的活跃线程数
     * @return false 不提交给服务端业务线程池直接返回，客户端可以在 Filter 中捕获 RpcException true 不限流
     */
    @Override
    public boolean tryAcquire(Request request, int activeTaskCount) {
        //第一次获取最大线程数
        isInit();
        try {
            if(MyConf.active.getAndSet(activeTaskCount)==MyConf.max){
                Thread.sleep(10);
            }
        }catch (Exception e)
        {
            logger.error(e);
        }
        return true;
    }


    public void isInit() {
        if (!init.get()) {
            if (init.compareAndSet(false, true)) {
                //获取最大线程数配置
                //通过spi获取最大线程数
                DataStore dataStore = ExtensionLoader.getExtensionLoader(DataStore.class).getDefaultExtension();
                Map<String, Object> executors = dataStore.get(Constants.EXECUTOR_SERVICE_COMPONENT_KEY);
                for (Map.Entry<String, Object> map : executors.entrySet()) {
                    ExecutorService executor = (ExecutorService) map.getValue();
                    if (executor instanceof ThreadPoolExecutor) {
                        ThreadPoolExecutor tp = (ThreadPoolExecutor) executor;
                        MyConf.max = tp.getMaximumPoolSize();
                        //线程池
                    }
                }
            }
        }
    }
}

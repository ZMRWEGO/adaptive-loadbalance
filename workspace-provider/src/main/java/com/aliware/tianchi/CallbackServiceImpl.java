package com.aliware.tianchi;

import java.util.Date;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.common.store.DataStore;
import org.apache.dubbo.rpc.listener.CallbackListener;
import org.apache.dubbo.rpc.service.CallbackService;

/**
 * @author daofeng.xjf
 * <p>
 * 服务端回调服务 可选接口 用户可以基于此服务，实现服务端向客户端动态推送的功能
 */
public class CallbackServiceImpl implements CallbackService {

    Map<String, Long> map = new ConcurrentHashMap<>();
    static volatile long maximumPoolSize;

    public CallbackServiceImpl() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (!listeners.isEmpty()) {
                    for (Map.Entry<String, CallbackListener> entry : listeners.entrySet()) {
                        try {
                            String name = System.getProperty("quota");
                            //通过spi获取最大线程数
                            DataStore dataStore = ExtensionLoader.getExtensionLoader(DataStore.class).getDefaultExtension();
                            Map<String, Object> executors = dataStore.get(Constants.EXECUTOR_SERVICE_COMPONENT_KEY);
                            for (Map.Entry<String, Object> map : executors.entrySet()) {
                                ExecutorService executor = (ExecutorService) map.getValue();
                                if (executor instanceof ThreadPoolExecutor) {
                                    ThreadPoolExecutor tp = (ThreadPoolExecutor) executor;
                                    maximumPoolSize = tp.getMaximumPoolSize();
                                    //线程池
                                }
                            }
                            MyConf.max = maximumPoolSize;
                            map.put(name, maximumPoolSize);
                            entry.getValue()
                                .receiveServerMsg(map.toString());
                        } catch (Throwable t1) {
                            listeners.remove(entry.getKey());
                        }
                    }
                }
            }
        }, 0, 5000);
    }

    private Timer timer = new Timer();

    /**
     * key: listener type value: callback listener
     */
    private final Map<String, CallbackListener> listeners = new ConcurrentHashMap<>();

    @Override
    public void addListener(String key, CallbackListener listener) {
        listeners.put(key, listener);
    }

}


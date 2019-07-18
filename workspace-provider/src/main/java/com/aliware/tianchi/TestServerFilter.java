package com.aliware.tianchi;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.extension.ExtensionLoader;
import org.apache.dubbo.common.store.DataStore;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.RpcStatus;

/**
 * @author daofeng.xjf
 *
 * 服务端过滤器 可选接口 用户可以在服务端拦截请求和响应,捕获 rpc 调用时产生、服务端返回的已知异常。
 */
@Activate(group = Constants.PROVIDER)
public class TestServerFilter implements Filter {

    private final AtomicBoolean init = new AtomicBoolean(false);
    private volatile ThreadPoolExecutor myPool;
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try {
            Result result = invoker.invoke(invocation);
            return result;
        } catch (Exception e) {
            throw e;
        }

    }

    @Override
    public Result onResponse(Result result, Invoker<?> invoker, Invocation invocation) {
        getThreadPool();
        result.setAttachment("activeTask", String.valueOf(myPool.getCompletedTaskCount()));
        return result;

    }

    public void getThreadPool() {
        if (!init.get()) {
            if (init.compareAndSet(false, true)) {
                DataStore dataStore = ExtensionLoader.getExtensionLoader(DataStore.class).getDefaultExtension();
                Map<String, Object> executors = dataStore.get(Constants.EXECUTOR_SERVICE_COMPONENT_KEY);
                for (String i : executors.keySet()) {
                    ExecutorService executor = (ExecutorService) executors.get(i);
                    if (executor instanceof ThreadPoolExecutor) {
                        myPool = (ThreadPoolExecutor) executor;
                    }
                }
            }
        }
    }

}

package com.aliware.tianchi;

import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;

/**
 * @author daofeng.xjf
 *
 * 服务端过滤器 可选接口 用户可以在服务端拦截请求和响应,捕获 rpc 调用时产生、服务端返回的已知异常。
 */
@Activate(group = Constants.PROVIDER)
public class TestServerFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(TestServerFilter.class);

    long useful = 0;
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try {
            //这里是同步调用
            Result result = invoker.invoke(invocation);
            //     可用线程数/上一次rtt
            if (MyConf.max.get() < MyConf.active.decrementAndGet()) {
                MyConf.max.set(MyConf.poolSize.get());
            }
            long remain = MyConf.max.get() - MyConf.active.get();
//            logger.info("rtt:" +rtt);
//            MyConf.active--;
            result.setAttachment("useful", String.valueOf(remain));
            return result;
        } catch (Exception e) {
            throw e;
        }

    }

    @Override
    public Result onResponse(Result result, Invoker<?> invoker, Invocation invocation) {
        if (result.getException() != null) {
            MyConf.max.set(MyConf.active.get());
        }
        return result;
    }

}

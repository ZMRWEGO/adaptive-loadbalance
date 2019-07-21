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
            long start = System.currentTimeMillis();
            //这里是同步调用
            Result result = invoker.invoke(invocation);
            long rtt = (System.currentTimeMillis() - start);
            //     可用线程数/上一次rtt
            long remain = MyConf.max - MyConf.active.decrementAndGet();
            if (remain < 0.8 * MyConf.max) {
                 useful = remain * 550 / rtt;
            } else {
                useful = remain;
            }
//            logger.info("rtt:" +rtt);
//            MyConf.active--;
            result.setAttachment("useful", String.valueOf(useful));
            return result;
        } catch (Exception e) {
            throw e;
        }

    }

    @Override
    public Result onResponse(Result result, Invoker<?> invoker, Invocation invocation) {
        return result;
    }

}

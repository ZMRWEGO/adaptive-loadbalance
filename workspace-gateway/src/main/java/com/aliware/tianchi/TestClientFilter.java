package com.aliware.tianchi;

import com.aliware.tianchi.util.GlobalConf;
import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;

/**
 * @author daofeng.xjf
 *
 * 客户端过滤器 可选接口 用户可以在客户端拦截请求和响应,捕获 rpc 调用时产生、服务端返回的已知异常。
 */
@Activate(group = Constants.CONSUMER)
public class TestClientFilter implements Filter {
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
        //解析Result  RpcResult [result=-870665090, exception=null]
        //解析Result  RpcResult [result=-870665090, exception=null]
        String host = invoker.getUrl().getHost();
        if (host.equals("provider-large")) {
            GlobalConf.largeActive = Integer.valueOf(result.getAttachment("activeTask")) / 13;
        } else if (host.equals("provider-medium")) {
            GlobalConf.mediumActive = Integer.valueOf(result.getAttachment("activeTask")) / 9;
        } else {
            GlobalConf.smallActive = Integer.valueOf(result.getAttachment("activeTask")) / 4;
        }

        return result;
    }
}

package com.aliware.tianchi;

import com.aliware.tianchi.util.MyConf;
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
 * 客户端过滤器
 * 可选接口
 * 用户可以在客户端拦截请求和响应,捕获 rpc 调用时产生、服务端返回的已知异常。
 */
@Activate(group = Constants.CONSUMER)
public class TestClientFilter implements Filter {

    long current ;
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try{
            current = System.currentTimeMillis();
            Result result = invoker.invoke(invocation);
            return result;
        }catch (Exception e){
            throw e;
        }

    }

    @Override
    public Result onResponse(Result result, Invoker<?> invoker, Invocation invocation) {
        //解析Result  RpcResult [result=-870665090, exception=null]
        long time = System.currentTimeMillis() - current;
        switch (invoker.getUrl().getHost()) {
            case "provider-small": {
                MyConf.smallSumTime.getAndAdd(time);
                MyConf.smallNUM.getAndAdd(1);

            }
            case "provider-medium":{
                MyConf.mediumSumTime.getAndAdd(time);
                MyConf.mediumNUM.getAndAdd(1);
            }
            case "provider-large": {
                MyConf.largeSumTime.getAndAdd(time);
                MyConf.largeNUM.getAndAdd(1);
            }
        }

        System.out.println(invoker.getUrl().getHost()+""+time);

        return result;
    }
}

package com.aliware.tianchi;

import com.aliware.tianchi.util.GlobalConf;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.rpc.listener.CallbackListener;

/**
 * @author daofeng.xjf
 *
 * 客户端监听器 可选接口 用户可以基于获取获取服务端的推送信息，与 CallbackService 搭配使用
 */
public class CallbackListenerImpl implements CallbackListener {
    private static final Logger logger = LoggerFactory.getLogger(CallbackListenerImpl.class);

    @Override
    public void receiveServerMsg(String msg) {
        String[] map = msg.substring(1, msg.length() - 1).split("=");
        String name = map[0];
        String max = map[1];
        //logger.info("map:"+msg);
        if (name.equals("large")) {
            GlobalConf.largeMax = Integer.valueOf(max);
        } else if (name.equals("medium")) {
            GlobalConf.mediumMax = Integer.valueOf(max);
        } else {
            GlobalConf.smallMax = Integer.valueOf(max);
        }
    }

}

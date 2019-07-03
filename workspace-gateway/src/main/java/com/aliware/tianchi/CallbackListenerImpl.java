package com.aliware.tianchi;

import com.aliware.tianchi.util.MyConf;
import org.apache.dubbo.rpc.listener.CallbackListener;

/**
 * @author daofeng.xjf
 *
 * 客户端监听器 可选接口 用户可以基于获取获取服务端的推送信息，与 CallbackService 搭配使用
 */
public class CallbackListenerImpl implements CallbackListener {

    @Override
    public void receiveServerMsg(String msg) {
        System.out.println("receive msg from server :" + msg+ MyConf.NUM.getAndAdd(1));
        String[] s1 = msg.split(",");
        if (s1.length == 4) {
            String name = s1[3].split("=")[1];
            String exception = s1[2].split("=")[1];
            name = name.substring(0, name.length() - 1);
            exception = exception.substring(0, exception.length() - 1);
            switch (name) {
                case "small":
                    if (!exception.equals("null")) {
                        MyConf.smallException.set(true);
                    }
                case "large":
                    if (!exception.equals("null")) {
                        MyConf.largeException.set(true);
                    }
                case "medium":
                    if (!exception.equals("null")) {
                        MyConf.mediumException.set(true);
                    }
            }
        }
    }

}

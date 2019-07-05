package com.aliware.tianchi;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yiting
 * @version 1.0
 * @date 2019/7/2 17:03
 */
public class MyConf {

    public static LinkedList<Integer> queue = new LinkedList<>();

    public static AtomicInteger active=new AtomicInteger(0);
    public static String RESPONSE;
    public static String REQUEST;

}

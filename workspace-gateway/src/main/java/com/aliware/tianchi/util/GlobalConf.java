package com.aliware.tianchi.util;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author yiting
 * @version 1.0
 * @date 2019/7/1 11:15
 */
public class GlobalConf {
    public static volatile int smallActive = 0;
    public static volatile int mediumActive = 0;
    public static volatile int largeActive = 0;
}

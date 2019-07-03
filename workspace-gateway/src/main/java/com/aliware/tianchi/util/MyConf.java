package com.aliware.tianchi.util;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author yiting
 * @version 1.0
 * @date 2019/7/1 11:15
 */
public class MyConf {

    public static int[] smallMC = {150, 200, 200, 150};
    public static int[] mediumMC = {500, 450, 500, 430};
    public static int[] largeMC = {650, 650, 600, 630};
    public static AtomicBoolean smallException = new AtomicBoolean(false);
    public static AtomicBoolean mediumException = new AtomicBoolean(false);
    public static AtomicBoolean largeException = new AtomicBoolean(false);
    public static AtomicInteger NUM = new AtomicInteger(0);
    public static int WEIGHT=3;
}

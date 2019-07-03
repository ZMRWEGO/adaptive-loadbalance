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

    public static int[] smallMC = {130, 200, 200, 150};
    public static int[] mediumMC = {500, 430, 500, 500};
    public static int[] largeMC = {650, 650, 530, 650};
    public static AtomicLong smallSumTime = new AtomicLong(0);
    public static AtomicLong mediumSumTime = new AtomicLong(0);
    public static AtomicLong largeSumTime = new AtomicLong(0);
    public static AtomicInteger smallNUM = new AtomicInteger(0);
    public static AtomicInteger mediumNUM = new AtomicInteger(0);
    public static AtomicInteger largeNUM = new AtomicInteger(0);
    public static int WEIGHT=3;
    public static AtomicLong TIME = new AtomicLong(0);
    public static AtomicBoolean EXCEPTION = new AtomicBoolean(false);
}

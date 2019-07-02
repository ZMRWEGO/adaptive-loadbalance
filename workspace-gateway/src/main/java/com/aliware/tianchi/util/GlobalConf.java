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

    public static int[] smallMC = {150, 150, 150, 150, 150, 150, 150, 150, 150, 150};
    public static int[] mediumMC = {470, 470, 500, 500, 500, 500, 500, 500, 470, 470};
    public static int[] largeMC = {650, 650, 650, 650, 650, 650, 620, 620, 650, 650};
    public static int PROVIDER;
    public static AtomicLong TIME;
}

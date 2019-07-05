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
    public static int[] mediumMC = {500, 500, 500, 500, 500, 500, 500, 500, 500, 500};
    public static int[] largeMC = {650, 650, 650, 650, 650, 650, 650, 650, 650, 650};
    public static double[] smallRT = {44.36, 56.27, 36.46, 58.61, 36.99, 42.91, 26.19, 36.93, 32.29, 64.74};
    public static double[] mediumRT = {62.07, 47.13, 39.27, 46.74, 50.31, 43.85, 34.20, 48.00, 36.01, 66.15};
    public static double[] largeRT = {56.48, 48.14, 55.13, 58.96, 36.03, 35.98, 36.26, 40.75, 37.96, 70.73};
    public static int PROVIDER;
    public static int smallActive = 0;
    public static int mediumActive = 0;
    public static int largeActive = 0;
    public static AtomicLong TIME;
}

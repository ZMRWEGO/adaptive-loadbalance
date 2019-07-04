package com.aliware.tianchi.util;

/**
 * @author yiting
 * @version 1.0
 * @date 2019/7/3 14:53
 */
public class MyList {

   public int value;
    public MyList(int value) {
        this.value = value;
    }

    public MyList add(int value) {
        if(this!=null){
            this.next = new MyList(value);
            return this.next;
        }else {
            throw new NullPointerException();
        }
    }
   public MyList next;
}

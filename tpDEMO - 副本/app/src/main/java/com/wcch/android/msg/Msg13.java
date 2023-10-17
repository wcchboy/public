package com.wcch.android.msg;

/**
 * @author created by Lzq
 * @time：2022/3/24
 * @Des： type: 1、app在前台， 2、app在后台， 3、屏幕关屏， 4、屏幕点亮， 5、手机已解锁
 */
public class Msg13 extends Msg {
    private int type;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

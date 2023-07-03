package com.wcch.android.msg;

/**
 * @author created by Lzq
 * @time：2022/3/24
 * @Des： S->c : 服务端通知客户端亮度调节（服务端发送hid后发送如下cmd告知客户端，需要在cmd1 saving：1才可操作）
 *       c->s :  当前设备亮度值 0-100
 */
public class Msg14 extends Msg{
    private int brightness;

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }
}

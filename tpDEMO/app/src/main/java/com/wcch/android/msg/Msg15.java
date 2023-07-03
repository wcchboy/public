package com.wcch.android.msg;

/**
 * @author created by Lzq
 * @time：2022/3/24
 * @Des：手机端屏幕旋转消息 ori: 0 home键在下方， 1 home键在右侧， 2 home键在上方 3 home键在左侧
 */
public class Msg15 extends Msg{
    private int ori;

    public int getOri() {
        return ori;
    }

    public void setOri(int ori) {
        this.ori = ori;
    }
}

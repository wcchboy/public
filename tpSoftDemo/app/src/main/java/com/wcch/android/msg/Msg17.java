package com.wcch.android.msg;

/**
 * @author created by Lzq
 * @time：2022/3/31
 * @Des：
 */
public class Msg17 extends Msg{

//    private String msg;
    private String taskId;
    //0:投屏达到最大数量   1：遥控达到最大数量   2：解码器启动失败   3:遥控编码失败   4:当前不在Android信源通道下  5:retry失败
    private int code;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}

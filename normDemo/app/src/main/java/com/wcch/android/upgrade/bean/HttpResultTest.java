package com.wcch.android.upgrade.bean;

/**
 * Create by RyanWang
 *
 * @date
 * @Dis
 */
public class HttpResultTest<T> extends BaseEntity {
    public int code;
    public int ret;
    private boolean isSuccess;
    private T data;
    private String msg;

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public T getResult() {
        return data;
    }


    public void setResult(T result) {
        this.data = result;

    }

    public boolean isSuccess() {
        return code == 200;
    }

    public int getCode() {
        return code;
    }

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }
}

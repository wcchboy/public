package com.wcch.android.upgrade.bean;

/**
 * Create by RyanWang
 *
 * @date
 * @Dis
 */
public class HttpResult<T> extends BaseEntity {
    public int code;
    private boolean isSuccess;
    private T result;
    private String msg;

    public void setMsg(String msg) {
        this.msg = msg;
    }


    public T getResult() {
        return result;
    }


    public void setResult(T result) {
        this.result = result;

    }

    public boolean isSuccess() {
        return code == 200;
    }

    public int getCode() {
        return code;
    }
}

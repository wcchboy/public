package com.wcch.android.upgrade.bean;

import java.util.List;

/**
 * Create by RyanWang
 *
 * @date
 * @Dis
 */
public class EntityTest {
    private int ret;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    private int code;
    private String msg;
    private List<ResTest> data;

    public int getRet() {
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<ResTest> getData() {
        return data;
    }

    public void setData(List<ResTest> data) {
        this.data = data;
    }
}

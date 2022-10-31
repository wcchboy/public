package com.wcch.serialport.bean;


/**
 * Description
 *
 * @author RyanWang
 * 2020/11/17, RyanWang,Create file
 * @deprecated 暂未使用
 */
public class ResultBean {

    /**
     * 原始结果报文
     */
    public String mRawResultStr;
    /**
     * 原始结果报文
     */
    public byte[] mRawResultBytes;

    /**
     * 确认码，成功码和错误码返回的字段
     */
    public String mResultCode;

    public String getResultCode() {
        return mResultCode;
    }

    @Override
    public String toString() {
        return "ResultBean{" +
                "mRawResultStr='" + mRawResultStr + '\'' +
                ", mRawResultBytes=" + getResultCode() +
                ", mResultCode=" + mResultCode +
                '}';
    }

    public String getmRawResultStr() {
        return mRawResultStr;
    }

    public void setRawResultStr(String mRawResultStr) {
        this.mRawResultStr = mRawResultStr;
    }

    public byte[] getmRawResultBytes() {
        return mRawResultBytes;
    }

    public void setRawResultBytes(byte[] mRawResultBytes) {
        this.mRawResultBytes = mRawResultBytes;
    }

    public void setResultCode(String mResultCode) {
        this.mResultCode = mResultCode;
    }
}

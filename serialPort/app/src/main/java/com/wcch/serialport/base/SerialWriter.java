package com.wcch.serialport.base;

import com.wcch.serialport.utils.ConvertUtils;
import com.wcch.serialport.utils.LogUtils;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author RyanWang
 * 2022/10/30, RyanWang, Create file
 * 串口写入数据
 */
public class SerialWriter {
    private final String TAG = "SerialWriter";
    private final OutputStream mOutputStream;

    public SerialWriter(OutputStream outputStream) {
        mOutputStream = outputStream;
    }

    public void write(String data) {
        LogUtils.d(TAG, "SerialWriter write 的数据为:" + data);
        try {
            write(ConvertUtils.hexString2Bytes(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void write(byte[] data) {
        try {
            mOutputStream.write(data);
            mOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

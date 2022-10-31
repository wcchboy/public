package com.wcch.serialport.inter;

/**
 * @author RyanWang
 * 2022/10/30, RyanWang, Create file
 * 打开串行端口侦听器
 */
public interface OnOpenSerialPortListener {

    void onResult(boolean success, String errorMsg);
}

package com.wcch.serialport.inter;

/**
 * Description
 *
 * @author RyanWang
 * 2020/11/11, RyanWang,Create file
 * 串行数据监听器
 */
public interface SerialDataListener {

    void onDataReceived(String data);
}

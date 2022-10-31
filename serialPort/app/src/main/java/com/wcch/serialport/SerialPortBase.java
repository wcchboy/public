package com.wcch.serialport;

import com.wcch.serialport.base.SerialReader;
import com.wcch.serialport.base.SerialWriter;
import com.wcch.serialport.base.ThreadHelper;
import com.wcch.serialport.inter.OnOpenSerialPortListener;
import com.wcch.serialport.inter.SerialDataListener;
import com.wcch.serialport.utils.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * serialport
 * <p>
 * Created by RyanWang on 2022/10/31
 * Copyright © 2022年 IGRS. All rights reserved.
 * <p>
 * Describe:
 */
public class SerialPortBase implements SerialDataListener {
    private static final String TAG = SerialPortBase.class.getSimpleName();


    private List<SerialDataListener> mListenerList = new ArrayList<>();

    public SerialPortBase() {
    }


    /**
     * 注册串行数据监听器
     * @param listener
     */
    public synchronized void registerListener(SerialDataListener listener){
        mListenerList.add(listener);
    }

    public synchronized void unRegisterListener(SerialDataListener listener){
        mListenerList.remove(listener);
    }

    @Override
    public void onDataReceived(String data) {
        LogUtils.d(TAG,"onDataReceived data:"+data);
        synchronized (this){
            for (SerialDataListener listener : mListenerList) {
                //返回数据后面约定好之后我在处理返回，这里没有处理发送的原始数据
                listener.onDataReceived(data);
            }
        }
    }
}

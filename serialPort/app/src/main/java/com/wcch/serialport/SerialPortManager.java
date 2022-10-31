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
 * 
 * @author RyanWang
 * 2022/10/30, RyanWang, Create file
 * 串口管理类
 */
public class SerialPortManager extends SerialPortBase {

    private static final String TAG = SerialPortManager.class.getSimpleName();

    private volatile SerialPort mPort;

    private SerialWriter mWriter;


    public SerialPortManager() {
    }

    public void openSerialPort(String path, int baudrate, int databits, int parity, int stopbits, int flag, int delayTime, OnOpenSerialPortListener listener){
        ThreadHelper.getInstance().execute(()->{
            if(mPort==null){
                LogUtils.d(TAG, "init serial port, open it ");
                LogUtils.d(TAG, "path ："+path+"baudrate ："+baudrate+"databits ："+databits+ "parity ："+parity+"stopbits ："+stopbits+"flag ："+flag);
                try {
                    mPort =new SerialPort(new File(path), baudrate, databits,parity,stopbits,flag);
                    mWriter = new SerialWriter(mPort.getOutputStream());
                    if (delayTime>0){
                        ThreadHelper.getInstance().execute(new SerialReader(mPort.getInputStream(),this,delayTime));
                    }else{
                        ThreadHelper.getInstance().execute(new SerialReader(mPort.getInputStream(),this));
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    listener.onResult(false,e.getMessage());
                    return;
                }
            }else{
                LogUtils.d(TAG,"serial prot is already opened");
            }
            listener.onResult(true,"");
        });
    }

    

    public void closeSerialPort(){
        if(mPort!=null){
            LogUtils.d(TAG, "closeSerialPort: ");
            mPort.closePort();
            mPort=null;
        }else{
            LogUtils.d(TAG, "closeSerialPort: serial port is already closed");
        }
    }

    public void write(String data){
        if(mPort==null){
            LogUtils.e(TAG, "write: serial port is not opened,please check serial port .");
            return;
        }
        if(mPort.getOutputStream()==null){
            LogUtils.e(TAG, "write: serial outputstream is null,please check serial port .");
            return;
        }
        mWriter.write(data);
    }
    public void write(byte[] data){
        if(mPort==null){
            LogUtils.e(TAG, "write: serial port is not opened,please check serial port .");
            return;
        }
        if(mPort.getOutputStream()==null){
            LogUtils.e(TAG, "write: serial outputstream is null,please check serial port .");
            return;
        }
        mWriter.write(data);
    }
}

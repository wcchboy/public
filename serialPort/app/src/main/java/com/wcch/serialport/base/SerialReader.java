package com.wcch.serialport.base;

import android.nfc.Tag;
import android.os.SystemClock;
import android.util.Log;

import com.wcch.serialport.inter.SerialDataListener;
import com.wcch.serialport.utils.ConvertUtils;
import com.wcch.serialport.utils.LogUtils;
import com.wcch.serialport.utils.StringUtil;


import java.io.IOException;
import java.io.InputStream;

/**
 * SerialReader
 *
 * @author RyanWang
 * 2022/10/30, RyanWang, Create file
 * 串口读取
 */
public class SerialReader implements Runnable {
    private final InputStream mInputStream;
    private final SerialDataListener mReceiver;
    private boolean mIsInterrupted = false;
    //如不设置间隔时间，则使用这个默认时间，雷达读取间隔时间 ms
    private  int readInterval = 50;

    public SerialReader(InputStream inputStream, SerialDataListener receiver) {
        mInputStream = inputStream;
        mReceiver = receiver;
    }

    /**
     *
     * @param inputStream
     * @param receiver
     * @param readInterval 默认读取是50ms间隔，如需改变在这里设置
     */
    public SerialReader(InputStream inputStream, SerialDataListener receiver,int readInterval) {
        mInputStream = inputStream;
        mReceiver = receiver;
        this.readInterval = readInterval;
    }

    @Override
    public void run() {
        InputStream is = mInputStream;
        int available;
        int first;
        try {
            while (!mIsInterrupted
                    && is != null
                    && (first = is.read()) != -1) {
                do {
                    available = is.available();
                    SystemClock.sleep(readInterval);
                } while (available != is.available());
                available = is.available();
                byte[] bytes = new byte[available + 1];
                is.read(bytes, 1, available);
                bytes[0] = (byte) (first & 0xFF);
                report(bytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    private void report(final byte[] data) {
        ThreadHelper.getInstance().execute(() -> {
            LogUtils.d("report:",bytes2hex(data));
            String temp = ConvertUtils.bytes2HexString(data);
            try {
                if (StringUtil.isNotEmpty(temp)) {
                    mReceiver.onDataReceived(temp);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


    /**
     * @param bytes 出入的byte数组
     * @return 返回一个22一组的string
     */
    public static String bytes2hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        String tmp;
        sb.append("[");
        for (byte b : bytes) {
            // 将每个字节与0xFF进行与运算，然后转化为10进制，然后借助于Integer再转化为16进制
            tmp = Integer.toHexString(0xFF & b);
            if (tmp.length() == 1) {
                //只有一位的前面补个0
                tmp = "0" + tmp;
            }
            //每个字节用空格断开
            sb.append(tmp).append(" ");
        }
        //删除最后一个字节后面对于的空格
        sb.delete(sb.length() - 1, sb.length());
        sb.append("]");
        return sb.toString();
    }
}

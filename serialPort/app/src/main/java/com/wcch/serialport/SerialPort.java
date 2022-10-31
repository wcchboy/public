/*
 * Copyright 2009 Cedric Priscal
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wcch.serialport;


import com.wcch.serialport.utils.LogUtils;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author RyanWang
 * 2022/10/30, RyanWang, Create file
 * 串口 端口
 */
public class SerialPort {
    private static final String TAG = "SerialPort";

    /**
     * Do not remove or rename the field mFd: it is used by native method close();
     */
    private FileDescriptor mFd;
    private FileInputStream mFileInputStream;
    private FileOutputStream mFileOutputStream;

    /**
     * 设置串口数据，校验位,速率，停止位
     *
     * @param path     类型 String数据位 串口路径
     * @param baudrate 类型 int数据位 波特率
     * @param databits 类型 int数据位 取值 位7或8
     * @param stopbits 类型 int 停止位 取值1 或者 2
     * @param parity   类型 char 校验类型 取值N ,E, O
     * @return
     */
    public SerialPort(File path, int baudrate, int databits, int parity, int stopbits, int flag) throws SecurityException, IOException {
        LogUtils.d(TAG, "SerialPort start open ");
        mFd = open(path.getAbsolutePath(), baudrate, databits, parity, stopbits, flag);
        if (mFd == null) {
            LogUtils.d(TAG, "SerialPort start error open null");
            throw new IOException();
        }
        LogUtils.d(TAG, "SerialPort start finish");
        mFileInputStream = new FileInputStream(mFd);
        mFileOutputStream = new FileOutputStream(mFd);
    }

    public InputStream getInputStream() {
        return mFileInputStream;
    }

    public OutputStream getOutputStream() {
        return mFileOutputStream;
    }

    public void closePort() {
        try {
            if (mFileInputStream != null) {
                mFileInputStream.close();
            }
            if (mFileOutputStream != null) {
                mFileOutputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        close();
    }

    /**
     * 设置串口数据，校验位,速率，停止位
     *
     * @param path     类型 String数据位 串口路径
     * @param baudrate 类型 int数据位 波特率
     * @param databits 类型 int数据位 取值 位7或8
     * @param stopbits 类型 int 停止位 取值1 或者 2
     * @param parity   类型 char 校验类型 取值N ,E, O即不校验0，奇校验：1，偶校验：2
     * @return
     */
    private native static FileDescriptor open(String path, int baudrate, int databits, int parity, int stopbits, int flags);

    private native void close();

    static {
        System.loadLibrary("serial");
    }
}

package com.wcch.android.soft.bonjourdiscover.module.bonjour;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.igrs.betotablet.soft.model.LoginVo;
import com.igrs.betotablet.soft.util.AndroidUtil;
import com.igrs.betotablet.soft.util.ThinkvisionUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

public class TcpSocketManager {
    Socket tcpSocket;
    String host;
    Context context;

    final static int MSG_REPORT = 100;
    BufferedReader bufferedReader;
    Handler reportHandler;

    public TcpSocketManager(Context context, final String host) {
        this.context = context;
        this.host = host;
        HandlerThread handlerThread = new HandlerThread("TcpSocketManagerThread");
        handlerThread.start();
        reportHandler = new Handler(handlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_REPORT:
                        reportToServer();
                        break;
                }

            }
        };
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        tcpSocket = new Socket(host, 12345);//
                        tcpSocket.setTcpNoDelay(true);

                        reportToServer();
                        bufferedReader = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));

                        while (bufferedReader != null) {
                            String jsonStr = bufferedReader.readLine();
                            if (jsonStr != null) {
                                Map map = new Gson().fromJson(jsonStr, Map.class);
                                if (map != null) {
                                    Double error_code = (Double) map.get("error_code");
                                    if (error_code == 0) {
                                        reportHandler.removeMessages(MSG_REPORT);
                                        close();
                                        break;
                                    }
                                }
                            }
//                            Thread.sleep(1000);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reportToServer() {
        try {
            OutputStream os = tcpSocket.getOutputStream();
            os.write(getLoginJson(context).getBytes());
            reportHandler.sendEmptyMessageDelayed(MSG_REPORT, 3000); //3秒
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (tcpSocket != null) {
                tcpSocket.close();
                tcpSocket = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String getLoginJson(Context context) {
        LoginVo loginVo = new LoginVo();
        loginVo.hardware_id = AndroidUtil.getAndroidID(context);
        String name = AndroidUtil.getDeviceName(context);
        String desc = null;
        if (TextUtils.isEmpty(name)) {
            loginVo.name = AndroidUtil.getSystemModel();//Build.MODEL+"_"+ loginVo.hardware_id;//
        } else {
            loginVo.name = name;
        }
        if (desc == null) {
            loginVo.desc = "品牌：" + AndroidUtil.getDeviceBrand() + " 型号: " + AndroidUtil.getSystemModel();
        } else {
            loginVo.desc = desc;
        }
        loginVo.ip = AndroidUtil.getLocalIpAddress();
        loginVo.os = "Android " + AndroidUtil.getSystemVersion();
        loginVo.version = AndroidUtil.getVersionName(context);
        String path = Environment.getExternalStorageDirectory().getAbsolutePath();
        loginVo.free_space = "" + AndroidUtil.getFreeSpace(path);
        loginVo.total_space = "" + AndroidUtil.getTotalSpace(path);
        loginVo.sn = "";
        loginVo.hotspot = ThinkvisionUtil.getAPName(context);
        return new Gson().toJson(loginVo);
    }
}

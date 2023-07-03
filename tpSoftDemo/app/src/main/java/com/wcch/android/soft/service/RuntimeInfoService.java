package com.wcch.android.soft.service;


import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.igrs.sml.RuntimeInfo;
import com.igrs.sml.tcp.TcpConst;
import com.igrs.sml.util.L;
import com.wcch.android.R;
import com.wcch.android.soft.util.NetWorkUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;

public class RuntimeInfoService extends Service {
    //客户端分辨率
    private int l_width = 0;
    private int l_height = 0;
    private boolean isExit = false;
    private WindowManager windowManager;
    private View displayView;

    private String lost = new String();
    private String delay = new String();
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onCreate() {
        super.onCreate();
        L.i("RuntimeInfoService->onCreate this:" + this.hashCode());
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point outPoint = new Point();
        display.getRealSize(outPoint);
        l_width = outPoint.x;
        l_height = outPoint.y;
        if (outPoint.x > outPoint.y) {
            l_width = outPoint.x;
            l_height = outPoint.y;
        } else {
            l_width = outPoint.y;
            l_height = outPoint.x;
        }
        showFloatingWindow();
        new Thread(){
            @Override
            public void run() {
                super.run();
                while (!isExit){
                    try {
                        Process p = Runtime.getRuntime().exec("ping -c 6 " + RuntimeInfo.ser_ip);
                        BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
                        String str = new String();
                        while ((str = buf.readLine()) != null) {
                            if (str.contains("packet loss")) {
                                int i = str.indexOf("received");
                                int j = str.indexOf("%");
                                lost = str.substring(i + 10, j + 1);
                            }
                            if (str.contains("avg")) {
                                int i = str.indexOf("/", 20);
                                int j = str.indexOf(".", i);
                                delay = str.substring(i + 1, j);
                                delay = delay + "ms";
                            }
                            //L.i("丢包率:" + lost+" 延迟:" + delay);
                        }
                    } catch (Exception e) {
                    }
                }
                L.i("Runtime ser thead out");
                stopSelf();
            }
        }.start();
    }

    private TextView txt_info;

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void showFloatingWindow() {
        if (displayView != null) {
            windowManager.removeView(displayView);
        }
        if (Build.VERSION.SDK_INT >= 23 && Settings.canDrawOverlays(this)) {
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            displayView = layoutInflater.inflate(R.layout.float_txt, null);
            displayView.setFocusable(false);
            displayView.setClickable(false);
            txt_info = displayView.findViewById(R.id.txt_info);
            txt_info.setFocusable(false);
            txt_info.setClickable(false);
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            }
            layoutParams.format = PixelFormat.RGBA_8888;
            layoutParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.alpha=0.7f;//android 12后需要，不然touch不能穿透
            windowManager.addView(displayView, layoutParams);
        }
        uiHandler.sendEmptyMessageDelayed(0, 2000);
    }

    private Handler uiHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            showInfo();
            if (!isExit) {
                uiHandler.sendEmptyMessageDelayed(0, 2000);
            }
            return false;
        }
    });

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        isExit = true;
        uiHandler.removeMessages(0);
        super.onDestroy();
        if (displayView != null) {
            windowManager.removeView(displayView);
            displayView=null;
        }
        stopForeground(true);
        L.e("RuntimeInfoService->onDestroy start");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    Random random = new Random();

    private void showInfo() {
        if (txt_info != null) {
            String frequency = RuntimeInfo.is5G ? "5G" : RuntimeInfo.is24G ? "2.4G" : "none";
            String info = "测试信息\n" +
                    "WIFI:" + NetWorkUtil.getSSID(RuntimeInfoService.this) + "\n" +
                    //"组长:" + (RuntimeInfo.isGroupOwner ? "true" : "false") + "\n" +
                    "频率:" + frequency + "\n" +
                    "信号:" + RuntimeInfo.info_signalLevel + "\n" +
                    "带宽:" + RuntimeInfo.info_speed + "Mbps\n" +
                    "IP:" + NetWorkUtil.getHostIPAddress(RuntimeInfoService.this) + "\n" +
//                    "网关:" + NetWorkUtil.getGateway(InfoService.this) + "\n" +
                    "-------------\n" +
                    "ser_ip:" + RuntimeInfo.ser_ip + "\n" +
                    "ser_port:" + RuntimeInfo.ser_port + "\n" +
                    "ser_mac:" + RuntimeInfo.ser_mac + "\n" +
                    "丢包率:" + lost + "\n" +
                    "延迟:" + delay + "\n" +

                    "MIME:" + TcpConst.mime_type + "\n" +
                    "分辨率:" + RuntimeInfo.mScreenWidth+"x"+RuntimeInfo.mScreenHeight + "\n" +
                    "帧率:" + RuntimeInfo.info_fps + "\n" +
                    "码率:" + RuntimeInfo.info_bit + "\n";
            txt_info.setText(info);

            int red = random.nextInt(256);
            int green = random.nextInt(256);
            int blue = random.nextInt(256);
            int color = Color.rgb(red, green, blue);
            txt_info.setTextColor(color);
        }
    }
}

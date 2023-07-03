package com.wcch.android.soft.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import com.igrs.betotablet.R;
import com.igrs.betotablet.soft.entity.Device;
import com.igrs.betotablet.soft.util.NetWorkUtil;
import com.igrs.sml.RuntimeInfo;
import com.igrs.sml.event.TcpEvent;
import com.igrs.sml.tcp.TcpCallback;
import com.igrs.sml.tcp.TcpStateCallback;
import com.igrs.sml.util.BaseUtil;
import com.igrs.sml.util.Common;
import com.igrs.sml.util.L;
import com.igrs.tpsdk.ProjectionSDK;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TcpManager {


//        <string name="tcp_disconnect">网络已断开</string>
//    <string name="tcp_connect_wlan_faile">您的手机与大屏不在同一网络，正在尝试自动连接大屏热点…</string>
//    <string name="tcp_connect_fail">连接失败，请检查大屏网络和投屏应用</string>
//    <string name="tcp_authentication_fail">%s 验证失败</string>
//    <string name="tcp_dev_id_faile">接收端IP获取失败，无法连接</string>
//    <string name="tcp_apinfo_faile">热点未开启，请在大屏-设置中打开大屏热点</string>
//    <string name="tcp_connect_ap_faile">自动连接大屏热点失败，请您手动连接</string>
//    <string name="tcp_retry">%s已断开,正在尝试恢复连接</string>

    public final static int code_msg = 0;
    public final static int code_connect_fail = -1;
    public final static int code_connect_success = 1;

    public final static int code_wlan_faile = -2;
    public final static int code_ap_faile = -3;



    private boolean wait_ap = false;
    private volatile static TcpManager instance;
    private Timer timerOut;

    public static TcpManager getInstance() {
        if (instance == null) {
            synchronized (TcpManager.class) {
                if (instance == null) {
                    instance = new TcpManager();
                }
            }
        }
        return instance;
    }
    private ExecutorService executorService;
    private TcpManager() {

    }

    private Device device;
    private boolean isExit = false;

    private TcpManageCallback tcpStateCallback;

    Activity context;
    @SuppressLint("StaticFieldLeak")
    public synchronized void connectDevice(Activity context, Device device, TcpManageCallback tcpStateCallback) {
        this.context = context;
        if(executorService!=null){
            executorService.shutdown();
        }

        if (tcpStateCallback == null) {
            return;
        }
        if (device == null) {
            tcpStateCallback.stateChange(-1,context.getString(R.string.tcp_connect_fail));
            return;
        }
        if ((TextUtils.isEmpty(device.wlan_ip) || "0.0.0.0".equals(device.wlan_ip)) && (TextUtils.isEmpty(device.ap_ip) || "0.0.0.0".equals(device.ap_ip))) {
            tcpStateCallback.stateChange(0,context.getString(R.string.tcp_dev_id_faile));
            return;
        }

        executorService = Executors.newFixedThreadPool(2);
        wait_ap = false;
        this.tcpStateCallback = tcpStateCallback;
        this.device = device;

        if (timerOut != null) {
            timerOut.cancel();
            timerOut = null;
        }
        timerOut = new Timer();
        timerOut.schedule(new TimerTask() {
            @Override
            public void run() {
                L.e("TcpManager connect->time out");
                tcpStateCallback.stateChange(-1,context.getString(R.string.tcp_connect_fail));
                onDestroy();
            }
        }, 15000);

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                boolean connect = connectTcp(device.wlan_ip);
                L.e("TcpManager connectDevice-->connected :"+connect);
                if (connect) {
                    L.e("TcpManager connectDevice-->connected = true wait syscmd");
                    L.e("zjx","TcpManager connectDevice-->connected = true wait syscmd");
                } else {
                    if (device.device_type == 4) {
                        tcpStateCallback.stateChange(-1,context.getString(R.string.tcp_connect_fail));
                        if (timerOut != null) {
                            timerOut.cancel();
                            timerOut = null;
                        }
                        return;
                    }

                    Log.e("zjx1","device.ap_ip "+device.ap_ip  +"device.ap_name"+device.ap_name + "device.ap_pwd "+device.ap_pwd);
//                    if (TextUtils.isEmpty(device.ap_ip)
//                            || "0.0.0.0".equals(device.ap_ip)
//                            || TextUtils.isEmpty(device.ap_name)
//                            || TextUtils.isEmpty(device.ap_pwd)) {

                    if (TextUtils.isEmpty(device.ap_ip) || "0.0.0.0".equals(device.ap_ip)  || TextUtils.isEmpty(device.ap_name) ) {
                        if (TextUtils.isEmpty(device.wlan_ip) || "0.0.0.0".equals(device.wlan_ip)) {
                            tcpStateCallback.stateChange(code_msg,context.getString(R.string.tcp_dev_id_faile));
                        } else {
                            tcpStateCallback.stateChange(code_msg,context.getString(R.string.tcp_apinfo_faile));
                        }
                        if (timerOut != null) {
                            timerOut.cancel();
                            timerOut = null;
                        }
                    } else {
                        wait_ap = true;
                        connectAp(context, device);
                    }
                }
            }
        });
    }
    @SuppressLint("StaticFieldLeak")
    private void connectAp(Activity context, Device device) {
        L.i("TcpManager connectAp-->" + device.ap_name);

        WifiManager mWifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (executorService != null && mWifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
            tcpStateCallback.stateChange(code_ap_faile, "");
            return;
        }

        boolean result = false;
        String ssid = NetWorkUtil.getSSID(context);
        if (!TextUtils.isEmpty(ssid) && ssid.equals(device.ap_name)) {
            result = true;
        } else {
            result = BaseUtil.connectWifi(context, device.ap_name, device.ap_pwd, "WPA");
        }
        tcpStateCallback.stateChange(code_wlan_faile,"");

        L.i("TcpManager connectAp--result->" + result);
        if (executorService != null && result) {
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    // 执行你的耗时操作代码
                    boolean connect = false;
                    L.i("TcpManager connectAp--doInBackground->");
                    long startTime = System.currentTimeMillis();
                    while (wait_ap) {
                        if (Common.ping(device.ap_ip)) {
                            wait_ap = false;
                            connect = connectTcp(device.ap_ip);
                            if(connect){
                                L.e("TcpManager ap tcp -->connected = true wait syscmd");
                            }
                            break;
                        } else {
                            try {
                                if(System.currentTimeMillis()-startTime>=10_000){
                                    break;
                                }
                                Thread.sleep(500);
                            } catch (Exception e) {
                                break;
                            }
                        }
                    }
                    if(!isExit && !connect){
                        tcpStateCallback.stateChange(code_ap_faile,context.getString(R.string.tcp_connect_ap_faile));
                        if (timerOut != null) {
                            timerOut.cancel();
                            timerOut = null;
                        }
                    }
                    L.i("TcpManager connectAp--out->isExit:"+isExit+" connect:"+connect);
                }
            });
        } else {
            tcpStateCallback.stateChange(code_ap_faile,context.getString(R.string.tcp_connect_ap_faile));
            if (timerOut != null) {
                timerOut.cancel();
                timerOut = null;
            }
        }
    }

    private boolean connectTcp(String ip) {

        L.i("TcpManager connectTcp->connecting-start-->" + ip);
        Log.e("zjx05","TcpManager connectTcp->connecting-start-->" + ip);
         ProjectionSDK.getInstance().onDestroy();
        boolean result = ProjectionSDK.getInstance().init(ip,12345, ConfigUtil.getInstance().getIdentification());
        L.i("TcpManager connectTcp->connecting-result:" + result);
        L.i("zjx05","TcpManager connectTcp->connecting-result:" + result);
        if(!result){
            return false;
        }
        RuntimeInfo.ser_ip = ip;

        ProjectionSDK.getInstance().setTcpStateCallback(new TcpStateCallback() {
            @Override
            public void stateChange(String dev_id, boolean isConnected) {

            }
        });

        ProjectionSDK.getInstance().setTcpSysCallback(new TcpCallback() {
            @Override
            public void rev_msg(String dev_id, byte type, byte[] data) {
                L.e("TcpManager->rev_sys_msg->dev_id:"+dev_id+" TYPY_SYS:" + new String(data));
                if(timerOut!=null){
                    timerOut.cancel();
                    timerOut = null;
                }
                if(device!=null){
                    L.i("TcpManager->rev_sys_msg->device->device_mac:"+device.device_mac+" dev_id:"+dev_id);
                }else {
                    L.i("TcpManager->rev_sys_msg->device is null");
                }

                if(true){
                    EventBus.getDefault().postSticky(new TcpEvent(TcpEvent.TYPE_TCP_CONNECT, "连接成功"));
                    tcpStateCallback.stateChange(code_connect_success,"");
                    return;
                }

                String name = "ignore";
                try{
                    JSONObject json = new JSONObject(new String(data));
                    name = json.getString("name");
                }catch (Exception e){
                }

                if(TextUtils.isEmpty(name) || "ignore".equals(name) || "null".equals(name) ){
                    EventBus.getDefault().postSticky(new TcpEvent(TcpEvent.TYPE_TCP_CONNECT, "连接成功"));
                    tcpStateCallback.stateChange(code_connect_success, "");
                    return;
                }

                if(device!=null && !TextUtils.isEmpty(device.device_mac) && (device.device_mac.equals(name))){
                    EventBus.getDefault().postSticky(new TcpEvent(TcpEvent.TYPE_TCP_CONNECT, "连接成功"));
                    tcpStateCallback.stateChange(code_connect_success, "");
                }else{
                    tcpStateCallback.stateChange(-1,context.getString(R.string.tcp_authentication_fail,device.room_name));
                    onDestroy();
                }
            }
        });
        ProjectionSDK.getInstance().setTcpCMDCallback(new TcpCallback() {
            @Override
            public void rev_msg(String dev_id, byte type, byte[] data) {
                try {
                    String msg = new String(data);
                    L.i("TcpManager->rev_msg--->" + msg);
                    EventBus.getDefault().postSticky(new TcpEvent(0,dev_id,msg));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        L.i("TcpManager connectTcp->connecting-end-->" + ip);


        /*boolean result = ProjectionSDK.getInstance().init(ip,12345, ConfigUtil.getInstance().getIdentification());
        L.i("TcpManager connectTcp->connecting-result:" + result);
        L.i("zjx05","TcpManager connectTcp->connecting-result:" + result);
        if(!result){
            return false;
        }
        RuntimeInfo.ser_ip = ip;*/
        return true;
    }

    public void onDestroy(){
        isExit = true;
        wait_ap = false;
        if(timerOut!=null){
            timerOut.cancel();
            timerOut = null;
        }
        if(executorService!=null){
            try{
                executorService.shutdown();
            }catch (Exception e){
            }
        }
    }

    public interface TcpManageCallback {
        public void stateChange(int code,String msg);
    }

}

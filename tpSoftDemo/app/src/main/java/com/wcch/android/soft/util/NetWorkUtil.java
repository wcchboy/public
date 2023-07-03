package com.wcch.android.soft.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.igrs.sml.RuntimeInfo;
import com.igrs.sml.util.L;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

/**
 * 需要权限
 * ACCESS_WIFI_STATE 用于获取wifi信息
 * ACCESS_NETWORK_STATE 用于获取网络状态信息
 * ACCESS_COARSE_LOCATION 用于获取wifi列表
 * ACCESS_FINE_LOCATION 用于获取wifi列表
 */
public class NetWorkUtil {

    private static final int MIN_RSSI = -100;
    private static final int MAX_RSSI = -55;
    public static final int RSSI_LEVELS = 5;
    public static int calculateSignalLevel(int rssi, int numLevels) {
        if (rssi <= MIN_RSSI) {
            return 0;
        } else if (rssi >= MAX_RSSI) {
            return numLevels - 1;
        } else {
            float inputRange = (MAX_RSSI - MIN_RSSI);
            float outputRange = (numLevels - 1);
            return (int)((float)(rssi - MIN_RSSI) * outputRange / inputRange);
        }
    }

    /**
     * get the network type
     *
     * @param ctx Context
     * @return networktype
     */
    public static String getNetWorkType(Context ctx) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        String type = null;
        NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
        if (info == null) {
            type = "none";
        } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
            type = "wifi";
        } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            int subType = info.getSubtype();
            if (subType == TelephonyManager.NETWORK_TYPE_CDMA || subType == TelephonyManager.NETWORK_TYPE_GPRS
                    || subType == TelephonyManager.NETWORK_TYPE_EDGE) {
                type = "2g";
            } else if (subType == TelephonyManager.NETWORK_TYPE_UMTS || subType == TelephonyManager.NETWORK_TYPE_HSDPA
                    || subType == TelephonyManager.NETWORK_TYPE_EVDO_A || subType == TelephonyManager.NETWORK_TYPE_EVDO_0
                    || subType == TelephonyManager.NETWORK_TYPE_EVDO_B) {
                type = "3g";
            } else if (subType == TelephonyManager.NETWORK_TYPE_LTE) {// LTE是3g到4g的过渡，是3.9G的全球标准
                type = "4g";
            }
            else if (subType == TelephonyManager.NETWORK_TYPE_NR) {//5G
                type = "5g";
            }
        }
        return type;
    }


    public static String intToInetAddress(int ip) {
        return (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." + (ip >> 24 & 0xFF);
    }

    public static String getGateway(Context context) {
        String gateway = null;
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            DhcpInfo info = wifiManager.getDhcpInfo();
            int ip = info.gateway;
            gateway = (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." + (ip >> 24 & 0xFF);
            //L.i("NetUtil->getGateway->" + ip + " " + gateway);

            int net = info.netmask;
            String netmask = (net & 0xFF) + "." + ((net >> 8) & 0xFF) + "." + ((net >> 16) & 0xFF) + "." + (net >> 24 & 0xFF);
            //L.i("NetUtil->getGateway->net:" + net + " netmask：" + netmask);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return gateway;
    }

    public static String getHostIPAddress(Context context) {
        String IPAddress = null;
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            //L.i("NetUtil->getHostIPAddress->WifiInfo\n" + wifiInfo.toString());
            int ip = wifiInfo.getIpAddress();
            int mFrequency =wifiInfo.getFrequency();

            RuntimeInfo.is5G = is5GHz(mFrequency);
            RuntimeInfo.is24G = is24GHz(mFrequency);

            int signalLevel = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 5);
            int speed = wifiInfo.getLinkSpeed();
            RuntimeInfo.info_signalLevel = signalLevel;
            RuntimeInfo.info_speed = speed;
            //L.i("NetUtil->getHostIPAddress->mFrequency:"+mFrequency+" is5G:" + RuntimeInfo.is5G+" is24G:"+RuntimeInfo.is24G);

            IPAddress = (ip & 0xFF) + "." + ((ip >> 8) & 0xFF) + "." + ((ip >> 16) & 0xFF) + "." + (ip >> 24 & 0xFF);
            if (!TextUtils.isEmpty(IPAddress) && !"0.0.0.0".equals(IPAddress)) {
                return IPAddress;
            }
        } catch (Exception e) {
            e.printStackTrace();
            L.e("NetUtil->getHostIPAddress->e:" + e.toString());
        }
        try {
            Enumeration nis = NetworkInterface.getNetworkInterfaces();
            while (nis.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) nis.nextElement();
                Enumeration ias = ni.getInetAddresses();
                while (ias.hasMoreElements()) {
                    InetAddress ia = (InetAddress) ias.nextElement();
                    if (ia instanceof Inet6Address) {
                        continue;
                    }
                    String hostAddress = ia.getHostAddress();
                    if (!"127.0.0.1".equals(hostAddress)) {
                        IPAddress = hostAddress;
                        break;
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return IPAddress;
    }

    /**
     * 判断wifi是否为2.4G
     * @param freq
     * @return
     */
    public static boolean is24GHz(int freq) {
        return freq > 2400 && freq < 2500;
    }

    /**
     * 判断wifi是否为5G
     * @param freq
     * @return
     */
    public static boolean is5GHz(int freq) {
        return freq > 4900 && freq < 5900;
    }


    public static String getP2pMac() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface ntwInterface : interfaces) {
                if (ntwInterface.getName().toLowerCase().startsWith("p2p")) {//以前是p2p0，修正为wlan
                    byte[] byteMac = ntwInterface.getHardwareAddress();
                    if (byteMac == null) {
                        continue;
                    }
                    StringBuilder strBuilder = new StringBuilder();
                    for (int i = 0; i < byteMac.length; i++) {
                        L.i("i:" + byteMac[i]);
                        strBuilder.append(String.format("%02X:", byteMac[i]));
                    }
                    if (strBuilder.length() > 0) {
                        strBuilder.deleteCharAt(strBuilder.length() - 1);
                    }
                    L.e("2 >>>>>>>>>>>>" + strBuilder.toString());

                    return strBuilder.toString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getP2pDeviceStateString(int deviceStatus) {
        switch (deviceStatus) {
            case WifiP2pDevice.AVAILABLE:
                return "可用";
            case WifiP2pDevice.INVITED:
                return "邀请";
            case WifiP2pDevice.CONNECTED:
                return "已连接";
            case WifiP2pDevice.FAILED:
                return "失败";
            case WifiP2pDevice.UNAVAILABLE:
                return "不可用";
            default:
                return "Unknown";
        }
    }

    public static String getSSID(Context context) {
        String ssid = "";
        //L.i("WifiAdmin- getSSID:" + Log.getStackTraceString(new Throwable()));
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wm.getConnectionInfo();
        if (info != null) {
            int networkId = info.getNetworkId();

            if (wm.getConnectionInfo().getSSID() != null) {
                if(!"<unknown ssid>".equals(ssid)){
                    ssid = wm.getConnectionInfo().getSSID().replaceAll("\"", "");
                    //L.i("WifiAdmin->0 getSSID:" + ssid+" networkId:"+networkId);
                    return ssid;
                }
            }

            @SuppressLint("MissingPermission")
            List<WifiConfiguration> configuredNetworks = wm.getConfiguredNetworks();
            //L.i("WifiAdmin-> configuredNetworks:"+configuredNetworks);
            for (WifiConfiguration wifiConfiguration:configuredNetworks){
                //L.i("WifiAdmin-> networkId:"+networkId);
                if (wifiConfiguration.networkId==networkId){
                    ssid = wifiConfiguration.SSID;
                    //L.i("WifiAdmin->1 getSSID:" + ssid);
                    return ssid;
                }
            }
        }
        if("<unknown ssid>".equals(ssid)){
            ConnectivityManager ctm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = ctm.getActiveNetworkInfo();
            if (networkInfo!=null && networkInfo.isConnected()) {
                if (networkInfo.getExtraInfo()!=null){
                    ssid = networkInfo.getExtraInfo().replace("\"","");
                    L.i("WifiAdmin->2 getSSID:" + ssid);
                }else{
                    L.e("WifiAdmin->getSSID->getExtraInfo:null" );
                }
            }else{
                L.e("WifiAdmin->getSSID->isConnected:false" );
                ssid = "none";
            }
        }
        return ssid;
    }

    public static boolean ping2(String address, int pingTimes, int timeOut) {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec( "ping "  + "-c " + pingTimes + " -w " + timeOut+ " "+address);
            InputStreamReader r = new InputStreamReader(process.getInputStream());

            LineNumberReader returnData = new LineNumberReader(r);

            String returnMsg="";

            String line = "";

            while ((line = returnData.readLine()) != null) {

                System.out.println(line);

                returnMsg += line;

            }

            if(returnMsg.indexOf("100% packet loss")!=-1){

                System.out.println("与 " +address +" 连接不畅通.");
                return  false;
            } else{

                System.out.println("与 " +address +" 连接畅通.");
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;

    }

}
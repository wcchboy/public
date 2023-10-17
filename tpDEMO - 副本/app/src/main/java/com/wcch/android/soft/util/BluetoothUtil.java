package com.wcch.android.soft.util;

import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.Context;

import com.igrs.sml.util.L;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

public class BluetoothUtil {

    private volatile static BluetoothUtil instance;

    public static BluetoothUtil getInstance() {
        if (instance == null) {
            synchronized (BluetoothUtil.class) {
                if (instance == null) {
                    instance = new BluetoothUtil();
                }
            }
        }
        return instance;
    }

    private BluetoothUtil() {

    }

    public BluetoothDevice getConnectDevice() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null) {
            //得到已配对的设备列表
            Set<BluetoothDevice> devices = defaultAdapter.getBondedDevices();
            for (BluetoothDevice bluetoothDevice : devices) {
                boolean isConnect = false;
                try {
                    //获取当前连接的蓝牙信息
                    isConnect = (boolean) bluetoothDevice.getClass().getMethod("isConnected").invoke(bluetoothDevice);
                } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
                if (isConnect) {
                    L.d( "Connect dev=" + bluetoothDevice.getAddress());
                    return bluetoothDevice;
                }else{
                    L.d( "Connect dev=" + bluetoothDevice.getName()+"<>"+bluetoothDevice.getAddress());
                }
            }
        }
        return null;
    }


    /**
     * 断开蓝牙设备连接
     *
     * @param bluetoothDevice BluetoothDevice
     */
    public void disconnect(Context context, BluetoothDevice bluetoothDevice) {
        if(bluetoothDevice==null){
            return;
        }
        L.i("disconnect bluetoothDevice->name:"+bluetoothDevice.getName()+" mac:"+bluetoothDevice.getAddress());
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //获取HEADSET代理对象
        mBluetoothAdapter.getProfileProxy(context, new BluetoothProfile.ServiceListener() {
            @Override
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                if (profile == BluetoothProfile.A2DP) {
                    //使用A2DP的协议断开蓝牙设备（使用了反射技术调用断开的方法）
                    BluetoothA2dp bluetoothA2dp = (BluetoothA2dp) proxy;
                    boolean isDisConnect = false;
                    try {
                        Method connect = bluetoothA2dp.getClass().getDeclaredMethod("disconnect", BluetoothDevice.class);
                        connect.setAccessible(true);
                        isDisConnect = (boolean) connect.invoke(bluetoothA2dp, bluetoothDevice);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    L.d("isDisConnect:" + (isDisConnect ? "断开音频成功" : "断开音频失败") + bluetoothDevice.getName());
                }
            }
            @Override
            public void onServiceDisconnected(int profile) {

            }
        }, BluetoothProfile.A2DP);
    }


    public void connect(Context context, BluetoothDevice bluetoothDevice) {
        if(bluetoothDevice==null){
            return;
        }
        L.i("connect bluetoothDevice->name:"+bluetoothDevice.getName()+" mac:"+bluetoothDevice.getAddress());
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //获取HEADSET代理对象
        mBluetoothAdapter.getProfileProxy(context, new BluetoothProfile.ServiceListener() {
            @Override
            public void onServiceConnected(int profile, BluetoothProfile proxy) {
                L.e("connect onServiceConnected->profile:"+profile);
                try{
                    if (profile == BluetoothProfile.A2DP) {
                        //使用A2DP的协议断开蓝牙设备（使用了反射技术调用断开的方法）
                        BluetoothA2dp bluetoothA2dp = (BluetoothA2dp) proxy;
                        boolean isConnect = false;
                        try {
                            Method connect = bluetoothA2dp.getClass().getDeclaredMethod("connect", BluetoothDevice.class);
                            connect.setAccessible(true);
                            isConnect = (boolean) connect.invoke(bluetoothA2dp, bluetoothDevice);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        L.d("isConnect:" + (isConnect ? "连接音频成功" : "连接音频失败") + bluetoothDevice.getName());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    L.e("connect bluetoothDevice->e:"+e.toString());
                }
            }

            @Override
            public void onServiceDisconnected(int profile) {

            }
        }, BluetoothProfile.A2DP);
    }

}

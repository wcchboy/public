package com.wcch.android.entity;

import android.text.TextUtils;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

public class Device extends LitePalSupport implements Serializable {

    public long id;
    public int device_type;//蓝牙包类型：IWB 01. MC 02.PC 03  pad 04
    public String ap_name;
    public String ap_pwd;
    public String wlan_ip;//大屏 WLANO IP地址，同一局域网时使用
    public String ap_ip;//大屏AP 0 IP地址，连接大屏热点时使用
    public int port=0;//端口
    public String ap_suffix;//大屏热点，用于PC连接热点的时候计算密码
    public int room=0; //用于标识大屏所创建的会议室大小，默认小会议室（-85），中会议室 （-100）．大会议享 (-120)
//    @Column(unique = true)
    public String device_mac="";//设备MAC地址
    public String room_name="";//会议室名

    @Column(ignore=true)
    private String castCode = "";//投屏码

    public long connectTime;



    @Column(ignore=true)
    public static final int STATE_NONE=0;
    @Column(ignore=true)
    public static final int STATE_LAST=-1;
    @Column(ignore=true)
    public static final int STATE_CONNECT=1;

    @Column(ignore=true)
    public int connect_index=0;//0 默认； 1 正在连接Wlan； 2尝试连接ap  3ap连接成功等待初始化 4连接成功 5上次连接

    @Column(ignore=true)
    public boolean room_has_type1=false;//是否收到type1
    @Column(ignore=true)
    public String room_name_s="　";//81 name
    @Column(ignore=true)
    public String room_name_e="";//91 name

    @Column(ignore=true)
    public boolean room_name_isComplete =false;

//    @Column(ignore=true)
//    public int connect_type=0;//0 wlan； 1 ap


    public String getCastCode() {
        return castCode;
    }

    public void setCastCode(int castCode) {
        if (castCode <= 0) {
            this.castCode = "";
        } else if (castCode < 10) {
            this.castCode = "000" + castCode;
        } else if (castCode < 100) {
            this.castCode = "00" + castCode;
        } else if (castCode < 1000) {
            this.castCode = "0" + castCode;
        } else {
            this.castCode = "" + castCode;
        }
    }

    public void setCastCode(String castCode) {
        if (TextUtils.isEmpty(castCode)) {
            this.castCode = "";
        } else {
            this.castCode = castCode;
        }
    }

    @Override
    public String toString() {
        return "Device{" +
                "id=" + id +
                ", device_type=" + device_type +
                ", ap_name='" + ap_name + '\'' +
                ", ap_pwd='" + ap_pwd + '\'' +
                ", wlan_ip='" + wlan_ip + '\'' +
                ", ap_ip='" + ap_ip + '\'' +
                ", port=" + port +
                ", ap_suffix='" + ap_suffix + '\'' +
                ", room=" + room +
                ", device_mac='" + device_mac + '\'' +
                ", room_name='" + room_name + '\'' +
                ", castCode=" + castCode +
                ", connectTime=" + connectTime +
                ", connect_index=" + connect_index +
                ", room_has_type1=" + room_has_type1 +
                ", room_name_s='" + room_name_s + '\'' +
                ", room_name_e='" + room_name_e + '\'' +
                ", room_name_isComplete=" + room_name_isComplete +
                '}';
    }
}

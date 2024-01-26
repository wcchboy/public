package com.wcch.android.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.wcch.android.bean.DeviceInfo;

import java.util.ArrayList;
import java.util.List;

public class ProviderUtil {

    /*************************************** 协议状态表 start***************************************/
    //设置协议状态
    public static void setAgreementStatus(ContentResolver resolver, int status) {
        ContentValues value_agreement = new ContentValues();
        value_agreement.put("agreement", status);
        resolver.update(CourseProviders.URI_AGREEMENT, value_agreement, null, null);
    }

    //获取协议状态
    public static int getAgreementStatus(ContentResolver resolver) {
        int status = 0;
        Cursor cursor_agreement = resolver.query(CourseProviders.URI_AGREEMENT, null, null, null, null);
        if (cursor_agreement == null) {
            return status;
        }
        while (cursor_agreement.moveToNext()) {
            status = cursor_agreement.getInt(0);
        }
        cursor_agreement.close();
        return status;
    }
    /*************************************** 协议状态表 end***************************************/

    /*************************************** 设备信息表 start***************************************/
    //设置设备信息
    public static void setDeviceInfo(ContentResolver resolver, String deviceName, String deviceModel) {
        DeviceInfo deviceInfo = new DeviceInfo(deviceName, deviceModel);
        Gson gson = new Gson();
        String info = gson.toJson(deviceInfo);

        ContentValues value_info = new ContentValues();
        value_info.put("info", info);
        resolver.update(CourseProviders.URI_DEVICE_INFO, value_info, null, null);
    }

    //获取设备名
    public static String getDeviceName(ContentResolver resolver) {
        String deviceInfo = "";
        String deviceName = "";
        Cursor cursor_info = resolver.query(CourseProviders.URI_DEVICE_INFO, null, null, null, null);
        if (cursor_info == null) return deviceName;
        while (cursor_info.moveToNext()) {
            deviceInfo = cursor_info.getString(0);
        }
        Gson gson = new Gson();
        DeviceInfo device = gson.fromJson(deviceInfo, DeviceInfo.class);
        if (device != null) {
            cursor_info.close();
            return device.getName();
        }
        cursor_info.close();
        return deviceName;
    }

    //获取设备名
    public static String getDeviceModel(ContentResolver resolver) {
        String deviceInfo = "";
        String deviceModel = "";
        Cursor cursor_info = resolver.query(CourseProviders.URI_DEVICE_INFO, null, null, null, null);
        if (cursor_info == null) return deviceModel;
        while (cursor_info.moveToNext()) {
            deviceInfo = cursor_info.getString(0);
        }
        Gson gson = new Gson();
        DeviceInfo device = gson.fromJson(deviceInfo, DeviceInfo.class);
        if (device != null) {
            cursor_info.close();
            return device.getModel();
        }
        cursor_info.close();
        return deviceModel;
    }
    /*************************************** 设备信息表 end***************************************/


    /*************************************** 保活服务表 start***************************************/
    //设置协议状态
    public static void addKeepAliveService(ContentResolver resolver, String service) {
        if (resolver == null) return;
        if (TextUtils.isEmpty(service)) return;
        List<String> exitKeepAliveServiceList = null;
        Cursor cursor_service = resolver.query(CourseProviders.URI_SERVICE, null, null, null, null);
        if (cursor_service != null) {
            while (cursor_service.moveToNext()) {
                String keepAliveService = cursor_service.getString(0);
                if (exitKeepAliveServiceList == null) {
                    exitKeepAliveServiceList = new ArrayList<>();
                }
                exitKeepAliveServiceList.add(keepAliveService);
            }
            if (exitKeepAliveServiceList != null && exitKeepAliveServiceList.size() > 0) {
                for (String s : exitKeepAliveServiceList) {
                    if (TextUtils.equals(service,s)) {
                        return;//如果这个服务已经添加到了保活列表里面了，就不要再添加了
                    }
                }
            }

        }

        ContentValues value_service = new ContentValues();
        value_service.put("service", service);
        resolver.insert(CourseProviders.URI_SERVICE,value_service);//加入保活列表
    }

    public static List<String> getKeepAliveService(ContentResolver resolver) {
        if (resolver == null) return null;
        List<String> keepAliveService = null;
        Cursor cursor_service = resolver.query(CourseProviders.URI_SERVICE, null, null, null, null);
        if (cursor_service == null) return null;
        while (cursor_service.moveToNext()) {
            String service = cursor_service.getString(0);
            if (keepAliveService == null) {
                keepAliveService = new ArrayList<>();
            }
            keepAliveService.add(service);
        }
        cursor_service.close();
        return keepAliveService;
    }


    /*************************************** 保活服务表 end***************************************/
}

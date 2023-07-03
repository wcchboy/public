// IBetoService.aidl
package com.lenovo.betoservice.module.beto;
import com.lenovo.betoservice.module.beto.model.BeToPhysicalDevice;
import java.util.List;
import com.lenovo.betoservice.module.beto.IBetoEventListener;
// Declare any non-default types here with import statements

interface IBetoService {

    BeToPhysicalDevice getLocalDevice();
    List<BeToPhysicalDevice> getDevices();
    void addEventListener(in String device_uid,in IBetoEventListener listener);
    void removeEventListener(in String device_uid,in IBetoEventListener listener);
    void setPhysicalDeviceProp(in String device_uid, in Map<String, String> props);
    void setFunctionDeviceProp(in String device_uid,in String func_device_uid, in  Map<String, String> props);

    void setAllMute(int mute);
    Map<String, String> getCameras();
    void connectCamera(String funDevId);
    void disconnectCamera(String funDevId);

    //本机（相机、扬声器、麦克风、投屏、远控）是否正在被使用
    boolean getCameraIsUse();
    boolean getSpeakerIsUse();
    boolean getMicIsUse();
    boolean getScreencastIsUse();
    boolean getRemotectrlIsUse();
}
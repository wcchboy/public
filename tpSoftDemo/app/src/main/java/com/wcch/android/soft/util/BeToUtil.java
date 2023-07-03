package com.wcch.android.soft.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.igrs.sml.util.Common;
import com.igrs.sml.util.L;
import com.wcch.android.soft.entity.Device;
import com.wcch.android.utils.StringUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BeToUtil {

    private static boolean isDebug = true;

    public static HashMap<String, Device> map_devices = new HashMap<>();


    //定义名称拼装的全局变量
    private static int bleRoomNameMaxLenth = 9;

    private static Map<String, byte[]> mergeNameDict = new ConcurrentHashMap<>();
    //补全函数：
    private  static  byte[] GetFullDevNameBytes(int roomNameLen, byte[] oldByte, byte[] newByte) {
        String flag = "$";
        int flagIndexOld = -1;
        int flagIndexNew = -1;
        byte flagByte = flag.getBytes()[0];
        for (int i = 0; i < newByte.length; i++){
            if (newByte[i] == flagByte){
                flagIndexNew = i;
                break;
            }
        }
        if (flagIndexNew == -1) return newByte;//已经不包含$,容错
        for (int i = 0; i < oldByte.length; i++){
            if (oldByte[i] == flagByte){
                flagIndexOld = i;
                break;
            }
        }
        //判断新的名字是否已经修改
        if(flagIndexOld == -1){ //老的名字已经拼接完成，判断新名字是不是已经修改
            if(oldByte.length< newByte.length)return newByte;
            if(flagIndexNew==0){ //新名字的后半部分
                boolean isChange = false;
                for (int i = 0;i< (newByte.length - 1); i++){//不比较newByte的第一个$标记
                    if (newByte[newByte.length-1-i] != oldByte[oldByte.length-1-i]){
                        isChange = true;
                        break;
                    }
                }
                if(isChange) return newByte;
            }else { //新名字的前半部分
                boolean isChange = false;
                for (int i = 0; i < (newByte.length-1); i++){
                    if (newByte[i] != oldByte[i]){
                        isChange = true;
                        break;
                    }
                }
                if (isChange) return newByte;
            }
        } else{ //老名字仍然未拼接完成，这种情况较少，
            //这种情况没办法知道是不是改名字了，等下次蓝牙包过来时走 flagIndexOld == -1 的检查
        }
        if (flagIndexOld == -1) return oldByte;//已经不包含$,即已经拼接完成或者老名字就是全名不需要切片
        if (flagIndexOld == flagIndexNew){ //都在同一个位置
            return oldByte;
        }else{ //不在同一个位置
            //比较 看哪个是左哪个是右
            byte[] fullName = new byte[roomNameLen];
            if (flagIndexOld > flagIndexNew) { //老的是左半部分  新的是右半部分
                System.arraycopy(newByte, 0, fullName, roomNameLen - newByte.length, newByte.length);//拷贝右半边
                System.arraycopy(oldByte, 0, fullName, 0, oldByte.length - 1);//拷贝左半边,并去掉$符号

            } else { //老的是右半部分  新的是左半部分
                System.arraycopy(oldByte, 0, fullName, roomNameLen - oldByte.length, oldByte.length);//拷贝右半边
                System.arraycopy(newByte, 0, fullName, 0, newByte.length - 1);//拷贝左半边,并去掉$符号
            }
            return fullName;
        }
    }

    private static boolean isCompleteName;

    public static synchronized String setDevice(byte[] data) {

       // Log.e("zjx11"," data lenth:"+data.length);

        if (data[4] == 66 && data[5] == 101 && data[6] == 84 && data[7] == 111) {

            ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
            buffer.position(4);

            byte[] beto = new byte[4];
            buffer.get(beto, 0, 4);
            if (isDebug) L.i(Common.bytesToHex(beto));

            int type = buffer.get();

            Log.e("zjx"," 主包的 type :"+type);
            boolean isScanPackage =  (type & 0x80) == 0x80;

            Log.e("zjx"," 是否是扫描包 :"+isScanPackage);
            if(isScanPackage){
                type  = type & 0x0F;
                Log.e("zjx"," 扫描包的  type  :"+type);
            }


//            if (type != 1 && type != 4) {
////                if (isDebug)
////                    L.e("\n\n=====================find device==========================break   type=" + type);
//                    Log.e("zjx","=====================find device==========================break   type=" + type);
//                return null;
//            }

            if (isDebug) L.e("\n\n=====================find device==========================\n\n");
            L.i("\n" + Common.bytesToHex(data) + "\n");
            if (isDebug) L.i("type:" + " " + type);

//            Device device = new Device();
//            device.device_type = type;


            if (type == 1 && !isScanPackage) {

                Log.e("zjx"," 主包数据");

                byte[] ips = new byte[4];
                buffer.get(ips, 0, 4);
                StringBuilder wlan_ip = new StringBuilder();
                for (int i = 0; i < ips.length; i++) {
                    if (i != ips.length - 1) {
                        wlan_ip.append(ips[i] & 0xff).append(".");
                    } else {
                        wlan_ip.append(ips[i] & 0xff);
                    }
                }
                if (isDebug)
                    L.i("wlan_ip:" + wlan_ip.toString() + "->" + Common.bytesToHex(ips));
                Log.e("zjx"," 主包数据 获取到 ：wlan_ip:" + wlan_ip.toString() + "->" + Common.bytesToHex(ips));

                byte[] ips1 = new byte[4];
                buffer.get(ips1, 0, 4);
                StringBuilder ap_ip = new StringBuilder();
                for (int i = 0; i < ips1.length; i++) {
                    if (i != ips1.length - 1) {
                        ap_ip.append(ips1[i] & 0xff).append(".");
                    } else {
                        ap_ip.append(ips1[i] & 0xff);
                    }
                }
                if (isDebug) L.i("ap_ip:" + ap_ip.toString() + "->" + Common.bytesToHex(ips1));

                Log.e("zjx"," 主包数据 获取到 ：ap_ip:" + ap_ip );

                byte[] ports = new byte[2];
                buffer.get(ports, 0, 2);

                int port = Common.byteToShort(ports, false);
                if (isDebug) L.i("port:" + port + "->" + Common.bytesToHex(ports));

                Log.e("zjx"," 主包数据 获取到 ：port:" + port);


                byte[] ap_suffixs = new byte[2];
                buffer.get(ap_suffixs, 0, 2);
                String ap_suffix = Common.bytesToHex(ap_suffixs);
                if (isDebug) L.i("ap_suffix:" + ap_suffix);
                Log.e("zjx"," 主包数据 获取到 ：ap_suffix:" + ap_suffix);
                String ap_name = "";
                String ap_pwd = "";
                if (!TextUtils.isEmpty(ap_suffix)) {
//                    if ("0000".equals(ap_suffix)) return null;
                    ap_name = "Lenovo_" + ap_suffix;
                    String md5 = Common.getMD5Str(ap_name).toLowerCase();
                    byte[] pwds = md5.substring(8, 16).getBytes();
                    for (int i = 0; i < pwds.length; i++) {
                        byte b = pwds[i];
                        if (b < 48 || b > 57) {
                            pwds[i] = (byte) (b % 10 + 48); //转为0~9
                        }
                    }
                    if (isDebug) L.i("ap_name:" + ap_name);
                    ap_pwd = new String(pwds);
                    if (isDebug) L.i("ap_pwd:" + ap_pwd);
                }

                byte[] macs = new byte[6];
                buffer.get(macs, 0, 6);
                StringBuilder mac = new StringBuilder();
                for (int i = 0; i < macs.length; i++) {
                    if (i != macs.length - 1) {
                        mac.append(String.format("%02X:", macs[i]));
                    } else {
                        mac.append(String.format("%02X", macs[i]));
                    }
                }
                String device_mac = mac.toString();
                Log.e("zjx","device_mac:" + device_mac + "->" + Common.bytesToHex(macs));
                if (isDebug) L.i("device_mac:" + device_mac + "->" + Common.bytesToHex(macs));

                if (TextUtils.isEmpty(device_mac) || "00:00:00:00:00:00".equals(device_mac)) {
                    return null;
                }
                Device device = map_devices.get(device_mac);
                if (device == null) {
                    device = new Device();
                    device.device_mac = device_mac;
                    map_devices.put(device_mac, device);
                    Log.e("zjx111","添加device 第一处");
                }

                Log.e("zjx111","ap_ip :"+ap_ip);
                Log.e("zjx111","wlan_ip :"+wlan_ip);
                device.ap_ip = ap_ip.toString();
                device.wlan_ip = wlan_ip.toString();
                device.port = port;
                device.ap_suffix = ap_suffix;
                device.ap_name = ap_name;
                device.ap_pwd = ap_pwd;
                device.device_mac = mac.toString();
                device.room = buffer.get();
                if (isDebug) L.i("room:" + device.room);

                Log.e("zjx111","room : "+device.room);

                byte[] castCodes = new byte[2];
                buffer.get(castCodes, 0, 2);
                int castCode = Common.byteToShort(castCodes, false);
                device.setCastCode(castCode);
                Log.e("zjx111", "castCode: " + castCode);

                ///////////////////////////////////////////////////////////////////////////
//                byte[] data_sub = new byte[6 + 4];
//                buffer.get(data_sub, 0, data_sub.length);
//                //00 00 1D FF C5 02 4265546F
//                if (isDebug) L.i("fg:" + Common.bytesToHex(data_sub) + " " + data_sub[5]);
////
//                if (data_sub[6] == 66 && data_sub[7] == 101 && data_sub[8] == 84 && data_sub[9] == 111) {
//                    buffer.position(buffer.position() + 1 + 6);//固定值：BeTo，十六进制：4265 54 6F
//                    try {
//                        //会议室名称长度
//                        int nameLength = buffer.get() & 0xFF;
//                        Log.e("zjx1111", "nameLength "+nameLength);
//                        if (isDebug) L.i("nameLength:" + nameLength);
//                        //会议室名称
//                        byte[] names = new byte[nameLength];
//                        buffer.get(names, 0, nameLength);
//                        String name = new String(names);
////                        if (TextUtils.isEmpty(name) && !TextUtils.isEmpty(ap_name)) {
////                            name = ap_name;
////                        }
//
//                        if (isDebug) L.i("name:" +name);
//
//
//                        Log.e("zjx111", "name "+name);
//                        int totalLength = buffer.get() & 0xff;
//                        Log.e("zjx1", "totalLength  "+nameLength);
//                        if (isDebug) L.i("totalLength:" + totalLength);
//                        int postion = buffer.get() & 0xff;
//                        if (isDebug) L.i("postion:" + postion);
//                        int count = buffer.get() & 0xff;
//                        if (isDebug) L.i("count:" +count);
//
//                        if(nameLength==totalLength){
//                            device.room_name = name;
//                        }
//                        if (isDebug) L.i("room_name:" + device.room_name);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                        L.e("----e:"+e.toString());
//                        //Toast.makeText(DiscoverActivity.this,"设备名读取失败",Toast.LENGTH_SHORT).show();
////                        if (TextUtils.isEmpty(device.room_name)) {
////                            device.room_name = device.ap_name;
////                        }
//                    }
//                }
//
//                if (TextUtils.isEmpty(device.room_name) && !TextUtils.isEmpty(device.room_name_s) && !TextUtils.isEmpty(device.room_name_e)) {
//                    device.room_name = device.room_name_s + device.room_name_e;
//                }
//
//
//
//                Log.e("zjx","room_name:" + device.room_name + "  s:" + device.room_name_s + " e:" + device.room_name_e);
//                if (isDebug)
//                    L.i("room_name:" + device.room_name + "  s:" + device.room_name_s + " e:" + device.room_name_e);


//                try{
//                    //拼接room_name
//
//                    int nameIndex = 21;
//                    int nameIndex1 = 22;
//                    int roomNameLen = 0xff & data[nameIndex];
//
//                    Log.e("zjx2","roomNameLen " +  roomNameLen);
//                    String roomName = "";
//                    if(roomNameLen <= bleRoomNameMaxLenth){
//                        //如果roomName的长度小于等于最大长度,直接获取蓝牙数据包中的名字
//                        byte[] roomNameArray = Arrays.copyOfRange(data, nameIndex1, nameIndex1+roomNameLen);
//                        roomName = new String(roomNameArray, StandardCharsets.UTF_8);
//                    }else{
//                        //如果roomName的长度大于最大长度，需要从蓝牙包获取到后进行拼装
//                        if(!mergeNameDict.containsKey(device_mac)){
//                            byte[] roomNameArray = Arrays.copyOfRange(data, nameIndex1, nameIndex1+bleRoomNameMaxLenth);
//                            mergeNameDict.put(device_mac, roomNameArray);
//                        }else{
//                            byte[] roomNameArray = GetFullDevNameBytes(roomNameLen,
//                                    mergeNameDict.get(device_mac),
//                                    Arrays.copyOfRange(data, nameIndex1, nameIndex1+bleRoomNameMaxLenth));
//                            mergeNameDict.put(device_mac, roomNameArray);
//                        }
//                        roomName = new String(mergeNameDict.get(device_mac), StandardCharsets.UTF_8);
//                    }
//
//                    Log.e("zjx","roomName " +  roomName);
//
//                    //替换名称中包含的特殊字符� 和 $
//                    device.room_name = roomName.replace("�", "").replace("$", "");
//
//                    Log.e("zjx","room name 2 " +  device.room_name);
//                    Log.e("zjx","room name 2 替换后 " +roomName.replace("?", "").replace("$", ""));
//                }
//                catch (Exception ex)
//                {
//
//                }

                device.room_has_type1 = true;
                //map_devices.put(device.device_mac,device);

                Log.e("zjx","设备名字："+device.room_name);

                if (!device.room_name.isEmpty()&& device.room_name_isComplete){
                    return device_mac;
                } else {
                    return null;
                }

            }
            else if (type == 1 && isScanPackage) {

                Log.e("zjx"," 扫描包数据");
                //设备MAC地址
                byte[] macs1 = new byte[6];
                buffer.get(macs1, 0, 6);
                StringBuilder mac = new StringBuilder();
                for (int i = 0; i < macs1.length; i++) {
                    if (i != macs1.length - 1) {
                        mac.append(String.format("%02X:", macs1[i]));
                    } else {
                        mac.append(String.format("%02X", macs1[i]));
                    }
                }
                String device_mac = mac.toString();
                if (isDebug)
                    L.i("device_mac:" + device_mac + "->" + Common.bytesToHex(macs1));//Scan包类型：81 1WB. 82MC. 83P
                if (TextUtils.isEmpty(device_mac) || "00:00:00:00:00:00".equals(device_mac)) {
                    return null;
                }
                //会议室名称长度
                int nameLength = buffer.get() & 0xff;

                Log.e("zjx", "nameLength:"+nameLength);

                if (isDebug) L.i("nameLength:" + nameLength);

                //会议室名称
                byte[] names = new byte[nameLength];
                buffer.get(names, 0, nameLength);
                String name = new String(names);
                if (isDebug) L.i("name:" + name);

                if (TextUtils.isEmpty(name)) {
                    return null;
                }

                Device device = map_devices.get(device_mac);
                if (device == null) {
                    device = new Device();
                    device.device_mac = device_mac;
                    map_devices.put(device_mac, device);
                    Log.e("zjx111","添加device 第2 处");
                }

                int nameIndex = 15;
                int tempNameIndex = nameIndex +1;
                int roomNameLen = 0xff & data[nameIndex];//15?
                byte[] roomNameArray = Arrays.copyOfRange(data, tempNameIndex, tempNameIndex+roomNameLen);
                String roomNamePart = new String(roomNameArray, StandardCharsets.UTF_8);

                byte totalChar = data[tempNameIndex+roomNameLen];
                byte startPositionInStr = data[tempNameIndex+roomNameLen+1];
                byte charCount = data[tempNameIndex+roomNameLen+2];

                String oldName = "";
                if(map_devices.containsKey(device_mac)) {
                    oldName = map_devices.get(device_mac).room_name;
                }

                Log.e("zjx2","oldName："+oldName);

                device.room_name_isComplete =false;

                if((oldName.length()!=totalChar)) {
                    //只要长度不一样，就说明room_name有变化，需要重置，
                    //此处中文'￥'作为占位符，只要包含这个占位符，则认为会议室名称还未获取完整
                    device.room_name = StringUtil.getStringFilledBy(totalChar, '￥');
                }
                else
                {
                    device.room_name = oldName;
                    Log.e("zjx2","device.room_name  老名字  ："+device.room_name);
                    device.room_name_isComplete =true;
                }

                Log.e("zjx2","device.room_name   ："+device.room_name);

                String newName = oldName;

                Log.e("zjx2","roomNamePart："+roomNamePart);
                Log.e("zjx2","startPositionInStr："+startPositionInStr);
                if (oldName.indexOf(roomNamePart) != startPositionInStr) {
                    //旧名称内不包含新名称，需要将新名称与现有room_name合并
                    newName = StringUtil.replaceByPosition(device.room_name, startPositionInStr, charCount, roomNamePart);
                }

                device.room_name = newName;
                Log.e("zjx2","device.room_name 新名字  替换后 ："+device.room_name);
                if (newName.contains("￥")){
                    device.room_name_isComplete =false;
                } else {
                    device.room_name_isComplete =true;
                    if (map_devices.containsKey(mac.toString())){
                        map_devices.get(mac.toString()).room_name = newName;
                        return  device_mac;
                    }
                }
            }

        }
        return null;

    }

    //TODO 根据mac地址判断是否已连接(这里参数可以直接用BluetoothDevice对象)
    public static boolean isConnected(String macAddress) {
        if (!BluetoothAdapter.checkBluetoothAddress(macAddress)) {
            return false;
        }
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(macAddress);

        Method isConnectedMethod = null;
        boolean isConnected;
        try {
            isConnectedMethod = BluetoothDevice.class.getDeclaredMethod("isConnected", (Class[]) null);
            isConnectedMethod.setAccessible(true);
            isConnected = (boolean) isConnectedMethod.invoke(device, (Object[]) null);
        } catch (NoSuchMethodException e) {
            isConnected = false;
        } catch (IllegalAccessException e) {
            isConnected = false;
        } catch (InvocationTargetException e) {
            isConnected = false;
        }
        return isConnected;
    }

    /**
     * 获取系统中已连接的蓝牙设备
     * @return
     */
    @SuppressLint("MissingPermission")
    public static Set<BluetoothDevice> getConnectedDevicesV1() {
        Class<BluetoothAdapter> bluetoothAdapterClass = BluetoothAdapter.class;//得到BluetoothAdapter的Class对象
        Set<BluetoothDevice> deviceSet = new HashSet<>();
        //是否存在连接的蓝牙设备
        try {
            Method method = bluetoothAdapterClass.getDeclaredMethod("getMostRecentlyConnectedDevices", (Class[]) null);
            //打开权限
            method.setAccessible(true);
            List<BluetoothDevice> list = (List<BluetoothDevice>) method.invoke(BluetoothAdapter.getDefaultAdapter(), (Object[]) null);
            if (isDebug) L.d("最近连接过的设备:");
            for (BluetoothDevice dev : list
            ) {
                String Type = "";
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    switch (dev.getType()) {
                        case BluetoothDevice.DEVICE_TYPE_CLASSIC:
                            Type = "经典";
                            break;
                        case BluetoothDevice.DEVICE_TYPE_LE:
                            Type = "BLE";
                            break;
                        case BluetoothDevice.DEVICE_TYPE_DUAL:
                            Type = "双模";
                            break;
                        default:
                            Type = "未知";
                            break;
                    }
                }
                String connect = "设备未连接";
                if (isConnected(dev.getAddress())) {
                    deviceSet.add(dev);
                    connect = "设备已连接";
                }
                if (isDebug)
                    L.i(connect + ", address = " + dev.getAddress() + "(" + Type + "), name --> " + dev.getName());

            }
        } catch (NoSuchMethodException e) {
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deviceSet;
    }

    /**
     * 获取系统中已连接的蓝牙设备
     * @return
     */
    public static Set<BluetoothDevice> getConnectedDevicesV2(Context context) {

        Set<BluetoothDevice> result = new HashSet<>();
        Set<BluetoothDevice> deviceSet = new HashSet<>();

        BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
        //获取BLE的设备, profile只能是GATT或者GATT_SERVER
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        List GattDevices = bluetoothManager.getConnectedDevices(BluetoothProfile.GATT);
        if (GattDevices!=null && GattDevices.size()>0){
            deviceSet.addAll(GattDevices);
        }
        //获取已配对的设备
        Set ClassicDevices = bluetoothManager.getAdapter().getBondedDevices();
        if (ClassicDevices!=null && ClassicDevices.size()>0){
            deviceSet.addAll(ClassicDevices);
        }

        for (BluetoothDevice dev:deviceSet
        ) {
            String Type = "";
            switch (dev.getType()){
                case BluetoothDevice.DEVICE_TYPE_CLASSIC:
                    Type = "经典";
                    break;
                case BluetoothDevice.DEVICE_TYPE_LE:
                    Type = "BLE";
                    break;
                case BluetoothDevice.DEVICE_TYPE_DUAL:
                    Type = "双模";
                    break;
                default:
                    Type = "未知";
                    break;
            }
            String connect = "设备未连接";
            if (isConnected(dev.getAddress())){
                result.add(dev);
                connect = "设备已连接";
            }
            if(isDebug)L.i(connect+", address = "+dev.getAddress() + "("+ Type + "), name --> "+dev.getName());
        }
        return result;
    }


}

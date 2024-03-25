package com.wcch.android.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wcch.android.utils.LogUtils;

/**
 * Created by RyanWang on 2024/3/19.
 *
 * @Description:接受 跨设备
 */
public class FindDevicesReceiver extends BroadcastReceiver {
    private static final String TAG = FindDevicesReceiver.class.getSimpleName();
    public static final String FIND_DEV_KEY = "find_devices";
    public static final String REPLY_MESSAGE = "replyMessage";
    public static final String INTENT_FILTER_FIND_DEVICES = "com.mphotool.wireless.server.findDevices";
    public static final String INTENT_FILTER_FIND_RETURN = "com.mphotool.wireless.server.findDevicesReturn";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        LogUtils.d(TAG, "onReceive: " + intent);
        // 处理接收到的广播
        String message = intent.getStringExtra(FIND_DEV_KEY);
        if (message != null) {
            // 处理接收到的消息
            LogUtils.d(TAG,"--->MSG:"+message);

            // 发送回复广播给设备A
            Intent replyIntent = new Intent(INTENT_FILTER_FIND_RETURN);
            replyIntent.putExtra(REPLY_MESSAGE, "Reply from Tablet B!");
            context.sendBroadcast(replyIntent);
        }
    }
}

package com.wcch.android.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wcch.android.Constants;
import com.wcch.android.utils.LogUtils;

/**
 * Created by RyanWang on 2024/3/19.
 *
 * @Description: 发送 跨设备
 */
public class FindDevReplyReceiver extends BroadcastReceiver {
    private static final String TAG = FindDevReplyReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent)
    {
        LogUtils.d(TAG, "receive intent:"+intent.getAction());
        if (intent.getAction().equals(Constants.INTENT_FILTER_FIND_RETURN))
        {
            LogUtils.d(TAG, "receive INTENT_FILTER_FIND_RETURN:");
            // 处理来自设备B的回复广播
            String replyMessage = intent.getStringExtra(Constants.REPLY_MESSAGE);
            if (replyMessage != null)
            {
                // 处理回复消息
                LogUtils.d(TAG, " reply:"+replyMessage);
            }
        }
    }
}

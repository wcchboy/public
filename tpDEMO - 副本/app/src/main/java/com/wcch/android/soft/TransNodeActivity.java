package com.wcch.android.soft;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.igrs.betotablet.TabletApp;
import com.igrs.betotablet.soft.service.RuntimeInfoService;
import com.igrs.betotablet.soft.service.ScreenRecordService;
import com.igrs.betotablet.soft.util.ConfigUtil;
import com.igrs.sml.util.BaseUtil;
import com.igrs.sml.util.L;
import com.igrs.tpsdk.ProjectionSDK;

import java.nio.ByteBuffer;

public class TransNodeActivity extends AppCompatActivity {

    protected String deviceName = "大屏";
    protected float width_scale = 1;
    protected float height_scale = 1;

    protected final float r_width = 1920.0f;
    protected final float r_height = 1080.0f;

    protected int swidth, sheight;

    protected String dev_id;
    protected int activeState;//0 未激活; 1 已激活
    protected int result;

    protected boolean isExit;

    public int myDeviceType=0;//0 默认 ；1 Android Phone，2 iPhone，3，PC(Win)，4 Android Pad，5 iPad
    public String myDeviceName="none";//目标名称


    protected Handler exitHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            /*L.i("TransNodeActivity exitHandler----> " + TransNodeActivity.this.hashCode() + "\n what:" + msg.what
                    + " isScreenRecord:" + TabletApp.Companion.getApplication().isScreenRecord);*/
            exitHandler.removeMessages(0);
            stopService(new Intent(TransNodeActivity.this, RuntimeInfoService.class));
            stopService(new Intent(TransNodeActivity.this, ScreenRecordService.class));
            finish();
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point outPoint = new Point();
        display.getRealSize(outPoint);
        sheight = outPoint.y;
        swidth = (int) (sheight / 0.5625f);
        dev_id = ConfigUtil.getInstance().getIdentification();

        if (TabletApp.Companion.getApplication().getCurrent_device() != null) {
            deviceName =TabletApp.Companion.getApplication().getCurrent_device().room_name;
        }
        if(BaseUtil.isPad(TransNodeActivity.this)){
            myDeviceType= 4;
        }else{
            myDeviceType= 1;
        }

        try {
            String tempName = Settings.Secure.getString(getContentResolver(), "bluetooth_name");
            if (!TextUtils.isEmpty(tempName)) {
                myDeviceName = tempName;
            }
        } catch (Exception e) {
        }
        if (TextUtils.isEmpty(myDeviceName)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            myDeviceName = BluetoothAdapter.getDefaultAdapter().getName();
        }


      //  L.i("TransNodeActivity onCreate->" + TransNodeActivity.this.hashCode() + "\n" + Log.getStackTraceString(new Exception("test")));

    }

    /**
     * 后台和前台切换  压后台后服务器会停止发送数据;到前台后服务器恢复发送数据
     *
     * @param isPause true:切换至后台; false:切换至前台
     * @return true调用成功；false调用失败（可能是未初始化成功）
     */
    protected boolean setPause(boolean isPause) {
        //-100 切换至后台 ；-101 切换至前台
        int pasu = isPause ? -100 : -101;
        sendMotionEvent(pasu, 0, 0);
        L.e("TransNodeActivity setPause-->isPause:" + isPause + " time:" + System.currentTimeMillis());
        return true;
    }

    ByteBuffer buffer = ByteBuffer.allocate(100);
    protected void sendKeEvent(int action, int key) {
        buffer.clear();
        buffer.putInt(action);
        buffer.putInt(key);
        L.i("TransNodeActivity sendKeEvent-----action:" + action + " key:" + key );
        buffer.flip();
        int len = buffer.limit() - buffer.position();
        byte[] bytes = new byte[len];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = buffer.get();
        }
        ProjectionSDK.getInstance().sendTouchMsg(dev_id,bytes);
    }

    protected void sendMotionEvent(int action, float x, float y) {
        ByteBuffer buffer = ByteBuffer.allocate(1000);
        buffer.putInt(0);
        buffer.putInt(action);
        buffer.putInt(1);//point
        buffer.putInt(0);//id
        buffer.putInt((int) (x * width_scale));//x
        buffer.putInt((int) (y * height_scale));//y
        L.i("TransNodeActivity sendMotionEvent----- action:" + action + " width_scale:" + width_scale + " height_scale:" + height_scale );
        buffer.flip();
        int len = buffer.limit() - buffer.position();
        byte[] bytes = new byte[len];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = buffer.get();
        }
        ProjectionSDK.getInstance().sendTouchMsg(dev_id,bytes);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //exitHandler.removeMessages(0);
        L.i("TransNodeActivity onDestroy->"+TransNodeActivity.this.hashCode());
    }
}
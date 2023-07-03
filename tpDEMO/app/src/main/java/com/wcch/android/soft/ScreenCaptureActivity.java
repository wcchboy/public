package com.wcch.android.soft;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.gson.Gson;
import com.igrs.betotablet.R;
import com.igrs.betotablet.TabletApp;
import com.igrs.betotablet.activity.SettingActivity;
import com.igrs.betotablet.soft.entity.Device;
import com.igrs.betotablet.soft.entity.Msg19;
import com.igrs.betotablet.soft.entity.SwitchApp;
import com.igrs.betotablet.soft.service.RuntimeInfoService;
import com.igrs.betotablet.soft.service.ScreenRecordService;
import com.igrs.betotablet.soft.statusBar.StatusBarUtils;
import com.igrs.betotablet.soft.util.AlertDialog;
import com.igrs.betotablet.soft.util.BluetoothUtil;
import com.igrs.betotablet.soft.util.ConfigUtil;
import com.igrs.betotablet.soft.util.Dialog_activation;
import com.igrs.betotablet.soft.util.IgrsToast;
import com.igrs.betotablet.soft.view.FullScreenSettingView;
import com.igrs.betotablet.utils.LogUtil;
import com.igrs.sml.CMD;
import com.igrs.sml.RuntimeInfo;
import com.igrs.sml.TaskModel;
import com.igrs.sml.event.SystemEvent;
import com.igrs.sml.event.TcpEvent;
import com.igrs.sml.util.L;
import com.igrs.tpsdk.ProjectionSDK;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 主动投屏相关代码
 */
public class ScreenCaptureActivity extends TransNodeActivity {

    private final Gson gson = new Gson();
    private final int REQUEST_MEDIA_PROJECTION = 1002;

    private TaskModel taskModel;
    private Timer finishTimer = new Timer();


    private FullScreenSettingView fullScreenSettingView;
    private LinearLayout layout_window;
    private ConstraintLayout layout_full;
    private LinearLayout layout_split;
    private TextView txt_bt;
    private LinearLayout layout_bt;
    private View layout_home;
    private ImageView windowImg, fullImg, fullRightImg, splitImg;
    private TextView windowTv, fullTv, splitTv;
    private TextView txtDevice;
    Class<?> cls = ScreenRecordService.class;
    private String TAG = "ScreenCaptureActivity";
    private ProjectionSDK projectionSDK ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.setStatusTransparent_b(this);
        setContentView(R.layout.activity_screen_capture);
        L.i("ScreenCaptureActivity onCreate->" + ScreenCaptureActivity.this.hashCode() + "\n->isScreenRecord:" + TabletApp.Companion.getApplication().isScreenRecord().get());

        fullScreenSettingView = findViewById(R.id.fullScreenSettingView);

        layout_window = findViewById(R.id.layout_window);
        layout_full = findViewById(R.id.layout_full);
        layout_split = findViewById(R.id.layout_split);

        txt_bt = findViewById(R.id.txt_bt);
        layout_bt = findViewById(R.id.layout_bt);
        layout_home = findViewById(R.id.layout_home);

        windowImg = findViewById(R.id.window_img);
        fullImg = findViewById(R.id.full_img);
        fullRightImg = findViewById(R.id.full_right_img);
        splitImg = findViewById(R.id.split_img);
        windowTv = findViewById(R.id.window_tv);
        fullTv = findViewById(R.id.full_tv);
        splitTv = findViewById(R.id.split_tv);
        txtDevice = findViewById(R.id.txt_device);

        taskModel = TabletApp.Companion.getApplication().getLastTaskModel();

        if (!TabletApp.Companion.getApplication().isScreenRecord().get()) {
            initTask(1);
        }
        EventBus.getDefault().register(this);

        Device device = TabletApp.Companion.getApplication().getCurrent_device();
        if (device != null) {
            LogUtil.d(TAG,"device != null");
            String deviceName = device.room_name;
            if (TextUtils.isEmpty(deviceName)) {
                deviceName = device.getCastCode();
            }
            txtDevice.setText(deviceName);
        }else{
            LogUtil.d(TAG,"device is null");
        }

        //projectionSDK = new ProjectionSDK();
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public synchronized void onSystemEventBus(SystemEvent event) {
        L.e("ScreenCaptureActivity->onSystemEventBus->" + event.type);
        EventBus.getDefault().removeStickyEvent(event);
        switch (event.type) {
            case SystemEvent.TYPE_REC_FAILED: {
                L.e("ScreenCaptureActivity->TYPE_REC_FAILED->exitHandler");
                RuntimeInfo.screenRecordIntent = null;
                TabletApp.Companion.getApplication().isScreenRecord().set(false);
                IgrsToast.getInstance().showToast_error(getApplication(), getString(R.string.screen_capture_fail), 4000);
                exitHandler.sendEmptyMessageDelayed(0, 3800);
                ConfigUtil.getInstance().setNeedScreenIntent(false);
            }
            break;
            case SystemEvent.TYPE_REC_EXIT: {
                L.e("ScreenCaptureActivity->TYPE_REC_EXIT->exitHandler");
                TabletApp.Companion.getApplication().isScreenRecord().set(false);
                IgrsToast.getInstance().showToast_icon(getApplication(), getString(R.string.screen_capture_stop), 3500, R.drawable.ic_t_stop_rec);
                exitHandler.sendEmptyMessageDelayed(0, 3000);
            }
            break;
        }
    }

    private void initTask(int retry) {
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();

        Point outSize = new Point();
        display.getRealSize(outSize);

        int width = outSize.x;
        int height = outSize.y;

        if (width > height) {
            int temp = width;
            width = height;
            height = temp;
        }
        if (height < RuntimeInfo.screenRecordSize) {
            RuntimeInfo.screenRecordSize = height;
        } else {
            RuntimeInfo.screenRecordSize = 1920;
        }

        L.i("initTask->size change->" + width + "x" + height + " old:" + RuntimeInfo.mScreenWidth + "x" + RuntimeInfo.mScreenHeight + " dev:" + width + "x" + height);
        if (height != RuntimeInfo.mScreenHeight) {
            RuntimeInfo.mScreenWidth = (int) (RuntimeInfo.screenRecordSize * width / height);
            RuntimeInfo.mScreenHeight = RuntimeInfo.screenRecordSize;
            RuntimeInfo.mScreenWidth -= RuntimeInfo.mScreenWidth % 8;
            RuntimeInfo.mScreenHeight -= RuntimeInfo.mScreenHeight % 8;
        }

        if (taskModel != null) {
            taskModel.cmd = 2;
            taskModel.taskState = 2;
            projectionSDK.sendCMDMessage(dev_id, new Gson().toJson(taskModel).getBytes());
        }

        taskModel = new TaskModel();
        taskModel.cmd = 1;
      //  taskModel.targetPort = 9527;
        taskModel.targetPort = 12345;
        taskModel.resolution_x = (int) RuntimeInfo.mScreenWidth;
        taskModel.resolution_y = (int) RuntimeInfo.mScreenHeight;
        taskModel.taskState = 0;
        taskModel.targetName = "ANDROID_" + Build.BRAND + "_" + Build.MODEL;
        taskModel.deviceType = myDeviceType;
        taskModel.deviceName = myDeviceName;
        taskModel.taskType = 0;
        taskModel.taskId = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        LogUtil.d(TAG, "oncreate task dev_id:"+dev_id+" taskModel:"+taskModel.toString());
        projectionSDK.sendCMDMessage(dev_id, new Gson().toJson(taskModel).getBytes());

        finishTimer = new Timer();
        finishTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (!isExit) {
                            L.e("服务器无应答--->");

                            IgrsToast.getInstance().showToast_error(getApplication(), getString(R.string.screen_capture_time_out), 3000);
                            exitHandler.sendEmptyMessageDelayed(0, 2900);

                        }
                    }
                });
                L.e("ScreenCaptureActivity-------服务器无应答--->->exitHandler ");
                TabletApp.Companion.getApplication().setCurrent_device(null);
            }
        }, 8000);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public synchronized void onTcpEventBus(TcpEvent event) {
        L.i("ScreenCaptureActivity->onTcpEventBus->code:" + event.code + " ip:" + RuntimeInfo.ser_ip + " msg:" + event.msg + "\n cur_dev:" + TabletApp.Companion.getApplication().getCurrent_device());
        EventBus.getDefault().removeStickyEvent(event);

        if (isExit) {
            L.e("ScreenCaptureActivity->onTcpEventBus->Exit   break....");
            return;
        }
        if (event.code == TcpEvent.TYPE_TCP_CONNECT) {
            IgrsToast.getInstance().close();
            L.e("TYPE_TCP_CONNECT ->exitHandler");
            exitHandler.removeMessages(0);
            if (taskModel != null && TabletApp.Companion.getApplication().isScreenRecord().get()) {
                taskModel.retry = 1;
                projectionSDK.sendCMDMessage(dev_id, new Gson().toJson(taskModel).getBytes());
            }
            return;
        } else if (event.code == TcpEvent.TYPE_TCP_RETRY) {
            try {
                IgrsToast.getInstance().showToast_load(getApplication(), getString(R.string.tcp_retry, getString(R.string.screen_capture)), 15000);
            } catch (Exception e) {
            }
        } else if (event.code == TcpEvent.TYPE_TCP_DISCONNECT) {
            L.e("TYPE_TCP_DISCONNECT ->exitHandler");
            IgrsToast.getInstance().showToast_error(getApplication(), getString(R.string.tcp_disconnect), 3000);
            exitHandler.sendEmptyMessageDelayed(0, 2900);
            return;
        }

        if (event != null && !TextUtils.isEmpty(event.msg)) {
            EventBus.getDefault().removeStickyEvent(event);
            try {
                if (finishTimer != null) {
                    finishTimer.cancel();
                    finishTimer = null;
                }
                JSONObject json = new JSONObject(event.msg.toLowerCase());
                int cmd = json.getInt("cmd");
                if (cmd == CMD.CMD_ERROR_MSG) {//17

                    String msg = "";
                    int code = json.getInt("code");
                    switch(code){
                        case 0:
                            msg = getResources().getString(R.string.cmd17_0);
                            break;
                        case 1:
                            msg = getResources().getString(R.string.cmd17_1, deviceName);
                            break;
                        case 2:
                            msg = getResources().getString(R.string.cmd17_2);
                            break;
                        case 3:
                            msg = getResources().getString(R.string.cmd17_3);
                            break;
                        case 4:
                            msg = getResources().getString(R.string.cmd17_4);
                            break;
                        case 5:
                            msg = getResources().getString(R.string.cmd17_5);
                            break;
                        case 6:
                            msg = getResources().getString(R.string.cmd17_6);
                            break;
                        case 7:
                        {
                            msg = "";
                            isExit = true;
                            Dialog_activation dialog_activation = new Dialog_activation(this, getString(R.string.activation_1sc));
                            dialog_activation.setConfirmButton(getString(R.string.confirm), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog_activation.dismiss();
                                    finish();
                                }
                            });
                        }
                        return;
                        case 8:
                        case 9: {
                            msg = "";
                            isExit = true;
                            Dialog_activation dialog_activation = new Dialog_activation(this, getString(R.string.activation_vip));
                            dialog_activation.setConfirmButton(getString(R.string.confirm), new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog_activation.dismiss();
                                    finish();
                                }
                            });
                        }
                        break;
                        case 10:
                            msg = getResources().getString(R.string.cmd17_10);
                            break;
                        default:
                            L.e("screencapture   xxxxxxxxxxx ->undefined:" + code);
                            Toast.makeText(ScreenCaptureActivity.this, "undefined:" + code, Toast.LENGTH_LONG).show();
                            break;
                    }
                    taskModel.cmd = 2;
                    taskModel.taskState = 2;
                    Log.d(TAG, "CMD=2 taskstate =2 taskModel:"+taskModel.toString());
                    projectionSDK.sendCMDMessage(dev_id, new Gson().toJson(taskModel).getBytes());
                    if(!TextUtils.isEmpty(msg)){
                        IgrsToast.getInstance().showToast_error(getApplication(), msg, 3000);
                    }
                    L.e(msg+"---> ->exitHandler");
                    isExit = true;
                    exitHandler.sendEmptyMessageDelayed(0, 2800);
                    return;
                } else if (cmd == CMD.CMD_LAYOUT_MSG) {
                    Msg19 fromModel = gson.fromJson(event.msg, Msg19.class);
                    setProjectionLayout(fromModel);
                    return;
                }
                TaskModel fromModel = gson.fromJson(event.msg, TaskModel.class);
                L.e("onTcpEventBus fromModel cmd ->"+fromModel.cmd);
                if (fromModel.cmd == 1) {
                    activeState = fromModel.activeState;

                    if (fromModel.taskState == 0) {
                        L.e("服务器IP：" + RuntimeInfo.ser_ip + " 初始化成功。。");
                        TaskModel model = new TaskModel();
                        model.taskId = fromModel.taskId;
                        model.cmd = 2;
                        model.taskState = 0;
                        projectionSDK.sendCMDMessage(dev_id, new Gson().toJson(model).getBytes());
                        TabletApp.Companion.getApplication().setLastTaskModel(taskModel) ;
                        try {
                            if (ConfigUtil.getInstance().getNeedScreenIntent() && Objects.requireNonNull(RuntimeInfo.screenRecordIntent) != null) {//录屏已经有了直接走这里
                                /*TabletApp.Companion.getApplication().isScreenRecord().set(true);
                                Intent intent = new Intent(ScreenCaptureActivity.this, cls);
                                intent.putExtra("resultData", RuntimeInfo.screenRecordIntent);
                                intent.putExtra("packageName", "com.igrs.betotablet.soft.ScreenCaptureActivity");
                                startService(intent);*/
                                startRecordService();
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();

                        }

                        if (!TabletApp.Companion.getApplication().isScreenRecord().get()) {
                            MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                            startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
                        }

                    } else {
                        isExit = true;
                        L.e("投屏失败---> ->exitHandler");
                        IgrsToast.getInstance().showToast_error(getApplication(), getString(R.string.screen_capture_fail), 3000);
                        isExit = true;
                        exitHandler.sendEmptyMessageDelayed(0, 2800);
                    }
                } else if (fromModel.cmd == 10) {
                    if (taskModel!=null && fromModel.taskId.equals(taskModel.taskId)) {
                        isExit = true;
                        exitHandler.removeMessages(0);
                        L.e("cmd=10 " + getString(R.string.cmd10_screen_capture) + "---> ->exitHandler");
                        IgrsToast.getInstance().showToast_icon(getApplication(), getString(R.string.cmd10_screen_capture, deviceName), 3000, R.drawable.ic_t_stop_rec);
                        exitHandler.sendEmptyMessageDelayed(0, 2800);
                        TabletApp.Companion.getApplication().isScreenRecord().set(false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            L.e("ORIENTATION_LANDSCAPE");
            layout_home.setVisibility(View.GONE);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            L.e("ORIENTATION_PORTRAIT");
            layout_home.setVisibility(View.VISIBLE);
        }
    }


    private void setProjectionLayout(Msg19 msg19) {

        if (activeState == 0) {
            activeState = msg19.activeState;
        }

        switch (msg19.getCurrentMode()) {
            case Msg19.MODE_WINDOW: {
                windowImg.setImageResource(R.drawable.ic_ck_selected);
                windowTv.setTextColor(Color.parseColor("#ff158cff"));
                fullImg.setImageResource(R.drawable.ic_qp_default);
                fullTv.setTextColor(Color.parseColor("#ff141414"));
                splitImg.setImageResource(R.drawable.ic_fp_default);
                splitTv.setTextColor(Color.parseColor("#ff141414"));
            }
            break;
            case Msg19.MODE_FULL_SCREEN:
                windowImg.setImageResource(R.drawable.ic_ck_default);
                windowTv.setTextColor(Color.parseColor("#ff141414"));
                fullImg.setImageResource(R.drawable.ic_qp_selected);
                fullTv.setTextColor(Color.parseColor("#ff158cff"));
                splitImg.setImageResource(R.drawable.ic_fp_default);
                splitTv.setTextColor(Color.parseColor("#ff141414"));
                break;
            case Msg19.MODE_DF:
                windowImg.setImageResource(R.drawable.ic_ck_default);
                windowTv.setTextColor(Color.parseColor("#ff141414"));
                fullImg.setImageResource(R.drawable.ic_qp_default);
                fullTv.setTextColor(Color.parseColor("#ff141414"));
                splitImg.setImageResource(R.drawable.ic_fp_selected);
                splitTv.setTextColor(Color.parseColor("#ff158cff"));
                break;
        }

        if(msg19.isAndroid==1){
            layout_window.setVisibility(View.GONE);
        }else {
            layout_window.setVisibility(View.VISIBLE);
        }

        List<Msg19.ProjectionStatus> statusList = msg19.getList();

        if (statusList != null && statusList.size() > 0) {

            if(statusList.size()==1){
                layout_split.setVisibility(View.GONE);
                fullRightImg.setVisibility(View.GONE);
            }else{
                layout_split.setVisibility(View.VISIBLE);
                fullRightImg.setVisibility(View.VISIBLE);
            }


            List<FullScreenSettingView.FullScreenSettingModel> list = new ArrayList<>();
            for (Msg19.ProjectionStatus status : statusList) {
                FullScreenSettingView.FullScreenSettingModel model = new FullScreenSettingView.FullScreenSettingModel();
                if (taskModel.taskId.equals(status.getTaskId())) {
                    model.isMy = true;
                    int width = status.getWidth();
                    int height = status.getHeight();
                    L.e("cmd=19 my size:" + width + "x" + height);
                    if ((width >= 0 && height >= 0 && width != height)|| (width==0 && height==0)) {
                        TaskModel tm = new TaskModel();
                        tm.fps = status.getFps();
                        if (msg19.getCurrentMode() == Msg19.MODE_FULL_SCREEN && status.getFullScreen() == 0) {
                            tm.resolution_x = 0;
                            tm.resolution_y = 0;
                        } else {
                            tm.resolution_x = width;
                            tm.resolution_y = height;
                        }
                        EventBus.getDefault().postSticky(new SystemEvent(SystemEvent.TYPE_SIZE_CHANGE, tm));
                    }
                }
                model.isDef = status.getFullScreen() == 1;
                try {
                    model.dev_name = status.getDeviceName();
                    //model.dev_name = status.getDeviceName().substring(status.getDeviceName().indexOf("_") + 1);
                } catch (Exception e) {
                    model.dev_name = status.getDeviceName();
                }
                model.dev_id = status.getTaskId();
                model.dev_type = status.deviceType;

//                if(TextUtils.isEmpty(status.getDeviceName())){
//                    model.dev_type = FullScreenSettingView.FullScreenSettingModel.TYPE_ANDROID;
//                }else{
//                    if (status.getDeviceName().startsWith("ANDROID")) {
//                        model.dev_type = FullScreenSettingView.FullScreenSettingModel.TYPE_ANDROID;
//                    } else if (status.getDeviceName().startsWith("IPHONE")) {
//                        model.dev_type = FullScreenSettingView.FullScreenSettingModel.TYPE_IOS;
//                    } else if (status.getDeviceName().startsWith("PC")) {
//                        model.dev_type = FullScreenSettingView.FullScreenSettingModel.TYPE_PC;
//                    } else {
//                        model.dev_type = FullScreenSettingView.FullScreenSettingModel.TYPE_ANDROID;
//                    }
//                }

                if (model.isMy) {
                    list.add(0, model);
                } else {
                    list.add(model);
                }
                if (msg19.getCurrentMode() == Msg19.MODE_FULL_SCREEN) {
                    if (status.getFullScreen() == 1) {
                        String dev_name = model.dev_name;
//                        if (TextUtils.isEmpty(dev_name)) {
//                            txt_full_dev.setText("");
//                        } else {
//                            txt_full_dev.setText(dev_name);
//                        }
                    }
                }
            }
            fullScreenSettingView.update(list);
        }
    }

    private void startRecordService(){
        Looper.myQueue().addIdleHandler(() -> {
            TabletApp.Companion.getApplication().isScreenRecord().set(true);
            Intent intent = new Intent(ScreenCaptureActivity.this, cls);
            intent.putExtra("resultData", RuntimeInfo.screenRecordIntent);
            intent.putExtra("packageName", "com.igrs.betotablet.soft.ScreenCaptureActivity");
            startService(intent);

            Log.e(TAG,"BootCompletedReceiver startService");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent);
            } else {
                startService(intent);
            }

            return false;
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        L.i("ScreenCaptureActivity onDestroy->" + ScreenCaptureActivity.this.hashCode());
        if (taskModel != null) {
            taskModel.cmd = 2;
            taskModel.taskState = 2;
            projectionSDK.sendCMDMessage(dev_id, new Gson().toJson(taskModel).getBytes());
        }
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        L.e("ScreenCaptureActivity-------onActivityResult-------requestCode:" + requestCode + " resultCode:" + resultCode + " data:" + data);
        if (requestCode == REQUEST_MEDIA_PROJECTION && !isExit) {
            if (resultCode == Activity.RESULT_OK && data != null) {  // 指用户允许截屏
                TabletApp.Companion.getApplication().isScreenRecord().set(true);
                L.e("-------onActivityResult-----获得录屏权限，启动Service进行录制---");
                RuntimeInfo.screenRecordIntent = data;
                LogUtil.e("-------onActivityResult-----data:-"+data);
                Intent intent = new Intent(ScreenCaptureActivity.this, cls);
                intent.putExtra("resultData", data);
                //com/igrs/betotablet/soft/ScreenCaptureActivity.java
                intent.putExtra("packageName", "com.igrs.betotablet.soft.ScreenCaptureActivity");

                //startService(intent);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    startForegroundService(intent);
                } else {
                    startService(intent);
                }
                //startRecordService();
            } else {
                L.e("ScreenCaptureActivity-------onActivityResult 授权失败 ->exitHandler");
                IgrsToast.getInstance().showToast_error(getApplication(), getString(R.string.authority_fail), 3000);
                try {
                    TabletApp.Companion.getApplication().isScreenRecord().set(false);
                    TabletApp.Companion.getApplication().setLastTaskModel(null);
                    L.e("ScreenCaptureActivity-------onBackPressed taskModel:" + taskModel);
                    if (taskModel != null) {
                        taskModel.cmd = 2;
                        taskModel.taskState = 2;
                        projectionSDK.sendCMDMessage(dev_id, new Gson().toJson(taskModel).getBytes());
                        taskModel = null;
                    }
                } catch (Exception e) {
                    L.e("ScreenCaptureActivity-------onBackPressed send cmd 2 e:" + e.toString());
                }
                exitHandler.sendEmptyMessageDelayed(0, 2800);
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void switchApp(SwitchApp app) {
        if (app.isForeground()) {
            if (!isExit && TabletApp.Companion.getApplication().isScreenRecord().get() && TabletApp.Companion.getApplication().getCurrent_device()!=null) {
                startService(new Intent(ScreenCaptureActivity.this, cls).putExtra("showTag", false));
            }
            L.e("ScreenCaptureActivity-------onStart ->isScreenRecord："+TabletApp.Companion.getApplication().isScreenRecord().get());
        } else {
            if (!isExit && TabletApp.Companion.getApplication().isScreenRecord().get()) {
                startService(new Intent(ScreenCaptureActivity.this, cls).putExtra("showTag", true));
            }
            L.e("ScreenCaptureActivity-------onStop->isScreenRecord：" + TabletApp.Companion.getApplication().isScreenRecord().get());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mBluetoothDevice;

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
        Device device = TabletApp.Companion.getApplication().getCurrent_device();
        if (device != null && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q ) {
            mBluetoothDevice = BluetoothUtil.getInstance().getConnectDevice();
            if(TextUtils.isEmpty(device.device_mac) || device.device_mac.startsWith( "00:00:00")){
                IgrsToast.getInstance().showToast_error(getApplication(),getString(R.string.get_btinfo_fail),5000);
                txt_bt.setText(getString(R.string.btinfo_connect_show, device.room_name));//"请将蓝牙连接至" + device.room_name + "大屏,以传输音频");
                return;
            }
            if (mBluetoothDevice != null && mBluetoothDevice.getAddress().equals(device.device_mac)) {
                L.i("已连接到相应蓝牙设备！");
                layout_bt.setVisibility(View.GONE);
            } else {
                try {
                    if (mBluetoothAdapter == null) {
                        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    }
                    mBluetoothDevice = mBluetoothAdapter.getRemoteDevice(device.device_mac);

                    L.i("未连接到相应蓝牙设备！ 远程设备=" + mBluetoothDevice);
                    // 连接建立之前的先配对
                    if (mBluetoothDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                        Method creMethod = BluetoothDevice.class.getMethod("createBond");
                        L.i("远程设备未配对->start....");
                        creMethod.invoke(mBluetoothDevice);
                        L.i("远程设备未配对->end....");
                    } else {
                        L.i("远程设备已配对->准备连接 start....");
                        BluetoothUtil.getInstance().connect(ScreenCaptureActivity.this, mBluetoothDevice);
                        L.i("远程设备已配对->连接 end....");
                    }
                } catch (Exception e) {
                    L.e("蓝牙连接异常  e:" + e.toString());
                    e.printStackTrace();
                }
                txt_bt.setText(getString(R.string.btinfo_connect_show, device.room_name));//"请将蓝牙连接至" + device.room_name + "大屏,以传输音频");
                layout_bt.setVisibility(View.VISIBLE);
            }
            try {
                L.i("------------------------->device_mac:" + device.device_mac + " btname:" + mBluetoothDevice.getName() + " btmac:" + mBluetoothDevice.getAddress());
            } catch (Exception e) {
                L.i("------------------------->device:" + device + " bt is null");
            }
        } else {
            layout_bt.setVisibility(View.GONE);
        }
        //layout_bt.setVisibility(View.GONE);
    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    /**
     * 停止投屏
     */
    private void closeProjection() {
        AlertDialog alertDialog = new AlertDialog(ScreenCaptureActivity.this);
        alertDialog.setTitle(getString(R.string.screen_capture_exit));
        alertDialog.setConfirmButton_red(getString(R.string.confirm), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                L.e("ScreenCaptureActivity-------onBackPressed mBluetoothDevice:" + mBluetoothDevice);
                if (mBluetoothDevice != null) {
                    BluetoothUtil.getInstance().disconnect(ScreenCaptureActivity.this, mBluetoothDevice);
                    mBluetoothDevice = null;
                }
                try {
                    TabletApp.Companion.getApplication().isScreenRecord().set(false);
                    TabletApp.Companion.getApplication().setLastTaskModel(null);
                    L.e("ScreenCaptureActivity-------onBackPressed taskModel:" + taskModel);
                    if (taskModel != null) {
                        taskModel.cmd = 2;
                        taskModel.taskState = 2;
                        projectionSDK.sendCMDMessage(dev_id, new Gson().toJson(taskModel).getBytes());
                        taskModel = null;
                    }
                } catch (Exception e) {
                    L.e("ScreenCaptureActivity-------onBackPressed send cmd 2 e:" + e.toString());
                }
                stopService(new Intent(ScreenCaptureActivity.this, RuntimeInfoService.class));
                stopService(new Intent(ScreenCaptureActivity.this, cls));
                alertDialog.dismiss();
//                exitHandler.removeMessages(0);
//                exitHandler.removeCallbacksAndMessages(this);
                finish();
            }
        });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_close:
                closeProjection();
                break;
           /* case R.id.txt_state:
                //MediaProjectionManager mediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
                //startActivityForResult(mediaProjectionManager.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION);
                break;*/
            case R.id.layout_window:
                if (activeState == 0) {
                    Dialog_activation dialog_activation = new Dialog_activation(this, getString(R.string.activation_vip));
                    break;
                }
                fullScreenSettingView.switchLayout(Msg19.MODE_WINDOW, "");
                break;
            case R.id.layout_full:
                if (activeState == 0) {
                    Dialog_activation dialog_activation = new Dialog_activation(this, getString(R.string.activation_vip));
                    break;
                }
                if (fullScreenSettingView.getList() != null && fullScreenSettingView.getList().size() > 1) {
                    fullScreenSettingView.setVisibility(View.VISIBLE);
                } else {
                    fullScreenSettingView.switchLayout(Msg19.MODE_FULL_SCREEN, taskModel.taskId);
                }
                break;
            case R.id.layout_split:
                if (activeState == 0) {
                    Dialog_activation dialog_activation = new Dialog_activation(this, getString(R.string.activation_vip));
                    break;
                }
                fullScreenSettingView.switchLayout(Msg19.MODE_DF, "");
                break;
            case R.id.layout_bt:
                startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                break;
            case R.id.txt_home:
                Intent home_intent = new Intent(Intent.ACTION_MAIN);
                home_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                home_intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(home_intent);
                break;
            case R.id.img_setting:
                startActivity(new Intent(this, SettingActivity.class));
                break;
            case R.id.layout_file_transfer:
                startActivity(new Intent(this, TransferDiscoverActivity.class));
                overridePendingTransition(0, 0);
                break;
        }
    }
}
package com.wcch.android.service;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.endo.common.utilcode.util.ScreenUtils;
import com.google.gson.Gson;
import com.igrs.betotablet.Constants;
import com.igrs.betotablet.IScreenConnectService;
import com.igrs.betotablet.R;
import com.igrs.betotablet.TabletApp;
import com.igrs.betotablet.flavor.FlavorUtils;
import com.igrs.betotablet.msg.DestroyTaskEvent;
import com.igrs.betotablet.msg.Msg15;
import com.igrs.betotablet.msg.Msg18;
import com.igrs.betotablet.msg.MsgUtil;
import com.igrs.betotablet.soft.util.AndroidUtil;
import com.igrs.betotablet.soft.util.ConfigUtil;
import com.igrs.betotablet.soft.util.FileUtils;
import com.igrs.betotablet.utils.BroadCastUtils;
import com.igrs.betotablet.utils.LogUtil;
import com.igrs.betotablet.utils.OpenFileUtil;
import com.igrs.betotablet.utils.ProviderUtil;
import com.igrs.betotablet.utils.SPUtils;
import com.igrs.betotablet.utils.WindowFinder;
import com.igrs.betotablet.view.FileReceiveView;
import com.igrs.betotablet.view.ToastUtils;
import com.igrs.betotablet.window.ProjectionWindowManager;
import com.igrs.betotablet.window.SoftWindow;
import com.igrs.sml.AudioManage;
import com.igrs.sml.TaskModel;
import com.igrs.sml.VideoManage;
import com.igrs.sml.event.SystemEvent;
import com.igrs.sml.tcp.TcpCallback;
import com.igrs.sml.tcp.TcpStateCallback;
import com.igrs.sml.util.L;
import com.igrs.tpsdk.ProjectionSDK;
import com.igrs.transferlib.FileTransferEventListener;
import com.igrs.transferlib.FileTransferUtil;
import com.igrs.transferlib.entity.FileTransferError;
import com.igrs.transferlib.entity.FileTransferProgress;
import com.igrs.transferlib.entity.FileTransferRequestInfo;
import com.igrs.transferlib.entity.FileTransferState;
import com.igrs.transferlib.entity.FileTransferTask;
import com.igrs.transferlib.entity.TransferFileInfo;
import com.igrs.transferlib.enums.CoverStrategy;
import com.igrs.transferlib.enums.ResponseType;
import com.igrs.transferlib.enums.TaskState;
import com.igrs.transferlib.enums.TaskType;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


/**
 * create by LZQ on 2021-1-24
 * 投屏服务：每次主动投屏时初始化SDK。
 * 和接收端投屏冲突了。所以需要另外起一个进程
 */
public class ScreenProjectionService extends Service {
    private String TAG = "ScreenProjectionService";

    private final Gson gson = new Gson();
    boolean running = false;
    private boolean isInitSoftProjection = false;

    IBinder binder = new IScreenConnectService.Stub() {
        @Override
        public void connectDeviceByIp(String ip) throws RemoteException {

        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG, "ScreenProjectionService onCreate");
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }


    /**
     * 初始化软投屏
     */
    private final int PORT = 12345;
    private boolean isExit = false;

    public void initSoftProjection() {
    }


    /**
     * 当服务被销毁时，发送一条广播，重新拉起服务
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
    }








}
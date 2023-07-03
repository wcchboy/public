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
import com.wcch.android.App;
import com.wcch.android.Constants;
import com.wcch.android.R;
import com.wcch.android.msg.DestroyTaskEvent;
import com.wcch.android.msg.Msg15;
import com.wcch.android.msg.Msg18;
import com.wcch.android.msg.MsgUtil;
import com.wcch.android.soft.util.BroadCastUtils;
import com.wcch.android.soft.util.FileUtils;
import com.wcch.android.soft.util.LogUtil;
import com.wcch.android.soft.view.FileReceiveView;
import com.wcch.android.utils.AndroidUtil;
import com.wcch.android.utils.OpenFileUtil;
import com.wcch.android.utils.ProviderUtil;
import com.wcch.android.utils.SPUtils;
import com.wcch.android.utils.WindowFinder;
import com.wcch.android.view.ToastUtils;
import com.wcch.android.window.ProjectionWindowManager;
import com.wcch.android.window.SoftWindow;

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
 * 投屏服务：开机自动启动,监听投屏源。
 */
public class ProjectionService extends Service implements TcpCallback, TcpStateCallback,
        FileTransferEventListener {
    private String TAG = "ProjectionService";

    private final Gson gson = new Gson();
    boolean running = false;
    private boolean isInitSoftProjection = false;

    private boolean isInitSoftProjectionSDKSuccess = false;
    private String currentSource = "ANDROID";
    //private View castCodeFloatView;
    private WindowManager windowManager;
    Map<String, TaskModel> lFDMap = new HashMap<>();
    private final int projectionServiceId = 1;
    private TaskModel fromModel = null;

    private Timer mTimer;//调度器
    private TimerTask mTask;
    private int temp_duration;
    private int countDownTime = 5;//默认需要倒计时的时间

    View dialogView;
    AlertDialog dialog;
    Button btn_cancel;
    Button btn_agree;
    TextView title;
    TextView subTitle;
    TaskModel tmpTaskModel;//弹框后取得数据

    private Handler mHandler;
    private ProjectionSDK projectionSDK;

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.d(TAG, "ProjectionService onCreate");
        //在 Service 生命周期方法 onCreate() 中初始化 mHandler
        mHandler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1001:
                        if (temp_duration > 0) {
                            if (btn_agree != null) {
                                btn_agree.setText(String.format(getString(R.string.req_pro_sceen_tip_btn_agree), temp_duration + ""));
                            }
                        } else {
                            btn_agree.setText(String.format(getString(R.string.req_pro_sceen_tip_btn_agree), "0"));
                            if (dialog != null) {
                                dialog.dismiss();
                            }
                            stopTimer();
                            //倒计时还未选择，默认开启投屏
                            RequestScreenCasting();
                        }
                        break;
                    case 1002:
                        //点击了同意或者取消
                        if (dialog != null) {
                            dialog.dismiss();
                            stopTimer();
                        }
                        break;
                    default:
                        super.handleMessage(msg);
                }
            }
        };
        //适配8.0service
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel mChannel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel("InitService", getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_LOW);
            notificationManager.createNotificationChannel(mChannel);
            Notification notification = new Notification.Builder(getApplicationContext(),
                    "InitService").build();
            startForeground(projectionServiceId, notification);
        }
        boolean registered = EventBus.getDefault().isRegistered(this);
        if (!registered) {
            EventBus.getDefault().register(this);
        }
        running = true;

        if (!isInitSoftProjection) {
            //initSoftProjection();//初始化软投屏
        }

        FileReceiveView.getInstance().init(this);
        String deviceName = ProviderUtil.getDeviceName(getContentResolver());
        if (TextUtils.isEmpty(deviceName)) {
            deviceName = Settings.Global.getString(getContentResolver(),
                    Settings.Global.DEVICE_NAME);
        }
        int controlPort = 11320;
        int dataPort = 11321;
        int bufferSize = 1024 * 1024;
        FileTransferUtil.getInstance().init(controlPort, dataPort, bufferSize,
                AndroidUtil.INSTANCE.getAndroidID(this), deviceName, this);


    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /*if(projectionSDK == null) {
            projectionSDK = new ProjectionSDK();
        }*/
        projectionSDK = ProjectionSDK.getInstance();
        initSoftProjection();
        return START_STICKY;
    }


    /**
     * 初始化软投屏
     */
    private final int PORT = 12345;
    private boolean isExit = false;

    public void initSoftProjection() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //需要权限检查一下
        //boolean result = projectionSDK.init("", PORT,BluetoothAdapter.getDefaultAdapter().getAddress());
        boolean result = ProjectionSDK.getInstance().init("", PORT, BluetoothAdapter.getDefaultAdapter().getAddress());
        if (!result) {
            ToastUtils.getInstance().showToast(ToastUtils.ToastType.ERROR,getString(R.string.init_fail));
            isInitSoftProjectionSDKSuccess = false;
            return;
        } else {
            isInitSoftProjectionSDKSuccess = true;
        }


        projectionSDK.setTcpCMDCallback(this);//手机端发投屏指令。
        projectionSDK.setTcpStateCallback(this);//检测连接状态 手机端投屏 大屏连接状态，断连等
    }


    /**
     * 当服务被销毁时，发送一条广播，重新拉起服务
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.e("zjx", "Projection Service Destroy");
        BroadCastUtils.sendProjectionStatus(App.getInstance().getApplicationContext(),
                BroadCastUtils.PROJECTION_STOP);
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        stopForeground(true);
        Intent intent = new Intent("com.igrs.projection.service.destroy");
        sendBroadcast(intent);
    }

    /*long cameraTask = 0;
    private List<String> projectionList = new ArrayList();*/

    @SuppressLint({"StringFormatInvalid", "WrongConstant"})
    @Override
    public void rev_msg(String dev_id, byte type, byte[] data) {
        try {
            TaskModel fromModel = gson.fromJson(new String(data), TaskModel.class);
            fromModel.dev_id = dev_id;
            LogUtil.e("zjx", "接收消息：cmd:" + fromModel.cmd + "   type:" + fromModel.taskType + "  " +
                    "dev_id:" + dev_id);
            if (fromModel.cmd == 1) {
                //达到最大投屏数
                /*if (fromModel.retry == 1) {//是否在OPS端
                    boolean inMap = taskInMap(fromModel.dev_id);
                    if (!inMap) {
                        MsgUtil.sendCmd10Msg(fromModel.dev_id, fromModel.taskId);
                        return;
                    }
                } else {*/
                if ((fromModel.taskType == 0 || fromModel.taskType == 1 || fromModel.taskType == 2 || fromModel.taskType == 3) && fromModel.useCustomSink != 1) {
                    //int windowCount = ProjectionWindowManager.getInstance().getWindowCount();
                    int windowCount = 1;
                    LogUtil.d(TAG, "windowCount:" + windowCount);
                    if (windowCount > 1) {
                        //当前存在记录投屏，不让再投，通过这个发送消息
                        MsgUtil.sendCmd17Msg(fromModel.dev_id, fromModel.taskId, 0);
                        return;
                    }
                }

                //tab 不支持遥控
                    /*if (fromModel.taskType == 4) {
                        boolean supportScreenCast = PolicyManager.getInstance().getPolicy()
                        .isSupportScreenCast();
                        // MS2 屏蔽 遥控功能,提示该机型不支持遥控
                        if (BuildConfig.FLAVOR=="s2"||BuildConfig.FLAVOR=="m2" || FlavorUtils
                        .isVisteng()){
                            MsgUtil.sendCmd17Msg(fromModel.dev_id, fromModel.taskId, 10);
                            ToastUtils.getInstance().showToast(ToastUtils.ToastType.ERROR,
                            "该机型不支持遥控功能");
                            return;
                        }
                        if (!supportScreenCast) {
                            MsgUtil.sendCmd17Msg(fromModel.dev_id, fromModel.taskId, 8);
                            return;
                        }
                        if (sendDeskMap.size() >= PolicyManager.getInstance().getPolicy()
                        .getSupportScreenCastCount()) {//达到最大遥控数
                            MsgUtil.sendCmd17Msg(fromModel.dev_id, fromModel.taskId, 1);
                            return;
                        }
                    }*/
                LogUtil.e("initTask dev_id = " + dev_id);
                int result = initTask(fromModel);
                LogUtil.e("initTask result = " + result);
                //}

            } else if (fromModel.cmd == 2) {
                LogUtil.e("initTask cmd  = 2");
                if (fromModel != null) {
                    switch (fromModel.taskState) {
                        case 0: {//启动
                            TaskModel model = getTaskModel();
                            if (model != null) {
//                                L.e("start task = " + model.taskId);
                                if (model.targetName.startsWith("PCbeto")) {
                                    lFDMap.put(fromModel.taskId, fromModel);
                                }
                                TaskModel tempModel = fromModel;
                                tempModel.taskState = 0;
                                projectionSDK.sendCMDMessage(tempModel.dev_id,
                                        gson.toJson(tempModel).getBytes());
                                start(fromModel.taskId);
                            }
                        }
                        break;
                        case 1: {// 停止
//                            LogUtil.e(TAG, "rev_msg: stop fromModel.taskId = " + fromModel
//                            .taskId);
                            stop(fromModel.taskId);
                        }
                        break;
                        case 2: {//释放
//                            LogUtil.e(TAG, "rev_msg: destroy fromModel.taskId = " + fromModel
//                            .taskId);
                            destroy(fromModel.taskId);
                        }
                        break;
                    }
                }
            } else if (fromModel.cmd == 10) {
                LogUtil.d("zjx", "收到cmd 10 ,发送给beto消息 ");
                //  sendToBeTo(fromModel.taskId);
            } else if (fromModel.cmd == 11) {
                if (fromModel != null) {
                    setParam(gson.toJson(fromModel));
                }
            } else if (fromModel.cmd == 13) {
//                //"type":1 // 1、app在前台， 2、app在后台， 3、屏幕关屏， 4、屏幕点亮， 5、手机已解锁
            } else if (fromModel.cmd == 14) {
//                Log.d(TAG, "tcp_msg_proc_cb: cmd = " + 14);
//                Msg14 msg14 = gson.fromJson(new String(data), Msg14.class);
            } else if (fromModel.cmd == 15) {
                Msg15 msg15 = gson.fromJson(new String(data), Msg15.class);
                int ori = msg15.getOri();
                ProjectionWindowManager.getInstance().onScreenOri(fromModel.dev_id, ori);
            } else if (fromModel.cmd == 18) {
                    Msg18 msg18 = gson.fromJson(new String(data), Msg18.class);
                    ProjectionWindowManager.getInstance().onModeChange(msg18);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stateChange(String dev_id, boolean isConnect) {
        LogUtil.e("stateChange: dev_id = " + dev_id + "  connect " + isConnect);
        SoftWindow softWindowByDevId = WindowFinder.getInstance().getSoftWindowByDevId(dev_id);
        if (softWindowByDevId != null && !isConnect) {
            /*if (FlavorUtils.isPublic()) {
                ActivityWindowManager.getInstance().removeWindow(softWindowByDevId);
            } else {
                ProjectionWindowManager.getInstance().removeWindow(softWindowByDevId, true);
            }*/
            ProjectionWindowManager.getInstance().removeWindow(softWindowByDevId, true);
            offLine(dev_id);
            @SuppressLint({"StringFormatInvalid", "LocalSuppress"}) String deviceDisStr =
                    getString(R.string.device_disconnect, softWindowByDevId.getDeviceName());
            ToastUtils.getInstance().showToast(ToastUtils.ToastType.NORMAL, deviceDisStr);
        }
    }


    private final Object checkOffLine = new Object();

    /**
     * 多实例同时运行---支持多投的
     * <p>
     * param   投屏任务的初始化配置，考虑到参数的多样 性，使用 Json 格式作为参数传递
     * taskId  任务ID
     *
     * @return 状态码，0 成功
     */
    public int initTask(TaskModel fromModel) {
        this.tmpTaskModel = fromModel;
        try {
            if (fromModel.taskType != 5) {//不是文件传输
                L.i(" initTask->check other task start");
                /*String removeTaskId = "";
                List<String> tempList = new ArrayList<>();
                synchronized (checkOffLine) {
                        try {
                            TaskModel taskModel = getTaskModel();
                            LogUtil.e("taskModel dev_id = " + taskModel.dev_id + "   taskID = " +
                             taskModel.taskId);
                            //同一台设备 只支持单任务
                            if (taskModel.dev_id.equals(fromModel.dev_id)) {
//                                destroy(taskModel.taskId);
                                tempList.add(taskModel.taskId);
//                                removeTaskId = taskModel.taskId;
                            }
                        } catch (Exception e) {
                            L.e(e.getMessage());
                        }
                    L.i("initTask->check other task end");
                }*/
                if (getTaskModel() != null) {
                    destroy(getTaskModel().taskId);
                }
            }

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    //show dialog
                    showDialog();
                }
            });

            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("initTask->e:" + e.toString());
            return 2;
        }
//        }
    }

    /**
     * 答复投屏请求
     */
    private void RequestScreenCasting() {
        if (this.tmpTaskModel == null) return;
        LogUtil.e("ShanLian111", "init task taskId = " + this.tmpTaskModel.taskId);
        TaskModel toModel = new TaskModel();
        toModel.cmd = 1;
        toModel.taskId = this.tmpTaskModel.taskId;
        setTaskModel(this.tmpTaskModel);
        toModel.taskState = 0;
        LogUtil.e(this.tmpTaskModel.dev_id + "--->" + gson.toJson(toModel));
        LogUtil.i(TAG, "答复投屏请求 this.tmpTaskModel.dev_id:"+",toModel:"+toModel.toString());
        projectionSDK.sendCMDMessage(this.tmpTaskModel.dev_id,
                gson.toJson(toModel).getBytes());
        //loginResult(this.tmpTaskModel.dev_id, 0, true, "");

    }

    //计时开始
    private void startTimer() {
        temp_duration = countDownTime;
        mTimer = new Timer();
        mTask = new TimerTask() {
            @Override
            public void run() {
                LogUtil.d(TAG, "temp_duration:" + temp_duration);
                temp_duration--;
                mHandler.sendEmptyMessage(1001);
                LogUtil.d(TAG, "2 temp_duration:" + temp_duration);

            }
        };
        mTimer.schedule(mTask, 0, 1000);//调度分配，延迟0秒，时间间隔为1秒
    }

    //计时结束
    private void stopTimer() {
        temp_duration = 0;
        if (mTask != null) {
            mTask.cancel();
            mTask = null;
        }
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    private void justShowDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext())
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("service中弹出Dialog了")
                .setMessage("是否关闭dialog？")
                .setPositiveButton("确定",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                            }
                        })
                .setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int whichButton) {
                            }
                        });
        //下面这行代码放到子线程中会 Can't create handler inside thread that has not called Looper.prepare()
        AlertDialog dialog = builder.create();
        //设置点击其他地方不可取消此 Dialog
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY));
        dialog.show();
    }

    private void showDialog() {
        dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_alert_casting, null, false);
        dialog = new AlertDialog.Builder(getApplicationContext()).setView(dialogView).create();
        //dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        btn_cancel = dialogView.findViewById(R.id.bt_left);
        btn_agree = dialogView.findViewById(R.id.bt_right);
        title = dialogView.findViewById(R.id.tv_alert_content);
        subTitle = dialogView.findViewById(R.id.tv_alert_second_content);
        subTitle.setVisibility(View.GONE);


        title.setText(String.format(getString(R.string.req_pro_sceen_tip_title),
                this.tmpTaskModel.deviceName));
        title.setText(R.string.req_pro_sceen_tip_title);
        btn_cancel.setText(R.string.req_pro_sceen_tip_btn_refuse);

        btn_agree.setText(String.format(getString(R.string.req_pro_sceen_tip_btn_agree),
                countDownTime + ""));
        startTimer();
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHandler.sendEmptyMessage(1002);
                //dialog.dismiss();
            }
        });

        btn_agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestScreenCasting();
                mHandler.sendEmptyMessage(1002);
                //dialog.dismiss();
            }
        });

        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);

        dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY));
        dialog.show();
        //此处设置位置窗体大小，我这里设置为了手机屏幕宽度的3/4  注意一定要在show方法调用后再写设置窗口大小的代码，否则不起效果会
        //dialog.getWindow().setLayout((ScreenUtils.getScreenWidth(this)/4*3), LinearLayout
        // .LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setLayout((ScreenUtils.getScreenWidth()/3*1), LinearLayout.LayoutParams.WRAP_CONTENT);
    }


    public TaskModel getTaskModel() {
        return fromModel;
    }

    public void setTaskModel(TaskModel task) {
        fromModel = task;
    }


    /**
     * 多实例同时运行---支持多投的
     * <p>
     * param   投屏任务的初始化配置，考虑到参数的多样 性，使用 Json 格式作为参数传递
     * taskId  任务ID
     *
     * @return 状态码，0 成功
     */
    /*public int initTask(TaskModel fromModel) {
//        synchronized (checkOffLine) {
        try {
            LogUtil.e("1 initTask->fromModel:" + new Gson().toJson(fromModel) + " size:" +
            taskModelMap.size());
            if (fromModel.taskType != 5) {//不是文件传输
                L.i(" initTask->check other task start");
                String removeTaskId = "";
                List<String> tempList = new ArrayList<>();
                synchronized (checkOffLine) {
                    Iterator<Map.Entry<String, TaskModel>> iterator = taskModelMap.entrySet()
                    .iterator();
                    while (iterator.hasNext()) {
                        try {
                            TaskModel taskModel = iterator.next().getValue();
//                            L.i("check from dev_id:" + fromModel.dev_id + " dev_id:" +
taskModel.dev_id + " taskId:" + taskModel.taskId);
                            LogUtil.e("taskModel dev_id = " + taskModel.dev_id + "   taskID = " +
                             taskModel.taskId);
                            //同一台设备 只支持单任务
                            if (taskModel.dev_id.equals(fromModel.dev_id)) {
//                                destroy(taskModel.taskId);
                                tempList.add(taskModel.taskId);
//                                removeTaskId = taskModel.taskId;
                            }
                        } catch (Exception e) {
                            L.e(e.getMessage());
                        }
                    }
                    L.i("initTask->check other task end");
                }
                if (tempList.size() > 0) {
                    for (String s : tempList) {
                        destroy(s);
                    }
                }
                tempList.clear();

//                synchronized (checkOffLine) {
//                    Iterator<Map.Entry<String, TaskModel>> iterator = taskModelMap.entrySet()
.iterator();
//                    while (iterator.hasNext()) {
//                        try {
//                            TaskModel taskModel = iterator.next().getValue();
//                            L.i("check from dev_id:" + fromModel.dev_id + " dev_id:" +
taskModel.dev_id + " taskId:" + taskModel.taskId);
//                        } catch (Exception e) {
//                            L.e(e.getMessage());
//                        }
//                    }
//                }
            }

            LogUtil.e("2 initTask->check start task size:" + taskModelMap.size());
            LogUtil.e("ShanLian111", "init task taskId = " + fromModel.taskId);
            TaskModel toModel = new TaskModel();
            toModel.cmd = 1;
            toModel.taskId = fromModel.taskId;
            toModel.activeState = PolicyManager.getInstance().getPolicy().isActive() ? 1 : 0;
            taskModelMap.put(fromModel.taskId, fromModel);
            toModel.taskState = 0;
            LogUtil.e(fromModel.dev_id + "--->" + gson.toJson(toModel));
            ProjectionSDK.getInstance().sendCMDMessage(fromModel.dev_id, gson.toJson(toModel)
            .getBytes());
            //loginResult(fromModel.dev_id, 0, true, "");
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("initTask->e:" + e.toString());
            return 2;
        }
//        }
    }*/

    /***
     * 开始任务
     *      开始投屏，遥控，文件传输任务
     * @param taskId
     * @return
     */
    public int start(String taskId) {
        L.e("start taskId = " + taskId);
//        synchronized (checkOffLine) {
        TaskModel model = getTaskModel();
        if (model != null) {
            switch (model.taskType) {
                case 0://全屏投屏
                case 1://窗口投屏
                case 2://扩展屏投屏
                case 3://区域投屏
                    VideoManage.getInstance().clean(model.dev_id);
                    AudioManage.getInstance().clean(model.dev_id);

                    SoftWindow softWindow = new SoftWindow();
                    softWindow.setDev_id(model.dev_id);
                    softWindow.setTaskModel(model);
                    ProjectionWindowManager.getInstance().addWindow(softWindow);
                    AudioManage.getInstance().startPlay(getSystemService(AudioManager.class),
                            model.dev_id);

                    break;
                case 4://遥控
                    L.i(model.dev_id + "-" + model.taskId + " start->遥控" + " useCustomSource:" + model.useCustomSource);
                    //addSendDeskClient(this, model);
                    break;
                case 5://文件传输
                    L.i(model.dev_id + "-" + model.taskId + " start->文件传输");
                    //QCSDK.getInstance()._readyRecvFile(model.dev_id, "/sdcard");
                    break;
            }
        } else {
            L.e("对方 start->未查询到taskid=" + taskId + " 的任务");
        }
//        }
        return 0;
    }

    /**
     * 停止任务
     * 发送文件到大屏，但不释放当前的所有任务已经使用的链路和模块，以便快速重新 Start()
     *
     * @param taskId
     * @return
     */
    public long stop(String taskId) {
        TaskModel model = getTaskModel();
        if (model != null) {
            L.i("stop->taskType:" + model.taskType + " taskId:" + taskId + "  \n" + Log.getStackTraceString(new Throwable()));
            if (model.taskType == 0 || model.taskType == 1 || model.taskType == 2 || model.taskType == 3) {
//                if (player4DesktopListener != null) {
//                    player4DesktopListener.removePlayerCallBack(model);
//                }
//                SoftWindow softWindow = new SoftWindow();
//                softWindow.setTaskModel(model);
//                softWindow.setDev_id(model.dev_id);
//                ProjectionWindowManager.getInstance().removeWindow(softWindow,true);

            } else if (model.taskType == 4) {
                //removeSendDeskClient(this, model);
            }
            model.cmd = 10;
            model.taskState = 1;//1 停止 2销毁
            projectionSDK.sendCMDMessage(model.dev_id, gson.toJson(model).getBytes());
            return 0;
        }
        return 1;
    }

    public long destroy(String taskId) {
        TaskModel model = getTaskModel();
        if (model != null) {
            LogUtil.e("destroy->taskType:" + model.taskType + " taskId:" + taskId);
            if (model.taskType == 0 || model.taskType == 1 || model.taskType == 2 || model.taskType == 3) {
                SoftWindow softWindow = new SoftWindow();
                softWindow.setTaskModel(model);
                softWindow.setDev_id(model.dev_id);
                ProjectionWindowManager.getInstance().removeWindow(softWindow, true);
                //cleanTaskModelFromMap(model.dev_id);
                VideoManage.getInstance().clean(model.dev_id);
                AudioManage.getInstance().clean(model.dev_id);
            } else if (model.taskType == 4) {
                //removeSendDeskClient(this, model);
            }
            LogUtil.e("destroy taskMap remove->taskId:" + taskId);
//            synchronized (checkOffLine) {
            //taskModelMap.remove(taskId);
//            }

            model.cmd = 10;
            model.taskState = 2;//1 停止 2销毁
            projectionSDK.sendCMDMessage(model.dev_id, gson.toJson(model).getBytes());
            return 0;
        }
        return 1;
    }


    public void offLine(String dev_id) {
        LogUtil.e("offLine start-client_id:" + dev_id + " dev_id:" + dev_id + "---->>>>>>>>\n");
        synchronized (checkOffLine) {

            TaskModel taskModel = getTaskModel();
            if (taskModel.dev_id.equals(dev_id)) {
                LogUtil.e("offLine remove ip:" + dev_id + " ->del  taskId:" + taskModel.taskId +
                        " taskType:" + taskModel.taskType);
                if (taskModel.taskType == 0 || taskModel.taskType == 1 || taskModel.taskType == 2 || taskModel.taskType == 3) {
                    SoftWindow softWindow = new SoftWindow();
                    softWindow.setTaskModel(taskModel);
                    softWindow.setDev_id(taskModel.dev_id);
                    ProjectionWindowManager.getInstance().removeWindow(softWindow, true);
//                    removeSendDeskClient(this, taskModel);
                } else if (taskModel.taskType == 4) {
                    //removeSendDeskClient(this, taskModel);
                }
                setTaskModel(null);
            }
            LogUtil.e("offLine end jni_delTransNode- dev_id:" + dev_id);
        }
        //cleanTaskModelFromMap(dev_id);
        VideoManage.getInstance().clean(dev_id);
        AudioManage.getInstance().clean(dev_id);
    }

    /**
     * 运行时参数设置
     * 设置运行时参数，如分辨率，方向，fps，音量，静音等
     *
     * @param param
     * @return
     */
    public int setParam(String param) {
        try {
//            L.i("setParam param:" + param);
            TaskModel taskModel = null;
            TaskModel fromModel = gson.fromJson(param, TaskModel.class);
            if (fromModel != null && !TextUtils.isEmpty(fromModel.taskId)) {
                taskModel = getTaskModel();
                if (taskModel != null) {
                    if (fromModel.dataType == 0) {//视频
                        if (taskModel.useCustomSource == 0) {
                            //QCSDK.getInstance().removeSendDeskClient(context, taskModel);
                            taskModel.fps = fromModel.fps;
                            taskModel.resolution_x = fromModel.resolution_x;
                            taskModel.resolution_y = fromModel.resolution_y;
                            //QCSDK.getInstance().addSendDeskClient(context, taskModel);
                            EventBus.getDefault().post(new SystemEvent(SystemEvent.TYPE_VIDEO_SIZE, taskModel));
                        }
                    } else if (fromModel.dataType == 1) {//外部音频
                        //渲染
                        //QCSDK.getInstance()._setAudioPlayMode(taskModel.dev_id, fromModel
                        // .useCustomSink != 1);
                    }
                } else {
//                    L.e("setParam not find->taskId:" + taskModel.taskId);
                    return 1;
                }
            }
            L.i(fromModel.dev_id + " setParam->dataType:" + fromModel.dataType + " useCustomSink" +
                    ":" + fromModel.useCustomSink + " useCustomSource:" + fromModel.useCustomSource);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }


    //如果不要支持多路这里就不需要了
    /*private HashMap<String, TaskModel> taskModelMap = new HashMap<>();
    private HashMap<String, Integer> sendDeskMap = new HashMap<>();
    private final Object sendLock = new Object();
    private boolean needIdr = false;

    public void cleanTaskModelFromMap(String dev_id) {
        Iterator<Map.Entry<String, TaskModel>> iterator = taskModelMap.entrySet().iterator();
        while (iterator.hasNext()) {
            try {
                TaskModel taskModel = iterator.next().getValue();
                if (taskModel.dev_id.equals(dev_id)) {
                    LogUtil.e("clean taskMaps remove:" + taskModel.taskId + " old size:" +
                    taskModelMap.size());
                    taskModelMap.remove(taskModel);
                }
            } catch (Exception e) {
            }
        }
    }


    private boolean taskInMap(String devId) {
        Iterator<Map.Entry<String, TaskModel>> iterator = taskModelMap.entrySet().iterator();
        while (iterator.hasNext()) {
            TaskModel model = iterator.next().getValue();
            if (model.dev_id.equals(devId)) {
                return true;
            }
        }
        Iterator<Map.Entry<String, Integer>> sendIterator = sendDeskMap.entrySet().iterator();
        while (sendIterator.hasNext()) {
            String key = sendIterator.next().getKey();
            if (key.equals(devId)) {
                return true;
            }
        }
        return false;
    }


    public List<String> getSendDesktopList() {
        if (sendDeskMap.size() == 0) {
            return null;
        }
        List<String> sendDesktopList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : sendDeskMap.entrySet()) {
            if (entry.getValue() == 1) {
                sendDesktopList.add(entry.getKey());
            }
        }
        //L.e("fps","getSendDesktopList--size:" + sendDesktopList.size());
        return sendDesktopList;
    }

    *//**
     * @return
     *//*
    public int getSendDeskMapSize() {
        if (sendDeskMap == null) return -1;
        return sendDeskMap.size();
    }

    //设置要发送桌面的列表
    public void setSendDeskSwitch(String ip, boolean isSend) {
        L.e("setSendDeskSwitch--ip:" + ip + " isSend:" + isSend);
        Integer canSend = sendDeskMap.get(ip);
        if (canSend != null) {
            int temp = canSend.intValue();
            canSend = isSend ? 1 : 2;
            if (temp == 2 && isSend) {
                EventBus.getDefault().post(new SystemEvent(SystemEvent.TYPE_VIDEO_REQ_KEY));
            }
            L.i("setSendDeskSwitch--end-canSend:" + canSend + " oldCanSend:" + temp + " ip:" + ip);
        }
    }

    //添加要发送桌面的列表
    public void addSendDeskClient(Context context, TaskModel taskModel) {
        L.i("addSendDeskClient--start\n"
                + " sendDeskMap.size:" + sendDeskMap.size() + "\n"
                + " dev_id:" + taskModel.dev_id + "\n"
                + " taskId:" + taskModel.taskId + "\n"
                + " context=" + context);
        synchronized (sendLock) {
            if (context != null && taskModel != null && !TextUtils.isEmpty(taskModel.dev_id)) {
                sendDeskMap.put(taskModel.dev_id, 1);
                context.startService(new Intent(context, ScreenRecordService_sys.class));
            } else {
                L.e("addSendDeskClient--error-----");
            }
        }
        needIdr = true;
    }

    //删除要发送桌面的列表
    public void removeSendDeskClient(Context context, TaskModel taskModel) {
        L.i("removeSendDeskClient--start-big.size:" + sendDeskMap.size() + " small.size:" +
        sendDeskMap.size() + " dev_id:" + taskModel.dev_id + " taskId:" + taskModel.taskId);
        try {
            sendDeskMap.remove(taskModel.dev_id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (context != null && sendDeskMap.size() == 0) {
            context.stopService(new Intent(context, ScreenRecordService_sys.class));
        }
    }
*/
    /**
     * 设置页展示投屏码按钮操作消息
     */
  /*  @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCastCodeEvent(CastCodeEvent event) {
        if (event == null) return;
        if (event.isShow()) {
            showCastCodeWindow();
        } else {
            hiddenCastCodeWindow();
        }
    }*/

    /**
     * 主动关闭投屏
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDestroyTaskEvent(DestroyTaskEvent event) {
        if (event == null) return;
        if (TextUtils.isEmpty(event.getTaskId())) return;
        destroy(event.getTaskId());
    }


    @Override
    public void transferRequestEvent(FileTransferRequestInfo fileTransferRequestInfo) {
        FileTransferUtil.getInstance().FileTransferResponse(fileTransferRequestInfo.getTaskId(),
                ResponseType.confirming, FileUtils.receiveFileSavePath(), CoverStrategy.ReWrite);
    }

    @Override
    public void taskErrorEvent(FileTransferError fileTransferError) {

    }

    @Override
    public void progressChangeEvent(FileTransferProgress fileTransferProgress) {

    }

    @Override
    public void taskStateChangeEvent(FileTransferState fileTransferState) {
        if (fileTransferState.getStat() == TaskState.TransferDone.getValue() && fileTransferState.getType() == TaskType.receiver.ordinal()) {
            FileTransferTask transferTask =
                    FileTransferUtil.getInstance().getTask(fileTransferState.getTaskId());
            if (transferTask != null) {
                TransferFileInfo[] fileInfos = transferTask.getRequestInfo().getFileList();
                if (fileInfos.length > 0) {
                    String path =
                            FileUtils.receiveFileSavePath() + "/" + fileInfos[0].getName() + fileInfos[0].getExtensionName();
                    Log.e(TAG, "path==" + path);
                    boolean autoOpenFile = SPUtils.getBool(Constants.AUTO_OPEN_FILE, false);
                    if (autoOpenFile) {
                        boolean allTypeSame = false;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            allTypeSame =
                                    Arrays.stream(fileInfos).allMatch(info -> info.getExtensionName().equalsIgnoreCase(fileInfos[0].getExtensionName()));
                        }
                        if (allTypeSame) {
                            Intent intent = OpenFileUtil.openFile(this, path);
                            if (intent == null) {
                                ToastUtils.getInstance().showToast(ToastUtils.ToastType.ERROR,
                                        getString(R.string.file_not_exit));
                            } else {
                                startActivity(intent);
                            }
                        } else {
                            FileReceiveView.getInstance().show(path);
                        }
                    } else {
                        FileReceiveView.getInstance().show(path);
                    }
                }
            }
        }
    }

}
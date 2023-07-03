package com.wcch.android.window;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.MediaFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.igrs.sml.PlayerView;
import com.igrs.sml.TaskModel;
import com.igrs.sml.callback.DecoderCallback;
import com.igrs.sml.event.SystemEvent;
import com.wcch.android.App;
import com.wcch.android.R;
import com.wcch.android.config.Config;
import com.wcch.android.msg.DestroyTaskEvent;
import com.wcch.android.msg.Msg18;
import com.wcch.android.msg.MsgUtil;
import com.wcch.android.soft.util.BroadCastUtils;
import com.wcch.android.soft.util.LogUtil;
import com.wcch.android.soft.util.WindowLocationUtil;
import com.wcch.android.utils.ScreenShotUtils;
import com.wcch.android.utils.StringUtil;
import com.wcch.android.utils.SystemHelper;
import com.wcch.android.utils.WindowFinder;
import com.wcch.android.utils.WindowResUtil;
import com.wcch.android.view.ToastUtils;
import com.wcch.android.view.TouchFrameLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;


/**
 * @author created by Lzq
 * @time：2022/3/16
 * @Des：白板内投屏窗口管理。处理投屏窗口添加、移除、移动、缩放等操作。
 */
public class ProjectionWindowManager implements View.OnClickListener, View.OnTouchListener,
        TouchFrameLayout.OnFrameLayoutTouchListener, View.OnKeyListener {
    private String TAG = "ProjectionWindowManager";
    //private static final int MAX_WINDOW_COUNT = 4;//最大投屏窗口数量
    private static ProjectionWindowManager projectionWindowManager;
    private WindowManager manager;
    private List<Window> windowList = new ArrayList<>();
    private List<SoftWindow> preSoftWindowList = new ArrayList<>();
    static Object mLock = new Object();
    private int currentMode = Config.WINDOW_MODE;
    private int lastMode = Config.WINDOW_MODE;
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private final int fullScreenWindowBgColor = Color.parseColor("#FF626D82");//窗口全屏背景颜色
    private int windowBgColor = Color.parseColor("#00FFFFFF");//窗口背景颜色

    private ProjectionWindowManager() {
    }

    public static ProjectionWindowManager getInstance() {
        synchronized (mLock) {
            if (projectionWindowManager == null) {
                projectionWindowManager = new ProjectionWindowManager();
            }
            return projectionWindowManager;
        }
    }


    /**
     * 添加一个投屏窗口
     * 1：区分投屏类型：W20 or 软投屏
     * 2：加载对应窗口
     * 3：调整窗口位置
     */
    public void addWindow(Window window) {
        LogUtil.e("zjx", "windowlist size : " + windowList.size());
        if (windowList.size() == 0) {
            BroadCastUtils.sendProjectionStatus(App.getInstance().getApplicationContext(), BroadCastUtils.PROJECTION_START);
        }
        if (window == null) {
            return;
        }
        if (!canInsertWindow()) {
            return;
        }
        if (window instanceof SoftWindow) {
            mainHandler.post(() -> {
                LogUtil.d("addWindow task = " + ((SoftWindow) window).getTaskModel().taskId);
                SoftWindow softWindow = (SoftWindow) window;
                addSoftWindow(softWindow);
            });
        }
    }

    /**
     * 添加一个软投屏窗口
     *
     * @param softWindow
     */
    private void addSoftWindow(SoftWindow softWindow) {
        if (softWindow == null) return;
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        //去重
        removeRepeatedWindowByDevId(softWindow.getDev_id());
        if (manager == null) {
            manager = (WindowManager) App.getInstance().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        }
        RelativeLayout rlWindowView = (RelativeLayout) LayoutInflater.from(App.getInstance().getApplicationContext()).inflate(R.layout.layout_soft_window_floating, null);
        rlWindowView.setOnKeyListener(this::onKey);
        rlWindowView.requestFocus();
        TouchFrameLayout flVideoParentView = rlWindowView.findViewById(R.id.fl_video_parent_view);
        flVideoParentView.setOnFrameLayoutTouchListener(this);
        //CardView cardView = rlWindowView.findViewById(R.id.card_view);
        PlayerView playerView = rlWindowView.findViewById(R.id.playView);
        //ConstraintLayout toolBar = rlWindowView.findViewById(R.id.viewControl);
        //ImageView screenShot = rlWindowView.findViewById(R.id.iv_screen_shot);
        ImageView ivClose = rlWindowView.findViewById(R.id.img_close);
        //ImageView ivScale = rlWindowView.findViewById(R.id.iv_scale_window);
        /*RelativeLayout cardBg = rlWindowView.findViewById(R.id.card_bg);
        LinearLayout windowStatusIconGroup = rlWindowView.findViewById(R.id.ll_window_status_icon_group);
        RelativeLayout dfModeViewGroup = rlWindowView.findViewById(R.id.rl_df_mode);
        ImageView ivDfModeIcon = rlWindowView.findViewById(R.id.iv_df_mode_icon);
        ImageView ivDfMode = rlWindowView.findViewById(R.id.iv_df_mode);
        ImageView ivFullScreenMode = rlWindowView.findViewById(R.id.iv_full_screen);
        ImageView fullScreenModeIcon = rlWindowView.findViewById(R.id.iv_full_screen_icon);
        ImageView lockScreenIcon = rlWindowView.findViewById(R.id.iv_lock_screen_icon);*/
//        TextView tvToolBarDevicesName = rlWindowView.findViewById(R.id.tv_tool_bar_device_name);
        RelativeLayout rlFocusBg = rlWindowView.findViewById(R.id.rl_focus_bg);

       /* ImageView ivWindowModeIcon = rlWindowView.findViewById(R.id.iv_window_mode_icon);
        RelativeLayout rlWindowMode = rlWindowView.findViewById(R.id.rl_window_mode);
        RelativeLayout rlFullScreenMode = rlWindowView.findViewById(R.id.rl_full_screen_mode);
        RelativeLayout rlDfMode = rlWindowView.findViewById(R.id.rl_df_mode);
        LinearLayout llDeviceIconAndNameView = rlWindowView.findViewById(R.id.ll_device_icon_and_name_view);
        ImageView ivDeviceIcon = rlWindowView.findViewById(R.id.iv_device_icon);
        TextView tvDeviceName = rlWindowView.findViewById(R.id.tv_device_name);
        RelativeLayout rlDeviceNameTouchBar = rlWindowView.findViewById(R.id.rl_device_name_touch_bar);
        TextView tvDeviceNameTouchBarDeviceName = rlWindowView.findViewById(R.id.tv_device_name_touch_bar_device_name);
        RelativeLayout rlScreenLock = rlWindowView.findViewById(R.id.rl_screen_lock);*/


        softWindow.setFlVideoParentView(flVideoParentView);
        //ivWindowModeIcon.setOnClickListener(this);
        //softWindow.setIvWindowModeIcon(ivWindowModeIcon);
        softWindow.setRlFocusBg(rlFocusBg);
//        softWindow.setRlWindowModeGroup(rlWindowMode);
//        softWindow.setRlDfModeGroup(rlDfMode);
//        softWindow.setRlFullScreenModeGroup(rlFullScreenMode);
//        softWindow.setIvDfModeIcon(ivDfModeIcon);
//        softWindow.setIvDfMode(ivDfMode);
//        softWindow.setTvToolBarDeviceName(tvToolBarDevicesName);
//        softWindow.setLlDeviceNameAndIconView(llDeviceIconAndNameView);
//        softWindow.setIvDeviceTypeIcon(ivDeviceIcon);
//        softWindow.setTvDeviceName(tvDeviceName);
//        softWindow.setRlDeviceNameTouchBar(rlDeviceNameTouchBar);
//        softWindow.setTvDeviceNameTouchBarDeviceName(tvDeviceNameTouchBarDeviceName);
        softWindow.setSelected(true);
        if (windowList != null && windowList.size() >= 1) {
            for (Window window : windowList) {
                window.setSelected(false);
            }
        }

//        ivFullScreenMode.setOnClickListener(this);
//        ivDfMode.setOnClickListener(this);
//        ivDfModeIcon.setOnClickListener(this);
//        fullScreenModeIcon.setOnClickListener(this);
//        lockScreenIcon.setOnClickListener(this);
//        screenShot.setOnClickListener(this);
        ivClose.setOnClickListener(this);
//        ivScale.setOnTouchListener(this);
//        toolBar.setOnTouchListener(this);
//        rlDeviceNameTouchBar.setOnTouchListener(this);
//        windowModeIcon.setOnClickListener(this);
        softWindow.setFloatWindowView(rlWindowView);
//        softWindow.setCardView(cardView);
        softWindow.setPlayerView(playerView);
//        softWindow.setToolBar(toolBar);
//        softWindow.setScaleView(ivScale);
//        softWindow.setIvScreenShot(screenShot);
        softWindow.setIvClose(ivClose);
//        softWindow.setCardViewBg(cardBg);
//        softWindow.setWindowStatusIconGroup(windowStatusIconGroup);
//        softWindow.setDfModeViewGroup(dfModeViewGroup);
//        softWindow.setIvFullScreenModeIcon(fullScreenModeIcon);
//        softWindow.setLockScreenIcon(lockScreenIcon);
//        softWindow.setIvFullScreenMode(ivFullScreenMode);
//        softWindow.setRlScreenLock(rlScreenLock);
        //softWindow.setWindowStatus(Config.WINDOW_MODE);
        softWindow.setWindowStatus(Config.FULL_SCREEN_MODE);
        softWindow.setShowing(false);

        //1，确认设备型号  2，确认目前投屏窗口数量
        TaskModel taskModel = softWindow.getTaskModel();
        int deviceType = taskModel.deviceType;
        if (deviceType == 0) {
            if (taskModel.targetName.startsWith("IPHONE")) {//设置设备类型为iOS设备
                softWindow.setType(Config.TYPE_IOS_PHONE);
            } else if (taskModel.targetName.startsWith("ANDROID")) {
                softWindow.setType(Config.TYPE_ANDROID_PHONE);
                softWindow.setOri(0);
            } else {
                softWindow.setType(Config.TYPE_PC);
                softWindow.setOri(-1);
            }
        } else {
            softWindow.setType(deviceType);
            if (deviceType == Config.TYPE_PC) {
                softWindow.setOri(-1);
            } else {
                softWindow.setOri(0);
            }
        }

        String deviceName = taskModel.deviceName;
        if (TextUtils.isEmpty(deviceName)) {
            deviceName = WindowResUtil.deviceNameFilter(softWindow.getTaskModel().targetName);
            if (TextUtils.isEmpty(deviceName)) {
                deviceName = App.getInstance().getApplicationContext().getResources().getString(WindowResUtil.getDeviceDefaultName(softWindow.getType()));
            }
        }
//        tvToolBarDevicesName.setText(deviceName);
//        tvDeviceName.setText(deviceName);
//        tvDeviceNameTouchBarDeviceName.setText(deviceName);
        softWindow.setDeviceName(deviceName);


//        ivDeviceIcon.setImageResource(WindowResUtil.getWindowTypeIcon(softWindow.getType()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            playerView.init(taskModel, new DecoderCallback() {
                @Override
                public void decoderCallback(int code) {
                    //解码器启动回调
                    //code:1:硬解码成功  2:硬解码失败，软解码成功   -1:解码失败
                    LogUtil.e("decoderCallback: code = " + code);
                    if (code == -1) {//解码失败
                        ToastUtils.getInstance().showToast(ToastUtils.ToastType.ERROR, softWindow.getTaskModel().targetName + "解码失败");
    //                    LogUtil.e("zjx", "softWindow is " + softWindow);
                        if (softWindow == null) return;
                        MsgUtil.sendCmd17Msg(softWindow.getTaskModel().dev_id, softWindow.getTaskModel().taskId, 2);
                        String windowIp = softWindow.getIp();
    //                    LogUtil.e("zjx", "windowIp is " + windowIp);
                        if (windowIp == null) return;
                        SoftWindow softWindowFromPreList = WindowFinder.getInstance().getSoftWindowFromPreList(windowIp);
                        if (softWindowFromPreList != null) {
                            RelativeLayout floatWindowView = softWindowFromPreList.getFloatWindowView();
                            manager.removeViewImmediate(floatWindowView);
                            removeWindowFromList(softWindow);
                        }

                        SoftWindow softWindowByTaskIp = WindowFinder.getInstance().getSoftWindowByDevId(softWindow.getDev_id());
                        if (softWindowByTaskIp != null) {
                            if (windowList.contains(softWindowByTaskIp))
                                windowList.remove(softWindowByTaskIp);
                        }
                    }
                }

                @Override
                public void decoderSizeChage(int width, int height, MediaFormat format) {
                    //解码分辨率变化   ori 设备类型
    //                LogUtil.e("decoderSizeChage width = " + width + "    height = " + height);
                    if (currentMode == Config.WINDOW_MODE) {
                        ViewGroup.LayoutParams layoutParams = softWindow.getFlVideoParentView().getLayoutParams();
                        if ((layoutParams.width > layoutParams.height) && (width > height) ||
                                (layoutParams.height > layoutParams.width) && (height > width) && softWindow.isShowing()) {
                            return;
                        }
    //                        LogUtil.e("decoderSizeChage: layoutParams.height = " + layoutParams.height + "    height = " + height);
    //                    if (Math.abs((width - layoutParams.width)) < 10) {
    //                        LogUtil.e("decoderSizeChage return");
    //                        return;
    //                    }
                    }
                    //TODO 如何区分这是缩放还是模式切换还是屏幕旋转导致的分辨率变化 cmd 19新增type字段是否可行？
                    //如果是横竖屏切换
                    mainHandler.post(() -> {
                        setSoftWindowParams(softWindow, width, height);
                        onDeskSizeChange(softWindow.getDev_id(), width, height);
                    });
                }
            });
        }
        playerView.setScaleX(1.00001f);
        //preview params
        WindowManager.LayoutParams perParams = new WindowManager.LayoutParams();
        perParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG;
        perParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        int tempFlags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        perParams.flags = tempFlags;
        perParams.format = PixelFormat.TRANSLUCENT;
        perParams.width = 50;
        perParams.height = 50;
        perParams.x = 0;
        perParams.y = 0;
        perParams.gravity = Gravity.LEFT | Gravity.TOP;

        manager.addView(rlWindowView, perParams);
        preSoftWindowList.add(softWindow);
    }

    private void setSoftWindowParams(SoftWindow softWindow, int width, int height) {
        LogUtil.d(TAG, "setSoftWindowParams width:"+width+" height:"+height+" softWindow.getType():"+softWindow.getType());
        if (!softWindow.isShowing()) {
            WindowManager.LayoutParams windowParams = new WindowManager.LayoutParams();
            windowParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_DIALOG;
            windowParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;

            int flags = WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            windowParams.flags = flags;
            windowParams.format = PixelFormat.TRANSLUCENT;
            //确定设备宽高比
            float screenRatio = (float) ((height * 1.0) / (width * 1.0));
            if (softWindow.getType() != Config.TYPE_PC) {
                if (height > width) {
                    //现在是竖屏
                    softWindow.setOri(0);
                } else {
                    //横屏过来的
                    screenRatio = (float) ((width * 1.0) / (height * 1.0));
                    softWindow.setOri(1);
                }
            } else {
                screenRatio = (float) ((height * 1.0) / (width * 1.0));
            }

            Window.WindowLocation windowLocation = WindowLocationUtil.getWindowLocation(windowList.size() + 1, softWindow.getType(),
                    windowList.size(), softWindow.getOri(), screenRatio);
            int windowMargin = WindowLocationUtil.getWindowMargin(softWindow.getType(), softWindow.getOri(), windowLocation.getWindowWidth());
            //CardView cardView = softWindow.getCardView();
            TouchFrameLayout flVideoParentView = softWindow.getFlVideoParentView();
            ViewGroup.LayoutParams flVideoParentViewParams = flVideoParentView.getLayoutParams();
            int windowRadius = WindowLocationUtil.getWindowRadius(softWindow.getType(), 0, flVideoParentViewParams.height);
            //cardView.setRadius(windowRadius);
            /*flVideoParentViewParams.width = windowLocation.getCardViewWidth();
            flVideoParentViewParams.height = windowLocation.getCardViewHeight();*/
            flVideoParentView.setLayoutParams(flVideoParentViewParams);

//            FrameLayout.LayoutParams cardViewLayoutParams = (FrameLayout.LayoutParams) cardView.getLayoutParams();
//            cardViewLayoutParams.setMargins(windowMargin, windowMargin, windowMargin, windowMargin);

            windowParams.width = windowLocation.getWindowWidth();
            windowParams.height = windowLocation.getWindowHeight();
            windowParams.x = windowLocation.getX();
            windowParams.y = windowLocation.getY();
            windowParams.gravity = Gravity.LEFT | Gravity.TOP;
            softWindow.setScreenRatio(screenRatio);
            softWindow.setParams(windowParams);
            softWindow.setWindowLocation(windowLocation);
        }
    }



    /**
     * 重新摆放视频窗口的位置
     */
    private void onWindowAdd() {
        if (windowList == null || windowList.size() == 0) {
            return;
        }
        Log.d(TAG, "重新摆放视频窗口的位置 当前投屏有：" + windowList.size());
        if (windowList.size() == 1 && !SystemHelper.isWhiteBoardForeground(App.getInstance().getApplicationContext())) {
            //白板没在前台，切当前只有一个投屏，全屏。
            currentMode = Config.DF_MODE;
            fullScreen(windowList.get(0));
            sendCdm19Msg(true);
            return;
        }
//         //最后一路 强占全屏
//        if (windowList.size() == 1 && !SystemHelper.isWhiteBoardForeground(App.getInstance())) {
//            //白板没在前台，切当前只有一个投屏，全屏。
//            currentMode = Config.DF_MODE;
//            fullScreen(windowList.get(0));
//            sendCdm19Msg(true);
//            return;
//        }
//        if (windowList.size() == 2 && !SystemHelper.isWhiteBoardForeground(App.getInstance())) {
//            //白板没在前台，切当前只有一个投屏，全屏。
//            currentMode = Config.DF_MODE;
//            fullScreen(windowList.get(1));
//            sendCdm19Msg(true);
//            return;
//        }
//        if (windowList.size() == 3 && !SystemHelper.isWhiteBoardForeground(App.getInstance())) {
//            //白板没在前台，切当前只有一个投屏，全屏。
//            currentMode = Config.DF_MODE;
//            fullScreen(windowList.get(2));
//            sendCdm19Msg(true);
//            return;
//        }
//
//        if (windowList.size() == 4 && !SystemHelper.isWhiteBoardForeground(App.getInstance())) {
//            //白板没在前台，切当前只有一个投屏，全屏。
//            currentMode = Config.DF_MODE;
//            fullScreen(windowList.get(3));
//            sendCdm19Msg(true);
//            return;
//        }

        LogUtil.d("zjx", "currentMode is " + currentMode);

        if (currentMode == Config.WINDOW_MODE) {
            if (windowList.size() > 1) {//大于一路视频，等分模式开启
                Log.d(TAG, "WINDOW_MODE > 1");
                for (Window window : windowList) {
                    ImageView dfModeIcon = window.getIvDfMode();
                    dfModeIcon.setVisibility(View.VISIBLE);
                }
            } else {
                Log.d(TAG, "WINDOW_MODE else");
                for (Window window : windowList) {
                    ImageView dfModeIcon = window.getIvDfMode();
                    dfModeIcon.setVisibility(View.GONE);
                }
            }
            windowMode();
        } else if (currentMode == Config.DF_MODE) {
            if (windowList.size() == 1) {//只有一路视频，窗口模式
                Log.d(TAG, "DF_MODE > 1");
                windowMode();
            } else {
                Log.d(TAG, "DF_MODE > else");
                dfMode();
            }

        } else if (currentMode == Config.FULL_SCREEN_MODE) {
            if (lastMode == Config.WINDOW_MODE || (windowList.size() == 1)) {
                Log.d(TAG, "FULL_SCREEN_MODE > 1");
                windowMode();
            } else {
                Log.d(TAG, "FULL_SCREEN_MODE else 1");
                dfMode();
            }
        }
        sendCdm19Msg(true);
    }

    private void onWindowRemove() {
        Log.d(TAG, "onWindowRemove 当前投屏有：" + windowList.size());
        synchronized (mLock) {
            //updateFrameRate(windowList.size(), Constants.MD5080_REMOVE,"");

            if (windowList == null || windowList.size() == 0){
                BroadCastUtils.sendProjectionStatus(App.getInstance().getApplicationContext(), BroadCastUtils.PROJECTION_STOP);
                return;
            }


            if (currentMode == Config.WINDOW_MODE) {
                if (windowList.size() > 1) {//大于一路视频，等分模式开启
                    for (Window window : windowList) {
                        ImageView dfModeIcon = window.getIvDfMode();
                        dfModeIcon.setVisibility(View.VISIBLE);
                    }
                } else {
                    for (Window window : windowList) {
                        ImageView dfModeIcon = window.getIvDfMode();
                        dfModeIcon.setVisibility(View.GONE);
                    }
//                    checkSelectedWindow();
//                    setWindowToolBar();
                }
                checkSelectedWindow();
                setWindowToolBar();
            } else if (currentMode == Config.DF_MODE) {
                if (windowList.size() == 1) {
                    fullScreen(windowList.get(0));
                }
//                else if (windowList.size() == 2) {
//                    fullScreen(windowList.get(1));
//                }
                else {
                    dfMode();
                }
            } else {//当前是全屏模式
                //1，确定退出的窗口是不是全屏的   2，退出的是其他的窗口
                boolean exitFullScreenWindow = true;
                for (Window w : windowList) {
                    int windowStatus = w.getWindowStatus();
                    if (windowStatus == Config.FULL_SCREEN_MODE) {
                        exitFullScreenWindow = false;
                        break;
                    }
                }
                if (exitFullScreenWindow) {
                    //退出的是全屏的窗口
                    if (lastMode == Config.DF_MODE) {
                        if (windowList.size() == 1) {
                            fullScreen(windowList.get(0));
                        } else if (windowList.size() > 1) {
                            dfMode();
                        }
                    } else if (lastMode == Config.WINDOW_MODE) {
                        windowMode();
                    } else {
                        fullScreen(windowList.get(0));
                    }
                } else {
                    if (windowList.size() == 1) {
                        Window window = windowList.get(0);
                        window.getDfModeViewGroup().setVisibility(View.GONE);
                    }
                }
            }
            sendCdm19Msg(true);

//            MsgUtil.sendGroupCmd19Msg(currentMode, windowList);
        }
    }
	
    /**
     * 是否允许添加一个投屏窗口
     *
     * @return
     */
    public boolean canInsertWindow() {
        //todo  NullPointerException 高级激活相关处理
        if (windowList.size() >= 1) {
            ToastUtils.getInstance().showToast(ToastUtils.ToastType.WARNING, App.getInstance().getApplicationContext()
            .getString(R.string.over_max_projection_count));
            return false;
        }
        //TODO  test 空指针异常，这里手动写死，后期修改
        if (windowList.size() >= 4) {
            ToastUtils.getInstance().showToast(ToastUtils.ToastType.WARNING,
                    App.getInstance().getApplicationContext().getString(R.string.over_max_projection_count));
            return false;
        }
        return true;
    }


    /**
     * 移除一个窗口
     * 1：区分投屏类型：W20 or 软投屏
     * 2：移除对应窗口
     */
    public void removeWindow(String ip, boolean fromPc) {
        Window window = findWindowsByIp(ip);
        if (window != null) {
            removeWindow(window, fromPc);
        }
    }


    /**
     * 移除一个窗口
     * 1：区分投屏类型：W20 or 软投屏
     * 2：移除对应窗口
     */
    public void removeWindow(Window window, boolean fromPc) {
        LogUtil.d(TAG,"removeWindow:ip:" +window.getIp());
        if (window == null) return;
        if (windowList == null || windowList.size() == 0) {
            LogUtil.d(TAG,"removeWindow:windowlist == 0:");
            BroadCastUtils.sendProjectionStatus(App.getInstance().getApplicationContext(), BroadCastUtils.PROJECTION_STOP);
            return;
        }
        mainHandler.post(() -> {

             if (window instanceof SoftWindow) {
                LogUtil.e("removeWindow SoftWindow task = " + ((SoftWindow) window).getTaskModel().taskId);
                SoftWindow softWindow = (SoftWindow) window;
                TaskModel taskModel = softWindow.getTaskModel();
                removeSoftWindow(taskModel, fromPc);
            }

            onWindowRemove();
        });
    }

    /**
     * 移除一个软投屏窗口
     *
     * @param taskModel
     */
    private void removeSoftWindow(TaskModel taskModel, boolean fromPc) {
        if (taskModel == null) {
            return;
        }
        if (windowList.size() == 0) {
            return;
        }
        SoftWindow softWindow = WindowFinder.getInstance().getSoftWindowByTaskId(taskModel.taskId);
        if (softWindow == null) {
            return;
        }
        RelativeLayout windView = softWindow.getFloatWindowView();
        PlayerView playerView = softWindow.getPlayerView();
        if (!fromPc) {
            EventBus.getDefault().post(new DestroyTaskEvent(taskModel.taskId));
        }
        if (playerView != null) {
            playerView.onDestroy();
        }
        manager.removeViewImmediate(windView);
        removeWindowFromList(softWindow);
    }


    /**
     * 移除所有窗口
     */
    public void removeAllWindow() {
        if (windowList == null || windowList.size() == 0) {
            return;
        }
        List<Window> tempList = windowList;

        for (Window window : tempList) {
            if (window instanceof SoftWindow) {
                SoftWindow softWindow = (SoftWindow) window;
                removeSoftWindow(softWindow.getTaskModel(), false);
            }
        }
    }

    /**
     * 同步白板窗口缩放
     */
    void onWindowScale() {
        //todo 同步白板缩放操作
    }

    /**
     * 同步白板窗口移动
     */
    void onWindowMove() {
        //TODO 同步白板移动操作
    }


    public Window findWindowsByIp(String ip) {
        if (StringUtil.isNotEmpty(ip) && windowList != null && windowList.size() > 0) {
            for (Window window : windowList) {
                if (ip.equals(window.getIp())) {
                    return window;
                }
            }
        }
        return null;

    }

    public List<SoftWindow> getPerWindowList() {
        return preSoftWindowList;
    }

    public int getWindowCount() {
        if (windowList == null) {
            return -1;
        }
        return windowList.size();
    }

    public List<Window> getWindowList() {
        return windowList;
    }


    private void removeWindowFromList(Window window) {
        LogUtil.d(TAG, "removeWindowFromList window:"+window.getIp());
        if (window == null) {
            return;
        }
        if (windowList != null || windowList.size() > 0) {
            if (windowList.contains(window)) {
                LogUtil.d(TAG, "removeWindowFromList 删除前 windowList大小:"+windowList.size());
                LogUtil.d(TAG, "removeWindowFromList 删除 window:"+ StringUtil.identityToString(window));
                windowList.remove(window);

                LogUtil.d(TAG, "removeWindowFromList 删除和 windowList大小:"+windowList.size());
            }
        }
        if (preSoftWindowList != null || preSoftWindowList.size() > 0) {
            if (preSoftWindowList.contains(window)) {
                preSoftWindowList.remove(window);
            }
        }
    }

    //去重
    private void removeRepeatedWindowByDevId(String dev_id) {
        if (TextUtils.isEmpty(dev_id)) {
            return;
        }
//        LogUtil.d("ShanLian222", "new dev_id = " + dev_id);
        if (windowList.size() == 0) {
            return;
        }
        if (windowList != null && windowList.size() > 0) {
            Window w = null;
            for (Window window : windowList) {
                if (window instanceof SoftWindow) {
                    String dev_id1 = ((SoftWindow) window).getDev_id();
                    if (TextUtils.equals(dev_id1, dev_id)) {
                        LogUtil.d("zjx", "removeRepeatedWindowByDevId: dev_id = " + dev_id);
                        w = window;
                        break;
                    }
                }
            }
            if (w != null) {
                windowList.remove(w);
            }
        }

        if (preSoftWindowList != null && preSoftWindowList.size() > 0) {
            List<Window> tempList = new ArrayList<>();
            for (SoftWindow softWindow : preSoftWindowList) {
                String dev_id1 = softWindow.getDev_id();
                if (TextUtils.equals(dev_id1, dev_id)) {
//                    LogUtil.d("ShanLian222", "removePreWindowByDevId: dev_id = " + dev_id);
//                Log.d(TAG, "removeRepeatedWindowByDevId: per dev_id = " + dev_id);
                    tempList.add(softWindow);
                    break;
                }
            }
            if (tempList.size() > 0) {
                for (Window tempWindow : tempList) {
                    preSoftWindowList.remove(tempWindow);
                }
            }
        }
    }


    /**
     * 屏幕旋转通知
     * 投屏源 屏幕旋转事件
     * @param dev_id
     * @param width
     * @param height
     */
    public void onDeskSizeChange(String dev_id, int width, int height) {
        LogUtil.e("onDeskSizeChange: dev_id = " + dev_id);
//        LogUtil.e("zjx", "onDeskSizeChange: dev_id = " + dev_id);
        SoftWindow softWindow = WindowFinder.getInstance().getSoftWindowFromPreList(dev_id);
        //预加载投屏集合
        if (softWindow == null) {//如果预加载的集合取到的是空，从windList去取。
            softWindow = WindowFinder.getInstance().getSoftWindowByDevId(dev_id);
        }
        if (softWindow == null) {
            return;
        }
        boolean showing = softWindow.isShowing();
        //横屏过来的
        if (!showing) {
            try {
                WindowManager.LayoutParams windowParams = softWindow.getParams();
                if (windowParams == null) {
                    setSoftWindowParams(softWindow, width, height);
                }
                manager.updateViewLayout(softWindow.getFloatWindowView(), softWindow.getParams());
                if (!canInsertWindow()) {
                    manager.removeViewImmediate(softWindow.getFloatWindowView());
                    if (preSoftWindowList != null && preSoftWindowList.contains(softWindow)) {
                        preSoftWindowList.remove(softWindow);
                    }
                    return;
                }
                softWindow.setShowing(true);
                // 判断窗口是否重复添加
                if (!windowList.contains(softWindow)) {
                    windowList.add(softWindow);
                    setWindowSelected(softWindow);
                    preSoftWindowList.remove(softWindow);
                } else {
                    return;
                }

                onWindowAdd();
            } catch (NullPointerException e) {
                LogUtil.e("onDeskSizeChange: windowParams is null");
            }

        } else {
            LogUtil.e("onDeskSizeChange rotateScreen ResolutionWidth = " + width + "    ResolutionHeight = " + height);
            softWindow.setResolutionWidth(width);
            softWindow.setResolutionHeight(height);
            rotateScreen(softWindow);
        }
    }

    /**
     * 屏幕旋转,UI调整
     *  手机画面旋转事件 竖屏0 横屏 1
     * @param ori
     */
    public void onScreenOri(String dev_id, int ori) {
        LogUtil.d(TAG, "onScreenOri: dev_id = " + dev_id + "   ori = " + ori);
        SoftWindow softWindow = WindowFinder.getInstance().getSoftWindowByDevId(dev_id);
        if (softWindow != null) {
            softWindow.setOri(ori);
        }
    }

    private void rotateScreen(SoftWindow softWindow) {
        synchronized (TAG) {//ori: 0 home键在下方， 1 home键在右侧， 2 home键在上方 3 home键在左侧
            if (softWindow == null) {
                return;
            }
//            LogUtil.e("zjx", "屏幕旋转 ， type is " + softWindow.getType());
            if (softWindow.getType() == Config.TYPE_PC) {
                return;
            }
            int ori = softWindow.getOri();
            int resolutionWidth = softWindow.getResolutionWidth();
            int resolutionHeight = softWindow.getResolutionHeight();
            if (resolutionWidth > resolutionHeight) {//方向校正
                if (ori == 0 || ori == 2) {
                    ori = 1;
                }
            } else {
                if (ori == 1 || ori == 3) {
                    ori = 0;
                }
            }
            if (softWindow.getWindowStatus() == Config.FULL_SCREEN_MODE) {
                //全屏模式下旋转
                TouchFrameLayout flVideoParentView = softWindow.getFlVideoParentView();
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) flVideoParentView.getLayoutParams();
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                if (ori == 0 || ori == 2) {
                    //手机当前竖屏状态
                    layoutParams.height = Config.SCREEN_HEIGHT;
                    layoutParams.width = (int) (Config.SCREEN_HEIGHT / softWindow.getScreenRatio());

                } else {
                    //手机当前横屏状态
                    layoutParams.width = Config.SCREEN_WIDTH;
                    layoutParams.height = (int) (Config.SCREEN_WIDTH / softWindow.getScreenRatio());

                    if (layoutParams.height >= Config.SCREEN_HEIGHT) {
                        layoutParams.height = Config.SCREEN_HEIGHT;
                        layoutParams.width = (int) (Config.SCREEN_HEIGHT * softWindow.getScreenRatio());
                    }
                }

                Window.WindowLocation windowLocation = softWindow.getWindowLocation();
                //int cardViewWidth = windowLocation.getCardViewWidth();
                //int cardViewHeight = windowLocation.getCardViewHeight();
                //windowLocation.setCardViewWidth(cardViewHeight);
                //windowLocation.setCardViewHeight(cardViewWidth);
                windowLocation.setWindowWidth(layoutParams.height);
                windowLocation.setWindowHeight(layoutParams.width + Config.WINDOW_TOOL_BAR_HEIGHT);
                softWindow.setWindowLocation(windowLocation);

                flVideoParentView.setLayoutParams(layoutParams);

            } /*else if (softWindow.getWindowStatus() == Config.DF_MODE) {

                RelativeLayout floatWindowView = softWindow.getFloatWindowView();
                TouchFrameLayout flVideoParentView = softWindow.getFlVideoParentView();
                ViewGroup.LayoutParams windowViewLayoutParams = floatWindowView.getLayoutParams();
                RelativeLayout.LayoutParams flVideoParentViewLayoutParams = (RelativeLayout.LayoutParams) flVideoParentView.getLayoutParams();

                if (ori == 0 || ori == 2) {
                    //竖屏
                    flVideoParentViewLayoutParams.height = windowViewLayoutParams.height;
                    flVideoParentViewLayoutParams.width = (int) (flVideoParentViewLayoutParams.height / softWindow.getScreenRatio());
                } else {
                    //横屏
                    flVideoParentViewLayoutParams.width = windowViewLayoutParams.width;
                    flVideoParentViewLayoutParams.height = (int) (flVideoParentViewLayoutParams.width / softWindow.getScreenRatio());

                    if (flVideoParentViewLayoutParams.height >= windowViewLayoutParams.height) {
                        flVideoParentViewLayoutParams.height = windowViewLayoutParams.height;
                        flVideoParentViewLayoutParams.width = (int) (flVideoParentViewLayoutParams.height * softWindow.getScreenRatio());
                    }
                }
                flVideoParentView.setLayoutParams(flVideoParentViewLayoutParams);
            } */else {//windowMode旋转
                //TODO 动态计算x,y值
                RelativeLayout windowView = softWindow.getFloatWindowView();
                WindowManager.LayoutParams windowViewLayoutParams = softWindow.getParams();
                //CardView cardView = softWindow.getCardView();

                //FrameLayout.LayoutParams cardViewLayoutParams = (FrameLayout.LayoutParams) cardView.getLayoutParams();
                TouchFrameLayout flVideoParentView = softWindow.getFlVideoParentView();
                ViewGroup.LayoutParams layoutParams = flVideoParentView.getLayoutParams();
                int width = layoutParams.width;
                int height = layoutParams.height;

                int x = width / 2 + windowViewLayoutParams.x;
                int y = height / 2 + windowViewLayoutParams.y;

                layoutParams.width = height;
                layoutParams.height = width;


                if (ori == 0 || ori == 2) {
                    //竖屏限制最大窗口
                    if (layoutParams.width > Config.PHONE_WINDOW_VERTICAL_MAX_WIDTH) {
                        layoutParams.width = Config.PHONE_WINDOW_VERTICAL_MAX_WIDTH;
                        layoutParams.height = (int) (layoutParams.width * softWindow.getScreenRatio());
                    }
                } else {
                    //横屏限制最大窗口
                    if (layoutParams.width > Config.PHONE_WINDOW_HOR_MAX_WIDTH) {
                        layoutParams.width = Config.PHONE_WINDOW_HOR_MAX_WIDTH;
                        layoutParams.height = (int) (layoutParams.width / softWindow.getScreenRatio());
                    }
                }

                if (ori == 0 || ori == 2) {
                    //手机当前竖屏状态
                    windowViewLayoutParams.height = layoutParams.height + (Config.is4K ? 88 : 44);
                    windowViewLayoutParams.width = layoutParams.width + ((Config.is4K ? 32 : 18));
                } else {
                    //手机当前横屏状态
                    windowViewLayoutParams.width = layoutParams.width;
                    windowViewLayoutParams.height = layoutParams.height + (Config.is4K ? 88 : 44);
                }

                int newX = x - layoutParams.width / 2;
                int newY = y - layoutParams.height / 2;
                windowViewLayoutParams.x = newX;
                windowViewLayoutParams.y = newY;

                LogUtil.e("layoutParams.width = " + layoutParams.width + "      layoutParams.height = " + layoutParams.height);
                manager.updateViewLayout(windowView, windowViewLayoutParams);
                //ios设备做方向校验？

                int windowRadius = WindowLocationUtil.getWindowRadius(softWindow.getType(), ori, layoutParams.height);
                int windowMargin = WindowLocationUtil.getWindowMargin(softWindow.getType(), ori, layoutParams.width);
//                cardView.setRadius(windowRadius);
//                LogUtil.e("ori = " + ori + "      windowMargin = " + windowMargin);
//                cardViewLayoutParams.setMargins(windowMargin, windowMargin, windowMargin, windowMargin);
//                if (softWindow.getType() == Config.TYPE_IOS_PHONE && (softWindow.getOri() == 0 || softWindow.getOri() == 2)) {
//                    cardViewLayoutParams.setMargins(windowMargin, windowMargin - 10, windowMargin, windowMargin - 10);
//                }
//                cardView.setLayoutParams(cardViewLayoutParams);
                flVideoParentView.setLayoutParams(layoutParams);
                //RelativeLayout cardViewBg = softWindow.getCardViewBg();
                int windowBg = WindowResUtil.getWindowBg(softWindow.getType(), ori);
                //cardViewBg.setBackground(App.getInstance().getApplicationContext().getResources().getDrawable(windowBg));
            }
            softWindow.setDeskSizeChange(null);
        }
    }

    private void sendCdm19Msg(boolean windowCountChange) {
        MsgUtil.sendGroupCmd19Msg(currentMode, windowList);
    }

    /**
     * 当屏幕旋转
     *
     * @param softWindow
     */
    private void onPhoneWindowOriChange(SoftWindow softWindow) {

    }

    /**
     * 全屏
     */
    private void fullScreen(Window window) {
        //记住窗口当前位置状态
        removeDfModeBgView();
        if (window == null) return;
//        if (window instanceof SoftWindow) {
//            LogUtil.d(TAG, "fullScreen window dev id = " + ((SoftWindow) window).getDev_id());
//        }

        RelativeLayout cardViewBg = window.getCardViewBg();
        TouchFrameLayout flVideoParentView = window.getFlVideoParentView();
        RelativeLayout.LayoutParams flVideoParentViewLayoutParams = (RelativeLayout.LayoutParams) flVideoParentView.getLayoutParams();
        CardView cardView = window.getCardView();
        FrameLayout.LayoutParams cardViewLayoutParams = (FrameLayout.LayoutParams) cardView.getLayoutParams();
        lastMode = currentMode;
        if (currentMode == Config.WINDOW_MODE) {
            saveWindowLocation(window);
        }
        window.getWindowStatusIconGroup().setVisibility(View.VISIBLE);
        window.getRlWindowModeGroup().setVisibility(View.VISIBLE);
        window.getIvWindowModeIcon().setVisibility(View.VISIBLE);
        window.getRlFullScreenMode().setVisibility(View.GONE);
        window.getLlDeviceNameAndIconView().setVisibility(View.GONE);
        window.getRlDeviceNameTouchBar().setVisibility(View.GONE);
        window.getScaleView().setVisibility(View.GONE);

        if (windowList.size() == 1) {
            window.getRlDfModeGroup().setVisibility(View.GONE);
        } else {
            window.getRlDfModeGroup().setVisibility(View.VISIBLE);
        }

        RelativeLayout floatWindowView = window.getFloatWindowView();
        floatWindowView.setBackgroundColor(fullScreenWindowBgColor);

        ConstraintLayout toolBar = window.getToolBar();
        cardViewLayoutParams.setMargins(0, 0, 0, 0);
        cardView.setRadius(0);
        cardView.setLayoutParams(cardViewLayoutParams);
        cardViewBg.setBackground(null);
        toolBar.setVisibility(View.GONE);
        window.getRlScreenLock().setVisibility(View.VISIBLE);

        if (window.getType() != Config.TYPE_PC) {
            SoftWindow softWindow = (SoftWindow) window;
            //iOS设备需要做方向校正
            checkOri(window);
            int ori = softWindow.getOri();
            flVideoParentViewLayoutParams.removeRule(RelativeLayout.CENTER_HORIZONTAL);
            flVideoParentViewLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);

//
//            if (flVideoParentViewLayoutParams.width > flVideoParentViewLayoutParams.height) {
//                if (ori == 0 || ori == 2) {
//                    ori = 1;
//                }
//            } else {
//                if (ori == 1 || ori == 3) {
//                    ori = 0;
//                }
//            }

            if (ori == 0 || ori == 2) {
                //手机当前竖屏状态
                flVideoParentViewLayoutParams.height = Config.SCREEN_HEIGHT;
                flVideoParentViewLayoutParams.width = (int) (Config.SCREEN_HEIGHT / softWindow.getScreenRatio());
            } else {
                //手机当前横屏状态
                flVideoParentViewLayoutParams.width = Config.SCREEN_WIDTH;
                flVideoParentViewLayoutParams.height = (int) (Config.SCREEN_WIDTH / softWindow.getScreenRatio());
            }
            LogUtil.e(TAG, "full screen ori = " + ori);
        } else {
            flVideoParentViewLayoutParams.width = Config.SCREEN_WIDTH;
            flVideoParentViewLayoutParams.height = Config.SCREEN_HEIGHT;
        }

        floatWindowView.setLayoutParams(flVideoParentViewLayoutParams);

        WindowManager.LayoutParams windowParams = window.getParams();
        windowParams.width = Config.SCREEN_WIDTH;
        windowParams.height = Config.SCREEN_HEIGHT;
        windowParams.x = 0;
        windowParams.y = 0;
        windowParams.gravity = Gravity.LEFT | Gravity.TOP;

        if (windowList.size() > 1) {
            for (Window w : windowList) {
                if (window != w) {
                    WindowManager.LayoutParams params = w.getParams();
                    params.width = 0;
                    params.height = 0;
                    manager.updateViewLayout(w.getFloatWindowView(), params);
                } else {
                    manager.updateViewLayout(floatWindowView, windowParams);
                }
            }
        } else {
            manager.updateViewLayout(floatWindowView, windowParams);
        }
        for (Window w : windowList) {
            if (w == window) {
                w.setWindowStatus(Config.FULL_SCREEN_MODE);
            } else {
                w.setWindowStatus(Config.FULL_SCREEN_SMALL_WINDOW_MODE);
            }
        }

        currentMode = Config.FULL_SCREEN_MODE;
        if (sourceChange) {
            MsgUtil.sendSourceChangeGroupCmd19Msg(currentMode, windowList);
            sourceChange = false;
        } else {
            sendCdm19Msg(true);
//            MsgUtil.sendGroupCmd19Msg(currentMode, windowList);
        }
    }

    private void saveWindowLocation(Window window) {
        WindowManager.LayoutParams windowParams = window.getParams();
        TouchFrameLayout flVideoParentView = window.getFlVideoParentView();
        ViewGroup.LayoutParams layoutParams = flVideoParentView.getLayoutParams();
        int cWidth = layoutParams.width;
        int cHeight = layoutParams.height;
        Window.WindowLocation location = new Window.WindowLocation();
        location.setWindowWidth(windowParams.width);
        location.setWindowHeight(windowParams.height);
        location.setGravity(windowParams.gravity);
        location.setX(windowParams.x);
        location.setY(windowParams.y);
        location.setCardViewWidth(cWidth);
        location.setCardViewHeight(cHeight);
        window.setWindowLocation(location);
    }

    /**
     * 等分模式
     */
    private void dfMode() {
        if (windowList == null || windowList.size() == 0) {
            return;
        }
        //1，确定窗口数量   2，确定各个位置窗口类型   3，摆放窗口
        //  临时规避 在一路投屏的成等分模式， 从而出现黑色背景，窗口下方工具条消失
        if (windowList.size() == 1) {
            ToastUtils.getInstance().showToast(ToastUtils.ToastType.WARNING, "一路投屏无法等分模式显示");
            return;
        }
        lastMode = currentMode;
        for (int i = 0; i < windowList.size(); i++) {
            Window window = windowList.get(i);
            if (i == 0) {
                addDfModeBgView();
            }
            if (i == windowList.size() - 1) {
                window.getRlScreenLock().setVisibility(View.VISIBLE);
            } else {
                window.getRlScreenLock().setVisibility(View.GONE);
            }

            if (currentMode == Config.WINDOW_MODE) {
                saveWindowLocation(window);
            }

            checkOri(window);

            Window.WindowLocation dfModeLocation = WindowLocationUtil.getDfModeLocation(windowList.size(), i, window);
            if (dfModeLocation == null) continue;
            window.getLlDeviceNameAndIconView().setVisibility(View.VISIBLE);
            window.getToolBar().setVisibility(View.GONE);
            window.getRlDeviceNameTouchBar().setVisibility(View.GONE);
            window.getWindowStatusIconGroup().setVisibility(View.VISIBLE);
            window.getRlDfModeGroup().setVisibility(View.GONE);
            window.getRlWindowModeGroup().setVisibility(View.VISIBLE);
            window.getRlFullScreenMode().setVisibility(View.VISIBLE);
            window.getIvWindowModeIcon().setVisibility(View.VISIBLE);
            window.getFloatWindowView().setBackgroundColor(fullScreenWindowBgColor);
            window.getScaleView().setVisibility(View.GONE);


            TouchFrameLayout flVideoParentView = window.getFlVideoParentView();
            RelativeLayout.LayoutParams flVideoParentViewLayoutParams = (RelativeLayout.LayoutParams) flVideoParentView.getLayoutParams();
            RelativeLayout cardViewBg = window.getCardViewBg();
            CardView cardView = window.getCardView();
            FrameLayout.LayoutParams cardViewLayoutParams = (FrameLayout.LayoutParams) cardView.getLayoutParams();
            cardViewLayoutParams.setMargins(0, 0, 0, 0);
            cardView.setRadius(0);
            cardView.setLayoutParams(cardViewLayoutParams);
            cardViewBg.setBackground(null);


            flVideoParentViewLayoutParams.width = dfModeLocation.getCardViewWidth();
            flVideoParentViewLayoutParams.height = dfModeLocation.getCardViewHeight();

            flVideoParentViewLayoutParams.removeRule(RelativeLayout.CENTER_HORIZONTAL);
            flVideoParentViewLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            flVideoParentView.setLayoutParams(flVideoParentViewLayoutParams);

            WindowManager.LayoutParams windowParams = window.getParams();

            RelativeLayout floatWindowView = window.getFloatWindowView();
            windowParams.width = dfModeLocation.getWindowWidth();
            windowParams.height = dfModeLocation.getWindowHeight();
            windowParams.x = dfModeLocation.getX();
            windowParams.y = dfModeLocation.getY();
            manager.updateViewLayout(floatWindowView, windowParams);

            window.setWindowStatus(Config.DF_MODE);
        }
        currentMode = Config.DF_MODE;
        if (sourceChange) {
            MsgUtil.sendSourceChangeGroupCmd19Msg(currentMode, windowList);
            sourceChange = false;
        } else {
            sendCdm19Msg(true);
//            MsgUtil.sendGroupCmd19Msg(currentMode, windowList);
        }
    }

    /**
     * 添加等分模式黑色背景
     */
    RelativeLayout dfModeBgView;
    boolean dfModeViewAdded = false;

    private void addDfModeBgView() {
        if (dfModeViewAdded) return;
        if (manager == null) {
            manager = (WindowManager) App.getInstance().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        }
        if (dfModeBgView == null) {
            dfModeBgView = (RelativeLayout) LayoutInflater.from(App.getInstance().getApplicationContext()).inflate(R.layout.view_df_mode_bg, null);
        }

        WindowManager.LayoutParams dfModeBgParams = new WindowManager.LayoutParams();
        dfModeBgParams.type = WindowManager.LayoutParams.TYPE_PHONE;

        int tempFlags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

        dfModeBgParams.flags = tempFlags;
        dfModeBgParams.format = PixelFormat.TRANSLUCENT;
        dfModeBgParams.width = Config.SCREEN_WIDTH;
        dfModeBgParams.height = Config.SCREEN_HEIGHT;
        dfModeBgParams.x = 0;
        dfModeBgParams.y = 0;
        dfModeBgParams.gravity = Gravity.LEFT | Gravity.TOP;
        manager.addView(dfModeBgView, dfModeBgParams);
        dfModeViewAdded = true;
    }

    /**
     * 移除等分模式背景view
     */
    private void removeDfModeBgView() {
        if (dfModeBgView != null) {
         //   manager.removeViewImmediate(dfModeBgView);
            manager.removeView(dfModeBgView);
            dfModeBgView = null;
            dfModeViewAdded = false;
        }
    }

    /**
     * 窗口模式
     */
    private void windowMode() {
         removeDfModeBgView();
         LogUtil.e("zjx","进来 了窗口模式 ，windowlist size :"+windowList.size());
        if (windowList == null || windowList.size() == 0) return;

        //TODO 免费版不支持窗口模式 判断不支持窗口模式直接返回

        LogUtil.d(TAG, "windowMode: do windowMode");
        lastMode = currentMode;
        for (int i = 0; i < windowList.size(); i++) {
            removeDfModeBgView();
            checkSelectedWindow();
            Window window = windowList.get(i);
            //方向校验
            checkOri(window);

            Window.WindowLocation location = WindowLocationUtil.getWindowLocation(windowList.size(), window.getType(), i, window.getOri(), window.getScreenRatio());
            if (location == null) continue;
            RelativeLayout floatWindowView = window.getFloatWindowView();
            TouchFrameLayout flVideoParentView = window.getFlVideoParentView();
            RelativeLayout.LayoutParams flVideoParentViewParams = (RelativeLayout.LayoutParams) flVideoParentView.getLayoutParams();
            CardView cardView = window.getCardView();
            FrameLayout.LayoutParams cardViewLayoutParams = (FrameLayout.LayoutParams) cardView.getLayoutParams();
            RelativeLayout cardViewBg = window.getCardViewBg();
            LogUtil.e("zjx","cardViewBg is "+cardViewBg);
            flVideoParentViewParams.removeRule(RelativeLayout.CENTER_IN_PARENT);
            flVideoParentViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            flVideoParentViewParams.width = location.getCardViewWidth();
            flVideoParentViewParams.height = location.getCardViewHeight();
            int cardViewBgId = WindowResUtil.getWindowBg(window.getType(), window.getOri());
            LogUtil.e("zjx","cardViewBgId is "+cardViewBgId);
            int windowRadius = WindowLocationUtil.getWindowRadius(window.getType(), window.getOri(), flVideoParentViewParams.height);
            int windowMargin = WindowLocationUtil.getWindowMargin(window.getType(), window.getOri(), flVideoParentViewParams.width);
            cardViewLayoutParams.setMargins(windowMargin, windowMargin, windowMargin, windowMargin);
//            if (window.getType() == Config.TYPE_IOS_PHONE && (window.getOri() == 0 || window.getOri() == 2)) {
//                cardViewLayoutParams.setMargins(windowMargin, windowMargin - 10, windowMargin, windowMargin - 10);
//            }

            if (window.getType() == Config.TYPE_IOS_PHONE && (window.getOri() == 0 || window.getOri() == 2)) {
                cardViewLayoutParams.setMargins(windowMargin, windowMargin - 10, windowMargin, windowMargin - 10);
            }

            cardView.setRadius(windowRadius);
            cardView.setLayoutParams(cardViewLayoutParams);
            LogUtil.e("zjx","cardViewLayoutParams is "+cardViewLayoutParams);
            cardViewBg.setBackground(App.getInstance().getApplicationContext().getDrawable(cardViewBgId));


//            flVideoParentView.bringChildToFront(cardViewBg);

            LogUtil.e("window mode ori = " + window.getOri());
            window.getWindowStatusIconGroup().setVisibility(View.GONE);
            window.getFloatWindowView().setBackgroundColor(windowBgColor);
            WindowManager.LayoutParams windowParams = window.getParams();
            windowParams.width = location.getWindowWidth();
            windowParams.height = location.getWindowHeight();
            windowParams.x = location.getX();
            windowParams.y = location.getY();
            windowParams.gravity = Gravity.LEFT | Gravity.TOP;
            LogUtil.e("window windowParams.width = " + windowParams.width);
            LogUtil.e("window windowParams.height = " + windowParams.height);
            manager.updateViewLayout(floatWindowView, windowParams);

            window.getRlWindowModeGroup().setVisibility(View.VISIBLE);
            if (windowList.size() <= 1) {
                window.getIvDfMode().setVisibility(View.GONE);
                window.getRlDfModeGroup().setVisibility(View.GONE);
            } else {
                window.getIvDfMode().setVisibility(View.VISIBLE);
                window.getRlDfModeGroup().setVisibility(View.VISIBLE);
            }
            window.getRlFullScreenMode().setVisibility(View.GONE);
            window.setWindowStatus(Config.WINDOW_MODE);
            window.getIvWindowModeIcon().setVisibility(View.GONE);

            window.getIvFullScreenMode().setVisibility(View.VISIBLE);
            window.getLlDeviceNameAndIconView().setVisibility(View.GONE);

            if (windowList.size() == 1) {
                window.setSelected(true);
            }
            if (window.isSelected()) {
                window.getRlDeviceNameTouchBar().setVisibility(View.GONE);
                window.getToolBar().setVisibility(View.VISIBLE);
            } else {
                window.getRlDeviceNameTouchBar().setVisibility(View.VISIBLE);
                window.getToolBar().setVisibility(View.GONE);
            }

            if (window.getRlDeviceNameTouchBar().getVisibility() == View.GONE && window.getToolBar().getVisibility() == View.GONE) {
                window.getToolBar().setVisibility(View.VISIBLE);
            }
            window.getScaleView().setVisibility(View.VISIBLE);

            manager.updateViewLayout(floatWindowView, windowParams);
        }

        currentMode = Config.WINDOW_MODE;
        sendCdm19Msg(true);
//        MsgUtil.sendGroupCmd19Msg(currentMode, windowList);
    }

    private void checkOri(Window window) {
        if (window.getType() != Config.TYPE_PC) {
            TouchFrameLayout flVideoParentView = window.getFlVideoParentView();
            ViewGroup.LayoutParams layoutParams = flVideoParentView.getLayoutParams();
            int ori = window.getOri();

            if (layoutParams.width > layoutParams.height) {//方向校正
                if (ori == 0 || ori == 2) {
                    window.setOri(1);
                }
            } else {
                if (ori == 1 || ori == 3) {
                    window.setOri(0);
                }
            }
        }
    }


    float mDownX, mDownY, mMoveX, mMoveY;

    /**
     * 移动窗口
     *
     * @param window
     * @param event
     */
    private void dragWindow(Window window, MotionEvent event) {

        LogUtil.d("zjx", " dragWindow  ......");

        ScreenShotUtils.setIsDraged(true);
        switch (event.getAction()) {//窗口拖动
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getRawX();
                mDownY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                mMoveX = event.getRawX();
                mMoveY = event.getRawY();
                float difX = (mMoveX - mDownX);
                float difY = (mMoveY - mDownY);
//                if (difX < mSlop && difY < mSlop) return;
                WindowManager.LayoutParams windowParams = window.getParams();
                windowParams.x += difX;
                windowParams.y += difY;
                manager.updateViewLayout(window.getFloatWindowView(), windowParams);
                window.setParams(windowParams);
                mDownX = mMoveX;
                mDownY = mMoveY;
                break;
        }
    }

    private int startX, startY;
    private int mSlop = ViewConfiguration.getWindowTouchSlop();//最小滑动距离

    /**
     * 缩放窗口
     *
     * @param window
     * @param event
     */
    private void scaleWindow(Window window, MotionEvent event) {
        //1,是否是手机投屏？
        if (window instanceof SoftWindow && window.getType() != Config.TYPE_PC) {
            SoftWindow softWindow = (SoftWindow) window;
            scalePhoneWindow(softWindow, event);
        } else {
            scalePcWindow(window, event);
        }
    }

    /**
     * PC缩放
     *
     * @param window
     * @param event
     */
    private void scalePcWindow(Window window, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) event.getRawX();
                startY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = (int) (event.getRawX() - startX);
                int deltaY = (int) (event.getRawY() - startY);
                if (Math.abs(deltaX) < mSlop && Math.abs(deltaY) < mSlop) {
                    return;
                }
                if (Math.abs(deltaX) >= Config.SCREEN_WIDTH || Math.abs(deltaY) >= Config.SCREEN_HEIGHT) {
                    return;
                }
                deltaY = (int) (deltaX * Config.RAT);
                WindowManager.LayoutParams windowParams = window.getParams();
                TouchFrameLayout flVideoParentView = window.getFlVideoParentView();
                CardView cardView = window.getCardView();
                FrameLayout.LayoutParams cardViewLayoutParams = (FrameLayout.LayoutParams) cardView.getLayoutParams();
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) flVideoParentView.getLayoutParams();
                layoutParams.width = layoutParams.width + deltaX;
                layoutParams.height = layoutParams.height + deltaY;
                //限制最大
                if (layoutParams.width >= Config.PC_WINDOW_MAX_WIDTH) {
                    layoutParams.width = Config.PC_WINDOW_MAX_WIDTH;
                    layoutParams.height = (int) (layoutParams.width * Config.RAT);
                }

                if (layoutParams.width <= Config.PC_WINDOW_MIN_WIDTH) {
                    layoutParams.width = Config.PC_WINDOW_MIN_WIDTH;
                    layoutParams.height = (int) (layoutParams.width * Config.RAT);
                }

                windowParams.width = layoutParams.width;
                windowParams.height = layoutParams.height + Config.WINDOW_TOOL_BAR_HEIGHT;
                LogUtil.e("zjx","scalePcWindow windowParams.width = " + windowParams.width);
                LogUtil.e("zjx","scalePcWindow windowParams.height = " + windowParams.height);


                int windowMargin = WindowLocationUtil.getWindowMargin(window.getType(), window.getOri(), layoutParams.width);
                cardViewLayoutParams.setMargins(windowMargin, windowMargin, windowMargin, windowMargin);
                cardView.setLayoutParams(cardViewLayoutParams);
                manager.updateViewLayout(window.getFloatWindowView(), windowParams);

                startX = (int) event.getRawX();
                startY = (int) event.getRawY();


                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (window instanceof SoftWindow) {
//                    MsgUtil.sendGroupCmd19Msg(currentMode, windowList);
                    sendCdm19Msg(false);
                }

                break;
        }
    }

    /**
     * 投屏源 窗口缩放
     *
     * @param window
     * @param event
     */
    private void scalePhoneWindow(SoftWindow window, MotionEvent event) {
        //1，确定手机横竖屏   2，确定手机宽高比
        //ori: 0 home键在下方， 1 home键在右侧， 2 home键在上方 3 home键在左侧
        checkOri(window);
        float screenRatio = window.getScreenRatio();
        int ori = window.getOri();
        LogUtil.e("scalePhoneWindow device name = " + window.getTaskModel().deviceName);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = (int) event.getRawX();
                startY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaX = (int) (event.getRawX() - startX);
                int deltaY = (int) (event.getRawY() - startY);
                if (Math.abs(deltaX) < mSlop && Math.abs(deltaY) < mSlop) {
                    return;
                }
                if (Math.abs(deltaX) >= Config.SCREEN_WIDTH || Math.abs(deltaY) >= Config.SCREEN_HEIGHT) {
                    return;
                }
                //1,处理好投屏窗口的缩放比例，以X轴缩放为基准
                TouchFrameLayout flVideoParentView = window.getFlVideoParentView();
                ViewGroup.LayoutParams layoutParams = flVideoParentView.getLayoutParams();
                //宽高增量添加
                layoutParams.width = layoutParams.width + deltaX;
                if (ori == 0 || ori == 2) {
                    //竖屏状态
                    layoutParams.height = (int) (layoutParams.height + (deltaX * screenRatio));
                } else {
                    //横屏状态
                    layoutParams.height = (int) (layoutParams.height + (deltaX / screenRatio));
                }

                //限制最大窗口
                if (ori == 0 || ori == 2) {
                    //竖屏限制最大窗口
                    if (layoutParams.width > Config.PHONE_WINDOW_VERTICAL_MAX_WIDTH) {
                        layoutParams.width = Config.PHONE_WINDOW_VERTICAL_MAX_WIDTH;
                        layoutParams.height = (int) (layoutParams.width * screenRatio);
                    }
                } else {
                    //横屏限制最大窗口
                    if (layoutParams.width > Config.PHONE_WINDOW_HOR_MAX_WIDTH) {
                        layoutParams.width = Config.PHONE_WINDOW_HOR_MAX_WIDTH;
                        layoutParams.height = (int) (layoutParams.width / screenRatio);
                    }

                }
                //限制手机投屏最小窗口
                if (ori == 0 || ori == 2) {
                    //竖屏状态先限制窗口最小
                    if (layoutParams.width < Config.PHONE_WINDOW_VERTICAL_MIN_WIDTH) {
                        layoutParams.width = Config.PHONE_WINDOW_VERTICAL_MIN_WIDTH;
                        layoutParams.height = (int) (Config.PHONE_WINDOW_VERTICAL_MIN_WIDTH * screenRatio);
                    }

                } else {
                    //横屏状态先限制窗口最小
                    if (layoutParams.width < Config.PHONE_WINDOW_HOR_MIN_WIDTH) {
                        layoutParams.width = Config.PHONE_WINDOW_HOR_MIN_WIDTH;
                        layoutParams.height = (int) (Config.PHONE_WINDOW_HOR_MIN_WIDTH / screenRatio);
                    }
                }

                flVideoParentView.setLayoutParams(layoutParams);
                //2,处理toolBar的缩放
                WindowManager.LayoutParams windowParams = window.getParams();

                windowParams.width = flVideoParentView.getLayoutParams().width;
                if (windowParams.width < Config.WINDOW_TOOL_BAR_MIN_WIDTH) {
                    windowParams.width = Config.WINDOW_TOOL_BAR_MIN_WIDTH;
                }

                CardView cardView = window.getCardView();
                FrameLayout.LayoutParams cardViewLayoutParams = (FrameLayout.LayoutParams) cardView.getLayoutParams();
                int windowRadius = WindowLocationUtil.getWindowRadius(window.getType(), ori, flVideoParentView.getLayoutParams().height);
                int windowMargin = WindowLocationUtil.getWindowMargin(window.getType(), window.getOri(), flVideoParentView.getLayoutParams().width);
                cardViewLayoutParams.setMargins(windowMargin, windowMargin, windowMargin, windowMargin);
                if (window.getType() == Config.TYPE_IOS_PHONE && (window.getOri() == 0 || window.getOri() == 2)) {
                    cardViewLayoutParams.setMargins(windowMargin, windowMargin - 10, windowMargin, windowMargin - 10);
                }

                cardView.setRadius(windowRadius);
                cardView.setLayoutParams(cardViewLayoutParams);
                windowParams.height = flVideoParentView.getLayoutParams().height + Config.WINDOW_TOOL_BAR_HEIGHT;
                manager.updateViewLayout(window.getFloatWindowView(), windowParams);

                startX = (int) event.getRawX();
                startY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                //发送cmd19消息
//                MsgUtil.sendGroupCmd19Msg(currentMode, windowList);
                sendCdm19Msg(false);
                break;
        }
    }

    Handler handler = new Handler(Looper.myLooper());

    @Override
    public void onClick(View v) {
        for (Window window : windowList) {
            if (window.getIvClose() == v) {
                removeWindow(window, false);//关闭

                return;
            }

            if (window.getIvScreenShot() == v) {//截屏
                ScreenShot(window);
            }

            if (window.getIvFullScreen() == v) {//全屏
                fullScreen(window);
            }

            if (window.getIvFullScreenMode() == v || window.getIvFullScreenModeIcon() == v) {//等分模式下切全屏模式
                fullScreen(window);
            }

            if (window.getIvDfMode() == v || window.getIvDfModeIcon() == v) {//切换等分模式
                dfMode();
            }
            if (window.getIvWindowModeIcon() == v) {//窗口模式
                windowMode();
            }
            if (window.getLockScreenIcon() == v) {//锁屏
                //ScreenShotUtils.lockScreen();
            }
        }
    }

    /**
     * 截屏
     *
     * @param window
     */
    private void ScreenShot(Window window) {
        window.getCardViewBg().setVisibility(View.GONE);
        CardView cardView = window.getCardView();
        float radius = cardView.getRadius();
        cardView.setRadius(0);
        handler.postDelayed(() -> ScreenShotUtils.screenShot(App.getInstance().getApplicationContext(), window, result -> {
            if (result == 0 || result == 1) {
                window.getCardViewBg().setVisibility(View.VISIBLE);
                cardView.setRadius(radius);
            } else {
                ToastUtils.getInstance().showToast(ToastUtils.ToastType.ERROR, App.getInstance().getApplicationContext().getString(R.string.screen_shot_err));
            }
        }), 1500);
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        for (Window window : windowList) {
            if (v == window.getToolBar() || v == window.getRlDeviceNameTouchBar()) {//窗口拖动
                dragWindow(window, event);
            }
            if (v == window.getScaleView()) {//窗口缩放
                if (window.getWindowStatus() != Config.FULL_SCREEN_MODE) {
                    scaleWindow(window, event);
                }
            }
        }
        return true;
    }

    /**
     * 当窗口处于选中状态
     *
     * @param touchFrameLayout
     * @param event
     */
    @Override
    public void onFrameLayoutTouched(TouchFrameLayout touchFrameLayout, MotionEvent event) {
        LogUtil.e("zjx", "当前窗口处于选中状态");
        if (windowList == null || windowList.size() <= 1) {
            return;
        }
        if (currentMode != Config.WINDOW_MODE) {
            return;
        }
        for (Window window : windowList) {
            TouchFrameLayout flVideoParentView = window.getFlVideoParentView();
            if (flVideoParentView == touchFrameLayout) {//设备被选中
                window.setSelected(true);
            } else {//设备未选中
                window.setSelected(false);
            }
        }
        setWindowToolBar();
    }

    /**
     * 当窗口处于选中状态，控制条状态设置
     */
    private void setWindowToolBar() {
        if (currentMode != Config.WINDOW_MODE) {
            return;
        }
        if (windowList == null || windowList.size() == 0) {
            return;
        }
        if (windowList.size() == 1) {//只有一个窗口的时候，显示多功能控制条
            Window window = windowList.get(0);
            window.setSelected(true);
        }
        for (Window window : windowList) {
            boolean selected = window.isSelected();
            ConstraintLayout toolBar = window.getToolBar();
            RelativeLayout rlDeviceNameTouchBar = window.getRlDeviceNameTouchBar();
            if (selected) {//设备被选中
                if (toolBar.getVisibility() == View.GONE || toolBar.getVisibility() == View.INVISIBLE) {
                    toolBar.setVisibility(View.VISIBLE);
                }
                rlDeviceNameTouchBar.setVisibility(View.GONE);
            } else {//设备未选中
                if (rlDeviceNameTouchBar.getVisibility() == View.GONE || rlDeviceNameTouchBar.getVisibility() == View.INVISIBLE) {
                    rlDeviceNameTouchBar.setVisibility(View.VISIBLE);
                }
                toolBar.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 设置窗口的选中状态
     */
    private void checkSelectedWindow() {
        if (windowList == null || windowList.size() == 0) {
            return;
        }
        if (windowList.size() == 1) {
            Window window = windowList.get(0);
            window.setSelected(true);
            return;
        }
        for (Window window : windowList) {
            if (window.isSelected()) {
                return;//被选中的窗口还没有被移除
            }
        }
        windowList.get(windowList.size() - 1).setSelected(true);
    }

    private void setWindowSelected(Window window) {
        if (windowList == null || windowList.size() == 0) {
            return;
        }
        if (windowList.size() == 1) {
            windowList.get(0).setSelected(true);
            return;
        }
        for (Window w : windowList) {
            if (w == window) {
                w.setSelected(true);
            } else {
                w.setSelected(false);
            }
        }
    }

    /**
     * 设备掉线提示
     *
     * @param client_id
     * @param dev_id
     */
    public void onDeviceOffLine(int client_id, String dev_id) {
        SoftWindow softWindow = WindowFinder.getInstance().getSoftWindowByDevId(dev_id);
        if (softWindow == null) return;
        ToastUtils.getInstance().showToast(ToastUtils.ToastType.ERROR, softWindow.getTaskModel().targetName + "已断开连接");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveMsg(SystemEvent event) {
        if (event.type == SystemEvent.TYPE_REC_FAILED) {
            MsgUtil.sendCmd17Msg(event.taskModel.dev_id, event.taskModel.taskId, 3);
//            Log.d(TAG, "onReceiveMsg: 编码器失败，发送cmd 17消息 event.taskModel.taskId = " + event.taskModel.taskId);
        }
    }

    /**
     * 发送端设置显示模式
     *
     * @param msg18
     */
    public void onModeChange(Msg18 msg18) {
        if (msg18 == null) {
            return;
        }
        mainHandler.post(() -> {
            int mode = msg18.getMode();
            String taskId = msg18.getTaskId();
            SoftWindow softWindowByTaskId = WindowFinder.getInstance().getSoftWindowByTaskId(taskId);
            LogUtil.d(TAG, "onModeChange: mode = " + mode);
            if (mode == Config.WINDOW_MODE) {//窗口
                /*boolean supportWindowMode = PolicyManager.getInstance().getPolicy().isSupportWindowMode();
                if (!supportWindowMode) {
                    ToastUtils.getInstance().showToast(ToastUtils.ToastType.WARNING, "非激活版本，无法切换至窗口模式");
                    return;
                }*/
                windowMode();
            } else if (mode == Config.DF_MODE) {//等分
                if (windowList.size() < 2) {
                    ToastUtils.getInstance().showToast(ToastUtils.ToastType.WARNING, "当前无法切换至分屏模式");
                    return;
                }
                dfMode();
            } else {//全屏
                if (!TextUtils.isEmpty(taskId)) {
                    fullScreen(softWindowByTaskId);
                } else {
                    ToastUtils.getInstance().showToast(ToastUtils.ToastType.WARNING, "任务Id为空,无法切换全屏模式");
                }
            }
        });
    }

    /**
     * 当切换至非Android通道下面
     */
    boolean sourceChange = false;

    public void onNotAndroidSource() {
        if (windowList.size() == 0) {
            return;
        }
        sourceChange = true;
        if (currentMode == Config.WINDOW_MODE) {
            //如果当前模式是窗口模式
            if (windowList.size() == 1) {
                fullScreen(windowList.get(0));
            } else {
                dfMode();
            }
        }
        if (lastMode == Config.WINDOW_MODE) {
            lastMode = Config.DF_MODE;
        }
    }

    /**
     * 重新开始解码
     */
    public void onReEncode() {
        sendCdm19Msg(false);
//        MsgUtil.sendGroupCmd19Msg(currentMode, windowList);
    }

    /***************************遥控适配***************************/

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if (event.getAction() == KeyEvent.ACTION_UP) {
            Window focusWindow = WindowFinder.getInstance().getFocusWindow();//选择焦点窗口
            if (focusWindow == null) {
                focusWindow = WindowFinder.getInstance().getDefaultFocusWindow();
                setWindowFocus(focusWindow);

            }
            if (keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (currentMode == Config.FULL_SCREEN_MODE) {
                    return true;
                }
                int i = windowList.indexOf(focusWindow);
                i = (i - 1) < 0 ? 0 : (i - 1);
                setWindowFocus(windowList.get(i));
                return true;
            }

            if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {//选择下一个窗口
                if (currentMode == Config.FULL_SCREEN_MODE) {
                    return true;
                }
                int i = windowList.indexOf(focusWindow);
                i = (i + 1) >= windowList.size() ? windowList.size() - 1 : (i + 1);
                setWindowFocus(windowList.get(i));
                return true;
            }

            if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER && (currentMode != Config.FULL_SCREEN_MODE)) {
                fullScreen(WindowFinder.getInstance().getFocusWindow());//全屏
                return true;
            }

            if (keyCode == KeyEvent.KEYCODE_BACK && currentMode == Config.FULL_SCREEN_MODE) {
                if (lastMode == Config.WINDOW_MODE || windowList.size() == 1) {//返回上一个模式
                    windowMode();
                } else {
                    dfMode();
                }
                return true;
            }
        }
        return false;
    }

    private void setWindowFocus(Window window) {
        if (windowList == null || windowList.size() == 0) {
            return;
        }
        for (Window w : windowList) {
            if (w != window) {
                w.getRlFocusBg().setBackground(null);
                w.setFocus(false);
                w.getParams().flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM;
            } else {
                w.getRlFocusBg().setBackground(App.getInstance().getApplicationContext().getDrawable(R.drawable.shape_window_select));
                w.setFocus(true);
                w.getParams().flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
            }
            if (currentMode == Config.FULL_SCREEN_MODE) {
                w.getRlFocusBg().setBackground(null);
            }
            LogUtil.e("setWindowFocus window = " + window.getName());
            manager.updateViewLayout(w.getFloatWindowView(), w.getParams());
            w.getFloatWindowView().requestFocus();
        }
        count = 0;
        focusHandler.sendEmptyMessage(WHAT);
    }

    int WHAT = 1009;
    int count = 0;
    Handler focusHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            if (msg.what == WHAT) {
                Window focusWindow = WindowFinder.getInstance().getFocusWindow();
                if (focusWindow == null) {
                    return false;
                }
                if (count == 3) {
                    focusWindow.getRlFocusBg().setBackground(null);
                    count = 0;
                    return true;
                }
                count++;
                focusHandler.sendEmptyMessageDelayed(WHAT, 1000);
            }
            return false;
        }
    });

    /***************************遥控适配***************************/
}

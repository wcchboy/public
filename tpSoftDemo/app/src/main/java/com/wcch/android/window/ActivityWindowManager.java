package com.wcch.android.window;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.media.MediaFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.igrs.sml.PlayerView;
import com.igrs.sml.TaskModel;
import com.igrs.sml.callback.DecoderCallback;
import com.wcch.android.App;
import com.wcch.android.Constants;
import com.wcch.android.R;
import com.wcch.android.config.Config;
import com.wcch.android.msg.DestroyTaskEvent;
import com.wcch.android.msg.MsgUtil;
import com.wcch.android.soft.util.LogUtil;
import com.wcch.android.soft.util.WindowLocationUtil;
import com.wcch.android.utils.ResolutionUtil;
import com.wcch.android.utils.WindowFinder;
import com.wcch.android.view.ProjectionFrameLayout;
import com.wcch.android.view.ToastUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import inter.OnWindowStatusChangeListener;

/**
 * @author created by Lzq
 * @time：2022/3/17
 * @Des：白板外投屏窗口管理器。统一管理投屏窗口添加、移除、模式切换。 TODO UI调整、全屏bug修复、全屏按钮拖拽
 */
public class ActivityWindowManager implements View.OnClickListener, View.OnTouchListener, ProjectionFrameLayout.OnWindowSelectedListener {
    private String TAG = "ActivityWindowManager";
    private Context context;
    private ProjectionFrameLayout parentLayout;
    private static ActivityWindowManager activityWindowManager;
    private List<Window> windowList = new ArrayList<>();
    private final int MAX_WINDOW_COUNT = 4;//最大支持的窗口数量
    private int currentWindowCount = 0;//当前窗口数量
    private int currentWindowMode = 0;//当前窗口模式  0：全屏模式   1：分屏模式   2:最小化
    private Window currentWindow;//当前选中窗口
    private OnWindowStatusChangeListener listener;
    private LockScreenHelper mLockScreenHelper = new LockScreenHelper();
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    private List<FullScreenIcon> imageList = new ArrayList<>();


    private boolean timerWorking = false;
    private final long GONE_TIME = 5000;//全屏按钮，5秒没操作自动消失
    Timer timer = new Timer();
    MyTask task = null;
    Handler handler = new Handler(Looper.getMainLooper());

    class MyTask extends TimerTask {
        @Override
        public void run() {
            for (FullScreenIcon icon : imageList) {
                long lastTouchTime = icon.getLastTouchTime();
                long l = System.currentTimeMillis();
                if (l - lastTouchTime >= GONE_TIME) {
                    RelativeLayout relativeLayout = icon.getRelativeLayout();
                    if (relativeLayout != null && relativeLayout.getVisibility() == View.VISIBLE) {
                        handler.post(() -> relativeLayout.setVisibility(View.GONE));
                    }
                }
            }
        }
    }


    private VideoView mVideoView;

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    private ActivityWindowManager() {
    }

    public static ActivityWindowManager getInstance() {
        if (activityWindowManager == null) {
            /*synchronized (SoftWindowManager.class) {
                activityWindowManager = new ActivityWindowManager();
            }*/
                activityWindowManager = new ActivityWindowManager();
        }
        return activityWindowManager;
    }

    public void setParameter(Context context, ProjectionFrameLayout parentLayout, OnWindowStatusChangeListener listener) {
        this.context = context;
        this.parentLayout = parentLayout;
        this.listener = listener;

        this.parentLayout.setOnWindowSelectedListener(this);
    }

    /**
     * 是否允许添加一个投屏窗口
     *
     * @return
     */
    private boolean canInsertWindow() {
        if (currentWindowCount >= MAX_WINDOW_COUNT) {
            ToastUtils.getInstance().showToast(ToastUtils.ToastType.WARNING,
                    App.getInstance().getApplicationContext().getResources().getString(R.string.over_max_projection_count));
            return false;
        }
        return true;
    }

    /**
     * 添加一个投屏窗口
     * 1：区分投屏类型：W20 or 软投屏
     * 2：加载对应窗口
     * 3：调整窗口位置
     */
    public void addWindow(Window window) {
        if (window == null) return;
        if (!canInsertWindow()) return;
        if (window instanceof SoftWindow) {
            SoftWindow softWindow = (SoftWindow) window;
            addSoftWindow(softWindow);
        }

        if (!(window instanceof SoftWindow)) {
            onWindowChange();
        }
    }

    /**
     * 添加一个软投屏窗口
     *
     * @param softWindow
     */
    private void addSoftWindow(SoftWindow softWindow) {
        if (context == null || parentLayout == null)
            throw new IllegalArgumentException("setParameter first");
        if (!canInsertWindow()) return;//超过最大投屏数量
        softWindow.setDev_id(softWindow.getTaskModel().dev_id);
        RelativeLayout windowView = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.layout_video_group, null);
        windowView.setTag(softWindow.getTaskModel().dev_id);

        PlayerView playerView = windowView.findViewById(R.id.soft_player_view);
        RelativeLayout rlFullScreen = windowView.findViewById(R.id.rl_full_screen);
        RelativeLayout rlFullScreenBg = windowView.findViewById(R.id.rl_full_screen_bg);
        rlFullScreen.setOnClickListener(this);
        rlFullScreen.setOnTouchListener(this);

        playerView.setOnTouchListener(this);

        ImageView dragBg = windowView.findViewById(R.id.iv_drag_bg);
        softWindow.setDragBg(dragBg);
        softWindow.setSelected(false);
        softWindow.setShowing(false);
        softWindow.setWindowView(windowView);
        softWindow.setPlayerView(playerView);
        softWindow.setFullScreenButton(rlFullScreen);
        softWindow.setFullScreenBg(rlFullScreenBg);
        int size = windowList.size();
        if (size > 0) {
            softWindow.setWindowStatus(Constants.SPLIT_MODE);
            for (Window window : windowList) {
                window.setWindowStatus(Constants.SPLIT_MODE);
            }
            LogUtil.e("current Mode = SPLIT_MODE");
        } else {
            softWindow.setWindowStatus(Constants.FULL_SCREEN_MODE);
            LogUtil.e("current Mode = FULL_SCREEN_MODE");
        }

        TaskModel taskModel = softWindow.getTaskModel();
        if (taskModel.targetName.startsWith("IPHONE")) {//设置设备类型为iOS设备
            softWindow.setType(Config.TYPE_IOS_PHONE);
        } else if (taskModel.targetName.startsWith("ANDROID")) {
            softWindow.setType(Config.TYPE_ANDROID_PHONE);
            softWindow.setOri(0);
        } else {
            softWindow.setType(Config.TYPE_PC);
            softWindow.setOri(-1);
        }
        windowList.add(softWindow);
        imageList.add(new FullScreenIcon(rlFullScreenBg));

        playerView.init(softWindow.getTaskModel(), new DecoderCallback() {
            @Override
            public void decoderCallback(int code) {
                //解码器启动回调
                //code:1:硬解码成功  2:硬解码失败，软解码成功   -1:解码失败
                LogUtil.d("decoderCallback: code = " + code);
                if (code == -1) {//解码失败
                    ToastUtils.getInstance().showToast(ToastUtils.ToastType.ERROR, softWindow.getTaskModel().targetName + "解码失败");
                    if (softWindow == null) return;
                    MsgUtil.sendCmd17Msg(softWindow.getTaskModel().dev_id, softWindow.getTaskModel().taskId, 2);

                    SoftWindow softWindowByTaskIp = WindowFinder.getInstance().getSoftWindowByDevId(softWindow.getDev_id());
                    if (softWindowByTaskIp != null) {
                        if (windowList.contains(softWindowByTaskIp))
                            windowList.remove(softWindowByTaskIp);
                    }
                }
            }

            @Override
            public void decoderSizeChage(int width, int height, MediaFormat var3) {
                //解码分辨率变化   ori 设备类型
//                LogUtil.d("decoderSizeChage: width = " + width + "    height = " + height);
                //如果是横竖屏切换
                mainHandler.post(() -> {
                    setSoftWindowParams(softWindow, width, height);
                    onDeskSizeChange(softWindow.getDev_id(), width, height);
                });
            }
        });

        playerView.setScaleX(1.00001f);
        parentLayout.addView(windowView);
        currentWindowCount++;
        if (!timerWorking) {
            timerWorking = true;
            if (task == null) task = new MyTask();
            if (timer == null) timer = new Timer();
            timer.schedule(task, 1000, 1000);
        }
    }

    private void removeTimer() {
        if (timerWorking) {
            timerWorking = false;
            timer.cancel();
            timer.purge();
            task = null;
            timer = null;
        }
    }

    private void setSoftWindowParams(SoftWindow softWindow, int width, int height) {
        if (!softWindow.isShowing()) {
            float screenRatio = (float) ((height * 1.0) / (width * 1.0));//确定设备宽高比
            if (softWindow.getType() == Config.TYPE_IOS_PHONE
                    || softWindow.getType() == Config.TYPE_ANDROID_PHONE) {
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
            softWindow.setScreenRatio(screenRatio);

            Window.WindowLocation softWindowSize = WindowLocationUtil.getSoftWindowSize(windowList.size(), softWindow.getType(), softWindow.getOri(), screenRatio);
            softWindow.setWindowLocation(softWindowSize);

        }
    }


    /**
     * 移除一个窗口
     * 1：区分投屏类型：W20 or 软投屏
     * 2：移除对应窗口
     */
    public void removeWindow(Window window) {

        if (window == null) return;
        if (windowList == null || windowList.size() == 0) return;
        if (window instanceof SoftWindow) {
            SoftWindow softWindow = (SoftWindow) window;
            TaskModel taskModel = softWindow.getTaskModel();
            removeSoftWindow(taskModel, true);
        }

        onWindowChange();
    }

    /**
     * 移除一个软投屏窗口
     *
     * @param taskModel
     */
    private void removeSoftWindow(TaskModel taskModel, boolean fromPc) {
        if (taskModel == null) return;
        if (currentWindowCount <= 0) return;
        if (windowList.size() == 0) return;
        LogUtil.e("removeSoftWindow");
        if (windowList.size() == 1) removeTimer();
        SoftWindow softWindow = getSoftWindowByTaskModel(taskModel);
        if (softWindow == null) return;
        PlayerView playerView = softWindow.getPlayerView();
        RelativeLayout windView = softWindow.getWindowView();
        if (!fromPc) {
            EventBus.getDefault().post(new DestroyTaskEvent(taskModel.taskId));
        }
        Log.d(TAG, "removeWindow TaskId = " + taskModel.taskId);
        if (playerView != null) {
            playerView.onDestroy();
        }
        parentLayout.removeView(windView);
        windView.removeAllViews();

        removeWindowFromList(softWindow);

        currentWindowCount--;
        if (currentWindowCount < 0) currentWindowCount = 0;
        if (listener != null) listener.onWindowRemoveListener(softWindow);

    }


    /**
     * 移除所有投屏窗口
     */
    public void removeAllWindow() {
        currentWindowCount = 0;
        if (windowList == null || windowList.size() == 0) return;
        for (Window window : windowList) {
            if (window instanceof SoftWindow) {
                SoftWindow softWindow = (SoftWindow) window;
                removeSoftWindow(softWindow.getTaskModel(), false);
            }
        }
        windowList.clear();
    }

    @Override
    public void onClick(View v) {
        for (Window window : windowList) {
            RelativeLayout fullScreenButton = window.getFullScreenButton();
            if (v == fullScreenButton) {
                if (window.getWindowStatus() == Constants.FULL_SCREEN_MODE) {
                    window.setWindowStatus(Constants.SPLIT_MODE);
                    parentLayout.exitFullScreen(window.getWindowView());
                } else {
                    window.setWindowStatus(Constants.FULL_SCREEN_MODE);
                    parentLayout.windowFullScreen(window.getWindowView());
                }

            }
        }
    }

    private SoftWindow getSoftWindowByTaskModel(TaskModel taskModel) {
        if (taskModel == null) return null;
        if (windowList == null || windowList.size() == 0) return null;
        for (Window window : windowList) {
            if (window instanceof SoftWindow) {
                SoftWindow softWindow = (SoftWindow) window;
                if (TextUtils.equals(softWindow.getTaskModel().taskId, taskModel.taskId))
                    return softWindow;
            }
        }
        return null;
    }

    private SoftWindow getSoftWindowByDevId(String devId) {
        if (TextUtils.isEmpty(devId)) return null;
        if (windowList == null || windowList.size() == 0) return null;
        for (Window window : windowList) {
            if (window instanceof SoftWindow) {
                SoftWindow softWindow = (SoftWindow) window;
                String dev_id = softWindow.getDev_id();
                if (TextUtils.equals(dev_id, devId)) return softWindow;
            }
        }
        return null;
    }

    private void removeWindowFromList(Window window) {
        if (window == null) return;
        if (windowList == null || windowList.size() == 0) return;
        if (windowList.contains(window)) {
            windowList.remove(window);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    @Override
    public void onWindowSelected(View view) {
//        String tag = view.getTag().toString();
        Object tag = view.getTag();
        if (tag == null) {
            ToastUtils.getInstance().showToast(ToastUtils.ToastType.ERROR, "view tag为空了啊");
            return;
        }
        String targetIp = tag.toString();
        for (Window window : windowList) {
            String windowIp = window.getIp();
            RelativeLayout fullScreenBg = window.getFullScreenBg();
            if (TextUtils.equals(targetIp, windowIp)) {
                if (fullScreenBg.getVisibility() == View.GONE && windowList.size() > 1) {
                    fullScreenBg.setVisibility(View.VISIBLE);
                }
                for (FullScreenIcon icon : imageList) {
                    RelativeLayout relativeLayout = icon.getRelativeLayout();
                    if (relativeLayout == fullScreenBg) {
                        icon.setLastTouchTime(System.currentTimeMillis());
                    }
                }
            } else {
                if (fullScreenBg.getVisibility() == View.VISIBLE)
                    fullScreenBg.setVisibility(View.GONE);
            }
        }
    }

    public int getCurrentWindowCount() {
        return windowList.size();
    }

    public List<Window> getWindowList() {
        return windowList;
    }

    /**
     * 软投屏设备分辨率发生变化（手机、平板...）
     *
     * @param devId     设备ID
     * @param newWidth
     * @param newHeight
     */
    public void onDeskSizeChange(String devId, int newWidth, int newHeight) {
        LogUtil.e("onDeskSizeChange devId = " + devId + "   newWidth = " + newWidth + "   newHeight = " + newHeight);
        if (windowList == null || windowList.size() == 0) return;
        SoftWindow sizeChangeWindow = getSoftWindowByDevId(devId);
        if (sizeChangeWindow == null) return;
        sizeChangeWindow.setResolutionWidth(newWidth);
        sizeChangeWindow.setResolutionHeight(newHeight);
        if (!sizeChangeWindow.isShowing()) {
//            MsgUtil.sendGroupCmd19Msg(Config.DF_MODE, windowList);
            onWindowChange();
            sizeChangeWindow.setShowing(true);
        } else {
            setOri(devId,newWidth,newHeight);
        }

    }

    private void onWindowChange() {
        if (windowList == null || windowList.size() == 0) return;
        for (Window window : windowList) {
            if (window instanceof SoftWindow) {
                SoftWindow softWindow = (SoftWindow) window;
                PlayerView playerView = softWindow.getPlayerView();
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) playerView.getLayoutParams();
                ResolutionUtil.Resolution newResolution;
                if (windowList.size() > 1) {//当前只有一个投屏窗口
                    newResolution = ResolutionUtil.getNewResolution(Constants.SPLIT_MODE, softWindow.getResolutionWidth(), softWindow.getResolutionHeight());
                } else {//当前有多个投屏窗口
                    newResolution = ResolutionUtil.getNewResolution(Constants.FULL_SCREEN_MODE, softWindow.getResolutionWidth(), softWindow.getResolutionHeight());
                }
                layoutParams.width = newResolution.getWidth();
                layoutParams.height = newResolution.getHeight();
                LogUtil.e("layoutParams.width = " + layoutParams.width);
                LogUtil.e("layoutParams.height = " + layoutParams.height);
                playerView.setLayoutParams(layoutParams);
            }
        }
    }

    private void setOri(String devId, int newWidth, int newHeight) {
        if (windowList == null || windowList.size() == 0) return;
        SoftWindow sizeChangeWindow = getSoftWindowByDevId(devId);
        if (sizeChangeWindow == null) return;
        //设备分辨率发生改变
        PlayerView playerView = sizeChangeWindow.getPlayerView();
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) playerView.getLayoutParams();
        ResolutionUtil.Resolution newResolution;
        if (windowList.size() > 1) {//当前只有一个投屏窗口
            newResolution = ResolutionUtil.getNewResolution(Constants.SPLIT_MODE, newWidth, newHeight);
        } else {//当前有多个投屏窗口
            newResolution = ResolutionUtil.getNewResolution(Constants.FULL_SCREEN_MODE, newWidth, newHeight);
        }
        layoutParams.width = newResolution.getWidth();
        layoutParams.height = newResolution.getHeight();
        playerView.setLayoutParams(layoutParams);
    }



    private void hideDlnaPlayAndControlView(){
        LogUtil.e("zjx","隐藏控制UI ");

    }

    private void showDlnaPlayAndControlView(){

    }

}

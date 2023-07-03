package com.wcch.android.msg;

import android.view.ViewGroup;

import com.google.gson.Gson;
import com.igrs.sml.PlayerView;
import com.igrs.sml.TaskModel;
import com.igrs.tpsdk.ProjectionSDK;
import com.wcch.android.config.Config;
import com.wcch.android.soft.util.LogUtil;
import com.wcch.android.view.TouchFrameLayout;
import com.wcch.android.window.SoftWindow;
import com.wcch.android.window.Window;

import java.util.ArrayList;
import java.util.List;

/**
 * todo 发送消息
 *
 * @author created by Lzq
 * @time：2022/5/5
 * @Des：软投屏消息工具类
 */
public class MsgUtil {

    private static final String TAG = MsgUtil.class.getSimpleName();


    /**
     * msg 10消息类型：通知发送端目前处切换至非Android通道
     *
     * @param dev_id
     */
    public static void sendCmd10Msg(String dev_id, String taskId) {
        Msg10 msg10 = new Msg10();
        msg10.setCmd(10);
        msg10.setTaskId(taskId);
        Gson gson = new Gson();
        ProjectionSDK.getInstance().sendCMDMessage(dev_id, gson.toJson(msg10).getBytes());
    }


    /**
     * msg 17类型消息：投屏、遥控超限，设备解码失败.....
     *
     * @param dev_id
     * @param taskId
     * @param code   //0:投屏达到最大数量
     *               1：遥控达到最大数量
     *               2：解码器启动失败
     *               3:遥当前不在Android信源通道下控编码失败
     *               4:当前不在Android通道
     *               5:遥控过程中切换至非Android通道,发送端关闭遥控
     *               6:retry失败，接收端任务列表已移除此任务
     *               7:非激活版本，最多允许一路投屏
     *               8:非激活版本不允许遥控
     *               9:非激活版本无法切换窗口模式
     */
    public static void sendCmd17Msg(String dev_id, String taskId, int code) {
        Msg17 msg17 = new Msg17();
        msg17.setCmd(17);
        msg17.setTaskId(taskId);
        msg17.setCode(code);
        Gson gson = new Gson();
        ProjectionSDK.getInstance().sendCMDMessage(dev_id, gson.toJson(msg17).getBytes());
    }

    /**
     * msg 20消息类型：通知发送端目前处切换至非Android通道
     *
     * @param dev_id
     */
    public static void sendCmd20Msg(String dev_id) {
        Msg20 msg20 = new Msg20();
        msg20.setCmd(20);
        Gson gson = new Gson();
        ProjectionSDK.getInstance().sendCMDMessage(dev_id, gson.toJson(msg20).getBytes());
    }

    /**
     * 投屏状态同步消息，通知发送端投屏状态信息（群组方式）
     * 动态分辨力设置消息
     *
     * @param currentMode 当前模式
     * @param windowList
     */
    public static void sendGroupCmd19Msg(int currentMode, List<Window> windowList) {
        if (windowList == null || windowList.size() == 0) return;
        List<SoftWindow> tempWindowList = new ArrayList<>();
        //过滤W20设备 md50/80设备
        for (Window window : windowList) {
          /*  if (window instanceof W20Window) continue;
            if (window instanceof M50Window) continue;//add by ryanwang*/
            SoftWindow softWindow = (SoftWindow) window;
            tempWindowList.add(softWindow);
        }
        if (tempWindowList == null || tempWindowList.size() == 0) return;
        List<Msg19.ProjectionStatus> statusList = new ArrayList<>();
        String deviceName = "";
        for (SoftWindow softWindow : tempWindowList) {

            TaskModel taskModel = softWindow.getTaskModel();
            TouchFrameLayout flVideoParentView = softWindow.getFlVideoParentView();
            ViewGroup.LayoutParams layoutParams = flVideoParentView.getLayoutParams();
            int width = layoutParams.width;
            int height = layoutParams.height;
            LogUtil.e("zjx","width is "+width);
            LogUtil.e("zjx","height is "+height);
            int resolutionWidth = getResolution(width);
            int resolutionHeight = getResolution(height);
            LogUtil.e("zjx","resolutionWidth is "+width);
            LogUtil.e("zjx","resolutionHeight  is "+height);




            LogUtil.e("zjx_resolution","当前投屏路数:"+windowList.size() + "当前分辨率 ：resolutionWidth ： "+resolutionWidth + " resolutionHeight: "+resolutionHeight);

            Msg19.ProjectionStatus status = new Msg19.ProjectionStatus();
            status.setWidth(resolutionWidth);
            status.setHeight(resolutionHeight);
            status.setDeviceName(taskModel.targetName);
            status.setTaskId(taskModel.taskId);

           //设置固定帧率
            if (windowList.size()==1){   //一路
                    status.setFps(22);
            } else {              //多路
                status.setFps(22);
            }

            LogUtil.e("zjx_resolution","当前投屏路数:"+windowList.size() + "fps： "+status.getFps());


            if (currentMode == Config.FULL_SCREEN_MODE &&
                    softWindow.getWindowStatus() == Config.FULL_SCREEN_MODE) {
                status.setFullScreen(1);//全屏
            } else if (currentMode == Config.FULL_SCREEN_MODE &&
                    softWindow.getWindowStatus() == Config.FULL_SCREEN_SMALL_WINDOW_MODE) {
                status.setFullScreen(0);//非全屏
                status.setWidth(0);
                status.setHeight(0);
            } else {
                status.setFullScreen(0);//非全屏
            }
            LogUtil.d("ShanLian222", "sendGroupCmd19Msg taskId = " + taskModel.taskId);
            statusList.add(status);
            deviceName = softWindow.getDeviceName();
        }

        Msg19 msg19 = new Msg19();
        //msg19.setActiveState(PolicyManager.getInstance().getPolicy().isActive() ? 1 : 0);
        msg19.setActiveState(1 );
        msg19.setCurrentMode(currentMode);
        msg19.setDeviceName(deviceName);
        //int source = SourceUtil.isAndroidSource() ? 0 : 1;
        int source = 0;//使用其他SDK这里写死了
        msg19.setIsAndroid(source);
        msg19.setCmd(19);
        msg19.setList(statusList);

        Gson gson = new Gson();

        for (SoftWindow softWindow : tempWindowList) {
            ProjectionSDK.getInstance().sendCMDMessage(softWindow.getDev_id(), gson.toJson(msg19).getBytes());
        }
    }

    /**
     * 投屏状态同步消息，通知发送端投屏状态信息（群组方式）
     *
     * @param currentMode 当前模式
     * @param windowList
     */
    public static void sendSourceChangeGroupCmd19Msg(int currentMode, List<Window> windowList) {
        if (windowList == null || windowList.size() == 0) return;
        List<SoftWindow> tempWindowList = new ArrayList<>();
        //过滤W20设备 md50/80设备
        for (Window window : windowList) {
            /*if (window instanceof W20Window) continue;
            if (window instanceof M50Window) continue;//add by ryanwang */
            SoftWindow softWindow = (SoftWindow) window;
            tempWindowList.add(softWindow);
        }
        if (tempWindowList == null || tempWindowList.size() == 0) return;
        List<Msg19.ProjectionStatus> statusList = new ArrayList<>();

        for (SoftWindow softWindow : tempWindowList) {

            TaskModel taskModel = softWindow.getTaskModel();
            int width = 0;
            int height = 0;


            Msg19.ProjectionStatus status = new Msg19.ProjectionStatus();
            status.setWidth(0);
            status.setHeight(0);
            status.setDeviceName(taskModel.targetName);
            status.setTaskId(taskModel.taskId);
            if (currentMode == Config.FULL_SCREEN_MODE &&
                    softWindow.getWindowStatus() == Config.FULL_SCREEN_MODE) {
                status.setFullScreen(1);//全屏
            } else if (currentMode == Config.FULL_SCREEN_MODE &&
                    softWindow.getWindowStatus() == Config.FULL_SCREEN_SMALL_WINDOW_MODE) {
                status.setFullScreen(0);//非全屏
                status.setWidth(0);
                status.setHeight(0);
            } else {
                status.setFullScreen(0);//非全屏
            }
//            LogUtil.e("ShanLian222", "send cmd 19 window taskId = " + softWindow.getTaskModel().taskId);
            statusList.add(status);
        }

        Msg19 msg19 = new Msg19();
        msg19.setCurrentMode(currentMode);
        //int source = SourceUtil.isAndroidSource() ? 0 : 1;
        int source = 0;//使用其他SDK这里写死了
        msg19.setIsAndroid(source);
        msg19.setCmd(19);
        msg19.setList(statusList);

        Gson gson = new Gson();

        for (SoftWindow softWindow : tempWindowList) {
            ProjectionSDK.getInstance().sendCMDMessage(softWindow.getDev_id(), gson.toJson(msg19).getBytes());
        }
    }

    /**
     * 发送单个cmd19消息
     *
     * @param currentMode
     * @param softWindow
     */
    @Deprecated
    public static void sendCmd19Msg(int currentMode, SoftWindow softWindow) {
        List<Msg19.ProjectionStatus> statusList = new ArrayList<>();
        TaskModel taskModel = softWindow.getTaskModel();
        PlayerView playerView = softWindow.getPlayerView();

        Msg19.ProjectionStatus status = new Msg19.ProjectionStatus();
        status.setWidth(getResolution(playerView.getWidth()));
        status.setHeight(getResolution(playerView.getHeight()));
        status.setDeviceName(taskModel.targetName);
        status.setTaskId(taskModel.taskId);
        status.setFullScreen(currentMode);
        statusList.add(status);

        Msg19 msg19 = new Msg19();
        msg19.setCurrentMode(currentMode);
        msg19.setCmd(19);
        msg19.setList(statusList);

//        Log.d(TAG, "sendCmd19Msg: playerViewLayoutParams.width = " + playerView.getWidth());
//        Log.d(TAG, "sendCmd19Msg: playerViewLayoutParams.height = " + playerView.getHeight());
//        Log.d(TAG, "sendCmd19Msg: softWindow.getType() = " + softWindow.getType());
//        Log.d(TAG, "sendCmd19Msg: taskModel.targetName = " + taskModel.targetName);
//        Log.d(TAG, "sendCmd19Msg: softWindow.getWindowStatus() = " + softWindow.getWindowStatus());
//        Log.d(TAG, "sendCmd19Msg: ================================================================");
//        LogUtil.e("ShanLian222", "send cmd 19 window taskId = " + softWindow.getTaskModel().taskId);
        ProjectionSDK.getInstance().sendCMDMessage(softWindow.getDev_id(), new Gson().toJson(msg19).getBytes());
    }

    private static int getResolution(int resolution) {
        int i = (resolution % 8);
        return resolution - i;
    }

}

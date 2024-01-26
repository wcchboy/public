package com.wcch.android.window;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.igrs.sml.PlayerView;
import com.igrs.sml.TaskModel;


/**
 * @author created by Lzq
 * @time：2022/3/16
 * @Des：
 */
public class SoftWindow extends Window {
    private TaskModel taskModel;
    private String dev_id;
    private PlayerView playerView;

//    private RelativeLayout floatWindowView;
    private ImageView scaleView;
    private ImageView ivClose;
    private ImageView ivFullScreen;
    private ImageView ivScreenShot;
    private String deviceName;


    private Button btnZj,btnJs;
    private TextView r;


    public Button getBtnZj() {
        return btnZj;
    }

    public void setBtnZj(Button btnZj) {
        this.btnZj = btnZj;
    }

    public Button getBtnJs() {
        return btnJs;
    }

    public void setBtnJs(Button btnJs) {
        this.btnJs = btnJs;
    }

    public TextView getR() {
        return r;
    }

    public void setR(TextView r) {
        this.r = r;
    }

    public SoftWindow() {
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public SoftWindow(TaskModel taskModel) {
        this.taskModel = taskModel;
    }

    public TaskModel getTaskModel() {
        return taskModel;
    }

    public void setTaskModel(TaskModel taskModel) {
        this.taskModel = taskModel;
    }

    public String getDev_id() {
        return dev_id;
    }

    public void setDev_id(String dev_id) {
        this.dev_id = dev_id;
    }

    public PlayerView getPlayerView() {
        return playerView;
    }

    public void setPlayerView(PlayerView playerView) {
        this.playerView = playerView;
    }

//    public RelativeLayout getFloatWindowView() {
//        return floatWindowView;
//    }
//
//    public void setFloatWindowView(RelativeLayout floatWindowView) {
//        this.floatWindowView = floatWindowView;
//    }

    public ImageView getScaleView() {
        return scaleView;
    }

    public void setScaleView(ImageView scaleView) {
        this.scaleView = scaleView;
    }

    public ImageView getIvClose() {
        return ivClose;
    }

    public void setIvClose(ImageView ivClose) {
        this.ivClose = ivClose;
    }

    public ImageView getIvFullScreen() {
        return ivFullScreen;
    }

    public void setIvFullScreen(ImageView ivFullScreen) {
        this.ivFullScreen = ivFullScreen;
    }

    public ImageView getIvScreenShot() {
        return ivScreenShot;
    }

    public void setIvScreenShot(ImageView ivScreenShot) {
        this.ivScreenShot = ivScreenShot;
    }
}

package com.wcch.android.bean;

/**
 * 01BetoTabletApp
 *
 * @author Created by RyanWang on 2023/1/30
 * Copyright © 2023年 IGRS. All rights reserved.
 * Describe:
 */
public class DisDeviceListModeBean extends IBean {
    private String mainIconIv;//主标识图标
    private String selectIconIv;//选中icon
    private String signalIv;//信号图标
    private String nameTv;//名称
    private String signalTv;//信号文字
    private String localTv;//本机文字

    public String getMainIconIv() {
        return mainIconIv;
    }

    public void setMainIconIv(String mainIconIv) {
        this.mainIconIv = mainIconIv;
    }

    public String getSelectIconIv() {
        return selectIconIv;
    }

    public void setSelectIconIv(String selectIconIv) {
        this.selectIconIv = selectIconIv;
    }

    public String getSignalIv() {
        return signalIv;
    }

    public void setSignalIv(String signalIv) {
        this.signalIv = signalIv;
    }

    public String getNameTv() {
        return nameTv;
    }

    public void setNameTv(String nameTv) {
        this.nameTv = nameTv;
    }

    public String getSignalTv() {
        return signalTv;
    }

    public void setSignalTv(String signalTv) {
        this.signalTv = signalTv;
    }

    public String getLocalTv() {
        return localTv;
    }

    public void setLocalTv(String localTv) {
        this.localTv = localTv;
    }
}

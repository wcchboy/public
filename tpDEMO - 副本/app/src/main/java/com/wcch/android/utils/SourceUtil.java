package com.wcch.android.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import com.igrs.betotablet.flavor.FlavorUtils;

/**
 * @author created by Lzq
 * @time：2022/5/27
 * @Des：获取信源通道工具类
 */
public class SourceUtil {


    /**
     * 检测当前是否为Android通道
     * @return
     */
   /* public static boolean isAndroidSource() {
        if (FlavorUtils.isT86Fct()) {
            return getAndroidSourceInTx();
        }
        if (FlavorUtils.isT86Hht()) {
            return getAndroidSourceInT86HHT();
        }
        if (FlavorUtils.isM2()||FlavorUtils.isS2()){
            return getAndroidSourceInM2S2();
        }
        return true;
    }

    public static String getCurrentSource() {
        if (FlavorUtils.isT86Fct()) {
            return getSourceInTx();
        }
        if (FlavorUtils.isT86Hht()) {
            return getSourceInT86HHT();
        }
        if (FlavorUtils.isS2()||FlavorUtils.isM2()) {
            return getSourceInM2S2();
        }
        return "ANDROID";
    }

    private static String getSourceInM2S2() {
        ImwSourceManager ism = (ImwSourceManager) App.getInstance().getSystemService(ImwSourceManager.SERVICE);
        return ism.getCurSource();
    }

    private static String getSourceInT86HHT() {
        String sourcename = HHTCommand.getInstance(App.getInstance()).getValueByCommand("current_source_name");
        if (TextUtils.isEmpty(sourcename)) {
            if (IsSourceManager.getInstance().isTvWindow()){
                sourcename = IsSourceManager.getInstance().getCurSourceChannel();
            }else {
                sourcename = "ANDROID";
            }
        }
        return sourcename;
    }

    private static String getSourceInTx() {
        @SuppressLint("WrongConstant") ImwSourceManager ism =
                (ImwSourceManager) App.getInstance().getSystemService(ImwSourceManager.SERVICE);
        return ism.getCurSource();
     //   return "";
    }

    private static boolean getAndroidSourceInM2S2() {
        @SuppressLint("WrongConstant") ImwSourceManager ism =
                (ImwSourceManager) App.getInstance().getSystemService(ImwSourceManager.SERVICE);
        String curSource = ism.getCurSource();
        if (TextUtils.equals("ANDROID",curSource)) {
            return true;
        }
        return false;
    }

    //富创T系列获取当前信源通道
    private static boolean getAndroidSourceInTx() {
        @SuppressLint("WrongConstant") ImwSourceManager ism =
                (ImwSourceManager) App.getInstance().getSystemService(ImwSourceManager.SERVICE);
        String curSource = ism.getCurSource();
        if (TextUtils.equals("ANDROID",curSource)) {
            return true;
        }
        return false;
    }
    //鸿合T86获取当前信源通道
    private static boolean getAndroidSourceInT86HHT() {
        String sourcename = HHTCommand.getInstance(App.getInstance()).getValueByCommand("current_source_name");
        if (TextUtils.isEmpty(sourcename)) {
            if (IsSourceManager.getInstance().isTvWindow()){
                sourcename = IsSourceManager.getInstance().getCurSourceChannel();
            }else {
                sourcename = "ANDROID";
            }
        }
        return TextUtils.equals("ANDROID",sourcename);
    }*/
}

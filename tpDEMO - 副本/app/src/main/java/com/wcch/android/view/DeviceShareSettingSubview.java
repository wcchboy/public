package com.wcch.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.igrs.betotablet.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 01BetoTabletApp
 *
 * @author Created by RyanWang on 2022/12/28
 * Copyright © 2022年 IGRS. All rights reserved.
 * Describe:
 */
public class DeviceShareSettingSubview extends ConstraintLayout {
    private ImageView device_setting_type_icon;
    private TextView device_setting_explain,device_setting_remarks;
    //private SlideButton device_setting_slide_btn;
    private Switch device_setting_slide_btn;
    private LinearLayout device_setting_slide_linear;
    //private List<SlideButton.SlideButtonOnCheckedListener> listenersList = new ArrayList<SlideButton.SlideButtonOnCheckedListener>();
    public DeviceShareSettingSubview(@NonNull Context context) {
        super(context);
    }

    public DeviceShareSettingSubview(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context,attrs);
    }

    public DeviceShareSettingSubview(@NonNull Context context, @Nullable AttributeSet attrs,
                                     int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initView(Context context,AttributeSet attrs){
        LayoutInflater.from(context).inflate(R.layout.device_share_settings_subview_layout, this,true);
        device_setting_type_icon = findViewById(R.id.device_setting_type_icon);
        device_setting_explain = findViewById(R.id.device_setting_explain);
        device_setting_remarks = findViewById(R.id.device_setting_remarks);
        device_setting_slide_btn = findViewById(R.id.device_setting_slide_btn);
        device_setting_slide_linear = findViewById(R.id.device_setting_slide_linear);
        //device_setting_slide_btn.setOnCheckedListener(new MySlideButtonOnCheckedListener());

        /**
         * 获取自定义属性
         */
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DeviceShareSettingSubview);
        Drawable typeIconDraw = typedArray.getDrawable(R.styleable.DeviceShareSettingSubview_iconImage);
        if (typeIconDraw!=null){
            device_setting_type_icon.setImageDrawable(typeIconDraw);
        }
        String explainText = typedArray.getString(R.styleable.DeviceShareSettingSubview_explainText);
        device_setting_explain.setText(explainText);

        String remarksText = typedArray.getString(R.styleable.DeviceShareSettingSubview_remarksText);
        device_setting_remarks.setText(remarksText);

        boolean isRemarksShow = typedArray.getBoolean(R.styleable.DeviceShareSettingSubview_remarksVisibility,false);
        device_setting_remarks.setVisibility(isRemarksShow?VISIBLE:GONE);

        boolean isShowPopUp = typedArray.getBoolean(R.styleable.DeviceShareSettingSubview_showPopUp,false);
        device_setting_slide_linear.setVisibility(isShowPopUp?VISIBLE:GONE);

        boolean popUpClickable = typedArray.getBoolean(R.styleable.DeviceShareSettingSubview_popUpClickable,false);
        device_setting_slide_linear.setClickable(popUpClickable);
    }

    public void setTypeIcon(int resid){
        device_setting_type_icon.setImageResource(resid);
    }
    public void setSettingExplainText(int resid){
        device_setting_explain.setText(resid);
    }
    public void setSettingRemarksText(int resid){
        device_setting_remarks.setText(resid);
    }

    /**
     *
     * @param show 是否显示改空间
     * @param isOnclick 是否可以点击，不可以点击置灰
     */
    public void setSettingSlideShow(boolean show,boolean isOnclick){
        device_setting_slide_linear.setVisibility(show?VISIBLE:GONE);
        device_setting_slide_linear.setClickable(isOnclick);
        //TODO 后面完善功能  //是否可以点击，不可以点击置灰
        if(isOnclick){

        }
    }
    /*public void registerSlideButtonOnCheckedListener(SlideButton.SlideButtonOnCheckedListener listener){
        listenersList.add(listener);
    }
    public void removeSlideButtonOnCheckedListener(SlideButton.SlideButtonOnCheckedListener listener){
        if (listenersList.contains(listener)){
            listenersList.remove(listener);
        }
    }*/
    /*class MySlideButtonOnCheckedListener implements SlideButton.SlideButtonOnCheckedListener{

        @Override
        public void onCheckedChangeListener(SlideButton slideButton, boolean isChecked) {
            for (SlideButton.SlideButtonOnCheckedListener listener : listenersList) {
                listener.onCheckedChangeListener(slideButton,isChecked);
            }
        }
    }*/

}

package com.wcch.android.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.igrs.betotablet.R;
import com.xujiaji.happybubble.BubbleDialog;

/**
 * 01BetoTabletApp
 *
 * @author Created by RyanWang on 2023/1/17
 * Copyright © 2023年 IGRS. All rights reserved.
 * Describe:
 *
 * lookAt  属性
 * left, top, right, bottom 值
 * 箭头指向 描述
 *
 *
 * lookLength
 * dimension
 * 箭头的长度
 *
 *
 * lookPosition
 * dimension
 * 箭头相对于x或y轴的位置
 *
 *
 * lookWidth
 * dimension
 * 箭头的宽度
 *
 *
 * bubbleColor
 * color
 * 气泡的颜色
 *
 *
 * bubbleRadius
 * dimension
 * 气泡四角的圆弧
 *
 *
 * bubblePadding
 * dimension
 * 气泡边缘到内容的距离
 *
 *
 * shadowRadius
 * dimension
 * 阴影的扩散大小
 *
 *
 * shadowX
 * dimension
 * 阴影在x轴方向的偏移
 *
 *
 * shadowY
 * dimension
 * 阴影在y轴方向的偏移
 *
 *
 * shadowColor
 * color
 * 阴影的颜色
 *
 * 作者：奏响曲
 * 链接：https://juejin.cn/post/6844903533989330958
 * 来源：稀土掘金
 * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
 */

/*PoroverCastSoundAndControlScreen codDialog = new PoroverCastSoundAndControlScreen(this)
        .setPosition(mPosition)
        .setClickedView(mButton10);
        codDialog.setClickListener(new PoroverCastSoundAndControlScreen.OnClickCustomButtonListener()
        {
@Override
public void onClick(String str)
        {
        mButton10.setText("点击了：" + str);
        }
        });
        codDialog.show();*/

public class PoroverCastSoundAndControlScreen extends BubbleDialog implements CompoundButton.OnCheckedChangeListener {
    private ViewHolder mViewHolder;
    private OnClickCustomButtonListener mListener;

    public PoroverCastSoundAndControlScreen(Context context)
    {
        super(context);
        calBar(true);
        setTransParentBackground();
        setPosition(Position.TOP);
        View rootView = LayoutInflater.from(context).inflate(R.layout.projection_screen_setting_layout, null);
        mViewHolder = new ViewHolder(rootView);
        addContentView(rootView);
        mViewHolder.ckConScreen.setOnCheckedChangeListener(this);
        mViewHolder.ckConScreen.setOnCheckedChangeListener(this);
    }

/*    @Override
    public void onClick(View v)
    {
        if (mListener != null)
        {
            mListener.onClick(((Button)v).getText().toString());
        }
    }*/

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton.getId()==R.id.projection_cast_second_ck){
            if (mListener != null)
            {
                mListener.onCheckBox(0,b);
            }
        }else if(compoundButton.getId()==R.id.projection_control_screen_ck){
            mListener.onCheckBox(1,b);
        }
    }

    private static class ViewHolder
    {
        CheckBox ckCastSencond, ckConScreen;
        public ViewHolder(View rootView)
        {
            ckCastSencond = rootView.findViewById(R.id.projection_cast_second_ck);
            ckConScreen = rootView.findViewById(R.id.projection_control_screen_ck);
        }
    }

    public void setClickListener(OnClickCustomButtonListener l)
    {
        this.mListener = l;
    }

    public interface OnClickCustomButtonListener
    {
        void onClick(String str);
        void onCheckBox(int id,boolean checked);
    }
}

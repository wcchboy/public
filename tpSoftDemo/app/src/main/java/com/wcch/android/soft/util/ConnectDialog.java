package com.wcch.android.soft.util;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.wcch.android.R;


public class ConnectDialog {
	Context context;
	androidx.appcompat.app.AlertDialog ad;
	TextView txt_state;
	TextView txt_msg;
	TextView cast_code_tv;
	ImageView img_loading;

	public ConnectDialog(Context context, String title, String castCode) {
		this.context = context;
		ad = new androidx.appcompat.app.AlertDialog.Builder(context, R.style.dialog).create();
		ad.show();
        ad.getWindow().setContentView(R.layout.dialog_connect);
		ad.setCancelable(false);


		Window window = ad.getWindow();
		txt_state=window.findViewById(R.id.txt_state);
		txt_msg=window.findViewById(R.id.txt_msg);
		cast_code_tv = window.findViewById(R.id.cast_code_tv);
		img_loading=window.findViewById(R.id.img_loading);

		txt_state.setText(title);
		cast_code_tv.setText(castCode);
		//字体文字找不到
		/*Typeface typeface = Typeface.createFromAsset(context.getAssets(), "AlbertSans-Bold.ttf");
		cast_code_tv.setTypeface(typeface);*/
		setAnimation(true);
	}

	public void setOnKeyListener( DialogInterface.OnKeyListener onKeyListener) {
		ad.setOnKeyListener(onKeyListener);
	}

	private ObjectAnimator animation;
	public void setAnimation(boolean start) {
		if (start) {
			if (animation == null) {
				animation = ObjectAnimator.ofFloat(img_loading, "rotation", 0f, 360f);
				animation.setDuration(1000);
				animation.setRepeatCount(ValueAnimator.INFINITE);
				animation.setInterpolator(new LinearInterpolator());
			}
			animation.start();
		} else {
			if (animation != null) animation.end();
		}
	}
	public void setMsg(String msg){
		if(TextUtils.isEmpty(msg)){
			txt_msg.setVisibility(View.GONE);
			txt_msg.setText("");
		}else{
			txt_msg.setVisibility(View.VISIBLE);
			txt_msg.setText(msg);
		}
	}
	/**
	 * 关闭对话框
	 */
	public void dismiss() {
		try{
			setAnimation(false);
			ad.dismiss();
		}catch (Exception e){
		}
	}

}
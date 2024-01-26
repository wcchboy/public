package com.wcch.android.utils;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.wcch.android.R;


public class AlertDialog {
	Context context;
	androidx.appcompat.app.AlertDialog ad;
	TextView titleView,confirmButton,cancelButton;

	public AlertDialog(Context context) {
		this.context = context;
		ad = new androidx.appcompat.app.AlertDialog.Builder(context, R.style.dialog).create();
		ad.show();
		Window window = ad.getWindow();
        ad.getWindow().setContentView(R.layout.alertdialog);
		titleView = (TextView) window.findViewById(R.id.d_title);
		cancelButton=(TextView)window.findViewById(R.id.cancelButton);
		confirmButton=(TextView)window.findViewById(R.id.confirmButton);
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				dismiss();
			}
		});
	}

	public void setTitle(int resId) {
		titleView.setText(resId);
	}

	public void setTitle(String title) {
		titleView.setText(title);
	}
	public void setCancelable(boolean flag){
		if(ad!=null)ad.setCancelable(flag);
	}

	public void setCancelButton(String text,
			final OnClickListener listener) {
		cancelButton.setVisibility(View.VISIBLE);
		cancelButton.setText(text);
		cancelButton.setOnClickListener(listener);
	}
	/**
	 * 设置按钮
	 * 
	 * @param text
	 * @param listener
	 */
	public void setConfirmButton_red(String text,
			final OnClickListener listener) {
		confirmButton.setVisibility(View.VISIBLE);
		confirmButton.setText(text);
		confirmButton.setTextColor(0xffff2a21);
		confirmButton.setOnClickListener(listener);
	}
	/**
	 * 设置按钮
	 *
	 * @param text
	 * @param listener
	 */
	public void setConfirmButton(String text,
								 final OnClickListener listener) {
		confirmButton.setVisibility(View.VISIBLE);
		confirmButton.setText(text);
		confirmButton.setTextColor(0xff158cff);
		confirmButton.setOnClickListener(listener);
	}
	/**
	 * 设置按钮
	 *
	 * @param text
	 * @param listener
	 */
	public void set1ConfirmButton(String text,
								 final OnClickListener listener) {
		confirmButton.setVisibility(View.VISIBLE);
		confirmButton.setText(text);
		confirmButton.setTextColor(0xff158cff);
		confirmButton.setOnClickListener(listener);
	}
	/**
	 * 关闭对话框
	 */
	public void dismiss() {
		ad.dismiss();
	}

}
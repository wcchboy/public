package com.wcch.android.soft.util;

import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.wcch.android.R;


public class Dialog_activation {
	Context context;
	androidx.appcompat.app.AlertDialog ad;
	TextView txt_subtitle;
	TextView confirmButton;

	public Dialog_activation(Context context, String title) {
		this.context = context;
		ad = new androidx.appcompat.app.AlertDialog.Builder(context, R.style.dialog).create();
		ad.show();
        ad.getWindow().setContentView(R.layout.dialog_activation);
		ad.setCancelable(false);

		Window window = ad.getWindow();
		txt_subtitle=window.findViewById(R.id.txt_subtitle);
		txt_subtitle.setText(title);
		confirmButton = window.findViewById(R.id.confirmButton);
		confirmButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dismiss();
			}
		});
	}
	/**
	 * 设置按钮
	 *
	 * @param text
	 * @param listener
	 */
	public void setConfirmButton(String text,
								 final View.OnClickListener listener) {
		confirmButton.setVisibility(View.VISIBLE);
		confirmButton.setText(text);
		confirmButton.setOnClickListener(listener);
	}

	/**
	 * 关闭对话框
	 */
	public void dismiss() {
		try{
			ad.dismiss();
		}catch (Exception e){
		}
	}

}
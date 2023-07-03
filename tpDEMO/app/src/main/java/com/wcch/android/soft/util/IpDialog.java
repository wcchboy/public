package com.wcch.android.soft.util;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.igrs.betotablet.R;
import com.igrs.betotablet.TabletApp;
import com.igrs.betotablet.soft.entity.Device;
import com.igrs.betotablet.soft.util.IgrsToast;
import com.igrs.sml.util.BaseUtil;


public class IpDialog {
    Context context;
    androidx.appcompat.app.AlertDialog ad;
    EditText ipEt;
    ImageView castCodeClear;
    Button cancelBtn;
    Button connectBtn;
    IpDialogListener dialogListener;

    public IpDialog(Context context) {
        this.context = context;
        ad = new androidx.appcompat.app.AlertDialog.Builder(context, R.style.dialog).create();
        ad.show();
        ad.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        ad.getWindow().setContentView(R.layout.dialog_ip);
        ad.setCancelable(false);

        Window window = ad.getWindow();
        ipEt = window.findViewById(R.id.ip_et);
        ipEt.postDelayed(() -> {
            ipEt.requestFocus();
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }, 100);
        ipEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    connectBtn.setEnabled(BaseUtil.isCorrectIp(s.toString()));
                    castCodeClear.setVisibility(View.VISIBLE);
                } else {
                    castCodeClear.setVisibility(View.GONE);
                    connectBtn.setEnabled(false);
                }
            }
        });

        castCodeClear = window.findViewById(R.id.cast_code_clear);
        cancelBtn = window.findViewById(R.id.cancel_btn);
        connectBtn = window.findViewById(R.id.connect_btn);
        connectBtn.setEnabled(false);

        castCodeClear.setOnClickListener((v) -> {
            ipEt.setText("");
        });
        cancelBtn.setOnClickListener(arg0 -> {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            dismiss();
        });
        connectBtn.setOnClickListener(arg0 -> {
            String input = ipEt.getText().toString();
            if (input.equals("") || !BaseUtil.isCorrectIp(input)) {
                IgrsToast.getInstance().showToast_error(TabletApp.Companion.getApplication(), "IP not correct!", 3000);
            } else {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                Device d1 = new Device();
                d1.room_name = input;
                d1.wlan_ip = input;
                d1.device_type = 1;
                d1.device_mac = "ignore";
                if (dialogListener != null) {
                    dialogListener.connect(d1);
                }
                dismiss();
            }
        });
    }

    public void setOnConnectListener(IpDialogListener listener) {
        dialogListener = listener;
    }

    /**
     * 关闭对话框
     */
    public void dismiss() {
        try {
            ad.dismiss();
        } catch (Exception e) {
        }
    }

    public interface IpDialogListener {
        void connect(Device device);
    }
}
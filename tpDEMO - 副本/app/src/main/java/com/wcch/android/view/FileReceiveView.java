package com.wcch.android.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.igrs.betotablet.R;
import com.igrs.betotablet.flavor.FlavorUtils;
import com.igrs.betotablet.utils.OpenFileUtil;

import java.io.File;

/**
 * 文件传输
 */
public class FileReceiveView implements View.OnClickListener {

    private String TAG = "FloatingView";
    private static FileReceiveView fileReceiveView;
    private Context mContext;
    private WindowManager manager;
    private WindowManager.LayoutParams params;
    private LinearLayout view;
    private boolean isShowing = false;
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private String mFilePath;

    private FileReceiveView() {
    }

    public static FileReceiveView getInstance() {
        if (fileReceiveView == null) {
            synchronized (FileReceiveView.class) {
                fileReceiveView = new FileReceiveView();
            }
        }
        return fileReceiveView;
    }

    public void init(Context context) {
        this.mContext = context;
        if (manager == null) {
            manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            params = new WindowManager.LayoutParams();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            }
            int flags = WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            // 如果设置了WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE，弹出的View收不到Back键的事件
            params.flags = flags;
            // 不设置这个弹出框的透明遮罩显示为黑色
            params.format = PixelFormat.TRANSLUCENT;
            params.gravity = Gravity.START|Gravity.BOTTOM;
            if (FlavorUtils.isPublic()) {
                params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 381, context.getResources().getDisplayMetrics());
                params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 57, context.getResources().getDisplayMetrics());
                params.x = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 449, context.getResources().getDisplayMetrics());
                params.y = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 99, context.getResources().getDisplayMetrics());
            } else {
                params.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 459, context.getResources().getDisplayMetrics());
                params.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 72, context.getResources().getDisplayMetrics());
                params.x = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 411, context.getResources().getDisplayMetrics());
                params.y = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 11, context.getResources().getDisplayMetrics());
            }
        }
    }

    public void show(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            ToastUtils.getInstance().showToast(ToastUtils.ToastType.ERROR, mContext.getString(R.string.file_not_exit));
        }
        mFilePath = filePath;
        if (view == null) {
            initView();
        }

        tvFileName.setText(file.getName());

        if (isShowing) {
            mHandler.removeCallbacks(removeRunnable);
            mHandler.post(removeRunnable);
        }
        isShowing = true;
        mHandler.post(addRunnable);
        mHandler.postDelayed(removeRunnable, 5000);
    }

    Runnable removeRunnable = new Runnable() {
        @Override
        public void run() {
            manager.removeViewImmediate(view);
            isShowing = false;
        }
    };

    Runnable addRunnable = new Runnable() {
        @Override
        public void run() {
            ViewParent parent = view.getParent();
            if (parent == null) {
                manager.addView(view, params);
            }

        }
    };

    TextView tvFileName, tvOpenDir, tvOpenFile;
    View rlClose;

    private void initView() {
        if (view == null) {
            view = (LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.view_file_receive, null);
            tvFileName = view.findViewById(R.id.tv_file_name);
            tvOpenDir = view.findViewById(R.id.tv_open_dir);
            tvOpenFile = view.findViewById(R.id.tv_open_file);
            rlClose = view.findViewById(R.id.rl_close);

            rlClose.setOnClickListener(this);
            tvOpenDir.setOnClickListener(this);
            tvOpenFile.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_open_dir:
                OpenFileUtil.openIgrsFileManager(mContext, mFilePath);
                remove();
                break;
            case R.id.tv_open_file:
                Intent intent = OpenFileUtil.openFile(mContext, mFilePath);
                if (intent == null) {
                    ToastUtils.getInstance().showToast(ToastUtils.ToastType.ERROR, mContext.getString(R.string.file_not_exit));
                } else {
                    boolean hasWPF = intent.getBooleanExtra("hasWPF", true);
                    if (hasWPF) {
                        mContext.startActivity(intent);
                    }
                }
                remove();
                break;
            case R.id.rl_close:
                remove();
                break;
        }
    }

    private void remove() {
        mHandler.removeCallbacks(removeRunnable);
        mHandler.post(removeRunnable);
    }
}

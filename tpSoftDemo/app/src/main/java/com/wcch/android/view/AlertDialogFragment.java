package com.wcch.android.view;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.wcch.android.R;


/**
 * Description: 消息提醒框
 */
public class AlertDialogFragment extends DialogFragment implements View.OnClickListener {
    

	private static final String TAG = AlertDialogFragment.class.getSimpleName();
    private TextView tv_text, tv_alert_second_content;
    private String mText, mSecondText;
    private TextView bt_left, bt_right;
    private String leftButtonText;
    private String rightButtonText;
    private OnClickListener leftListener;
    private OnClickListener rightListener;
    private boolean isCanceledOnTouchOutside = false;//在触摸外部时被取消
    private View mView;
	private View viewDivider;
	private ViewGroup fl_content;
	private View mContentView;
	private float mWidthScale = 0.93f;
    private int leftBackground = 0;
    private int leftTextColorId = 0;

    public AlertDialogFragment() {

	}

	@Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setCanceledOnTouchOutside(isCanceledOnTouchOutside);
        mView = inflater.inflate(R.layout.dialog_alert, container, false);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initView();
        initData();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void initView() {
    	setupWindow();
    	fl_content = findViewById(R.id.fl_content);
        tv_text = findViewById(R.id.tv_alert_content);
        tv_alert_second_content = findViewById(R.id.tv_alert_second_content);
        bt_left = findViewById(R.id.bt_left);
        bt_right = findViewById(R.id.bt_right);
        viewDivider = findViewById(R.id.view_divider);
    }

    private void setupWindow() {
    	int screenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
        lp.width = (int) (screenWidth * mWidthScale) ;
        getDialog().getWindow().setAttributes(lp);
    }

    protected <V extends View> V findViewById(int id) {
        return (V)mView.findViewById(id);
    }

    private void initData() {
    	setupContentView();
        setTextView();
        setSecondTextView();
        setButtonDefault();
        setLeftButton();
        setRightButton();
        setLeftButtonUI();
        setLeftListener();
        setRightListener();
    }

    private void setButtonDefault() {
        if(leftButtonText == null) {
            leftButtonText = getString(R.string.cancel);
        }
        if(rightButtonText == null) {
            rightButtonText = getString(R.string.sure);
        }
    }
    
    public AlertDialogFragment setContentView(View view) {
		mContentView = view;
		setupContentView();
    	return this;
    }

    private void setupContentView() {
    	if(mContentView != null && fl_content != null) {
    		fl_content.removeAllViews();
    		fl_content.addView(mContentView);
    	}
	}

	public AlertDialogFragment setText(String text){
        mText = text;
        setTextView();
        return this;
    }

    public AlertDialogFragment setSecondText(String text){
        mSecondText = text;
        setSecondTextView();
        return this;
    }

    public AlertDialogFragment setLeft(String text){
        leftButtonText = text;
        setLeftButton();
        return this;
    }

    public AlertDialogFragment setLeft(int id){
    	return setLeft(getString(id));
    }

    public AlertDialogFragment setRight(int id){
    	return setRight(getString(id));
    }

    public AlertDialogFragment setRight(String text){
        rightButtonText = text;
        setRightButton();
        return this;
    }

    private void setLeftButton(int leftBackground, int leftTextColorId) {
        this.leftBackground = leftBackground;
        this.leftTextColorId = leftTextColorId;
        setLeftButtonUI();
    }

    private void setLeftButtonUI() {
        if(bt_left != null) {
            if (leftBackground != 0) {
                bt_left.setBackgroundResource(leftBackground);
            }
            if (leftTextColorId != 0) {
                int color;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    color = getContext().getResources().getColor(leftTextColorId, getContext().getTheme());
                } else {
                    color = getContext().getResources().getColor(leftTextColorId);
                }
                bt_left.setTextColor(color);
            }
        }
    }

    /**
     * @param listener 左边按钮监听器
     */
    public AlertDialogFragment setLeftListener(OnClickListener listener){
        leftListener = listener;
        setLeftListener();
        return this;
    }

    private void setLeftListener() {
        if(bt_left != null) {
            bt_left.setOnClickListener(this);
        }
    }

    /**
     * @param listener 右边按钮监听器
     * @return
     */
    public AlertDialogFragment setRightListener(OnClickListener listener){
        rightListener = listener;
        setRightListener();
        return this;
    }

    private void setRightListener() {
        if(bt_right != null) {
            bt_right.setOnClickListener(this);
        }
    }

    private void setTextView() {
        if(tv_text != null) {
            tv_text.setText(mText);
            if(TextUtils.isEmpty(mText)) {
                tv_text.setVisibility(View.GONE);
            } else {
                tv_text.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setSecondTextView() {
        if(tv_alert_second_content != null) {
            tv_alert_second_content.setText(mSecondText);
            if(TextUtils.isEmpty(mSecondText)) {
                tv_alert_second_content.setVisibility(View.GONE);
            } else {
                tv_alert_second_content.setVisibility(View.VISIBLE);
            }
        }
    }

    private void setLeftButton() {
        if(bt_left != null) {
            bt_left.setText(leftButtonText);
            if(TextUtils.isEmpty(leftButtonText)) {
                bt_left.setVisibility(View.GONE);
            } else {
                bt_left.setVisibility(View.VISIBLE);
            }
            setDivider();
        }
    }

    /**
     * 设置按钮间隔
     */
    private void setDivider() {
		boolean showDivider = bt_left.getVisibility() != View.GONE && bt_right.getVisibility() != View.GONE;
		viewDivider.setVisibility(showDivider ? View.VISIBLE : View.GONE);
	}

	private void setRightButton() {
        if(bt_right != null) {
            bt_right.setText(rightButtonText);
            if(TextUtils.isEmpty(rightButtonText)) {
                bt_right.setVisibility(View.GONE);
            } else {
                bt_right.setVisibility(View.VISIBLE);
            }
            setDivider();
        }
    }

    public AlertDialogFragment setCanceledOnTouchOutside(boolean canceledOnTouchOutside) {
        isCanceledOnTouchOutside = canceledOnTouchOutside;
        if (getDialog() != null) {
            getDialog().setCanceledOnTouchOutside(canceledOnTouchOutside);
        }
        return this;
    }

    public void show(FragmentManager manager) {
        showDialog(manager);
    }

    void showDialog(FragmentManager manager) {
        try {
            // DialogFragment.show() will take care of adding the fragment
            // in a transaction.  We also want to remove any currently showing
            // dialog, so make our own transaction and take care of that here.
            FragmentTransaction ft = manager.beginTransaction();
            Fragment prev = manager.findFragmentByTag(TAG);
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            super.show(ft, TAG);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dismiss() {
        dismissAllowingStateLoss();
    }

    @Override
    public void onClick(View v) {
        if (v == bt_right) {
            if (rightListener != null) {
                rightListener.onClick(getDialog(), DialogInterface.BUTTON_POSITIVE);
            } else {
                dismiss();
            }
        } else if (v == bt_left) {
            if (leftListener != null) {
                leftListener.onClick(getDialog(), DialogInterface.BUTTON_NEGATIVE);
            } else {
                dismiss();
            }
        }
    }

    public static class Builder {
        private final Context mContext;
        private String left;
    	private OnClickListener leftListener;
		private String title;
		private String right;
		private String content;
		private OnClickListener rightListener;
		private View mContentView;
        private int leftBackground;
        private int leftTextColorId;
        private boolean canceledOnTouchOutside;//触摸外部可关闭
        private Boolean mCancelable = null;//返回可关闭

        public Builder(Context context) {
    		mContext = context;
    	}
		
		public Builder setLeftListener(OnClickListener listener) {
			this.leftListener = listener;
			return this;
		}

		public Builder setRightListener(OnClickListener listener) {
			this.rightListener = listener;
			return this;
		}
		
		public Builder setRight(String text) {
			this.right = text;
			return this;
		}

		public Builder setRight(int resId) {
			return setRight(mContext.getString(resId));
		}
		
		public Builder setLeft(String text) {
            this.left = text;
            return this;
        }

        public Builder setLeftBackground(int resId, int textColorId) {
            this.leftBackground = resId;
            this.leftTextColorId = textColorId;
            return this;
        }
		
		public Builder setLeft(int resId) {
			return setLeft(mContext.getString(resId));
		}

		public Builder setTitle(int resId) {
			return setTitle(mContext.getString(resId));
		}
		
		public Builder setTitle(String text) {
			this.title = text;
			return this;
		}
		
		public Builder setContent(int resId) {
			return setContent(mContext.getString(resId));
		}
		
		public Builder setContent(String text) {
			this.content = text;
			return this;
		}
		
		public Builder setContentView(View view) {
			this.mContentView = view;
			return this;
		}
		
		public Builder setCanceledOnTouchOutside(boolean b) {
			canceledOnTouchOutside = b;
			return this;
		}

        public Builder setCancelable(boolean b) {
            mCancelable = b;
            return this;
        }
		
		public AlertDialogFragment create() {
			AlertDialogFragment dialogFragment = new AlertDialogFragment();
			dialogFragment.setText(title);
			dialogFragment.setSecondText(content);
			dialogFragment.setLeft(left);
			dialogFragment.setRight(right);
            dialogFragment.setLeftButton(leftBackground, leftTextColorId);
			dialogFragment.setLeftListener(leftListener);
			dialogFragment.setRightListener(rightListener);
			dialogFragment.setContentView(mContentView);
			dialogFragment.setCanceledOnTouchOutside(canceledOnTouchOutside);
			if (mCancelable != null) {
                dialogFragment.setCancelable(mCancelable);
            }
			return dialogFragment;
		}

		public AlertDialogFragment show(FragmentManager fragmentManager) {
            AlertDialogFragment dialogFragment = create();
            dialogFragment.show(fragmentManager);
            return dialogFragment;
        }
	}


}

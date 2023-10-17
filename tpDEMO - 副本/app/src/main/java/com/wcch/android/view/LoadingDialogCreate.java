package com.wcch.android.view;

public class LoadingDialogCreate {
    /**
     * 得到自定义的progressDialog
     *
     * @param context
     * @param msg
     * @return
     */
  /*  public static Dialog createLoadingDialog(Context context, int msg) {
        return createLoadingDialog(context, context.getString(msg));
    }*/

    /*public static Dialog createLoadingDialog(Context context, String msg) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.loadingdialog, null);// 得到加载view
        LinearLayout layout = v.findViewById(R.id.dialog_view);// 加载布局
        // main.xml中的ImageView
        ImageView spaceshipImage = v.findViewById(R.id.img);
        TextView tipTextView = v.findViewById(R.id.tipTextView);// 提示文字
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                context, R.anim.loading_animation);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        if (TextUtils.isEmpty(msg)) {
            tipTextView.setVisibility(View.GONE);
        } else {
            tipTextView.setText(msg);// 设置加载信息
        }
        Dialog loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog

        loadingDialog.setCancelable(false);// 不可以用“返回键”取消
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
        return loadingDialog;
    }*/


    public final static int BT_OK = 0;
    public final static int BT_CANCEL = 1;

    /**
         * @param context   上下文
         * @param titleStr  标题
         * @param msg       内容
         * @param cancelTxt 取消按钮
         * @param okTxt     确定按钮
         * @param flag      是否点击空白取消对话框 true,可以，false,不可以
         * @param listener  回调监听
         * @return
         */
        /*public static Dialog createDialog(Context context, String titleStr, String msg, String cancelTxt, String okTxt, boolean flag, final Dialog.OnClickListener listener) {

            LayoutInflater inflater = LayoutInflater.from(context);
            View v = inflater.inflate(R.layout.custom_dialog, null);// 得到加载view
            TextView tvTitle = v.findViewById(R.id.tiptitle);
            if (!TextUtils.isEmpty(titleStr)) {
                tvTitle.setText(titleStr);
                tvTitle.setVisibility(View.VISIBLE);
            } else {
                tvTitle.setVisibility(View.GONE);
            }
            TextView tvTip = v.findViewById(R.id.tipTextView);// 提示文字
            if (!TextUtils.isEmpty(msg)) {
                tvTip.setText(msg);// 设置加载信息
                tvTip.setVisibility(View.VISIBLE);
            } else {
                tvTip.setVisibility(View.GONE);
            }
            final Dialog dialog = new Dialog(context, R.style.cus_dialog);// 创建自定义样式dialog
            TextView cancel = v.findViewById(R.id.bt_cancel);
            if (null != cancelTxt) {
                cancel.setText(cancelTxt);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (listener != null) {
                            listener.onClick(dialog, BT_CANCEL);
                        }


                    }
                });
            } else {
                cancel.setVisibility(View.GONE);
            }

            TextView ok = v.findViewById(R.id.bt_ok);
            if (null != okTxt) {
                ok.setText(okTxt);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (listener != null) {
                            listener.onClick(dialog, BT_OK);
                        }


                    }
                });
            } else {
                ok.setVisibility(View.GONE);
            }

            dialog.setCanceledOnTouchOutside(flag);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            dialog.setContentView(v, layoutParams);// 设置布局
            Window dialogWindow = dialog.getWindow();
            if (dialogWindow != null) {
                dialogWindow.setGravity(Gravity.CENTER);
            }

            return dialog;
        }*/

        /*public static void startAnim(Dialog view, Context context) {
            final ImageView spaceshipImage = view.findViewById(R.id.img);

            // 加载动画
            final Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
                    context, R.anim.loading_animation);
            // 使用ImageView显示动画
            spaceshipImage.startAnimation(hyperspaceJumpAnimation);
    }*/

    /**
     * @param context  上下文
     * @param titleStr 标题
     * @param flag     是否点击空白取消对话框 true,可以，false,不可以
     * @param listener 回调监听
     * @return
     */
    /*public static Dialog createDialog(Context context, String titleStr, boolean flag, final Dialog.OnClickListener listener) {
        return createDialog(context, titleStr, "", context.getString(R.string.no), context.getString(R.string.yes), flag, listener);
    }*/
}

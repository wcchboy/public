package com.wcch.android.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.wcch.android.R;
import com.wcch.android.utils.LogUtils;

import androidx.annotation.Nullable;

/**
 * Created by RyanWang on 2023/12/7.
 *
 * @Description:
 */
public class YaoKongQiActivity extends Activity {
    com.wcch.android.databinding.YaokongqiLayoutBinding binding;
    private String TAG = "YaoKongQiActivity";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = com.wcch.android.databinding.YaokongqiLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.lin1.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        binding.lin2.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        binding.lin3.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        binding.lin4.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        //binding.lin1.getViewTreeObserver().addOnGlobalFocusChangeListener(new onGlobalFocusChangeListener());
        //binding.lin1.setOnFocusChangeListener(new onFocusChangeListener());
        //binding.lin2.getViewTreeObserver().addOnGlobalFocusChangeListener(new onGlobalFocusChangeListener());
        //binding.lin2.setOnFocusChangeListener(new onFocusChangeListener());
        //binding.btn1.getViewTreeObserver().addOnGlobalFocusChangeListener(new onGlobalFocusChangeListener());
        binding.btn1.setOnFocusChangeListener(new onFocusChangeListener());
        // binding.btn2.getViewTreeObserver().addOnGlobalFocusChangeListener(new onGlobalFocusChangeListener());
        binding.btn2.setOnFocusChangeListener(new onFocusChangeListener());
        // binding.btn3.getViewTreeObserver().addOnGlobalFocusChangeListener(new onGlobalFocusChangeListener());
        binding.btn3.setOnFocusChangeListener(new onFocusChangeListener());
        //binding.btn4.getViewTreeObserver().addOnGlobalFocusChangeListener(new onGlobalFocusChangeListener());
        binding.btn4.setOnFocusChangeListener(new onFocusChangeListener());

        //binding.btn5.getViewTreeObserver().addOnGlobalFocusChangeListener(new onGlobalFocusChangeListener());
        binding.btn5.setOnFocusChangeListener(new onFocusChangeListener());
        //binding.btn6.getViewTreeObserver().addOnGlobalFocusChangeListener(new onGlobalFocusChangeListener());
        binding.btn6.setOnFocusChangeListener(new onFocusChangeListener());
        //binding.btn7.getViewTreeObserver().addOnGlobalFocusChangeListener(new onGlobalFocusChangeListener());
        binding.btn7.setOnFocusChangeListener(new onFocusChangeListener());
        //binding.btn8.getViewTreeObserver().addOnGlobalFocusChangeListener(new onGlobalFocusChangeListener());
        binding.btn8.setOnFocusChangeListener(new onFocusChangeListener());


        //binding.btn9.getViewTreeObserver().addOnGlobalFocusChangeListener(new onGlobalFocusChangeListener());
        binding.btn9.setOnFocusChangeListener(new onFocusChangeListener());
        //binding.btn10.getViewTreeObserver().addOnGlobalFocusChangeListener(new onGlobalFocusChangeListener());
        binding.btn10.setOnFocusChangeListener(new onFocusChangeListener());
        //binding.btn11.getViewTreeObserver().addOnGlobalFocusChangeListener(new onGlobalFocusChangeListener());
        binding.btn11.setOnFocusChangeListener(new onFocusChangeListener());
        //binding.btn12.getViewTreeObserver().addOnGlobalFocusChangeListener(new onGlobalFocusChangeListener());
        binding.btn12.setOnFocusChangeListener(new onFocusChangeListener());



        //binding.btn13.getViewTreeObserver().addOnGlobalFocusChangeListener(new onGlobalFocusChangeListener());
        binding.btn13.setOnFocusChangeListener(new onFocusChangeListener());
        //binding.btn14.getViewTreeObserver().addOnGlobalFocusChangeListener(new onGlobalFocusChangeListener());
        binding.btn14.setOnFocusChangeListener(new onFocusChangeListener());
        //binding.btn15.getViewTreeObserver().addOnGlobalFocusChangeListener(new onGlobalFocusChangeListener());
        binding.btn15.setOnFocusChangeListener(new onFocusChangeListener());
        //binding.btn16.getViewTreeObserver().addOnGlobalFocusChangeListener(new onGlobalFocusChangeListener());
        binding.btn16.setOnFocusChangeListener(new onFocusChangeListener());

//        binding.btn1.setFocusable(false);
//        binding.btn2.setFocusable(false);
//        binding.btn5.setFocusable(false);
//        binding.btn6.setFocusable(false);
//        binding.btn3.setFocusable(false);
//        binding.btn4.setFocusable(false);
//        binding.btn7.setFocusable(false);
//        binding.btn8.setFocusable(false);
        binding.lin1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                LogUtils.d(TAG,"onKeyDown lin1 onFocusChange hasFocus:"+hasFocus);
                if (hasFocus) {
//                    // 当 lin1 获取焦点时，设置 lin1 的子项可获取焦点
//                    binding.btn1.setFocusable(true);
//                    binding.btn2.setFocusable(true);
//                    binding.btn5.setFocusable(true);
//                    binding.btn6.setFocusable(true);
                    binding.lin1.setBackground(getResources().getDrawable(R.drawable.btn_bg_dialog_continue));
                } else {
//                    // 当 lin1 失去焦点时，设置 lin1 的子项不可获取焦点
//                    binding.btn1.setFocusable(false);
//                    binding.btn2.setFocusable(false);
//                    binding.btn5.setFocusable(false);
//                    binding.btn6.setFocusable(false);
                    binding.lin1.setBackground(getResources().getDrawable(R.color.transparent));
                }
            }
        });

        binding.lin2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                LogUtils.d(TAG,"onKeyDown lin2 onFocusChange hasFocus:"+hasFocus);
                if (hasFocus) {
//                    // 当 lin1 获取焦点时，设置 lin1 的子项可获取焦点
//                    binding.btn3.setFocusable(true);
//                    binding.btn4.setFocusable(true);
//                    binding.btn7.setFocusable(true);
//                    binding.btn8.setFocusable(true);
                    binding.lin2.setBackground(getResources().getDrawable(R.drawable.btn_bg_dialog_continue));
                } else {
//                    // 当 lin1 失去焦点时，设置 lin1 的子项不可获取焦点
//                    binding.btn3.setFocusable(false);
//                    binding.btn4.setFocusable(false);
//                    binding.btn7.setFocusable(false);
//                    binding.btn8.setFocusable(false);
                    binding.lin2.setBackground(getResources().getDrawable(R.color.transparent));
                }
            }
        });


        binding.lin3.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                LogUtils.d(TAG,"onKeyDown lin3 onFocusChange hasFocus:"+hasFocus);
                if (hasFocus) {

                    binding.lin3.setBackground(getResources().getDrawable(R.drawable.btn_bg_dialog_continue));
                } else {

                    binding.lin3.setBackground(getResources().getDrawable(R.color.transparent));
                }
            }
        });


        binding.lin4.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                LogUtils.d(TAG,"onKeyDown lin4 onFocusChange hasFocus:"+hasFocus);
                if (hasFocus) {

                    binding.lin4.setBackground(getResources().getDrawable(R.drawable.btn_bg_dialog_continue));
                } else {
                     binding.lin4.setBackground(getResources().getDrawable(R.color.transparent));
                }
            }
        });
    }


    private class onFocusChangeListener  implements View.OnFocusChangeListener{
        @Override
        public void onFocusChange(View view, boolean hasFocus)
        {
            LogUtils.d(TAG,"onFocusChange b："+hasFocus);
            if (hasFocus){
                view.setBackgroundColor(Color.GREEN);

            }else{
                //view.setBackground(getResources().getDrawable(R.drawable.shape_oval_bg_grey));
                //view.setBackgroundColor(Color.GRAY);
                view.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }
    private class onGlobalFocusChangeListener implements ViewTreeObserver.OnGlobalFocusChangeListener{

        @Override
        public void onGlobalFocusChanged(View view, View view1)
        {
            LogUtils.d(TAG,"onGlobalFocusChanged");
            if (view !=null){
                LogUtils.d(TAG,"onGlobalFocusChanged view !=null");
                view.setBackgroundColor(Color.BLUE);
            }else{
                LogUtils.d(TAG,"onGlobalFocusChanged view =null");
            }
            if (view1 !=null){
                LogUtils.d(TAG,"onGlobalFocusChanged view1 !=null");
                view1.setBackgroundColor(Color.YELLOW);
            }else{
                LogUtils.d(TAG,"onGlobalFocusChanged view1 =null");
            }

        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event)
    {
        LogUtils.d(TAG,"dispatchKeyEvent：");
        final int keyCode = event.getKeyCode();
        final int action = event.getAction();
        LogUtils.d(TAG,"keyCode："+keyCode);//按下确认键是23号
        LogUtils.d(TAG,"action："+action);//按下确认键会发送0 再发一次 1
        if (keyCode == 23){
            //OK
            if(binding.lin1.hasFocus()){
                binding.btn1.setFocusable(true);
                binding.btn2.setFocusable(true);
                binding.btn5.setFocusable(true);
                binding.btn6.setFocusable(true);
                binding.btn1.requestFocus();

                binding.btn3.setFocusable(false);
                binding.btn4.setFocusable(false);
                binding.btn7.setFocusable(false);
                binding.btn8.setFocusable(false);

                binding.btn9.setFocusable(false);
                binding.btn10.setFocusable(false);
                binding.btn11.setFocusable(false);
                binding.btn12.setFocusable(false);

                binding.btn13.setFocusable(false);
                binding.btn14.setFocusable(false);
                binding.btn15.setFocusable(false);
                binding.btn16.setFocusable(false);


                binding.lin1.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
                binding.lin2.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
                binding.lin3.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
                binding.lin4.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            }else if(binding.lin2.hasFocus()){
                binding.btn1.setFocusable(false);
                binding.btn2.setFocusable(false);
                binding.btn5.setFocusable(false);
                binding.btn6.setFocusable(false);

                binding.btn3.setFocusable(true);
                binding.btn4.setFocusable(true);
                binding.btn7.setFocusable(true);
                binding.btn8.setFocusable(true);
                binding.btn3.requestFocus();

                binding.btn9.setFocusable(false);
                binding.btn10.setFocusable(false);
                binding.btn11.setFocusable(false);
                binding.btn12.setFocusable(false);

                binding.btn13.setFocusable(false);
                binding.btn14.setFocusable(false);
                binding.btn15.setFocusable(false);
                binding.btn16.setFocusable(false);

                binding.lin2.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
                binding.lin1.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
                binding.lin3.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
                binding.lin4.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            }else if(binding.lin3.hasFocus()){
                binding.btn1.setFocusable(false);
                binding.btn2.setFocusable(false);
                binding.btn5.setFocusable(false);
                binding.btn6.setFocusable(false);

                binding.btn3.setFocusable(false);
                binding.btn4.setFocusable(false);
                binding.btn7.setFocusable(false);
                binding.btn8.setFocusable(false);

                binding.btn9.setFocusable(true);
                binding.btn10.setFocusable(true);
                binding.btn11.setFocusable(true);
                binding.btn12.setFocusable(true);
                binding.btn9.requestFocus();

                binding.btn13.setFocusable(false);
                binding.btn14.setFocusable(false);
                binding.btn15.setFocusable(false);
                binding.btn16.setFocusable(false);
                binding.lin3.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
                binding.lin1.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
                binding.lin2.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
                binding.lin4.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            }else if(binding.lin4.hasFocus()){
                binding.btn1.setFocusable(false);
                binding.btn2.setFocusable(false);
                binding.btn5.setFocusable(false);
                binding.btn6.setFocusable(false);

                binding.btn3.setFocusable(false);
                binding.btn4.setFocusable(false);
                binding.btn7.setFocusable(false);
                binding.btn8.setFocusable(false);

                binding.btn9.setFocusable(false);
                binding.btn10.setFocusable(false);
                binding.btn11.setFocusable(false);
                binding.btn12.setFocusable(false);

                binding.btn13.setFocusable(true);
                binding.btn14.setFocusable(true);
                binding.btn15.setFocusable(true);
                binding.btn16.setFocusable(true);
                binding.btn13.requestFocus();


                binding.lin4.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
                binding.lin1.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
                binding.lin2.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
                binding.lin3.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            }
        }else if(keyCode == 4){
            if(binding.lin1.hasFocus()){
//                binding.btn1.setFocusable(true);
//                binding.btn2.setFocusable(true);
//                binding.btn5.setFocusable(true);
//                binding.btn6.setFocusable(true);
//                binding.btn1.requestFocus();
//
//                binding.btn3.setFocusable(false);
//                binding.btn4.setFocusable(false);
//                binding.btn7.setFocusable(false);
//                binding.btn8.setFocusable(false);
                binding.lin1.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
            }else if(binding.lin2.hasFocus()){
//                binding.btn3.setFocusable(true);
//                binding.btn4.setFocusable(true);
//                binding.btn7.setFocusable(true);
//                binding.btn8.setFocusable(true);
//                binding.btn3.requestFocus();
//
//                binding.btn1.setFocusable(false);
//                binding.btn2.setFocusable(false);
//                binding.btn5.setFocusable(false);
//                binding.btn6.setFocusable(false);
                binding.lin2.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

            }else if(binding.lin3.hasFocus()){
                //                binding.btn3.setFocusable(true);
                //                binding.btn4.setFocusable(true);
                //                binding.btn7.setFocusable(true);
                //                binding.btn8.setFocusable(true);
                //                binding.btn3.requestFocus();
                //
                //                binding.btn1.setFocusable(false);
                //                binding.btn2.setFocusable(false);
                //                binding.btn5.setFocusable(false);
                //                binding.btn6.setFocusable(false);
                binding.lin3.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

            }
            else if(binding.lin4.hasFocus()){
                //                binding.btn3.setFocusable(true);
                //                binding.btn4.setFocusable(true);
                //                binding.btn7.setFocusable(true);
                //                binding.btn8.setFocusable(true);
                //                binding.btn3.requestFocus();
                //
                //                binding.btn1.setFocusable(false);
                //                binding.btn2.setFocusable(false);
                //                binding.btn5.setFocusable(false);
                //                binding.btn6.setFocusable(false);
                binding.lin4.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

            }


        }

        return super.dispatchKeyEvent(event);
    }
    // 监听确认键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        LogUtils.d(TAG,"onKeyDown keyCode:"+keyCode);
        if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
            LogUtils.d(TAG,"onKeyDown KEYCODE_ENTER");
            // 确认键按下时，子项可以获取焦点
            if (binding.lin1.hasFocus()) {
                binding.btn1.requestFocus();
            } else if (binding.lin2.hasFocus()) {
                binding.btn3.requestFocus();
            }else if (binding.lin3.hasFocus()) {
                binding.btn9.requestFocus();
            }else if (binding.lin4.hasFocus()) {
                binding.btn13.requestFocus();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

     //监听返回键
    @Override
    public void onBackPressed() {
        // 返回键按下时，返回到父布局
        LogUtils.d(TAG,"onBackPressed binding.lin1.hasFocus():"+binding.lin1.hasFocus());
        if (binding.btn1.hasFocus()||binding.btn2.hasFocus()||binding.btn5.hasFocus()||binding.btn6.hasFocus()) {
            //binding.btn1.requestFocus();
            binding.lin1.requestFocus();
            return;
        } else if (binding.btn3.hasFocus()||binding.btn4.hasFocus()||binding.btn7.hasFocus()||binding.btn8.hasFocus()) {
            //binding.btn3.requestFocus();
            binding.lin2.requestFocus();
            return;
        }else if (binding.btn9.hasFocus()||binding.btn10.hasFocus()||binding.btn11.hasFocus()||binding.btn12.hasFocus()) {
            //binding.btn3.requestFocus();
            binding.lin3.requestFocus();
            return;
        }else if (binding.btn13.hasFocus()||binding.btn14.hasFocus()||binding.btn15.hasFocus()||binding.btn16.hasFocus()) {
            //binding.btn3.requestFocus();
            binding.lin4.requestFocus();
            return;
        }

        super.onBackPressed();
    }
    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
}

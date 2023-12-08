package com.wcch.android.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.Nullable;

/**
 * Created by RyanWang on 2023/12/7.
 *
 * @Description:
 */
public class YaoKongQiActivity extends Activity {
    com.wcch.android.databinding.YaokongqiLayoutBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = com.wcch.android.databinding.YaokongqiLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.lin1.getViewTreeObserver().addOnGlobalFocusChangeListener(new onGlobalFocusChangeListener());
        binding.lin1.setOnFocusChangeListener(new onFocusChangeListener());
        binding.lin2.getViewTreeObserver().addOnGlobalFocusChangeListener(new onGlobalFocusChangeListener());
        binding.lin2.setOnFocusChangeListener(new onFocusChangeListener());
        binding.btn1.getViewTreeObserver().addOnGlobalFocusChangeListener(new onGlobalFocusChangeListener());
        binding.btn1.setOnFocusChangeListener(new onFocusChangeListener());
        binding.btn2.getViewTreeObserver().addOnGlobalFocusChangeListener(new onGlobalFocusChangeListener());
        binding.btn2.setOnFocusChangeListener(new onFocusChangeListener());
        binding.btn3.getViewTreeObserver().addOnGlobalFocusChangeListener(new onGlobalFocusChangeListener());
        binding.btn3.setOnFocusChangeListener(new onFocusChangeListener());
        binding.btn4.getViewTreeObserver().addOnGlobalFocusChangeListener(new onGlobalFocusChangeListener());
        binding.btn4.setOnFocusChangeListener(new onFocusChangeListener());
    }

    private class onFocusChangeListener  implements View.OnFocusChangeListener{
        @Override
        public void onFocusChange(View view, boolean b)
        {
            System.out.println("onFocusChange b："+b);
            if (b){
                view.setBackgroundColor(Color.GREEN);

            }else{
                //view.setBackground(getResources().getDrawable(R.drawable.shape_oval_bg_grey));
                //view.setBackgroundColor(Color.GRAY);
                view.setBackground(null);
            }
        }
    }
    private class onGlobalFocusChangeListener implements ViewTreeObserver.OnGlobalFocusChangeListener{

        @Override
        public void onGlobalFocusChanged(View view, View view1)
        {
            System.out.println("onGlobalFocusChanged");
            if (view !=null){
                System.out.println("onGlobalFocusChanged view !=null");
                //view.setBackgroundColor(Color.BLUE);
            }else{
                System.out.println("onGlobalFocusChanged view =null");
            }
            if (view1 !=null){
                System.out.println("onGlobalFocusChanged view1 !=null");
                //view1.setBackgroundColor(Color.RED);
            }else{
                System.out.println("onGlobalFocusChanged view1 =null");
            }

        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event)
    {


        final int keyCode = event.getKeyCode();
        final int action = event.getAction();
        System.out.println("keyCode："+keyCode);//按下确认键是23号
        System.out.println("action："+action);//按下确认键会发送0 再发一次 1

        return super.dispatchKeyEvent(event);
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

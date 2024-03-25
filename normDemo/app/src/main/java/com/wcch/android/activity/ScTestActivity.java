package com.wcch.android.activity;

import android.app.Activity;
import android.os.Bundle;

import com.wcch.android.databinding.ScTestBinding;

import androidx.annotation.Nullable;

/**
 * Created by RyanWang on 2023/12/7.
 *
 * @Description:
 */
public class ScTestActivity extends Activity {
    ScTestBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        binding = ScTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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

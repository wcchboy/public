package com.zrw.numin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private com.zrw.numin.NumIn numIn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        numIn = (NumIn)findViewById(R.id.numin);
        numIn.setInputCallback(new NumIn.InputCallBack() {
            @Override
            public void onInputFinish(String str) {
                Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onTextChanged(int inputNumber) {

            }
        });
    }
}

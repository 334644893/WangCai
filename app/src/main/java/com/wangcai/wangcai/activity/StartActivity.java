package com.wangcai.wangcai.activity;

import android.content.Intent;
import android.os.Bundle;

import com.wangcai.wangcai.MainActivity;
import com.wangcai.wangcai.R;
import com.wangcai.wangcai.common.BaseActivity;


public class StartActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        startActivity(new Intent(StartActivity.this, MainActivity.class));
        finish();
    }
}

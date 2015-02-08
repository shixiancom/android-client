package com.shixian.android.client.activities;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.shixian.android.client.R;


public class LoginActivity extends Activity
{
    private String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                // 跳过登录
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
            }
        }, 1000);
    }


}

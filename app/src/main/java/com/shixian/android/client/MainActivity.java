package com.shixian.android.client;


import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class MainActivity extends Activity
{
    private String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


}

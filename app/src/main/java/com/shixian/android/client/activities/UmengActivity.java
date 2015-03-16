package com.shixian.android.client.activities;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by tangtang on 15/3/16.
 */
public class UmengActivity extends ActionBarActivity {


    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}

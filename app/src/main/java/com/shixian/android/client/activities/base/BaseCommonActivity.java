package com.shixian.android.client.activities.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;

import com.shixian.android.client.R;
import com.shixian.android.client.contants.AppContants;
import com.shixian.android.client.utils.ApiUtils;
import com.shixian.android.client.utils.CommonUtil;

/**
 * Created by tangtang on 15/4/2.
 * 通用的activity父类
 *
 * 在这里加载三个drawable  是和卡片式有关的  让这些drawable常驻内存 减少每次解析xml到内存
 *
 */
public class BaseCommonActivity extends UmengActivity {


    /**
     *  三个和卡片式有关的drawable
     */
    public  static Drawable layer_comment_last;
    public  static Drawable layer_comment_not_last;
    public  static Drawable layer_feed_no_comment;
    public static Drawable shape_card;

    /**
     * 关闭所有activity的广播接收者
     */
    protected  FinishActivityReceiver receiver;


    //写个广播接受着 用于关闭activity


    protected static final String LABEL="LABLE";




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(null != savedInstanceState)
        {

            String label = savedInstanceState.getString(LABEL);
            this.setTitle(label);
        }


        super.onCreate(savedInstanceState);
        receiver=new FinishActivityReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction(AppContants.ACTION_FINISHACTIVITY);
        registerReceiver(receiver,filter);



    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        layer_comment_last=getResources().getDrawable(R.drawable.layer_card_comment);
        layer_comment_not_last=getResources().getDrawable(R.drawable.layer_not_last);
        layer_feed_no_comment=getResources().getDrawable(R.drawable.layer_card);
        shape_card=getResources().getDrawable(R.drawable.shape_card);
    }

    protected class FinishActivityReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            CommonUtil.logDebug("AAAAAA", "广播接受着已经其起到＝用");
            finish();
        }
    }


    @Override
    protected void onDestroy() {
        if(receiver!=null)
        {
            unregisterReceiver(receiver);
            receiver=null;
        }

        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save away the original text, so we still have it if the activity
        // needs to be killed while paused.

        savedInstanceState.putString(LABEL,getTitle().toString());

        super.onSaveInstanceState(savedInstanceState);

    }



}

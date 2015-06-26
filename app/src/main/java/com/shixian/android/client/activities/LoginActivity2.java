package com.shixian.android.client.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.ImageView;

import com.shixian.android.client.R;
import com.shixian.android.client.adapter.ViewPagerAdapter;
import com.shixian.android.client.sina.AccessTokenKeeper;
import com.shixian.android.client.sina.Constants;
import com.shixian.android.client.sina.widget.LoginButton;
import com.shixian.android.client.utils.ApiUtils;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.umeng.analytics.MobclickAgent;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by tangtang on 15/4/16.
 */
public class LoginActivity2 extends FragmentActivity{

    private ViewPager viewPager;
    private Oauth2AccessToken mAccessToken;
    private AuthInfo mAuthInfo;

    private LoginButton loginButton;


    private ImageView[] imageViews=new ImageView[4];

    private int[] dot_Id={
            R.id.dot1,
            R.id.dot2,
            R.id.dot3,
            R.id.dot4
    };
    private ImageView currentDot;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initJpush();


        // 创建授权认证信息
        mAuthInfo = new AuthInfo(this, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);


//        AccessTokenKeeper.clear(this);
        mAccessToken= AccessTokenKeeper.readAccessToken(this);
        // Toast.makeText(this,mAccessToken.getToken(),Toast.LENGTH_LONG).show();
        //不为null 并且可用
        if(mAccessToken!=null&&mAccessToken.isSessionValid()) {

            ApiUtils.init(this);
            //  Log.i("AAAA",mAccessToken.getToken()+"本地保存有token的时候--------------------");
            //这里还要验证token是否可用
            //在这里需要现实进度条给用户提示

            String cookie = this.getSharedPreferences("userinfo", Context.MODE_PRIVATE).getString("cookie", "");

            ApiUtils.client.addHeader("Cookie", cookie);
            ApiUtils.client.addHeader("user-agent", "android");

            startActivity(new Intent(this, MainActivity.class));
            this.finish();
        }

        setContentView(R.layout.activity_start);
        viewPager= (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));





        for(int i=0;i<4;i++)
        {
            imageViews[i]= (ImageView) findViewById(dot_Id[i]);
        }

        currentDot=imageViews[0];


        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentDot.setImageResource(R.drawable.page_indicator_unfocused);
                currentDot=imageViews[position];
                currentDot.setImageResource(R.drawable.page_indicator_focused);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
        MobclickAgent.onResume(this);


    }

    @Override
    public void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
        MobclickAgent.onPause(this);

    }

    private void initJpush() {

        JPushInterface.init(this);
        JPushInterface.setLatestNotificationNumber(this, 3);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if(loginButton!=null)
        {
            loginButton.onActivityResult(requestCode, resultCode, data);
        }
    }


    public void setLoginButton(LoginButton loginButton)
    {
        this.loginButton=loginButton;
    }

}

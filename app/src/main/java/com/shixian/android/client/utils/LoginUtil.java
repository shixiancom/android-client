package com.shixian.android.client.utils;

import android.app.Activity;

import com.shixian.android.client.sina.Constants;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;

/**
 * Created by s0ng on 2015/2/8.
 */
public class LoginUtil {

    /**
     * 登陆时获取token  调用此方法之前应该先调用AccessTokenKeeper的readToke方法并 判断token是否可用 如果可用无需调用此方法
     * @param activity
     * @param authListener  授权验证回调方法
     */
    public static void getToken(Activity activity,WeiboAuthListener authListener)
    {
        AuthInfo mAuthInfo=new AuthInfo(activity, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        SsoHandler mSsoHandler=new SsoHandler(activity,mAuthInfo);
        if(authListener!=null)
            mSsoHandler.authorizeWeb(authListener);
    }
}

package com.shixian.android.client.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shixian.android.client.R;
import com.shixian.android.client.activities.MainActivity;
import com.shixian.android.client.model.Message;
import com.shixian.android.client.sina.Constants;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;

import org.apache.http.Header;

/**
 * Created by s0ng on 2015/2/8.
 */
public class LoginUtil {

    /**
     * 登陆时获取token  调用此方法之前应该先调用AccessTokenKeeper的readToke方法并 判断token是否可用 如果可用无需调用此方法
     *
     * @param activity
     * @param authListener 授权验证回调方法
     */
    public static void getToken(Activity activity, WeiboAuthListener authListener) {
        AuthInfo mAuthInfo = new AuthInfo(activity, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
        SsoHandler mSsoHandler = new SsoHandler(activity, mAuthInfo);
        if (authListener != null)
            mSsoHandler.authorizeWeb(authListener);
    }


    /**
     * 验证登陆token再我们的服务器上是否
     *
     * @param context      是当前页面 也就是登陆页面
     * @param mAccessToken 微博认证
     */
    public static void validationToken(final Activity context, Oauth2AccessToken mAccessToken) {
        RequestParams params = new RequestParams();
        params.add("access_token", mAccessToken.getToken());


        final AsyncHttpClient client = new AsyncHttpClient();


        client.get("http://www.shixian.com/api/v1/api_login.json", params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {

                Gson gson = new Gson();
                Message msg = gson.fromJson(new String(bytes), Message.class);


                if ("ok".equals(msg.getValue())) {
                    //从头里面获取cookie
                    for (int j = 0; j < headers.length; j++) {
                        //
                        if ("Set-Cookie".equals(headers[j].getName())) {
                            SharedPreferences sp = context.getSharedPreferences("userinfo", Context.MODE_PRIVATE);
                            sp.edit().putString("cookie", headers[j].getValue()).commit();

                           // ApiUtils.client.addHeader(headers[j].getName(), headers[j].getValue());
                            ApiUtils.client.addHeader("Cookie", headers[j].getValue());
                            ApiUtils.client.addHeader("user-agent", "android");



                            CommonUtil.logDebug("AAAA", headers[j].getValue());
                            break;
                        }

                    }
                    context.startActivity(new Intent(context, MainActivity.class));
                    context.finish();
                } else {
                    Toast.makeText(context, msg.getValue(), Toast.LENGTH_LONG);
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(context,context.getResources().getString(R.string.check_net), Toast.LENGTH_LONG).show();
            }
        });
    }
}

package com.shixian.android.client.sina;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.exception.WeiboException;

/**
 * Created by tangtang on 15/3/26.
 */
public class WeiBoUtils {


    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     *
     */
    public static void sendMessage(final Activity context,String text,IWeiboShareAPI mWeiboShareAPI) {


            // 1. 初始化微博的分享消息
            WeiboMultiMessage weiboMessage = new WeiboMultiMessage();

                weiboMessage.textObject = getTextObj(text);



            // 2. 初始化从第三方到微博的消息请求
            SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
            // 用transaction唯一标识一个请求
            request.transaction = String.valueOf(System.currentTimeMillis());
            request.multiMessage = weiboMessage;

            // 3. 发送请求消息到微博，唤起微博分享界面

                AuthInfo authInfo = new AuthInfo(context, Constants.APP_KEY, Constants.REDIRECT_URL, Constants.SCOPE);
                Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(context.getApplicationContext());
                String token = "";
                if (accessToken != null) {
                    token = accessToken.getToken();
                }
                mWeiboShareAPI.sendRequest(context, request, authInfo, token, new WeiboAuthListener() {

                    @Override
                    public void onWeiboException( WeiboException arg0 ) {
                    }

                    @Override
                    public void onComplete( Bundle bundle ) {
                        // TODO Auto-generated method stub
                        Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
                        AccessTokenKeeper.writeAccessToken(context, newToken);
                    }

                    @Override
                    public void onCancel() {
                    }
                });

        }




    /**
     * 创建文本消息对象。
     *
     * @return 文本消息对象。
     */
    private static TextObject getTextObj(String text) {
        TextObject textObject = new TextObject();
        if(TextUtils.isEmpty(text))
            return null;
        textObject.text = text;
        return textObject;
    }




}

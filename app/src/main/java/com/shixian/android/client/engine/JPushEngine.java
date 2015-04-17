package com.shixian.android.client.engine;

import android.content.Context;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shixian.android.client.Global;
import com.shixian.android.client.contants.AppContants;
import com.shixian.android.client.utils.ApiUtils;
import com.shixian.android.client.utils.CommonUtil;

import org.apache.http.Header;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by tangtang on 15/4/13.
 * 发送jpush 信息
 */
public class JPushEngine {

    /******************************************************JPUSH****************/
    /**
     * 发送极光推送信息
     */
    public static void sendJpushData(Context context,boolean isInit) {

        String regId = JPushInterface.getRegistrationID(context);
        RequestParams params = new RequestParams();
        params.put("reg_id", regId);
        if(isInit)
            params.put("uid", Global.USER_ID);
        params.put("device", CommonUtil.getImei(context, ""));


        ApiUtils.post(context, AppContants.JPUSH_NEED_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int position, Header[] headers, byte[] bytes) {

            }

            @Override
            public void onFailure(int position, Header[] headers, byte[] bytes, Throwable throwable) {
            }
        });


    }


}

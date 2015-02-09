package com.shixian.android.client.engine;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shixian.android.client.contants.AppContants;
import com.shixian.android.client.utils.ApiUtils;

/**
 * Created by s0ng on 2015/2/9.
 * 用于访问网络的一些业务方法
 * 目前请求的类型不算太多  就用这个包含所有与网络交互的业务方法了
 */
public class CommonEngine {

    public static void getMyUserInfo(AsyncHttpResponseHandler handler)
    {
        ApiUtils.get(AppContants.URL_MY_INFO, null,handler);
    }

}

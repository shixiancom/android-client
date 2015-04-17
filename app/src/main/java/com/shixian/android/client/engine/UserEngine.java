package com.shixian.android.client.engine;

import android.content.Context;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shixian.android.client.contants.AppContants;
import com.shixian.android.client.utils.ApiUtils;

/**
 * Created by tangtang on 15/4/15.
 */
public class UserEngine {


    public static void editIcon(Context context,RequestParams params,AsyncHttpResponseHandler handler)
    {
        ApiUtils.initupload(context).setTimeout(2000000);
        ApiUtils.uploadPost(context, AppContants.USER_EDIT_URL,params,handler);
    }
}

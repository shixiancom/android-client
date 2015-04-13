package com.shixian.android.client.engine;

import android.content.Context;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shixian.android.client.contants.AppContants;
import com.shixian.android.client.utils.ApiUtils;

import org.apache.http.Header;

/**
 * Created by tangtang on 15/4/8.
 */
public class BootEngine {

     public static void getBootFeed(Context context,AsyncHttpResponseHandler httpResponseHandler)
     {
         ApiUtils.get(context, AppContants.BOOT_URL, null, httpResponseHandler);
     }
}

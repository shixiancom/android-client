package com.shixian.android.client.utils;

import android.util.Log;

import com.shixian.android.client.contants.AppContants;

/**
 * Created by s0ng on 2015/2/8.
 */
public class CommonUtil {

    public static void logUtil(String tag,String msg)
    {
        if(AppContants.DEBUG)
            Log.d(tag,msg);
    }
}

package com.shixian.android.client.utils;

import android.content.Context;

/**
 * Created by s0ng on 2015/2/9.
 */
public class SharedPerenceUtil {

    //用于一些本地缓存操作的存取

    /**
     * 存入 Me信息的工具方法
     * @param context
     * @param userinfo
     */
    public static void putUserInfo(Context context,String userinfo)
    {
        context.getSharedPreferences("userinfo",context.MODE_PRIVATE).edit().putString("userjson",userinfo);

    }

    public static String getUserInfo(Context context)
    {
        return context.getSharedPreferences("userinfo",context.MODE_PRIVATE).getString("userjson","");
    }
}

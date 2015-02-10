package com.shixian.android.client.utils;

import android.content.Context;

/**
 * Created by s0ng on 2015/2/9.
 * 本地缓存采用缓存第一页 也放在这个类中
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


    /**
     * 缓存我的项目数据
     */
    public static String getMyProject(Context context)
    {
        return context.getSharedPreferences("cachedate",context.MODE_PRIVATE).getString("myproject","");
    }

    /**
     * 保存我的项目数据到本地
     */
    public static void setMyProject(Context context,String json)
    {
        context.getSharedPreferences("cachedate",context.MODE_PRIVATE).edit().putString("myproject",json);

    }

}

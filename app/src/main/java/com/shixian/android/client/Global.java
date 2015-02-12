package com.shixian.android.client;

import android.content.Context;

import com.shixian.android.client.model.User;
import com.shixian.android.client.utils.ImageCache;

/**
 * Created by s0ng on 2015/2/9.
 * 用于存放全局信息  比如说 user cookie 好吧
 */
public class Global {

    //TODO
    //放在这里容易被回收
    public static User user;
    public static String USER_ID;

    public static Context MAIN;

    //cookie的格式是  key=value  传送的时候记得拆分
    public static String cookie;

    public static ImageCache IMGCACHE=ImageCache.getInstance();
}

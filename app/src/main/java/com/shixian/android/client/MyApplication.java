package com.shixian.android.client;

import android.app.Application;
import android.text.TextUtils;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;


/**
 * Created by tangtang on 15/3/8.
 */
public class MyApplication extends Application {


    private String cookie;

    @Override
    public void onCreate() {
        super.onCreate();

        //File cacheDir = StorageUtils.getOwnCacheDirectory(getApplicationContext(), "shixian");
        //创建默认的ImageLoader配置参数
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
                .createDefault(this);




        //Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(configuration);


    }


    public  void setCookie(String cookie)
    {
        this.cookie=cookie;
    }

    public String getCookie()
    {
        return cookie;
    }

}

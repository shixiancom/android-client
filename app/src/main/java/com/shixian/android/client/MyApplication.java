package com.shixian.android.client;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

/**
 * Created by tangtang on 15/3/8.
 */
public class MyApplication extends Application {

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

}

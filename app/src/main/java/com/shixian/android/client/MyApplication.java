package com.shixian.android.client;

import android.app.Application;
import android.text.TextUtils;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.shixian.android.client.utils.SharedPerenceUtil;


/**
 * Created by tangtang on 15/3/8.
 */
public class MyApplication extends Application {


    //发表项目是否有草稿
    private boolean hasCaogao;

    private String sx_plat="android";
    private sx_platname=""

    "sx_plat"="ios" 平台，ios or android
    "sx_platname"="iPhone5,1" 设备名称
    "sx_appversion"="1.0.1" app版本
    "sx_osversion"="7.0.1" 系统版本
    "sx_udid"="xxxx-xxxx-x-xx" 设备唯一标识
    "sx_uid"="2" 如果没有可不传
    "sx_resolution" 分辨率
    "sx_apikey"  用微博apikey
    "sx_ts"	时间戳
    "sx_sign" 参数签名


    @Override
    public void onCreate() {
        super.onCreate();


        setHasCaogao(SharedPerenceUtil.hasNewProject(this));
        //File cacheDir = StorageUtils.getOwnCacheDirectory(getApplicationContext(), "shixian");
        //创建默认的ImageLoader配置参数
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration
                .createDefault(this);




        //Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(configuration);


    }




    public boolean getHasCaogao()
    {
        return hasCaogao;
    }

    public void setHasCaogao(boolean hasCaogao)
    {
        this.hasCaogao=hasCaogao;
    }

}

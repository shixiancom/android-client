package com.shixian.android.client;

import android.app.Application;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.shixian.android.client.utils.CommonUtil;
import com.shixian.android.client.utils.DisplayUtil;
import com.shixian.android.client.utils.SharedPerenceUtil;


/**
 * Created by tangtang on 15/3/8.
 */
public class MyApplication extends Application {


    //发表项目是否有草稿
    private boolean hasCaogao;

    public static final  String sx_plat="android";
    public static final  String sx_platname= Build.MODEL;
    public static String sx_appversion;
    public static final String sx_osversion=android.os.Build.VERSION.RELEASE;
    public static  String sx_resolution;
    public static String sx_udid;







    @Override
    public void onCreate() {
        super.onCreate();

        sx_resolution=DisplayUtil.getResolution(this);
        try {
            sx_appversion=getPackageManager().getPackageInfo("com.shixian.android.client", PackageManager.GET_META_DATA).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            sx_appversion="1.0.2";
        }
        sx_udid=CommonUtil.getImei(this,"");

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

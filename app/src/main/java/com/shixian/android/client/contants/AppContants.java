package com.shixian.android.client.contants;

/**
 * Created by s0ng on 2015/2/8.
 * 用于保存全局常量
 */
public interface AppContants {

    //调试的时候为真 输出log
    boolean DEBUG=true;

    /**
     * 服务器接口地址前缀
     */
    String DOMAIN="http://www.shixian.com";

    /**
     * 获取自身信息url
     */
    String URL_MY_INFO=DOMAIN+"/api/v1/users/me.json";
    /**
     * 获取项目信息url
     */
    String URL_MY_PROJECT_INFO=DOMAIN+"/api/v1/projects/me.json";

    /**
     * 获取首页信息url
     */
    String INDEX_URL=DOMAIN+"/api/v1/feeds.json";



    String errorMsg="{\"value\":\"error\"}";

    /**
     * 用户头像
     */
    String USER_ICON_NAME="usre_icon";




}

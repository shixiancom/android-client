package com.shixian.android.client.contants;

/**
 * Created by s0ng on 2015/2/8.
 * 用于保存全局常量
 */
public interface AppContants {

    //调试的时候为真 输出log
    boolean DEBUG=true;

    /***********************API接口地址***************************************************/
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

    String USER_INFO_INDEX_URL=DOMAIN+"/api/v1/users/{user_id}.json";

    String USER_FEED_INDEX_URL=DOMAIN+"/api/v1/users/{user_name}/feeds.json";



    /******************************数据消息 **************************************************/


    String errorMsg="{\"value\":\"error\"}";


    /********************************关于缓存的一些东西*******************************************/
    /**
     * 用户头像
     */
    String USER_ICON_NAME="usre_icon";

    String IMAGE_PATH = "/img2";




    /* ***********************不同feed类型***************************************************/

    /**
     * common
     */
    String FEADE_TYPE_COMMON="common";


    /**
     * 每一页大小
     */
    int PAGESIZE=10;

}

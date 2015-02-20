package com.shixian.android.client.contants;

/**
 * Created by s0ng on 2015/2/8.
 * 用于保存全局常量
 */
public interface AppContants {

    //调试的时候为真 输出log
    boolean DEBUG=false;

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

    /**
     * 用户信息
     */
    String USER_INFO_INDEX_URL=DOMAIN+"/api/v1/users/{user_id}.json";

    String USER_FEED_INDEX_URL=DOMAIN+"" +
            "/api/v1/users/{user_name}/feeds.json";

    /**
     * project 项目信息
     */
    String PROJECT_INFO_URL=DOMAIN+"/api/v1/projects/{project_id}.json";

    String PROJECT_FEED_URL=DOMAIN+"/api/v1/projects/{project_id}/feeds.json";


    String DESCORY_PROJECT_URL=DOMAIN+"/api/v1/projects.json";


    //notification
    String NOTIFICATION_URL=DOMAIN+"/api/v1/notifications.json";

    String COMMENT_URL=DOMAIN+"/api/v1/%s/%s/comments.json";

    //用户关注地址
    String USER_FOLLOW_URL=DOMAIN+"/api/v1/users/%s/follow.json";

    //用户取消关注地址
    String USER_UNFOLLOW_URL=DOMAIN+"/api/v1/users/%s/unfollow.json";

    //生成项目关注信息
    String PROJECT_FOLLOW_URL=DOMAIN+"/api/v1/projects/%s/follow.json";

    //取消项目关注
    String PROJECT_UNFOLLOW_URL=DOMAIN+"/api/v1/projects/{project_id}/unfollow.json";

    String MSG_STATUS_URL=DOMAIN+"/api/v1/notifications/status.json";





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

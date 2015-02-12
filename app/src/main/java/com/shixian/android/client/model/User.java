package com.shixian.android.client.model;

import java.io.Serializable;

/**
 * Created by sllt on 15/1/28.
 */
public class User implements Serializable{



    //用户大图
    public String url;
    //描述
    public String description;

    public String email;

    public String id;

    public boolean is_admin;
    //状态
    public Status status;
    //标签
    public String tag_list;
    //上次更新
    public String updated_at;
    //用户名
    public String username;
    //是否被你关注
    public boolean has_followed;

    public Avatar avatar;




    public class Status implements  Serializable{
        public int feeds_count;
        public int followed_projects_count;
        public int followers_count;
        public int followings_count;
    }
     public static class Avatar implements  Serializable{
        //小头像
        public Small small;
        //更小的头像
        public Tiny tiny;
        //大头像
        public String url;

      public static  class Small implements  Serializable{
            public String url;
        }

       public static class Tiny implements  Serializable{
            public String url;
        }

    }
}

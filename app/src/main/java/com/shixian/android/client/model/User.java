package com.shixian.android.client.model;

/**
 * Created by sllt on 15/1/28.
 */
public class User {



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




    class Status{
        public int feeds_count;
        public int followed_projects_count;
        public int followers_count;
        public int followings_count;
    }
     public static class Avatar{
        //小头像
        public Small small;
        //更小的头像
        public Tiny tiny;
        //大头像
        public String url;

      public static  class Small{
            public String url;
        }

       public static class Tiny{
            public String url;
        }

    }
}

package com.shixian.android.client.model;

import java.io.Serializable;

/**
 * Created by s0ng on 2015/2/13.
 */
public class News  implements Serializable {

    public String created_at;
    public NewsData data;
    public String id;
    public String noti_type;
    public String notifiable_id;
    public String notifiable_type;
    public SimpleProject project;
    public User receiver;
    public User user;
    public String type;








    public static  class  NewsData implements Serializable{
        public  String content;
        public String content_html;
        public String commentable_id;
        public String commentable_type;
        public String notifiable_type;
        public String id;

    }
}

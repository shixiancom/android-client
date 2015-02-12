package com.shixian.android.client.model;

import java.io.Serializable;

/**
 * Created by s0ng on 2015/2/12.
 */
public class Project implements Serializable{

    // 项目ID
    public int id;
// 项目标题
    public String title;

    // 项目描述[html标签内容]
   public String  description;
    // 创建时间
    public String created_at;
    // 最后一次修改时间
    public String updated_at;
// 用户ID
    public String user_id;
// 是否被archived
    public boolean is_archived;
// 是否含有wiki
    public boolean has_wiki;
    //  标识是否已经follow
    public  boolean has_followed;
    //  标识是否已经join
    public boolean has_joined;
    //  标识项目被关注的次数
    public int followed_count;

}

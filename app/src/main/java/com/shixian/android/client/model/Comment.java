package com.shixian.android.client.model;

import com.shixian.android.client.model.feeddate.BaseFeed;

/**
 * Created by sllt on 15/1/28.
 */
public class Comment extends BaseFeed{

    public String content;
    public String content_html;
    public String commentable_id;
    public String commentable_type;
    public User user;
    public boolean isLast;
    public String project_id;




}

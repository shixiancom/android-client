package com.shixian.android.client.model.feeddate;

import com.shixian.android.client.model.Feed;

import java.io.Serializable;

/**
 * Created by s0ng on 2015/2/11.
 * 我把 回复也做成一个feed 因为展示需要啊   用的一套界面
 */
public class BaseFeed implements Serializable{

    public static final int TYPE_COMMENT=0;
    public static final int TYPE_FEED=1;

    public String created_at;
    public String id;
    public String updated_at;
    public String feedable_type;
    public String project_id;;
    public int position;
    public String feedable_id;


    public int type;

}

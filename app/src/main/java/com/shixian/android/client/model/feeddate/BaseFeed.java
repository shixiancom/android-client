package com.shixian.android.client.model.feeddate;

import java.io.Serializable;

/**
 * Created by s0ng on 2015/2/11.
 * 我把 回复也做成一个feed 因为展示需要啊   用的一套界面
 */
public class BaseFeed implements Serializable{

    public String created_at;
    public String id;
    public String updated_at;
    public String feedable_type;
    public String project_id;;
    public int position;


}

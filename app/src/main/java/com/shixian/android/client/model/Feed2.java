package com.shixian.android.client.model;

import com.shixian.android.client.model.feeddate.AllItemType;

/**
 * Created by s0ng on 2015/2/10.
 */
public  class Feed2{
    public String action_type;
    public String create_at;
    public String feedable_id;
    public String feedable_type;
    public String id;
    public boolean is_archived;
    public String project_id;
    public String update_at;

    //所有类型的date字段都在这里面 省事
    public AllItemType date;


}

package com.shixian.android.client.model;

import com.shixian.android.client.model.feeddate.AllItemType;
import com.shixian.android.client.model.feeddate.BaseFeed;

/**
 * Created by s0ng on 2015/2/10.
 */
public  class Feed2 extends BaseFeed{
    public String action_type;
    public boolean is_archived;

    //所有类型的date字段都在这里面 省事
    public AllItemType data;

    public boolean hasChildren;

    public int lastChildPosition;



}

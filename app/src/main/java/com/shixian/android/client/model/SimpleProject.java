package com.shixian.android.client.model;

/**
 * Created by s0ng on 2015/2/9.
 * 简单的Project实体 之存放id和对应的标题
 */
public class SimpleProject {


    private int id;
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}

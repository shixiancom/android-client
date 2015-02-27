package com.shixian.android.client.model;

import java.io.Serializable;

/**
 * Created by tangtang on 15/2/27.
 */
public class Plan implements Serializable{
    public String content;
    public String created_at;
    public String finish_on;
    public String id;


    public SimpleProject project;

    public User user;



}

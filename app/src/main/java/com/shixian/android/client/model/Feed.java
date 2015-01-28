package com.shixian.android.client.model;

import java.util.Date;

/**
 * Created by sllt on 15/1/28.
 */
public class Feed {

    private int id;
    private String type;
    private User user;
    private User receiver;
    private Date created;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

}

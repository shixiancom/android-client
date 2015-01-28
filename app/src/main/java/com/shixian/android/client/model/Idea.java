package com.shixian.android.client.model;

/**
 * Created by sllt on 15/1/28.
 */
public class Idea {

    private int id;
    private String content;
    private Comment comments[] = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Comment[] getComments() {
        return comments;
    }

    public void setComments(Comment[] comments) {
        this.comments = comments;
    }

}

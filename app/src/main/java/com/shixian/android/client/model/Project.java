package com.shixian.android.client.model;

/**
 * Created by sllt on 15/1/28.
 */
public class Project {
    private int id;
    private String  title,content;
    private Idea ideas[] = null;
    private Image images[] = null;
    private Feed feeds[] = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Idea[] getIdeas() {
        return ideas;
    }

    public void setIdeas(Idea[] ideas) {
        this.ideas = ideas;
    }

    public Image[] getImages() {
        return images;
    }

    public void setImages(Image[] images) {
        this.images = images;
    }

    public Feed[] getFeeds() {
        return feeds;
    }

    public void setFeeds(Feed[] feeds) {
        this.feeds = feeds;
    }

}

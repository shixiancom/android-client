package com.shixian.android.client.model.feeddate;

/**
 * Created by s0ng on 2015/2/10.
 * 把所有用到的字段都放在这个里面吧 取得时候注意就得了
 */
public class AllItemType extends  BaseFeedItem{
    public String content;
    public String content_html;
    public String id;
    public String description;
    public String status;
    public String title;
    public String finish_on;
    public Attachment attachment;
    public String file_name;
    public String comments_count;
    public String type;
    public String entity_type;


    public static class Attachment {
        public String url;

        public  Thumb thumb;


        public static class  Thumb{
            public String url;
        }
    }

}

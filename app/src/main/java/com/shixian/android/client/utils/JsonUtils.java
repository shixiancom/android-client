package com.shixian.android.client.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.shixian.android.client.activities.fragment.MsgDetialFragment;
import com.shixian.android.client.contants.AppContants;
import com.shixian.android.client.model.BaseBoot;
import com.shixian.android.client.model.Boot;
import com.shixian.android.client.model.Comment;
import com.shixian.android.client.model.Feed2;
import com.shixian.android.client.model.News;
import com.shixian.android.client.model.Project;
import com.shixian.android.client.model.feeddate.AllItemType;
import com.shixian.android.client.model.feeddate.BaseFeed;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by sllt on 15/1/28.
 */
public class JsonUtils {

    public static String getString(JSONObject obj, String key, String dft) {
        if (!obj.has(key)) {
            return dft;
        }
        try {
            String value = obj.getString(key);
            if (value == null) {
                return dft;
            }
            return value;
        } catch (JSONException e) {
            e.printStackTrace();
            Log.d("JSON ERROR","parse error");
            return dft;
        }
    }

    public static String getString(JSONObject obj, String key) {
        return getString(obj, key, null);
    }


    /**
     *
     * @param json
     * @return
     */
    public  static List<BaseBoot> parseBoots(String json)
    {
        List<BaseBoot> boots=new ArrayList<>();
        Gson gson=new Gson();

        try {
            JSONObject jsonObject=new JSONObject(json);
            String data = jsonObject.getString("data");
            JSONArray jsonArrayrray = new JSONArray(data);

            for(int i=0;i<jsonArrayrray.length();i++)
            {
                JSONObject jobj = jsonArrayrray.getJSONObject(i);
                Boot boot=gson.fromJson(jobj.toString(), Boot.class);
                boots.add(boot);
            }

            boots.add(0,new BaseBoot());
            boots.add(4,new BaseBoot());
            boots.add(8,new BaseBoot());

        } catch (JSONException e) {

        }

        return  boots;
    }

    /**
     * 解析feed的工具方法
     * @param json
     * @return
     */
    public static List<BaseFeed> ParseFeeds(String json)
    {
        List<BaseFeed> feeds=new ArrayList<BaseFeed>();


        Gson gson=new Gson();
        try {
            JSONObject jdataObj = new JSONObject(json);
            String data = jdataObj.getString("data");
            JSONArray jsonArrayrray = new JSONArray(data);

            for (int j = 0; j < jsonArrayrray.length(); j++) {
                JSONObject jobj = jsonArrayrray.getJSONObject(j);
                Feed2 feed = gson.fromJson(jobj.toString(), Feed2.class);

//                if(!"Agreement".equals(feed.feedable_type)) {
//                    feeds.add(feed);
//                    feed.type=BaseFeed.TYPE_FEED;
//                }
                feeds.add(feed);
                feed.type=BaseFeed.TYPE_FEED;

                if("Project".equals(feed.feedable_type))
                {

                    continue;
                }

                int i=0;
                try{
                    String commArrayStr=jobj.getJSONObject("data").getString("comments");
                    if(!"[]".equals(commArrayStr)){
                        feed.hasChildren=true;
                        JSONArray array=new JSONArray(commArrayStr);


                        for(i=0;i<array.length();i++)
                        {



                            Comment comment=gson.fromJson(array.getString(i), Comment.class);
                            comment.feedable_type= AppContants.FEADE_TYPE_COMMON;
                            comment.type=BaseFeed.TYPE_COMMENT;

                            comment.parent=feed;
                            if(i==0)
                            {
                                comment.isFirst=true;
                            }

                            if(i==array.length()-1)
                            {
                                comment.isLast=true;
                            }

                            comment.project_id=feed.project_id;
                            comment.parent_id=feed.id;



                            feeds.add(comment);


                        }
                    }

                    //lastposition已经行不通了  只能按照距离去计算才对
                    feed.lastChildPosition=i;
                }catch (Exception e)
                {
                    feed.lastChildPosition=i;
                    continue;
                }





            }
        }catch (Exception e)
        {
            CommonUtil.logDebug("JsonUtil",e.toString());
        }

        return feeds;
    }


    public static List<Project> ParsesProject(String json){
        List<Project> projects=new ArrayList<>();

        Gson gson=new Gson();
        try {
            JSONArray jsonArray=new JSONArray(json);

            for(int i=0;i<jsonArray.length();i++)
            {

                Project project=gson.fromJson(jsonArray.getString(i), Project.class);
                projects.add(project);
            }

        }catch (Exception e)
        {

        }

        return projects;
    }


    public  static List<News> parseNews(String json)
    {
        List<News> newses=new ArrayList<>();
        Gson gson=new Gson();
        try {
            JSONObject object=new JSONObject(json);
            JSONArray jsonArray=object.getJSONArray("data");
            for(int j=0;j<jsonArray.length();j++)
            {
                News news=gson.fromJson(jsonArray.getString(j),News.class);

                newses.add(news);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return newses;
    }


    public static MsgDetialFragment.MsgFeedEntry parseAllItemType(String firstPageDate, MsgDetialFragment.MsgType msgType) {



        MsgDetialFragment.MsgFeedEntry entry=new MsgDetialFragment.MsgFeedEntry();

        try {
            JSONObject jsonObject=new JSONObject(firstPageDate);

            String data;

            String type;

            if(msgType.isComment){

                type=msgType.commentable_type.toLowerCase()+"s";
                if("UserProjectRelation".equals(msgType.commentable_type))
                {
                    type="user_project_relations";
                }

            }else{
                type=msgType.notifiable_type.toLowerCase()+"s";
                if("UserProjectRelation".equals(msgType.notifiable_type))
                {
                    type="user_project_relations";
                }
            }

            data=jsonObject.getString(type);





            AllItemType allItemType=new Gson().fromJson(data,AllItemType.class);

            allItemType.type=type;


            entry.firstEntry=allItemType;

            JSONArray jsonArray=jsonObject.getJSONArray("comments");


            List<BaseFeed> baseFeeds=new ArrayList<>();
            if(jsonArray!=null)
            {

                for(int i=0;i<jsonArray.length();i++)
                {
                    String comment=jsonArray.getString(i);
                    Comment cco=new Gson().fromJson(comment,Comment.class);
                    if(cco.id.equals(msgType.notifiable_id))
                    {
                        msgType.position=i+1;
                    }
                    baseFeeds.add(cco);
                    cco.feedable_type= AppContants.FEADE_TYPE_COMMON;
                    cco.project_id=allItemType.project.id;
                    cco.parent_id=allItemType.id;

                }


            }
            entry.baseFeeds=baseFeeds;



        } catch (JSONException e) {


            e.printStackTrace();
        }

        return entry;
    }


}

package com.shixian.android.client.utils;

import android.text.TextUtils;
import android.util.ArrayMap;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by sllt on 15/1/28.
 */
public class JsonUtils {



    private static Map<String,Integer>  feedTypes;
    private static Map<String,Integer> newsTypes;
    private static Map<String,Integer> bootTypes;

    private static String[] arrayFeedType={
            "Idea",
            "Project",
            "Plan",
            "Image",
            "UserProjectRelation",
            "Homework",
            "Task",
            "Vote",
            "Attachment",
            "Agreement"

    };

    private static String[] arrayBoots={
            "competitor_projects",
            "team_recruit_projects",
            "old_boot_projects"
    };

    private static String[] arratNewsTypes={
            "new_agreement",
            "invit_follow",
            "join_accept",
            "join_accept",
            "join_reject",
            "new_comment",
            "new_entity",
            "UserRelation",
            "new_follow",
            "new_homework",
            "new_mention",
            "new_reply",
            "new_task"

    };



    static{
        feedTypes=new HashMap<>();
        newsTypes=new HashMap<>();
        bootTypes=new HashMap<>();

        int i=0;
        for(String str:arrayFeedType)
        {
            Integer integer=new Integer(i);
            i++;
            feedTypes.put(str,integer);
        }

        i=0;
        for(String str:arratNewsTypes)
        {
            Integer integer=new Integer(i);
            i++;
            newsTypes.put(str,integer);
        }

        i=0;

        for(String str:arrayBoots)
        {
            Integer integer=new Integer(i);
            i++;
            bootTypes.put(str,integer);
        }
    }

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
                if(boot==null||bootTypes.get(boot.type)==null)
                {
                    continue;
                }
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
                if(feed==null||TextUtils.isEmpty(feed.feedable_type)||feedTypes.get(feed.feedable_type)==null)
                    continue;

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
                if(project!=null)
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

                if(news==null||newsTypes.get(news.noti_type)==null)
                {
                    continue;
                }else{
                    newses.add(news);

                }

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return newses;
    }


    /**
     * 好脑残啊
     * 这真是恶心至极  让我来分析分析这里是怎么个逻辑 由于写的时间比较久 又不能跟以前格式保持一致 这里我几乎已经忘记了
     * 先获取一个alltype 对象   并没有放在list中
     * 又获取一些Comment对象 放在list中
     * 然后需要考虑的是如何维护负责关系 嗯 这里不需要显示那个回复框 不需要维护 直接删除就好了 对啊
     * @param firstPageDate
     * @param msgType
     * @return
     */
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

                int N=jsonArray.length();
                for(int i=0;i<N;i++)
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
                    if(i==0)
                    {
                        cco.isFirst=true;

                    }
                    if(i==N)
                    {
                        cco.isLast=true;
                    }



                }


            }
            entry.baseFeeds=baseFeeds;



        } catch (JSONException e) {


            e.printStackTrace();
        }

        return entry;
    }


}

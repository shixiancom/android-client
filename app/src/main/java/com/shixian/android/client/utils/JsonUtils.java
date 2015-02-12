package com.shixian.android.client.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.shixian.android.client.contants.AppContants;
import com.shixian.android.client.model.Comment;
import com.shixian.android.client.model.Feed2;
import com.shixian.android.client.model.Project;
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
                feeds.add(feed);


                String commArrayStr=jobj.getJSONObject("data").getString("comments");
                if(!"[]".equals(commArrayStr)){
                    JSONArray array=new JSONArray(commArrayStr);

                    for(int i=0;i<array.length();i++)
                    {
                        Comment comment=gson.fromJson(array.getString(i), Comment.class);
                        comment.feedable_type= AppContants.FEADE_TYPE_COMMON;
                        if(i==array.length()-1)
                        {
                            comment.isLast=true;
                        }

                        comment.project_id=feed.project_id;

                        feeds.add(comment);

                    }
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
}

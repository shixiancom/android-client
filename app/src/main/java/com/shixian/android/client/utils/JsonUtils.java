package com.shixian.android.client.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.shixian.android.client.model.Feed2;

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
    public static List<Feed2> ParseFeeds(String json)
    {
        List<Feed2> feeds=new ArrayList<Feed2>();


        Gson gson=new Gson();
        try {
            JSONObject jdataObj = new JSONObject(json);
            String data = jdataObj.getString("data");
            JSONArray jsonArrayrray = new JSONArray(data);

            for (int j = 0; j < jsonArrayrray.length(); j++) {
                JSONObject jobj = jsonArrayrray.getJSONObject(j);
                Feed2 feed = gson.fromJson(jobj.toString(), Feed2.class);
                feeds.add(feed);

            }
        }catch (Exception e)
        {
            CommonUtil.logDebug("JsonUtil",e.toString());
        }

        return feeds;
    }
}

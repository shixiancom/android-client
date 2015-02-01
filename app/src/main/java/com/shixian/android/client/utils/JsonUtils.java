package com.shixian.android.client.utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by sllt on 15/1/28.
 */
public class JsonUtils
{

    public static String getString(JSONObject obj, String key, String dft)
    {
        if (!obj.has(key))
        {
            return dft;
        }
        try
        {
            String value = obj.getString(key);
            if (value == null)
            {
                return dft;
            }
            return value;
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            Log.d("JSON ERROR", "parse error");
            return dft;
        }
    }

    public static String getString(JSONObject obj, String key)
    {
        return getString(obj, key, null);
    }
}

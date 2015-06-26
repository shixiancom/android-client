package com.shixian.android.client.engine;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shixian.android.client.contants.AppContants;
import com.shixian.android.client.utils.ApiUtils;

/**
 * Created by tangtang on 15/4/18.
 */
public class CommentEngine {


    public static void deleteComment(Context context,String id,AsyncHttpResponseHandler handler)
    {

        String url= String.format(AppContants.COMMENT_DELETE_URL,id);
        ApiUtils.delete(context,url,handler);

    }
}

package com.shixian.android.client.engine;

import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shixian.android.client.contants.AppContants;
import com.shixian.android.client.utils.ApiUtils;



/**
 * Created by tangtang on 15/3/24.
 */
public class ProjectEngine {


    private static final String TAG="ProjectEngine";

    public static void addIdea(final  String projectId,String content,final  AsyncHttpResponseHandler handler)  {

        RequestParams params=new RequestParams();
        params.add("project_id",projectId);
        params.add("idea_conten",content);


        ApiUtils.post(AppContants.ADD_IDEA_URL,params,handler);

    }
}

package com.shixian.android.client.engine;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shixian.android.client.contants.AppContants;
import com.shixian.android.client.utils.ApiUtils;



/**
 * Created by tangtang on 15/3/24.
 */
public class ProjectEngine extends  BaseEngine{


    private static final String TAG="ProjectEngine";

    public static void addIdea(Context context,final  String projectId,String content,final  AsyncHttpResponseHandler handler)  {

        RequestParams params=new RequestParams();
        params.add("project_id",projectId);
        params.add("idea_content",content);


        ApiUtils.post(context,AppContants.ADD_IDEA_URL,params,handler);

    }


    public static void addProject(Context context,String title,String content,String fuzeren,final AsyncHttpResponseHandler handler)
    {
        RequestParams params=new RequestParams();
        params.add("project[title]",title);
        params.add("project[description]",content);
        params.add("default",fuzeren);

        ApiUtils.post(context,AppContants.ADD_PROJECT_URL,params,handler);

    }

    /**
     * 点赞事件 是为不知道具体的点赞类型 所有函数名称用xxx写
     * @param entity_type
     * @param entity_id
     */
    public static void agreeXXX(Context context,boolean isagree,String entity_type,String entity_id,final AsyncHttpResponseHandler handler)
    {
        RequestParams params=new RequestParams();

        params.put("entity_type", entity_type);
        params.put("entity_id", entity_id);

        if(isagree)
        {
            ApiUtils.post(context,AppContants.AGREE_URL,params,handler);
        }else{
            ApiUtils.post(context,AppContants.DISAGREE_URL,params,handler);
        }

    }
}

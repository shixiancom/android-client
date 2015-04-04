package com.shixian.android.client.engine;

import android.content.Context;

import com.loopj.android.http.RequestParams;

/**
 * Created by tangtang on 15/4/4.
 */
public class BaseEngine  {


    private  CommonRequestParams commonRequestParams;

    public  CommonRequestParams getCommonRequestParams(Context context){

        if(commonRequestParams==null)
        {
            commonRequestParams=new CommonRequestParams(context);
        }

        return commonRequestParams;
    }

    public static class CommonRequestParams extends RequestParams{

        private Context context;
        public CommonRequestParams(Context context)
        {
         //   ((MyApplication)context.getApplicationContext())
        }
    }
}

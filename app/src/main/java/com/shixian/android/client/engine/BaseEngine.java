package com.shixian.android.client.engine;

import android.content.Context;

import com.loopj.android.http.RequestParams;
import com.shixian.android.client.MyApplication;
import com.shixian.android.client.sina.AccessTokenKeeper;
import com.shixian.android.client.sina.Constants;

import java.util.Date;

/**
 * Created by tangtang on 15/4/4.
 */
public class BaseEngine  {

    public static class CommonRequestParams extends RequestParams{
        public CommonRequestParams(Context context)
        {
            this.add("sx_plat",((MyApplication)context.getApplicationContext()).sx_plat);
            this.add("sx_platname",((MyApplication)context.getApplicationContext()).sx_platname);
            this.add("sx_appversion",((MyApplication)context.getApplicationContext()).sx_appversion);
            this.add("sx_osversion",((MyApplication)context.getApplicationContext()).sx_osversion);
            this.add("sx_udid",((MyApplication)context.getApplicationContext()).sx_resolution);
            this.add("sx_resolution",((MyApplication)context.getApplicationContext()).sx_resolution);
            this.add("sx_apikey", Constants.APP_KEY);

            //签名和时间戳已经在外部实现
            this.add("sx_ts",new Date().toString());
            //还差校验和


        }


    }

}

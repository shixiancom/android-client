package com.shixian.android.client;

import android.content.Context;
import android.test.InstrumentationTestCase;
import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shixian.android.client.contants.AppContants;
import com.shixian.android.client.engine.ProjectEngine;
import com.shixian.android.client.utils.ApiUtils;

import org.apache.http.Header;

/**
 * Created by tangtang on 15/3/24.
 */
public class Test extends InstrumentationTestCase {


    public void testAddIdea()
    {

            ProjectEngine.addEngine("17","你好周星驰",new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int position, Header[] headers, byte[] bytes) {

                    Log.i("AAAA",new String(bytes) + "success");

                }

                @Override
                public void onFailure(int position, Header[] headers, byte[] bytes, Throwable throwable) {
                    Log.i("AAAA",new String(bytes)+"+ fo");
                }
            });

    }


    public void testpostxxx()
    {
        String url=String.format("http://172.168.96.93:3000");
        ApiUtils.client.addHeader("user-agent", "android");





        String cookie=getInstrumentation().getContext().getSharedPreferences("userinfo", Context.MODE_PRIVATE).getString("cookie","");

        ApiUtils.client.addHeader("Cookie", cookie);
        ApiUtils.client.addHeader("user-agent", "android");



        ApiUtils.get(AppContants.INDEX_URL, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int position, Header[] headers, byte[] bytes) {

                Log.i("AAAA", new String(bytes));

            }

            @Override
            public void onFailure(int position, Header[] headers, byte[] bytes, Throwable throwable) {
                Log.i("AAAA", new String(bytes));
            }
        });
    }

}

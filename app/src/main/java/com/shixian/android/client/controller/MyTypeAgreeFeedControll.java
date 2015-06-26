package com.shixian.android.client.controller;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shixian.android.client.R;
import com.shixian.android.client.engine.ProjectEngine;
import com.shixian.android.client.model.Feed2;
import com.shixian.android.client.model.feeddate.BaseFeed;

import org.apache.http.Header;

import java.util.List;

/**
 * Created by tangtang on 15/4/21.
 */
public class MyTypeAgreeFeedControll implements View.OnClickListener {

    private Context context;
    private BaseAdapter adapter;
    private List<BaseFeed>  feeds;
    private TextView tv_argeecount;
    private Feed2 feed;
    private boolean isAgree=false;
    private boolean clickable=true;




    public  MyTypeAgreeFeedControll(TextView tv_argeecount,Context context ,Feed2 feed,List<BaseFeed> feeds,BaseAdapter adapter)
    {
        this.tv_argeecount=tv_argeecount;
        this.context=context;
        this.feed=feed;
        this.isAgree=feed.agreement_status;
        this.feeds=feeds;
        this.adapter=adapter;
    }



    @Override
    public void onClick(final  View v) {

        if (clickable) {
            clickable = false;
            //发送请求
            String catagory;

            if (feed.feedable_type.equals("Agreement")) {
                catagory = feed.data.feedable_type;
            } else {
                catagory = feed.feedable_type;
            }

            ProjectEngine.agreeXXX(context, feed.agreement_status, catagory, feed.data.id, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {

                    feeds.remove(feed);
                    adapter.notifyDataSetChanged();

                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    Toast.makeText(context, R.string.check_net, Toast.LENGTH_SHORT).show();

                    clickable = true;
                }
            });


        }
    }
}

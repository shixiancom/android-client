package com.shixian.android.client.controller;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shixian.android.client.R;
import com.shixian.android.client.engine.ProjectEngine;
import com.shixian.android.client.model.Feed2;
import com.shixian.android.client.model.Image;
import com.shixian.android.client.model.feeddate.AllItemType;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tangtang on 15/4/3.
 * 赞同的controller
 */
public class ArgeeOnClickController implements View.OnClickListener {


    private static final int TYPE_FEED=0;
    private static  final int TYPE_ALLTYPE=1;

    private TextView tv_argeecount;
    private Context context;
    private Feed2 feed;
    private int type;
    private boolean isAgree;


    private boolean clickable=true;




    public ArgeeOnClickController(Context context,Feed2 feed,TextView tv_argeecount)
    {
        this.tv_argeecount=tv_argeecount;
        this.context=context;
        this.feed=feed;
        this.type=TYPE_FEED;
        this.isAgree=feed.agreement_status;

    }



    @Override
    public void onClick(final View v) {

        if(clickable)
        {
            clickable=false;
            //发送请求
            String catagory;

            if(feed.feedable_type.equals("Agreement"))
            {
                catagory=feed.data.feedable_type;
            }else{
                catagory=feed.feedable_type;
            }

            ProjectEngine.agreeXXX(context,feed.agreement_status,catagory,feed.data.id,new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {

                    try {
                        JSONObject jsonObject=new JSONObject(new String(bytes));


                            tv_argeecount.setText(jsonObject.getString("agreement_count"));
                            if(isAgree) {
                                ((ImageView) v).setImageResource(R.drawable.liked);
                            }
                            else {
                                ((ImageView) v).setImageResource(R.drawable.like);
                            }

                            if(TYPE_FEED==type) {
                                feed.agreement_status = !feed.agreement_status;
                                isAgree=!isAgree;
                            }

                        clickable=true;
                    } catch (Exception e) {
                        Toast.makeText(context,"服务器异常 稍后再试",Toast.LENGTH_SHORT).show();
                        clickable=true;
                    }



                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    Toast.makeText(context, R.string.check_net,Toast.LENGTH_SHORT).show();

                    clickable=true;
                }
            });


        }

    }
}

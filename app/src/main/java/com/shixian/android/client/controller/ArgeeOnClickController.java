package com.shixian.android.client.controller;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shixian.android.client.R;
import com.shixian.android.client.engine.ProjectEngine;
import com.shixian.android.client.model.Image;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tangtang on 15/4/3.
 */
public class ArgeeOnClickController implements View.OnClickListener {


    private TextView tv_argeecount;
    private String feed_type;
    private boolean isAgree;
    private String feed_id;
    private Context context;


    public ArgeeOnClickController(Context context,boolean isAgree,String feed_type,String feed_id,TextView tv_argeecount)
    {
        this.tv_argeecount=tv_argeecount;
        this.isAgree=isAgree;
        this.feed_type=feed_type;
        this.feed_id=feed_id;
        this.context=context;
    }

    @Override
    public void onClick(final View v) {

        //发送请求
        ProjectEngine.agreeXXX(context,isAgree,feed_type,feed_id,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {

                try {
                    JSONObject jsonObject=new JSONObject(new String(bytes));
                    if("ok".equals(jsonObject.getString("message")))
                    {
                        tv_argeecount.setText(jsonObject.getString("count"));
                        if(isAgree)
                            ((ImageView)v).setImageResource(R.drawable.liked);

                        else
                            ((ImageView)v).setImageResource(R.drawable.like);
                    }
                } catch (Exception e) {
                    Toast.makeText(context,"服务器异常 稍后再试",Toast.LENGTH_SHORT).show();;
                }



            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(context, R.string.check_net,Toast.LENGTH_SHORT).show();
            }
        });

    }
}

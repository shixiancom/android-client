package com.shixian.android.client.controller;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shixian.android.client.R;
import com.shixian.android.client.activities.fragment.MsgDetialFragment;
import com.shixian.android.client.engine.ProjectEngine;
import com.shixian.android.client.model.feeddate.AllItemType;

import org.apache.http.Header;
import org.json.JSONObject;

/**
 * Created by tangtang on 15/4/13.
 */
public class MsgAgreeOnClickController implements View.OnClickListener {


    private Context context;
    private AllItemType allItemType;
    private TextView tv_argeecount;
    private boolean clickable=true;
    private String type;
    private String id;
    private boolean isAgree;


    public MsgAgreeOnClickController(Context context, AllItemType allItemType, TextView tv,String type,String id) {
        this.context = context;
        this.allItemType = allItemType;
        this.tv_argeecount = tv;
        this.type=type;
        this.id=id;
        this.isAgree=allItemType.agreement_status;
    }

    @Override
    public void onClick(final View v) {

        if (clickable) {
            clickable = false;



            //发送请求
            String catagory=allItemType.feedable_type;;


            ProjectEngine.agreeXXX(context, allItemType.agreement_status, type, id, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {


                    try {
                        JSONObject jsonObject = new JSONObject(new String(bytes));

                            tv_argeecount.setText(jsonObject.getString("agreement_count"));
                            if (isAgree)
                                ((ImageView) v).setImageResource(R.drawable.liked);
                            else
                                ((ImageView) v).setImageResource(R.drawable.like);


                            allItemType.agreement_status = !allItemType.agreement_status;
                            isAgree = !isAgree;

                        clickable = true;
                    } catch (Exception e) {
                        Toast.makeText(context, "服务器异常 稍后再试", Toast.LENGTH_SHORT).show();
                        clickable = true;
                    }


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

package com.shixian.android.client.controller;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.shixian.android.client.activities.fragment.MsgDetialFragment;

/**
 * Created by tangtang on 15/4/13.
 */
public class MsgAgreeOnClickController implements View.OnClickListener {


    private Context context;
    private MsgDetialFragment.MsgType msgType;
    private TextView tv;



    public MsgAgreeOnClickController(Context context, MsgDetialFragment.MsgType msgType,TextView tv)
    {
        this.context=context;
        this.msgType=msgType;
        this.tv=tv;
    }

    @Override
    public void onClick(View v) {


    }
}

package com.shixian.android.client.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.shixian.android.client.R;
import com.shixian.android.client.activities.ProjectActivity;
import com.shixian.android.client.activities.UserActivity;
import com.shixian.android.client.contants.AppContants;
import com.shixian.android.client.model.Boot;
import com.shixian.android.client.model.Comment;
import com.shixian.android.client.model.Feed2;
import com.shixian.android.client.model.feeddate.BaseFeed;

/**
 * Created by s0ng on 2015/2/12.
 * 这个类要怎么处理
 * 传过来一个feed？ 为了减少创建OnClickListener的个数 这是一个方法吧 但是每个条目还是必须创建一个监听对象的
 */

public class BootOnClickController implements View.OnClickListener {


    public static final int USER_FRAGMENT = 0;



    private Boot boot;
    private Activity context;


    //穿过来的feed
    public BootOnClickController(Activity context, Boot boot) {
        this.boot = boot;
        this.context =context;
    }


    @Override
    public void onClick(View v) {

        Bundle bundle = new Bundle();
        Intent intent ;
        switch (v.getId()) {

            //用户
            case R.id.rl_user:

                //跳转到个人主页
                intent= new Intent(context, UserActivity.class);
                bundle.putSerializable("user", boot.user);



                intent.putExtras(bundle);

                context.startActivity(intent);


//                context.switchFragment(uf,null);

//                Toast.makeText(context,"tv_name",Toast.LENGTH_SHORT).show();
                break;
            //项目
            case R.id.ll_project:

                //跳转到项目主页
                intent = new Intent(context, ProjectActivity.class);
                intent.putExtra("project_id", boot.project.id+"");

                intent.putExtras(bundle);

                context.startActivity(intent);



                break;



            //图片内容 默认是隐藏的 当feedable_type为image时显示
            case R.id.iv_content:
                Toast.makeText(context, "暂不支持下载文件", Toast.LENGTH_SHORT).show();
                break;
            //回复框 发表项目的时候是隐藏的
            case R.id.tv_response:
                Toast.makeText(context, "tv_response", Toast.LENGTH_SHORT).show();
                break;

        }
    }


}

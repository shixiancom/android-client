package com.shixian.android.client.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.shixian.android.client.R;
import com.shixian.android.client.activities.BaseActivity;
import com.shixian.android.client.activities.DetailActivity;
import com.shixian.android.client.activities.MainActivity;
import com.shixian.android.client.contants.AppContants;
import com.shixian.android.client.model.Comment;
import com.shixian.android.client.model.Feed2;
import com.shixian.android.client.model.feeddate.BaseFeed;

/**
 * Created by s0ng on 2015/2/12.
 * 这个类要怎么处理
 * 传过来一个feed？ 为了减少创建OnClickListener的个数 这是一个方法吧 但是每个条目还是必须创建一个监听对象的
 */

public class IndexOnClickController implements View.OnClickListener {


    public static final int USER_FRAGMENT = 0;
    public static final int PROJECT_FRAGMENT = 1;


    private BaseFeed mFeed;
    private BaseActivity context;


    //穿过来的feed
    public IndexOnClickController(Activity context, BaseFeed mFeed) {
        this.mFeed = mFeed;
        this.context = (MainActivity) context;
    }


    @Override
    public void onClick(View v) {

        Bundle bundle = new Bundle();
        Intent intent = new Intent(context, DetailActivity.class);
        switch (v.getId()) {

            //头像
            case R.id.iv_icon:


                //用户名
            case R.id.tv_name:
                //跳转到个人主页
                Toast.makeText(context, "iv_ivcon", Toast.LENGTH_SHORT).show();
                //跳转到个人主页
                String userid = "";
                if (mFeed.feedable_type.equals(AppContants.FEADE_TYPE_COMMON)) {
                    Comment comment = (Comment) mFeed;
                    bundle.putSerializable("user", comment.user);
                    userid = comment.user.id;
                } else {
                    Feed2 feed = (Feed2) mFeed;
                    bundle.putSerializable("user", feed.data.user);
                    userid = feed.data.user.id;
                }

                bundle.putInt("type", USER_FRAGMENT);

                intent.putExtras(bundle);

                context.startActivity(intent);


//                context.switchFragment(uf,null);

//                Toast.makeText(context,"tv_name",Toast.LENGTH_SHORT).show();
                break;
            //项目
            case R.id.tv_project:


                //跳转到项目主页
                if (mFeed.feedable_type.equals(AppContants.FEADE_TYPE_COMMON)) {
                    Comment comment = (Comment) mFeed;
                    bundle.putSerializable("user", comment.user);
                    userid = comment.user.id;
                } else {
                    Feed2 feed = (Feed2) mFeed;
                    bundle.putSerializable("user", feed.data.user);
                    userid = feed.data.user.id;
                }

                bundle.putInt("type", PROJECT_FRAGMENT);
                bundle.putString("project_id", mFeed.project_id);

                intent.putExtras(bundle);

                context.startActivity(intent);


//                context.switchFragment(uf,null);

//                Toast.makeText(context,"tv_name",Toast.LENGTH_SHORT).show();
                break;

            //内容
            case R.id.tv_content:
                Toast.makeText(context, "tv_name", Toast.LENGTH_SHORT).show();
                break;
            //图片内容 默认是隐藏的 当feedable_type为image时显示
            case R.id.iv_content:
                Toast.makeText(context, "tv_name", Toast.LENGTH_SHORT).show();
                break;
            //回复框 发表项目的时候是隐藏的
            case R.id.tv_response:
                Toast.makeText(context, "tv_name", Toast.LENGTH_SHORT).show();
                break;

        }
    }


}

package com.shixian.android.client.controller;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.shixian.android.client.R;
import com.shixian.android.client.activities.MainActivity;
import com.shixian.android.client.activities.fragment.ProjectFeedFragment;
import com.shixian.android.client.activities.fragment.UserIndexFragment;
import com.shixian.android.client.contants.AppContants;
import com.shixian.android.client.model.Comment;
import com.shixian.android.client.model.Feed2;
import com.shixian.android.client.model.feeddate.BaseFeed;

/**
 * Created by s0ng on 2015/2/12.
 * 这个类要怎么处理
 * 传过来一个feed？ 为了减少创建OnClickListener的个数 这是一个方法吧 但是每个条目还是必须创建一个监听对象的
 */
public class OnClickController implements View.OnClickListener{

    private BaseFeed mFeed;
    private MainActivity context;


    //穿过来的feed
    public OnClickController(Activity context,BaseFeed mFeed)
    {
        this.mFeed=mFeed;
        this.context= (MainActivity) context;
    }


    @Override
    public void onClick(View v) {

        Bundle bundle=new Bundle();
        switch (v.getId())
        {

            //头像
            case R.id.iv_icon:


            //用户名
            case R.id.tv_name:
                //跳转到个人主页
                Toast.makeText(context,"iv_ivcon",Toast.LENGTH_SHORT).show();
                //跳转到个人主页
                UserIndexFragment uf=new UserIndexFragment();


                String userid="";
                if(mFeed.feedable_type.equals(AppContants.FEADE_TYPE_COMMON))
                {
                    Comment comment= (Comment) mFeed;
                    bundle.putSerializable("user",comment.user);
                    userid=comment.user.id;
                }else {
                    Feed2 feed= (Feed2) mFeed;
                    bundle.putSerializable("user",feed.data.user);
                    userid=feed.data.user.id;
                }

                uf.setArguments(bundle);
                context.switchFragment(uf,null);

//                Toast.makeText(context,"tv_name",Toast.LENGTH_SHORT).show();
                break;
            //项目
            case R.id.tv_project:
                //点击项目要进入项目界面 我先把项目数据拿到看一看　


                ProjectFeedFragment feedFragment=new ProjectFeedFragment();
                bundle.putString("project_id",mFeed.project_id);
                feedFragment.setArguments(bundle);
                context.switchFragment(feedFragment,null);

//                Toast.makeText(context,"tv_name",Toast.LENGTH_SHORT).show();
                break;
            //内容
            case R.id.tv_content:
                Toast.makeText(context,"tv_name",Toast.LENGTH_SHORT).show();
                break;
            //图片内容 默认是隐藏的 当feedable_type为image时显示
            case R.id.iv_content:
                Toast.makeText(context,"tv_name",Toast.LENGTH_SHORT).show();
                break;
            //回复框 发表项目的时候是隐藏的
            case R.id.tv_response:
                Toast.makeText(context,"tv_name",Toast.LENGTH_SHORT).show();
                break;

        }
    }
}

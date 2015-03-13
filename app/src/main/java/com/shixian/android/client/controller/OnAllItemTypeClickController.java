package com.shixian.android.client.controller;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.shixian.android.client.R;
import com.shixian.android.client.activities.BaseActivity;
import com.shixian.android.client.activities.fragment.ProjectFeedFragment;
import com.shixian.android.client.activities.fragment.UserIndexFragment;
import com.shixian.android.client.model.feeddate.AllItemType;

/**
 * Created by s0ng on 2015/2/12.
 * 这个类要怎么处理
 * 传过来一个feed？ 为了减少创建OnClickListener的个数 这是一个方法吧 但是每个条目还是必须创建一个监听对象的
 */
public class OnAllItemTypeClickController implements View.OnClickListener{

    private AllItemType allItemType;
    private BaseActivity context;



    //穿过来的feed
    public OnAllItemTypeClickController(Activity context, AllItemType allItemType)
    {
        this.allItemType=allItemType;
        this.context= (BaseActivity) context;
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

                    bundle.putSerializable("user",allItemType.user);
                    userid=allItemType.user.id;


                uf.setArguments(bundle);
                context.switchFragment(uf,null);

//                Toast.makeText(context,"tv_name",Toast.LENGTH_SHORT).show();
                break;
            //项目
            case R.id.tv_project:
            //内容
            case R.id.tv_content:

                //点击项目要进入项目界面 我先把项目数据拿到看一看　
                ProjectFeedFragment feedFragment=new ProjectFeedFragment();
                bundle.putString("project_id",allItemType.project.id);
                feedFragment.setArguments(bundle);
                context.switchFragment(feedFragment,null);

//                Toast.makeText(context,"tv_name",Toast.LENGTH_SHORT).show();
                break;

            //图片内容 默认是隐藏的 当feedable_type为image时显示
            case R.id.iv_content:
                Toast.makeText(context,"tv_name",Toast.LENGTH_SHORT).show();
                break;
            //回复框 发表项目的时候是隐藏的
            case R.id.tv_response:
                break;

        }
    }
}

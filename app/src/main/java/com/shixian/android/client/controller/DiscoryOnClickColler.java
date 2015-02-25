package com.shixian.android.client.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.shixian.android.client.R;
import com.shixian.android.client.activities.BaseActivity;
import com.shixian.android.client.activities.DetailActivity;
import com.shixian.android.client.activities.fragment.ProjectFeedFragment;
import com.shixian.android.client.contants.AppContants;
import com.shixian.android.client.model.Comment;
import com.shixian.android.client.model.Feed2;
import com.shixian.android.client.model.Project;

/**
 * Created by s0ng on 2015/2/19.
 */
public class DiscoryOnClickColler implements View.OnClickListener {

    public static final int PROJECT_FRAGMENT = 1;

    private BaseActivity context;
    private Project project;

    public DiscoryOnClickColler(BaseActivity activity, Project project) {
        this.project = project;
        this.context = activity;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.tv_title:
            case R.id.tv_content:
                //点击项目要进入项目界面 我先把项目数据拿到看一看　
                Bundle bundle = new Bundle();
                ProjectFeedFragment feedFragment = new ProjectFeedFragment();
                bundle.putString("project_id", project.id + "");


                Intent intent = new Intent(context, DetailActivity.class);

                bundle.putInt("type", PROJECT_FRAGMENT);


                intent.putExtras(bundle);

                context.startActivity(intent);


//                context.switchFragment(uf,null);

//                Toast.makeText(context,"tv_name",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}

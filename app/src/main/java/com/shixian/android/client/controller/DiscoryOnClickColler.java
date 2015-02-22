package com.shixian.android.client.controller;

import android.os.Bundle;
import android.view.View;

import com.shixian.android.client.R;
import com.shixian.android.client.activities.BaseActivity;
import com.shixian.android.client.activities.fragment.ProjectFeedFragment;
import com.shixian.android.client.model.Project;

/**
 * Created by s0ng on 2015/2/19.
 */
public class DiscoryOnClickColler implements View.OnClickListener {

    private BaseActivity context;
    private Project project;

    public  DiscoryOnClickColler(BaseActivity activity,Project project)
    {
        this.project=project;
        this.context=activity;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.tv_title:
            case R.id.tv_content:
                //点击项目要进入项目界面 我先把项目数据拿到看一看　
                Bundle bundle=new Bundle();
                ProjectFeedFragment feedFragment=new ProjectFeedFragment();
                bundle.putString("project_id",project.id+"");
                feedFragment.setArguments(bundle);
                context.switchFragment(feedFragment,null);
                break;
        }
    }
}

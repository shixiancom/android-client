package com.shixian.android.client.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.shixian.android.client.R;
import com.shixian.android.client.activities.base.SwipeActivity;
import com.shixian.android.client.activities.base.UmengActivity;
import com.shixian.android.client.activities.fragment.AddProjectStepOneFragment;
import com.shixian.android.client.activities.fragment.interfaces.FragmentHelper;


/**
 * Created by tangtang on 15/3/30.
 * 创建 activity
 * 这个是创建项目的activity
 */
public class NewProjectActivity extends SwipeActivity {


    private static final String TAG = "NewProjectActivity";

    private FragmentHelper currentFragment;


    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_newproject);

        initUI();

        initListener();


        addFragment(new AddProjectStepOneFragment());

    }

    public void addFragment(Fragment fragment) {


        android.support.v4.app.FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(null);
        transaction.replace(R.id.continer, fragment).commit();

    }


    private void initListener() {
        // Handle Back Navigation :D
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewProjectActivity.this.onBackPressed();
            }
        });


    }

    private void initUI() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("发布项目");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    public void setCurrentFragment(FragmentHelper currentFragment) {
        this.currentFragment = currentFragment;
    }


    @Override
    public void onBackPressed() {
        currentFragment.onBackPressed();
    }
}

package com.shixian.android.client.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.shixian.android.client.R;
import com.shixian.android.client.activities.fragment.MsgDetialFragment;
import com.shixian.android.client.activities.fragment.ProjectFeedFragment;
import com.shixian.android.client.activities.fragment.UserIndexFragment;
import com.shixian.android.client.activities.fragment.base.BaseFragment;
import com.shixian.android.client.controller.IndexOnClickController;
import com.shixian.android.client.sina.AccessTokenKeeper;
import com.shixian.android.client.utils.SharedPerenceUtil;

import java.util.Random;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBarUtils;
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;

public class DetailActivity extends BaseActivity {



    private Toolbar toolbar;
    private SmoothProgressBar mProgressBar;

    private MenuItem menuItemQuit;

    private boolean isShowQuitItem=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //Utils.configureWindowEnterExitTransition(getWindow());

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Handle Back Navigation :D
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailActivity.this.onBackPressed();
            }
        });


        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
//                    case R.id.action_news_search:
//                        Toast.makeText(MainActivity.this, "Search", Toast.LENGTH_LONG).show();
//                        break;
                    case R.id.action_quit:
                        logout();
                        break;
                }
                return true;
            }
        });






        addFragment();

        mProgressBar= (SmoothProgressBar) findViewById(R.id.pocket);
        mProgressBar.setSmoothProgressDrawableBackgroundDrawable(
                SmoothProgressBarUtils.generateDrawableWithColors(
                        getResources().getIntArray(R.array.pocket_background_colors),
                        ((SmoothProgressDrawable) mProgressBar.getIndeterminateDrawable()).getStrokeWidth()));


    }

    @Override
    protected void addFragment() {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
         switch(getIntent().getIntExtra("type",0))
         {

             case IndexOnClickController.USER_FRAGMENT:
                 UserIndexFragment uf=new UserIndexFragment();
                 uf.setArguments(getIntent().getExtras());


                 fragmentTransaction.replace(R.id.main_fragment_layout, uf);
                 fragmentTransaction.commit();
                 break;

             case IndexOnClickController.PROJECT_FRAGMENT:
                 //点击项目要进入项目界面 我先把项目数据拿到看一看　
                 ProjectFeedFragment feedFragment = new ProjectFeedFragment();
                 feedFragment.setArguments(getIntent().getExtras());
                 fragmentTransaction.replace(R.id.main_fragment_layout,feedFragment);
                 fragmentTransaction.commit();
                 break;

             case IndexOnClickController.TYPE_MSG_DETILA:

                 Bundle bundle=getIntent().getExtras();
                 MsgDetialFragment fragment=new MsgDetialFragment();
                 fragment.setArguments(bundle);
                 fragmentTransaction.replace(R.id.main_fragment_layout,fragment);
                 fragmentTransaction.commit();

                 break;
         }


    }


    public void logout()
    {
        SharedPerenceUtil.clearAllData(this);
        AccessTokenKeeper.clear(this);
        Intent intent=new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void switchFragment(BaseFragment fragment, String key) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

//        fragmentTransaction.

//        NewsFragment newsFragment = new NewsFragment();
        fragmentTransaction.replace(R.id.main_fragment_layout, fragment,"xxx"+ new Random().nextInt(1000));
        fragmentTransaction.addToBackStack(key);
        fragmentTransaction.commit();

    }

    @Override
    public void setLable(String lable) {
        toolbar.setTitle(lable);
    }

    @Override
    public void showProgress() {
        mProgressBar.progressiveStart();
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void dissProgress() {
        mProgressBar.progressiveStop();
        mProgressBar.setVisibility(View.GONE);
    }


    @Override
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        return super.onCreatePanelMenu(featureId, menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detial_menu,menu);

        menuItemQuit=menu.findItem(R.id.action_quit);

        if(isShowQuitItem)
        {
            menuItemQuit.setVisible(true);
        }else{
            menuItemQuit.setVisible(false);
        }

        return super.onCreateOptionsMenu(menu);
    }



    public void hideQuitMenu()
    {
        isShowQuitItem=false;
        invalidateOptionsMenu();

    }

    public void showQuitMenu()
    {
        isShowQuitItem=true;
        invalidateOptionsMenu();

    }


}

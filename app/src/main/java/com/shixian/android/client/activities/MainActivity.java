package com.shixian.android.client.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shixian.android.client.Global;
import com.shixian.android.client.R;
import com.shixian.android.client.activities.fragment.NewsFragment;
import com.shixian.android.client.contants.AppContants;
import com.shixian.android.client.engine.CommonEngine;
import com.shixian.android.client.model.User;
import com.shixian.android.client.utils.ApiUtils;
import com.shixian.android.client.utils.CommonUtil;
import com.shixian.android.client.utils.SharedPerenceUtil;

import org.apache.http.Header;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by doom on 15/2/2.
 */
public class MainActivity extends ActionBarActivity {
    private String TAG = "MainActivity";
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;



    //touxiang头像
    private ImageView iv_icon;
    //昵称
    private TextView tv_uname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_main);



        initUI();
        addFragment();

        initDate();
    }

    private void initDate() {

        //初始化用户头像id
        initUserInfo();
        //初始化用户的项目
        initUserProjects();

    }

    private void initUserProjects() {
        //获取用户项目
        ApiUtils.get(AppContants.URL_MY_PROJECT_INFO,null,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String json=new String(bytes);
                if(!AppContants.errorMsg.equals(json))
                {
                    Gson gson=new Gson();

                        //TODO
                        //这里需要解析json数组  放在listView里面，先不写这里了 还有三个条目没写呢


                }

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }

    /**
     * 初始化左侧抽屉user 信息
     */
    private void initUserInfo() {
        CommonEngine.getMyUserInfo(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {

//                CommonUtil.logDebug(TAG, new String(bytes));

                String json=new String(bytes);
                //出错
                if(!AppContants.errorMsg.equals(json))
                {
                    Gson gson=new Gson();
                    User user=gson.fromJson(json, User.class);
                    tv_uname.setText(user.username);
                    SharedPerenceUtil.putUserInfo(MainActivity.this, json);

                    //异步下载图片(头像)
                    ApiUtils.get(AppContants.DOMAIN+user.avatar.small.url,null,new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, final byte[] bytes) {
                            Bitmap icon=BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            iv_icon.setImageBitmap(icon);

                            //将投向保存在本地
                            new Thread(){
                                public void run(){
                                    File file=new File(MainActivity.this.getFilesDir().getAbsolutePath()+AppContants.USER_ICON_NAME);
                                    try {
                                        FileOutputStream fos=new FileOutputStream(file);
                                        fos.write(bytes,0,bytes.length);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }.start();



                        }

                        @Override
                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                        }
                    });

                }

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                CommonUtil.logDebug(TAG, new String(bytes));
            }
        });
    }

    private void initUI() {
        drawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.action_news_search:
                        Toast.makeText(MainActivity.this, "Search", Toast.LENGTH_LONG).show();
                        break;
                }
                return true;
            }
        });
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerOpened(View drawerView) {
                Toast.makeText(MainActivity.this, "打开", Toast.LENGTH_LONG).show();
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                Toast.makeText(MainActivity.this, "关闭", Toast.LENGTH_LONG).show();
                super.onDrawerClosed(drawerView);
            }
        };
        drawerToggle.syncState();
        drawerLayout.setDrawerListener(drawerToggle);


        tv_uname= (TextView) findViewById(R.id.tv_uname);
        iv_icon= (ImageView) findViewById(R.id.iv_icon);
        if(Global.user!=null)
        {
            if(TextUtils.isEmpty(Global.user.username))
                tv_uname.setText(Global.user.username);
        }


        iv_icon.setImageBitmap(BitmapFactory.decodeFile(getFilesDir().getAbsolutePath()+AppContants.USER_ICON_NAME));

    }

    private void addFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        NewsFragment newsFragment = new NewsFragment();
        fragmentTransaction.replace(R.id.main_fragment_layout, newsFragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

}

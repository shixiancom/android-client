package com.shixian.android.client.activities;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shixian.android.client.Global;
import com.shixian.android.client.R;
import com.shixian.android.client.activities.fragment.NewsFragment;
import com.shixian.android.client.activities.fragment.base.BaseFragment;
import com.shixian.android.client.contants.AppContants;
import com.shixian.android.client.engine.CommonEngine;
import com.shixian.android.client.model.SimpleProject;
import com.shixian.android.client.model.User;
import com.shixian.android.client.utils.ApiUtils;
import com.shixian.android.client.utils.SharedPerenceUtil;
import com.shixian.android.client.views.ParentListView;
import com.shixian.android.client.views.SubListView;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by doom on 15/2/2.
 */
public class MainActivity extends ActionBarActivity {
    private String TAG = "MainActivity";
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;

    //抽屉里的子listView
    private List<SimpleProject> projectList;
    private ProjectAdapter projectAdapter;
    private SubListView subListView;


    //抽屉里的主ListView
    private ListView lv_menu;


    //touxiang头像
    private ImageView iv_icon;
    //昵称
    private TextView tv_uname;

    //存放我的项目
    private String myProjectjson;


    //userinfo本地缓存
    private String userInfo;

    /**
     * 丑陋的进度条 搞好逻辑之后替换
     */
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_main);

        Global.MAIN=this;

        initUI();
        addFragment();

        initDate();
    }

    private void initDate() {

        //初始化用户头像id
        initUserInfo();
        //初始化用户的项目
        initUserProjects();

        initIndexDate();

    }

    /**
     * 初始化主页数据
     */
    private void initIndexDate() {
        //终于搞主页了  在搞主页之前我觉得应该弄一个牛逼的进度条  算了 有空再弄吧 先把逻辑写出来吧

//        progressDialog.show();




    }

    private void initUserProjects() {
        projectList = new ArrayList<>();

        myProjectjson = SharedPerenceUtil.getMyProject(this);

        //获取用户项目
        ApiUtils.get(AppContants.URL_MY_PROJECT_INFO, null, new AsyncHttpResponseHandler() {
            @Override
            public void onFinish() {
                super.onFinish();
            }

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {


                myProjectjson = new String(bytes);
                if (!AppContants.errorMsg.equals(myProjectjson)) {
                    projectList.clear();
                    Gson gson = new Gson();
                    try {
                        JSONArray array = new JSONArray(myProjectjson);
                        for (int j = 0; j < array.length(); j++) {
                            SimpleProject sp = gson.fromJson(array.getString(j), SimpleProject.class);


                            projectList.add(sp);

                            SharedPerenceUtil.putMyProject(MainActivity.this, myProjectjson);

                            if (projectAdapter == null) {
                                projectAdapter = new ProjectAdapter();
                                //TODO 给listView设置上 现在设置到主lv里面了 明天再改 //还需要做缓存
                                subListView.setAdapter(projectAdapter);
                                lv_menu.setAdapter(new MenuAdapter());
//                                Utility.setListViewHeightBasedOnChildren(subListView);


                            } else {
                                projectAdapter.notifyDataSetChanged();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


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
        userInfo=SharedPerenceUtil.getUserInfo(this);

        CommonEngine.getMyUserInfo(new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {

//                CommonUtil.logDebug(TAG, new String(bytes));

                userInfo = new String(bytes);
                //出错
                if (!AppContants.errorMsg.equals(userInfo)) {
                    Gson gson = new Gson();
                    User user = gson.fromJson(userInfo, User.class);
                    Global.USER_ID=user.id;
                    tv_uname.setText(user.username);
                    SharedPerenceUtil.putUserInfo(MainActivity.this, userInfo);

                    //异步下载图片(头像)
                    ApiUtils.get(AppContants.DOMAIN + user.avatar.small.url, null, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, final byte[] bytes) {
                            Bitmap icon = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                            iv_icon.setImageBitmap(icon);

                            //将投向保存在本地
                            new Thread() {
                                public void run() {
                                    File file = new File(MainActivity.this.getFilesDir().getAbsolutePath() + AppContants.USER_ICON_NAME);
                                    try {
                                        FileOutputStream fos = new FileOutputStream(file);
                                        fos.write(bytes, 0, bytes.length);
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
//                CommonUtil.logDebug(TAG, new String(bytes));
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


        tv_uname = (TextView) findViewById(R.id.tv_uname);
        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        if (Global.user != null) {
            if (TextUtils.isEmpty(Global.user.username))
                tv_uname.setText(Global.user.username);
        }


        iv_icon.setImageBitmap(BitmapFactory.decodeFile(getFilesDir().getAbsolutePath() + AppContants.USER_ICON_NAME));

        lv_menu = (ParentListView) findViewById(R.id.lv_menu);


        subListView = new SubListView(MainActivity.this);

        progressDialog =new ProgressDialog(MainActivity.this);

    }

    private void addFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        IndexFragment newsFragment=new IndexFragment();
        NewsFragment newsFragment = new NewsFragment();

//        DiscoryProjectFragment newsFragment=new DiscoryProjectFragment();
        fragmentTransaction.replace(R.id.main_fragment_layout, newsFragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    private class ProjectAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return projectList.size();
        }

        @Override
        public Object getItem(int position) {
            return projectList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view;
            ProjectHolder holder;
            if (convertView == null) {
                view = View.inflate(MainActivity.this, R.layout.lv_project_item, null);
                holder = new ProjectHolder();
                holder.tv_title = (TextView) view.findViewById(R.id.tv_title);
                view.setTag(holder);

            } else {
                view = convertView;
                holder = (ProjectHolder) view.getTag();
            }


            holder.tv_title.setText(projectList.get(position).getTitle());

            return view;
        }
    }


    class ProjectHolder {
        TextView tv_title;
    }


    class MenuAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TextView tv = new TextView(MainActivity.this);
            tv.setBackgroundColor(Color.BLUE);
            tv.setText("xxxxxxxxxxxxxx");
            switch (position) {
                case 0:

                case 1:
                case 2:

                case 3:
                    return tv;
                case 4:
                    return subListView;
            }

            return tv;

        }
    }


    public void showProgress()
    {
        progressDialog.show();
    }

    public void dissProgress()
    {
        progressDialog.dismiss();
    }


    public void switchFragment(BaseFragment fragment,String key)
    {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

//        fragmentTransaction.


        fragmentTransaction.addToBackStack(null);
//        NewsFragment newsFragment = new NewsFragment();
        fragmentTransaction.replace(R.id.main_fragment_layout, fragment);
        fragmentTransaction.commit();




    }

}

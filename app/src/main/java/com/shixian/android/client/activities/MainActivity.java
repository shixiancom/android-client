package com.shixian.android.client.activities;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shixian.android.client.Global;
import com.shixian.android.client.R;
import com.shixian.android.client.activities.fragment.DiscoryProjectFragment;
import com.shixian.android.client.activities.fragment.IndexFragment;
import com.shixian.android.client.activities.fragment.NewsFragment;
import com.shixian.android.client.activities.fragment.ProjectFeedFragment;
import com.shixian.android.client.activities.fragment.base.BaseFragment;
import com.shixian.android.client.contants.AppContants;
import com.shixian.android.client.controller.IndexOnClickController;
import com.shixian.android.client.engine.CommonEngine;
import com.shixian.android.client.model.NewsSataus;
import com.shixian.android.client.model.SimpleProject;
import com.shixian.android.client.model.User;
import com.shixian.android.client.sina.AccessTokenKeeper;
import com.shixian.android.client.utils.ApiUtils;
import com.shixian.android.client.utils.CommonUtil;
import com.shixian.android.client.utils.DisplayUtil;
import com.shixian.android.client.utils.SharedPerenceUtil;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;


import cn.jpush.android.api.JPushInterface;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBarUtils;
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;
import android.util.TypedValue;
import android.widget.ToggleButton;


/**
 * Created by doom on 15/2/2.
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {

    private String TAG = "MainActivity";


    /**
     * 如果是主页的话该值为true
     */
    private boolean isIndex;

    private boolean onBackQuit=false;

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;

    private Drawable drawable;

    //抽屉里的子listView
    private List<SimpleProject> projectList;
    private MenuAdapter projectAdapter;


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
    private SmoothProgressBar mProgressBar ;


    private LinearLayout ll_descory;
    private LinearLayout ll_index;
    private RelativeLayout ll_msg;

    //用于记录 当前属于哪个
    private int current_menuid=R.id.ll_index;

   //private RedPointView titleImgPoint;
   private TextView tv_msg_count;
   private ImageView iv_msg;

    private User user;

    private TextView toastView;

    /**
     * Toast 的Params
     */
    private WindowManager.LayoutParams mParams;

    private WindowManager windowManager;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_main);

        Global.MAIN=this;
        Global.context=this;
        Global.screenWidth= CommonUtil.getScreenWidth(this);

        initUI();

        initJpush();

        addFragment();

        initDate();
    }



    private void initDate() {

        //初始化用户头像id
        initUserInfo();
        //初始化用户的项目
        initUserProjects();



    }

    private void initMsgStatus() {
        ApiUtils.get(AppContants.MSG_STATUS_URL,null,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final byte[] bytes) {
                Gson gson=new Gson();
                final NewsSataus status=gson.fromJson(new String(bytes), NewsSataus.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        //TODO

                       // settingMsgCount(1);

                        if(status.total!=0)
                            settingMsgCount(status.total);

                        else{

                           hideMsg();

                        }

                    }
                });
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(MainActivity.this, getString(R.string.check_net), Toast.LENGTH_SHORT);
            }
        });
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
                                projectAdapter = new MenuAdapter();


                                lv_menu.setAdapter(projectAdapter);


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
                    MainActivity.this.user=user;
                    Global.USER_ID=user.id;
                    Global.USER_NAME=user.username;


                    //发送推送需要的信息
                    sendJpushData();

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





        windowManager=getWindowManager();


        Global.iv_conte_size= DisplayUtil.dip2px(this,200);


        drawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);




        drawable=toolbar.getNavigationIcon();

       // bt_msg_count= (Button) findViewById(R.id.bt_msg_count);



        setSupportActionBar(toolbar);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
//                    case R.id.action_news_search:
//                        Toast.makeText(MainActivity.this, "Search", Toast.LENGTH_LONG).show();
//                        break;
//                    case R.id.action_quit:
//                        logout();
//                        break;
                }
                return true;
            }
        });
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.app_name, R.string.app_name) {
            @Override
            public void onDrawerOpened(View drawerView) {
  //              Toast.makeText(MainActivity.this, "打开", Toast.LENGTH_LONG).show();
                super.onDrawerOpened(drawerView);

                //打开重新加载数据
                //TODO
                tv_uname.setText(user.username);

                initUserProjects();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
            //    Toast.makeText(MainActivity.this, "关闭", Toast.LENGTH_LONG).show();
                super.onDrawerClosed(drawerView);

            }
        };

        mProgressBar= (SmoothProgressBar) findViewById(R.id.pocket);
        mProgressBar.setSmoothProgressDrawableBackgroundDrawable(
                SmoothProgressBarUtils.generateDrawableWithColors(
                        getResources().getIntArray(R.array.pocket_background_colors),
                        ((SmoothProgressDrawable) mProgressBar.getIndeterminateDrawable()).getStrokeWidth()));

        drawerToggle.syncState();
        drawerLayout.setDrawerListener(drawerToggle);


        tv_uname = (TextView) findViewById(R.id.tv_uname);
        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        if (Global.user != null) {
            if (TextUtils.isEmpty(Global.user.username))
                tv_uname.setText(Global.user.username);
        }


        iv_icon.setImageBitmap(BitmapFactory.decodeFile(getFilesDir().getAbsolutePath() + AppContants.USER_ICON_NAME));

        lv_menu = (ListView) findViewById(R.id.lv_menu);



        lv_menu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SimpleProject project=projectList.get(position);
                if(project!=null)
                {
                    //跳转到项目页面
                    Bundle bundle=new Bundle();
                    ProjectFeedFragment feedFragment=new ProjectFeedFragment();
                    bundle.putString("project_id",project.getId()+"");

                    bundle.putInt("type", IndexOnClickController.PROJECT_FRAGMENT);
                    Intent intent=new Intent(MainActivity.this,DetailActivity.class);
                    intent.putExtras(bundle);
                    MainActivity.this.startActivity(intent);

                }

            }
        });

        /**
         * 抽屉中的三个选项
         */
        ll_descory= (LinearLayout) findViewById(R.id.ll_descory);
        ll_msg= (RelativeLayout) findViewById(R.id.ll_msg);
        ll_index= (LinearLayout) findViewById(R.id.ll_index);

        ll_descory.setOnClickListener(this);
        ll_msg.setOnClickListener(this);
        ll_index.setOnClickListener(this);



        //设置左侧抽屉的宽度等于屏幕宽度减去Toolbar的高度
        LinearLayout ll_left= (LinearLayout) findViewById(R.id.ll_left);
        int actionBarHeight=0;
        TypedValue tv = new TypedValue();
        if (this.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, this.getResources().getDisplayMetrics());
        }
        ll_left.getLayoutParams().width=Global.screenWidth-actionBarHeight;



        //现实消息
       // showMsg(5);
        iv_msg= (ImageView) findViewById(R.id.iv_msg);
        //TODO
      //  titleImgPoint=new RedPointView(this,toolbar);
          tv_msg_count= (TextView) findViewById(R.id.tv_msg_count);



        //头像的点击事件 和用户名的点击事件
        View.OnClickListener userOnClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(user!=null) {
                    Bundle bundle = new Bundle();

                    bundle.putInt("type", IndexOnClickController.USER_FRAGMENT);

                    bundle.putSerializable("user", user);
                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                    intent.putExtras(bundle);

                    MainActivity.this.startActivity(intent);
                    drawerLayout.closeDrawers();
                }

            }
        };

        tv_uname.setOnClickListener(userOnClickListener);
        iv_icon.setOnClickListener(userOnClickListener);


    }

    protected void addFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        IndexFragment indexFragment=new IndexFragment();
        fragmentTransaction.replace(R.id.main_fragment_layout, indexFragment);
        fragmentTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }



    @Override
    public void onClick(View v) {

        onBackQuit=false;
        switch (v.getId())
        {
            case R.id.ll_descory:
                switchFragment(new DiscoryProjectFragment(),null);
                isIndex=false;
                break;
            case R.id.ll_index:
                isIndex=true;
                switchFragment(new IndexFragment(),null);

                break;
            case R.id.ll_msg:
                hideMsg();
                switchFragment(new NewsFragment(),null);
                isIndex=false;
                break;
        }

    }


    private class MenuAdapter extends BaseAdapter {

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
                holder.iv_sit= (ImageView) view.findViewById(R.id.iv_sit);

                view.setTag(holder);

            } else {
                view = convertView;
                holder = (ProjectHolder) view.getTag();
            }


            holder.iv_sit.setAlpha(0.54f);

            holder.tv_title.setText(projectList.get(position).getTitle());

            return view;
        }
    }


    class ProjectHolder {
        TextView tv_title;
        ImageView iv_sit;
    }





    public void showProgress()
    {
        mProgressBar.progressiveStart();
        mProgressBar.setVisibility(View.VISIBLE);
    }

    public void dissProgress()
    {
        mProgressBar.progressiveStop();
        mProgressBar.setVisibility(View.GONE);
    }





    public void switchFragment(BaseFragment fragment,String key)
    {

        onBackQuit=false;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if(fragment instanceof  IndexFragment)
        {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        }

//        fragmentTransaction.

//        NewsFragment newsFragment = new NewsFragment();

        fragmentTransaction.replace(R.id.main_fragment_layout, fragment);
        fragmentTransaction.addToBackStack(key);
        fragmentTransaction.commit();
        drawerLayout.closeDrawers();

    }

    public void  setLable(String lable)
    {
        toolbar.setTitle(lable);
    }


    public void settingMsgCount(int count)
    {


        if(count>99)
            count=99;

        showMsg(count);


    }

    @Override
    public void onBackPressed() {

        if(isIndex)
        {
            if(onBackQuit)
            {

                super.onBackPressed();
            }else {
                onBackQuit=true;
                Toast.makeText(this,"再次按返回键退出",Toast.LENGTH_SHORT).show();
            }
        }else{
            super.onBackPressed();
        }




    }

    //退出登陆操作  首先要清理数据 启动LoginActivity页面 然后finish
    public void logout()
    {
        SharedPerenceUtil.clearAllData(this);
        AccessTokenKeeper.clear(this);
        Intent intent=new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

/*****************************************************************/
    /**
     * 现实消息数量
     */
    public  void showMsg(int count)
    {

//        titleImgPoint.setContent(count);
//        titleImgPoint.setSizeContent(16);
//        titleImgPoint.setColorContent(Color.WHITE);
//        titleImgPoint.setColorBg(Color.RED);
//        titleImgPoint.setPosition(Gravity.CENTER, Gravity.CENTER);


        tv_msg_count.setVisibility(View.VISIBLE);


        tv_msg_count.setText(count+"");

 //       showMsg(count);

        showMyToast(count);




    }


    public void hideMsg()
    {
      //  titleImgPoint.hide();
        tv_msg_count.setVisibility(View.GONE);
        if(toastView!=null) {
            windowManager.removeView(toastView);
            toastView=null;
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        drawerLayout.closeDrawers();
    }


    /**
     * 自定义Toast显示我们的消息数量
     * @param count
     */
    public void showMyToast(int count) {
        if(toastView!=null)
        {
            getWindowManager().removeView(toastView);
            toastView=null;
        }

        toastView = new TextView(this);
        toastView.setTextColor(Color.WHITE);
        toastView.setTextSize(11);
        toastView.setBackgroundResource(R.drawable.cicle_msg);

        toastView.setText(count+"");
        toastView.setGravity(Gravity.CENTER);

        mParams = new WindowManager.LayoutParams();
        mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;



        mParams.gravity = Gravity.LEFT+Gravity.TOP;

        mParams.x=40;
        mParams.y=40;
        mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE   ◊‘∂®“ÂµƒÕ¡Àæ–Ë“™”√ªß¥•√˛
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        mParams.format = PixelFormat.TRANSLUCENT;
//		mParams.type = WindowManager.LayoutParams.TYPE_TOAST; Õ¡Àæ¥∞ÃÂÃÏ…˙≤ªœÏ”¶¥•√˛ ¬º˛
        mParams.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;


        windowManager.addView(toastView, mParams);

        // ∏¯view∂‘œÛ…Ë÷√“ª∏ˆ¥•√˛ ¬º˛°£
//        toastView.setOnTouchListener(new View.OnTouchListener() {
//            int startX  ;
//            int startY  ;
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//
//                        startX = (int) event.getRawX();
//                        startY = (int) event.getRawY();
//
//                        break;
//                    case MotionEvent.ACTION_MOVE:
//
//                        int newX = (int) event.getRawX();
//                        int newY = (int) event.getRawY();
//
//                        int dx = newX - startX;
//                        int dy = newY - startY;
//
//
//                        mParams.x +=dx;
//                        mParams.y +=dy;
//                        if(mParams.x<0){
//                            mParams.x = 0;
//                        }
//                        if(mParams.y<0){
//                            mParams.y = 0;
//                        }
//                        if(mParams.x>(windowManager.getDefaultDisplay().getWidth()-toastView.getWidth())){
//                            mParams.x=(windowManager.getDefaultDisplay().getWidth()-toastView.getWidth());
//                        }
//                        if(mParams.y>(windowManager.getDefaultDisplay().getHeight()-toastView.getHeight())){
//                            mParams.y=(windowManager.getDefaultDisplay().getHeight()-toastView.getHeight());
//                        }
//                        windowManager.updateViewLayout(toastView, mParams);
//                        startX = (int) event.getRawX();
//                        startY = (int) event.getRawY();
//                        break;
//
//                    case MotionEvent.ACTION_UP:
//
//                        SharedPreferences.Editor editor = sp.edit();
//                        editor.putInt("lastx", mParams.x);
//                        editor.putInt("lasty", mParams.y);
//                        editor.commit();
//                        break;
//                }
//                return true;
//            }
//        });
   }

    /******************************************************JPUSH****************/
    /**
     * 发送极光推送信息
     */
    private void sendJpushData() {

        String regId=JPushInterface.getRegistrationID(this);
        RequestParams params=new RequestParams();
        params.put("reg_id",regId);
        params.put("uid",Global.USER_ID);
        params.put("device",CommonUtil.getImei(this,""));


        Log.i("AAAA1","reg:"+regId+" uid:"+Global.USER_ID+" device:"+CommonUtil.getImei(this,""));
        ApiUtils.post(AppContants.DEBUG_JPUSH_NEED_URL,params,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int position, Header[] headers, byte[] bytes) {
                Toast.makeText(MainActivity.this,"发送成功",Toast.LENGTH_LONG).show();

                Log.i("AAAAA",new String(bytes));
            }

            @Override
            public void onFailure(int position, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });



    }

    /**
     * 初始化极光推送
     */
    private void initJpush() {

        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }

    /*********重写的生命周期方法****************/
    @Override
    protected void onDestroy() {

        if(toastView!=null) {
            getWindowManager().removeView(toastView);
        }
        super.onDestroy();
    }


    @Override
    protected void onResume() {
        super.onResume();
        initMsgStatus();
    }
}

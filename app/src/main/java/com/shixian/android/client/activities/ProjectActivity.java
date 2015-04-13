package com.shixian.android.client.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shixian.android.client.R;
import com.shixian.android.client.activities.base.BaseFeedActivity;
import com.shixian.android.client.activities.fragment.base.BaseFeedFragment;
import com.shixian.android.client.anmi.ExpandAnimation;
import com.shixian.android.client.contants.AppContants;
import com.shixian.android.client.controller.IndexOnClickController;
import com.shixian.android.client.engine.CommonEngine;
import com.shixian.android.client.handler.content.ContentHandler;
import com.shixian.android.client.handler.feed.BaseFeedHandler;
import com.shixian.android.client.model.Comment;
import com.shixian.android.client.model.Feed2;
import com.shixian.android.client.model.Project;
import com.shixian.android.client.model.feeddate.BaseFeed;
import com.shixian.android.client.sina.Constants;
import com.shixian.android.client.sina.WeiBoUtils;
import com.shixian.android.client.utils.ApiUtils;
import com.shixian.android.client.utils.CommonUtil;
import com.shixian.android.client.utils.JsonUtils;
import com.shixian.android.client.utils.SharedPerenceUtil;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import org.apache.http.Header;

/**
 * Created by tangtang on 15/4/2.
 */
public class ProjectActivity extends BaseFeedActivity {



    public  static final int RESULT_ADD_IDEA=10087;

    public static final String PROJECT_ID="projectid";

    public static final int ANMI_DRUATION=300;

    public static final int RESULT_NOSEND=1000;



    private Project project=new Project();


    //动画期间不容许点击  纪录动画是否在执行
    private boolean anmi_state=true;



    private String project_info;

    private String TAG="ProjectFeedFragment";

    private TextView tv_caogao;


    /** 微博微博分享接口实例 */
    private IWeiboShareAPI mWeiboShareAPI = null;



    private boolean hasCaogao=false;


    protected void initCacheData() {

        firstPageDate= SharedPerenceUtil.getProjectIndexFeed(this.getApplicationContext(), project.id);

        String projectInfo=SharedPerenceUtil.getProjectIndexInfo(this.getApplicationContext(), project.id + "");

        if(!TextUtils.isEmpty(projectInfo))
            project=new Gson().fromJson(projectInfo, Project.class);
        feedList = JsonUtils.ParseFeeds(firstPageDate);

        if (adapter == null) {
            adapter = new ProjectFeedAdapter();
            pullToRefreshListView.getListView().setAdapter(adapter);

        } else {
            pullToRefreshListView.getListView().setAdapter(adapter);
            adapter.notifyDataSetChanged();

        }


    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // 创建微博分享接口实例
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, Constants.APP_KEY);

        // 注册第三方应用到微博客户端中，注册成功后该应用将显示在微博的应用列表中。
        // 但该附件栏集成分享权限需要合作申请，详情请查看 Demo 提示
        // NOTE：请务必提前注册，即界面初始化的时候或是应用程序初始化时，进行注册
        mWeiboShareAPI.registerApp();


    }





    @Override
    protected void initFirst() {
        project.id= Integer.parseInt(getIntent().getStringExtra("project_id"));

        initCacheData();
        initFirstData();
    }

    @Override
    protected void initLable() {
        toolbar.setTitle(getString(R.string.label_project));
    }

    @Override
    protected void getNextData() {
        page += 1;
        CommonEngine.getFeedData(ProjectActivity.this,AppContants.PROJECT_FEED_URL.replace("{project_id}", project.id + ""), page, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, final byte[] bytes) {
                final String temp = new String(bytes);
                if (!AppContants.errorMsg.equals(bytes)) {
                    //获取第一页数据

                    new Thread() {
                        public void run() {


                            //数据格式
                            CommonUtil.logDebug(TAG, new String(bytes));


                            feedList.addAll(JsonUtils.ParseFeeds(temp));

                            //TODO 第一页的缓存

                            //保存数据到本地
                            ProjectActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (adapter == null) {
                                        adapter = new ProjectFeedAdapter();

                                        pullToRefreshListView.getRefreshableView().setAdapter(adapter);
                                    } else {
                                        adapter.notifyDataSetChanged();
                                    }

                                    pullToRefreshListView.onPullUpRefreshComplete();
                                }
                            });

                        }
                    }.start();


                } else {
                    pullToRefreshListView.onPullDownRefreshComplete();
                }
            }


            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {


                //TODO 错误可能定义的不是太准确  最后一天调整
                Toast.makeText(ProjectActivity.this, getString(R.string.check_net), Toast.LENGTH_SHORT).show();
                pullToRefreshListView.onPullUpRefreshComplete();
                page -= 1;
            }
        });

    }

    @Override
    public void initDate(Bundle savedInstanceState) {

        if(feedList!=null&&feedList.size()>0)
        {
            if (adapter == null) {
                adapter = new ProjectFeedAdapter();


                pullToRefreshListView.getRefreshableView().setAdapter(adapter);
            } else {
                pullToRefreshListView.getRefreshableView().setAdapter(adapter);

            }



        }else{

            initFirst();
        }

    }

    @Override
    public void initFirstData() {
        //开始搞

     //   initCacheData();

        initProjectInfo();

        initProjectFeed();

    }




    private void initProjectFeed() {

        page=1;
        this.showProgress();

        CommonEngine.getFeedData(ProjectActivity.this,AppContants.PROJECT_FEED_URL.replace("{project_id}",project.id+"" ), page, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {

                final String temp = new String(bytes);
                if (!AppContants.errorMsg.equals(temp)) {

                    new Thread() {
                        public void run() {

                            firstPageDate = temp;
                            feedList = JsonUtils.ParseFeeds(firstPageDate);




                            ProjectActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (adapter == null) {
                                        adapter = new ProjectFeedAdapter();
                                        pullToRefreshListView.getRefreshableView().setAdapter(adapter);
                                    } else {
                                        adapter.notifyDataSetChanged();
                                    }

                                    pullToRefreshListView.onPullDownRefreshComplete();


                                    pullToRefreshListView.getFooterLoadingLayout().show(false);

                                    ProjectActivity.this.dissProgress();
                                }
                            });
                            SharedPerenceUtil.putProjectIndexFeed( ProjectActivity.this.getApplicationContext(), temp, project.id);



                        }
                    }.start();

                }else{
                    pullToRefreshListView.onPullDownRefreshComplete();
                    pullToRefreshListView.getFooterLoadingLayout().show(false);
                }
                //adapter
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText( ProjectActivity.this, R.string.check_net, Toast.LENGTH_SHORT).show();
                pullToRefreshListView.onPullDownRefreshComplete();
                pullToRefreshListView.onPullUpRefreshComplete();
                ProjectActivity.this.dissProgress();
            }


        });


    }

    private void initProjectInfo() {


        ApiUtils.get(ProjectActivity.this,AppContants.PROJECT_INFO_URL.replace("{project_id}", project.id + ""), null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {

                //
                final String temp = new String(bytes);
                if (!AppContants.errorMsg.equals(temp))
                    new Thread() {
                        public void run() {
                            project_info = temp;
                            Gson gson = new Gson();
                            project = gson.fromJson(project_info, Project.class);



                            ProjectActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (adapter == null) {
                                        adapter = new ProjectFeedAdapter();
                                        pullToRefreshListView.getRefreshableView().setAdapter(adapter);
                                    } else {
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            });
                            SharedPerenceUtil.putProjectIndexInfo( ProjectActivity.this.getApplicationContext(), temp, project.id);


                        }
                    }.start();

            }


            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText( ProjectActivity.this, R.string.check_net, Toast.LENGTH_SHORT).show();
            }
        });
    }



    public static final int TYPE_PROJECT=2;
    class ProjectFeedAdapter extends BaseFeedAdapter {

        @Override
        public int getCount() {
            return (1+feedList.size());
        }

        @Override
        public Object getItem(int position) {
            if(position==0)
                return project;
            return feedList.get(position-1);
        }

        @Override
        public long getItemId(int position) {
            return  position;
        }


        @Override
        public int getViewTypeCount() {
            return 3;

        }

        @Override
        public int getItemViewType(int position) {
            if(position==0)
                return 2;

            return feedList.get(position-1).type;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {


            View view = null;


            int itemType = getItemViewType(position);
            switch (itemType) {
                case TYPE_PROJECT:

                    view=View.inflate( ProjectActivity.this,R.layout.project_index_item,null);

                    if(project!=null)
                    {
                        TextView tv_name= (TextView) view.findViewById(R.id.tv_name);

                        final Button bt_follow= (Button) view.findViewById(R.id.bt_follow);

                        final Button bt_shrinkage= (Button) view.findViewById(R.id.bt_shrinkage);

                        final TextView tv_content= (TextView) view.findViewById(R.id.tv_content);

                        tv_name.setText(project.title);
                        if(project.description!=null)
                        {
                            ContentHandler contentHandler= new ContentHandler(tv_content).longClickCopy();
                            contentHandler.formatColorContent(tv_content,project.description);

                        }

                        if(project.has_followed)
                        {
                            bt_follow.setBackgroundResource(R.drawable.shape_unfollow);
                            bt_follow.setText(R.string.following);
                            bt_follow.setVisibility(View.GONE);
                            bt_shrinkage.setVisibility(View.VISIBLE);
                            tv_content.setVisibility(View.GONE);
                        }else{
                            bt_follow.setBackgroundResource(R.drawable.shape_follow);
                            bt_follow.setText(R.string.follow);
                            bt_follow.setVisibility(View.VISIBLE);
                            bt_shrinkage.setVisibility(View.GONE);
                        }

                        tv_caogao= (TextView) view.findViewById(R.id.tv_caogao);
                        if(hasCaogao)
                        {
                            tv_caogao.setVisibility(View.VISIBLE);
                        }else{
                            tv_caogao.findViewById(R.id.tv_caogao).setVisibility(View.GONE);
                        }

                       LinearLayout ll_addidea= (LinearLayout) view.findViewById(R.id.ll_addidea);

                        ll_addidea.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent=new Intent(ProjectActivity.this,AddIdeaActivity.class);


                                if(project.id==0)
                                {
                                    return;
                                }
                                intent.putExtra(PROJECT_ID,project.id+"");

                                startActivityForResult(intent, RESULT_ADD_IDEA);

                            }
                        });



                        bt_shrinkage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                execAnmi(bt_shrinkage,tv_content);

                            }
                        });

                        bt_follow.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(project.has_followed)
                                {
                                    //取消关注api
                                    //关注api
//                           ApiUtils.post(String.format(AppContants.USER_UNFOLLOW_URL,user.id),null,new AsyncHttpResponseHandler() {
//                               @Override
//                               public void onSuccess(int i, Header[] headers, byte[] bytes) {
//                                   Toast.makeText(context,"取消关注成功",Toast.LENGTH_SHORT).show();
//                                   bt_follow.setBackgroundColor(Color.argb(0,32,168,192+15));//20a8cf
//                                   user.has_followed=false;
//                               }
//
//                               @Override
//                               public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
//                                   Toast.makeText(context,"取消关注失败，稍后再试",Toast.LENGTH_SHORT).show();
//                               }
//                           });

                                    //Toast.makeText(context,"暂不支持取消功能，我们正在飞速开发",Toast.LENGTH_SHORT).show();

                                }else{
                                    //关注api
                                    ApiUtils.post(ProjectActivity.this,String.format(AppContants.PROJECT_FOLLOW_URL,project.id),null,new AsyncHttpResponseHandler() {
                                        @Override
                                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
                                            Toast.makeText( ProjectActivity.this,"关注成功",Toast.LENGTH_SHORT).show();
                                            bt_follow.setBackgroundResource(R.drawable.shape_unfollow);
                                            bt_follow.setText(R.string.following);
                                            project.has_followed=true;

                                            bt_follow.setVisibility(View.GONE);
                                            bt_shrinkage.setVisibility(View.VISIBLE);
                                            execAnmi(tv_content);


                                        }

                                        @Override
                                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                                            Toast.makeText( ProjectActivity.this,"关注失败，稍后再试",Toast.LENGTH_SHORT).show();

                                        }
                                    });
                                }
                            }
                        });

                    }
                    break;

                case BaseFeed.TYPE_FEED:



                    view= BaseFeedHandler.initFeedItemView2(ProjectActivity.this, convertView);
                    BaseFeedFragment.FeedHolder feedHolder= (BaseFeedFragment.FeedHolder) view.getTag();

/******************************************************************/
                    Feed2 feed = (Feed2) feedList.get(position - 1);
                    feed.position=position-1;
                    BaseFeedHandler.initFeedItemViewData(ProjectActivity.this, feed, feedHolder, animateFirstListener);
/**************************************************/
                    initFeedItemOnClick(feed,feedHolder);

                    BaseFeedHandler.setFeedCommonClick(ProjectActivity.this, feed, feedHolder);



                    break;

                case BaseFeed.TYPE_COMMENT:

                    view= BaseFeedHandler.initCommentItem(ProjectActivity.this, convertView);



                    BaseFeedFragment.CommentHolder commentHolder = (BaseFeedFragment.CommentHolder) view.getTag();


/**************************************************************/
                    Comment comment = (Comment) feedList.get(position - 1);
                    BaseFeedHandler.initCommentItemData(comment, commentHolder, animateFirstListener);


/******************************************/

                    initCommentItemOnClick(comment,commentHolder);
                    break;


            }


            return view;
        }
    }





    /***********************Adapter 点击事件 **************************/
    /***********************Adapter 点击事件 **************************/
    private void initFeedItemOnClick(final Feed2 feed,final BaseFeedFragment.FeedHolder feedHolder) {

        //设置点击事件


        IndexOnClickController controller = new IndexOnClickController(this, feed);




        //点击头像和名字的响应事件是一致的 如果展示的是我的主页 再次点击不会响应

        feedHolder.iv_icon.setOnClickListener(controller);
        feedHolder.tv_name.setOnClickListener(controller);


        //因为项目主页都是使用同一个项目内容 点击也会进入到同一个项目 所以就不需要了
//                //项目
//                if(project!=null)
//                {
//                    if(!baseFeed.project_id.equals(ProjectFeedFragment.this.project.id+""))
//                        holder.tv_proect.setOnClickListener(controller);
//                }




        if(feedHolder.tv_content.getVisibility()==View.VISIBLE)
        {

            feedHolder.tv_content.setMovementMethod(LinkMovementMethod.getInstance());

        }




        if (feedHolder.iv_content.getVisibility() == View.VISIBLE) {
            if("Attachment".equals(feed.feedable_type))
            {
                feedHolder.iv_content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ProjectActivity.this,R.string.cant_downlowb,Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }


        if(feedHolder.tv_response.getVisibility()==View.VISIBLE)
        {
            feedHolder.tv_response.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //这里我需要得到最后一条评论的位置  该如何是好呢 ？
                    //是否要在feed中增加一条纪录该feed所有的评论数  还是有其他更好的方法  增加评论数到不难


                    popComment(v, feed, listView,1);


                }
            });

        }

    }


    private void initCommentItemOnClick(final Comment comment,BaseFeedFragment.CommentHolder commentHolder) {

        //设置点击事件


        IndexOnClickController controller = new IndexOnClickController(this, comment);




        //点击头像和名字的响应事件是一致的 如果展示的是我的主页 再次点击不会响应

        commentHolder.iv_icon.setOnClickListener(controller);
        commentHolder.tv_name.setOnClickListener(controller);


        //因为项目主页都是使用同一个项目内容 点击也会进入到同一个项目 所以就不需要了
//                //项目
//                if(project!=null)
//                {
//                    if(!baseFeed.project_id.equals(ProjectFeedFragment.this.project.id+""))
//                        holder.tv_proect.setOnClickListener(controller);
//                }


//        if (holder.tv_content.getVisibility() == View.VISIBLE) {
//            holder.tv_content.setOnClickListener(controller);
//        }

        if(commentHolder.tv_content.getVisibility()==View.VISIBLE)
        {

            //点击跳出回复框 带@的
            commentHolder.tv_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popComment(v,comment,listView,0);
                }
            });

        }






        if(commentHolder.tv_response.getVisibility()==View.VISIBLE)
        {
            commentHolder.tv_response.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //这里我需要得到最后一条评论的位置  该如何是好呢 ？
                    //是否要在feed中增加一条纪录该feed所有的评论数  还是有其他更好的方法  增加评论数到不难
                    //但是这肯定不是优雅的做法  由于一开始没有好好的构思 现在可能考虑投机取巧的方法去解决

                    popComment(v,comment.parent,listView,1);


                }


            });

        }


    }





    /**
     * 创建menu 菜单
     * @param menu
     *
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.project_menu,menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        hasCaogao=SharedPerenceUtil.hasIdeaEdit(this.getApplicationContext(),project.id+"");
        System.out.print("asdasd");
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        hasCaogao=SharedPerenceUtil.hasIdeaEdit(this.getApplicationContext(),project.id+"");

        System.out.print("asdasd");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {


            case R.id.action_share_webo:

                if(project!=null) {
                    String text = "#" + project.title + "#      " + project.description;
                    if (text.length() > 100) {
                        text = text.substring(0, 96) + "...\n" + "http://shixian.com/projects/" + project.id;

                    }
                    WeiBoUtils.sendMessage(this, text, mWeiboShareAPI);

                }else{
                    Toast.makeText(this,"稍后再试",Toast.LENGTH_SHORT).show();
                }
                break;

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==RESULT_ADD_IDEA)
            if(resultCode== Activity.RESULT_OK)
            {

                View view=View.inflate(this,R.layout.toastmy,null);


                Toast toast = new Toast(this);
                toast.setGravity(Gravity.CENTER, 12, 40);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(view);

                toast.show();
                initFirstData();
            }else  if(resultCode==RESULT_NOSEND)
            {
                hasCaogao=true;
                if(tv_caogao!=null)
                {
                    tv_caogao.setVisibility(View.VISIBLE);
                }
            }
    }



    //执行动画
    private void execAnmi(Button clickView,View anmiview)
    {

        if(anmi_state)
        {
            ExpandAnimation animation=new ExpandAnimation(anmiview,ANMI_DRUATION);
            animation.setAnimationListener(animationListener);

            anmiview.startAnimation(animation);

            //view已经展开
            if(animation.toggle())
            {
                Drawable drawable= getResources().getDrawable(R.drawable.ic_collapse_small_holo_light);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                clickView.setText(R.string.shrinkage_stat2);
                clickView.setCompoundDrawables(null,null,drawable,null);

            }else{ //view 已经关闭
                Drawable drawable= getResources().getDrawable(R.drawable.ic_expand_small_holo_light);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                clickView.setText(R.string.shrinkage_stat1);
                clickView.setCompoundDrawables(null,null,drawable,null);

            }
        }

    }
    private void execAnmi(View anmiview)
    {
        ExpandAnimation animation=new ExpandAnimation(anmiview,ANMI_DRUATION);
        anmiview.startAnimation(animation);
    }

    private Animation.AnimationListener animationListener=new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
            anmi_state=false;

        }

        //执行结束
        @Override
        public void onAnimationEnd(Animation animation) {
            anmi_state=true;
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };


    @Override
    public void onResume() {
        super.onResume();


        ViewGroup viewGroup= (ViewGroup) ((ViewGroup)getWindow().getDecorView()).getChildAt(0);
        Log.i("AAAA","root"+viewGroup.getClass().getSimpleName());
        Log.i("AAAA","root"+viewGroup.getChildAt(0).getClass().getSimpleName());
        Log.i("AAAA","root"+viewGroup.getChildAt(1).getClass().getSimpleName());
    }
}

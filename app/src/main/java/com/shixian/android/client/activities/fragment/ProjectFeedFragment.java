package com.shixian.android.client.activities.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shixian.android.client.R;
import com.shixian.android.client.activities.BaseActivity;
import com.shixian.android.client.activities.BigImageActivity;
import com.shixian.android.client.activities.fragment.base.BaseFeedFragment;
import com.shixian.android.client.contants.AppContants;
import com.shixian.android.client.controller.OnClickController;
import com.shixian.android.client.engine.CommonEngine;
import com.shixian.android.client.model.Comment;
import com.shixian.android.client.model.Feed2;
import com.shixian.android.client.model.Project;
import com.shixian.android.client.model.feeddate.BaseFeed;
import com.shixian.android.client.utils.ApiUtils;
import com.shixian.android.client.utils.CommonUtil;
import com.shixian.android.client.utils.DisplayUtil;
import com.shixian.android.client.utils.ImageCache;
import com.shixian.android.client.utils.ImageDownload;
import com.shixian.android.client.utils.ImageUtil;
import com.shixian.android.client.utils.JsonUtils;
import com.shixian.android.client.utils.SharedPerenceUtil;
import com.shixian.android.client.utils.TimeUtil;
import com.shixian.android.client.views.PersonItemLinearLayout;

import org.apache.http.Header;

/**
 * Created by s0ng on 2015/2/12.
 */
public class ProjectFeedFragment extends BaseFeedFragment {

    private Project project=new Project();



    private String project_info;

    private String TAG="ProjectFeedFragment";






    protected void initCacheData() {

        firstPageDate= SharedPerenceUtil.getProjectIndexFeed(context, project.id);

        String projectInfo=SharedPerenceUtil.getProjectIndexInfo(context, project.id + "");

        if(!TextUtils.isEmpty(projectInfo))
            project=new Gson().fromJson(projectInfo, Project.class);
        feedList = JsonUtils.ParseFeeds(firstPageDate);

        if (adapter == null) {
            adapter = new ProjectFeedAdapter();
            pullToRefreshListView.getListView().setAdapter(adapter);

        } else {
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void initFirst() {
        project.id= Integer.parseInt((String) getArguments().get("project_id"));

        initImageCallBack();
        initFirstData();
    }

    @Override
    protected void initLable() {
        context.setLable(getString(R.string.label_project));
    }

    @Override
    protected void getNextData() {
        page += 1;
        CommonEngine.getFeedData(AppContants.PROJECT_FEED_URL.replace("{project_id}",project.id+"" ), page, new AsyncHttpResponseHandler() {
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
                            context.runOnUiThread(new Runnable() {
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


                }else{
                    pullToRefreshListView.onPullDownRefreshComplete();
                }
            }


            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

//                Log.i("AAAA", new String(bytes));

                //TODO 错误可能定义的不是太准确  最后一天调整
                Toast.makeText(context, getString(R.string.check_net), Toast.LENGTH_SHORT);
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
                adapter.notifyDataSetChanged();
            }

            if(currentFirstPos<=feedList.size())
                pullToRefreshListView.getListView().setSelection(currentFirstPos);


        }else{

            initFirst();
        }





    }

    @Override
    protected void initFirstData() {
        //开始搞
        initCacheData();


        initProjectInfo();

        initProjectFeed();



    }

    private void initProjectFeed() {

        page=1;
        context.showProgress();

        CommonEngine.getFeedData(AppContants.PROJECT_FEED_URL.replace("{project_id}",project.id+"" ), page, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {

                final String temp = new String(bytes);
                if (!AppContants.errorMsg.equals(temp)) {

                    Log.i("AAAA","xxcxcxcxcxc");
                    new Thread() {
                        public void run() {

                            firstPageDate = temp;
                            feedList = JsonUtils.ParseFeeds(firstPageDate);



                             SharedPerenceUtil.putProjectIndexFeed(context, temp, project.id);

                            context.runOnUiThread(new Runnable() {
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

                                    context.dissProgress();
                                }
                            });


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
                Toast.makeText(context, R.string.check_net, Toast.LENGTH_SHORT);
                pullToRefreshListView.onPullDownRefreshComplete();
                pullToRefreshListView.onPullUpRefreshComplete();
                context.dissProgress();
            }


        });


    }

    private void initProjectInfo() {


        ApiUtils.get(AppContants.PROJECT_INFO_URL.replace("{project_id}",project.id+""),null,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {

                //
                final String temp=new String(bytes);
                if(!AppContants.errorMsg.equals(temp))
                    new Thread() {
                        public void run() {
                            project_info = temp;
                            Gson gson = new Gson();
                            project = gson.fromJson(project_info, Project.class);

                            SharedPerenceUtil.putProjectIndexInfo(context, temp, project.id);


                            context.runOnUiThread(new Runnable() {
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

                        }
                    }.start();

            }



            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(context, R.string.check_net, Toast.LENGTH_SHORT);
            }
        });
    }



    public static final int TYPE_PROJECT=2;
    class ProjectFeedAdapter extends BaseAdapter{

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

                view=View.inflate(context,R.layout.project_index_item,null);

                if(project!=null)
                {
                    TextView tv_name= (TextView) view.findViewById(R.id.tv_name);
                    final Button bt_follow= (Button) view.findViewById(R.id.bt_follow);
                    TextView tv_content= (TextView) view.findViewById(R.id.tv_content);

                    tv_name.setText(project.title);
                    if(project.description!=null)
                        tv_content.setText(Html.fromHtml(project.description));
                    if(project.has_followed)
                    {
                        bt_follow.setBackgroundResource(R.drawable.shape_unfollow);
                        bt_follow.setText(R.string.following);
                    }else{
                        bt_follow.setBackgroundResource(R.drawable.shape_follow);
                        bt_follow.setText(R.string.follow);
                    }


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

                                Toast.makeText(context,"赞不支持取消功能，我们正在飞速开发",Toast.LENGTH_SHORT).show();
                            }else{
                                //关注api
                                ApiUtils.post(String.format(AppContants.PROJECT_FOLLOW_URL,project.id),null,new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                                        Toast.makeText(context,"关注成功",Toast.LENGTH_SHORT).show();
                                        bt_follow.setBackgroundResource(R.drawable.shape_unfollow);
                                        bt_follow.setText(R.string.following);
                                        project.has_followed=true;
                                    }

                                    @Override
                                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                                        Toast.makeText(context,"关注失败，稍后再试",Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }
                        }
                    });

                }
                    break;

                case BaseFeed.TYPE_FEED:



                    view=initFeedItemView2(convertView);
                    FeedHolder feedHolder= (FeedHolder) view.getTag();

/******************************************************************/
                    Feed2 feed = (Feed2) feedList.get(position - 1);
                    feed.position=position-1;
                    initFeedItemViewData(feed,feedHolder);
/**************************************************/
                    initFeedItemOnClick(feed,feedHolder);



                    break;

                case BaseFeed.TYPE_COMMENT:

                    view= initCommentItem(convertView);



                    CommentHolder commentHolder = (CommentHolder) view.getTag();


/**************************************************************/
                    Comment comment = (Comment) feedList.get(position - 1);
                    initCommentItemData(comment,commentHolder);


/******************************************/

                    initCommentItemOnClick(comment,commentHolder);
                    break;


            }


            return view;
        }
    }





    /***********************Adapter 点击事件 **************************/
    /***********************Adapter 点击事件 **************************/
    private void initFeedItemOnClick(final Feed2 feed,final FeedHolder feedHolder) {

        //设置点击事件


        OnClickController controller = new OnClickController(context, feed);




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


//        if (holder.tv_content.getVisibility() == View.VISIBLE) {
//            holder.tv_content.setOnClickListener(controller);
//        }

//        if(feedHolder.tv_content.getVisibility()==View.VISIBLE)
//        {
//            feedHolder.tv_content.setOnClickListener(controller);
//
//        }



        if (feedHolder.iv_content.getVisibility() == View.VISIBLE) {
            if("Attachment".equals(feed.feedable_type))
            {
                feedHolder.iv_content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context,R.string.cant_downlowb,Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                feedHolder.iv_content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(context, BigImageActivity.class);
                        intent.putExtra("key",(String)feedHolder.iv_content.getTag());
                        context.startActivity(intent);
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


                    popComment(v, feed, lv);


                }
            });

        }

    }


    private void initCommentItemOnClick(final Comment comment, CommentHolder commentHolder) {

        //设置点击事件


        OnClickController controller = new OnClickController(context, comment);




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
                        popComment(v,comment,lv);
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

                        popComment(v,comment.parent,lv);


                }


            });

        }


    }

}

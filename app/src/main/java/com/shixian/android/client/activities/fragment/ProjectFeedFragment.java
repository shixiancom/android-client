package com.shixian.android.client.activities.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
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
        context.setLable("项目主页");
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

                                    context.dissProgress();
                                }
                            });


                        }
                    }.start();

                }else{
                    pullToRefreshListView.onPullDownRefreshComplete();
                }
                //adapter
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(context, R.string.check_net, Toast.LENGTH_SHORT);
                pullToRefreshListView.onPullDownRefreshComplete();
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


    class ProjectFeedAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return (1+feedList.size());
        }

        @Override
        public Object getItem(int position) {
            if(position==1)
                return project;
            return feedList.get(position-1);
        }

        @Override
        public long getItemId(int position) {
            if(position==1)
                return  position;
            return position-1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view=null;
            FeedHolder holder=null;
            if(position==0)
            {
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
                        bt_follow.setText("已关注");
                    }else{
                        bt_follow.setBackgroundResource(R.drawable.shape_follow);
                        bt_follow.setText("关注");
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
                                        bt_follow.setText("已关注");
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

                //设置关注按钮的点击事件






            }else{

                view=initHolderAndItemView(convertView);
                holder= (FeedHolder) view.getTag();

                final BaseFeed baseFeed = feedList.get(position - 1);
                baseFeed.position=position-1;

//                String type = "";
//                String project = "";
//                String content;
//
//                holder.tv_response.setVisibility(View.VISIBLE);
//                holder.v_line.setVisibility(View.VISIBLE);
//                holder.iv_content.setVisibility(View.GONE);
//
//                if (!baseFeed.feedable_type.equals(AppContants.FEADE_TYPE_COMMON)) {
//
//
//                    Feed2 feed = (Feed2) baseFeed;
//
//                    if (feed.data.project != null && !TextUtils.isEmpty(feed.data.project.title))
//                        project = feed.data.project.title;
//
//                    holder.iv_content.setVisibility(View.GONE);
//
//
//                    switch (feed.feedable_type) {
//                        case "Idea":
//                            type = context.getResources().getString(R.string.add_idea);
//                            holder.tv_content.setText(Html.fromHtml(feed.data.content_html));
//                            break;
//                        case "Project":
//                            type = context.getResources().getString(R.string.add_project);
//                            project = feed.data.title;
//                            holder.tv_content.setText(Html.fromHtml(feed.data.description));
//                            //隐藏回复框
//                            holder.tv_response.setVisibility(View.GONE);
//                            break;
//                        case "Plan":
//                            type = context.getResources().getString(R.string.add_plan);
//
//                            holder.tv_content.setText(feed.data.content + "   截至到: " + feed.data.finish_on);
//                            break;
//                        case "Image":
//
//                            type = context.getResources().getString(R.string.add_image);
//                            holder.tv_content.setText(Html.fromHtml(feed.data.content_html));
//
//                            String keys[]=feed.data.attachment.url.split("/");
//                            String key=keys[keys.length-1];
//
//                            holder.iv_content.setTag(key);
//                            holder.iv_content.setVisibility(View.VISIBLE);
//                            ImageUtil.loadingImage(holder.iv_content, BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher),callback,key,AppContants.DOMAIN+feed.data.attachment.url);
//
//                            break;
//                        case "UserProjectRelation":
//                            type = context.getResources().getString(R.string.join);
//                            //隐藏回复框
//                            holder.tv_response.setVisibility(View.GONE);
//                            holder.tv_content.setVisibility(View.GONE);
//                            break;
//                        case "Homework":
//                            type = context.getResources().getString(R.string.finish_homework);
//                            holder.tv_content.setText(Html.fromHtml(feed.data.content_html));
//                            break;
//                        case "Task":
//                            type = context.getResources().getString(R.string.finish_task);
//                            holder.tv_content.setText(Html.fromHtml(feed.data.content_html));
//                            break;
//                        case "Vote":
//                            type = context.getResources().getString(R.string.finish_task);
//
//                            holder.tv_content.setText(Html.fromHtml(feed.data.content_html));
//                            break;
//                        case "Attachment":
//                            type = context.getResources().getString(R.string.feed_attachment);
//                            holder.tv_content.setText(Html.fromHtml(feed.data.content_html));
//                            break;
//                        case "Agreement":
//
//                            //TODO ??????????
//                            holder.tv_content.setVisibility(View.GONE);
//                            break;
//                    }
//
//                    //头像图片处理
//                    String keys[] = feed.data.user.avatar.small.url.split("/");
//                    String key = keys[keys.length - 1];
//
////                ImageUtil.loadingImage(holder.iv_icon, BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher),callback,key,AppContants.DOMAIN+feed.data.user.avatar.small.url);
//
//                    Bitmap bm = ImageCache.getInstance().get(key);
//
//                    if (bm != null) {
//                        holder.iv_icon.setImageBitmap(bm);
//                    } else {
//                        holder.iv_icon.setImageResource(R.drawable.ic_launcher);
//                        holder.iv_icon.setTag(key);
//                        if (callback != null) {
//                            new ImageDownload(callback).execute(AppContants.DOMAIN + feed.data.user.avatar.small.url, key, ImageDownload.CACHE_TYPE_LRU);
//                        }
//                    }
//
//
//                    holder.tv_type.setText(type);
//                    holder.tv_proect.setText(project);
//                    holder.tv_name.setText(feed.data.user.username);
//
//
//                    //设置样式
////                int textSize=DisplayUtil.sp2px(context,13);
//                    holder.tv_name.setTextSize(13);
//                    holder.tv_time.setTextSize(13);
//                    holder.tv_content.setTextSize(13);
//
//                    ViewGroup.LayoutParams params = holder.iv_icon.getLayoutParams();
//                    int imageSize = DisplayUtil.dip2px(context, 40);
//                    params.height = imageSize;
//                    params.width = imageSize;
//                    holder.iv_icon.setLayoutParams(params);
//
//                    holder.tv_type.setVisibility(View.VISIBLE);
//                    holder.tv_proect.setVisibility(View.VISIBLE);
//
//                } else {
//                    Comment comment = (Comment) baseFeed;
//                    //初始化一些common信息
//                    holder.tv_name.setText(comment.user.username);
//                    holder.tv_time.setText(TimeUtil.getDistanceTime(comment.created_at));
//                    holder.tv_proect.setVisibility(View.GONE);
//                    holder.tv_type.setVisibility(View.GONE);
//                    holder.iv_content.setVisibility(View.GONE);
//                    holder.tv_content.setVisibility(View.VISIBLE);
//                    holder.tv_content.setText(Html.fromHtml(comment.content_html));
//
//
//                    holder.tv_name.setTextSize(13);
//                    holder.tv_time.setTextSize(13);
//                    holder.tv_content.setTextSize(13);
//
//                    ViewGroup.LayoutParams params = holder.iv_icon.getLayoutParams();
//                    int imageSize = DisplayUtil.dip2px(context, 20);
//                    params.height = imageSize;
//                    params.width = imageSize;
//                    holder.iv_icon.setLayoutParams(params);
//
//
//                    //头像图片处理
//                    String keys[] = comment.user.avatar.small.url.split("/");
//                    String key = keys[keys.length - 1];
//
//                    Bitmap bm = ImageCache.getInstance().get(key);
//
//                    if (bm != null) {
//                        holder.iv_icon.setImageBitmap(bm);
//                    } else {
//                        holder.iv_icon.setImageResource(R.drawable.ic_launcher);
//                        holder.iv_icon.setTag(position + key);
//                        if (callback != null) {
//                            new ImageDownload(callback).execute(AppContants.DOMAIN + comment.user.avatar.small.url, key, ImageDownload.CACHE_TYPE_LRU);
//                        }
//                    }
//
//
//                    //隐藏回复框
//                    if (!comment.isLast) {
//                        holder.tv_response.setVisibility(View.GONE);
//                        holder.v_line.setVisibility(View.GONE);
//                    }
//
//                }

                initFeedItemView(holder,baseFeed,position);
                setFeedOnClickListener(context,holder,baseFeed);


            }

            return view;
        }
    }




    protected void setFeedOnClickListener(final BaseActivity context, final FeedHolder holder,final BaseFeed baseFeed)
    {

        //设置点击事件


        OnClickController controller = new OnClickController(context, baseFeed);




        //点击头像和名字的响应事件是一致的 如果展示的是我的主页 再次点击不会响应

        holder.iv_icon.setOnClickListener(controller);
        holder.tv_name.setOnClickListener(controller);


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

        if(holder.tv_content.getVisibility()==View.VISIBLE)
        {
            if(baseFeed instanceof Comment)
            {
                //点击跳出回复框 带@的
                holder.tv_content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popComment(v,baseFeed,lv);
                    }
                });
            }
        }



        if (holder.iv_content.getVisibility() == View.VISIBLE) {
            if(baseFeed instanceof Feed2 && "Attachment".equals(((Feed2)baseFeed).feedable_type))
            {
                holder.iv_content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context,"暂且不支持下载文件",Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                holder.iv_content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(context, BigImageActivity.class);
                        intent.putExtra("key",(String)holder.iv_content.getTag());
                        context.startActivity(intent);
                    }
                });


            }
        }


        if(holder.tv_response.getVisibility()==View.VISIBLE)
        {
            holder.tv_response.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //这里我需要得到最后一条评论的位置  该如何是好呢 ？
                    //是否要在feed中增加一条纪录该feed所有的评论数  还是有其他更好的方法  增加评论数到不难
                    //但是这肯定不是优雅的做法  由于一开始没有好好的构思 现在可能考虑投机取巧的方法去解决
                    if(baseFeed instanceof  Comment)
                    {
                        popComment(v,((Comment)baseFeed).parent,lv);
                    }else{

                        popComment(v,baseFeed,lv);
                    }
                    //也只能这么做了

                }


            });

        }



    }

}

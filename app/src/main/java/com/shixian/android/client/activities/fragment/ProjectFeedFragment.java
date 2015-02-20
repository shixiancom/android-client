package com.shixian.android.client.activities.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shixian.android.client.R;
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

    private ProjectFeedAdapter adapter;

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
                adapter.notifyDataSetChanged();
            }


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
                            pullToRefreshListView.onPullDownRefreshComplete();


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
            FeedHolder holder;
            if(position==0)
            {
                view=View.inflate(context,R.layout.project_index_item,null);

                if(project!=null)
                {
                    TextView tv_name= (TextView) view.findViewById(R.id.tv_name);
                    TextView tv_follow= (TextView) view.findViewById(R.id.tv_follow);
                    TextView tv_content= (TextView) view.findViewById(R.id.tv_content);

                    tv_name.setText(project.title);
                    if(project.description!=null)
                        tv_content.setText(Html.fromHtml(project.description));
                    if(project.has_followed)
                    {
                        tv_follow.setBackgroundColor(Color.BLACK);
                    }

                }





            }else{
                if (convertView == null || (convertView instanceof PersonItemLinearLayout)) {
                    view = View.inflate(context, R.layout.feed_common_item, null);
                    holder = new FeedHolder();
                    holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                    holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
                    holder.tv_proect = (TextView) view.findViewById(R.id.tv_project);
                    holder.tv_time = (TextView) view.findViewById(R.id.tv_time);
                    ;
                    holder.tv_content = (TextView) view.findViewById(R.id.tv_content);
                    ;
                    holder.iv_content = (ImageView) view.findViewById(R.id.iv_content);
                    ;
                    holder.tv_response = (TextView) view.findViewById(R.id.tv_response);
                    holder.tv_type = (TextView) view.findViewById(R.id.tv_type);
                    holder.v_line = view.findViewById(R.id.v_line);
                    view.setTag(holder);


                } else {

                    view = convertView;
                    holder = (FeedHolder) view.getTag();


                }

                final BaseFeed baseFeed = feedList.get(position - 1);
                baseFeed.position=position-1;

                String type = "";
                String project = "";
                String content;

                holder.tv_response.setVisibility(View.VISIBLE);
                holder.v_line.setVisibility(View.VISIBLE);
                holder.iv_content.setVisibility(View.GONE);

                if (!baseFeed.feedable_type.equals(AppContants.FEADE_TYPE_COMMON)) {


                    Feed2 feed = (Feed2) baseFeed;

                    if (feed.data.project != null && !TextUtils.isEmpty(feed.data.project.title))
                        project = feed.data.project.title;

                    holder.iv_content.setVisibility(View.GONE);


                    switch (feed.feedable_type) {
                        case "Idea":
                            type = context.getResources().getString(R.string.add_idea);
                            holder.tv_content.setText(Html.fromHtml(feed.data.content_html));
                            break;
                        case "Project":
                            type = context.getResources().getString(R.string.add_project);
                            project = feed.data.title;
                            holder.tv_content.setText(Html.fromHtml(feed.data.description));
                            //隐藏回复框
                            holder.tv_response.setVisibility(View.GONE);
                            break;
                        case "Plan":
                            type = context.getResources().getString(R.string.add_plan);

                            holder.tv_content.setText(feed.data.content + "   截至到: " + feed.data.finish_on);
                            break;
                        case "Image":

                            type = context.getResources().getString(R.string.add_image);
                            holder.tv_content.setText(Html.fromHtml(feed.data.content_html));

                            String keys[]=feed.data.attachment.url.split("/");
                            String key=keys[keys.length-1];

                            holder.iv_content.setTag(key);
                            ImageUtil.loadingImage(holder.iv_content, BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher),callback,key,AppContants.DOMAIN+feed.data.attachment.url);

                            break;
                        case "UserProjectRelation":
                            type = context.getResources().getString(R.string.join);
                            //隐藏回复框
                            holder.tv_response.setVisibility(View.GONE);
                            holder.tv_content.setVisibility(View.GONE);
                            break;
                        case "Homework":
                            type = context.getResources().getString(R.string.finish_homework);
                            holder.tv_content.setText(Html.fromHtml(feed.data.content_html));
                            break;
                        case "Task":
                            type = context.getResources().getString(R.string.finish_task);
                            holder.tv_content.setText(Html.fromHtml(feed.data.content_html));
                            break;
                        case "Vote":
                            type = context.getResources().getString(R.string.finish_task);

                            holder.tv_content.setText(Html.fromHtml(feed.data.content_html));
                            break;
                        case "Attachment":
                            type = context.getResources().getString(R.string.feed_attachment);
                            holder.tv_content.setText(Html.fromHtml(feed.data.content_html));
                            break;
                    }

                    //头像图片处理
                    String keys[] = feed.data.user.avatar.small.url.split("/");
                    String key = keys[keys.length - 1];

//                ImageUtil.loadingImage(holder.iv_icon, BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher),callback,key,AppContants.DOMAIN+feed.data.user.avatar.small.url);

                    Bitmap bm = ImageCache.getInstance().get(key);

                    if (bm != null) {
                        holder.iv_icon.setImageBitmap(bm);
                    } else {
                        holder.iv_icon.setImageResource(R.drawable.ic_launcher);
                        holder.iv_icon.setTag(key);
                        if (callback != null) {
                            new ImageDownload(callback).execute(AppContants.DOMAIN + feed.data.user.avatar.small.url, key, ImageDownload.CACHE_TYPE_LRU);
                        }
                    }


                    holder.tv_type.setText(type);
                    holder.tv_proect.setText(project);
                    holder.tv_name.setText(feed.data.user.username);


                    //设置样式
//                int textSize=DisplayUtil.sp2px(context,13);
                    holder.tv_name.setTextSize(13);
                    holder.tv_time.setTextSize(13);
                    holder.tv_content.setTextSize(13);

                    ViewGroup.LayoutParams params = holder.iv_icon.getLayoutParams();
                    int imageSize = DisplayUtil.dip2px(context, 40);
                    params.height = imageSize;
                    params.width = imageSize;
                    holder.iv_icon.setLayoutParams(params);

                    holder.tv_type.setVisibility(View.VISIBLE);
                    holder.tv_proect.setVisibility(View.VISIBLE);

                } else {
                    Comment comment = (Comment) baseFeed;
                    //初始化一些common信息
                    holder.tv_name.setText(comment.user.username);
                    holder.tv_time.setText(TimeUtil.getDistanceTime(comment.created_at));
                    holder.tv_proect.setVisibility(View.GONE);
                    holder.tv_type.setVisibility(View.GONE);
                    holder.tv_content.setVisibility(View.VISIBLE);
                    holder.tv_content.setText(Html.fromHtml(comment.content_html));


                    holder.tv_name.setTextSize(13);
                    holder.tv_time.setTextSize(13);
                    holder.tv_content.setTextSize(13);

                    ViewGroup.LayoutParams params = holder.iv_icon.getLayoutParams();
                    int imageSize = DisplayUtil.dip2px(context, 20);
                    params.height = imageSize;
                    params.width = imageSize;
                    holder.iv_icon.setLayoutParams(params);


                    //头像图片处理
                    String keys[] = comment.user.avatar.small.url.split("/");
                    String key = keys[keys.length - 1];

                    Bitmap bm = ImageCache.getInstance().get(key);

                    if (bm != null) {
                        holder.iv_icon.setImageBitmap(bm);
                    } else {
                        holder.iv_icon.setImageResource(R.drawable.ic_launcher);
                        holder.iv_icon.setTag(position + key);
                        if (callback != null) {
                            new ImageDownload(callback).execute(AppContants.DOMAIN + comment.user.avatar.small.url, key, ImageDownload.CACHE_TYPE_LRU);
                        }
                    }


                    //隐藏回复框
                    if (!comment.isLast) {
                        holder.tv_response.setVisibility(View.GONE);
                        holder.v_line.setVisibility(View.GONE);
                    }

                }


                //设置点击事件


                OnClickController controller = new OnClickController(context, baseFeed);




                //点击头像和名字的响应事件是一致的 如果展示的是我的主页 再次点击不会响应

                    holder.iv_icon.setOnClickListener(controller);
                    holder.tv_name.setOnClickListener(controller);


                //项目
                if(project!=null)
                {
                    if(!baseFeed.project_id.equals(ProjectFeedFragment.this.project))
                        holder.tv_proect.setOnClickListener(controller);
                }


                if (holder.tv_content.getVisibility() == View.VISIBLE) {
                    holder.tv_content.setOnClickListener(controller);
                }


                if (holder.iv_content.getVisibility() == View.VISIBLE) {
                    holder.iv_content.setOnClickListener(controller);
                }


                if (holder.tv_response.getVisibility() == View.VISIBLE) {
                    holder.tv_response.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popComment(v,baseFeed,lv);
                        }
                    });
                }





            }

            return view;
        }
    }


}

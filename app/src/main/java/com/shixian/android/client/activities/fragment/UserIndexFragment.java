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
import android.widget.Button;
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
import com.shixian.android.client.model.User;
import com.shixian.android.client.model.feeddate.BaseFeed;
import com.shixian.android.client.utils.ApiUtils;
import com.shixian.android.client.utils.CommonUtil;
import com.shixian.android.client.utils.DisplayUtil;
import com.shixian.android.client.utils.ImageCache;
import com.shixian.android.client.utils.ImageCallback;
import com.shixian.android.client.utils.ImageDownload;
import com.shixian.android.client.utils.ImageUtil;
import com.shixian.android.client.utils.JsonUtils;
import com.shixian.android.client.utils.SharedPerenceUtil;
import com.shixian.android.client.utils.TimeUtil;
import com.shixian.android.client.views.PersonItemLinearLayout;

import org.apache.http.Header;

/**
 * Created by s0ng on 2015/2/12.
 * 个人主页
 */

public class UserIndexFragment extends BaseFeedFragment {


    private String TAG = "UserIndexFragment";
    private User user;
    private UserIndexFeedAdapte adapter;



    protected void initCacheData() {
        firstPageDate= SharedPerenceUtil.getUserIndexFeed(context,user.id);

        String userInfo=SharedPerenceUtil.getUserIndexInfo(context,user.id);
        if(!TextUtils.isEmpty(userInfo))
            user=new Gson().fromJson(userInfo,User.class);
        feedList = JsonUtils.ParseFeeds(firstPageDate);

        if(user.status!=null)
        if (adapter == null) {
            adapter = new UserIndexFeedAdapte();
            pullToRefreshListView.getRefreshableView().setAdapter(adapter);
        } else {
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void initFirst() {
        initImageCallBack();
        initFirstData();
    }

    @Override
    protected void initLable() {
        context.setLable("个人主页");
    }

    @Override
    protected void initImageCallBack() {
        this.callback = new ImageCallback() {

            @Override
            public void imageLoaded(Bitmap bitmap, Object tag) {
                ImageView imageView = (ImageView) pullToRefreshListView.getListView()
                        .findViewWithTag(tag);

                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);

                }
            }
        };
    }

    @Override
    protected void getNextData() {
        page += 1;
        CommonEngine.getFeedData(AppContants.USER_FEED_INDEX_URL.replace("{user_name}",user.username),page, new AsyncHttpResponseHandler() {
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



                            //保存数据到本地
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (adapter == null) {
                                        adapter = new UserIndexFeedAdapte();

                                        pullToRefreshListView.getRefreshableView().setAdapter(adapter);
                                    } else {
                                        adapter.notifyDataSetChanged();
                                    }

                                }
                            });

                        }
                    }.start();
                    pullToRefreshListView.onPullUpRefreshComplete();
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
        initImageCallBack();
        if(feedList!=null&&feedList.size()>0)
        {
            if (adapter == null) {
                adapter = new UserIndexFeedAdapte();


                pullToRefreshListView.getRefreshableView().setAdapter(adapter);
            } else {
                pullToRefreshListView.getRefreshableView().setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            if(currentFirstPos<=feedList.size())
                pullToRefreshListView.getListView().setSelection(currentFirstPos);

        }else{

            initFirstData();
        }

    }

    @Override
    protected void initFirstData() {

        user = (User) getArguments().get("user");
        initCacheData();

        initUserInfo();

        initUserFeed();


    }

    /**
     * 初始化用户信息
     */
    private void initUserFeed() {
        //如果是自身 说明是我的主页 我的信息都已经在登陆的时候拿到了 所以就不需要获取了 否则获取用户信息
        page=1;

        context.showProgress();
        CommonEngine.getFeedData(AppContants.USER_FEED_INDEX_URL.replace("{user_name}",user.username),page, new AsyncHttpResponseHandler(){
        @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {

                final String temp = new String(bytes);
                if (!AppContants.errorMsg.equals(temp)) {
                    new Thread() {
                        public void run() {

                            firstPageDate = temp;
                            feedList = JsonUtils.ParseFeeds(firstPageDate);
                            pullToRefreshListView.onPullDownRefreshComplete();

                            SharedPerenceUtil.putUserIndexFeed(context,firstPageDate,user.id);

                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (adapter == null) {
                                        adapter = new UserIndexFeedAdapte();
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


    private void initUserInfo() {


        String url = AppContants.USER_INFO_INDEX_URL.replace("{user_id}", user.id);
        ApiUtils.get(url, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String userinfo = new String(bytes);

                CommonUtil.logDebug("AAAA", userinfo);



                if (!AppContants.errorMsg.equals(userinfo)) {
                    Gson gson = new Gson();
                    User user = gson.fromJson(userinfo, User.class);
                    UserIndexFragment.this.user = user;

                    //保存userinfo
                    SharedPerenceUtil.putUserIndexInfo(context, userinfo, user.id + "");

                    if (adapter == null) {
                        adapter = new UserIndexFeedAdapte();
                        pullToRefreshListView.getRefreshableView().setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(context, R.string.check_net, Toast.LENGTH_SHORT);
            }


        });


    }


    class UserIndexFeedAdapte extends BaseAdapter {

        @Override
        public int getCount() {
            return 1 + feedList.size();
        }

        @Override
        public Object getItem(int position) {
            if (position == 1)
                return user;
            return feedList.get(position - 1) ;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = null;
            FeedHolder holder=null;


            if (position == 0) {


                //return 第一项 就是那个啥
                view = View.inflate(context, R.layout.person_index_item, null);


                TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
                //头像
                ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
                //关注按钮
                final Button bt_follow = (Button) view.findViewById(R.id.bt_follow);
                //签名
                TextView tv_winess = (TextView) view.findViewById(R.id.tv_witness);

                TextView tv_activitys = (TextView) view.findViewById(R.id.tv_activitys);
                TextView tv_project = (TextView) view.findViewById(R.id.tv_project);
                TextView tv_fllowen = (TextView) view.findViewById(R.id.tv_fllowen);
                TextView tv_fllowing = (TextView) view.findViewById(R.id.tv_fllowing);


                //下载头像
                String keys[] = user.avatar.small.url.split("/");
                String key = keys[keys.length - 1];

                Bitmap bm = ImageCache.getInstance().get(key);

                if (bm != null) {
                    iv_icon.setImageBitmap(bm);
                } else {
                    iv_icon.setImageResource(R.drawable.ic_launcher);
                    iv_icon.setTag(key);
                    if (callback != null) {
                        new ImageDownload(callback).execute(AppContants.DOMAIN + user.avatar.small.url, key, ImageDownload.CACHE_TYPE_LRU);
                    }
                }

                if(user.has_followed)
                {
                    bt_follow.setBackgroundColor(Color.GRAY);

                }else{
                    bt_follow.setBackgroundColor(Color.argb(1,32,168,192+15));
                }


                tv_activitys.setText(user.status.feeds_count + "");
                tv_project.setText(user.status.followed_projects_count + "");
                tv_fllowen.setText(user.status.followers_count + "");
                tv_fllowing.setText(user.status.followings_count + "");

                tv_winess.setText(user.description);
                tv_name.setText(user.username);

                //监听事件
                bt_follow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       if(user.has_followed)
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
                           ApiUtils.post(String.format(AppContants.USER_FOLLOW_URL,user.id),null,new AsyncHttpResponseHandler() {
                               @Override
                               public void onSuccess(int i, Header[] headers, byte[] bytes) {
                                   Toast.makeText(context,"关注成功",Toast.LENGTH_SHORT).show();
                                   bt_follow.setBackgroundColor(Color.GRAY);
                                   user.has_followed=true;
                               }

                               @Override
                               public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                                   Toast.makeText(context,"关注失败，稍后再试",Toast.LENGTH_SHORT).show();

                               }
                           });
                       }

                    }
                });



            } else {
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
                            holder.iv_content.setVisibility(View.VISIBLE);
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

                String userid = "";
                if (AppContants.FEADE_TYPE_COMMON.equals(baseFeed.feedable_type)) {
                    userid = ((Comment) baseFeed).user.id;
                } else {
                    userid = ((Feed2) baseFeed).data.user.id;

                    ////////////////////
                }

                //点击头像和名字的响应事件是一致的 如果展示的是我的主页 再次点击不会响应
                if (!userid.equals(user.id)) {
                    holder.iv_icon.setOnClickListener(controller);
                    holder.tv_name.setOnClickListener(controller);
                }

                //项目
                holder.tv_proect.setOnClickListener(controller);

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

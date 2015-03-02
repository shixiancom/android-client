package com.shixian.android.client.activities.fragment;

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
                }else {
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
                final TextView bt_follow = (TextView) view.findViewById(R.id.bt_follow);
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

                //TODO
                if(user.has_followed)
                {
                    bt_follow.setBackgroundResource(R.drawable.unfollow);

                }else{
//                    bt_follow.setBackgroundResource(R.drawable.bt_follow_selector);
//                    bt_follow.setText("关注");
                    bt_follow.setBackgroundResource(R.drawable.follow);
                }


                if(user.status!=null) {
                    tv_activitys.setText(user.status.feeds_count + "");
                    tv_project.setText(user.status.followed_projects_count + "");
                    tv_fllowen.setText(user.status.followers_count + "");
                    tv_fllowing.setText(user.status.followings_count + "");
                }
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
                                   bt_follow.setBackgroundResource(R.drawable.unfollow);
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
               view=initHolderAndItemView(convertView);
                holder= (FeedHolder) view.getTag();

                final BaseFeed baseFeed = feedList.get(position - 1);
                baseFeed.position=position-1;
//
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
//                    Feed2 feed = (Feed2) baseFeed;
//
//                    if (feed.data.project != null && !TextUtils.isEmpty(feed.data.project.title))
//                        project = feed.data.project.title;
//
//                    holder.iv_content.setVisibility(View.GONE);
//
//
//
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
//                    holder.tv_time.setTextSize(11);
//                    holder.tv_content.setTextSize(15);
//
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
                //设置点击事件
                setFeedOnClickListener(context,holder,baseFeed);


            }

            return view;
        }
    }



    @Override
    protected void setFeedOnClickListener(final BaseActivity context, final FeedHolder holder, final BaseFeed baseFeed) {


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
            }else{
                holder.tv_content.setOnClickListener(controller);
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

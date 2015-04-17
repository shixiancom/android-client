package com.shixian.android.client.activities.fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.shixian.android.client.Global;
import com.shixian.android.client.R;
import com.shixian.android.client.activities.LoginActivity;
import com.shixian.android.client.activities.MainActivity;
import com.shixian.android.client.activities.fragment.base.BaseFeedFragment;
import com.shixian.android.client.contants.AppContants;
import com.shixian.android.client.controller.IndexOnClickController;
import com.shixian.android.client.engine.CommonEngine;
import com.shixian.android.client.engine.JPushEngine;
import com.shixian.android.client.engine.UserEngine;
import com.shixian.android.client.handler.feed.BaseFeedHandler;
import com.shixian.android.client.model.Comment;
import com.shixian.android.client.model.Feed2;
import com.shixian.android.client.model.User;
import com.shixian.android.client.model.feeddate.BaseFeed;
import com.shixian.android.client.sina.AccessTokenKeeper;
import com.shixian.android.client.utils.ApiUtils;
import com.shixian.android.client.utils.CameraPhotoUtil;
import com.shixian.android.client.utils.CommonUtil;
import com.shixian.android.client.utils.JsonUtils;
import com.shixian.android.client.utils.SharedPerenceUtil;

import org.apache.http.Header;

import java.io.File;
import java.util.List;


/**
 * Created by s0ng on 2015/2/12.
 * 个人主页
 */

public class MyUserIndexFragment extends BaseFeedFragment {


    private String TAG = "UserIndexFragment";
    private User user;

    private final int RESULT_REQUEST_PHOTO = 1005;
    private final int RESULT_REQUEST_PHOTO_CROP = 1006;
    private static final int RESULT_EDIT_LIST = 11;

    private Uri fileUri;
    private Uri fileCropUri;

    //上传头像过程中的progressbar
    private ProgressBar progressBar;


    protected void initCacheData() {
        firstPageDate = SharedPerenceUtil.getUserIndexFeed(context.getApplicationContext(), user.id);

        String userInfo = SharedPerenceUtil.getUserIndexInfo(context.getApplicationContext(), user.id);
        if (!TextUtils.isEmpty(userInfo))
            user = new Gson().fromJson(userInfo, User.class);
        feedList = JsonUtils.ParseFeeds(firstPageDate);

        if (user.status != null)
            if (adapter == null) {
                adapter = new UserIndexFeedAdapte();
                pullToRefreshListView.getRefreshableView().setAdapter(adapter);
            } else {
                pullToRefreshListView.getListView().setAdapter(adapter);

                adapter.notifyDataSetChanged();
            }



    }

    @Override
    protected void initFirst() {
        initFirstData();
    }

    @Override
    protected void initLable() {
        context.setLable(getString(R.string.label_profile));
    }


    @Override
    protected void getNextData() {
        page += 1;
        CommonEngine.getFeedData(context,AppContants.USER_FEED_INDEX_URL.replace("{user_name}", user.username), page, new AsyncHttpResponseHandler() {
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
                } else {
                    pullToRefreshListView.onPullDownRefreshComplete();
                }
            }


            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {


                Toast.makeText(context, getString(R.string.check_net), Toast.LENGTH_SHORT);
                pullToRefreshListView.onPullUpRefreshComplete();
                page -= 1;
            }
        });
    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        if (feedList != null && feedList.size() > 0) {
            if (adapter == null) {
                adapter = new UserIndexFeedAdapte();


                pullToRefreshListView.getRefreshableView().setAdapter(adapter);
            } else {

            }

        } else {

            initFirstData();
        }

    }

    @Override
    public void initFirstData() {

        user = (User) getArguments().get("user");
        initCacheData();

        initUserInfo();

        initUserFeed();


    }

    @Override
    public boolean needRefresh() {
        return true;
    }


    /**
     * 初始化用户信息
     */
    private void initUserFeed() {
        //如果是自身 说明是我的主页 我的信息都已经在登陆的时候拿到了 所以就不需要获取了 否则获取用户信息
        page = 1;

        context.showProgress();
        CommonEngine.getFeedData(context,AppContants.USER_FEED_INDEX_URL.replace("{user_name}", user.username), page, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {

                final String temp = new String(bytes);
                if (!AppContants.errorMsg.equals(temp)) {
                    new Thread() {
                        public void run() {

                            firstPageDate = temp;
                            final List<BaseFeed> tempList = JsonUtils.ParseFeeds(firstPageDate);
                            pullToRefreshListView.onPullDownRefreshComplete();


                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    feedList=tempList;
                                    if (adapter == null) {
                                        adapter = new UserIndexFeedAdapte();
                                        pullToRefreshListView.getRefreshableView().setAdapter(adapter);

                                    } else {
                                        adapter.notifyDataSetChanged();
                                    }

                                    pullToRefreshListView.onPullDownRefreshComplete();
                                    pullToRefreshListView.getFooterLoadingLayout().show(false);
                                    context.dissProgress();
                                }
                            });
                            SharedPerenceUtil.putUserIndexFeed(context.getApplicationContext(), firstPageDate, user.id);

                        }
                    }.start();

                } else {
                    pullToRefreshListView.onPullDownRefreshComplete();
                    pullToRefreshListView.getFooterLoadingLayout().show(false);
                }
                //adapter
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(context, R.string.check_net, Toast.LENGTH_SHORT).show();
                pullToRefreshListView.onPullDownRefreshComplete();
                pullToRefreshListView.getFooterLoadingLayout().show(false);
                context.dissProgress();
            }


        });


    }


    private void initUserInfo() {


        String url = AppContants.USER_INFO_INDEX_URL.replace("{username}", user.username);
        ApiUtils.get(context,url, null, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                String userinfo = new String(bytes);

                CommonUtil.logDebug("AAAA", userinfo);


                if (!AppContants.errorMsg.equals(userinfo)) {
                    Gson gson = new Gson();
                    User user = gson.fromJson(userinfo, User.class);
                    MyUserIndexFragment.this.user = user;


                    if (adapter == null) {
                        adapter = new UserIndexFeedAdapte();
                        pullToRefreshListView.getRefreshableView().setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                    //保存userinfo
                    SharedPerenceUtil.putUserIndexInfo(context.getApplicationContext(), userinfo, user.id + "");

                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(context, R.string.check_net, Toast.LENGTH_SHORT).show();
            }


        });


    }


    public static final int TYPE_USERITEM = 2;

    class UserIndexFeedAdapte extends BaseFeedAdapter {

        @Override
        public int getCount() {
            return 1 + feedList.size();
        }

        @Override
        public Object getItem(int position) {
            if (position == 0)
                return user;
            return feedList.get(position - 1);
        }


        @Override
        public long getItemId(int position) {
            return position;
        }


        @Override
        public int getViewTypeCount() {
            return 3;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0)
                return TYPE_USERITEM;
            return feedList.get(position - 1).type;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view = null;


            int itemType = getItemViewType(position);
            switch (itemType) {
                case TYPE_USERITEM:
                    //return 第一项 就是那个啥
                    view = View.inflate(context, R.layout.myperson_index_item, null);


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

                    progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
                    progressBar.setVisibility(View.GONE);


                    //下载头像
                    String keys[] = user.avatar.small.url.split("/");
                    String key = keys[keys.length - 1];


                    ImageLoader.getInstance().displayImage(AppContants.ASSET_DOMAIN + user.avatar.small.url, iv_icon, BaseFeedHandler.feedOptions, animateFirstListener);


                    if (user.has_followed) {
                        bt_follow.setBackgroundResource(R.drawable.shape_unfollow);
                        bt_follow.setText(R.string.following);

                    } else {
//                    bt_follow.setBackgroundResource(R.drawable.bt_follow_selector);
//                    bt_follow.setText("关注");
                        bt_follow.setBackgroundResource(R.drawable.shape_follow);
                        bt_follow.setText(R.string.follow);

                    }


                    if (user.status != null) {
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
                            if (user.has_followed) {
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

                                Toast.makeText(context, "暂不支持取消功能，我们正在飞速开发", Toast.LENGTH_SHORT).show();
                            } else {
                                //关注api
                                ApiUtils.post(context,String.format(AppContants.USER_FOLLOW_URL, user.id), null, new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int i, Header[] headers, byte[] bytes) {
                                        Toast.makeText(context, "关注成功", Toast.LENGTH_SHORT).show();
                                        bt_follow.setBackgroundResource(R.drawable.shape_unfollow);
                                        bt_follow.setText(R.string.following);
                                        user.has_followed = true;
                                    }

                                    @Override
                                    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                                        Toast.makeText(context, "关注失败，稍后再试", Toast.LENGTH_SHORT).show();

                                    }
                                });
                            }

                        }
                    });

                    if (user.id.equals(Global.USER_ID))
                    {
                        bt_follow.setVisibility(View.GONE);
                    }

                    iv_icon.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setIcon();
                        }
                    });


                    break;

                case BaseFeed.TYPE_FEED:


                    view = BaseFeedHandler.initFeedItemView2(context, convertView);
                    FeedHolder feedHolder = (FeedHolder) view.getTag();

/******************************************************************/
                    Feed2 feed = (Feed2) feedList.get(position - 1);
                    feed.position = position - 1;
                    BaseFeedHandler.initFeedItemViewData(context, feed, feedHolder, animateFirstListener);
/**************************************************/
                    initFeedItemOnClick(feed, feedHolder);

                    BaseFeedHandler.setFeedCommonClick(context,feed,feedHolder);

                    break;

                case BaseFeed.TYPE_COMMENT:

                    view = BaseFeedHandler.initCommentItem(context, convertView);


                    CommentHolder commentHolder = (CommentHolder) view.getTag();


/**************************************************************/
                    Comment comment = (Comment) feedList.get(position - 1);
                    BaseFeedHandler.initCommentItemData(comment, commentHolder, animateFirstListener);


/******************************************/

                    initCommentItemOnClick(comment, commentHolder);
                    break;
            }


            return view;
        }
    }

    protected void initCommentItemOnClick(final Comment comment, CommentHolder commentHolder) {

        IndexOnClickController controller = new IndexOnClickController(context, comment);

        String couserid = comment.user.id;


        //点击头像和名字的响应事件是一致的 如果展示的是我的主页 再次点击不会响应
        if (!couserid.equals(user.id)) {
            commentHolder.iv_icon.setOnClickListener(controller);
            commentHolder.tv_name.setOnClickListener(controller);
        }


        if (commentHolder.tv_content.getVisibility() == View.VISIBLE) {

            //点击跳出回复框 带@的
            commentHolder.tv_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popComment(v, comment, listView, 0);
                }
            });


        }


        if (commentHolder.tv_response.getVisibility() == View.VISIBLE) {
            commentHolder.tv_response.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //这里我需要得到最后一条评论的位置  该如何是好呢 ？
                    //是否要在feed中增加一条纪录该feed所有的评论数  还是有其他更好的方法  增加评论数到不难
                    //但是这肯定不是优雅的做法  由于一开始没有好好的构思 现在可能考虑投机取巧的方法去解决

                    popComment(v, comment.parent, listView, 1);

                    //也只能这么做了

                }


            });

        }


    }


    protected void initFeedItemOnClick(final Feed2 feed, final FeedHolder feedHolder) {

        IndexOnClickController feedcontroller = new IndexOnClickController(context, feed);


        String userid = feed.data.user.id;


        //点击头像和名字的响应事件是一致的 如果展示的是我的主页 再次点击不会响应
        if (!userid.equals(user.id)) {
            feedHolder.iv_icon.setOnClickListener(feedcontroller);
            feedHolder.tv_name.setOnClickListener(feedcontroller);
        }

        //项目
        feedHolder.tv_proect.setOnClickListener(feedcontroller);


//        if (feedHolder.tv_content.getVisibility() == View.VISIBLE) {
//
//            feedHolder.tv_content.setOnClickListener(feedcontroller);
//
//        }


        if (feedHolder.iv_content.getVisibility() == View.VISIBLE) {
            if ("Attachment".equals(feed.feedable_type)) {
                feedHolder.iv_content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, R.string.cant_downlowb, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }


        if (feedHolder.tv_response.getVisibility() == View.VISIBLE) {
            feedHolder.tv_response.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //这里我需要得到最后一条评论的位置  该如何是好呢 ？
                    //是否要在feed中增加一条纪录该feed所有的评论数  还是有其他更好的方法  增加评论数到不难
                    //但是这肯定不是优雅的做法  由于一开始没有好好的构思 现在可能考虑投机取巧的方法去解决

                    popComment(v, feed, listView, 1);

                    //也只能这么做了

                }


            });

        }
    }


    /**
     * **********重写生命周期方法********************
     */
    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)context).initMsgStatus();
        ((MainActivity)context).setCurrentFragment(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    public void logout()
    {
        SharedPerenceUtil.clearAllData(context.getApplicationContext());
        AccessTokenKeeper.clear(context);
        Intent intent=new Intent(context,LoginActivity.class);
        startActivity(intent);
        Intent broadIntent=new Intent();
        broadIntent.setAction(AppContants.ACTION_FINISHACTIVITY);
        context.sendBroadcast(broadIntent);

        JPushEngine.sendJpushData(context,false);

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.myusre_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.action_quit:
                logout();
                break;
        }

        return super.onOptionsItemSelected(item);
    }



    //头像相关的
    void setIcon() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("更换头像")
                .setItems(R.array.camera_gallery, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            camera();
                        } else {
                            photo();
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
     //   dialogTitleLineColor(dialog);
    }


    private void camera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = CameraPhotoUtil.getOutputMediaFileUri();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        startActivityForResult(intent, RESULT_REQUEST_PHOTO);
    }

    private void photo() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_REQUEST_PHOTO);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULT_REQUEST_PHOTO) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    fileUri = data.getData();
                }

                fileCropUri = CameraPhotoUtil.getOutputMediaFileUri();
                cropImageUri(fileUri, fileCropUri, 640, 640, RESULT_REQUEST_PHOTO_CROP);
            }

        } else if (requestCode == RESULT_REQUEST_PHOTO_CROP) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    String filePath = CommonUtil.getPath(getActivity(), fileCropUri);
                    RequestParams params = new RequestParams();
                    File file=new File(filePath);
                    Log.i("AAAA", filePath);
                    params.put("avatar", file);
                    //
                   // postNetwork(HOST_USER_AVATAR, params, HOST_USER_AVATAR);



                    if(progressBar!=null)
                    {
                        progressBar.setVisibility(View.VISIBLE);
                    }
                    //TODO 发送图片
                    UserEngine.editIcon(getActivity(),params,new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
                            Toast.makeText(getActivity(),"成功",Toast.LENGTH_SHORT).show();
                            if(progressBar!=null)
                            {
                                progressBar.setVisibility(View.GONE);
                            }
                            initFirstData();

                        }

                        @Override
                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
//
                            if(progressBar!=null)
                            {
                                progressBar.setVisibility(View.GONE);
                            }


                        }
                    });


                } catch (Exception e) {
                }
            }
        } else if (requestCode == RESULT_EDIT_LIST) {
            if (resultCode == Activity.RESULT_OK) {

//                user = AccountInfo.loadAccount(this);
//                getUserInfoRows();
                adapter.notifyDataSetChanged();
            } else {

            }
        }
    }



    private void cropImageUri(Uri uri, Uri outputUri, int outputX, int outputY, int requestCode) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", outputX);
        intent.putExtra("outputY", outputY);
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        intent.putExtra("return-data", false);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        intent.putExtra("scale", true);// 去黑边
        intent.putExtra("scaleUpIfNeeded", true);// 去黑边
        startActivityForResult(intent, requestCode);
    }


}

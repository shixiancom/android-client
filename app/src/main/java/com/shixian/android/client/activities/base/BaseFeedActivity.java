package com.shixian.android.client.activities.base;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.shixian.android.client.Global;
import com.shixian.android.client.R;
import com.shixian.android.client.activities.SimpleSampleActivity;
import com.shixian.android.client.activities.fragment.base.BaseFeedFragment;
import com.shixian.android.client.contants.AppContants;
import com.shixian.android.client.controller.ArgeeOnClickController;
import com.shixian.android.client.enter.EnterLayout;
import com.shixian.android.client.handler.content.ContentHandler;
import com.shixian.android.client.model.Comment;
import com.shixian.android.client.model.Feed2;
import com.shixian.android.client.model.feeddate.BaseFeed;
import com.shixian.android.client.utils.ApiUtils;
import com.shixian.android.client.utils.DisplayUtil;
import com.shixian.android.client.utils.TimeUtil;
import com.shixian.android.client.views.pulltorefreshlist.PullToRefreshBase;
import com.shixian.android.client.views.pulltorefreshlist.PullToRefreshListView;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBarUtils;
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;

/**
 * Created by tangtang on 15/4/2.
 */
public abstract class BaseFeedActivity extends SwipeActivity {

    protected SmoothProgressBar mProgressBar;

    public static final int REFRESH_PAGE=10086;

    protected Toolbar toolbar;

    protected PullToRefreshListView pullToRefreshListView;


    protected boolean pauseOnScroll = false;
    protected boolean pauseOnFling = true;



    protected int page = 1;

    protected String firstPageDate;
    protected List<BaseFeed> feedList;
    protected BaseAdapter adapter;


    protected AbsListView listView;




    //图片加载有关＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
    //大头像
    protected DisplayImageOptions feedOptions;
    //内容
    protected DisplayImageOptions contentOptions;
    //小头像
    protected DisplayImageOptions commentOptions;







    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_basefeed);

        initView();

        initDate(savedInstanceState);

    }




    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pullToRefreshListView.getListView().setSelection(0);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        applyScrollListener();
    }



    private void applyScrollListener() {
        pullToRefreshListView.setOnScrollListener(new MyPauseOnScrollListener(ImageLoader.getInstance(), pauseOnScroll, pauseOnFling));

    }

    protected class MyPauseOnScrollListener extends PauseOnScrollListener {

        public MyPauseOnScrollListener(ImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnFling) {
            super(imageLoader, pauseOnScroll, pauseOnFling);
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            super.onScrollStateChanged(view, scrollState);

        }
    }




    /*************************************用于管理回复框 一级软键盘弹出 到这listView 变小  要滚动listView 这里网上资料很少 研究了差不多一天多***
     * 并且考虑到集成回复表情，但是考虑到论坛的性质 将回复表情功能砍掉*******************************************************************/

    protected int oldListHigh = 0;
    protected int needScrollY = 0;
    protected int cal1 = 0;
    protected View commonEnterRoot;
    protected EnterLayout mEnterLayout;

    //发送回复的监听发送事件
    protected  View.OnClickListener onClickSendText = new View.OnClickListener() {

        boolean sendAble=true;

        @Override
        public void onClick(View v) {

            if(sendAble)
            {
                sendAble=false;
            }else{
                Toast.makeText(BaseFeedActivity.this, "不要重复点击发送", Toast.LENGTH_SHORT).show();
                return;
            }



            String input = mEnterLayout.getContent();
            if (input.isEmpty()) {
                sendAble=true;
                return;
            }

            //发送
            BaseFeed baseFeed= (BaseFeed) mEnterLayout.getTag();
            String type;
            String id;
            if (AppContants.FEADE_TYPE_COMMON.equals(baseFeed.feedable_type)) {
                type = ((Comment) baseFeed).commentable_type;

                id=((Comment) baseFeed).commentable_id;
            } else {


                type = baseFeed.feedable_type;
                id=baseFeed.feedable_id;
            }

            if("UserProjectRelation".equals(type))
                type = "user_project_relation";
            String url=String.format(AppContants.COMMENT_URL,(type+"s").toLowerCase(),id);

            RequestParams params=new RequestParams();
            params.put("comment[content]",input);



            ApiUtils.post(BaseFeedActivity.this,url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {


                    BaseFeed baseFeed = (BaseFeed) mEnterLayout.getTag();
                    Gson gson = new Gson();

                    //新生成的comment
                    Comment comment = gson.fromJson(new String(bytes), Comment.class);
                    comment.feedable_type = AppContants.FEADE_TYPE_COMMON;

                    //点击的
                    if (baseFeed instanceof Comment) {
                        comment.parent_id = ((Comment) baseFeed).parent_id;
                        comment.project_id = ((Comment) baseFeed).project_id;
                        ((Comment) baseFeed).isLast = false;

                        if (((Comment) baseFeed).parent.hasChildren) {
                            ((Comment) feedList.get(((Comment) baseFeed).parent.lastChildPosition + ((Comment) baseFeed).parent.position)).isLast = false;
                        }
                        ((Comment) baseFeed).parent.hasChildren = true;
                        int position = ++((Comment) baseFeed).parent.lastChildPosition;
                        feedList.add(position + ((Comment) baseFeed).parent.position, comment);
                        comment.parent = ((Comment) baseFeed).parent;
                    } else {
                        if (comment != null) {
                            comment.parent_id = baseFeed.id;
                            comment.project_id = ((Feed2) baseFeed).project_id;

                            if (((Feed2) baseFeed).hasChildren) {
                                ((Comment) feedList.get(((Feed2) baseFeed).lastChildPosition + ((Feed2) baseFeed).position)).isLast = false;
                            }
                            ((Feed2) baseFeed).hasChildren = true;
                            ((Feed2) baseFeed).lastChildPosition++;
                            feedList.add(((Feed2) baseFeed).lastChildPosition + ((Feed2) baseFeed).position, comment);
                            comment.parent = (Feed2) baseFeed;
                        }
                    }


                    comment.isLast = true;


                    adapter.notifyDataSetChanged();
                    mEnterLayout.clearContent();
                    hideSoftkeyboard();
                    sendAble = true;

                    Toast.makeText(BaseFeedActivity.this, R.string.send_ok, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                    hideSoftkeyboard();
                    Toast.makeText(BaseFeedActivity.this, R.string.send_failed, Toast.LENGTH_SHORT).show();
                    sendAble = true;
                }
            });

        }
    };



    @Override
    public void onDestroy() {
        super.onDestroy();
        AnimateFirstDisplayListener.displayedImages.clear();
    }

    public static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }



    public void initView() {




        toolbar = (Toolbar) findViewById(R.id.toolbar);

        initLable();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Handle Back Navigation :D
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseFeedActivity.this.onBackPressed();
            }
        });



        mProgressBar= (SmoothProgressBar) findViewById(R.id.pocket);
        mProgressBar.setSmoothProgressDrawableBackgroundDrawable(
                SmoothProgressBarUtils.generateDrawableWithColors(
                        getResources().getIntArray(R.array.pocket_background_colors),
                        ((SmoothProgressDrawable) mProgressBar.getIndeterminateDrawable()).getStrokeWidth()));


        pullToRefreshListView = (PullToRefreshListView) findViewById(R.id.lv_index);
        pullToRefreshListView.getListView().setDividerHeight(0);



        this.listView=pullToRefreshListView.getListView();


        commonEnterRoot=this.findViewById(R.id.commonEnterRoot);

        settingListView(listView);





        // 滚动到底自动加载可用
        pullToRefreshListView.setScrollLoadEnabled(true);



        // 设置下拉刷新的listener
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

            //下拉舒心完成
            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {

                //上啦刷新
                initFirstData();
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                //getNewsList(moreUrl, false);
                //下拉加载更多
                getNextData();



            }
        });

        if(feedList==null)
            feedList = new ArrayList<BaseFeed>();




    }

    protected abstract void initFirst();

    protected abstract void initLable();


    protected abstract  void getNextData();


    public abstract  void initDate(Bundle savedInstanceState) ;






/**********************************回复相关************************************************/

    /**
     * 隐藏软键盘和输入框
     */
    protected void hideSoftkeyboard() {
//        mEnterLayout.restoreSaveStop();
        mEnterLayout.hide();
        mEnterLayout.clearContent();
        mEnterLayout.hideKeyboard();

    }

    protected void settingListView(final AbsListView listView)
    {
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();

                if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
                    hideSoftkeyboard();
                }

                return false;
            }
        });
        mEnterLayout = new EnterLayout(this,onClickSendText);
//        mEnterLayout.content.addTextChangedListener(new TextWatcherAt(this, this, 101));
        mEnterLayout.hide();



        ViewTreeObserver vto = listView.getViewTreeObserver();

        /*
        由于 中文数据法会使软键盘高度增高 这真是一件恶心人的事情
        另外无法监听软键盘隐藏事件 无法使输入框和软键盘一起消失
        这也是意见非常恶心人的事情  尝试了有一千次

         */
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                int listHeight = listView.getHeight();



                if (oldListHigh > listHeight) {
                    if (cal1 == 0) {
                        cal1 = 1;
                        needScrollY = needScrollY + oldListHigh - listHeight;

                    } else if (cal1 == 1) {
                        int scrollResult = needScrollY + oldListHigh - listHeight;
                        listView.smoothScrollBy(scrollResult, 1);
                        needScrollY = 0;

                    }

                    oldListHigh = listHeight;
                }else if(oldListHigh<listHeight){
                    //变化大于100 说明软键盘隐藏了 如此这般可否
                    if(cal1==1&&listHeight-oldListHigh>100)
                    {
                        hideSoftkeyboard();

                    }
                }
            }
        });


    }


    /**
     *  点击文本框事件
     * @param v
     * @param tag
     * @param lv
     * @param type 如果type为0
     */
    protected  void popComment(View v,Object tag,AbsListView lv,int type) {
        EditText comment = mEnterLayout.content;

        String data = (String) v.getTag();
        String response;
//        showEnterLayout(tag);


        //设置@啊
        if(tag instanceof Comment)
        {

            if(TextUtils.isEmpty(comment.getText()))
            {
                comment.setText("@"+ ((Comment)tag).user.username+" ");
            }
        }

        mEnterLayout.show(tag);
        //把光标移动到最后
        comment.setSelection(comment.getText().length());


//            mEnterLayout.restoreLoad(commentObject);

        int itemLocation[] = new int[2];
        v.getLocationOnScreen(itemLocation);
        int itemHeight = v.getHeight();

        int listLocation[] = new int[2];
        lv.getLocationOnScreen(listLocation);
        int listHeight = lv.getHeight();

        oldListHigh = listHeight;
        //needScrollY = (itemLocation[1] + commonEnterRoot.getHeight()-itemHeight-4) - (listLocation[1] + listHeight);

        if (type == 0) {
            needScrollY = (itemLocation[1] + itemHeight) - (listLocation[1] + listHeight);
        } else {
            //  needScrollY = (itemLocation[1] + itemHeight + commonEnterRoot.getHeight()) - (listLocation[1] + listHeight);
            needScrollY = itemLocation[1] - (listLocation[1] + listHeight);
        }


        cal1 = 0;

        comment.requestFocus();



        Global.popSoftkeyboard(this, comment, true);
    }








    public void setFeedCommonClick(Feed2 feed,BaseFeedFragment.FeedHolder feedHolder)
    {
        if(feedHolder.rl_agree.getVisibility()==View.VISIBLE)
        {
            feedHolder.iv_agree.setOnClickListener(new ArgeeOnClickController(BaseFeedActivity.this,feed.agreement_status,feed.feedable_type, feed.feedable_id,feedHolder.tv_agreecount));
        }
    }


    protected  abstract  class BaseFeedAdapter extends BaseAdapter{

        protected ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    }




    public abstract void initFirstData();



    public void setLable(String lable) {
        toolbar.setTitle(lable);
    }


    public void showProgress() {
        mProgressBar.progressiveStart();
        mProgressBar.setVisibility(View.VISIBLE);
    }


    public void dissProgress() {
        mProgressBar.progressiveStop();
        mProgressBar.setVisibility(View.GONE);
    }

}

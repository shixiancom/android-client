package com.shixian.android.client.activities.fragment.base;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shixian.android.client.Global;
import com.shixian.android.client.R;
import com.shixian.android.client.contants.AppContants;
import com.shixian.android.client.enter.EnterLayout;
import com.shixian.android.client.model.Comment;
import com.shixian.android.client.model.Feed2;
import com.shixian.android.client.model.feeddate.BaseFeed;
import com.shixian.android.client.utils.ApiUtils;
import com.shixian.android.client.utils.ImageCallback;
import com.shixian.android.client.views.pulltorefreshlist.PullToRefreshBase;
import com.shixian.android.client.views.pulltorefreshlist.PullToRefreshListView;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by s0ng on 2015/2/12.
 */
public abstract  class BaseFeedFragment extends BaseFragment {

    protected int page = 1;
    protected PullToRefreshListView pullToRefreshListView;
    protected String firstPageDate;
    protected List<BaseFeed> feedList;
    protected ImageCallback callback;
    protected BaseAdapter adapter;


    /*************************************用于管理回复框 一级软键盘弹出 到这listView 变小  要滚动listView 这里网上资料很少 研究了差不多一天多***
     * 并且考虑到集成回复表情，但是考虑到论坛的性质 将回复表情功能砍掉*******************************************************************/
    protected ListView lv;
    protected int oldListHigh = 0;
    protected int needScrollY = 0;
    protected int cal1 = 0;
    protected View commonEnterRoot;
    protected EnterLayout mEnterLayout;

    //发送回复的监听发送事件
   protected  View.OnClickListener onClickSendText = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            String input = mEnterLayout.getContent();
            if (input.isEmpty()) {
                return;
            }

            //发送
            BaseFeed baseFeed= (BaseFeed) mEnterLayout.getTag();
            String type;
            String id;
            if (AppContants.FEADE_TYPE_COMMON.equals(baseFeed.feedable_type)) {
                type = ((Comment) baseFeed).commentable_type;
                id=((Comment) baseFeed).parent_id;
            } else {
                type = baseFeed.feedable_type;
                id=baseFeed.id;
            }
            String url=String.format(AppContants.COMMENT_URL,type,id);

            StringBuilder builder=new StringBuilder("{\"comment\" : { \"content\" : \"").append(input).append("\"}}");
            RequestParams params=new RequestParams();
            params.put("comment",builder.toString());
            ApiUtils.post(url, null, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
                    Log.d("AAAA", new String(bytes));

                    BaseFeed baseFeed = (BaseFeed) mEnterLayout.getTag();
                    Gson gson = new Gson();
                    Comment comment = gson.fromJson(new String(bytes), Comment.class);
                    if (baseFeed instanceof Comment) {
                        comment.parent_id = ((Comment) baseFeed).parent_id;
                        comment.project_id = ((Comment) baseFeed).project_id;
                        ((Comment)baseFeed).isLast=false;
                    } else {
                        comment.parent_id = baseFeed.id;
                        comment.project_id = ((Feed2) baseFeed).project_id;
                    }
                    feedList.add(baseFeed.position+1, comment);

                    adapter.notifyDataSetChanged();
                    mEnterLayout.clearContent();
                    hideSoftkeyboard();
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    Log.d("AAAA", new String(bytes));
                    hideSoftkeyboard();
                    Toast.makeText(context, R.string.check_net, Toast.LENGTH_SHORT).show();
                }
            });

        }
    };



    @Override
    public View initView(LayoutInflater inflater) {

        View view = inflater.inflate(R.layout.fragment_index, null, false);

        initLable();

        pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.lv_index);
        pullToRefreshListView.getListView().setDividerHeight(0);


        this.lv=pullToRefreshListView.getListView();
        commonEnterRoot=context.findViewById(R.id.commonEnterRoot);

        settingListView(lv);


        // 滚动到底自动加载可用
        pullToRefreshListView.setScrollLoadEnabled(true);

        // 设置下拉刷新的listener
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

            //下拉舒心完成
            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {

                //上啦刷新
                Log.i("AAAA", "1111-------------------------------------------------------------------");
                initFirst();
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                //getNewsList(moreUrl, false);
                //下拉加载更多
                Log.i("AAAA","-------------------------------------------------------------------");
                getNextData();



            }
        });

        feedList = new ArrayList<BaseFeed>();



        return view;
    }

    protected abstract void initFirst();

    protected abstract void initLable();


    protected void initImageCallBack() {
        this.callback=new ImageCallback() {

            @Override
            public void imageLoaded(Bitmap bitmap, Object tag) {
                ImageView imageView = (ImageView)pullToRefreshListView.getListView()
                        .findViewWithTag(tag);

                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        };
    }


    protected abstract  void getNextData();

    @Override
    public abstract  void initDate(Bundle savedInstanceState) ;


    protected abstract void initFirstData();


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

    protected void settingListView(ListView listView)
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
        mEnterLayout = new EnterLayout(context,onClickSendText);
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

                int listHeight = lv.getHeight();



                if (oldListHigh > listHeight) {
                    if (cal1 == 0) {
                        cal1 = 1;
                        needScrollY = needScrollY + oldListHigh - listHeight;

                    } else if (cal1 == 1) {
                        int scrollResult = needScrollY + oldListHigh - listHeight;
                        lv.smoothScrollBy(scrollResult, 1);
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
     */
   protected  void popComment(View v,Object tag,ListView lv) {
        EditText comment = mEnterLayout.content;

        String data = (String) v.getTag();
        String response;
//        showEnterLayout(tag);
        //TODO
        mEnterLayout.show(tag);

//            mEnterLayout.restoreLoad(commentObject);

        int itemLocation[] = new int[2];
        v.getLocationOnScreen(itemLocation);
        int itemHeight = v.getHeight();

        int listLocation[] = new int[2];
        lv.getLocationOnScreen(listLocation);
        int listHeight = lv.getHeight();

        oldListHigh = listHeight;
        needScrollY = (itemLocation[1] + commonEnterRoot.getHeight()-itemHeight-4) - (listLocation[1] + listHeight);


        cal1 = 0;

        comment.requestFocus();
        Global.popSoftkeyboard(context, comment, true);
    }



    public void showEnterLayout(Object tag)
    {
        mEnterLayout.show(tag);

    }





    public static class FeedHolder {

        //事件类型 比如发布一个项目
        public TextView tv_type;
        //头像
        public ImageView iv_icon;
        //用户名
        public TextView tv_name;
        //项目
        public TextView tv_proect;
        //时间
        public TextView tv_time;
        //回复内容
        public TextView tv_content;
        //图片内容 默认是隐藏的 当feedable_type为image时显示
        public ImageView iv_content;
        //回复框 发表项目的时候是隐藏的
        public TextView tv_response;
        public View v_line;
    }
}

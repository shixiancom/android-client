package com.shixian.android.client.activities.fragment.base;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
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
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.shixian.android.client.Global;
import com.shixian.android.client.R;
import com.shixian.android.client.contants.AppContants;
import com.shixian.android.client.controller.ArgeeOnClickController;
import com.shixian.android.client.enter.EnterLayout;
import com.shixian.android.client.model.Comment;
import com.shixian.android.client.model.Feed2;
import com.shixian.android.client.model.feeddate.BaseFeed;
import com.shixian.android.client.utils.ApiUtils;
import com.shixian.android.client.views.pulltorefreshlist.PullToRefreshBase;
import com.shixian.android.client.views.pulltorefreshlist.PullToRefreshListView;
import org.apache.http.Header;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by s0ng on 2015/2/12.
 */
public abstract class BaseFeedFragment extends AbsListViewBaseFragment {

    protected int page = 1;

    protected String firstPageDate;
    protected List<BaseFeed> feedList;
    protected BaseAdapter adapter;
    protected AbsListView listView;

    //图片加载有关＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
    //大头像
    //protected DisplayImageOptions feedOptions;




    /**
     * **********************************用于管理回复框 一级软键盘弹出 到这listView 变小  要滚动listView 这里网上资料很少 研究了差不多一天多***
     * 并且考虑到集成回复表情，但是考虑到论坛的性质 将回复表情功能砍掉******************************************************************
     */

    protected int oldListHigh = 0;
    protected int needScrollY = 0;
    protected int cal1 = 0;
    protected View commonEnterRoot;
    protected EnterLayout mEnterLayout;

    //发送回复的监听发送事件
    protected View.OnClickListener onClickSendText = new View.OnClickListener() {

        boolean sendAble = true;

        @Override
        public void onClick(View v) {

            if (sendAble) {
                sendAble = false;
            } else {
                Toast.makeText(context, "不要重复点击发送", Toast.LENGTH_SHORT).show();
                return;
            }


            String input = mEnterLayout.getContent();
            if (input.isEmpty()) {
                sendAble = true;
                return;
            }

            //发送
            BaseFeed baseFeed = (BaseFeed) mEnterLayout.getTag();
            String type;
            String id;
            if (AppContants.FEADE_TYPE_COMMON.equals(baseFeed.feedable_type)) {
                type = ((Comment) baseFeed).commentable_type;

                id = ((Comment) baseFeed).commentable_id;
            } else {


                type = baseFeed.feedable_type;
                id = baseFeed.feedable_id;
            }

            if ("UserProjectRelation".equals(type))
                type = "user_project_relation";
            String url = String.format(AppContants.COMMENT_URL, (type + "s").toLowerCase(), id);

            RequestParams params = new RequestParams();
            params.put("comment[content]", input);


            ApiUtils.post(context,url, params, new AsyncHttpResponseHandler() {
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

                    Toast.makeText(context, R.string.send_ok, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                    hideSoftkeyboard();
                    Toast.makeText(context, R.string.send_failed, Toast.LENGTH_SHORT).show();
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


    @Override
    public View initView(LayoutInflater inflater) {

        View view = inflater.inflate(R.layout.fragment_index, null, false);

        initLable();

        pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.lv_index);
        pullToRefreshListView.getListView().setDividerHeight(0);


        this.listView = pullToRefreshListView.getListView();


        commonEnterRoot = context.findViewById(R.id.commonEnterRoot);

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

        if (feedList == null)
            feedList = new ArrayList<BaseFeed>();


        return view;
    }

    protected abstract void initFirst();

    protected abstract void initLable();


    protected abstract void getNextData();

    @Override
    public abstract void initDate(Bundle savedInstanceState);


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

    protected void settingListView(final AbsListView listView) {
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
        mEnterLayout = new EnterLayout(context, onClickSendText);
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
                } else if (oldListHigh < listHeight) {
                    //变化大于100 说明软键盘隐藏了 如此这般可否
                    if (cal1 == 1 && listHeight - oldListHigh > 100) {
                        hideSoftkeyboard();

                    }
                }
            }
        });


    }


    /**
     * 点击文本框事件
     *
     * @param v
     * @param tag
     * @param lv
     * @param type 如果type为0
     */
    protected void popComment(View v, Object tag, AbsListView lv, int type) {
        EditText comment = mEnterLayout.content;

        String data = (String) v.getTag();
        String response;
//        showEnterLayout(tag);


        //设置@啊
        if (tag instanceof Comment) {

            if (TextUtils.isEmpty(comment.getText())) {
                comment.setText("@" + ((Comment) tag).user.username + " ");
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
        Global.popSoftkeyboard(context, comment, true);
    }


    public void showEnterLayout(Object tag) {
        mEnterLayout.show(tag);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

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
        public LinearLayout ll_body;
        public RelativeLayout rl_title;

        public RelativeLayout rl_agree;

        public ImageView iv_agree;

        public TextView tv_agreecount;

    }

    public static class CommentHolder {


        //头像
        public ImageView iv_icon;
        //用户名
        public TextView tv_name;


        //时间
        public TextView tv_time;
        //回复内容
        public TextView tv_content;

        //回复框 发表项目的时候是隐藏的
        public TextView tv_response;
        //        public View v_line;
        public LinearLayout ll_body;

    }


    protected abstract class BaseFeedAdapter extends BaseAdapter {

        protected ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    }




    public abstract void initFirstData();


}

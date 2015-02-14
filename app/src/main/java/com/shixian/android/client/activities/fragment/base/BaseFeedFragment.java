package com.shixian.android.client.activities.fragment.base;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.shixian.android.client.R;
import com.shixian.android.client.model.feeddate.BaseFeed;
import com.shixian.android.client.utils.ImageCallback;
import com.shixian.android.client.views.pulltorefreshlist.PullToRefreshBase;
import com.shixian.android.client.views.pulltorefreshlist.PullToRefreshListView;

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


    @Override
    public View initView(LayoutInflater inflater) {

        View view = inflater.inflate(R.layout.fragment_index, null, false);

        initLable();

        pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.lv_index);
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


    protected class FeedHolder {

        //事件类型 比如发布一个项目
        TextView tv_type;
        //头像
        ImageView iv_icon;
        //用户名
        TextView tv_name;
        //项目
        TextView tv_proect;
        //时间
        TextView tv_time;
        //回复内容
        TextView tv_content;
        //图片内容 默认是隐藏的 当feedable_type为image时显示
        ImageView iv_content;
        //回复框 发表项目的时候是隐藏的
        TextView tv_response;
        View v_line;
    }
}

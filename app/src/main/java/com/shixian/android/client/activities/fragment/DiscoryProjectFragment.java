package com.shixian.android.client.activities.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shixian.android.client.R;
import com.shixian.android.client.activities.fragment.base.BaseFragment;
import com.shixian.android.client.contants.AppContants;
import com.shixian.android.client.model.Project;
import com.shixian.android.client.utils.ApiUtils;
import com.shixian.android.client.utils.CommonUtil;
import com.shixian.android.client.views.pulltorefreshlist.PullToRefreshBase;
import com.shixian.android.client.views.pulltorefreshlist.PullToRefreshListView;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by s0ng on 2015/2/12.
 */
public class DiscoryProjectFragment extends BaseFragment {

    private PullToRefreshListView pullToRefreshListView;
    private List<Project> projectList;
    private String firstPageDate;
    private String TAG="DiscoryProjectFragment";
    private ProjectAdapter adapter;


    @Override
    public View initView(LayoutInflater inflater) {

        View view=View.inflate(context, R.layout.fragment_index,null);

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
                initDate(null);
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

        projectList= new ArrayList<Project>();


        return view;
    }

    private void getNextData() {

    }

    @Override
    public void initDate(Bundle savedInstanceState) {
        initFirstData();
    }

    private void initFirstData() {

        ApiUtils.get(AppContants.DESCORY_PROJECT_URL,null,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                final String temp = new String(bytes);
                if (!AppContants.errorMsg.equals(bytes)) {
                    //获取第一页数据

                    new Thread() {
                        public void run() {


                            //数据格式
                           CommonUtil.logDebug(TAG, new String(temp));




                           // projectList.addAll(JsonUtils.ParseFeeds(firstPageDate));

                            //TODO 第一页的缓存

                            //保存数据到本地





                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (adapter == null) {
                                        adapter = new ProjectAdapter();

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
                Toast.makeText(context, getString(R.string.check_net), Toast.LENGTH_SHORT);
            }
        });
    }

    class ProjectAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return null;
        }
    }
}

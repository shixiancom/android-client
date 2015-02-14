package com.shixian.android.client.activities.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shixian.android.client.R;
import com.shixian.android.client.activities.fragment.base.BaseFragment;
import com.shixian.android.client.contants.AppContants;
import com.shixian.android.client.model.Project;
import com.shixian.android.client.utils.ApiUtils;
import com.shixian.android.client.utils.CommonUtil;
import com.shixian.android.client.utils.JsonUtils;
import com.shixian.android.client.utils.SharedPerenceUtil;
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
    private String TAG = "DiscoryProjectFragment";
    private ProjectAdapter adapter;
    private int page = 1;


    @Override
    public View initView(LayoutInflater inflater) {

        View view = View.inflate(context, R.layout.fragment_index, null);

        context.setLable("发现");

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
                initFirst();
            }



            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                //getNewsList(moreUrl, false);
                //下拉加载更多
                getNextData();


            }
        });

        projectList = new ArrayList<Project>();

        initCacheData();


        return view;
    }

    private void initFirst() {
        initFirstData();

    }

    private void initCacheData() {
        firstPageDate= SharedPerenceUtil.getProjectDiscoryProject(context);

        projectList= JsonUtils.ParsesProject(firstPageDate);

        if (adapter == null) {
            adapter = new ProjectAdapter();

        } else {
            adapter.notifyDataSetChanged();
        }

    }

    private void getNextData() {

        page += 1;
        RequestParams params = new RequestParams();
        params.add("page", page + "");
        ApiUtils.get(AppContants.DESCORY_PROJECT_URL, params, new AsyncHttpResponseHandler() {
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
                            projectList.addAll(JsonUtils.ParsesProject(temp));

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
                page -= 1;
                pullToRefreshListView.onPullUpRefreshComplete();
            }
        });
    }

    @Override
    public void initDate(Bundle savedInstanceState) {

        if(projectList!=null&&projectList.size()>0)
        {
            if (adapter == null) {
                adapter = new ProjectAdapter();


                pullToRefreshListView.getRefreshableView().setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }


        }else{

            initFirst();
        }

    }

    private void initFirstData() {
        context.showProgress();

        ApiUtils.get(AppContants.DESCORY_PROJECT_URL, null, new AsyncHttpResponseHandler() {
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
                            projectList.addAll(JsonUtils.ParsesProject(temp));

                            page = 1;


                            SharedPerenceUtil.putProjectDiscoryProject(context,temp);

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
                    pullToRefreshListView.onPullDownRefreshComplete();
                    context.dissProgress();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(context, getString(R.string.check_net), Toast.LENGTH_SHORT);
                pullToRefreshListView.onPullDownRefreshComplete();
                context.dissProgress();
            }
        });
    }

    class ProjectAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return projectList.size();
        }

        @Override
        public Object getItem(int position) {
            return projectList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view;
            Holder holder;

            if (convertView == null) {
                view = View.inflate(context, R.layout.discoryproject_item, null);
                holder = new Holder();
                holder.tv_content = (TextView) view.findViewById(R.id.tv_content);
                holder.tv_fllowen = (TextView) view.findViewById(R.id.tv_fllowen);
                holder.tv_title = (TextView) view.findViewById(R.id.tv_title);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (Holder) view.getTag();
            }

            Project project = projectList.get(position);
            holder.tv_title.setText(project.title);
            if (!TextUtils.isEmpty(project.description))
                holder.tv_content.setText(Html.fromHtml(project.description));

            if (project.has_followed) {
                holder.tv_fllowen.setBackgroundColor(Color.BLACK);
            } else {
                holder.tv_fllowen.setBackgroundColor(Color.BLUE);
            }


            return view;
        }
    }

    class Holder {
        TextView tv_title;
        TextView tv_fllowen;
        TextView tv_content;
    }
}

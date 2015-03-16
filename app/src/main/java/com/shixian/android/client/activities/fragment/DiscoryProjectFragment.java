package com.shixian.android.client.activities.fragment;

import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shixian.android.client.R;
import com.shixian.android.client.activities.MainActivity;
import com.shixian.android.client.activities.fragment.base.BaseFragment;
import com.shixian.android.client.contants.AppContants;
import com.shixian.android.client.controller.DiscoryOnClickColler;
import com.shixian.android.client.handler.content.ContentHandler;
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

    private int currentFirstPos=0;



    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)context).initMsgStatus();
        ((MainActivity)context).setCurrentFragment(this);


    }


    @Override
    public View initView(LayoutInflater inflater) {

        View view = View.inflate(context, R.layout.fragment_index, null);

        context.setLable(getString(R.string.label_discover));

        pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.lv_index);
        // 滚动到底自动加载可用
        pullToRefreshListView.setScrollLoadEnabled(true);

        //去掉分割线
        pullToRefreshListView.getListView().setDividerHeight(0);

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

        pullToRefreshListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                currentFirstPos=pullToRefreshListView.getListView().getFirstVisiblePosition();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });




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
            pullToRefreshListView.getListView().setAdapter(adapter);

        } else {
            pullToRefreshListView.getListView().setAdapter(adapter);


        }

    }

    private void getNextData() {
        ((MainActivity)context).initMsgStatus();

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
                pullToRefreshListView.getRefreshableView().setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }
            if(currentFirstPos<=projectList.size())
                pullToRefreshListView.getListView().setSelection(currentFirstPos);

        }else{
            initCacheData();
            initFirst();
        }


    }

    private void initFirstData() {
        context.showProgress();

        ((MainActivity)context).initMsgStatus();

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
                            projectList=JsonUtils.ParsesProject(temp);

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

    @Override
    public void onDestroyView() {
        currentFirstPos=pullToRefreshListView.getListView().getFirstVisiblePosition();
        super.onDestroyView();

    }

    @Override
    public void setCurrentPosition(int position) {
        pullToRefreshListView.getListView().setSelection(position);
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
            final Holder holder;

            if (convertView == null) {
                view = View.inflate(context, R.layout.discoryproject_item, null);
                holder = new Holder();
                holder.tv_content = (TextView) view.findViewById(R.id.tv_content);
                holder.bt_fllowen = (Button) view.findViewById(R.id.bt_fllowen);
                holder.tv_title = (TextView) view.findViewById(R.id.tv_title);
                view.setTag(holder);
            } else {
                view = convertView;
                holder = (Holder) view.getTag();
            }

            final Project project = projectList.get(position);
            holder.tv_title.setText(project.title);
            if (!TextUtils.isEmpty(project.description))
                holder.tv_content.setText(Html.fromHtml(project.description));

            if(project.has_followed)
            {
                //TODO
               holder.bt_fllowen.setBackgroundResource(R.drawable.shape_unfollow);
                holder.bt_fllowen.setText(R.string.following);
               // holder.tv_fllowen.setBackgroundResource(R.drawable.unfollow);


            }else{
                holder.bt_fllowen.setBackgroundResource(R.drawable.shape_follow);
                holder.bt_fllowen.setText(R.string.follow);
              //  holder.tv_fllowen.setBackgroundResource(R.drawable.follow);
            }


            //设置监听事件
            DiscoryOnClickColler onClickColler=new DiscoryOnClickColler(context,project);
            holder.tv_content .setOnClickListener(onClickColler);
            new ContentHandler(holder.tv_content).longClickCopy();

            holder.tv_title.setOnClickListener(onClickColler);
            holder.bt_fllowen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(project.has_followed)
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

                        Toast.makeText(context,"暂不支持取消功能，我们正在开发",Toast.LENGTH_SHORT).show();
                    }else{
                        //关注api
                        ApiUtils.post(String.format(AppContants.PROJECT_FOLLOW_URL,project.id),null,new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                                Toast.makeText(context,"关注成功",Toast.LENGTH_SHORT).show();
                                holder.bt_fllowen.setBackgroundResource(R.drawable.shape_unfollow);
                                holder.bt_fllowen.setText(R.string.following);
                                project.has_followed=true;
                            }

                            @Override
                            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                                Toast.makeText(context,"关注失败，稍后再试",Toast.LENGTH_SHORT).show();

                            }
                        });
                    }

                }
            });



            return view;
        }
    }




    class Holder {
        TextView tv_title;
        Button bt_fllowen;
        TextView tv_content;
    }
}

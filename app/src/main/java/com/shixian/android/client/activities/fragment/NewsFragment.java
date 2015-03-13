package com.shixian.android.client.activities.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.shixian.android.client.R;
import com.shixian.android.client.activities.DetailActivity;
import com.shixian.android.client.activities.MainActivity;
import com.shixian.android.client.activities.fragment.base.AbsListViewBaseFragment;
import com.shixian.android.client.activities.fragment.base.BaseFeedFragment;
import com.shixian.android.client.contants.AppContants;
import com.shixian.android.client.controller.IndexOnClickController;
import com.shixian.android.client.controller.NewsOnClickController;
import com.shixian.android.client.handler.content.ContentHandler;
import com.shixian.android.client.model.News;
import com.shixian.android.client.utils.ApiUtils;
import com.shixian.android.client.utils.DisplayUtil;
import com.shixian.android.client.utils.JsonUtils;
import com.shixian.android.client.utils.SharedPerenceUtil;
import com.shixian.android.client.utils.TimeUtil;
import com.shixian.android.client.views.pulltorefreshlist.PullToRefreshBase;
import com.shixian.android.client.views.pulltorefreshlist.PullToRefreshListView;
import org.apache.http.Header;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by doom on 15/2/8.
 */
public class NewsFragment extends AbsListViewBaseFragment
{
    private static final String TAG = "NewsFragment";


    private List<News> newsList;
    private String firstPageData;

    private NewsAdapter adapter;

    private int page=1;


    DisplayImageOptions feedOptions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //头像的
         feedOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_icon)
                .showImageForEmptyUri(R.drawable.default_icon)
                .showImageOnFail(R.drawable.default_icon)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(5))
                .build();


    }





    @Override
    public View initView(LayoutInflater inflater) {

        View view=inflater.inflate(R.layout.fragment_index,null,false);



        pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.lv_index);
        // 滚动到底自动加载可用
        pullToRefreshListView.setScrollLoadEnabled(true);

        pullToRefreshListView.getListView().setDividerHeight(0);

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

        newsList = new ArrayList<News>();




        return view;
    }

    private void initCacheData() {

        newsList=JsonUtils.parseNews(SharedPerenceUtil.getNews(context)) ;


        if (adapter == null) {
            adapter = new NewsAdapter();
            pullToRefreshListView.getListView().setAdapter(adapter);

        } else {
            pullToRefreshListView.getListView().setAdapter(adapter);

        }


    }

    @Override
    public void initDate(Bundle savedInstanceState) {


        if(newsList!=null&&newsList.size()>0)
        {
            if (adapter == null) {
                adapter = new NewsAdapter();


                pullToRefreshListView.getRefreshableView().setAdapter(adapter);
            } else {
                pullToRefreshListView.getRefreshableView().setAdapter(adapter);

            }

            if (currentFirstPos <= newsList.size())
                pullToRefreshListView.getListView().setSelection(currentFirstPos);


        }else{

            initCacheData();
            initFirst();
        }

    }

    @Override
    public void setCurrentPosition(int position) {
        pullToRefreshListView.getListView().setSelection(position);
    }

    private void initFirst() {

        context.showProgress();
        ApiUtils.get(AppContants.NOTIFICATION_URL,null,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {

                final String temp=new String(bytes);
                if(!AppContants.errorMsg.equals(temp))
                {
                    firstPageData=temp;

                    new Thread(){
                        public  void run()
                        {
                            newsList= JsonUtils.parseNews(temp);


                            SharedPerenceUtil.putNews(context,temp);
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(adapter==null)
                                    {
                                        adapter=new NewsAdapter();
                                        pullToRefreshListView.getListView().setAdapter(adapter);
                                    }else{
                                        adapter.notifyDataSetChanged();
                                    }
                                }
                            });
                        }
                    }.start();
                    page=1;

                    pullToRefreshListView.onPullDownRefreshComplete();
                    context.dissProgress();


                }

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(context, R.string.check_net, Toast.LENGTH_SHORT);
                pullToRefreshListView.onPullDownRefreshComplete();
                context.dissProgress();
            }
        });
    }


    public void getNextData()
    {
        page+=1;
        RequestParams params=new RequestParams();
        params.add("page",page+"");
        ApiUtils.get(AppContants.NOTIFICATION_URL,params,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {

                final String temp=new String(bytes);
                if(!AppContants.errorMsg.equals(temp))
                {
                    firstPageData=temp;

                    new Thread(){
                        public  void run()
                        {
                            newsList.addAll(JsonUtils.parseNews(temp));


                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(adapter==null)
                                    {
                                        adapter=new NewsAdapter();
                                        pullToRefreshListView.getListView().setAdapter(adapter);
                                    }else{
                                        adapter.notifyDataSetChanged();
                                    }

                                    pullToRefreshListView.onPullUpRefreshComplete();
                                }
                            });
                        }
                    }.start();





                }else {
                    pullToRefreshListView.onPullUpRefreshComplete();
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(context, R.string.check_net, Toast.LENGTH_SHORT);
                pullToRefreshListView.onPullUpRefreshComplete();
                page-=1;
            }
        });
    }







    public static final int TYPE_REQUEST=1;
    public static final int TYPE_OTHER=0;

    class NewsAdapter extends BaseAdapter{


        protected ImageLoadingListener animateFirstListener = new BaseFeedFragment.AnimateFirstDisplayListener();


        @Override
        public int getCount() {
            return newsList.size();
        }

        @Override
        public Object getItem(int position) {
            return newsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            if("Notification".equals(newsList.get(position).type))
            {
                return TYPE_OTHER;
            }else{
                return TYPE_REQUEST;
            }
        }


        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view=null;

            final News news=newsList.get(position);

            switch (getItemViewType(position))
            {
                case TYPE_OTHER:
                    if(convertView==null)
                    {


                        view=View.inflate(context,R.layout.news_item,null);
                        NewsHolder holder=new NewsHolder();
                        view.setTag(holder);
                        holder.iv_icon= (ImageView) view.findViewById(R.id.iv_icon);
                        holder.tv_name= (TextView) view.findViewById(R.id.tv_name);
                        holder.tv_type= (TextView) view.findViewById(R.id.tv_type);
                        holder.tv_project= (TextView) view.findViewById(R.id.tv_project);
                        holder.tv_time= (TextView) view.findViewById(R.id.tv_time);
                        holder.tv_content= (TextView) view.findViewById(R.id.tv_content);
                        holder.bt_accept= (Button) view.findViewById(R.id.bt_accept);
                        holder.tv_add= (TextView) view.findViewById(R.id.tv_add);
                        holder.tv_add_pri= (TextView) view.findViewById(R.id.tv_addpri);
                        holder.tv_post_type=(TextView)view.findViewById(R.id.tv_post_type);

                    }else{
                        view=convertView;


                    }

                    NewsHolder holder= (NewsHolder) view.getTag();
                    holder.bt_accept.setVisibility(View.GONE);
                    holder.tv_content.setVisibility(View.VISIBLE);
                    holder.tv_time.setText(TimeUtil.getDistanceTime(news.created_at));
                    holder.tv_project.setVisibility(View.VISIBLE);
                    holder.tv_add.setVisibility(View.VISIBLE);
                    holder.tv_add_pri.setVisibility(View.GONE);
                    holder.tv_post_type.setVisibility(View.GONE);

                    //头像图片处理

                    ImageLoader.getInstance().displayImage(AppContants.DOMAIN + news.user.avatar.small.url, holder.iv_icon, feedOptions, animateFirstListener);


                    ViewGroup.LayoutParams params = holder.iv_icon.getLayoutParams();
                    int imageSize= DisplayUtil.dip2px(context, 40);
                    params.height=imageSize;
                    params.width =imageSize;
                    holder.iv_icon.setLayoutParams(params);

                    new ContentHandler(holder.tv_content).longClickCopy();


                    switch (news.noti_type) {
                        case "invit_follow":
                            holder.tv_content.setVisibility(View.GONE);

                            if (news.project.getTitle().length() > 10) {
                                holder.tv_project.setText(news.project.getTitle().substring(0, 8) + "...");
                            } else {
                                holder.tv_project.setText(news.project.getTitle());
                            }
                            holder.tv_project.setText(news.project.getTitle());
                            holder.tv_name.setText(news.user.username);
                            holder.tv_type.setText(R.string.please_fllow);
                            holder.tv_add.setVisibility(View.GONE);
                            break;
                        case "join_accept":
                            holder.tv_content.setVisibility(View.GONE);

                            String project;
                            if (news.project.getTitle().length() > 10) {
                                project = news.project.getTitle().substring(0, 8) + "...";
                            } else {
                                project = news.project.getTitle();
                            }
                            holder.tv_project.setText(Html.fromHtml(getResources().getString(R.string.project_join_accept).replace("{project_title}", project)));
                            holder.tv_name.setText(news.user.username);
                            holder.tv_type.setText(R.string.join_accept);
                            holder.tv_add.setText("请求");
                            holder.tv_add_pri.setVisibility(View.VISIBLE);
                            holder.tv_add_pri.setText("的");
                            break;
                        case "join_reject":

                            holder.tv_content.setVisibility(View.GONE);

                            if (news.project.getTitle().length() > 10) {
                                project = news.project.getTitle().substring(0, 8) + "...";
                            } else {
                                project = news.project.getTitle();
                            }
                            holder.tv_project.setText(Html.fromHtml(getResources().getString(R.string.project_join_accept).replace("{project_title}", news.project.getTitle())));
                            holder.tv_name.setText(news.user.username);
                            holder.tv_type.setText(R.string.join_reject);
                            holder.tv_add.setText("请求");
                            holder.tv_add_pri.setVisibility(View.VISIBLE);
                            holder.tv_add_pri.setText("的");
                            break;
                        case "new_comment":
                            holder.tv_content.setVisibility(View.VISIBLE);

                            if (news.project.getTitle().length() > 10) {
                                project = news.project.getTitle().substring(0, 8) + "...";
                            } else {
                                project = news.project.getTitle();
                            }
                            holder.tv_project.setText(project);
                            holder.tv_name.setText(news.user.username);
                            holder.tv_type.setText(Html.fromHtml(getResources().getString(R.string.addcomment)));
                            holder.tv_content.setText(news.data.content);
                            holder.tv_post_type.setVisibility(View.VISIBLE);
                            holder.tv_post_type.setText("回复");
                            holder.tv_add.setVisibility(View.GONE);
                            break;
                        case "new_entity":
                            if (news.project.getTitle().length() > 10) {
                                project = news.project.getTitle().substring(0, 8) + "...";
                            } else {
                                project = news.project.getTitle();
                            }
                            switch (news.notifiable_type) {

                                case "Attachment":
                                    holder.tv_content.setVisibility(View.VISIBLE);

                                    holder.tv_project.setText(Html.fromHtml(getResources().getString(R.string.project_attachment).replace("{project_title}", project)));
                                    holder.tv_name.setText(news.user.username);
                                    holder.tv_type.setText(R.string.attachment);
                                    holder.tv_content.setText(news.data.content);
                                    holder.tv_add.setText("文件");
                                    holder.tv_add_pri.setVisibility(View.VISIBLE);
                                    holder.tv_add_pri.setText("添加了");

                                    break;
                                case "Homework":
                                    holder.tv_content.setVisibility(View.VISIBLE);
                                    holder.tv_project.setText(Html.fromHtml(getResources().getString(R.string.project_task).replace("{project_title}", project)));
                                    holder.tv_name.setText(news.user.username);
                                    holder.tv_type.setText(R.string.attachment);
                                    holder.tv_content.setText(news.data.content);
                                    holder.tv_add.setText("任务提交");
                                    holder.tv_add_pri.setVisibility(View.VISIBLE);
                                    holder.tv_add_pri.setText("添加了");

                                    break;
                                case "Idea":
                                    holder.tv_content.setVisibility(View.VISIBLE);
                                    holder.tv_project.setText(Html.fromHtml(getResources().getString(R.string.project_idea).replace("{project_title}", project)));
                                    holder.tv_name.setText(news.user.username);
                                    holder.tv_type.setText(R.string.attachment);
                                    holder.tv_add.setText("想法");
                                    holder.tv_content.setText(news.data.content);
                                    holder.tv_add_pri.setVisibility(View.VISIBLE);
                                    holder.tv_add_pri.setText("添加了");

                                    break;

                                case "Image":
                                    holder.tv_content.setVisibility(View.VISIBLE);
                                    holder.tv_project.setVisibility(View.VISIBLE);
                                    holder.tv_project.setText(Html.fromHtml(getResources().getString(R.string.project_image).replace("{project_title}", project)));
                                    holder.tv_name.setText(news.user.username);
                                    holder.tv_type.setText(R.string.attachment);
                                    holder.tv_content.setText(news.data.content);
                                    holder.tv_add.setText("图片");
                                    holder.tv_add_pri.setVisibility(View.VISIBLE);
                                    holder.tv_add_pri.setText("添加了");
                                    break;
                                case "Plan":
                                    holder.tv_content.setVisibility(View.VISIBLE);
                                    holder.tv_project.setVisibility(View.VISIBLE);
                                    holder.tv_project.setText(Html.fromHtml(getResources().getString(R.string.project_plan).replace("{project_title}", project)));
                                    holder.tv_name.setText(news.user.username);
                                    holder.tv_type.setText(R.string.attachment);
                                    holder.tv_add.setText("计划");
                                    holder.tv_add_pri.setVisibility(View.VISIBLE);
                                    holder.tv_add_pri.setText("添加了");
                                    if (news.data != null)
                                        holder.tv_content.setText(news.data.content);
                                    break;
                                case "Vote":
                                    holder.tv_content.setVisibility(View.VISIBLE);
                                    holder.tv_project.setText(Html.fromHtml(getResources().getString(R.string.project_vote).replace("{project_title}", project)));
                                    holder.tv_name.setText(news.user.username);
                                    holder.tv_type.setText(R.string.attachment);
                                    holder.tv_content.setText(news.data.content);
                                    holder.tv_add.setText("投票");
                                    holder.tv_add_pri.setVisibility(View.VISIBLE);
                                    holder.tv_add_pri.setText("添加了");
                                    break;

                                case "Task":
                                    holder.tv_content.setVisibility(View.VISIBLE);
                                    holder.tv_project.setText(Html.fromHtml(getResources().getString(R.string.project_vote).replace("{project_title}", project)));
                                    holder.tv_name.setText(news.user.username);
                                    holder.tv_type.setText(R.string.attachment);
                                    holder.tv_content.setText(news.data.content);
                                    holder.tv_add.setText("任务");
                                    holder.tv_add_pri.setVisibility(View.VISIBLE);
                                    holder.tv_add_pri.setText("添加了");
                                    break;


                            }


                            break;

                        case "UserRelation":
                            holder.tv_content.setVisibility(View.GONE);
                            holder.tv_project.setVisibility(View.GONE);
                            holder.tv_name.setText(news.user.username);
                            holder.tv_type.setText("关注了你");
                            holder.tv_add.setVisibility(View.GONE);
                            break;

                        case "new_follow":
                            holder.tv_add.setVisibility(View.GONE);
                            holder.tv_content.setVisibility(View.GONE);
                            holder.tv_project.setVisibility(View.GONE);
                            holder.tv_name.setText(news.user.username);
                            holder.tv_type.setText("关注了你");

                            break;

                        case "new_homework":
                            if (news.project.getTitle().length() > 10) {
                                project = news.project.getTitle().substring(0, 8) + "...";
                            } else {
                                project = news.project.getTitle();
                            }
                            holder.tv_content.setVisibility(View.VISIBLE);
                            holder.tv_project.setText(Html.fromHtml(getResources().getString(R.string.project_homework).replace("{project_title}", project)));
                            holder.tv_name.setText(news.user.username);
                            holder.tv_type.setText(R.string.attachment);
                            holder.tv_content.setText(news.data.content);
                            holder.tv_add.setText("任务");
                            holder.tv_add_pri.setVisibility(View.VISIBLE);
                            holder.tv_add_pri.setText("完成了您分配的");
                            break;
                        case "new_mention":

                            holder.tv_add.setVisibility(View.GONE);
                            switch (news.notifiable_type) {
                                case "Attachment":
                                    holder.tv_project.setVisibility(View.GONE);

                                    holder.tv_content.setVisibility(View.VISIBLE);
                                    holder.tv_name.setText(news.user.username);
                                    holder.tv_type.setText(Html.fromHtml(getResources().getString(R.string.new_mention_attachment)));
                                    holder.tv_content.setText(news.data.content);


                                    break;
                                case "Comment":
                                    holder.tv_project.setVisibility(View.GONE);

                                    holder.tv_content.setVisibility(View.VISIBLE);
                                    holder.tv_name.setText(news.user.username);
                                    holder.tv_type.setText(Html.fromHtml(getResources().getString(R.string.new_mention_comment)));
                                    holder.tv_content.setText(news.data.content);
                                    break;

                                case "Homework":
                                    holder.tv_project.setVisibility(View.GONE);

                                    holder.tv_content.setVisibility(View.VISIBLE);
                                    holder.tv_name.setText(news.user.username);
                                    holder.tv_type.setText(Html.fromHtml(getResources().getString(R.string.new_mention_homework)));
                                    holder.tv_content.setText(news.data.content);
                                    break;

                                case "Idea":
                                    holder.tv_project.setVisibility(View.GONE);

                                    holder.tv_content.setVisibility(View.VISIBLE);
                                    holder.tv_name.setText(news.user.username);
                                    holder.tv_type.setText(Html.fromHtml(getResources().getString(R.string.new_mention_idea)));
                                    holder.tv_content.setText(news.data.content);
                                    break;

                                case "Image":
                                    holder.tv_project.setVisibility(View.GONE);

                                    holder.tv_content.setVisibility(View.VISIBLE);
                                    holder.tv_name.setText(news.user.username);
                                    holder.tv_type.setText(Html.fromHtml(getResources().getString(R.string.new_mention_image)));
                                    holder.tv_content.setText(news.data.content);
                                    break;

                                case "task":
                                    holder.tv_project.setVisibility(View.GONE);

                                    holder.tv_content.setVisibility(View.VISIBLE);
                                    holder.tv_name.setText(news.user.username);
                                    holder.tv_type.setText(Html.fromHtml(getResources().getString(R.string.new_mention_task)));
                                    holder.tv_content.setText(news.data.content);
                                    break;

                            }
                            break;

                        case "new_reply":
                            if (news.project.getTitle().length() > 10) {
                                project = news.project.getTitle().substring(0, 8) + "...";
                            } else {
                                project = news.project.getTitle();
                            }
                            holder.tv_project.setText(project);
                            holder.tv_content.setVisibility(View.VISIBLE);
                            holder.tv_name.setText(news.user.username);
                            holder.tv_type.setText(Html.fromHtml(getResources().getString(R.string.new_reply)));
                            holder.tv_content.setText(news.data.content);
                            holder.tv_post_type.setVisibility(View.VISIBLE);
                            holder.tv_post_type.setText("回复");
                            holder.tv_add.setVisibility(View.GONE);


                            break;

                        case "new_task":

                            holder.tv_project.setVisibility(View.VISIBLE);

                            holder.tv_content.setVisibility(View.VISIBLE);
                            holder.tv_name.setText(news.user.username);
                            holder.tv_type.setText(Html.fromHtml("在"));

                            holder.tv_content.setText(news.data.content);
                            if (news.project.getTitle().length() > 10) {
                                project = news.project.getTitle().substring(0, 8) + "...";
                            } else {
                                project = news.project.getTitle();
                            }
                            holder.tv_project.setText(Html.fromHtml(getResources().getString(R.string.project_new_task).replace("{project_title}", project)));
                            holder.tv_add.setText("任务");
                            holder.tv_add_pri.setVisibility(View.VISIBLE);
                            holder.tv_add_pri.setText("给你分配了");
                            break;

                        case "new_agreement":

                            break;
                    }


                    View.OnClickListener onClickListener=new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            Bundle bundle=new Bundle();

                            bundle.putSerializable("news",news);

                            //跳转到个人主页

                            Intent intent=new Intent(context, DetailActivity.class);

                            bundle.putInt("type", IndexOnClickController.TYPE_MSG_DETILA);

                            intent.putExtras(bundle);

                            context.startActivity(intent);


                        }
                    };



                    //点击事件
                    if(holder.tv_add.VISIBLE==View.VISIBLE)
                    {

                        holder.tv_add.setOnClickListener(onClickListener);
                    }
                    if(holder.tv_type.VISIBLE==View.VISIBLE)
                    {
                        holder.tv_type.setOnClickListener(onClickListener);
                    }

                    if(holder.tv_content.VISIBLE==View.VISIBLE)
                    {
                        holder.tv_content.setOnClickListener(onClickListener);
                    }

                    if(holder.tv_post_type.VISIBLE==View.VISIBLE)
                    {
                        holder.tv_post_type.setOnClickListener(onClickListener);
                    }



                    NewsOnClickController newsOnClickController=new NewsOnClickController(context,news);


                    holder.tv_name.setOnClickListener(newsOnClickController);


                    holder.iv_icon.setOnClickListener(newsOnClickController);


                    if(holder.tv_project.VISIBLE==View.VISIBLE)
                    {
                        holder.tv_project.setOnClickListener(newsOnClickController);
                    }

                        break;
                case TYPE_REQUEST:
//                    holder.tv_project.setVisibility(View.VISIBLE);
//                    holder.bt_accept.setVisibility(View.GONE);
//
//                    holder.tv_content.setVisibility(View.VISIBLE);
//
//                    holder.tv_name.setText(news.user.username);
//                    holder.tv_type.setText("请求加入项目 ");
//                    holder.tv_content.setText(Html.fromHtml(StringUtils.trmDiv(news.data.content_html)));
//
//
//                    holder.tv_project.setText(news.project.getTitle());
//                    holder.tv_add.setVisibility(View.GONE);

                    break;
            }










            return view;
        }

    }

    class NewsHolder{
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_type;
        TextView tv_project;
        TextView tv_time;
        TextView tv_content;
        Button bt_accept;
        TextView tv_add;
        TextView tv_add_pri;
        TextView tv_post_type;
    }

    @Override
    public void onResume() {
        super.onResume();
        context.setLable(getString(R.string.label_notifications));
        ((MainActivity)context).setCurrentFragment(this);
        ((MainActivity)context).hideMsg();

    }


}

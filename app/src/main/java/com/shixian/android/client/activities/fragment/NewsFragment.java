package com.shixian.android.client.activities.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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


    private TextView tv_blank;

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

        View view=inflater.inflate(R.layout.fragment_news,null,false);



        pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.lv_index);
        // 滚动到底自动加载可用
        pullToRefreshListView.setScrollLoadEnabled(true);

        pullToRefreshListView.getListView().setDividerHeight(0);

        pullToRefreshListView.getFooterLoadingLayout().show(false);

        // 设置下拉刷新的listener
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

            //下拉舒心完成
            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {

                //上啦刷新
                initFirstData();            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                //getNewsList(moreUrl, false);
                //下拉加载更多
                getNextData();



            }
        });

        newsList = new ArrayList<News>();


        tv_blank= (TextView) view.findViewById(R.id.tv_blank);


        return view;
    }

    private void initCacheData() {

        newsList=JsonUtils.parseNews(SharedPerenceUtil.getNews(context.getApplicationContext())) ;
        if(newsList==null||newsList.size()==0)
        {
            tv_blank.setVisibility(View.VISIBLE);
        }

        if(newsList!=null&&newsList.size()>0){
            tv_blank.setVisibility(View.GONE);
        }

        if (adapter == null) {
            adapter = new NewsAdapter();
            pullToRefreshListView.getListView().setAdapter(adapter);

        } else {
            pullToRefreshListView.getListView().setAdapter(adapter);
            adapter.notifyDataSetChanged();
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

            }



        }else{

            initCacheData();
            initFirstData();
        }

    }



    public void initFirstData() {

        context.showProgress();
        ApiUtils.get(context,AppContants.NOTIFICATION_URL,null,new AsyncHttpResponseHandler() {
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

                                    if(newsList==null||newsList.size()==0)
                                    {
                                        tv_blank.setVisibility(View.VISIBLE);
                                    }

                                    if(newsList!=null&&newsList.size()>0){
                                        tv_blank.setVisibility(View.GONE);
                                    }

                                    ((MainActivity)context).hideMsg();



                                }
                            });

                            SharedPerenceUtil.putNews(context.getApplicationContext(),temp);

                        }
                    }.start();
                    page=1;

                    pullToRefreshListView.onPullDownRefreshComplete();
                    context.dissProgress();





                }

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Toast.makeText(context, R.string.check_net, Toast.LENGTH_SHORT).show();
                pullToRefreshListView.onPullDownRefreshComplete();
                context.dissProgress();

                if(newsList==null||newsList.size()==0)
                {
                    tv_blank.setVisibility(View.VISIBLE);
                }

                if(newsList!=null&&newsList.size()>0){
                    tv_blank.setVisibility(View.GONE);
                }

            }
        });
    }

    @Override
    public boolean needRefresh() {
        return false;
    }


    public void getNextData()
    {
        page+=1;
        RequestParams params=new RequestParams();
        params.add("page",page+"");
        ApiUtils.get(context,AppContants.NOTIFICATION_URL,params,new AsyncHttpResponseHandler() {
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
                Toast.makeText(context, R.string.check_net, Toast.LENGTH_SHORT).show();
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

                    }else{
                        view=convertView;


                    }

                    NewsHolder holder= (NewsHolder) view.getTag();
                    holder.tv_content.setVisibility(View.VISIBLE);
                    holder.tv_time.setText(TimeUtil.getDistanceTime(news.created_at));
                    holder.tv_project.setVisibility(View.VISIBLE);

                    //头像图片处理

                    ImageLoader.getInstance().displayImage(AppContants.DOMAIN + news.user.avatar.small.url, holder.iv_icon, feedOptions, animateFirstListener);


                    ViewGroup.LayoutParams params = holder.iv_icon.getLayoutParams();
                    int imageSize= DisplayUtil.dip2px(context, 40);
                    params.height=imageSize;
                    params.width =imageSize;
                    holder.iv_icon.setLayoutParams(params);

                    ContentHandler contentHandler=  new ContentHandler(holder.tv_content).longClickCopy();


                    switch (news.noti_type) {
                        //赞同

                        case "new_agreement":
                            String catagory="";
                            switch (news.data.notifiable_type){
                                case "idea":
                                    catagory="的想法";

                                   break;
                                case "attachment":
                                    catagory="的文件";
                                    break;
                                case "image":
                                    catagory="的图片";
                                    break;
                            }

                            holder.tv_content.setText(news.data.content);
                            holder.tv_project.setText(news.project.getTitle());
                            holder.tv_name.setText(news.user.username);
                            holder.tv_type.setText(getResources().getString(R.string.agreement_you)+catagory);

                            break;

                        case "invit_follow":
                            holder.tv_content.setVisibility(View.GONE);
                            holder.tv_project.setText(news.project.getTitle());
                            holder.tv_project.setText(news.project.getTitle());
                            holder.tv_name.setText(news.user.username);
                            holder.tv_type.setText(R.string.please_fllow);
                            break;
                        case "join_accept":
                            holder.tv_content.setVisibility(View.GONE);

                            String project;


                            holder.tv_project.setText(news.project.getTitle());
                            holder.tv_name.setText(news.user.username);
                            holder.tv_type.setText(R.string.join_accept);
                            break;
                        case "join_reject":

                            holder.tv_content.setVisibility(View.GONE);
                            holder.tv_project.setText(news.project.getTitle());
                            holder.tv_name.setText(news.user.username);
                            holder.tv_type.setText(R.string.join_reject);
                            break;
                        case "new_comment":
                            holder.tv_content.setVisibility(View.VISIBLE);
                            holder.tv_project.setText(news.project.getTitle());
                            holder.tv_name.setText(news.user.username);
                            holder.tv_type.setText(Html.fromHtml(getResources().getString(R.string.new_reply)));
                           // holder.tv_content.setText(news.data.content);
                            contentHandler.formatColorContent(holder.tv_content,news.data.content);
                            break;
                        case "new_entity":

                                project = news.project.getTitle();

                            switch (news.notifiable_type) {

                                case "Attachment":
                                    holder.tv_content.setVisibility(View.VISIBLE);

                                    holder.tv_project.setText(project);

                                    holder.tv_name.setText(news.user.username);
                                    holder.tv_type.setText(R.string.attachment);
                                   // holder.tv_content.setText(news.data.content);
                                    contentHandler.formatColorContent(holder.tv_content,news.data.content);

                                    break;
                                case "Homework":
                                    holder.tv_content.setVisibility(View.VISIBLE);
                                    holder.tv_project.setText(project);
                                    holder.tv_name.setText(news.user.username);
                                    holder.tv_type.setText(R.string.new_homework);
                                   // holder.tv_content.setText(news.data.content);
                                    contentHandler.formatColorContent(holder.tv_content,news.data.content);

                                    break;
                                case "Idea":
                                    holder.tv_content.setVisibility(View.VISIBLE);
                                    holder.tv_project.setText(project);
                                    holder.tv_name.setText(news.user.username);
                                    holder.tv_type.setText(R.string.new_idea);
                                  //  holder.tv_content.setText(news.data.content);
                                    contentHandler.formatColorContent(holder.tv_content,news.data.content);
                                    break;

                                case "Image":
                                    holder.tv_project.setText(project);
                                    holder.tv_content.setVisibility(View.VISIBLE);
                                    holder.tv_project.setVisibility(View.VISIBLE);
                                    holder.tv_name.setText(news.user.username);
                                    holder.tv_type.setText(R.string.new_image);
                                   // holder.tv_content.setText(news.data.content);
                                    contentHandler.formatColorContent(holder.tv_content,news.data.content);
                                    break;
                                case "Plan":
                                    holder.tv_content.setVisibility(View.VISIBLE);
                                    holder.tv_project.setVisibility(View.VISIBLE);
                                    holder.tv_name.setText(news.user.username);
                                    holder.tv_type.setText(R.string.new_plan);
                                    holder.tv_project.setText(project);
                                    if (news.data != null)
                                       // holder.tv_content.setText(news.data.content);
                                        contentHandler.formatColorContent(holder.tv_content,news.data.content);
                                    break;
                                case "Vote":
                                    holder.tv_content.setVisibility(View.VISIBLE);
                                    holder.tv_project.setText(news.project.getTitle());
                                    holder.tv_name.setText(news.user.username);
                                    holder.tv_type.setText(R.string.new_vote);
//                                    holder.tv_content.setText(news.data.content);
                                    contentHandler.formatColorContent(holder.tv_content,news.data.content);
                                    break;

                                case "Task":
                                    holder.tv_content.setVisibility(View.VISIBLE);
                                    holder.tv_project.setText(news.project.getTitle());
                                    holder.tv_name.setText(news.user.username);
                                    holder.tv_type.setText(R.string.new_task1);
//                                    holder.tv_content.setText(news.data.content);
                                    contentHandler.formatColorContent(holder.tv_content,news.data.content);
                                    break;


                            }


                            break;

                        case "UserRelation":
                            holder.tv_content.setVisibility(View.GONE);
                            holder.tv_project.setVisibility(View.GONE);
                            holder.tv_name.setText(news.user.username);
                            holder.tv_type.setText("关注了你");
                            break;

                        case "new_follow":
                            holder.tv_content.setVisibility(View.GONE);
                            holder.tv_project.setVisibility(View.GONE);
                            holder.tv_name.setText(news.user.username);
                            holder.tv_type.setText("关注了你");

                            break;

                        case "new_homework":
                            holder.tv_content.setVisibility(View.VISIBLE);
                            holder.tv_project.setText(news.project.getTitle());
                            holder.tv_name.setText(news.user.username);
                            holder.tv_type.setText(R.string.compele_task);
//                            holder.tv_content.setText(news.data.content);
                            contentHandler.formatColorContent(holder.tv_content,news.data.content);
                            break;
                        case "new_mention":

                            switch (news.notifiable_type) {
                                case "Attachment":
                                    holder.tv_project.setVisibility(View.GONE);
                                    holder.tv_content.setVisibility(View.VISIBLE);
                                    holder.tv_name.setText(news.user.username);
                                    holder.tv_type.setText(R.string.new_mention);
//                                    holder.tv_content.setText(news.data.content);
                                    contentHandler.formatColorContent(holder.tv_content,news.data.content);

                                    break;
                                case "Comment":
                                    holder.tv_project.setVisibility(View.GONE);

                                    holder.tv_content.setVisibility(View.VISIBLE);
                                    holder.tv_name.setText(news.user.username);
                                    holder.tv_type.setText(R.string.new_mention);
//                                    holder.tv_content.setText(news.data.content);
                                    contentHandler.formatColorContent(holder.tv_content,news.data.content);
                                    break;

                                case "Homework":
                                    holder.tv_project.setVisibility(View.GONE);

                                    holder.tv_content.setVisibility(View.VISIBLE);
                                    holder.tv_name.setText(news.user.username);
//                                    holder.tv_content.setText(news.data.content);
                                    holder.tv_type.setText(R.string.new_mention);
                                    contentHandler.formatColorContent(holder.tv_content,news.data.content);
                                    break;

                                case "Idea":
                                    holder.tv_project.setVisibility(View.GONE);

                                    holder.tv_content.setVisibility(View.VISIBLE);
                                    holder.tv_name.setText(news.user.username);
//                                    holder.tv_content.setText(news.data.content);
                                    holder.tv_type.setText(R.string.new_mention);
                                    contentHandler.formatColorContent(holder.tv_content,news.data.content);
                                    break;

                                case "Image":
                                    holder.tv_project.setVisibility(View.GONE);

                                    holder.tv_content.setVisibility(View.VISIBLE);
                                    holder.tv_name.setText(news.user.username);
//                                    holder.tv_content.setText(news.data.content);
                                    holder.tv_type.setText(R.string.new_mention);
                                    contentHandler.formatColorContent(holder.tv_content,news.data.content);
                                    break;

                                case "task":
                                    holder.tv_project.setVisibility(View.GONE);

                                    holder.tv_content.setVisibility(View.VISIBLE);
                                    holder.tv_name.setText(news.user.username);
//                                    holder.tv_content.setText(news.data.content);
                                    holder.tv_type.setText(R.string.new_mention);
                                    contentHandler.formatColorContent(holder.tv_content,news.data.content);
                                    break;

                            }
                            break;

                        case "new_reply":

                                project = news.project.getTitle();

                            holder.tv_project.setText(project);
                            holder.tv_content.setVisibility(View.VISIBLE);
                            holder.tv_name.setText(news.user.username);
                            holder.tv_type.setText(R.string.reply_you);
//                            holder.tv_content.setText(news.data.content);
                            contentHandler.formatColorContent(holder.tv_content,news.data.content);


                            break;

                        case "new_task":

                            holder.tv_project.setVisibility(View.VISIBLE);

                            holder.tv_content.setVisibility(View.VISIBLE);
                            holder.tv_name.setText(news.user.username);
                            holder.tv_type.setText(R.string.give_task);

//                            holder.tv_content.setText(news.data.content);
                            contentHandler.formatColorContent(holder.tv_content,news.data.content);
                            holder.tv_project.setText(news.project.getTitle());
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



                    if(holder.tv_type.VISIBLE==View.VISIBLE)
                    {
                        holder.tv_type.setOnClickListener(onClickListener);
                    }

                    if(holder.tv_content.VISIBLE==View.VISIBLE)
                    {
                        holder.tv_content.setOnClickListener(onClickListener);
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
    }

    @Override
    public void onResume() {
        super.onResume();
        context.setLable(getString(R.string.label_notifications));
        ((MainActivity)context).setCurrentFragment(this);
        ((MainActivity)context).hideMsg();
        ((MainActivity)context).hideReponse();

    }

    @Override
    public void onPause() {
        super.onPause();

    }
}

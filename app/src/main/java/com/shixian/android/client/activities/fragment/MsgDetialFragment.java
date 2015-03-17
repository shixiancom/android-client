package com.shixian.android.client.activities.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.shixian.android.client.Global;
import com.shixian.android.client.R;
import com.shixian.android.client.activities.BaseActivity;
import com.shixian.android.client.activities.SimpleSampleActivity;
import com.shixian.android.client.activities.fragment.base.AbsListViewBaseFragment;
import com.shixian.android.client.activities.fragment.base.BaseFeedFragment;
import com.shixian.android.client.contants.AppContants;
import com.shixian.android.client.controller.OnAllItemTypeClickController;
import com.shixian.android.client.controller.OnClickController;
import com.shixian.android.client.enter.EnterLayout;
import com.shixian.android.client.handler.content.ContentHandler;
import com.shixian.android.client.model.Comment;
import com.shixian.android.client.model.Feed2;
import com.shixian.android.client.model.News;
import com.shixian.android.client.model.feeddate.AllItemType;
import com.shixian.android.client.model.feeddate.BaseFeed;
import com.shixian.android.client.utils.ApiUtils;
import com.shixian.android.client.utils.CommonUtil;
import com.shixian.android.client.utils.DisplayUtil;
import com.shixian.android.client.utils.JsonUtils;
import com.shixian.android.client.utils.SharedPerenceUtil;
import com.shixian.android.client.utils.TimeUtil;
import com.shixian.android.client.views.pulltorefreshlist.PullToRefreshBase;
import com.shixian.android.client.views.pulltorefreshlist.PullToRefreshListView;
import org.apache.http.Header;
import java.util.List;
import com.shixian.android.client.activities.fragment.base.BaseFeedFragment.FeedHolder;

/**
 * Created by s0ng on 2015/2/10.
 */
public class MsgDetialFragment extends AbsListViewBaseFragment {

    private String TAG = "IndexFragment";





    private String firstPageDate;



    private MsgType msgType;


    private MsgFeedEntry feedEntry;

    private FeedAdapter adapter;


    private String lable;


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


        contentOptions = new DisplayImageOptions
                .Builder()
                .showImageOnLoading(R.drawable.default_iv)
                .showImageForEmptyUri(R.drawable.default_iv)
                .showImageOnFail(R.drawable.default_iv)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY)
                .build();


        commentOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.default_icon)
                .showImageForEmptyUri(R.drawable.default_icon)
                .showImageOnFail(R.drawable.default_icon)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(4))
                .build();

    }



    @Override
    public View initView(LayoutInflater inflater) {

        if(!TextUtils.isEmpty(lable)){
            initLable();
        }

        View view =inflater.inflate(R.layout.fragment_msgdetial,null,false);

        pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.lv_index);

        lv=pullToRefreshListView.getListView();
        commonEnterRoot=context.findViewById(R.id.commonEnterRoot);

        settingListView(pullToRefreshListView.getListView());

        pullToRefreshListView.getListView().setDividerHeight(0);




//        commonEnterRoot=context.findViewById(R.id.commonEnterRoot);
//
//        settingListView(lv);


        // 滚动到底自动加载可用
        pullToRefreshListView.setScrollLoadEnabled(true);


        pullToRefreshListView.getFooterLoadingLayout().show(false);


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
                pullToRefreshListView.onPullUpRefreshComplete();



            }
        });

        return view;
    }




//    @Override
//    public View initView(LayoutInflater inflater) {
//
//        View view = inflater.inflate(R.layout.fragment_index, null, false);
//
//        context.setLable("首页");
//
//        pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.lv_index);
//        // 滚动到底自动加载可用
//        pullToRefreshListView.setScrollLoadEnabled(true);
//
//
//        // 设置下拉刷新的listener
//        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
//
//            //下拉舒心完成
//            @Override
//            public void onPullDownToRefresh(
//                    PullToRefreshBase<ListView> refreshView) {
//
//                //上啦刷新
//                Log.i("AAAA","1111-------------------------------------------------------------------");
//                initDate(null);
//            }
//
//            @Override
//            public void onPullUpToRefresh(
//                    PullToRefreshBase<ListView> refreshView) {
//                //getNewsList(moreUrl, false);
//                //下拉加载更多
//                Log.i("AAAA", "-------------------------------------------------------------------");
//                getNextData();
//
//
//
//            }
//        });
//
//        feedList = new ArrayList<BaseFeed>();
//
//
//        initCacheData();
//
//        return view;
//    }
//
//
    protected void initCacheData() {

        firstPageDate= SharedPerenceUtil.getIndexFeed(context);
        feedEntry = JsonUtils.parseAllItemType(firstPageDate, msgType);
        if (adapter == null) {
            adapter = new FeedAdapter();
            pullToRefreshListView.getListView().setAdapter(adapter);
            pullToRefreshListView.onPullUpRefreshComplete();

        } else {
            adapter.notifyDataSetChanged();
        }

    }


    @Override
    public void initDate(Bundle savedInstanceState) {



        if(feedEntry!=null&&feedEntry.firstEntry!=null)
        {
            if (adapter == null) {
                adapter = new FeedAdapter();
                pullToRefreshListView.getRefreshableView().setAdapter(adapter);
            } else {
                pullToRefreshListView.getRefreshableView().setAdapter(adapter);


            }

            if (currentFirstPos <= feedEntry.baseFeeds.size())
                pullToRefreshListView.getListView().setSelection(currentFirstPos);



        }else{

            initFirst();
        }
    }

    @Override
    public void setCurrentPosition(int position) {
        pullToRefreshListView.getListView().setSelection(position);
    }

    /*********************************************获取数据*******************************/
    /**
     * 初始化第一页数据
     */
    public void initFirstData()
    {

        context.showProgress();



       ApiUtils.get(msgType.url, null, new AsyncHttpResponseHandler() {
           @Override
           public void onSuccess(int i, Header[] headers, final byte[] bytes) {
               final String temp = new String(bytes);
               if (!AppContants.errorMsg.equals(temp)) {


                   new Thread() {
                       public void run() {
                           //获取第一页数据
                           firstPageDate = temp;
                           //数据格式
                           CommonUtil.logDebug(TAG, new String(bytes));


                           MsgFeedEntry ms = JsonUtils.parseAllItemType(firstPageDate, msgType);

                           feedEntry.baseFeeds = ms.baseFeeds;
                           feedEntry.firstEntry = ms.firstEntry;

                           //TODO
                           //  SharedPerenceUtil.putIndexFeed(context,firstPageDate);


                           //保存数据到本地

                           context.runOnUiThread(new Runnable() {
                               @Override
                               public void run() {

                                   if (adapter == null) {
                                       adapter = new FeedAdapter();


                                       pullToRefreshListView.getRefreshableView().setAdapter(adapter);
                                   } else {
                                       adapter.notifyDataSetChanged();
                                   }

                                   pullToRefreshListView.onPullDownRefreshComplete();
                                   pullToRefreshListView.onPullUpRefreshComplete();
                                   context.dissProgress();
                                   pullToRefreshListView.getListView().setSelection(msgType.position);

                               }
                           });


                       }
                   }.start();


               }else{
                   pullToRefreshListView.onPullDownRefreshComplete();
                   pullToRefreshListView.onPullUpRefreshComplete();
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








    protected void initFirst() {

        feedEntry=new MsgFeedEntry();

        //TODO
       // initCacheData();

        initNews();

        initLable();

        initFirstData();

    }

    private void initNews() {


        News news= (News) getArguments().get("news");

        msgType=new MsgType(news);


    }

    /******************************************************************************************/

    protected void initLable() {
        if(TextUtils.isEmpty(lable)){
            context.setLable(msgType.notifiable_type);
            lable=msgType.notifiable_type;
        }else{
            context.setLable(lable);
        }

    }


    /************************************Adapter**********************************************/

    /**
     * adapter对象
     */
    class FeedAdapter extends BaseAdapter {

        protected ImageLoadingListener animateFirstListener = new BaseFeedFragment.AnimateFirstDisplayListener();

        @Override
        public int getCount() {

            if(feedEntry.baseFeeds==null)
                return 1;
            return 1+feedEntry.baseFeeds.size();
        }

        @Override
        public Object getItem(int position) {
            if(position==1)
            {
                return feedEntry.firstEntry;
            }
            return feedEntry.baseFeeds.get(position-1);
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

           View view=null;
           final BaseFeedFragment.FeedHolder holder;

            if(convertView==null)
            {
                view=View.inflate(context,R.layout.feed_common_item,null);
                holder=new BaseFeedFragment.FeedHolder();
                holder.iv_icon= (ImageView) view.findViewById(R.id.iv_icon);
                holder.tv_name= (TextView) view.findViewById(R.id.tv_name);
                holder.tv_proect= (TextView) view.findViewById(R.id.tv_project);
                holder.tv_time= (TextView) view.findViewById(R.id.tv_time);;
                holder.tv_content= (TextView) view.findViewById(R.id.tv_content);;
                holder.iv_content=(ImageView) view.findViewById(R.id.iv_content);;
                holder.tv_response= (TextView) view.findViewById(R.id.tv_response);
                holder.tv_type= (TextView) view.findViewById( R.id.tv_type);
                holder.v_line=view.findViewById(R.id.v_line);
                holder.ll_body= (LinearLayout) view.findViewById(R.id.ll_body);

                view.setTag(holder);

            }else{
                view=convertView;
                holder= (FeedHolder) view.getTag();
            }


            new ContentHandler(holder.tv_content).longClickCopy();

            if(position==0) {

                AllItemType allItemType=feedEntry.firstEntry;







                if(allItemType!=null)
                {


                    ImageLoader.getInstance().displayImage(AppContants.DOMAIN + allItemType.user.avatar.small.url, holder.iv_icon, feedOptions, animateFirstListener);


                    String type="";
                    String project=project = allItemType.project.title;


                    String switchOpt;

                    if(msgType.isComment)
                    {
                        switchOpt=msgType.commentable_type;

                    }else{
                        switchOpt=msgType.notifiable_type;
                    }

                    switch (switchOpt) {
                        case "Idea":
                            type = context.getResources().getString(R.string.feed_add_idea);
                            holder.tv_content.setText(allItemType.content);
                            break;
                        case "Project":
                            type = context.getResources().getString(R.string.feed_add_project);
                            project = allItemType.project.title;
                            holder.tv_content.setText(Html.fromHtml(allItemType.description));
                            //隐藏回复框
                            holder.tv_response.setVisibility(View.GONE);
                            break;
                        case "Plan":
                            type = context.getResources().getString(R.string.feed_add_plan);

                            holder.tv_content.setText(allItemType.content + "   "+getString(R.string.feed_end)+": " + allItemType.finish_on);
                            break;
                        case "Image":

                            type = context.getResources().getString(R.string.feed_add_image);
                            holder.tv_content.setText(allItemType.content);
                            holder.iv_content.setVisibility(View.VISIBLE);


                            ImageLoader.getInstance().displayImage(AppContants.DOMAIN + allItemType.attachment.thumb.url, holder.iv_content, contentOptions, animateFirstListener);

                            ivContentOnClickListener(holder,allItemType.attachment.url);


                            break;
                        case "UserProjectRelation":
                            type = context.getResources().getString(R.string.feed_join_project);
                            //隐藏回复框
                            if (TextUtils.isEmpty(allItemType.comments_count)) {
                                holder.tv_response.setVisibility(View.GONE);
                                // holder.tv_content.setVisibility(View.INVISIBLE);
                            } else {
                                holder.tv_content.setVisibility(View.GONE);
                            }
                            break;
                        case "Homework":
                            type = context.getResources().getString(R.string.feed_completed_task);
                            holder.tv_content.setText(Html.fromHtml(allItemType.content_html));
                            break;
                        case "Task":
                            type = context.getResources().getString(R.string.feed_add_task);
                            holder.tv_content.setText(allItemType.content);
                            break;
                        case "Vote":
                            type = context.getResources().getString(R.string.feed_join_vote);

                            holder.tv_content.setText(allItemType.content);
                            break;
                        case "Attachment":


                            type = context.getResources().getString(R.string.feed_add_file);
                            holder.tv_content.setText(allItemType.content+"\n"+"  "+allItemType.file_name);
                            holder.iv_content.setVisibility(View.VISIBLE);
                            holder.iv_content.setImageResource(R.drawable.file);
                            break;
                    }

                    holder.tv_type.setText(type);


                        holder.tv_proect.setText(project);


                    holder.tv_name.setText(feedEntry.firstEntry.user.username);


                    //设置样式
//                int textSize=DisplayUtil.sp2px(context,13);
                    holder.tv_name.setTextSize(13);
                    holder.tv_time.setTextSize(11);
                    holder.tv_content.setTextSize(15);

                    ViewGroup.LayoutParams params = holder.iv_icon.getLayoutParams();
                    int imageSize= DisplayUtil.dip2px(context, 40);
                    params.height=imageSize;
                    params.width =imageSize;
                    holder.iv_icon.setLayoutParams(params);

                    LinearLayout.LayoutParams lp= (LinearLayout.LayoutParams) holder.ll_body.getLayoutParams();

                    //设置内容与顶部拒领14dp 内容与底部之间12dp
                    lp.setMargins(0,DisplayUtil.dip2px(context, 14),0,DisplayUtil.dip2px(context, 12));

                    holder.tv_type.setVisibility(View.VISIBLE);
                    holder.tv_proect.setVisibility(View.VISIBLE);
                    holder.tv_response.setVisibility(View.GONE);

                    setAllItemCommonClickListener(context,holder,allItemType);




                }


            }else{


                Comment comment= (Comment) feedEntry.baseFeeds.get(position-1);
                comment.position=position-1;
                //初始化一些common信息
                holder.tv_name.setText(comment.user.username);
                holder.tv_time.setText(TimeUtil.getDistanceTime(comment.created_at));
                holder.tv_proect.setVisibility(View.GONE);
                holder.tv_type.setVisibility(View.GONE);
                holder.iv_content.setVisibility(View.GONE);
                holder.tv_content.setVisibility(View.VISIBLE);
                holder.tv_content.setText(comment.content);



                holder.tv_name.setTextSize(13);
                holder.tv_time.setTextSize(11);
                holder.tv_content.setTextSize(14);

                ViewGroup.LayoutParams params = holder.iv_icon.getLayoutParams();
                int imageSize=DisplayUtil.dip2px(context,20);
                params.height=imageSize;
                params.width =imageSize;
                holder.iv_icon.setLayoutParams(params);
                LinearLayout.LayoutParams lp= (LinearLayout.LayoutParams) holder.ll_body.getLayoutParams();
                lp.setMargins(0, DisplayUtil.dip2px(context, 5), 0, DisplayUtil.dip2px(context, 10));


                ImageLoader.getInstance().displayImage(AppContants.DOMAIN + comment.user.avatar.small.url, holder.iv_icon, commentOptions, animateFirstListener);


                //隐藏回复框
                holder.tv_response.setVisibility(View.GONE);

                if(!comment.isLast) {

                    holder.v_line.setVisibility(View.GONE);
                }

                if(msgType.isComment)
                {
                    if(comment.id.equals(msgType.notifiable_id))
                    {
                        view.setBackgroundResource(R.color.select_comment_color);

                    }
                }


                //设置点击事件
                setFeedOnClickListener(context,holder,comment);

            }





            return view;

        }
    }

    private void ivContentOnClickListener(final FeedHolder feedHolder,final String url) {

        feedHolder.iv_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SimpleSampleActivity.class);
                intent.putExtra("key", (String) feedHolder.iv_content.getTag());
                intent.putExtra("url", url);


                context.startActivity(intent);
            }
        });
    }

//   protected void initFeedItemView(FeedHolder holder,BaseFeed baseFeed,int position) {
//
//        baseFeed.position=position;
//
//        String type="";
//        String project="";
//
//
//        //开始switch
//        holder.tv_response.setVisibility(View.VISIBLE);
//        holder.v_line.setVisibility(View.VISIBLE);
//        holder.iv_content.setVisibility(View.GONE);
//
//        //用户名和头像是同一设置的
//
//
//        //Feed2类型的
//        if(!baseFeed.feedable_type.equals(AppContants.FEADE_TYPE_COMMON))
//        {
//            holder.tv_content.setVisibility(View.VISIBLE);
//
//            Feed2 feed= (Feed2) baseFeed;
//
//            //设置project
//            if(feed.data.project!=null&&!TextUtils.isEmpty(feed.data.project.title))
//                project=feed.data.project.title;
//            switch (feed.feedable_type) {
//                case "Idea":
//                    type = context.getResources().getString(R.string.add_idea);
//                    holder.tv_content.setText(feed.data.content);
//                    break;
//                case "Project":
//                    type = context.getResources().getString(R.string.add_project);
//                    project = feed.data.title;
//                    holder.tv_content.setText(Html.fromHtml(feed.data.description));
//                    //隐藏回复框
//                    holder.tv_response.setVisibility(View.GONE);
//                    break;
//                case "Plan":
//                    type = context.getResources().getString(R.string.add_plan);
//
//                    holder.tv_content.setText(feed.data.content + "   截至到: " + feed.data.finish_on);
//                    break;
//                case "Image":
//
//                    type = context.getResources().getString(R.string.add_image);
//                    holder.tv_content.setText(Html.fromHtml(feed.data.content_html));
//
//                    String keys[]=feed.data.attachment.url.split("/");
//                    String key=keys[keys.length-1];
//
//                    holder.iv_content.setTag(key);
//                    holder.iv_content.setVisibility(View.VISIBLE);
//                    ImageUtil.loadingImage(holder.iv_content, BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher),callback,key,AppContants.DOMAIN+feed.data.attachment.url);
//
//                    break;
//                case "UserProjectRelation":
//                    type = context.getResources().getString(R.string.join);
//                    //隐藏回复框
//                    if(feed.hasChildren) {
//                        holder.tv_response.setVisibility(View.GONE);
//                        // holder.tv_content.setVisibility(View.INVISIBLE);
//                    }else{
//                        holder.tv_content.setVisibility(View.GONE);
//                    }
//                    break;
//                case "Homework":
//                    type = context.getResources().getString(R.string.finish_homework);
//                    holder.tv_content.setText(Html.fromHtml(feed.data.content_html));
//                    break;
//                case "Task":
//                    type = context.getResources().getString(R.string.finish_task);
//                    holder.tv_content.setText(Html.fromHtml(feed.data.content_html));
//                    break;
//                case "Vote":
//                    type = context.getResources().getString(R.string.finish_task);
//
//                    holder.tv_content.setText(Html.fromHtml(feed.data.content_html));
//                    break;
//                case "Attachment":
//                    type = context.getResources().getString(R.string.feed_attachment);
//                    holder.tv_content.setText(Html.fromHtml(feed.data.content_html));
//                    break;
//            }
//
//            if(feed.hasChildren) {
//                holder.v_line.setVisibility(View.GONE);
//
//            }else{
//                holder.tv_response.setVisibility(View.VISIBLE);
//            }
//
//
//            //头像图片处理
//            String keys[]=feed.data.user.avatar.small.url.split("/");
//            String key=keys[keys.length-1];
//
////                ImageUtil.loadingImage(holder.iv_icon, BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher),callback,key,AppContants.DOMAIN+feed.data.user.avatar.small.url);
//
//            Bitmap bm = ImageCache.getInstance().get(key);
//
//            if (bm != null) {
//                holder.iv_icon.setImageBitmap(bm);
//            } else {
//                holder.iv_icon.setImageResource(R.drawable.ic_launcher);
//                holder.iv_icon.setTag(key);
//                if (callback != null) {
//                    new ImageDownload(callback).execute(AppContants.DOMAIN+feed.data.user.avatar.small.url, key, ImageDownload.CACHE_TYPE_LRU);
//                }
//            }
//
//
//            holder.tv_type.setText(type);
//            holder.tv_proect.setText(project);
//            holder.tv_name.setText(feed.data.user.username);
//
//            //设置样式
////                int textSize=DisplayUtil.sp2px(context,13);
//            holder.tv_name.setTextSize(13);
//            holder.tv_time.setTextSize(11);
//            holder.tv_content.setTextSize(15);
//
//            ViewGroup.LayoutParams params = holder.iv_icon.getLayoutParams();
//            int imageSize=DisplayUtil.dip2px(context,40);
//            params.height=imageSize;
//            params.width =imageSize;
//            holder.iv_icon.setLayoutParams(params);
//
//            holder.tv_type.setVisibility(View.VISIBLE);
//            holder.tv_proect.setVisibility(View.VISIBLE);
//
//        }else{
//            Comment comment= (Comment) baseFeed;
//            //初始化一些common信息
//            holder.tv_name.setText(comment.user.username);
//            holder.tv_time.setText(TimeUtil.getDistanceTime(comment.created_at));
//            holder.tv_proect.setVisibility(View.GONE);
//            holder.tv_type.setVisibility(View.GONE);
//            holder.iv_content.setVisibility(View.GONE);
//            holder.tv_content.setVisibility(View.VISIBLE);
//            holder.tv_content.setText(comment.content);
//
//
//
//            holder.tv_name.setTextSize(13);
//            holder.tv_time.setTextSize(11);
//            holder.tv_content.setTextSize(14);
//
//            ViewGroup.LayoutParams params = holder.iv_icon.getLayoutParams();
//            int imageSize=DisplayUtil.dip2px(context,20);
//            params.height=imageSize;
//            params.width =imageSize;
//            holder.iv_icon.setLayoutParams(params);
//
//
//            //头像图片处理
//            String keys[]=comment.user.avatar.small.url.split("/");
//            String key=keys[keys.length-1];
//
//            Bitmap bm = ImageCache.getInstance().get(key);
//
//            if (bm != null) {
//                holder.iv_icon.setImageBitmap(bm);
//            } else {
//                holder.iv_icon.setImageResource(R.drawable.ic_launcher);
//                holder.iv_icon.setTag(position+key);
//                if (callback != null) {
//                    new ImageDownload(callback).execute(AppContants.DOMAIN+comment.user.avatar.small.url, key, ImageDownload.CACHE_TYPE_LRU);
//                }
//            }
//
//
//            //隐藏回复框
//            if(!comment.isLast) {
//                holder.tv_response.setVisibility(View.GONE);
//                holder.v_line.setVisibility(View.GONE);
//            }
//
//        }
//
//    }



    protected void setFeedOnClickListener(final BaseActivity context, final FeedHolder holder,final BaseFeed baseFeed) {

        OnClickController controller=new OnClickController(context,baseFeed);
        holder.iv_icon.setOnClickListener(controller);
        holder.tv_name.setOnClickListener(controller);
        //项目
        holder.tv_proect.setOnClickListener(controller);



        if(holder.iv_content.getVisibility()==View.VISIBLE)
        {
            if(baseFeed instanceof Feed2 && ("Attachment".equals(((Feed2)baseFeed).feedable_type)))
            {
                holder.iv_content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, R.string.cant_downlowb, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }


        if(holder.tv_content.getVisibility()==View.VISIBLE)
        {
            holder.tv_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popComment(v,baseFeed,pullToRefreshListView.getListView(),0);
                }
            });

        }
    }


    protected void setAllItemCommonClickListener(final BaseActivity context, final FeedHolder holder,final AllItemType allItemType) {

        OnAllItemTypeClickController controller=new OnAllItemTypeClickController(context,allItemType);
        holder.iv_icon.setOnClickListener(controller);
        holder.tv_name.setOnClickListener(controller);
        //项目
        holder.tv_proect.setOnClickListener(controller);



        if(holder.iv_content.getVisibility()==View.VISIBLE)
        {
            if( "Attachment".equals(msgType.notifiable_type)||"Attachment".equals(msgType.commentable_type)){
                holder.iv_content.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "暂且不支持下载文件", Toast.LENGTH_SHORT).show();
                    }
                });

            }

        }


        if(holder.tv_content.getVisibility()==View.VISIBLE)
        {
            holder.tv_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popComment(v,allItemType,pullToRefreshListView.getListView(),0);
                }
            });

        }
    }



    /**************************************** 回复框相关***************************/
    /*************************************用于管理回复框 一级软键盘弹出 到这listView 变小  要滚动listView 这里网上资料很少 研究了差不多一天多***
     * 并且考虑到集成回复表情，但是考虑到论坛的性质 将回复表情功能砍掉*******************************************************************/
    protected ListView lv;
    protected int oldListHigh = 0;
    protected int needScrollY = 0;
    protected int cal1 = 0;
    protected View commonEnterRoot;
    protected EnterLayout mEnterLayout;

    //发送回复的监听发送事件
    //发送回复的监听发送事件
    protected  View.OnClickListener onClickSendText = new View.OnClickListener() {


        boolean sendAble=true;
        @Override
        public void onClick(View v) {


            if(sendAble)
            {
                sendAble=false;
            }else{
                Toast.makeText(context,"不要重复点击发送",Toast.LENGTH_SHORT).show();
                return;
            }

            String input = mEnterLayout.getContent();
            if (input.isEmpty()) {
                return;
            }

            //发送
            final Object obj= mEnterLayout.getTag();
            String type;
            String id;
            if (obj instanceof Comment) {
                type = ((Comment) obj).commentable_type.toLowerCase();

                id=((Comment) obj).commentable_id;
            } else {

                AllItemType allItemType= (AllItemType) obj;

                String notifyType;
                if(msgType.isComment)
                {
                    notifyType=msgType.commentable_type;
                    id=msgType.commentable_id;


                }else{
                    notifyType=msgType.notifiable_type;
                    id=msgType.notifiable_id;
                }


                if("UserProjectRelation".equals(notifyType))
                    type = "user_project_relation";
                else
                    type = notifyType.toLowerCase();


            }
            String url=String.format(AppContants.COMMENT_URL,type+"s".toLowerCase(),id);

            RequestParams params=new RequestParams();
            params.put("comment[content]",input);

            ApiUtils.post(url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {

                    Object  object =  mEnterLayout.getTag();
                    Gson gson = new Gson();
                    Comment comment = gson.fromJson(new String(bytes), Comment.class);
                    comment.feedable_type=AppContants.FEADE_TYPE_COMMON;
                    if (object instanceof Comment) {
                        comment.parent_id = ((Comment) object).parent_id;
                        comment.project_id = ((Comment) object).project_id;
                        ((Comment)object).isLast=false;

                    } else {
                        comment.parent_id = feedEntry.firstEntry.id;
                        comment.project_id = feedEntry.firstEntry.project.id;

                    }
                    feedEntry.baseFeeds.add(feedEntry.baseFeeds.size(), comment);
                    comment.isLast=true;


                    adapter.notifyDataSetChanged();
                    mEnterLayout.clearContent();
                    hideSoftkeyboard();
                    Toast.makeText(context, R.string.send_ok, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                    hideSoftkeyboard();
                    Toast.makeText(context, R.string.send_failed, Toast.LENGTH_SHORT).show();
                }
            });

        }
    };


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



    protected  void popComment(View v,Object tag,ListView lv,int type) {
        EditText comment = mEnterLayout.content;

        String data = (String) v.getTag();
        String response;
//        showEnterLayout(tag);

        if(tag instanceof Comment)
        {

            if(TextUtils.isEmpty(comment.getText()))
            {
                comment.setText("@"+ ((Comment)tag).user.username+" ");
            }
        }

        mEnterLayout.show(tag);
        comment.setSelection(comment.getText().length());





//            mEnterLayout.restoreLoad(commentObject);

        int itemLocation[] = new int[2];
        v.getLocationOnScreen(itemLocation);
        int itemHeight = v.getHeight();

        int listLocation[] = new int[2];
        lv.getLocationOnScreen(listLocation);
        int listHeight = lv.getHeight();

        oldListHigh = listHeight;

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



    public class MsgType{

        public boolean isComment;
        public String commentable_id;
        public String commentable_type;
        public String notifiable_type;
        public String notifiable_id;


        public int position =0;

        public String url;




        public MsgType(News news)
        {
            String type="";

            this.notifiable_type=news.notifiable_type;
            this.notifiable_id=news.notifiable_id;




            url=String.format(AppContants.MSG_RESOPNSE_URL,notifiable_type.toLowerCase()+"s",notifiable_id);
            if("UserProjectRelation".equals(notifiable_type))
            {
                url=String.format(AppContants.MSG_RESOPNSE_URL,"user_project_relations",notifiable_id);
            }
            if("Comment".equals(news.notifiable_type))
            {
                isComment=true;
                commentable_id=news.data.commentable_id;
                commentable_type=news.data.commentable_type;
                url=String.format(AppContants.MSG_RESOPNSE_URL,commentable_type.toLowerCase()+"s",commentable_id);
                if("UserProjectRelation".equals(commentable_type))
                {
                    url=String.format(AppContants.MSG_RESOPNSE_URL,"user_project_relations",commentable_id);
                }
            }




        }
    }


     public static class MsgFeedEntry{
         public List<BaseFeed> baseFeeds;
         public AllItemType firstEntry;
     }

}

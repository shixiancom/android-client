package com.shixian.android.client.activities.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.shixian.android.client.activities.base.BaseActivity;
import com.shixian.android.client.activities.SimpleSampleActivity;
import com.shixian.android.client.activities.base.BaseFeedActivity;
import com.shixian.android.client.activities.fragment.base.AbsListViewBaseFragment;
import com.shixian.android.client.activities.fragment.base.BaseFeedFragment;
import com.shixian.android.client.contants.AppContants;
import com.shixian.android.client.controller.ArgeeOnClickController;
import com.shixian.android.client.controller.IndexOnClickController;
import com.shixian.android.client.controller.OnAllItemTypeClickController;
import com.shixian.android.client.enter.EnterLayout;
import com.shixian.android.client.handler.content.ContentHandler;
import com.shixian.android.client.handler.feed.BaseFeedHandler;
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
 * 不要乱动这个类 否则后果自负
 */
public class MsgDetialFragment extends AbsListViewBaseFragment {

    private String TAG = "IndexFragment";

    private String firstPageDate;

    private MsgType msgType;


    private MsgFeedEntry feedEntry;

    private FeedAdapter adapter;


    private String lable;

    private TextView mCommentText;
    private BaseFeed mComment;


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
            }


        }else{

            initFirst();
        }
    }



    /*********************************************获取数据*******************************/
    /**
     * 初始化第一页数据
     */
    public void initFirstData()
    {

        context.showProgress();



        ApiUtils.get(context,msgType.url, null, new AsyncHttpResponseHandler() {
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



                Toast.makeText(context, getString(R.string.check_net), Toast.LENGTH_SHORT).show();
                pullToRefreshListView.onPullDownRefreshComplete();
                context.dissProgress();
            }
        });
    }

    @Override
    public boolean needRefresh() {
        return false;
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
    private static final int TYPE_ALLTYPE=0;
    private static final int TYPE_COMMENT=1;

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
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            if(position==0)
                return TYPE_ALLTYPE;
            return TYPE_COMMENT;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view=null;


            switch (getItemViewType(position))
            {


                case TYPE_ALLTYPE:
                    BaseFeedFragment.FeedHolder holder;
//                    if(convertView==null)
//                    {
//                        view=View.inflate(context,R.layout.feed_common_item,null);
//                        holder=new BaseFeedFragment.FeedHolder();
//                        holder.iv_icon= (ImageView) view.findViewById(R.id.iv_icon);
//                        holder.tv_name= (TextView) view.findViewById(R.id.tv_name);
//                        holder.tv_proect= (TextView) view.findViewById(R.id.tv_project);
//                        holder.tv_time= (TextView) view.findViewById(R.id.tv_time);;
//                        holder.tv_content= (TextView) view.findViewById(R.id.tv_content);;
//                        holder.iv_content=(ImageView) view.findViewById(R.id.iv_content);;
//                        holder.tv_response= (TextView) view.findViewById(R.id.tv_response);
//                        holder.tv_type= (TextView) view.findViewById( R.id.tv_type);
//                        holder.v_line=view.findViewById(R.id.v_line);
//                        holder.ll_body= (LinearLayout) view.findViewById(R.id.ll_body);
//                        holder.rl_agree= (android.widget.RelativeLayout) view.findViewById(R.id.rl_agree);
//                        holder.tv_agree= (TextView) view.findViewById(R.id.tv_agree);
//                        holder.tv_agreecount= (TextView) view.findViewById(R.id.tv_agreecount);
//
//                        view.setTag(holder);
//
//
//
//                    }else{
//                        view=convertView;
//                        holder= (FeedHolder) view.getTag();
//                    }
//
//
//                    view.setBackgroundColor(Color.WHITE);
//
//
//                    ContentHandler contentHandler=new ContentHandler(holder.tv_content).longClickCopy();
//
//
//
//                        AllItemType allItemType=feedEntry.firstEntry;
//
//                        if(allItemType!=null) {
//
//
//
//
//                            String type = "";
//                            String project = project = allItemType.project.title;
//
//
//                            String switchOpt;
//
//                            if (msgType.isComment) {
//                                switchOpt = msgType.commentable_type;
//
//                            } else {
//                                switchOpt = msgType.notifiable_type;
//                            }
//

//
//                            holder.tv_type.setText(type);
//
//
//                            holder.tv_proect.setText(project);
//
//
//                            holder.tv_name.setText(feedEntry.firstEntry.user.username);
//                            ImageLoader.getInstance().displayImage(AppContants.DOMAIN + allItemType.user.avatar.small.url, holder.iv_icon, feedOptions, animateFirstListener);
//
//
//
//
//                            //设置样式
//
//
//                            holder.tv_type.setVisibility(View.VISIBLE);
//                            holder.tv_proect.setVisibility(View.VISIBLE);
//                            holder.tv_response.setVisibility(View.GONE);
//
//                            setAllItemCommonClickListener(context, holder, allItemType);
//
//
//                        }
//



                    view= BaseFeedHandler.initFeedItemView2(context,convertView);

                    holder= (FeedHolder) view.getTag();


                    view.setBackgroundColor(Color.WHITE);


                    ContentHandler contentHandler= new ContentHandler(holder.tv_content).longClickCopy();

                    holder.rl_agree.setVisibility(View.GONE);
                    if(position==0) {

                        AllItemType allItemType = feedEntry.firstEntry;


                        if (allItemType != null) {


                            ImageLoader.getInstance().displayImage(AppContants.DOMAIN + allItemType.user.avatar.small.url, holder.iv_icon, feedOptions, animateFirstListener);


                            String type = "";
                            String project = project = allItemType.project.title;




                            String switchOpt;

                            if (msgType.isComment) {
                                switchOpt = msgType.commentable_type;

                            } else {
                                switchOpt = msgType.notifiable_type;
                            }



                            switch (switchOpt) {
                                case "Idea":
                                    type = context.getResources().getString(R.string.feed_add_idea);
                                    //  holder.tv_content.setText(allItemType.content);
                                    contentHandler.formatColorContent(holder.tv_content, allItemType.content);
                                    holder.rl_agree.setVisibility(View.VISIBLE);
                                    break;
                                case "Project":
                                    type = context.getResources().getString(R.string.feed_add_project);
                                    project = allItemType.project.title;
                                    // holder.tv_content.setText(Html.fromHtml(allItemType.description));
                                    contentHandler.formatColorContent(holder.tv_content, allItemType.content);
                                    //隐藏回复框
                                    holder.tv_response.setVisibility(View.GONE);
                                  //  holder.rl_agree.setVisibility(View.GONE);
                                    break;
                                case "Plan":
                                    type = context.getResources().getString(R.string.feed_add_plan);

                                    //  holder.tv_content.setText(allItemType.content + "   "+getString(R.string.feed_end)+": " + allItemType.finish_on);
                                    contentHandler.formatColorContent(holder.tv_content, allItemType.content + "   " + getString(R.string.feed_end) + ": " + allItemType.finish_on);
                                    break;
                                case "Image":

                                    type = context.getResources().getString(R.string.feed_add_image);
                                    //  holder.tv_content.setText(allItemType.content);

                                    contentHandler.formatColorContent(holder.tv_content, allItemType.content);
                                    holder.iv_content.setVisibility(View.VISIBLE);


                                    ImageLoader.getInstance().displayImage(AppContants.DOMAIN + allItemType.attachment.thumb.url, holder.iv_content, contentOptions, animateFirstListener);

                                    ivContentOnClickListener(holder, allItemType.attachment.url);
                                    holder.rl_agree.setVisibility(View.VISIBLE);

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
                                    //   holder.tv_content.setText(Html.fromHtml(allItemType.content_html));
                                    contentHandler.formatColorContent(holder.tv_content, allItemType.content);
                                    break;
                                case "Task":
                                    type = context.getResources().getString(R.string.feed_add_task);
                                    //  holder.tv_content.setText(allItemType.content);
                                    contentHandler.formatColorContent(holder.tv_content, allItemType.content);
                                    break;
                                case "Vote":
                                    type = context.getResources().getString(R.string.feed_join_vote);

                                    // holder.tv_content.setText(allItemType.content);
                                    contentHandler.formatColorContent(holder.tv_content, allItemType.content);
                                    break;
                                case "Attachment":


                                    type = context.getResources().getString(R.string.feed_add_file);
                                    contentHandler.formatColorContent(holder.tv_content, allItemType.content + "\n" + "  " + allItemType.file_name);
                                    // holder.tv_content.setText(allItemType.content+"\n"+"  "+allItemType.file_name);
                                    holder.iv_content.setVisibility(View.VISIBLE);
                                    holder.iv_content.setImageResource(R.drawable.file);
                                    holder.rl_agree.setVisibility(View.VISIBLE);
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
                            int imageSize = DisplayUtil.dip2px(context, 40);
                            params.height = imageSize;
                            params.width = imageSize;
                            holder.iv_icon.setLayoutParams(params);

                            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) holder.ll_body.getLayoutParams();

                            //设置内容与顶部拒领14dp 内容与底部之间12dp
                            lp.setMargins(0, DisplayUtil.dip2px(context, 14), 0, DisplayUtil.dip2px(context, 12));

                            holder.tv_type.setVisibility(View.VISIBLE);
                            holder.tv_proect.setVisibility(View.VISIBLE);
                            holder.tv_response.setVisibility(View.GONE);

                            setAllItemCommonClickListener(context, holder, allItemType);

                            setFeedCommonClick(switchOpt,allItemType.id,holder);


                        }
                    }

                        break;

                case TYPE_COMMENT:
                    BaseFeedFragment.CommentHolder commentHolder=null;
                    if(convertView==null)
                    {
                        view=View.inflate(context,R.layout.comment_common_item,null);
                        commentHolder=new BaseFeedFragment.CommentHolder();
                        commentHolder.iv_icon= (ImageView) view.findViewById(R.id.iv_icon);
                        commentHolder.tv_content= (TextView) view.findViewById(R.id.tv_content);

                        commentHolder.tv_name= (TextView) view.findViewById(R.id.tv_name);
                        commentHolder.tv_response= (TextView) view.findViewById(R.id.tv_response);
                        commentHolder.tv_time= (TextView) view.findViewById(R.id.tv_time);
                        view.setTag(commentHolder);

                    }else{
                        view=convertView;
                        commentHolder= (BaseFeedFragment.CommentHolder) view.getTag();
                    }


                    view.setBackgroundColor(Color.WHITE);


                    final Comment comment= (Comment) feedEntry.baseFeeds.get(position-1);
                    comment.position=position-1;
                    //初始化一些common信息
                    commentHolder.tv_name.setText(comment.user.username);
                    commentHolder.tv_time.setText(TimeUtil.getDistanceTime(comment.created_at));
                    commentHolder.tv_content.setVisibility(View.VISIBLE);
                    // holder.tv_content.setText(comment.content);

                    contentHandler=new ContentHandler(commentHolder.tv_content).longClickCopy();

                    contentHandler.formatColorContent(commentHolder.tv_content,comment.content);

                    ImageLoader.getInstance().displayImage(AppContants.DOMAIN + comment.user.avatar.small.url, commentHolder.iv_icon, commentOptions, animateFirstListener);


                    //隐藏回复框
                    commentHolder.tv_response.setVisibility(View.GONE);



                    if(msgType.isComment)
                    {
                        if(comment.id.equals(msgType.notifiable_id))
                        {
                            mCommentText=commentHolder.tv_content;

                            view.setBackgroundResource(R.color.select_comment_color);

                            if(comment!=null)
                            {
                                mEnterLayout.content.setHint("@"+comment.user.username);

                                final BaseFeedFragment.CommentHolder finalCommentHolder = commentHolder;
                                mEnterLayout.content.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                    @Override
                                    public void onFocusChange(View v, boolean hasFocus) {
                                        if(hasFocus)
                                        {

                                            popComment(finalCommentHolder.tv_content,comment,pullToRefreshListView.getListView(),0);

                                        }
                                    }
                                });

                            }


                        }
                    }


                    //设置点击事件
                    setFeedOnClickListener(context,commentHolder,comment);

                    break;





            }

            return view;









        }
    }

    public void setFeedCommonClick(String type,String id,BaseFeedFragment.FeedHolder feedHolder)
    {
        if(feedHolder.rl_agree.getVisibility()==View.VISIBLE)
        {
            feedHolder.iv_agree.setOnClickListener(new ArgeeOnClickController(context,true,type, id,feedHolder.tv_agreecount));
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

    protected void setFeedOnClickListener(final BaseActivity context, final BaseFeedFragment.CommentHolder holder,final BaseFeed baseFeed) {

        IndexOnClickController controller=new IndexOnClickController(context,baseFeed);
        holder.iv_icon.setOnClickListener(controller);
        holder.tv_name.setOnClickListener(controller);




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
                sendAble=true;
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

            ApiUtils.post(context,url, params, new AsyncHttpResponseHandler() {
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
                    sendAble=true;
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                    sendAble=true;
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
                //说明键盘出来了
                if(oldListHigh-listHeight>100)
                {
                    int scrollResult = needScrollY + oldListHigh - listHeight;
                    lv.smoothScrollBy(scrollResult, 1);

                }
            }
        });

    }



    protected  void popComment(View v,Object tag,ListView lv,int type) {
        mEnterLayout.content.setOnFocusChangeListener(null);
        EditText comment = mEnterLayout.content;

        String data = (String) v.getTag();
        String response;
//        showEnterLayout(tag);

        if(tag instanceof Comment)
        {

            if(TextUtils.isEmpty(comment.getText())|| !comment.getText().toString().contains("@"+ ((Comment)tag).user.username+" "))
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

    @Override
    public void onResume() {
        super.onResume();
        mEnterLayout.show(null);

    }


}

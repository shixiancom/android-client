package com.shixian.android.client.activities.fragment.base;

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
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.shixian.android.client.Global;
import com.shixian.android.client.R;
import com.shixian.android.client.activities.SimpleSampleActivity;
import com.shixian.android.client.contants.AppContants;
import com.shixian.android.client.enter.EnterLayout;
import com.shixian.android.client.model.Comment;
import com.shixian.android.client.model.Feed2;
import com.shixian.android.client.model.feeddate.BaseFeed;
import com.shixian.android.client.utils.ApiUtils;
import com.shixian.android.client.utils.DisplayUtil;
import com.shixian.android.client.utils.StringUtils;
import com.shixian.android.client.utils.TimeUtil;
import com.shixian.android.client.views.PersonItemLinearLayout;
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
public abstract  class BaseFeedFragment extends AbsListViewBaseFragment {

    protected int page = 1;

    protected String firstPageDate;
    protected List<BaseFeed> feedList;
    protected BaseAdapter adapter;
    protected int currentFirstPos=0;

    protected AbsListView listView;




    //图片加载有关＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
    //大头像
    protected DisplayImageOptions feedOptions;
    //内容
    protected DisplayImageOptions contentOptions;
    //小头像
    protected DisplayImageOptions commentOptions;




    /*************************************用于管理回复框 一级软键盘弹出 到这listView 变小  要滚动listView 这里网上资料很少 研究了差不多一天多***
     * 并且考虑到集成回复表情，但是考虑到论坛的性质 将回复表情功能砍掉*******************************************************************/

    protected int oldListHigh = 0;
    protected int needScrollY = 0;
    protected int cal1 = 0;
    protected View commonEnterRoot;
    protected EnterLayout mEnterLayout;

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
            BaseFeed baseFeed= (BaseFeed) mEnterLayout.getTag();
            String type;
            String id;
            if (AppContants.FEADE_TYPE_COMMON.equals(baseFeed.feedable_type)) {
                type = ((Comment) baseFeed).commentable_type;

                id=((Comment) baseFeed).commentable_id;
            } else {


                type = baseFeed.feedable_type;
                id=baseFeed.feedable_id;
            }

            if("UserProjectRelation".equals(type))
                type = "user_project_relation";
            String url=String.format(AppContants.COMMENT_URL,(type+"s").toLowerCase(),id);

            RequestParams params=new RequestParams();
            params.put("comment[content]",input);



            ApiUtils.post(url, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {


                    BaseFeed baseFeed = (BaseFeed) mEnterLayout.getTag();
                    Gson gson = new Gson();

                    //新生成的comment
                    Comment comment = gson.fromJson(new String(bytes), Comment.class);
                    comment.feedable_type=AppContants.FEADE_TYPE_COMMON;

                    //点击的
                    if (baseFeed instanceof Comment) {
                        comment.parent_id = ((Comment) baseFeed).parent_id;
                        comment.project_id = ((Comment) baseFeed).project_id;
                        ((Comment)baseFeed).isLast=false;

                        if (((Comment)baseFeed).parent.hasChildren) {
                            ( (Comment)feedList.get(((Comment)baseFeed).parent.lastChildPosition+((Comment) baseFeed).parent.position)).isLast = false;
                        }
                        ((Comment)baseFeed).parent.hasChildren=true;
                        int position=++((Comment)baseFeed).parent.lastChildPosition;
                        feedList.add(position+((Comment)baseFeed).parent.position, comment);
                        comment.parent=((Comment)baseFeed).parent;
                    } else {
                        if(comment!=null) {
                            comment.parent_id = baseFeed.id;
                            comment.project_id = ((Feed2) baseFeed).project_id;

                            if (((Feed2) baseFeed).hasChildren) {
                                ((Comment) feedList.get(((Feed2) baseFeed).lastChildPosition+((Feed2)baseFeed).position)).isLast = false;
                            }
                            ((Feed2) baseFeed).hasChildren = true;
                            ((Feed2) baseFeed).lastChildPosition++;
                            feedList.add(((Feed2) baseFeed).lastChildPosition+((Feed2) baseFeed).position, comment);
                            comment.parent = (Feed2) baseFeed;
                        }
                    }


                    comment.isLast=true;


                    adapter.notifyDataSetChanged();
                    mEnterLayout.clearContent();
                    hideSoftkeyboard();
                    sendAble=true;

                    Toast.makeText(context,R.string.send_ok,Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                    hideSoftkeyboard();
                    Toast.makeText(context, R.string.send_failed, Toast.LENGTH_SHORT).show();
                    sendAble=true;
                }
            });

        }
    };


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
                .displayer(new RoundedBitmapDisplayer(10))
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
                .displayer(new RoundedBitmapDisplayer(10))
                .build();

    }

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



        this.listView=pullToRefreshListView.getListView();


        commonEnterRoot=context.findViewById(R.id.commonEnterRoot);

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

        if(feedList==null)
            feedList = new ArrayList<BaseFeed>();



        return view;
    }

    protected abstract void initFirst();

    protected abstract void initLable();


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

    protected void settingListView(final AbsListView listView)
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
     * @param type 如果type为0
     */
    protected  void popComment(View v,Object tag,AbsListView lv,int type) {
        EditText comment = mEnterLayout.content;

        String data = (String) v.getTag();
        String response;
//        showEnterLayout(tag);


        //设置@啊
        if(tag instanceof Comment)
        {

            Log.i("AAAA","isComment-------------------------------");
            if(TextUtils.isEmpty(comment.getText()))
            {
                comment.setText("@"+ ((Comment)tag).user.username+" ");
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


    public void showEnterLayout(Object tag)
    {
        mEnterLayout.show(tag);

    }

    @Override
    public void onDestroyView() {
        currentFirstPos=pullToRefreshListView.getListView().getFirstVisiblePosition();
        super.onDestroyView();

    }

    protected View initHolderAndItemView(View convertView) {
        View view=null;
        FeedHolder holder=null;
        if(convertView==null||convertView instanceof PersonItemLinearLayout)
        {
            view=getLayoutInflater(null).inflate(R.layout.feed_common_item,null,true);
           // view =View.inflate(context,R.layout.feed_common_item,null);
            holder=new FeedHolder();
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
            //holder= (FeedHolder) view.getTag();
        }

        return view;
    }





    /************************************Adapter的一些函数****************/
    protected View initFeedItemView2(View convertView) {


        View view;
        if (convertView == null) {
            view= getLayoutInflater(null).inflate(R.layout.feed_common_item, null, true);
            // view =View.inflate(context,R.layout.feed_common_item,null);
            FeedHolder  feedHolder = new FeedHolder();
            feedHolder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
            feedHolder.tv_name = (TextView) view.findViewById(R.id.tv_name);
            feedHolder.tv_proect = (TextView) view.findViewById(R.id.tv_project);
            feedHolder.tv_time = (TextView) view.findViewById(R.id.tv_time);

            feedHolder.tv_content = (TextView) view.findViewById(R.id.tv_content);

            feedHolder.iv_content = (ImageView) view.findViewById(R.id.iv_content);

            feedHolder.tv_response = (TextView) view.findViewById(R.id.tv_response);
            feedHolder.tv_type = (TextView) view.findViewById(R.id.tv_type);
            feedHolder.v_line = view.findViewById(R.id.v_line);
            feedHolder.ll_body = (LinearLayout) view.findViewById(R.id.ll_body);

            view.setTag(feedHolder);

        } else {
            view = convertView;



        }

        return  view;
    }



    protected void initFeedItemViewData(Feed2 feed,FeedHolder feedHolder,ImageLoadingListener animateFirstListener) {
        String type = "";
        String project = "";


        //开始switch
        feedHolder.tv_response.setVisibility(View.VISIBLE);
        feedHolder.v_line.setVisibility(View.VISIBLE);
        feedHolder.iv_content.setVisibility(View.GONE);

        feedHolder.tv_content.setVisibility(View.VISIBLE);


        //设置project
        if (feed.data.project != null && !TextUtils.isEmpty(feed.data.project.title))
            project = feed.data.project.title;
        switch (feed.feedable_type) {
            case "Idea":
                type = context.getResources().getString(R.string.add_idea);
                feedHolder.tv_content.setText(feed.data.content.trim());
                break;
            case "Project":
                type = context.getResources().getString(R.string.add_project);
                project = feed.data.title;
                feedHolder.tv_content.setText(Html.fromHtml(feed.data.description));
                //隐藏回复框
                feedHolder.tv_response.setVisibility(View.GONE);
                break;
            case "Plan":
                type = context.getResources().getString(R.string.feed_add_plan);

                feedHolder.tv_content.setText(feed.data.content + "   "+getString(R.string.feed_end)+": " + feed.data.finish_on);
                break;
            case "Image":

                type = context.getResources().getString(R.string.feed_add_image);
                feedHolder.tv_content.setText(Html.fromHtml(feed.data.content_html));

                feedHolder.iv_content.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().displayImage(AppContants.DOMAIN + feed.data.attachment.thumb.url, feedHolder.iv_content, contentOptions, animateFirstListener);


                ivContentOnClickListener(feedHolder,feed.data.attachment.url);

                break;
            case "UserProjectRelation":
                type = context.getResources().getString(R.string.feed_join_project);
                //隐藏回复框
                if(feed.hasChildren) {
                    feedHolder.tv_response.setVisibility(View.GONE);

                }
                feedHolder.tv_content.setVisibility(View.GONE);
                break;
            case "Homework":
                type = context.getResources().getString(R.string.feed_completed_task);
                feedHolder.tv_content.setText(feed.data.content);
                break;
            case "Task":
                type = context.getResources().getString(R.string.feed_add_task);
                feedHolder.tv_content.setText(feed.data.content);
                break;
            case "Vote":
                type = context.getResources().getString(R.string.feed_join_vote);

                feedHolder.tv_content.setText(feed.data.content);
                break;
            case "Attachment":
                type = context.getResources().getString(R.string.feed_add_file);
                feedHolder.tv_content.setText(feed.data.content+"\n"+"  "+feed.data.file_name);
                feedHolder.iv_content.setVisibility(View.VISIBLE);
                feedHolder.iv_content.setImageResource(R.drawable.file);
                break;

            case "Agreement":
//                    type="加入项目";
//                    holder.tv_content.setVisibility(View.GONE);
                break;

        }

        if (feed.hasChildren) {

            feedHolder.tv_response.setVisibility(View.GONE);

        } else {
            feedHolder.v_line.setVisibility(View.GONE);
        }



        ImageLoader.getInstance().displayImage(AppContants.DOMAIN + feed.data.user.avatar.small.url, feedHolder.iv_icon, feedOptions, animateFirstListener);


        feedHolder.tv_type.setText(type);
        feedHolder.tv_proect.setText(project);
        feedHolder.tv_name.setText(feed.data.user.username);
        feedHolder.tv_time.setText(TimeUtil.getDistanceTime(feed.created_at));

        //设置样式
//                int textSize=DisplayUtil.sp2px(context,13);
        feedHolder.tv_name.setTextSize(13);
        feedHolder.tv_time.setTextSize(11);
        feedHolder.tv_content.setTextSize(15);

        ViewGroup.LayoutParams params = feedHolder.iv_icon.getLayoutParams();
        int imageSize = DisplayUtil.dip2px(context, 40);
        params.height = imageSize;
        params.width = imageSize;
        feedHolder.iv_icon.setLayoutParams(params);

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) feedHolder.ll_body.getLayoutParams();

        //设置内容与顶部拒领14dp 内容与底部之间12dp
        lp.setMargins(0, DisplayUtil.dip2px(context, 14), 0, DisplayUtil.dip2px(context, 12));

        //设置8dp 为头像留白
        //    ( (RelativeLayout.LayoutParams)holder.ll_name_type.getLayoutParams()).setMargins(0,0,0,0);

//            RelativeLayout.LayoutParams layoutParams= (RelativeLayout.LayoutParams) holder.rl_title.getLayoutParams();
//            layoutParams.setMargins(0,0,0,0);

//


        feedHolder.tv_type.setVisibility(View.VISIBLE);
        feedHolder.tv_proect.setVisibility(View.VISIBLE);

        if ("Homework".equals(feed.feedable_type) || "Project".equals(feed.feedable_type) || "Agreement".equals(feed.feedable_type)) {
            feedHolder.tv_response.setVisibility(View.GONE);
        }

        if(TextUtils.isEmpty(feedHolder.tv_content.getText()))
        {
            feedHolder.tv_content.setVisibility(View.GONE);
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


    protected View initCommentItem(View convertView) {

        View view;
        if (convertView == null) {
            view = getLayoutInflater(null).inflate(R.layout.comment_common_item, null, true);
            // view =View.inflate(context,R.layout.feed_common_item,null);
            CommentHolder commentHolder = new CommentHolder();
            commentHolder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
            commentHolder.tv_name = (TextView) view.findViewById(R.id.tv_name);
            commentHolder.tv_time = (TextView) view.findViewById(R.id.tv_time);

            commentHolder.tv_content = (TextView) view.findViewById(R.id.tv_content);

            commentHolder.tv_response = (TextView) view.findViewById(R.id.tv_response);
//            commentHolder.v_line = view.findViewById(R.id.v_line);
            commentHolder.ll_body = (LinearLayout) view.findViewById(R.id.ll_body);

            view.setTag(commentHolder);

        } else {
            view = convertView;

        }

        return view;
    }



    protected void initCommentItemData(Comment comment,CommentHolder commentHolder,ImageLoadingListener animateFirstListener) {
        //开始switch
        commentHolder.tv_response.setVisibility(View.VISIBLE);
//        commentHolder.v_line.setVisibility(View.VISIBLE);



        //初始化一些common信息
        commentHolder.tv_name.setText(comment.user.username);
        commentHolder.tv_time.setText(TimeUtil.getDistanceTime(comment.created_at));
        commentHolder.tv_content.setVisibility(View.VISIBLE);
        commentHolder.tv_content.setText(Html.fromHtml(StringUtils.trmDiv(comment.content_html)));


//        commentHolder.tv_name.setTextSize(13);
//        commentHolder.tv_time.setTextSize(11);
//        commentHolder.tv_content.setTextSize(14);

//        ViewGroup.LayoutParams params2 = commentHolder.iv_icon.getLayoutParams();
//        int imageSize2 = DisplayUtil.dip2px(context, 20);
//        params2.height = imageSize2;
//        params2.width = imageSize2;
//        commentHolder.iv_icon.setLayoutParams(params2);
//        LinearLayout.LayoutParams lp2 = (LinearLayout.LayoutParams) commentHolder.ll_body.getLayoutParams();
//        lp2.setMargins(0, DisplayUtil.dip2px(context, 5), 0, DisplayUtil.dip2px(context, 10));


        //头像图片处理
        ImageLoader.getInstance().displayImage(AppContants.DOMAIN + comment.user.avatar.small.url, commentHolder.iv_icon, commentOptions, animateFirstListener);



        //隐藏回复框
        if (!comment.isLast) {
            commentHolder.tv_response.setVisibility(View.GONE);
//            commentHolder.v_line.setVisibility(View.GONE);
        }
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


    protected  abstract  class BaseFeedAdapter extends BaseAdapter{

        protected ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    }


    public   void setCurrentPosition(int position){};




}

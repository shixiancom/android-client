package com.shixian.android.client.activities.fragment.base;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.shixian.android.client.Global;
import com.shixian.android.client.R;
import com.shixian.android.client.activities.BaseActivity;
import com.shixian.android.client.contants.AppContants;
import com.shixian.android.client.enter.EnterLayout;
import com.shixian.android.client.model.Comment;
import com.shixian.android.client.model.Feed2;
import com.shixian.android.client.model.feeddate.BaseFeed;
import com.shixian.android.client.utils.ApiUtils;
import com.shixian.android.client.utils.DisplayUtil;
import com.shixian.android.client.utils.ImageCache;
import com.shixian.android.client.utils.ImageCallback;
import com.shixian.android.client.utils.ImageDownload;
import com.shixian.android.client.utils.ImageUtil;
import com.shixian.android.client.utils.StringUtils;
import com.shixian.android.client.utils.TimeUtil;
import com.shixian.android.client.views.PersonItemLinearLayout;
import com.shixian.android.client.views.pulltorefreshlist.PullToRefreshBase;
import com.shixian.android.client.views.pulltorefreshlist.PullToRefreshListView;

import org.apache.http.Header;
import org.w3c.dom.Text;

import java.lang.annotation.Target;
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
    protected BaseAdapter adapter;
    protected int currentFirstPos=0;



    /*************************************用于管理回复框 一级软键盘弹出 到这listView 变小  要滚动listView 这里网上资料很少 研究了差不多一天多***
     * 并且考虑到集成回复表情，但是考虑到论坛的性质 将回复表情功能砍掉*******************************************************************/
    protected ListView lv;
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

            Log.e("AAAA",url);

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
                            ( (Comment)feedList.get(((Comment)baseFeed).parent.lastChildPosition)).isLast = false;
                        }
                        ((Comment)baseFeed).parent.hasChildren=true;
                        int position=++((Comment)baseFeed).parent.lastChildPosition;
                        feedList.add(position, comment);
                        comment.parent=((Comment)baseFeed).parent;
                    } else {
                        if(comment!=null) {
                            comment.parent_id = baseFeed.id;
                            comment.project_id = ((Feed2) baseFeed).project_id;

                            if (((Feed2) baseFeed).hasChildren) {
                                ((Comment) feedList.get(((Feed2) baseFeed).lastChildPosition)).isLast = false;
                            }
                            ((Feed2) baseFeed).hasChildren = true;
                            ((Feed2) baseFeed).lastChildPosition++;
                            feedList.add(((Feed2) baseFeed).lastChildPosition, comment);
                            comment.parent = (Feed2) baseFeed;
                        }
                    }


                    comment.isLast=true;


                    adapter.notifyDataSetChanged();
                    mEnterLayout.clearContent();
                    hideSoftkeyboard();
                    sendAble=true;
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    Log.d("AAAA", new String(bytes));
                    hideSoftkeyboard();
                    Toast.makeText(context, R.string.check_net, Toast.LENGTH_SHORT).show();
                    sendAble=true;
                }
            });

        }
    };



    @Override
    public View initView(LayoutInflater inflater) {

        View view = inflater.inflate(R.layout.fragment_index, null, false);

        initLable();

        pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.lv_index);
        pullToRefreshListView.getListView().setDividerHeight(0);


        this.lv=pullToRefreshListView.getListView();
        commonEnterRoot=context.findViewById(R.id.commonEnterRoot);

        settingListView(lv);


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

    protected abstract  void setFeedOnClickListener(BaseActivity context,FeedHolder holder,final BaseFeed baseFeed);


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


    /**
     *  点击文本框事件
     * @param v
     * @param tag
     * @param lv
     */
   protected  void popComment(View v,Object tag,ListView lv) {
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
               comment.setText("@"+ ((Comment)tag).user.username);
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
        needScrollY = (itemLocation[1] + commonEnterRoot.getHeight()-itemHeight-4) - (listLocation[1] + listHeight);


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


    /**
     * 初始化Feed的Item 由于三个页面都一样所以再这里进行抽取
     *
     */
    protected void initFeedItemView(FeedHolder holder,BaseFeed baseFeed,int position) {


        String type="";
        String project="";


        //开始switch
        holder.tv_response.setVisibility(View.VISIBLE);
        holder.v_line.setVisibility(View.VISIBLE);
        holder.iv_content.setVisibility(View.GONE);

        //用户名和头像是同一设置的


        //Feed2类型的
        if(!baseFeed.feedable_type.equals(AppContants.FEADE_TYPE_COMMON))
        {
            holder.tv_content.setVisibility(View.VISIBLE);

            Feed2 feed= (Feed2) baseFeed;

            //设置project
            if(feed.data.project!=null&&!TextUtils.isEmpty(feed.data.project.title))
                project=feed.data.project.title;
            switch (feed.feedable_type) {
                case "Idea":
                    type = context.getResources().getString(R.string.add_idea);
                    holder.tv_content.setText(feed.data.content);
                    break;
                case "Project":
                    type = context.getResources().getString(R.string.add_project);
                    project = feed.data.title;
                    holder.tv_content.setText(Html.fromHtml(feed.data.description));
                    //隐藏回复框
                    holder.tv_response.setVisibility(View.GONE);
                    break;
                case "Plan":
                    type = context.getResources().getString(R.string.add_plan);

                    holder.tv_content.setText(feed.data.content + "   截至到: " + feed.data.finish_on);
                    break;
                case "Image":

                    type = context.getResources().getString(R.string.add_image);
                    holder.tv_content.setText(Html.fromHtml(feed.data.content_html));

                    String keys[]=feed.data.attachment.url.split("/");
                    String key=keys[keys.length-1];

                    holder.iv_content.setTag(key);
                    holder.iv_content.setVisibility(View.VISIBLE);
                    ImageUtil.loadingImage(holder.iv_content, BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher), callback, key, AppContants.DOMAIN + feed.data.attachment.url);

                    break;
                case "UserProjectRelation":
                    type = context.getResources().getString(R.string.join);
                    //隐藏回复框
                    if(feed.hasChildren) {
                        holder.tv_response.setVisibility(View.GONE);

                    }
                    holder.tv_content.setVisibility(View.GONE);
                    break;
                case "Homework":
                    type = context.getResources().getString(R.string.finish_homework);
                    holder.tv_content.setText(feed.data.content);
                    break;
                case "Task":
                    type = context.getResources().getString(R.string.finish_task);
                    holder.tv_content.setText(feed.data.content);
                    break;
                case "Vote":
                    type = context.getResources().getString(R.string.finish_task);

                    holder.tv_content.setText(feed.data.content);
                    break;
                case "Attachment":
                    type = context.getResources().getString(R.string.feed_attachment);
                    holder.tv_content.setText(feed.data.content+"\n"+"  "+feed.data.file_name);
                    holder.iv_content.setVisibility(View.VISIBLE);
                    holder.iv_content.setImageResource(R.drawable.file);
                    break;

                case "Agreement":
                    type="加入项目";
                    holder.tv_content.setVisibility(View.GONE);

            }

            if(feed.hasChildren) {

                holder.tv_response.setVisibility(View.GONE);

            }else{
                holder.v_line.setVisibility(View.GONE);
            }


            //头像图片处理
            String keys[]=feed.data.user.avatar.small.url.split("/");
            String key=keys[keys.length-1];

//                ImageUtil.loadingImage(holder.iv_icon, BitmapFactory.decodeResource(getResources(),R.drawable.ic_launcher),callback,key,AppContants.DOMAIN+feed.data.user.avatar.small.url);

            Bitmap bm = ImageCache.getInstance().get(key);

            if (bm != null) {
                holder.iv_icon.setImageBitmap(bm);
            } else {
                holder.iv_icon.setImageResource(R.drawable.ic_launcher);
                holder.iv_icon.setTag(key);
                if (callback != null) {
                    new ImageDownload(callback).execute(AppContants.DOMAIN+feed.data.user.avatar.small.url, key, ImageDownload.CACHE_TYPE_LRU);
                }
            }


            holder.tv_type.setText(type);
            holder.tv_proect.setText(project);
            holder.tv_name.setText(feed.data.user.username);
            holder.tv_time.setText(TimeUtil.getDistanceTime(feed.created_at));

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

            //设置8dp 为头像留白
        //    ( (RelativeLayout.LayoutParams)holder.ll_name_type.getLayoutParams()).setMargins(0,0,0,0);

//            RelativeLayout.LayoutParams layoutParams= (RelativeLayout.LayoutParams) holder.rl_title.getLayoutParams();
//            layoutParams.setMargins(0,0,0,0);

//





            holder.tv_type.setVisibility(View.VISIBLE);
            holder.tv_proect.setVisibility(View.VISIBLE);

            if("Homework".equals(baseFeed.feedable_type)||"Project".equals(baseFeed.feedable_type)||"Agreement".equals(baseFeed.feedable_type))
            {
                holder.tv_response.setVisibility(View.GONE);
            }



        }else{
            Comment comment= (Comment) baseFeed;
            //初始化一些common信息
            holder.tv_name.setText(comment.user.username);
            holder.tv_time.setText(TimeUtil.getDistanceTime(comment.created_at));
            holder.tv_proect.setVisibility(View.GONE);
            holder.tv_type.setVisibility(View.GONE);
            holder.iv_content.setVisibility(View.GONE);
            holder.tv_content.setVisibility(View.VISIBLE);
            holder.tv_content.setText(Html.fromHtml(StringUtils.trmDiv(comment.content_html)));




            holder.tv_name.setTextSize(13);
            holder.tv_time.setTextSize(11);
            holder.tv_content.setTextSize(14);

            ViewGroup.LayoutParams params = holder.iv_icon.getLayoutParams();
            int imageSize=DisplayUtil.dip2px(context,20);
            params.height=imageSize;
            params.width =imageSize;
            holder.iv_icon.setLayoutParams(params);
            LinearLayout.LayoutParams lp= (LinearLayout.LayoutParams) holder.ll_body.getLayoutParams();
            lp.setMargins(0,DisplayUtil.dip2px(context, 5),0,DisplayUtil.dip2px(context, 10));

            //设置5dp为头像留白
//            ( (LinearLayout.LayoutParams)holder.ll_name_type.getLayoutParams()).setMargins(DisplayUtil.dip2px(context,5),0,0,0);


//            android.widget.AbsListView.LayoutParams layoutParams= (android.widget.AbsListView.LayoutParams) holder.rl_title.getLayoutParams();
//            if(comment.isFirst)
//            {
//
//                layoutParams.setMargins(0,DisplayUtil.dip2px(context,12),0,0);
//            }else{
//                layoutParams.setMargins(0,0,0,0);
//            }




            //头像图片处理
            String keys[]=comment.user.avatar.small.url.split("/");
            String key=keys[keys.length-1];

            Bitmap bm = ImageCache.getInstance().get(key);

            if (bm != null) {
                holder.iv_icon.setImageBitmap(bm);
            } else {
                holder.iv_icon.setImageResource(R.drawable.ic_launcher);
                holder.iv_icon.setTag(position+key);
                if (callback != null) {
                    new ImageDownload(callback).execute(AppContants.DOMAIN+comment.user.avatar.small.url, key, ImageDownload.CACHE_TYPE_LRU);
                }
            }


            //隐藏回复框
            if(!comment.isLast) {
                holder.tv_response.setVisibility(View.GONE);
                holder.v_line.setVisibility(View.GONE);
            }



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
}

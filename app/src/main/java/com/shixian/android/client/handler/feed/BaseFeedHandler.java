package com.shixian.android.client.handler.feed;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.shixian.android.client.Global;
import com.shixian.android.client.R;
import com.shixian.android.client.activities.SimpleSampleActivity;
import com.shixian.android.client.activities.fragment.base.BaseFeedFragment;
import com.shixian.android.client.contants.AppContants;
import com.shixian.android.client.controller.ArgeeOnClickController;
import com.shixian.android.client.handler.content.ContentHandler;
import com.shixian.android.client.model.Comment;
import com.shixian.android.client.model.Feed2;
import com.shixian.android.client.utils.DisplayUtil;
import com.shixian.android.client.utils.TimeUtil;

/**
 * Created by tangtang on 15/4/3.
 */
public class BaseFeedHandler {


    public  static DisplayImageOptions contentOptions = new DisplayImageOptions
            .Builder()
            .showImageOnLoading(R.drawable.default_iv)
    .showImageForEmptyUri(R.drawable.default_iv)
    .showImageOnFail(R.drawable.default_iv)
    .cacheInMemory(true)
    .cacheOnDisk(true)
    .considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565)
    .imageScaleType(ImageScaleType.EXACTLY)
    .build();

    public  static DisplayImageOptions feedOptions = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.default_icon)
    .showImageForEmptyUri(R.drawable.default_icon)
    .showImageOnFail(R.drawable.default_icon)
    .cacheInMemory(true)
    .cacheOnDisk(true)
    .considerExifParams(true)
    .displayer(new RoundedBitmapDisplayer(5))
            .build();

    public  static DisplayImageOptions commentOptions = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.default_icon)
    .showImageForEmptyUri(R.drawable.default_icon)
    .showImageOnFail(R.drawable.default_icon)
    .cacheInMemory(true)
    .cacheOnDisk(true)
    .considerExifParams(true)
    .displayer(new RoundedBitmapDisplayer(4))
            .build();


    public static void initFeedItemViewData(Context context, Feed2 feed,BaseFeedFragment.FeedHolder feedHolder,ImageLoadingListener animateFirstListener) {
        String type = "";
        String project = "";


        //开始switch
        feedHolder.tv_response.setVisibility(View.VISIBLE);
        feedHolder.v_line.setVisibility(View.VISIBLE);
        feedHolder.iv_content.setVisibility(View.GONE);

        feedHolder.tv_content.setVisibility(View.VISIBLE);
        feedHolder.rl_agree.setVisibility(View.GONE);

        ContentHandler contentHandler=new ContentHandler(feedHolder.tv_content).longClickCopy();



        //设置project
        if (feed.data.project != null && !TextUtils.isEmpty(feed.data.project.title))
            project = feed.data.project.title;
        switch (feed.feedable_type) {
            case "Idea":
                type = context.getResources().getString(R.string.add_idea);
                // feedHolder.tv_content.setText(feed.data.content.trim());
                contentHandler.formatColorContent(feedHolder.tv_content,feed.data.content);
                feedHolder.rl_agree.setVisibility(View.VISIBLE);

                feedHolder.tv_agreecount.setText(feed.agreement_count);

                break;
            case "Project":
                type = context.getResources().getString(R.string.add_project);
                project = feed.data.title;
                contentHandler.formatColorContent(feedHolder.tv_content,feed.data.description);
                // feedHolder.tv_content.setText(Html.fromHtml(feed.data.description));
                //隐藏回复框
                feedHolder.tv_response.setVisibility(View.GONE);

                break;
            case "Plan":
                type = context.getResources().getString(R.string.feed_add_plan);

                contentHandler.formatColorContent(feedHolder.tv_content,feed.data.content + "   "+context.getString(R.string.feed_end)+": " + feed.data.finish_on);
                // feedHolder.tv_content.setText(feed.data.content + "   "+getString(R.string.feed_end)+": " + feed.data.finish_on);
                break;
            case "Image":

                type = context.getResources().getString(R.string.feed_add_image);
                // feedHolder.tv_content.setText(feed.data.content);
                contentHandler.formatColorContent(feedHolder.tv_content,feed.data.content);
                feedHolder.iv_content.setVisibility(View.VISIBLE);
                ImageLoader.getInstance().displayImage(AppContants.DOMAIN + feed.data.attachment.thumb.url, feedHolder.iv_content, contentOptions, animateFirstListener);
                feedHolder.rl_agree.setVisibility(View.VISIBLE);

                ivContentOnClickListener(context,feedHolder,feed.data.attachment.url);

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
                //  feedHolder.tv_content.setText(feed.data.content);
                contentHandler.formatColorContent(feedHolder.tv_content,feed.data.content);

                break;
            case "Task":
                type = context.getResources().getString(R.string.feed_add_task);
                // feedHolder.tv_content.setText(feed.data.content);
                contentHandler.formatColorContent(feedHolder.tv_content,feed.data.content);
                break;
            case "Vote":
                type = context.getResources().getString(R.string.feed_join_vote);

                // feedHolder.tv_content.setText(feed.data.content);
                contentHandler.formatColorContent(feedHolder.tv_content,feed.data.content);
                break;
            case "Attachment":
                type = context.getResources().getString(R.string.feed_add_file);
                feedHolder.tv_content.setText(feed.data.content+"\n"+"  "+feed.data.file_name);
                feedHolder.iv_content.setVisibility(View.VISIBLE);
                feedHolder.iv_content.setImageResource(R.drawable.file);
                feedHolder.rl_agree.setVisibility(View.VISIBLE);
                break;

            case "Agreement":
                feedHolder.rl_agree.setVisibility(View.VISIBLE);
                switch (feed.data.feedable_type){
                    case "idea":
                        type="赞同了想法";
                        contentHandler.formatColorContent(feedHolder.tv_content,feed.data.content);
                        feedHolder.tv_agreecount.setText(feed.agreement_count);
                        break;

                    case "attachment":
                        type="赞同了文件";
                        feedHolder.tv_content.setText(feed.data.content+"\n"+"  "+feed.data.file_name);
                        feedHolder.iv_content.setVisibility(View.VISIBLE);
                        feedHolder.iv_content.setImageResource(R.drawable.file);

                        break;
                    case "image":
                        type="赞同了图片";
                        contentHandler.formatColorContent(feedHolder.tv_content,feed.data.content);
                        feedHolder.iv_content.setVisibility(View.VISIBLE);
                        ImageLoader.getInstance().displayImage(AppContants.DOMAIN + feed.data.attachment.thumb.url, feedHolder.iv_content, contentOptions, animateFirstListener);
                        ivContentOnClickListener(context,feedHolder,feed.data.attachment.url);
                        break;

                }


                feedHolder.tv_content.setVisibility(View.VISIBLE);
                if(feed.data.content!=null)
                contentHandler.formatColorContent(feedHolder.tv_content,feed.data.content);

               // project=feed.data.project.title;
                break;

        }


        if (feed.hasChildren) {

            feedHolder.tv_response.setVisibility(View.GONE);

        } else {
            feedHolder.v_line.setVisibility(View.GONE);
        }

        if(feed.data.user.id.equals(Global.USER_ID+""))
        {
            feedHolder.rl_agree.setVisibility(View.GONE);
        }



        ImageLoader.getInstance().displayImage(AppContants.DOMAIN + feed.data.user.avatar.small.url, feedHolder.iv_icon, feedOptions, animateFirstListener);


        feedHolder.tv_type.setText(type);
        feedHolder.tv_proect.setText(project);
        feedHolder.tv_name.setText(feed.data.user.username);
        feedHolder.tv_time.setText(TimeUtil.getDistanceTime(feed.created_at));

        if(feed.agreement_status)
        {
            feedHolder.iv_agree.setImageResource(R.drawable.liked);
        }else{
            feedHolder.iv_agree.setImageResource(R.drawable.like);
        }

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

    public  static void ivContentOnClickListener(final Context context ,final BaseFeedFragment.FeedHolder feedHolder,final String url) {

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

    public static View initFeedItemView2(Context context,View convertView) {


        View view;
        if (convertView == null) {
            view= View.inflate(context, R.layout.feed_common_item, null);
            // view =View.inflate(context,R.layout.feed_common_item,null);
            BaseFeedFragment.FeedHolder feedHolder = new BaseFeedFragment.FeedHolder();
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

            feedHolder.rl_agree= (RelativeLayout) view.findViewById(R.id.rl_agree);
            feedHolder.iv_agree= (ImageView) view.findViewById(R.id.iv_agree);
            feedHolder.tv_agreecount= (TextView) view.findViewById(R.id.tv_agreecount);

            view.setTag(feedHolder);

        } else {
            view = convertView;



        }

        return  view;
    }


    public static View initCommentItem(Context context,View convertView) {

        View view;
        if (convertView == null) {
            view = View.inflate(context, R.layout.comment_common_item, null);
            // view =View.inflate(context,R.layout.feed_common_item,null);
            BaseFeedFragment.CommentHolder commentHolder = new BaseFeedFragment.CommentHolder();
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



    public static  void initCommentItemData(Comment comment,BaseFeedFragment.CommentHolder commentHolder,ImageLoadingListener animateFirstListener) {
        //开始switch
        commentHolder.tv_response.setVisibility(View.VISIBLE);
//        commentHolder.v_line.setVisibility(View.VISIBLE);



        //初始化一些common信息
        commentHolder.tv_name.setText(comment.user.username);
        commentHolder.tv_time.setText(TimeUtil.getDistanceTime(comment.created_at));
        commentHolder.tv_content.setVisibility(View.VISIBLE);
       // commentHolder.tv_content.setText(comment.content);


        ContentHandler contentHandler= new ContentHandler(commentHolder.tv_content).longClickCopy();
        contentHandler.formatColorContent(commentHolder.tv_content,comment.content);


        //头像图片处理
        ImageLoader.getInstance().displayImage(AppContants.DOMAIN + comment.user.avatar.small.url, commentHolder.iv_icon, commentOptions, animateFirstListener);



        //隐藏回复框
        if (!comment.isLast) {
            commentHolder.tv_response.setVisibility(View.GONE);
//            commentHolder.v_line.setVisibility(View.GONE);
        }
    }

    public static  void setFeedCommonClick(Context context ,Feed2 feed, BaseFeedFragment.FeedHolder feedHolder) {
        if (feedHolder.rl_agree.getVisibility() == View.VISIBLE) {
            feedHolder.iv_agree.setOnClickListener(new ArgeeOnClickController(context, feed, feedHolder.tv_agreecount));
        }
    }



}

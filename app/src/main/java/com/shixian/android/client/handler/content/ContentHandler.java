package com.shixian.android.client.handler.content;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shixian.android.client.R;
import com.shixian.android.client.engine.CommentEngine;
import com.shixian.android.client.model.Comment;
import com.shixian.android.client.model.feeddate.BaseFeed;
import com.shixian.android.client.utils.MyLinkMovementMethod;
import com.shixian.android.client.views.CosmterUrlSpan;
import com.shixian.android.client.views.CustomDialog;

import org.apache.http.Header;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tangtang on 15/3/10.
 * 用处处理文本
 */
public class ContentHandler {

    private static final String TAG="ContentHandler";

    private static final int URLCOLOR=Color.parseColor("#0088cc");

    private TextView content;


    //\&%_\./-~-]*)?

    private static final Pattern URLPATTERN = Pattern
            .compile("((http://|https://|ftp://){0,1}(([0-9a-zA-Z\\-]+\\.)+(com|net|cn|me|tw|fr|tk|edu|io)[0-9a-zA-Z/\\._%\\?&=\\-#]*))|@[^\\s]+\\s");


    public ContentHandler(TextView tv) {
        this.content = tv;

    }

    public ContentHandler longClickCopy() {


        content.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setItems(R.array.message_action_text_copy, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                           copy(((TextView) v).getText().toString(), v.getContext());
                           Toast.makeText(v.getContext(), "已复制", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                AlertDialog dialog = builder.show();



                CustomDialog.dialogTitleLineColor(v.getContext(), dialog);
                return true;
            }
        });


        return this;

    }



    public void formatColorContent(TextView tv,CharSequence s)
    {


        tv.setMovementMethod(MyLinkMovementMethod.getInstance());
        tv.setClickable(true);

        SpannableString spannableString=new SpannableString(s);
        Matcher urlMatcher= URLPATTERN.matcher(s);


        while (urlMatcher.find())
        {
            URLSpan span=new CosmterUrlSpan(urlMatcher.group(),URLCOLOR);
            spannableString.setSpan(span,urlMatcher.start(),urlMatcher.end(),Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }


        tv.setText(spannableString);




    }

    public static void copy(String content, Context context) {
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content);
    }


    public ContentHandler longClickCopyAndDelete(final Comment comment, final BaseAdapter adapter,final List<BaseFeed> feeds) {


        final Context context=content.getContext();
        content.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(content.getContext());
                builder.setItems(R.array.comment_gallery, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    copy(((TextView) v).getText().toString(), v.getContext());
                                    Toast.makeText(v.getContext(), "已复制", Toast.LENGTH_SHORT).show();
                                } else {

                                    CommentEngine.deleteComment(content.getContext(),comment.id,new AsyncHttpResponseHandler(){

                                        @Override
                                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
                                            onDelectSucess(comment, feeds, adapter);
                                        }

                                        @Override
                                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                                            Toast.makeText(context,"删除失败",Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();




                return false;
            }
        });

        return this;
    }


    /**
     * {@link com.shixian.android.client.handler.feed.BaseFeedHandler}
     * @param comment
     * @param adapter
     * @param feeds
     */
    public ContentHandler clickCopyAndDelete(final Comment comment, final BaseAdapter adapter,final List<BaseFeed> feeds)
    {
        final Context context=content.getContext();
        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(content.getContext());
                builder
                        .setItems(R.array.comment_gallery, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    copy(((TextView) v).getText().toString(), v.getContext());
                                    Toast.makeText(v.getContext(), "已复制", Toast.LENGTH_SHORT).show();
                                } else {

                                    CommentEngine.deleteComment(content.getContext(),comment.id,new AsyncHttpResponseHandler(){

                                        @Override
                                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
                                            onDelectSucess(comment,feeds,adapter);


                                        }

                                        @Override
                                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                                            Toast.makeText(context,"删除失败",Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();





            }
        });

        return this;
    }


    public void onDelectSucess(Comment comment,List<BaseFeed> feeds,BaseAdapter adapter)
    {

        /**
         * 分析
         * 这里有两大类情况
         * 1 .如果只有1条回复  那么 第一条也就是最后 一条 删除后 就 没了  父亲的hasClildren就变成了false
         * 2。 如果有一条以上回复  这样 有三种情况
         *     （1）  删除第一条回复  那么 下一条变成了第一条
         *     （2） 删除最后一条 上一条就变成了最后一条
         *      (3) 删除中间一条  不做任何改变
         *
         */

        comment.parent.lastChildPosition-=1;

        //1 只有一条回复
        if(comment.isFirst&&comment.isLast)
        {
            comment.parent.hasChildren=false;

        }else {
            //2（1）删除第一条回复
            if(comment.isFirst)
            {
                Comment postComment= (Comment) feeds.get(comment.position+1);
                postComment.isFirst=true;
            }

            //2(2) 删除最后一条
            if(comment.isLast)
            {
                Comment preComment= (Comment) feeds.get(comment.position-1);
                //肯定不是一个  如果是两个的情况呢
                preComment.isLast=true;
            }

        }

        feeds.remove(comment);

        adapter.notifyDataSetChanged();
        Toast.makeText(content.getContext(), "删除成功", Toast.LENGTH_SHORT).show();

    }



    public ContentHandler longClickMsgDetialCopyAndDelete(final Comment comment, final BaseAdapter adapter,final List<BaseFeed> feeds) {


        final Context context=content.getContext();
        content.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(content.getContext());
                builder
                        .setItems(R.array.comment_gallery, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    copy(((TextView) v).getText().toString(), v.getContext());
                                    Toast.makeText(v.getContext(), "已复制", Toast.LENGTH_SHORT).show();
                                } else {

                                    CommentEngine.deleteComment(content.getContext(),comment.id,new AsyncHttpResponseHandler(){

                                        @Override
                                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
                                            onMsgDelectSucess(comment, feeds, adapter);
                                        }

                                        @Override
                                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                                            Toast.makeText(context,"删除失败",Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();




                return false;
            }
        });

        return this;
    }


    public void onMsgDelectSucess(Comment comment,List<BaseFeed> feeds,BaseAdapter adapter)
    {


        feeds.remove(comment);

        adapter.notifyDataSetChanged();
        Toast.makeText(content.getContext(), "删除成功", Toast.LENGTH_SHORT).show();

    }


    /**
     * {@link com.shixian.android.client.handler.feed.BaseFeedHandler}
     * @param comment
     * @param adapter
     * @param feeds
     */
    public ContentHandler clickMsgCopyAndDelete(final Comment comment, final BaseAdapter adapter,final List<BaseFeed> feeds)
    {
        final Context context=content.getContext();
        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(content.getContext());
                builder
                        .setItems(R.array.comment_gallery, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == 0) {
                                    copy(((TextView) v).getText().toString(), v.getContext());
                                    Toast.makeText(v.getContext(), "已复制", Toast.LENGTH_SHORT).show();
                                } else {

                                    CommentEngine.deleteComment(content.getContext(),comment.id,new AsyncHttpResponseHandler(){

                                        @Override
                                        public void onSuccess(int i, Header[] headers, byte[] bytes) {
                                           onMsgDelectSucess(comment,feeds,adapter);

                                        }

                                        @Override
                                        public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                                            Toast.makeText(context,"删除失败",Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();





            }
        });

        return this;
    }


}
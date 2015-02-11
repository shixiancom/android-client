package com.shixian.android.client.activities.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shixian.android.client.R;
import com.shixian.android.client.activities.fragment.base.BaseFragment;
import com.shixian.android.client.contants.AppContants;
import com.shixian.android.client.engine.CommonEngine;
import com.shixian.android.client.model.Comment;
import com.shixian.android.client.model.Feed2;
import com.shixian.android.client.model.feeddate.BaseFeed;
import com.shixian.android.client.utils.CommonUtil;
import com.shixian.android.client.utils.ImageCache;
import com.shixian.android.client.utils.ImageCallback;
import com.shixian.android.client.utils.ImageDownload;
import com.shixian.android.client.utils.JsonUtils;
import com.shixian.android.client.utils.TimeUtil;

import org.apache.http.Header;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by s0ng on 2015/2/10.
 */
public class IndexFragment extends BaseFragment {

    private String TAG = "IndexFragment";

    private ListView lv_index;
    private int page = 1;

    private String firstPageDate;
    private List<BaseFeed> feedList;
    private FeedAdapter adapter;

    private ImageCallback callback;




    @Override
    public View initView(LayoutInflater inflater) {

        View view = inflater.inflate(R.layout.fragment_index, null, false);
        lv_index = (ListView) view.findViewById(R.id.lv_index);
        feedList = new ArrayList<BaseFeed>();
        return view;
    }

    @Override
    public void initDate(Bundle savedInstanceState) {

        initImageCallBack();


        CommonEngine.getIndexDate(page, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                if (!AppContants.errorMsg.equals(bytes)) {
                    //获取第一页数据
                    firstPageDate = new String(bytes);
                    //数据格式
                    CommonUtil.logDebug(TAG, new String(bytes));


                    feedList = JsonUtils.ParseFeeds(firstPageDate);

                    //保存数据到本地

                    if (adapter == null) {
                        adapter = new FeedAdapter();
                        lv_index.setAdapter(adapter);
                    } else {
                        adapter.notifyDataSetChanged();
                    }


                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                Log.i("AAAA", new String(bytes));
            }
        });

    }

    private void initImageCallBack() {
        this.callback=new ImageCallback() {

            @Override
            public void imageLoaded(Bitmap bitmap, Object tag) {
                ImageView imageView = (ImageView) lv_index
                        .findViewWithTag(tag);

                if (imageView != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        };
    }


    class FeedAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return feedList.size();
        }

        @Override
        public Object getItem(int position) {
            return feedList.get(position);
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

           View view=null;
            FeedHolder holder;

            if(convertView==null)
            {
                view=View.inflate(context,R.layout.feed_common_item,null);
                holder=new FeedHolder();
                holder.iv_icon= (ImageView) view.findViewById(R.id.iv_icon);
                holder.tv_name= (TextView) view.findViewById(R.id.tv_name);
                holder.tv_proect= (TextView) view.findViewById(R.id.tv_proect);
                holder.tv_time= (TextView) view.findViewById(R.id.tv_time);;
                holder.tv_content= (TextView) view.findViewById(R.id.tv_content);;
                holder.iv_content=(ImageView) view.findViewById(R.id.iv_content);;
                holder.tv_response= (TextView) view.findViewById(R.id.tv_response);
                holder.tv_type= (TextView) view.findViewById( R.id.tv_type);
                view.setTag(holder);

            }else{
                view=convertView;
                holder= (FeedHolder) view.getTag();
            }

            BaseFeed baseFeed=feedList.get(position);

            String type="";
            String project="";
            String content;

            if(!baseFeed.feedable_type.equals(AppContants.FEADE_TYPE_COMMON))
            {
                Feed2 feed= (Feed2) baseFeed;

                if(feed.data.project!=null&&!TextUtils.isEmpty(feed.data.project.title))
                    project=feed.data.project.title;

                holder.iv_content.setVisibility(View.GONE);



                switch (feed.feedable_type) {
                    case "Idea":
                        type = context.getResources().getString(R.string.add_idea);
                        content = Html.fromHtml(feed.data.content_html).toString();
                        holder.tv_content.setText(content);
                        break;
                    case "Project":
                        type = context.getResources().getString(R.string.add_project);
                        project = feed.data.title;
                        content = Html.fromHtml(feed.data.description).toString();
                        holder.tv_content.setText(content);

                        break;
                    case "Plan":
                        type = context.getResources().getString(R.string.add_plan);
                        content = feed.data.content + "   截至到: " + feed.data.finish_on;
                        break;
                    case "Image":
                        content = Html.fromHtml(feed.data.content_html).toString();
                        type = context.getResources().getString(R.string.add_image);
                        holder.tv_content.setText(content);

                        break;
                    case "UserProjectRelation":
                        switch (feed.data.status) {
                            case "jion":
                                type = context.getResources().getString(R.string.join);
                                break;
                        }

                        break;
                }


                //头像图片处理
                String keys[]=feed.data.user.avatar.small.url.split("/");
                String key=keys[keys.length-1];
                Bitmap bm = ImageCache.getInstance().get(key);
//

                if (bm != null) {
                    holder.iv_icon.setImageBitmap(bm);
                } else {
                    holder.iv_icon.setImageResource(R.drawable.ic_launcher);
                    holder.iv_icon.setTag(key);
                    if (callback != null) {
                        new ImageDownload(callback).execute(AppContants.DOMAIN+feed.data.user.avatar.small.url, key, ImageDownload.CACHE_TYPE_LRU);
                    }
                }



            }else{
                Comment comment= (Comment) baseFeed;
                //初始化一些common信息
                holder.tv_name.setText(comment.user.username);
                holder.tv_time.setText(TimeUtil.getDistanceTime(comment.created_at));
                holder.tv_proect.setVisibility(View.INVISIBLE);
                holder.tv_type.setVisibility(View.INVISIBLE);
                holder.tv_content.setText(Html.fromHtml(comment.content_html));
                holder.tv_type.setText(type);
                holder.tv_proect.setText(project);
                holder.tv_time.setText(TimeUtil.getDistanceTime(baseFeed.created_at));

            }



            return view;

        }
    }


    /**
     * 有些控件要求隐藏
     */
    class FeedHolder{

        //事件类型 比如发布一个项目
        TextView tv_type;
        //头像
        ImageView iv_icon;
        //用户名
        TextView tv_name;
        //项目
        TextView tv_proect;
        //时间
        TextView tv_time;
        //回复内容
        TextView tv_content;
        //图片内容 默认是隐藏的 当feedable_type为image时显示
        ImageView iv_content;
        //回复框 发表项目的时候是隐藏的
        TextView tv_response;
    }

}

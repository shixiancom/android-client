package com.shixian.android.client.activities.fragment.base;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shixian.android.client.R;
import com.shixian.android.client.contants.AppContants;
import com.shixian.android.client.engine.CommonEngine;
import com.shixian.android.client.model.Feed2;
import com.shixian.android.client.utils.CommonUtil;
import com.shixian.android.client.utils.JsonUtils;

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
    private List<Feed2> feedList;
    private FeedAdapter adapter;


    @Override
    public View initView(LayoutInflater inflater) {

        View view = inflater.inflate(R.layout.fragment_index, null, false);
        lv_index = (ListView) view.findViewById(R.id.lv_index);
        feedList = new ArrayList<Feed2>();
        return view;
    }

    @Override
    public void initDate(Bundle savedInstanceState) {


        CommonEngine.getIndexDate(page, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                if (!AppContants.errorMsg.equals(bytes)) {
                    //获取第一页数据
                    firstPageDate = new String(bytes);
                    //数据格式
                    CommonUtil.logDebug(TAG, new String(bytes));

                    Gson gson = new Gson();
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
                view.setTag(holder);

            }else{
                view=convertView;
                holder= (FeedHolder) view.getTag();
            }


            return view;

        }
    }



    class FeedHolder{

    }

}

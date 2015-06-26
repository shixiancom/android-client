package com.shixian.android.client.activities.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.shixian.android.client.R;
import com.shixian.android.client.activities.MainActivity;
import com.shixian.android.client.activities.UserActivity;
import com.shixian.android.client.activities.base.BaseFeedActivity;
import com.shixian.android.client.activities.fragment.base.AbsListViewBaseFragment;
import com.shixian.android.client.contants.AppContants;
import com.shixian.android.client.controller.BootOnClickController;
import com.shixian.android.client.engine.BootEngine;
import com.shixian.android.client.handler.feed.BaseFeedHandler;
import com.shixian.android.client.model.BaseBoot;
import com.shixian.android.client.model.Boot;
import com.shixian.android.client.model.User;
import com.shixian.android.client.utils.JsonUtils;
import com.shixian.android.client.utils.SharedPerenceUtil;
import com.shixian.android.client.views.pulltorefreshlist.PullToRefreshBase;
import com.shixian.android.client.views.pulltorefreshlist.PullToRefreshListView;
import org.apache.http.Header;
import java.util.Collections;
import java.util.List;

/**
 * Created by tangtang on 15/3/30.
 * x 本周启动
 */
public class SpotlightFragment extends AbsListViewBaseFragment {

    private  static final String TAG="SpotlightFragment";

    private ListView listView;

    private TeamOnlineAdapter adapter;

    private List<BaseBoot> bootList= Collections.emptyList();

    private Context context;


    private Handler handler=new Handler()
    {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            adapter.notifyDataSetChanged();
        }
    };




    protected ImageLoadingListener animateFirstListener = new BaseFeedActivity.AnimateFirstDisplayListener();


    @Override
    public View initView(LayoutInflater inflater)
    {

        context=getActivity();
        View view = inflater.inflate(R.layout.fragment_spotlight, null, false);

        initLable();

        pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.lv_index);
        pullToRefreshListView.getListView().setDividerHeight(0);


        this.listView = pullToRefreshListView.getListView();

        // 滚动到底自动加载可用
        pullToRefreshListView.setScrollLoadEnabled(true);


        adapter=new TeamOnlineAdapter();
        pullToRefreshListView.getListView().setAdapter(adapter);


        // 设置下拉刷新的listener
        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

            //下拉舒心完成
            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {

                //上啦刷新
                initFirstData();
            }



            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                //getNewsList(moreUrl, false);
                //下拉加载更多
                pullToRefreshListView.getFooterLoadingLayout().setVisibility(View.GONE);



            }
        });

//        if (feedList == null)
//            feedList = new ArrayList<BaseFeed>();

        pullToRefreshListView.setPullLoadEnabled(false);

        return view;
    }




    private void initFirst() {

        initCacheDate();
        initFirstData();
    }


    private void initCacheDate()
    {
        bootList= JsonUtils.parseBoots(SharedPerenceUtil.getBoots(context.getApplicationContext())) ;

        if (adapter == null) {
            adapter = new TeamOnlineAdapter();
            pullToRefreshListView.getListView().setAdapter(adapter);

        } else {
            handler.sendEmptyMessage(0);
        }

    }

    private void initLable() {

        getActivity().setTitle("在线组队");
    }

    @Override
    public void initDate(Bundle savedInstanceState) {

        initFirst();
    }



    @Override
    public void initFirstData() {

        ((MainActivity)context).showProgress();
        BootEngine.getBootFeed(context,new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {
                final String json=new String(bytes);
                bootList= JsonUtils.parseBoots(json);
                handler.sendEmptyMessage(0);
                pullToRefreshListView.onPullDownRefreshComplete();
                ((MainActivity)context).dissProgress();
                new Thread()
                {
                    public  void run()
                    {
                        SharedPerenceUtil.putBoots(context.getApplicationContext(),json);

                    }
                }.start();

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                if(isAdded())
                {
                    Toast.makeText(context, R.string.check_net,Toast.LENGTH_SHORT).show();
                    pullToRefreshListView.onPullDownRefreshComplete();
                    ((MainActivity)context).dissProgress();
                }

            }
        });
    }



    @Override
    public boolean needRefresh() {
        return false;
    }


    private static final int TYPE_PARENT_TITLE=0;
    private static final int TYPE_TEAM_RERITED=1;
    private static final int TYPE_TEAM_LEADER=2;

    private class TeamOnlineAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return bootList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public int getViewTypeCount() {
            return 4;
        }

        @Override
        public int getItemViewType(int position) {

            if("type_title".equals(bootList.get(position).type))
            {
                return  TYPE_PARENT_TITLE;
            }

            if("competitor_projects".equals(bootList.get(position).type))
            {
                return  TYPE_TEAM_LEADER;
            }

            return  TYPE_TEAM_RERITED;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            BootOnClickController bootOnClickController=null;

            View view=null;
            switch (getItemViewType(position))
            {
                case TYPE_PARENT_TITLE:
                    view =View.inflate(context,R.layout.team_common,null);
                    TextView tv_parent_title= (TextView) view.findViewById(R.id.tv_parent_title);
                    switch (position){
                        case 0:
                            tv_parent_title.setText("团队招募中");
                            break;
                        case 4:
                            tv_parent_title.setText("负责选举中");
                            break;
                        case 8:
                            tv_parent_title.setText("往期组队项目");
                            break;

                    }
                    break;
                case TYPE_TEAM_LEADER:

                    Boot competitorBoot= (Boot) bootList.get(position);
                    bootOnClickController=new BootOnClickController(getActivity(),competitorBoot);
                    CompetitorHolder coHolder = null;

                    if(convertView!=null)
                    {
                        view=convertView;
                        coHolder= (CompetitorHolder) view.getTag();


                    }
                    else {
                        view=View.inflate(context,R.layout.get_team_leader,null);
                         coHolder=new CompetitorHolder();
                        coHolder.gl_compoters= (GridLayout) view.findViewById(R.id.gl_compoters);
                        coHolder.tv_content= (TextView) view.findViewById(R.id.tv_content);
                        coHolder.tv_title= (TextView) view.findViewById(R.id.tv_title);
                        coHolder.ll_project= (LinearLayout) view.findViewById(R.id.ll_project);
                        coHolder.tv_zero= (TextView) view.findViewById(R.id.tv_zero);
                        view.setTag(coHolder);
                    }



                    coHolder.tv_title.setText(competitorBoot.project.title);
                    coHolder.tv_content.setText(competitorBoot.project.description);

                    coHolder.gl_compoters.removeAllViews();

                    if(competitorBoot.competitors==null||competitorBoot.competitors.size()==0)
                    {
                        coHolder.tv_zero.setVisibility(View.VISIBLE);
                        coHolder.gl_compoters.setVisibility(View.GONE);
                    }else {
                        coHolder.tv_zero.setVisibility(View.GONE);
                        coHolder.gl_compoters.setVisibility(View.VISIBLE);

                        for(final User user: competitorBoot.competitors)
                        {

                            View view1=View.inflate(getActivity(),R.layout.simple_icon_item,null);
                       //     ImageView imageView=new ImageView(getActivity());

                        //    ImageLoader.getInstance().displayImage(AppContants.DOMAIN + user.avatar.small.url, imageView, BaseFeedHandler.commentOptions, animateFirstListener);
                          //  imageView.setImageResource(R.drawable.default_icon);
                                ImageLoader.getInstance().displayImage(AppContants.ASSET_DOMAIN + user.avatar.small.url, (ImageView)view1.findViewById(R.id.iv_icon), BaseFeedHandler.commentOptions, animateFirstListener);

                            coHolder.gl_compoters.addView(view1);

                            view1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent= new Intent(context, UserActivity.class);
                                    Bundle bundle=new Bundle();
                                    bundle.putSerializable("user",user);
                                    intent.putExtras(bundle);
                                    context.startActivity(intent);
                                }
                            });
                        }


                    }





                    coHolder.ll_project.setOnClickListener(bootOnClickController);


                    break;


                case  TYPE_TEAM_RERITED:
                    Boot reritedBoot= (Boot) bootList.get(position);
                    bootOnClickController=new BootOnClickController(getActivity(),reritedBoot);

                    ParentHolder parentHolder=null;

                    if(convertView!=null)
                    {
                        view=convertView;
                        parentHolder= (ParentHolder) view.getTag();

                    }else{
                        view=View.inflate(context,R.layout.team_recruited_item,null);
                        parentHolder=new ParentHolder();
                        parentHolder.iv_icon= (ImageView) view.findViewById(R.id.iv_icon);
                        parentHolder.tv_content= (TextView) view.findViewById(R.id.tv_content);
                        parentHolder.tv_name= (TextView) view.findViewById(R.id.tv_name);
                        parentHolder.tv_title= (TextView) view.findViewById(R.id.tv_title);
                        parentHolder.tv_userdescription= (TextView) view.findViewById(R.id.tv_userdescription);
                        parentHolder.tv_vacancy= (TextView) view.findViewById(R.id.tv_vacancy);
                        parentHolder.ll_project= (LinearLayout) view.findViewById(R.id.ll_project);
                        parentHolder.rl_user= (RelativeLayout) view.findViewById(R.id.rl_user);
                        parentHolder.ll_position= (LinearLayout) view.findViewById(R.id.ll_position);
                        view.setTag(parentHolder);


                    }

                    if("team_recruit_projects".equals(reritedBoot.type))
                    {
                        parentHolder.tv_vacancy.setText(reritedBoot.vacancy);


                    }else{
                        parentHolder.tv_vacancy.setText("暂无");
                    }
                    parentHolder.tv_title.setText(reritedBoot.project.title);
                    ImageLoader.getInstance().displayImage(AppContants.ASSET_DOMAIN + reritedBoot.user.avatar.small.url, parentHolder.iv_icon, BaseFeedHandler.feedOptions, animateFirstListener);
                    parentHolder.tv_content.setText(reritedBoot.project.description);
                    parentHolder.tv_name.setText(reritedBoot.user.username);
                    parentHolder.tv_userdescription.setText(reritedBoot.user.description);


                    parentHolder.rl_user.setOnClickListener(bootOnClickController);
                    parentHolder.ll_project.setOnClickListener(bootOnClickController);

                    if("old_boot_projects".equals(reritedBoot.type))
                        parentHolder.ll_position.setVisibility(View.GONE);


                    break;

            }

            return view;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity)getActivity()).setCurrentFragment(this);
    }


    private class ParentHolder{
        TextView tv_title;
        TextView tv_content;
        ImageView iv_icon;
        TextView tv_name;
        TextView tv_userdescription;
        TextView tv_vacancy;
        LinearLayout ll_project;
        RelativeLayout rl_user;
        LinearLayout ll_position;
    }

    private class CompetitorHolder{
        TextView tv_title;
        TextView tv_content;
        GridLayout gl_compoters;
        LinearLayout ll_project;
        TextView tv_zero;
    }


    private class LeaderAdapter extends  BaseAdapter{

        private List<User> users;

         public  LeaderAdapter(List<User> users)
         {
             this.users=users;
         }

        @Override
        public int getCount() {
            return users.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            SimpleIconHolder simpleIconHolder;
            if(convertView==null)
            {
                view=View.inflate(getActivity(), R.layout.simple_icon_item, null);
                simpleIconHolder=new SimpleIconHolder();
                simpleIconHolder.iv_icon= (ImageView) view.findViewById(R.id.iv_icon);
                view.setTag(simpleIconHolder);

            }else{
               view=convertView;
                simpleIconHolder= (SimpleIconHolder) view.getTag();
            }

            ImageLoader.getInstance().displayImage(AppContants.ASSET_DOMAIN + users.get(position).avatar.small.url, simpleIconHolder.iv_icon, BaseFeedHandler.commentOptions, animateFirstListener);

            return view;
        }
    }


    private  class SimpleIconHolder{
        ImageView iv_icon;
    }
}

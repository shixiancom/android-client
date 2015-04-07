package com.shixian.android.client.activities.fragment.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shixian.android.client.activities.base.BaseActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * Created by s0ng on 2015/2/9.
 */
public abstract class BaseFragment extends Fragment  {


    protected BaseActivity context;

    protected Bundle savedInstanceState;




    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getSimpleName());

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context= (BaseActivity) getActivity();

        View view=initView(inflater);
//        initDate(savedInstanceState);
        this.savedInstanceState=savedInstanceState;
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        initDate(savedInstanceState);
        MobclickAgent.onPageStart(getClass().getSimpleName()); //统计页面

    }

    public abstract View initView(LayoutInflater inflater);
    public abstract void initDate(Bundle savedInstanceState);

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }





    /**
     * 刷新界面
     */
    public abstract  void initFirstData();

    /**
     * 由于有一些页面是在mainactivity中
     * 所以呢 我们需要在发表完项目的时候刷新页面
     * 返回true 就需要刷新
     * 返回false 则不需要刷新
     * @return
     */
    public abstract  boolean needRefresh();

}

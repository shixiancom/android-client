package com.shixian.android.client.activities.fragment.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shixian.android.client.activities.BaseActivity;
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


    public abstract  void setCurrentPosition(int position);
}

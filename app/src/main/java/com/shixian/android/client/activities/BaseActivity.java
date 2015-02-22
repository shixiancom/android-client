package com.shixian.android.client.activities;

import android.support.v7.app.ActionBarActivity;

import com.shixian.android.client.activities.fragment.base.BaseFragment;

/**
 * Created by s0ng on 2015/2/22.
 */
public abstract  class BaseActivity extends ActionBarActivity {


    /**
     * 添加第一个activity
     */
    protected abstract void addFragment();

    /**
     * 切换fragment
     * @param fragment   要切换的fragment
     * @param key     放入回退栈的key
     */
    public abstract void switchFragment(BaseFragment fragment,String key);

    public abstract  void  setLable(String lable);

    public abstract  void showProgress();

    public abstract void dissProgress();
}

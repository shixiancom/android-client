package com.shixian.android.client.activities.fragment.base;

import android.support.v4.app.Fragment;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by tangtang on 15/3/31.
 */
public class UmengFragment extends Fragment{

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getSimpleName()); //统计页面

    }


    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getSimpleName());

    }
}

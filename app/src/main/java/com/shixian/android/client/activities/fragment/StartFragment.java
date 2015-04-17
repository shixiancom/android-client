package com.shixian.android.client.activities.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shixian.android.client.R;

/**
 * Created by tangtang on 15/4/16.
 */
public class StartFragment extends Fragment{


    private int resId;

    public static StartFragment getInstance(int  resId)
    {
        StartFragment fragment=new StartFragment();

        fragment.setResId(resId);

        return fragment;
    }


    private void setResId(int resId)
    {
        this.resId=resId;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view= inflater.inflate( R.layout.fragment_start, null, false);


        view.setBackgroundResource(resId);

        return view;
    }


}

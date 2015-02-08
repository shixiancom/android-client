package com.shixian.android.client.activities.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shixian.android.client.R;

/**
 * Created by doom on 15/2/8.
 */
public class NewsFragment extends Fragment
{
    private static final String TAG = "NewsFragment";
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        Log.d(TAG,"onCreateView");
        return inflater.inflate(R.layout.fragment_news, container, false);
    }
}

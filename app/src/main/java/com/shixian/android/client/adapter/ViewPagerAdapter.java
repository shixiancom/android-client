package com.shixian.android.client.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.shixian.android.client.R;
import com.shixian.android.client.activities.fragment.LoginFragment;
import com.shixian.android.client.activities.fragment.StartFragment;

/**
 * Created by tangtang on 15/4/16.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter{




    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position)
        {
            case 0:
                return StartFragment.getInstance(R.drawable.tutorial_1);
            case 1:
                return StartFragment.getInstance(R.drawable.tutorial_2);
            case 2:
                return StartFragment.getInstance(R.drawable.tutorial_3);

            case 3:
                return new LoginFragment();
        }

        return null;


    }

    @Override
    public int getCount() {
        return 4;
    }
}

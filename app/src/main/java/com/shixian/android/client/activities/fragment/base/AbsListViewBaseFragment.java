/*******************************************************************************
 * Copyright 2011-2014 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.shixian.android.client.activities.fragment.base;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AbsListView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.shixian.android.client.activities.base.BaseActivity;
import com.shixian.android.client.views.pulltorefreshlist.PullToRefreshListView;



/**
 * @author Sergey Tarasevich (nostra13[at]gmail[dot]com)
 */
public abstract class AbsListViewBaseFragment extends BaseFragment {


    public static final int REFRESH_PAGE=10086;


	protected PullToRefreshListView pullToRefreshListView;

    protected int currentFirstPos=0;

	protected boolean pauseOnScroll = false;
	protected boolean pauseOnFling = true;



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((BaseActivity)getActivity()).setToolbarOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pullToRefreshListView.getListView().setSelection(0);
            }
        });
    }

    @Override
	public void onResume() {
		super.onResume();
		applyScrollListener();
	}



	private void applyScrollListener() {
        pullToRefreshListView.setOnScrollListener(new MyPauseOnScrollListener(ImageLoader.getInstance(), pauseOnScroll, pauseOnFling));

	}

    protected class MyPauseOnScrollListener extends PauseOnScrollListener{

        public MyPauseOnScrollListener(ImageLoader imageLoader, boolean pauseOnScroll, boolean pauseOnFling) {
            super(imageLoader, pauseOnScroll, pauseOnFling);
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            super.onScrollStateChanged(view, scrollState);
            currentFirstPos=pullToRefreshListView.getListView().getFirstVisiblePosition();

        }
    }


}

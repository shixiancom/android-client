package com.shixian.android.client.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Created by s0ng on 2015/2/10.
 * 嵌套listView的父view
 */
public class ParentListView extends ListView{
    public ParentListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
//                MeasureSpec.AT_MOST);
//        super.onMeasure(widthMeasureSpec, expandSpec);
//    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

}

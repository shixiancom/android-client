package com.shixian.android.client.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Created by s0ng on 2015/2/9.
 * 嵌套的listview的子listView  应该屏蔽点击事件分发
 */
public class SubListView extends ListView{

    public SubListView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public SubListView(Context context) {
        super(context);
    }




    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}

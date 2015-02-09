package com.shixian.android.client.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by s0ng on 2015/2/9.
 * 外层ListView 重写onMear方法
 */
public class ParentListView extends ListView {

    public ParentListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {


        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,


                MeasureSpec.AT_MOST);


        super.onMeasure(widthMeasureSpec, expandSpec);


    }


}

package com.shixian.android.client.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * Created by s0ng on 2015/2/12.
 * 在搞个人主页ListView的时候遇到了大坑 为了区分类型 在这里继承一个LinearLayout 什么都不作 只是改变了类型
 */
public class PersonItemLinearLayout extends LinearLayout {
    public PersonItemLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}

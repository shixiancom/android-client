package com.shixian.android.client;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.shixian.android.client.model.User;


/**
 *
 * 用于存放全局信息  比如说 user cookie 好吧
 */
public class Global {

    //TODO
    //放在这里容易被回收
    public static User user;
    public static String USER_ID;
    public static String USER_NAME="";

    public static Context context;


    public static Context MAIN;

    //cookie的格式是  key=value  传送的时候记得拆分
    public static String cookie;



    public static int screenWidth;

    public static int iv_conte_size;


    //弹出软键盘
    public static void popSoftkeyboard(Context ctx, View view, boolean wantPop) {
        InputMethodManager imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (wantPop) {
            view.requestFocus();
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        } else {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


}

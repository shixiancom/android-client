package com.shixian.android.client.utils;

import android.content.Context;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.shixian.android.client.contants.AppContants;

/**
 * Created by s0ng on 2015/2/8.
 */
public class CommonUtil {

    public static void logDebug(String tag,String msg)
    {
        if(AppContants.DEBUG)
            Log.d(tag,msg);
    }

    /**
     * 获取屏幕宽度
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }
}

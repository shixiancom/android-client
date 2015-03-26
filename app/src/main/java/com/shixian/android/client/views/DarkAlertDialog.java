package com.shixian.android.client.views;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;

/**
 * Created by tangtang on 15/3/25.
 */
public class DarkAlertDialog extends AlertDialog {
    protected DarkAlertDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);

    }


    public DarkAlertDialog(Context context) {
        super(context);
    }

    public DarkAlertDialog(Context context, int theme) {
        super(context, theme);
    }

    public static AlertDialog create(Context context) {


            return new DarkAlertDialog(context, THEME_HOLO_DARK);
    }


    public class DrakAlertBuild extends Builder{


        public Builder create(final Context context) {
            return new DarkAlertDialog.Builder(context, THEME_HOLO_DARK);
        }

        public DrakAlertBuild(Context context, int theme) {
            super(context, theme);

        }
    }





}

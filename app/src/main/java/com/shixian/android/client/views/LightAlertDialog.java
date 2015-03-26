package com.shixian.android.client.views;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Build;

/**
 * Created by tangtang on 15/3/25.
 */
public class LightAlertDialog  extends AlertDialog {


    public static AlertDialog create(final Context context)
    {
        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        {
            return new LightAlertDialog(context,THEME_HOLO_LIGHT);

        }else
        {
            return new LightAlertDialog(context);
        }
    }


    private LightAlertDialog(final Context context, final int theme) {
        super(context, theme);
    }

    private LightAlertDialog(final Context context) {
        super(context);
    }

    public  static class Builder extends  AlertDialog.Builder{

        public static LightAlertDialog.Builder create(final Context context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                return new LightAlertDialog.Builder(context, THEME_HOLO_LIGHT);
            else
                return new LightAlertDialog.Builder(context);
        }

        private Builder(Context context) {
            super(context);
        }

        private Builder(Context context, int theme) {
            super(context, theme);
        }

    }


}

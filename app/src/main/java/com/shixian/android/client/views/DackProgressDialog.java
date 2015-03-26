package com.shixian.android.client.views;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.shixian.android.client.R;

/**
 * Created by tangtang on 15/3/25.
 */
public class DackProgressDialog extends ProgressDialog {
    public DackProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    public DackProgressDialog(Context context) {
        super(context);
    }

    public static AlertDialog create(Context context,CharSequence message)
    {

        AlertDialog dialog = DarkAlertDialog.create(context);
        dialog.setInverseBackgroundForced(true);
        View view = LayoutInflater.from(context).inflate(R.layout.progress_dialog, null);
        ((TextView) view.findViewById(R.id.tv_loading)).setText(message);
        dialog.setView(view);
        return dialog;
    }


}

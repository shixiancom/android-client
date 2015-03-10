package com.shixian.android.client.handler.content;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.shixian.android.client.Global;
import com.shixian.android.client.R;
import com.shixian.android.client.views.CustomDialog;

/**
 * Created by tangtang on 15/3/10.
 * 用处处理文本
 */
public class ContentHandler {


    private TextView content;

    public ContentHandler(TextView tv) {
        this.content = tv;

    }

    public ContentHandler longClickCopy() {

        content.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {
                v.setBackgroundColor(Color.GRAY);
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setItems(R.array.message_action_text_copy, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                           copy(((TextView) v).getText().toString(), v.getContext());
                           Toast.makeText(v.getContext(), "已复制", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

                AlertDialog dialog = builder.show();

                dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        v.setBackgroundColor(Color.WHITE);
                    }
                });

                CustomDialog.dialogTitleLineColor(v.getContext(), dialog);
                return true;
            }
        });


        return this;

    }


    public static void copy(String content, Context context) {
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content);
    }

}
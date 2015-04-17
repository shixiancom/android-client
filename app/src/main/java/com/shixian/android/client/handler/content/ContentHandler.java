package com.shixian.android.client.handler.content;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.shixian.android.client.R;
import com.shixian.android.client.utils.MyLinkMovementMethod;
import com.shixian.android.client.views.CosmterUrlSpan;
import com.shixian.android.client.views.CustomDialog;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tangtang on 15/3/10.
 * 用处处理文本
 */
public class ContentHandler {

    private static final String TAG="ContentHandler";

    private static final int URLCOLOR=Color.parseColor("#0088cc");

    private TextView content;


    //\&%_\./-~-]*)?

    private static final Pattern URLPATTERN = Pattern
            .compile("((http://|https://|ftp://){0,1}(([0-9a-zA-Z\\-]+\\.)+(com|net|cn|me|tw|fr|tk|edu|io)[0-9a-zA-Z/\\._%\\?&=\\-#]*))|@[^\\s]+\\s");


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
                        v.setBackgroundColor(Color.TRANSPARENT);
                    }
                });

                CustomDialog.dialogTitleLineColor(v.getContext(), dialog);
                return true;
            }
        });


        return this;

    }



    public void formatColorContent(TextView tv,CharSequence s)
    {


        tv.setMovementMethod(MyLinkMovementMethod.getInstance());
        tv.setClickable(true);

        SpannableString spannableString=new SpannableString(s);
        Matcher urlMatcher= URLPATTERN.matcher(s);


        while (urlMatcher.find())
        {
            URLSpan span=new CosmterUrlSpan(urlMatcher.group(),URLCOLOR);
            spannableString.setSpan(span,urlMatcher.start(),urlMatcher.end(),Spanned.SPAN_INCLUSIVE_INCLUSIVE);
        }


        tv.setText(spannableString);




    }

    public static void copy(String content, Context context) {
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content);
    }

}
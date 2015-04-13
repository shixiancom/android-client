package com.shixian.android.client.views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.style.URLSpan;
import android.view.View;
import android.widget.Toast;

import com.shixian.android.client.activities.UserActivity;
import com.shixian.android.client.activities.WebActivity;
import com.shixian.android.client.controller.IndexOnClickController;
import com.shixian.android.client.model.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by tangtang on 15/4/1.
 */
public class CosmterUrlSpan extends URLSpan {


    private int color;

    public CosmterUrlSpan(String url, int color) {
        super(url);
        this.color = color;
    }


    public static void openActivityByUri(Context context, String uriString, boolean newTask) {
        openActivityByUri(context, uriString, newTask, true);
    }


    public static boolean openActivityByUri(Context context, String uriString, boolean newTask, boolean defaultIntent) {
        Intent intent = new Intent();
        if (newTask) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }


        if (uriString.startsWith("@")) {

            String username = uriString.substring(1).trim();

            User user = new User();
            Bundle bundle = new Bundle();
            user.username = username;
            bundle.putSerializable("user", user);

            bundle.putInt("type", IndexOnClickController.USER_FRAGMENT);
            intent.putExtras(bundle);
            intent.setClass(context, UserActivity.class);
            context.startActivity(intent);

            return true;

        }


        final String imageSting = "(http|https):.*?.[.]{1}(gif|jpg|png|bmp)";
        Pattern pattern = Pattern.compile(imageSting);
        Matcher matcher = pattern.matcher(uriString);
        if (matcher.find()) {
            //    intent.setClass(context, ImagePagerActivity_.class);
            intent.putExtra("mSingleUri", uriString);
            context.startActivity(intent);
            return true;
        }

//        // 加了自定义图片前缀的链接
//        if (uriString.indexOf(HtmlContent.TYPE_IMAGE_HEAD) == 0) {
////            String imageUrl = uriString.replaceFirst(HtmlContent.TYPE_IMAGE_HEAD, "");
////                intent.setClass(context, ImagePagerActivity_.class);
////                intent.putExtra("mSingleUri", imageUrl);
////                intent.putExtra("isPrivate", true);
////                context.startActivity(intent);
//
//            return true;
//        }

        try {
            if (defaultIntent) {
                intent = new Intent(context, WebActivity.class);

                if (newTask) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }


                intent.putExtra("url", uriString);
                context.startActivity(intent);
            }
        } catch (Exception e) {
            Toast.makeText(context, "" + uriString.toString(), Toast.LENGTH_LONG).show();
        }

        return false;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(false);
        ds.setColor(color);
    }

    @Override
    public void onClick(View widget) {
        openActivityByUri(widget.getContext(), getURL(), false);
    }


}

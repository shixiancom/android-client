package com.shixian.android.client;

import android.content.Context;
import android.test.InstrumentationTestCase;
import android.util.Log;
import android.widget.TextClock;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.shixian.android.client.contants.AppContants;
import com.shixian.android.client.engine.BootEngine;
import com.shixian.android.client.engine.CommentEngine;
import com.shixian.android.client.engine.ProjectEngine;
import com.shixian.android.client.handler.content.ContentHandler;
import com.shixian.android.client.model.BaseBoot;
import com.shixian.android.client.sina.WeiBoUtils;
import com.shixian.android.client.utils.ApiUtils;
import com.shixian.android.client.utils.JsonUtils;

import org.apache.http.Header;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tangtang on 15/3/24.
 */
public class Test extends InstrumentationTestCase {




    public  void test2()
    {
        CommentEngine.deleteComment(getInstrumentation().getContext(),"ideas","9021","27712",new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

            }
        });
    }


    public void testpostxxx()
    {
        String url=String.format("http://172.168.96.93:3000");
        ApiUtils.client.addHeader("user-agent", "android");





        String cookie=getInstrumentation().getContext().getSharedPreferences("userinfo", Context.MODE_PRIVATE).getString("cookie","");

        ApiUtils.client.addHeader("Cookie", cookie);
        ApiUtils.client.addHeader("user-agent", "android");




    }

    public void testSendMessage()
    {
        //WeiBoUtils.sendMessage(this,"你谁啊",);
    }

//    public void testFormat()
//    {
//        ContentHandler.formatColorContent(new TextView(getInstrumentation().getContext()),"你好nwww.baidu.com这好的http://www.shixian.com?action=xxx");
//    }


    public void testPattern()
    {
        Pattern URLPATTERN = Pattern
                .compile("(^(http://|ftp://|https://|www){0,1}[^\u4e00-\u9fa5\\s]*?\\.(com|net|cn|me|tw|fr)[^\u4e00-\u9fa5\\s]*)|^@(.)*\\s");

        String text="@nihao ";
        Matcher matcher=URLPATTERN.matcher(text);

        while (matcher.find())
        {
            Log.i("AAAA",text);
        }

    }


    public void testBoot()
    {
        BootEngine.getBootFeed(getInstrumentation().getContext(),new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {

                Log.i("AAAA",new String(bytes));

                List<BaseBoot> boots= JsonUtils.parseBoots(new String(bytes));


                System.out.print("xxxxx");
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                Log.i("AAAA",new String(bytes));
            }
        });
    }

}

package com.shixian.android.client.utils;

/**
 * Created by tangtang on 15/2/28.
 *
 * 由于奇葩的业务需求 我们需要去掉服务器段返回的带有倒霉的<Div></>包装的html数据
 * 因为同时要显示该死的@的颜色 所以我写了这个可怜的StringUtils
 * 这么说你不会嘲笑我把
 */
public class StringUtils {


    public static String trmDiv(String content)
    {
        if(content.startsWith("<div>")&&content.endsWith("</div>"))
        {
            return content.substring(5,content.length()-1-5);
        }


       return content;
    }
}

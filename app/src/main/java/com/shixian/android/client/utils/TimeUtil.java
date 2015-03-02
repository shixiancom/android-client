package com.shixian.android.client.utils;

import android.support.v4.util.TimeUtils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by s0ng on 2015/2/11.
 * 时间计算 计算时间差
 */
public class TimeUtil {

    public static String getDistanceTime(String createtime) {

        createtime=createtime.substring(0,19).replace("T", " ");


        String interval = null;
        try {
            SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            ParsePosition pos = new ParsePosition(0);
            Date d1 = (Date) sd.parse(createtime, pos);
            // 用现在距离1970年的时间间隔new
            // Date().getTime()减去以前的时间距离1970年的时间间隔d1.getTime()得出的就是以前的时间与现在时间的时间间隔
            long time = new Date().getTime() - d1.getTime();// 得出的时间间隔是毫秒
            if (time / 1000 < 10 && time / 1000 >= 0) {
                // 如果时间间隔小于10秒则显示“刚刚”time/10得出的时间间隔的单位是秒
                interval = "刚刚";
            } else if (time / 1000 < 60 && time / 1000 > 0) {
                // 如果时间间隔小于60秒则显示多少秒前
                int se = (int) ((time % 60000) / 1000);
                interval = se + "秒前";
            } else if (time / 60000 < 60 && time / 60000 > 0) {
                // 如果时间间隔小于60分钟则显示多少分钟前
                int m = (int) ((time % 3600000) / 60000);// 得出的时间间隔的单位是分钟
                interval = m + "分钟前";
            } else if (time / 3600000 < 24 && time / 3600000 >= 0) {
                // 如果时间间隔小于24小时则显示多少小时前
                int h = (int) (time / 3600000);// 得出的时间间隔的单位是小时
                interval = h + "小时前";
            } else if (time / 86400000 < 3 && time / 86400000 >= 0) {
                // 如果时间间隔小于3天则显示多少天前
                int h = (int) (time / 86400000);// 得出的时间间隔的单位是天
                interval = h + "天前";
            } else {
                // 大于3天，则显示正常的时间，但是不显示秒
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                ParsePosition pos2 = new ParsePosition(0);
                Date d2 = (Date) sdf.parse(createtime, pos2);
                interval = sdf.format(d2);
            }
        } catch (Exception e) {
            return createtime;
        }
        return interval;
    }





}

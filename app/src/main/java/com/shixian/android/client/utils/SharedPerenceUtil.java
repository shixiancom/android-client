package com.shixian.android.client.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.shixian.android.client.activities.AddIdeaActivity;
import com.shixian.android.client.activities.MainActivity;

import java.io.File;

/**
 * Created by s0ng on 2015/2/9.
 * 本地缓存采用缓存第一页 也放在这个类中
 */
public class SharedPerenceUtil {




    public  static void clearAllData(Context context)
    {

        context.getSharedPreferences("cachedate",context.MODE_PRIVATE).edit().remove("myproject").remove("indexdata").remove("userindexdata").remove("descoryproject").remove("news").commit();
        context.getSharedPreferences("userinfo",context.MODE_PRIVATE).edit().remove("userjson").remove("myproject").remove("cookie").commit();


    }

    public static void clearIdeaEdit(Context context, String projectid) {
        context.getSharedPreferences("editidea", Context.MODE_PRIVATE).edit().remove("content"+projectid).commit();
    }

    public static void putEditHasEdit(Context context,boolean has,String projectid)
    {
        context.getSharedPreferences("editidea",Context.MODE_PRIVATE).edit().putBoolean("hascontent+projectid",has).commit();
    }

    public static void putEditIdea(Context context,String projectid,String content)
    {
        context.getSharedPreferences("editidea", Context.MODE_PRIVATE).edit().putString("content"+projectid,content).putBoolean("hascontent+projectid",true).commit();
    }

    public static String getEditIdea(Context context,String projectid)
    {

      return   context.getSharedPreferences("editidea", Context.MODE_PRIVATE).getString("content" + projectid, "");
    }

    public static boolean hasIdeaEdit(Context context,String projectid)
    {
       return  context.getSharedPreferences("editidea",Context.MODE_PRIVATE).getBoolean("hascontent+projectid",false);
    }

    public static void putNewProject(Context context,String title ,String content)
    {
        context.getSharedPreferences("newproject",Context.MODE_PRIVATE).edit().putString("title",title).putString("content",content).putBoolean("hascontent",true).commit();

    }

    public static void putNewProject(Context context,String title ,String content,int fuzeren)
    {
        context.getSharedPreferences("newproject",Context.MODE_PRIVATE).edit().putString("title",title).putString("content",content).putBoolean("hascontent",true).putInt("fuzeren",fuzeren).commit();

    }

    public static void putNewFuzeren(Context context,int fuzeren)
    {
        context.getSharedPreferences("newproject",Context.MODE_PRIVATE).edit().putInt("fuzeren",fuzeren).commit();
    }

    public static String getNewProjectTitle(Context context)
    {
       return context.getSharedPreferences("newproject",Context.MODE_PRIVATE).getString("title","");

    }

    public static String getNewProjectContent(Context context)
    {
        return  context.getSharedPreferences("newproject",Context.MODE_PRIVATE).getString("content","");

    }

    public  static int getNewFuzeren(Context context)
    {
        return context.getSharedPreferences("newproject",Context.MODE_PRIVATE).getInt("fuzeren",0);

    }

    public static  void clearNewProject(Context context)
    {
        context.getSharedPreferences("newproject",Context.MODE_PRIVATE).edit().clear().commit();


    }

    public static boolean hasNewProject(Context context)
    {
        return context.getSharedPreferences("newproject",Context.MODE_PRIVATE).getBoolean("hascontent",false);
    }




    /** * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理 * * @param directory */
    private static void deleteFilesByDirectory(File directory) {
        if (directory != null && directory.exists() && directory.isDirectory()) {
            for (File item : directory.listFiles()) {
                item.delete();
            }
        }
    }



    //用于一些本地缓存操作的存取

    /**
     * 存入 Me信息的工具方法
     * @param context
     * @param userinfo
     */
    public static void putUserInfo(Context context,String userinfo)
    {
        context.getSharedPreferences("userinfo",context.MODE_PRIVATE).edit().putString("userjson",userinfo);

    }

    public static String getUserInfo(Context context)
    {
        return context.getSharedPreferences("userinfo",context.MODE_PRIVATE).getString("userjson","");
    }


    /**
     * 缓存我的项目数据
     */
    public static String getMyProject(Context context)
    {
        return context.getSharedPreferences("cachedate",context.MODE_PRIVATE).getString("myproject","");
    }

    /**
     * 保存我的项目数据到本地
     */
    public static void putMyProject(Context context,String json)
    {
        context.getSharedPreferences("cachedate",context.MODE_PRIVATE).edit().putString("myproject",json).commit();

    }


    /**
     * 缓存首页数据
     * @param context
     * @param json
     */
    public static void putIndexFeed(Context context,String json)
    {
        context.getSharedPreferences("cachedate",context.MODE_PRIVATE).edit().putString("indexdata",json).commit();
    }

    /**
     * 获取首页缓存数据
     * @param context
     * @return
     */
    public static String getIndexFeed(Context context)
    {
        return    context.getSharedPreferences("cachedate",context.MODE_PRIVATE).getString("indexdata","");
    }



    public static void putUserIndexFeed(Context context,String json,String userid){
        context.getSharedPreferences("cachedate",context.MODE_PRIVATE).edit().putString("userindexdata"+userid,json).commit();
    }

    /**
     * 缓存用户首页
     * @param context
     * @return
     */
    public static String getUserIndexFeed(Context context,String userid) {
        return    context.getSharedPreferences("cachedate",context.MODE_PRIVATE).getString("userindexdata"+userid,"");
    }


    /**
     * 缓存用户信息
     * @param context
     * @param json
     */
    public static void putUserIndexInfo(Context context,String json,String userid){
        context.getSharedPreferences("cachedate",context.MODE_PRIVATE).edit().putString("userifo"+userid,json).commit();
    }

    /**
     * 缓存用户首页
     * @param context
     * @return
     */
    public static String getUserIndexInfo(Context context,String userid) {
        return    context.getSharedPreferences("cachedate",context.MODE_PRIVATE).getString("userifo"+userid,"");
    }

    public static String getProjectIndexFeed(Context context, int id) {
        return context.getSharedPreferences("cachedate",context.MODE_PRIVATE).getString("projectFeed"+id,"");
    }

    public static String getProjectIndexInfo(Context context, String id) {

        return context.getSharedPreferences("cachedate",context.MODE_PRIVATE).getString("projectIndexinfo"+id,"");
    }

    public static void putProjectIndexFeed(Context context, String json,int id) {
         context.getSharedPreferences("cachedate",context.MODE_PRIVATE).edit().putString("projectFeed"+id,json).commit();
    }

    public static void putProjectIndexInfo(Context context,String json, int id) {
        context.getSharedPreferences("cachedate",context.MODE_PRIVATE).edit().putString("projectIndexinfo"+id,json).commit();
    }

    public static String getProjectDiscoryProject(Context context) {
        return context.getSharedPreferences("cachedate",context.MODE_PRIVATE).getString("descoryproject","");
    }

    public static void putProjectDiscoryProject(Context context, String json) {
        context.getSharedPreferences("cachedate",context.MODE_PRIVATE).edit().putString("descoryproject",json).commit();
    }

    public static String getNews(Context context) {

        return context.getSharedPreferences("cachedate",context.MODE_PRIVATE).getString("news","");
    }

    public static void putNews(Context context, String temp) {
        context.getSharedPreferences("cachedate",context.MODE_PRIVATE).edit().putString("news",temp).commit();
    }


    public static boolean checkNeedUpdate(Context context)
    {
        return context.getSharedPreferences("config", Context.MODE_PRIVATE).getBoolean("update",true);
    }

    public static void settingCheckUpdate(Context context,boolean isNeed)
    {
        context.getSharedPreferences("config", Context.MODE_PRIVATE).edit().putBoolean("update",isNeed).commit();

    }


}


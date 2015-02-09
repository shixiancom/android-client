package com.shixian.android.client.utils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by sllt on 15/1/30.
 */
public class ApiUtils {

    public static AsyncHttpClient client = new AsyncHttpClient();


    public static void get(String url, RequestParams params, AsyncHttpResponseHandler handler) {
           client.get(url, params, handler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler handler) {
        client.post(url, params, handler);
    }

    public static void delete(String url, AsyncHttpResponseHandler handler) {
        client.delete(url, handler);
    }

    public static void put(String url, RequestParams params, AsyncHttpResponseHandler handler) {
        client.put(url, params, handler);
    }
}

package com.shixian.android.client.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.shixian.android.client.Global;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.ByteArrayBuffer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class ImageDownload extends AsyncTask<String, Integer, Bitmap> {
	public static final String CACHE_TYPE_SOFT = "SOFT";
	public static final String CACHE_TYPE_LRU = "LRU";
	public static final String CACHE_TYPE_DIS = "DIS";

    public  boolean compress=false;

	private ImageCallback imageCallback;
	private Object tag;

    public static HttpClient client=new DefaultHttpClient();


    public ImageDownload(ImageCallback imageCallback) {
        this.imageCallback = imageCallback;
        this.compress=false;

    }

	public ImageDownload(ImageCallback imageCallback,boolean compress) {
		this.imageCallback = imageCallback;
        this.compress=compress;

	}

	@Override
	protected Bitmap doInBackground(String... params) {
        Bitmap bitmap=null;
        if(compress)
        {
          bitmap=loadImageFromUrlCompree(params[0]);
        }else {
            bitmap = loadImageFromUrl(params[0]);
        }

		if (bitmap != null) {
			tag = params[1];
			String flag = CACHE_TYPE_SOFT;
			try {
				flag = params[2];
			} catch (Exception e) {
			}
			if (CACHE_TYPE_LRU.equals(flag)) {
				ImageCache.getInstance().put(tag, bitmap);
			} else if (CACHE_TYPE_DIS.equals(flag)) {

			} else {
				Global.IMGCACHE.put(tag, bitmap);
			}

		}
		return bitmap;
	}

	@Override
	protected void onPostExecute(Bitmap result) {
		if (imageCallback != null)
			imageCallback.imageLoaded(result, tag);
		super.onPostExecute(result);
	}

	public static Bitmap loadImageFromUrl(String url) {
		// url=StringUtils.replace(url, "10.0.2.2", "192.168.1.101");
		InputStream i = null;
		try {


			i = loadImg(url);

			// http://blog.csdn.net/xianming01/article/details/8280434
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inPreferredConfig = Bitmap.Config.RGB_565;
//			opt.inPurgeable = true;
//			opt.inInputShareable = true;

            opt.inJustDecodeBounds = false;





            return BitmapFactory.decodeStream(i, null, opt);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

    public static Bitmap loadImageFromUrlCompree(String url) {
        // url=StringUtils.replace(url, "10.0.2.2", "192.168.1.101");
        InputStream i = null;
        try {


            i = loadImg(url);

            // http://blog.csdn.net/xianming01/article/details/8280434




            ByteArrayOutputStream bos=new ByteArrayOutputStream();

            int len=0;
            byte bytebuff[]=new byte[4096];
           while((len=i.read(bytebuff,0,4096))!=-1)
           {
               bos.write(bytebuff,0,len);
           }

            byte byteArrray[]=bos.toByteArray();

            BitmapFactory.Options opt = new BitmapFactory.Options();

            opt.inJustDecodeBounds = true;

            BitmapFactory.decodeByteArray(byteArrray,0,byteArrray.length,opt);


            int w = opt.outWidth;
            int h = opt.outHeight;


            //200dp
            int ww=Global.iv_conte_size;

            int be = opt.outWidth/ww;

            if (be <= 0)
                be = 1;
            opt.inSampleSize = be;//设置采样率


            opt.inPurgeable = true;// 同时设置才会有效
            opt.inInputShareable = true;//。当系统内存不够时候图片自动被回收
            opt.inPreferredConfig = Bitmap.Config.RGB_565;


            opt.inJustDecodeBounds = false;

             Bitmap bt=BitmapFactory.decodeByteArray(byteArrray,0,byteArrray.length,opt);
            return bt;


        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static InputStream loadImg(String uri) {
        HttpGet get = new HttpGet(uri);
        HttpResponse response;

        HttpParams httpParams = new BasicHttpParams();//
        HttpConnectionParams.setConnectionTimeout(httpParams, 8000);
        HttpConnectionParams.setSoTimeout(httpParams, 8000);
        get.setParams(httpParams);

        try {
            response = client.execute(get);

            if (response.getStatusLine().getStatusCode() == 200) {
                return response.getEntity().getContent();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }




}

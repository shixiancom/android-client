package com.shixian.android.client.utils;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.shixian.android.client.R;

/**
 * Created by s0ng on 2015/2/11.
 */
public class ImageUtil {

    public static  void loadingImage(ImageView iv,Bitmap defaultImage,ImageCallback callback,String key,String url){
        //头像图片处理


        Bitmap bm = ImageCache.getInstance().get(key);
//

        if (bm != null) {
            iv.setImageBitmap(bm);
        } else {
            iv.setImageBitmap(defaultImage);
            iv.setTag(key);
            if (callback != null) {
                new ImageDownload(callback,true).execute(url, key, ImageDownload.CACHE_TYPE_LRU);
            }
        }
    }
}


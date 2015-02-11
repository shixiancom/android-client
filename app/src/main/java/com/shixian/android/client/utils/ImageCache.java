package com.shixian.android.client.utils;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.shixian.android.client.Global;
import com.shixian.android.client.contants.AppContants;

import java.io.File;

public class ImageCache {
	private static final int MAXSIZE = 1024 * 1024 * 5;// 5M
	private static final int DIS_CACHE_SIZE = 1024 * 1024 * 10; // 10MB占据SDCard的空间
	protected static final String TAG = "ImageCache";
	private static ImageCache cache = new ImageCache();

	public static ImageCache getInstance() {
		return cache;
	}

	private LruCache<Object, Bitmap> lrucache;// 图片的缓存;设置的Value必须能够计算出所占有的内存的大小
	private DiskLruCache diskLruCache;// 将图片缓存在SDCard上

	private ImageCache() {
		// put(key ,object) size+1——集合中所存元素的个数
		// 当向集合中添加一个Bitmap的时候，能够获知Bitmap所占用的内存大小
		// maxSize代表当前分配给Bitmap的集合的内存大小

		lrucache = new LruCache<Object, Bitmap>(MAXSIZE) {

			@Override
			protected int sizeOf(Object key, Bitmap value) {
				// 每添加一张图片，size的变动代表该bitmap占用的内存大小
				// Log.i(TAG, key.toString());
				return getSize(value);
			}

			@Override
			protected void entryRemoved(boolean evicted, Object key,
					Bitmap oldValue, Bitmap newValue) {
				// evicted 如果为True所代表：MAXSIZE不够用
				if (evicted) {
					// MAXSIZE不够用
					Log.i(TAG, "remove:" + key.toString());
					// 两种缓存的结合使用
					// 如果内存空间充值：可以将被清楚的bitmap对象存入软引用的集合
					// GloableParams.IMGCACHE.put(key, oldValue);
				}
				super.entryRemoved(evicted, key, oldValue, newValue);
			}

		};// 就只能放5M的图片

		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			File externalStorageDirectory = Environment
					.getExternalStorageDirectory();
			String path = externalStorageDirectory.getAbsolutePath()
					+ AppContants.IMAGE_PATH;
			diskLruCache = DiskLruCache.openCache(Global.MAIN, new File(
					path), DIS_CACHE_SIZE);
		}

	}

	private int getSize(Bitmap value) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
			return value.getByteCount();
		}
		// Pre HC-MR1
		return value.getRowBytes() * value.getHeight();
	}

	public void put(Object key, Bitmap value) {
		// 如果一旦添加的Bitmap所占据的总的内存超过了MAXSIZE，移除一部分不常使用的map
		lrucache.put(key, value);

		if (diskLruCache != null) {
			diskLruCache.put(key.toString(), value);
		}
	}

	public Bitmap get(Object key) {
		// 获取硬引用图片
		Bitmap bitmap = lrucache.get(key);

		// 如果没有
		if (bitmap == null) {
			// 读取SDCard中得资源
			if (diskLruCache != null) {
				bitmap = diskLruCache.get(key.toString());
			}
		}

		return bitmap;
	}

	public void clear() {
		lrucache.evictAll();
		if (diskLruCache != null) {
			diskLruCache.clearCache();
		}

	}
}

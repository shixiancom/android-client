package com.shixian.android.client.utils;

import android.graphics.Bitmap;
/**
 * 图片下载完成的监听
 * @author Administrator
 *
 */
public interface ImageCallback {
	/**
	 * 监听处理
	 * @param bitmap：图片资源
	 * @param tag：操作的ImageView 的 标记
	 */
	void imageLoaded(Bitmap bitmap, Object tag);


}

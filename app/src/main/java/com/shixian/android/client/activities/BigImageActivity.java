/**
 *ImgDisplayActivity.java
 *2011-11-20 上午11:10:04
 *Touch Android
 *http://bbs.droidstouch.com
 */
package com.shixian.android.client.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.shixian.android.client.R;
import com.shixian.android.client.contants.AppContants;
import com.shixian.android.client.utils.ImageCache;

import java.io.File;
import java.io.FileOutputStream;


public class BigImageActivity extends Activity
{

	
	
	private ImageView imgDisplay;
//	private Button btnZoomin;
//	private Button btnZoomout;
  private LinearLayout LLayoutDisplay;
  private LinearLayout lLayoutDisplay;
  private Bitmap bitmap;
  private Button bt_save;
  
  private int imgId=0;
  
  private double scale_in=0.8;// 缩小的比例
  private double scale_out=1.25;// 放大的比例
  
  private float scaleWidth=1;
  private float scaleHeight=1;
	
  
  
  private final static int NONE=0;
  private final static int DRAG=1;
  private final static int ZOOM=2;
  private int mode=NONE;
  
  private Matrix matrix;
  private Matrix currMatrix;
  
  private PointF starPoint;
  private PointF midPointF;
  
  private float startDistance;


    /**
     * 用于处理双击事件
     */
  private boolean zoomOutAble=true;
  private long[] clicktimes=new long[2];
  private int clicktimeIndex=0;

  
  
	protected void onCreate(Bundle savedInstanceState)
	{
		
		super.onCreate(savedInstanceState);
        setContentView(R.layout.display_img);

		
		lLayoutDisplay = (LinearLayout) this.findViewById(R.id.llayout_img_display);
		LLayoutDisplay = (LinearLayout) this.findViewById(R.id.flayout_img_display);
		imgDisplay = (ImageView) this.findViewById(R.id.img_display);
        bt_save= (Button) findViewById(R.id.bt_save);
		/*btnZoomin = (Button) this.findViewById(R.id.btn_zoomin);
		btnZoomout = (Button) this.findViewById(R.id.btn_zoomout);*/
		
		
		matrix = new Matrix(); // 保存拖拽变化
		currMatrix = new Matrix(); // 保存当前的
		
		starPoint = new PointF(); // 开始点
		
		
/*		btnZoomout.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				zoomOut();				
			}
		});
		
		btnZoomin.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				zoomin();				
			}
		});*/
		
		
		imgDisplay.setOnTouchListener(new ImageViewOnTouchListener());



        final String key=getIntent().getStringExtra("key");
        final Bitmap bt= ImageCache.getInstance().get(key);


        if(bt!=null) {

            imgDisplay.setImageBitmap(bt);
        }else{
            bitmap=BitmapFactory.decodeResource(getResources(),R.drawable.login_icon);
        }



        //目前双击事件设置为不响应
       imgDisplay.setOnClickListener(new DoubleOnClickListener());



        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                   File externalStorageDirectory = Environment
                           .getExternalStorageDirectory();
                   String path = externalStorageDirectory.getAbsolutePath()
                           + "/SHIXIAN";

                   saveBitmap(path,bt,key);



            }
        });
		
	}
	
	
	
	private void zoomin()
	{
		
		reSizeBmp(scale_in);
        zoomOutAble=true;
	}
	
	/**
	 * 
	 */
			
	private void zoomOut()
	{
        zoomOutAble=false;
		
		reSizeBmp(scale_out);
		
//		/*btnZoomin.setEnabled(true);*/
		//int bmpWidth = bitmap.getWidth();
		//int bmtHeight = bitmap.getHeight();
			
		
	
		/*Bitmap reSizeBmp=Bitmap.createBitmap(bitmap, 0, 0, bmpWidth, bmtHeight, matrix, true);
		
		
		
		//把旧的ImageView移掉
		if(imgId==0)
		{
			lLayoutDisplay.removeView(imgDisplay);
		}
		else {
			lLayoutDisplay.removeView(findViewById(imgId));
		}
		
		imgId ++;
		ImageView imageView = new ImageView(this);
		imageView.setImageBitmap(reSizeBmp);
		imageView.setId(imgId);
		
		lLayoutDisplay.addView(imageView);
		
		setContentView(fLayoutDisplay);
		*/
		
	}
	


	
	
	private void reSizeBmp(double scale)
	{
		
		scaleWidth = (float) (scaleWidth * scale);
		scaleHeight = (float) (scaleHeight * scale);
	
		// 重新生成一个放大/缩小 后图片
		
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		
		imgDisplay.setImageMatrix(matrix);
	}


    final class DoubleOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            clicktimes[clicktimeIndex]= SystemClock.currentThreadTimeMillis();

            //双击事件
            if(clicktimes[clicktimeIndex]-clicktimes[clicktimeIndex=clicktimeIndex++%2]>500)
            {
                if(zoomOutAble)
                {
                    zoomOut();
                }else{
                    zoomin();
                }
            }

            clicktimeIndex=clicktimeIndex++%2;

        }
    }
	
	
	
	
	final class ImageViewOnTouchListener implements OnTouchListener
	{

		public boolean onTouch(View v, MotionEvent event)
		{
			
			
			
			switch (event.getAction() & MotionEvent.ACTION_MASK)
			{
				case MotionEvent.ACTION_DOWN:  // 单只手指按下

					currMatrix.set(matrix);
					starPoint.set(event.getX(), event.getY());
				
					mode = DRAG;
				break;
				
				case MotionEvent.ACTION_POINTER_DOWN: // 如果有一只手指按下了屏幕，后续在右手指按下屏幕的时候，就会触发这个事件
				{
					startDistance =distance(event);
					
					if(startDistance > 5f) // 2手指之间的距离像素如果大于5，那我则认为是多点
					{
						mode = ZOOM;
						currMatrix.set(matrix);
						
						midPointF = getMidPoint(event);
						
					}
					
					break;
				}
				
				case MotionEvent.ACTION_MOVE:
				{
					
					if(mode == DRAG) // 拖拽模式
					{
						
						float dx = event.getX() -  starPoint.x;
						float dy = event.getY() - starPoint.y;
						matrix.set(currMatrix);
						
						matrix.postTranslate(dx, dy);// 移动到指定点
						
					}
					
					else if(mode == ZOOM)
					{
						
						float distance = distance(event);
						
						if(distance > 5f)
						{
							matrix.set(currMatrix);
							float  cale =  distance / startDistance;
							
							matrix.preScale(cale, cale, midPointF.x, midPointF.y);
						}
						
					}
					break;
				}
				
				
				case MotionEvent.ACTION_UP: // 最后一只手机离开屏幕后触发此事件
				case MotionEvent.ACTION_POINTER_UP: // 一只手指离开屏幕，但还有一只手指在屏幕上，会触发此事件
					mode = NONE;
				break;
				
			default:
				break;
			}
			
			
	   	imgDisplay.setImageMatrix(matrix);
			
			return false;
		}

		/**
		 * @param event
		 * @return
		 */
		private PointF getMidPoint(MotionEvent event)
		{
			
			float x = (event.getX(1) - event.getX(0)) / 2;
			float y = (event.getY(1) - event.getY(0)) / 2;
			
			return new PointF(x, y);
		}
		
		
	}
	
	
	
	private float distance(MotionEvent event)
	{
		
		float eX = event.getX(1) - event.getX(0);
		float eY = event.getY(1) - event.getY(0);
		
		return FloatMath.sqrt(eX * eX + eY * eY);
		
	}


    public void saveBitmap(final String path,final Bitmap bm, final String fileName) {


        new Thread(new Runnable() {
            @Override
            public void run() {
                File f = new File(path);

                if(!f.exists())
                {
                    f.mkdirs();
                }

                File picture=new File(f,fileName);

                if (picture.exists()) {
                    picture.delete();
                }
                try {
                    FileOutputStream out = new FileOutputStream(picture);
                    bm.compress(Bitmap.CompressFormat.PNG, 90, out);
                    out.flush();
                    out.close();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(BigImageActivity.this,"保存成功 "+path+"/"+fileName,Toast.LENGTH_SHORT).show();
                        }
                    });

                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }).start();



    }
	
}

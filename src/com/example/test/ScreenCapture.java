package com.example.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

public class ScreenCapture {

	private final String path = "/test/image/";
		
	public Bitmap getBitmapFromActivity(Activity activity) {
		if (activity==null) {
			Log.e(Test.TAG, "getBitmapFromActivity input null");
			return null;
		}
		WindowManager windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
		View decorview = activity.getWindow().getDecorView();

//		Point outSize = new Point();
//		windowManager.getDefaultDisplay().getSize(outSize);
//		Log.d(Test.TAG, String.format("size:%d;%d", outSize.x, outSize.y));
//		Bitmap bmp = Bitmap.createBitmap(outSize.x, outSize.y, Config.ARGB_8888);
//		decorview.setDrawingCacheEnabled(true);
//		bmp = decorview.getDrawingCache();
		
		decorview.buildDrawingCache(true);
		Bitmap bmp = decorview.getDrawingCache(true).copy(Config.ARGB_8888, false);
		decorview.destroyDrawingCache();
		return bmp;
	}
	
	public Bitmap getBitmapFromView(View view) {
		Log.d(Test.TAG, String.format("x:%d,y:%d", view.getWidth(), view.getHeight()));
		if ((view.getWidth()<=0)||(view.getHeight()<=0)) {
			Log.e(Test.TAG, "get bitmap from view error!");
			return null;
		}
		//Define a bitmap with the same size as the view
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null) 
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else 
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        return returnedBitmap;
	}
	
	public void saveBmp2png(Bitmap bmp, String fileName) {
		if (!fileName.endsWith(".png")) {
			fileName += ".png";
		}
		String absoluteName = Environment.getExternalStorageDirectory() + path + fileName;
		File file = new File(absoluteName);
		Log.d(Test.TAG, "path: " + file.getParentFile());
		if (!file.getParentFile().exists()) {
			Log.w(Test.TAG, "path is not exit");
			file.getParentFile().mkdirs();
		}
		try {
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.PNG, 90, fos);
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void saveActivity2png(Activity activity) {
		save2png(activity);
	}
	public void saveView2png(View view) {
		save2png(view);
	}
	
	/**
	 * 保存Activity或View的图像
	 * @param obj Activiy or View
	 */
	private void save2png(Object obj) {
		Bitmap bmp;
		if (obj instanceof Activity) {
			Log.d(Test.TAG, "save2png input a Activity");
			bmp = getBitmapFromActivity((Activity) obj);
		} else if (obj instanceof View){
			Log.d(Test.TAG, "save2png input a View");
			bmp = getBitmapFromView((View) obj);
		} else {
			Log.e(Test.TAG, "save2png is not Activity or View");
			return;
		}
		if (bmp==null) {
			Log.e(Test.TAG, "getBitmap error!");
			return;
		}
		Time now = new Time();
		now.setToNow();
		String name = now.format2445();
		Log.d(Test.TAG, "current time:" + name);
		saveBmp2png(bmp, name);
	}
}

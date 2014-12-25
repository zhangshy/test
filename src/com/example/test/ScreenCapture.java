package com.example.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Point;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

public class ScreenCapture {

	private Context mContext = null;
	private Activity mActivity = null;
	private String path = "/test/image/";
	
	public ScreenCapture(Activity activity) {
		mActivity = activity;
		mContext = activity.getApplicationContext();
	}
	
	public void save2Pic() {
		if (mContext==null) {
			Log.e(Test.TAG, "ScreenCapture context is null");
			return;
		}
		WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		Point outSize = new Point();
		windowManager.getDefaultDisplay().getSize(outSize);
		Log.d(Test.TAG, String.format("size:%d;%d", outSize.x, outSize.y));
		Bitmap bmp = Bitmap.createBitmap(outSize.x, outSize.y, Config.ARGB_8888);
		
		View decorview = mActivity.getWindow().getDecorView();
		decorview.setDrawingCacheEnabled(true);
		bmp = decorview.getDrawingCache();
		
		Time now = new Time();
		now.setToNow();
		String name = now.format2445();
		Log.d(Test.TAG, "current time:" + name);
		path = Environment.getExternalStorageDirectory() + path + name + ".png";
		File file = new File(path);
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
}

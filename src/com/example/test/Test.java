package com.example.test;

import java.util.List;

import android.app.Activity;
import android.app.ActionBar;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import android.os.Build;

public class Test extends Activity implements View.OnClickListener{

	public static final String TAG = "TestApk";
	public static final int SHOWINFO = 101;
	public static final int SCREENCAPTURE = 102;
	
	private Button btnGetBoxinfo;
	private Button btnRelase;
	private Button btnCapture;
	private MyHandler mHandler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		btnGetBoxinfo = (Button) findViewById(R.id.btn_box_info);
		btnGetBoxinfo.setOnClickListener(this);
		btnRelase = (Button) findViewById(R.id.btn_relase);
		btnRelase.setOnClickListener(this);
		btnCapture = (Button) findViewById(R.id.btn_capture);
		btnCapture.setOnClickListener(this);
		mHandler = new MyHandler();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.btn_box_info:
			mHandler.removeMessages(SHOWINFO);
			mHandler.sendEmptyMessage(SHOWINFO);
			break;
		case R.id.btn_relase:
			new ReleaseTask(Test.this).execute();
			break;
		case R.id.btn_capture:
			mHandler.removeMessages(SCREENCAPTURE);
			mHandler.sendEmptyMessage(SCREENCAPTURE);
			break;
		}
	}

	class MyHandler extends Handler {

		public void handleMessage(Message msg) {
			switch(msg.what) {
			case SHOWINFO:
				Boxinfo boxinfo = new Boxinfo();
				boxinfo.logAndroidBuild();
				boxinfo.logWifiInfo(getApplicationContext());
				boxinfo.logNetworkInfo();
				long storageBytes = boxinfo.getStorageBytes();
				int memSize = boxinfo.getMemSizeKB();
				StringBuilder info = new StringBuilder();
				info.append("型号：" + Build.MODEL + "\n");
				info.append("cpu: " + Build.CPU_ABI + " " + boxinfo.getcpuNum() + "核\n");
				info.append("有线mac: " + boxinfo.getMACAddress("eth0") + "\n");
				info.append("无线mac: " + boxinfo.getMACAddress("wlan0") + "\n");
				info.append("蓝牙mac: " + boxinfo.getBluetoothMac() + "\n");
				info.append("系统版本：" + Build.VERSION.RELEASE + "\n");
				info.append("内存：" + String.format("%.2f", memSize/1024/1024.0) + "G\n");
				info.append("存储：" + String.format("%.2f", storageBytes/1024/1024/1024.0) + "G\n");
				AlertDialog.Builder builder = new AlertDialog.Builder(Test.this)
					.setTitle("设备信息")
					.setMessage(info.toString())
					.setPositiveButton("确定", new OnClickListener() {
						public void onClick(DialogInterface dialog, int arg1) {
							dialog.dismiss();
						}
					});
				AlertDialog alertDialog = builder.create();
				//可用于server显示dialog，需要SYSTEM_ALERT_WINDOW和SYSTEM_OVERLAY_WINDOW权限
				//alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);	
				alertDialog.show();
//				new AlertDialog.Builder(Test.this)
//					.setTitle("设备信息")
//					.setMessage(info.toString())
//					.setPositiveButton("确定", new OnClickListener() {
//						public void onClick(DialogInterface dialog, int arg1) {
//							dialog.dismiss();
//						}
//					})
//					.show();
				break;
			case SCREENCAPTURE:
				new ScreenCapture().saveActivity2png(Test.this);
				Toast.makeText(Test.this, "截屏成功", Toast.LENGTH_SHORT).show();
				break;
			}
		}
		
	}	
	
}

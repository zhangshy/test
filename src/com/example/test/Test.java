package com.example.test;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.os.Build;

public class Test extends Activity implements View.OnClickListener{

	public static final String TAG = "TestApk";
	public static final int SHOWINFO = 101;
	
	public static final String STORAGEBYTES = "storagesize";
	public static final String MEMSIZEKB = "memsize";
	
	private Button btnGetBoxinfo;
	private MyHandler mHandler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		btnGetBoxinfo = (Button) findViewById(R.id.get_box_info);
		btnGetBoxinfo.setOnClickListener(this);
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
		case R.id.get_box_info:
			Boxinfo boxinfo = new Boxinfo();
			boxinfo.logAndroidBuild();
			long storageBytes = boxinfo.getStorageBytes();
			int memSize = boxinfo.getMemSizeKB();
			boxinfo.logWifiInfo(getApplicationContext());
			boxinfo.logNetworkInfo();
			Message msg = mHandler.obtainMessage();
			msg.what = SHOWINFO;
			Bundle bundle = new Bundle();
			bundle.putInt(MEMSIZEKB, memSize);
			bundle.putLong(STORAGEBYTES, storageBytes);
			msg.setData(bundle);
			mHandler.removeMessages(SHOWINFO);
			mHandler.sendMessage(msg);
			break;
		}
	}

	class MyHandler extends Handler {

		public void handleMessage(Message msg) {
			switch(msg.what) {
			case SHOWINFO:
				long storageBytes = msg.getData().getLong(STORAGEBYTES);
				int memSize = msg.getData().getInt(MEMSIZEKB);
				StringBuilder info = new StringBuilder();
				info.append("内存：" + String.format("%.2f", memSize/1024/1024.0) + "G\n");
				info.append("存储：" + String.format("%.2f", storageBytes/1024/1024/1024.0) + "G\n");
				new AlertDialog.Builder(Test.this)
					.setTitle("设备信息")
					.setMessage(info.toString())
					.setPositiveButton("确定", new OnClickListener() {
						public void onClick(DialogInterface dialog, int arg1) {
							dialog.dismiss();
						}
					})
					.show();
				break;
			}
		}
		
	}

}

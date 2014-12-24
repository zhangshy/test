package com.example.test;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;


class ReleaseTask extends AsyncTask<String, String, Void> {
	ProgressDialog progressDialog = null;
	private Activity mActivity;
	
	public ReleaseTask(Activity activity) {
		mActivity = activity;
	}
	
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = new ProgressDialog(mActivity);
		// 设置进度条风格，风格为圆形，旋转的  
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // 设置ProgressDialog 标题  
        progressDialog.setTitle(mActivity.getString(R.string.release));
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface paramDialogInterface, int paramInt) {
				Log.d(Test.TAG, "progressDialog cancell button");
				ReleaseTask.this.cancel(true);
			}
		});
        progressDialog.show();
	}

	protected Void doInBackground(String... paramVarArgs) {
		ActivityManager activityManager = (ActivityManager) mActivity.getSystemService(mActivity.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> infos = activityManager.getRunningAppProcesses();
		for (RunningAppProcessInfo info : infos) {
			if (isCancelled()) {
				Log.d(Test.TAG, "ReleaseTask is cancelled");
				return null;
			}
			Log.d(Test.TAG, "pid: " + info.pid);
			Log.d(Test.TAG, "processName: " + info.processName);
			Log.d(Test.TAG, "importance: " + info.importance);
			if (info.importance>ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE) {
				String[] pkgs = info.pkgList;
				for (String pkg : pkgs) {
					Log.d(Test.TAG, "pkg:" + pkg);
					publishProgress("释放" + pkg);
					activityManager.killBackgroundProcesses(pkg);
				}
			}
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(String... values) {
		super.onProgressUpdate(values);
		progressDialog.setMessage(values[0]);
	}

	@Override
	protected void onCancelled() {
		Log.d(Test.TAG, "ReleaseTask onCancelled");
		super.onCancelled();
		progressDialog.cancel();
	}

	@Override
	protected void onPostExecute(Void result) {
		Log.d(Test.TAG, "ReleaseTask onPostExecute");
		super.onPostExecute(result);
		progressDialog.cancel();
	}
	
}

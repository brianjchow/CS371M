package com.example.uis;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ActivityWaitForCxn extends ActionBarActivity {

	private static final String TAG = "ActivityWaitForCxn";
	private static final int TIMEOUT_AFTER = 60000;			// milliseconds
	
	private Context mContext;
	private boolean mHasCxn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wait_for_cxn);
		
		mContext = this;
		mHasCxn = false;
		
		if (is_connected_wifi_or_mobile()) {
			Log.d(TAG, "ERROR: entered ActivityWaitForCxn with active wifi/mobile connection");
			
			mHasCxn = true;
			start_loading_csv();
		}
		
		final WaitTask wait_task = new WaitTask();
		wait_task.execute();
		
		// http://stackoverflow.com/questions/7882739/android-setting-a-timeout-for-an-asynctask
		Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				if (wait_task.getStatus() == AsyncTask.Status.RUNNING) {
					wait_task.cancel(true);
				}
			}
			
		}, TIMEOUT_AFTER);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// register BroadcastReceiver
		Log.d(TAG, "Registering broadcast receiver, onResume()");
		IntentFilter intent_filter = new IntentFilter();
		intent_filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);	// SUPPLICANT_CONNECTION_CHANGE_ACTION
		registerReceiver(broadcast_receiver, intent_filter);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		// unregister BroadcastReceiver
		Log.d(TAG, "Unregistering broadcast receiver, onResume()");
		unregisterReceiver(broadcast_receiver);
	}
	
	private BroadcastReceiver broadcast_receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// http://stackoverflow.com/questions/5888502/how-to-detect-when-wifi-connection-has-been-established-in-android?rq=1
			
			final String action = intent.getAction();
			if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {	// SUPPLICANT_CONNECTION_CHANGE_ACTION
				Log.d(TAG, "Wifi state changed to enabled, onReceive()");
//				Toast.makeText(ActivityWaitForCxn.this, "Wifi state changed to enabled, ActivityWaitForCxn", Toast.LENGTH_SHORT).show();
				
				mHasCxn = true;
			}
			else {
				Log.d(TAG, "Wifi state changed to disabled, onReceive()");
//				Toast.makeText(ActivityWaitForCxn.this, "Wifi state changed to disabled, ActivityWaitForCxn", Toast.LENGTH_SHORT).show();
				
				mHasCxn = false;
			}
		}
	};
	
	private boolean is_connected_wifi_or_mobile() {
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo net_info = manager.getActiveNetworkInfo();
		if (net_info != null) {
			
//			if ((net_info.getType() == ConnectivityManager.TYPE_WIFI || net_info.getType() == ConnectivityManager.TYPE_MOBILE) && net_info.isConnected()) {
//				if (Constants.DEBUG) {
//					if (net_info.getType() == ConnectivityManager.TYPE_WIFI) {
//						Log.d(TAG, "Wifi cxn enabled and connected on startup, onCreate(), LoadCSV");
//					}
//					else {
//						Log.d(TAG, "Mobile cxn enabled and connected on startup, onCreate(), LoadCSV");
//					}
//				}
//				return true;
//			}
			
			if (net_info.getType() == ConnectivityManager.TYPE_WIFI && net_info.isConnected()) {
				Log.d(TAG, "Wifi cxn enabled and connected on startup, onCreate(), LoadCSV");
				return true;
			}
			else if (net_info.getType() == ConnectivityManager.TYPE_MOBILE && net_info.isConnected()) {
				Log.d(TAG, "Mobile cxn enabled and connected on startup, onCreate(), LoadCSV");
				return true;		
			}
		}
		
		return false;
	}

//	private boolean is_connected_wifi() {
//		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//		NetworkInfo net_info = manager.getActiveNetworkInfo();
//		if (net_info != null && net_info.getType() == ConnectivityManager.TYPE_WIFI && net_info.isConnected()) {
//			return true;
//		}
//		return false;
//	}
//	
//	private boolean is_connected_mobile() {
//		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//		NetworkInfo net_info = manager.getActiveNetworkInfo();
//		if (net_info != null && net_info.getType() == ConnectivityManager.TYPE_MOBILE && net_info.isConnected()) {
//			return true;
//		}
//		return false;
//	}
	
	private class WaitTask extends AsyncTask<Void, Void, Boolean> {
		
		protected Boolean doInBackground(Void... args) {
			boolean result = false;
//			double time_elapsed = 0;
//			
//			Stopwatch stopwatch = new Stopwatch();
//			stopwatch.start();
//			
//			while ((time_elapsed = stopwatch.time()) < TIMEOUT_AFTER && !mHasCxn) {
//				// wait
//			}
//			
//			stopwatch.stop();
			
			while (!mHasCxn) {
				// do nothing; refer to timeout code in onCreate()
				
				if (isCancelled()) {
					return false;
				}
			}
			
			if (mHasCxn) {		// redundant
				Log.d(TAG, "Cxn established, transferring to ActivityLoadCSV...");
				result = true;
			}
			
			return result;
		}
		
		@Override
		protected void onCancelled(Boolean result) {
			Log.d(TAG, "AsyncTask was cancelled, checking results");
			if (result.equals(Boolean.valueOf(true))) {
				start_loading_csv();
			}
			else {
				show_failure_dialog();
			}
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			if (result.equals(Boolean.valueOf(true))) {
				start_loading_csv();
			}
			else {
				show_failure_dialog();
			}
		}
	}
	
	private void start_loading_csv() {
		startActivity(new Intent(ActivityWaitForCxn.this, ActivityLoadCSV.class));
		finish();
	}

	private void show_failure_dialog() {
		final Dialog dialog = new Dialog(ActivityWaitForCxn.this);
		dialog.setTitle("Wait for connection timeout");
		dialog.setContentView(R.layout.load_csv_failure_dialog);
		dialog.setCancelable(false);
		
		TextView err_msg = (TextView) findViewById(R.id.error_msg);
		err_msg.setText("Timeout period exceeded.\n\nRestart/abort?");
		
		dialog.findViewById(R.id.continue_button).setVisibility(View.GONE);
		
//		Button continue_button = (Button) dialog.findViewById(R.id.continue_button);
		Button restart_button = (Button) dialog.findViewById(R.id.restart_button);
		Button abort_button = (Button) dialog.findViewById(R.id.abort_button);
		
//		continue_button.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				Log.d(TAG, "Failed to complete reading CSV, now entering MainActivity...");
//				startActivity(new Intent(mContext, ActivityMain.class));
//				finish();
//			}
//		});

		restart_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d(TAG, "Timed out while waiting for cxn establishment, now restarting app...");
				startActivity(new Intent(mContext, ActivityLoadCSV.class));
				finish();
			}
		});

		abort_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d(TAG, "Timed out while waiting for cxn establishment, now aborting...");
				finish();
			}
		});
		
		dialog.show();
	}
		
	
	
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_wait_for_cxn, menu);
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
}

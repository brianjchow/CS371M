package com.example.uis;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
	
	private static final boolean INFINITE_TIMEOUT = true;
	private static final int TIMEOUT_AFTER = 60000;			// milliseconds
	
	private Context mContext;
	private boolean mHasCxn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wait_for_cxn);
		
		mContext = this;
		mHasCxn = false;

		final WaitTask wait_task = new WaitTask();
		wait_task.execute();
		
		if (!INFINITE_TIMEOUT) {
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
				Log.d(TAG, "Network state just changed");
				
				NetworkInfo net_info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				if (net_info != null) {
					
//					if ((net_info.getType() == ConnectivityManager.TYPE_WIFI || net_info.getType() == ConnectivityManager.TYPE_MOBILE) && net_info.isConnected()) {
//						if (Constants.DEBUG) {
//							if (net_info.getType() == ConnectivityManager.TYPE_WIFI) {
//								Log.d(TAG, "Wifi cxn enabled and connected on startup, onCreate(), LoadCSV");
//							}
//							else {
//								Log.d(TAG, "Mobile cxn enabled and connected on startup, onCreate(), LoadCSV");
//							}
//						}
//						return true;
//					}
					
					if (net_info.getState().equals(NetworkInfo.State.CONNECTED)) {
						Log.d(TAG, "Network connectivity just enabled");
						mHasCxn = true;
					}
					else {
						mHasCxn = false;
					}
					
//					if (net_info.getType() == ConnectivityManager.TYPE_WIFI && net_info.isConnected()) {
//						Log.d(TAG, "Wifi cxn just enabled");
//						mHasCxn = true;
//					}
//					else if (net_info.getType() == ConnectivityManager.TYPE_MOBILE && net_info.isConnected()) {
//						Log.d(TAG, "Mobile cxn just enabled");
//						mHasCxn = true;
//					}
//					else {
//						mHasCxn = false;
//					}
				}
				else {
					Log.d(TAG, "Net info null");
					mHasCxn = false;
				}
				
//				boolean no_cxn = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
//				if (no_cxn) {
//					mHasCxn = false;
//				}
//				else {
//					mHasCxn = true;
//				}
				
//				Log.d(TAG, "Wifi state changed to enabled, onReceive()");
////				Toast.makeText(ActivityWaitForCxn.this, "Wifi state changed to enabled, ActivityWaitForCxn", Toast.LENGTH_SHORT).show();
//				
//				mHasCxn = true;
			}
//			else {
//				Log.d(TAG, "Wifi state changed to disabled, onReceive()");
////				Toast.makeText(ActivityWaitForCxn.this, "Wifi state changed to disabled, ActivityWaitForCxn", Toast.LENGTH_SHORT).show();
//				
//				mHasCxn = false;
//			}
		}
	};

	private class WaitTask extends AsyncTask<Void, Void, Boolean> {
		
		protected Boolean doInBackground(Void... args) {
			Boolean result = Boolean.valueOf(false);

			while (!mHasCxn) {
				// do nothing; refer to timeout code in onCreate()
				
				if (isCancelled()) {
					return Boolean.valueOf(false);
				}
			}
			
			if (mHasCxn) {		// redundant
				Log.d(TAG, "Cxn established, transferring to ActivityLoadCSV...");
				result = Boolean.valueOf(true);
			}
			
			return result;
		}
		
		@Override
		protected void onCancelled(Boolean result) {
			Log.d(TAG, "AsyncTask was cancelled, checking results");
			if (result == null || result.equals(Boolean.valueOf(false))) {
				show_failure_dialog();
			}
			else {
				start_loading_csv();
			}
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			Log.d(TAG, "Reached onPostExecute(), ActivityWaitForCxn");
			if (result == null || result.equals(Boolean.valueOf(false))) {
				show_failure_dialog();
			}
			else {
				start_loading_csv();
			}
		}
	}
	
	private void start_loading_csv() {
		startActivity(new Intent(ActivityWaitForCxn.this, ActivityLoadCSV.class));
		finish();
		return;
	}

	private void show_failure_dialog() {
		final Dialog dialog = new Dialog(ActivityWaitForCxn.this);
		dialog.setTitle("Wait for connection timeout");
		dialog.setContentView(R.layout.load_csv_failure_dialog);
		dialog.setCancelable(false);
		
		TextView err_msg = (TextView) dialog.findViewById(R.id.error_msg);
		err_msg.setText("Timeout period exceeded.\n\nRestart/abort?");
		
		dialog.findViewById(R.id.continue_button).setVisibility(View.GONE);
		
		Button restart_button = (Button) dialog.findViewById(R.id.restart_button);
		Button abort_button = (Button) dialog.findViewById(R.id.abort_button);

		restart_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d(TAG, "Timed out while waiting for cxn establishment, now restarting app...");
				dialog.dismiss();
				startActivity(new Intent(mContext, ActivityLoadCSV.class));
				finish();
				return;
			}
		});

		abort_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d(TAG, "Timed out while waiting for cxn establishment, now aborting...");
				dialog.dismiss();
				finish();
				return;
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
	
}		// end of file




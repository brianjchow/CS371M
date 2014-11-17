package com.example.app;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uis.R;

/*
 * TODO: behaviour when cxn shuts off in the middle of reading
 * 
 * Other
 * 		- call return; directly after finish(); finish() doesn't actually kill the activity (http://stackoverflow.com/questions/4924071/calling-finish-on-an-android-activity-doesnt-actually-finish)
 * 		- default background color ranges from 0xfbfbfb to 0xfdfdfd
 */

public class ActivityLoadCSV extends ActionBarActivity {

	private static final String TAG = "ActivityLoadCSV";
	private static final int TIMEOUT_AFTER = 15000;		// milliseconds

	private Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_load_csv);
		findViewById(R.id.no_cxn_msg).setVisibility(View.GONE);
		
//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		// http://learniosandroid.blogspot.com/2013/04/android-customising-progress-spinner.html
		ImageView progressSpinner = (ImageView) findViewById(R.id.progressSpinner);
		progressSpinner.setBackgroundResource(R.anim.panda_cycle_loader_animation);
		AnimationDrawable anim_draw = (AnimationDrawable) progressSpinner.getBackground();
		anim_draw.start();

		mContext = this;
		
		if (!has_network_connectivity() && !Constants.DEBUG) {
			Log.d(TAG, "No wifi and/or mobile cxn detected on startup, onCreate(), LoadCSV");
			goto_wait_for_cxn();
			return;
		}

		final ReadFeedTask read_csv = new ReadFeedTask();
		read_csv.execute(getApplicationContext());
		
		if (!Constants.DEBUG) {
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					if (read_csv.getStatus() == AsyncTask.Status.RUNNING) {
						read_csv.cancel(true);
					}
				}
				
			}, TIMEOUT_AFTER);
		}
		
	}
	
	// MUST CALL return AFTER CALLING THIS METHOD
	private void goto_wait_for_cxn() {
//		Constants.CSV_FEEDS_MASTER = null;
//		Constants.CSV_FEEDS_CLEANED = null;
		
		startActivity(new Intent(ActivityLoadCSV.this, ActivityWaitForCxn.class));
		finish();
		return;
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
		Log.d(TAG, "Unregistering broadcast receiver, onPause()");
		unregisterReceiver(broadcast_receiver);
	}
	
	private BroadcastReceiver broadcast_receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// http://stackoverflow.com/questions/5888502/how-to-detect-when-wifi-connection-has-been-established-in-android?rq=1
			
			final String action = intent.getAction();
			if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {	// SUPPLICANT_CONNECTION_CHANGE_ACTION
				
				NetworkInfo net_info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				if (net_info != null) {

//					if (!net_info.getState().equals(NetworkInfo.State.CONNECTED)) {
//						Log.d(TAG, "Network connectivity just disabled");
//						goto_wait_for_cxn();
//						return;
//					}
					
					// call AsyncTask.cancel() here?
					if ((net_info.getType() == ConnectivityManager.TYPE_WIFI || net_info.getType() == ConnectivityManager.TYPE_MOBILE) && !net_info.isConnected()) {
						goto_wait_for_cxn();
					}
					// else do nothing, because connection was newly established (should never happen within this class)
				}
				else {
					// ??? assume disconnected ???
					goto_wait_for_cxn();
					return;
				}

			}
		}
	};
	
	private boolean has_network_connectivity() {
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo net_info = manager.getActiveNetworkInfo();
		if (net_info != null) {
			
			if (net_info.getState().equals(NetworkInfo.State.CONNECTED)) {
				return true;
			}
			
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
			
//			if (net_info.getType() == ConnectivityManager.TYPE_WIFI && net_info.isConnected()) {
//				Log.d(TAG, "Wifi cxn enabled and connected on startup, onCreate(), LoadCSV");
//				return true;
//			}
//			else if (net_info.getType() == ConnectivityManager.TYPE_MOBILE && net_info.isConnected()) {
//				Log.d(TAG, "Mobile cxn enabled and connected on startup, onCreate(), LoadCSV");
//				return true;		
//			}
		}
		
		return false;
	}

	private class ReadFeedTask extends AsyncTask<Context, Void, Boolean> {
		private Exception exception = null;
		
		protected Boolean doInBackground(Context... context) {
			boolean result = false;
			try {
				Log.d(TAG, "Now reading CSV...");
				Constants.init(context[0]);
				if (!isCancelled()) {
					Log.d(TAG, "Successfully finished reading CSV");
					result = true;
				}
			}
			catch (Exception e) {
				Log.d(TAG, "Caught an exception while reading CSV: " + e.getMessage());
				this.exception = e;
				result = false;
			}
			Log.d(TAG, "Now returning " + result + " after reading CSV...");
			return result;
		}
		
		@Override
		protected void onCancelled(Boolean done) {
			Log.d(TAG, "Timed out");
			show_warning_dialog();
		}
		
		@Override
		protected void onPostExecute(Boolean done) {
			if (exception != null) {
				Log.d(TAG, "Exception occurred while reading CSV");
				show_failure_dialog();
			}
			
			if (done.equals(Boolean.valueOf(true))) {
				Log.d(TAG, "Done reading CSV, now entering MainActivity...");
				Toast.makeText(mContext, "Took " + CSVReader.time_to_read + " seconds to read " + CSVReader.lines_read + " lines", Toast.LENGTH_LONG).show();
				startActivity(new Intent(mContext, ActivityMain.class));
				finish();
				return;
			}
			else {
				Log.d(TAG, "Unknown error while loading CSV");
				finish();
				return;
			}
		}
	}

	private void show_warning_dialog() {
		final Dialog dialog = new Dialog(ActivityLoadCSV.this);
		dialog.setTitle(R.string.warning);
		dialog.setContentView(R.layout.load_csv_failure_dialog);
		dialog.setCancelable(false);		
		
		TextView err_msg = (TextView) dialog.findViewById(R.id.error_msg);
		err_msg.setText(R.string.load_warning_msg);
		
		dialog.findViewById(R.id.restart_button).setVisibility(View.GONE);
		
		Button continue_button = (Button) dialog.findViewById(R.id.continue_button);
		Button abort_button = (Button) dialog.findViewById(R.id.abort_button);
		
		continue_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Constants.CSV_FEEDS_MASTER = null;
				Constants.CSV_FEEDS_CLEANED = null;
				Constants.init(mContext, true);
				
				dialog.dismiss();
				startActivity(new Intent(mContext, ActivityMain.class));
				finish();
				return;
			}
		});

		abort_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
				finish();
				return;
			}
		});
		
		dialog.show();
	}
	
	private void show_failure_dialog() {
		final Dialog dialog = new Dialog(ActivityLoadCSV.this);
		dialog.setTitle("Load failure");
		dialog.setContentView(R.layout.load_csv_failure_dialog);
		dialog.setCancelable(false);
		Button continue_button = (Button) dialog.findViewById(R.id.continue_button);
		Button restart_button = (Button) dialog.findViewById(R.id.restart_button);
		Button abort_button = (Button) dialog.findViewById(R.id.abort_button);
		
		continue_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d(TAG, "Failed to complete reading CSV, now entering MainActivity...");
				dialog.dismiss();
				startActivity(new Intent(mContext, ActivityMain.class));
				finish();
				return;
			}
		});

		restart_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d(TAG, "Failed to complete reading CSV, now restarting app...");
				dialog.dismiss();
				startActivity(new Intent(mContext, ActivityLoadCSV.class));
				finish();
				return;
			}
		});

		abort_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d(TAG, "Failed to complete reading CSV, now aborting...");
				dialog.dismiss();
				finish();
				return;
			}
		});
		
		dialog.show();
	}
	
/*
	
	private BroadcastReceiver broadcast_receiver;
	private boolean has_network_cxn = false;
	
	public class WifiReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// http://stackoverflow.com/questions/5888502/how-to-detect-when-wifi-connection-has-been-established-in-android?rq=1
			
			final String action = intent.getAction();
			if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {	// SUPPLICANT_CONNECTION_CHANGE_ACTION
				Log.d(TAG, "Wifi state changed to enabled, onReceive()");
				Toast.makeText(ActivityLoadCSV.this, "Wifi state changed to enabled, ActivityLoadCSV", Toast.LENGTH_SHORT).show();
			}
			else {
				Log.d(TAG, "Wifi state changed to disabled, onReceive()");
				Toast.makeText(ActivityLoadCSV.this, "Wifi state changed to disabled, ActivityLoadCSV", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	// http://stackoverflow.com/questions/9434235/android-i-want-to-set-listener-to-listen-on-wireless-state-can-anyone-help-me-w
	private void set_network_state() {
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo net_info = manager.getActiveNetworkInfo();
		if (net_info != null && (net_info.getType() == ConnectivityManager.TYPE_WIFI || net_info.getType() == ConnectivityManager.TYPE_MOBILE)) {
			if (net_info.isConnected() || net_info.isConnectedOrConnecting()) {
				
			}
			else {
				Log.d(TAG, "Wifi/mobile network cxn may be enabled, but it isn't connected");
			}
			
			Log.d(TAG, "Wifi/mobile network cxn enabled, not necessarily connected");
			has_network_cxn = true;
		}
		else {
			Log.d(TAG, "Wifi/mobile network cxn disabled, not necessarily connected");
			has_network_cxn = false;
		}
	}
				
		
//		if (!has_network_cxn) {
//			findViewById(R.id.loading_msg).setVisibility(View.GONE);
//			findViewById(R.id.loading_animation).setVisibility(View.GONE);
//			findViewById(R.id.no_cxn_msg).setVisibility(View.VISIBLE);
//			
////			while (!has_network_cxn) {		// set timeout period !!!!!!!!!!!
////				// do nothing
////			}
////
////			findViewById(R.id.loading_msg).setVisibility(View.VISIBLE);
////			findViewById(R.id.loading_animation).setVisibility(View.VISIBLE);
////			findViewById(R.id.no_cxn_msg).setVisibility(View.GONE);
//		}
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.load_csv, menu);
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
	
 */
	
}		// end of file

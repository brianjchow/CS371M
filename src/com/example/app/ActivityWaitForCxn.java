package com.example.app;

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
import android.widget.Toast;

public class ActivityWaitForCxn extends ActionBarActivity {

	private static final String TAG = "ActivityWaitForCxn";
	
	private static final boolean INFINITE_TIMEOUT = true;
	private static final int TIMEOUT_AFTER = 60000;			// milliseconds
	
	private static final String mHASCXN = "mHasCxn";
	
	private boolean mHasCxn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wait_for_cxn);
//		setContentView(R.layout.floor_0_frame_layout);
//		
//		ImageView dinosaur = (ImageView) findViewById(R.id.dinosaur);
//		dinosaur.setBackgroundColor(Color.RED);
//		
//		mContext = ActivityWaitForCxn.this;
//		
		if (savedInstanceState != null) {
			mHasCxn = savedInstanceState.getBoolean(mHASCXN, false);
		}
		else {
			mHasCxn = false;
		}

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

		Button try_again_button = (Button) findViewById(R.id.try_again);
		try_again_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (has_network_connectivity()) {
					startActivity(new Intent(ActivityWaitForCxn.this, ActivityLoadCSV.class));
					finish();
					return;					
				}
			}
		});
		
		Button quit_button = (Button) findViewById(R.id.quit_button);
		quit_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
				return;
			}
		});
		
	}		// end onCreate()
	
	@Override
	protected void onResume() {
		super.onResume();
		
		// register BroadcastReceiver
		Log.d(TAG, "Registering broadcast receiver, onResume()");
		IntentFilter intent_filter = new IntentFilter();
		intent_filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);	// SUPPLICANT_CONNECTION_CHANGE_ACTION
		intent_filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(broadcast_receiver, intent_filter);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		// unregister BroadcastReceiver
		Log.d(TAG, "Unregistering broadcast receiver, onPause()");
		unregisterReceiver(broadcast_receiver);
	}
	
//	@Override
//	protected void onDestroy() {
//		super.onDestroy();
//		
//		// unregister BroadcastReceiver
//		Log.d(TAG, "Unregistering broadcast receiver, onDestroy()");
//		unregisterReceiver(broadcast_receiver);
//	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putBoolean(mHASCXN, mHasCxn);
	}
	
	private BroadcastReceiver broadcast_receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// http://stackoverflow.com/questions/5888502/how-to-detect-when-wifi-connection-has-been-established-in-android?rq=1
			// http://stackoverflow.com/questions/2802472/detect-network-connection-type-on-android
			
			final String action = intent.getAction();
			
//			Toast.makeText(ActivityWaitForCxn.this, "BroadcastReceiver action was " + action, Toast.LENGTH_LONG).show();
			
			if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
				Log.d(TAG, "Network state just changed, broadcast_receiver.onReceive() for WiFi");

//				if (Constants.DEBUG) {
//					Toast.makeText(ActivityWaitForCxn.this, "Network state just changed", Toast.LENGTH_LONG).show();
//				}
				
				NetworkInfo net_info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				if (net_info != null) {

					if (net_info.isConnected() && net_info.getType() == ConnectivityManager.TYPE_WIFI) {
						Log.d(TAG, "Wifi cxn just enabled");
						
						if (Constants.DEBUG) {
							Toast.makeText(ActivityWaitForCxn.this, "Wifi cxn just enabled", Toast.LENGTH_LONG).show();
						}
						
						mHasCxn = true;
					}
//					else if (net_info.getType() == ConnectivityManager.TYPE_MOBILE && net_info.isConnected()) {
//						Log.d(TAG, "Mobile cxn just enabled");
//						Toast.makeText(ActivityWaitForCxn.this, "Mobile cxn just enabled", Toast.LENGTH_LONG).show();
//						mHasCxn = true;
//					}
					else {
						mHasCxn = false;
					}
				}
				else {
					Log.d(TAG, "Net info null, broadcast_receiver.onReceive() for WiFi");

					if (Constants.DEBUG) {
						Toast.makeText(ActivityWaitForCxn.this, "Net info null, broadcast_receiver.onReceive() for WiFi", Toast.LENGTH_LONG).show();
					}
					
					mHasCxn = false;
				}

			}
			else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				Log.d(TAG, "Network state just changed, broadcast_receiver.onReceive() for mobile");

//				if (Constants.DEBUG) {
//					Toast.makeText(ActivityWaitForCxn.this, "Network state just changed", Toast.LENGTH_LONG).show();
//				}
				
//				NetworkInfo net_info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
				
				ConnectivityManager con_man = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
				
				NetworkInfo net_info = con_man.getActiveNetworkInfo();
				
//				int net_type = intent.getExtras().getInt(ConnectivityManager.EXTRA_NETWORK_TYPE);
//				NetworkInfo net_info = con_man.getNetworkInfo(net_type);
				
//				NetworkInfo net_info = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
				if (net_info != null) {

					if (net_info.isConnected() && net_info.getType() == ConnectivityManager.TYPE_MOBILE) {
						Log.d(TAG, "Mobile cxn just enabled");
						
						if (Constants.DEBUG) {
							Toast.makeText(ActivityWaitForCxn.this, "Mobile cxn just enabled", Toast.LENGTH_LONG).show();
						}
						
						mHasCxn = true;
					}
					else {
						mHasCxn = false;
					}
				}
				else {
					Log.d(TAG, "Net info null, broadcast_receiver.onReceive() for mobile");

					if (Constants.DEBUG) {
						Toast.makeText(ActivityWaitForCxn.this, "Net info null, broadcast_receiver.onReceive() for mobile", Toast.LENGTH_LONG).show();
					}
					
					mHasCxn = false;
				}

			}
			else {
				mHasCxn = false;
			}
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
				startActivity(new Intent(ActivityWaitForCxn.this, ActivityLoadCSV.class));
				finish();
				return;
			}
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			Log.d(TAG, "Reached onPostExecute(), ActivityWaitForCxn");
			if (result == null || result.equals(Boolean.valueOf(false))) {
				show_failure_dialog();
			}
			else {
				startActivity(new Intent(ActivityWaitForCxn.this, ActivityLoadCSV.class));
				finish();
				return;
			}
		}
	}
	
//	private void start_loading_csv() {
//		startActivity(new Intent(ActivityWaitForCxn.this, ActivityLoadCSV.class));
//		finish();
//		return;
//	}

	private void show_failure_dialog() {
		final Dialog dialog = new Dialog(ActivityWaitForCxn.this);
		dialog.setTitle(R.string.wait_title);
		dialog.setContentView(R.layout.load_csv_failure_dialog);
		dialog.setCancelable(false);
		
		TextView err_msg = (TextView) dialog.findViewById(R.id.error_msg);
		err_msg.setText(R.string.timeout_exceeded_msg);
		
		dialog.findViewById(R.id.continue_button).setVisibility(View.GONE);
		
		Button restart_button = (Button) dialog.findViewById(R.id.restart_button);
		Button abort_button = (Button) dialog.findViewById(R.id.abort_button);

		restart_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d(TAG, "Timed out while waiting for cxn establishment, now restarting app...");
				dialog.dismiss();
				startActivity(new Intent(ActivityWaitForCxn.this, ActivityLoadCSV.class));
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
	
	protected boolean has_network_connectivity() {
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
		
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.find_a_room_now){
			getRoomRec();
			return true;
		}
		if (id == R.id.find_a_room_later){
			find_room_later();
			return true;
		}
		if (id == R.id.get_room_schedule){
			get_room_schedule();
			return true;
		}
		if (id == R.id.exit){
			exitApp();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	

	public void getRoomRec() {
//		startActivityForResult(new Intent(this, ActivityFindRoomLater.class), 0);
		startActivity(new Intent(this, ActivityFindRoomLater.class));
		finish();
	}
	
	private void find_room_later() {
		Intent intent = new Intent(this, ActivityFindRoomLater.class);
//		intent.putExtra(Query.PARCELABLE_QUERY, this.query);
		startActivity(intent);
		finish();
	}
	
	private void get_room_schedule() {
		Intent intent = new Intent(this, ActivityGetRoomSchedule.class);
//		intent.putExtra(Query.PARCELABLE_QUERY, this.query);
		startActivity(intent);
		finish();
	}
	
	public void exitApp() {
		Intent intent = new Intent(getApplicationContext(),ActivityMain.class );
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("EXIT", true);
		startActivity(intent);
	}
	
}		// end of file





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

/*
 * TODO: behaviour when cxn shuts off in the middle of reading
 * 
 * Other
 * 		- call return; directly after finish(); finish() doesn't actually kill the activity (http://stackoverflow.com/questions/4924071/calling-finish-on-an-android-activity-doesnt-actually-finish)
 * 		- default background color ranges from 0xfbfbfb to 0xfdfdfd
 */

public class ActivityLoadCSV extends ActionBarActivity {

	private static final String TAG = "ActivityLoadCSV";
	private static final int TIMEOUT_AFTER = 20000;		// milliseconds
	private static boolean TIMEOUT_FLAG = false;
	
	private final ReadFeedTask read_csv = new ReadFeedTask();

//	private Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_load_csv);
		findViewById(R.id.no_cxn_msg).setVisibility(View.GONE);
		
//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

		try {
			Class.forName("com.example.app.Constants", true, this.getClass().getClassLoader());
		}
		catch (ClassNotFoundException e) {
			throw new RuntimeException("Failed to initialise necessary global constants class");
		}
		
		// http://learniosandroid.blogspot.com/2013/04/android-customising-progress-spinner.html
		ImageView progressSpinner = (ImageView) findViewById(R.id.progressSpinner);
		progressSpinner.setBackgroundResource(R.anim.panda_cycle_loader_animation);
		AnimationDrawable anim_draw = (AnimationDrawable) progressSpinner.getBackground();
		anim_draw.start();

//		mContext = ActivityLoadCSV.this;
		
//		Intent calling_intent = getIntent();
//		if (calling_intent == null) {
//			Log.d(TAG, "App just freshly launched");
//		}
//		else {
//			Log.d(TAG, "App restarted from " + calling_intent.toString());
//		}
		
		if (!has_network_connectivity()) {
			Log.d(TAG, "No wifi and/or mobile cxn detected on startup, onCreate(), LoadCSV");
			goto_wait_for_cxn();
			return;
		}

		read_csv.execute(ActivityLoadCSV.this);

		if (!Constants.DEBUG) {
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					if (read_csv.getStatus() == AsyncTask.Status.RUNNING) {
						TIMEOUT_FLAG = true;
						read_csv.cancel(true);
//						show_warning_dialog(getResources().getString(R.string.load_error_msg));
					}
				}
				
			}, TIMEOUT_AFTER);
		}
		
	}
	
	// MUST CALL return AFTER CALLING THIS METHOD
	private void goto_wait_for_cxn() {
//		Constants.CSV_FEEDS_MASTER = null;
//		Constants.CSV_FEEDS_CLEANED = null;
		
//		Intent intent = new Intent(ActivityLoadCSV.this, ActivityWaitForCxn.class);
		startActivity(new Intent(ActivityLoadCSV.this, ActivityWaitForCxn.class));
		finish();
	}
	
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
	
	private BroadcastReceiver broadcast_receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// http://stackoverflow.com/questions/5888502/how-to-detect-when-wifi-connection-has-been-established-in-android?rq=1
			
			final String action = intent.getAction();
			if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {	// SUPPLICANT_CONNECTION_CHANGE_ACTION
				
				NetworkInfo net_info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				if (net_info != null) {

					// call AsyncTask.cancel() here?
					if (net_info.getType() == ConnectivityManager.TYPE_WIFI && !net_info.isConnected()) {
						if (read_csv.getStatus() == AsyncTask.Status.RUNNING) {
							Log.d(TAG, "fail 1");
//							read_csv.cancel(true);
						}
//						else {
//							show_warning_dialog(getResources().getString(R.string.cxn_lost_warning_msg));
//						}
						
//						goto_wait_for_cxn();
//						return;
					}
					// else do nothing, because connection was newly established (should never happen within this class)
				}
				else {
					// ??? assume disconnected ???
					
					if (read_csv.getStatus() == AsyncTask.Status.RUNNING) {
						Log.d(TAG, "fail 2");
//						read_csv.cancel(true);
					}
//					else {
//						show_warning_dialog(getResources().getString(R.string.cxn_lost_warning_msg));
//					}
					
//					goto_wait_for_cxn();
//					return;
				}

			}
			else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				
				ConnectivityManager con_man = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo net_info = con_man.getActiveNetworkInfo();
				
				if (net_info != null) {

					// call AsyncTask.cancel() here?
					if (net_info.getType() == ConnectivityManager.TYPE_MOBILE && !net_info.isConnected()) {
						if (read_csv.getStatus() == AsyncTask.Status.RUNNING) {
							Log.d(TAG, "fail 3");
//							read_csv.cancel(true);
						}
//						else {
//							show_warning_dialog(getResources().getString(R.string.cxn_lost_warning_msg));
//						}
						
//						goto_wait_for_cxn();
//						return;
					}
					// else do nothing, because connection was newly established (should never happen within this class)
				}
				else {
					// ??? assume disconnected ???
					
					if (read_csv.getStatus() == AsyncTask.Status.RUNNING) {
						Log.d(TAG, "fail 4");
//						read_csv.cancel(true);
					}
//					else {
//						show_warning_dialog(getResources().getString(R.string.cxn_lost_warning_msg));
//					}
					
//					goto_wait_for_cxn();
//					return;
				}
			}
		}
	};
	
	private boolean has_network_connectivity() {
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo net_info = manager.getActiveNetworkInfo();
		if (net_info != null && net_info.getState().equals(NetworkInfo.State.CONNECTED)) {
			Log.d(TAG, "Network connectivity detected, has_network_connectivity()");
			return true;
		}
		
		Log.d(TAG, "No network connectivity detected, has_network_connectivity()");
		return false;
	}

	private class ReadFeedTask extends AsyncTask<Context, Void, Boolean> {
		private Exception exception = null;
		
		@Override
		protected Boolean doInBackground(Context... context) {
			boolean result = false;
			try {
				Log.d(TAG, "Now reading CSV...");
				Constants.init(context[0]);
				if (!isCancelled()) {
					Log.d(TAG, "Successfully finished reading CSV");
					result = true;
				}
//				else {
//					Log.d(TAG, "FAILED to read CSV");
//					result = false;
////					show_warning_dialog(getResources().getString(R.string.load_error_msg));
//				}
			}
			catch (Exception e) {
//				http://stackoverflow.com/questions/6834106/try-catch-exception-always-returns-null
//				if (e == null) {
//					Log.d(TAG, "Exception e is actually null (?)");
//					throw new RuntimeException();
//				}

				Log.d(TAG, "Caught an exception while reading CSV: " + e.toString());
				e.printStackTrace();
				this.exception = e;
				result = false;
			}
			Log.d(TAG, "Now returning " + result + " after reading CSV...");
			return result;
		}
		
		@Override
		protected void onCancelled(Boolean done) {
//			Log.d(TAG, "Timed out");
			
			delete_all_feeds();
			
			if (!TIMEOUT_FLAG) {
				show_warning_dialog(getResources().getString(R.string.cxn_lost_warning_msg));
			}
			else {
				show_warning_dialog(getResources().getString(R.string.load_error_msg));
			}
		}
		
		@Override
		protected void onPostExecute(Boolean done) {
			if (exception != null) {
				Log.d(TAG, "Exception occurred while reading CSV");
				show_failure_dialog();
			}
			
			if (done.equals(Boolean.valueOf(true))) {
				Log.d(TAG, "Done reading CSV, now entering MainActivity...");
				
				if (Constants.DEBUG) {
//					Toast.makeText(ActivityLoadCSV.this, "Took " + CSVReader.time_to_read + " seconds to read " + CSVReader.lines_read + " lines", Toast.LENGTH_LONG).show();
					Toast.makeText(ActivityLoadCSV.this, "Took " + (CSVReader.time_to_read + Constants.time_to_read) + " seconds to read CSV feeds (" + CSVReader.lines_read + " lines) and load GDC course schedule", Toast.LENGTH_LONG).show();
				}
				
				startActivity(new Intent(ActivityLoadCSV.this, ActivityMain.class));
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

	private void show_warning_dialog(String msg) {
		if (msg == null) {
			throw new IllegalArgumentException();
		}
		
		final Dialog dialog = new Dialog(ActivityLoadCSV.this);
		dialog.setTitle(R.string.warning);
		dialog.setContentView(R.layout.load_csv_failure_dialog);
		dialog.setCancelable(false);		
		
		TextView err_msg = (TextView) dialog.findViewById(R.id.error_msg);
//		err_msg.setText(R.string.load_warning_msg);
		err_msg.setText(msg);
		
		dialog.findViewById(R.id.restart_button).setVisibility(View.GONE);
		
		Button continue_button = (Button) dialog.findViewById(R.id.continue_button);
		Button abort_button = (Button) dialog.findViewById(R.id.abort_button);
		
		continue_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				Constants.CSV_FEEDS_MASTER = null;
//				Constants.CSV_FEEDS_CLEANED = null;
				
				Constants.init(ActivityLoadCSV.this, true);
				
				dialog.dismiss();
				startActivity(new Intent(ActivityLoadCSV.this, ActivityMain.class));
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
				
				/*
				 * TODO - by default, routes back to ActivityMain; kill that as well
				 */
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
				startActivity(new Intent(ActivityLoadCSV.this, ActivityMain.class));
				finish();
				return;
			}
		});

		restart_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d(TAG, "Failed to complete reading CSV, now restarting app...");
				
				delete_all_feeds();
				
				dialog.dismiss();
				startActivity(new Intent(ActivityLoadCSV.this, ActivityLoadCSV.class));
				finish();
				return;
			}
		});

		abort_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d(TAG, "Failed to complete reading CSV, now aborting...");
				
				delete_all_feeds();
				
				dialog.dismiss();
				finish();
				return;
			}
		});
		
		dialog.show();
	}
	
	// DO NOT CALL UNLESS ASYNCTASK WAS CANCELLED
	private void delete_all_feeds() {
		String download_filename;
		boolean deleted = false;
		
		download_filename = CSVReader.ALL_EVENTS_SCHEDULE_FILENAME;
		if (ActivityLoadCSV.this.getFileStreamPath(download_filename).exists()) {
			deleted = ActivityLoadCSV.this.deleteFile(download_filename);
			Log.d(TAG, "File " + download_filename + " was deleted and no longer exists in internal storage directory (delete_all()): " + deleted);
		}
		
		download_filename = CSVReader.ALL_ROOMS_SCHEDULE_FILENAME;
		if (ActivityLoadCSV.this.getFileStreamPath(download_filename).exists()) {
			deleted = ActivityLoadCSV.this.deleteFile(download_filename);
			Log.d(TAG, "File " + download_filename + " was deleted and no longer exists in internal storage directory (delete_all()): " + deleted);
		}
		
		download_filename = CSVReader.ALL_TODAYS_EVENTS_FILENAME;
		if (ActivityLoadCSV.this.getFileStreamPath(download_filename).exists()) {
			deleted = ActivityLoadCSV.this.deleteFile(download_filename);
			Log.d(TAG, "File " + download_filename + " was deleted and no longer exists in internal storage directory (delete_all()): " + deleted);
		}
	}
	
/*
	
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

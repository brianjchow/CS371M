package com.example.uis;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

public class LoadCSVActivity extends ActionBarActivity {

	private final String TAG = "LoadCSVActivity";
	
	private boolean has_network_cxn = false;
	
	private Context mContext;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_load_csv);
		findViewById(R.id.no_cxn_msg).setVisibility(View.GONE);
		
		// http://learniosandroid.blogspot.com/2013/04/android-customising-progress-spinner.html
		ImageView progressSpinner = (ImageView) findViewById(R.id.progressSpinner);
		progressSpinner.setBackgroundResource(R.anim.panda_cycle_loader_animation);
		AnimationDrawable anim_draw = (AnimationDrawable) progressSpinner.getBackground();
		anim_draw.start();

		mContext = this;

		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo net_info = manager.getActiveNetworkInfo();
		if (net_info != null && (net_info.getType() == ConnectivityManager.TYPE_WIFI || net_info.getType() == ConnectivityManager.TYPE_MOBILE)) {
			Log.d(TAG, "Wifi/mobile network cxn enabled onCreate LoadCSV");
			has_network_cxn = true;
		}
		else {
			Log.d(TAG, "Wifi/mobile network cxn disabled onCreate LoadCSV");
			has_network_cxn = false;
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
		
		ReadFeedTask read_csv = new ReadFeedTask();
		read_csv.execute(this);		// 4.78 seconds
		
//		boolean done = read_csv.doInBackground(this);		// only 1.52 seconds - what gives
//		if (done) {
//			startActivity(new Intent(this, MainActivity.class));
//		}
//		else {
//			finish();
//		}

	}

//	@Override
//	public boolean onCreateOptionsMenu(Menu menu) {
//		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.load_csv, menu);
//		return true;
//	}
//
//	@Override
//	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle action bar item clicks here. The action bar will
//		// automatically handle clicks on the Home/Up button, so long
//		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
//		if (id == R.id.action_settings) {
//			return true;
//		}
//		return super.onOptionsItemSelected(item);
//	}
	
	public class WifiReceiver extends BroadcastReceiver {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			set_network_state();
		}
	}
	
	private void set_network_state() {
		ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo net_info = manager.getActiveNetworkInfo();
		if (net_info != null && (net_info.getType() == ConnectivityManager.TYPE_WIFI || net_info.getType() == ConnectivityManager.TYPE_MOBILE)) {
			Log.d(TAG, "Wifi/mobile network cxn enabled");
			has_network_cxn = true;
		}
		else {
			Log.d(TAG, "Wifi/mobile network cxn disabled");
			has_network_cxn = false;
		}
	}
	
	private class ReadFeedTask extends AsyncTask<Context, Void, Boolean> {
		private Exception exception;
		
		protected Boolean doInBackground(Context... context) {
			try {
				Log.d(TAG, "Now reading CSV...");
				Constants.init(context[0]);
				Log.d(TAG, "Now returning true after reading CSV...");
				return Boolean.valueOf(true);
			}
			catch (Exception e) {
				this.exception = e;
				return Boolean.valueOf(false);
			}
		}
		
		@Override
		protected void onPostExecute(Boolean done) {
			if (done.equals(Boolean.valueOf(true))) {
				Log.d(TAG, "Done reading CSV, now entering MainActivity...");
//				Intent intent = new Intent(this, MainActivity.class);
//				startActivity(intent);
//				startActivity(new Intent(getApplicationContext(), MainActivity.class));
				startActivity(new Intent(mContext, MainActivity.class));
				finish();	// kill this activity off the stack; prevent back button from returning to this activity
			}
			else {
				Log.d(TAG, "Unknown error while loading CSV");
				finish();
			}
		}
	}

}

package com.example.uis;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class ActivityMain extends ActionBarActivity implements View.OnClickListener {

	private static final String TAG = "MainActivity";
	
	Button find_room_now;
	Button find_room_later;
	
	protected static boolean has_network_cxn = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		set_network_state();		
////		Constants.init(getApplicationContext());
//		ReadFeedTask read_feed_task = new ReadFeedTask();
//		read_feed_task.doInBackground(getApplicationContext());
		
		setContentView(R.layout.main);
		find_room_now = (Button)findViewById(R.id.find_room_now);
		find_room_later = (Button)findViewById(R.id.find_room_for_later);
		find_room_now.setOnClickListener(this);
		find_room_later.setOnClickListener(this);
		
		if (getIntent().getBooleanExtra("EXIT",  false)) {
			finish();
		}
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
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}



	public void findRoom() {
		startActivityForResult(new Intent(this, ActivityRoomRec.class), 0);
	}

	public void findRoomLater() {
		startActivityForResult(new Intent(this, ActivityFindRoomLater.class), 0);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.find_room_now:
				findRoom();
				break;
			case R.id.find_room_for_later:
				findRoomLater();
				break;
		}
	}
	
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
			Log.d(TAG, "Wifi/mobile network cxn just enabled");
			has_network_cxn = true;
		}
		else {
			Log.d(TAG, "Wifi/mobile network cxn just disabled");
			has_network_cxn = false;
		}
	}
	
//	private class ReadFeedTask extends AsyncTask<Context, Void, Boolean> {
//		private Exception exception;
//		
//		protected Boolean doInBackground(Context... context) {
//			try {
//				Constants.init(context[0]);
//				return true;
//			}
//			catch (Exception e) {
//				this.exception = e;
//				return false;
//			}
//		}
//	}

}
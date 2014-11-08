package com.example.app;

import com.example.uis.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class ActivityMain extends ActionBarActivity implements View.OnClickListener {

	private static final String TAG = "MainActivity";
	
//	Button find_room_now;
//	Button find_room_later;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		Button find_room_now = (Button) findViewById(R.id.find_room_now);
		Button find_room_later = (Button) findViewById(R.id.find_room_for_later);
		find_room_now.setOnClickListener(this);
		find_room_later.setOnClickListener(this);
		
		if (getIntent().getBooleanExtra("EXIT",  false)) {
			Log.d(TAG, "Now exiting");
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
//		startActivityForResult(new Intent(this, ActivityRoomRec.class), 0);
		startActivity(new Intent(ActivityMain.this, ActivityRoomRec.class));
	}

	public void findRoomLater() {
//		startActivityForResult(new Intent(this, ActivityFindRoomLater.class), 0);
		startActivity(new Intent(ActivityMain.this, ActivityFindRoomLater.class));
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

}		// end of file





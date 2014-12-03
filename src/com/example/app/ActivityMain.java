package com.example.app;

import java.util.Calendar;

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
		
		setContentView(R.layout.activity_main);
		Button find_room_now = (Button) findViewById(R.id.find_room_now);
		Button find_room_later = (Button) findViewById(R.id.find_room_for_later);
		Button get_room_schedule = (Button) findViewById(R.id.get_room_schedule);
		find_room_now.setOnClickListener(this);
		find_room_later.setOnClickListener(this);
		get_room_schedule.setOnClickListener(this);
		
		if (getIntent().getBooleanExtra(Constants.EXIT,  false)) {
			Log.d(TAG, "Now exiting");
			finish();
			return;
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
		if (id == R.id.find_a_room_now){
			findRoom();
			return true;
		}
		if (id == R.id.find_a_room_later){
			findRoomLater();
			return true;
		}
		if (id == R.id.get_room_schedule){
			get_room_schedule();
			return true;
		}
		if (id == R.id.exit){
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void findRoom() {
		Calendar calendar = Calendar.getInstance();
		
		Query query = new Query(ActivityMain.this, calendar.getTime());
		Query.QueryResult query_result = query.search();

		Intent intent = new Intent(ActivityMain.this, ActivityRoomRec.class);
		intent.putExtra(Query.PARCELABLE_QUERY, query);
		intent.putExtra(Query.QueryResult.PARCELABLE_QUERY_RESULT, query_result);
		
		startActivity(intent);
	}

	private void findRoomLater() {
		startActivity(new Intent(ActivityMain.this, ActivityFindRoomLater.class));
	}
	
	private void get_room_schedule() {
		startActivity(new Intent(ActivityMain.this, ActivityGetRoomSchedule.class));
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
			case R.id.get_room_schedule:
				get_room_schedule();
				break;
		}
		
//		finish();
//		return;
	}

}		// end of file





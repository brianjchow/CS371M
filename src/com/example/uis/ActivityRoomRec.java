package com.example.uis;

import java.lang.reflect.Field;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ActivityRoomRec extends ActionBarActivity implements View.OnClickListener {

	private final String TAG = "RoomRecActivity";
	
	private Query this_query;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this_query = new Query();

		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
			Query query = (Query) bundle.getParcelable("this_query");
			if (query != null) {
				Log.d(TAG, "Using transmitted parcelable: " + query.toString());
				this_query = query;
			}
		}
		
		search();
	}
	
	// @Override protected void onActivityResult(int requestCode, int resultCode, Intent intent) { }

	private void search() {
		search(this_query);
	}
	
	private void search(Query query) {
		setContentView(R.layout.activity_find_room);
		
		Button ohkay = (Button) findViewById(R.id.ohkay);
		Button newRoom = (Button) findViewById(R.id.new_Room);
		Button find_room_later_button = (Button) findViewById(R.id.find_room_later);
		ohkay.setOnClickListener(this);
		newRoom.setOnClickListener(this);
		find_room_later_button.setOnClickListener(this);
		
		String recommendation = query.search();
		setTextViewInfo(recommendation);
		
		View background = findViewById(R.id.background);
		if (!recommendation.equals(Constants.NO_ROOMS_AVAIL_MSG)) {
			String temp = new Location(recommendation).get_room().replaceAll("\\.", "");
			int res_id = getResId("gdc_" + temp, R.drawable.class);
			if (res_id != -1) {
				background.setBackgroundResource(res_id);	// getResources().getDrawable(int id), View.setBackgroundResource(int id)
			}
		}
		
		update_query_textview(query);
	}

	// http://stackoverflow.com/questions/4427608/android-getting-resource-id-from-string
	private int getResId(String var_name, Class<?> c) {
		int id = -1;
		try {
			Field id_field = c.getDeclaredField(var_name);
			id = id_field.getInt(id_field);
		}
		catch (Exception e) {
			id = -1;
		}
		
		return id;
	}

	private void setTextViewInfo(String recommendation) {
		TextView roomRecText = (TextView) findViewById(R.id.room_num);
		if (!recommendation.equals(Constants.NO_ROOMS_AVAIL_MSG)) {
			recommendation = "Try " + recommendation;
		}
		roomRecText.setText(recommendation);		
	}
	
	private void update_query_textview(Query query) {
		TextView temp = (TextView) findViewById(R.id.this_query);
		temp.setText("Current query:\n" + query.toString());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.find_room, menu);
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

	public void exit() {
		startActivityForResult(new Intent(this, ActivityExit.class), 0);
	}

	public void getRoomRec() {
		startActivityForResult(new Intent(this, ActivityFindRoomLater.class), 0);
	}
	
	private void find_room_later() {
		Intent intent = new Intent(getApplicationContext(), ActivityFindRoomLater.class);
		intent.putExtra("this_query", this_query);
		startActivity(intent);
		finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.ohkay:
//				exit();
				Intent intent = new Intent(this, ActivityMain.class);
				startActivity(intent);
				finish();
				break;
			case R.id.new_Room:
//				getRoomRec();
				search();
				break;
			case R.id.find_room_later:
				Log.d(TAG, "Clicked search later button");
				find_room_later();
				break;
		}

	}

}		// end of file





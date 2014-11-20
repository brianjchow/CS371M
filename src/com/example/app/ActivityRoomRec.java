package com.example.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uis.R;

/*
 * USE DialogFragment TO REOPEN DIALOG UPON ORIENTATION CHANGE
 *	// OR THIS: http://stackoverflow.com/questions/1111980/how-to-handle-screen-orientation-change-when-progress-dialog-and-background-thre
 *
 * ADD this_query_result PARCELABLE HANDLING TO onCreate()
 * OVERRIDE toString() IN Query.QueryResult; USE IN set_query_textview()
 */

// public class ActivityRoomRec extends ActionBarActivity implements View.OnClickListener {
public class ActivityRoomRec extends ActionBarActivity {

	private final String TAG = "RoomRecActivity";
	
	private Query this_query;
	private Query.QueryResult this_query_result;
	
	private String curr_recommendation;
	private int curr_recommendation_res_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_room);		
		
		if (savedInstanceState != null) {
//			Log.d(TAG, "Orientation changed, in onCreate(); bground res id: " + savedInstanceState.getInt("curr_recommendation_res_id", -65));
			Query temp = (Query) savedInstanceState.getParcelable("this_query");
			if (temp != null) {
				this_query = temp;
				this_query.set_context(ActivityRoomRec.this);
			}
			else {
//				this_query = new Query(getApplicationContext());
				this_query = new Query(ActivityRoomRec.this);
			}
			
			curr_recommendation = savedInstanceState.getString("curr_recommendation", Query.MessageStatus.NO_ROOMS_AVAIL.toString());
			curr_recommendation_res_id = savedInstanceState.getInt("curr_recommendation_res_id", 0);

			View background = findViewById(R.id.background);
			if (!curr_recommendation.equals(Query.MessageStatus.NO_ROOMS_AVAIL.toString())) {
				background.setBackgroundResource(curr_recommendation_res_id);
			}
			else {
				background.setBackgroundResource(R.drawable.gdcemptyroom);
			}

			setTextViewInfo(curr_recommendation);
			update_query_textview(this_query);
		}
		else {
//			this_query = new Query(getApplicationContext());
			this_query = new Query(ActivityRoomRec.this);		
			
			Bundle bundle = getIntent().getExtras();
			if (bundle != null) {
				Query query = (Query) bundle.getParcelable("this_query");
				if (query != null) {
//					Log.d(TAG, "Using transmitted parcelable:\n" + query.toString());
					this_query = query;
//					this_query.set_context(getApplicationContext());
					this_query.set_context(ActivityRoomRec.this);
				}
			}
		}
		
		findViewById(R.id.ohkay).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(ActivityRoomRec.this, ActivityMain.class);
				startActivity(intent);
				finish();
				return;
			}
		});
		
		findViewById(R.id.new_Room).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				search();
			}
		});
		
		findViewById(R.id.find_room_later).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				find_room_later();
			}
		});

		search();
	}
	
	// @Override protected void onActivityResult(int requestCode, int resultCode, Intent intent) { }

	private void search() {
		search(this_query);
	}
	
	private void search(Query query) {
//		Log.d(TAG, "Size of CSV_FEEDS_MASTER: " + Constants.CSV_FEEDS_MASTER.get_size());
//		Log.d(TAG, "Size of CSV_FEEDS_CLEANED: " + Constants.CSV_FEEDS_CLEANED.get_size());
		
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start();
		
		Query.QueryResult results = query.search();
		this.curr_recommendation = results.get_random_room();
		
		stopwatch.stop();
		Log.d(TAG, "Took " + stopwatch.time() + " seconds to execute search");
		
		if (Constants.DEBUG) {
			Toast.makeText(ActivityRoomRec.this, "Took " + stopwatch.time() + " seconds to execute search", Toast.LENGTH_SHORT).show();
		}
		
		setTextViewInfo(curr_recommendation);
		
		View background = findViewById(R.id.background);
		if (results.get_message_status().equals(Query.MessageStatus.SEARCH_SUCCESS.toString())) {
			
			String building_name = results.get_building_name();
			if (building_name.equalsIgnoreCase(Constants.GDC)) {
//				String temp = new Location(curr_recommendation).get_room().replaceAll("\\.", "");
				String building_pic_str = this.curr_recommendation.toLowerCase(Constants.DEFAULT_LOCALE).replaceAll("\\.", "").replaceAll("\\s+", "_");
				int res_id = Utilities.getResId(building_pic_str, R.drawable.class);
				if (res_id != -1) {
					background.setBackgroundResource(res_id);
					curr_recommendation_res_id = res_id;
				}
				else {
					// display tower?
					res_id = Utilities.getResId("campus_tower", R.drawable.class);
					background.setBackgroundResource(res_id);
					curr_recommendation_res_id = res_id;
				}
				
				Log.d(TAG, "Looking for building picture " + building_pic_str + " with res id " + res_id);
			}
			else {
				// get the building name
				// get its picture
				// set res id
				
				String building_pic_str = "campus_" + building_name.toLowerCase(Constants.DEFAULT_LOCALE);
				int res_id = Utilities.getResId(building_pic_str, R.drawable.class);
				if (res_id != -1) {
					background.setBackgroundResource(res_id);
					curr_recommendation_res_id = res_id;
				}
				else {
					// display tower?
					res_id = Utilities.getResId("campus_tower", R.drawable.class);
					background.setBackgroundResource(res_id);
					curr_recommendation_res_id = res_id;
				}
				
				Log.d(TAG, "Looking for building picture " + building_pic_str + " with res id " + res_id);
			}
		}
		else {
			// display tower
			int res_id = Utilities.getResId("campus_tower", R.drawable.class);
			background.setBackgroundResource(res_id);
			curr_recommendation_res_id = res_id;
			
			Log.d(TAG, "Search returned " + results.get_message_status());
		}
		
		
//		if (!curr_recommendation.equals(Query.MessageStatus.NO_ROOMS_AVAIL.toString())) {
//			String temp = new Location(curr_recommendation).get_room().replaceAll("\\.", "");
//			int res_id = getResId("gdc_" + temp, R.drawable.class);
//			if (res_id != -1) {
//				background.setBackgroundResource(res_id);	// getResources().getDrawable(int id), View.setBackgroundResource(int id)
//				curr_recommendation_res_id = res_id;
//			}
//		}
		
		update_query_textview(query);
	}

//	private boolean rec_is_message_status_flag(String recommendation) {
//		if (recommendation == null) {
//			throw new IllegalArgumentException();
//		}
//		else if (recommendation.length() <= 0) {
//			recommendation = Query.MessageStatus.SEARCH_ERROR.toString();
//			return true;
//		}
//		
//		for (int i = 0; i < Constants.MESSAGE_STATUS_FLAGS.length; i++) {
//			if (recommendation.equals(Constants.MESSAGE_STATUS_FLAGS[i])) {
//				return true;
//			}
//		}
//		
//		return false;
//	}
//	
//	private void setTextViewInfo(String recommendation) {
//		if (recommendation == null || recommendation.length() <= 0) {
//			recommendation = Query.MessageStatus.SEARCH_ERROR.toString();
//		}
//		else if (!rec_is_message_status_flag(recommendation)) {
//			recommendation = "Try " + recommendation;
//		}
//		
//		TextView roomRecText = (TextView) findViewById(R.id.room_num);
//		roomRecText.setText(recommendation);		
//	}
	
	private void setTextViewInfo(String recommendation) {
		if (recommendation == null) {
			throw new IllegalArgumentException();
		}
		
		TextView roomRecText = (TextView) findViewById(R.id.room_num);
		roomRecText.setText(recommendation);
	}
	
	private void update_query_textview(Query query) {
		TextView temp = (TextView) findViewById(R.id.this_query);
		temp.setText("Current query:\n" + query.toString());
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putParcelable("this_query", this_query);
		outState.putParcelable("this_query_result", this_query_result);
		outState.putString("curr_recommendation", curr_recommendation);
		outState.putInt("curr_recommendation_res_id", curr_recommendation_res_id);
		
//		Log.d(TAG, "Orientation changed, in onSaveInstanceState(); bground res id: " + outState.getInt("curr_recommendation_res_id", -64));
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
//		startActivityForResult(new Intent(this, ActivityExit.class), 0);
		startActivity(new Intent(ActivityRoomRec.this, ActivityExit.class));
		finish();
		return;
	}

	public void getRoomRec() {
//		startActivityForResult(new Intent(this, ActivityFindRoomLater.class), 0);
		startActivity(new Intent(ActivityRoomRec.this, ActivityFindRoomLater.class));
		finish();
		return;
	}
	
	private void find_room_later() {
		Intent intent = new Intent(getApplicationContext(), ActivityFindRoomLater.class);
		intent.putExtra("this_query", this_query);
		startActivity(intent);
		finish();
		return;
	}

//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//			case R.id.ohkay:
////				exit();
//				Intent intent = new Intent(this, ActivityMain.class);
//				startActivity(intent);
//				finish();
//				break;
//			case R.id.new_Room:
////				getRoomRec();
//				search();
//				break;
//			case R.id.find_room_later:
//				Log.d(TAG, "Clicked search later button");
//				find_room_later();
//				break;
//		}
//
//	}

}		// end of file





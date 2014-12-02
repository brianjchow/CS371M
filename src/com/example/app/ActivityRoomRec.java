package com.example.app;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

/*
 * USE DialogFragment TO REOPEN DIALOG UPON ORIENTATION CHANGE
 *	// OR THIS: http://stackoverflow.com/questions/1111980/how-to-handle-screen-orientation-change-when-progress-dialog-and-background-thre
 *
 * ADD this_query_result PARCELABLE HANDLING TO onCreate()
 * OVERRIDE toString() IN Query.QueryResult; USE IN set_query_textview()
 */

// public class ActivityRoomRec extends ActionBarActivity implements View.OnClickListener {
public class ActivityRoomRec extends ActionBarActivity {

	private static final String TAG = "ActivityRoomRec";
	
	private static final String CURR_RECOMMENDATION = "curr_recommendation";
	private static final String CURR_RECOMMENDATION_INFO_TEXTVIEW = "curr_recommendation_info_textview";
	private static final String CURR_RECOMMENDATION_RES_ID = "curr_recommendation_res_id";
	
	private Query query;
	private Query.QueryResult query_result;
	
	private String curr_recommendation;
	private String curr_recommendation_info_textview;
	private int curr_recommendation_res_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_room);
		
//		TextView info_textview = (TextView) findViewById(R.id.info_textview);
//		info_textview.setMovementMethod(new ScrollingMovementMethod());
		
		if (savedInstanceState != null) {
			/*
			 * Orientation change/etc
			 * 
			 * recover the 4 instance variables
			 * update the TextViews
			 */
			
			Query query = (Query) savedInstanceState.getParcelable(Query.PARCELABLE_QUERY);
			if (query != null) {
				this.query = query;
				this.query.set_context(ActivityRoomRec.this);
			}
			else {
//				this_query = new Query(getApplicationContext());
				this.query = new Query(ActivityRoomRec.this);
			}
			
			Query.QueryResult query_result = (Query.QueryResult) savedInstanceState.getParcelable(Query.QueryResult.PARCELABLE_QUERY_RESULT);
			if (query_result != null) {
				this.query_result = query_result;
				this.curr_recommendation = savedInstanceState.getString(CURR_RECOMMENDATION, Query.MessageStatus.NO_ROOMS_AVAIL.toString());
				this.curr_recommendation_info_textview = savedInstanceState.getString(CURR_RECOMMENDATION_INFO_TEXTVIEW, "");
				this.curr_recommendation_res_id = savedInstanceState.getInt(CURR_RECOMMENDATION_RES_ID, Utilities.getResId("campus_tower", R.drawable.class));

				setTextViewInfo(this.curr_recommendation);
				update_background();
				update_info_textview(this.curr_recommendation_info_textview);
				
				// CHECK QUERYRESULT'S SEARCHTYPE HERE; UPDATE LOWER TEXTVIEW AS NECESSARY
			}
			else {
				search();
			}

		}
		else {
			/*
			 * Activity was just launched
			 * 
			 * get the 2 Query-related Parcelables from the Intent
			 * if both are not null:
			 * 		if query_result's SearchType is GET_RANDOM_ROOM:
			 * 			pick random room; set instance var
			 * 			get background pic; set instance var
			 * 		else (is GET_ROOM_DETAILS)
			 * 			get Query's building and room; set recommendation-related instance vars; display in upper TextView
			 * 			place event schedule in lower TextView
			 * else (failsafe)
			 * 		instantiate new Query
			 * 		run search()
			 * 			
			 */
			
			this.query = new Query(ActivityRoomRec.this);
			
			Bundle bundle = getIntent().getExtras();
			if (bundle != null) {
				Query query = (Query) bundle.getParcelable(Query.PARCELABLE_QUERY);
				Query.QueryResult query_result = (Query.QueryResult) bundle.getParcelable(Query.QueryResult.PARCELABLE_QUERY_RESULT);
				
				if (query != null && query_result != null) {
					this.query = query;
					this.query.set_context(ActivityRoomRec.this);
					
					this.query_result = query_result;
					
					if (this.query_result.get_search_type() == Query.SearchType.GET_RANDOM_ROOM.get_enum_val()) {
						handle_search_random_room();
					}
					
					else {
						handle_search_get_room_schedule();
					}
				}
				else {
					search();
				}
			}
			else {
				search();
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
		
		findViewById(R.id.new_room).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Calendar calendar = Calendar.getInstance();
				query.set_start_date(calendar.getTime());
				
				search();
				return;
			}
		});
		
		findViewById(R.id.find_room_later).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				find_room_later();
				return;
			}
		});
		
		findViewById(R.id.get_room_schedule).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				get_room_schedule();
				return;
			}
		});

	}
	
	// @Override protected void onActivityResult(int requestCode, int resultCode, Intent intent) { }

	private void search() {
		search(this.query);
	}
	
	private void search(Query query) {
		
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start();
		
		this.query_result = query.search();
		this.curr_recommendation = query_result.get_random_room();
		
		stopwatch.stop();
		handle_search_random_room();
	}
	
	// http://androidcocktail.blogspot.in/2012/05/solving-bitmap-size-exceeds-vm-budget.html
	private void update_background() {
//		final int TRANSPARENCY_VAL = 165;
		
		View background = findViewById(R.id.background);
		if (this.query_result.get_message_status().equals(Query.MessageStatus.SEARCH_SUCCESS.toString())) {
			
			String building_name = this.query_result.get_building_name();
			if (building_name.equalsIgnoreCase(Constants.GDC)) {
//				String temp = new Location(curr_recommendation).get_room().replaceAll("\\.", "");
				String building_pic_str = this.curr_recommendation.toLowerCase(Constants.DEFAULT_LOCALE).replaceAll("\\.", "").replaceAll("\\s+", "_");
				int res_id = Utilities.getResId(building_pic_str, R.drawable.class);
				if (res_id != -1) {
					background.setBackgroundResource(res_id);
					this.curr_recommendation_res_id = res_id;
					
//					Drawable background_image = background.getBackground();
//					background_image.setAlpha(TRANSPARENCY_VAL);	// 0xcc == 204
				}
				else {
					// display tower?
					res_id = Utilities.getResId("campus_tower", R.drawable.class);
					background.setBackgroundResource(res_id);
					this.curr_recommendation_res_id = res_id;
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
					this.curr_recommendation_res_id = res_id;
					
//					Drawable background_image = background.getBackground();
//					background_image.setAlpha(TRANSPARENCY_VAL);	// 0xcc == 204
				}
				else {
					// display tower?
					res_id = Utilities.getResId("campus_tower", R.drawable.class);
					background.setBackgroundResource(res_id);
					this.curr_recommendation_res_id = res_id;
				}
				
				Log.d(TAG, "Looking for building picture " + building_pic_str + " with res id " + res_id);
			}
		}
		else {
			// display tower
			int res_id = Utilities.getResId("campus_tower", R.drawable.class);
			background.setBackgroundResource(res_id);
			this.curr_recommendation_res_id = res_id;
			
			Log.d(TAG, "Search returned " + this.query_result.get_message_status());
		}
		
		// http://stackoverflow.com/questions/4968883/opacity-on-a-background-drawable-image-in-view-using-xml-layout
		// http://stackoverflow.com/questions/2838757/how-to-set-opacity-alpha-for-view-in-android
//		Drawable background_image = background.getBackground();
//		background_image.setAlpha(165);	// 0xcc == 204
	}
	
	private void setTextViewInfo(String recommendation) {
		if (recommendation == null) {
			throw new IllegalArgumentException();
		}
		
		TextView roomRecText = (TextView) findViewById(R.id.room_num);
		roomRecText.setText(recommendation);
	}
	
	private void update_info_textview(String text) {
		if (text == null) {
			throw new IllegalArgumentException();
		}
		TextView temp = (TextView) findViewById(R.id.info_textview);
		temp.setText(text);
	}
	
	private void handle_search_random_room() {
		this.curr_recommendation = this.query_result.get_random_room();
		
		final String TAB = "    ";
		
		StringBuilder msg = new StringBuilder();
		
		String message_status = this.query_result.get_message_status();
		
		if (message_status.equals(Query.MessageStatus.SEARCH_ERROR.toString())) {
			msg.append("We're not sure why this is happening, but shoot us an email containing" +
					"the information below and we'll get it fixed. Thanks!\n\n" + this.query.toString());
		}
		else if (message_status.equals(Query.MessageStatus.SEARCH_SUCCESS.toString())) {
			List<String> results = this.query_result.get_results();
			
//			Log.d(TAG, results.toString());
			
			if (results.size() == 1) {
				msg.append("Search found one room available.\n\n");
			}
			else {
				msg.append("Search found " + results.size() + " rooms available.\n\n");
			}
			
			String search_building = this.query.get_option_search_building();
			int capacity = this.query.get_option_capacity();
			
			msg.append("Search criteria:\n");
			msg.append(TAB + "Building: " + search_building + "\n");
			msg.append(TAB + "Start date: " + this.query.get_start_date().toString() + "\n");
			msg.append(TAB + "Duration: at least " + this.query.get_duration() + " minute(s)\n");
			
			if (capacity > 0) {
				msg.append(TAB + "Capacity: at least " + capacity + " people\n");
			}
			else {
				msg.append(TAB + "Capacity: no preference\n");
			}
			
			if (Utilities.str_is_gdc(search_building)) {
				msg.append(TAB + "Must have power plugs:\t" + this.query.get_option_power() + "\n");
			}
			
			msg.append("\n");
			msg.append("All rooms available under identical search criteria:\n");
			
			if (results.size() == 1) {
				msg.append(TAB + "None\n");
			}
			else {
				for (String room : results) {
					msg.append(TAB + search_building + " " + room + "\n");
				}
			}
		}
		else if (message_status.equals(Query.MessageStatus.GO_HOME.toString())) {
			msg.append("You're gonna fail that exam tomorrow anyway.");
		}
		else if (!message_status.equals(Query.MessageStatus.NO_ROOMS_AVAIL.toString())) {
			msg.append("Be sure you have the appropriate authorization (if any) to enter and use" +
					" the rooms in " + this.query.get_option_search_building() + ".");
		}

		update_info_textview(msg.toString());
		this.curr_recommendation_info_textview = msg.toString();
		
		setTextViewInfo(this.curr_recommendation);
		update_background();
		
//		Log.d(TAG, "PROCESSED regular random-room search - " + msg.toString());
	}
	
	private void handle_search_get_room_schedule() {
		Room search_room;
		String search_building_str = this.query.get_option_search_building();
		
		String curr_course_schedule = this.query.get_current_course_schedule();
		if (curr_course_schedule.equals(Query.MessageStatus.SUMMER.toString())) {
			this.curr_recommendation = curr_course_schedule;
			setTextViewInfo(Query.MessageStatus.ALL_ROOMS_AVAIL.toString());
			update_info_textview("Campus closed for holidays; check that you have permission before entering.");
		}
		else if (curr_course_schedule.equals(Query.MessageStatus.HOLIDAY.toString())) {
			this.curr_recommendation = curr_course_schedule;
			setTextViewInfo("Some rooms available");
			update_info_textview("Summer hours; check course schedule on UTDirect for more information.");
		}
		else {
			Building search_building = Building.get_instance(ActivityRoomRec.this, search_building_str, curr_course_schedule);
			search_room = search_building.get_room(this.query.get_option_search_room());

			if (search_room == null) {
				throw new IllegalStateException("Fatal error: corrupted Building object\n\n" + this.query.toString());
			}

			int capacity = search_room.get_capacity();
			List<String> events = this.query_result.get_results();
			
//			Log.d(TAG, search_room.toString());
//			Log.d(TAG, events.toString());
			
			DateFormat format = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.ENGLISH);
			String date_str = format.format(this.query.get_start_date());
			
			StringBuilder msg = new StringBuilder();
			
			if (Utilities.str_is_gdc(search_building_str)) {
				msg.append("Room type: " + search_room.get_type() + "\n");
				msg.append("Capacity: " + capacity + " people\n");
				msg.append("Power plugs: " + search_room.get_has_power() + "\n");
				msg.append("\n");
			}
			else {
				if (capacity > 0) {
					msg.append("Capacity: " + capacity + " people\n");
				}
				else {
					msg.append("Capacity: unknown\n");
				}
				msg.append("\n");
			}
			
			if (events.size() <= 0) {
				msg.append("There are no events scheduled on " + date_str + ".\n\n");
			}
			else {
				if (events.size() == 1) {
					msg.append("There is one event scheduled on " + date_str + ":\n\n");
				}
				else {
					msg.append("There are " + events.size() + " events scheduled on " + date_str + ":\n\n");
				}
				
				for (String curr_event : events) {
//					curr_event = curr_event.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(", ", "");
					msg.append(curr_event);
				}
			}
			
			this.curr_recommendation = search_building_str + " " + this.query.get_option_search_room();
			setTextViewInfo(this.curr_recommendation);
			update_info_textview(msg.toString());
			this.curr_recommendation_info_textview = msg.toString();
		}
		
		update_background();
		
//		Log.d(TAG, "PROCESSED room course schedule search - " + msg.toString());
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putParcelable(Query.PARCELABLE_QUERY, this.query);
		outState.putParcelable(Query.QueryResult.PARCELABLE_QUERY_RESULT, this.query_result);
		outState.putString(CURR_RECOMMENDATION, this.curr_recommendation);
		outState.putString(CURR_RECOMMENDATION_INFO_TEXTVIEW, this.curr_recommendation_info_textview);
		outState.putInt(CURR_RECOMMENDATION_RES_ID, this.curr_recommendation_res_id);
		
//		Log.d(TAG, "Orientation changed, in onSaveInstanceState(); bground res id: " + outState.getInt("curr_recommendation_res_id", -64));
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
		startActivity(new Intent(ActivityRoomRec.this, ActivityFindRoomLater.class));
		finish();
	}
	
	private void find_room_later() {
		Intent intent = new Intent(ActivityRoomRec.this, ActivityFindRoomLater.class);
		intent.putExtra(Query.PARCELABLE_QUERY, this.query);
		startActivity(intent);
		finish();
	}
	
	private void get_room_schedule() {
		Intent intent = new Intent(ActivityRoomRec.this, ActivityGetRoomSchedule.class);
		intent.putExtra(Query.PARCELABLE_QUERY, this.query);
		startActivity(intent);
		finish();
	}
	
	private void exitApp() {
		Intent intent = new Intent(ActivityRoomRec.this, ActivityMain.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra(Constants.EXIT, true);
		startActivity(intent);
		finish();
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
//			case R.id.new_room:
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





package com.example.app;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
 * BLOCK ROTATION WHEN DIALOG OPEN
 * 	// http://stackoverflow.com/questions/3611457/android-temporarily-disable-orientation-changes-in-an-activity
 * 
 * USE DialogFragment TO REOPEN DIALOG UPON ORIENTATION CHANGE
 * 	// http://stackoverflow.com/questions/7557265/prevent-dialog-dismissal-on-screen-rotation-in-android
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
		setContentView(R.layout.activity_room_rec);
		
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
				this.query = new Query(ActivityRoomRec.this);
			}
			
			Query.QueryResult query_result = (Query.QueryResult) savedInstanceState.getParcelable(Query.QueryResult.PARCELABLE_QUERY_RESULT);
			if (query_result != null) {
				this.query_result = query_result;
				this.curr_recommendation = savedInstanceState.getString(CURR_RECOMMENDATION, Query.SearchStatus.NO_ROOMS_AVAIL.toString());
				this.curr_recommendation_info_textview = savedInstanceState.getString(CURR_RECOMMENDATION_INFO_TEXTVIEW, "");
				this.curr_recommendation_res_id = savedInstanceState.getInt(CURR_RECOMMENDATION_RES_ID, Utilities.getResId("campus_tower", R.drawable.class));

				setTextViewInfo(this.curr_recommendation);
				update_background();
				update_info_textview(this.curr_recommendation_info_textview);
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
		
		String orientation = Utilities.getRotation(ActivityRoomRec.this);
		if (orientation.equals("portrait") || orientation.equals("reverse portrait")) {
			findViewById(R.id.ohkay).setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
//					Intent intent = new Intent(ActivityFindRoomLater.this, ActivityMain.class);
//					startActivity(intent);
					finish();
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
//		this.curr_recommendation = query_result.get_random_room();
		
		stopwatch.stop();
		handle_search_random_room();
	}

	private boolean needs_truncation_gdc_room(String room) {
		if (room == null) {
			return false;
//			throw new IllegalArgumentException();
		}

		if (room.equalsIgnoreCase("gdc 2.210") || room.equalsIgnoreCase("gdc 2.410")) {
			return true;
		}		
		return false;
	}
	
	// http://androidcocktail.blogspot.in/2012/05/solving-bitmap-size-exceeds-vm-budget.html
	private void update_background() {
		
		View background = findViewById(R.id.background);
		if (this.query_result.get_search_status().equals(Query.SearchStatus.SEARCH_SUCCESS.toString())) {
			
			String building_name = this.query_result.get_building_name();
			if (building_name.equalsIgnoreCase(Constants.GDC)) {
				
				String room_rec = this.curr_recommendation.toLowerCase(Constants.DEFAULT_LOCALE);
				if (needs_truncation_gdc_room(room_rec)) {
					room_rec = room_rec.substring(0, 8);		// substring length guaranteed to be 9 if true (see needs_truncation_gdc_room())
				}
				
				String building_pic_str = room_rec.replaceAll("\\.", "").replaceAll("\\s+", "_");
				int res_id = Utilities.getResId(building_pic_str, R.drawable.class);
				if (res_id != -1) {
					background.setBackgroundResource(res_id);
					this.curr_recommendation_res_id = res_id;
				}
				else {
					res_id = Utilities.getResId("campus_gdc", R.drawable.class);
					background.setBackgroundResource(res_id);
					this.curr_recommendation_res_id = res_id;
				}
				
				Log.d(TAG, "Looking for building picture " + building_pic_str + " with res id " + res_id);
			}
			else {
				String building_pic_str = "campus_" + building_name.toLowerCase(Constants.DEFAULT_LOCALE);
				int res_id = Utilities.getResId(building_pic_str, R.drawable.class);
				if (res_id != -1) {
					background.setBackgroundResource(res_id);
					this.curr_recommendation_res_id = res_id;
				}
				else {
					res_id = Utilities.getResId("campus_tower", R.drawable.class);
					background.setBackgroundResource(res_id);
					this.curr_recommendation_res_id = res_id;
				}
				
				Log.d(TAG, "Looking for building picture " + building_pic_str + " with res id " + res_id);
			}
		}
		else {
			int res_id = Utilities.getResId("campus_tower", R.drawable.class);
			background.setBackgroundResource(res_id);
			this.curr_recommendation_res_id = res_id;
			
			Log.d(TAG, "Search returned " + this.query_result.get_search_status());
		}
	}
	
	private void setTextViewInfo(String recommendation) {
		if (recommendation == null) {
			throw new IllegalArgumentException();
		}

		TextView roomRecText = (TextView) findViewById(R.id.room_num);
		
		if (recommendation.toUpperCase(Constants.DEFAULT_LOCALE).equals("GDC 2.21") || recommendation.toUpperCase(Constants.DEFAULT_LOCALE).equals("GDC 2.41")) {
			roomRecText.setText(recommendation + "0");
		}
		else {
			roomRecText.setText(recommendation);
		}
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
		
		String message_status = this.query_result.get_search_status();
		
		if (message_status.equals(Query.SearchStatus.SEARCH_ERROR.toString())) {
			msg.append("We're not sure why this is happening, but shoot us an email containing" +
					"the information below and we'll get it fixed. Thanks!\n\n" + this.query.toString());
		}
//		else if (message_status.equals(Query.SearchStatus.SEARCH_SUCCESS.toString())) {
		else {
			String[] curr_rec_split = this.curr_recommendation.split("\\s");
			if (curr_rec_split.length > 1 && message_status.equals(Query.SearchStatus.SEARCH_SUCCESS.toString())) {
				this.query.set_option_search_room(curr_rec_split[1]);
			}
			
			List<String> results = this.query_result.get_results();
			
			if (results.size() == 1) {
				msg.append("Search found one room available.\n\n");
			}
			else {
				msg.append("Search found " + results.size() + " rooms available.\n\n");
			}
			
			if (message_status.equals(Query.SearchStatus.HOLIDAY.toString())) {
				this.curr_recommendation = "Some or all rooms available";
				msg.append("NOTE: search occurs during final exams or the holidays. Final exam schedules are NOT considered in the search results below.\n\n");
			}
			else if (message_status.equals(Query.SearchStatus.SUMMER.toString())) {
				this.curr_recommendation = "Some or all rooms available";
				msg.append("NOTE: search occurs during summer hours; consult the course schedule on UTDirect for more information.\n\n");
			}
			
			if (!Constants.SHORT_CIRCUIT_SEARCH_FOR_ROOM) {
				if (this.query.search_is_on_weekend()) {
					msg.append("NOTE: search occurs partly through or during the weekend; you may not be able to enter without appropriate authorization.\n\n");
				}
				else if (this.query.search_is_at_night()) {
					msg.append("NOTE: search occurs partly through or during after-hours; you may not be able to enter without appropriate authorization.\n\n");
				}
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
				boolean must_have_power = this.query.get_option_power();
				String choice = "no";
				if (must_have_power) {
					choice = "yes";
				}
				
				msg.append(TAB + "Must have power plugs: " + choice + "\n");
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
//		else if (message_status.equals(Query.SearchStatus.GO_HOME.toString())) {
//			msg.append("You're gonna fail that exam tomorrow anyway.");
//		}
//		else if (!message_status.equals(Query.SearchStatus.NO_ROOMS_AVAIL.toString())) {
//			msg.append("Be sure you have the appropriate authorization (if any) to enter and use" +
//					" the rooms in " + this.query.get_option_search_building() + ".");
//		}

		update_info_textview(msg.toString());
		this.curr_recommendation_info_textview = msg.toString();
		
		setTextViewInfo(this.curr_recommendation);
		
		update_background();
	}
	
	private void handle_search_get_room_schedule() {
		Room search_room;
		String search_building_str = this.query.get_option_search_building();
		
		String curr_course_schedule = this.query.get_current_course_schedule();
//		if (curr_course_schedule.equals(Query.SearchStatus.SUMMER.toString())) {
//			this.curr_recommendation = curr_course_schedule;
//			setTextViewInfo(Query.SearchStatus.ALL_ROOMS_AVAIL.toString());
//			update_info_textview("Summer schedule; consult course schedule on UTDirect for more information.");
//		}
//		else if (curr_course_schedule.equals(Query.SearchStatus.HOLIDAY.toString())) {
//			this.curr_recommendation = curr_course_schedule;
//			setTextViewInfo("Some or all rooms available");
//			update_info_textview("Finals schedule or campus closed for holidays.");
//		}
//		else {
			Building search_building = Building.get_instance(ActivityRoomRec.this, search_building_str, curr_course_schedule);
			search_room = search_building.get_room(this.query.get_option_search_room());

			if (search_room == null) {
				throw new IllegalStateException("Fatal error: corrupted Building object\n\n" + this.query.toString());
			}

			int capacity = search_room.get_capacity();
			List<String> events = this.query_result.get_results();
			
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
			else if (events.size() == 1) {
				msg.append("There is one event scheduled on " + date_str + ":\n\n");
			}
			else {
				msg.append("There are " + events.size() + " events scheduled on " + date_str + ":\n\n");
			}
						
			if (curr_course_schedule.equals(Query.SearchStatus.HOLIDAY.toString())) {
				this.curr_recommendation = "Some or all rooms available";
				msg.append("NOTE: search occurs during final exams or the holidays. Final exam schedules are NOT considered in the search results below.\n\n");
			}
			else if (curr_course_schedule.equals(Query.SearchStatus.SUMMER.toString())) {
				this.curr_recommendation = "Some or all rooms available";
				msg.append("NOTE: search occurs during summer hours; consult the course schedule on UTDirect for more information.\n\n");
			}
			
			for (String curr_event : events) {
				msg.append(curr_event);
			}

			this.curr_recommendation = search_building_str + " " + this.query.get_option_search_room();
			setTextViewInfo(this.curr_recommendation);
			update_info_textview(msg.toString());
			this.curr_recommendation_info_textview = msg.toString();
//		}
		
		update_background();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		
		outState.putParcelable(Query.PARCELABLE_QUERY, this.query);
		outState.putParcelable(Query.QueryResult.PARCELABLE_QUERY_RESULT, this.query_result);
		outState.putString(CURR_RECOMMENDATION, this.curr_recommendation);
		outState.putString(CURR_RECOMMENDATION_INFO_TEXTVIEW, this.curr_recommendation_info_textview);
		outState.putInt(CURR_RECOMMENDATION_RES_ID, this.curr_recommendation_res_id);
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

}		// end of file





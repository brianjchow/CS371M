package com.example.app;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.SortedSet;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

public class ActivityGetRoomSchedule extends ActionBarActivity {

	private static final String TAG = "ActivityGetRoomSchedule";
	
	private Query query;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_get_room_schedule);
		
		if (savedInstanceState != null) {
			Query query = (Query) savedInstanceState.getParcelable(Query.PARCELABLE_QUERY);
			if (query != null) {
				this.query = query;
				this.query.set_context(ActivityGetRoomSchedule.this);
			}
			else {
				this.query = new Query(ActivityGetRoomSchedule.this);
			}
		}
		else {
//			this.query = new Query(getApplicationContext());
			this.query = new Query(ActivityGetRoomSchedule.this);
			
			Bundle bundle = getIntent().getExtras();
			if (bundle != null) {
				Query query = (Query) bundle.getParcelable(Query.PARCELABLE_QUERY);
				if (query != null) {
//					Log.d(TAG, "Using transmitted parcelable\n" + query.toString());
					this.query = query;
					this.query.set_context(ActivityGetRoomSchedule.this);
				}
			}
		}

		setSearchBuildingSpinnerOnItemSelectedListener();
		
		setDateButtonOnClickListener();
						
	}		// end onCreate()

	private void setSearchBuildingSpinnerOnItemSelectedListener() {
		final Spinner spinner = (Spinner) findViewById(R.id.choose_building_spinner);
		final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ActivityGetRoomSchedule.this, R.array.campus_buildings, android.R.layout.simple_spinner_dropdown_item);	// or android.R.layout.simple_spinner_item
//		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(ActivityFindRoomLater.this, android.R.layout.simple_spinner_item, Constants.CAMPUS_BUILDINGS);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		
//		spinner.setSelection(Constants.CAMPUS_BUILDINGS.get(Constants.GDC));
		
		setSearchBuildingButtonSpinnerOnItemSelectedListener(spinner);
	}

	private void setSearchBuildingButtonSpinnerOnItemSelectedListener(final Spinner spinner) {

		spinner.setSelection(Constants.CAMPUS_BUILDINGS.get(this.query.get_option_search_building()));
		
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				Log.d(TAG, "In onItemSelected(); selected " + spinner.getSelectedItem());
				
				String selected_item = spinner.getSelectedItem().toString().substring(0, 3).toUpperCase(Constants.DEFAULT_LOCALE);
				
				query.set_option_search_building(selected_item);
				
				setSearchRoomSpinnerOnItemSelectedListener();

			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				Log.d(TAG, "In onNothingSelected(); nothing selected in spinner");
			}
		});
		
	}
	
	private void setSearchRoomSpinnerOnItemSelectedListener() {
		String no_rooms_found = getResources().getString(R.string.no_rooms_found);
		String[] rooms;
		
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start();
		
		String curr_course_schedule = this.query.get_current_course_schedule();
		if (curr_course_schedule == null) {
			rooms = new String[] { no_rooms_found };
		}
		else {
			Building search_building = Building.get_instance(ActivityGetRoomSchedule.this, this.query.get_option_search_building(), curr_course_schedule);
			SortedSet<String> roomset = search_building.get_keyset();
			
			if (roomset.size() <= 0) {
				rooms = new String[] { no_rooms_found };
			}
			else {
				rooms = roomset.toArray(new String[roomset.size()]);
			}
		}
		
		Button get_room_button = (Button) findViewById(R.id.get_room);
		if (rooms.length == 0 || (rooms.length == 1 && rooms[0].equals(no_rooms_found))) {
			get_room_button.setText(getResources().getString(R.string.home));
			get_room_button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(ActivityGetRoomSchedule.this, ActivityMain.class);
					startActivity(intent);
					finish();
					return;
				}
			});
		}
		else {
			get_room_button.setText(getResources().getString(R.string.get_room_enthusiastic));
			get_room_button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					get_room_rec();
					return;
				}
			});
		}
		
		final Spinner spinner = (Spinner) findViewById(R.id.choose_room_spinner);
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(ActivityGetRoomSchedule.this, android.R.layout.simple_spinner_dropdown_item, rooms);	
		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		
		if (rooms.length == 0 || (rooms.length == 1 && rooms[0].equals(no_rooms_found))) {
			spinner.setSelection(0);
		}
		else {
			String search_room = this.query.get_option_search_room();
			if (search_room.equals(Constants.RANDOM)) {
				this.query.set_option_search_room(rooms[0]);
				spinner.setSelection(0);
			}
			else {
				for (int i = 0; i < rooms.length; i++) {
					if (rooms[i].equals(search_room)) {
						spinner.setSelection(i);
						break;
					}
				}
			}
			
			setSearchRoomSpinnerOnItemSelectedListener(spinner);
		}
		
		stopwatch.stop();
		if (Constants.DEBUG) {
			Toast.makeText(ActivityGetRoomSchedule.this, "Took " + stopwatch.time() + " seconds to read from DB and populate rooms spinner", Toast.LENGTH_SHORT).show();
		}
	}

	private void setSearchRoomSpinnerOnItemSelectedListener(final Spinner spinner) {

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				String selected_item = spinner.getSelectedItem().toString();
				
				query.set_option_search_room(selected_item);

				Log.d(TAG, "In onItemSelected(); selected room " + selected_item);
				
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				Log.d(TAG, "In onNothingSelected(); nothing selected in spinner");
			}
		});
		
	}
	
	private void setDateButtonOnClickListener() {
		
		final Date start_date = this.query.get_start_date();
		set_start_date_button_text(start_date);

		findViewById(R.id.datepicker_button).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Calendar calendar = Calendar.getInstance();
				
				calendar.setTime(start_date);
				
				DatePickerDialog datepicker_dialog = new DatePickerDialog(ActivityGetRoomSchedule.this, datepicker_dialog_listener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
//				DatePickerDialog datepicker_dialog = new DatePickerDialog(ActivityFindRoomLater.this, datepicker_dialog_listener, selected_year, selected_month - 1, selected_day);
				if (!Constants.DEBUG) {
					datepicker_dialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
					
					Calendar temp = Calendar.getInstance();
					Date date = calendar.getTime();
					int year = calendar.get(Calendar.YEAR);
					if (Utilities.date_is_during_spring(date)) {
						date = Utilities.get_date(Constants.FALL_END_MONTH, Constants.FALL_END_DAY, year, 2359);
						temp.setTime(date);
						datepicker_dialog.getDatePicker().setMaxDate(temp.getTimeInMillis());
					}
					else {
						date = Utilities.get_date(Constants.SPRING_END_MONTH, Constants.SPRING_END_DAY, year + 1, 2359);
						temp.setTime(date);
						datepicker_dialog.getDatePicker().setMaxDate(temp.getTimeInMillis());
					}
					
				}
				datepicker_dialog.show();
			}
		});
	}
	
	private DatePickerDialog.OnDateSetListener datepicker_dialog_listener = new OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//			Toast.makeText(FindRoomLaterActivity.this, "Date selected: " + year + "/" + (monthOfYear + 1) + "/" + dayOfMonth, Toast.LENGTH_SHORT).show();
			
			query.set_start_date((monthOfYear + 1), dayOfMonth, year);
			set_start_date_button_text(query.get_start_date());
			
//			long time = this.query.get_start_date().getTime();
//			time = (time / 1000) % SECS_IN_DAY;
//			time /= 60;
//			int time_remaining_in_day_mins = (int) time;
//			if (selected_duration > time_remaining_in_day_mins) {
//				selected_duration = time_remaining_in_day_mins;
//				update_this.query();
//			}
		}
	};

	private void set_start_date_button_text(Date date) {
		if (date == null) {
			throw new IllegalArgumentException();
		}
		
		DateFormat format = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.ENGLISH);
		String date_str = format.format(date);
		
		Button start_date_button = (Button) findViewById(R.id.datepicker_button);
		start_date_button.setText(date_str);
	}
	
	
	
	
	
	
	
	
	
	
	
	private void get_room_rec() {
		Query.QueryResult query_result = this.query.search_get_schedule_by_room();
		
		Intent intent = new Intent(getApplicationContext(), ActivityRoomRec.class);
		intent.putExtra(Query.PARCELABLE_QUERY, this.query);
		intent.putExtra(Query.QueryResult.PARCELABLE_QUERY_RESULT, query_result);
		
		startActivity(intent);
		finish();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putParcelable(Query.PARCELABLE_QUERY, this.query);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_get_room_schedule, menu);
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
}

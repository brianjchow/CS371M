package com.example.app;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

//public class FindRoomLaterActivity extends ActionBarActivity implements View.OnClickListener {
public class ActivityFindRoomLater extends FragmentActivity {	//  implements OnDateSetListener, TimePickerDialog.OnTimeSetListener

	private static final String TAG = "ActivityFindRoomLater";

	private static final int SECS_IN_DAY = 86400;		// 24 * 60 * 60

	private Query query;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_room_later);

		if (savedInstanceState != null) {
			Query query = (Query) savedInstanceState.getParcelable(Query.PARCELABLE_QUERY);
			if (query != null) {
				this.query = query;
				this.query.set_context(ActivityFindRoomLater.this);
			}
			else {
//				this.query = new Query(getApplicationContext());
				this.query = new Query(ActivityFindRoomLater.this);
			}
		}
		else {
//			this.query = new Query(getApplicationContext());
			this.query = new Query(ActivityFindRoomLater.this);
			
			Bundle bundle = getIntent().getExtras();
			if (bundle != null) {
				Query query = (Query) bundle.getParcelable(Query.PARCELABLE_QUERY);
				if (query != null) {
//					Log.d(TAG, "Using transmitted parcelable\n" + query.toString());
					this.query = query;
//					this.query.set_context(getApplicationContext());
					this.query.set_context(ActivityFindRoomLater.this);
				}
			}
		}
		
		setSearchBuildingSpinnerOnItemSelectedListener();
		
		setDateButtonOnClickListener();
		
		setTimeButtonOnClickListener();

		setMinDurationOnClickListener();
		
		setMinCapacityOnClickListener();
		
		setCheckBoxOnCheckedChangeListener();
		
//        if (savedInstanceState != null) {
//            DatePickerDialog dpd = (DatePickerDialog) getSupportFragmentManager().findFragmentByTag(DATEPICKER_TAG);
//            if (dpd != null) {
//                dpd.setOnDateSetListener(this);
//            }
//
//            TimePickerDialog tpd = (TimePickerDialog) getSupportFragmentManager().findFragmentByTag(TIMEPICKER_TAG);
//            if (tpd != null) {
//                tpd.setOnTimeSetListener(this);
//            }
//        }
		
		findViewById(R.id.get_Room).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getRoomRec();
			}
		});
		
	}		// end onCreate()




	private void setMinDurationOnClickListener() {
		set_min_duration_button_text(this.query.get_duration());
		
		findViewById(R.id.min_duration_button).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				show_duration_picker();
			}
		});		
	}

	private void setMinCapacityOnClickListener() {
		set_min_capacity_button_text(this.query.get_option_capacity());
		
		findViewById(R.id.min_capacity_button).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				show_capacity_picker();
			}
		});
	}

	private void setCheckBoxOnCheckedChangeListener() {

		// http://stackoverflow.com/questions/8386832/android-checkbox-listener
		CheckBox has_power = (CheckBox) findViewById(R.id.has_power);
		if (this.query.get_option_search_building().equalsIgnoreCase(Constants.GDC)) {
			has_power.setChecked(this.query.get_option_power());
		}
		else {
			has_power.setVisibility(View.GONE);
		}
		has_power.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					query.set_option_power(Boolean.valueOf(true));
				}
				else {
					query.set_option_power(Boolean.valueOf(false));
				}
//				Toast.makeText(FindRoomLaterActivity.this, "Selected power option: " + selected_power, Toast.LENGTH_SHORT).show();
			}
		});
	}

	private void setSearchBuildingSpinnerOnItemSelectedListener() {
		String[] buildings = getResources().getStringArray(R.array.campus_buildings);
		
		final Spinner spinner = (Spinner) findViewById(R.id.choose_building_spinner);
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(ActivityFindRoomLater.this, android.R.layout.simple_spinner_dropdown_item, buildings) {
			
			public View getView(int position, View convertView, ViewGroup parent) {
				View v = super.getView(position, convertView, parent);
				((TextView) v).setTextColor(getResources().getColorStateList(R.color.white));
				return v;
			}
			
		};
		
//		final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ActivityFindRoomLater.this, R.array.campus_buildings, android.R.layout.simple_spinner_dropdown_item);	// or android.R.layout.simple_spinner_item
//		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(ActivityFindRoomLater.this, android.R.layout.simple_spinner_item, Constants.CAMPUS_BUILDINGS);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		
//		spinner.setSelection(Constants.CAMPUS_BUILDINGS.get(Constants.GDC));
		
		setSearchBuildingSpinnerOnItemSelectedListener(spinner);
	}

	// http://stackoverflow.com/questions/21485590/the-final-local-variable-cannot-be-assigned-since-it-is-defined-in-an-enclosing
	private void setSearchBuildingSpinnerOnItemSelectedListener(final Spinner spinner) {

		spinner.setSelection(Constants.CAMPUS_BUILDINGS.get(this.query.get_option_search_building()));
		
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			CheckBox has_power = (CheckBox) findViewById(R.id.has_power);
			
			@Override
			public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
				Log.d(TAG, "In onItemSelected(); selected " + spinner.getSelectedItem());
				
				String selected_item = spinner.getSelectedItem().toString().substring(0, 3).toUpperCase(Constants.DEFAULT_LOCALE);
				
				query.set_option_search_building(selected_item);

				if (selected_item.equalsIgnoreCase(Constants.GDC)) {
					has_power.setVisibility(View.VISIBLE);
					has_power.setChecked(query.get_option_power());

				}
				else {
					has_power.setVisibility(View.GONE);
				}

			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parentView) {
				Log.d(TAG, "In onNothingSelected(); nothing selected in spinner");
			}
		});
		
	}

	private void setTimeButtonOnClickListener() {
		
		final Date start_date = this.query.get_start_date();
		set_start_time_button_text(start_date);

		findViewById(R.id.timepicker_button).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Calendar calendar = Calendar.getInstance();
				
				calendar.setTime(start_date);
				
				Dialog timepicker_dialog = new TimePickerDialog(ActivityFindRoomLater.this, timepicker_dialog_listener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
//				Dialog timepicker_dialog = new TimePickerDialog(ActivityFindRoomLater.this, timepicker_dialog_listener, selected_hour, selected_minute, true);
				timepicker_dialog.show();
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
				
				Calendar curr_start_date = Calendar.getInstance();
				curr_start_date.setTime(start_date);
				
				DatePickerDialog datepicker_dialog = new DatePickerDialog(ActivityFindRoomLater.this, datepicker_dialog_listener, curr_start_date.get(Calendar.YEAR), curr_start_date.get(Calendar.MONTH), curr_start_date.get(Calendar.DAY_OF_MONTH));
//				DatePickerDialog datepicker_dialog = new DatePickerDialog(ActivityFindRoomLater.this, datepicker_dialog_listener, selected_year, selected_month - 1, selected_day);
				
				if (!Constants.DEBUG) {
					DatePicker datepicker = datepicker_dialog.getDatePicker();
					datepicker.setMinDate(calendar.getTimeInMillis());
					
					Date date = calendar.getTime();
					int year = calendar.get(Calendar.YEAR);
					
					Calendar temp = Calendar.getInstance();
					if (Utilities.date_is_during_spring(date)) {
						
						int end_month = Constants.FALL_END_MONTH;
						int end_day = Constants.FALL_END_DAY;
						if (Constants.DISABLE_SEARCHES_NEXT_SEMESTER) {
							end_month = Constants.SPRING_END_MONTH;
							end_day = Constants.SPRING_END_DAY;
						}
						
						date = Utilities.get_date(end_month, end_day, year, 2359);
						temp.setTime(date);
						datepicker.setMaxDate(temp.getTimeInMillis());
					}
					else {
						
						int end_month = Constants.SPRING_END_MONTH;
						int end_day = Constants.SPRING_END_DAY;
						if (Constants.DISABLE_SEARCHES_NEXT_SEMESTER) {
							end_month = Constants.FALL_END_MONTH;
							end_day = Constants.FALL_END_DAY;
							year--;
						}
						
						date = Utilities.get_date(end_month, end_day, year + 1, 2359);
						temp.setTime(date);
						datepicker.setMaxDate(temp.getTimeInMillis());
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
	
	private TimePickerDialog.OnTimeSetListener timepicker_dialog_listener = new OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//			Toast.makeText(FindRoomLaterActivity.this, "Time selected: " + hourOfDay + ":" + minute, Toast.LENGTH_SHORT).show();
			
			query.set_start_time(hourOfDay, minute);
			set_start_time_button_text(query.get_start_date());
		}
	};

	// http://stackoverflow.com/questions/17805040/how-to-create-a-number-picker-dialog
	// http://stackoverflow.com/questions/16968111/android-numberpicker-doesn%C2%B4t-work
	private void show_capacity_picker() {
		final Dialog dialog = new Dialog(ActivityFindRoomLater.this);
		dialog.setTitle(R.string.set_minimum_capacity);
		dialog.setContentView(R.layout.numberpicker_dialog);
		Button set_button = (Button) dialog.findViewById(R.id.set_button);
		Button cancel_button = (Button) dialog.findViewById(R.id.cancel_button);
		
		final String zero_capacity = ActivityFindRoomLater.this.getResources().getString(R.string.zero_capacity);
		final NumberPicker np = (NumberPicker) dialog.findViewById(R.id.number_picker);
		String[] nums = new String[Constants.MAX_CAPACITY + 1];
		nums[0] = zero_capacity;
		for (int i = 1; i < nums.length; i++) {
			nums[i] = Integer.toString(i);
		}
		
		np.setMinValue(0);
		np.setMaxValue(nums.length - 1);
		np.setWrapSelectorWheel(false);
		np.setDisplayedValues(nums);
		np.setValue(this.query.get_option_capacity());
		np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//				Toast.makeText(FindRoomLaterActivity.this, "Old val: " + oldVal + "; new val: " + newVal, Toast.LENGTH_SHORT).show();
			}
		});
		
		set_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (Constants.DEBUG) {
					Toast.makeText(ActivityFindRoomLater.this, "Capacity selected: " + np.getValue(), Toast.LENGTH_SHORT).show();
				}
				
				Integer val = Integer.valueOf(np.getValue());
				query.set_option_capacity(val);
				set_min_capacity_button_text(val);
				
				dialog.dismiss();
			}
		});
		
		cancel_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		dialog.show();
	}
	
	private void show_duration_picker() {
		final Dialog dialog = new Dialog(ActivityFindRoomLater.this);
		dialog.setTitle(R.string.set_minimum_duration);
		dialog.setContentView(R.layout.numberpicker_dialog);
		Button set_button = (Button) dialog.findViewById(R.id.set_button);
		Button cancel_button = (Button) dialog.findViewById(R.id.cancel_button);
		
		long time = this.query.get_start_date().getTime();
		time = (time / 1000) % SECS_IN_DAY;
		time /= 60;
		int time_remaining_in_day_mins = (int) time;
		
//		time_remaining_in_day_mins = SECS_IN_DAY / 60;	// disable if limiting searches to the current day
		
		final NumberPicker np = (NumberPicker) dialog.findViewById(R.id.number_picker);
		String[] nums = new String[(int) time_remaining_in_day_mins + 1];
		for (int i = 0; i < nums.length; i++) {
			nums[i] = Integer.toString(i + 1);
		}
		
		np.setMinValue(1);
		np.setMaxValue(time_remaining_in_day_mins);
		np.setWrapSelectorWheel(false);
		np.setDisplayedValues(nums);
		np.setValue(this.query.get_duration());
		np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//				Toast.makeText(FindRoomLaterActivity.this, "Old val: " + oldVal + "; new val: " + newVal, Toast.LENGTH_SHORT).show();
			}
		});
		
		set_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				Toast.makeText(FindRoomLaterActivity.this, "Duration selected: " + np.getValue(), Toast.LENGTH_SHORT).show();
				
				Integer val = Integer.valueOf(np.getValue());
				query.set_duration(val);
				set_min_duration_button_text(val);
				
				dialog.dismiss();
			}
		});
		
		cancel_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		dialog.show();
	}

	private String get_res_string(int res_id) {
		return (ActivityFindRoomLater.this.getResources().getString(res_id));
	}
	
	private void set_start_date_button_text(Date date) {
		if (date == null) {
			throw new IllegalArgumentException();
		}
		
		DateFormat format = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.ENGLISH);
		String date_str = format.format(date);
		
		Button start_date_button = (Button) findViewById(R.id.datepicker_button);
		start_date_button.setText(date_str);
	}
	
	private void set_start_time_button_text(Date date) {
		if (date == null) {
			throw new IllegalArgumentException();
		}
		
		Button start_time_button = (Button) findViewById(R.id.timepicker_button);
		start_time_button.setText(Utilities.get_time(date));
	}

	private void set_min_duration_button_text(Integer val) {
		if (val < 1) {
			throw new IllegalArgumentException();
		}
		
		Button min_duration_button = (Button) findViewById(R.id.min_duration_button);
		
		if (val == 1) {
			min_duration_button.setText(val + " " + get_res_string(R.string.minute));
		}
		else {
			min_duration_button.setText(val + " " + get_res_string(R.string.minutes));
		}
	}
	
	// ASSUMES ARGUMENT COMES ONLY FROM CAPACITY SELECTED IN CAPACITY PICKER
	private void set_min_capacity_button_text(Integer val) {
		if (val < 0 || val > Constants.MAX_CAPACITY) {
			throw new IllegalArgumentException();
		}
		
		Button min_capacity_button = (Button) findViewById(R.id.min_capacity_button);
		
		if (val == 0) {
			min_capacity_button.setText(get_res_string(R.string.zero_capacity));
		}
		else if (val == 1) {
			min_capacity_button.setText(val + " " + get_res_string(R.string.person));
		}
		else {
			min_capacity_button.setText(val + " " + get_res_string(R.string.people));
		}
	}

	
	
	
	
	
	
	
	

	private void getRoomRec() {
		Query.QueryResult query_result = this.query.search();
		
		Intent intent = new Intent(getApplicationContext(), ActivityRoomRec.class);
		intent.putExtra(Query.PARCELABLE_QUERY, this.query);
		intent.putExtra(Query.QueryResult.PARCELABLE_QUERY_RESULT, query_result);
		
//		Log.d(TAG, "Transmitting parcelable to RoomRecActivity: " + this.query.toString());
		
		startActivity(intent);
//		startActivityForResult(intent, 0);
//		startActivityForResult(new Intent(this, RoomRecActivity.class), 0);
		
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
		getMenuInflater().inflate(R.menu.find_room_later, menu);
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

/*
	// http://stackoverflow.com/questions/11800589/number-picker-dialog
	LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//	View np_view = inflater.inflate(R.layout.numberpicker_layout, null);
	AlertDialog.Builder builder = new AlertDialog.Builder(FindRoomLaterActivity.this);
	Dialog dialog = builder
		.setTitle("Set minimum capacity")
//		.setView(np_view)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				
			}
		})
		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				
			}
		})
		.create();
	
	dialog.show();
	
	findViewById(R.id.min_capacity).requestFocus();
	findViewById(R.id.min_capacity).setOnFocusChangeListener(new OnFocusChangeListener() {

		private EditText set_capacity = (EditText) findViewById(R.id.min_capacity);
		private InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus) {
				imm.showSoftInput(set_capacity, InputMethodManager.SHOW_IMPLICIT);
			}
			else {
				imm.hideSoftInputFromWindow(set_capacity.getWindowToken(), 0);
			}
		}
	});
	
	findViewById(R.id.min_capacity).setOnClickListener(new OnClickListener() {

//		private EditText set_capacity = (EditText) findViewById(R.id.min_capacity);
//		private InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		
		@Override
		public void onClick(View v) {
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
//			imm.showSoftInput(set_capacity, InputMethodManager.SHOW_IMPLICIT);
		}
	});
	
	private void update_this.query() {
		String hour_str = Integer.toString(selected_hour);
		hour_str = Utilities.pad_to_len_leading_zeroes(hour_str, 2);
		String minute_str = Integer.toString(selected_minute);
		minute_str = Utilities.pad_to_len_leading_zeroes(minute_str, 2);
		String temp = hour_str + minute_str;
		Date new_date = Utilities.get_date(selected_month, selected_day, selected_year, Integer.parseInt(temp));
		
		Log.d(TAG, "Selected time: " + selected_hour + ":" + selected_minute);
		Log.d(TAG, "Selected " + selected_month + "/" + selected_day + "/" + selected_year + " at " + Integer.parseInt(temp));
		
		this.query = new Query(new_date);
		
		this.query.set_duration(Integer.valueOf(selected_duration));
		this.query.set_option_capacity(Integer.valueOf(selected_capacity));
		if (selected_power) {
			this.query.set_option_power(Boolean.valueOf(true));
		}
		
		update_query_textview();
	}
  
#########################################################################################################

//	private int selected_year;
//	private int selected_month;
//	private int selected_day;
//	private int selected_hour;
//	private int selected_minute;
//	private int selected_duration;
//	private int selected_capacity;
//	private boolean selected_power;

//		Calendar curr_time = Calendar.getInstance();
//		selected_year = curr_time.get(Calendar.YEAR);
//		selected_month = curr_time.get(Calendar.MONTH) + 1;
//		selected_day = curr_time.get(Calendar.DAY_OF_MONTH);
//		selected_hour = curr_time.get(Calendar.HOUR_OF_DAY);
//		selected_minute = curr_time.get(Calendar.MINUTE);
//		selected_duration = 60;
//		selected_capacity = 0;
//		selected_power = false;	  
  
#########################################################################################################
	
	private Button getRoom;
	private int hour;
	private int minute;
	private TimePicker timePicker1;
//	TextView roomRecText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_room_later);

		getRoom = (Button)findViewById(R.id.get_Room);
		getRoom.setOnClickListener(this);
//		setTextViewInfo();
		setCurrentTimeOnView();
	}
  
	static final int TIME_DIALOG_ID = 999;

	private void setTextViewInfo() 
	{	// get the TextViews
		roomRecText = (TextView) findViewById(R.id.information);
		
	}
	  
	// display current time
	public void setCurrentTimeOnView() {
 
		timePicker1 = (TimePicker) findViewById(R.id.timePicker1);
 
		final Calendar c = Calendar.getInstance();
		hour = c.get(Calendar.HOUR_OF_DAY);
		minute = c.get(Calendar.MINUTE);
 
		// set current time into timepicker
		timePicker1.setCurrentHour(hour);
		timePicker1.setCurrentMinute(minute);
 
	}
 
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case TIME_DIALOG_ID:
			// set time picker as current time
			return new TimePickerDialog(this, 
                                        timePickerListener, hour, minute, false);
 
		}
		return null;
	}
 
	private TimePickerDialog.OnTimeSetListener timePickerListener = 
            new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int selectedHour,
				int selectedMinute) {
			hour = selectedHour;
			minute = selectedMinute;
 			timePicker1.setCurrentHour(hour);
			timePicker1.setCurrentMinute(minute);
		}
	};
	
	
	
	
 */
		
}		// end of file

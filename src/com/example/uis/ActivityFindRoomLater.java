package com.example.uis;

import java.util.Calendar;

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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

//public class FindRoomLaterActivity extends ActionBarActivity implements View.OnClickListener {
public class ActivityFindRoomLater extends FragmentActivity {	//  implements OnDateSetListener, TimePickerDialog.OnTimeSetListener

	private static final String TAG = "FindRoomLaterActivity";
	private static final int SECS_IN_DAY = 24 * 60 * 60;

	private Query this_query;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_room_later);

		this_query = new Query();
		update_query_textview();
		
		findViewById(R.id.dateButton).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Calendar calendar = Calendar.getInstance();
				
				calendar.setTime(this_query.get_start_date());
				
				DatePickerDialog datepicker_dialog = new DatePickerDialog(ActivityFindRoomLater.this, datepicker_dialog_listener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
//				DatePickerDialog datepicker_dialog = new DatePickerDialog(ActivityFindRoomLater.this, datepicker_dialog_listener, selected_year, selected_month - 1, selected_day);
				if (!Constants.DEBUG) {
					datepicker_dialog.getDatePicker().setMinDate(calendar.getTimeInMillis());
				}
				datepicker_dialog.show();
			}
		});
		
		findViewById(R.id.timeButton).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Calendar calendar = Calendar.getInstance();
				
				calendar.setTime(this_query.get_start_date());
				
				Dialog timepicker_dialog = new TimePickerDialog(ActivityFindRoomLater.this, timepicker_dialog_listener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
//				Dialog timepicker_dialog = new TimePickerDialog(ActivityFindRoomLater.this, timepicker_dialog_listener, selected_hour, selected_minute, true);
				timepicker_dialog.show();
			}
		});
		
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
		
		findViewById(R.id.min_duration).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				show_duration_picker();
			}
		});
		
		findViewById(R.id.min_capacity).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				show_capacity_picker();
			}
		});

		// http://stackoverflow.com/questions/8386832/android-checkbox-listener
		CheckBox has_power = (CheckBox) findViewById(R.id.has_power);
		has_power.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					this_query.set_option_power(Boolean.valueOf(true));
				}
				else {
					this_query.set_option_power(Boolean.valueOf(false));
				}
//				Toast.makeText(FindRoomLaterActivity.this, "Selected power option: " + selected_power, Toast.LENGTH_SHORT).show();
				update_query_textview();
			}
		});
		
		findViewById(R.id.get_Room).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getRoomRec();
			}
		});
		
	}		// end onCreate()
	
	private DatePickerDialog.OnDateSetListener datepicker_dialog_listener = new OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//			Toast.makeText(FindRoomLaterActivity.this, "Date selected: " + year + "/" + (monthOfYear + 1) + "/" + dayOfMonth, Toast.LENGTH_SHORT).show();
			
			this_query.set_start_date((monthOfYear + 1), dayOfMonth, year);
			update_query_textview();
			
//			long time = this_query.get_start_date().getTime();
//			time = (time / 1000) % SECS_IN_DAY;
//			time /= 60;
//			int time_remaining_in_day_mins = (int) time;
//			if (selected_duration > time_remaining_in_day_mins) {
//				selected_duration = time_remaining_in_day_mins;
//				update_this_query();
//			}
		}
	};
	
	private TimePickerDialog.OnTimeSetListener timepicker_dialog_listener = new OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//			Toast.makeText(FindRoomLaterActivity.this, "Time selected: " + hourOfDay + ":" + minute, Toast.LENGTH_SHORT).show();
			
			this_query.set_start_time(hourOfDay, minute);
			update_query_textview();
		}
	};
	
	// http://stackoverflow.com/questions/17805040/how-to-create-a-number-picker-dialog
	// http://stackoverflow.com/questions/16968111/android-numberpicker-doesn%C2%B4t-work
	private void show_capacity_picker() {
		final Dialog dialog = new Dialog(ActivityFindRoomLater.this);
		dialog.setTitle("Set minimum capacity (0 = no preference)");
		dialog.setContentView(R.layout.numberpicker_dialog);
		Button set_button = (Button) dialog.findViewById(R.id.set_button);
		Button cancel_button = (Button) dialog.findViewById(R.id.cancel_button);
		
		final NumberPicker np = (NumberPicker) dialog.findViewById(R.id.number_picker);
		String[] nums = new String[51];
		for (int i = 0; i < nums.length; i++) {
			nums[i] = Integer.toString(i);
		}
		
		np.setMinValue(0);
		np.setMaxValue(nums.length - 1);
		np.setWrapSelectorWheel(false);
		np.setDisplayedValues(nums);
		np.setValue(this_query.get_option_capacity());
		np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
			
			@Override
			public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//				Toast.makeText(FindRoomLaterActivity.this, "Old val: " + oldVal + "; new val: " + newVal, Toast.LENGTH_SHORT).show();
			}
		});
		
		set_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				Toast.makeText(FindRoomLaterActivity.this, "Capacity selected: " + np.getValue(), Toast.LENGTH_SHORT).show();
				
				this_query.set_option_capacity(np.getValue());
				update_query_textview();
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
		dialog.setTitle("Set minimum duration");
		dialog.setContentView(R.layout.numberpicker_dialog);
		Button set_button = (Button) dialog.findViewById(R.id.set_button);
		Button cancel_button = (Button) dialog.findViewById(R.id.cancel_button);
		
		long time = this_query.get_start_date().getTime();
		time = (time / 1000) % SECS_IN_DAY;
		time /= 60;
		int time_remaining_in_day_mins = (int) time;
		
		time_remaining_in_day_mins = SECS_IN_DAY / 60;	// disable if limiting searches to the current day
		
		final NumberPicker np = (NumberPicker) dialog.findViewById(R.id.number_picker);
		String[] nums = new String[(int) time_remaining_in_day_mins + 1];
		for (int i = 0; i < nums.length; i++) {
			nums[i] = Integer.toString(i + 1);
		}
		
		np.setMinValue(1);
		np.setMaxValue(time_remaining_in_day_mins);
		np.setWrapSelectorWheel(false);
		np.setDisplayedValues(nums);
		np.setValue(this_query.get_duration());
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
				
				this_query.set_duration(np.getValue());
				update_query_textview();
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

	private void update_query_textview() {
		TextView temp = (TextView) findViewById(R.id.this_query);
		temp.setText("Current query:\n" + this_query.toString());
	}

	public void getRoomRec() {
		Intent intent = new Intent(getApplicationContext(), ActivityRoomRec.class);
		intent.putExtra("this_query", this_query);
		
		Log.d(TAG, "Transmitting parcelable to RoomRecActivity: " + this_query.toString());
		
//		startActivity(intent);
		startActivityForResult(intent, 0);
//		startActivityForResult(new Intent(this, RoomRecActivity.class), 0);
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
	
	private void update_this_query() {
		String hour_str = Integer.toString(selected_hour);
		hour_str = Utilities.pad_to_len_leading_zeroes(hour_str, 2);
		String minute_str = Integer.toString(selected_minute);
		minute_str = Utilities.pad_to_len_leading_zeroes(minute_str, 2);
		String temp = hour_str + minute_str;
		Date new_date = Utilities.get_date(selected_month, selected_day, selected_year, Integer.parseInt(temp));
		
		Log.d(TAG, "Selected time: " + selected_hour + ":" + selected_minute);
		Log.d(TAG, "Selected " + selected_month + "/" + selected_day + "/" + selected_year + " at " + Integer.parseInt(temp));
		
		this_query = new Query(new_date);
		
		this_query.set_duration(Integer.valueOf(selected_duration));
		this_query.set_option_capacity(Integer.valueOf(selected_capacity));
		if (selected_power) {
			this_query.set_option_power(Boolean.valueOf(true));
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

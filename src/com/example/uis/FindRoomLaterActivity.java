package com.example.uis;

import java.util.Calendar;
import java.util.Date;

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
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

//public class FindRoomLaterActivity extends ActionBarActivity implements View.OnClickListener {
public class FindRoomLaterActivity extends FragmentActivity {	//  implements OnDateSetListener, TimePickerDialog.OnTimeSetListener

	private final String TAG = "FindRoomLaterActivity";
	
	Button getRoom;
	int hour;
	int minute;
	TimePicker timePicker1;
//	TextView roomRecText;
	
	private int selected_year;
	private int selected_month;
	private int selected_day;
	private int selected_hour;
	private int selected_minute;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_find_room_later);

		Calendar curr_time = Calendar.getInstance();
		selected_year = curr_time.get(Calendar.YEAR);
		selected_month = curr_time.get(Calendar.MONTH) + 1;
		selected_day = curr_time.get(Calendar.DAY_OF_MONTH);
		selected_hour = curr_time.get(Calendar.HOUR_OF_DAY);
		selected_minute = curr_time.get(Calendar.MINUTE);
		
		findViewById(R.id.dateButton).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Calendar calendar = Calendar.getInstance();
				Dialog datepicker_dialog = new DatePickerDialog(FindRoomLaterActivity.this, datepicker_dialog_listener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
				datepicker_dialog.show();
			}
		});
		
		findViewById(R.id.timeButton).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Calendar calendar = Calendar.getInstance();
				Dialog timepicker_dialog = new TimePickerDialog(FindRoomLaterActivity.this, timepicker_dialog_listener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
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
		
		findViewById(R.id.get_Room).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getRoomRec();
			}
		});
	}
	
	private DatePickerDialog.OnDateSetListener datepicker_dialog_listener = new OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			Toast.makeText(FindRoomLaterActivity.this, "Date selected: " + year + "/" + (monthOfYear + 1) + "/" + dayOfMonth, Toast.LENGTH_LONG).show();
			selected_year = year;
			selected_month = monthOfYear + 1;
			selected_day = dayOfMonth;
		}
	};
	
	private TimePickerDialog.OnTimeSetListener timepicker_dialog_listener = new OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			Toast.makeText(FindRoomLaterActivity.this, "Time selected: " + hourOfDay + ":" + minute, Toast.LENGTH_LONG).show();
			selected_hour = hourOfDay;
			selected_minute = minute;
		}
	};

//	@Override
//	protected void onCreate(Bundle savedInstanceState) 
//	{
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.activity_find_room_later);
//
//		getRoom = (Button)findViewById(R.id.get_Room);
//		getRoom.setOnClickListener(this);
////		setTextViewInfo();
//		setCurrentTimeOnView();
//	}
//
//	private void setTextViewInfo() 
//	{	// get the TextViews
//		roomRecText = (TextView) findViewById(R.id.information);
//		
//	}
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

  
	static final int TIME_DIALOG_ID = 999;
  
	// display current time
//	public void setCurrentTimeOnView() {
// 
//		timePicker1 = (TimePicker) findViewById(R.id.timePicker1);
// 
//		final Calendar c = Calendar.getInstance();
//		hour = c.get(Calendar.HOUR_OF_DAY);
//		minute = c.get(Calendar.MINUTE);
// 
//		// set current time into timepicker
//		timePicker1.setCurrentHour(hour);
//		timePicker1.setCurrentMinute(minute);
// 
//	}
 
	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case TIME_DIALOG_ID:
			// set time picker as current time
			return new TimePickerDialog(this, 
                                        timePickerListener, hour, minute,false);
 
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
	
	public void getRoomRec()
	{
		Intent intent = new Intent(getApplicationContext(), RoomRecActivity.class);
		String hour_str = Integer.toString(selected_hour);
		hour_str = Utilities.pad_to_len_leading_zeroes(hour_str, 2);
		String minute_str = Integer.toString(selected_minute);
		minute_str = Utilities.pad_to_len_leading_zeroes(minute_str, 2);
		String temp = hour_str + minute_str;
		Date new_date = Utilities.get_date(selected_month, selected_day, selected_year, Integer.parseInt(temp));
		
		Log.d(TAG, "Selected time: " + selected_hour + ":" + selected_minute);
		Log.d(TAG, "Selected " + selected_month + "/" + selected_day + "/" + selected_year + " at " + Integer.parseInt(temp));
		
		Query new_query = new Query(new_date);
		intent.putExtra("this_query", new_query);
		
		Log.d(TAG, "Transmitting parcelable to RoomRecActivity: " + new_query.toString());
		
//		startActivity(intent);
		
		startActivityForResult(intent, 0);
		
//		startActivityForResult(new Intent(this, RoomRecActivity.class), 0);
	}


}

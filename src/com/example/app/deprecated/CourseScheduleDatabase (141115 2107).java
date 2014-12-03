package com.example.app;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

public class CourseScheduleDatabase extends SQLiteAssetHelper {

	private static final String TAG				=	"CourseScheduleDatabase";
	private static final int DATABASE_VERSION	=	1;
	
	/* USE THESE FOR THE OLD 9-ARG ROW DB FORMAT */
//	private static final int COL_DEPT			=	0;		// String
//	private static final int COL_NUM			=	1;		// String
//	private static final int COL_NAME			=	2;		// String
//	private static final int COL_MEETING_DAYS	=	3;		// String
//	private static final int COL_START_TIME		=	4;		// Integer
//	private static final int COL_END_TIME		=	5;		// Integer
//	private static final int COL_BUILDING		=	6;		// String
//	private static final int COL_ROOM			=	7;		// String
//	private static final int COL_CAPACITY		=	8;		// Integer
//	private static final int NUM_COLS			=	9;
	
	/* USE THESE FOR THE OLD 6-ARG ROW DB FORMAT */
//	private static final int COL_NAME			=	0;		// String
//	private static final int COL_MEETING_DAYS	=	1;		// String
//	private static final int COL_START_TIME		=	2;		// Integer
//	private static final int COL_END_TIME		=	3;		// Integer
//	private static final int COL_LOCATION		=	4;		// String
//	private static final int COL_CAPACITY		=	5;		// Integer
	
	private static final int COL_NAME			=	0;		// String
	private static final int COL_MEETING_DAYS	=	1;		// String
	private static final int COL_START_TIME		=	2;		// Integer
	private static final int COL_END_TIME		=	3;		// Integer
	private static final int COL_BUILDING		=	6;		// String
	private static final int COL_ROOM			=	7;		// String
	private static final int COL_CAPACITY		=	5;		// Integer
	
	private static final int NUM_COLS			=	6;
	
	private String database_name;
	
	public CourseScheduleDatabase(Context context, String database_name) {
		super(context, database_name, null, DATABASE_VERSION);
		
		this.database_name = database_name;
	}
	
	protected RoomList get_roomlist() {
		RoomList out = new RoomList();
		
		String table_name, query;
		SQLiteDatabase db;
		Cursor cursor;
		
		table_name = this.database_name;
		int file_ext_dot_index = this.database_name.indexOf(".");
		if (file_ext_dot_index >= 0 && file_ext_dot_index < this.database_name.length()) {
			table_name = table_name.substring(0, file_ext_dot_index);
		}
		
		Log.d(TAG, "Selecting from table " + table_name + " in db file " + this.database_name);
		
		query = "SELECT * FROM " + table_name;
		
		db = this.getReadableDatabase();
		cursor = db.rawQuery(query, null);
		
		Event curr_event;
		String name;
		boolean[] meeting_days;
		Date start_time, end_time;
		Location location;
		String capacity;
		if (cursor.moveToFirst()) {
			do {
//				name = cursor.getString(COL_DEPT) + " " + cursor.getString(COL_NUM) + " - " + cursor.getString(COL_NAME);
//				meeting_days = set_meeting_days(cursor.getString(COL_MEETING_DAYS));
//				start_time = Utilities.get_date(cursor.getInt(COL_START_TIME));
//				end_time = Utilities.get_date(cursor.getInt(COL_END_TIME));
//				location = new Location(cursor.getString(COL_BUILDING) + " " + cursor.getString(COL_ROOM));
//				capacity = cursor.getString(COL_CAPACITY);
				
//				name = cursor.getString(COL_NAME);
//				meeting_days = set_meeting_days(cursor.getString(COL_MEETING_DAYS));
//				start_time = Utilities.get_date(cursor.getInt(COL_START_TIME));
//				end_time = Utilities.get_date(cursor.getInt(COL_END_TIME));
//				location = new Location(cursor.getString(COL_LOCATION));
//				capacity = cursor.getString(COL_CAPACITY);
				
				name = cursor.getString(COL_NAME);
				meeting_days = set_meeting_days(cursor.getString(COL_MEETING_DAYS));
				start_time = Utilities.get_date(cursor.getInt(COL_START_TIME));
				end_time = Utilities.get_date(cursor.getInt(COL_END_TIME));
				location = new Location(cursor.getString(COL_BUILDING) + " " + cursor.getString(COL_ROOM));
				capacity = cursor.getString(COL_CAPACITY);
				
				if (start_time != null && end_time != null) {
					curr_event = new Event(name, start_time, end_time, location);
					out.add_event(meeting_days, location, capacity, curr_event);
				}
			}
			while (cursor.moveToNext());
		}
		
		cursor.close();
		db.close();

		Log.d(TAG, "Number of courses: " + out.get_size());
		
		return out;
	}
	
	private boolean[] initialise_days_of_week_array() {
		boolean[] array = new boolean[Constants.NUM_DAYS_IN_WEEK];
		
		for (int i = 0; i < Constants.NUM_DAYS_IN_WEEK; i++) {
			array[i] = false;
		}
		
		return array;	
	}
	
	// IGNORES SATURDAY AND SUNDAY (not that they're likely to appear)
	private boolean[] set_meeting_days(String code) {
		if (code == null || code.length() < 0) {
			throw new IllegalArgumentException();
		}
		
		boolean[] days = initialise_days_of_week_array();
		
		int code_length = code.length();
		for (int i = 0; i < code_length; i++) {
			char curr_char = code.charAt(i);
			String char_to_str = Character.toString(curr_char);
			if (Utilities.containsIgnoreCase(char_to_str, "t")) {
				if (i < code_length - 1 && Utilities.containsIgnoreCase(Character.toString(code.charAt(i + 1)), "h")) {
					days[Constants.THURSDAY] = true;
					i++;
				}
				else {
					days[Constants.TUESDAY] = true;
				}
				continue;
			}
			
			if (Utilities.containsIgnoreCase(char_to_str, "m")) {
				days[Constants.MONDAY] = true;
				continue;
			}
			else if (Utilities.containsIgnoreCase(char_to_str, "w")) {
				days[Constants.WEDNESDAY] = true;
				continue;
			}
			else if (Utilities.containsIgnoreCase(char_to_str, "f")) {
				days[Constants.FRIDAY] = true;
				continue;
			}
		}
		
		return days;
	}
	
	protected List<ArrayList<String>> get_all_courses() {
		List<ArrayList<String>> out = new ArrayList<ArrayList<String>>(15000);
		
		String table_name, query;
		SQLiteDatabase db;
		Cursor cursor;
		ArrayList<String> curr_row;
		
		table_name = this.database_name;
		int file_ext_dot_index = this.database_name.indexOf(".");
		if (file_ext_dot_index >= 0 && file_ext_dot_index < this.database_name.length()) {
			table_name = table_name.substring(0, file_ext_dot_index);
		}
		
		Log.d(TAG, "Selecting from table " + table_name + " in db file " + this.database_name);
		
		query = "SELECT * FROM " + table_name;
		
		db = this.getReadableDatabase();
		cursor = db.rawQuery(query, null);
		
		if (cursor.moveToFirst()) {
			do {
				curr_row = new ArrayList<String>(NUM_COLS);
				
//				curr_row.add(cursor.getString(COL_DEPT));
//				curr_row.add(cursor.getString(COL_NUM));
//				curr_row.add(cursor.getString(COL_NAME));
//				curr_row.add(cursor.getString(COL_MEETING_DAYS));
//				curr_row.add(cursor.getString(COL_START_TIME));
//				curr_row.add(cursor.getString(COL_END_TIME));
//				curr_row.add(cursor.getString(COL_BUILDING));
//				curr_row.add(cursor.getString(COL_ROOM));
//				curr_row.add(cursor.getString(COL_CAPACITY));
				
//				curr_row.add(cursor.getString(COL_NAME));
//				curr_row.add(cursor.getString(COL_MEETING_DAYS));
//				curr_row.add(cursor.getString(COL_START_TIME));
//				curr_row.add(cursor.getString(COL_END_TIME));
//				curr_row.add(cursor.getString(COL_LOCATION));
//				curr_row.add(cursor.getString(COL_CAPACITY));
				
				curr_row.add(cursor.getString(COL_NAME));
				curr_row.add(cursor.getString(COL_MEETING_DAYS));
				curr_row.add(cursor.getString(COL_START_TIME));
				curr_row.add(cursor.getString(COL_END_TIME));
				curr_row.add(cursor.getString(COL_BUILDING));
				curr_row.add(cursor.getString(COL_ROOM));
				curr_row.add(cursor.getString(COL_CAPACITY));
				
				out.add(curr_row);
			}
			while (cursor.moveToNext());
		}
		
		cursor.close();
		db.close();
		
		Log.d(TAG, "Number of courses: " + out.size());
		
		return out;
	}
	
	protected String get_database_name() {
		return this.database_name;
	}
	
}

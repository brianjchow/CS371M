package com.example.app;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

// https://github.com/jgilfelt/android-sqlite-asset-helper
public class CourseScheduleDatabase extends SQLiteAssetHelper {

	private static final String TAG				=	"CourseScheduleDatabase";
	private static final int DATABASE_VERSION	=	1;

//	private static final String		BUILDING		=	"building";
	private static final String		ROOM			=	"room";
	private static final String		CAPACITY		=	"capacity";
	private static final String		NAME			=	"name";
	private static final String		MEETING_DAYS	=	"meeting_days";
	private static final String		START_TIME		=	"start_time";
	private static final String		END_TIME		=	"end_time";
	
	private String database_name;
	
	public CourseScheduleDatabase(Context context, String database_name) {
		super(context, database_name, null, DATABASE_VERSION);
		
		this.database_name = database_name;
	}
	
	protected Map<String, Room> get_courses(String building_name, String db_file_name) {
		if (building_name == null || building_name.length() != Constants.BUILDING_CODE_LENGTH || db_file_name == null || db_file_name.length() <= 0) {
			Log.d(TAG, "One or more arguments null, CourseScheduleDatabase.get_courses()");
			throw new IllegalArgumentException("One or more arguments null, CourseScheduleDatabase.get_courses()");
		}
		
		Map<String, Room> out = new HashMap<String, Room>(Utilities.get_hashmap_size(15));

		String table_name, query;
		SQLiteDatabase db;
		Cursor cursor;
		
		table_name = strip_file_name_formatting(this.database_name);
		
		Log.d(TAG, "Selecting from table " + table_name + " in db file " + this.database_name);
		
		query = "SELECT * FROM " + table_name + " WHERE building=\"" + building_name.toUpperCase(Constants.DEFAULT_LOCALE) + "\"";
		
		db = this.getReadableDatabase();
		cursor = db.rawQuery(query, null);

		if (!cursor.moveToFirst()) {
			return out;
		}
		
		// consider switching to using a ContentProvider later
		final int COL_ROOM = cursor.getColumnIndex(ROOM);
		final int COL_CAPACITY = cursor.getColumnIndex(CAPACITY);
		final int COL_NAME = cursor.getColumnIndex(NAME);
		final int COL_MEETING_DAYS = cursor.getColumnIndex(MEETING_DAYS);
		final int COL_START_TIME = cursor.getColumnIndex(START_TIME);
		final int COL_END_TIME = cursor.getColumnIndex(END_TIME);
		
		boolean building_is_gdc = building_name.equalsIgnoreCase(Constants.GDC);
		if (building_is_gdc) {
			out = initialise_gdc_room_properties();
		}
		
		String room_num, name;
		boolean[] meeting_days;
		Date start_date, end_date;
		Integer capacity;
		
		Room room;
		Location location;
		Event event;
		Integer start_time, end_time;
		
		do {
			room_num = cursor.getString(COL_ROOM);
			capacity = cursor.getInt(COL_CAPACITY);
			name = cursor.getString(COL_NAME);
			meeting_days = set_meeting_days(cursor.getString(COL_MEETING_DAYS));
			
			start_time = cursor.getInt(COL_START_TIME);
			end_time = cursor.getInt(COL_END_TIME);
			
			start_date = Utilities.get_date(start_time);
			end_date = Utilities.get_date(end_time);
			
			if (start_time != null && end_time != null) {
				if ((room = out.get(room_num)) == null) {
					if (building_is_gdc) {
						continue;
					}
				}
				
				location = new Location(building_name, room_num);
				event = new Event(name, start_date, end_date, location);
				
				if ((room = out.get(room_num)) == null) {
					
					if (capacity > 0) {
						room = new Room(location, Constants.DEFAULT_ROOM_TYPE, capacity, false);
					}
					else {
						room = new Room(location);
					}
				}
				
				for (int i = Constants.SUNDAY; i <= Constants.SATURDAY; i++) {
					if (meeting_days[i]) {
						room.add_event(i, event);
					}
				}
				
				out.put(room_num, room);
			}
		}
		while (cursor.moveToNext());
		
		cursor.close();
		db.close();

		Log.d(TAG, "Number of courses: " + out.size());
		
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

	private Map<String, Room> initialise_gdc_room_properties() {
		Map<String, Room> out = new HashMap<String, Room>(Utilities.get_hashmap_size(Constants.VALID_GDC_ROOMS.length));

		String[] room_types = Constants.VALID_GDC_ROOMS_TYPES;
		int[] room_capacities = Constants.VALID_GDC_ROOMS_CAPACITIES;
		boolean[] room_powers = Constants.VALID_GDC_ROOMS_POWERS;
		String gdc_str = Constants.GDC + " ";
		Room room;
		for (int i = 0; i < Constants.VALID_GDC_ROOMS.length; i++) {
			if ((!Constants.INCLUDE_GDC_CONFERENCE_ROOMS && room_types[i].equals(Constants.CONFERENCE)) ||
				(!Constants.INCLUDE_GDC_LOBBY_ROOMS && room_types[i].equals(Constants.LOBBY)) ||
				(!Constants.INCLUDE_GDC_LOUNGE_ROOMS && room_types[i].equals(Constants.LOUNGE))) {
				continue;
			}
			room = new Room(new Location(gdc_str + Constants.VALID_GDC_ROOMS[i]), room_types[i], room_capacities[i], room_powers[i]);
			out.put(Constants.VALID_GDC_ROOMS[i], room);
		}
		
		return out;
	}
	
	private String strip_file_name_formatting(String file_name) {
		String out = file_name;
		
		int subfolder_index = file_name.lastIndexOf("/");
		int file_ext_dot_index = file_name.lastIndexOf(".");
		if (file_ext_dot_index >= 0 && file_ext_dot_index < file_name.length()) {
			if (subfolder_index == -1) {
				out = file_name.substring(0, file_ext_dot_index);
			}
			else {
				out = file_name.substring(subfolder_index + 1, file_ext_dot_index);
			}
		}
		else {
			out = file_name;
		}
		
		return out;
	}
	
	protected String get_database_name() {
		return this.database_name;
	}
	
}

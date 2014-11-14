package com.example.app;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;

import com.example.uis.R;

/*
 * A class to chain multiple Room objects together.
 * 
 * RoomLists cannot be modified and will always be
 * identical, even if Constants.java is modified.
 */

final class RoomList {
	
	private static final String TAG = "RoomList";

	private static final boolean READ_ONLY_GDC = false;
	
	private static final int DELIMITER = (int) '\t';
	private static final int EOL = (int) '#';
	private static final int MAX_ROOM_LENGTH = 4;
	
	private static final int	STD_LINE_SIZE				=	75;
	
	private static final int	INDEX_CLASS_DEPT			=	4;
	private static final int	INDEX_CLASS_NUM				=	5;
	private static final int	INDEX_CLASS_NAME			=	7;
	
	private static final int	INDEX_CLASS_MEETING_DAYS	=	29;
	private static final int	INDEX_CLASS_START_DATE		=	30;
	private static final int 	INDEX_CLASS_END_DATE		=	31;
	private static final int 	INDEX_CLASS_BUILDING		=	32;
	private static final int 	INDEX_CLASS_ROOM			=	33;
	private static final int 	INDEX_CLASS_ROOM_CAPACITY	=	34;
	
	private static final int 	INDEX_LAB_MEETING_DAYS		=	37;
	private static final int 	INDEX_LAB_START_TIME		=	38;
	private static final int 	INDEX_LAB_END_TIME			=	39;
	private static final int 	INDEX_LAB_BUILDING			=	40;
	private static final int 	INDEX_LAB_ROOM				=	41;
	private static final int 	INDEX_LAB_ROOM_CAPACITY		=	42;
	
	private static int line_counter = 0;
	
	private Map<Location, Room> list;

	private String db_name = null;
	private Connection connection = null;
	private Statement statement = null;
	
	/**
	 * Default constructor.
	 */
	protected RoomList() {
		this.list = new HashMap<Location, Room>(Constants.VALID_GDC_ROOMS.length * 2);
		initialise();
	}
	
	protected RoomList(Context context, String schedule_file) {
		if (context == null || schedule_file == null || schedule_file.length() <= 0) {
			throw new IllegalArgumentException("Context arg cannot be null.");
		}
		
		this.list = new HashMap<Location, Room>(25000);		// F14: 16710 buckets; S15: 15001 buckets
		this.db_name = schedule_file;
		initialise();
		
		int res_id = getResId(schedule_file, R.raw.class);
		if (res_id == -1) {
			throw new IllegalStateException("Database file missing!");
		}
		
		try {			
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + db_name + ".db");
			statement = connection.createStatement();
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start();
		
		Log.d(TAG, "Now reading course schedule from res ID " + res_id);
		
//		this.read_course_schedules(context, res_id);	// FULL_COURSE_SCHEDULE_F14, FULL_COURSE_SCHEDULE_S15
		read_course_schedule_from_db();
		
		stopwatch.stop();
		if (Constants.DEBUG) {
			System.out.printf("Took %f seconds to read %d lines from schedule \"%s\"\n\n", stopwatch.time(), line_counter, db_name);
		}
		
		try {
			if (connection != null) {
				connection.close();
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		
		Log.d(TAG, "Finished reading course schedule from res ID " + res_id);
		Log.d(TAG, "Took " + stopwatch.time() + "s to read " + line_counter + " lines from schedule");
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

	/**
	 * Initialise this RoomList's backing Map. Populate it with
	 * the rooms available in GDC.
	 */
	private void initialise() {
		String[] room_types = Constants.VALID_GDC_ROOMS_TYPES;
		int[] room_capacities = Constants.VALID_GDC_ROOMS_CAPACITIES;
		boolean[] room_powers = Constants.VALID_GDC_ROOMS_POWERS;
		String gdc_str = Constants.GDC + " ";
		Room room;
		for (int i = 0; i < Constants.VALID_GDC_ROOMS.length; i++) {
			room = new Room(new Location(gdc_str + Constants.VALID_GDC_ROOMS[i]), room_types[i], room_capacities[i], room_powers[i]);
			this.add(room);
		}
		
		line_counter = 0;
	}
	
	private boolean add_event(boolean[] meeting_days, Location location, String capacity, Event event) {
		if (meeting_days == null || location == null || capacity == null || event == null) {
			throw new IllegalArgumentException("ERROR: one or more args null, RoomList.add_class_event()");
		}
		
		Room curr_room;
		if ((curr_room = this.list.get(location)) != null) {
			for (int i = Constants.SUNDAY; i < meeting_days.length; i++) {
				if (meeting_days[i]) {
					curr_room.add_event(i, event);
				}
			}
			
			this.list.put(location, curr_room);
			return true;
		}

		if (!READ_ONLY_GDC) {
			/* TODO: ignore if not in GDC? */
			
			int room_capacity = 0;

			capacity = Utilities.regex_replace(capacity, "[^\\d]", "");
			room_capacity = Integer.parseInt(capacity);
			
			Room to_add;
			
			if (room_capacity > 0) {
				to_add = new Room(location, Constants.DEFAULT_ROOM_TYPE, room_capacity, Constants.DEFAULT_ROOM_HAS_POWER);
			}
			else {
				to_add = new Room(location);
			}
			
			for (int i = 0; i < meeting_days.length; i++) {
				if (meeting_days[i]) {
					to_add.add_event(i, event);
				}
			}

			this.list.put(location, to_add);
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * @return The size of this RoomList's backing Map.
	 */
	protected int get_size() {
		return this.list.size();
	}
	
	/**
	 * @param room
	 * @return True if room was successfully added to this
	 * 		   RoomList's backing Map, false otherwise.
	 */
	private boolean add(Room room) {
		if (room == null) {
			return false;
		}
		
		Location this_location = room.get_location();
		
		// disallow multiples in backing Map
		if (this.list.get(this_location) != null) {
			return false;
		}
		
		this.list.put(this_location, room);		
		return true;
	}
	
	protected final RoomList get_gdc_rooms() {
		if (this.list == null) {
			throw new IllegalStateException("This RoomList's backing Map cannot be null.");
		}
		
		RoomList out = new RoomList();
		
		Room temp;
		for (Location location : this.list.keySet()) {
			temp = this.list.get(location);
			if (temp != null) {
				out.add(temp);
			}
		}
		
		return out;
	}

	/**
	 * @return An iterator over this RoomList's backing Map.
	 */
	protected Iterator<Map.Entry<Location, Room>> get_iterator() {
		return (this.list.entrySet().iterator());
	}
	
	protected final Set<Location> get_keyset() {
		if (this.list == null) {
			throw new IllegalStateException("This RoomList's backing Map cannot be null.");
		}
		
		return (this.list.keySet());
	}
	
	protected Iterator<Map.Entry<Location, Room>> get_sorted_map_iterator() {
		Map<Location, Room> temp = this.get_sorted_map();
		return (temp.entrySet().iterator());
	}
	
	/* USE THIS METHOD FOR TESTING ONLY */
	protected int get_num_events_all_rooms() {
		if (this.list == null) {
			throw new IllegalStateException("RoomList's backing Map is null, RoomList.get_num_events_all_rooms()");
		}
		
		int total = 0;
		
		Room curr_room;
//		Map<Integer, Set<Event>> curr_room_events;
		SparseArray<Set<Event>> curr_room_events;
		for (Map.Entry<Location, Room> entry : this.list.entrySet()) {
			curr_room = entry.getValue();
			curr_room_events = curr_room.get_events();
			
			for (int i = Constants.SUNDAY; i <= Constants.SATURDAY; i++) {
				total += curr_room_events.get(i).size();
			}
		}
		
		return total;
	}
	
	/**
	 * @param location
	 * @return Null if location is null or could not be found
	 * 		   in this RoomList's backing Map; the Map's value
	 * 		   of location otherwise.
	 */
	protected Room get_room(Location location) {
		if (location == null) {
			return null;
		}
		return (this.list.get(location));
	}
	
	/**
	 * @return This RoomList's backing Map stuffed into
	 * 		   a TreeMap (which is sorted).
	 */
	private Map<Location, Room> get_sorted_map() {
		if (this.list == null) {
			throw new IllegalArgumentException();
		}
		Map<Location, Room> out = new TreeMap<Location, Room>(this.list);
		return out;
	}
	
	protected final Set<Room> get_valueset() {
		if (this.list == null) {
			throw new IllegalStateException("This RoomList's backing Map cannot be null.");
		}
		
		return (new TreeSet<Room>(this.list.values()));
	}
	
	private boolean[] initialise_days_of_week_array() {
		boolean[] array = new boolean[Constants.NUM_DAYS_IN_WEEK];
		
		for (int i = 0; i < Constants.NUM_DAYS_IN_WEEK; i++) {
			array[i] = false;
		}
		
		return array;	
	}
	
	private String pad_trailing_zeroes(String str, int final_len) {
		if (str == null) {
			throw new IllegalArgumentException();
		}
		if (final_len <= str.length()) {
			return str;
		}
		
		StringBuilder out = new StringBuilder(str.length() * 3);
		out.append(str);
		for (int i = str.length(); i <= final_len; i++) {
			out.append("0");
		}
		
		return (out.toString());
	}

	private void parse_line(List<String> tokens) {
		if (tokens == null || tokens.size() != STD_LINE_SIZE) {
			return;
		}
		
		if (tokens.get(INDEX_CLASS_MEETING_DAYS).length() <= 0 || tokens.get(INDEX_CLASS_START_DATE).length() <= 0 || tokens.get(INDEX_CLASS_END_DATE).length() <= 0 || tokens.get(INDEX_CLASS_BUILDING).length() <= 0 || tokens.get(INDEX_CLASS_ROOM).length() <= 0) {
			return;
		}

		Event class_event = null;
		String class_name = tokens.get(INDEX_CLASS_DEPT) + tokens.get(INDEX_CLASS_NUM) + " - " + tokens.get(INDEX_CLASS_NAME);
		boolean[] class_meeting_days = set_meeting_days(tokens.get(INDEX_CLASS_MEETING_DAYS));
		Date class_start_time = Utilities.get_date(Integer.parseInt(Utilities.time_to_24h(tokens.get(INDEX_CLASS_START_DATE))));
		Date class_end_time = Utilities.get_date(Integer.parseInt(Utilities.time_to_24h(tokens.get(INDEX_CLASS_END_DATE))));
		
		String class_room = tokens.get(INDEX_CLASS_ROOM);
		if (class_room.length() < MAX_ROOM_LENGTH && Utilities.containsIgnoreCase(class_room, "\\.")) {
			class_room = pad_trailing_zeroes(class_room, MAX_ROOM_LENGTH);
		}
		
		Location class_location = new Location(tokens.get(INDEX_CLASS_BUILDING) + " " + class_room);

		Event lab_event = null;
		String lab_name;
		boolean[] lab_meeting_days;
		Date lab_start_time, lab_end_time;
		Location lab_location;
		
		if (tokens.get(INDEX_LAB_MEETING_DAYS).length() > 0 && tokens.get(INDEX_LAB_START_TIME).length() > 0 && tokens.get(INDEX_LAB_END_TIME).length() > 0 && tokens.get(INDEX_LAB_BUILDING).length() > 0 && tokens.get(INDEX_LAB_ROOM).length() > 0) {
			lab_name = class_name + " (Lab)";
			lab_meeting_days = set_meeting_days(tokens.get(INDEX_LAB_MEETING_DAYS));
			lab_start_time = Utilities.get_date(Integer.parseInt(Utilities.time_to_24h(tokens.get(INDEX_LAB_START_TIME))));
			lab_end_time = Utilities.get_date(Integer.parseInt(Utilities.time_to_24h(tokens.get(INDEX_LAB_END_TIME))));
			
			String lab_room = tokens.get(INDEX_LAB_ROOM);
			if (lab_room.length() < MAX_ROOM_LENGTH && Utilities.containsIgnoreCase(lab_room, ".")) {
				lab_room = pad_trailing_zeroes(lab_room, MAX_ROOM_LENGTH);
			}
			
			lab_location = new Location(tokens.get(INDEX_LAB_BUILDING) + " " + lab_room);
			
			if (lab_start_time != null && lab_end_time != null) {
				lab_event = new Event(lab_name, lab_start_time, lab_end_time, lab_location);
				this.add_event(lab_meeting_days, lab_location, tokens.get(INDEX_LAB_ROOM_CAPACITY), lab_event);
			}
		}
		
		if (class_start_time != null && class_end_time != null) {
			class_event = new Event(class_name, class_start_time, class_end_time, class_location);
			this.add_event(class_meeting_days, class_location, tokens.get(INDEX_CLASS_ROOM_CAPACITY), class_event);
		}
	}
	
	private void read_course_schedules(Context context, int res_id) {
		if (this.list == null) {
			this.list = new RoomList().list;
		}
		
//		final String WHICH_SCHEDULE = FULL_COURSE_SCHEDULE_S15;	// CS_COURSE_SCHEDULE_S15, FULL_COURSE_SCHEDULE_S15
				
		InputReader input = new InputReader(context, res_id);
		
		StringBuilder curr_line = new StringBuilder(200);
		List<String> curr_line_tokens = new ArrayList<String>();
		int curr_char;
		while ((curr_char = input.read()) != -1) {
			
			if (curr_char == EOL) {
//				System.out.println(curr_line_tokens.size() + " " + curr_line_tokens.toString());
//				for (int i = 0; i < curr_line_tokens.size(); i++) {
//					System.out.printf("(%d) %s, ", i, curr_line_tokens.get(i));
//				}
				
				parse_line(curr_line_tokens);
				
				curr_line.setLength(0);
				curr_line_tokens = new ArrayList<String>();
				
				line_counter++;
			}
			else if (curr_char == DELIMITER) {
//				if (curr_line.length() > 0) {
					String temp = Utilities.regex_replace(curr_line.toString(), "\"", "");
					curr_line_tokens.add(temp);
					curr_line.setLength(0);
//				}
			}
			else {
				curr_line.append((char) curr_char);
			}			
		}
	}
	
	private void read_course_schedule_from_db() {
		if (this.list == null) {
//			this.list = new RoomList().list;
			throw new IllegalStateException("This RoomList's backing Map cannot be null.");
		}
		
		if (db_name == null || connection == null || statement == null) {
			return;
		}
		
		try {
			ResultSet rs = statement.executeQuery("select * from " + db_name);
			
			Event event = null;
			String name;
			boolean[] meeting_days;
			Date start_time, end_time;
			Location location;
			int capacity;
			
			while (rs.next()) {
				name = rs.getString("dept") + " " + rs.getString("num") + " - " + rs.getString("name");
				meeting_days = set_meeting_days(rs.getString("meeting_days"));
				start_time = Utilities.get_date(rs.getInt("start_time"));
				end_time = Utilities.get_date(rs.getInt("end_time"));
				location = new Location(rs.getString("building") + " " + rs.getString("room"));
				capacity = rs.getInt("capacity");
				
				if (start_time != null && end_time != null) {
					event = new Event(name, start_time, end_time, location);
					this.add_event(meeting_days, location, Integer.toString(capacity), event);		// INEFFICIENT! (for Integer.toString())
				}
				
				line_counter++;
			}
		}
		catch (SQLException e) {
			throw new RuntimeException();
		}
		
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
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder out = new StringBuilder();
		
		Map<Location, Room> temp = this.get_sorted_map();
		
		for (Map.Entry<Location, Room> entry : temp.entrySet()) {
			out.append(entry.getValue().toString() + "\n");
		}
		
		return (out.toString());
	}

}

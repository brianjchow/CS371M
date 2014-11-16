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

/*
 * A class to chain multiple Room objects together.
 * 
 * RoomLists cannot be modified and will always be
 * identical, even if Constants.java is modified.
 */

// @SuppressWarnings("unused")
final class RoomList {

//	private static final String CS_COURSE_SCHEDULE_S15 = "course_schedules/cs_course_schedule_s15.csv";
//	private static final String FULL_COURSE_SCHEDULE_S15 = "course_schedules/master_course_schedule_s15.csv";
//	private static final String FULL_COURSE_SCHEDULE_F14 = "course_schedules/master_course_schedule_f14.csv";
	
	private static final boolean WRITE_TO_DB = false;
	
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
	
	private static int line_counter;
	
	private Map<Location, Room> list;
	
	private String db_name = null;
	private Connection connection = null;
	private Statement statement = null;
	
	/**
	 * Default constructor.
	 */
//	protected RoomList() {
//		this.list = new HashMap<Location, Room>(Constants.VALID_GDC_ROOMS.length * 2);
//		initialise(Constants.COURSE_SCHEDULE_THIS_SEMESTER);
//	}
	
	protected RoomList() {
		this.list = new HashMap<Location, Room>(Constants.VALID_GDC_ROOMS.length * 2);
//		initialise_gdc_rooms();
	}
	
	protected RoomList(String course_schedule_file_name) {
		if (course_schedule_file_name == null || course_schedule_file_name.length() <= 0) {
			throw new IllegalArgumentException("Invalid String argument, RoomList constructor.");
		}
		
		this.list = new HashMap<Location, Room>(25000);		// F14: 16710 buckets; S15: 15001 buckets		
		initialise_gdc_rooms();
		
		// https://bitbucket.org/xerial/sqlite-jdbc
		try {
			db_name = course_schedule_file_name.substring(17, 43);
			
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + db_name + ".db");
			statement = connection.createStatement();
			
			if (WRITE_TO_DB) {
				statement.executeUpdate("drop table if exists " + db_name);
				statement.executeUpdate("create table " + db_name + " ("
						+ "name string, meeting_days string, start_time INTEGER, end_time INTEGER, building string, room string, capacity INTEGER)");
				
//				statement.executeUpdate("create table " + db_name + " ("
//						+ "name string, meeting_days string, start_time INTEGER, end_time INTEGER, location string, capacity INTEGER)");
				
//				statement.executeUpdate("create table " + db_name + " ("
//						+ "dept string, num string, name string, meeting_days string, start_time INTEGER, end_time INTEGER, building string, room string, capacity INTEGER)");
			}
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
		
		if (WRITE_TO_DB) {
			this.read_course_schedules(course_schedule_file_name);	// FULL_COURSE_SCHEDULE_F14, FULL_COURSE_SCHEDULE_S15
		}
		else {
			read_course_schedule_from_db();
		}
		
		stopwatch.stop();
		if (Constants.DEBUG) {
			System.out.printf("Took %f seconds to read %d lines from schedule \"%s\"\n\n", stopwatch.time(), line_counter, course_schedule_file_name);
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
	}
	
	/**
	 * Initialise this RoomList's backing Map. Populate it with
	 * the rooms available in GDC.
	 */
	protected void initialise_gdc_rooms() {
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
		
		Location curr_loc;
		Room curr_room;
//		for (Location location : this.list.keySet()) {
//			curr_room = this.list.get(location);
//			if (curr_room != null) {
//				out.add(curr_room);
//			}
//		}

		String gdc_str = "GDC ";
		for (int i = 0; i < Constants.VALID_GDC_ROOMS.length; i++) {
			curr_loc = new Location(gdc_str + Constants.VALID_GDC_ROOMS[i]);
			curr_room = this.list.get(curr_loc);
			if (curr_room != null) {
//				System.out.println(curr_room.toString() + "\n");
				out.list.put(curr_loc, curr_room.clone());
			}
		}
		
		return out;
	}
	
	protected final Set<Location> get_keyset() {
		if (this.list == null) {
			throw new IllegalStateException("This RoomList's backing Map cannot be null.");
		}
		
		return (this.list.keySet());
	}
	
	protected final Set<Room> get_valueset() {
		if (this.list == null) {
			throw new IllegalStateException("This RoomList's backing Map cannot be null.");
		}
		
		return (new TreeSet<Room>(this.list.values()));
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
	 * @return An iterator over this RoomList's backing Map.
	 */
	protected final Iterator<Map.Entry<Location, Room>> get_iterator() {
		return (this.list.entrySet().iterator());
	}
	
	protected final Iterator<Map.Entry<Location, Room>> get_sorted_map_iterator() {
		Map<Location, Room> temp = this.get_sorted_map();
		return (temp.entrySet().iterator());
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
				/* 9-arg rows */
//				name = rs.getString("dept") + " " + rs.getString("num") + " - " + rs.getString("name");
//				meeting_days = set_meeting_days(rs.getString("meeting_days"));
//				start_time = Utilities.get_date(rs.getInt("start_time"));
//				end_time = Utilities.get_date(rs.getInt("end_time"));
//				location = new Location(rs.getString("building") + " " + rs.getString("room"));
//				capacity = rs.getInt("capacity");
				
				/* 6-arg rows */
//				name = rs.getString("name");
//				meeting_days = set_meeting_days(rs.getString("meeting_days"));
//				start_time = Utilities.get_date(rs.getInt("start_time"));
//				end_time = Utilities.get_date(rs.getInt("end_time"));
//				location = new Location(rs.getString("location"));
//				capacity = rs.getInt("capacity");

				location = new Location(rs.getString("building") + " " + rs.getString("room"));
				capacity = rs.getInt("capacity");
				name = rs.getString("name");
				meeting_days = set_meeting_days(rs.getString("meeting_days"));
				start_time = Utilities.get_date(rs.getInt("start_time"));
				end_time = Utilities.get_date(rs.getInt("end_time"));
				
				if (start_time != null && end_time != null) {
					event = new Event(name, start_time, end_time, location);
					this.add_event(meeting_days, location, Integer.toString(capacity), event);		// INEFFICIENT! (for Integer.toString())
				}
				
				line_counter++;
			}
		}
		catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	private void read_course_schedules(String schedule_file) {
		if (schedule_file == null || schedule_file.length() <= 0) {
			throw new IllegalArgumentException();
		}
		if (this.list == null) {
//			this.list = new RoomList().list;
			throw new IllegalStateException("This RoomList's backing Map cannot be null.");
		}
		
//		final String WHICH_SCHEDULE = FULL_COURSE_SCHEDULE_S15;	// CS_COURSE_SCHEDULE_S15, FULL_COURSE_SCHEDULE_S15
				
		InputReader input = new InputReader(schedule_file);
		
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
	
	/*
	 * - event name: 3 + 4 + " - " + 6
	 * - meeting days: 28
	 * - start time: 29
	 * - end time: 30
	 * - building code: 31
	 * - room: 32
	 * - room capacity: 33 (is not always listed accurately)
	 * 
	 * if 36, 37, 38, 39, 40 length > 0
	 * - lab meeting days: 36
	 * - lab start time: 37
	 * - lab end time: 38
	 * - lab building code: 39
	 * - lab room: 40
	 * - lab room capacity: 41 (is not always listed accurately)
	 */
	private void parse_line(List<String> tokens) {
		if (tokens == null || tokens.size() != STD_LINE_SIZE) {
//			System.out.println("hi diddly hoe" + " " + tokens.size());
			return;
		}
		
		if (tokens.get(INDEX_CLASS_MEETING_DAYS).length() <= 0 || tokens.get(INDEX_CLASS_START_DATE).length() <= 0 || tokens.get(INDEX_CLASS_END_DATE).length() <= 0 || tokens.get(INDEX_CLASS_BUILDING).length() <= 0 || tokens.get(INDEX_CLASS_ROOM).length() <= 0) {
			return;
		}

		String dept = tokens.get(INDEX_CLASS_DEPT);
		String num = Utilities.regex_replace(tokens.get(INDEX_CLASS_NUM), " ", "");
		String name = Utilities.regex_replace(tokens.get(INDEX_CLASS_NAME), "[,']", "");
		String meeting_days = tokens.get(INDEX_CLASS_MEETING_DAYS);
		int start_time = Integer.parseInt(Utilities.time_to_24h(tokens.get(INDEX_CLASS_START_DATE)));
		int end_time = Integer.parseInt(Utilities.time_to_24h(tokens.get(INDEX_CLASS_END_DATE)));
		String building = tokens.get(INDEX_CLASS_BUILDING);
		String room = tokens.get(INDEX_CLASS_ROOM);
		int capacity = Integer.parseInt(Utilities.regex_replace(tokens.get(INDEX_CLASS_ROOM_CAPACITY), "[^\\d]", ""));
		
		Event class_event = null;
		String class_name = dept + num + " - " + name;
		boolean[] class_meeting_days = set_meeting_days(meeting_days);
		Date class_start_time = Utilities.get_date(start_time);
		Date class_end_time = Utilities.get_date(end_time);
		
		String class_room = tokens.get(INDEX_CLASS_ROOM);
		if (class_room.length() < MAX_ROOM_LENGTH && Utilities.containsIgnoreCase(class_room, "\\.")) {
			class_room = pad_trailing_zeroes(class_room, MAX_ROOM_LENGTH);
		}
		
		Location class_location = new Location(building + " " + class_room);
		
		if (class_start_time != null && class_end_time != null) {
			class_event = new Event(class_name, class_start_time, class_end_time, class_location);
			this.add_event(class_meeting_days, class_location, tokens.get(INDEX_CLASS_ROOM_CAPACITY), class_event);
			
			if (WRITE_TO_DB) {
				insert_into_db(dept, num, name, meeting_days, start_time, end_time, building, room, capacity);
			}
			
//			System.out.println(class_event.toString() + "\n");
		}
//System.out.println(class_name + "\n" + Arrays.toString(class_meeting_days) + "\n" + Utilities.time_to_24h(tokens.get(29)) + "\n" + Utilities.time_to_24h(tokens.get(30)));
		
		Event lab_event = null;
		String lab_name;
		boolean[] lab_meeting_days;
		Date lab_start_time, lab_end_time;
		Location lab_location;
		
		/* TODO: is the precondition check sufficient to determine whether or not the class has a lab? */
		if (tokens.get(INDEX_LAB_MEETING_DAYS).length() > 0 && tokens.get(INDEX_LAB_START_TIME).length() > 0 && tokens.get(INDEX_LAB_END_TIME).length() > 0 && tokens.get(INDEX_LAB_BUILDING).length() > 0 && tokens.get(INDEX_LAB_ROOM).length() > 0) {
			name = name + " (Lab)";
			meeting_days = tokens.get(INDEX_LAB_MEETING_DAYS);
			start_time = Integer.parseInt(Utilities.time_to_24h(tokens.get(INDEX_LAB_START_TIME)));
			end_time = Integer.parseInt(Utilities.time_to_24h(tokens.get(INDEX_LAB_END_TIME)));
			building = tokens.get(INDEX_LAB_BUILDING);
			room = tokens.get(INDEX_LAB_ROOM);
			capacity = Integer.parseInt(Utilities.regex_replace(tokens.get(INDEX_LAB_ROOM_CAPACITY), "[^\\d]", ""));
			
			lab_name = class_name + " (Lab)";
			lab_meeting_days = set_meeting_days(meeting_days);
			lab_start_time = Utilities.get_date(start_time);
			lab_end_time = Utilities.get_date(end_time);
			
			String lab_room = tokens.get(INDEX_LAB_ROOM);
			if (lab_room.length() < MAX_ROOM_LENGTH && Utilities.containsIgnoreCase(lab_room, ".")) {
				lab_room = pad_trailing_zeroes(lab_room, MAX_ROOM_LENGTH);
			}
			
			lab_location = new Location(building + " " + lab_room);
			
//System.out.println(lab_name + "\n" + Arrays.toString(lab_meeting_days) + "\n" + Utilities.time_to_24h(tokens.get(37)) + "\n" + Utilities.time_to_24h(tokens.get(38)));
			
			if (lab_start_time != null && lab_end_time != null) {
				lab_event = new Event(lab_name, lab_start_time, lab_end_time, lab_location);
				this.add_event(lab_meeting_days, lab_location, tokens.get(INDEX_LAB_ROOM_CAPACITY), lab_event);
				
				if (WRITE_TO_DB) {
					insert_into_db(dept, num, name, meeting_days, start_time, end_time, building, room, capacity);
				}
				
//				System.out.println(lab_event.toString() + "\n");
			}
		}

	}
	
	/* name string, meeting_days string, start_time INTEGER, end_time INTEGER, location string, capacity INTEGER */
	private void insert_into_db(String dept, String num, String name, String meeting_days, int start_time, int end_time, String building, String room, int capacity) {
		if (db_name == null || connection == null || statement == null) {
			return;
		}
		
		try {
			String to_insert;
			
			/* 9-arg rows */
//			to_insert = String.format("INSERT INTO %s VALUES(%s, %s, %s, %s, %s, %d, %d, %s, %s, %d)", db_name, dept, num, name, meeting_days, start_time, end_time, building, room, capacity);
//			to_insert = "'" + dept + "', '" + num + "', '" + name + "', '" + meeting_days + "', " + start_time + ", " + end_time + ", '" + building + "', '" + room + "', " + capacity;
//			System.out.println(to_insert);
			
			/* 6-arg rows */
//			to_insert = "'" + dept + " " + num + " - " + name + "', '" + meeting_days + "', " + start_time + ", " + end_time + ", '" + building + " " + room + "', " + capacity;
			
			to_insert = "'" + dept + " " + num + " - " + name + "', '" + meeting_days + "', " + start_time + ", " + end_time + ", '" + building + "', '" + room + "', " + capacity;
			
			statement.executeUpdate("INSERT INTO " + db_name + " VALUES(" + to_insert + ")");
		}
		catch (SQLException e) {
			System.out.println("*** ERROR: " + e.getMessage());
			e.printStackTrace();
			throw new RuntimeException(e);
		}
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
			
			//System.out.println(event.toString() + "\n");
			
			return true;
		}

		if (!READ_ONLY_GDC) {
			/* TODO: ignore if not in GDC? */
			
			// make new Room() using capacity in tokens[]
			// add the event to it
			// add it to this.list

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
		
//		System.out.println(event.toString() + "\n");
		
		return false;
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
	
	/* USE THIS METHOD FOR TESTING ONLY */
	protected int get_num_events_all_rooms() {
		if (this.list == null) {
			throw new IllegalStateException("RoomList's backing Map is null, RoomList.get_num_events_all_rooms()");
		}
		
		int total = 0;
		
		Room curr_room;
		Map<Integer, Set<Event>> curr_room_events;
		for (Map.Entry<Location, Room> entry : this.list.entrySet()) {
			curr_room = entry.getValue();
			curr_room_events = curr_room.get_events();
			
			for (int i = Constants.SUNDAY; i <= Constants.SATURDAY; i++) {
				total += curr_room_events.get(i).size();
			}
		}
		
		return total;
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		else if (!(other instanceof RoomList)) {
			return false;
		}
		
		RoomList other_list = (RoomList) other;
		if (this.get_size() != other_list.get_size()) {
			return false;
		}
		
		Location curr_loc;
		Room curr_room, other_list_room;
		for (Map.Entry<Location, Room> entry : this.list.entrySet()) {
			curr_loc = entry.getKey();
			curr_room = entry.getValue();
			
			if ((other_list_room = other_list.list.get(curr_loc)) != null) {
				if (!curr_room.equals(other_list_room)) {
					return false;
				}
			}
			else {
				return false;
			}
		}
		
		return true;
	}
	
	/*
	 * TODO
	 * 
	 * OVERRIDE HASHCODE HERE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	 */
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder out = new StringBuilder();
		
		Map<Location, Room> temp = this.get_sorted_map();
		
		for (Map.Entry<Location, Room> entry : temp.entrySet()) {
			out.append(entry.getValue().toString() + "\n");
//			out.append("# events: " + entry.getValue().get_num_events() + "\n");
//			out.append("\n");
		}
		
		return (out.toString());
	}

}

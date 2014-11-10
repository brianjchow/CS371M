import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/*
 * A class to chain multiple Room objects together.
 * 
 * RoomLists cannot be modified and will always be
 * identical, even if Constants.java is modified.
 */

@SuppressWarnings("unused")
final class RoomList {

//	private static final String CS_COURSE_SCHEDULE_S15 = "course_schedules/cs_course_schedule_s15.csv";
	private static final String FULL_COURSE_SCHEDULE_S15 = "course_schedules/master_course_schedule_s15.csv";
	private static final String FULL_COURSE_SCHEDULE_F14 = "course_schedules/master_course_schedule_f14.csv";
	
	private static final boolean READ_ONLY_GDC = false;
	
	private static final int DELIMITER = (int) '\t';
	private static final int EOL = (int) '#';
	private static final int MAX_ROOM_LENGTH = 4;
	
	/* If reading from master list, change OFFSET to 1; otherwise, keep at 0 (not sure why this happens) */
	private static final int OFFSET = 1;
	private static final int STD_LINE_SIZE = 74 + OFFSET;
	
	private static final int INDEX_CLASS_MEETING_DAYS = 28 + OFFSET;
	private static final int INDEX_CLASS_START_DATE = 29 + OFFSET;
	private static final int INDEX_CLASS_END_DATE = 30 + OFFSET;
	private static final int INDEX_CLASS_BUILDING = 31 + OFFSET;
	private static final int INDEX_CLASS_ROOM = 32 + OFFSET;
	private static final int INDEX_CLASS_ROOM_CAPACITY = 33 + OFFSET;
	
	private static final int INDEX_LAB_MEETING_DAYS = 36 + OFFSET;
	private static final int INDEX_LAB_START_TIME = 37 + OFFSET;
	private static final int INDEX_LAB_END_TIME = 38 + OFFSET;
	private static final int INDEX_LAB_BUILDING = 39 + OFFSET;
	private static final int INDEX_LAB_ROOM = 40 + OFFSET;
	private static final int INDEX_LAB_ROOM_CAPACITY = 41 + OFFSET;
	
	private static int line_counter = 0;
	
	private Map<Location, Room> list;
	
	/**
	 * Default constructor.
	 */
	protected RoomList() {
		this.list = new HashMap<Location, Room>(Constants.VALID_GDC_ROOMS.length * 2);
		initialise();
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
		
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start();
		
		this.read_course_schedules(FULL_COURSE_SCHEDULE_F14);
		
		stopwatch.stop();
		if (Constants.DEBUG) {
			System.out.printf("\nTook %f seconds to read %d lines from schedule\n", stopwatch.time(), line_counter);
		}
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
	protected Iterator<Map.Entry<Location, Room>> get_iterator() {
		return (this.list.entrySet().iterator());
	}
	
	protected Iterator<Map.Entry<Location, Room>> get_sorted_map_iterator() {
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
	
	private void read_course_schedules(String schedule_file) {
		if (schedule_file == null || schedule_file.length() <= 0) {
			throw new IllegalArgumentException();
		}
		if (this.list == null) {
			this.list = new RoomList().list;
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

		Event class_event = null;
		String class_name = tokens.get(3 + OFFSET) + tokens.get(4 + OFFSET) + " - " + tokens.get(6 + OFFSET);
		boolean[] class_meeting_days = set_meeting_days(tokens.get(INDEX_CLASS_MEETING_DAYS));
		Date class_start_time = Utilities.get_date(Integer.parseInt(Utilities.time_to_24h(tokens.get(INDEX_CLASS_START_DATE))));
		Date class_end_time = Utilities.get_date(Integer.parseInt(Utilities.time_to_24h(tokens.get(INDEX_CLASS_END_DATE))));
		
		String class_room = tokens.get(INDEX_CLASS_ROOM);
		if (class_room.length() < MAX_ROOM_LENGTH && Utilities.containsIgnoreCase(class_room, "\\.")) {
			class_room = pad_trailing_zeroes(class_room, MAX_ROOM_LENGTH);
		}
		
		Location class_location = new Location(tokens.get(INDEX_CLASS_BUILDING) + " " + class_room);

//System.out.println(class_name + "\n" + Arrays.toString(class_meeting_days) + "\n" + Utilities.time_to_24h(tokens.get(29)) + "\n" + Utilities.time_to_24h(tokens.get(30)));
		
		Event lab_event = null;
		String lab_name;
		boolean[] lab_meeting_days;
		Date lab_start_time, lab_end_time;
		Location lab_location;
		
		/* TODO: is the precondition check sufficient to determine whether or not the class has a lab? */
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
			
//System.out.println(lab_name + "\n" + Arrays.toString(lab_meeting_days) + "\n" + Utilities.time_to_24h(tokens.get(37)) + "\n" + Utilities.time_to_24h(tokens.get(38)));
			
			if (lab_start_time != null && lab_end_time != null) {
				lab_event = new Event(lab_name, lab_start_time, lab_end_time, lab_location);
				this.add_event(lab_meeting_days, lab_location, tokens.get(41), lab_event);
				
//				System.out.println(lab_event.toString() + "\n");
			}
		}
		
		if (class_start_time != null && class_end_time != null) {
			class_event = new Event(class_name, class_start_time, class_end_time, class_location);
			this.add_event(class_meeting_days, class_location, "0", class_event);
			
//			System.out.println(class_event.toString() + "\n");
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
			curr_room_events = curr_room.get_room_events();
			
			for (int i = Constants.SUNDAY; i <= Constants.SATURDAY; i++) {
				total += curr_room_events.get(i).size();
			}
		}
		
		return total;
	}
			
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

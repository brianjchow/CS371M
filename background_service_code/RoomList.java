import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/*
 * A class to chain multiple Room objects together.
 * 
 * RoomLists cannot be modified and will always be
 * identical, even if Constants.java is modified.
 */

final class RoomList {

	private static final String FILE_NAME = "course_schedules/cs_course_schedule_s15.csv";
	private static final int DELIMITER = (int) '\t';
	private static final int EOL = (int) '#';
	private static final int STD_LINE_SIZE = 74;
	
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
		
		this.read_course_schedules();
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
	
	private void read_course_schedules() {
		if (this.list == null) {
			this.list = new RoomList().list;
		}
		
		InputReader input = new InputReader(FILE_NAME);
		
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
			return;
		}
		
		if (tokens.get(28).length() <= 0 || tokens.get(29).length() <= 0 || tokens.get(30).length() <= 0 || tokens.get(31).length() <= 0 || tokens.get(32).length() <= 0) {
			return;
		}

		Event class_event = null;
		String class_name = tokens.get(3) + tokens.get(4) + " - " + tokens.get(6);
		boolean[] class_meeting_days = set_meeting_days(tokens.get(28));
		Date class_start_time = Utilities.get_date(Integer.parseInt(Utilities.time_to_24h(tokens.get(29))));
		Date class_end_time = Utilities.get_date(Integer.parseInt(Utilities.time_to_24h(tokens.get(30))));
		
		String class_room = tokens.get(32);
		if (class_room.length() < 4) {
			class_room = pad_trailing_zeroes(class_room, 4);
		}
		
		Location class_location = new Location(tokens.get(31) + " " + class_room);

//System.out.println(class_name + "\n" + Arrays.toString(class_meeting_days) + "\n" + Utilities.time_to_24h(tokens.get(29)) + "\n" + Utilities.time_to_24h(tokens.get(30)));
		
		Event lab_event = null;
		String lab_name;
		boolean[] lab_meeting_days;
		Date lab_start_time, lab_end_time;
		Location lab_location;
		
		/* TODO: is the precondition check sufficient to determine whether or not the class has a lab? */
		if (tokens.get(36).length() > 0 && tokens.get(37).length() > 0 && tokens.get(38).length() > 0 && tokens.get(39).length() > 0 && tokens.get(40).length() > 0) {
			lab_name = class_name + " (Lab)";
			lab_meeting_days = set_meeting_days(tokens.get(36));
			lab_start_time = Utilities.get_date(Integer.parseInt(Utilities.time_to_24h(tokens.get(37))));
			lab_end_time = Utilities.get_date(Integer.parseInt(Utilities.time_to_24h(tokens.get(38))));
			
			String lab_room = tokens.get(40);
			if (lab_room.length() < 4) {
				lab_room = pad_trailing_zeroes(lab_room, 4);
			}
			
			lab_location = new Location(tokens.get(39) + " " + lab_room);
			
//System.out.println(lab_name + "\n" + Arrays.toString(lab_meeting_days) + "\n" + Utilities.time_to_24h(tokens.get(37)) + "\n" + Utilities.time_to_24h(tokens.get(38)));
			
			if (lab_start_time != null && lab_end_time != null) {
				lab_event = new Event(lab_name, lab_start_time, lab_end_time, lab_location);
				this.add_event(lab_meeting_days, lab_location, lab_event);
				
//				System.out.println(lab_event.toString() + "\n");
			}
		}
		
		if (class_start_time != null && class_end_time != null) {
			class_event = new Event(class_name, class_start_time, class_end_time, class_location);
			this.add_event(class_meeting_days, class_location, class_event);
			
//			System.out.println(class_event.toString() + "\n");
		}
	}
	
	private void add_event(boolean[] meeting_days, Location location, Event event) {
		if (meeting_days == null || location == null || event == null) {
			throw new IllegalArgumentException("ERROR: one or more args null, RoomList.add_class_event()");
		}
		
		Room curr_room;
		if ((curr_room = this.list.get(location)) != null) {
			for (int i = 0; i < meeting_days.length; i++) {
				if (meeting_days[i]) {
					curr_room.add_event(i, event);
				}
			}
			
			this.list.put(location, curr_room);
			
//System.out.println(event.toString() + "\n");
		}
		else {
			// ignore for now if not in GDC
			
			// make new Room() using capacity in tokens[]
			// add the event to it
			// add it to this.list
		}
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
			
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder out = new StringBuilder();
		
		Map<Location, Room> temp = this.get_sorted_map();
		
		for (Map.Entry<Location, Room> entry : temp.entrySet()) {
			out.append(entry.getValue().toString() + "\n");
			out.append("# events: " + entry.getValue().get_num_events());
			out.append("\n\n");
		}
		
		return (out.toString());
	}

}

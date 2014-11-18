import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

/**
 * @author Fatass
 *
 */
// @SuppressWarnings("unused")
final class CSVReader {
	
	private static final String ALL_EVENTS_SCHEDULE		= "https://www.cs.utexas.edu/calendar/touch/feed";			// events
	private static final String ALL_ROOMS_SCHEDULE		= "https://www.cs.utexas.edu/calendar/touch/all/feed";		// rooms
	private static final String ALL_TODAYS_EVENTS		= "https://www.cs.utexas.edu/calendar/touch/today/feed";	// today's events
	
	private static final char DELIMITER = '\"';
	
	/**
	 * Default constructor. Empty.
	 */
	private CSVReader() {	}
	
	/**
	 * @param src
	 * @param what
	 * @return True if src contains what (ignore case), false otherwise.
	 */
	private boolean containsIgnoreCase(String src, String what) {
		return (Utilities.containsIgnoreCase(src, what));
	}

	/**
	 * @param str
	 * @return True if str represents a valid 3-letter building code, false otherwise.
	 */
	private boolean is_campus_building_str(String str) {
		if (str == null || str.length() <= 2) {
			return false;
		}
		
		String compare = str.substring(0, 3);
		
		for (int i = 0; i < Constants.CAMPUS_BUILDINGS.length; i++) {
			if (compare.equals(Constants.CAMPUS_BUILDINGS[i]) &&
				!is_ignored_room(str)) {
				
				return true;
			}
		}
		return false;
	}

	/**
	 * @param str
	 * @return True if str contains a day of the week, false otherwise.
	 */
	private boolean is_date_string(String str) {
		if (str == null) {
			return false;
		}
		for (int i = Constants.SUNDAY; i <= Constants.SATURDAY; i++) {
			if (containsIgnoreCase(str, Constants.DAYS_OF_WEEK_SHORT[i]) ||
				containsIgnoreCase(str, Constants.DAYS_OF_WEEK_LONG[i])) {
				
				return true;
			}
		}
		return false;
	}
		
	/**
	 * @param str
	 * @return True if str matches a list of ignored "rooms", false otherwise.
	 */
	private boolean is_ignored_room(String str) {
		if (str == null || str.length() <= 0) {
			return false;
		}
				
		for (int i = 0; i < Constants.IGNORE_ROOMS.length; i++) {
			if (containsIgnoreCase(str, Constants.IGNORE_ROOMS[i])) {
				return true;
			}
		}
		return false;
	}
		
	/**
	 * @return An EventList of events parsed from a CSV feed(s).
	 */
	protected static EventList read_csv() {
		if (Constants.get_has_feed_been_read()) {
			return Constants.CSV_FEEDS_MASTER.clone();
		}
		Constants.set_has_feed_been_read();
		
		CSVReader reader = new CSVReader();
		List<HashMap<String, String>> event_strings;
		EventList events = new EventList();
		
//		Stopwatch stopwatch = new Stopwatch();
//		stopwatch.start();

		if (!Constants.DEBUG) {
			event_strings = reader.read_csv_from_url(ALL_EVENTS_SCHEDULE);
			events.add(event_strings);
			
			event_strings = reader.read_csv_from_url(ALL_ROOMS_SCHEDULE);
			events.add(event_strings);
			
			event_strings = reader.read_csv_from_url(ALL_TODAYS_EVENTS);
			events.add(event_strings);
		}
		else {
			event_strings = reader.read_csv_from_file("feeds/calendar_events_feed_1411.csv");
			events.add(event_strings);
			
			event_strings = reader.read_csv_from_file("feeds/calendar_events_today_feed_1411.csv");
			events.add(event_strings);
			
			event_strings = reader.read_csv_from_file("feeds/calendar_rooms_feed_1411.csv");
			events.add(event_strings);	
		}
		
//		stopwatch.stop();
		
//		if (Constants.DEBUG) {
//			System.out.println("Took " + stopwatch.time() + " seconds to read\n");
//		}

		return events;		
	}
		
	/**
	 * @param filename
	 * @return a List of Maps of Strings; each Map contains Strings for one
	 * 		   event parsed from the CSV feed given by filename
	 */
	private List<HashMap<String, String>> read_csv_from_file(String filename) {
		if (filename == null || filename.length() <= 0) {
			throw new IllegalArgumentException("Error: cannot read from null or empty file name, read_csv_from_file()");
		}

		List<HashMap<String, String>> schedules = new ArrayList<HashMap<String, String>>(100);
		InputReader input = new InputReader(filename);
		
		int temp = 0;

		StringBuilder curr_line = new StringBuilder();
		HashMap<String, String> result;

		while ((temp = input.read()) != -1) {
			char curr_byte = (char) temp;
			if (curr_byte != '\n') {
				curr_line.append(curr_byte);
			}
			
			// end of line reached in file; parse this event
			else {
// System.out.println(curr_line.toString());
				result = split_line(curr_line);
				if (result != null) {
// System.out.println(result.toString());
					schedules.add(result);
				}
				curr_line.setLength(0);
			}
		}
		
		input.close();
		return schedules;
	}
	
	/**
	 * @param site
	 * @return a List of Maps of Strings; each Map contains Strings for one
	 * 		   event parsed from the CSV feed given by site (a URL)
	 */
	private List<HashMap<String, String>> read_csv_from_url(String site) {
		if (site == null || site.length() <= 0) {
			throw new IllegalArgumentException("Error: cannot read from null or empty site name, read_csv_from_url()");
		}

		List<HashMap<String, String>> schedules = new ArrayList<HashMap<String, String>>(100);
		URLReader reader = new URLReader(site);

		int temp = 0;
		
		HashMap<String, String> result;
		StringBuilder curr_line = new StringBuilder();
		while ((temp = reader.read()) != -1) {
			char curr_byte = (char) temp;
			if (curr_byte != '\n') {
				curr_line.append(curr_byte);
			}
			
			// end of line reached in this CSV feed; parse this event
			else {
				result = split_line(curr_line);
				if (result != null) {
					schedules.add(result);
				}
				curr_line.setLength(0);
			}
		}
		
		reader.close();
		return schedules;
	}

	/**
	 * @param str
	 * @param regex
	 * @param replace_with
	 * @return str, after the regex has been applied
	 */
	private String regex_replace(String str, String regex, String replace_with) {
		return (Utilities.regex_replace(str, regex, replace_with));
	}
	
	/**
	 * @param str
	 * @return a HashMap of Strings that represent one event parsed from
	 * 		   a CSV feed
	 */
	private HashMap<String, String> split_line(StringBuilder str) {
		if (str == null || str.length() < 0) {
			throw new IllegalArgumentException("Error: cannot parse null or empty String, split_line()");
		}
		else if (str.length() == 0) {
			return (null);
		}
		
		HashMap<String, String> tuple = new HashMap<String, String>(10);
		boolean delim_in_stack = false;
		str.reverse();
		
		Stack<Character> stack = new Stack<Character>();
		
		int length = str.length();
		char curr_char;
		String temp_to_str = null;
		for (int i = 0; i < length; i++) {
			curr_char = str.charAt(i);
			
			// enough info collected from one segment of current event String; pop
			// everything off the Stack and process the resultant String
			// according to what info it contains
			if (curr_char == DELIMITER && delim_in_stack) {
				StringBuilder temp = new StringBuilder();
				char curr_stack_char;
				while (!stack.empty() && (curr_stack_char = stack.pop()) != DELIMITER) {
					temp.append(curr_stack_char);
				}
				delim_in_stack = false;
				
				// strip formatting
				temp_to_str = temp.toString();
				temp_to_str = regex_replace(temp_to_str, "(  )+", " ");
				temp_to_str = regex_replace(temp_to_str, "(CST|CDT|registrar?( - )*|room?(: )*)?[():,]*", "");

				if (temp_to_str.length() > 0) {
					
//					System.out.println(temp_to_str);
					
					// location encountered
					if (containsIgnoreCase(temp_to_str, Constants.GDC) ||
						is_campus_building_str(temp_to_str)) {
//System.out.println("location " + temp_to_str);
						tuple.put(Constants.LOCATION, temp_to_str);
					}
					
					// event time and date encountered
					else if (is_date_string(temp_to_str)) {
//System.out.println("date " + temp_to_str);
						if (containsIgnoreCase(temp_to_str, Constants.ALL_DAY)) {
							temp_to_str = regex_replace(temp_to_str, "(" + Constants.ALL_DAY + ")", "0001");
							String copy = temp_to_str;
							copy = regex_replace(copy, "0001", "2359");
							tuple.put(Constants.END_DATE, copy);
						}
						tuple.put(Constants.START_DATE, temp_to_str);
					}
					
					// event name encountered
					else {
//System.out.println("event " + temp_to_str);
						String curr_event_name;
						if ((curr_event_name = tuple.get(Constants.EVENT_NAME)) != null) {
							temp_to_str = new StringBuilder().append(temp_to_str).append(curr_event_name).toString();
						}
						tuple.put(Constants.EVENT_NAME, temp_to_str);
					}
				}
			}
			
			// begin processing next segment in current event String
			else if (curr_char == DELIMITER && !delim_in_stack) {
				stack.push(curr_char);
				delim_in_stack = true;
			}
			
			else {
				stack.push(curr_char);
			}
		}
		
		// skip malformed lines by counting the number of remaining
		// delimiters in the Stack
		if (!stack.empty()) {
			int num_delims = 0;
			while (!stack.empty()) {
				if ((curr_char = stack.pop()) == DELIMITER) {
					num_delims++;
				}
			}
			if (num_delims % 2 > 0) {
				return null;
			}
		}
		
		// set default location of this event if none specified
		if (tuple.get(Constants.LOCATION) == null) {
			tuple.put(Constants.LOCATION, Constants.GDC + " " + Constants.DEFAULT_GDC_LOCATION);
		}

		return tuple;
	}

	/* TODO
	 * - add "limit searches to GDC only" button
	 * - holidays? would be handled in Query.determine_which_schedule_to_use()
	 * - change DatePicker in Android to allow this and next semester only
	 * - change TimePicker in Android to only allow searches up to a day in length
	 * - keep separate databases for each semester; change database according to query
	 * - remove the boolean option for gdc_only in Query
	 * - fix Query.search_is_at_night() (before fixing bug in next step)
	 * - fix Utilities.time_schedules_overlap() to match Query.search_is_at_night()
	 * - fix Query.set_option_search_for_building() to reject invalid building codes
	 * - add set_standard_option() method to Query
	 * - change RoomList's backing Map to a Map<String, Room> (Room objects already have a Location field)
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) {
		Constants.init();
		
//		CourseScheduleDatabase.generate("course_schedules/master_course_schedule_f14_raw.csv");
//		CourseScheduleDatabase.generate("course_schedules/master_course_schedule_s15_raw.csv");
//		CourseScheduleDatabase.read_course_schedule_from_db("master_course_schedule_f14.db");
//		CourseScheduleDatabase.read_course_schedule_from_db("master_course_schedule_s15.db");
//		
//		Stopwatch stopwatch = new Stopwatch();
//		stopwatch.start();
//		for (int i = 0; i < Constants.CAMPUS_BUILDINGS.length; i++) {
//			System.out.println(Building.get_instance(Constants.CAMPUS_BUILDINGS[i], "master_course_schedule_f14.db").toString());
//		}
//		stopwatch.stop();
//		System.out.println("Size of BUILDING_CACHELIST_THIS_SEMESTER: " + Constants.BUILDING_CACHELIST_THIS_SEMESTER.get_size());
//		System.out.println("Took " + stopwatch.time() + " seconds to read from db (average " + (stopwatch.time() / Constants.BUILDING_CACHELIST_THIS_SEMESTER.get_size()) + " seconds ea)");
//		
//		Building gdc = Building.get_instance(Constants.GDC, "master_course_schedule_f14.db");
//		System.out.println(gdc.toString());
//		
//		Building cal = Building.get_instance("CAL", "master_course_schedule_f14.db");
//		System.out.println(cal.toString());
		
		int current_month = 11;
		int current_day = 14;
		
		String db_file = "master_course_schedule_f14";

		test(Utilities.get_date(current_month, current_day, 2014, 400), 61);
		test(Utilities.get_date(current_month, current_day, 2014, 800), 61);
		test(Utilities.get_date(current_month, current_day, 2014, 1030), 61);
		test(Utilities.get_date(current_month, current_day, 2014, 1300), 61);
		test(Utilities.get_date(current_month, current_day, 2014, 1300), 1);
		test(Utilities.get_date(current_month, current_day, 2014, 1630), 61);
		test(Utilities.get_date(current_month, current_day, 2014, 1656), 60);
		test(Utilities.get_date(current_month, current_day, 2014, 2159), 1);
		test(Utilities.get_date(current_month, current_day, 2014, 2300), 61);
		test(Utilities.get_date(current_month, current_day, 2014, 759), 1);
		test(Utilities.get_date(current_month, current_day, 2014, 759), 2);
		test(Utilities.get_date(current_month, current_day, 2014, 0000), Constants.MINUTES_IN_DAY);
		test(Utilities.get_date(current_month, current_day, 2014, 2317), 60);
		
		System.out.println(Constants.BUILDING_CACHELIST_THIS_SEMESTER.toString());
		
//		test_all_buildings(Utilities.get_date(current_month, current_day, 2014, 1300), 1);
		
//		System.out.println(Constants.USED_ROOMS_THIS_SEMESTER.toString());
		
//		RoomList gdc_rooms = Constants.USED_ROOMS_THIS_SEMESTER.get_gdc_rooms();
//		System.out.println(gdc_rooms.toString());
		
//		System.out.println("Size of RoomList (# unique rooms in schedule): " + Constants.USED_ROOMS_THIS_SEMESTER.get_size());
//		System.out.println("Total number events in RoomList: " + Constants.USED_ROOMS_THIS_SEMESTER.get_num_events_all_rooms() + "\n");
		
//		for (int i = 0; i < Constants.CAMPUS_BUILDINGS.length; i++) {
////			System.out.printf("<string name = \"%s\">%s</string>\n", Constants.CAMPUS_BUILDINGS[i], Constants.CAMPUS_BUILDINGS[i]);
////			System.out.printf("<item>@string/%s</item>\n", Constants.CAMPUS_BUILDINGS[i]);
//			
////			if (Constants.CAMPUS_BUILDINGS[i].equalsIgnoreCase(Constants.GDC)) {
////				System.out.println(i);
////			}
//		}
		
//		Connection connection = null;
//		try {
//			connection = DriverManager.getConnection("jdbc:sqlite:" + db_file + ".db");
//			Statement statement = connection.createStatement();
//			
//			ResultSet rs = statement.executeQuery("select * from " + db_file);
//			
//			while (rs.next()) {
//				System.out.printf("%s, %s, %s, %s, %d, %d, %s, %s, %d\n", rs.getString("dept"), rs.getString("num"), rs.getString("name"),
//						rs.getString("meeting_days"), rs.getInt("start_time"), rs.getInt("end_time"), rs.getString("building"), rs.getString("room"), rs.getInt("capacity"));
//			}
//		}
//		catch (SQLException e) {
//			e.printStackTrace();
//		}
//		finally {
//			try {
//				if (connection != null) {
//					connection.close();
//				}
//			}
//			catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
		
	}
	
	public static void test(Date date, int duration) {
		if (date == null) {
			Calendar calendar = Calendar.getInstance();
			date = calendar.getTime();
		}
		Stopwatch stopwatch = new Stopwatch();
		double time_to_exec;
		int num_events_in_raw_feeds;	//, num_events_available;
		
		EventList events = new EventList();

//		events = read_csv();
		events = Constants.CSV_FEEDS_MASTER;
		num_events_in_raw_feeds = events.get_size();

		events.sort_by_event_name(true);
		events.sort_by_start_date(true);
		
//System.out.println(events.toString());
		
		stopwatch.start();
		Query query = new Query(date);
		
		query.set_duration(duration);
		query.set_option_power(false);
		query.set_option_capacity(0);
//		query.set_option_capacity(new Integer(0));
		
		query.set_option_search_building("geb");

		String random_room = query.search();

		stopwatch.stop();
		time_to_exec = stopwatch.time();
		
		System.out.println("Random room chosen:\t" + random_room + "\n");
		System.out.println("Query:\n" + query.toString() + "\n");
		
		System.out.println("Done.\n");
		System.out.println("Num events, raw feeds:\t" + num_events_in_raw_feeds);
		System.out.println("Num events, cleaned:\t" + Constants.CSV_FEEDS_CLEANED.get_size());
//		System.out.println("Num events avail at\n    " + query + ":\t" + num_events_available);
		System.out.println("Time to search:\t\t" + time_to_exec + " seconds\n");
//		System.out.println("Total time elapsed:\t" + time_to_exec + " seconds\n");
		System.out.println("##########################################################################\n");
	}
	
	public static void test_all_buildings(Date date, int duration) {
		if (date == null) {
			Calendar calendar = Calendar.getInstance();
			date = calendar.getTime();
		}
		
		int total_buildings = 0;
		
		Stopwatch stopwatch = new Stopwatch();
		double time_to_exec = 0;
		double total_time_all_searches = 0;
		
		Query query = new Query(date);
		
		query.set_duration(duration);
		query.set_option_power(false);
		query.set_option_capacity(0);
		
		String random_room;
		
		for (String building : Constants.CAMPUS_BUILDINGS) {
			query.set_option_search_building(building);
			
			stopwatch.start();
			random_room = query.search();
			stopwatch.stop();
			total_time_all_searches += stopwatch.time();
			
			time_to_exec = stopwatch.time();
			
			System.out.println("Random room chosen:\t" + random_room + "\n");
			System.out.println("Query:\n" + query.toString() + "\n");
			
			System.out.println("Done.\n");
//			System.out.println("Num events avail at\n    " + query + ":\t" + num_events_available);
			System.out.println("Time to search:\t\t" + time_to_exec + " seconds\n");
//			System.out.println("Total time elapsed:\t" + time_to_exec + " seconds\n");
			System.out.println("##########################################################################\n");
			
			total_buildings++;
		}
		
		System.out.printf("Searched %d buildings in %f seconds (avg %f seconds).\n\n", total_buildings, total_time_all_searches, (total_time_all_searches / total_buildings));
		
	}
	
//	public static void print_rooms_read_from_schedule() {
//		int total_num_courses = 0;
//		List<String> all_rooms = new ArrayList<String>(2000);
//		Iterator<Map.Entry<Location, Room>> itr = Constants.USED_ROOMS_THIS_SEMESTER.get_iterator();
//		Map.Entry<Location, Room> curr_entry;
//		Room curr_room;
//		while (itr.hasNext()) {
//			curr_entry = itr.next();
//			curr_room = curr_entry.getValue();
//			Map<Integer, Set<Event>> curr_room_events = curr_room.get_events();
//			
//			all_rooms.add(curr_room.get_location().toString());
//			
//			System.out.print(curr_room.get_location().toString() + "\n   ");
//			for (int i = Constants.MONDAY; i <= Constants.FRIDAY; i++) {
//				System.out.print(Constants.DAYS_OF_WEEK_SHORT[i] + ": ");
//				
//				for (Event event : curr_room_events.get(i)) {
//					System.out.printf("%s (%s), ", event.get_event_name(), Utilities.get_time(event.get_start_date()));
//				}
//				
//				System.out.print("\n   ");
//			}
//			
//			System.out.println("\n");
//			
//			total_num_courses += curr_room.get_num_events();
//		}
//		Collections.sort(all_rooms);
//		System.out.println(all_rooms.toString() + "\n");
//		System.out.println("Total events in RoomList: " + total_num_courses);
//		System.out.println("Size of RoomList (# unique rooms in schedule): " + Constants.USED_ROOMS_THIS_SEMESTER.get_size());
//	}
	
	
	
}		// end of file















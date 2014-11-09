//import org.apache.commons.csv;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

final class CSVReader {
	
	private static final String ALL_EVENTS_SCHEDULE = "https://www.cs.utexas.edu/calendar/touch/feed";		// events
	private static final String ALL_ROOMS_SCHEDULE = "https://www.cs.utexas.edu/calendar/touch/all/feed";	// rooms
	private static final String ALL_TODAYS_EVENTS = "https://www.cs.utexas.edu/calendar/touch/today/feed";	// today's events
	
	private static final char DELIMITER = '\"';
	
	private CSVReader() {	}

	// http://crunchify.com/java-how-to-convert-byte-array-to-string/
	private List<HashMap<String, String>> read_csv_from_url(String site) {
		if (site == null || site.length() <= 0) {
			throw new IllegalArgumentException();
		}

		List<HashMap<String, String>> schedules = new ArrayList<HashMap<String, String>>(100);
		
		try {
			site = new URI(site).toString();	// check url now?
		}
		catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		URL url = null;
		try {
			url = new URL(site);
		}
		catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		if (url == null) {
			return schedules;
		}
		
		BufferedInputStream input = null;		
		try {
			input = new BufferedInputStream(url.openStream());
			int temp = 0;
			
			HashMap<String, String> result;
			StringBuilder curr_line = new StringBuilder();
			while ((temp = input.read()) != -1) {
				char curr_byte = (char) temp;
				if (curr_byte != '\n') {
					curr_line.append(curr_byte);
				}
				else {
					result = split_line(curr_line);
					if (result != null) {
						schedules.add(result);
					}
					curr_line.setLength(0);
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try_close(input);
		}

		return schedules;
	}
	
//	private List<HashMap<String, String>> try_read(List<HashMap<String, String>> schedules, InputStream stream) {
//		try {
//			int temp = 0;
//
//			StringBuilder curr_line = new StringBuilder();
//			HashMap<String, String> result;
//			
//			while ((temp = stream.read()) != -1) {
//				char curr_byte = (char) temp;
//				if (curr_byte != '\n') {
//					curr_line.append(curr_byte);
//				}
//				else {
//					result = split_line(curr_line);
//					if (result != null) {
//						schedules.add(result);
//					}
//					curr_line.setLength(0);
//				}
//			}
//		}
//		catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		return schedules;
//	}
	
	private List<HashMap<String, String>> read_csv_from_file(String filename) {
		if (filename == null || filename.length() <= 0) {
			throw new IllegalArgumentException();
		}

		List<HashMap<String, String>> schedules = new ArrayList<HashMap<String, String>>(100);
		
		FileInputStream file_in = null;
		try {
			file_in = new FileInputStream(filename);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		if (file_in == null) {
			return schedules;
		}
		
		BufferedInputStream input = null;
		try {
			input = new BufferedInputStream(file_in);
			int temp = 0;

			StringBuilder curr_line = new StringBuilder();
			HashMap<String, String> result;
			
			while ((temp = input.read()) != -1) {
				char curr_byte = (char) temp;
				if (curr_byte != '\n') {
					curr_line.append(curr_byte);
				}
				else {
					result = split_line(curr_line);
					if (result != null) {
						schedules.add(result);
					}
					curr_line.setLength(0);
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try_close(input);
			try_close(file_in);
		}
				
		return schedules;
	}
	
	private void try_close(InputStream stream) {
		if (stream == null) {
			return;
		}
		try {
			stream.close();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	protected static EventList read_csv() {
		if (Constants.get_has_feed_been_read()) {
			return Constants.CSV_FEEDS_MASTER;
		}
		Constants.set_has_feed_been_read();
		
		CSVReader reader = new CSVReader();
		List<HashMap<String, String>> event_strings;
		EventList events = new EventList();
		
		if (!Constants.DEBUG) {
			event_strings = reader.read_csv_from_url(ALL_EVENTS_SCHEDULE);
			events.add(event_strings);
			
			event_strings = reader.read_csv_from_url(ALL_ROOMS_SCHEDULE);
			events.add(event_strings);
			
			event_strings = reader.read_csv_from_url(ALL_TODAYS_EVENTS);
			events.add(event_strings);
		}
		else {
			event_strings = reader.read_csv_from_file("calendar_events_feed_2210.csv");
			events.add(event_strings);
			
			event_strings = reader.read_csv_from_file("calendar_events_today_feed_2210.csv");
			events.add(event_strings);
			
			event_strings = reader.read_csv_from_file("calendar_rooms_feed_2210.csv");
			events.add(event_strings);	
		}

		return events;		
	}
	
	private HashMap<String, String> split_line(StringBuilder str) {
		if (str == null || str.length() < 0) {
			throw new IllegalArgumentException();
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
			if (curr_char == DELIMITER && delim_in_stack) {
				StringBuilder temp = new StringBuilder();
				char curr_stack_char;
				while (!stack.empty() && (curr_stack_char = stack.pop()) != DELIMITER) {
					temp.append(curr_stack_char);
				}
				delim_in_stack = false;
				
				temp_to_str = temp.toString();
				temp_to_str = regex_replace(temp_to_str, "(  )+", " ");
				temp_to_str = regex_replace(temp_to_str, "(CST|CDT|registrar?( - )*|room?(: )*)?[():,]*", "");

				if (temp_to_str.length() > 0) {
					if (containsIgnoreCase(temp_to_str, Constants.GDC) ||
						is_campus_building_str(temp_to_str)) {
						tuple.put(Constants.LOCATION, temp_to_str);
//						if (Constants.DEBUG) System.out.println(temp_to_str);
					}
					else if (is_date_string(temp_to_str)) {
						if (containsIgnoreCase(temp_to_str, Constants.ALL_DAY)) {
							temp_to_str = regex_replace(temp_to_str, "(" + Constants.ALL_DAY + ")", "0001");
							String copy = temp_to_str;
							copy = regex_replace(copy, "0001", "2359");
							tuple.put(Constants.END_DATE, copy);
						}
						tuple.put(Constants.START_DATE, temp_to_str);
//						if (Constants.DEBUG) System.out.println(temp_to_str);
					}
					else {
						String curr_event_name;
						if ((curr_event_name = tuple.get(Constants.EVENT_NAME)) != null) {
							temp_to_str = new StringBuilder().append(temp_to_str).append(curr_event_name).toString();
						}
						tuple.put(Constants.EVENT_NAME, temp_to_str);
//						if (Constants.DEBUG) System.out.println(temp_to_str);
					}
				}
			}
			else if (curr_char == DELIMITER && !delim_in_stack) {
				stack.push(curr_char);
				delim_in_stack = true;
			}
			else {
				stack.push(curr_char);
			}
		}
		
		if (!stack.empty()) {
//			System.out.println("\n\n\nFUCKLES");
			int num_delims = 0;
			while (!stack.empty()) {
				if ((curr_char = stack.pop()) == DELIMITER) {
					num_delims++;
				}
			}
			if (num_delims % 2 > 0) {
//				System.out.println("Num delims: " + num_delims);
				return null;
			}
		}
		
		if (tuple.get(Constants.LOCATION) == null) {
			tuple.put(Constants.LOCATION, Constants.GDC + Constants.DEFAULT_GDC_LOCATION);
		}

//		if (Constants.DEBUG) System.out.println();
		
		return tuple;
	}
	
	private boolean containsIgnoreCase(String src, String what) {
		return (Utilities.containsIgnoreCase(src, what));
	}
		
	private boolean is_date_string(String str) {
		if (str == null) {
			return false;
		}
		for (int i = 0; i < Constants.DAYS_OF_WEEK_LONG.length; i++) {
			if (containsIgnoreCase(str, Constants.DAYS_OF_WEEK_SHORT[i]) || containsIgnoreCase(str, Constants.DAYS_OF_WEEK_LONG[i])) {
				return true;
			}
		}
		return false;
	}
	
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
	
	private String regex_replace(String str, String regex, String replace_with) {
		return (Utilities.regex_replace(str, regex, replace_with));
	}
	
	public static void main(String[] args) throws IOException {
		
//		try {
//			Thread.sleep(500);
//		}
//		catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		
		Stopwatch stopwatch = new Stopwatch();
		double time_to_read, time_to_exec;
		int num_events_in_raw_feeds;	//, num_events_available;
		
//		CSVReader test = new CSVReader();
//		List<HashMap<String, String>> event_strings;
		EventList events = new EventList();

		stopwatch.start();
//		events = read_csv();
		events = Constants.CSV_FEEDS_MASTER;
		stopwatch.stop();
		time_to_read = stopwatch.time();
		num_events_in_raw_feeds = events.get_size();

		events.sort_by_event_name(true);
		events.sort_by_start_date(true);
				
//		if (Constants.DEBUG) {
//			System.out.println(events.toString());
//			System.out.println("##########################################################################\n");
//		}
		
		stopwatch.start();
				
//		events.sort_by_end_date(false);
//		events.sort_by_location(true);
//		events = events.get_all_rooms_by_date("Oct 23 2014");

//		num_events_available = events.get_size();
		
		Date date = Utilities.get_date(10, 22, 2014, 1630);
		if (date == null) {
			throw new IllegalStateException();
		}
		Query query = new Query(date);
		query.set_duration(59);
//		query = new Query(Utilities.get_date());
		
//		events.test(query);
		String random_room = query.search();

		stopwatch.stop();
		time_to_exec = stopwatch.time();
		
		System.out.println("Random room chosen:\t" + random_room + "\n");
		System.out.println("Query:\n" + query.toString() + "\n");
		
		System.out.println("Done.\n");
		System.out.println("Num events, raw feeds:\t" + num_events_in_raw_feeds);
//		System.out.println("Num events avail at\n    " + query + ":\t" + num_events_available);
		System.out.println("Time to read feeds:\t" + time_to_read + " seconds");
		System.out.println("Time to process:\t" + time_to_exec + " seconds");
		System.out.println("Total time elapsed:\t" + (time_to_read + time_to_exec) + " seconds");

//		Date one_start = Utilities.get_date(10, 22, 2014, 1500);
//		Date one_end = Utilities.get_date(10, 22, 2014, 1700);
//		Date two_start = Utilities.get_date(10, 22, 2014, 1630);
//		Date two_end = Utilities.get_date(10, 22, 2014, 1830);
//		System.out.println("Times overlap: " + EventObjectList.times_overlap(one_start, one_end, two_start, two_end));
//		System.out.println("Occur on same day: " + EventObjectList.occur_on_same_day(one_start, two_start));
		
//		System.out.println("##########################################################################\n");
//		EventObjectList clone = Constants.CSV_FEEDS_MASTER.clone();
//		clone.sort_by_event_name(true);
//		System.out.println(clone.toString());
//		System.out.println("Clone size: " + clone.get_size());
		
	}

}

/*
	
	protected String time_to_24h(String time) {
		if (time == null || time.length() <= 0) {
			throw new IllegalArgumentException();
		}
		
		time = time.toLowerCase();
		time = time.replaceAll(":", "");
		time = time.replaceAll(" ", "");
		if (time.contains("am")) {
			time = time.replaceAll("am", "");
			int temp = Integer.parseInt(time);		// negative times?
			if (temp >= 1200 && temp <= 1259) {
				time = Integer.toString(temp -= 1200);
				StringBuilder pad = new StringBuilder(8);
				for (int i = time.length(); i < 4; i++) {
					pad.append("0");
				}
				time = pad.append(time).toString();
			}
			
		}
		else if (time.contains("pm")) {
			time = time.replaceAll("pm", "");
			int temp = Integer.parseInt(time);		// negative times?
			if (temp >= 100 && temp <= 1159) {
				time = Integer.toString(temp += 1200);
			}
		}
		
		return time;
	}
	
	protected String time_to_12h(String time) {
		if (time == null || time.length() <= 0) {
			throw new IllegalArgumentException();
		}

		time = time.replaceAll(":", "");
		time = time.replaceAll(" ", "");
		time = time.substring(0, 4);
		
		try {
			Integer.parseInt(time);
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
		}

		DateFormat format_1 = new SimpleDateFormat("hhmm");
		Date date = null;
		try {
			date = format_1.parse(time);
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		
		DateFormat format_2 = new SimpleDateFormat("h:mma");
		
		return format_2.format(date).toLowerCase();
	}
	
	// http://commons.apache.org/proper/commons-csv/download_csv.cgi
	// http://www.journaldev.com/924/how-to-download-file-from-url-in-java
	public static void blah() throws IOException {
		URL url = new URL(ALL_EVENTS_SCHEDULE);
		BufferedInputStream bis = new BufferedInputStream(url.openStream());
		FileOutputStream fis = new FileOutputStream("temp.txt");
		byte[] buffer = new byte[1024];
		int count = 0;
		while ((count = bis.read(buffer, 0, 1024)) != -1) {
			fis.write(buffer, 0, count);
		}
		fis.close();
		bis.close();
	}
	
	protected void read_csv_from_url_all(String site) throws IOException, URISyntaxException {
		if (site == null || site.length() <= 0) {
			throw new IllegalArgumentException();
		}
		
		site = new URI(site).toString();	// check url now?
		
		URL url = new URL(site);
		
		BufferedInputStream input = new BufferedInputStream(url.openStream());
		int temp = 0;
		int num_bytes_read = 0;
		
		boolean mark_supported = input.markSupported();
		if (mark_supported) {
			input.mark(0);
		}

		while ((temp = input.read()) != -1) {
			num_bytes_read++;
		}

		if (mark_supported) {
			input.reset();
		}
		else {
			input.close();
			input = new BufferedInputStream(url.openStream());
		}
		
		byte[] buffer = new byte[num_bytes_read];
		while ((temp = input.read(buffer, 0, num_bytes_read)) != -1) {
			String out = new String(buffer);
			System.out.print(out);
			
	//		try {
	//			String out = new String(buffer, "UTF-8");
	//			System.out.print(out);
	//		}
	//		catch (UnsupportedEncodingException e) {
	//			e.printStackTrace();
	//		}
		}
		
		input.close();
	}
		
	private ArrayList<String> split_line(StringBuilder str) {
		if (str == null || str.length() < 0) {
			throw new IllegalArgumentException();
		}
		else if (str.length() == 0) {
			return (new ArrayList<String>());
		}
		
		ArrayList<String> tuple = new ArrayList<String>(10);
		boolean delim_in_stack = false;
		
		Stack<Character> stack = new Stack<Character>();
		if (str.charAt(0) != DELIMITER) {
			return tuple;
		}
		else {
			stack.push(str.charAt(0));
			delim_in_stack = true;
		}
		
		int length = str.length();
		char curr_char;
		String temp_to_str = null;
		for (int i = 1; i < length; i++) {
			curr_char = str.charAt(i);
			if (curr_char == DELIMITER && delim_in_stack) {
				StringBuilder temp = new StringBuilder();
				char curr_stack_char;
				while (!stack.empty() && (curr_stack_char = stack.pop()) != DELIMITER) {
					if (curr_stack_char != ':' && curr_stack_char != ',') {
						temp.append(curr_stack_char);
					}
				}
				delim_in_stack = false;
				temp = temp.reverse();
				
				// ******************** CHANGE TO REGEX WHEN INITIAL TESTING COMPLETE ***********************
				
				temp_to_str = temp.toString();
				if (containsIgnoreCase(temp_to_str, "registrar")) {
					int new_start_index = "registrar".length();
					if (containsIgnoreCase(temp_to_str, " - ")) {
						new_start_index += " - ".length();
					}
					temp_to_str = temp.substring(new_start_index, temp.length());
				}
				else if (containsIgnoreCase(temp_to_str, "room")) {
					int new_start_index = "room:".length();
					if (length > "room:".length() && temp_to_str.charAt(5) == ' ') {
						new_start_index++;
					}
					temp_to_str = temp.substring(new_start_index, temp.length());
				}
				
				while (containsIgnoreCase(temp_to_str, "  ")) {
					temp_to_str = temp_to_str.replaceAll("  ", " ");
				}

				temp_to_str = temp_to_str.replaceAll("CST", "");
				temp_to_str = temp_to_str.replaceAll("CDT", "");
				
				// ******************** CHANGE TO REGEX WHEN INITIAL TESTING COMPLETE ***********************
				
				if (temp_to_str.length() > 0) {
					tuple.add(temp_to_str);
					System.out.println(temp_to_str);
				}
			}
			else if (curr_char == DELIMITER && !delim_in_stack) {
//				if (stack.empty()) {
//					return (new ArrayList<String>());
//				}
				stack.push(curr_char);
				delim_in_stack = true;
			}
			else {
				stack.push(curr_char);
			}
		}
		
		System.out.println();
		
		if (!stack.empty()) {
//			System.out.println("\n\n\nFUCKLES");
			int num_delims = 0;
			while (!stack.empty()) {
				if ((curr_char = stack.pop()) == DELIMITER) {
					num_delims++;
				}
			}
			if (num_delims % 2 > 0) {
				System.out.println("Num delims: " + num_delims);
				return (new ArrayList<String>());
			}
		}
		
		return tuple;
	}
	
*/

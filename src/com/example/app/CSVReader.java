package com.example.app;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.content.Context;
import android.util.Log;

/**
 * @author Fatass
 *
 */
@SuppressWarnings("unused")
final class CSVReader {
	
	private static final String ALL_EVENTS_SCHEDULE		= "https://www.cs.utexas.edu/calendar/touch/feed";			// events
	private static final String ALL_ROOMS_SCHEDULE		= "https://www.cs.utexas.edu/calendar/touch/all/feed";		// rooms
	private static final String ALL_TODAYS_EVENTS		= "https://www.cs.utexas.edu/calendar/touch/today/feed";	// today's events
	
	private static final char DELIMITER = '\"';
	private static final String TAG = "CSVReader";
	
	protected static int lines_read = 0;
	protected static double time_to_read = 0;
	
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
		if (str == null || str.length() <= 0) {
			return false;
		}
		
		String[] split = str.split("\\s+");
		
		for (int i = 0; i < split.length; i++) {
			for (int j = 1; j < Constants.DAYS_OF_WEEK_SHORT.length; j++) {
				if (split[i].equalsIgnoreCase(Constants.DAYS_OF_WEEK_SHORT[j])) {
					return true;
				}
			}
		}
		
//		for (int i = 0; i < Constants.DAYS_OF_WEEK_LONG.length; i++) {
//			if (containsIgnoreCase(str, Constants.DAYS_OF_WEEK_SHORT[i]) ||
//				containsIgnoreCase(str, Constants.DAYS_OF_WEEK_LONG[i])) {
//				
//				return true;
//			}
//		}
		
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
	
	protected static EventList read_csv(Context context) {
		return read_csv(context, Constants.DEBUG);
	}
		
	/**
	 * @return An EventList of events parsed from a CSV feed(s).
	 */
	protected static EventList read_csv(Context context, boolean read_from_local_feeds) {
//		if (Constants.CSV_FEEDS_MASTER != null) {
//			return Constants.CSV_FEEDS_MASTER.clone();
//		}
		
		if (Constants.get_has_feed_been_read()) {
			return Constants.CSV_FEEDS_MASTER.clone();
		}
		Constants.set_has_feed_been_read();
		
		CSVReader reader = new CSVReader();
		List<HashMap<String, String>> event_strings;
		EventList events = new EventList();
				
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start();

		if (!read_from_local_feeds) {
			Log.d(TAG, "Now reading from UTCS servers");
//			event_strings = reader.read_csv_from_url(ALL_EVENTS_SCHEDULE);
//			events.add(event_strings);
//			
//			event_strings = reader.read_csv_from_url(ALL_ROOMS_SCHEDULE);
//			events.add(event_strings);
//			
//			event_strings = reader.read_csv_from_url(ALL_TODAYS_EVENTS);
//			events.add(event_strings);
			try {
				event_strings = reader.read_csv_from_url(new URL(ALL_EVENTS_SCHEDULE));
				events.add(event_strings);

				event_strings = reader.read_csv_from_url(new URL(ALL_ROOMS_SCHEDULE));
				events.add(event_strings);

				event_strings = reader.read_csv_from_url(new URL(ALL_TODAYS_EVENTS));
				events.add(event_strings);
			}
			catch (IOException e) {
				Log.d(TAG, "Failed to read due to malformed URL");
			}
		}
		else {
			Log.d(TAG, "Now reading from local files");
			
			String ext = ".csv";
			
//			event_strings = reader.read_csv_from_file("calendar_events_today_feed_2710.csv");
//			event_strings = reader.read_csv_from_file(context, R.raw.calendar_events_today_feed_1411);
			event_strings = reader.read_csv_from_file(context, "calendar_events_today_feed_1411" + ext);
			events.add(event_strings);
			
//			event_strings = reader.read_csv_from_file("calendar_events_feed_2710.csv");
//			event_strings = reader.read_csv_from_file(context, R.raw.calendar_events_feed_1411);
			event_strings = reader.read_csv_from_file(context, "calendar_events_feed_1411" + ext);
			events.add(event_strings);

//			event_strings = reader.read_csv_from_file("calendar_rooms_feed_2710.csv");
//			event_strings = reader.read_csv_from_file(context, R.raw.calendar_rooms_feed_1411);
			event_strings = reader.read_csv_from_file(context, "calendar_rooms_feed_1411" + ext);
			events.add(event_strings);	
		}
		
		stopwatch.stop();
		
		time_to_read = stopwatch.time();
//		Log.d(TAG, "Took " + time_to_read + " seconds to read " + lines_read + " lines");
		Log.d(TAG, "Took " + (time_to_read + Constants.time_to_read) + " seconds to read CSV feeds (" + lines_read + " lines) and load GDC course schedule");
		Log.d(TAG, "Num events supposed to be in CSV_FEEDS_MASTER: " + events.get_size());
//		Toast.makeText(context, "Took " + stopwatch.time() + " seconds to read " + lines_read + " lines", Toast.LENGTH_SHORT).show();	// DO NOT TOAST HERE; WILL CAUSE EXCEPTION (can't update UI thread here)
		
		return events;		
	}
		
	private List<HashMap<String, String>> read_csv_from_file(Context context, String filename) {
		if (context == null) {
			throw new IllegalArgumentException();
		}
		else if (filename == null || filename.length() <= 0) {
			throw new IllegalArgumentException("Error: cannot read from null or empty file name, read_csv_from_file()");
		}

		List<HashMap<String, String>> schedules = new ArrayList<HashMap<String, String>>(100);
		InputReader input = new InputReader(context, filename);
		
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
				
				lines_read++;
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
	 * @param filename
	 * @return a List of Maps of Strings; each Map contains Strings for one
	 * 		   event parsed from the CSV feed given by filename
	 */
	private List<HashMap<String, String>> read_csv_from_file(Context context, int res_id) {
//		if (filename == null || filename.length() <= 0) {
//			throw new IllegalArgumentException("Error: cannot read from null or empty file name, read_csv_from_file()");
//		}
//
//		Log.d("CSVReader", "filename is " + filename + ", read_csv_from_file");
		
		List<HashMap<String, String>> schedules = new ArrayList<HashMap<String, String>>(100);
//		InputReader input = new InputReader(filename);
		InputReader input = new InputReader(context, res_id);
		
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
//Log.d(TAG, curr_line.toString());
				
				if (curr_line.toString().equals("Location") || curr_line.toString().equals("Title")) {
					curr_line.setLength(0);
					continue;
				}
				
				lines_read++;
				result = split_line(curr_line);
				if (result != null) {
//Log.d(TAG, result.toString());
					schedules.add(result);
				}
				curr_line.setLength(0);
			}
		}
		
		input.close();
		return schedules;
	}
	
	private List<HashMap<String, String>> read_csv_from_url(URL url) {

		List<HashMap<String, String>> schedules = new ArrayList<HashMap<String, String>>(100);
		
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
		}
		catch (IOException e) {
			Log.d(TAG, "Error establishing connection");
		}
		
		if (connection == null) {
			throw new IllegalStateException();
		}
		
		InputReader reader = null;
		try {
			reader = new InputReader(connection.getInputStream());
		}
		catch (IOException e) {
			Log.d(TAG, "Error opening reader");
		}
		
		int temp = 0;
		
		HashMap<String, String> result;
		StringBuilder curr_line = new StringBuilder();
		while ((temp = reader.read()) != -1) {
//Log.d(TAG, Character.toString((char) temp));
			char curr_byte = (char) temp;
			if (curr_byte != '\n') {
				curr_line.append(curr_byte);
			}
			
			// end of line reached in this CSV feed; parse this event
			else {
				lines_read++;
				result = split_line(curr_line);
				if (result != null) {
					schedules.add(result);
				}
				curr_line.setLength(0);
			}
		}
		
		if (connection != null) {
			connection.disconnect();
		}
		
		Log.d(TAG, "Now exiting reading from URL");
		
//		reader.close();
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
		
		HttpClient http_client = new DefaultHttpClient();
		HttpContext local_context = new BasicHttpContext();
		HttpGet http_get = new HttpGet(site);

		HttpResponse http_response = null;
		try {
			http_response = http_client.execute(http_get, local_context);
			Log.d(TAG, "HTTP request processed with status code " + http_response.getStatusLine().getStatusCode());
		}
		catch (IOException e) {
			Log.d(TAG, "Unable to execute client request");
			e.printStackTrace();
		}
		
		if (http_response == null) {
			throw new IllegalStateException();
		}
		
		InputReader reader = null;
		try {
			reader = new InputReader(new InputStreamReader(http_response.getEntity().getContent()));
		}
		catch (IOException e) {
			Log.d(TAG, "Unable to open reader");
			e.printStackTrace();
		}
		
		int temp = 0;
		
		HashMap<String, String> result;
		StringBuilder curr_line = new StringBuilder();
		while ((temp = reader.read()) != -1) {
//Log.d(TAG, Character.toString((char) temp));
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
		
		Log.d(TAG, "Now exiting reading from URL");
		
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

				if (temp_to_str.equals("Location") || temp_to_str.equals("Title")) {
					return null;
				}
				
				if (temp_to_str.length() > 0) {
										
					// location encountered
					if (containsIgnoreCase(temp_to_str, Constants.GDC) ||
						is_campus_building_str(temp_to_str)) {
//						Log.d(TAG, "location " + temp_to_str + " " + temp_to_str.length());
						tuple.put(Constants.LOCATION, temp_to_str);
					}
					
					// event time and date encountered
					else if (is_date_string(temp_to_str)) {
//						Log.d(TAG, "date string |" + temp_to_str + "| " + temp_to_str.length());
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
//						Log.d(TAG, "event name " + temp_to_str + " " + temp_to_str.length());
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
//			tuple.put("location", "GDC Gateshenge");
		}

//		Log.d(TAG, tuple.toString());
		
		return tuple;
	}

}		// end of file





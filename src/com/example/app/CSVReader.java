package com.example.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * @author Fatass
 *
 */
final class CSVReader {

	private static final String CSV_EXT = ".csv";
	
	private static final String ALL_EVENTS_SCHEDULE		= "https://www.cs.utexas.edu/calendar/touch/feed";			// events
	private static final String ALL_ROOMS_SCHEDULE		= "https://www.cs.utexas.edu/calendar/touch/all/feed";		// rooms
	private static final String ALL_TODAYS_EVENTS		= "https://www.cs.utexas.edu/calendar/touch/today/feed";	// today's events
	
	protected static final String ALL_EVENTS_SCHEDULE_FILENAME 	= "calendar_events_feed" + CSV_EXT;
	protected static final String ALL_ROOMS_SCHEDULE_FILENAME 	= "calendar_rooms_feed" + CSV_EXT;
	protected static final String ALL_TODAYS_EVENTS_FILENAME 	= "calendar_events_today_feed" + CSV_EXT;
	
	private static final int DAILY_UPDATE_TIME	=	859;	// 8:59a; update if curr time is 9a or after

	private static final char DELIMITER	= '\"';
	private static final String TAG 	= "CSVReader";
	
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
		
		for (String room : Constants.CAMPUS_BUILDINGS.keySet()) {
			if (compare.equals(room) && !is_ignored_room(str)) {
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

			try {
				event_strings = reader.read_csv_from_url(context, new URL(ALL_EVENTS_SCHEDULE));
				events.add(event_strings);

				event_strings = reader.read_csv_from_url(context, new URL(ALL_ROOMS_SCHEDULE));
				events.add(event_strings);

				event_strings = reader.read_csv_from_url(context, new URL(ALL_TODAYS_EVENTS));
				events.add(event_strings);
			}
			catch (MalformedURLException e) {
				Log.d(TAG, "Failed to read due to malformed URL");
			}
			finally {
				if (get_csv_feeds_write_success(context, Constants.CSV_FEED_ALL_EVENTS_WRITE_SUCCESS) &&
					get_csv_feeds_write_success(context, Constants.CSV_FEED_ALL_ROOMS_WRITE_SUCCESS) &&
					get_csv_feeds_write_success(context, Constants.CSV_FEED_ALL_TODAYS_EVENTS_WRITE_SUCCESS)) {

					set_csv_feeds_write_success(context, Constants.CSV_FEEDS_WRITE_SUCCESS, true);
					Log.d(TAG, "All 3 feeds read successfully, end of read_csv()");
				}
				else {
					set_csv_feeds_write_success(context, Constants.CSV_FEEDS_WRITE_SUCCESS, false);
					
					set_csv_feeds_write_success(context, Constants.CSV_FEED_ALL_EVENTS_WRITE_SUCCESS, false);
					set_csv_feeds_write_success(context, Constants.CSV_FEED_ALL_ROOMS_WRITE_SUCCESS, false);
					set_csv_feeds_write_success(context, Constants.CSV_FEED_ALL_TODAYS_EVENTS_WRITE_SUCCESS, false);
					
					Log.d(TAG, "One or more feeds not successfully read, end of read_csv()");
				}
			}
		}
		else {
			Log.d(TAG, "Now reading from local files");
			
			event_strings = reader.read_csv_from_file(context, "calendar_events_today_feed_0412" + CSV_EXT, true);
			events.add(event_strings);
			
			event_strings = reader.read_csv_from_file(context, "calendar_events_feed_0412" + CSV_EXT, true);
			events.add(event_strings);

			event_strings = reader.read_csv_from_file(context, "calendar_rooms_feed_0412" + CSV_EXT, true);
			events.add(event_strings);
						
		}
		
		stopwatch.stop();
		
		time_to_read = stopwatch.time();
		Log.d(TAG, "Took " + (time_to_read + Constants.time_to_read) + " seconds to read CSV feeds (" + lines_read + " lines) and load GDC course schedule");
		Log.d(TAG, "Num events supposed to be in CSV_FEEDS_MASTER: " + events.get_size());
		
		return events;	
	}
		
	private List<HashMap<String, String>> read_csv_from_file(Context context, String filename, boolean file_is_asset) {
		if (context == null) {
			throw new IllegalArgumentException();
		}
		else if (filename == null || filename.length() <= 0) {
			throw new IllegalArgumentException("Error: cannot read from null or empty file name, read_csv_from_file()");
		}

		List<HashMap<String, String>> schedules = new ArrayList<HashMap<String, String>>(100);
		InputReader input;
		if (file_is_asset) {
			input = InputReader.get_asset_reader(context, filename);
		}
		else {
			try {
				FileInputStream input_stream = context.openFileInput(filename);
				if (input_stream == null) {
					Log.d(TAG, "Failed to get FileInputStream for file " + filename);
					return schedules;
				}
								
				input = new InputReader(input_stream);
			}
			catch (FileNotFoundException e) {
				Log.d(TAG, "Failed to open file " + filename + " for reading");
				return schedules;
			}
		}
				
		int temp;

		StringBuilder curr_line = new StringBuilder();
		HashMap<String, String> result;

		while ((temp = input.read()) != -1) {
			char curr_byte = (char) temp;
			
			if (curr_byte != '\n') {
				curr_line.append(curr_byte);
			}
			
			// end of line reached in file; parse this event
			else {
				lines_read++;
				result = split_line(curr_line);
				if (result != null) {
					schedules.add(result);
				}
				curr_line.setLength(0);
			}
		}
		
		input.close();
		return schedules;
	}
	
	private boolean file_exists(Context context, String filename) {
		if (context == null) {
			throw new IllegalArgumentException("Cannot accept null Context argument");
		}
		else if (filename == null) {
			throw new IllegalArgumentException("Null filename specified");
		}
		
		File file = context.getFileStreamPath(filename);
		boolean file_exists = file.exists();
		if (file_exists) {
			Log.d(TAG, "File " + filename + " exists and has length " + file.length() + " bytes");
		}
		return file_exists;
	}
	
	private boolean file_is_current(Context context, String filename) {
		if (context == null) {
			throw new IllegalArgumentException("Cannot accept null Context argument");
		}
		else if (filename == null) {
			throw new IllegalArgumentException("Null filename specified");
		}
		
		if (!file_exists(context, filename)) {
			Log.d(TAG, "File " + filename + " does not exist");
			return false;
		}
		
		String dir = context.getFilesDir().getAbsolutePath();
		File file = new File(dir + "/" + filename);
		
		Log.d(TAG, "In " + dir + "/" + filename + ", checking if file is current");
		
		Date last_modified = new Date(file.lastModified());
				
		Calendar calendar = Calendar.getInstance();
		Date curr_date = calendar.getTime();
		int curr_month = calendar.get(Calendar.MONTH) + 1;
		int curr_day = calendar.get(Calendar.DAY_OF_MONTH);
		int curr_day_of_year = calendar.get(Calendar.DAY_OF_YEAR);
		int curr_year = calendar.get(Calendar.YEAR);
		
		Date update_time = Utilities.get_date(curr_month, curr_day, curr_year, DAILY_UPDATE_TIME);
		calendar.setTime(last_modified);
		
		Log.d(TAG, "Last modified, " + filename + ": " + last_modified.toString());
		Log.d(TAG, "Update time: " + update_time.toString());
		
		if (calendar.get(Calendar.DAY_OF_YEAR) == curr_day_of_year && calendar.get(Calendar.YEAR) == curr_year && last_modified.after(update_time)) {
//			if (filename.equals(ALL_EVENTS_SCHEDULE_FILENAME)) {
//				return (get_csv_feeds_write_success(context, Constants.CSV_FEED_ALL_EVENTS_WRITE_SUCCESS));
//			}
//			else if (filename.equals(ALL_ROOMS_SCHEDULE_FILENAME)) {
//				return (get_csv_feeds_write_success(context, Constants.CSV_FEED_ALL_ROOMS_WRITE_SUCCESS));
//			}
//			else if (filename.equals(ALL_TODAYS_EVENTS_FILENAME)) {
//				return (get_csv_feeds_write_success(context, Constants.CSV_FEED_ALL_TODAYS_EVENTS_WRITE_SUCCESS));
//			}
			Log.d(TAG, "Returning true, pos 1");
			return true;
		}
		else if (calendar.get(Calendar.DAY_OF_YEAR) == curr_day_of_year - 1 && calendar.get(Calendar.YEAR) == curr_year && update_time.after(curr_date)) {
//			Log.d(TAG, "Maybe returning true, pos 2");
			Date yesterday_update_time = Utilities.get_date(curr_month, curr_day - 1, curr_year, DAILY_UPDATE_TIME);
			if (last_modified.after(yesterday_update_time)) {
				Log.d(TAG, "Returning true, pos 2");
				return true;
			}
		}
		
		return false;
	}
	
	private static boolean get_csv_feeds_write_success(Context context, String pref_name) {
		if (context == null) {
			throw new IllegalArgumentException("Cannot accept null Context argument");
		}
		else if (pref_name == null) {
			throw new IllegalArgumentException("Cannot accept null String argument");
		}
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		if (!prefs.contains(pref_name)) {
			return false;
		}
		return (prefs.getBoolean(pref_name, false));
	}
		
	private static boolean set_csv_feeds_write_success(Context context, String pref_name, boolean success) {
		if (context == null) {
			throw new IllegalArgumentException("Cannot accept null Context argument");
		}
		else if (pref_name == null) {
			throw new IllegalArgumentException("Cannot accept null String argument");
		}
		
		SharedPreferences.Editor prefs_edit = PreferenceManager.getDefaultSharedPreferences(context).edit();
		prefs_edit.putBoolean(pref_name, success);
		return (prefs_edit.commit());
	}
	
	private boolean file_delete(Context context, String filename) {
		if (context == null) {
			throw new IllegalArgumentException("Cannot accept null Context argument");
		}
		else if (filename == null) {
			throw new IllegalArgumentException("Null filename specified");
		}
		
		return (context.deleteFile(filename));
	}
	
	protected void delete_all_feeds(Context context) {
		if (context == null) {
			throw new IllegalArgumentException("Cannot accept null Context argument");
		}
		
		String download_filename;
		boolean deleted = false;
		
		download_filename = ALL_EVENTS_SCHEDULE_FILENAME;
		if (file_exists(context, download_filename)) {
			deleted = file_delete(context, download_filename);
			Log.d(TAG, "File " + download_filename + " was deleted and no longer exists in internal storage directory (delete_all()): " + deleted);
		}
		
		download_filename = ALL_ROOMS_SCHEDULE_FILENAME;
		if (file_exists(context, download_filename)) {
			deleted = file_delete(context, download_filename);
			Log.d(TAG, "File " + download_filename + " was deleted and no longer exists in internal storage directory (delete_all()): " + deleted);
		}
		
		download_filename = ALL_TODAYS_EVENTS_FILENAME;
		if (file_exists(context, download_filename)) {
			deleted = file_delete(context, download_filename);
			Log.d(TAG, "File " + download_filename + " was deleted and no longer exists in internal storage directory (delete_all()): " + deleted);
		}
	}
	
	private List<HashMap<String, String>> read_csv_from_url(Context context, URL url) {
		if (context == null) {
			throw new IllegalArgumentException("Cannot accept null Context argument");
		}
		else if (url == null) {
			throw new IllegalArgumentException("Cannot accept null URL argument");
		}

		final boolean download_new_csv_copy;
		String download_filename = null;
		if (!Constants.STORE_LOCAL_COPY_CSV_FEEDS) {
			delete_all_feeds(context);
			download_new_csv_copy = false;
		}		
		else {
			String url_str = url.toString();
			if (url_str.equals(ALL_EVENTS_SCHEDULE)) {
				download_filename = ALL_EVENTS_SCHEDULE_FILENAME;
				
				if (file_exists(context, download_filename)) {
					if (file_is_current(context, download_filename)) {
						if (!get_csv_feeds_write_success(context, Constants.CSV_FEED_ALL_EVENTS_WRITE_SUCCESS)) {
							file_delete(context, download_filename);
							download_new_csv_copy = true;
							Log.d(TAG, "File " + download_filename + " was corrupt and needs to be re-downloaded");
						}
						else {
							Log.d(TAG, "File " + download_filename + " already exists and is up-to-date in internal storage directory");
							return (read_csv_from_file(context, download_filename, false));
						}
					}
					else {
						Log.d(TAG, "File " + download_filename + " is not current");
						boolean deleted = file_delete(context, download_filename);
						download_new_csv_copy = true;
						
						Log.d(TAG, "File " + download_filename + " was deleted and no longer exists in internal storage directory: " + deleted);
					}
				}
				else {
					download_new_csv_copy = true;
					
					Log.d(TAG, "File " + ALL_EVENTS_SCHEDULE_FILENAME + " does not exist in internal storage directory");
				}
			}
			else if (url_str.equals(ALL_ROOMS_SCHEDULE)) {
				download_filename = ALL_ROOMS_SCHEDULE_FILENAME;
				
				if (file_exists(context, download_filename)) {
					if (file_is_current(context, download_filename)) {						
						if (!get_csv_feeds_write_success(context, Constants.CSV_FEED_ALL_ROOMS_WRITE_SUCCESS)) {
							file_delete(context, download_filename);
							download_new_csv_copy = true;
							Log.d(TAG, "File " + download_filename + " was corrupt and needs to be re-downloaded");
						}
						else {
							Log.d(TAG, "File " + download_filename + " already exists and is up-to-date in internal storage directory");
							return (read_csv_from_file(context, download_filename, false));
						}
					}
					else {
						Log.d(TAG, "File " + download_filename + " is not current");
						boolean deleted = file_delete(context, download_filename);
						download_new_csv_copy = true;
						
						Log.d(TAG, "File " + download_filename + " was deleted and no longer exists in internal storage directory: " + deleted);
					}
				}
				else {
					download_new_csv_copy = true;
					
					Log.d(TAG, "File " + download_filename + " does not exist in internal storage directory");
				}
			}
			else {
				download_filename = ALL_TODAYS_EVENTS_FILENAME;
				
				if (file_exists(context, download_filename)) {
					if (file_is_current(context, download_filename)) {
						if (!get_csv_feeds_write_success(context, Constants.CSV_FEED_ALL_TODAYS_EVENTS_WRITE_SUCCESS)) {
							file_delete(context, download_filename);
							download_new_csv_copy = true;
							Log.d(TAG, "File " + download_filename + " was corrupt and needs to be re-downloaded");
						}
						else {
							Log.d(TAG, "File " + download_filename + " already exists and is up-to-date in internal storage directory");
							return (read_csv_from_file(context, download_filename, false));
						}
					}
					else {
						Log.d(TAG, "File " + download_filename + " is not current");
						boolean deleted = file_delete(context, download_filename);
						download_new_csv_copy = true;
						
						Log.d(TAG, "File " + download_filename + " was deleted and no longer exists in internal storage directory: " + deleted);
					}
				}
				else {
					download_new_csv_copy = true;

					Log.d(TAG, "File " + download_filename + " does not exist in internal storage directory");
				}
			}			
		}

		List<HashMap<String, String>> schedules = new ArrayList<HashMap<String, String>>(100);

		set_csv_feeds_write_success(context, Constants.CSV_FEEDS_WRITE_SUCCESS, false);
		
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.connect();
		}
		catch (IOException e) {
			Log.d(TAG, "Error establishing connection:1");
		}
		
		if (connection == null) {
//			throw new IllegalStateException();
			Log.d(TAG, "Error establishing connection:2");
			return schedules;
		}
		
		InputStream conn_stream = null;
		try {
			conn_stream = connection.getInputStream();
		}
		catch (IOException e) {
			Log.w(TAG, "WARNING: failed to get InputStream from connection!");
		}
		finally {
			if (conn_stream == null) {
				Log.d(TAG, "Error getting connection InputStream");
				Log.d(TAG, "URL is " + url.toString());
				return schedules;
			}
		}
				
		InputReader reader = new InputReader(conn_stream);
		
		FileOutputWriter writer = null;
		if (download_new_csv_copy) {
			writer = FileOutputWriter.get_instance(context, download_filename);
			if (writer == null) {
				return schedules;
			}
		}
		
		int temp;
		String line_to_write;
		HashMap<String, String> result;
		StringBuilder curr_line = new StringBuilder();
		
		if (download_filename.equals(ALL_EVENTS_SCHEDULE_FILENAME)) {
			set_csv_feeds_write_success(context, Constants.CSV_FEED_ALL_EVENTS_WRITE_SUCCESS, false);
		}
		else if (download_filename.equals(ALL_ROOMS_SCHEDULE_FILENAME)) {
			set_csv_feeds_write_success(context, Constants.CSV_FEED_ALL_ROOMS_WRITE_SUCCESS, false);
		}
		else if (download_filename.equals(ALL_TODAYS_EVENTS_FILENAME)) {
			set_csv_feeds_write_success(context, Constants.CSV_FEED_ALL_TODAYS_EVENTS_WRITE_SUCCESS, false);
		}
		
		while ((temp = reader.read()) != -1) {
			char curr_byte = (char) temp;
			if (curr_byte != '\n') {
				curr_line.append(curr_byte);
			}
			
			// end of line reached in this CSV feed; parse this event
			else {
				lines_read++;
				
				if (download_new_csv_copy && writer != null) {
					line_to_write = curr_line.toString() + "\n";
					writer.write(line_to_write);
				}
				
				result = split_line(curr_line);
				if (result != null) {
					schedules.add(result);
				}
				curr_line.setLength(0);
			}
		}
		
		boolean success = true;
		if (writer.get_exception() != null) {
			success = false;
		}
		
		if (download_filename.equals(ALL_EVENTS_SCHEDULE_FILENAME)) {
			set_csv_feeds_write_success(context, Constants.CSV_FEED_ALL_EVENTS_WRITE_SUCCESS, success);
		}
		else if (download_filename.equals(ALL_ROOMS_SCHEDULE_FILENAME)) {
			set_csv_feeds_write_success(context, Constants.CSV_FEED_ALL_ROOMS_WRITE_SUCCESS, success);
		}
		else if (download_filename.equals(ALL_TODAYS_EVENTS_FILENAME)) {
			set_csv_feeds_write_success(context, Constants.CSV_FEED_ALL_TODAYS_EVENTS_WRITE_SUCCESS, success);
		}
		Log.d(TAG, "Successfully wrote copy of CSV feeds for " + download_filename + ": " + success + " (writer exception occurred: " + (writer.get_exception() != null) + ")");
		
		if (download_new_csv_copy && writer != null) {
			writer.close();
		}
		reader.close();
		
		if (connection != null) {
			connection.disconnect();
		}
		
		Log.d(TAG, "Now exiting reading from URL");
		
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
		
		HashMap<String, String> tuple = new HashMap<String, String>(Utilities.get_hashmap_size(3));
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
						tuple.put(Constants.LOCATION, temp_to_str);
					}
					
					// event time and date encountered
					else if (is_date_string(temp_to_str)) {
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
		
		// skip malformed lines by counting the number of remaining delimiters in the Stack
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

	@SuppressWarnings("unused")
	private static class InputReader {
		
		private static final String TAG = "InputReader";
		
		private static final String CHAR_ENCODING = "UTF-8";
		private static final int BUF_SIZE = 8192;	// bytes
		
		private BufferedReader reader;
		private Exception exception;
		
		private InputReader(Context context, String filename, boolean is_asset) {
			if (context == null || filename == null || filename.length() <= 0) {
				throw new IllegalArgumentException("Invalid argument, InputReader constructor");
			}
			
			this.exception = null;
			
			try {
				if (is_asset) {
					this.reader = new BufferedReader(new InputStreamReader(context.getAssets().open(filename), CHAR_ENCODING));
				}
				else {
					/* TODO: THIS WILL FAIL - DO NOT USE !!!!! */
					
					Log.e(TAG, "ERROR: do NOT use constructor if not reading asset!");
//					this.reader = new BufferedReader(new FileReader(filename));
					this.reader = null;
				}
			}
			catch (IOException e) {
				this.reader = null;
				this.exception = e;
			}			
		}
		
		protected static InputReader get_asset_reader(Context context, String filename) {
			if (context == null || filename == null || filename.length() <= 0) {
				throw new IllegalArgumentException("Invalid argument, get_asset_reader()");
			}
			
			InputReader out = new InputReader(context, filename, true);
			
			if (out.reader == null) {
				return null;
			}
			return out;
		}
		
		protected InputReader(InputStream stream) {
			if (stream == null) {
				throw new IllegalArgumentException("InputStream argument cannot be null, InputReader constructor");
			}
			
			this.reader = new BufferedReader(new InputStreamReader(stream), BUF_SIZE);
			this.exception = null;
		}
		
		protected InputReader(Context context, int res_id) {
			if (context == null) {
				throw new IllegalArgumentException("Context argument cannot be null, InputReader constructor");
			}
			
			InputStream input_stream = context.getResources().openRawResource(res_id);
			if (input_stream == null) {
				throw new IllegalArgumentException("Invalid resource ID specified (" + res_id + ")");
			}
			
			InputStreamReader input_stream_reader = new InputStreamReader(input_stream);
			this.reader = new BufferedReader(input_stream_reader, BUF_SIZE);
			this.exception = null;
		}
		
		protected Exception get_exception() {
			return this.exception;
		}
		
		protected int read() {
			if (this.reader == null) {
				Log.w(TAG, "WARNING: BufferedReader reader is uninitialised!");
				return -1;
			}
			
			try {
				return (this.reader.read());
			}
			catch (IOException e) {
				this.exception = e;
				return -1;
			}
		}
		
		protected String read_line() {
			if (this.reader == null) {
				Log.w(TAG, "WARNING: BufferedReader reader is uninitialised!");
				return null;
			}
			
			try {
				return (this.reader.readLine());
			}
			catch (IOException e) {
				this.exception = e;
				return null;
			}
		}
		
		protected boolean close() {
			if (this.reader == null) {
				Log.w(TAG, "WARNING: BufferedReader reader is uninitialised!");
				return false;
			}
			
			try {
				this.reader.close();
			}
			catch (IOException e) {
				this.exception = e;
				return false;
			}
			
			return true;
		}
	}
	
	private static class FileOutputWriter {
		
		private static final String TAG = "FileOutputWriter";
		
		private FileOutputStream writer;
		private Exception exception;
		
		protected static final FileOutputWriter get_instance(Context context, String filename) {
			if (context == null || filename == null || filename.length() <= 0) {
				throw new IllegalArgumentException("Invalid argument, get_instance()");
			}
			
			FileOutputWriter out = new FileOutputWriter(context, filename);
			
			if (out.writer == null) {
				return null;
			}
			return out;
		}
		
		private FileOutputWriter(Context context, String filename) {
			if (context == null || filename == null || filename.length() <= 0) {
				throw new IllegalArgumentException("Invalid argument, FileOutputWriter constructor");
			}
			
			try {
				this.writer = context.openFileOutput(filename, Context.MODE_PRIVATE);
				this.exception = null;
			}
			catch (IOException e) {
				this.writer = null;
				this.exception = e;
			}
		}
		
		protected Exception get_exception() {
			return this.exception;
		}
		
		protected boolean write(String line) {
			if (line == null) {
				throw new IllegalArgumentException("Argument cannot be null, write_line()");
			}
			else if (this.writer == null) {
				Log.w(TAG, "WARNING: FileOutputWriter writer is uninitialised!");
				return false;
			}
			
			try {
				this.writer.write(line.getBytes());
			}
			catch (IOException e) {
				this.exception = e;
				return false;
			}
			
			return true;
		}
		
		protected boolean close() {
			if (this.writer == null) {
				Log.w(TAG, "WARNING: FileOutputWriter writer is uninitialised!");
				return false;
			}
			
			try {
				this.writer.close();
			}
			catch (IOException e) {
				this.exception = e;
				return false;
			}
			
			return true;
		}
		
	}
		
}		// end of file



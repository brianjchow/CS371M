package com.example.app;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Query implements Parcelable {

	private static final String TAG = "Query";
	protected static final String PARCELABLE_QUERY = "query";
	
	private Context mContext;

	private Date start_date;				// must have date and time at minimum
	private Date end_date;					// automatically set
	private int duration;					// how long the user needs the room to be available, in minutes; default 60
	private Map<String, Object> options;	// [capacity, power plugs, search building, search room]

	protected Query(Context context) {
		this(context, Calendar.getInstance().getTime());
	}

	protected Query(Context context, final Date start_date) {
		if (context == null) {
			throw new IllegalArgumentException("Error: context argument cannot be null, Query constructor");
		}
		if (start_date == null) {
			throw new IllegalArgumentException("Error: starting date cannot be null, Query constructor");
		}
		
		this.mContext = context;

		this.duration = Constants.DEFAULT_QUERY_DURATION;
		this.set_start_date(start_date);

		this.options = new HashMap<String, Object>(Utilities.get_hashmap_size(4));
		this.options.put(Constants.CAPACITY, Integer.valueOf(0));
		this.options.put(Constants.POWER, Boolean.valueOf(false));
		this.options.put(Constants.SEARCH_BUILDING, Constants.GDC);
		this.options.put(Constants.SEARCH_ROOM, Constants.RANDOM);
	}

	protected int get_duration() {
		return (this.duration);
	}

	protected final Date get_end_date() {
		return (this.end_date);
	}

	protected final Integer get_option_capacity() {
		return ((Integer) this.options.get(Constants.CAPACITY));
	}

	protected final Boolean get_option_power() {
		return ((Boolean) this.options.get(Constants.POWER));
	}

	protected final String get_option_search_building() {
		return ((String) this.options.get(Constants.SEARCH_BUILDING));
	}
	
	protected final String get_option_search_room() {
		String search_room = (String) this.options.get(Constants.SEARCH_ROOM);
		return search_room;
	}

	protected final Date get_start_date() {
		return (this.start_date);
	}

	protected String get_current_course_schedule() {
		Date now = Calendar.getInstance().getTime();

		if (Utilities.date_is_during_spring_trimester(this.start_date)) {
//			if (Utilities.date_is_during_spring(this.start_date)) {
				if (Utilities.date_is_during_spring_trimester(now)) {
//					if (Utilities.date_is_during_spring(now)) {
						return Constants.COURSE_SCHEDULE_THIS_SEMESTER;
//					}
//					else {
//						Log.d(TAG, "pos a");
//						return SearchStatus.HOLIDAY.toString();
//					}
				}
				else if (Utilities.date_is_during_summer(now)) {
					if (Constants.COURSE_SCHEDULE_NEXT_SEMESTER == null) {
						throw new IllegalArgumentException("Fatal error - next semester's course schedule doesn't exist");
					}
					return Constants.COURSE_SCHEDULE_NEXT_SEMESTER;		// should never happen if it's null (see DatePicker code)
				}
				else if (Utilities.date_is_during_fall_trimester(now)) {
					if (Constants.COURSE_SCHEDULE_NEXT_SEMESTER == null) {
						throw new IllegalArgumentException("Fatal error - next semester's course schedule doesn't exist");
					}
					return Constants.COURSE_SCHEDULE_NEXT_SEMESTER;		// should never happen if it's null (see DatePicker code)
				}
				else {
					Log.d(TAG, "pos b");
					return SearchStatus.HOLIDAY.toString();
				}
//			}
//			else {
//				Log.d(TAG, "pos c");
//				return SearchStatus.HOLIDAY.toString();
//			}
		}

		else if (Utilities.date_is_during_summer(this.start_date)) {
			return SearchStatus.SUMMER.toString();
		}

		else if (Utilities.date_is_during_fall_trimester(this.start_date)) {
//			if (Utilities.date_is_during_fall(this.start_date)) {
				if (Utilities.date_is_during_spring_trimester(now)) {
					if (Constants.COURSE_SCHEDULE_NEXT_SEMESTER == null) {
						throw new IllegalArgumentException("Fatal error - next semester's course schedule doesn't exist");
					}
					return Constants.COURSE_SCHEDULE_NEXT_SEMESTER;		// should never happen if it's null (see DatePicker code)
				}
				else if (Utilities.date_is_during_summer(now)) {
					return Constants.COURSE_SCHEDULE_THIS_SEMESTER;
				}
				else if (Utilities.date_is_during_fall_trimester(now)) {
//					if (Utilities.date_is_during_fall(now)) {
						return Constants.COURSE_SCHEDULE_THIS_SEMESTER;
//					}
//					else {
//						Log.d(TAG, "pos d");
//						return SearchStatus.HOLIDAY.toString();
//					}
				}
				else {
					Log.d(TAG, "pos e");
					return SearchStatus.HOLIDAY.toString();
				}
//			}
//			else {
//				Log.d(TAG, "pos f");
//				return SearchStatus.HOLIDAY.toString();
//			}
		}

		else {
			Log.d(TAG, "pos g");
			return SearchStatus.HOLIDAY.toString();
		}
	}
	
	private String get_current_course_schedule(QueryResult query_result) {
		if (query_result == null) {
			throw new IllegalArgumentException();
		}
		
//		boolean holiday_flag = false;
		Date now = Calendar.getInstance().getTime();

		if (Utilities.date_is_during_spring_trimester(this.start_date)) {
			if (Utilities.date_is_during_spring(this.start_date)) {
				if (Utilities.date_is_during_spring_trimester(now)) {
					if (Utilities.date_is_during_spring(now)) {
						return Constants.COURSE_SCHEDULE_THIS_SEMESTER;
					}
					else {
						Log.d(TAG, "pos 1");
						query_result.set_search_status(SearchStatus.HOLIDAY);
						return null;
					}
				}
				else if (Utilities.date_is_during_summer(now)) {
					if (Constants.COURSE_SCHEDULE_NEXT_SEMESTER == null) {
						query_result.set_search_status(SearchStatus.NO_INFO_AVAIL);
						return null;
					}
					return Constants.COURSE_SCHEDULE_NEXT_SEMESTER;		// should never happen if it's null (see DatePicker code)
				}
				else if (Utilities.date_is_during_fall_trimester(now)) {
					if (Constants.COURSE_SCHEDULE_NEXT_SEMESTER == null) {
						query_result.set_search_status(SearchStatus.NO_INFO_AVAIL);
						return null;
					}
					return Constants.COURSE_SCHEDULE_NEXT_SEMESTER;		// should never happen if it's null (see DatePicker code)
				}
				else {
					Log.d(TAG, "pos 2");
					query_result.set_search_status(SearchStatus.HOLIDAY);
					return null;
				}
			}
			else {
				Log.d(TAG, "pos 3");
				query_result.set_search_status(SearchStatus.HOLIDAY);
				return null;
			}
		}

		else if (Utilities.date_is_during_summer(this.start_date)) {
			query_result.set_search_status(SearchStatus.SUMMER);
			return null;
		}

		else if (Utilities.date_is_during_fall_trimester(this.start_date)) {
			if (Utilities.date_is_during_fall(this.start_date)) {
				if (Utilities.date_is_during_spring_trimester(now)) {
					if (Constants.COURSE_SCHEDULE_NEXT_SEMESTER == null) {
						query_result.set_search_status(SearchStatus.NO_INFO_AVAIL);
						return null;
					}
					return Constants.COURSE_SCHEDULE_NEXT_SEMESTER;		// should never happen if it's null (see DatePicker code)
				}
				else if (Utilities.date_is_during_summer(now)) {
					return Constants.COURSE_SCHEDULE_THIS_SEMESTER;
				}
				else if (Utilities.date_is_during_fall_trimester(now)) {
					if (Utilities.date_is_during_fall(now)) {
						return Constants.COURSE_SCHEDULE_THIS_SEMESTER;
					}
					else {
						Log.d(TAG, "pos 4");
						query_result.set_search_status(SearchStatus.HOLIDAY);
						return null;
					}
				}
				else {
					Log.d(TAG, "pos 5");
					query_result.set_search_status(SearchStatus.HOLIDAY);
					return null;
				}
			}
			else {
				Log.d(TAG, "pos 6");
				query_result.set_search_status(SearchStatus.HOLIDAY);
				return null;
			}
		}

		else {
			Log.d(TAG, "pos 7");
			query_result.set_search_status(SearchStatus.HOLIDAY);
			return null;
		}
	}

	private int get_this_day_of_week() {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(this.start_date);
		int today = calendar.get(Calendar.DAY_OF_WEEK);

		return today;
	}
	
	private boolean is_truncated_gdc_room(String room) {
		if (room == null) {
			return false;
//			throw new IllegalArgumentException();
		}

		if (Utilities.str_is_gdc(this.get_option_search_building()) && (room.equals("2.21") || room.equals("2.41"))) {
			return true;
		}
		return false;
	}
	
	private boolean needs_truncation_gdc_room(String room) {
		if (room == null) {
			return false;
//			throw new IllegalArgumentException();
		}
		
		if (Utilities.str_is_gdc(this.get_option_search_building()) && (room.equals("2.210") || room.equals("2.410"))) {
			return true;
		}		
		return false;
		
//		return !is_truncated_gdc_room(room);
	}

	protected void reset() {
		Date start_date = Calendar.getInstance().getTime();
		set_start_date(start_date);
		this.duration = Constants.DEFAULT_QUERY_DURATION;

		set_end_date();

		this.set_standard_options();
	}
	
	protected boolean search_is_at_night() {
		int this_start_time = Utilities.get_time_from_date(this.start_date);
		int this_end_time = Utilities.get_time_from_date(this.end_date);

		if (this_start_time >= Constants.LAST_TIME_OF_DAY && this_start_time <= 2359) {
			return true;
		}
//		else if (this_start_time >= 0 && this_start_time < Constants.LAST_TIME_OF_NIGHT - 1 && this_end_time < Constants.LAST_TIME_OF_NIGHT) {
//			return true;
//		}
		else if (this_start_time >= 0 && this_start_time < Constants.LAST_TIME_OF_NIGHT) {
			return true;
		}

		Date this_start, this_end;

		this_start = Utilities.get_date(1, 1, 2014, this_start_time);
		if (this_end_time > this_start_time) {
			this_end = Utilities.get_date(1, 1, 2014, this_end_time);
		}
		else {
			this_end = Utilities.get_date(1, 2, 2014, this_end_time);
		}
		// note: the times will never be the same; query duration must be >= 1 minute

		return Utilities.times_overlap(this_start, this_end, Constants.NIGHTFALL, Constants.DAYBREAK);
	}

	protected boolean search_is_on_weekend() {
		int today = get_this_day_of_week();
		
		if (today == Constants.SATURDAY || today == Constants.SUNDAY) {
			return true;
		}
		
		return false;
	}

	protected boolean set_context(Context context) {
		if (context == null) {
//			return false;
			throw new IllegalArgumentException("Argument cannot be null, Query.set_context()");
		}
		
		this.mContext = context;
		return true;
	}

	protected boolean set_duration(int duration) {
		if (duration <= 0) {
			return false;
		}

		this.duration = duration;
		set_end_date();

		return true;
	}

	// automatically set new end date according to
	// start_date and specified duration
	private boolean set_end_date() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(start_date);
		calendar.add(Calendar.MINUTE, this.duration);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		this.end_date = calendar.getTime();
		return true;
	}

	protected boolean set_option(final String option, final Object value) {
		if (option == null || value == null) {
//			return false;
			throw new IllegalArgumentException("Error: one or more arguments is null, set_option()");
		}

		if (option.equals(Constants.CAPACITY)) {
			if (!(value instanceof Integer)) {
				return false;
			}
			return (set_option_capacity((Integer) value));
		}
		else if (option.equals(Constants.POWER)) {
			if (!(value instanceof Boolean)) {
				return false;
			}
			return (set_option_power((Boolean) value));
		}
		else if (option.equals(Constants.SEARCH_BUILDING)) {
			if (!(value instanceof String)) {
				return false;
			}
			return (set_option_search_building((String) value));
		}
		else if (option.equals(Constants.SEARCH_ROOM)) {
			if (!(value instanceof String)) {
				return false;
			}
			return (set_option_search_room((String) value));
		}
		
		return false;
	}

	protected boolean set_option_capacity(final Integer capacity) {
		if (capacity == null) {
			//				return false;
			throw new IllegalArgumentException("Error: capacity cannot be null, set_option_capacity()");
		}
		if (capacity < 0) {
			return false;
		}

		this.options.put(Constants.CAPACITY, capacity);
		return true;
	}

	protected boolean set_option_power(final Boolean power) {
		if (power == null) {
			//				return false;
			throw new IllegalArgumentException("Error: power cannot be null, set_option_power()");
		}
		this.options.put(Constants.POWER, power);
		return true;
	}

	protected boolean set_option_search_building(final String building_code) {
		if (building_code == null) {
//			return false;
			throw new IllegalArgumentException("Error: argument cannot be null, set_option_search_for_building()");
		}
		else if (building_code.length() != 3) {
//			return false;
			Log.e(TAG, "FATAL ERROR: invalid building code (" + building_code + ")");
			throw new IllegalArgumentException("Error: invalid building code (" + building_code + ")");
		}
		
		/* There is currently no info for the power plug statii of non-GDC buildings */
		if (!building_code.equalsIgnoreCase(Constants.GDC)) {
			this.set_option_power(false);
		}
		this.options.put(Constants.SEARCH_BUILDING, building_code.toUpperCase(Constants.DEFAULT_LOCALE));
		return true;		
	}
	
	protected boolean set_option_search_room(final String room_num) {
		if (room_num == null) {
//			return false;
			throw new IllegalArgumentException("Error: argument cannot be null, set_option_search_for_building()");		
		}
		
		if (this.needs_truncation_gdc_room(room_num)) {
			this.options.put(Constants.SEARCH_ROOM, room_num.substring(0, 4));		// [0, 4) is guaranteed; see needs_truncation_gdc_room()
		}
		else {
			this.options.put(Constants.SEARCH_ROOM, room_num);	
		}
		
		return true;
	}

	protected void set_standard_options() {
		this.set_option_capacity(0);
		this.set_option_power(false);
		this.set_option_search_building(Constants.GDC);
		this.set_option_search_room(Constants.RANDOM);
	}

	protected boolean set_start_date(final Date start_date) {
		if (start_date == null) {
//			return false;
			throw new IllegalArgumentException("Error: starting date of event cannot be null, set_start_date()");
		}

		Calendar date = Calendar.getInstance();
		date.setTime(start_date);
		date.set(Calendar.SECOND, 0);
		date.set(Calendar.MILLISECOND, 0);
		
		this.start_date = date.getTime();
		set_end_date();

		return true;
	}

	protected boolean set_start_date(int month, int day, int year) {
		if (month < 1 || month > 12) {
			return false;
		}
		else if (year < Constants.MIN_YEAR || year > Constants.MAX_YEAR) {
			return false;
		}

		int days_in_this_month = Constants.DAYS_IN_MONTH[month - 1];
		if (month == 2 && Utilities.is_leap_year(year)) {
			days_in_this_month++;
		}
		if (day < 1 || day > days_in_this_month) {
			return false;
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(this.start_date);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		this.start_date = calendar.getTime();
		set_end_date();

		return true;
	}

	protected boolean set_start_time(int hour, int minute) {
		if (hour < 0 || hour > 23 || minute < 0 || minute > 59) {
			return false;
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(this.start_date);
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		this.start_date = calendar.getTime();
		set_end_date();

		return true;
	}

	protected Query clone() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(this.start_date);

		Query copy = new Query(mContext, calendar.getTime());
		copy.set_duration(this.duration);	// updates end_time
		for (Map.Entry<String, Object> entry : this.options.entrySet()) {
			copy.options.put(entry.getKey(), entry.getValue());
		}

		return copy;
	}

	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if (!(other instanceof Query)) {
			return false;
		}

		Query other_query = (Query) other;
		if (Utilities.dates_are_equal(this.start_date, other_query.start_date) &&
			Utilities.dates_are_equal(this.end_date, other_query.end_date) &&
			this.duration == other_query.duration &&
			this.options.equals(other_query.options)) {

			return true;
		}
		return false;
	}

	public int hashCode() {
		return (37 * Integer.parseInt(Utilities.get_time(this.start_date).replaceAll(":", "")) * 
				Integer.parseInt(Utilities.get_time(this.end_date).replaceAll(":", "")) * 17);
	}

	public String toString() {
		StringBuilder out = new StringBuilder(100);

		String minutes = "minutes";
		if (this.duration == 1) {
			minutes = "minute";
		}

		DateFormat format = new SimpleDateFormat("MMM dd yyyy HH:mm", Locale.ENGLISH);
		out.append("Start date:\t" + format.format(this.start_date) + "\n");
		out.append("End date:\t" + format.format(this.end_date) + "\n");
		out.append("Duration:\t" + this.duration + " " + minutes + "\n");
		out.append("Options:\t" + options.toString());

		return (out.toString());
	}

/* ############################### BEGIN QUERY PARCELABLE IMPLEMENTATION ############################### */
	
	@SuppressWarnings("unchecked")
	public Query(Parcel parcel) {
		Object[] fields = new Object[4];
		fields = parcel.readArray(Query.class.getClassLoader());
		
		this.start_date = (Date) fields[0];
		this.end_date = (Date) fields[1];
		this.duration = (Integer) fields[2];
		this.options = (Map<String, Object>) fields[3];
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeArray(new Object[] { this.start_date, this.end_date, Integer.valueOf(this.duration), this.options });
	}
	
	public static final Parcelable.Creator<Query> CREATOR = new Parcelable.Creator<Query>() {
		
		@Override
		public Query createFromParcel(Parcel in) {
			return new Query(in);
		}
		
		@Override
		public Query[] newArray(int size) {
			return new Query[size];
		}
	};
	
/* ############################### END QUERY PARCELABLE IMPLEMENTATION ############################### */	
	
/* TODO ############################### SEARCH - GET RANDOM ROOM ALGORITHM AND HELPERS ############################### */


	protected QueryResult search() {
		return (search(Constants.CSV_FEEDS_CLEANED));
	}

	private QueryResult search(final EventList eolist) {
		
		String search_building_str = this.get_option_search_building();
		boolean searching_gdc = Utilities.str_is_gdc(search_building_str);
		
		QueryResult query_result = new QueryResult(SearchType.GET_RANDOM_ROOM, search_building_str);
		Boolean search_is_during_long_semester = true;
		
		String course_schedule = this.get_current_course_schedule(query_result);
		if (course_schedule == null) {
			course_schedule = Constants.COURSE_SCHEDULE_THIS_SEMESTER;
			search_is_during_long_semester = Boolean.valueOf(false);
		}
		else if (!Utilities.is_valid_db_filename(course_schedule)) {
			throw new IllegalArgumentException("FATAL ERROR: invalid course schedule (" + course_schedule + ")");
		}
		
//		Building search_building = check_search_preconditions_and_get_building(query_result, search_is_during_long_semester);
		Building search_building = Building.get_instance(this.mContext, search_building_str, course_schedule);
		if (search_building == null) {
			query_result.set_search_status(SearchStatus.NO_INFO_AVAIL);
			query_result.set_results(new ArrayList<String>());
			return query_result;
		}

		/* Actual searching begins here. */
		
		SortedSet<String> all_rooms = search_building.get_keyset();
		SortedSet<String> rooms_to_remove = new TreeSet<String>();

		List<String> all_valid_rooms = new ArrayList<String>(all_rooms.size());
		
		// filter out by CSV feeds
		if (searching_gdc && eolist != null) {
			rooms_to_remove = add_invalid_rooms_by_gdc_csv_feeds(eolist, rooms_to_remove);
		}
		
		// check room characteristics (options)
		rooms_to_remove = add_invalid_rooms_by_options(search_building, all_rooms, rooms_to_remove);
		
		// if search occurs during one of the two long semesters, check course schedule
		if (search_is_during_long_semester) {
			rooms_to_remove = add_invalid_rooms_by_course_schedule(search_building, all_rooms, rooms_to_remove);
		}

		/* Search complete. */
		
		// "remove" invalid rooms from valid_rooms
		for (Iterator<String> itr = all_rooms.iterator(); itr.hasNext(); ) {
			String check_remove_room = itr.next();
			if (!rooms_to_remove.contains(check_remove_room)) {
				if (searching_gdc && is_truncated_gdc_room(check_remove_room)) {
					check_remove_room += "0";
				}
				all_valid_rooms.add(check_remove_room);
			}
		}
		
		if (search_is_during_long_semester) {
			if (all_valid_rooms.size() <= 0) {
				query_result.set_search_status(SearchStatus.NO_ROOMS_AVAIL);
			}
			else {
				query_result.set_search_status(SearchStatus.SEARCH_SUCCESS);
			}
		}
		
		query_result.set_results(all_valid_rooms);
		return query_result;
	}

	private boolean is_valid_gdc_room(Room room) {
		if (room == null) {
			return false;
		}
		
		Location location = room.get_location();
		if (!Utilities.str_is_gdc(location.get_building())) {
			return false;
		}
		
		if (location.get_room().equalsIgnoreCase("2.506")) {
			return false;
		}
		
		if (room.get_capacity() < this.get_option_capacity() || (this.get_option_power() && !room.get_has_power())) {
			return false;
		}
		
		return true;
	}
	
	private SortedSet<String> add_invalid_rooms_by_gdc_csv_feeds(final EventList eolist, SortedSet<String> rooms_to_remove) {
		Event curr_event;
		String curr_room_str;

		Iterator<Event> itr = eolist.get_iterator();
		while (itr.hasNext()) {
			curr_event = itr.next();
			curr_room_str = curr_event.get_location().get_room();
			
			if (Utilities.occur_on_same_day(curr_event.get_start_date(), this.start_date) && 
				Utilities.times_overlap(curr_event.get_start_date(), curr_event.get_end_date(), this.start_date, this.end_date)) {
				
				rooms_to_remove.add(curr_room_str);
			}
		}
		
		return rooms_to_remove;
	}
	
	private SortedSet<String> add_invalid_rooms_by_options(Building search_building, SortedSet<String> valid_rooms, SortedSet<String> rooms_to_remove) {		
		Room curr_room;
		String curr_room_str;

		for (Iterator<String> itr = valid_rooms.iterator(); itr.hasNext(); ) {
			curr_room_str = itr.next();
			curr_room = search_building.get_room(curr_room_str);
			
			if (rooms_to_remove.contains(curr_room_str)) {
				continue;
			}
			
			if (Utilities.str_is_gdc(this.get_option_search_building()) && !is_valid_gdc_room(curr_room)) {
				rooms_to_remove.add(curr_room_str);
			}
			else {
				if (curr_room.get_capacity() < this.get_option_capacity()) {
					rooms_to_remove.add(curr_room_str);
				}
			}
		}
		
		return rooms_to_remove;
	}
	
	private SortedSet<String> add_invalid_rooms_by_course_schedule(Building search_building, SortedSet<String> valid_rooms, SortedSet<String> rooms_to_remove) {
		final Calendar cal1 = Calendar.getInstance();
		final Calendar cal2 = Calendar.getInstance();

		int today = this.get_this_day_of_week();
		Room curr_room;

		for (String curr_room_str : valid_rooms) {
			if (rooms_to_remove.contains(curr_room_str)) {
				continue;
			}
						
			curr_room = search_building.get_room(curr_room_str);
			if (curr_room == null) {
				continue;
			}

			Set<Event> courses = curr_room.get_events(today);

			Date curr_start_date, curr_end_date;
			for (Event curr_event : courses) {
				curr_start_date = curr_event.get_start_date();
				curr_end_date = curr_event.get_end_date();	

				cal1.setTime(curr_start_date);
				cal2.setTime(this.start_date);

				cal1.set(Calendar.DAY_OF_YEAR, cal2.get(Calendar.DAY_OF_YEAR));
				cal1.set(Calendar.YEAR, cal2.get(Calendar.YEAR));
				curr_start_date = cal1.getTime();

				cal1.setTime(curr_end_date);
				cal2.setTime(this.end_date);

				cal1.set(Calendar.DAY_OF_YEAR, cal2.get(Calendar.DAY_OF_YEAR));
				cal1.set(Calendar.YEAR, cal2.get(Calendar.YEAR));
				curr_end_date = cal1.getTime();

				if (Utilities.times_overlap(curr_start_date, curr_end_date, this.start_date, this.end_date)) {
					rooms_to_remove.add(curr_room_str);
					break;
				}
			}
		}

		return rooms_to_remove;
	}
	
//	private Building check_search_preconditions_and_get_building(QueryResult qr, Boolean search_during_long_sem) {
//		
//		String course_schedule = this.get_current_course_schedule(qr);
//		if (course_schedule == null) {
//			course_schedule = Constants.COURSE_SCHEDULE_THIS_SEMESTER;
//			search_during_long_sem = Boolean.valueOf(false);
//			
//			Log.d(TAG, "Search precondition check, search during long semester is now false");
//		}
////		else if (!Utilities.is_valid_db_filename(course_schedule) && qr.get_search_type() == SearchType.GET_RANDOM_ROOM) {
////			query_result.set_search_status(SearchStatus.SEARCH_ERROR);
////			query_result.set_results(new ArrayList<String>());
////			return query_result;
////		}
//		else if (!Utilities.is_valid_db_filename(course_schedule)) {
//			throw new IllegalArgumentException("FATAL ERROR: invalid course schedule (" + course_schedule + ")");
//		}
//		
//		Building search_building = Building.get_instance(this.mContext, this.get_option_search_building(), course_schedule);
//		return search_building;
//	}
	

/* TODO ############################### SEARCH - GET ROOM SCHEDULE ALGORITHM AND HELPERS ############################### */
	
	protected QueryResult search_get_room_schedule() {
		return (search_get_room_schedule(Constants.CSV_FEEDS_CLEANED));
	}

	protected QueryResult search_get_room_schedule(final EventList eolist) {
		
		String search_building_str = this.get_option_search_building();
		boolean searching_gdc = Utilities.str_is_gdc(search_building_str);
		
		QueryResult query_result = new QueryResult(SearchType.GET_ROOM_SCHEDULE, search_building_str);
		Boolean search_is_during_long_semester = Boolean.valueOf(true);
		
		String course_schedule = this.get_current_course_schedule(query_result);
		if (course_schedule == null) {
			course_schedule = Constants.COURSE_SCHEDULE_THIS_SEMESTER;
			search_is_during_long_semester = Boolean.valueOf(false);
		}
		else if (!Utilities.is_valid_db_filename(course_schedule)) {
			throw new IllegalArgumentException("FATAL ERROR: invalid course schedule (" + course_schedule + ")");
		}
		
//		Building search_building = check_search_preconditions_and_get_building(query_result, search_is_during_long_semester);
		Building search_building = Building.get_instance(this.mContext, search_building_str, course_schedule);
		if (search_building == null) {
			query_result.set_search_status(SearchStatus.NO_INFO_AVAIL);
			query_result.set_results(new ArrayList<String>());
			return query_result;
		}
		
		Room search_room = get_search_room(search_building);
		if (search_room == null) {
			query_result.set_search_status(SearchStatus.NO_INFO_AVAIL);
			query_result.set_results(new ArrayList<String>());
			return query_result;
		}
		
		Log.d(TAG, "Get course sked: search during long semester is " + search_is_during_long_semester);

		/* Actual searching begins here. */
		
		Set<Event> all_events = get_course_schedule_for_room(search_room, search_is_during_long_semester);
		
		if (searching_gdc && eolist != null) {
			all_events = add_gdc_csv_feed_events(eolist, all_events, search_room, search_is_during_long_semester);
		}
		
		List<String> schedule = eventset_to_string(all_events);
		
		/* Search complete here. */

		if (search_is_during_long_semester) {
			if (schedule.size() <= 0) {
				query_result.set_search_status(SearchStatus.ROOM_FREE_ALL_DAY);
			}
			else {
				query_result.set_search_status(SearchStatus.SEARCH_SUCCESS);
			}
		}
		
		query_result.set_results(schedule);		
		return query_result;
	}

	private Room get_search_room(Building search_building) {
		String search_room_str = this.get_option_search_room();
		if (!search_room_str.equals(Constants.RANDOM)) {
			return (search_building.get_room(search_room_str));
		}
		return (search_building.get_random_room());
	}
	
	private Set<Event> get_course_schedule_for_room(Room search_room, boolean search_is_during_long_semester) {
		Set<Event> all_events;
		if (search_is_during_long_semester) {
			int today = this.get_this_day_of_week();
			all_events = search_room.get_events(today);
			
			Calendar cal1 = Calendar.getInstance();
			cal1.setTime(this.start_date);
			int day_of_year = cal1.get(Calendar.DAY_OF_YEAR);
			int year = cal1.get(Calendar.YEAR);
			
			for (Event event : all_events) {
				cal1.setTime(event.get_start_date());
				cal1.set(Calendar.DAY_OF_YEAR, day_of_year);
				cal1.set(Calendar.YEAR, year);
				event.set_start_date(cal1.getTime());
			}
		}
		else {
			all_events = new TreeSet<Event>();
		}
		
		return all_events;
	}
	
	private Set<Event> add_gdc_csv_feed_events(final EventList eolist, 
			final Set<Event> all_events, final Room search_room, boolean search_is_during_long_semester) {
		
		boolean is_valid = true;
		Iterator<Event> itr = eolist.get_iterator();
		
		Event event;
		while (itr.hasNext()) {
			event = itr.next();
			
			if (
//				event.get_location().get_room().equals(search_room.get_location().get_room()) &&
				event.get_location().equals(search_room.get_location()) &&
				Utilities.occur_on_same_day(event.get_start_date(), this.start_date)) {
				
				if (search_is_during_long_semester) {
					/*
					 * This loop IS necessary. The CSV feeds also list courses occurring, with their
					 * names prefixed by the word "Registrar"; however, it also files extracurricular
					 * events (such as tutoring sessions) with the "Registrar" prefix, meaning that for
					 * maximum accuracy we can't just simply ignore all Events from the CSV feeds
					 * that are prefixed with "Registrar."
					 */
					for (Event course_event : all_events) {
						if (Utilities.containsIgnoreCase(course_event.get_event_name(), event.get_event_name())) {
							is_valid = false;
							break;
						}
					}
				}
				
				if (is_valid) {
					all_events.add(event);
				}
				
				is_valid = true;					
			}
		}
		
		return all_events;
	}
	
	private List<String> eventset_to_string(final Set<Event> eventset) {
		List<String> schedule = new ArrayList<String>(eventset.size());
		
		String event_name, start_time, end_time;
		StringBuilder event_str = new StringBuilder(50);
		
		Event prev_event = null;
		for (Event event : eventset) {
			event_name = event.get_event_name();
			start_time = Utilities.get_time(event.get_start_date());	// Utilities.time_to_24h(Utilities.get_time(event.get_start_date()));
			end_time = Utilities.get_time(event.get_end_date());		// Utilities.time_to_24h(Utilities.get_time(event.get_end_date()));
			
			if (prev_event == null) {
				prev_event = event;
			}
			else {
				
				/* Allow identical courses listed under different departments */
				if (event_name.equalsIgnoreCase(prev_event.get_event_name()) && start_time.equals(Utilities.get_time(prev_event.get_start_date()))) {
					continue;
				}
			}
			
			event_str.append(event_name + "\n");
			event_str.append("Start: " + start_time + "\n");
			event_str.append("End: " + end_time + "\n");
			event_str.append("\n");
			
			schedule.add(event_str.toString());
			event_str.setLength(0);
		}
		
		return schedule;
	}
	
/* ############################### END SEARCH ALGORITHMS ############################### */	
	
	protected static class QueryResult implements Parcelable {
		
		protected static final String PARCELABLE_QUERY_RESULT = "query_result";
		
		/*
		 * search_type isn't the enum - http://stackoverflow.com/questions/2836256/passing-enum-or-object-through-an-intent-the-best-solution
		 */
		
		private int search_type;
		private String building_name;
		private List<String> results;
		private String message_status;
		
		private QueryResult(SearchType search_type, final String building_name) {
			if (building_name == null || building_name.length() != Constants.BUILDING_CODE_LENGTH) {
				throw new IllegalArgumentException();
			}
			
			this.search_type = search_type.get_enum_val();
			this.building_name = building_name.toUpperCase(Constants.DEFAULT_LOCALE);
			this.results = new ArrayList<String>();
			this.message_status = SearchStatus.SEARCH_ERROR.toString();
		}

		protected String get_building_name() {
			return this.building_name;
		}
		
		protected int get_num_results() {
			return this.results.size();
		}
		
		protected String get_random_room() {
			if (this.results.size() <= 0 || !this.message_status.equals(SearchStatus.SEARCH_SUCCESS.toString())) {
				return this.message_status;
			}
			
			int random_index = new Random().nextInt(this.results.size());
			String random_room = this.results.get(random_index);
			
			return (building_name + " " + random_room);
		}
		
		protected List<String> get_results() {
			return this.results;
		}
		
		protected String get_search_status() {
			return this.message_status;
		}
		
		protected int get_search_type() {
			return this.search_type;
		}

		private boolean set_results(List<String> results) {
			if (results == null) {
//				return false;
				throw new IllegalArgumentException();
			}
			
			this.results = results;
			return true;
		}
		
		private boolean set_search_status(SearchStatus message_status) {
			if (message_status == null) {
//				return false;
				throw new IllegalArgumentException();
			}
			
			this.message_status = message_status.toString();
			return true;
		}

/* ############################### BEGIN QUERYRESULT PARCELABLE IMPLEMENTATION ############################### */
		
		@SuppressWarnings("unchecked")
		public QueryResult(Parcel parcel) {
			Object[] fields = new Object[4];
			fields = parcel.readArray(QueryResult.class.getClassLoader());
			
			this.search_type = (Integer) fields[0];
			this.building_name = (String) fields[1];
			this.results = (List<String>) fields[2];
			this.message_status = (String) fields[3];
		}
		
		@Override
		public int describeContents() {
			return 0;
		}
		
		@Override
		public void writeToParcel(Parcel out, int flags) {
			out.writeArray(new Object[] { this.search_type, this.building_name, this.results, this.message_status });
		}
		
		public static final Parcelable.Creator<QueryResult> CREATOR = new Parcelable.Creator<QueryResult>() {
			
			public QueryResult createFromParcel(Parcel in) {
				return new QueryResult(in);
			}
			
			public QueryResult[] newArray(int size) {
				return new QueryResult[size];
			}
		};
		
	}
	
/* ############################### END QUERYRESULT PARCELABLE IMPLEMENTATION ############################### */
	
	public enum SearchStatus {
		
		ALL_ROOMS_AVAIL ("All rooms available"),
		NO_ROOMS_AVAIL	("No rooms available; please try again"),
		GO_HOME			("Go home and sleep, you procrastinator"),
		SUMMER			("Some or all rooms available (summer hours); check course schedules"),
		HOLIDAY			("Some or all rooms available (finals schedule or campus closed for holidays)"),
		NO_INFO_AVAIL	("Not enough info available for search; please try again"),
		SEARCH_ERROR	("Unknown search error; please try again."),
		
		ROOM_FREE_ALL_DAY	("This room has no scheduled events for today."),
		
		SEARCH_SUCCESS	("Search successful.")
		;
		
		private final String msg;
		
		private SearchStatus(String msg) {
			this.msg = msg;
		}
	
		@Override
		public String toString() {
			return this.msg;
		}
	}
	
	public enum SearchType {
		
		GET_RANDOM_ROOM		(0),
		GET_ROOM_SCHEDULE	(1)
		;
		
		private final int type;
		
		private SearchType(int type) {
			this.type = type;
		}
		
		// NOT supposed to be an override
		protected boolean equals(int other) {
			return (this.type == other);
		}
		
		protected int get_enum_val() {
			return this.type;
		}
	}

}		// end of file

/*

	protected String get_current_course_schedule() {
		Date now = Calendar.getInstance().getTime();

		if (Utilities.date_is_during_spring(this.start_date)) {
			if (Utilities.date_is_during_spring(now)) {
				return Constants.COURSE_SCHEDULE_THIS_SEMESTER;
			}
			else if (Utilities.date_is_during_summer(now)) {
				if (Constants.COURSE_SCHEDULE_NEXT_SEMESTER == null) {
					throw new IllegalArgumentException("Fatal error - next semester's course schedule doesn't exist");
				}
				return Constants.COURSE_SCHEDULE_NEXT_SEMESTER;		// should never happen if it's null (see DatePicker code)
			}
			else if (Utilities.date_is_during_fall(now)) {
				if (Constants.COURSE_SCHEDULE_NEXT_SEMESTER == null) {
					throw new IllegalArgumentException("Fatal error - next semester's course schedule doesn't exist");
				}
				return Constants.COURSE_SCHEDULE_NEXT_SEMESTER;		// should never happen if it's null (see DatePicker code)
			}
			else {
				return SearchStatus.HOLIDAY.toString();
			}
		}

		else if (Utilities.date_is_during_summer(this.start_date)) {
			return SearchStatus.SUMMER.toString();
		}

		else if (Utilities.date_is_during_fall(this.start_date)) {
			if (Utilities.date_is_during_spring(now)) {
				if (Constants.COURSE_SCHEDULE_NEXT_SEMESTER == null) {
					throw new IllegalArgumentException("Fatal error - next semester's course schedule doesn't exist");
				}
				return Constants.COURSE_SCHEDULE_NEXT_SEMESTER;		// should never happen if it's null (see DatePicker code)
			}
			else if (Utilities.date_is_during_summer(now)) {
				return Constants.COURSE_SCHEDULE_THIS_SEMESTER;
			}
			else if (Utilities.date_is_during_fall(now)) {
				return Constants.COURSE_SCHEDULE_THIS_SEMESTER;
			}
			else {
				return SearchStatus.HOLIDAY.toString();
			}
		}

		else {
			return SearchStatus.HOLIDAY.toString();
		}
	}
	
 */


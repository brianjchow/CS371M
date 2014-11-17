import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;

public class Query {
	
	private Date start_date;				// must have date and time at minimum
	private Date end_date;					// automatically set
	private int duration;					// how long the user needs the room to be available, in minutes; default 60
	private Map<String, Object> options;	// [capacity, power plugs, search building] for now
	
	private static int MESSAGE_STATUS_FLAG = -1;
	
	/**
	 * Default constructor. Uses the current system time.
	 */
	protected Query() {
		this(Calendar.getInstance().getTime());
	}

	/**
	 * @param start_date
	 */
	protected Query(Date start_date) {
		if (start_date == null) {
			throw new IllegalArgumentException("Error: starting date cannot be null, Query constructor");
		}
		
		this.start_date = start_date;
		this.duration = Constants.DEFAULT_QUERY_DURATION;

		set_end_date();
		
		this.options = new HashMap<String, Object>(5);
		this.options.put(Constants.CAPACITY, Integer.valueOf(0));
		this.options.put(Constants.POWER, Boolean.valueOf(false));
		this.options.put(Constants.SEARCH_BUILDING, Constants.GDC);
		
	}

	/**
	 * @return The master CSV feeds, with all events included.
	 */
	protected final EventList get_all_room_schedules() {
		return Constants.CSV_FEEDS_MASTER;
	}
	
	/**
	 * @return The master CSV feeds, minus any events with no
	 * 		   location specified and/or located in rooms normally
	 * 		   inaccessible to undergrads (e.g., the faculty lounge).
	 */
	protected final EventList get_all_valid_room_schedules() {
		return Constants.CSV_FEEDS_CLEANED;
	}
	
	/**
	 * @param location
	 * @return All events occurring at location based on this Query's
	 * 		   dates and times.
	 */
	protected EventList get_room_schedule(Location location) {
		EventList schedule = new EventList();
		
		if (location == null) {
			throw new IllegalArgumentException("Error: location cannot be null, get_room_schedule()");
		}

		Iterator<Event> itr = Constants.CSV_FEEDS_CLEANED.get_iterator();
		Event event;
		while (itr.hasNext()) {
			event = itr.next();
			if (event.get_location().equals(location)) {
				schedule.add(event);
			}
		}

		schedule.sort_by_start_date(true);
		return schedule;
	}

	/**
	 * @return How long the user of the app needs
	 * 		   a room to be available/unoccupied for.
	 */
	protected int get_duration() {
		return (this.duration);
	}
		
	/**
	 * @return The end date of this Query.
	 */
	protected final Date get_end_date() {
		return (this.end_date);
	}

	/**
	 * @return An EventList of all events occurring on
	 * 		   the start date specified by this Query, culled
	 * 		   from the master CSV feeds.
	 */
	protected EventList get_events_by_date() {
		return (get_events_by_date(Constants.CSV_FEEDS_MASTER));
	}

	/**
	 * @param eolist
	 * @return An EventList of all events occurring on
	 * 		   the start date specified by this Query, culled
	 * 		   from eolist.
	 */
	protected EventList get_events_by_date(EventList eolist) {
		if (eolist == null) {
			throw new IllegalArgumentException("Error: eolist cannot be null, get_events_by_date()");
		}
		
		EventList reduced = new EventList();
		
		if (eolist.get_size() == 0) {
			return reduced;
		}

		Iterator<Event> itr = eolist.get_iterator();
		Event event;
		while (itr.hasNext()) {
			event = itr.next();
			
			// skip dummy locations
			if (!event.get_location().equals(Constants.GDC_ATRIUM) &&
				!event.get_location().equals(Constants.GDC_GATESHENGE) &&
				Utilities.occur_on_same_day(event.get_start_date(), this.start_date)) {
					
					reduced.add(event);
				}
		}

		return reduced;
	}
	
	/**
	 * @return An EventList of all events occurring during
	 * 		   the start and end times specified by this Query, culled
	 * 		   from the master CSV feeds.
	 */
	protected EventList get_events_by_time() {
		return (get_events_by_time(Constants.CSV_FEEDS_MASTER));
	}

	/**
	 * @param eolist
	 * @return An EventList of all events occurring during
	 * 		   the start and end times specified by this Query, culled
	 * 		   from eolist.
	 */
	protected EventList get_events_by_time(EventList eolist) {
		if (eolist == null) {
			throw new IllegalArgumentException("Error: eolist cannot be null, get_events_by_time()");
		}
		
		EventList reduced = new EventList();
		
		if (eolist.get_size() == 0) {
			return reduced;
		}

		Iterator<Event> itr = eolist.get_iterator();
		Event event;
		while (itr.hasNext()) {
			event = itr.next();
			if (Utilities.times_overlap(event.get_start_date(), event.get_end_date(), this.start_date, this.end_date)) {
				reduced.add(event);
			}
		}

		return reduced;
	}
	
	/**
	 * @return How many seats the user requires
	 * 		   in any room chosen by the app.
	 */
	protected final Integer get_option_capacity() {
		return ((Integer) this.options.get(Constants.CAPACITY));
	}
	
	/**
	 * @return True if the user specified power plugs
	 * 		   should be available for most if not all
	 * 		   occupants of any room chosen by the app.
	 * 		   False otherwise.
	 */
	protected final Boolean get_option_power() {
		return ((Boolean) this.options.get(Constants.POWER));
	}
	
	protected final String get_option_search_building() {
		return ((String) this.options.get(Constants.SEARCH_BUILDING));
	}
	
	/**
	 * @return A List of Strings representing the keyset
	 * 		   of this Query's options map.
	 */
	protected final List<String> get_options() {
		return (new ArrayList<String>(options.keySet()));
	}
	
	/**
	 * @return A Map of String-to-Object pairs representing
	 * 		   the user-selectable options and their current values.
	 */
	protected final Map<String, Object> get_options_map() {
		return this.options;
	}
			
	/**
	 * @return The starting date of this Query.
	 */
	protected Date get_start_date() {
		return (this.start_date);
	}

	private boolean search_is_at_night() {
		int this_start_time = Utilities.get_time_from_date(this.start_date);
		int this_end_time = Utilities.get_time_from_date(this.end_date);
		
		if (this_start_time >= Constants.LAST_TIME_OF_DAY && this_start_time < 2359) {
			return true;
		}
		else if (this_start_time >= 0 && this_start_time < Constants.LAST_TIME_OF_NIGHT - 1 && this_end_time < Constants.LAST_TIME_OF_NIGHT) {
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

	private String get_message_from_flag() {
		if (MESSAGE_STATUS_FLAG >= 0 && MESSAGE_STATUS_FLAG < Constants.MESSAGE_STATUS_FLAGS.length) {
			return Constants.MESSAGE_STATUS_FLAGS[MESSAGE_STATUS_FLAG];
		}
		return Constants.MESSAGE_STATUS_FLAGS[Constants.SEARCH_ERROR];
	}

	private void reset_message_status_flag() {
		MESSAGE_STATUS_FLAG = -1;
	}

	private String get_current_course_schedule() {
		Date now = Calendar.getInstance().getTime();
		
		if (Utilities.date_is_during_spring(this.start_date)) {
			if (Utilities.date_is_during_spring(now)) {
				return Constants.COURSE_SCHEDULE_THIS_SEMESTER;
			}
			else if (Utilities.date_is_during_summer(now)) {
				if (Constants.COURSE_SCHEDULE_NEXT_SEMESTER == null) {
					MESSAGE_STATUS_FLAG = Constants.NO_INFO;
				}
				return Constants.COURSE_SCHEDULE_NEXT_SEMESTER;		// should never happen if it's null (see DatePicker code)
			}
			else if (Utilities.date_is_during_fall(now)) {
				if (Constants.COURSE_SCHEDULE_NEXT_SEMESTER == null) {
					MESSAGE_STATUS_FLAG = Constants.NO_INFO;
				}
				return Constants.COURSE_SCHEDULE_NEXT_SEMESTER;		// should never happen if it's null (see DatePicker code)
			}
			else {
				MESSAGE_STATUS_FLAG = Constants.HOLIDAY;
				return null;
			}
		}
		
		else if (Utilities.date_is_during_summer(this.start_date)) {
			MESSAGE_STATUS_FLAG = Constants.SUMMER;
			return null;
		}
		
		else if (Utilities.date_is_during_fall(this.start_date)) {
			if (Utilities.date_is_during_spring(now)) {
				if (Constants.COURSE_SCHEDULE_NEXT_SEMESTER == null) {
					MESSAGE_STATUS_FLAG = Constants.NO_INFO;
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
				MESSAGE_STATUS_FLAG = Constants.HOLIDAY;
				return null;
			}
		}
		
		else {
			MESSAGE_STATUS_FLAG = Constants.HOLIDAY;
			return null;
		}
		
	}
	
	private int get_this_day_of_week() {
		final Calendar calendar = Calendar.getInstance();
		calendar.setTime(this.start_date);
		int today = calendar.get(Calendar.DAY_OF_WEEK);
		
		return today;
	}

	/**
	 * @param duration
	 * @return True if an update to this.duration was successfully
	 * 		   applied to this Query, false otherwise.
	 */
	protected boolean set_duration(int duration) {
		if (duration <= 0) {
			return false;
		}
		
		this.duration = duration;
		set_end_date();		// automatically set new end date according to new duration
		
		return true;
	}
	
	private void set_message_status_flag(int message_code) {
		if (message_code < 0 || message_code >= Constants.MESSAGE_STATUS_FLAGS.length) {
			throw new IllegalArgumentException();
		}
		
		MESSAGE_STATUS_FLAG = message_code;
	}

	/**
	 * @param option
	 * @param value
	 * @return True if this.options was successfully updated
	 * 		   with the provided arguments, false otherwise.
	 */
	protected boolean set_option(String option, Object value) {
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
		else {
			return false;
		}

	}

	/**
	 * @param capacity
	 * @return True if this.capacity was successfully updated
	 * 		   with the provided argument, false otherwise.
	 */
	protected boolean set_option_capacity(Integer capacity) {
		if (capacity == null) {
//			return false;
			throw new IllegalArgumentException("Error: capacity cannot be null, set_option_capacity()");
		}
		if (capacity < 0) {
			return false;
		}
		
		this.options.put(Constants.CAPACITY, capacity);
		return true;
	}

	/**
	 * @param power
	 * @return True if this.has_power was successfully updated
	 * 		   with the provided argument, false otherwise.
	 */
	protected boolean set_option_power(Boolean power) {
		if (power == null) {
//			return false;
			throw new IllegalArgumentException("Error: power cannot be null, set_option_power()");
		}
		this.options.put(Constants.POWER, power);
		return true;
	}

	protected boolean set_option_search_building(String building_code) {
		if (building_code == null) {
//			return false;
			throw new IllegalArgumentException("Error: argument cannot be null, set_option_search_for_building()");		
		}
		
		/* There is currently no info for the power plug statii of non-GDC buildings */
		if (!building_code.equalsIgnoreCase(Constants.GDC)) {
			this.set_option_power(false);
		}
		this.options.put(Constants.SEARCH_BUILDING, building_code);
		return true;		
	}
	
	protected void reset() {
		Date start_date = Calendar.getInstance().getTime();
		this.start_date = start_date;
		this.duration = Constants.DEFAULT_QUERY_DURATION;

		set_end_date();
		
		this.set_standard_options();
	}
	
	protected void set_standard_options() {
		this.set_option_capacity(0);
		this.set_option_power(false);
		this.set_option_search_building(Constants.GDC);
	}
			
	/**
	 * @param start_date
	 * @return True if this.start_date was successfully updated
	 * 		   with the provided argument, false otherwise.
	 */
	protected boolean set_start_date(Date start_date) {
		if (start_date == null) {
//			return false;
			throw new IllegalArgumentException("Error: starting date of event cannot be null, set_start_date()");
		}
		
		this.start_date = start_date;
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
		calendar.set(Calendar.HOUR, hour);
		calendar.set(Calendar.MINUTE, minute);
		this.start_date = calendar.getTime();
		set_end_date();
		
		return true;
	}

	// automatically set new end date according to
	// start_date and specified duration
	private boolean set_end_date() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(start_date);
		calendar.add(Calendar.MINUTE, this.duration);
		this.end_date = calendar.getTime();
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	protected Query clone() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(this.start_date);
		
		Query copy = new Query(calendar.getTime());
		copy.set_duration(this.duration);	// updates end_time
		for (Map.Entry<String, Object> entry : this.options.entrySet()) {
			copy.options.put(entry.getKey(), entry.getValue());
		}
		
		return copy;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
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
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return (37 * Integer.parseInt(Utilities.get_time(this.start_date).replaceAll(":", "")) * 
				Integer.parseInt(Utilities.get_time(this.end_date).replaceAll(":", "")) * 17);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
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
	
/* ######################### SEARCH ALGORITHM DEVELOPMENT ######################### */
		
	/**
	 * @return A String representing a Location if a
	 * 		   room is found by the search algorithm.
	 */
	protected String search() {
		return (search(Constants.CSV_FEEDS_CLEANED));
	}
	
	// O(n^2)
	/**
	 * @param eolist
	 * @return A String representing a Location if a
	 * 		   room is found by the search algorithm.
	 */
	protected String search(EventList eolist) {
		if (eolist == null) {
			throw new IllegalArgumentException("Error: eolist cannot be null, search()");
		}
		else if (eolist.get_size() <= 0) {
			set_message_status_flag(Constants.NO_ROOMS_AVAIL);
			return get_message_from_flag();
		}
		
		List<String> valid_rooms = this.search_get_all_available_rooms(eolist);

		if (valid_rooms.size() <= 0) {
			return get_message_from_flag();
		}

		/* Get a random room. */
		int random_index = new Random().nextInt(valid_rooms.size());
		String random_room = valid_rooms.get(random_index).toString();
		
		if (Constants.DEBUG) {
			Collections.sort(valid_rooms);
			System.out.println(valid_rooms.size() + " " + valid_rooms.toString() + "\n");			
		}
		
		return random_room;
	}
	
	protected List<String> search_get_all_available_rooms() {
		return (search_get_all_available_rooms(Constants.CSV_FEEDS_CLEANED));
	}
	
	protected List<String> search_get_all_available_rooms(EventList eolist) {
		if (eolist == null) {
			throw new IllegalArgumentException("Error: eolist cannot be null, search()");
		}

		List<String> out = new ArrayList<String>();
		
		if (eolist.get_size() <= 0) {
			set_message_status_flag(Constants.NO_ROOMS_AVAIL);
			return out;
		}
		else if (this.search_is_at_night()) {
			set_message_status_flag(Constants.GO_HOME);
			return out;
		}
		
		reset_message_status_flag();
		String course_schedule = this.get_current_course_schedule();
		if (course_schedule == null) {
			return out;
		}
		
		int wanted_capacity = this.get_option_capacity();
		
		Building search_building = Building.get_instance(this.get_option_search_building(), course_schedule);
		if (search_building == null) {
			set_message_status_flag(Constants.NO_INFO);
		}
		
		SortedSet<String> valid_rooms = search_building.get_keyset();
		
		if (this.get_option_search_building().equalsIgnoreCase(Constants.GDC)) {
			boolean wanted_power = this.get_option_power();
			
			Iterator<Event> itr = eolist.get_iterator();
			Event curr_event;
			String curr_room_str;
			Room curr_room;
			while (itr.hasNext()) {
				curr_event = itr.next();
				curr_room_str = curr_event.get_location().get_room();
				curr_room = search_building.get_room(curr_room_str);
				
				if (curr_room == null) {
					continue;
				}
				else if (wanted_power && !curr_room.get_has_power()) {
					if (valid_rooms.contains(curr_room_str)) {
						valid_rooms.remove(curr_room_str);
						continue;
					}
				}
				
				if (!Utilities.occur_on_same_day(curr_event.get_start_date(), this.start_date)) {
					if (valid_rooms.contains(curr_room_str)) {
						valid_rooms.remove(curr_room_str);
						continue;
					}
				}
				
				if (Utilities.times_overlap(curr_event.get_start_date(), curr_event.get_end_date(), this.start_date, this.end_date)) {
					if (valid_rooms.contains(curr_room_str)) {
						valid_rooms.remove(curr_room_str);
						continue;
					}
				}
			}
		}
		
		int today = this.get_this_day_of_week();
		boolean is_valid = true;
		Room curr_room;
		for (String curr_room_str : valid_rooms) {
			curr_room = search_building.get_room(curr_room_str);
			if (curr_room == null) {
				continue;
			}
			
			Set<Event> courses = curr_room.get_events(today);
			for (Event curr_event : courses) {
				if (Utilities.times_overlap(curr_event.get_start_date(), curr_event.get_end_date(), this.start_date, this.end_date) ||
						curr_room.get_capacity() < wanted_capacity) {
					is_valid = false;
					break;
				}
			}
			
			if (is_valid) {
				out.add(curr_room_str);
			}
			
			is_valid = true;
		}

		return out;
	}

	
}		// end of file






























































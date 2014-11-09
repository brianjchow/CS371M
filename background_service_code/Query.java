import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Query {
	
	private Date start_date;				// must have date and time at minimum
	private Date end_date;					// automatically set
	private int duration;					// how long the user needs the room to be available, in minutes; default 60
	private Map<String, Object> options;	// [capacity, power plugs] for now
	
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

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(start_date);
		calendar.add(Calendar.MINUTE, this.duration);
		this.end_date = calendar.getTime();
		
		this.options = new HashMap<String, Object>(10);
		this.options.put(Constants.CAPACITY, new Integer(0));
		this.options.put(Constants.POWER, new Boolean(false));
	}

	/**
	 * @param other_set
	 * @return A copy of other_set, with elements removed as
	 * 		   specified by the search options in this Query.
	 */
	private Set<Location> filter_by_query(Set<Location> other_set) {
		if (other_set == null) {
			throw new IllegalArgumentException("Error: other_set cannot be null, filter_by_query()");
		}
		else if (other_set.size() <= 0) {
			return other_set;
		}

		Set<Location> merged = new HashSet<Location>();
		Set<Location> invalid = new HashSet<Location>();
		
		int wanted_capacity = this.get_option_capacity();
		boolean wanted_power = this.get_option_power();

		Room curr_room;
		boolean is_valid = true;
		for (Location location : other_set) {
			if ((curr_room = Constants.VALID_GDC_ROOMS_ROOMLIST.get_room(location)) == null) {
				continue;
			}
			
			// check power and capacity requirements
			if (wanted_power && !curr_room.get_has_power()) {
				is_valid = false;
			}
			if (curr_room.get_capacity() < wanted_capacity) {
				is_valid = false;
			}
			
			/*
			 * Add current location to list of valid locations only
			 * if all reqs as specified by the options are met; add
			 * to list of invalid locations otherwise
			 */
			if (is_valid) {
				merged.add(location);
				
//				Log.d(TAG, "Adding " + location.toString() + " to locs filtered by query");
			}
			else {
				invalid.add(location);
			}
			
			is_valid = true;
		}
		
		merged = this.filter_by_query_get_all_other_rooms(merged, invalid);

		return merged;
	}
	
	private Set<Location> filter_by_query_get_all_other_rooms(Set<Location> valid, Set<Location> invalid) {
//		if (invalid == null) {
//			throw new IllegalArgumentException("Error: other_set cannot be null, filter_by_query_get_all_other_rooms()");
//		}
//		else if (invalid.size() <= 0) {
//			return invalid;
//		}
		
		int wanted_capacity = this.get_option_capacity();
		boolean wanted_power = this.get_option_power();

		Iterator<Map.Entry<Location, Room>> itr = Constants.VALID_GDC_ROOMS_ROOMLIST.get_iterator();
		Map.Entry<Location, Room> curr_entry;
		Location curr_loc;
		Room curr_room;
		boolean is_valid = true;
		while (itr.hasNext()) {
			curr_entry = itr.next();
			curr_loc = curr_entry.getKey();
			curr_room = curr_entry.getValue();
			
			boolean valid_contains_curr_loc = valid.contains(curr_loc);
			boolean invalid_contains_curr_loc = invalid.contains(curr_loc);
			
			if (valid_contains_curr_loc || invalid_contains_curr_loc) {
				continue;
			}
			
			if (!valid_contains_curr_loc && !invalid_contains_curr_loc) {
				
				// check power and capacity requirements
				if (wanted_power && !curr_room.get_has_power()) {
					is_valid = false;
				}
				if (curr_room.get_capacity() < wanted_capacity) {
					is_valid = false;
				}
				
				/*
				 * Add current location to list of valid locations only
				 * if all reqs as specified by the options are met.
				 */
				if (is_valid) {
					valid.add(curr_loc);
				}
				
				is_valid = true;				
			}
		}
		
		return valid;
	}
	
	/**
	 * @param eolist
	 * @return A copy of eolist, with elements removed as
	 * 		   specified by this Query's start and end times.
	 */
	private Set<Location> filter_by_time(EventList eolist) {
		Set<Location> valid_rooms = new HashSet<Location>();
		
		if (eolist == null) {
			throw new IllegalArgumentException("Error: argument cannot be null, filter_by_time()");
		}
		else if (eolist.get_size() <= 0) {
			return valid_rooms;
		}
		
		// must be sorted in order for loop to correctly function
		eolist.sort_by_location(true);
		
		Event curr_eo = eolist.get_event(0);
		Location curr_eo_loc = curr_eo.get_location();
		
		boolean is_valid = true;
		Iterator<Event> itr = eolist.get_iterator();
		Event event;
		Location curr_loc;
		while (itr.hasNext()) {
			event = itr.next();
			curr_loc = event.get_location();
			
			// if this location has already been determined to be
			// unavailable, continue skipping it
			if (!is_valid && curr_loc.equals(curr_eo_loc)) {
				continue;
			}
			
			// new location encountered in eolist; reset flag variables
			if (!curr_loc.equals(curr_eo_loc)) {
				if (is_valid && !valid_rooms.contains(curr_eo_loc)) {
					valid_rooms.add(curr_eo_loc);
				}
				is_valid = true;
				curr_eo = event;
				curr_eo_loc = event.get_location();
			}
			
			// mark location as unavailable if it is in use during the
			// date and times specified in this Query
			if (Utilities.times_overlap(event.get_start_date(), event.get_end_date(), this.start_date, this.end_date)) {
				is_valid = false;
			}
		}

		return valid_rooms;
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
	protected Integer get_option_capacity() {
		return ((Integer) this.options.get(Constants.CAPACITY));
	}
	
	/**
	 * @return True if the user specified power plugs
	 * 		   should be available for most if not all
	 * 		   occupants of any room chosen by the app.
	 * 		   False otherwise.
	 */
	protected Boolean get_option_power() {
		return ((Boolean) this.options.get(Constants.POWER));
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
	
	/**
	 * @return True if the user only specified a starting date
	 * 		   and duration when searching for a room, false otherwise.
	 */
	private boolean has_standard_options() {
		if (this.get_option_power() || this.get_option_capacity() > 0) {
			return false;
		}
		return true;
	}

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
		if (eolist.get_size() == 0) {
			return Constants.NO_ROOMS_AVAIL_MSG;
		}

		/* Reduce search space by eliminating Event dates not within range of this Query. */
		EventList reduced = get_events_by_date(eolist);
		if (reduced.get_size() <= 0) {
			return Constants.NO_ROOMS_AVAIL_MSG;
		}

		/* Continue reducing search space by eliminating Event times not within range of this Query. */
		Set<Location> valid_rooms = this.filter_by_time(reduced);
		
		/* Apply any selected options. */
		if (!this.has_standard_options() && valid_rooms.size() > 0) {
			valid_rooms = this.filter_by_query(valid_rooms);
		}
		
		if (valid_rooms.size() <= 0) {
			return Constants.NO_ROOMS_AVAIL_MSG;
		}

		/* Get a random room. */
		List<Location> random = new ArrayList<Location>(valid_rooms);
		int random_index = new Random().nextInt(valid_rooms.size());
		String random_room = random.get(random_index).toString();
		
		if (Constants.DEBUG) {
			Collections.sort(random);
			System.out.println(random.size() + " " + random.toString() + "\n");			
		}
		
		return random_room;
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
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(this.start_date);
		calendar.add(Calendar.MINUTE, duration);
		
		// automatically set new end date according to new duration
		this.end_date = calendar.getTime();
		
		return true;
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

		return false;
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
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(start_date);
		calendar.add(Calendar.MINUTE, this.duration);
		
		// automatically set new end date according to
		// start_date and specified duration
		this.end_date = calendar.getTime();
		
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
		if (this == other) {
			return true;
		}
		if (!(other instanceof Query)) {
			return false;
		}
		
		Query other_query = (Query) other;
		if (this.start_date.equals(other_query.start_date) &&
			this.end_date.equals(other_query.end_date) &&
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
		return (37 * this.start_date.hashCode() * this.end_date.hashCode() * 17);
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
	
	
}		// end of file





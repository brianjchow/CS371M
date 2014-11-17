import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.collect.ComparisonChain;

final class Room implements Comparable<Room> {

	private static final String TAB = "    ";
	
	private Location location;
	private String type;
	private int capacity;
	private boolean has_power;

//	private Map<Integer, EventList> course_schedule;
	private Map<Integer, Set<Event>> course_schedule;

	/**
	 * @param location
	 * 
	 * Default constructor. Fills in instance variables
	 * with default values.
	 */
	protected Room(Location location) {
		this(location, Constants.DEFAULT_ROOM_TYPE, Constants.DEFAULT_ROOM_CAPACITY, false);
	}

	/**
	 * @param location
	 * @param type
	 * @param capacity
	 * @param has_power
	 */
	protected Room(Location location, String type, int capacity, boolean has_power) {
		if (location == null || type == null || capacity < Constants.DEFAULT_ROOM_CAPACITY) {
			throw new IllegalArgumentException("Error: one or more arguments is null, Room constructor");
		}

		this.location = location;
		this.type = type;
		this.capacity = capacity;
		this.has_power = has_power;
		
		// loopify?
		this.course_schedule = new HashMap<Integer, Set<Event>>();
		this.course_schedule.put(Constants.SUNDAY, new TreeSet<Event>());
		this.course_schedule.put(Constants.MONDAY, new TreeSet<Event>());
		this.course_schedule.put(Constants.TUESDAY, new TreeSet<Event>());
		this.course_schedule.put(Constants.WEDNESDAY, new TreeSet<Event>());
		this.course_schedule.put(Constants.THURSDAY, new TreeSet<Event>());
		this.course_schedule.put(Constants.FRIDAY, new TreeSet<Event>());
		this.course_schedule.put(Constants.SATURDAY, new TreeSet<Event>());
	}

	// use the Constants provided for the day; will fail otherwise
	protected boolean add_event(int day_of_week, Event event) {
		if (!Utilities.valid_day_of_week(day_of_week) || event == null) {
			return false;
		}
		
		Set<Event> events = this.course_schedule.get(day_of_week);
		if (events.contains(event)) {
			return false;
		}
		
		events.add(event);
		this.course_schedule.put(day_of_week, events);
		return true;
	}
	
	protected final Set<Event> get_events(int day_of_week) {
		if (!Utilities.valid_day_of_week(day_of_week)) {
//			return null;
			throw new IllegalArgumentException();
		}
		return (this.course_schedule.get(day_of_week));
	}
	
	protected final int get_num_events() {
		int total_size = 0;
		for (int i = Constants.SUNDAY; i <= Constants.SATURDAY; i++) {
			total_size += this.course_schedule.get(i).size();
		}
		return total_size;
	}
	
	protected final Map<Integer, Set<Event>> get_events() {
		return this.course_schedule;
	}

	/**
	 * @return The Location of this Room.
	 */
	protected Location get_location() {
		return this.location;
	}

	/**
	 * @return The Location of this Room, in String format.
	 */
	protected String get_name() {
		return this.location.toString();
	}

	/**
	 * @return The 3-letter building code of this
	 * 		   Room's Location.
	 */
	protected String get_building_name() {
		return this.location.get_building();
	}

	/**
	 * @return The room number of this Room's Location.
	 */
	protected String get_room_number() {
		return this.location.get_room();
	}

	/**
	 * @return The type of room of this Room's
	 * 		   Location (conference room, classroom, etc).
	 */
	protected String get_type() {
		return this.type;
	}

	/**
	 * @return The maximum capacity of this Room.
	 */
	protected int get_capacity() {
		return this.capacity;
	}

	/**
	 * @return True if this Room has power plugs available
	 * 		   for most occupants, false otherwise.
	 */
	protected boolean get_has_power() {
		return this.has_power;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	protected Room clone() {
		Room out = new Room(this.location.clone());
		out.type = this.type;
		out.capacity = this.capacity;
		out.has_power = this.has_power;
		
		out.course_schedule = new HashMap<Integer, Set<Event>>(this.course_schedule.size() * 2);
		
		Integer curr_key;
		Set<Event> curr_val;
		for (Map.Entry<Integer, Set<Event>> entry : this.course_schedule.entrySet()) {
			curr_key = Integer.valueOf(entry.getKey());
			curr_val = new HashSet<Event>(entry.getValue().size() * 2);
			for (Event event : entry.getValue()) {
				curr_val.add(event.clone());
			}
			out.course_schedule.put(curr_key, curr_val);
		}
		
		return out;
	}

	// ????? is this what we really want to do
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Room other) {
		int result = ComparisonChain.start()
			.compare(this.location, other.location)
			.compare(this.type, other.type)
			.compare(this.capacity, other.capacity)
			.compare(this.has_power, other.has_power)
			.result();
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}

		if (!(other instanceof Room)) {
			return false;
		}

		Room other_room = (Room) other;
		if (other_room.location.equals(this.location) &&
				other_room.type.equals(this.type) &&
				other_room.capacity == this.capacity &&
				other_room.has_power == this.has_power) {
			return true;
		}

		return false;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return (Utilities.stringHashCode(this.location.toString()));
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder out = new StringBuilder();

		out.append("Room:\t" + this.location.toString() + "\n");
		out.append("Type:\t" + this.type + "\n");
		out.append("Size:\t" + this.capacity + "\n");
		out.append("Power:\t" + this.has_power + "\n");
		out.append("Schedule:\n" + TAB + this.get_num_events() + " weekly class(es)\n");
		
		for (int i = Constants.SUNDAY; i <= Constants.SATURDAY; i++) {
			out.append(TAB + Constants.DAYS_OF_WEEK_SHORT[i] + ": ");
			
			Set<Event> temp = this.course_schedule.get(i);
			Set<Event> sorted_by_time = new TreeSet<Event>(temp);
			
			int counter = 0;
//			Event prev_event = null;
			for (Event event : sorted_by_time) {
				if (counter > 0) {
					out.append(", ");
				}
//System.out.println("\n " + event.hashCode() + "\n" + event.toString() + "\n");				
				out.append(event.get_event_name() + " (" + Utilities.get_time(event.get_start_date()) + ")");
//				out.append(" (" + event.hashCode() + ")");
				
//				if (prev_event != null) {
//					out.append(" (" + (event.hashCode() == prev_event.hashCode()) + ", " + (event.equals(prev_event)) + ", " + (prev_event.equals(event)) + ")");
//				}
				
				counter++;
//				prev_event = event;
			}
			
			out.append("\n");
			
//			out.append(sorted_by_time.toString() + "\n");
		}

		return (out.toString());
	}

}		// end of file





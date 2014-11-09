import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

final class RoomList {
	
	private Map<Location, Room> list;
	
	protected RoomList() {
		this.list = new HashMap<Location, Room>(Constants.VALID_GDC_ROOMS.length * 2);
		initialise();
	}
	
	private void initialise() {
		String[] room_types = Constants.VALID_GDC_ROOMS_TYPES;
		int[] room_capacities = Constants.VALID_GDC_ROOMS_CAPACITIES;
		boolean[] room_powers = Constants.VALID_GDC_ROOMS_POWERS;
		String gdc_str = Constants.GDC + " ";
		Room room;
		for (int i = 0; i < Constants.VALID_GDC_ROOMS.length; i++) {
			room = new Room(new Location(gdc_str + Constants.VALID_GDC_ROOMS[i]), room_types[i], room_capacities[i], room_powers[i]);
			this.add(room);
			
//			this.add(new Room(new Location(gdc_str + Constants.VALID_GDC_ROOMS[i])));
		}
	}

	protected int get_size() {
		return this.list.size();
	}
	
	private boolean add(Room room) {
		if (room == null) {
			return false;
		}
		
		Location this_location = room.get_location();
		if (this.list.get(this_location) != null) {
			return false;
		}
		
		this.list.put(this_location, room);		
		return true;
	}
	
	protected Room get_room(Location location) {
		if (location == null) {
			return null;
		}
		return (this.list.get(location));
	}
	
	protected Iterator<Map.Entry<Location, Room>> get_iterator() {
		return (this.list.entrySet().iterator());
	}
	
//	protected Set<Location> filter_by_query(Set<Location> other_set, Query query) {
//		if (other_set == null || query == null) {
//			throw new IllegalArgumentException();
//		}
//		else if (other_set.size() <= 0) {
//			return other_set;
//		}
//
//		Set<Location> merged = new HashSet<Location>();
//		
//		int wanted_capacity = query.get_option_capacity();
//		boolean wanted_power = query.get_option_power();
//		
//		boolean is_valid = true;
//		
//		Room curr_room;
//		for (Map.Entry<Location, Room> entry : this.list.entrySet()) {
//			curr_room = entry.getValue();
//			
//			if (curr_room.get_has_power() != wanted_power) {
//				is_valid = false;
//			}
//			if (curr_room.get_capacity() < wanted_capacity) {
//				is_valid = false;
//			}
//			
//			if (is_valid) {
//				merged.add(entry.getKey());
//			}
//			
//			is_valid = true;
//		}
//		
//		
////		Set<Location> valid_rooms = find_by_query(query);
//////		boolean search_by_date = (query.get_date() == null);
////		for (Location location : other_set) {
////			if (valid_rooms.contains(location) && !merged.contains(location)) {
////				merged.add(location);
////			}
////		}
//		
//		return merged;
//	}
	
//	protected Set<Location> find_by_query(Query query) {
//		if (query == null) {
//			return (new HashSet<Location>());
//		}
//		
//		Set<Location> results = new HashSet<Location>(this.list.size() * 2);
//		int option_capacity = query.get_option_capacity();
//		boolean option_power = query.get_option_power();
//		
//		if (option_capacity <= Constants.DEFAULT_ROOM_CAPACITY && !option_power) {
//			return (new HashSet<Location>(this.list.keySet()));
//		}
//		
//		for (Map.Entry<Location, Room> entry : this.list.entrySet()) {
//			Room curr_room = entry.getValue();
//			if (option_power) {
//				if (curr_room.get_has_power() && curr_room.get_capacity() >= option_capacity) {
//					results.add(entry.getKey());
//				}
//			}
//			else {
//				if (curr_room.get_capacity() >= option_capacity) {
//					results.add(entry.getKey());
//				}
//			}
//		}
//		
//		return results;
//	}
	
	private Map<Location, Room> get_sorted_map() {
		if (this.list == null) {
			throw new IllegalArgumentException();
		}
		Map<Location, Room> out = new TreeMap<Location, Room>(this.list);
		return out;
	}
	
	public String toString() {
		StringBuilder out = new StringBuilder();
		
		Map<Location, Room> temp = this.get_sorted_map();
		
		for (Map.Entry<Location, Room> entry : temp.entrySet()) {
			out.append(entry.getValue().toString() + "\n\n");
		}
		
		return (out.toString());
	}
	
//	protected class Room implements Comparable<Room> {
//		
//		private Location location;
//		private String type;
//		private int capacity;
//		private boolean has_power;
//		
//		protected Room(Location location) {
//			this(location, Constants.CONFERENCE, Constants.DEFAULT_ROOM_CAPACITY, false);
//		}
//		
//		protected Room(Location location, String type, int capacity, boolean has_power) {
//			if (location == null || type == null || capacity < 0) {
//				throw new IllegalArgumentException();
//			}
//			
//			this.location = location;
//			this.type = type;
//			this.capacity = capacity;
//			this.has_power = has_power;
//		}
//		
//		protected Location get_location() {
//			return this.location;
//		}
//		
//		protected String get_name() {
//			return this.location.toString();
//		}
//		
//		protected String get_building_name() {
//			return this.location.get_building();
//		}
//		
//		protected String get_room_number() {
//			return this.location.get_room();
//		}
//		
//		protected String get_type() {
//			return this.type;
//		}
//		
//		protected int get_capacity() {
//			return this.capacity;
//		}
//		
//		protected boolean get_has_power() {
//			return this.has_power;
//		}
//		
//		// ????? is this what we really want to do
//		public int compareTo(Room other) {
//			return (this.location.compareTo(other.location));
//		}
//		
//		protected Room clone() {
//			Room out = new Room(this.location.clone());
//			out.type = this.type;
//			out.capacity = this.capacity;
//			out.has_power = this.has_power;
//			return out;
//		}
//		
//		public boolean equals(Object other) {
//			if (this == other) {
//				return true;
//			}
//			
//			if (!(other instanceof Room)) {
//				return false;
//			}
//			
//			Room other_room = (Room) other;
//			if (other_room.location.equals(this.location) &&
//				other_room.type.equals(this.type) &&
//				other_room.capacity == this.capacity &&
//				other_room.has_power == this.has_power) {
//				return true;
//			}
//			
//			return false;
//		}
//		
//		public int hashCode() {
//			return (Utilities.stringHashCode(this.location.toString()));
//			
////			return (this.location.hashCode());
//		}
//		
//		public String toString() {
//			StringBuilder out = new StringBuilder();
//			
//			out.append("Room:\t" + this.location.toString() + "\n");
//			out.append("Type:\t" + this.type + "\n");
//			out.append("Size:\t" + this.capacity + "\n");
//			out.append("Power:\t" + this.has_power);
//			
//			return (out.toString());
//		}
//		
////		protected boolean set_capacity(int capacity) {
////			if (capacity < 0) {
////				return false;
////			}
////			
////			this.capacity = capacity;
////			return true;
////		}
//	//	
////		protected boolean set_power(boolean power) {
////			this.has_power = power;
////			return true;
////		}
//	}
}

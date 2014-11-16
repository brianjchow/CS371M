import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Building implements Comparable<Building> {

	private String name;
	private Map<String, Room> rooms;
//	private Set<Room> rooms;
//	private RoomList rooms;
	
	protected Building(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}
		
		this.name = name;
		this.rooms = new HashMap<String, Room>();
	}
	
//	protected boolean add_room(String room_num) {
//		if (room_num == null) {
////			return false;
//			throw new IllegalArgumentException();
//		}
//		
//		this.rooms.add(room);
//		return true;
//	}
	
	protected boolean contains_room(String room_num) {
		if (room_num == null) {
//			return false;
			throw new IllegalArgumentException();
		}
		
		return (this.rooms.get(room_num) != null);
	}
	
	protected static Building get_gdc_instance() {
		Building out = new Building(Constants.GDC);
		
		String[] room_types = Constants.VALID_GDC_ROOMS_TYPES;
		int[] room_capacities = Constants.VALID_GDC_ROOMS_CAPACITIES;
		boolean[] room_powers = Constants.VALID_GDC_ROOMS_POWERS;
		String gdc_str = Constants.GDC + " ";
		Room room;
		for (int i = 0; i < Constants.VALID_GDC_ROOMS.length; i++) {
			room = new Room(new Location(gdc_str + Constants.VALID_GDC_ROOMS[i]), room_types[i], room_capacities[i], room_powers[i]);
			out.rooms.put(Constants.VALID_GDC_ROOMS[i], room);
		}
		
		return out;
	}
	
//	protected Iterator<Room> get_iterator() {
//		return this.rooms.iterator();
//	}
	
	protected Iterator<Map.Entry<String, Room>> get_iterator() {
		return (this.rooms.entrySet().iterator());
	}
	
	protected String get_name() {
		return this.name;
	}
	
	protected int get_num_rooms() {
		return this.rooms.size();
	}
	
//	protected Room get_room(String room_num) {
//		if (room_num == null || room_num.length() <= 0) {
//			throw new IllegalArgumentException();
//		}
//		
//		for (Room room : this.rooms) {
//			if (room.get_location().get_room().equals(room_num)) {
//				return room;
//			}
//		}
//		
//		return null;
//	}
	
	protected Room get_room(String room_num) {
		return (this.rooms.get(room_num));
	}
	
	protected boolean put_room(String room_num, Room room) {
		if (room_num == null || room_num.length() <= 0 || room == null) {
//			return false;
			throw new IllegalArgumentException();
		}
		this.rooms.put(room_num, room);
		return true;
	}
	
	@Override
	public int compareTo(Building other) {
		return (this.name.compareTo(other.name));
	}
	
	@Override
	public boolean equals(Object other_building) {
		if (this == other_building) {
			return true;
		}
		else if (!(other_building instanceof Building)) {
			return false;
		}
		
		Building other = (Building) other_building;
		if (!this.name.equals(other.name) || this.rooms.size() != other.rooms.size()) {
			return false;
		}
		
//		for (Room room : this.rooms) {
//			if (!other.rooms.contains(room)) {
//				return false;
//			}
//		}
		
		String curr_room_num;
		Room curr_room, other_room;
		for (Map.Entry<String, Room> entry : other.rooms.entrySet()) {
			curr_room_num = entry.getKey();
			curr_room = entry.getValue();
			
			if ((other_room = this.rooms.get(curr_room_num)) == null) {
				return false;
			}
			else {
				if (!other_room.equals(curr_room)) {
					return false;
				}
			}
		}
		
		return true;
	}
	
	@Override
	public int hashCode() {
		return (37 * Utilities.stringHashCode(this.name));
	}
	
	@Override
	public String toString() {
		StringBuilder out = new StringBuilder(500);
		out.append("Building:\t" + this.name + "\n");
		
		for (Room room : this.rooms.values()) {
			out.append(room.toString() + "\n");
		}
		
		return (out.toString());
	}
	
}

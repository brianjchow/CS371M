import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;


final class Building implements Comparable<Building> {

	private String name;
	private Map<String, Room> rooms;
	
	private Building(String name) {
		if (name == null) {
			throw new IllegalArgumentException();
		}
		else if (name.length() != Constants.BUILDING_CODE_LENGTH) {
			throw new IllegalArgumentException("Building codes must be exactly 3 characters in length.");
		}
		
		
		this.name = name.toUpperCase();
		this.rooms = new HashMap<String, Room>();
	}
		
	protected boolean contains_room(String room_num) {
		if (room_num == null) {
//			return false;
			throw new IllegalArgumentException();
		}
		
		return (this.rooms.get(room_num) != null);
	}
		
//	protected static Building get_gdc_instance(String db_file_name) {
//		if (db_file_name == null || db_file_name.length() <= 0) {
//			throw new IllegalArgumentException();
//		}
//		
//		Building out = new Building(Constants.GDC);
//		out.populate(db_file_name);
//		return out;
//	}
	
//	protected static final Building get_instance(String building_name, String db_file_name) {
//		if (building_name == null || building_name.length() != Constants.BUILDING_CODE_LENGTH || db_file_name == null || db_file_name.length() <= 0) {
//			throw new IllegalArgumentException();
//		}
//		
//		Building out;
//		
//		if (Constants.COURSE_SCHEDULE_NEXT_SEMESTER != null &&
//			Utilities.containsIgnoreCase(Constants.COURSE_SCHEDULE_NEXT_SEMESTER, db_file_name)) {
//			if ((out = Constants.BUILDING_CACHELIST_NEXT_SEMESTER.get_building(building_name)) == null) {
//				out = new Building(building_name);
//				out.populate(db_file_name);
//				Constants.BUILDING_CACHELIST_NEXT_SEMESTER.put_building(building_name, out);
//			}
//		}
//		else if (Utilities.containsIgnoreCase(Constants.COURSE_SCHEDULE_THIS_SEMESTER, db_file_name)) {
//			if ((out = Constants.BUILDING_CACHELIST_THIS_SEMESTER.get_building(building_name)) == null) {
//				out = new Building(building_name);
//				out.populate(db_file_name);
//				Constants.BUILDING_CACHELIST_THIS_SEMESTER.put_building(building_name, out);
//			}
//		}
//		else {
//			out = null;
//		}
//		
//		return out;
//	}
	
	protected static final Building get_instance(String building_name, String db_file_name) {
		if (building_name == null || building_name.length() != Constants.BUILDING_CODE_LENGTH || db_file_name == null || db_file_name.length() <= 0) {
			throw new IllegalArgumentException("Bad argument, Building.get_instance()");
		}
		
		int file_ext_dot_index = db_file_name.lastIndexOf(".");
		if (file_ext_dot_index == -1) {
			db_file_name += "." + Constants.DEFAULT_DB_EXTENSION;
		}
		
		Building out;
		
		if (Constants.COURSE_SCHEDULE_NEXT_SEMESTER != null &&
			Utilities.containsIgnoreCase(Constants.COURSE_SCHEDULE_NEXT_SEMESTER, db_file_name)) {

			if ((out = Constants.BUILDING_CACHELIST_NEXT_SEMESTER.get_building(building_name)) != null) {
				return out;
			}
			
			out = new Building(building_name);
			out.populate(db_file_name);
			
			if (Constants.BUILDING_CACHELIST_NEXT_SEMESTER != null) {
				Constants.BUILDING_CACHELIST_NEXT_SEMESTER.put_building(building_name, out);
			}
			
//			if ((out = Constants.BUILDING_CACHELIST_NEXT_SEMESTER.get_building(building_name)) == null) {
//				Constants.BUILDING_CACHELIST_NEXT_SEMESTER.put_building(building_name, out);
//			}
		}
//		else if (Utilities.containsIgnoreCase(Constants.COURSE_SCHEDULE_THIS_SEMESTER, db_file_name)) {
		else {
			
			if ((out = Constants.BUILDING_CACHELIST_THIS_SEMESTER.get_building(building_name)) != null) {
				return out;
			}
			
			out = new Building(building_name);
			out.populate(db_file_name);
			
			if (Constants.BUILDING_CACHELIST_THIS_SEMESTER != null) {
				Constants.BUILDING_CACHELIST_THIS_SEMESTER.put_building(building_name, out);
			}
			
//			if ((out = Constants.BUILDING_CACHELIST_THIS_SEMESTER.get_building(building_name)) == null) {
//				Constants.BUILDING_CACHELIST_THIS_SEMESTER.put_building(building_name, out);
//			}
		}
//		else {
//			out = null;
//		}
		
		return out;
	}
	
	protected final SortedSet<String> get_keyset() {
		SortedSet<String> out = new TreeSet<String>();
		
		for (String room_num : this.rooms.keySet()) {
			out.add(room_num);
		}
		
		return out;
	}
	
//	protected final Iterator<Map.Entry<String, Room>> get_iterator() {
//		return (this.rooms.entrySet().iterator());
//	}
	
	protected String get_name() {
		return this.name;
	}
	
	protected int get_num_rooms() {
		return this.rooms.size();
	}
	
	protected Room get_room(String room_num) {
		return (this.rooms.get(room_num));
	}
	
	private void populate(String db_file_name) {
		if (db_file_name == null || db_file_name.length() <= 0) {
			throw new IllegalArgumentException();
		}
		
		this.rooms = Database.populate(this.name, db_file_name);
	}
	
//	protected boolean put_room(String room_num, Room room) {
//		if (room_num == null || room_num.length() <= 0 || room == null) {
////			return false;
//			throw new IllegalArgumentException();
//		}
//		this.rooms.put(room_num, room);
//		return true;
//	}
	
	@Override
	protected Building clone() {
		Building out = new Building(this.name);
		
		String curr_room_str;
		Room curr_room;
		for (Map.Entry<String, Room> entry : this.rooms.entrySet()) {
			curr_room_str = entry.getKey();
			curr_room = entry.getValue();
			
			out.rooms.put(curr_room_str, curr_room.clone());
		}
		
		return out;
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
//		out.append("Building:\t" + this.name + "\n");
		
		SortedSet<Room> values = new TreeSet<Room>(this.rooms.values());
		
		for (Room room : values) {
			out.append(room.toString() + "\n");
		}
		
		return (out.toString());
	}
		
	private static class Database {
		
//		private static final String		BUILDING		=	"building";
		private static final String		ROOM			=	"room";
		private static final String		CAPACITY		=	"capacity";
		private static final String		NAME			=	"name";
		private static final String		MEETING_DAYS	=	"meeting_days";
		private static final String		START_TIME		=	"start_time";
		private static final String		END_TIME		=	"end_time";
		
		private static String db_name = null;
		private static Connection connection = null;
		private static Statement statement = null;
		
		// http://zetcode.com/db/sqlite/select/
		protected static Map<String, Room> populate(String building_name, String db_file_name) {
			if (building_name == null || building_name.length() != Constants.BUILDING_CODE_LENGTH || db_file_name == null || db_file_name.length() <= 0) {
				throw new IllegalArgumentException();
			}
			
			Map<String, Room> out = new HashMap<String, Room>(25);
						
			db_name = strip_file_name_formatting(db_file_name);
			
			establish_cxn();
			
//			final Calendar cal = Calendar.getInstance();
//			final Calendar start_cal = Calendar.getInstance();
//			final Calendar end_cal = Calendar.getInstance();
						
			String room_num, name;
			boolean[] meeting_days;
			Date start_date, end_date;
			Integer capacity;
			
			Room room;
			Location location;
			Event event;
			Integer start_time, end_time;
			
			try {
				ResultSet rs = statement.executeQuery("SELECT * FROM " + db_name + " WHERE building=\"" + building_name.toUpperCase() + "\"");
				
				boolean building_is_gdc = building_name.equalsIgnoreCase(Constants.GDC);
				
				if (building_is_gdc) {
					out = initialise_gdc_room_properties();
				}
				
				while (rs.next()) {
					room_num = rs.getString(ROOM);
					name = rs.getString(NAME);
					meeting_days = set_meeting_days(rs.getString(MEETING_DAYS));
					
					start_time = rs.getInt(START_TIME);
					end_time = rs.getInt(END_TIME);
					
					start_date = Utilities.get_date(start_time);
					end_date = Utilities.get_date(end_time);
					capacity = rs.getInt(CAPACITY);
					
					if (start_date != null && end_date != null) {
						if ((room = out.get(room_num)) == null) {
							if (building_is_gdc) {
								continue;
							}
						}
						
						location = new Location(building_name, room_num);
						event = new Event(name, start_date, end_date, location);
						
						if (room == null) {
							
							if (capacity > 0) {
								room = new Room(location, Constants.DEFAULT_ROOM_TYPE, capacity, false);
							}
							else {
								room = new Room(location);
							}
						}
						
//						start_cal.setTime(start_time);
//						end_cal.setTime(end_time);
						
						// cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
						

						for (int i = Constants.SUNDAY; i <= Constants.SATURDAY; i++) {
							if (meeting_days[i]) {
//								cal.set(Calendar.DAY_OF_WEEK, i);
//								start_date = Utilities.get_date(cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),
//										cal.get(Calendar.YEAR), start_time);
//								
//								end_date = Utilities.get_date(cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),
//										cal.get(Calendar.YEAR), end_time);
//								
//								event.set_start_date(start_date);
//								event.set_end_date(end_date);
								
								room.add_event(i, event);
							}
						}
						
						out.put(room_num, room);
					}
				}

			}
			catch (SQLException e) {
				throw new RuntimeException(e);
			}
			
			close_cxn();
			
			return out;
		}

		private static void close_cxn() {
			try {
				if (connection != null) {
					connection.close();
				}
			}			
			catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		
		private static void establish_cxn() {
			
			// https://bitbucket.org/xerial/sqlite-jdbc
			try {
				Class.forName("org.sqlite.JDBC");
				connection = DriverManager.getConnection("jdbc:sqlite:" + db_name + "." + Constants.DEFAULT_DB_EXTENSION);
				statement = connection.createStatement();
			}
			catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			
		}

		private static boolean[] initialise_days_of_week_array() {
			boolean[] array = new boolean[Constants.NUM_DAYS_IN_WEEK];
			
			for (int i = 0; i < Constants.NUM_DAYS_IN_WEEK; i++) {
				array[i] = false;
			}
			
			return array;	
		}
		
		private static Map<String, Room> initialise_gdc_room_properties() {
			Map<String, Room> out = new HashMap<String, Room>(Constants.VALID_GDC_ROOMS.length * 2);

			String[] room_types = Constants.VALID_GDC_ROOMS_TYPES;
			int[] room_capacities = Constants.VALID_GDC_ROOMS_CAPACITIES;
			boolean[] room_powers = Constants.VALID_GDC_ROOMS_POWERS;
			String gdc_str = Constants.GDC + " ";
			Room room;
			for (int i = 0; i < Constants.VALID_GDC_ROOMS.length; i++) {
				if ((Constants.IGNORE_CONFERENCE_ROOMS && room_types[i].equals(Constants.CONFERENCE)) ||
						room_types[i].equals(Constants.LOBBY) ||
						room_types[i].equals(Constants.LOUNGE)) {
					continue;
				}
				room = new Room(new Location(gdc_str + Constants.VALID_GDC_ROOMS[i]), room_types[i], room_capacities[i], room_powers[i]);
				out.put(Constants.VALID_GDC_ROOMS[i], room);
			}
			
			return out;
		}
		
		// IGNORES SATURDAY AND SUNDAY (not that they're likely to appear)
		private static boolean[] set_meeting_days(String code) {
			if (code == null || code.length() < 0) {
				throw new IllegalArgumentException();
			}
			
			boolean[] days = initialise_days_of_week_array();
			
			int code_length = code.length();
			for (int i = 0; i < code_length; i++) {
				char curr_char = code.charAt(i);
				String char_to_str = Character.toString(curr_char);
				if (Utilities.containsIgnoreCase(char_to_str, "t")) {
					if (i < code_length - 1 && Utilities.containsIgnoreCase(Character.toString(code.charAt(i + 1)), "h")) {
						days[Constants.THURSDAY] = true;
						i++;
					}
					else {
						days[Constants.TUESDAY] = true;
					}
					continue;
				}
				
				if (Utilities.containsIgnoreCase(char_to_str, "m")) {
					days[Constants.MONDAY] = true;
					continue;
				}
				else if (Utilities.containsIgnoreCase(char_to_str, "w")) {
					days[Constants.WEDNESDAY] = true;
					continue;
				}
				else if (Utilities.containsIgnoreCase(char_to_str, "f")) {
					days[Constants.FRIDAY] = true;
					continue;
				}
			}
			
			return days;
		}
		
		private static String strip_file_name_formatting(String file_name) {
			String out = file_name;
			
			int subfolder_index = file_name.lastIndexOf("/");
			int file_ext_dot_index = file_name.lastIndexOf(".");
			if (file_ext_dot_index >= 0 && file_ext_dot_index < file_name.length()) {
				if (subfolder_index == -1) {
					out = file_name.substring(0, file_ext_dot_index);
				}
				else {
					out = file_name.substring(subfolder_index + 1, file_ext_dot_index);
				}
			}
			else {
				out = file_name;
			}
			
			return out;
		}
		
	}
	
}

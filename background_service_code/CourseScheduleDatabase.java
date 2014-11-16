import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class CourseScheduleDatabase {
	
	private static final int DELIMITER = (int) '\t';
	private static final int EOL = (int) '#';
	private static final int MAX_ROOM_LENGTH = 5;
	
	private static final int	STD_LINE_SIZE				=	75;
	
	private static final int	INDEX_CLASS_DEPT			=	4;
	private static final int	INDEX_CLASS_NUM				=	5;
	private static final int	INDEX_CLASS_NAME			=	7;
	
	private static final int	INDEX_CLASS_MEETING_DAYS	=	29;
	private static final int	INDEX_CLASS_START_DATE		=	30;
	private static final int 	INDEX_CLASS_END_DATE		=	31;
	private static final int 	INDEX_CLASS_BUILDING		=	32;
	private static final int 	INDEX_CLASS_ROOM			=	33;
	private static final int 	INDEX_CLASS_ROOM_CAPACITY	=	34;
	
	private static final int 	INDEX_LAB_MEETING_DAYS		=	37;
	private static final int 	INDEX_LAB_START_TIME		=	38;
	private static final int 	INDEX_LAB_END_TIME			=	39;
	private static final int 	INDEX_LAB_BUILDING			=	40;
	private static final int 	INDEX_LAB_ROOM				=	41;
	private static final int 	INDEX_LAB_ROOM_CAPACITY		=	42;
	
	private static final String	DB_FORMAT					=	"building string, room string, capacity integer, name string, meeting_days string, start_time integer, end_time integer";
	
	private static String db_name = null;
	private static Connection connection = null;
	private static Statement statement = null;
	
	private static Map<Location, ArrayList<Details>> info;
	
	private static void establish_cxn() {
		
		// https://bitbucket.org/xerial/sqlite-jdbc
		try {

			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:" + db_name + ".db");
			statement = connection.createStatement();
			
//			if (WRITE_TO_DB) {
//				statement.executeUpdate("drop table if exists " + db_name);
//				statement.executeUpdate("create table " + db_name + " ("
//						+ "name string, meeting_days string, start_time INTEGER, end_time INTEGER, building string, room string, capacity INTEGER)");
//				
////				statement.executeUpdate("create table " + db_name + " ("
////						+ "name string, meeting_days string, start_time INTEGER, end_time INTEGER, location string, capacity INTEGER)");
//				
////				statement.executeUpdate("create table " + db_name + " ("
////						+ "dept string, num string, name string, meeting_days string, start_time INTEGER, end_time INTEGER, building string, room string, capacity INTEGER)");
//			}
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
	
	private static void read_course_schedules(String schedule_file) {
		if (schedule_file == null || schedule_file.length() <= 0) {
			throw new IllegalArgumentException();
		}
		
		InputReader input = new InputReader(schedule_file);
		
		StringBuilder curr_line = new StringBuilder(200);
		List<String> curr_line_tokens = new ArrayList<String>();
		int curr_char;
		while ((curr_char = input.read()) != -1) {
			
			if (curr_char == EOL) {				
				parse_line(curr_line_tokens);
				
				curr_line.setLength(0);
				curr_line_tokens = new ArrayList<String>();
			}
			else if (curr_char == DELIMITER) {
//				if (curr_line.length() > 0) {
					String temp = Utilities.regex_replace(curr_line.toString(), "\"", "");
					curr_line_tokens.add(temp);
					curr_line.setLength(0);
//				}
			}
			else {
				curr_line.append((char) curr_char);
			}			
		}
	}
	
	private static void parse_line(List<String> tokens) {
		if (tokens == null || tokens.size() != STD_LINE_SIZE) {
//			System.out.println("hi diddly hoe" + " " + tokens.size());
			return;
		}
		
		if (tokens.get(INDEX_CLASS_MEETING_DAYS).length() <= 0 || tokens.get(INDEX_CLASS_START_DATE).length() <= 0 || tokens.get(INDEX_CLASS_END_DATE).length() <= 0 || tokens.get(INDEX_CLASS_BUILDING).length() <= 0 || tokens.get(INDEX_CLASS_ROOM).length() <= 0) {
			return;
		}

		String dept = tokens.get(INDEX_CLASS_DEPT);
		String num = Utilities.regex_replace(tokens.get(INDEX_CLASS_NUM), " ", "");
//		String num = tokens.get(INDEX_CLASS_NUM);
		String name = Utilities.regex_replace(tokens.get(INDEX_CLASS_NAME), "[,']", "");
		String meeting_days = tokens.get(INDEX_CLASS_MEETING_DAYS);
		int start_time = Integer.parseInt(Utilities.time_to_24h(tokens.get(INDEX_CLASS_START_DATE)));
		int end_time = Integer.parseInt(Utilities.time_to_24h(tokens.get(INDEX_CLASS_END_DATE)));
		String building = tokens.get(INDEX_CLASS_BUILDING);
		String room = tokens.get(INDEX_CLASS_ROOM);
		int capacity = Integer.parseInt(Utilities.regex_replace(tokens.get(INDEX_CLASS_ROOM_CAPACITY), "[^\\d]", ""));
		
		Event class_event = null;
		String class_name = dept + " " + num + " - " + name;
		Date class_start_time = Utilities.get_date(start_time);
		Date class_end_time = Utilities.get_date(end_time);
		
		String class_room = tokens.get(INDEX_CLASS_ROOM);
		if (building.equalsIgnoreCase(Constants.GDC) && class_room.length() < MAX_ROOM_LENGTH) {
			class_room = pad_trailing_zeroes(class_room, MAX_ROOM_LENGTH);
		}
				
		Location class_location = new Location(building + " " + class_room);
		
		if (class_start_time != null && class_end_time != null) {			
			ArrayList<Details> temp;
			Details details = new Details(capacity, class_name, meeting_days, start_time, end_time);
			if ((temp = info.get(class_location)) == null) {
				temp = new ArrayList<Details>();
			}
			temp.add(details);
			info.put(class_location, temp);

		}
		
		Event lab_event = null;
		String lab_name;
		boolean[] lab_meeting_days;
		Date lab_start_time, lab_end_time;
		Location lab_location;
		
		if (tokens.get(INDEX_LAB_MEETING_DAYS).length() > 0 && tokens.get(INDEX_LAB_START_TIME).length() > 0 && tokens.get(INDEX_LAB_END_TIME).length() > 0 && tokens.get(INDEX_LAB_BUILDING).length() > 0 && tokens.get(INDEX_LAB_ROOM).length() > 0) {
			name = name + " (Lab)";
			meeting_days = tokens.get(INDEX_LAB_MEETING_DAYS);
			start_time = Integer.parseInt(Utilities.time_to_24h(tokens.get(INDEX_LAB_START_TIME)));
			end_time = Integer.parseInt(Utilities.time_to_24h(tokens.get(INDEX_LAB_END_TIME)));
			building = tokens.get(INDEX_LAB_BUILDING);
			room = tokens.get(INDEX_LAB_ROOM);
			capacity = Integer.parseInt(Utilities.regex_replace(tokens.get(INDEX_LAB_ROOM_CAPACITY), "[^\\d]", ""));
			
			lab_name = class_name + " (Lab)";
			lab_start_time = Utilities.get_date(start_time);
			lab_end_time = Utilities.get_date(end_time);
			
			String lab_room = tokens.get(INDEX_LAB_ROOM);
			if (building.equalsIgnoreCase(Constants.GDC) && lab_room.length() < MAX_ROOM_LENGTH) {
				lab_room = pad_trailing_zeroes(lab_room, MAX_ROOM_LENGTH);
			}
			
			lab_location = new Location(building + " " + lab_room);
						
			if (lab_start_time != null && lab_end_time != null) {
				ArrayList<Details> temp;
				Details details = new Details(capacity, lab_name, meeting_days, start_time, end_time);
				if ((temp = info.get(lab_location)) == null) {
					temp = new ArrayList<Details>();
				}
				temp.add(details);
				info.put(lab_location, temp);

			}
		}

	}
	
	private static String pad_trailing_zeroes(String str, int final_len) {
		if (str == null) {
			throw new IllegalArgumentException();
		}
		if (final_len <= str.length()) {
			return str;
		}
		
		StringBuilder out = new StringBuilder(str.length() * 3);
		out.append(str);
		for (int i = str.length(); i <= final_len; i++) {
			out.append("0");
		}
		
		return (out.toString());
	}
	
	// "building string, room string, capacity integer, name string, meeting_days string, start_time integer, end_time integer"
	private static void insert_all() {
		try {
			statement.executeUpdate("drop table if exists " + db_name);
			statement.executeUpdate("create table " + db_name + " (" + DB_FORMAT + ")");
		}
		catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
		Location curr_loc;
		ArrayList<Details> curr_details;
		String curr_building, curr_room;
		Details curr_detail;
		for (Map.Entry<Location, ArrayList<Details>> entry : info.entrySet()) {
			curr_loc = entry.getKey();
			curr_details = entry.getValue();
			
			curr_building = curr_loc.get_building();
			curr_room = curr_loc.get_room();
			for (int i = 0; i < curr_details.size(); i++) {
				curr_detail = curr_details.get(i);
				insert_into_db(curr_building, curr_room, curr_detail.capacity, curr_detail.name, curr_detail.meeting_days, curr_detail.start_time, curr_detail.end_time);
			}
		}
	}
	
	private static void insert_into_db(String building, String room, Integer capacity, String name, String meeting_days, Integer start_time, Integer end_time) {
		try {
			String to_insert = "'" + building + "', '" + room + "', " + capacity + ", '" + name + "', '" + meeting_days + "', " + start_time + ", " + end_time;
			statement.executeUpdate("INSERT INTO " + db_name + " VALUES(" + to_insert + ")");
		}
		catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	// e.g. "master_course_schedules_f14.db"
	public static void read_course_schedule_from_db(String db_file_name) {
		int subfolder_index = db_file_name.lastIndexOf("/");
		int file_ext_dot_index = db_file_name.indexOf(".");
		if (file_ext_dot_index >= 0 && file_ext_dot_index < db_file_name.length()) {
			if (subfolder_index == -1) {
				db_name = db_file_name.substring(0, file_ext_dot_index);
			}
			else {
				db_name = db_file_name.substring(subfolder_index + 1, file_ext_dot_index);
			}
		}
		else {
			db_name = db_file_name;
		}
		
		establish_cxn();
		
		try {
			ResultSet rs = statement.executeQuery("select * from " + db_name);
			
			while (rs.next()) {
				System.out.printf("%s %s, %d, %s, %s, %d, %d\n", rs.getString("building"), rs.getString("room"), rs.getInt("capacity"), rs.getString("name"), rs.getString("meeting_days"), rs.getInt("start_time"), rs.getInt("end_time"));
			}
		}
		catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
		close_cxn();
	}
	
	// e.g. "course_schedules/master_course_schedules_f14.csv"
	public static void generate(String db_file_name) {
		if (db_file_name == null || db_file_name.length() <= 0) {
			throw new IllegalArgumentException("Invalid String argument, RoomList constructor.");
		}
		
		info = new TreeMap<Location, ArrayList<Details>>();
		
		int subfolder_index = db_file_name.lastIndexOf("/");
		int file_ext_dot_index = db_file_name.indexOf(".");
		if (file_ext_dot_index >= 0 && file_ext_dot_index < db_file_name.length()) {
			if (subfolder_index == -1) {
				db_name = db_file_name.substring(0, file_ext_dot_index);
			}
			else {
				db_name = db_file_name.substring(subfolder_index + 1, file_ext_dot_index);
			}
		}
		else {
			db_name = db_file_name;
		}
		
		int raw_index = db_name.indexOf("_raw");
		if (raw_index != -1) {
			db_name = db_name.substring(0, raw_index);
		}
		
		read_course_schedules(db_file_name);
		establish_cxn();
		insert_all();
		close_cxn();
	}
	
	private static class Details {
		
		public Integer capacity;
		public String name;
		public String meeting_days;
		public Integer start_time;
		public Integer end_time;
		
		public Details(Integer capacity, String name, String meeting_days, Integer start_time, Integer end_time) {
			this.capacity = capacity;
			this.name = name;
			this.meeting_days = meeting_days;
			this.start_time = start_time;
			this.end_time = end_time;
		}
	}
	
//	public static void main() {
//		generate("course_schedules/master_course_schedule_f14.csv");
//		generate("course_schedules/master_course_schedule_s15.csv");
//	}
	
}

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class BuildingList {

	private Map<String, Building> buildings;
	
	protected BuildingList() {
		this.buildings = new TreeMap<String, Building>();
	}
	
	protected BuildingList(String[] building_names) {
		if (building_names == null) {
			throw new IllegalArgumentException();
		}
		
		this.buildings = new TreeMap<String, Building>();
		for (int i = 0; i < building_names.length; i++) {
			this.put(building_names[i], new Building(building_names[i]));
		}
	}
	
	protected boolean contains_building(String name) {
		if (this.get_building(name) == null) {
			return false;
		}
		return true;
	}
		
	protected Building get_building(String name) {
		if (name == null || name.length() <= 0) {
			throw new IllegalArgumentException();
		}
		
		return (this.buildings.get(name.toUpperCase()));
	}
	
	protected Iterator<Map.Entry<String, Building>> get_iterator() {
		return this.buildings.entrySet().iterator();
	}
	
	protected int get_size() {
		return this.buildings.size();
	}
	
	protected void populate(String db_file_name) {
		if (db_file_name == null || db_file_name.length() <= 0) {
			throw new IllegalArgumentException("Invalid db file specified.");
		}
	}
	
	protected boolean put(String name, Building building) {
		if (name == null || name.length() <= 0 || building == null) {
//			return false;
			throw new IllegalArgumentException();
		}
		
		this.buildings.put(name, building);
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder out = new StringBuilder(1000);
		
		for (Building building : this.buildings.values()) {
			out.append(building.toString() + "\n");
		}
		
		return (out.toString());
	}
	
	private static class Database {
		
		private static final String		BUILDING		=	"building";
		private static final String		ROOM			=	"room";
		private static final String		CAPACITY		=	"capacity";
		private static final String		NAME			=	"name";
		private static final String		MEETING_DAYS	=	"meeting_days";
		private static final String		START_TIME		=	"start_time";
		private static final String		END_TIME		=	"end_time";
		
		private static String db_name = null;
		private static Connection connection = null;
		private static Statement statement = null;
		
		protected static BuildingList populate(BuildingList list, String db_file_name) {
			if (list == null || db_file_name == null || db_file_name.length() <= 0) {
				throw new IllegalArgumentException();
			}
						
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
						
			/*
			 *	if curr building str is same as prev building str
			 *		if curr room str is same as prev room str
			 *			add all details to prev room
			 *		else
			 *			create new Room
			 *			add this details to new Room
			 *			update prev room
			 * 	else
			 * 		- create new Building
			 * 		- create new Room
			 * 		- add new Room to new Building
			 * 		- add new Building to list
			 * 		- update prev building/room and curr building/room
			 */
			try {
				ResultSet rs = statement.executeQuery("select * from " + db_name);
				
//				Building prev_building = null;
//				Room prev_room = null;
//				Building curr_building = null;
//				Room curr_room = null;
//				String prev_building_str = null;
//				String prev_room_str = null;
//				String curr_building_str = null;
//				String curr_room_str = null;
//				while (rs.next()) {
//					curr_building_str = rs.getString(BUILDING);
//					curr_room_str = rs.getString(ROOM);
//					
//					if (prev_building_str == null) {
//						prev_building_str = curr_building_str;
//						prev_building = new Building(prev_building_str);
//						curr_building = prev_building;
//					}
//					if (prev_room_str == null) {
//						prev_room_str = curr_room_str;
//						
//					}
//					
//					if (!curr_room.equals(prev_room)) {
//						
//					}
//					else {
//						
//					}
//					
//					
//				}
			}
			catch (SQLException e) {
				throw new RuntimeException(e);
			}
			
			close_cxn();
			
			return list;
		}
		
		private static void establish_cxn() {
			
			// https://bitbucket.org/xerial/sqlite-jdbc
			try {
				Class.forName("org.sqlite.JDBC");
				connection = DriverManager.getConnection("jdbc:sqlite:" + db_name + ".db");
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
		
	}

}

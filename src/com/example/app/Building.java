package com.example.app;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import android.content.Context;

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
		
		this.name = name.toUpperCase(Locale.US);
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
	
	protected static final Building get_instance(Context context, String building_name, String db_file_name) {
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
			out.populate(context, db_file_name);
			
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
			out.populate(context, db_file_name);
			
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
	
	private void populate(Context context, String db_file_name) {
		if (context == null || db_file_name == null || db_file_name.length() <= 0) {
			throw new IllegalArgumentException();
		}
		
		CourseScheduleDatabase db = new CourseScheduleDatabase(context, db_file_name);
		this.rooms = db.get_courses(this.name, db_file_name);
		
//		System.out.println(this.rooms.toString());
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
			
}


package com.example.app;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/*
 * A class to chain multiple Room objects together.
 * 
 * RoomLists cannot be modified and will always be
 * identical, even if Constants.java is modified.
 */

final class RoomList {
	
	private Map<Location, Room> list;
	
	/**
	 * Default constructor.
	 */
	protected RoomList() {
		this.list = new HashMap<Location, Room>(Constants.VALID_GDC_ROOMS.length * 2);
		initialise();
	}
	
	/**
	 * Initialise this RoomList's backing Map. Populate it with
	 * the rooms available in GDC.
	 */
	private void initialise() {
		String[] room_types = Constants.VALID_GDC_ROOMS_TYPES;
		int[] room_capacities = Constants.VALID_GDC_ROOMS_CAPACITIES;
		boolean[] room_powers = Constants.VALID_GDC_ROOMS_POWERS;
		String gdc_str = Constants.GDC + " ";
		Room room;
		for (int i = 0; i < Constants.VALID_GDC_ROOMS.length; i++) {
			room = new Room(new Location(gdc_str + Constants.VALID_GDC_ROOMS[i]), room_types[i], room_capacities[i], room_powers[i]);
			this.add(room);
		}
	}

	/**
	 * @return The size of this RoomList's backing Map.
	 */
	protected int get_size() {
		return this.list.size();
	}
	
	/**
	 * @param room
	 * @return True if room was successfully added to this
	 * 		   RoomList's backing Map, false otherwise.
	 */
	private boolean add(Room room) {
		if (room == null) {
			return false;
		}
		
		Location this_location = room.get_location();
		
		// disallow multiples in backing Map
		if (this.list.get(this_location) != null) {
			return false;
		}
		
		this.list.put(this_location, room);		
		return true;
	}
	
	/**
	 * @param location
	 * @return Null if location is null or could not be found
	 * 		   in this RoomList's backing Map; the Map's value
	 * 		   of location otherwise.
	 */
	protected Room get_room(Location location) {
		if (location == null) {
			return null;
		}
		return (this.list.get(location));
	}
	
	/**
	 * @return An iterator over this RoomList's backing Map.
	 */
	protected Iterator<Map.Entry<Location, Room>> get_iterator() {
		return (this.list.entrySet().iterator());
	}

	/**
	 * @return This RoomList's backing Map stuffed into
	 * 		   a TreeMap (which is sorted).
	 */
	private Map<Location, Room> get_sorted_map() {
		if (this.list == null) {
			throw new IllegalArgumentException();
		}
		Map<Location, Room> out = new TreeMap<Location, Room>(this.list);
		return out;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder out = new StringBuilder();
		
		Map<Location, Room> temp = this.get_sorted_map();
		
		for (Map.Entry<Location, Room> entry : temp.entrySet()) {
			out.append(entry.getValue().toString() + "\n\n");
		}
		
		return (out.toString());
	}

}

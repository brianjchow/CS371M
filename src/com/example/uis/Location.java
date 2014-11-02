package com.example.uis;

public class Location implements Comparable<Location> {

	private String building;
	private String room;

	/**
	 * @param building_room ( [building code] [space] [room number] )
	 * 
	 * Default constructor.
	 */
	protected Location(String building_room) {
		if (building_room == null) {
			throw new IllegalArgumentException("Error: location String cannot be null, Location constructor");
		}

		String[] temp = building_room.split("\\s+");
		
		// if no location specified, set default dummy location
		if (temp.length < 2) {
			this.building = Constants.GDC;
			this.room = Constants.DEFAULT_GDC_LOCATION;
			return;
		}
		this.building = temp[0];
		this.room = temp[1];
	}

	/**
	 * @param building
	 * @param room
	 * 
	 * Allows "illegal"/nonexistent combinations of building codes
	 * and room numbers in the arguments; they will be discarded
	 * during the search phase.
	 */
	protected Location(String building, String room) {
		if (building == null) {
			building = Constants.GDC;
		}
		if (room == null) {
			room = Constants.DEFAULT_GDC_LOCATION;
		}

		this.building = building;
		this.room = room;
	}

	/**
	 * @return This Location's building code.
	 */
	protected String get_building() {
		return this.building;
	}

	/**
	 * @return This Location's room number.
	 */
	protected String get_room() {
		return this.room;
	}

	/**
	 * @param building
	 */
	protected boolean set_building(String building) {
		if (building == null) {
//			return false;
			throw new IllegalArgumentException("Error: argument cannot be null, set_building()");
		}

		this.building = building;
		return true;
	}

	/**
	 * @param room
	 */
	protected boolean set_room(String room) {
		if (room == null) {
//			return false;
			throw new IllegalArgumentException("Error: argument cannot be null, set_room()");
		}

		this.room = room;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	protected Location clone() {
		return (new Location(this.toString()));
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Location other) {
		int compare_result = this.toString().compareTo(other.toString());
		
		if (compare_result < 0) {
			return -1;
		}
		else if (compare_result == 0) {
			return 0;
		}
		else {
			return 1;
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if (!(other instanceof Location)) {
			return false;
		}
		
		Location other_location = (Location) other;
		if (!this.get_building().equals(other_location.get_building()) ||
			!this.get_room().equals(other_location.get_room())) {
			return false;
		}
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return (Utilities.stringHashCode(this.toString()));
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if (this.building == null || this.room == null) {
			throw new IllegalStateException("Error: one or more instance variables is wrong, Location.toString()");
		}
		return (this.get_building() + " " + this.get_room());
	}
}


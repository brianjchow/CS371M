package com.example.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class EventList {

	private List<Event> list;
	
	/**
	 * Default constructor.
	 */
	protected EventList() {
		this.list = new ArrayList<Event>(100);
	}
	
	/**
	 * @param strings
	 */
	protected EventList(List<HashMap<String, String>> strings) {
		EventList temp = new EventList();
		if (!temp.add(strings)) {
			throw new IllegalStateException("Error adding List of HashMap of Strings to EventList, EventList constructor");
		}
		
		this.list = temp.list;
	}

	/**
	 * @param eo
	 * @return True if eo was added to this EventList's backing list, false otherwise.
	 */
	protected boolean add(Event eo) {
		if (eo == null) {
			return false;
		}
		
		this.list.add(eo);
		return true;
	}
	
	/**
	 * @param eolist
	 * @return True if eolist was added/appended to this EventList's backing list, false otherwise.
	 */
	protected boolean add(EventList eolist) {
		if (eolist == null) {
			return false;
		}
		
		EventList original_clone = this.clone();
		EventList eo_clone = eolist.clone();
		
		for (Event event : eolist.list) {
			if (!this.add(event)) {
				this.list = original_clone.list;		// fail-safe? i think
				eolist = eo_clone;
				return false;
			}
		}
		return true;
	}
	
	/**
	 * @param strings
	 * @return True if strings was added/appended to this EventList's backing list, false otherwise.
	 */
	protected boolean add(List<HashMap<String, String>> strings) {
		if (strings == null) {
			return false;
		}
		
		for (int i = 0; i < strings.size(); i++) {
			HashMap<String, String> curr_event = strings.get(i);
			String event_name, start_date, end_date, location;
			
			// don't add if already present
			if ((event_name = curr_event.get(Constants.EVENT_NAME)) == null ||
				(start_date = curr_event.get(Constants.START_DATE)) == null) {
				continue;
			}
			
			// set dummy location if not specified in feed
			if ((location = curr_event.get(Constants.LOCATION)) == null) {
				location = Constants.GDC + " " +  Constants.DEFAULT_GDC_LOCATION;
			}
			
			// set default duration of Event as 90 minutes if none specified
			if ((end_date = curr_event.get(Constants.END_DATE)) == null) {
				this.list.add(new Event(event_name, start_date, null, location));
			}
			else {
				this.list.add(new Event(event_name, start_date, end_date, location));
			}
			
		}
		
		return true;
	}
	
	/**
	 * @param index
	 * @return The Event at index in this EventList's backing list.
	 */
	protected Event get_event(int index) {
		if (index < 0 || index >= this.list.size()) {
			throw new IllegalArgumentException("Error: index out of bounds (max size " + this.list.size() + "), get_event()");
		}
		return this.list.get(index);
	}
	
	/**
	 * @return An Iterator for this EventList's backing list.
	 */
	protected Iterator<Event> get_iterator() {
		return (this.list.iterator());
	}

	/**
	 * @return The size of this EventList's backing list.
	 */
	protected int get_size() {
		return this.list.size();
	}
	
	/**
	 * @param sort_ascending
	 * Sorts this EventList's backing list by end date.
	 */
	protected void sort_by_end_date(boolean sort_ascending) {
		if (this.list == null) {
			throw new IllegalStateException("Error: EventList's backing list cannot be null, sort_by_end_date()");
		}
		else if (this.list.size() == 0) {
			return;
		}
		
		final boolean ascending = sort_ascending;
				
		Collections.sort(list, new Comparator<Event>() {
			
			@Override
			public int compare(Event lhs, Event rhs) {
				Date lhs_date = lhs.get_end_date();
				Date rhs_date = rhs.get_end_date();
				
				if (lhs_date.getTime() < rhs_date.getTime()) {
					if (ascending) {
						return -1;
					}
					return 1;
				}
				else if (lhs_date.getTime() == rhs_date.getTime()) {
					return 0;
				}
				else {
					if (ascending) {
						return 1;
					}
					return -1;
				}
			}
		});
		
	}
	
	/**
	 * @param sort_ascending
	 * Sorts this EventList's backing list by event name (ASCII-betical).
	 */
	protected void sort_by_event_name(boolean sort_ascending) {
		if (list == null) {
			throw new IllegalStateException("Error: EventList's backing list cannot be null, sort_by_event_name()");
		}
		else if (list.size() == 0) {
			return;
		}
		
		final boolean ascending = sort_ascending;
		
		Collections.sort(list, new Comparator<Event>() {
			
			@Override
			public int compare(Event lhs, Event rhs) {
				String lhs_event_name = lhs.get_event_name();
				String rhs_event_name = rhs.get_event_name();
				int compare_result = lhs_event_name.compareTo(rhs_event_name);
				
				if (compare_result < 0) {
					if (ascending) {
						return -1;
					}
					return 1;
				}
				else if (compare_result == 0) {
					return 0;
				}
				else {
					if (ascending) {
						return 1;
					}
					return -1;
				}
			}
		});
		
	}

	/**
	 * @param sort_ascending
	 * Sorts this EventList's backing list by location (ASCII-betical).
	 */
	protected void sort_by_location(boolean sort_ascending) {
		if (this.list == null) {
			throw new IllegalStateException("Error: EventList's backing list cannot be null, sort_by_location()");
		}
		else if (this.list.size() == 0) {
			return;
		}
		
		final boolean ascending = sort_ascending;
		
		Collections.sort(this.list, new Comparator<Event>() {
			
			@Override
			public int compare(Event lhs, Event rhs) {
				String lhs_location = lhs.get_location().toString();
				String rhs_location = rhs.get_location().toString();
				int compare_result = lhs_location.compareTo(rhs_location);
				
				if (compare_result < 0) {
					if (ascending) {
						return -1;
					}
					return 1;
				}
				else if (compare_result == 0) {
					return 0;
				}
				else {
					if (ascending) {
						return 1;
					}
					return -1;
				}
			}
		});
				
	}
	
	/**
	 * @param sort_ascending
	 * Sorts this EventList's backing list by start date.
	 */
	protected void sort_by_start_date(boolean sort_ascending) {
		if (this.list == null) {
			throw new IllegalStateException("Error: EventList's backing list cannot be null, sort_by_start_date()");
		}
		else if (this.list.size() == 0) {
			return;
		}
		
		final boolean ascending = sort_ascending;
				
		Collections.sort(list, new Comparator<Event>() {
			
			@Override
			public int compare(Event lhs, Event rhs) {
				Date lhs_date = lhs.get_start_date();
				Date rhs_date = rhs.get_start_date();
				
				if (lhs_date.getTime() < rhs_date.getTime()) {
					if (ascending) {
						return -1;
					}
					return 1;
				}
				else if (lhs_date.getTime() == rhs_date.getTime()) {
					return 0;
				}
				else {
					if (ascending) {
						return 1;
					}
					return -1;
				}
			}
		});
		
	}
	
//	protected Comparator<Event> start_date_comparator = new Comparator<Event>() {
//		public int compare(Event lhs, Event rhs) {
//			return -1;
//		}
//	};
	
	protected int binary_search(Event search_for) {
		if (search_for == null) {
			throw new IllegalArgumentException();
		}
		else if (this.list == null) {
			throw new IllegalStateException();
		}
		return Collections.binarySearch(this.list, search_for);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	protected EventList clone() {
		EventList out = new EventList();
		
		for (Event event : this.list) {
			out.add(event.clone());
		}
		
		return out;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof EventList)) {
			return false;
		}
		
		EventList other_eo = (EventList) other;
		int this_size = this.list.size();
		if (this_size !=  other_eo.get_size()) {
			return false;
		}
		
		for (int i = 0; i < this_size; i++) {
			if (!this.list.get(i).equals(other_eo.list.get(i))) {
				return false;
			}
		}
		
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if (this.list == null) {
			throw new IllegalStateException("Error: EventList's backing list cannot be null, toString()");
		}
		
		StringBuilder out = new StringBuilder();
		if (list.size() == 0) {
			return (out.toString());
		}
		
		int i = 0;
		for (; i < this.list.size() - 1; i++) {
			out.append(this.list.get(i).toString() + "\n\n");
		}
		out.append(this.list.get(i).toString() + "\n");

		return (out.toString());
			
	}

}		// end of file



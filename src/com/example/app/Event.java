package com.example.app;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.google.common.collect.ComparisonChain;

public class Event implements Comparable<Event> {
	
	private String event_name;
	private Date start_date;
	private Date end_date;
	private Location location;

	/**
	 * @param event_name
	 * @param start_date
	 * @param end_date
	 * @param location
	 * 
	 * Default constructor.
	 * 
	 * end_date may be null; if so, this Event's duration
	 * will be automatically set to 90 minutes.
	 */
	protected Event(String event_name, String start_date, String end_date, String location) {
		if (event_name == null || start_date == null || location == null) {
			throw new IllegalArgumentException("Error: one or more arguments is null, Event constructor");
		}
		
		this.event_name = event_name;
		this.start_date = to_date(start_date);
		this.location = new Location(location);
		
		if (end_date == null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(this.start_date);
			calendar.add(Calendar.MINUTE, Constants.DEFAULT_EVENT_DURATION);
			this.end_date = calendar.getTime();
		}
		else {
			this.end_date = to_date(end_date);
		}
	}
	
	/**
	 * @param event_name
	 * @param start_date
	 * @param end_date
	 * @param location
	 */
	protected Event(String event_name, Date start_date, Date end_date, String location) {
		this(event_name, start_date, end_date, new Location(location));
	}
	
	protected Event(String event_name, Date start_date, Date end_date, Location location) {
		if (event_name == null || start_date == null || location == null) {
			throw new IllegalArgumentException("Error: one or more arguments is null, Event constructor");
		}
		
		this.event_name = event_name;
		this.start_date = start_date;
		this.location = location;
		
		if (end_date == null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(this.start_date);
			calendar.add(Calendar.MINUTE, Constants.DEFAULT_EVENT_DURATION);
			this.end_date = calendar.getTime();
		}
		else {
			this.end_date = end_date;
		}
	}

	/**
	 * @return This Event's ending date.
	 */
	protected Date get_end_date() {
		return (this.end_date);
	}

	/**
	 * @return This Event's starting date.
	 */
	protected String get_event_date() {
		DateFormat format = new SimpleDateFormat(Constants.US_DATE_NO_TIME_FORMAT, Locale.ENGLISH);		// MM dd yyyy
		return (format.format(this.start_date));
	}
		
	/**
	 * @return The day of the week this Event occurs on (Monday, Thursday, etc).
	 */
	protected String get_event_day_of_week() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(this.start_date);
		return (Constants.DAYS_OF_WEEK_LONG[calendar.get(Calendar.DAY_OF_WEEK) - 1]);
	}
		
	/**
	 * @return This Event's name.
	 */
	protected String get_event_name() {
		return (this.event_name);
	}

	/**
	 * @return A String representing this Event's starting time, expressed in HH:mm (14:30, 19:00, etc).
	 */
	protected String get_event_time() {
		DateFormat format = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
		return (format.format(this.start_date));
	}

	/**
	 * @return This Event's Location.
	 */
	protected Location get_location() {
		return (this.location);
	}

	/**
	 * @return This Event's starting date.
	 */
	protected Date get_start_date() {
		return (this.start_date);
	}
		
	/**
	 * @param event_name
	 */
	protected void set_event_name(String event_name) {
		if (event_name == null) {
			throw new IllegalArgumentException("Error: event name cannot be null, set_event_name()");
		}
		
		this.event_name = event_name;
	}
	
	/**
	 * @param end_date
	 */
	protected void set_end_date(String end_date) {
		if (end_date == null) {
			throw new IllegalArgumentException("Error: end date cannot be null, set_end_date()");
		}
		
		this.end_date = to_date(end_date);
	}
	
	/**
	 * @param location
	 */
	protected void set_location(String location) {
		if (location == null) {
			throw new IllegalArgumentException("Error: event location cannot be null, set_location()");
		}
		
		String[] temp = location.split("\\s+");
		if (temp.length != 2) {
			throw new IllegalArgumentException();
		}
		
		this.location.set_building(temp[0]);
		this.location.set_room(temp[1]);
	}
		
	/**
	 * @param start_date
	 */
	protected void set_start_date(String start_date) {
		if (start_date == null) {
			throw new IllegalArgumentException("Error: event start date cannot be null, set_start_date()");
		}
		
		this.start_date = to_date(start_date);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(this.start_date);
		calendar.add(Calendar.MINUTE, Constants.DEFAULT_EVENT_DURATION);
		this.end_date = calendar.getTime();
	}

	// Mon 20 Oct 2014 1700
	/**
	 * @param date_time
	 * @return A Date representing the information culled
	 * 		   from the CSV feeds.
	 */
	private Date to_date(String date_time) {
		if (date_time == null) {
			throw new IllegalArgumentException("Error: String representing date of event cannot be null, to_date()");
		}
		
		DateFormat format = new SimpleDateFormat(Constants.UTCS_CSV_FEED_FORMAT, Locale.ENGLISH);
		Date date = null;
		
		try {
			date = format.parse(date_time);
		}
		catch (ParseException e) {
			System.out.printf("Error parsing date/time String \"%s\", to_date()", date_time);
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
		return (date);
	}
		
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	protected Event clone() {
		DateFormat format = new SimpleDateFormat(Constants.UTCS_CSV_FEED_FORMAT, Locale.ENGLISH);
		String start_date_copy = format.format(this.get_start_date());
		String end_date_copy = format.format(this.get_end_date());
		Event copy = new Event(this.event_name, start_date_copy, end_date_copy, this.get_location().toString());
		return copy;
	}
	
	public int compareTo(Event other) {
		int result = ComparisonChain.start()
			.compare(this.event_name, other.event_name)
			.compare(this.start_date, other.start_date)
			.compare(this.location, other.location)
			.compare(this.end_date, other.end_date)
			.result();
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		else if (!(other instanceof Event)) {
			return false;
		}

		Event other_eo = (Event) other;

		if (this.event_name.equals(other_eo.event_name) &&
			Utilities.dates_are_equal(this.start_date, other_eo.start_date) &&
			Utilities.dates_are_equal(this.end_date, other_eo.end_date) &&
			this.get_location().equals(other_eo.get_location())) {
			return true;
		}

		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
//		long result = Utilities.stringHashCode(this.event_name) * Utilities.dateHashCode(this.start_date) *
//					  Utilities.dateHashCode(this.end_date) * this.location.hashCode() * 37;
		
//		long result = Utilities.stringHashCode(this.event_name) * this.location.hashCode();

//		return ((int) result);
		
		int start_time = Integer.parseInt(Utilities.get_time(this.start_date).replaceAll(":", ""));
		return (Utilities.stringHashCode(this.event_name) * start_time * this.location.hashCode());
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder out = new StringBuilder();
		out.append(this.get_event_name() + "\n");
		out.append(this.get_start_date().toString() + "\n");
		out.append(this.get_end_date().toString() + "\n");
		out.append(this.get_location().toString());
		return (out.toString());
	}

}


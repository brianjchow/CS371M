import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

final class EventObject {
	
	private String event_name;
	private Date start_date;
	private Date end_date;
	private Location location;

	protected EventObject(String event_name, String start_date, String end_date, String location) {
		if (event_name == null || start_date == null || location == null) {
			throw new IllegalArgumentException();
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
	
	protected EventObject(String event_name, Date start_date, Date end_date, String location) {
		if (event_name == null || start_date == null || location == null) {
			throw new IllegalArgumentException();
		}
		
		this.event_name = event_name;
		this.start_date = start_date;
		this.location = new Location(location);
		
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
	
	// Mon 20 Oct 2014 1700
	private Date to_date(String date_time) {
		if (date_time == null) {
			throw new IllegalArgumentException();
		}
		
		DateFormat temp = new SimpleDateFormat("EEE dd MMM yyyy kkmm", Locale.ENGLISH);
		Date date = null;
		
		try {
			date = temp.parse(date_time);
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		
		return (date);
	}
	
	protected String get_event_name() {
		return (this.event_name);
	}
	
	protected void set_event_name(String event_name) {
		if (event_name == null) {
			throw new IllegalArgumentException();
		}
		
		this.event_name = event_name;
	}
	
	protected String get_event_day_of_week() {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(this.get_start_date());
		return (Constants.DAYS_OF_WEEK_LONG[calendar.get(Calendar.DAY_OF_WEEK) - 1]);
	}
	
	protected String get_event_date() {
		DateFormat format = new SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH);
		return (format.format(this.get_start_date()));
	}
	
	protected String get_event_time() {
		DateFormat format = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
		return (format.format(this.get_start_date()));
	}

	protected Date get_start_date() {
		return (this.start_date);
	}
	
	protected void set_start_date(String start_date) {
		if (start_date == null) {
			throw new IllegalArgumentException();
		}
		
		this.start_date = to_date(start_date);
	}
	
	protected Date get_end_date() {
		return (this.end_date);
	}
	
	protected void set_end_date(String end_date) {
		if (end_date == null) {
			throw new IllegalArgumentException();
		}
		
		this.end_date = to_date(end_date);
	}
	
	protected Location get_location() {
		return (this.location);
	}
	
	protected void set_location(String location) {
		if (location == null) {
			throw new IllegalArgumentException();
		}
		
		String[] temp = location.split("\\s+");
		if (temp.length != 2) {
			throw new IllegalArgumentException();
		}
		
		this.location.set_building(temp[0]);
		this.location.set_room(temp[1]);
	}
	
	protected EventObject clone() {
		DateFormat format = new SimpleDateFormat("EEE dd MMM yyyy kkmm", Locale.ENGLISH);
		String start_date_copy = format.format(this.get_start_date());
		String end_date_copy = format.format(this.get_end_date());
		EventObject copy = new EventObject(this.event_name, start_date_copy, end_date_copy, this.get_location().toString());
		return copy;
	}
	
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		else if (!(other instanceof EventObject)) {
			return false;
		}

		EventObject other_eo = (EventObject) other;
		
		Calendar this_cal_start = Calendar.getInstance();
		this_cal_start.setTime(this.start_date);
		Calendar this_cal_end = Calendar.getInstance();
		this_cal_end.setTime(this.end_date);
		Calendar other_cal_start = Calendar.getInstance();
		other_cal_start.setTime(other_eo.start_date);
		Calendar other_cal_end = Calendar.getInstance();
		other_cal_end.setTime(other_eo.end_date);
		
		if (this.event_name.equals(other_eo.event_name) &&
			this_cal_start.equals(other_cal_start) &&
			other_cal_start.equals(other_cal_start) &&
			this.get_location().equals(other_eo.get_location())) {
			return true;
		}
		
		return false;
	}
	
	public int hashCode() {
		return (Utilities.stringHashCode(this.event_name) * 37 * Utilities.stringHashCode(this.location.toString()));
	}
	
	public String toString() {
		StringBuilder out = new StringBuilder();
		out.append(this.get_event_name() + "\n");
		out.append(this.get_start_date().toString() + "\n");
		out.append(this.get_end_date().toString() + "\n");
		out.append(this.get_location().toString());
		return (out.toString());
	}

}

/*
	
	protected static List<EventObject> listToEventObjectArray(List<ArrayList<String>> strings) {
		if (strings == null) {
			throw new IllegalArgumentException();
		}
		
		int size = strings.size();
		if (size == 0) {
			return (new ArrayList<EventObject>());
		}
		
		List<EventObject> out = new ArrayList<EventObject>(size);
		
		for (int i = 0; i < size; i++) {
			List<String> curr_event = strings.get(i);
			int counter = curr_event.size() - 1;
			String location = curr_event.get(counter--);
			String start_date = curr_event.get(counter--);
			String event_name = "";
			for (int j = 0; j <= counter; j++) {
				event_name += curr_event.get(j);
			}
			
			out.add(new EventObject(event_name, start_date, null, location));
		}
				
		return out;
	}
	
*/


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.joda.time.DateTime;
import org.joda.time.Interval;

public class Utilities {
	
	protected static int get_hashmap_size(int size) {
		if (size < 0) {
			throw new IllegalArgumentException();
		}
		
		int out = (int) Math.ceil((double) size / Constants.DEFAULT_HASHMAP_LOAD_FACTOR);
		return out;
	}
	
	protected static boolean date_is_in_range(Date what, Date start, Date end) {
		if (what == null || start == null || end == null) {
			throw new IllegalArgumentException("Null argument.");
		}
		return (!what.before(start) && !what.after(end));
	}
	
	protected static boolean date_is_during_spring(Date date) {
		if (date == null) {
			throw new IllegalArgumentException("Null argument.");
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		int year = calendar.get(Calendar.YEAR);
		Date start = get_date(Constants.SPRING_START_MONTH, Constants.SPRING_START_DAY, year, 0);
		Date end = get_date(Constants.SPRING_END_MONTH, Constants.SPRING_END_DAY, year, 2359);
		
		boolean result = date_is_in_range(date, start, end);		
		return result;
	}
	
	protected static boolean date_is_during_summer(Date date) {
		if (date == null) {
			throw new IllegalArgumentException("Null argument.");
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		int year = calendar.get(Calendar.YEAR);
		Date start = get_date(Constants.SUMMER_START_MONTH, Constants.SUMMER_START_DAY, year, 0);
		Date end = get_date(Constants.SUMMER_END_MONTH, Constants.SUMMER_END_DAY, year, 2359);
		
		boolean result = date_is_in_range(date, start, end);		
		return result;
	}
	
	protected static boolean date_is_during_fall(Date date) {
		if (date == null) {
			throw new IllegalArgumentException("Null argument.");
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		int year = calendar.get(Calendar.YEAR);
		Date start = get_date(Constants.FALL_START_MONTH, Constants.FALL_START_DAY, year, 0);
		Date end = get_date(Constants.FALL_END_MONTH, Constants.FALL_END_DAY, year, 2359);
		
		boolean result = date_is_in_range(date, start, end);		
		return result;
	}

	protected static boolean dates_are_equal(Date date1, Date date2) {
		if (date1 == null || date2 == null) {
			throw new IllegalArgumentException();
		}
		
		Calendar date1_cal = Calendar.getInstance();
		date1_cal.setTime(date1);
		
		Calendar date2_cal = Calendar.getInstance();
		date2_cal.setTime(date2);
		
		boolean same = date1_cal.get(Calendar.YEAR) == date2_cal.get(Calendar.YEAR) &&
					   date1_cal.get(Calendar.DAY_OF_YEAR) == date2_cal.get(Calendar.DAY_OF_YEAR) &&
					   date1_cal.get(Calendar.HOUR_OF_DAY) == date2_cal.get(Calendar.HOUR_OF_DAY) &&
					   date1_cal.get(Calendar.MINUTE) == date2_cal.get(Calendar.MINUTE);
		
		return same;
	}
		
	protected static String get_time(Date date) {
		if (date == null) {
			throw new IllegalArgumentException();
		}
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		String out = "";
		out += pad_to_len_leading_zeroes(Integer.toString(calendar.get(Calendar.HOUR_OF_DAY)), 2) + ":" + pad_to_len_leading_zeroes(Integer.toString(calendar.get(Calendar.MINUTE)), 2);
		
		return out;
	}
	
	protected static boolean valid_day_of_week(int day_of_week) {
		if (day_of_week < Constants.SUNDAY || day_of_week > Constants.SATURDAY) {
			return false;
		}
		return true;
	}
	
	protected static Date get_date(int time) {
		if (time < Constants.MIN_TIME || time > Constants.MAX_TIME || time % 100 >= Constants.MINUTES_IN_HOUR) {
//System.out.println((time < Constants.MIN_TIME) + " " + (time > Constants.MAX_TIME) + " " +  (time % 100 >= Constants.MINUTES_IN_HOUR));
			return null;
		}

		Calendar calendar = Calendar.getInstance();
		
		String temp = Integer.toString(time);
		temp = Utilities.pad_to_len_leading_zeroes(temp, 4);
//System.out.println(temp.substring(0, 2) + " " + temp.substring(2, 4));		
		calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(temp.substring(0, 2)));
		calendar.set(Calendar.MINUTE, Integer.parseInt(temp.substring(2, 4)));
		calendar.set(Calendar.SECOND, 0);
//System.out.println("\nfweiojfwe\n");		
		return (calendar.getTime());
	}
	
	// http://grepcode.com/file/repository.grepcode.com/java/root/jdk/openjdk/8-b132/java/util/Date.java?av=f
	// adapted from standard JDK 8 hashCode() implementation (in case future updates change or bork the algorithm)
	protected static int dateHashCode(Date date) {
		if (date == null) {
			throw new IllegalArgumentException("Null date argument, Utilities.dateHashCode()");
		}
		long ht = date.getTime();
		return ((int) ht ^ (int) (ht >> 32));
	}
	
	protected static boolean time_schedules_overlap(Date start1, Date end1, Date start2, Date end2) {
		if (start1 == null || end1 == null || start2 == null || end2 == null) {
			return false;
		}
		
		int start_time1, end_time1, start_time2, end_time2;
		
		start_time1 = get_time_from_date(start1);
		end_time1 = get_time_from_date(end1);
		start_time2 = get_time_from_date(start2);
		end_time2 = get_time_from_date(end2);
		
		Date start_date1, end_date1, start_date2, end_date2;
		start_date1 = get_date(1, 1, 2014, start_time1);
		if (end_time1 < start_time1) {
			end_date1 = get_date(1, 2, 2014, end_time1);
		}
		else {
			end_date1 = get_date(1, 1, 2014, end_time1);
		}
		
		start_date2 = get_date(1, 1, 2014, start_time2);
		if (end_time2 < start_time2) {
			end_date2 = get_date(1, 2, 2014, end_time2);
		}
		else {
			end_date2 = get_date(1, 1, 2014, end_time2);
		}
		return (times_overlap(start_date1, end_date1, start_date2, end_date2));
		
//		return (start_time1 < end_time2 && start_time2 < end_time1);
	}
	
	protected static int get_time_from_date(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		String out = Integer.toString(calendar.get(Calendar.HOUR_OF_DAY));
		String temp = Integer.toString(calendar.get(Calendar.MINUTE));
		temp = pad_to_len_leading_zeroes(temp, 2);
		
		out += temp;
		
		return (Integer.parseInt(out));
	}
	
	// http://stackoverflow.com/questions/18938152/check-if-two-date-periods-overlap
	protected static boolean times_overlap(Date start1, Date end1, Date start2, Date end2) {
		if (start1 == null || end1 == null || start2 == null || end2 == null) {
			return false;
		}
		return (start1.before(end2) && start2.before(end1));
	}
	
//	protected static boolean times_overlap(Date start1, Date end1, Date start2, Date end2) {
//		if (start1 == null || end1 == null || start2 == null || end2 == null) {
//			throw new IllegalArgumentException("One or more arguments is null");
//		}
//		
//		Calendar calendar = Calendar.getInstance();
//		
//		calendar.setTime(start1);
//		DateTime start_one = new DateTime(calendar.getTime());
//		
//		calendar.setTime(end1);
//		DateTime end_one = new DateTime(calendar.getTime());
//		
//		calendar.setTime(start2);
//		DateTime start_two = new DateTime(calendar.getTime());
//		
//		calendar.setTime(end2);
//		DateTime end_two = new DateTime(calendar.getTime());
//		
//		Interval one = new Interval(start_one, end_one);
//		Interval two = new Interval(start_two, end_two);
//		
//		return (one.overlaps(two));
//	}
	
	protected static boolean occur_on_same_day(Date date1, Date date2) {
		if (date1 == null || date2 == null) {
			return false;
		}
		
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(date1);
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(date2);
		
		boolean result = cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
						 cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
		
		return result;
	}
	
	protected static boolean is_leap_year(int year) {
		if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
			return true;
		}
		return false;
	}

	protected static Date get_date(int month, int day, int year, int time) {
		if (month < 1 || month > 12) {
			return null;
		}
		else if (year < Constants.MIN_YEAR || year > Constants.MAX_YEAR) {
			return null;
		}
		else if (time <  Constants.MIN_TIME || time > Constants.MAX_TIME || time % 100 >= Constants.MINUTES_IN_HOUR) {
			return null;
		}
		
		int days_in_this_month = Constants.DAYS_IN_MONTH[month - 1];
		if (month == 2 && is_leap_year(year)) {
			days_in_this_month++;
		}
		if (day < 1 || day > days_in_this_month) {
			return null;
		}
		
		String month_str = Constants.MONTHS_SHORT[month - 1];
		String day_str = Integer.toString(day);
		String year_str = Integer.toString(year);
		String time_str = Integer.toString(time);

		if (day_str.length() == 1) {
			day_str = "0" + day_str;
		}
		if (time_str.length() < 4) {
			time_str = pad_to_len_leading_zeroes(time_str, 4);
		}
		
		String date_str = new StringBuilder((month_str.length() + day_str.length() + year_str.length() + time_str.length()) * 2).append(month_str + " " + day_str + " " + year_str + " " + time_str).toString();
		
		DateFormat date_format = new SimpleDateFormat(Constants.US_DATE_24H_TIME_FORMAT);
		Date date = null;
		try {
			date = date_format.parse(date_str);
		}
		catch (ParseException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
				
//System.out.println(month_str + " " + day_str + " " + year_str + " " + time_str);
		
		return date;
	}

	protected static Date get_date() {
		Calendar calendar = Calendar.getInstance();
		return (calendar.getTime());
	}
	
//	protected static Date get_date(int month, int day, int year) {
//		if (month < 1 || month > 12) {
//			return null;
//		}
//		else if (year < Constants.MIN_YEAR || year > Constants.MAX_YEAR) {
//			return null;
//		}
//		
//		int days_in_this_month = Constants.DAYS_IN_MONTH[month - 1];
//		if (month == 2 && is_leap_year(year)) {
//			days_in_this_month++;
//		}
//		if (day < 1 || day > days_in_this_month) {
//			return null;
//		}
//		
//		String month_str = Constants.MONTHS_SHORT[month - 1];
//		String day_str = Integer.toString(day);
//		String year_str = Integer.toString(year);
//
//		if (day_str.length() == 1) {
//			day_str = "0" + day_str;
//		}
//		
//		String date_str = new StringBuilder((month_str.length() + day_str.length() + year_str.length()) * 2).append(month_str + " " + day_str + " " + year_str).toString();
//		
//		DateFormat date_format = new SimpleDateFormat("MMM dd yyyy");
//		Date date = null;
//		try {
//			date = date_format.parse(date_str);
//		}
//		catch (ParseException e) {
//			e.printStackTrace();
//		}
//				
////System.out.println(month_str + " " + day_str + " " + year_str);
//				
//		return date;
//	}
	
//	public static void main(String args[]) {
//		Date date = get_date("10 3 14 a1");
//		if (date == null) {
//			System.out.println("fuck");
//		}
//		else {
//			System.out.println("FEJIOWEF");
//		}
//		
//		get_date(1, 3, 2014, 0);
//	}
	
	protected static String regex_replace(String str, String regex, String replace_with) {
		if (str == null || regex == null || replace_with == null) {
			throw new IllegalArgumentException();
		}
		else if (regex.length() == 0) {
			return str;
		}
		
		// http://www.coderanch.com/t/381432/java/java/regex-replace-substring
		try {
			Pattern regex_pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
			Matcher regex_matcher = regex_pattern.matcher(str);
			
			try {
				str = regex_matcher.replaceAll(replace_with);
			}
			catch (IllegalArgumentException e) {
				e.printStackTrace();	// syntax error in replacement text
				throw new RuntimeException(e);
			}
			catch (IndexOutOfBoundsException e) {
				e.printStackTrace();	// non-existent backreference used replacement text
				throw new RuntimeException(e);
			}
		}
		catch (PatternSyntaxException e) {
			e.printStackTrace();		// syntax error in regex
			throw new RuntimeException(e);
		}
		
		return str;		
	}

	// http://stackoverflow.com/questions/86780/is-the-contains-method-in-java-lang-string-case-sensitive/25379180#25379180
	protected static boolean containsIgnoreCase(String src, String what) {
		if (src == null || what == null) {
			throw new IllegalArgumentException();
		}
		
	    final int length = what.length();
	    if (length == 0)
	        return true; // Empty string is contained

	    final char firstLo = Character.toLowerCase(what.charAt(0));
	    final char firstUp = Character.toUpperCase(what.charAt(0));

	    for (int i = src.length() - length; i >= 0; i--) {
	        // Quick check before calling the more expensive regionMatches() method:
	        final char ch = src.charAt(i);
	        if (ch != firstLo && ch != firstUp)
	            continue;

	        if (src.regionMatches(true, i, what, 0, length))
	            return true;
	    }

	    return false;
	}
	
	// doesn't work with weird am/pm Strings, e.g. "aM or Pm"
	// does not check for extra/erroneous letters or digits
	protected static String time_to_24h(String time) {
		if (time == null || time.length() <= 0) {
			throw new IllegalArgumentException();
		}
		
		time = time.toLowerCase();
		time = time.replaceAll(":", "");
		time = time.replaceAll(" ", "");
		
		if (containsIgnoreCase(time, "noon")) {
			time = regex_replace(time, "(?i)noon", "pm");
//			System.out.println(time);
		}

		if ((containsIgnoreCase(time, "am") && (time.indexOf("am") == time.length() - 2 || time.indexOf("AM") == time.length() - 2)) ||
				(containsIgnoreCase(time, "a") && (time.indexOf("a") == time.length() - 1 || time.indexOf("A") == time.length() - 1))) {
			time = time.replaceAll("a", "");
			time = time.replaceAll("m", "");
//			time = regex_replace(time, "(?i)a", "");
//			time = regex_replace(time, "(?i)m", "");
			int temp = Integer.parseInt(time);		// negative times?
			if (temp >= 1200 && temp <= 1259) {
				time = Integer.toString(temp -= 1200);
				time = pad_to_len_leading_zeroes(time, 4);
			}
			
		}
		else if ((containsIgnoreCase(time, "pm") && (time.indexOf("pm") == time.length() - 2 || time.indexOf("PM") == time.length() - 2)) ||
				 (containsIgnoreCase(time, "p") && (time.indexOf("p") == time.length() - 1 || time.indexOf("P") == time.length() - 1))) {
			time = time.replaceAll("p", "");
			time = time.replaceAll("m", "");
//			time = regex_replace(time, "(?i)p", "");
//			time = regex_replace(time, "(?i)m", "");
			int temp = Integer.parseInt(time);		// negative times?
			if (temp >= 100 && temp <= 1159) {
				time = Integer.toString(temp += 1200);
			}
		}
		
		return time;
	}
	
	protected static String pad_to_len_leading_zeroes(String str, int final_len) {
		if (final_len < 0) {
			throw new IllegalArgumentException();
		}
		else if (final_len <= str.length()) {
			return str;
		}
		
		StringBuilder pad = new StringBuilder((str.length() + final_len) * 2);
		for (int i = str.length(); i < final_len; i++) {
			pad.append("0");
		}
		pad.append(str);
		
		return (pad.toString());
	}
	
//	does not check for extra/erroneous letters or digits
//	protected static String time_to_12h(String time) {
//		if (time == null || time.length() <= 0) {
//			throw new IllegalArgumentException();
//		}
//
//		time = time.replaceAll(":", "");
//		time = time.replaceAll(" ", "");
//		time = time.substring(0, 4);
//		
//		try {
//			Integer.parseInt(time);
//		}
//		catch (NumberFormatException e) {
//			e.printStackTrace();
//			throw new RuntimeException(e);
//		}
//
//		DateFormat format_1 = new SimpleDateFormat("hhmm");
//		Date date = null;
//		try {
//			date = format_1.parse(time);
//		}
//		catch (ParseException e) {
//			e.printStackTrace();
//			throw new RuntimeException(e);
//		}
//		
//		DateFormat format_2 = new SimpleDateFormat("h:mma");
//		
//		return format_2.format(date).toLowerCase();
//	}

	// http://hg.openjdk.java.net/jdk7u/jdk7u6/jdk/file/8c2c5d63a17e/src/share/classes/java/lang/String.java
	// adapted from standard JDK 7 hashCode() implementation (in case future updates change or bork the algorithm)
	public static int stringHashCode(String str) {
		if (str == null) {
			throw new IllegalArgumentException();
		}
		int hash = 0;
		int h = hash;
		char[] str_arr = str.toCharArray();
        if (h == 0 && str_arr.length > 0) {
            char val[] = str_arr;

            for (int i = 0; i < str_arr.length; i++) {
                h = 31 * h + val[i];
            }
            hash = h;
        }
        return h;
	}
	
//	int hash = 0;
//	int h = hash;
//	char[] str_arr = this.toString().toCharArray();
//    if (h == 0 && str_arr.length > 0) {
//        char val[] = str_arr;
//
//        for (int i = 0; i < str_arr.length; i++) {
//            h = 31 * h + val[i];
//        }
//        hash = h;
//    }
//    return h;
    			
}

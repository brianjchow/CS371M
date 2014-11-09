import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Utilities {
	
	protected static boolean valid_day_of_week(int day_of_week) {
		if (day_of_week < Constants.MONDAY || day_of_week > Constants.SUNDAY) {
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
	
	protected static boolean times_overlap(Date start1, Date end1, Date start2, Date end2) {
		if (start1 == null || end1 == null || start2 == null || end2 == null) {
			return false;
		}
		return (start1.before(end2) && start2.before(end1));
	}
	
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
	
	private static String process_month(String month) {
		int month_len = month.length();

		boolean month_in_digits = true;
		for (int i = 0; i < month_len; i++) {
			if (!Character.isDigit(month.charAt(i))) {
				month_in_digits = false;
			}
		}

		if (month_in_digits) {
			int month_val = Integer.parseInt(month);
			if ((month_len != 1 && month_len != 2) || (month_val < 1 || month_val > 12)) {
				return null;
			}
			month = Constants.MONTHS_LONG[Integer.parseInt(month) - 1];
		}
		else {
			if (month_len < 3 || month_len > 9) {		// "September" is longest month
				return null;
			}
			
			for (int i = 0; i < month_len; i++) {
				if (!Character.isLetter(month.charAt(i))) {
					return null;
				}
			}
			
			if (month_len == 3) {
				int fully_qualified_index = -1;
				for (int i = 0; i < Constants.MONTHS_SHORT.length; i++) {
					if (month.equalsIgnoreCase(Constants.MONTHS_SHORT[i])) {
						fully_qualified_index = i;
						break;
					}
				}
				if (fully_qualified_index == -1) {
					return null;
				}
				else {
					month = Constants.MONTHS_LONG[fully_qualified_index];
				}
			}
			else {
				int num_matches = 0;
				int fully_qualified_index = -1;
				for (int i = 0; i < Constants.MONTHS_LONG.length; i++) {
					if (containsIgnoreCase(Constants.MONTHS_LONG[i], month)) {
						num_matches++;
						fully_qualified_index = i;
					}
				}
				
				if (num_matches != 1) {
					return null;
				}
				else {
					month = Constants.MONTHS_LONG[fully_qualified_index];
				}
			}
		}

		return month;
	}
	
	private static String process_day(String day) {
		day = regex_replace(day, "[A-Za-z]+", "");
		
		int day_len = day.length();
		if (day_len != 1 && day_len != 2) {
			return null;
		}
		for (int i = 0; i < day_len; i++) {
			if (!Character.isDigit(day.charAt(i))) {
				return null;
			}
		}
		if (day_len == 1) {
			day = new StringBuilder().append("0").append(day).toString();
		}
		
		return day;
	}
	
	private static String process_year(String year) {
		year = regex_replace(year, "[A-Za-z]+", "");
		
		int year_len = year.length();
		if (year_len != 2 && year_len != 4) {
			return null;
		}
		for (int i = 0; i < year_len; i++) {
			if (!Character.isDigit(year.charAt(i))) {
				return null;
			}
		}
		if (year_len == 2) {
			year = new StringBuilder().append("20").append(year).toString();
		}
		
		return year;
	}
	
	private static String process_time(String time_str) {
		time_str = time_str.toLowerCase();
		int time_str_len = time_str.length();
		if (time_str_len < 1 || time_str_len > 6) {		// < "1" > "1230pm"
			return null;
		}
		else if (time_str_len == 1) {
			if (Character.isDigit(time_str.charAt(0))) {
				time_str = new StringBuilder().append("000").append(time_str).toString();
				return time_str;
			}
			return null;
//			else {
//				if (time_str.charAt(time_str_len - 1) == 'a' || time_str.charAt(time_str_len - 1) == 'p') {
//					time_str += "m";
//					time_str = time_to_24h(time_str);
////					query_split[3] = time_to_24h(time_str);
//					return time_str;
//				}
//				else {
//					return null;
//				}
//			}
		}
		else if (time_str.charAt(time_str_len - 1) == 'a' || time_str.charAt(time_str_len - 1) == 'p') {
			time_str += "m";
			time_str = time_to_24h(time_str);
		}
		else if (time_str_len == 2) {
			if (time_str.substring(time_str_len - 2, time_str_len).equalsIgnoreCase("am") || 
				 time_str.substring(time_str_len - 2, time_str_len).equalsIgnoreCase("pm")) {
				return null;
			}
//			else if () {		// 1 digit, 1 'a'/'p' #############################################################################################
//				
//			}
//			time_str = time_to_24h(time_str);
		}
		
		time_str = regex_replace(time_str, "[A-Za-z]+", "");
		time_str_len = time_str.length();
		if (time_str_len > 4 || Integer.parseInt(time_str) % 100 >= Constants.MINUTES_IN_HOUR) {
			return null;
		}
		else if (time_str_len < 4) {
			StringBuilder pad = new StringBuilder(8);
			for (int i = time_str_len; i < 4; i++) {
				pad.append("0");
			}
			time_str = pad.append(time_str).toString();
		}
		
		return time_str;
	}
	
	protected static boolean is_leap_year(int year) {
		if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
			return true;
		}
		return false;
	}
	
	// very slow; use only if necessary
	/*
	Accepts:
		- MMM[...] [d]d[,] [yy]yy HH[:]mm, e.g. "Oct 23 2014 1830"
		- [M]M [d]d[,] [yy]yy HH[:]mm, e.g. "4 8 2014 2330"
		- MMM[...] [d]d[,] [yy]yy hh[:]mma[a]
		- [M]M [d]d[,] [yy]yy hh[:]mma[a]
		- all of the above, but without a time field
	- a for am/pm marker
	- E for day name in week
	- u for day number in week (1 = Monday, ... , 7 = Sunday)
	- H for hour in day (0 - 23)
	- k for hour in day (1 - 24)
	- K for hour in am/pm (0 - 11)
	- h for hour in am/pm (1 - 12)
	- m for minute in hour
	- s for second in minute
	- S for millisecond
	
	http://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
	
	###################### NOT FULLY TESTED ######################
	
	*/
	protected static Date get_date(String query) {
		if (query == null || query.length() <= 0) {
			throw new IllegalArgumentException();
		}
		
		String regex = "[():,]*";
		query = regex_replace(query, regex, "");
		
		String[] query_split = query.split("\\s+");
		if (query_split.length != 3 && query_split.length != 4) {
			return null;
		}

		String time_str = "";
		if (query_split.length == 4) {
			time_str = query_split[3];
			time_str = process_time(time_str);
			if (time_str == null) {
				return null;
			}
			double time_double = Double.parseDouble(time_str);
			if (time_double < (double) Constants.MIN_TIME || time_double > (double) Constants.MAX_TIME) {
				return null;
			}
//			query_split[3] = time_str;
		}
		
		String year = query_split[2];
		year = process_year(year);
		if (year == null) {
			return null;
		}
		double year_double = Double.parseDouble(year);
		if (year_double < (double) Constants.MIN_YEAR || year_double > (double) Constants.MAX_YEAR) {
			return null;
		}
//		query_split[2] = year;

		String day = query_split[1];
		day = process_day(day);
//		query_split[1] = day;

		String month = query_split[0];
		month = process_month(month);
//		query_split[0] = month;
		
		if (month == null || day == null) {
			return null;
		}
		int month_index = -1;
		for (int i = 0; i < Constants.MONTHS_LONG.length; i++) {
			if (month.equalsIgnoreCase(Constants.MONTHS_LONG[i])) {
				month_index = i;
				break;
			}
		}

		if (month_index == -1) {
			return null;
		}
		
		int max_days_in_month = Constants.DAYS_IN_MONTH[month_index];
		int day_of_this_month = Integer.parseInt(day);
		if (month_index == 1 && is_leap_year(Integer.parseInt(year))) {
			max_days_in_month++;
		}
		if (day_of_this_month < 1 || day_of_this_month > max_days_in_month) {
			return null;
		}
		
		String date_str = new StringBuilder(50).append(month).append(" " + day).append(" " + year).append(" " + time_str).toString();
				
//System.out.println("fweijfowef " + date_str);

		DateFormat date_format;
		Date date = null;
		if (query_split.length == 3) {
			date_format = new SimpleDateFormat(Constants.US_DATE_NO_TIME_FORMAT, Locale.ENGLISH);
		}
		else {
			date_format = new SimpleDateFormat(Constants.US_DATE_24H_TIME_FORMAT, Locale.ENGLISH);
		}
		
		try {
			date = date_format.parse(date_str);
		}
		catch (ParseException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

		return date;
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
	// adapted from standard JDK 1.7 hashCode() implementation (in case future updates change or bork the algorithm)
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

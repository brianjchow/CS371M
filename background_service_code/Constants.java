import java.util.Date;
import java.util.Iterator;

final class Constants {
	public static		 	EventList 	CSV_FEEDS_MASTER = null;
	public static			EventList	CSV_FEEDS_CLEANED = null;
	private static 			boolean 	has_feed_been_read;
	
	protected static final 	boolean 	DEBUG;
	
	public static final 	Date 		DAYBREAK;
	public static final 	Date 		NIGHTFALL;
	
	public static final 	String 		ALL_DAY;
	public static final 	String 		ATRIUM;
	public static final 	String[] 	CAMPUS_BUILDINGS;
	public static final 	String 		CAPACITY;
	
	public static final 	int[] 		DAYS_IN_MONTH;
	public static final 	String[] 	DAYS_OF_WEEK_LONG;
	public static final 	String[] 	DAYS_OF_WEEK_SHORT;
	public static final 	int 		MONDAY;
	public static final 	int 		TUESDAY;
	public static final 	int 		WEDNESDAY;
	public static final 	int 		THURSDAY;
	public static final 	int 		FRIDAY;
	public static final 	int 		SATURDAY;
	public static final 	int 		SUNDAY;
	public static final 	int 		NUM_DAYS_IN_WEEK;
	
	public static final		String[]	DEPARTMENTS;
	
	public static final 	int 		DEFAULT_EVENT_DURATION;		// minutes	
	public static final 	int 		DEFAULT_QUERY_DURATION;
	public static final		boolean		DEFAULT_ROOM_HAS_POWER;
	public static final 	int 		DEFAULT_ROOM_CAPACITY;
	public static final		String		DEFAULT_ROOM_TYPE;
	public static final 	String 		DEFAULT_GDC_LOCATION;
	public static final 	String 		END_DATE;
	public static final 	String 		EVENT_NAME;
	public static final 	String 		GDC;
	public static final 	Location 	GDC_ATRIUM;
	public static final 	Location 	GDC_GATESHENGE;
	public static final 	String 		GO_HOME_MSG;
	protected static final 	String[] 	IGNORE_ROOMS;
	public static final 	String 		LOCATION;
	
	public static final 	String[] 	MONTHS_LONG;
	public static final 	String[] 	MONTHS_SHORT;

	public static final 	String 		NO_ROOMS_AVAIL_MSG;
	public static final 	String 		POWER;
	public static final 	String 		START_DATE;

	private static final	int 		YAM;		// 25 in Cantonese
	public static final 	int 		MIN_YEAR;
	public static final 	int 		MAX_YEAR;
	public static final 	int 		MAX_TIME;
	public static final 	int 		MIN_TIME;
	public static final 	int 		MINUTES_IN_HOUR;
	public static final 	int 		MINUTES_IN_DAY;
	public static final 	int 		HOURS_IN_DAY;
	public static final		int			LAST_TIME_OF_DAY;
	public static final		int			LAST_TIME_OF_NIGHT;
	
	public static final		String		SEARCH_GDC_ONLY;
	public static final		String		SEARCH_FOR_BUILDING;
	
	public static final 	String 		UTCS_CSV_FEED_FORMAT;
	public static final 	String 		US_DATE_24H_TIME_FORMAT;
	public static final 	String 		US_DATE_NO_TIME_FORMAT;

	public static final		String[]	VALID_GDC_ROOMS;
	public static final		String[]	VALID_GDC_ROOMS_TYPES;
	public static final 	int[] 		VALID_GDC_ROOMS_CAPACITIES;
	public static final 	boolean[] 	VALID_GDC_ROOMS_POWERS;
	public static final 	RoomList 	VALID_GDC_ROOMS_ROOMLIST;
	public static final		String		CLASS;
	public static final		String		CONFERENCE;
	public static final		String		LAB;
	public static final		String		LECTURE_HALL;
	public static final		String		LOBBY;
	public static final		String		LOUNGE;
	public static final		String		SEMINAR;
	
	static {
		DEBUG						=	true;
		
		ALL_DAY						=	"all day";
		ATRIUM						=	"Atrium";
		
		CAMPUS_BUILDINGS			=	initialise_campus_buildings();
			
		CAPACITY					=	"capacity";
//		MONDAY						=	0;
//		TUESDAY						=	1;
//		WEDNESDAY					=	2;
//		THURSDAY					=	3;
//		FRIDAY						=	4;
//		SATURDAY					=	5;
//		SUNDAY						=	6;
//		NUM_DAYS_IN_WEEK			=	7;
		
		SUNDAY						=	1;
		MONDAY						=	2;
		TUESDAY						=	3;
		WEDNESDAY					=	4;
		THURSDAY					=	5;
		FRIDAY						=	6;
		SATURDAY					=	7;
		NUM_DAYS_IN_WEEK			=	8;
		
		
		DAYS_IN_MONTH				=	initialise_days_in_month();
		DAYS_OF_WEEK_LONG			=	initialise_days_of_week_long();
		DAYS_OF_WEEK_SHORT			=	initialise_days_of_week_short();
		
		DEFAULT_GDC_LOCATION		=	"Gateshenge";
		END_DATE					=	"end_date";
		EVENT_NAME					=	"event_name";
		GDC							=	"GDC";
		GDC_ATRIUM					=	new Location(GDC + " " + ATRIUM);
		GDC_GATESHENGE				=	new Location(GDC + " " + DEFAULT_GDC_LOCATION);
		GO_HOME_MSG					=	"Go home and sleep, you procrastinator";
		
		DEPARTMENTS					=	initialise_departments();
		
		IGNORE_ROOMS				=	initialise_ignore_rooms();
		LOCATION					=	"location";
		MONTHS_LONG					=	initialise_months_long();
		MONTHS_SHORT				=	initialise_months_short();

		NO_ROOMS_AVAIL_MSG			=	"No rooms available; please try again.";
		POWER						=	"power";
		START_DATE					=	"start_date";	
		
		DEFAULT_EVENT_DURATION		=	90;		// minutes
		DEFAULT_QUERY_DURATION		=	60;
		DEFAULT_ROOM_CAPACITY		=	25;
		YAM							=	DEFAULT_ROOM_CAPACITY;	// 25 in Cantonese
		MIN_YEAR					=	2014;
		MAX_YEAR					=	2099;
		MAX_TIME					=	2359;
		MIN_TIME					=	0000;
		MINUTES_IN_HOUR				=	60;
		MINUTES_IN_DAY				=	1440;
		HOURS_IN_DAY				=	24;
		LAST_TIME_OF_DAY			=	2200;
		LAST_TIME_OF_NIGHT			=	800;
		
		SEARCH_GDC_ONLY				=	"search_gdc_only";
		SEARCH_FOR_BUILDING			=	"search_for_building";
		
		UTCS_CSV_FEED_FORMAT		=	"EEE dd MMM yyyy HHmm";
		US_DATE_24H_TIME_FORMAT		=	"MMM dd yyyy HHmm";
		US_DATE_NO_TIME_FORMAT		=	"MMM dd yyyy";

		CLASS						=	"class";
		CONFERENCE					=	"conference";
		LAB							=	"lab";
		LECTURE_HALL				=	"lecture_hall";
		LOBBY						=	"lobby";
		LOUNGE						=	"lounge";
		SEMINAR						=	"seminar";
		VALID_GDC_ROOMS				=	initialise_valid_gdc_rooms();
		VALID_GDC_ROOMS_TYPES		=	initialise_valid_gdc_rooms_types();
		VALID_GDC_ROOMS_CAPACITIES	=	initialise_valid_gdc_rooms_capacities();
		VALID_GDC_ROOMS_POWERS		=	initialise_valid_gdc_rooms_powers();
		
		DEFAULT_ROOM_HAS_POWER		=	false;
		DEFAULT_ROOM_TYPE			=	CLASS;

		VALID_GDC_ROOMS_ROOMLIST	=	new RoomList();
		
		has_feed_been_read			=	false;

		/* ################################# DO NOT MOVE ANYTHING BELOW THIS LINE ABOVE IT ################################# */
		
//		CSV_FEEDS_MASTER			=	CSVReader.read_csv();
//		CSV_FEEDS_CLEANED			=	get_events_cleaned();

		DAYBREAK					=	Utilities.get_date(1, 2, 2014, 800);
		NIGHTFALL					=	Utilities.get_date(1, 1, 2014, 2200);

	}

	protected static void init() {
		if (CSV_FEEDS_MASTER == null) {
			has_feed_been_read = false;
			CSV_FEEDS_CLEANED = null;
		}
		
		CSV_FEEDS_MASTER = CSVReader.read_csv();
		CSV_FEEDS_CLEANED = get_events_cleaned();
	}
	
	/*
	 * Ignored rooms (room num, room type, capacity)
	 * 	1.210	Telepresence Room	10
	 * 	2.100	Atrium (Main)	?
	 * 	3.100	Atrium Bridge	18
	 * 	4.100	Atrium Bridge	18
	 * 	4.202	Lounge (Graduate)	?
	 * 	4.314	?	?
	 * 	5.100	Atrium Bridge	12
	 * 	6.100	Atrium Bridge	12
	 * 	6.302	Lounge (Faculty)	70
	 */
	private static final EventList get_events_cleaned() {
//		final String[] ignore_rooms = { "1.210", "2.100", "3.100", "4.100", "4.202", "4.314", "5.100", "6.100", "6.302" };
		if (CSV_FEEDS_MASTER == null) {
			return null;
		}
		EventList out = new EventList();
		Iterator<Event> itr = CSV_FEEDS_MASTER.get_iterator();
		Event event;
		Location curr_eo_loc;
		String loc_str;
		while (itr.hasNext()) {
			event = itr.next();
			curr_eo_loc = event.get_location();
			loc_str = curr_eo_loc.toString();
			if (Utilities.containsIgnoreCase(loc_str, "1.210") ||
				Utilities.containsIgnoreCase(loc_str, "2.100") ||
				Utilities.containsIgnoreCase(loc_str, "3.100") ||
				Utilities.containsIgnoreCase(loc_str, "4.100") ||
				Utilities.containsIgnoreCase(loc_str, "4.202") ||
				Utilities.containsIgnoreCase(loc_str, "4.314") ||
				Utilities.containsIgnoreCase(loc_str, "5.100") ||
				Utilities.containsIgnoreCase(loc_str, "6.100") ||
				Utilities.containsIgnoreCase(loc_str, "6.302") ||
				Utilities.containsIgnoreCase(loc_str, ATRIUM) ||
				Utilities.containsIgnoreCase(loc_str, DEFAULT_GDC_LOCATION)) {		// unrolled loop
				continue;
			}
			out.add(event.clone());
		}
		
		return out;
	}

	protected static boolean get_has_feed_been_read() {
		return has_feed_been_read;
	}
	
	protected static void set_has_feed_been_read() {
		has_feed_been_read = true;
	}

	private static final String[] initialise_campus_buildings() {
		final String[] CAMPUS_BUILDINGS = {
				"ACA", "ADH", "AFP", "AHG", "ANB", "AND", "ARC", "ART", "ATT",
				"BAT", "BEL", "BEN", "BHD", "BIO", "BLD", "BMA", "BMC", "BME", "BOT", "BRB", "BRG", "BTL", "BUR", "BWY",
				"CAL", "CBA", "CCG", "CCJ", "CDA", "CDL", "CEE", "CCF", "CLA", "CLK", "CMA", "CMB", "CML", "COM", "CPE", "CRB", "CRD", "CRH", "CSA", "CS3", "CS4", "CS5", "CTR",
				"DEV", "DCP", "DFA", "DFF",
				"EAS", "ECJ", "ENS", "EPS", "ERC", "ETC",
				"FAC", "FC1", "FC2", "FC3", "FC4", "FC5", "FC6", "FC7", "FC8", "FDF", "FDH", "FNT", "FPC",
				"GAR", "GDC", "GEA", "GEB", "GOL", "GRE", "GRG", "GSB", "GUG",
				"HMA", "HRC", "HRH", "HSM",
				"INT", "IPF",
				"JCD", "JES", "JGB", "JHH", "JON",
				"KIN",
				"LBJ", "LCH", "LDH", "LFH", "LLA", "LLB", "LLC", "LLD", "LLE", "LLF", "LTD", "LTH",
				"MAG", "MAI", "MBB", "MEZ", "MFH", "MHD", "MMS", "MNC", "MRH", "MSB",
				"NEZ", "NHB", "NMS", "NOA", "NUR",
				"PAC", "PAI", "PAR", "PAT", "PCL", "PHD", "PHR", "POB", "PPA", "PPB", "PPE", "PPL",
				"RHD", "RLM", "RSC",
				"SAC", "SAG", "SBS", "SEA", "SER", "SJG", "SJH", "SRH", "SSB", "SSW", "STD", "SUT", "SWG", "SW7", "SZB",
				"TCC", "TMM", "TNH", "TRG", "TSC", "TSG", "TTC",
				"UA9", "UIL", "UNB", "UPB", "UTA", "UTC", "UTX",
				"WAG", "WCH", "WEL", "WIN", "WMB", "WRW", "WWH"
		};
		return CAMPUS_BUILDINGS;
	}
	
	private static final int[] initialise_days_in_month() {
		final int[] DAYS_IN_MONTH =	{ 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
		return DAYS_IN_MONTH;
	}
	
	private static final String[] initialise_days_of_week_long() {
//		final String[] DAYS_OF_WEEK_LONG = { "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY" };
		final String[] DAYS_OF_WEEK_LONG = { "", "SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY" };
		return DAYS_OF_WEEK_LONG;
	}
	
	private static final String[] initialise_days_of_week_short() {
//		final String[] DAYS_OF_WEEK_SHORT =	{ "MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN" };
		final String[] DAYS_OF_WEEK_SHORT =	{ "", "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT" };
		return DAYS_OF_WEEK_SHORT;
	}
	
	private static final String[] initialise_departments() {
		final String[] DEPARTMENTS = { 
				"ACC", "ACF", "ADV", "ASE", "AFR", "AFS", "ASL", "AMS", "AHC", "ANT", "ALD", "ARA", "ARE", "ARI", "ARC", "AED", "ARH", "AAS", "ANS", "AST",
				"BSN", "BCH", "BIO", "BME", "BDP", "B A", "BGS",
				"CHE", "CH", "CHI", "C E", "CLA", "C C", "CGS", "CSD", "COM", "CMS", "CRP", "C L", "CSE", "C S", "CON", "CTI", "CRW",
				"EDC", "CZ", "DAN", "DES", "DEV", "D B", "DRS", "DCH",
				"ECO", "EDA", "EDP", "E E", "EER", "ENM", "E M", "E S", "E", "ESL", "ENS", "EVS", "EUP", "EUS",
				"FIN", "F A", "FLU", "FLE", "FR", "F H",
				"G E", "GRG", "GEO", "GER", "GSD", "GOV", "GRS", "GK", "GUI",
				"HAR", "H S", "HED", "HEB", "HIN", "HIS", "HDF", "HDO", "HMN",
				"ILA", "LAL", "INF", "I B", "IRG", "ISL", "ITL", "ITC",
				"JPN", "J S", "J",
				"KIN", "KOR", "LAR",
				"LAT", "LAS", "LAW", "LEB", "L A", "LAH", "LIN",
				"MAL", "MAN", "MIS", "MFG", "MNS", "MKT", "MSE", "M", "M E", "MDV", "MAS", "MEL", "MES", "M S", "MOL", "MUS", "MBU", "MRT",
				"NSC", "N S", "NEU", "NOR", "N", "NTR",
				"OBO", "OPR", "O M", "ORI", "ORG",
				"PER", "PRS", "PGE", "PHR", "PGS", "PHL", "PED", "P S", "PHY", "PIA", "POL", "POR", "PRC", "PSY", "P A", "PBH", "P R",
				"RTF", "R E", "R S", "RHE", "R M", "REE", "RUS",
				"SAN", "SAX", "STC", "STM", "SCI", "S C", "SEL", "S S", "S W", "SOC", "SPN", "SPC", "SED", "STA", "SDS", "ART", "SWE",
				"TAM", "TEL", "TXA", "T D", "TRO", "TRU", "TBA", "TUR", "T C",
				"UGS", "URB", "URD", "UTL", "UTS",
				"VIA", "VIO", "V C", "VAS", "VOI",
				"WGS", "WRT",
				"YID", "YOR"
		};
		return DEPARTMENTS;
	}
	
	private static final String[] initialise_ignore_rooms() {
		final String[] IGNORE_ROOMS = {
				"UTCS", "AI", "Alumni", "Ambassador", "Holiday", "Party", "Tailgate", "Colloquia", "Colloquium"
		};
		return IGNORE_ROOMS;
	}
	
	private static final String[] initialise_months_long() {
		final String[] MONTHS_LONG = {
				"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"
		};
		return MONTHS_LONG;
	}
	
	private static final String[] initialise_months_short() {
		final String[] MONTHS_SHORT = {
				"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
		};
		return MONTHS_SHORT;
	}

	/*
	 * Ignored rooms (room num, room type, capacity)
	 * 	1.210	Telepresence Room	10
	 * 	2.100	Atrium (Main)	?
	 * 	3.100	Atrium Bridge	18
	 * 	4.100	Atrium Bridge	18
	 * 	4.202	Lounge (Graduate)	?
	 * 	4.314	?	?
	 * 	5.100	Atrium Bridge	12
	 * 	6.100	Atrium Bridge	12
	 * 	6.302	Lounge (Faculty)	70
	 */
	private static final String[] initialise_valid_gdc_rooms() {
		final String[] VALID_GDC_ROOMS = {
				"1.304", "1.406",
				"2.104", "2.210", "2.216", "2.402", "2.410", "2.502", "2.506", "2.712", "2.902",
				"3.416", "3.516", "3.816", "3.828",
				"4.202", "4.302", "4.304", "4.314", "4.416", "4.516", "4.816", "4.828",
				"5.302", "5.304", "5.416", "5.516", "5.816", "5.828",
				"6.202", "6.302", "6.416", "6.516", "6.816", "6.828",
				"7.514", "7.808", "7.820"
		};
		return VALID_GDC_ROOMS;
	}
	
	private static final String[] initialise_valid_gdc_rooms_types() {
		final String[] VALID_GDC_ROOMS_TYPES = {
				CLASS, CLASS,
				CONFERENCE, CLASS, LECTURE_HALL, LAB, CLASS, CLASS, LAB, CONFERENCE, CONFERENCE,
				CONFERENCE, SEMINAR, SEMINAR, CONFERENCE,
				LOUNGE, CLASS, SEMINAR, CONFERENCE, CONFERENCE, SEMINAR, SEMINAR, LOBBY,
				CLASS, LAB, CONFERENCE, SEMINAR, SEMINAR, LOBBY,
				SEMINAR, LOUNGE, CONFERENCE, SEMINAR, SEMINAR, LOBBY,
				CONFERENCE, CONFERENCE, LOBBY
		};
		return VALID_GDC_ROOMS_TYPES;
	}
	
	private static final int[] initialise_valid_gdc_rooms_capacities() {
		final int[] VALID_GDC_ROOMS_CAPACITIES = { 
				81, 34,
				20, 34, 198, 20, 28, 24, 27, 10, 12,
				8, 18, 14, 8,
				YAM, 48, 48, YAM, 8, 18, 14, 8,
				62, 24, 8, 18, 14, 8,
				35, 70, 8, 18, 18, 8,
				YAM, YAM, 8
		};
		return VALID_GDC_ROOMS_CAPACITIES;
	}
	
	private static final boolean[] initialise_valid_gdc_rooms_powers() {
		final boolean[] VALID_GDC_ROOMS_POWERS = {
				false, false,
				false, false, false, false, false, false, true, false, false,
				false, false, false, false,
				false, false, false, false, false, false, false, false,
				false, false, false, false, false, false,
				false, false, false, false, false, false,
				false, false, false
		};
		return VALID_GDC_ROOMS_POWERS;
	}

}		// end of file

// ################################################
/*
 * all set_start_date()s: prevent previous date from being assigned
 * null check all toString()s
 */

/*

	public static void main(String[] args){
		
		int current_month = 10;
		int current_day = 27;

		test(Utilities.get_date(current_month, current_day, 2014, 400), 61);
		test(Utilities.get_date(current_month, current_day, 2014, 800), 61);
		test(Utilities.get_date(current_month, current_day, 2014, 1030), 61);
		test(Utilities.get_date(current_month, current_day, 2014, 1300), 61);
		test(Utilities.get_date(current_month, current_day, 2014, 1300), 1);
		test(Utilities.get_date(current_month, current_day, 2014, 1630), 61);
		test(Utilities.get_date(current_month, current_day, 2014, 2300), 61);
		test(Utilities.get_date(current_month, current_day, 2014, 0000), Constants.MINUTES_IN_DAY);

	}
	
	public static void test(Date date, int duration) {
		if (date == null) {
			Calendar calendar = Calendar.getInstance();
			date = calendar.getTime();
		}
		Stopwatch stopwatch = new Stopwatch();
		double time_to_read, time_to_exec;
		int num_events_in_raw_feeds;	//, num_events_available;
		
		EventList events = new EventList();

		stopwatch.start();
//		events = read_csv();
		events = Constants.CSV_FEEDS_MASTER;
		stopwatch.stop();
		time_to_read = stopwatch.time();
		num_events_in_raw_feeds = events.get_size();

		events.sort_by_event_name(true);
		events.sort_by_start_date(true);
		
//System.out.println(events.toString());
		
		stopwatch.start();
		Query query = new Query(date);
		
		query.set_duration(duration);
		query.set_option_power(false);
//		query.set_option_capacity(0);
		query.set_option(Constants.CAPACITY, new Integer(0));

		String random_room = query.search();

		stopwatch.stop();
		time_to_exec = stopwatch.time();
		
		System.out.println("Random room chosen:\t" + random_room + "\n");
		System.out.println("Query:\n" + query.toString() + "\n");
		
		System.out.println("Done.\n");
		System.out.println("Num events, raw feeds:\t" + num_events_in_raw_feeds);
		System.out.println("Num events, cleaned:\t" + Constants.CSV_FEEDS_CLEANED.get_size());
//		System.out.println("Num events avail at\n    " + query + ":\t" + num_events_available);
		System.out.println("Time to read feeds:\t" + time_to_read + " seconds");
		System.out.println("Time to process:\t" + time_to_exec + " seconds");
		System.out.println("Total time elapsed:\t" + (time_to_read + time_to_exec) + " seconds\n");
		System.out.println("##########################################################################\n");
	}
	
 */



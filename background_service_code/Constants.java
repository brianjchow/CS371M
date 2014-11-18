import java.util.Date;
import java.util.Iterator;

final class Constants {
	protected static		EventList 	CSV_FEEDS_MASTER				=	null;
	protected static		EventList	CSV_FEEDS_CLEANED				=	null;
	private static 			boolean 	has_feed_been_read;

//	protected static	 	RoomList 	GDC_ROOMS_THIS_SEMESTER			=	null;
//	protected static		RoomList	GDC_ROOMS_NEXT_SEMESTER			=	null;
//	protected static		RoomList	USED_ROOMS_THIS_SEMESTER		=	null;
//	protected static		RoomList	USED_ROOMS_NEXT_SEMESTER		=	null;
	protected static		BuildingList	BUILDING_CACHELIST_THIS_SEMESTER	=	null;
	protected static		BuildingList	BUILDING_CACHELIST_NEXT_SEMESTER	=	null;
	protected static		boolean		DISABLE_SEARCHES_NEXT_SEMESTER	=	false;
	
	protected static final	boolean		IGNORE_CONFERENCE_ROOMS			=	true;
	
	protected static final	String		COURSE_SCHEDULE_THIS_SEMESTER	=	"course_schedules/master_course_schedule_f14.csv";
	protected static final	String		COURSE_SCHEDULE_NEXT_SEMESTER	=	"course_schedules/master_course_schedule_s15.csv";
	protected static final	String		DEFAULT_DB_EXTENSION			=	"db";
	
	protected static final	int			BUILDING_CODE_LENGTH			=	3;

	protected static final	int			SPRING_START_MONTH				=	1;
	protected static final	int			SPRING_START_DAY				=	10;
	protected static final	int			SPRING_END_MONTH				=	5;
	protected static final	int			SPRING_END_DAY					=	20;
	protected static final	int			SUMMER_START_MONTH				=	5;
	protected static final	int			SUMMER_START_DAY				=	25;
	protected static final	int			SUMMER_END_MONTH				=	8;
	protected static final	int			SUMMER_END_DAY					=	19;
	protected static final	int			FALL_START_MONTH				=	8;
	protected static final	int			FALL_START_DAY					=	20;
	protected static final	int			FALL_END_MONTH					=	12;
	protected static final	int			FALL_END_DAY					=	20;
	
	protected static final 	boolean 	DEBUG;
	
	protected static final 	Date 		DAYBREAK;
	protected static final 	Date 		NIGHTFALL;
	
	protected static final 	String 		ALL_DAY;
	protected static final 	String 		ATRIUM;
	protected static final 	String[] 	CAMPUS_BUILDINGS;
	
	protected static final 	String 		CAPACITY;
	protected static final 	String 		POWER;
	protected static final	String		SEARCH_GDC_ONLY;
	protected static final	String		SEARCH_BUILDING;
	
	protected static final	String		ALL_ROOMS_AVAIL_MSG;
	protected static final 	String 		NO_ROOMS_AVAIL_MSG;
	protected static final 	String 		GO_HOME_MSG;
	protected static final	String		SUMMER_MSG;
	protected static final	String		HOLIDAY_MSG;
	protected static final	String		NO_INFO_AVAIL_MSG;
	protected static final	String		SEARCH_ERROR_MSG;
	protected static final	int			ALL_ROOMS_AVAIL					=	0;
	protected static final	int			NO_ROOMS_AVAIL					=	1;
	protected static final	int			GO_HOME							=	2;
	protected static final	int			SUMMER							=	3;
	protected static final	int			HOLIDAY							=	4;
	protected static final	int			NO_INFO							=	5;
	protected static final	int			SEARCH_ERROR					=	6;
	protected static final	String[]	MESSAGE_STATUS_FLAGS;
		
	protected static final 	int[] 		DAYS_IN_MONTH;
	protected static final 	String[] 	DAYS_OF_WEEK_LONG;
	protected static final 	String[] 	DAYS_OF_WEEK_SHORT;
	protected static final 	int 		MONDAY;
	protected static final 	int 		TUESDAY;
	protected static final 	int 		WEDNESDAY;
	protected static final 	int 		THURSDAY;
	protected static final 	int 		FRIDAY;
	protected static final 	int 		SATURDAY;
	protected static final 	int 		SUNDAY;
	protected static final 	int 		NUM_DAYS_IN_WEEK;
	
	protected static final	String[]	DEPARTMENTS;
	
	protected static final 	int 		DEFAULT_EVENT_DURATION;		// minutes
	protected static final 	int 		DEFAULT_QUERY_DURATION;
	protected static final	boolean		DEFAULT_ROOM_HAS_POWER;
	protected static final 	int 		DEFAULT_ROOM_CAPACITY;
	protected static final	String		DEFAULT_ROOM_TYPE;
	protected static final 	String 		DEFAULT_GDC_LOCATION;
	protected static final 	String 		END_DATE;
	protected static final 	String 		EVENT_NAME;
	protected static final 	String 		GDC;
	protected static final 	Location 	GDC_ATRIUM;
	protected static final 	Location 	GDC_GATESHENGE;
	protected static final 	String[] 	IGNORE_ROOMS;
	protected static final 	String 		LOCATION;
	
	protected static final 	String[] 	MONTHS_LONG;
	protected static final 	String[] 	MONTHS_SHORT;

	protected static final 	String 		START_DATE;

	protected static final	int 		YAM;		// 25 in Cantonese
	protected static final 	int 		MIN_YEAR;
	protected static final 	int 		MAX_YEAR;
	protected static final 	int 		MAX_TIME;
	protected static final 	int 		MIN_TIME;
	protected static final 	int 		MINUTES_IN_HOUR;
	protected static final 	int 		MINUTES_IN_DAY;
	protected static final 	int 		HOURS_IN_DAY;
	protected static final	int			LAST_TIME_OF_DAY;
	protected static final	int			LAST_TIME_OF_NIGHT;
	
	protected static final 	String 		UTCS_CSV_FEED_FORMAT;
	protected static final 	String 		US_DATE_24H_TIME_FORMAT;
	protected static final 	String 		US_DATE_NO_TIME_FORMAT;

	protected static final	String[]	VALID_GDC_ROOMS;
	protected static final	String[]	VALID_GDC_ROOMS_TYPES;
	protected static final 	int[] 		VALID_GDC_ROOMS_CAPACITIES;
	protected static final 	boolean[] 	VALID_GDC_ROOMS_POWERS;
	protected static final	String		CLASS;
	protected static final	String		CONFERENCE;
	protected static final	String		LAB;
	protected static final	String		LECTURE_HALL;
	protected static final	String		LOBBY;
	protected static final	String		LOUNGE;
	protected static final	String		SEMINAR;
	
	protected static final	double		DEFAULT_HASHMAP_LOAD_FACTOR = 0.7;
	
	static {
		
		DEBUG						=	true;
		
		ALL_DAY						=	"all day";
		ATRIUM						=	"Atrium";
		
		CAMPUS_BUILDINGS			=	initialise_campus_buildings();
			
		CAPACITY					=	"capacity";
		POWER						=	"power";
		SEARCH_GDC_ONLY				=	"search_gdc_only";
		SEARCH_BUILDING				=	"search_building";
		
		ALL_ROOMS_AVAIL_MSG			=	"All rooms available.";
		NO_ROOMS_AVAIL_MSG			=	"No rooms available; please try again.";
		GO_HOME_MSG					=	"Go home and sleep, you procrastinator";
		SUMMER_MSG					=	"Some rooms available (summer hours; check course schedules).";
		HOLIDAY_MSG					=	"All rooms available (campus closed for holidays).";
		NO_INFO_AVAIL_MSG			=	"Not enough info available for search; please try again.";
		SEARCH_ERROR_MSG			=	"Unknown search error; please try again.";
		MESSAGE_STATUS_FLAGS		=	initialise_message_status_flags();
		
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
		
		DEPARTMENTS					=	initialise_departments();
		
		IGNORE_ROOMS				=	initialise_ignore_rooms();
		LOCATION					=	"location";
		MONTHS_LONG					=	initialise_months_long();
		MONTHS_SHORT				=	initialise_months_short();

		START_DATE					=	"start_date";	
		
		DEFAULT_EVENT_DURATION		=	90;		// minutes
		DEFAULT_QUERY_DURATION		=	60;
		DEFAULT_ROOM_CAPACITY		=	-1;
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

//		VALID_GDC_ROOMS_ROOMLIST	=	new RoomList();
//		VALID_ROOMS_THIS_SEMESTER	=	new RoomList();
//		VALID_ROOMS_NEXT_SEMESTER	=	new RoomList();
		
		has_feed_been_read			=	false;

		/* ################################# DO NOT MOVE ANYTHING BELOW THIS LINE ABOVE IT ################################# */
		
//		CSV_FEEDS_MASTER			=	CSVReader.read_csv();
//		CSV_FEEDS_CLEANED			=	get_events_cleaned();

		DAYBREAK					=	Utilities.get_date(1, 2, 2014, 800);
		NIGHTFALL					=	Utilities.get_date(1, 1, 2014, 2200);

	}
	
	private static void reset() {
		CSV_FEEDS_MASTER = null;
		has_feed_been_read = false;
		CSV_FEEDS_CLEANED = null;
		
		BUILDING_CACHELIST_THIS_SEMESTER = null;
		BUILDING_CACHELIST_NEXT_SEMESTER = null;
		DISABLE_SEARCHES_NEXT_SEMESTER = false;
	}
	
	protected static void init() {
		if (COURSE_SCHEDULE_THIS_SEMESTER == null) {
			throw new IllegalArgumentException("App needs at least the current course schedule to correctly function.");
		}
		
		reset();
		
		CSV_FEEDS_MASTER = CSVReader.read_csv();
		
		if (CSV_FEEDS_MASTER == null) {
			throw new IllegalStateException("Unknown error occurred while reading CSV feeds.");
		}
		
		CSV_FEEDS_CLEANED = get_events_cleaned();
		
		BUILDING_CACHELIST_THIS_SEMESTER = new BuildingList();
		BUILDING_CACHELIST_THIS_SEMESTER.put_building(GDC, Building.get_instance(GDC, COURSE_SCHEDULE_THIS_SEMESTER));
		
		if (COURSE_SCHEDULE_NEXT_SEMESTER == null) {
			DISABLE_SEARCHES_NEXT_SEMESTER = true;
		}
		else {
			BUILDING_CACHELIST_NEXT_SEMESTER = new BuildingList();
			BUILDING_CACHELIST_NEXT_SEMESTER.put_building(GDC, Building.get_instance(GDC, COURSE_SCHEDULE_NEXT_SEMESTER));
		}
		
		
		
//		if (COURSE_SCHEDULE_THIS_SEMESTER == null) {
//			USED_ROOMS_THIS_SEMESTER = null;
//		}
//		else {
//			USED_ROOMS_THIS_SEMESTER = new RoomList(COURSE_SCHEDULE_THIS_SEMESTER);
//		}
//		
//		if (COURSE_SCHEDULE_NEXT_SEMESTER == null) {
//			USED_ROOMS_NEXT_SEMESTER = null;
//		}
//		else {
//			USED_ROOMS_NEXT_SEMESTER = new RoomList(COURSE_SCHEDULE_NEXT_SEMESTER);
//		}
//		
//		if (USED_ROOMS_THIS_SEMESTER == null) {
//			GDC_ROOMS_THIS_SEMESTER = new RoomList();
//			GDC_ROOMS_THIS_SEMESTER.initialise_gdc_rooms();
//		}
//		else {
//			GDC_ROOMS_THIS_SEMESTER = USED_ROOMS_THIS_SEMESTER.get_gdc_rooms();
//		}
//		
//		if (USED_ROOMS_NEXT_SEMESTER == null) {
//			GDC_ROOMS_NEXT_SEMESTER = new RoomList();
//			GDC_ROOMS_THIS_SEMESTER.initialise_gdc_rooms();
//		}
//		else {
//			GDC_ROOMS_NEXT_SEMESTER = USED_ROOMS_NEXT_SEMESTER.get_gdc_rooms();
//		}
		
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
//		if (CSV_FEEDS_MASTER == null) {
//			return null;
//		}
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
				"CAL", "CBA", "CCG", "CCJ", "CDA", "CDL", "CEE", "CCF", "CLA", "CLK", "CMA", "CMB", "CML", "COM", "CPE", "CRB", "CRD", "CRH", "CSA", "CTR",
				"DEV", "DCP", "DFA", "DFF",
				"EAS", "ECJ", "ENS", "EPS", "ERC", "ETC",
				"FAC", "FDF", "FDH", "FNT", "FPC",			// , "FC1", "FC2", "FC3", "FC4", "FC5", "FC6", "FC7", "FC8" (facilities complex)
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
	
	private static final String[] initialise_message_status_flags() {
		final String[] FLAGS = {
				ALL_ROOMS_AVAIL_MSG, NO_ROOMS_AVAIL_MSG, GO_HOME_MSG, SUMMER_MSG, HOLIDAY_MSG, NO_INFO_AVAIL_MSG, SEARCH_ERROR_MSG
		};
		return FLAGS;
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
	
	/* UNKNOWN:
	 * 	- 4.202
	 * 	- 4.314
	 * 	- 7.514
	 * 	- 7.808 */
	private static final int[] initialise_valid_gdc_rooms_capacities() {
		final int[] VALID_GDC_ROOMS_CAPACITIES = { 
				81, 34,
				20, 34, 198, 20, 28, 24, 27, 10, 12,
				8, 18, 14, 8,
				35, 48, 48, 8, 8, 18, 14, 8,
				62, 24, 8, 18, 14, 8,
				35, 70, 8, 18, 18, 8,
				8, 8, 8
		};
		return VALID_GDC_ROOMS_CAPACITIES;
	}
	
	private static final boolean[] initialise_valid_gdc_rooms_powers() {
		final boolean[] VALID_GDC_ROOMS_POWERS = {
				false, false,
				false, false, false, true, true, true, true, false, false,
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



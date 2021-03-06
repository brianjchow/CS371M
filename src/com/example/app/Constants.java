package com.example.app;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

final class Constants {

	protected static final	String		CSV_FEEDS_WRITE_SUCCESS			=	"CSV_FEEDS_WRITE_SUCCESS";
	protected static final	String		CSV_FEED_ALL_EVENTS_WRITE_SUCCESS	=	"CSV_FEEDS_ALL_EVENTS_WRITE_SUCCESS";
	protected static final	String		CSV_FEED_ALL_ROOMS_WRITE_SUCCESS	=	"CSV_FEEDS_ALL_ROOMS_WRITE_SUCCESS";
	protected static final	String		CSV_FEED_ALL_TODAYS_EVENTS_WRITE_SUCCESS	=	"CSV_FEED_ALL_TODAYS_EVENTS_WRITE_SUCCESS";
	
	protected static final	String		COURSE_SCHEDULE_THIS_SEMESTER	=	"master_course_schedule_f14";
	protected static final	String		COURSE_SCHEDULE_NEXT_SEMESTER	=	"master_course_schedule_s15";
	protected static final	String		DEFAULT_DB_EXTENSION			=	"db";

	private Constants() { }
	
	private static final	String		TAG								=	"Constants";

	protected static		EventList 	CSV_FEEDS_MASTER				=	null;
	protected static		EventList	CSV_FEEDS_CLEANED				=	null;
	private static 			boolean 	has_feed_been_read;

	protected static	BuildingList	BUILDING_CACHELIST_THIS_SEMESTER	=	null; // new BuildingList();
	protected static	BuildingList	BUILDING_CACHELIST_NEXT_SEMESTER	=	null; // new BuildingList();
	protected static final	boolean		DISABLE_SEARCHES_NEXT_SEMESTER;
	
	protected static final	int			BUILDING_CODE_LENGTH			=	3;

	protected static final	int			SPRING_START_MONTH				=	1;		// default 1		Spring '15
	protected static final	int			SPRING_START_DAY				=	20;		// default 10
	protected static final	int			SPRING_END_MONTH				=	5;		// default 5
	protected static final	int			SPRING_END_DAY					=	8;		// default 20
	protected static final	int			SUMMER_START_MONTH				=	6;		// default 20		Summer '15
	protected static final	int			SUMMER_START_DAY				=	4;		// default 25
	protected static final	int			SUMMER_END_MONTH				=	8;		// default 8
	protected static final	int			SUMMER_END_DAY					=	14;		// default 19
	protected static final	int			FALL_START_MONTH				=	8;		// default 8		Fall '14
	protected static final	int			FALL_START_DAY					=	27;		// default 20
	protected static final	int			FALL_END_MONTH					=	12;		// default 12
	protected static final	int			FALL_END_DAY					=	5;		// default 20
	
	protected static final 	boolean 	DEBUG;
	
	protected static final 	Date 		DAYBREAK;
	protected static final 	Date 		NIGHTFALL;
	
	protected static final	String		EXIT							=	"EXIT";
	
	protected static final 	String 		ALL_DAY;
	protected static final 	String 		ATRIUM;
	protected static final 	Map<String, Integer> 	CAMPUS_BUILDINGS;
	
	protected static final	Locale		DEFAULT_LOCALE					=	Locale.US;
	
	protected static final 	String 		CAPACITY;
	protected static final 	String 		POWER;
	protected static final	String		SEARCH_GDC_ONLY;
	protected static final	String		SEARCH_BUILDING;
	protected static final	String		SEARCH_ROOM						=	"search_room";
	protected static final	String		RANDOM							=	"Random";
	
	protected static final	int			MAX_CAPACITY					=	50;

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
	
	/* Optimization flags */
	protected static final	boolean		STORE_LOCAL_COPY_CSV_FEEDS		=	true;
	protected static final	boolean		INCLUDE_GDC_CONFERENCE_ROOMS	=	false;
	protected static final	boolean		INCLUDE_GDC_LOBBY_ROOMS			=	false;
	protected static final	boolean		INCLUDE_GDC_LOUNGE_ROOMS		=	false;
	protected static final	boolean		SHORT_CIRCUIT_SEARCH_FOR_ROOM	=	false;
	
	protected static final	double		DEFAULT_HASHMAP_LOAD_FACTOR		=	0.75;
	
	static {
		
		DEBUG						=	false;

		GDC							=	"GDC";
		
		ALL_DAY						=	"all day";
		ATRIUM						=	"Atrium";
		
		CAMPUS_BUILDINGS			=	initialise_campus_buildings();
		
		CLASS						=	"class";
		CONFERENCE					=	"conference";
		LAB							=	"lab";
		LECTURE_HALL				=	"lecture hall";
		LOBBY						=	"lobby";
		LOUNGE						=	"lounge";
		SEMINAR						=	"seminar";

		VALID_GDC_ROOMS				=	initialise_valid_gdc_rooms();
		VALID_GDC_ROOMS_TYPES		=	initialise_valid_gdc_rooms_types();
		VALID_GDC_ROOMS_CAPACITIES	=	initialise_valid_gdc_rooms_capacities();
		VALID_GDC_ROOMS_POWERS		=	initialise_valid_gdc_rooms_powers();

		DEPARTMENTS					=	initialise_departments();
		
		IGNORE_ROOMS				=	initialise_ignore_rooms();

		DEFAULT_GDC_LOCATION		=	"Gateshenge";
		EVENT_NAME					=	"event_name";
		GDC_ATRIUM					=	new Location(GDC + " " + ATRIUM);
		GDC_GATESHENGE				=	new Location(GDC + " " + DEFAULT_GDC_LOCATION);

		LOCATION					=	"location";
		
		CAPACITY					=	"capacity";
		POWER						=	"power";
		SEARCH_GDC_ONLY				=	"search_gdc_only";
		SEARCH_BUILDING				=	"search_building";

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
		
		MONTHS_LONG					=	initialise_months_long();
		MONTHS_SHORT				=	initialise_months_short();

		END_DATE					=	"end_date";

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
		LAST_TIME_OF_DAY			=	2230;
		LAST_TIME_OF_NIGHT			=	730;
		
		UTCS_CSV_FEED_FORMAT		=	"EEE dd MMM yyyy HHmm";
		US_DATE_24H_TIME_FORMAT		=	"MMM dd yyyy HHmm";
		US_DATE_NO_TIME_FORMAT		=	"MMM dd yyyy";

		DEFAULT_ROOM_HAS_POWER		=	false;
		DEFAULT_ROOM_TYPE			=	CLASS;

		has_feed_been_read			=	false;

		/* ################################# DO NOT MOVE ANYTHING BELOW THIS LINE ABOVE IT ################################# */
		
		DAYBREAK					=	Utilities.get_date(1, 2, 2014, LAST_TIME_OF_NIGHT);
		NIGHTFALL					=	Utilities.get_date(1, 1, 2014, LAST_TIME_OF_DAY);
		
		if (COURSE_SCHEDULE_NEXT_SEMESTER == null) {
			DISABLE_SEARCHES_NEXT_SEMESTER = true;
		}
		else {
			DISABLE_SEARCHES_NEXT_SEMESTER = false;
		}
		
		Log.d(TAG, "Reached end of static initialiser block");
	}
	
	protected static int lines_read = 0;
	protected static double time_to_read = 0;

	private static void reset() {
		CSV_FEEDS_MASTER = null;
		has_feed_been_read = false;
		CSV_FEEDS_CLEANED = null;
		
		BUILDING_CACHELIST_THIS_SEMESTER = null;
		BUILDING_CACHELIST_NEXT_SEMESTER = null;
	}
	
	private static void init_shared_prefs(Context context) {
		if (context == null) {
			throw new IllegalArgumentException("Context argument cannot be null.");
		}
		
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		if (!prefs.contains(CSV_FEEDS_WRITE_SUCCESS)) {
			SharedPreferences.Editor prefs_edit = prefs.edit();
			prefs_edit.putBoolean(CSV_FEEDS_WRITE_SUCCESS, false);
			prefs_edit.apply();
		}
		
		if (!prefs.contains(CSV_FEED_ALL_EVENTS_WRITE_SUCCESS)) {
			SharedPreferences.Editor prefs_edit = prefs.edit();
			prefs_edit.putBoolean(CSV_FEED_ALL_EVENTS_WRITE_SUCCESS, false);
			prefs_edit.apply();
		}
		
		if (!prefs.contains(CSV_FEED_ALL_ROOMS_WRITE_SUCCESS)) {
			SharedPreferences.Editor prefs_edit = prefs.edit();
			prefs_edit.putBoolean(CSV_FEED_ALL_ROOMS_WRITE_SUCCESS, false);
			prefs_edit.apply();
		}
		
		if (!prefs.contains(CSV_FEED_ALL_TODAYS_EVENTS_WRITE_SUCCESS)) {
			SharedPreferences.Editor prefs_edit = prefs.edit();
			prefs_edit.putBoolean(CSV_FEED_ALL_TODAYS_EVENTS_WRITE_SUCCESS, false);
			prefs_edit.apply();
		}
	}
	
	protected static void init(Context context) {
		init(context, DEBUG);
	}
	
	protected static void init(Context context, boolean read_from_local_feeds) {
		if (context == null) {
			throw new IllegalArgumentException("Context argument cannot be null.");
		}
		else if (COURSE_SCHEDULE_THIS_SEMESTER == null) {
			throw new IllegalArgumentException("App needs at least the current course schedule to correctly function.");
		}

		reset();
//		delete_all_feeds(context);
		
		init_shared_prefs(context);

		CSV_FEEDS_MASTER = CSVReader.read_csv(context, read_from_local_feeds);		
		if (CSV_FEEDS_MASTER == null) {
			throw new IllegalStateException("Unknown error occurred while reading CSV feeds; CSV_FEEDS_MASTER is null");
		}
		
		CSV_FEEDS_CLEANED = get_events_cleaned();
		
		Log.d(TAG, "Now finished reading CSV feeds");
		Log.d(TAG, "Size of CSV_FEEDS_MASTER: " + CSV_FEEDS_MASTER.get_size());
		Log.d(TAG, "Size of CSV_FEEDS_CLEANED: " + CSV_FEEDS_CLEANED.get_size());
		
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start();
		
		Log.d(TAG, "Now creating initial BuildingLists");

		BUILDING_CACHELIST_THIS_SEMESTER = new BuildingList();
		final Building gdc_instance_this_semester = Building.get_instance(context, GDC, COURSE_SCHEDULE_THIS_SEMESTER);
		if (gdc_instance_this_semester == null) {
			throw new IllegalStateException("Fatal error: failed to get GDC Building instance for this semester");
		}
		
		stopwatch.stop();
		time_to_read += stopwatch.time();
		
//		Log.d(TAG, "Took " + stopwatch.time() + " seconds to read GDC course schedules");
		
		if (DEBUG) {
			Log.d(TAG, "Num rooms in GDC list: " + BUILDING_CACHELIST_THIS_SEMESTER.get_building(GDC).get_num_rooms());
		}
		
		if (COURSE_SCHEDULE_NEXT_SEMESTER != null) {
			BUILDING_CACHELIST_NEXT_SEMESTER = new BuildingList();
			
			final Building gdc_instance_next_semester = Building.get_instance(context, GDC, COURSE_SCHEDULE_NEXT_SEMESTER);
			if (gdc_instance_next_semester == null) {
				throw new IllegalStateException("Fatal error: failed to get GDC Building instance for next semester");
			}
		}

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
		if (CSV_FEEDS_MASTER == null) {
			throw new IllegalStateException("ERROR: CSV_FEEDS_MASTER is null, Constants.get_events_cleaned()");
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

	private static final Map<String, Integer> initialise_campus_buildings() {
		final String[] CAMPUS_BUILDINGS = {
				"ACA", "AHG", "ART", "ATT",
				"BAT", "BEL", "BEN", "BIO", "BMC", "BME", "BRB", "BTL", "BUR",
				"CAL", "CBA", "CCJ", "CLA", "CMA", "CMB", "CRD", "DFA",
				"ECJ", "ENS", "EPS", "ETC",
				"FAC", "FDH", "FNT",
				"GAR", "GDC", "GEA", "GEB", "GOL", "GRE", "GSB",
				"HRC", "HRH",
				"INT",
				"JES", "JGB", "JON",
				"LTH",
				"MAI", "MBB", "MEZ", "MRH",
				"NEZ", "NHB", "NMS", "NOA", "NUR",
				"PAC", "PAI", "PAR", "PAT", "PCL", "PHR", "POB",
				"RLM", "RSC",
				"SAC", "SEA", "SRH", "SSB", "SSW", "STD", "SUT", "SZB",
				"TNH", "TSC",
				"UTA", "UTC",
				"WAG", "WCH", "WEL", "WIN", "WMB", "WRW"
		};
		
		final Map<String, Integer> CAMPUS_BUILDINGS_MAP = new HashMap<String, Integer>(Utilities.get_hashmap_size(CAMPUS_BUILDINGS.length));
		for (int i = 0; i < CAMPUS_BUILDINGS.length; i++) {
			CAMPUS_BUILDINGS_MAP.put(CAMPUS_BUILDINGS[i], i);
		}
		
		return CAMPUS_BUILDINGS_MAP;
	}
	
	private static final int[] initialise_days_in_month() {
		final int[] DAYS_IN_MONTH =	{ 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
		return DAYS_IN_MONTH;
	}
	
	private static final String[] initialise_days_of_week_long() {
		final String[] DAYS_OF_WEEK_LONG = { "", "SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY" };
		return DAYS_OF_WEEK_LONG;
	}
	
	private static final String[] initialise_days_of_week_short() {
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
				"2.104", "2.21", "2.216", "2.402", "2.41", "2.502", "2.506", "2.712", "2.902",
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



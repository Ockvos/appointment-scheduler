package model;

/**
 *  A class housing commonly used variables.
 *  This class is used by the developer to easily alter data for testing purposes.
 *  Be aware changing variables in this class will result in widespread changes
 *  across the application.
 */
public class Globals {

    // Constants used to define GUI visuals
    public static final String WINDOW_NAME = "Appointment Scheduler";

    public static final int LOGIN_WIDTH = 340;
    public static final int LOGIN_HEIGHT = 400;

    public static final int MAIN_MENU_WIDTH = 600;
    public static final int MAIN_MENU_HEIGHT = 400;

    public static final int PREFERENCES_WIDTH = 232;
    public static final int PREFERENCES_HEIGHT = 250;

    public static final int APPOINTMENTS_WIDTH = 900;
    public static final int APPOINTMENTS_HEIGHT = 400;

    public static final int CUST_RECORDS_WIDTH = 680;
    public static final int CUST_RECORDS_HEIGHT = 400;

    public static final int TYPE_REPORTS_WIDTH = 500;
    public static final int TYPE_REPORTS_HEIGHT = 320;


    // Visual resource paths
    public static final String PROFILE_1 = "/resources/profileOwl1.png";
    public static final String PROFILE_2 = "/resources/profileOwl2.png";
    public static final String PROFILE_3 = "/resources/profileOwl3.png";
    public static final String CHECK_MARK_ALERT = "/resources/checkMark.png";
    public static final String INFO_ALERT = "/resources/infoSymbol.png";
    public static final String APPLICATION_ICON = "/resources/iconOwl.png";


    // Logging text file
    public static final String LOGGING_FILE_PATH = "LOGIN_ATTEMPTS.txt";
    public static final boolean generateFiles = true;  // When true, files (such as txt) can be generated


    // Default user
    public static final int defaultUserId = 1;  // Used during auto login
    public static final String defaultUsername = "test";
    public static final String defaultUserPassword = "test";


    // Company hours and timezone. Local company time will be auto calculated from this root (24hr time format)
    public static String companyTimezone = "America/New_York";

    public static final int companyStartHour = 8;  // 8AM
    public static final int companyStartMinute = 0;

    public static final int companyEndHour = 22; // 10PM (use companyOpenDuration to change end hour instead)
    public static final int companyEndMinute = 0;

    public static final int companyOpenDuration = 840;  // Amount of minutes from company open to company close (840m = 14h), use this to extend company hours.


    // Alerts
    public static int alertTimeframe = 15;  // Amount of minutes in advance to scan for upcoming appointments


    // Database variables
    public static String databaseUsername = "sqlUser";
    public static String databasePassword = "Passw0rd!";
    public static final boolean writeToDatabase = true;  // When false, data generated will not be saved when application closes


    // Debugging variables
    public static final String versionNumber = "1.0";
    public static final boolean autoLogin = false; // Used during development to bypass login credentials
    public static final boolean debugMode = true;  // When true, useful data will be printed to console


    // Appointment overlaps
    public static final boolean checkOverlapCustomers = true;  // When true do not allow two customers to have overlapping appointments.
    public static final boolean checkOverlapContacts = false;  // When true do not allow two contacts to have overlapping appointments.


}

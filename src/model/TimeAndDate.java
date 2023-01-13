package model;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;



/**
 * A class used to store all date and time keeping functions.
 */
public class TimeAndDate {

    public enum Unit {
        DAY,
        WEEK,
        MONTH
    }

    public static Integer[] defaultHourRange = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23};

    public static String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    public static List<Integer> localHoursList = new ArrayList<Integer>();  // If company hours are 8-22, hoursList would be [8,9,...21,22]
    public static int localStartMinute = 0;  // If company opens at 9:15, startMin is 15
    public static int localEndMinute = 0;  // If company closes at 20:45, startMin is 45
    public static ZonedDateTime localStartTime;
    public static ZonedDateTime localEndTime;


    /**
     * Gets local timezone.
     * @return Local ZoneId
     */
    public static ZoneId getTimeZone() {

        // Get current time
        ZonedDateTime zoneddatetime = ZonedDateTime.now();

        // Get & return time zone
        return ZoneId.from(zoneddatetime);
    }


    /**
     * Gets local time but in ZonedDateTime format.
     * @return Local ZonedDateTime.
     */
    public static ZonedDateTime getSystemZonedTime() {
        return ZonedDateTime.now();
    }


    /**
     * Gets current local time and date.
     * @return Current LocalDateTime
     */
    public static LocalDateTime getCurrentDateTime() {  // Returns exact current time and date
        return LocalDateTime.now();
    }


    /**
     * Gets current year.
     * @return Current year.
     */
    public static int getCurrentYear() {
        return LocalDateTime.now().getYear();
    }


    /**
     * Gets integer value of month.
     * For example: January is 1, December is 12.
     * @return Integer value of month.
     */
    public static int getCurrentMonthValue() {
        return LocalDateTime.now().getMonthValue();
    }


    /**
     * Gets current day value.
     * For example: If date is July 15, returns 15.
     * @return Integer day of month.
     */
    public static int getCurrentDayValue() {return LocalDateTime.now().getDayOfMonth();}


    /**
     * Gets current hour value.
     * For example: If time is 8:00AM, returns 8.
     * @return Current hour value.
     */
    public static int getCurrentHour() {return LocalDateTime.now().getHour();}


    /**
     * Gets company timezone.
     * For example: If company timezone is EST, ZoneId will be EST.
     * @return ZoneId of company.
     */
    public static ZoneId getCompanyTimeZone() {
        return ZoneId.of(Globals.companyTimezone);
    }


    /**
     * Gets hour range the company is open.
     * For example: If company is open 8:00-20:00, hour range is [8,...,20]
     * @return Company hour range.
     */
    public static List<Integer> getCompanyHourRange() {
        return localHoursList;
    }


    /**
     * Takes current system time and converts it to current company time.
     * @return Current ZonedDateTime at company.
     */
    public static ZonedDateTime getCurrentCompanyTime() {

        return getSystemZonedTime().withZoneSameInstant(getCompanyTimeZone());

    }


    /**
     * Advances time by one month and returns that.
     * @return LocalDateTime one month from current time.
     */
    public static LocalDateTime nextMonth() {
        return LocalDateTime.now().plusMonths(1);
    }


    /**
     *  Turns time back by one month and returns that.
     * @return LocalDateTime one month prior current time.
     */
    public static LocalDateTime previousMonth() {  // Advances current date by 1 week
        return LocalDateTime.now().minusMonths(1);
    }


    /**
     * Advances time by one week and returns that.
     * @return LocalDateTime one week from current time.
     */
    public static LocalDateTime nextWeek() {  // Advances current date by 1 week
        return LocalDateTime.now().plusWeeks(1);
    }


    /**
     *  Turns time back by one week and returns that.
     *  @return LocalDateTime one week prior current time.
     */
    public static LocalDateTime previousWeek() {  // Advances current date by 1 week
        return LocalDateTime.now().minusWeeks(1);
    }


    /**
     * Advances time by one day and returns that.
     * @return LocalDateTime one day from current time.
     */
    public static LocalDateTime nextDay() {  // Advances current date by 1 week
        return LocalDateTime.now().plusDays(1);
    }

    /**
     *  Turns time back by one day and returns that.
     *  @return LocalDateTime one day prior current time.
     */
    public static LocalDateTime previousDay() {  // Advances current date by 1 week
        return LocalDateTime.now().minusDays(1);
    }

    // Advances LocalDateTime using specific amount of minutes

    /**
     *  Advances time forward in minutes.
     *  @param originalDate the date to advance.
     *  @param minute amount of minutes to advance time.
     *  @return LocalDateTime one week prior current time.
     */
    public static LocalDateTime advanceTime(LocalDateTime originalDate, int minute) {

        LocalDateTime newDate = originalDate.plusMinutes(minute);

        return newDate;
    }

    /**
     *  Converts a time in UTC to local time.
     *  @param ldt UTC time to convert.
     *  @return UTC time converted to local time.
     */
    public static ZonedDateTime toLocalTime(LocalDateTime ldt) {


        // Convert to ZoneDateTime at origin ZoneID
        ZonedDateTime zonedDateTime = ldt.atZone(ZoneId.of("UTC"));  // Tells it current input time is in UTC



        // Console output
        System.out.println("-----toLocalTime-----");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDateTime = zonedDateTime.format(formatter);
        System.out.println("Zone Prior (UTC): " + formattedDateTime + zonedDateTime.getZone());

        zonedDateTime = zonedDateTime.withZoneSameInstant(ZoneId.systemDefault());  // withZoneSameInstant simply does timezone conversions, in this case we are telling it convert UTC to system default (EST)
        LocalDateTime convertedTime = zonedDateTime.toLocalDateTime();


        // Console output
        formattedDateTime = convertedTime.format(formatter);
        System.out.println("Zone After (EST): " + formattedDateTime + zonedDateTime.getZone());

        return zonedDateTime;
    }

    /**
     *  Converts LocalDateTime to string.
     *  @param ldt Time to convert to string.
     *  @return String version of LocalDateTime.
     */    public static String toLocalTimeFormattedString(LocalDateTime ldt) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        return (ldt.format(formatter));
    }


    /**
     *  Given a time and custom formatting, will return string in that formatting.
     *  @param ldt The time to be formatted.
     *  @param format The style to format time in.
     *  @return Formatted string time.
     */
    public static String customTimeFormattedString(LocalDateTime ldt, String format) {

        // Special case for interacting with database
        if (Objects.equals(format, "''")) {
            return ("'"+ ldt + "'");
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

        return (ldt.format(formatter));
    }


    /**
     *  Given a ZonedDateTime and custom formatting, will return string in that formatting.
     *  @param ldt The time to be formatted.
     *  @param format The style to format time in.
     *  @return Formatted string time.
     */
    public static String customTimeFormattedString(ZonedDateTime ldt, String format) {

        // Special case for interacting with database
        if (Objects.equals(format, "''")) {
            return ("'"+ ldt + "'");
        }


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);

        return (ldt.format(formatter));
    }




    /**
     *  Converts local time to UTC
     *  @param ldt The time to be converted.
     *  @return ZonedDateTime in UTC.
     */
    public static ZonedDateTime toUTC(LocalDateTime ldt) {

        ZonedDateTime zonedDateTime = ldt.atZone(ZoneId.systemDefault());  // Tells it current input timezone is system timezone


        zonedDateTime = zonedDateTime.withZoneSameInstant(ZoneId.of("UTC"));  // withZoneSameInstant simply does timezone conversions, in this case we are telling it convert UTC to system default (EST)


        boolean printConversionsToConsole = false;

        if (printConversionsToConsole) {  // Used for testing the functionality of the method only

            System.out.println("-----toUTC-----");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = zonedDateTime.format(formatter);
            System.out.println("Zone Prior (EST): " + formattedDateTime + " " + zonedDateTime.getZone());


            LocalDateTime convertedTime = zonedDateTime.toLocalDateTime();
            formattedDateTime = convertedTime.format(formatter);
            System.out.println("Zone After (UTC): " + formattedDateTime + " " + zonedDateTime.getZone());
        }

        return zonedDateTime;
    }


    /**
     *  Determines how many minutes the company will remain open after start of meeting.
     *  All times converted to local time.
     *  @param meetingEndHour The hour the appointment ends.
     *  @param meetingEndMinute The minute the appointment ends.
     *  @return Double representing amount of minutes the company is open after meeting start.
     */
    public static double calculateMinutesTillClose(int meetingEndHour, int meetingEndMinute) {

        return  (((getLocalEndHour() * 60) + getLocalEndMinute()) - ((meetingEndHour * 60) + meetingEndMinute));

    }


    /**
     *  Used to manually set the time of a LocalDateTime.
     *  @param date The date to be used.
     *  @param hour The hour to be used.
     *  @param minute The minute to be used.
     *  @return Manually created LocalDateTime.
     */
    public static LocalDateTime setHourMin(LocalDateTime date, int hour, int minute) {
        LocalDateTime ldt = LocalDateTime.of(date.getYear(), date.getMonth(), date.getDayOfMonth(), hour, minute, 0);
        return ldt;
    }


    /**
     *  Returns amount (in seconds) that local time differs from UTC.
     *  Negative number indicates local timezone is behind UTC by that amount.
     *  Positive number indicates local timezone is ahead of UTC by that amount.
     *  This technique also compensates for daylight savings.
     *  @return Timezone difference in minutes.
     */
    public static int calculateOffsetLocal() {

        ZoneOffset zoneOffset = ZonedDateTime.now().getOffset();

        int totalSec = zoneOffset.getTotalSeconds();

        // Convert to minutes
        return (totalSec / 60);
    }


    /**
     *  Returns amount that local time differs from UTC.
     *  Negative number indicates local timezone is behind UTC by that amount.
     *  Positive number indicates local timezone is ahead of UTC by that amount.
     *  This technique also compensates for daylight savings.
     *  @return ZoneOffset of local time in relation to UTC
     */
    public static ZoneOffset  getZoneOffset() {

        return ZonedDateTime.now().getOffset();
    }


    /**
     *  Returns amount that local time differs from UTC.
     *  Negative number indicates local timezone is behind UTC by that amount.
     *  Positive number indicates local timezone is ahead of UTC by that amount.
     *  This technique also compensates for daylight savings.
     *  If correct offset is not found, default is UTC.
     *  @param zoneOffset Offset to convert to string.
     *  @return Offset converted to string.
     */
    public static String getZoneOffsetString(ZoneOffset zoneOffset) {


        String offset = zoneOffset.toString();


        if (offset.contains("+") || offset.contains("-")) {  // Make sure offset is legitimate
            return zoneOffset.toString();

        } else {  // UTC has an offset of Z, so numeric offset must be manually set.

            return "+0:00";
        }
    }


    /**
     *  Checks if appointment ends after company end.
     *  @param ldt Appointment time to check.
     *  @return True if appointment ends after company end.
     */
    public static boolean appointmentEndAfterCompanyEnd(LocalDateTime ldt) {

       if(ldt.isBefore(localEndTime.toLocalDateTime())) {
           return false;
       }
       else {
           return true;
       }
    }


    /**
     *  Calculates what hours company is open, converting to local time
     *  For example: if the company is open from 08:30-22:30 EST, and the user is in MST, (2 hrs behind),
     *  all the hours will display as 6:30-20:30 MST.
     */
    public static void calculateCompanyHourRange() {

        ZonedDateTime companyOpenHour = ZonedDateTime.of(getCurrentYear(),getCurrentMonthValue(),getCurrentDayValue(),Globals.companyStartHour,Globals.companyStartMinute,0,0, getCompanyTimeZone());

        ZonedDateTime companyCloseHour = companyOpenHour.plusMinutes(Globals.companyOpenDuration);

        ZonedDateTime localOpenTime = companyOpenHour.withZoneSameInstant(ZoneId.systemDefault());
        setLocalStartTime(localOpenTime);

        ZonedDateTime localCloseTime = companyCloseHour.withZoneSameInstant(ZoneId.systemDefault());
        setLocalEndTime(localCloseTime);

        // Determine open hour range in local time
        int openHr = localOpenTime.getHour();
        int openMin = localOpenTime.getMinute();

        setLocalStartMinute(openMin);

        int closeHr = localCloseTime.getHour();
        int closeMin = localCloseTime.getMinute();

        setLocalEndMinute(closeMin);

        localHoursList.clear();  // Start work with clean slate

        for (int i = 0; i <= Duration.between(localOpenTime, localCloseTime).toHours(); i++) {
            localHoursList.add(localOpenTime.plusHours(i).getHour());
        }
    }


    /**
     *  Compare two dates returns difference in minutes.
     * @param ldt1 Date one to compare.
     * @param ldt2 Date two to compare.
     * @return Integer difference in minutes between the two dates.
     */
    public static int determineDifferenceMinutes(LocalDateTime ldt1, LocalDateTime ldt2) {

        return (int) Duration.between(ldt1, ldt2).toMinutes();  // Safe to cast to int since for this application results will never be long
    }

    /**
     *  Parses string to integer, if string has leading '0' this is removed.
     * @param stringTime The string time to be converted.
     */
    public static int parseTime(String stringTime) {

        int intTime;

        // Check if input has leading 0
        if(stringTime.charAt(0) == '0') {

            stringTime = stringTime.substring(1);  // Remove leading 0

        }

        // Parse to integer
        intTime = Integer.parseInt(stringTime);

        return intTime;
    }

    /**
     *  Gets number associated with month.
     *  For example: December is month number 12.
     *  @param inputMonth The month to find associated number of.
     *  @return The number associated with given month.
     */
    public static int monthToNumber(String inputMonth) {

        inputMonth = inputMonth.toLowerCase();

        for (int i = 0; i < monthNames.length; i++) {
            if (monthNames[i].toLowerCase().equals(inputMonth)) {
                return i + 1; // Index starts at 0, so 1 must be added to get logical month number
            }
        }

        Tools.consoleMessage(Tools.MsgType.ERROR, "Bad input", "TimeAndDate.monthToNumber()");
        return -1;  // Error case
    }


    /**
     *  Gets name associated with month number.
     *  For example: 12 is the month of December.
     *  @param number The number to find the month name of.
     *  @return Month associated with number.
     */
    public static String numberToMonth(int number) {

        number = number - 1; // Index starts at 0 so in reality December = 11

        if (number < monthNames.length) {
            return monthNames[number];
        }
        else {
            Tools.consoleMessage(Tools.MsgType.ERROR, "Bad input", "TimeAndDate.numberToMonth()");
            return "N/A";
        }
    }


    /**
     *  Sets local minute company opens at.
     *  @param startMinute The minute company opens
     */
    public static void setLocalStartMinute(int startMinute) {
        localStartMinute = startMinute;
    }


    /**
     *  Sets local minute company closes at.
     *  @param endMinute The minute company closes.
     */
    public static void setLocalEndMinute(int endMinute) {
        localEndMinute = endMinute;
    }


    /**
     *  Gets local minute company opens at.
     *  @return Minute at which company opens.
     */
    public static int getLocalStartMinute() {
        return localStartMinute;
    }


    /**
     *  Gets local minute company closes at.
     *  @return Minute at which closes opens.
     */
    public static int getLocalEndMinute() {
        return localEndMinute;
    }

    /**
     *  Gets local hour company opens at.
     *  @return Hour at which company opens.
     */
    public static int getLocalStartHour() {
        return localHoursList.get(0);
    }


    /**
     *  Gets local hour company closes at.
     *  For example: If company closes at 22:00, last open hour is 21:00.
     *  @return Last hour company is open.
     */
    public static int getLocalEndHour() {
        return localHoursList.get(localHoursList.size() - 1);
    }


    /**
     *  Sets local time company closes at.
     *  @param endTime The time company closes.
     */
    private static void setLocalEndTime(ZonedDateTime endTime) {
        localEndTime = endTime;
    }


    /**
     *  Sets local time company opens at.
     *  @param startTime The time company opens.
     */
    public static void setLocalStartTime(ZonedDateTime startTime) {
        localStartTime = startTime;
    }


    /**
     *  Gets local time company opens.
     *  @return ZonedDateTime at which company opens.
     */
    public static ZonedDateTime getLocalStartTime() {return localStartTime;}


    /**
     *  Gets local time company closes
     *  @return ZonedDateTime at which company closes.
     */
    public static ZonedDateTime getLocalEndTime() {return localEndTime;}

}

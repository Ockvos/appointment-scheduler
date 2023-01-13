package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Appointment;
import model.Globals;
import model.TimeAndDate;
import model.Tools;

import java.sql.*;
import java.time.*;
import java.util.ArrayList;
import java.util.List;


/**
 *  This class allows appointments to be written, updated, and deleted from database.
 *
 */
public class AppointmentQuery {


    /**
     * Retrieves all appointments IDs from database.
     * @return List containing all appointment IDs found in database.
     */
    public static List<Integer> fetchAllId() throws SQLException {

        String sql = "SELECT Customer_Id FROM appointments;";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();  // a result set is essentially an entire table, so we can fetch values from it

        List<Integer> allAppointmentId = new ArrayList<>();

        while(rs.next()){  //rs will read line by line through the results returned

            // fetch appointment_id and add it to list containing all appointments
            allAppointmentId.add(rs.getInt("Customer_ID"));
        }

        return allAppointmentId;
    }


    /**
     * Retrieves all appointments from database, creates local appointment objects.
     * Adds appointments to a static list located in Appointments class.
     */
    public static void fetchAllAppointments() throws SQLException {


        String sql = "SELECT * FROM appointments ORDER BY Start;";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();  // a result set is essentially an entire table, so we can fetch values from it, see below

        int appointmentId;
        int customerId;
        int contactId;
        int userId;
        String title;
        String description;
        String location;
        String type;
        String createdBy;
        String lastUpdatedBy;
        LocalDateTime startDate;
        LocalDateTime endDate;
        LocalDateTime creationDate;
        LocalDateTime lastUpdate;


        while(rs.next()){  //rs will read line by line through the results returned

            // fetch data from current row
            appointmentId = rs.getInt("Appointment_ID");
            customerId = rs.getInt("Customer_ID");
            contactId = rs.getInt("Contact_ID");
            userId = rs.getInt("User_ID");
            title = rs.getString("Title");
            description = rs.getString("Description");
            location = rs.getString("Location");
            type = rs.getString("Type");
            createdBy = rs.getString("Created_By");
            lastUpdatedBy = rs.getString("Last_Updated_By");
            startDate = rs.getTimestamp("Start").toLocalDateTime();  // This will convert UTC to local time
            endDate = rs.getTimestamp("End").toLocalDateTime();
            creationDate = rs.getTimestamp("Create_Date").toLocalDateTime();
            lastUpdate = rs.getTimestamp("Last_Update").toLocalDateTime();

            Appointment newAppointment = new Appointment(startDate, endDate, customerId, appointmentId, contactId, title, description, location, type, creationDate, lastUpdate, createdBy, lastUpdatedBy, userId);

            Appointment.allAppointments.add(newAppointment);
        }
    }


    /**
     * Checks the database to see if an appointment ID exists.
     * @param id The ID to be checked.
     * @return True if appointment ID already exists.
     */
    public static boolean checkAppointmentIdExists(int id) throws SQLException {

        String sql = "SELECT Appointment_ID FROM appointments ORDER BY Start;";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();  // a result set is essentially an entire table, so we can fetch values from it, see below

        List<Integer> allAppointmentId = new ArrayList<>();

        while(rs.next()){  // rs will read line by line through the results returned

            // fetch appointment_id and add it to list containing all appointments
            allAppointmentId.add(rs.getInt("Appointment_ID"));
        }


        return allAppointmentId.contains(id);
    }


    /**
     * Updates an appointment in database, by overwriting old appointment with new one.
     * @param newAppointment The new appointment to be written to database.
     * @param appointmentId The ID of the appointment to be overwritten.
     */
    public static void updateAppointment(Appointment newAppointment, int appointmentId) throws SQLException {

        String sql = "DELETE FROM Appointments WHERE Appointment_Id = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, appointmentId);

        ps.executeUpdate();

        writeAppointment(newAppointment);
    }

    /**
     * Writes new appointment to database.
     * @param appointment The appointment object to be written to database.
     */
    public static void writeAppointment(Appointment appointment) throws SQLException {

        if (Globals.writeToDatabase) {

            String sql = "INSERT INTO appointments values(?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ps.setInt(1, appointment.getAppointmentId());
            ps.setString(2, appointment.getTitle());
            ps.setString(3, appointment.getDescription());
            ps.setString(4, appointment.getLocation());
            ps.setString(5, appointment.getType());
            ps.setTimestamp(6, appointment.getStartDateTimestamp());
            ps.setTimestamp(7, appointment.getEndDateTimestamp());
            ps.setTimestamp(8, appointment.getCreateDateTimestamp());
            ps.setString(9, appointment.getCreatedBy());
            ps.setTimestamp(10, appointment.getLastUpdateTimestamp());
            ps.setString(11, appointment.getLastUpdatedBy());
            ps.setInt(12, appointment.getCustomerId());
            ps.setInt(13, appointment.getUserId());
            ps.setInt(14, appointment.getContactId());

            ps.executeUpdate();

        }
    }


    /**
     * Deletes appointment in database.
     * @param appointmentId The ID of appointment to be deleted.
     */
    public static void deleteAppointment(int appointmentId) throws SQLException {

        if (Globals.writeToDatabase){
            String sql = "DELETE from appointments WHERE Appointment_Id = ?;";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ps.setInt(1, appointmentId);

            ps.executeUpdate();
        }
    }


    /**
     * Retrieves appointments based on type and month.
     * Used for reporting.
     * @return List containing report information.
     */
    public static ObservableList<String>  fetchAppointmentsByTypeAndMonth() throws SQLException {


        String sql = "SELECT EXTRACT(MONTH FROM CONVERT_TZ(Start, ?, ?)) AS Month_Number , Type, count(Type) AS Amount FROM appointments " +
                     "GROUP BY Type, Month_Number " +
                     "ORDER BY Month_Number, Type;";

        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1,"+00:00");
        ps.setString(2,TimeAndDate.getZoneOffsetString(TimeAndDate.getZoneOffset()));
        ResultSet rs =  ps.executeQuery();

        ObservableList<String> appointmentsByTypeAndMonth = FXCollections.observableArrayList();
        appointmentsByTypeAndMonth.add("MONTH,   TYPE,   AMOUNT");  // First index value acts as a label

        while(rs.next()){  //rs will read line by line through the results returned

            // First data containing row is formatted differently
            if (appointmentsByTypeAndMonth.size() == 1) {
                appointmentsByTypeAndMonth.add("\n\n"+TimeAndDate.numberToMonth(rs.getInt( "Month_Number")));  // Add month name (first convert it from number to name)
            }
            else {
                appointmentsByTypeAndMonth.add("\n"+TimeAndDate.numberToMonth(rs.getInt( "Month_Number")));
            }
            appointmentsByTypeAndMonth.add("  " + rs.getString("Type"));  // Add appointment type to array
            appointmentsByTypeAndMonth.add("  "  + rs.getString("Amount"));  // Add amount that type occurs per month
       }

        Tools.consoleMessage(Tools.MsgType.INFO, "For me info on how this report is generated, check this method.", "AppointmentQuery.fetchAppointmentsByTypeAndMonth()");

        return appointmentsByTypeAndMonth;
    }


    /**
     * Retrieves appointments matching contact ID.
     * Used for reporting.
     * @param contactId The ID of the contact to associate with appointments.
     * @return A list containing all appointments associated with contact.
     */
    public static ObservableList<String> fetchAppointmentsByContact(int contactId) throws SQLException {

        String sql = "SELECT * FROM Appointments WHERE Contact_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, contactId);

        ResultSet rs =  ps.executeQuery();

        ObservableList<String> appointments = FXCollections.observableArrayList();

        while(rs.next()){  //rs will read line by line through the results returned

            // fetch appointment_id and add it to list containing all appointments
            appointments.add(rs.getString("Appointment_ID"));
        }

        return appointments;
    }


    /**
     * Retrieves appointments between current datetime, and future datetime.
     * @param minutesFromCurrent The amount of minutes into the future to check.
     * @return A list containing all appointments between current datetime and future datetime.
     */
    public static Appointment fetchAppointmentsBetweenDates(long minutesFromCurrent) throws SQLException {

        String sql = "SELECT * FROM Appointments WHERE " +
                     "Start BETWEEN ? AND ? " +
                     "ORDER BY Start desc;";

        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        String t1 = TimeAndDate.customTimeFormattedString(TimeAndDate.toUTC(LocalDateTime.now()), "YYYY-MM-dd HH:mm:ss");
        String t2 = TimeAndDate.customTimeFormattedString(TimeAndDate.toUTC(LocalDateTime.now().plusMinutes(minutesFromCurrent)), "YYYY-MM-dd HH:mm:ss");


        ps.setString(1, t1);
        ps.setString(2, t2);
        ResultSet rs =  ps.executeQuery();

        int appointmentId;
        int customerId;
        int contactId;
        int userId;
        String title;
        String description;
        String location;
        String type;
        String createdBy;
        String lastUpdatedBy;
        LocalDateTime startDate;
        LocalDateTime endDate;
        LocalDateTime creationDate;
        LocalDateTime lastUpdate;


        if (rs.next()){  //rs will read line by line through the results returned

            // fetch data from current row
            appointmentId = rs.getInt("Appointment_ID");
            customerId = rs.getInt("Customer_ID");
            contactId = rs.getInt("Contact_ID");
            userId = rs.getInt("User_ID");
            title = rs.getString("Title");
            description = rs.getString("Description");
            location = rs.getString("Location");
            type = rs.getString("Type");
            createdBy = rs.getString("Created_By");
            lastUpdatedBy = rs.getString("Last_Updated_By");
            startDate = rs.getTimestamp("Start").toLocalDateTime();  // This will convert UTC to local time
            endDate = rs.getTimestamp("End").toLocalDateTime();
            creationDate = rs.getTimestamp("Create_Date").toLocalDateTime();
            lastUpdate = rs.getTimestamp("Last_Update").toLocalDateTime();

            return new Appointment(startDate, endDate, customerId, appointmentId, contactId, title, description, location, type, creationDate, lastUpdate, createdBy, lastUpdatedBy, userId);
        }

        return null;
    }


    /**
     * Retrieves appointments matching customer ID.
     * @param customerId The ID of the customer associated with appointments.
     * @return Integer list containing all appointment IDs associated with customer.
     */
    public static List<Integer> fetchAppointmentsByCustomer(int customerId) throws SQLException {

        String sql = "SELECT Appointment_ID FROM appointments WHERE Customer_ID = ? ORDER BY Start;";

        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, customerId);
        ResultSet rs = ps.executeQuery();  // a result set is essentially an entire table, so we can fetch values from it, see below

        List<Integer> allAppointmentId = new ArrayList<>();

        while(rs.next()){  //rs will read line by line through the results returned

            // fetch appointment_id and add it to list containing all appointments
            allAppointmentId.add(rs.getInt("Appointment_ID"));
        }

        return allAppointmentId;
    }
}

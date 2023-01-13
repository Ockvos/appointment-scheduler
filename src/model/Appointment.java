package model;

import DAO.AppointmentQuery;
import DAO.ContactsQuery;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;


/**
 * A class used to create appointment objects.
 */
public class Appointment {


    LocalDateTime startDate;
    LocalDateTime endDate;
    LocalDateTime creationDate;
    LocalDateTime lastUpdate;
    int userId;
    int customerId;
    int contactId;
    int appointmentId;

    String createdBy;  // The original creator
    String title;
    String description;
    String location;
    String contactName;
    String type;
    String lastUpdatedBy;

    public static String[] appointmentTypes = {"Consultation", "Project Status", "Final Debriefing"};

    public static ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    public static ObservableList<Appointment> appointmentsByWeek = FXCollections.observableArrayList();
    public static ObservableList<Appointment> appointmentsByDay = FXCollections.observableArrayList();
    public static ObservableList<Appointment> appointmentsByMonth = FXCollections.observableArrayList();

    /**
     *  Constructor used for making new appointment.
     * @param startDate Time appointment starts.
     * @param endDate Time appointment ends.
     * @param contactId Contact's ID.
     * @param customerId Customer attending the meeting.
     * @param appointmentId Appointment's unique ID.
     * @param title Title of appointment
     * @param description Appointment's description.
     * @param location Where appointment will take place.
     * @param type Type of appointment being conducted.
     * @param creationDate Time at which application created appointment.
     * @param lastUpdate Last time application modified the appointment.
     * @param createdBy Original creator.
     * @param lastUpdatedBy Last person to modify the appointment.
     * @param userId User associated with appointment, usually the user who created or last updated the appointment.
     */
    public Appointment(LocalDateTime startDate, LocalDateTime endDate, int customerId, int appointmentId, int contactId, String title, String description, String location, String type, LocalDateTime creationDate, LocalDateTime lastUpdate, String createdBy, String lastUpdatedBy, int userId) throws SQLException {
        this.startDate = startDate;
        this.endDate = endDate;
        this.creationDate = creationDate;
        this.lastUpdate = lastUpdate;
        this.customerId = customerId;
        this.contactId = contactId;
        this.appointmentId = appointmentId;
        this.createdBy = createdBy;
        this.title = title;
        this.description = description;
        this.location = location;
        this.contactName = ContactsQuery.fetchContactName(this.contactId);
        this.type = type;
        this.lastUpdatedBy = lastUpdatedBy;
        this.userId = userId;
    }


    /**
     * @return Start date.
     */
    public LocalDateTime getStartDate() {
       return startDate;
    }


    /**
     * @return Start date in Timestamp format.
     */
    public Timestamp getStartDateTimestamp() {
        return Timestamp.valueOf(startDate);
    }


    /**
     * @return End date.
     */
    public LocalDateTime getEndDate() {
        return endDate;
    }


    /**
     * @return The end date in Timestamp format.
     */
    public Timestamp getEndDateTimestamp() {
        return Timestamp.valueOf(endDate);
    }

    /**
     * @return Creation time Timestamp format.
     */
    public Timestamp getCreateDateTimestamp() {
        return Timestamp.valueOf(creationDate);
    }

    /**
     * @return Last update time Timestamp format.
     */
    public Timestamp getLastUpdateTimestamp() {
        return Timestamp.valueOf(lastUpdate);
    }


    /**
     * @return Appointment creator.
     */
    public String getCreatedBy() {return createdBy;}


    /**
     * Sets last time appointment was updated.
     */
    public void setLastUpdate(LocalDateTime lastUpdate) {
        this.lastUpdate = lastUpdate;
    }


    /**
     * @return Last time appointment was updated.
     */
    public LocalDateTime getLastUpdate() {return lastUpdate; }


    /**
     * @return Last person to update appointment.
     */
    public String getLastUpdatedBy() {return lastUpdatedBy; }


    /**
     * @return User ID.
     */
    public int getUserId() {
        return userId;
    }


    /**
     * Sets user ID.
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }


    /**
     * @return Customer ID.
     */
    public int getCustomerId() {
        return customerId;
    }


    /**
     *  Generates unique ID for appointment.
     *  @return  New appointment ID.
     */
    public static int setAppointmentId() throws SQLException {

        int id;  // ID will never = 0
        boolean idFound = false;

        id = allAppointments.size();

      if (id == 0) {id = 1;}  // Prevent ID from being 0

        while (!idFound && id < Integer.MAX_VALUE)  // Continue searching for an ID until the size becomes too large
        {
            if (AppointmentQuery.checkAppointmentIdExists(id)) // Check if generated ID is available, query returns true if id already exists
            {
                id = id + 1;
            }
            else
            {
                idFound = true;
            }
        }

        if(idFound) {
            return id;
        }
        else {
            Tools.consoleMessage(Tools.MsgType.ERROR, "Id was never generated", "Appointments.setAppointmentId");
            return -1; // Return a value to show error
        }
    }


    /**
     * @return Appointment ID.
     */
    public int getAppointmentId() {
        return appointmentId;
    }


    /**
     * @return Title
     */
    public String getTitle() {
        return title;
    }


    /**
     * @return Description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return Location.
     */
    public String getLocation() {
        return location;
    }


    /**
     * @return Contact name.
     */
    public String getContactName() {
        return contactName;
    }

    /**
     * @return Contact ID.
     */
    public int getContactId() {return contactId;}


    /**
     * @return Type.
     */
    public String getType() {
        return type;
    }


    /**
     *  Sets creation date.
     */
    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * @return Creation date.
     */
    public LocalDateTime getCreationDate() {
        return creationDate;
    }



    /**
     *  Sets list containing all appointments within the next week.
     *  This list is used in the GUI to display data to user.
     */
    public static void setAppointmentsByWeek() {

        // Start with clean list
        if(!(appointmentsByWeek.isEmpty())) {
            appointmentsByWeek.clear();
        }

        for(int i = 0; i < allAppointments.size(); i++) {

            // If appointment takes place within next 7 days, add it to the list
            if (allAppointments.get(i).getStartDate().isBefore(TimeAndDate.nextWeek()) && allAppointments.get(i).getStartDate().isAfter(LocalDateTime.now())) {
                appointmentsByWeek.add(allAppointments.get(i));
            }

        }

    }



    /**
     * @return List containing all appointments for next week.
     */
    public static ObservableList getAppointmentsByWeek() {
        if (appointmentsByWeek.isEmpty()) {setAppointmentsByWeek();}  // Make sure weekly appointments are calculated
        return appointmentsByWeek;
    }


    /**
     *  Sets list containing all appointments within the next day.
     *  This list is used in the GUI to display data to user.
     */
    public static  void setAppointmentsByDay() {

        // Start with clean list
        if(!(appointmentsByDay.isEmpty())) {
            appointmentsByDay.clear();
        }

        for(int i = 0; i < allAppointments.size(); i++) {

            // If appointment takes place within next 1 day, add it to the list
            if (allAppointments.get(i).getStartDate().isBefore(TimeAndDate.nextDay()) && allAppointments.get(i).getStartDate().isAfter(LocalDateTime.now())) {
                appointmentsByDay.add(allAppointments.get(i));
            }

        }

    }

    /**
     *  Sets list containing all appointments for the next month.
     *  This list is used in the GUI to display data to user.
     */
    public static  void setAppointmentsByMonth() {

        // Start with clean list
        if(!(appointmentsByMonth.isEmpty())) {
            appointmentsByMonth.clear();
        }

        for(int i = 0; i < allAppointments.size(); i++) {

            // If appointment takes place within next 30 days, add it to the list
            if (allAppointments.get(i).getStartDate().isBefore(TimeAndDate.nextMonth()) && allAppointments.get(i).getStartDate().isAfter(LocalDateTime.now())) {
                appointmentsByMonth.add(allAppointments.get(i));
            }

        }

    }


    /**
     * @return List containing all appointments for next day.
     */
    public static ObservableList getAppointmentsByDay() {
        return appointmentsByDay;
    }


    /**
     * @return List containing all appointments for next month.
     */
    public static ObservableList getAppointmentsByMonth() {
        return appointmentsByMonth;
    }


    /**
     *  Finds index of appointment in local list, based on ID.
     * @param appointmentId Appointment ID to used find local list location.
     */
    public static int findLocalAppointmentIndex(int appointmentId) {

        for (int i = 0; i < allAppointments.size(); i++) {
            if (allAppointments.get(i).getAppointmentId() == appointmentId) {
                return i;
            }
        }

        Tools.consoleMessage(Tools.MsgType.ERROR, "Failed to find appointment", "Appointment.findLocalAppointmentIndex");
        return -1;  // Error case
    }



    /**
     *  Searches through local list of appointments, creates new list based on given contact ID.
     * @param contactId Contact to find appointments of.
     */
    public static ObservableList<Appointment> getAppointmentsByContact(int contactId) {

        ObservableList<Appointment> appointmentsByContact = FXCollections.observableArrayList();

        for (Appointment appointment : allAppointments) {
            if (appointment.getContactId() == contactId) {
                appointmentsByContact.add(appointment);
            }
        }

        return appointmentsByContact;
    }


    /**
     *  Deletes appointment from local memory.
     * @param appointmentId ID of appointment to be deleted.
     */
    public static void deleteAppointment(int appointmentId) {
        for (int i = 0; i < allAppointments.size(); i++) {
            if (allAppointments.get(i).getAppointmentId() == appointmentId) {
                allAppointments.remove(i);
                break;
            }
        }
    }

}

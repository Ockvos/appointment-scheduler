package controller;

import DAO.*;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.ArrayList;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

import model.*;

/**
 *  This class allows the creation of new appointment objects to be added to the database.
 */
public class AddAppointmentsScreen implements Initializable {

    public ComboBox hourComboBox;
    public ComboBox minuteComboBox;
    public ComboBox contactNameBox;
    public ComboBox customerNameBox;
    public ComboBox typeBox;
    public ComboBox userIdBox;

    public Button onExitButton;
    public Button onBackButton;
    public Button exitButton;
    public Button backButton;
    public Button createAppointmentButton;

    public TableColumn startDateCol;
    public TableColumn endDateCol;
    public TableColumn customerIdCol;
    public TableColumn appointmentIdCol;
    public TableColumn titleCol;
    public TableColumn descriptionCol;
    public TableColumn typeCol;

    public TextField appointmentTitle;
    public TextArea appointmentDescription;
    public TextField appointmentDuration;

    public Label minutesRemainingLabel;
    public Label characterCountLabel;

    public DatePicker datePicker;

    public TextField appointmentLocation;

    List<String> defaultMinuteOptionsString = new ArrayList<>();
    List<String> startMinuteOptionsString = new ArrayList<>();
    List<String> endMinuteOptionsString = new ArrayList<>();

    List<Integer> allCustomerId;
    List<Integer> allContactId;
    List<Integer> allUserId;

    Double timeRemaining = -0.0;  // Tracks how many minutes the company is open, after meeting start time



    /**
     *  Sets up all interactive options for user, such as combo boxes and date picker.
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Setup customer names combo box
        try {

            customerNameBox.getItems().addAll(CustomerQuery.fetchAllNameIdPairs());  // Name for display
            allCustomerId = CustomerQuery.fetchAllId();  // List of customer IDs

            contactNameBox.getItems().addAll(ContactsQuery.fetchAllNameIdPairs());  // Name for display
            allContactId = ContactsQuery.fetchAllId();  // List of contact IDs

            userIdBox.getItems().addAll(UserQuery.fetchAllNameIdPairs());
            allUserId = UserQuery.fetchAllUserId();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Make sure date picker can only select present and future dates
        setupDatePicker();

        // Setup appointment types combo box
        typeBox.getItems().addAll(Appointment.appointmentTypes);

        // Prevent appointmentDescription from being more than 80 chars, which is roughly 2-3 sentences
        appointmentDescription.setTextFormatter(new TextFormatter<String>(change -> change.getControlNewText().length() <= 80 ? change : null));

        // Populate time options (Hours and minutes dropdown)
        setupTimeBoxes();

    }


    /**
     *  Returns user to previous screen, without saving.
     */
    public void onCancelButton(ActionEvent actionEvent) throws IOException {

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/viewAppointmentsScreen.fxml")));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, Globals.APPOINTMENTS_WIDTH, Globals.APPOINTMENTS_HEIGHT); // Length, height
        stage.setTitle("Appointment Scheduler");
        stage.setScene(scene);
        stage.show();

    }


    /**
     *  When activated, checks all input fields to verify they are not blank
     */
    public void onContactNameBox() {
        if(verifyNotNull()) {createAppointmentButton.setDisable(false);}
    }


    /**
     *  When activated, checks all input fields to verify they are not blank
     */
    public void onCustomerNameBox(ActionEvent actionEvent) {
        if(verifyNotNull()) {createAppointmentButton.setDisable(false);}
    }


    /**
     *  When activated, checks all input fields to verify they are not blank
     */
    public void onTypeBox() {
        if(verifyNotNull()) {createAppointmentButton.setDisable(false);}
    }

    /**
     *  When activated, checks all input fields to verify they are not blank
     */
    public void onDatePicker() {
        if(verifyNotNull()) {createAppointmentButton.setDisable(false);}
    }


    /**
     *  Activates when key is typed, makes sure user does not exceed description character limit
     */
    public void onAppointmentDescription() {

        int charsRemaining = 80;  // Character limit
        int charsTyped =  appointmentDescription.getText().length();

        if ((charsRemaining - charsTyped) <= 0) {  // No characters remaining
            characterCountLabel.setText("0 Character(s) Remaining\n       Press Backspace");
            characterCountLabel.setTextFill(Paint.valueOf("#ff6600"));  // orange text
        }
        else {  // Characters remaining
            characterCountLabel.setText(charsRemaining - charsTyped + " Character(s) Remaining");
            characterCountLabel.setTextFill(Paint.valueOf("#000000"));  // black text
        }

        if(verifyNotNull()) {createAppointmentButton.setDisable(false);}
    }


    /**
     *  Sets up combo boxes for minutes and hours.
     */
    public void setupTimeBoxes() {


        // Certain local timezones cause company hours to fall between two dates for example EST(8AM-10PM) UTC(12PM-2AM)
        // this makes automatically scaled hour boxes confusing to the end user, therefore if a date change is detected,
        // no automatic scaling is applied and user is simply given a list of hours from 0-23 to chose from.
        if (TimeAndDate.localHoursList.contains(0))  {
            hourComboBox.getItems().addAll(TimeAndDate.defaultHourRange);

            // Create default list for user to select minutes from (00-59)
            for (int i = 0; i < 60; i++) {

                if (i < 10) {
                    String withLeadingZero = String.format("%02d", i);
                    defaultMinuteOptionsString.add(withLeadingZero);
                }
                else {
                    defaultMinuteOptionsString.add(Integer.toString(i));
                }
            }

            minuteComboBox.getItems().addAll(defaultMinuteOptionsString);

        }
        else {
            // No date change detected, make combo boxes automatically scaled
            advancedTimeBoxesSetup();
        }


    }


    /**
     *  Time boxes (hour box and minute box) automatically scale based on local timezone.
     *  For example, if the company is open from 8:00-22:00EST and local time is MST,
     *  hour combo box will only show 6:00-19:59 as selectable times (MST is 2hrs behind EST).
     *  Minutes are also scaled, for example if company opens at 6:30, the minute combo box will
     *  scale to only show 30-59 when opening hour is selected. Advanced time boxes currently only
     *  apply to timezones that are no more than 2hrs behind EST and no more than 8hrs ahead of EST
     */
    public void advancedTimeBoxesSetup() {

        int displayMinCounter = TimeAndDate.getLocalEndMinute();

        // If company closes at 22:00, make last selectable start time 21:59
        if (TimeAndDate.getLocalEndMinute() == 0) {

            // Shorten hour range by cutting off the end hour
            for (int i = 0; i < TimeAndDate.localHoursList.size()-1; i++) {
                hourComboBox.getItems().add(TimeAndDate.localHoursList.get(i));
            }

            displayMinCounter = 60;
        }
        else {  // If company closes at 22:30 for example, no need to shorten hour range
            hourComboBox.getItems().addAll(TimeAndDate.localHoursList);
        }

        // Create DEFAULT list for user to select minutes from (00-59)
        for (int i = 0; i < 60; i++) {

            if (i < 10) {
                String withLeadingZero = String.format("%02d", i);
                defaultMinuteOptionsString.add(withLeadingZero);
            }
            else {
                defaultMinuteOptionsString.add(Integer.toString(i));
            }
        }

        // Create special list for OPENING hour, for example if company opens at 8:30, list is 30-59
        for (int i = TimeAndDate.getLocalStartMinute(); i < 60; i++) {

            if (i < 10) {

                String withLeadingZero = String.format("%02d", i);
                startMinuteOptionsString.add(withLeadingZero);
            }
            else {
                startMinuteOptionsString.add(Integer.toString(i));
            }
        }

        // Create special list for CLOSING hour, for example if company closes at 10:30, list is 00-29
        for (int i = 0; i < displayMinCounter; i++) {

            if (i < 10) {
                String withLeadingZero = String.format("%02d", i);
                endMinuteOptionsString.add(withLeadingZero);
            }
            else {
                if (i != 60) {
                    endMinuteOptionsString.add(Integer.toString(i));
                }
            }
        }

        // Notice how the minute box is shown to user in string form. This is due to needing leading zeros
        minuteComboBox.getItems().addAll(defaultMinuteOptionsString);
    }



    /**
     *  Verifies all required fields have been filled in, enables "Create Appointment" button.
     *  This method is called each time user interacts with fields, checking if they are blank.
     */
    private boolean verifyNotNull() {

        // A value of false means the field is NOT empty
        boolean null1 = appointmentTitle.getText().isEmpty();

        boolean null2 = appointmentDescription.getText().isEmpty();

        boolean null3 = datePicker.getEditor().getText().isEmpty();

        boolean null4 = typeBox.getSelectionModel().getSelectedIndex() == -1; // -1 is default index if nothing has been selected

        boolean null5 = customerNameBox.getSelectionModel().getSelectedIndex() == -1;

        boolean null6 = appointmentDuration.getText().isEmpty();

        boolean null7 = contactNameBox.getSelectionModel().getSelectedIndex() == -1;

        boolean null8 = appointmentLocation.getText().isEmpty();

        boolean null9 = hourComboBox.getSelectionModel().getSelectedIndex() == -1;

        boolean null10 = hourComboBox.getSelectionModel().getSelectedIndex() == -1;


        if (null1 || null2 || null3 || null4 || null5 || null6 || null7 || null8 || null9 || null10) {

            return false;  // All inputs are filled in

        } else {

            return true;  // One or more inputs is blank

        }
    }

    /**
     *  When activated, checks all input fields to verify they are not blank
     */
    public void onDurationKeyTyped() {

        if(verifyNotNull()) {createAppointmentButton.setDisable(false);}

    }



    /**
     *  Selected hour determines contents of minute combo box. Start and end hour can potentially have
     *  different ranges as opposed to the standard 0-59.
     */
    public void onHourComboBox() {

        /* When a different hour is selected, this will change value of minutesComboBox, clearing is current selection
         * (each hour has a minutes list associated with it) this action causes the action event for the minuteComboBox to trigger.
         * When the minuteComboBox is triggered, it attempts to grab the selected value which is null.
         * In order to avoid the resulting exception, when interacting with the hourComboBox,
         * the action event is set to null for the minuteComboBox, essentially disabling it.
         * The original event is saved as 'handler', at the end of the hourComboBox event, the minuteComboBox is given its
         * original action event back.
         * */

        EventHandler<ActionEvent> handler = minuteComboBox.getOnAction();
        minuteComboBox.setOnAction(null);

        // Disables auto-scaling on time boxes if timezone causes local company hours to fall between two dates, local timezone of UTC will trigger this
        if (TimeAndDate.localHoursList.contains(0)) {

            minuteComboBox.getItems().clear();
            minuteComboBox.getItems().addAll(defaultMinuteOptionsString);

        }
        // The company's opening and closing hours have special requirements since not all 60 minutes might be available
        else if ((int) hourComboBox.getValue() == TimeAndDate.getLocalStartHour()) {

            // Setup minute box containing correct range base on selected hour
            minuteComboBox.getItems().clear();
            minuteComboBox.getItems().addAll(startMinuteOptionsString);
            minuteComboBox.getSelectionModel().selectFirst();  // Select the starting value to avoid blank

        }
        else if ((int) hourComboBox.getValue() == TimeAndDate.getLocalEndHour()) {

            // Setup minute box containing correct range base on selected hour
            minuteComboBox.getItems().clear();
            minuteComboBox.getItems().addAll(endMinuteOptionsString);
            minuteComboBox.getSelectionModel().selectFirst();

        }
        else {  // Default where minute options are 0-59

            minuteComboBox.getItems().clear();
            minuteComboBox.getItems().addAll(defaultMinuteOptionsString);
            minuteComboBox.getSelectionModel().selectFirst();

        }

        minuteComboBox.setOnAction(handler);

        if(verifyNotNull()) {
            createAppointmentButton.setDisable(false);
        }

        timeRemaining();  // Set time remaining label
    }


    /**
     *  Launches timeRemaining() method to calculate how long before company close the appointment starts.
     *  This method also checks all input fields to verify they are not blank
     */
    public void onMinuteComboBox() {

        timeRemaining();

        if(verifyNotNull()) {
            createAppointmentButton.setDisable(false);
        }
        else {
            createAppointmentButton.setDisable(true);
        }
    }


    /**
     *  Creates new appointment and writes it to the database.
     *  If there are any overlaps, time/date issues, or other errors,
     *  this method will print a message to the user informing them of the issue
     *  and not save the appointment.
     */
    public void onCreateAppointment(ActionEvent actionEvent) throws SQLException, IOException {

        // Generate unique appointment ID
        int appointmentId = Appointment.setAppointmentId();

        // Get customer ID
        int customerId = allCustomerId.get(customerNameBox.getSelectionModel().getSelectedIndex());

        // Get contact ID
        int contactId = allContactId.get(contactNameBox.getSelectionModel().getSelectedIndex());

        // Get user ID
        int userId;
        if (userIdBox.getSelectionModel().getSelectedIndex() != -1) {  // -1 indicates no selection
            userId = allUserId.get(userIdBox.getSelectionModel().getSelectedIndex());
        } else {
            userId = User.getUserId();  // Default ID is user currently logged in
        }

        // Get location of appointment
        String location = appointmentLocation.getText();

        // Get appointment type from combo box, convert object to string
        String type = typeBox.getValue().toString();

        // Get appointment title
        String title = appointmentTitle.getText();

        // Get appointment description
        String description = appointmentDescription.getText();

        // Get start date, set the time
        LocalDateTime startDate = datePicker.getValue().atStartOfDay();  // This technique converts LocalDate to LocalDateTime
        int startHour = Integer.parseInt(hourComboBox.getValue().toString());
        int startMinute = TimeAndDate.parseTime((String) minuteComboBox.getValue()); // Get start minute (convert to int first)
        startDate = TimeAndDate.setHourMin(startDate, startHour, startMinute);  // Manually set the hour and minute

        if (startDate.isBefore(LocalDateTime.now())) {
            Tools.errorMessage("Incorrect Start Time", "Meeting start time has already passed");
            return;
        }


        // Get end date, set the time
        String durationString = appointmentDuration.getText();  // Get meeting duration, which is used to calculate endHour;
        int durationInt;

        try {
            durationInt = Integer.parseInt(durationString);
        }
        catch(Exception e) {
            Tools.errorMessage("Incorrect Duration", "Meeting duration must be an integer");
            return;
        }

        LocalDateTime endDate = startDate.plusMinutes(durationInt);

        if (endDate.toLocalTime().isAfter(TimeAndDate.localEndTime.toLocalTime())) {
            Tools.infoMessage("Shorten Meeting Duration", "Meeting ends outside company hours");
            return;
        }
        // When updating an appointment, this data will be referenced
        LocalDateTime originalCreation = LocalDateTime.now();
        LocalDateTime updatedCreation = LocalDateTime.now();
        String originalCreator = UserQuery.fetchUsername(userId);  // Selected user ID is used to determine creator
        String lastUpdatedBy = UserQuery.fetchUsername(userId);

        // Create appointment object
        Appointment newAppointment = new Appointment(startDate, endDate, customerId, appointmentId, contactId, title, description, location, type, originalCreation, updatedCreation, originalCreator, lastUpdatedBy, userId);

        // Check if appointment overlaps existing appointment
        if(checkDateOverlap(newAppointment)) {
            return;  // Overlap was detected, return before saving to DB
        }

        Appointment.allAppointments.add(newAppointment);  // Add appointment to local list (used for visuals in GUI)

        AppointmentQuery.writeAppointment(newAppointment);  // Add appointment to DB

        showAppointmentMenu(actionEvent);
    }

    /**
     *  Exits the program without saving.
     */
    public void onExitButton(ActionEvent actionEvent) throws IOException {
        showAppointmentMenu(actionEvent);
    }


    /**
     *  Sets up a date picker that prevents user from selecting a date already past.
     */
    public void setupDatePicker() {

        datePicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();

                setDisable(empty || date.compareTo(today) < 0 );
            }
        });

        datePicker.getEditor().setDisable(true);  // Disable manual date entering
    }

    /**
     *  Returns user to previous menu where all appointments can be viewed.
     */
    public void showAppointmentMenu(ActionEvent actionEvent) throws IOException {

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/viewAppointmentsScreen.fxml")));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, Globals.APPOINTMENTS_WIDTH, Globals.APPOINTMENTS_HEIGHT); // Length, height
        stage.setTitle(Globals.WINDOW_NAME);
        stage.setScene(scene);
        stage.show();
    }

    /**
     *  Sets text of label informing user how many minutes after appointment starts, company closes.
     *  Label becomes orange if appointment start is near company close.
     *  This feature is disabled if local timezone is UTC.
     */
    public void timeRemaining() {

        if (TimeAndDate.localHoursList.contains(0)) {

            minutesRemainingLabel.setText("");
            return;

        }

        Integer hour = Integer.parseInt(hourComboBox.getValue().toString());
        String minute = minuteComboBox.getValue().toString();

        if (hour != null && minute != null) {
            timeRemaining = TimeAndDate.calculateMinutesTillClose(hour, TimeAndDate.parseTime(minute));

            if (timeRemaining > 30.0) {  // Black text if appointments starts over 30 minutes prior to company closing time

                minutesRemainingLabel.setTextFill(Paint.valueOf("#000000"));

            }
            else {  // Orange text if appointments starts 30 minutes or less prior to company closing time

                minutesRemainingLabel.setTextFill(Paint.valueOf("#ff6600"));

            }

            minutesRemainingLabel.setText(timeRemaining + " Minute(s) Until Company Close");

        }
    }


    /**
     *  Checks newly created appointment against all existing appointments for overlaps.
     *  This method can check for overlaps based on contact or customer, if an overlap
     *  is detected, a message will be printed to the user and the appointment will not
     *  be saved to the database.
     * @param appointment The newly created appointment to be checked.
     * @return True if an overlap exists, false if not.
     */
    private boolean checkDateOverlap(Appointment appointment) {

        boolean overlapFound = false;

        LocalDateTime newDateStart = appointment.getStartDate();
        LocalDateTime newDateEnd = appointment.getEndDate();

        // Cycle through all existing appointments
        for (int i = 0; i < Appointment.allAppointments.size(); i++) {

            Appointment existingAppointment = Appointment.allAppointments.get(i);
            LocalDateTime existingStartDate = existingAppointment.getStartDate();
            LocalDateTime existingEndDate = existingAppointment.getEndDate();

            if (existingAppointment.getCustomerId() == appointment.getCustomerId() && Globals.checkOverlapCustomers) {


                //  Date starts or ends at exact time of another appointment (D1 and D2 are the same)
                if (newDateStart.equals(existingStartDate) || newDateEnd.equals(existingEndDate) || newDateStart.equals(existingEndDate) || newDateEnd.equals(existingStartDate)) {
                    overlapFound = true;
                }
                // Date starts before, and ends after existing date (D2 takes place within D1)
                else if (newDateStart.isBefore(existingStartDate) && newDateEnd.isAfter(existingEndDate)) {
                    overlapFound =  true;
                }
                // Date starts before existing date, but ends after existing start date (D1 end time partially overlaps D2)
                else if (newDateStart.isBefore(existingStartDate) && newDateEnd.isAfter(existingStartDate)) {
                    overlapFound = true;
                }
                //  Date starts after existing date, and ends before existing date end (D1 takes place within D2)
                else if (newDateStart.isAfter(existingStartDate)  && newDateEnd.isBefore(existingEndDate)) {
                    overlapFound = true;
                }
                // Date starts after existing date, but ends after existing date end (D1 overlaps latter half of D2)
                else if (newDateStart.isAfter(existingStartDate) && newDateStart.isBefore(existingEndDate) && newDateEnd.isAfter(existingEndDate)) {
                    overlapFound = true;
                }

                if (overlapFound) {
                    Tools.infoMessage("Invalid Time", "Selected customer (ID: " + existingAppointment.getCustomerId() + ") has an overlapping appointment");
                    return true;
                }
            }

            if (existingAppointment.getContactId() == appointment.getContactId()  && Globals.checkOverlapContacts) {

                if (newDateStart.isEqual(existingStartDate) || newDateEnd.isEqual(existingEndDate)) {
                    overlapFound = true;
                }
                // Date starts before, and ends after existing date (D2 takes place within D1)
                else if (newDateStart.isBefore(existingStartDate) && newDateEnd.isAfter(existingEndDate)) {
                    overlapFound =  true;
                }
                // Date starts before existing date, but ends after existing start date (D1 end time partially overlaps D2)
                else if (newDateStart.isBefore(existingStartDate) && newDateEnd.isAfter(existingStartDate)) {
                    overlapFound = true;
                }
                //  Date starts after existing date, and ends before existing date end (D1 takes place within D2)
                else if (newDateStart.isAfter(existingStartDate)  && newDateEnd.isBefore(existingEndDate)) {
                    overlapFound = true;
                }
                // Date starts after existing date, but ends after existing date end (D1 overlaps latter half of D2)
                else if (newDateStart.isAfter(existingStartDate) && newDateStart.isBefore(existingEndDate) && newDateEnd.isAfter(existingEndDate)) {
                    overlapFound = true;
                }

                if (overlapFound) {
                    Tools.infoMessage("Invalid Time", "Selected contact (ID: " + existingAppointment.getContactId() + ") has an overlapping appointment");
                    return true;
                }
            }
        }

        Tools.consoleMessage(Tools.MsgType.INFO, "No overlaps found when creating appointment " + appointment.getAppointmentId(), "AddAppointmentsScreen.checkDateOverlap()");
        return false;

    }

    /**
     *  Prints message informing user about manually setting user ID.
     *  It is recommended to let program determine user ID automatically.
     *  For example, if user1 is logged in and creates the appointment,
     *  user2 could be selected as the user ID, making user2 the creator.
     *  This does not cause any issues, but does not make sense from a
     *  logical perspective.
     */
    public void onQuestionButton() {
        Tools.infoMessage("Default user ID is current logged in user", "ID chosen determines appointment creator. Setting the ID manually is not recommended.");
    }
}
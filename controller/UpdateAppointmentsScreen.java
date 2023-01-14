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
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.sql.SQLException;

import java.io.IOException;

import java.net.URL;

import model.*;


/**
 *  This class allows existing appointment objects to be modified and rewritten to the database.
 */
public class UpdateAppointmentsScreen implements Initializable {

    public ComboBox hourComboBox;
    public ComboBox contactNameBox;
    public ComboBox userIdBox;
    public ComboBox customerNameBox;
    public ComboBox typeBox;
    public ComboBox<String> minuteComboBox;

    public TextField appointmentLocation;
    public TextField appointmentDuration;
    public TextField appointmentTitle;
    public TextField AppointmentIdField;

    public Label minutesRemainingLabel;
    public Label characterCountLabel;

    public Button onExitButton;
    public Button onBackButton;
    public Button backButton;
    public Button createAppointmentButton;

    public DatePicker datePicker;

    public TextArea appointmentDescription;

    List<String> defaultMinuteOptionsString = new ArrayList<String>();
    List<String> startMinuteOptionsString = new ArrayList<>();
    List<String> endMinuteOptionsString = new ArrayList<>();

    List<Integer> allCustomerId;
    List<Integer> allContactId;
    List<Integer> allUserId;

    Double timeRemaining = -0.0;  // Tracks how many minutes the company is open, after meeting start time
    private int charsRemaining = 80;  // Character limit for description


    /**
     *  Sets up all interactive options for user, such as combo boxes and date picker.
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Date must be set manually during update otherwise they will be null
        datePicker.setValue(AppointmentsScreen.selectedAppointment.getStartDate().toLocalDate());
        datePicker.getEditor().setDisable(true);

        // Attempt to grab old appointment info and populate text fields
        try {
            populateFields();
        } catch (SQLException e) {
            e.printStackTrace();
        }

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

        // Populate time options
        setupTimeBoxes();
        timeRemaining();
    }


    /**
     *  Returns user to previous screen without saving.
     */
    public void onCancelButton(ActionEvent actionEvent) throws IOException {

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/viewAppointmentsScreen.fxml")));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, Globals.APPOINTMENTS_WIDTH, Globals.APPOINTMENTS_HEIGHT); // Length, height
        stage.setTitle(Globals.WINDOW_NAME);
        stage.setScene(scene);
        stage.show();

    }


    /**
     *  When activated, checks all input fields to verify they are not blank
     */
    public void onContactNameBox() {

        if(verifyNotNull()) {
            createAppointmentButton.setDisable(false);
        }
        else {
            createAppointmentButton.setDisable(true);
        }
    }

    /**
     *  When activated, checks all input fields to verify they are not blank
     */
    public void onCustomerNameBox() {
        if(verifyNotNull()) {
            createAppointmentButton.setDisable(false);
        }
        else {
            createAppointmentButton.setDisable(true);
        }
    }


    /**
     *  When activated, checks all input fields to verify they are not blank
     */
    public void onTypeBox() {

        if(verifyNotNull()) {
            createAppointmentButton.setDisable(false);
        }
        else {
            createAppointmentButton.setDisable(true);
        }

    }

    /**
     *  When activated, checks all input fields to verify they are not blank
     */
    public void onDatePicker() {

        if(verifyNotNull()) {
            createAppointmentButton.setDisable(false);
        }
        else {
            createAppointmentButton.setDisable(true);
        }

    }


    /**
     *  Activates when key is typed, makes sure user does not exceed description character limit
     */
    public void onAppointmentDescription() {

        int charsTyped =  appointmentDescription.getText().length();
        if ((charsRemaining - charsTyped) <= 0) {
            characterCountLabel.setText("0 Character(s) Remaining\n       Press Backspace");
            characterCountLabel.setTextFill(Paint.valueOf("#ff6600"));  // orange text
        }
        else {
            characterCountLabel.setText(charsRemaining - charsTyped + " Character(s) Remaining");
            characterCountLabel.setTextFill(Paint.valueOf("#000000"));  // black text
        }

        if(verifyNotNull()) {
            createAppointmentButton.setDisable(false);
        }
        else {
            createAppointmentButton.setDisable(true);
        }
    }

    /**
     *  Sets up combo boxes for minutes and hours.
     */
    private void setupTimeBoxes() {

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
    private void advancedTimeBoxesSetup() {

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

        // Create special list for opening hour, for example if company opens at 8:30, list is 30-59
        for (int i = TimeAndDate.getLocalStartMinute(); i < 60; i++) {

            if (i < 10) {

                String withLeadingZero = String.format("%02d", i);
                startMinuteOptionsString.add(withLeadingZero);
            }
            else {
                startMinuteOptionsString.add(Integer.toString(i));
            }
        }


        // Create special list for closing hour, for example if company closes at 10:30, list is 00-29
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
     *  Verifies all required fields have been filled in, enables "Update Appointment" button.
     *  This method is called each time user interacts with fields, checking if they are blank.
     */
    private boolean verifyNotNull() {

        // A value of false means the field is NOT empty
        boolean null1 = customerNameBox.getSelectionModel().isEmpty();

        boolean null2 = appointmentTitle.getText().isEmpty();

        boolean null3 = appointmentDescription.getText().isEmpty();

        boolean null4 = datePicker.getEditor().getText().isEmpty();

        boolean null5 = appointmentDuration.getText().isEmpty();

        boolean null6 = appointmentLocation.getText().isEmpty();


        if(null1 || null2 || null3 || null4 || null5 || null6) {

            return false;  // All inputs are filled in

        } else {

            return true;  // One or more inputs is blank
        }
    }

    /**
     *  When activated, checks all input fields to verify they are not blank
     */
    public void onDurationKeyTyped(KeyEvent keyEvent) {

        if(verifyNotNull()) {
            createAppointmentButton.setDisable(false);
        }
        else {
            createAppointmentButton.setDisable(true);
        }

    }


    /**
     *  Selected hour determines contents of minute combo box. Start and end hour can potentially have
     *  different ranges as opposed to the standard 0-59.
     */
    public void onHourComboBox(ActionEvent actionEvent) {

       /* When a different hour is selected, this will change value of minutesComboBox, clearing is current selection
       * (each hour has a minutes list associated with it) this action causes the action event for the minuteComboBox to trigger.
       * When the minuteComboBox is triggered, it attempts to grab the selected value which is null.
       * In order to avoid the resulting exception, when interacting with the hourComboBox,
       * the action event is set to null for the minuteComboBox, essentially disabling it.
       * the original event is saved as 'handler', at the end of the hourComboBox event, the minuteComboBox is given its
       * original action event back.
       * */

       EventHandler<ActionEvent> handler = minuteComboBox.getOnAction();
       minuteComboBox.setOnAction(null);

        // Disables auto-scaling on time boxes if timezone causes local company hours to fall between two dates
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
        else {
            createAppointmentButton.setDisable(true);
        }

        timeRemaining();  // Set time remaining label
    }


    /**
     *  Allows user to change minutes without needing to interact with the hour combo box.
     *  Normally creation of the minutes list is tied to first selecting a value from the hour combo box.
     *  This concept works when creating a new appointment, but during updating a user may desire to advance
     *  the time of the minutes box without interacting with the hour box first. This method retrieves the
     *  correct scaled minutes list for user to interact with.
     */
    public void onMinuteComboBoxClicked(MouseEvent mouseEvent) {

        if ((hourComboBox.getValue().equals(Integer.toString(TimeAndDate.getLocalStartHour())))) {

            // Setup minute box containing correct range base on selected hour
            minuteComboBox.getItems().clear();
            minuteComboBox.getItems().addAll(startMinuteOptionsString);
            minuteComboBox.getSelectionModel().selectFirst();  // Select the starting value to avoid blank

        }
        else if ((hourComboBox.getValue().equals(Integer.toString(TimeAndDate.getLocalEndHour())))) {

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
     *  Creates a new appointment by grabbing all data in the input fields.
     *  This new appointment is then used to overwrite the existing appointment.
     *  If there are any overlaps, time/date issues, or other errors,
     *  this method will print a message to the user informing them of the issue
     *  and not save the appointment.
     */
    public void onCreateAppointment(ActionEvent actionEvent) throws SQLException, IOException {

        // Grab already existing ID
        int appointmentId = AppointmentsScreen.selectedAppointment.getAppointmentId();

        // Get customer ID
        int customerId = allCustomerId.get(customerNameBox.getSelectionModel().getSelectedIndex());

        // Get contact ID
        int contactId = allContactId.get(contactNameBox.getSelectionModel().getSelectedIndex());

        // Get user ID
        int userId;
        if (userIdBox.getSelectionModel().getSelectedIndex() != -1) {
            userId = allUserId.get(userIdBox.getSelectionModel().getSelectedIndex());
        } else {
            userId = User.getUserId();  // Default is current user
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
        LocalDateTime startDate = datePicker.getValue().atStartOfDay();

        int startHour = Integer.parseInt(hourComboBox.getValue().toString()); // Get start time hour
        int startMinute = TimeAndDate.parseTime(minuteComboBox.getValue()); // Get start minute (convert to int first)
        startDate = TimeAndDate.setHourMin(startDate, startHour, startMinute);  // Manually set the hour and minute

        if (startDate.isBefore(LocalDateTime.now())) {
            Tools.errorMessage("Incorrect Start Time", "Meeting start time has already passed");
            return;
        }

        // Get end date, set the time
        String durationString = appointmentDuration.getText();  // Get meeting duration, used to calculate endHour
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

        //  Find local index of appointment being updated, this will be needed for overwriting
        int index = Appointment.findLocalAppointmentIndex(appointmentId);

        LocalDateTime originalCreation = AppointmentsScreen.selectedAppointment.getCreationDate();
        LocalDateTime updatedCreation = LocalDateTime.now();  // Set last update time to current
        String originalCreator = AppointmentsScreen.selectedAppointment.getCreatedBy();
        String lastUpdatedBy = UserQuery.fetchUsername(userId);


        Appointment newAppointment = new Appointment(startDate, endDate, customerId, appointmentId, contactId, title, description, location, type, originalCreation, updatedCreation, originalCreator, lastUpdatedBy, userId);


        // Check if appointment overlaps existing appointment
        if(checkDateOverlap(newAppointment)) {
            return;  // Overlap was detected, return before saving
        }

        Appointment.allAppointments.set(index, newAppointment);  // Overwrite old appointment with update locally

        AppointmentQuery.updateAppointment(newAppointment, appointmentId);  // Update appointment in database

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

        datePicker.getEditor().setDisable(true);
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
     *  Data from the appointment being modified is fetched and populated into the correct fields.
     */
    public void populateFields() throws SQLException {

        // Setup appointment Id
        AppointmentIdField.setText((Integer.toString(AppointmentsScreen.selectedAppointment.getAppointmentId())));

        // Setup contact name combo box
        contactNameBox.getSelectionModel().select(ContactsQuery.fetchAllNameIdPair(AppointmentsScreen.selectedAppointment.getContactId()));

        // Setup customer name combo box
        customerNameBox.getSelectionModel().select(CustomerQuery.fetchAllNameIdPair(AppointmentsScreen.selectedAppointment.getCustomerId()));

        // Setup type box
        typeBox.getSelectionModel().select(AppointmentsScreen.selectedAppointment.getType());

        // Setup location
        appointmentLocation.setText(AppointmentsScreen.selectedAppointment.getLocation());

        // Setup title
        appointmentTitle.setText(AppointmentsScreen.selectedAppointment.getTitle());

        // Setup start date
        String startDate = TimeAndDate.customTimeFormattedString(AppointmentsScreen.selectedAppointment.getStartDate(),  "M/dd/yyyy");
        datePicker.getEditor().setText(startDate);

        // Setup start time hr
        String hour = Integer.toString(AppointmentsScreen.selectedAppointment.getStartDate().getHour());
        hourComboBox.getSelectionModel().select(hour);

        // Setup start time min
        String minute = Integer.toString(AppointmentsScreen.selectedAppointment.getStartDate().getMinute());
        if (Integer.parseInt(minute) < 10) {  // Visual formatting
            minute = "0" + minute;
        }
        minuteComboBox.getSelectionModel().select(minute);

        // Setup duration
        LocalDateTime startTime = AppointmentsScreen.selectedAppointment.getStartDate();
        LocalDateTime endTime = AppointmentsScreen.selectedAppointment.getEndDate();
        appointmentDuration.setText(Integer.toString(TimeAndDate.determineDifferenceMinutes(startTime, endTime)));

        // Setup description
        appointmentDescription.setText(AppointmentsScreen.selectedAppointment.getDescription());

        int charsTyped =  appointmentDescription.getText().length();
        if ((charsRemaining - charsTyped) <= 0) {
            characterCountLabel.setText("0 Character(s) Remaining\n       Press Backspace");
            characterCountLabel.setTextFill(Paint.valueOf("#ff6600"));  // orange text
        }
        else {
            characterCountLabel.setText(charsRemaining - charsTyped + " Character(s) Remaining");
            characterCountLabel.setTextFill(Paint.valueOf("#000000"));  // black text
        }
    }


    /**
     *  When activated, checks all input fields to verify they are not blank
     */
    public void onLocation() {
        if(verifyNotNull()) {
            createAppointmentButton.setDisable(false);
        }
        else {
            createAppointmentButton.setDisable(true);
        }
    }


    /**
     *  When activated, checks all input fields to verify they are not blank
     */
    public void onTitle() {

        if(verifyNotNull()) {
            createAppointmentButton.setDisable(false);
        }
        else {
            createAppointmentButton.setDisable(true);
        }
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
        String minute = minuteComboBox.getValue();

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

        for (int i = 0; i < Appointment.allAppointments.size(); i++) {

            Appointment existingAppointment = Appointment.allAppointments.get(i);
            LocalDateTime existingStartDate = existingAppointment.getStartDate();
            LocalDateTime existingEndDate = existingAppointment.getEndDate();

            // When updating, the appointment being modified is already in the DB, no point in checking an appointment overlap on itself
            if (existingAppointment.getAppointmentId() != appointment.getAppointmentId())  {

                if (existingAppointment.getCustomerId() == appointment.getCustomerId()  && Globals.checkOverlapCustomers) {


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

                if (existingAppointment.getContactId() == appointment.getContactId() && Globals.checkOverlapContacts) {

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
        }

        Tools.consoleMessage(Tools.MsgType.INFO, "No overlaps found when creating appointment " + appointment.getAppointmentId(), "AddAppointmentsScreen.checkDateOverlap()");
        return false;

    }

    /**
     *  One way to enable the "Update Appointment" button is by simply changing the user ID.
     */
    public void onUserIdBox() {
        createAppointmentButton.setDisable(false);  // User ID is optional, changing it is enough to allow appointment creation
    }
}

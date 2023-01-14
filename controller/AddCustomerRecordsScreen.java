package controller;

import DAO.CustomerQuery;
import DAO.FirstLevelDivisionQuery;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.CountriesAndDivisions;
import model.Customer;
import model.Globals;
import model.User;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 *  This class provides the needed GUI functionality to create customer objects.
 */
public class AddCustomerRecordsScreen implements Initializable {

    public Button exitButton;
    public Button backButton;
    public Button createRecordButton;
    public Label customerRecordsLabel;
    public TextField phoneNumberField;
    public ComboBox<CountriesAndDivisions> countryComboBox;
    public ComboBox divisionComboBox;
    public Label eitherOrBothLabel;
    public Label villageTownLabel;
    public Label postalCodeLabel;
    public TextField postalCodeField;
    public TextField villageTownField;
    public TextField cityField;
    public Label cityLabel;
    public Label asterisk1;
    public Label asterisk2;
    public TextField nameField;
    public TextField buildingNumberField;
    public TextField streetNameField;


    /**
     *  Sets up combo boxes for countries and first level divisions, also applies custom formatting to two fields.
     *  Building and phone number have special text filtering preventing user from typing letters.
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {

        countryComboBox.setItems(CountriesAndDivisions.countries);
        phoneNumberField.setTextFormatter(new TextFormatter<String>(change -> filterTyping(change.getText()) ? change : null));
        buildingNumberField.setTextFormatter(new TextFormatter<String>(change -> filterTypingInteger(change.getText()) ? change : null));

        setupComboBoxes();
    }


    /**
     *  Filters typing to allow user to only type integers and characters associated with phone numbers.
     * @param keyTyped The key user typed in text field.
     */
    public boolean filterTyping(String keyTyped) {

        // Allows use of backspace (represented by blank space) and single space
        if (keyTyped.isBlank()) {
            return true;
        }

        // Allow number to start with "+"
        if(phoneNumberField.getText().length() == 0 && keyTyped.equals("+")) {
            return true;
        }

        // Prevent number from starting with "-"
        if(phoneNumberField.getText().length() == 0 && keyTyped.equals("-")) {
            return false;
        }

        // This section allows user to type "-" but not "--" and not "+-"
        if (Objects.equals(keyTyped, "-")) {

            if (!phoneNumberField.getText().endsWith("-") && !phoneNumberField.getText().endsWith("+")) {  // Makes sure previous character typed was not "-"
                return true;
            }
            else {
                return false;
            }
        }

        // This section attempts to parse input to int, if it can be parsed, it's a number
        try {
            Integer.parseInt(keyTyped);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     *  Filters typing to allow user to only type integers.
     * @param keyTyped The key user typed in text field.
     */
    public boolean filterTypingInteger(String keyTyped) {

        // Allows use of backspace (represented by blank for some reason instead of \b) and SINGLE space
        if (keyTyped.isBlank()) {
            return true;
        }

        // This section attempts to parse input to int, if it can be parsed, it's a number
        try {
            Integer.parseInt(keyTyped);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }


    /**
     *  Verifies all required fields have been filled in, enables "Create Record" button.
     *  This method is called each time user interacts with fields, checking if they are blank.
     */
    private boolean verifyNotNull() {


        // A value of false means the field is NOT empty
        boolean null1 = nameField.getText().isEmpty();

        boolean null2 = phoneNumberField.getText().isEmpty();

        boolean null3 = countryComboBox.getSelectionModel().getSelectedIndex() == -1;

        boolean null4 = divisionComboBox.getSelectionModel().getSelectedIndex() == -1;

        boolean null5 = buildingNumberField.getText().isEmpty();

        boolean null6 = streetNameField.getText().isEmpty();

        boolean null7 = postalCodeField.getText().isEmpty();

        boolean null8 = cityField.getText().isEmpty();

        boolean null9 = villageTownField.getText().isEmpty();



        if(null1 || null2 || null3 || null4 || null5 || null6 || null7) {  // Check non-optional fields

            return false;  // Something is blank

        } else {

            if (null8 && null9) {  // Both Village and City are null
                return false;
            }
            else {  // City or Village or both is not null
                return true;
            }
        }

    }


    /**
     *  Returns user to previous screen, without saving.
     */
    public void onBackButton(ActionEvent actionEvent) throws IOException {

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/customerRecordsScreen.fxml")));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, Globals.CUST_RECORDS_WIDTH, Globals.CUST_RECORDS_HEIGHT); // Length, height
        stage.setTitle(Globals.WINDOW_NAME);
        stage.setScene(scene);
        stage.show();
    }


    /**
     *  Exits the program without saving.
     */
    public void onExitButton() {
        System.exit(0);
    }


    /**
     *  Fetches first level divisions associated with country.
     *  If the selected country is UK additional text fields are available.
     *  These text fields appear if country is UK and will disappear if country
     *  is not UK.
     */
    public void onCountryComboBox() {


        divisionComboBox.setItems(countryComboBox.getSelectionModel().getSelectedItem().getAllDivisionNames());
        divisionComboBox.setDisable(false);

        // UK Requires special formatting
        if (Objects.equals(countryComboBox.getSelectionModel().getSelectedItem().getCountryName(), "UK")) {

            // Make all U.K. specific fields visible
            eitherOrBothLabel.setVisible(true);
            asterisk1.setVisible(true);
            asterisk2.setVisible(true);
            villageTownLabel.setVisible(true);
            villageTownField.setVisible(true);

            // Move city field down
            cityLabel.setLayoutX(355.0);
            cityLabel.setLayoutY(228.0);
            cityField.setLayoutX(474.0);
            cityField.setLayoutY(225.0);

            // Move postal code field down
            postalCodeLabel.setLayoutX(355.0);
            postalCodeLabel.setLayoutY(263.0);
            postalCodeField.setLayoutX(474.0);
            postalCodeField.setLayoutY(260.0);


            // Move village/town field into position, make visible
            villageTownLabel.setLayoutX(355.0);
            villageTownLabel.setLayoutY(193.0);
            villageTownField.setLayoutX(474.0);
            villageTownField.setLayoutY(190.0);

        }
        else {  // Country is not U.K.

            // Make all U.K. specific fields invisible
            eitherOrBothLabel.setVisible(false);
            asterisk1.setVisible(false);
            asterisk2.setVisible(false);
            villageTownLabel.setVisible(false);
            villageTownField.setVisible(false);

            // Move village/town field away, make invisible
            villageTownLabel.setLayoutX(14.0);
            villageTownLabel.setLayoutY(360.0);
            villageTownField.setLayoutX(130.0);
            villageTownField.setLayoutY(360.0);

            // Move city field up
            cityLabel.setLayoutX(355.0);
            cityLabel.setLayoutY(193.0);
            cityField.setLayoutX(474.0);
            cityField.setLayoutY(190.0);


            // Move postal field up
            postalCodeLabel.setLayoutX(355.0);
            postalCodeLabel.setLayoutY(228.0);
            postalCodeField.setLayoutX(474.0);
            postalCodeField.setLayoutY(225.0);

            villageTownField.clear();  // Clear potential data stored in the hidden fields

        }

        if (verifyNotNull()) {
            createRecordButton.setDisable(false);
        }
        else {
            createRecordButton.setDisable(true);
        }
    }

    /**
     *  Allows combo boxes to be filled with country objects instead of strings.
     *  The country names are still displayed to the user in string form.
     */
    public void setupComboBoxes() {

        Callback<ListView<CountriesAndDivisions>, ListCell<CountriesAndDivisions>> factory = lv -> new ListCell<>() {


            @Override
            protected void updateItem(CountriesAndDivisions countryName, boolean empty) {
                super.updateItem(countryName, empty);
                setText(empty ? "" : countryName.getCountryName());
            }

        };

        countryComboBox.setCellFactory(factory);
        countryComboBox.setButtonCell(factory.call(null));

    }


    /**
     *  Creates a new customer object which is saved to the database.
     *  Special formatting is used when saving a UK address to the database.
     *  The given phone number is also checked to verify it has correct formatting
     */
    public void onCreateRecordButton(ActionEvent actionEvent) throws SQLException, IOException {

        String name = nameField.getText();
        String phoneNumber = phoneNumberField.getText();
        if (phoneNumber.endsWith("-")) {  // Do not allow phone number to end with "-"
            phoneNumber = phoneNumber.substring(0,phoneNumber.length() - 1);
        }
        String country = countryComboBox.getSelectionModel().getSelectedItem().getCountryName();
        String division = divisionComboBox.getSelectionModel().getSelectedItem().toString();
        String buildingNumber = buildingNumberField.getText();
        String streetName = streetNameField.getText();
        String city = cityField.getText();
        String postalCode = postalCodeField.getText();


        String fullStreetAddress = "-1";  // Will be overwritten
        String village;
        if (Objects.equals(countryComboBox.getSelectionModel().getSelectedItem().getCountryName(), "UK")) {
            if (divisionComboBox.getSelectionModel().getSelectedIndex() != -1) {
                village = villageTownField.getText();
                fullStreetAddress = Customer.createAddress(buildingNumber, streetName, village, city);
            }
        }
        else {
            fullStreetAddress = Customer.createAddress(buildingNumber, streetName, city);
        }

        int divisionId = FirstLevelDivisionQuery.fetchDivisionId(division);
        int customerId = Customer.generateCustomerId();
        String createdBy =  User.getUsername();
        Customer customer = new Customer(customerId, divisionId, postalCode, name, fullStreetAddress, phoneNumber, LocalDateTime.now(), LocalDateTime.now(), createdBy, createdBy);

        // This data is used to populate fields when updating customer, will not be written to DB
        customer.setCountry(country);
        customer.setDivisionName(division);

        // Add customer to local list
        Customer.allCustomers.add(customer);

        // Add customer to database
        CustomerQuery.writeCustomer(customer);

        showRecordsScreen(actionEvent);
    }



    /**
     *  When activated, checks all input fields to verify they are not blank
     */
    public void onDivisionComboBox() {
        if (verifyNotNull()) {
            createRecordButton.setDisable(false);
        }
        else {
            createRecordButton.setDisable(true);
        }
    }

    /**
     *  When activated, checks all input fields to verify they are not blank
     */
    public void onBuildingNumberField() {
        if (verifyNotNull()) {
            createRecordButton.setDisable(false);
        }
        else {
            createRecordButton.setDisable(true);
        }
    }

    /**
     *  When activated, checks all input fields to verify they are not blank
     */
    public void onStreetNameField() {
        if (verifyNotNull()) {
            createRecordButton.setDisable(false);
        }
        else {
            createRecordButton.setDisable(true);
        }
    }


    /**
     *  When activated, checks all input fields to verify they are not blank
     */
    public void onVillageTownField() {
        if (verifyNotNull()) {
            createRecordButton.setDisable(false);
        }
        else {
            createRecordButton.setDisable(true);
        }
    }


    /**
     *  When activated, checks all input fields to verify they are not blank
     */
    public void onCityField(KeyEvent keyEvent) {
        if (verifyNotNull()) {
            createRecordButton.setDisable(false);
        }
        else {
            createRecordButton.setDisable(true);
        }
    }


    /**
     *  When activated, checks all input fields to verify they are not blank
     */
    public void onPostalCodeField() {
        if (verifyNotNull()) {
            createRecordButton.setDisable(false);
        }
        else {
            createRecordButton.setDisable(true);
        }
    }


    /**
     *  When activated, checks all input fields to verify they are not blank
     */
    public void onPhoneNumberField() {
        if (verifyNotNull()) {
            createRecordButton.setDisable(false);
        }
        else {
            createRecordButton.setDisable(true);
        }
    }


    /**
     *  When activated, checks all input fields to verify they are not blank
     */
    public void onNameField() {
        if (verifyNotNull()) {
            createRecordButton.setDisable(false);
        }
        else {
            createRecordButton.setDisable(true);
        }
    }


    /**
     *  Returns user to previous menu where all customer records can be viewed.
     */
    public void showRecordsScreen(ActionEvent actionEvent) throws IOException {

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/customerRecordsScreen.fxml")));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, Globals.CUST_RECORDS_WIDTH, Globals.CUST_RECORDS_HEIGHT); // Length, height
        stage.setTitle(Globals.WINDOW_NAME);
        stage.setScene(scene);
        stage.show();
    }
}
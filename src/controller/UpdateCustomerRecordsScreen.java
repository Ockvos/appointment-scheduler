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
 *  This class provides the needed GUI functionality to update customer objects.
 */
public class UpdateCustomerRecordsScreen implements Initializable {
    public Button exitButton;
    public Button backButton;
    public Button createRecordButton;
    public Label customerRecordsLabel;
    public TextField phoneNumberField;
    public ComboBox<CountriesAndDivisions> countryComboBox;
    public ComboBox divisionComboBox;
    public Label postalCodeLabel;
    public TextField postalCodeField;
    public TextField nameField;
    public TextField IdField;
    public TextField addressField;
    public Label formatDescription;
    public Label formatLabel;
    Customer selectedCustomer = CustomerRecordsScreen.selectedCustomer;

    /**
     *  Populates all fields with data from the customer being modified
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {


        setupComboBoxes(selectedCustomer);

        if (countryComboBox.getSelectionModel().getSelectedItem().getCountryName().equals("UK")) {
            formatDescription.setText("Building Number, Street, Village (Optional), City");
        }
        else {
            formatDescription.setText("Building Number, Street, City");
        }

        IdField.setText(Integer.toString(CustomerRecordsScreen.selectedCustomer.getCustomerId()));
        nameField.setText(CustomerRecordsScreen.selectedCustomer.getCustomerName());
        phoneNumberField.setText(CustomerRecordsScreen.selectedCustomer.getPhoneNumber());
        phoneNumberField.setTextFormatter(new TextFormatter<String>(change -> filterTyping(change.getText()) ? change : null));
        addressField.setText(selectedCustomer.getCustomerAddress());
        postalCodeField.setText(CustomerRecordsScreen.selectedCustomer.getPostalCode());
    }


    /**
     *  Filters typing to allow user to only type integers and characters associated with phone numbers.
     * @param keyTyped The key user typed in text field.
     */
    public boolean filterTyping(String keyTyped) {

        // Allows use of backspace (represented by blank) and SINGLE space
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
     *  Verifies all required fields have been filled in, enables the "Update Record" button.
     *  This method is called each time user interacts with fields, checking if they are blank.
     */
    private boolean verifyNotNull() {

        // A value of false means the field is NOT empty
        boolean null1 = nameField.getText().isEmpty();

        boolean null2 = phoneNumberField.getText().isEmpty();

        boolean null3 = countryComboBox.getSelectionModel().getSelectedIndex() == -1;

        boolean null4 = divisionComboBox.getSelectionModel().getSelectedIndex() == -1;

        boolean null5 = addressField.getText().isEmpty();

        boolean null6 = postalCodeField.getText().isEmpty();



        if(null1 || null2 || null3 || null4 || null5 || null6) {  // Check non-optional fields

            return false;  // Something is blank

        } else {
            return true;
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
     *  Sets first level divisions associated with a country.
     *  Allows user to interact with the division box
     */
    public void onCountryComboBox() {

        divisionComboBox.setItems(countryComboBox.getSelectionModel().getSelectedItem().getAllDivisionNames());
        divisionComboBox.setDisable(false);

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
     *  This method also automatically selects user's country and division.
     */
    public void setupComboBoxes(Customer selectedCustomer) {

        countryComboBox.setItems(CountriesAndDivisions.countries);

        Callback<ListView<CountriesAndDivisions>, ListCell<CountriesAndDivisions>> factory = lv -> new ListCell<>() {


            @Override
            protected void updateItem(CountriesAndDivisions countryName, boolean empty) {
                super.updateItem(countryName, empty);
                setText(empty ? "" : countryName.getCountryName());
            }

        };

        countryComboBox.setCellFactory(factory);
        countryComboBox.setButtonCell(factory.call(null));

        // Select customer's country in combo box
        for (int i = 0; i < countryComboBox.getItems().size(); i++) {

            if (countryComboBox.getItems().get(i).getCountryName().equals(selectedCustomer.getCountry())) {
                countryComboBox.getSelectionModel().select(i);
                break;
            }
        }

        divisionComboBox.setItems(countryComboBox.getSelectionModel().getSelectedItem().getAllDivisionNames());

        divisionComboBox.getSelectionModel().select(selectedCustomer.getDivisionName());

    }


    /**
     *  Creates a new customer object which is used to overwrite existing customer in database.
     *  The given phone number is also checked to verify it has correct formatting
     */
    public void onCreateRecordButton(ActionEvent actionEvent) throws SQLException, IOException {

        String name = nameField.getText();
        String phoneNumber = phoneNumberField.getText();
        if (phoneNumber.endsWith("-")) {
            phoneNumber = phoneNumber.substring(0,phoneNumber.length() - 1);
        }
        String country = countryComboBox.getSelectionModel().getSelectedItem().getCountryName();
        String division = divisionComboBox.getSelectionModel().getSelectedItem().toString();

        String fullStreetAddress = addressField.getText();

        String postalCode = postalCodeField.getText();

        int divisionId = FirstLevelDivisionQuery.fetchDivisionId(division);
        int customerId = selectedCustomer.getCustomerId();  // The ID has already been generated

        String createdBy =  selectedCustomer.getCreatedBy();
        String lastUpdatedBy = User.getUsername();

        // Create new customer with updated parameters
        Customer customer = new Customer(customerId, divisionId, postalCode, name, fullStreetAddress, phoneNumber, selectedCustomer.getCreationDate(), LocalDateTime.now(), createdBy, lastUpdatedBy);


        // This data is used to populate fields when updating customer, will not be written to DB
        customer.setCountry(country);
        customer.setDivisionName(division);

        //  Update the customer locally
        int index = Customer.findLocalCustomerIndex(customerId);
        Customer.allCustomers.set(index, customer);  // Overwrite old appointment with update

        // Update customer in database
        CustomerQuery.updateCustomer(customer, customerId);

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
    public void onAddressField() {
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
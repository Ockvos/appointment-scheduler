package controller;

import DAO.AppointmentQuery;
import DAO.CustomerQuery;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.*;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;


/**
 *  This class allows user to visually view a list of all customers.
 *  From this screen user is able to select buttons to delete, create, and update customer records.
 */
public class CustomerRecordsScreen  implements Initializable {

    public TableView recordsTableView;
    public TableColumn idCol;
    public TableColumn postalCodeCol;
    public TableColumn phoneNumberCol;
    public TableColumn customerNameCol;
    public TableColumn customerAddressCol;
    public Button exitButton;
    public Button backButton;
    public Button addRecord;
    public Label customerRecordsLabel;
    public Button deleteButton;
    public Button updateRecordButton;

    public static Customer selectedCustomer;  // Used when updating an appointment
    public TableColumn customerDivisionCol;


    /**
     *  Adds all customer objects to a tableview.
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {


        recordsTableView.setItems(Customer.allCustomers);

        tableviewSetup();

    }


    /**
     *  Returns user to previous screen.
     */
    public void onBackButton(ActionEvent actionEvent) throws IOException {

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/mainMenuScreen.fxml")));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, Globals.MAIN_MENU_WIDTH, Globals.MAIN_MENU_HEIGHT); // Length, height
        stage.setTitle(Globals.WINDOW_NAME);
        stage.setScene(scene);
        stage.show();

    }

    /**
     *  Exits the program, closing all GUI windows.
     */
    public void onExitButton() {
        System.exit(0);
    }


    /**
     *  Opens new screen where user can add a new customer.
     */
    public void onAddRecordButton(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/addCustomerScreen.fxml")));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, Globals.CUST_RECORDS_WIDTH, Globals.CUST_RECORDS_HEIGHT); // Length, height
        stage.setTitle(Globals.WINDOW_NAME);
        stage.setScene(scene);
        stage.show();
    }


    /**
     *  Deletes customer and associated appointments from both the local memory and the database.
     *  The delete option only becomes available if a row is selected.
     *  A notification will be displayed if a customer is deleted.
     *  User will be prompted to confirm deletion if customer has appointments associated with them.
     */
    public void onDeleteRecord() throws SQLException {

        if (safeToDeleteCustomer(selectedCustomer.getCustomerId())) {
            CustomerQuery.deleteCustomer(selectedCustomer.getCustomerId());  // Delete customer from database
            Customer.deleteCustomer(selectedCustomer.getCustomerId());  // Delete customer from local memory
        }

        deleteButton.setDisable(true);
    }


    /**
     *  Checks if customer has appointments associated.
     *  If there are appointments associated, program will ask user to confirm deletion of both customer and appointment.
     *  @param customerId The ID of currently selected customer.
     *  @return True if no associated appointments found, or if user granted permission to delete associated appointments.
     */
    public  boolean safeToDeleteCustomer(int customerId) throws SQLException {

        // List containing all appointment IDs (if any)
        List<Integer> customerAppointments = AppointmentQuery.fetchAppointmentsByCustomer(customerId);

        if (customerAppointments.size() > 0) {  // Constraint exists

            if(Tools.confirmationMessage("Delete This Record?", "This will delete all appointments associated with customer. Press OK to Delete")) {
                for (int id : customerAppointments) {
                    AppointmentQuery.deleteAppointment(id);  // Delete associated appointments from DB
                    Appointment.deleteAppointment(id);  // Delete associated appointments locally
                }

                Tools.infoMessage("Customer with ID (" + customerId +") deleted", customerAppointments.size() + " appointment(s) where also deleted.");

                return true;  // Constraint existed, user confirmed it's ok to delete appointments
            }
            else {
                return false;  // Constraint existed, user chose to not delete appointments
            }


        }
        else {  // No constraints
            return true;
        }
    }


    /**
     *  Creates a table view using customers fetched from the database.
     */
    public void tableviewSetup() {

        idCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        postalCodeCol.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        phoneNumberCol.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerAddressCol.setCellValueFactory(new PropertyValueFactory<>("customerAddress"));
        customerDivisionCol.setCellValueFactory(new PropertyValueFactory<>("divisionName"));

    }


    /**
     *  Activates if user selects a row from the tableview.
     *  If a row is selected, user has the option to update or delete selected row.
     */
    public void onRowSelected() {

        if(recordsTableView.getSelectionModel().getSelectedItem() != null) {  // Only triggers when one row is selected

            selectedCustomer = (Customer) recordsTableView.getSelectionModel().getSelectedItem();

            deleteButton.setDisable(false);
            updateRecordButton.setDisable(false);
        }
    }


    /**
     *  Opens a new screen where user can update an existing customer.
     */
    public void onUpdateRecordButton(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/updateCustomerRecordsScreen.fxml")));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, Globals.CUST_RECORDS_WIDTH, Globals.CUST_RECORDS_HEIGHT); // Length, height
        stage.setTitle(Globals.WINDOW_NAME);
        stage.setScene(scene);
        stage.show();
    }
}

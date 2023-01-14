package controller;

import DAO.ContactsQuery;

import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.Appointment;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;


/**
 *  This class allows user to visually view a list of all appointments associated with a contact.
 */
public class ContactSchedulesScreen implements Initializable {

    public ImageView profilePicture;
    public TableColumn appointmentIdCol;
    public TableColumn startDateCol;
    public TableColumn endDateCol;
    public TableColumn customerIdCol;
    public TableColumn titleCol;
    public TableColumn descriptionCol;
    public TableColumn typeCol;
    public TableView tableview;
    public ComboBox comboBox;
    public Label contactIdLabel;
    public Label contactNameLabel;
    public Button closeButton;


    /**
     *  Adds all customer objects to a tableview, also sets values of contact info labels.
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {

        tableview.getSelectionModel().clearSelection();  // Start working with a clean slate
        try {
            contactIdLabel.setText(Integer.toString(ReportsScreen.selectedContact));
            contactNameLabel.setText(ContactsQuery.fetchContactName(ReportsScreen.selectedContact));
            tableviewSetup(ReportsScreen.selectedContact);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     *  Creates a table view using appointments fetched from the database.
     *  @param contactId The ID of the contact to view schedule of.
     */
    public void tableviewSetup(int contactId) throws SQLException {

        tableview.setItems(Appointment.getAppointmentsByContact(contactId));
        startDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        appointmentIdCol.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
    }


    /**
     *  Closes current window, parent window remains open.
     */
    public void onCloseButton() throws IOException {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}
package controller;

import DAO.AppointmentQuery;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.ResourceBundle;

import model.*;

import java.io.IOException;

import java.net.URL;

import java.sql.SQLException;

/**
 *  This class allows user to visually view a list of all appointments.
 *  User is also given the option to sort appointments by month and day,
 *  and delete appointments.
 */
public class AppointmentsScreen implements Initializable {

    public TableView appointmentsTableView;
    public TableColumn startDateCol;
    public TableColumn endDateCol;
    public TableColumn userIdCol;
    public TableColumn customerIdCol;
    public TableColumn appointmentIdCol;
    public TableColumn titleCol;
    public TableColumn descriptionCol;
    public TableColumn locationCol;
    public TableColumn contactCol;
    public TableColumn typeCol;

    public Button onExitButton;
    public Button onBackButton;
    public Button exitButton;
    public Button backButton;
    public Button deleteButton;
    public Button updateAppointmentButton;

    public ComboBox optionsComboBox;

    public Label appointmentsLabel;
    public Label infoLabel;
    public Label appointmentRangeLabel;

    public static Appointment selectedAppointment;  // Used when updating appointment

    String allAppointments = "All Appointments";
    String byDay = "Appointments By Day";
    String byWeek = "Appointments By Week";
    String byMonth = "Appointments By Month";


    /**
     *  Adds all appointment objects to a tableview.
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {

        setupComboBox();

        appointmentsTableView.setItems(Appointment.allAppointments);

        tableviewSetup();

        tableCellHover();  // Appointment start time is show when user hovers over column
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
     *  Here user can select appointments by day or by week.
     *  When a selection is made, the tableview will update to reflect choice.
     */
    public void onOptionsComboBox() {

        if(optionsComboBox.getValue().equals(byDay)) {

            Appointment.setAppointmentsByDay();
            tableviewSetup();
            appointmentsTableView.setItems(Appointment.getAppointmentsByDay());
            appointmentRangeLabel.setText("Showing appointments starting within one day of " + TimeAndDate.customTimeFormattedString(LocalDateTime.now(), "yyyy-MM-dd, HH:mm:ss"));

        }
        else if (optionsComboBox.getValue().equals(byWeek)) {

            Appointment.setAppointmentsByWeek();
            tableviewSetup();
            appointmentsTableView.setItems(Appointment.getAppointmentsByWeek());
            appointmentRangeLabel.setText("Showing appointments starting within one week of " + TimeAndDate.customTimeFormattedString(LocalDateTime.now(), "yyyy-MM-dd, HH:mm:ss"));


        }
        else if (optionsComboBox.getValue().equals(byMonth)) {
            Appointment.setAppointmentsByMonth();
            tableviewSetup();
            appointmentsTableView.setItems(Appointment.getAppointmentsByMonth());
            appointmentRangeLabel.setText("Showing appointments starting within one month of " + TimeAndDate.customTimeFormattedString(LocalDateTime.now(), "yyyy-MM-dd, HH:mm:ss"));

        }
        else {

            tableviewSetup();
            appointmentsTableView.setItems(Appointment.allAppointments);
            appointmentRangeLabel.setText("Currently showing all appointments");
        }

    }


    /**
     *  Sets up the combo box, allowing user to choice between filtering appointments by day, week and month.
     */
    public void setupComboBox() {

        optionsComboBox.getItems().addAll(allAppointments, byDay, byWeek, byMonth);
        optionsComboBox.getSelectionModel().selectFirst();  // By default, first option in box is selected
    }


    /**
     *  Creates a table view using appointments fetched from the database.
     */
    public void tableviewSetup() {
        startDateCol.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateCol.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        appointmentIdCol.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        contactCol.setCellValueFactory(new PropertyValueFactory<>("contactName"));
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

    }


    /**
     *  Opens new screen where user can add a new appointment.
     */
    public void onAddAppointmentButton(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/addAppointmentsScreen.fxml")));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, Globals.APPOINTMENTS_WIDTH, Globals.APPOINTMENTS_HEIGHT); // Length, height
        stage.setTitle(Globals.WINDOW_NAME);
        stage.setScene(scene);
        stage.show();
    }


    /**
     *  Deletes an appointment from both the local memory and the database.
     *  The delete option only becomes available if a row is selected.
     */
    public void onDeleteAppointment() throws SQLException {

        AppointmentQuery.deleteAppointment(selectedAppointment.getAppointmentId());  // Delete appointment from DB
        Appointment.deleteAppointment(selectedAppointment.getAppointmentId());  // Delete appointment from local memory

        Tools.infoMessage("Appointment Cancelled", "Appointment ID: " + selectedAppointment.getAppointmentId() + ", Type: " + selectedAppointment.getType());

        deleteButton.setDisable(true);
    }


    /**
     *  Activates if user selects a row from the tableview.
     *  If a row is selected, user has the option to update or delete selected row.
     */
    public void onRowSelected() {

        if(appointmentsTableView.getSelectionModel().getSelectedItem() != null) {  // Only triggers when one row is selected

            selectedAppointment = (Appointment) appointmentsTableView.getSelectionModel().getSelectedItem();

            deleteButton.setDisable(false);

            updateAppointmentButton.setDisable(false);

        }
    }


    /**
     *  Detects if the mouse is hovering over a row.
     *  If a hover is detected, that appointment's start time will be displayed to the user.
     */
    public void tableCellHover() {

        appointmentsTableView.setRowFactory(tableView -> {
            final TableRow<Appointment> row = new TableRow<>();

            row.hoverProperty().addListener((observable) -> {
                final Appointment person = row.getItem();

                if (row.isHover() && person != null) {
                    infoLabel.setText("Meeting Time: " + person.getStartDate());
                } else {
                    infoLabel.setText("Hover to view appointment start time");
                }
            });

            return row;

        });
    }


    /**
     *  Opens a new screen where user can update an existing appointment.
     */
    public void onUpdateAppointment(ActionEvent actionEvent) throws IOException {
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/UpdateAppointmentsScreen.fxml")));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, Globals.APPOINTMENTS_WIDTH, Globals.APPOINTMENTS_HEIGHT); // Length, height
        stage.setTitle(Globals.WINDOW_NAME);
        stage.setScene(scene);
        stage.show();
    }
}

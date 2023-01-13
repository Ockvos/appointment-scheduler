package controller;

import DAO.AppointmentQuery;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.util.Objects;
import java.util.ResourceBundle;

import java.net.URL;

import java.sql.SQLException;

import java.time.LocalDateTime;

import java.io.IOException;

import model.*;


/**
 *  This class allows the display of the main menu to user.
 */
public class MainMenuScreen implements Initializable {

    public Label currentUserLabel;
    public Label timeZoneLabel;
    public Label languageLabel;
    public Label appointmentAlertLabel;
    public Label lastUpdateLabel;
    public Label appointmentIdLabel;
    public Label appointmentDateLabel;
    public Label appointmentTimeLabel;

    public ImageView profilePicture;
    public ImageView alertSymbol;

    public Appointment upcomingAppointment;


    /**
     *  Sets up initial visuals for the user.
     *  These visuals include the user's information, and appointment alerts.
     *  Two lambda expressions are used in this method: AppointmentAlerts, and UserInfo.
     *  AppointmentAlerts handles upcoming appointment alerts.
     *  UserInfo displays information such as username and profile picture.
     *  Lambdas where chosen to handle visuals mentioned above, due to these items are only
     *  displayed once, therefore a function is not needed.
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {


        // Appointments are queried each time menu is shown, to keep appointment alert accurate
        try {
            upcomingAppointment =  AppointmentQuery.fetchAppointmentsBetweenDates(Globals.alertTimeframe);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        // One of the required lambda expressions
        UserInfo displayUserInfo = ()  -> {

            // Display profile picture
            profilePicture.setImage(new Image(getClass().getResourceAsStream(User.getProfilePicture())));

            // Display current user
            currentUserLabel.setText("Current User: " + User.getUsername());

            // Display timezone
            timeZoneLabel.setText("Timezone: " + User.getTimeZone());
            timeZoneLabel.setStyle("-fx-font-weight: bold");

            // Display language
            languageLabel.setText("Language: " + Languages.getLanguage());
            languageLabel.setStyle("-fx-font-weight: bold");

        };

        displayUserInfo.userInfo();


        // Another of the required lambda expressions
        AppointmentAlerts displayUpcomingAppointment = (appointment) -> {

            // Shows last time program checked for upcoming appointments
            lastUpdateLabel.setText(TimeAndDate.customTimeFormattedString(LocalDateTime.now(), "HH:mm"));

            if(appointment  != null) {

                alertSymbol.setImage(new Image(getClass().getResourceAsStream(Globals.INFO_ALERT)));
                appointmentAlertLabel.setText("Appointment in 15 mins");
                appointmentIdLabel.setText(Integer.toString(appointment.getAppointmentId()));
                appointmentTimeLabel.setText(TimeAndDate.customTimeFormattedString(appointment.getStartDate(), "HH:mm"));
                appointmentDateLabel.setText(TimeAndDate.customTimeFormattedString(appointment.getStartDate(), "YYYY-MM-dd"));

            Tools.infoMessage("Upcoming Appointment", "There is an appointment within the next " + Globals.alertTimeframe + " minutes. See \"Upcoming Appointment\" pane for more details.");


            } else {
                appointmentAlertLabel.setText("No Appointments within next 15 mins");
                alertSymbol.setImage(new Image(getClass().getResourceAsStream(Globals.CHECK_MARK_ALERT)));
            }

        };

        displayUpcomingAppointment.appointmentAlerts(upcomingAppointment);

    }


    /**
     *  Opens the preferences screen when "Preferences" button is clicked.
     */
    public void onPreferencesButton(ActionEvent actionEvent) throws IOException {
        showPreferencesScreen(actionEvent);
    }


    /**
     *  Contains all data needed to launch the preferences screen.
     */
    public void showPreferencesScreen(ActionEvent actionEvent) throws IOException
    {

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/preferencesScreen.fxml")));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, Globals.PREFERENCES_WIDTH, Globals.PREFERENCES_HEIGHT); // Length, height
        stage.setTitle("Appointment Scheduler");
        stage.setScene(scene);
        stage.show();

    }

    /**
     *  Closes all application windows without saving
     */
    public void onExitButton() {

        System.exit(0);

    }


    /**
     *  Activates when the appointments button is clicked.
     *  Opens the main appointments screen.
     */
    public void onAppointmentsButton(ActionEvent actionEvent) throws IOException {


        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/viewAppointmentsScreen.fxml")));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, Globals.APPOINTMENTS_WIDTH, Globals.APPOINTMENTS_HEIGHT); // Length, height
        stage.setTitle(Globals.WINDOW_NAME);
        stage.setScene(scene);
        stage.show();

    }


    /**
     *  Activates when the records button is clicked.
     *  Opens the main records screen.
     */
    public void onRecordsButton(ActionEvent actionEvent) throws IOException {


        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/customerRecordsScreen.fxml")));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, Globals.CUST_RECORDS_WIDTH, Globals.CUST_RECORDS_HEIGHT); // Length, height
        stage.setTitle(Globals.WINDOW_NAME);
        stage.setScene(scene);
        stage.show();

    }

    /**
     *  Activates when the reports button is clicked.
     *  Opens the main reports screen.
     */
    public void onReportsScreen(ActionEvent actionEvent) throws IOException {

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/reportsScreen.fxml")));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setResizable(false);
        Scene scene = new Scene(root, Globals.MAIN_MENU_WIDTH, Globals.MAIN_MENU_HEIGHT); // Length, height
        stage.setTitle(Globals.WINDOW_NAME);
        stage.setScene(scene);
        stage.show();

    }
}

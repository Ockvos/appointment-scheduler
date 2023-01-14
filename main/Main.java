package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.sql.SQLException;

import model.*;

import DAO.*;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/view/loginScreen.fxml"));
        primaryStage.setTitle(Globals.WINDOW_NAME);
        primaryStage.setScene(new Scene(root, Globals.LOGIN_WIDTH, Globals.LOGIN_HEIGHT));
        primaryStage.getIcons().add(new Image(this.getClass().getResourceAsStream(Globals.APPLICATION_ICON)));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) throws SQLException {

        Languages.setLanguage();  // Determine system language

        Tools.consoleMessage(Tools.MsgType.BUILD_INFO, "Version: " + Globals.versionNumber, "Main.main()");
        Tools.consoleMessage(Tools.MsgType.BUILD_INFO, "Write To Database: " + Globals.writeToDatabase, "Main.main()");
        Tools.consoleMessage(Tools.MsgType.BUILD_INFO, "Generate Files: " + Globals.generateFiles, "Main.main()");

        // Open database
        JDBC.openConnection();


        // Query database and load important data into memory, static lists housing data are kept in the relevant classes
        try {

            CustomerQuery.fetchAllCustomers();  // Attempt to populate list with all customers
            CountriesQuery.createCountryDivisionPairs();  // Attempts to populate list of country objects
            AppointmentQuery.fetchAllAppointments();  // Attempt to populate list with all appointments
            Tools.consoleMessage(Tools.MsgType.INFO, "Found " + Customer.allCustomers.size() + " customers in DB", "Main.main()");
            Tools.consoleMessage(Tools.MsgType.INFO, "Found " + Appointment.allAppointments.size() + " appointments in DB", "Main.main()");

        } catch (SQLException e) {

            e.printStackTrace();

        }

        TimeAndDate.calculateCompanyHourRange();  // Determine what hours the company is open, will convert to local time

        launch(args);  // Launches GUI visuals

        JDBC.closeConnection();  // Close database
    }
}

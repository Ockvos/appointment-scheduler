package controller;

import DAO.ContactsQuery;
import DAO.CountriesQuery;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.Globals;
import model.Tools;
import model.User;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;


/**
 *  This class allows the display of the main reporting screen to the user.
 *  From the main reports screen, additional reports can be generated.
 */
public class ReportsScreen implements Initializable {


    public Label currentUserLabel;
    public Label timeZoneLabel;
    public ImageView profilePicture;
    public ComboBox contactComboBox;
    public Button contactSchedulesButton;
    public ComboBox countryComboBox;
    public Button customerLocationButton;

    List<Stage> reportStages = new ArrayList<>();

    public static int selectedContact = 1;  // Placeholder that can get overwritten
    public static String selectedCountry;


    /**
     *  Sets up initial visuals for the user.
     *  These visuals include the user's information, such as username and profile icon.
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Display profile picture
        profilePicture.setImage(new Image(getClass().getResourceAsStream(User.getProfilePicture())));

        // Display current user
        currentUserLabel.setText("Current User: " + User.getUsername());

        try {
            contactComboBox.setItems(ContactsQuery.fetchAllId());
            countryComboBox.setItems(CountriesQuery.fetchAllCountries());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    /**
     *  Closes all application windows without saving
     */
    public void onExitButton() {
        System.exit(0);
    }


    /**
     *  Returns user to previous screen, closing all child windows.
     */
    public void onBackButton(ActionEvent actionEvent) throws IOException {

        if (!reportStages.isEmpty()) {
            if (!Tools.confirmationMessage("Press OK to continue", "Going back will close all open reports")) {
                return;
            }
        }

        // Close child windows
        for (Stage stage : reportStages) {
            stage.close();
        }

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/mainMenuScreen.fxml")));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        Scene scene = new Scene(root, Globals.MAIN_MENU_WIDTH, Globals.MAIN_MENU_HEIGHT); // Length, height
        stage.setTitle(Globals.WINDOW_NAME);
        stage.setScene(scene);
        stage.show();
    }


    /**
     *  Opens window where appointments are grouped by: type, month and amount.
     */
    public void onTypeMonthReportButton() throws IOException {

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/typeReportScreen.fxml")));


        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle(Globals.WINDOW_NAME);
        stage.getIcons().add(new Image(this.getClass().getResourceAsStream(Globals.APPLICATION_ICON)));

        Scene scene = new Scene(root, Globals.TYPE_REPORTS_WIDTH, Globals.TYPE_REPORTS_HEIGHT);
        stage.setScene(scene);

        reportStages.add(stage);

        stage.show();

    }


    /**
     *  Opens screen where user can visually view a list of all appointments associated with a selected contact.
     */
    public void onContactSchedules() throws IOException {

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/contactSchedulesScreen.fxml")));

        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle(Globals.WINDOW_NAME);
        stage.getIcons().add(new Image(this.getClass().getResourceAsStream(Globals.APPLICATION_ICON)));

        Scene scene = new Scene(root, Globals.TYPE_REPORTS_WIDTH, Globals.TYPE_REPORTS_HEIGHT);
        stage.setScene(scene);

        reportStages.add(stage);

        stage.show();
    }


    /**
     *  Allows user to select a contact ID which is used for reporting.
     */
    public void onContactComboBox() {

        if (contactComboBox.getSelectionModel().getSelectedIndex() != -1) {  // Index of -1 indicates no selection was made
            contactSchedulesButton.setDisable(false);
        }
        else {
            contactSchedulesButton.setDisable(true);
        }
        selectedContact = Integer.parseInt(contactComboBox.getSelectionModel().getSelectedItem().toString());
    }


    /**
     *  Allows user to select a country which is used for reporting.
     */
    public void onCountryComboBox(ActionEvent actionEvent) {
        if (countryComboBox.getSelectionModel().getSelectedIndex() != -1) {   // Index of -1 indicates no selection was made
            customerLocationButton.setDisable(false);
        }
        else {
            contactSchedulesButton.setDisable(true);
        }
        selectedCountry = countryComboBox.getSelectionModel().getSelectedItem().toString();
    }


    /**
     *  Opens screen where user can visually view a list of all customers associated with a selected country.
     */
    public void onCustomerLocationButton() throws IOException {

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/countriesReportScreen.fxml")));

        Stage stage = new Stage();
        stage.setResizable(false);
        stage.setTitle(Globals.WINDOW_NAME);
        stage.getIcons().add(new Image(this.getClass().getResourceAsStream(Globals.APPLICATION_ICON)));

        Scene scene = new Scene(root, Globals.TYPE_REPORTS_WIDTH, Globals.TYPE_REPORTS_HEIGHT);
        stage.setScene(scene);

        reportStages.add(stage);

        stage.show();
    }
}
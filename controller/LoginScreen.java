package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

import model.*;

/** This class displays login screen to user */
public class LoginScreen implements Initializable {

    public Label timeZoneLabel;
    public Label userIdLabel;
    public Label passwordLabel;

    public TextField userIDField;

    public PasswordField userPasswordField;

    public Button submitButton;

    /**
     *  Automatically translates labels to user's local language.
     *  The supported languages are: English, French, Dutch
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {

        // Disable login fields is autoLogin is true
        if(Globals.autoLogin) {
            userIDField.setDisable(true);
            userPasswordField.setDisable(true);
        }

        Languages.setLanguage();

        timeZoneLabel.setText(Languages.translateWord("timezone", true) + ": " + User.getTimeZone());
        userIdLabel.setText(Languages.translateWord("user", true) + " ID:");
        passwordLabel.setText(Languages.translateWord("password", true) + ":");
        submitButton.setText(Languages.translateWord("submit", true));
    }

    /**
     *   Checks if user submitted correct login details.
     *   If faulty details are given, a descriptive error message will be displayed.
     *   Login attempts are saved in a text file.
     */
    public void onSubmit(ActionEvent actionEvent) throws IOException, SQLException {

        if (Globals.autoLogin) {
            User.createTestUser(Globals.defaultUsername, Globals.defaultUserId, Globals.defaultUserPassword);  // Assigns default user during auto login so appointments can still be made
            showMainMenu(actionEvent);
        }

        // Check both inputs are filled
        if (userIDField.getText().isBlank() || userPasswordField.getText().isBlank()) {

            if (!Globals.autoLogin) {  // Do not print popups if autoLogin is true
                Tools.errorMessage(Languages.translateSentence("empty field", true), Languages.translateSentence("fields cannot be blank", true));
            }

            return;
        }


        // Grab user ID and password from fields
        String userIDStr = userIDField.getText();
        String inputPassword = userPasswordField.getText();

        boolean correctValues = User.initializeUser(userIDStr);  // Will return false if given a faulty ID

    if (correctValues) {

        // Creates new file, returns true if new file is created, false if file exists
        if(Tools.createFile(Globals.LOGGING_FILE_PATH)) {
            Tools.writeToFile(Globals.LOGGING_FILE_PATH, "-LOGIN ATTEMPTS SINCE " + TimeAndDate.getCurrentDateTime().toString() + " " + TimeAndDate.getTimeZone(), true);
        }

            if (Objects.equals(inputPassword, User.getUserPassword())) {  // Passwords match

                if (!Globals.autoLogin) {

                    Tools.writeToFile(Globals.LOGGING_FILE_PATH, "-SUCCESSFUL LOGIN, ID[" + userIDStr + "]" + " | ", false);
                    Tools.writeToFile(Globals.LOGGING_FILE_PATH, TimeAndDate.getCurrentDateTime().toString() + " " + TimeAndDate.getTimeZone(), true);
                }

                showMainMenu(actionEvent);

            }
            else {  // Passwords do not match

                if (!Globals.autoLogin) {  // No need to print this, if autoLogin is being used

                    Tools.errorMessage(Languages.translateSentence("incorrect password", true), Languages.translateSentence("please try again", true));

                    Tools.writeToFile(Globals.LOGGING_FILE_PATH, "-FAILED LOGIN, ID[" + userIDStr + "]" + " Invalid password | ", false);
                    Tools.writeToFile(Globals.LOGGING_FILE_PATH, TimeAndDate.getCurrentDateTime().toString() + " " + TimeAndDate.getTimeZone(), true);
                }
            }

        }
        else {  // non-existing user id case

            if (!Globals.autoLogin) {
                Tools.writeToFile(Globals.LOGGING_FILE_PATH, "-FAILED LOGIN, ID[" + userIDStr + "]" + " Invalid ID | ", false);
                Tools.writeToFile(Globals.LOGGING_FILE_PATH, TimeAndDate.getCurrentDateTime().toString() + " " + TimeAndDate.getTimeZone(), true);
            }
        }
    }



    /**
     *  Launches and displays the main menu screen.
     */
    public void showMainMenu(ActionEvent actionEvent) throws IOException
    {

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/view/mainMenuScreen.fxml")));
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setResizable(false);
        Scene scene = new Scene(root, Globals.MAIN_MENU_WIDTH, Globals.MAIN_MENU_HEIGHT); // Length, height
        stage.setTitle(Globals.WINDOW_NAME);
        stage.setScene(scene);
        stage.show();

    }


    /**
     *  Translates login screen to French.
     *  This is disabled by default and is only used for testing.
     */
    public void onToFrench() {
        Languages.setLanguageManually("French");
        userIdLabel.setText(Languages.translateWord("user", true) + " ID:");
        passwordLabel.setText(Languages.translateWord("password", true) + ":");
        timeZoneLabel.setText(Languages.translateWord("timezone", true) + ": " + User.getTimeZone());
        submitButton.setText(Languages.translateWord("submit", true));
    }
}

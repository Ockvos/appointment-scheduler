package controller;

import DAO.AppointmentQuery;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;


/**
 *  This class creates the visuals needed to view reports grouped by: type, month, amount.
 */
public class TypeReportScreen implements Initializable {

    public TextArea textArea;

    public Button closeButton;


    /**
     *  Attempts to set up text area where report data will be printed.
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
            textAreaSetup();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Populates the text area with report data.
     */
    public void textAreaSetup() throws SQLException {
        String displayString = AppointmentQuery.fetchAppointmentsByTypeAndMonth().toString();
        displayString = displayString.substring(1, displayString.length()-1);
        textArea.setText(displayString);
    }

    /**
     *  Closes the report view.
     */
    public void onCloseButton() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }


}
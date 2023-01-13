package controller;

import DAO.CustomerQuery;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.Customer;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;


/**
 *  This class allows user to visually view a list of all customers associated with a country.
 *  This is the custom report created to meet requirement A3f of assessment.
 */
public class CountriesReportScreen implements Initializable {

    public TableView recordsTableView;
    public TableColumn idCol;
    public TableColumn customerNameCol;
    public TableColumn postalCodeCol;
    public TableColumn customerAddressCol;
    public Label countryNameLabel;
    public Label customerCountLabel;
    public ImageView profilePicture;
    public Button closeButton;

    ObservableList<Customer> customerList = FXCollections.observableArrayList();


    /**
     *  Adds all customer objects to a tableview, also sets values of country info labels.
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {

        try {
           customerList = CustomerQuery.fetchAllCustomersByCountry(ReportsScreen.selectedCountry);
           tableviewSetup();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        countryNameLabel.setText(ReportsScreen.selectedCountry);
        customerCountLabel.setText(Integer.toString(customerList.size()));

    }


    /**
     *  Creates a table view using customer and country info fetched from the database.
     */
    public void tableviewSetup() throws SQLException {

        recordsTableView.setItems(CustomerQuery.fetchAllCustomersByCountry(ReportsScreen.selectedCountry));
        idCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        customerNameCol.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerAddressCol.setCellValueFactory(new PropertyValueFactory<>("customerAddress"));
        postalCodeCol.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
    }


    /**
     *  Closes current window, parent window remains open.
     */
    public void onCloseButton(ActionEvent actionEvent) throws IOException {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}


package model;

import DAO.CustomerQuery;
import DAO.FirstLevelDivisionQuery;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;


/**
 * A class used to create customer objects.
 */
public class Customer {

    int customerId;
    int divisionId;
    int divisionIndex = 0;
    int countryIndex = 0;

    String postalCode;
    String customerName;
    String customerAddress;
    String buildingNumber = "";  // Used only for visuals in GUI, not stored in DB
    String streetName = "";
    String villageName = "";
    String cityName = "";
    String divisionName = "";
    String country = "";
    String phoneNumber;
    String createdBy;
    String lastUpdatedBy;

    LocalDateTime creationDate;
    LocalDateTime lastUpdate;

    public static ObservableList<Customer> allCustomers = FXCollections.observableArrayList();


    /**
     * Constructor used to create customer objects.
     * @param customerId Customer's unique ID.
     * @param divisionId ID of division.
     * @param postalCode Customer's postal code.
     * @param customerAddress Customer's address.
     * @param phoneNumber Customer's phone number.
     * @param creationDate Date of original creation.
     * @param lastUpdate Last time customer was updated.
     * @param createdBy User logged in when customer was created.
     * @param lastUpdatedBy User logged in when customer was last updated.
     */
    public Customer(int customerId, int divisionId, String postalCode, String customerName, String customerAddress, String phoneNumber, LocalDateTime creationDate, LocalDateTime lastUpdate, String createdBy, String lastUpdatedBy) throws SQLException {

        this.customerId = customerId;
        this.divisionId = divisionId;
        this.postalCode = postalCode;
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.phoneNumber = phoneNumber;
        this.divisionName = FirstLevelDivisionQuery.fetchDivisionName(this.divisionId);

        this.createdBy = createdBy;
        this.lastUpdatedBy = lastUpdatedBy;

        this.creationDate = creationDate;
        this.lastUpdate = lastUpdate;
    }


    /**
     * @return Customer ID.
     */
    public int getCustomerId() {return this.customerId;}


    /**
     * @return Division ID.
     */
    public int getDivisionId() {return this.divisionId;}


    /**
     * @return Postal code.
     */
    public String getPostalCode() {return this.postalCode;}


    /**
     * @return Customer name.
     */
    public String getCustomerName() {return this.customerName;}


    /**
     * Sets building number.
     */
    public void setBuildingNumber(String buildingNumber) {this.buildingNumber = buildingNumber;}


    /**
     * @return Building number.
     */
    public String getBuildingNumber() {return this.buildingNumber;}


    /**
     * Sets street name.
     */
    public void setStreetName(String streetName) {this.streetName = streetName;}


    /**
     * @return Street name.
     */
    public String getStreetName() {return this.streetName;}


    /**
     * Sets village name.
     * UK addresses have special formatting that can include a village.
     */
    public void setVillageName(String villageName) {this.villageName = villageName;}



    /**
     * @return Village name.
     */
    public String getVillageName() {return this.villageName;}


    /**
     * Sets city name.
     */
    public void setCityName(String cityName) {this.cityName = cityName;}


    /**
     * @return City name.
     */
    public String getCityName() {return this.cityName;}


    /**
     * Sets country name.
     */
    public void setCountry(String country) {this.country = country;}


    /**
     * @return Country.
     */
    public String getCountry() {return this.country;}


    /**
     * @return Customer address.
     */
    public String getCustomerAddress() {return this.customerAddress;}


    /**
     * @return Phone number.
     */
    public String getPhoneNumber() {return this.phoneNumber;}


    /**
     * @return Created by.
     */
    public String getCreatedBy() {return this.createdBy;}


    /**
     * @return Last updated by.
     */
    public String getLastUpdatedBy() {return this.lastUpdatedBy;}


    /**
     * @return Creation date.
     */
    public LocalDateTime getCreationDate() {return this.creationDate;}


    /**
     * @return The Creation date in timestamp format.
     */
    public Timestamp getCreationDateTimestamp() {
        return Timestamp.valueOf(this.creationDate);
    }


    /**
     * @return Last update.
     */
    public LocalDateTime getLastUpdate() {return this.lastUpdate;}


    /**
     * @return Last update time in timestamp format.
     */
    public Timestamp getLastUpdateTimestamp() {
        return Timestamp.valueOf(this.lastUpdate);
    }


    /**
     * Sets division name.
     */
    public void setDivisionName(String divisionName) {this.divisionName = divisionName;}


    /**
     * @return Division name.
     */
    public String getDivisionName() {return divisionName;}


    /**
     * Sets division index.
     */
    public void setDivisionIndex(int divisionIndex) {this.divisionIndex = divisionIndex;}


    /**
     * @return Division index.
     */
    public int getDivisionIndex() {return divisionIndex;}


    /**
     * Sets country index.
     */
    public void setCountryIndex(int countryIndex) {this.countryIndex = countryIndex;}


    /**
     * Creates country object using customer's country.
     * @return Customer country.
     */
    public CountriesAndDivisions createCustomerCountry() {
        return new CountriesAndDivisions(getCountry());
    }


    /**
     * @return Country index.
     */
    public int getCountryIndex() {return countryIndex;}

    /**
     * Adds new customer to a list of all customers.
     */
    public static void addCustomer(Customer newCustomer) {
        allCustomers.add(newCustomer);
    }


    /**
     *  Applies special formatting to input strings, to create a UK style address.
     *  @param buildingNumber Address building number.
     *  @param streetName Name of the street.
     *  @param village Village name.
     *  @param city City name.
     *  @return Complete address string to store in database.
     */
    public static String createAddress(String buildingNumber, String streetName, String village, String city) {
        if (village.length() == 0 || village.equals(" ")) {  // Village left blank case
            return (buildingNumber + " " + streetName + ", " + city);
        }
        else if (city.length() == 0 || city.equals(" ")) {  // City left blank case
            return (buildingNumber + " " + streetName + ", " + village);
        }
        else {  // Village and city provided
            return (buildingNumber + " " + streetName + ", " + village + ", " + city);
        }
    }

    /**
     *  Standard address creator.
     *  @param buildingNumber Address building number.
     *  @param streetName Name of the street.
     *  @param city City name.
     *  @return Complete address string to store in database.
     */
    public static String createAddress(String buildingNumber, String streetName, String city) {
        return (buildingNumber + " " + streetName + ", " + city);
    }


    /**
     *  Generates unique ID for customer.
     *  @return  New customer ID.
     */
    public static int generateCustomerId() throws SQLException {

        int id;  // ID will never = 0
        boolean idFound = false;

        id = allCustomers.size();

        if (id == 0) {id = 1;}  // Prevent ID from being 0


        while (!idFound && id < Integer.MAX_VALUE)  // Continue searching for an ID until the size becomes too large
        {
            if (CustomerQuery.checkCustomerIdExists(id)) // Check if generated ID is available, query returns true if id already exists
            {
                id = id + 1;
            }
            else
            {
                idFound = true;
            }
        }

        if(idFound) {
            return id;
        }
        else {
            Tools.consoleMessage(Tools.MsgType.ERROR, "Id was never generated", "Customer.generateCustomerId");
            return -1; // Return a value to show error
        }
    }



    /**
     *  Delete customer from local memory.
     * @param customerId ID of customer to be deleted.
     */
    public static void deleteCustomer(int customerId) {
        for (int i = 0; i < allCustomers.size(); i++) {
            if (allCustomers.get(i).getCustomerId() == customerId) {
                allCustomers.remove(i);
                break;
            }
        }
    }


    /**
     *  Finds index of customer in local list, based on ID.
     * @param customerId Customer ID used to find local list location.
     */
    public static int findLocalCustomerIndex(int customerId) {

        for (int i = 0; i < allCustomers.size(); i++) {
            if (allCustomers.get(i).getCustomerId() == customerId) {
                return i;
            }
        }
        Tools.consoleMessage(Tools.MsgType.ERROR, "Failed to find appointment", "Appointment.findLocalAppointmentIndex");
        return -1;  // Error case
    }


}

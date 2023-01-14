package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.Customer;
import model.Globals;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


/**
 *  This class allows customers to be written, updated, and deleted from database.
 */
public class CustomerQuery {


    /**
     * Retrieves all customers from database, creates local customer objects.
     * Adds customers to a static list located in customers class.
     */
    public static void fetchAllCustomers() throws SQLException {


        String sql = "SELECT * FROM Customers ORDER BY Customer_ID;";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();  // a result set is essentially an entire table, so we can fetch values from it, see below


        int customerId;
        int divisionId;
        int countryId;

        String customerName;
        String address;
        String country;
        String postalCode;
        String phoneNumber;
        String createdBy;
        String lastUpdatedBy;

        LocalDateTime creationDate;
        LocalDateTime lastUpdate;


        while(rs.next()){  //rs will read line by line through the results returned

            // fetch data from current row
            customerId = rs.getInt("Customer_ID");
            divisionId = rs.getInt("Division_ID");

            // This data isn't relevant when storing customer to DB, but is used during customer updates in GUI
            countryId = FirstLevelDivisionQuery.fetchCountryId(divisionId);
            country = CountriesQuery.fetchCountryName(countryId);

            customerName = rs.getString("Customer_Name");
            address = rs.getString("Address");
            postalCode = rs.getString("Postal_Code");
            phoneNumber = rs.getString("Phone");
            createdBy = rs.getString("Created_By");
            lastUpdatedBy = rs.getString("Last_Updated_By");

            creationDate = rs.getTimestamp("Create_Date").toLocalDateTime();  // This will convert UTC to local time
            lastUpdate = rs.getTimestamp("Last_Update").toLocalDateTime();

            Customer customer = new Customer(customerId, divisionId, postalCode, customerName, address, phoneNumber, creationDate, lastUpdate, createdBy, lastUpdatedBy);

            // Set data needed for GUI operations, this data does not get stored in DB
            customer.setCountry(country);
            customer.setDivisionName(FirstLevelDivisionQuery.fetchDivisionName(customer.getDivisionId()));

            Customer.allCustomers.add(customer);
        }
    }


    /**
     * Retrieves all customer IDs from database.
     * @return List containing all customer IDs found in database.
     */
    public static List<Integer> fetchAllId() throws SQLException {

        // Using ORDER_BY is important since certain methods rely on sorting being consistent
        String sql = "SELECT Customer_Id FROM customers ORDER BY Customer_ID;";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();  // a result set is essentially an entire table, so we can fetch values from it, see below

        List<Integer> allCustomerId = new ArrayList<>();

        while(rs.next()){  //rs will read line by line through the results returned

            // fetch customer_id and add it to list containing all customers
            allCustomerId.add(rs.getInt("Customer_ID"));
        }

        return allCustomerId;
    }


    /**
     * Retrieves all customer names from database.
     * @return List containing all customer names found in database.
     */
    public static ObservableList<String> fetchAllNames() throws SQLException {

        // Using ORDER_BY is important since certain methods rely on sorting being consistent
        String sql = "SELECT Customer_Name FROM customers ORDER BY Customer_ID;";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();  // a result set is essentially an entire table, so we can fetch values from it, see below

        String customerName;
        ObservableList<String> customerNames = FXCollections.observableArrayList();

        while(rs.next()){  //rs will read line by line through the results returned
            customerName = rs.getString("Customer_Name"); // fetch specified data from current row
            customerNames.add(customerName);
        }

        return customerNames;
    }


    /**
     * Creates a list containing strings with both the name and ID.
     * This is useful in combo boxes to help user visualize data.
     * For example: "John Doe (1)", "Jane Doe (2").
     * @return List containing all customer name ID pairs found in database.
     */
    public static ObservableList<String> fetchAllNameIdPairs() throws SQLException {

        // Using ORDER_BY is important since certain methods rely on sorting being consistent
        String sql = "SELECT Customer_Name FROM customers ORDER BY Customer_ID;";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();  // a result set is essentially an entire table, so we can fetch values from it, see below

        String customerName;
        ObservableList<String> customerNames = FXCollections.observableArrayList();

        while(rs.next()){  //rs will read line by line through the results returned
            customerName = rs.getString("Customer_Name"); // fetch specified data from current row
            customerNames.add("(" + fetchId(customerName) + ") " + customerName);
        }

        return customerNames;
    }


    /**
     * Creates a string containing both customer name and ID.
     * This is useful in combo boxes to help user visualize data.
     * For example: "John Doe (1)".
     * @return String containing both customer name and ID.
     */
    public static String fetchAllNameIdPair(int customerId) throws SQLException {
        String sql = "SELECT Customer_Name FROM customers WHERE Customer_ID = ?;";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, customerId);

        ResultSet rs = ps.executeQuery();  // a result set is essentially an entire table, so we can fetch values from it, see below

        String customerName;
        String nameIdPair = "";

        while(rs.next()){  //rs will read line by line through the results returned
            customerName = rs.getString("Customer_Name"); // fetch specified data from current row
            nameIdPair = "(" + fetchId(customerName) + ") " + customerName;
        }

        return nameIdPair;
    }


    /**
     * Retrieves customer ID based on customer name.
     * This is useful in combo boxes to help user visualize data.
     * @param customerName The name of customer to find ID of.
     * @return Customer ID.
     */
    public static int fetchId(String customerName) throws SQLException {

        String sql = "SELECT Customer_Id FROM customers WHERE Customer_Name = ?;";

        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, customerName);
        ResultSet rs = ps.executeQuery();  // a result set is essentially an entire table, so we can fetch values from it, see below

        int customerId = -1;  // Temp value
        while(rs.next()){  //rs will read line by line through the results returned
            customerId = rs.getInt("Customer_ID"); // fetch specified data from current row
        }

        return customerId;
    }


    /**
     * Checks the database to see if customer ID exists.
     * @param id The ID to be checked.
     * @return True if customer ID already exists.
     */
    public static boolean checkCustomerIdExists(int id) throws SQLException {

        String sql = "SELECT Customer_ID FROM customers ORDER BY Customer_ID;";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();  // a result set is essentially an entire table, so we can fetch values from it, see below

        List<Integer> allCustomerId = new ArrayList<>();

        while(rs.next()){  //rs will read line by line through the results returned

            // fetch customer_id and add it to list containing all customer
            allCustomerId.add(rs.getInt("Customer_ID"));
        }


        return allCustomerId.contains(id);
    }


    /**
     * Writes new customer to database.
     * @param customer The customer object to be written to database.
     */
    public static void writeCustomer(Customer customer) throws SQLException {
        if (Globals.writeToDatabase) {
            String sql = "INSERT INTO customers values(?,?,?,?,?,?,?,?,?,?);";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ps.setInt(1, customer.getCustomerId());
            ps.setString(2, customer.getCustomerName());
            ps.setString(3, customer.getCustomerAddress());
            ps.setString(4, customer.getPostalCode());
            ps.setString(5, customer.getPhoneNumber());
            ps.setTimestamp(6, customer.getCreationDateTimestamp());
            ps.setString(7, customer.getCreatedBy());
            ps.setTimestamp(8, customer.getLastUpdateTimestamp());
            ps.setString(9, customer.getLastUpdatedBy());
            ps.setInt(10, customer.getDivisionId());



            ps.executeUpdate();

        }
    }


    /**
     * Updates customer in database, by overwriting old customer with new one.
     * @param newCustomer The new customer to be written to database.
     * @param customerId The ID of the customer to be overwritten.
     */
    public static void updateCustomer(Customer newCustomer, int customerId) throws SQLException {

        String sql = "DELETE FROM customers WHERE Customer_ID = ?;";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, customerId);

        ps.executeUpdate();

        writeCustomer(newCustomer);
    }


    /**
     * Retrieves all customers in given country.
     * @param countryNameInput The country to associated customers with.
     * @return List of customers in given country.
     */
    public static ObservableList<Customer> fetchAllCustomersByCountry(String countryNameInput) throws SQLException {

        ObservableList<Customer> customers = FXCollections.observableArrayList();
        int countryId = CountriesQuery.fetchCountryId(countryNameInput);

        String sql = "SELECT cust.*, cn.Country " +
                     "FROM customers cust " +
                     "INNER JOIN first_level_divisions fld " +
                     "ON cust.Division_ID = fld.Division_ID " +
                     "INNER JOIN countries cn " +
                     "ON fld.Country_ID = cn.Country_ID WHERE cn.Country_ID = ?;";


        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, countryId);

        ResultSet rs = ps.executeQuery();  // a result set is essentially an entire table, so we can fetch values from it, see below


        int customerId;
        int divisionId;

        String customerName;
        String address;
        String country;
        String postalCode;
        String phoneNumber;
        String createdBy;
        String lastUpdatedBy;

        LocalDateTime creationDate;
        LocalDateTime lastUpdate;





        while(rs.next()){  //rs will read line by line through the results returned

            // fetch data from current row
            customerId = rs.getInt("Customer_ID");
            divisionId = rs.getInt("Division_ID");

            // This data isn't relevant when storing customer to DB, but is used for certain reporting views
            country = rs.getString("Country");

            customerName = rs.getString("Customer_Name");
            address = rs.getString("Address");
            postalCode = rs.getString("Postal_Code");
            phoneNumber = rs.getString("Phone");
            createdBy = rs.getString("Created_By");
            lastUpdatedBy = rs.getString("Last_Updated_By");

            creationDate = rs.getTimestamp("Create_Date").toLocalDateTime();  // This will convert UTC to local time
            lastUpdate = rs.getTimestamp("Last_Update").toLocalDateTime();

            Customer customer = new Customer(customerId, divisionId, postalCode, customerName, address, phoneNumber, creationDate, lastUpdate, createdBy, lastUpdatedBy);

            // Set data needed for GUI operations, this data does not get stored in DB
            customer.setCountry(country);
            customer.setDivisionName(FirstLevelDivisionQuery.fetchDivisionName(customer.getDivisionId()));

            customers.add(customer);
        }

        return customers;
    }


    /**
     * Deletes customer in database.
     * @param customerId The ID of customer to be deleted.
     */
    public static void deleteCustomer(int customerId) throws SQLException {

        if (Globals.writeToDatabase){
            String sql = "DELETE from customers WHERE Customer_ID = ?;";
            PreparedStatement ps = JDBC.connection.prepareStatement(sql);
            ps.setInt(1, customerId);

            ps.executeUpdate();
        }
    }
}

package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 *  This class allows contacts to be written, updated, and deleted from database.
 */
public class ContactsQuery {


    /**
     * Retrieves all contact IDs from database.
     * @return List containing all contact IDs found in database.
     */
    public static ObservableList<Integer> fetchAllId() throws SQLException {

        // Using ORDER_BY is important since certain methods count on sorting being consistent
        String sql = "SELECT Contact_Id FROM contacts ORDER BY Contact_Id;";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();  // a result set is essentially an entire table, so we can fetch values from it, see below

        ObservableList<Integer> allContactId = FXCollections.observableArrayList();

        while(rs.next()){  //rs will read line by line through the results returned

            // fetch contact_id and add it to list containing all contacts
            allContactId.add(rs.getInt("Contact_ID"));
        }

        return allContactId;
    }


    /**
     * Retrieves all contact names from database.
     * @return List containing all contact names found in database.
     */
    public static ObservableList<String> fetchAllContactNames() throws SQLException {
        String sql = "SELECT Contact_Name FROM contacts ORDER BY Contact_Id";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();  // a result set is essentially an entire table, so we can fetch values from it, see below

        String contactName;
        ObservableList<String> contactNames = FXCollections.observableArrayList();

        while(rs.next()){  //rs will read line by line through the results returned
            contactName = rs.getString("Contact_Name"); // fetch specified data from current row
            contactNames.add(contactName);
        }

        return contactNames;
    }


    /**
     * Creates a list containing strings with both the name and ID.
     * This is useful in combo boxes to help user visualize data.
     * For example: "John Doe (1)", "Jane Doe (2").
     * @return List containing all contact name ID pairs found in database.
     */
    public static ObservableList<String> fetchAllNameIdPairs() throws SQLException {
        String sql = "SELECT Contact_Name FROM contacts ORDER BY Contact_Id";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();  // a result set is essentially an entire table, so we can fetch values from it, see below

        String contactName;
        ObservableList<String> contactNames = FXCollections.observableArrayList();

        while(rs.next()){  //rs will read line by line through the results returned
            contactName = rs.getString("Contact_Name"); // fetch specified data from current row
            contactNames.add("(" + fetchContactId(contactName) + ") " + contactName);
        }

        return contactNames;
    }


    /**
     * Creates a string containing both customer name and ID.
     * This is useful in combo boxes to help user visualize data.
     * For example: "John Doe (1)".
     * @return String containing both customer name and ID.
     */
    public static String fetchAllNameIdPair(int contactId) throws SQLException {
        String sql = "SELECT Contact_Name FROM contacts WHERE Contact_Id = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, contactId);

        ResultSet rs = ps.executeQuery();  // a result set is essentially an entire table, so we can fetch values from it, see below

        String contactName;
        String nameIdPair = "";

        if(rs.next()){
            contactName = rs.getString("Contact_Name"); // fetch specified data from current row
            nameIdPair = "(" + contactId + ") " + contactName;
        }

        return nameIdPair;
    }

    /**
     * Fetches contact ID based on given contact name.
     * @param contactName The contact name to find the ID of.
     * @return Contact ID.
     */
    public static int fetchContactId(String contactName) throws SQLException {
        String sql = "SELECT Contact_ID FROM contacts WHERE Contact_Name = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, contactName);
        ResultSet rs = ps.executeQuery();  // a result set is essentially an entire table, so we can fetch values from it, see below


        int contactId = -1; // Temp value

        while(rs.next()){  //rs will read line by line through the results returned
            contactId = rs.getInt("Contact_ID"); // fetch specified data from current row
        }

        return contactId;
    }


    /**
     * Retrieves contact name based on contact ID.
     * This is simply a method with a lambda expression inside it.
     * @param contactId The ID of contact to find the name of.
     * @return Contact name associated with contact ID.
     */
    public static String fetchContactName(int contactId) throws SQLException {

        // Lambda expression here is used to fetch Contact_Name based on Contact_ID
        FetchString fetchContactName = (id) -> {

            String result = "N/A";  // Default value, must be overwritten

            String sqlContact = "SELECT Contact_Name FROM contacts WHERE Contact_Id = ?";
            PreparedStatement psContact = JDBC.connection.prepareStatement(sqlContact);
            psContact.setInt(1, id);
            ResultSet rsContact = psContact.executeQuery();

            if(rsContact.next()) {  // No need to use loop since there will only be one value
                result = rsContact.getString("Contact_Name");
            }

            return result;
        };

        return(fetchContactName.fetchString(contactId));

    }

}

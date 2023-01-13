package DAO;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 *  This class allows for the retrieval and updating of users in the database.
 */
public class UserQuery {


    /**
     *  Updates user password.
     *  @param password Original password.
     *  @param newPassword New password.
     *  @return Amount of rows affected.
     */
    public static int update(String password, String newPassword) throws SQLException {

        String sql = "UPDATE USERS SET Password = ? WHERE Password = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);

        ps.setString(1, newPassword);
        ps.setString(2, password);

        int rowsAffected = ps.executeUpdate();
        return rowsAffected;

    }


    /**
     *  Deletes user.
     * @param userId ID of user to delete.
     * @return rows affected.
     */
    public static int delete(int userId) throws SQLException {
        String sql = "DELETE FROM USERS WHERE User_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, userId);

        int rowsAffected = ps.executeUpdate();
        return rowsAffected;

    }


    /**
     *  Selects all users from database.
     */
    public static void select() throws SQLException {
        String sql = "SELECT * FROM USERS";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();  // a result set is essentially an entire table, so we can fetch values from it, see below

        while(rs.next()){  //rs will read line by line through the results returned
            int userId = rs.getInt("User_ID"); // fetch user_id from current row
            String userName = rs.getString("User_Name"); // fetch user_name from current row
        }
    }


    /**
     *  Locates user password.
     *  @param userId ID of user to find password of.
     *  @return password.
     */
    public static String locatePassword(int userId) throws SQLException {
        String sql = "SELECT * FROM USERS WHERE User_Id  = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();  // a result set is essentially an entire table, so we can fetch values from it, see below
        String userPassword = "N/A";  // Placeholder that must be overwritten

        while (rs.next()) {  //rs will read line by line through the results returned
            userPassword = rs.getString("Password");
        }

        return userPassword;
    }


    /**
     * Checks the database to see if user ID exists.
     * @param userId ID to check existence of.
     * @return True if ID exists.
     */
    public static boolean idExists(int userId) throws SQLException {
        String sql = "SELECT * FROM USERS WHERE User_Id = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();  // a result set is essentially an entire table, so we can fetch values from it, see below

        if(rs.next()) {
            return true;
        }
        else {
            return false;
        }
    }


    /**
     * Fetches username based on ID.
     * @param userId ID to fetch name of.
     * @return username.
     */
    public static String fetchUsername(int userId) throws SQLException {


        String sql = "SELECT User_Name FROM users WHERE User_Id = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, userId);
        ResultSet rs = ps.executeQuery();  // a result set is essentially an entire table, so we can fetch values from it, see below

        String userName = "N/A";  // Placeholder must be overwritten

        while(rs.next()){  //rs will read line by line through the results returned
            userName = rs.getString("User_Name"); // fetch user_name from current row
        }

        return userName;
    }


    /**
     * Fetches all user IDs in the database.
     * @return List of all IDs.
     */
    public static ObservableList<Integer> fetchAllUserId() throws SQLException {


        String sql = "SELECT User_ID FROM users ORDER BY User_ID;";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();  // a result set is essentially an entire table, so we can fetch values from it, see below

        ObservableList<Integer> allUserId = FXCollections.observableArrayList();

        while(rs.next()){  //rs will read line by line through the results returned
            allUserId.add(rs.getInt("User_ID"));
        }

        return allUserId;
    }


    /**
     * Creates a list containing strings with both the name and ID.
     * This is useful in combo boxes to help user visualize data.
     * For example: "John Doe (1)", "Jane Doe (2").
     * @return List containing all username ID pairs found in database.
     */
    public static ObservableList<String> fetchAllNameIdPairs() throws SQLException {

        String sql = "SELECT User_Name FROM users ORDER BY User_ID;";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();  // a result set is essentially an entire table, so we can fetch values from it, see below

        String userName;
        ObservableList<String> userNames = FXCollections.observableArrayList();

        while(rs.next()){  //rs will read line by line through the results returned
            userName = rs.getString("User_Name"); // fetch specified data from current row
            userNames.add("(" + fetchId(userName) + ") " + userName);
        }

        return userNames;
    }


    /**
     * Fetches user ID based on given username.
     * @param userName Name to find ID of.
     * @return User ID.
     */
    public static int fetchId(String userName) throws SQLException {

        String sql = "SELECT User_Id FROM users WHERE User_Name = ?;";

        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, userName);
        ResultSet rs = ps.executeQuery();  // a result set is essentially an entire table, so we can fetch values from it, see below

        int userId = -1;  // Temp value
        while(rs.next()){  //rs will read line by line through the results returned
            userId = rs.getInt("User_ID"); // fetch specified data from current row
        }

        return userId;
    }
}

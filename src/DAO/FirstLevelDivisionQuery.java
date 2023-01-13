package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 *  This class allows for the retrieval of first level divisions from database.
 */
public class FirstLevelDivisionQuery {


    /**
     *  Fetches divisions associated with country.
     *  For example: If country is USA, fetches all 50 states.
     *  @param countryName Name of country to fetch divisions of.
     *  @return String list containing all divisions.
     */
    public static ObservableList<String> fetchDivisions(String countryName) throws SQLException {

        int countryId= CountriesQuery.fetchCountryId(countryName);

        String sql = "SELECT Division FROM First_Level_Divisions WHERE Country_ID = ? ORDER BY Division;";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, countryId);
        ResultSet rs = ps.executeQuery();  // a result set is essentially an entire table, so we can fetch values from it, see below


        ObservableList<String> divisions = FXCollections.observableArrayList();


        while(rs.next()){  //rs will read line by line through the results returned
            divisions.add(rs.getString("Division")); // fetch specified data from current row
        }

        return divisions;
    }


    /**
     *  Fetches division name based on divisionId.
     *  @param divisionId ID of division to fetch name of.
     *  @return Name of division.
     */
    public static String fetchDivisionName(int divisionId) throws SQLException {

        String sql = "SELECT Division FROM First_Level_Divisions WHERE Division_ID = ?;";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, divisionId);
        ResultSet rs = ps.executeQuery();  // a result set is essentially an entire table, so we can fetch values from it, see below

        String divisionName = "N/A";  // Will be overwritten
        while(rs.next()){  //rs will read line by line through the results returned
            divisionName = rs.getString("Division"); // fetch specified data from current row
        }

        return divisionName;
    }

    /**
     * Retrieves all division IDs based country.
     * @param countryName Country to find divisions of.
     * @return List containing all division IDs of country.
     */
    public static List<Integer> fetchDivisionIds(String countryName) throws SQLException {

        int countryId= CountriesQuery.fetchCountryId(countryName);

        String sql = "SELECT Division_ID FROM First_Level_Divisions WHERE Country_ID = ? ORDER BY Division";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, countryId);
        ResultSet rs = ps.executeQuery();  // a result set is essentially an entire table, so we can fetch values from it, see below


        List<Integer> divisionIds = new ArrayList<>();


        while(rs.next()){  //rs will read line by line through the results returned
            divisionIds.add(rs.getInt("Division_ID")); // fetch specified data from current row
        }

        return divisionIds;
    }


    /**
     * Fetches division ID based on division name.
     * @param divisionName Name to find division ID of.
     * @return ID of division.
     */
    public static int fetchDivisionId(String divisionName) throws SQLException {

        String sql = "SELECT Division_ID FROM First_Level_Divisions WHERE Division = ?;";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, divisionName);
        ResultSet rs = ps.executeQuery();  // a result set is essentially an entire table, so we can fetch values from it, see below

        int divisionId = -1;  // Will be overwritten
        while(rs.next()){  //rs will read line by line through the results returned
            divisionId = rs.getInt("Division_ID"); // fetch specified data from current row
        }

        return divisionId;
    }


    /**
     * Retrieves country ID based on division ID.
     * @param divisionId Division to find country ID of.
     * @return country ID of division in question.
     */
    public static int fetchCountryId(int divisionId) throws SQLException {

        String sql = "SELECT Country_ID FROM First_Level_Divisions WHERE Division_ID = ?;";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, divisionId);
        ResultSet rs = ps.executeQuery();  // a result set is essentially an entire table, so we can fetch values from it, see below

        int countryId = -1;  // Will be overwritten
        while(rs.next()){  //rs will read line by line through the results returned
            countryId = rs.getInt("Country_ID"); // fetch specified data from current row
        }

        return countryId;
    }
}

package DAO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.CountriesAndDivisions;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


/**
 *  This class allows countries to be fetched from database.
 */
public class CountriesQuery {

    public static ObservableList<String> fetchAllCountries() throws SQLException {
        String sql = "SELECT * FROM countries ORDER BY Country_ID";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();  // a result set is essentially an entire table, so we can fetch values from it, see below

        String countryName;
        ObservableList<String> countryNames = FXCollections.observableArrayList();

        while(rs.next()){  //rs will read line by line through the results returned
            countryName = rs.getString("Country"); // fetch specified data from current row
            countryNames.add(countryName);
        }

        return countryNames;
    }


    /**
     * Fetches country ID based on given country name.
     * @param countryName The country name to find the ID of.
     * @return Country ID.
     */
    public static int fetchCountryId(String countryName) throws SQLException {
        String sql = "SELECT Country_ID FROM countries WHERE Country = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, countryName);
        ResultSet rs = ps.executeQuery();  // a result set is essentially an entire table, so we can fetch values from it, see below

        int countryId = -1;  // will be overwritten
        while(rs.next()){  //rs will read line by line through the results returned
            countryId = rs.getInt("Country_ID"); // fetch specified data from current row
        }

        return countryId;
    }


    /**
     * Retrieves country name based on country ID.
     * @param countryId The ID of country to find the name of.
     * @return Country name associated with country ID.
     */
    public static String fetchCountryName(int countryId) throws SQLException {
        String sql = "SELECT Country FROM countries WHERE Country_ID = ?;";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1, countryId);
        ResultSet rs = ps.executeQuery();  // a result set is essentially an entire table, so we can fetch values from it, see below

        String countryName = "N/A";  // will be overwritten
        while(rs.next()){  //rs will read line by line through the results returned
            countryName = rs.getString("Country"); // fetch specified data from current row
        }

        return countryName;
    }


    /**
     *  Associates division lists with country objects.
     */
    public static void createCountryDivisionPairs() throws SQLException {

        ObservableList<String> countryNames = fetchAllCountries();

        for(int i = 0; i < countryNames.size(); i++) {

            // Create new country using country name
            CountriesAndDivisions country = new CountriesAndDivisions(countryNames.get(i));

            // Fetch all divisions associated with that country from database, add them to object
            ObservableList<String> divisionNames = FirstLevelDivisionQuery.fetchDivisions(countryNames.get(i));
            country.addAllDivisions(divisionNames);

            // Fetch all division IDs associated with country from database, add them to object
            List<Integer> divisionIds = FirstLevelDivisionQuery.fetchDivisionIds(countryNames.get(i));
            country.addAllDivisionIds(divisionIds);

            // Add new country to list containing all countries
            CountriesAndDivisions.countries.add(country);
        }
    }
}

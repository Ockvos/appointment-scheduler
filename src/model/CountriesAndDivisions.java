package model;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Objects;


/**
 * A class used to create country objects and associate them with lists of divisions.
 */
public class CountriesAndDivisions {

    int countryId = -1;
    String countryName = "N/A";  // Default value
    public static ObservableList<CountriesAndDivisions> countries = FXCollections.observableArrayList();
    List<Integer> divisionIds;
    ObservableList<String> divisionNames = FXCollections.observableArrayList();

    /**
     * A constructor using country name.
     * Sets country name.
     * @param countryName Name of country.
     */
    public CountriesAndDivisions (String countryName) {
        this.countryName = countryName;
    }

    /**
     * A constructor using country ID.
     * sets country ID.
     * @param countryId ID of country.
     */
    public CountriesAndDivisions (int countryId) {
        this.countryId = countryId;
    }

    /**
     * Adds additional division to list of divisions.
     * @param division Name of division to be added.
     */
    public void addDivisionName(String division) {
        this.divisionNames.add(division);
    }


    /**
     * Adds additional division to list of divisions.
     * @param divisionName Name of division to be added.
     * @param divisionId ID of division to be added.
     */
    public void addDivisionId(int divisionId, String divisionName) {

        // Searches through list of country names, if given name matches input name, assign ID
        for (String name : this.divisionNames) {
            if (Objects.equals(name, divisionName)) {
                this.divisionIds.add(divisionId);
            }
        }
    }

    /**
     * Creates a list containing all country's division names.
     * @param divisionNames List of names to be added.
     */
    public void addAllDivisions(ObservableList<String> divisionNames) {
        this.divisionNames = divisionNames;
    }


    /**
     * Creates a list containing all country's division IDs.
     * @param divisionIds List of IDs to be added.
     */
    public void addAllDivisionIds(List<Integer> divisionIds) {
        this.divisionIds = divisionIds;
    }


    /**
     * Sets country ID.
     * @param countryId ID to be set.
     */
    public void setCountryId(int countryId) {
        this.countryId = countryId;
    }


    /**
     * Sets country name.
     * @param countryName Name to be set.
     */
    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    /**
     * @return Country name.
     */
    public String getCountryName() {
        return this.countryName;
    }


    /**
     * @return All country names.
     */
    public static ObservableList<String> getAllCountryNames() {

        ObservableList<String> countryNames = FXCollections.observableArrayList();

        for (CountriesAndDivisions country : countries) {
            countryNames.add(country.countryName);
        }

        return countryNames;
    }

    /**
     * @return All division names.
     */
    public ObservableList<String> getAllDivisionNames() {
        return this.divisionNames;
    }

}

package seng302.model.locationData;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import seng302.model.Marshal;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static seng302.model.DrugInteractionsCache.createMapper;

/**
 * Class to hold information and methods relating to the allowable countries.
 */
public class CountryList {

    /**
     * List of 'allowable' countries
     */
    private Collection<String> allowableCountries;
    /**
     * List of country codes mapped to country names
     */
    private Map<String, String> countriesMap;
    private static CountryList countryList;

    private static final String TEST_LOCATION = "../countries";
    private static final String PROD_LOCATION = "countries";
    private static final String FILE_NAME = "countries";
    private static final String JSON_ENDING = ".json";

    private static String countriesLocation = PROD_LOCATION;

    /**
     * List of countries that will always be in the list of valid list of countries.
     */
    private static final String[] defaultValidCountries = {"NEW ZEALAND"};


    /**
     * Imports the list of valid countries from the countries directory.
     *
     * @return An Array list of country names.
     */
    public static List<String> importCountries() {

        // Retrieve the countries from the countries directory.
        File file = new File(countriesLocation + File.separator + FILE_NAME + JSON_ENDING);

        // Import the countries.
        ObjectMapper mapper = createMapper();
        ArrayList<String> importedCountries = new ArrayList<>();
        // Handle case where no file have been exported before.
        if (file != null) {
            try {
                JavaType type = mapper.getTypeFactory()
                        .constructCollectionType(ArrayList.class, String.class);
                importedCountries = mapper.readValue(file, type);
            } catch (IOException exception) {
                System.out.println(exception.getMessage());
                System.out.println("ERROR: Could not import " + "countries/countries.json"
                        + ". It may not be a valid country save file.");
            }
        }
        return importedCountries;
    }


    /**
     * Exports an array list fo country names to file.
     * @param countries An array list of country name strings
     * @throws IOException If there is an error exporting the files
     */
    public static void exportCountries(List<String> countries) throws IOException {
        ObjectMapper mapper = createMapper();
        String exportPath = FILE_NAME + File.separator;
        Marshal.createDirectory(PROD_LOCATION); // If it does not already exist.
        File file = new File(exportPath + "countries.json");
        mapper.writeValue(file, countries);
    }


    /**
     * Populates the list of valid countries with those from the defaultValidCountriesStringArray if they are not in the list.
     */
    public void populateCountryWithNewCountries(List<String> newCountries) {
        for (String country: newCountries) {
            if (!allowableCountries.contains(country)) {
                allowableCountries.add(country);
            }
        }
    }

    public Collection<String> getAllowableCountries() {
        return allowableCountries;
    }



    /**
     * Removes the given country from the list of valid countries if it exists in the list.
     * @param country A string of the country to be removed
     * @throws IllegalArgumentException If the country is New Zealand
     */
    public void removeCountry(String country) throws IllegalArgumentException{
        ArrayList<String> validCountries = new ArrayList<>(Arrays.asList(defaultValidCountries));
        if (validCountries.contains(country.toUpperCase())) {
            throw new IllegalArgumentException("You cannot remove " + country + "!");
        } else {
            allowableCountries.remove(country.toUpperCase());
        }
    }


    /**
     * Adds the given country to the valid country list if its not an empty string or a duplicate.
     * @param country String of the new country to add
     * @throws IllegalArgumentException If the string is blank or is a duplicate of an existing country.
     */
    public void addCountry(String country) throws IllegalArgumentException {
        if (country.equals("")) {
            throw new IllegalArgumentException("Country name must not be blank!");
        } else if (allowableCountries.contains(country.toUpperCase())) {
            throw new IllegalArgumentException(country + " already exists in list!");
        } else {
            allowableCountries.add(country.toUpperCase());
        }
    }


    /**
     * Replaces an existing country name in the account manager valid countries list with a new name.
     * @param oldCountry String of the country to replace
     * @param newCountry String of the new country name
     * @throws IllegalArgumentException Thrown when the new country name is an empty string
     */
    public void editCountry(String oldCountry, String newCountry) throws IllegalArgumentException {
        if (newCountry.equals("")) {
            throw new IllegalArgumentException("Country name must not be blank!");
        } else if (allowableCountries.contains(newCountry.toUpperCase())) {
            throw new IllegalArgumentException(newCountry + " already exists in list!");
        } else {
            removeCountry(oldCountry);
            addCountry(newCountry);
        }

    }

    /**
     * Populates the countries map with default values
     */
    private void initialiseCountriesMap() {
        countriesMap = new LinkedHashMap<>();
        countriesMap.put("NZ", "NEW ZEALAND");
        countriesMap.put("AU", "AUSTRALIA");
        countriesMap.put("NC", "NEW CALEDONIA");
        countriesMap.put("VU", "VANUATU");
        countriesMap.put("FJ", "FIJI");
        countriesMap.put("TO", "TONGA");
        countriesMap.put("NU", "NUIE");
        countriesMap.put("CK", "COOK ISLANDS");
        countriesMap.put("WF", "WALLIS AND FUTUNA");
        countriesMap.put("AS", "AMERICAN SAMOA");
        countriesMap.put("TK", "TOKELAU");
        countriesMap.put("TV", "TUVALU");
        countriesMap.put("PF", "FRENCH POLYNESIA");
        countriesMap.put("GB", "GREAT BRITAIN");
        countriesMap.put("US", "UNITED STATES OF AMERICA");
        countriesMap.put("FR", "FRANCE");
        countriesMap.put("DE", "GERMANY");
        countriesMap.put("IT", "ITALY");
        countriesMap.put("CN", "CHINA");
        countriesMap.put("JP", "JAPAN");
        countriesMap.put("RU", "RUSSIA");
        countriesMap.put("CA", "CANADA");

        countriesMap.put("NAR", "NARNIA");
        countriesMap.put("ATL", "ATLANTIS");
        countriesMap.put("BKD", "BARBAR'S KINGDOM");
        countriesMap.put("GDR", "GONDOR");
        countriesMap.put("MDR", "MORDOR");
        countriesMap.put("WKD", "WAKANDA");
        countriesMap.put("GL", "GAUL");
    }

    /**
     * Constructor with no arguments sets up the list with just the default allowed countries.
     */
    public CountryList() {
//        initialiseCountriesMap();
//        allowableCountries = importCountries();
//        if (allowableCountries.isEmpty()) {
//            allowableCountries = Arrays.asList(defaultValidCountries);
//        }
        allowableCountries = new ArrayList<>();
    }



    /**
     * Returns an instance of the CountryList class. If an instance exists, returns it. If no instance exists, creates
     * it and returns it.
     * @return The instance of the CountryList class.
     */
    public static CountryList getInstance() {
        if (countryList == null) {
            countryList = new CountryList();
            countryList.importCountries();
        }
        return countryList;
    }

    /**
     * Takes a country code and returns the name of the country. If the code is not recognised, returns null.
     * @param countryCode The country's code.
     * @return The country's name (or null, if not recognised).
     */
    public static String getCountryName(String countryCode) {
        return "New Zealand";
    }

    /**
     * Takes a country name and returns the country's code. If the name is not recognised, returns null.
     * @param targetCountryName The country's name (or null, if not recognised).
     * @return The country's code.
     */
    public String getCountryCode(String targetCountryName) {
        for (String countryCode : countriesMap.keySet()) {
            if (countriesMap.get(countryCode).equals(targetCountryName)) {
                return countryCode;
            }
        }
        return null;
    }

    /**
     * Takes a country name and checks whether or not it is in the list of allowable countries.
     * @param countryName The name of the country to be checked.
     * @return True if the country is allowable; false if it is not.
     */
    public boolean isAllowable(String countryName) {
        if (allowableCountries.contains(countryName)) {
            return true;
        }
        return false;
    }

    /**
     * Changes the location of the countries file for testing.
     */
    public static void setTestImport() {
        countriesLocation = TEST_LOCATION;
    }

}

package seng302.model.locationData;

import java.util.*;

/**
 * Class to hold information and methods relating to regions.
 */
public class RegionList {

    /**
     * HashMap of countries (as keys) mapped to arraylists of their regions.
     */
    private Map<String, ArrayList> countryRegions;
    private static RegionList regionList;

    /**
     * Adds a region to the given country.
     * @param regionName Name of the region to be added.
     * @param countryName Name of the country to which the region is to be added.
     */
    public void addRegionToCountry(String regionName, String countryName) {
        //NZRegions.add(regionName);
        Collection regions = countryRegions.get(countryName);
        regions.add(regionName);
    }

    /**
     * Removes a region from a given country.
     * @param regionName Name of the region to be removed.
     * @param countryName Name of the country.
     */
    public void removeRegionFromCountry(String regionName, String countryName) {
        Collection regions = countryRegions.get(countryName);
        regions.remove(regionName);
    }

    /**
     * Renames a region in the given country.
     * @param oldRegionName The old name of the region
     * @param newRegionName The new name of the region
     * @param countryName The name of the country containing the region
     */
    public void renameRegionInCountry(String oldRegionName, String newRegionName, String countryName) {
        removeRegionFromCountry(oldRegionName, countryName);
        addRegionToCountry(newRegionName, countryName);
    }

    /**
     * Constructor with no arguments. Creates the default list of New Zealand regions.
     */
    private RegionList() {
        ArrayList<String> NZRegions;
        ArrayList<String> GaulRegions;
        countryRegions = new LinkedHashMap();


        NZRegions = new ArrayList(Arrays.asList("Northland", "Auckland", "Waikato", "Bay of Plenty", "Gisborne",
                "Hawke's Bay", "Taranaki", "Manawatu-Wanganui", "Wellington", "Tasman", "Nelson", "Marlborough", "West Coast",
                "Canterbury", "Otago", "Southland", "Chatham Islands"));

        GaulRegions = new ArrayList(Arrays.asList("Armorican Gaul", "Belgica", "Central Gaul", "Eastern Gaul"));

        countryRegions.put("NZ", NZRegions);
        countryRegions.put("GL", GaulRegions);
    }

    /**
     * Returns an instance of RegionList. If an instance exists, returns it. If no instance exists, creates it and
     * returns it.
     * @return The instance of the Region List class.
     */
    public static RegionList getInstance() {
        if (regionList == null) {
            regionList = new RegionList();
        }
        return regionList;
    }

    /**
     * Returns a list of the regions of the country with the given code. Returns null if the country code cannot be found
     * in the countriesMap.
     * @param countryCode The country's code
     * @return A collection (array-list) containing the regions in the country.
     */
    public Collection<String> getRegions(String countryCode) {
        Collection<String> regions;
        regions = countryRegions.get(countryCode);
        return regions;
    }


}

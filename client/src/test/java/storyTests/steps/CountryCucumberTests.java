package storyTests.steps;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import seng302.model.locationData.CountryList;

import java.util.ArrayList;
import java.util.Comparator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
public class CountryCucumberTests {

    private CountryList countryList;
    private ArrayList<String> countries;

    private ArrayList<String> imported;





    @Given("^I am viewing the countries list$")
    public void iAmViewingTheCountriesList()  {
        countryList = CountryList.getInstance();
        countries = (ArrayList<String>) countryList.getAllowableCountries();
    }

    @Then("^I should should see; \"([^\"]*)\", \"([^\"]*)\", \"([^\"]*)\", \"([^\"]*)\", \"([^\"]*)\", \"([^\"]*)\", and \"([^\"]*)\"\\.$")
    public void iShouldShouldSeeAnd(String atlantis, String barbarsKingdom, String gondor, String mordor, String narnia, String newZealand, String wakanda) {
        assertTrue(countries.contains(atlantis));
        assertTrue(countries.contains(barbarsKingdom));
        assertTrue(countries.contains(gondor));
        assertTrue(countries.contains(mordor));
        assertTrue(countries.contains(narnia));
        assertTrue(countries.contains(newZealand));
        assertTrue(countries.contains(wakanda));
    }

    @Then("^The number of countries in the table should be \"([^\"]*)\"\\.$")
    public void theNumberOfCountriesInTheTableShouldBe(String number)  {
        assertEquals(countries.size(), Integer.parseInt(number));
    }

    @When("^I enter \"([^\"]*)\" into the insert box\\.$")
    public void iEnterIntoTheInsertBox(String country) {
        countryList.addCountry(country);
    }

    @Then("^\"([^\"]*)\" should be added into the table between \"([^\"]*)\" and \"([^\"]*)\"\\.$")
    public void shouldBeAddedIntoTheTableBetweenAnd(String country1, String country2, String country3) {
        countries.sort(Comparator.naturalOrder());
        assertEquals(2,countries.indexOf(country1));
        assertEquals(1,countries.indexOf(country2));
        assertEquals(3,countries.indexOf(country3));
        countryList.removeCountry(country1);
    }

    @Then("^\"([^\"]*)\" should not be added\\.$")
    public void shouldNotBeAdded(String country)  {

    }

    @When("^I have entered \"([^\"]*)\" into the insert box\\.$")
    public void iHaveEnteredIntoTheInsertBox(String country) throws Throwable {
        boolean thrown = false;
        try {
            countryList.addCountry(country);
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Then("^the size of the list should be \"([^\"]*)\"\\.$")
    public void theSizeOfTheListShouldBe(String size) {
        assertEquals(countries.size(), Integer.parseInt(size));
    }

    @When("^I have entered \"([^\"]*)\" and added it to the country list\\.$")
    public void iHaveEnteredAndAddedItToTheCountryList(String country) {
        countryList.addCountry(country);
    }

    @When("^I edit \"([^\"]*)\" name to \"([^\"]*)\"\\.$")
    public void iEditItsNameTo(String original, String newName)  {
        countryList.editCountry(original, newName);
    }

    @Then("^\"([^\"]*)\" should be replaced with \"([^\"]*)\"$")
    public void shouldBeReplacedWith(String oldCountry, String newCountry) {
        assertTrue(!countries.contains(oldCountry));
        assertTrue(countries.contains(newCountry));
        countryList.removeCountry(newCountry);
    }

    @When("^I edit \"([^\"]*)\" and try to change its name to \"([^\"]*)\"$")
    public void iEditAndTryToChangeItsNameTo(String oldName, String newName) {
        boolean thrown= false;
        try {
            countryList.editCountry(oldName, newName);
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);
    }

    @Then("^\"([^\"]*)\" will still be in the list\\.$")
    public void willStillBeInTheList(String oldName)  {
        assertTrue(countries.contains(oldName));
    }

    @Then("^\"([^\"]*)\" should not be\\.$")
    public void shouldNotBe(String newName) {
        assertTrue(!countries.contains(newName));
    }

    @When("^I delete \"([^\"]*)\"\\.$")
    public void iDelete(String country)  {
        countryList.removeCountry(country);
    }

    @Then("^\"([^\"]*)\" will no longer be in the list of countries\\.$")
    public void willNoLongerBeInTheListOfCountries(String country) {
        assertTrue(!countries.contains(country));
    }

    @When("^I delete \"([^\"]*)\"$")
    public void iDelete2(String country) {
        boolean thrown = false;
        try {
            countryList.removeCountry(country);
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertTrue(thrown);

    }

    @Then("^\"([^\"]*)\" should not be deleted and remain in the list of countries\\.$")
    public void shouldNotBeDeletedAndRemainInTheListOfCountries(String country) {
        assertTrue(countries.contains(country));
    }

    @When("^I import a list of countries containing \"([^\"]*)\", \"([^\"]*)\", and \"([^\"]*)\"\\.$")
    public void iImportAListOfCountriesContainingAnd(String country1, String country2, String country3) {
        ArrayList<String> newCountries = new ArrayList<>();
        newCountries.add(country1);
        newCountries.add(country2);
        newCountries.add(country3);
        boolean thrown = false;
        try {
            countryList.populateCountryWithNewCountries(newCountries);
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        //assertTrue(thrown);

    }

    @Then("^only \"([^\"]*)\" and \"([^\"]*)\" should be added to the country list\\.$")
    public void onlyAndShouldBeAddedToTheCountryList(String country1, String country2) {
        assertTrue(countries.contains(country1));
        assertTrue(countries.contains(country2));
    }

    @Then("^the number of countries is \"([^\"]*)\"\\.$")
    public void theNumberOfCountriesIs(String size) {
        assertEquals(countries.size(), Integer.parseInt(size));
    }

    @When("^I import countries from file\"\\.$")
    public void iImportCountriesFromFile()  {
        imported = new ArrayList<>(CountryList.importCountries());
    }

    @Then("^there should be \"([^\"]*)\" default countries in the list\\.$")
    public void thereShouldBeDefaultCountriesInTheList(String number)  {
        assertEquals(imported.size(), Integer.parseInt(number));
    }
}

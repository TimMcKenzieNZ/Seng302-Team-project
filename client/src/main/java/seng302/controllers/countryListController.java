package seng302.controllers;


import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.paint.Color;
import seng302.App;
import seng302.model.AccountManager;
import seng302.model.PageNav;
import seng302.model.Session;
import seng302.model.enums.ADDRESSES;
import seng302.model.locationData.CountryList;
import seng302.services.DeleteTask;
import seng302.services.GetTask;
import seng302.services.PostTask;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Controls all GUI interactions with the countries List pane
 */
public class countryListController {

    /**
     * Observable list of the countries that are valid.
     */
    private ObservableList<String> observableCountries;

    /**
     * The country currently being selected in the GUI table
     */
    private static String selectedCountry;

    /**
     * A regular list of valid countries that underpins the observable list
     */
    private ArrayList<String> countries;

    /**
     * CountryList object that handles all additions, deletions, and modifications to the country list
     */
    private CountryList countryList;


    @FXML
    private TextField newCountryField;

    @FXML
    private TableView<String> countryTable;

    @FXML
    private TableColumn<String, String> countryColumn;

    @FXML
    private Label saveMessage;

    @FXML
    private Button backButton;

    public static Session session = App.getCurrentSession();

    @FXML
    /**
     * Initializes country table and all variables.
     */
    public void initialize() {
        backButton.getStyleClass().add("backButton");
        getCountries();
    }

    private void getCountries() {

        GetTask task = new GetTask(ArrayList.class, ADDRESSES.SERVER.getAddress(), ADDRESSES.GET_COUNTRIES.getAddress(), session.getToken());

        task.setOnSucceeded(event -> {
            ArrayList<String> listOfCountries = (ArrayList) task.getValue();
            countryList = new CountryList();
            countryList.populateCountryWithNewCountries(listOfCountries);
            countries = (ArrayList<String>) countryList.getAllowableCountries();
            countryTable.setEditable(false);
            countryColumn.setEditable(false);

            observableCountries = FXCollections.observableArrayList(countries);
            SortedList<String> sortedCountries = new SortedList<String> (observableCountries, Comparator.comparing(String::toUpperCase));
            countryColumn.setCellValueFactory(cellData -> new SimpleStringProperty( cellData.getValue()));
            countryColumn.setCellFactory(TextFieldTableCell.forTableColumn());
            countryTable.setItems(sortedCountries);


            populateCountryTable();
        });
        task.setOnFailed(event -> {
            throw new IllegalArgumentException("Failed to get countries");

        });
        new Thread(task).start();

    }





    @FXML
    /**
     * Goes back to the administrator main pane in the GUI
     */
    void back(ActionEvent event) {
        {
            PageNav.loadNewPage(PageNav.ADMINMENU);
        }

    }


    @FXML
    /**
     * Deletes the selected country from the country list, if possible. Default countries cannot be deleted.
     */
    void countryDeleted(ActionEvent event) {
        selectedCountry = countryTable.getSelectionModel().getSelectedItem();
        if (selectedCountry == null) {
            // Warn user if no account was selected.
            noCountrySelectedAlert("Delete country");
        } else if(selectedCountry.equals("NEW ZEALAND")){
            saveMessage.setTextFill(Color.web("red"));
            saveMessage.setText("You cannot delete New Zealand!");
        } else {
            Dialog<Boolean> dialog = new Dialog<>();
            dialog.setTitle("Delete");
            dialog.setHeaderText("Deletion of Country");
            dialog.setContentText("Are you sure you want remove this country?");
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.NO, ButtonType.YES);
            final Button yesButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.YES);
            yesButton.addEventFilter(ActionEvent.ACTION, selectedEvent -> {
                deleteCountry();

            });
            dialog.showAndWait();

        }
    }

    public void deleteCountry() {
        DeleteTask deleteTask = new DeleteTask(ADDRESSES.SERVER.getAddress(), String.format(ADDRESSES.DELETE_COUNTRY.getAddress(), selectedCountry), session.getToken());
        deleteTask.setOnSucceeded(successEvent -> {
            PageNav.loaded();
            saveMessage.setTextFill(Color.web("green"));
            saveMessage.setText(selectedCountry + " successfully deleted.");
            getCountries();
            AccountManager.getStatusUpdates().add("Country " + selectedCountry + " deleted.");
        });
        deleteTask.setOnFailed( event -> {
            PageNav.loaded();
            saveMessage.setTextFill(Color.web("red"));
            saveMessage.setText(selectedCountry + " failed to delete.");

        });
        new Thread(deleteTask).start();
    }



    /**
     * (Re) populates the observable list with the list of countries
     */
    private void populateCountryTable() {
        observableCountries.removeAll(observableCountries);
        observableCountries.addAll(countries);
        observableCountries.sorted();
    }

    @FXML
    /**
     * Adds a new country to the list of countries, if possible. Duplicate countries cannot be added.
     */
    void countryEntered(ActionEvent event) {
        PostTask postTask = new PostTask(ADDRESSES.SERVER.getAddress(), String.format(ADDRESSES.POST_COUNTRY.getAddress(), newCountryField.getText()), session.getToken());
        postTask.setOnSucceeded(successEvent -> {
            PageNav.loaded();
            saveMessage.setTextFill(Color.web("green"));
            saveMessage.setText(newCountryField.getText() + " successfully added.");
            AccountManager.getStatusUpdates().add("Country " + newCountryField.getText() + " added.");
            newCountryField.clear();
            newCountryField.setPromptText("Enter Country Name");
            getCountries();
        });
        postTask.setOnFailed( e -> {
            PageNav.loaded();
            saveMessage.setTextFill(Color.web("red"));
            saveMessage.setText(newCountryField.getText() + " failed to add.");
        });
        new Thread(postTask).start();

    }


    /**
     * Generates an alert asking the user to select an country with a title specified by the parameter
     * title.
     *
     * @param title The title of the alert.
     */
    void noCountrySelectedAlert(String title) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText("Please select a country.");
        alert.showAndWait();
    }


}

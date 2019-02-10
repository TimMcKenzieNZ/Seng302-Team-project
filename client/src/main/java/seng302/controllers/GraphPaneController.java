package seng302.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.PieChart.Data;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.text.Text;
import seng302.App;
import seng302.model.PageNav;
import seng302.model.Session;
import seng302.model.enums.ADDRESSES;
import seng302.model.statistics.OrganCount;
import seng302.model.statistics.OrgansByRegionCount;
import seng302.services.GetTask;
import javafx.beans.value.ObservableValue;

import java.util.*;
import seng302.services.SyncGetTask;

public class GraphPaneController {

  private Session session = App.getCurrentSession();


    /**
     * ArrayList for storing CheckComboBox filtering elements
     */
    private ArrayList<ComboBox> comboBoxes = new ArrayList<>();

  public Integer zeroToTwelve = 0;
  public Integer twelveToTwenty = 0;
  public Integer twentyToThirty = 0;
  public Integer thirtyToForty = 0;
  public Integer fortyToFifty = 0;
  public Integer fiftyToSixty = 0;
  public Integer sixtyToSeventy = 0;
  public Integer seventyToEighty = 0;
  public Integer eightyToNinety = 0;
  public Integer ninetyPlus = 0;

    /**
     * ObservableList for storing age filtering options
     */
    private ObservableList<String> ages = FXCollections
            .observableArrayList("Age","0 to 12", "12 to 20", "20 to 30", "30 to 40", "40 to 50", "50 to 60", "60 to 70", "70 to 80", "80 to 90", "90+");


    /**
     * ObservableList for storing age filtering options
     */
    private ObservableList<String> genderList = FXCollections
            .observableArrayList("Gender", "Male", "Female", "Other", "Unknown/Unspecified");


    /**
     * ObservableList for storing age filtering options
     */
    private ObservableList<String> regions = FXCollections
            .observableArrayList("Region", "Northland", "Auckland", "Waikato", "Bay of Plenty", "Gisborne",
                    "Hawke's Bay", "Taranaki", "Manawatu-Wanganui", "Wellington", "Tasman", "Nelson", "Marlborough", "West Coast",
                    "Canterbury", "Otago", "Southland", "Chatham Islands");

    /**
     * ObservableList for storing age filtering options
     */
    private ObservableList<String> bloodTypes = FXCollections
            .observableArrayList("Blood Type","A+", "B+", "O+", "A-", "B-", "O-", "AB+", "AB-");

  private String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September"
      , "October", "November", "December"};
  private List<String> monthList = Arrays.asList(months);


  final static String austria = "Austria";
  final static String brazil = "Brazil";
  final static String france = "France";
  final static String italy = "Italy";
  final static String usa = "USA";

  private Map<String, Integer> samplePieData;
  private Map<String, Integer> sampleAreaSeries1Data;
  private Map<String, Integer> sampleAreaSeries2Data;
  private Map<String, Map<String, Number>> data;

  @FXML PieChart pieOne;
  @FXML PieChart pieTwo;

  @FXML AreaChart<String, Integer> area;
  @FXML CategoryAxis areaXAxis;
  @FXML NumberAxis areaYAxis;

  @FXML Text monthOrganList;

  @FXML BarChart<String, Number> bar;
  @FXML BarChart<String, Number> secondBar;
  @FXML CategoryAxis barXAxis;
  @FXML NumberAxis barYAxis;
  @FXML CategoryAxis secondBarXAxis;
  @FXML NumberAxis secondBarYAxis;

  @FXML StackedBarChart<String, Number> stackedBar;
  @FXML CategoryAxis stackedBarXAxis;
  @FXML NumberAxis stackedBarYAxis;

  @FXML
  ComboBox<String> bloodType;
  @FXML ComboBox<String> gender;
  @FXML ComboBox<String> age;
  @FXML ComboBox<String> region;

  @FXML
  Label filter;
  @FXML Button clear;

  @FXML
  Button barButton;

  @FXML
  public Button back;

  @FXML
  public TabPane graphsTab;
  @FXML
  public TabPane barTab;
  @FXML
  public TabPane stackTabPane;



  /**
   * Initializes the GUI elements for the admin GUI pane as well as the undo and redo functionality
   * for the editing text fields.
   */
  @FXML
  public void initialize() {
      back.getStyleClass().add("backButton");
      barButton.setVisible(true);
    pieOne.setVisible(false);
    bar.setVisible(true);
    secondBar.setVisible(true);
    stackedBar.setVisible(false);
    area.setVisible(false);
    monthOrganList.setVisible(false);
    monthOrganList.getStyleClass().add("organBox");

    bloodType.getItems().addAll(bloodTypes);
    gender.getItems().addAll(genderList);
    age.getItems().addAll(ages);
    region.getItems().addAll(regions);
    bloodType.getSelectionModel().selectFirst();
      gender.getSelectionModel().selectFirst();
      age.getSelectionModel().selectFirst();
      region.getSelectionModel().selectFirst();
    comboBoxes.add(gender);
    comboBoxes.add(region);
    comboBoxes.add(bloodType);
    comboBoxes.add(age);
      //Add an event listener to each comboBox
      comboBoxes.forEach(comboBox -> {
          comboBox.valueProperty().addListener(new ChangeListener<String>() {
              @Override
              public void changed(ObservableValue ov, String t, String t1) {
                  if (t1 != null) {
                      updateGraphFiltering();
                  }
              }
          });

      });

    samplePieData = new HashMap<>();
    samplePieData.put("Heart", 12);
    samplePieData.put("Lungs", 22);
    samplePieData.put("Cornea", 2);
    samplePieData.put("Liver", 42);
    samplePieData.put("Kidney", 8);

    sampleAreaSeries1Data = new LinkedHashMap<>();
    sampleAreaSeries1Data.put("January",2);
    sampleAreaSeries1Data.put("February",3);
    sampleAreaSeries1Data.put("March",4);
    sampleAreaSeries1Data.put("April",1);
    sampleAreaSeries1Data.put("May",12);
    sampleAreaSeries1Data.put("June",43);
    sampleAreaSeries1Data.put("July",10);
    sampleAreaSeries1Data.put("August",38);
    sampleAreaSeries1Data.put("September",23);
    sampleAreaSeries1Data.put("October",48);
    sampleAreaSeries1Data.put("November",2);
    sampleAreaSeries1Data.put("December",3);

    sampleAreaSeries2Data = new LinkedHashMap<>();
    sampleAreaSeries2Data.put("January",1);
    sampleAreaSeries2Data.put("February",6);
    sampleAreaSeries2Data.put("March",23);
    sampleAreaSeries2Data.put("April",4);
    sampleAreaSeries2Data.put("May",56);
    sampleAreaSeries2Data.put("June",17);
    sampleAreaSeries2Data.put("July",5);
    sampleAreaSeries2Data.put("August",41);
    sampleAreaSeries2Data.put("September",21);
    sampleAreaSeries2Data.put("October",0);
    sampleAreaSeries2Data.put("November",1);
    sampleAreaSeries2Data.put("December",7);

    //2003
    Map<String, Number> series1 = new LinkedHashMap<>();
    series1.put(austria, 25601.34);
    series1.put(brazil, 20148.82);
    series1.put(france, 10000);
    series1.put(italy, 35407.15);
    series1.put(usa, 12000);

    //2004
    Map<String, Number> series2 = new LinkedHashMap<>();
    series2.put(austria, 57401.85);
    series2.put(brazil, 41941.19);
    series2.put(france, 45263.37);
    series2.put(italy, 117320.16);
    series2.put(usa, 14845.27);

    //2005
    Map<String, Number> series3 = new LinkedHashMap<>();
    series3.put(austria, 45000.65);
    series3.put(brazil, 44835.76);
    series3.put(france, 18722.18);
    series3.put(italy, 17557.31);
    series3.put(usa, 92633.68);

    data = new LinkedHashMap<>();
    data.put("2003", series1);
    data.put("2004", series2);
    data.put("2005", series3);

    showPie(null);
  }

  private void initArea(String address) {
    resetArea();
    PageNav.loading();
    String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
    String queryParams = "?year=" + year + address;
    GetTask task = new GetTask(List.class, ADDRESSES.SERVER.getAddress(), ADDRESSES.GET_TOTAL_RECEIVER_ORGANS_YEAR.getAddress() + queryParams, session.getToken());
    task.setOnSucceeded(event -> {
      List<Integer> numContributions = (List<Integer>) task.getValue();
      Map<String, Integer> innerMap = new LinkedHashMap<>();
      for(int i = 0; i < months.length; i++) {
        innerMap.put(months[i], numContributions.get(i));
      }

      Map<String, Map<String, Integer>> organMaps = new LinkedHashMap<>();
      organMaps.put(year, innerMap);
      initAreaByMap(organMaps,"Receiver Registration Trends", address);
      PageNav.loaded();
    });
    task.setOnFailed(event -> {
      System.out.print("");
    });
    new Thread(task).start();
  }

  private void initPie(String address) {
      resetPie();
    pieOne.setTitle("Required Transplants by Organ Type");
    pieTwo.setTitle("Organs Pledged by Organ Type");

    GetTask task = new GetTask(OrganCount.class, ADDRESSES.SERVER.getAddress(), ADDRESSES.GET_RECEIVER_ORGANS.getAddress() + address, session.getToken());
    task.setOnSucceeded(event -> {
      OrganCount organCount = (OrganCount) task.getValue();
      populatePieChart(organCount, pieOne);

      PageNav.loaded();
    });
    task.setOnFailed(event -> {
      pieChartError( pieOne);

      PageNav.loaded();
    });
    new Thread(task).start();

    GetTask taskTwo = new GetTask(OrganCount.class, ADDRESSES.SERVER.getAddress(), ADDRESSES.GET_DONOR_ORGANS.getAddress() + address, session.getToken());
    taskTwo.setOnSucceeded(event -> {

      OrganCount organCount = (OrganCount) taskTwo.getValue();
      populatePieChart(organCount, pieTwo);

      PageNav.loaded();
    });
    taskTwo.setOnFailed(event -> {
      pieChartError(pieTwo);

      PageNav.loaded();
    });
    new Thread(taskTwo).start();
  }


    /**
     * Updates the content shown in the pie graph depending on the value of the chart filters.
     */
  private void updatePieGraph() {
      String address = "?";
      if (bloodType.getSelectionModel().getSelectedIndex() != 0) {
          address += "&bloodType=";
          address+= bloodType.getSelectionModel().getSelectedItem().toString();
      }
      if (age.getSelectionModel().getSelectedIndex() != 0) {
          address += "&age=";
          address+= age.getSelectionModel().getSelectedItem().toString().substring(0,2).replace(" ", "");
      }
      if (gender.getSelectionModel().getSelectedIndex() != 0) {
          address += "&gender=";
          address+= gender.getSelectionModel().getSelectedItem().toString().substring(0,1);
      }
      if (region.getSelectionModel().getSelectedIndex() != 0) {
          address += "&region=";
          address += region.getSelectionModel().getSelectedItem().toString();
      }
      if (address.length() > 1) {
          address = address.substring(0,1) + address.substring(2);
      } else {
          address = "";
      }

      initPie(address);


  }

  private void disableFilters() {
      bloodType.setDisable(true);
      gender.setDisable(true);
      age.setDisable(true);
      region.setDisable(true);
  }

  private void enableFilters() {
      bloodType.setDisable(false);
      gender.setDisable(false);
      age.setDisable(false);
      region.setDisable(false);
  }

    /**
     * Updates the content shown in the bar graph depending on the value of the chart filters.
     */
  private void updateBarGraph() {
      disableFilters();
      bar.setData(FXCollections.observableArrayList());
      String address = "?";
      if (bloodType.getSelectionModel().getSelectedIndex() != 0) {
          address += "&bloodType=";
          address+= bloodType.getSelectionModel().getSelectedItem().toString();
      }
      if (age.getSelectionModel().getSelectedIndex() != 0) {
          address += "&age=";
          address+= age.getSelectionModel().getSelectedItem().toString().substring(0,2).replace(" ", "");
      }
      if (gender.getSelectionModel().getSelectedIndex() != 0) {
          address += "&gender=";
          address+= gender.getSelectionModel().getSelectedItem().toString().substring(0,1);
      }
//      if (region.getSelectionModel().getSelectedIndex() != 0) {
//          address += "&region=";
//          address += region.getSelectionModel().getSelectedItem().toString();
//      }
      if (address.length() > 1) {
          address = address.substring(0,1) + address.substring(2);
      } else {
          address = "";
      }
      initBar(address);

  }


    /**
     * Updates the content shown in the stacked bar graph depending on the value of the chart filters.
     */
  private void updateStackedBarGraph() {
      String address = "";
      if (bloodType.getSelectionModel().getSelectedIndex() != 0) {
          address += "&bloodType=";
          address+= bloodType.getSelectionModel().getSelectedItem().toString();
      }
      if (age.getSelectionModel().getSelectedIndex() != 0) {
          address += "&age=";
          address+= age.getSelectionModel().getSelectedItem().toString().substring(0,2).replace(" ", "");
      }
      if (gender.getSelectionModel().getSelectedIndex() != 0) {
          address += "&gender=";
          address+= gender.getSelectionModel().getSelectedItem().toString().substring(0,1);
      }
//      if (region.getSelectionModel().getSelectedIndex() != 0) {
//          address += "&region=";
//          address += region.getSelectionModel().getSelectedItem().toString();
//      }
      if (address.length() > 1) {
          address = address.substring(0,1) + address.substring(2);
      } else {
          address = "";
      }

      getStackedBarDataFromServer(address);

  }


    /**
     * Updates the content shown in the area graph depending on the value of the chart filters.
     */
  private void updateAreaGraph() {
      String address = "";
      if (bloodType.getSelectionModel().getSelectedIndex() != 0) {
          address += "&bloodType=";
          address+= bloodType.getSelectionModel().getSelectedItem().toString();
      }
      if (age.getSelectionModel().getSelectedIndex() != 0) {
          address += "&age=";
          address+= age.getSelectionModel().getSelectedItem().toString().substring(0,2).replace(" ", "");
      }
      if (gender.getSelectionModel().getSelectedIndex() != 0) {
          address += "&gender=";
          address+= gender.getSelectionModel().getSelectedItem().toString().substring(0,1);
      }
      if (region.getSelectionModel().getSelectedIndex() != 0) {
          address += "&region=";
          address += region.getSelectionModel().getSelectedItem().toString();
      }
      if (address.length() < 1) {
          address = "";
      }
    initArea(address);
  }


    /**
     * Updates one of the GUI graphs based on changing filter settings.
     */
  private void updateGraphFiltering() {
      if (pieOne.isVisible()) {
          updatePieGraph();
      } else if (bar.isVisible()) {
          updateBarGraph();
      } else if (stackedBar.isVisible()) {
          updateStackedBarGraph();
      } else if (area.isVisible()) {
          updateAreaGraph();
      }

  }


  /**
   * Takes an Organ Count object and parses its contents into a linkedHashMap.
   * @param organsCount An OrganCount object storing integer counts for organs
   * @return A LinkedHashMap containing counts for organs.
   */
  public Map<String, Number> parseOrgansToMap(OrganCount organsCount) {
      LinkedHashMap<String, Number> organsMap = new LinkedHashMap();
      organsMap.put("Liver", organsCount.getLiverCount());
      organsMap.put("Kidneys", organsCount.getKidneyCount());
      organsMap.put("Pancreas", organsCount.getPancreasCount());
      organsMap.put("Heart", organsCount.getHeartCount());
      organsMap.put("Lungs", organsCount.getLungCount());
      organsMap.put("Intestines", organsCount.getIntestineCount());
      organsMap.put("Corneas", organsCount.getCorneasCount());
      organsMap.put("Middle Ears", organsCount.getMiddleEarsCount());
      organsMap.put("Skin", organsCount.getSkinCount());
      organsMap.put("Bone", organsCount.getBoneCount());
      organsMap.put("Bone Marrow", organsCount.getBoneMarrowCount());
      organsMap.put("Connective Tissue", organsCount.getConnectiveTissueCount());
      return organsMap;
    }

    private ObservableList<String> organList = FXCollections
            .observableArrayList("Liver","Kidneys", "Pancreas", "Heart", "Lungs", "Intestines", "Corneas",
                    "Middle Ears", "Skin", "Bone", "Bone Marrow", "Connective Tissue");

  /**
   * Takes in a OrgansByRegionCount object and parses its data into a LinkedHashMap.
   * @param organsByRegionStore A OrgansByRegionCount object that contains organ counts per region
   * @return A linkedHashMap containing a LinkedHashMap for each region that contains its organ counts.
   */
  public Map<String, Map<String, Number>> parseOrgansByRegionToMap( OrgansByRegionCount organsByRegionStore) {
    LinkedHashMap<String, Map<String, Number>> regions = new LinkedHashMap<>();
    regions.put("Northland", parseOrgansToMap(organsByRegionStore.getNorthland()));
    regions.put("Auckland", parseOrgansToMap(organsByRegionStore.getAuckland()));
    regions.put("Waikato", parseOrgansToMap(organsByRegionStore.getWaikato()));
    regions.put("Bay of Plenty", parseOrgansToMap(organsByRegionStore.getBayOfPlenty()));
    regions.put("Gisbourne", parseOrgansToMap(organsByRegionStore.getGisbourne()));
    regions.put("Hawke's Bay", parseOrgansToMap(organsByRegionStore.getHawkesBay()));
    regions.put("Taranaki", parseOrgansToMap(organsByRegionStore.getTaranaki()));
    regions.put("Manawatu-Wanganui", parseOrgansToMap(organsByRegionStore.getManawatuWanganui()));
    regions.put("Wellington", parseOrgansToMap(organsByRegionStore.getWellington()));
    regions.put("Tasman", parseOrgansToMap(organsByRegionStore.getTasman()));
    regions.put("Nelson", parseOrgansToMap(organsByRegionStore.getNelson()));
    regions.put("Malborough", parseOrgansToMap(organsByRegionStore.getMalborough()));
    regions.put("West Coast", parseOrgansToMap(organsByRegionStore.getWestCoast()));
    regions.put("Canterbury", parseOrgansToMap(organsByRegionStore.getCanterbury()));
    regions.put("Otago", parseOrgansToMap(organsByRegionStore.getOtago()));
    regions.put("Southland", parseOrgansToMap(organsByRegionStore.getSouthland()));
    regions.put("Chatham Islands", parseOrgansToMap(organsByRegionStore.getChathamIslands()));
    return regions;

  }





//
//  private Map<String, Map<String, Number>> revertOrganByRegionStats(OrgansByRegionCount organsByRegionStore) {
//      LinkedHashMap<String , Map<String, Number>> organs = new LinkedHashMap();
//      for (String organName: organList) {
//          LinkedHashMap<String, Number> org = new LinkedHashMap();
//          organs.put(organName, org);
//      }
//      for(Map.Entry<String, Map<String, Number>> entry : organs.entrySet()) {
//          String region = entry.getKey();
//          Map<String, Number> regionOrgans = entry.getValue();
//          for (Map.Entry<String, Number> organEntry: regionOrgans.entrySet()) {
//              String organ = organEntry.getKey();
//              Number count = organEntry.getValue();
//
//          }
//
//      }
//  }


  /**
   * A default display whfor the stacked bar graph when there is no data to display.
   * @param chart a stacked bar chart.
   */
  private void stackedBarChartError(StackedBarChart chart) {
    ObservableList<XYChart.Series> chartData = FXCollections.observableArrayList();
    XYChart.Series<String, Number> series = new XYChart.Series<>();
    series.getData().add(new XYChart.Data<>("Error", 1));
    chartData.add(series);
    chart.setData(chartData);

  }


  /**
   * Gets the organs by region data from the server and populates the stackbar graph with the info.
   */
  public void getStackedBarDataFromServer(String address) {
      PageNav.loading();
    GetTask task = new GetTask(OrgansByRegionCount.class, ADDRESSES.SERVER.getAddress(), ADDRESSES.GET_ALL_RECEIVED_ORGANS.getAddress() + address, session.getToken());
    task.setOnSucceeded(event -> {
      OrgansByRegionCount organsCount = (OrgansByRegionCount) task.getValue();
      Map<String, Map<String,Number>> organsbyRegion = parseOrgansByRegionToMap(organsCount);
      initStackedBar(organsbyRegion, "Organs Received per Region", "", "Number of Organs");
      PageNav.loaded();
    });
    task.setOnFailed(event -> {
      stackedBarChartError(stackedBar);
      PageNav.loaded();
    });
    new Thread(task).start();

//
//      GetTask task2 = new GetTask(OrgansByRegionCount.class, ADDRESSES.SERVER.getAddress(), ADDRESSES.GET_ALL_DONATED_ORGANS.getAddress() + address, session.getToken());
//      task.setOnSucceeded(event -> {
//          OrgansByRegionCount organsCount = (OrgansByRegionCount) task.getValue();
//          Map<String, Map<String,Number>> organsbyRegion = parseOrgansByRegionToMap(organsCount);
//          initStackedBar(stackedBar2, organsbyRegion, "ORGANS DONATED PER REGION", "Organs Donated", "Regions");
//          PageNav.loaded();
//      });
//      task.setOnFailed(event -> {
//          stackedBarChartError(stackedBar2);
//
//          PageNav.loaded();
//      });
//      new Thread(task).start();
  }


  /**
   * Displays one datapoint in a PieChart with label Error.
   *
   * @param pieChart PieChart to display the data in.
   */
  private void pieChartError(PieChart pieChart){
    ObservableList<Data> pieChartData = FXCollections.observableArrayList();
    pieChartData.add(new PieChart.Data("Error", 1));
    pieChart.setData(pieChartData);
  }


  /**
   * Populates a PieChart with organ counts.
   *
   * @param organCount Organ counts to display
   * @param pieChart Piechart to display them in.
   */
  private void populatePieChart(OrganCount organCount, PieChart pieChart){

    Map<String, Integer> data = new HashMap<>();

    data.put("Liver (" + organCount.getLiverCount() + ")", organCount.getLiverCount());
    data.put("Kidney (" + organCount.getKidneyCount() + ")", organCount.getKidneyCount());
    data.put("Pancreas (" + organCount.getPancreasCount() + ")", organCount.getPancreasCount());
    data.put("Heart (" + organCount.getHeartCount() + ")", organCount.getHeartCount());
    data.put("Lung (" + organCount.getLungCount() + ")", organCount.getLungCount());
    data.put("Intestine (" + organCount.getIntestineCount() + ")", organCount.getIntestineCount());
    data.put("Cornea (" + organCount.getCorneasCount() + ")", organCount.getCorneasCount());
    data.put("Middle Ear (" + organCount.getMiddleEarsCount() + ")", organCount.getMiddleEarsCount());
    data.put("Skin (" + organCount.getSkinCount() + ")", organCount.getSkinCount());
    data.put("Bone (" + organCount.getBoneCount() + ")", organCount.getBoneCount());
    data.put("Bone Marrow (" + organCount.getBoneMarrowCount() + ")", organCount.getBoneMarrowCount());
    data.put("Connective Tissue (" + organCount.getConnectiveTissueCount() + ")", organCount.getConnectiveTissueCount());


    ObservableList<Data> pieChartData = FXCollections.observableArrayList();

    for(Map.Entry<String, Integer> entry : data.entrySet()) {
      String key = entry.getKey();
      Integer value = entry.getValue();
      pieChartData.add(new PieChart.Data(key, value));
    }
    pieChart.setData(pieChartData);
    //pieChart.setLabelsVisible(false);
  }



  /**
   * Resets and then populates the GUI pie graph with the given inputData and displays the pie graph in the GUI.
   * @param inputData A Map<String, Integer> of statistical data to display. The keys are the pie slices and the values
   *                  specifiy the size of the slice.
   * @param title A string of the title of the pie graph to display
   */
  private void initPie(Map<String, Integer> inputData, String title, PieChart pie) {
    resetPie();
    ObservableList<Data> pieChartData = FXCollections.observableArrayList();

    for (Map.Entry<String, Integer> entry : inputData.entrySet()) {
      String key = entry.getKey();
      Integer value = entry.getValue();
      pieChartData.add(new PieChart.Data(key, value));
    }
    pie.setData(pieChartData);
    pie.setTitle(title);
  }

  private void initAreaByMap(Map<String, Map<String, Integer>> inputData, String title, String address) {
    resetArea();
    area.setTitle(title);
    Set<String> categories = new LinkedHashSet<>();
    int max = 0;
    int MAX_OFFSET = 2; // How much higher the table should be from the max value
    area.setTitle(title);
    areaYAxis.setAutoRanging(false);
    areaYAxis.setTickUnit(5);
    areaYAxis.setLowerBound(0);
    areaYAxis.setForceZeroInRange(true);

    int i = 0;
    for(Map.Entry<String, Map<String, Integer>> seriesData: inputData.entrySet()) {
      List<OrganCount> countList = new ArrayList<>();
      XYChart.Series series =  new XYChart.Series();
      series.setName(seriesData.getKey());

      for(Map.Entry<String, Integer> seriesDataValues : seriesData.getValue().entrySet()) {
        String category = seriesDataValues.getKey();
        categories.add(category);
//        Map organCountMap = seriesDataValues.getValue();
//        ObjectMapper mapper = new ObjectMapper();
//        OrganCount organCount = mapper.convertValue(organCountMap, OrganCount.class);
        int value = seriesDataValues.getValue();
        max = value > max ? value:max;
        XYChart.Data<String, Integer> data = new XYChart.Data<>(category,value);
        series.getData().add(data);
        // countList.add(organCount);
      }

      area.getData().add(series);
      setTextOrganInformation(i++, address);
    }

    areaYAxis.setUpperBound(max + MAX_OFFSET);
    areaXAxis.setCategories(FXCollections.observableArrayList(categories));
  }

  private void setTextOrganInformation(int seriesNo, String address) {
    // Iterate through the series and get the nodes on the data
    Series<String, Integer> series = area.getData().get(seriesNo);
    for(XYChart.Data<String, Integer> data: series.getData()) {
      String monthName = data.getXValue();
      int monthIndex = monthList.indexOf(monthName) + 1;
      String year = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
      String monthString = String.valueOf(monthIndex);
      if(monthString.length() == 1) {
        monthString = "0" + monthString;
      }
      String lowerBound = year + "-" + monthString + "-01";
      String upperBound;
      if(monthIndex == 12) {
          int nextYear = Integer.parseInt(year) + 1;
          upperBound = String.valueOf(nextYear) + "-01-01";
      }
      else {
        String newMonthString = String.valueOf(monthIndex + 1);
        if(newMonthString.length() == 1) {
          newMonthString = "0" + newMonthString;
        }
        upperBound = year +  "-" + String.valueOf(newMonthString) + "-01";
      }
      String queryString = "?upperBound=" + upperBound + "&lowerBound=" + lowerBound;
      String url = ADDRESSES.GET_RECEIVER_ORGANS_DATE_RANGE.getAddress() + queryString + address;
      SyncGetTask getOrganTask = new SyncGetTask(OrganCount.class, ADDRESSES.SERVER.getAddress(), url, session.getToken());

      Node dataNode = data.getNode();
      data.getNode().setOnMouseEntered(event -> {
        dataNode.getStyleClass().add("toolOnHover");
        String displayString = data.getXValue();
        if (data.getYValue().equals(0)) {
          displayString += "\n\nNo receivers registered in this month";
        } else {
          OrganCount thisCount = (OrganCount) getOrganTask.makeRequest();
          displayString += "\n\n" + thisCount.toString();
        }
        monthOrganList.setText(displayString);
        monthOrganList.setVisible(true);
      });
      data.getNode().setOnMouseExited(event -> {
        dataNode.getStyleClass().remove("toolOnHover");
      });
    }
  }


  private void initBar(String address) {

    resetBar();

    bar.setTitle("Number of Required Organs per Region");
    secondBar.setTitle("Number of Pledged Organs per Region");
    bar.setAnimated(false);
    bar.setLegendVisible(false);
    barXAxis.setLabel("Region");
    barYAxis.setLabel("Number of Organs");
    Map<String, String> regionsCount = new HashMap<>();
    regionsCount.put("Northland", "northlandCount");
    regionsCount.put("Auckland", "aucklandCount");
    regionsCount.put("Waikato", "waikatoCount");
    regionsCount.put("Bay of Plenty", "bayOfPlentyCount");
    regionsCount.put("Gisborne", "gisborneCount");
    regionsCount.put("Hawkes Bay", "hawkesBayCount");
    regionsCount.put("Taranaki", "taranakiCount");
    regionsCount.put("Manawatu-Wanganui", "manawatuWanganuiCount");
    regionsCount.put("Wellington", "wellingtonCount");
    regionsCount.put("Tasmin", "tasminCount");
    regionsCount.put("Nelson", "nelsonCount");
    regionsCount.put("Marlborough", "marlboroughCount");
    regionsCount.put("Canterbury", "canterburyCount");
    regionsCount.put("West Coast", "westCoastCount");
    regionsCount.put("Otago", "otagoCount");
    regionsCount.put("Southland", "southlandCount");
    regionsCount.put("Chatham Islands", "chathamIslandsCount");
    PageNav.loading();
      GetTask task = new GetTask(Map.class, ADDRESSES.SERVER.getAddress(), ADDRESSES.GET_RECEIVER_ORGANS_REGION.getAddress() + address, session.getToken());
      task.setOnSucceeded(event -> {
          Map<String, Integer> organMap = (LinkedHashMap<String, Integer>) task.getValue();
          XYChart.Series series1 = new XYChart.Series();
          series1.setName("Required Organs");
          for(Map.Entry<String, String> seriesData: regionsCount.entrySet()) {
            series1.getData().add(new XYChart.Data<>(seriesData.getKey(), organMap.get(seriesData.getValue())));
          }
          if(bar.getData().size() == 0)
            bar.getData().addAll(series1);

          initSecondBar(address);

      });
      task.setOnFailed(event -> {
          XYChart.Series series1 = new XYChart.Series();
          series1.setName("Error");
          series1.getData().add(new XYChart.Data("Error", 1));
            enableFilters();
          bar.getData().addAll(series1);
      });
      new Thread(task).start();
//      barXAxis.setAutoRanging(false);
//      barXAxis.setCategories(FXCollections.observableArrayList(regions));


  }

  private void initSecondBar(String address) {
      secondBar.setTitle("Number of Pledged Organs per Region");
      secondBar.setAnimated(false);
      secondBar.setLegendVisible(false);
      secondBarXAxis.setLabel("Region");
      secondBarYAxis.setLabel("Number of Organs");
      Map<String, String> regionsCount = new HashMap<>();
      regionsCount.put("Northland", "northlandCount");
      regionsCount.put("Auckland", "aucklandCount");
      regionsCount.put("Waikato", "waikatoCount");
      regionsCount.put("Bay of Plenty", "bayOfPlentyCount");
      regionsCount.put("Gisborne", "gisborneCount");
      regionsCount.put("Hawkes Bay", "hawkesBayCount");
      regionsCount.put("Taranaki", "taranakiCount");
      regionsCount.put("Manawatu-Wanganui", "manawatuWanganuiCount");
      regionsCount.put("Wellington", "wellingtonCount");
      regionsCount.put("Tasmin", "tasminCount");
      regionsCount.put("Nelson", "nelsonCount");
      regionsCount.put("Marlborough", "marlboroughCount");
      regionsCount.put("Canterbury", "canterburyCount");
      regionsCount.put("West Coast", "westCoastCount");
      regionsCount.put("Otago", "otagoCount");
      regionsCount.put("Southland", "southlandCount");
      regionsCount.put("Chatham Islands", "chathamIslandsCount");
      GetTask nextTask = new GetTask(Map.class, ADDRESSES.SERVER.getAddress(), ADDRESSES.GET_DONOR_ORGANS_REGION.getAddress() + address, session.getToken());
      nextTask.setOnSucceeded(event -> {
          Map<String, Integer> organMap = (LinkedHashMap<String, Integer>) nextTask.getValue();
          XYChart.Series series1 = new XYChart.Series();
          series1.setName("Pledged Organs");
          for(Map.Entry<String, String> seriesData: regionsCount.entrySet()) {
              series1.getData().add(new XYChart.Data<>(seriesData.getKey(), organMap.get(seriesData.getValue())));
          }
          if(secondBar.getData().size() == 0)
              secondBar.getData().addAll(series1);
          enableFilters();
          PageNav.loaded();
      });
      nextTask.setOnFailed(event -> {
          XYChart.Series series1 = new XYChart.Series();
          series1.setName("Error");
          series1.getData().add(new XYChart.Data("Error", 1));
          enableFilters();
          bar.getData().addAll(series1);
      });
      new Thread(nextTask).start();
  }


  private void initStackedBar() {
      resetStackedBar();


       XYChart.Series<String, Number> series1 = new XYChart.Series<>();
       XYChart.Series<String, Number> series2 = new XYChart.Series<>();
       XYChart.Series<String, Number> series3 = new XYChart.Series<>();

      stackedBar.setTitle("Country Summary");
      stackedBarXAxis.setLabel("Country");
      stackedBarXAxis.setCategories(FXCollections.<String>observableArrayList(
              Arrays.asList(austria, brazil, france, italy, usa)));
      stackedBarYAxis.setLabel("Value");
      series1.setName("2003");
      series1.getData().add(new XYChart.Data<>(austria, 25601.34));
      series1.getData().add(new XYChart.Data<>(brazil, 20148.82));
      series1.getData().add(new XYChart.Data<>(france, 10000));
      series1.getData().add(new XYChart.Data<>(italy, 35407.15));
      series1.getData().add(new XYChart.Data<>(usa, 12000));
      series2.setName("2004");
      series2.getData().add(new XYChart.Data<>(austria, 57401.85));
      series2.getData().add(new XYChart.Data<>(brazil, 41941.19));
      series2.getData().add(new XYChart.Data<>(france, 45263.37));
      series2.getData().add(new XYChart.Data<>(italy, 117320.16));
      series2.getData().add(new XYChart.Data<>(usa, 14845.27));
      series3.setName("2005");
      series3.getData().add(new XYChart.Data<>(austria, 45000.65));
      series3.getData().add(new XYChart.Data<>(brazil, 44835.76));
      series3.getData().add(new XYChart.Data<>(france, 18722.18));
      series3.getData().add(new XYChart.Data<>(italy, 17557.31));
      series3.getData().add(new XYChart.Data<>(usa, 92633.68));
      stackedBar.getData().addAll(series1, series2, series3);
  }


  /**
   * Resets and then populates the GUI stacked bar graph with the given inputData.
   * @param inputData A Map<String, Map<String, Number> where the keys are series names and the values are maps containing the data <String, Number> for that series.
   * @param title A string of the title of the stacked bargraph to display.
   * @param titleX A string of the x-axis legend.
   * @param titleY A string of the y-axis legend.
   */
  private void initStackedBar(Map<String, Map<String, Number>> inputData, String title, String titleX, String titleY) {
    resetStackedBar();
    stackedBar.setTitle(title);
    stackedBarXAxis.setLabel(titleX);
    stackedBarYAxis.setLabel(titleY);
    ArrayList<XYChart.Series<String, Number>> seriesList = new ArrayList();
    LinkedHashSet<String> valuesList = new LinkedHashSet<>();
    for(Map.Entry<String, Map<String, Number>> entry : inputData.entrySet()) {
      String seriesName = entry.getKey();
      Map<String, Number> seriesData = entry.getValue();
      XYChart.Series series = new XYChart.Series();
      series.setName(seriesName);
      for (Map.Entry<String, Number> dataEntry : seriesData.entrySet()) {
        String dataName = dataEntry.getKey();
        Number data = dataEntry.getValue();
        series.getData().add(new XYChart.Data(dataName, data));
        valuesList.add(dataName);
      }
      seriesList.add(series);
    }
    stackedBarXAxis.setCategories(FXCollections.<String>observableArrayList(
            valuesList));
    stackedBar.getData().addAll(seriesList);
  }

  @FXML
  private void showArea(ActionEvent event) {
      graphsTab.setVisible(false);
    barTab.setVisible(false);
      stackTabPane.setVisible(false);
    Map<String, Map<String, Integer>> inputData = new LinkedHashMap<>();
    inputData.put("2017",sampleAreaSeries1Data);
    inputData.put("2018",sampleAreaSeries2Data);
    // initAreaByMap(inputData, "Receiver registration");
    initArea("");

    pieOne.setVisible(false);
    pieTwo.setVisible(false);
    bar.setVisible(false);
    secondBar.setVisible(false);
    stackedBar.setVisible(false);
    area.setVisible(true);

      region.setVisible(true);
      age.setVisible(true);
      gender.setVisible(true);
      bloodType.setVisible(true);
      clear.setVisible(true);
      filter.setVisible(true);
  }

  @FXML
  private void showPie(ActionEvent event) {
      clearFilters();
    graphsTab.setVisible(true);
    barTab.setVisible(false);
    stackTabPane.setVisible(false);
    PageNav.loading();
    pieOne.setVisible(true);
    pieTwo.setVisible(true);
    bar.setVisible(false);
    secondBar.setVisible(false);
    stackedBar.setVisible(false);
    area.setVisible(false);
    monthOrganList.setVisible(false);
    // initPie(samplePieData, "Organs for everybody!");
    region.setVisible(true);
    initPie("");
    //initPie(sampleData, "Organs for everybody!", pieOne);
      age.setVisible(true);
      gender.setVisible(true);
      bloodType.setVisible(true);
      clear.setVisible(true);
      filter.setVisible(true);

  }

  @FXML
  private void showBar(ActionEvent event) {
      clearFilters();
    updateBarGraph();
    barTab.setVisible(true);
    graphsTab.setVisible(false);
    stackTabPane.setVisible(false);
//    initBar(data, "Pledged Organs by Region", "Regions", "Amount of Organs");

    pieOne.setVisible(false);
    pieTwo.setVisible(false);
    bar.setVisible(true);
    secondBar.setVisible(true);
    stackedBar.setVisible(false);
    area.setVisible(false);
    monthOrganList.setVisible(false);
    region.setVisible(false);
      age.setVisible(true);
      gender.setVisible(true);
      bloodType.setVisible(true);
      clear.setVisible(true);
      filter.setVisible(true);
  }

  @FXML
  private void showStack(ActionEvent event) {
      clearFilters();
    getStackedBarDataFromServer("");
      graphsTab.setVisible(false);
    barTab.setVisible(false);
    stackTabPane.setVisible(true);
    //initStackedBar(data, "Country Summary", "Summary", "Value");
    //initStackedBar(data, "Country Summary", "Summary", "Value");

    pieOne.setVisible(false);
    pieTwo.setVisible(false);
    bar.setVisible(false);
    secondBar.setVisible(false);
    stackedBar.setVisible(true);
    area.setVisible(false);
    monthOrganList.setVisible(false);
    region.setVisible(false);
    age.setVisible(false);
    gender.setVisible(false);
    bloodType.setVisible(false);
      clear.setVisible(false);
      filter.setVisible(false);
  }

  @FXML
  private void goBack(){
    if(App.getCurrentSession().getUserType().equals("clinician")) {
      PageNav.loadNewPage(PageNav.MAINMENU);
    } else {
      PageNav.loadNewPage(PageNav.ADMINMENU);

    }
  }

  private void resetPie() {
    pieOne.setData(FXCollections.observableArrayList());
    pieTwo.setData(FXCollections.observableArrayList());
  }

  private void resetBar() {
    bar.setData(FXCollections.observableArrayList());
    secondBar.setData(FXCollections.observableArrayList());
  }

  private void resetStackedBar() {
    stackedBar.setData(FXCollections.observableArrayList());
  }

  private void resetArea() {
    area.setData(FXCollections.observableArrayList());
    monthOrganList.setText("");
  }

  @FXML
  private void clearFilters() {

    gender.getSelectionModel().selectFirst();
      bloodType.getSelectionModel().selectFirst();
      age.getSelectionModel().selectFirst();
      region.getSelectionModel().selectFirst();
  }

}

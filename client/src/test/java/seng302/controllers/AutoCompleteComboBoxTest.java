package seng302.controllers;

import static java.util.concurrent.TimeUnit.SECONDS;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeoutException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.junit.After;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;
import seng302.model.MedicationAutoCompleteResult;
import seng302.model.MedicationService;


public class AutoCompleteComboBoxTest extends ApplicationTest {

  private ComboBox comboBox;
  private ObservableList<String> list;
  MedicationAutoCompleteResult medicationAutoCompleteResult;
  String invalidQuery = "\\!??>";


  /**
   * These settings are so test can be run in CI-runner, if you want to watch the robot perform the
   * tests on your local machine, comment this function out.
   */
  @BeforeClass
  public static void headless() {
    GitlabGUITestSetup.headless();
  }


  /**
   * Start up an anchor pane with an editable combobox and add an AutoCompleteComboBoxListener to
   * listen for events of that combobox.
   *
   * @param stage the stage
   * @throws Exception The stage does not load properly
   */
  @Override
  public void start(Stage stage) throws Exception {
    comboBox = new ComboBox();
    comboBox.setId("AutoCompleteComboBox");
    comboBox.setEditable(true);
    AnchorPane root = new AnchorPane();

    root.getChildren().add(comboBox);
    list = FXCollections.observableArrayList();
    comboBox.setItems(list);
    new AutoCompleteComboBoxListener(comboBox);

    Scene scene = new Scene(root, 300, 250);
    stage.setScene(scene);
    stage.show();
    stage.toFront();

  }


  /**
   * Check for internet access and then check api is available. This is because by default git-lab
   * runner will not have internet access and the external api being down is outside our control. If
   * either of these is true we want to ignore these tests.
   */
  @Before
  public void checkInternetEnabledAndResourceAvailable() {
    Assume.assumeTrue(this.isConnected());
  }

  public Boolean isConnected() {
    try {
      MedicationAutoCompleteResult medicationAutoCompleteResult = MedicationService
          .drugAutoComplete(invalidQuery);
      Assert.assertTrue(medicationAutoCompleteResult.getQuery().equals(invalidQuery));
      Assert.assertTrue(medicationAutoCompleteResult.getSuggestions().isEmpty());
      return true;
    } catch (com.fasterxml.jackson.core.JsonParseException jsonParseException) {
      System.out.println("No internet access");
      return false;
    } catch (IOException e) {
      System.out.println("Caught error while checking connection for autocomplete tests");
      return false;
    }
  }


  /**
   * Basic tests to check that autocomplete results are displayed for a typical query.
   */
  @Ignore
  @Test
  public void verifyThatAutoCompleteReturnsResults() {
    try {
      FxRobot robot = new FxRobot();
      List<String> queryResults = Arrays
          .asList("Reserpine", "Resectisol", "Resectisol in plastic container", "Restoril",
              "Rescriptor", "Restasis", "Rescula", "Reserpine and hydrochlorothiazide",
              "Reserpine, hydralazine hydrochloride and hydrochlorothiazide",
              "Reserpine, hydrochlorothiazide, and hydralazine hydrochloride",
              "Reserpine and hydrochlorothiazide-50",
              "Reserpine and hydroflumethiazide", "Resporal");

      robot.clickOn(comboBox).type(KeyCode.R);
      WaitForAsyncUtils.waitForFxEvents();
      sleep(1, SECONDS);
      robot.clickOn(comboBox).type(KeyCode.E).type(KeyCode.S);
      WaitForAsyncUtils.waitForFxEvents();
      list = comboBox.getItems();
      for (String result : queryResults) {
        assertThat(list, hasItem(result));
      }
      robot.clickOn(comboBox).type(KeyCode.BACK_SPACE).type(KeyCode.BACK_SPACE)
          .type(KeyCode.BACK_SPACE);
      WaitForAsyncUtils.waitForFxEvents();
      list = comboBox.getItems();
      assertTrue("Test that deleting query string removes suggestions from combobox observablelist",
          list.isEmpty());
    } catch (Exception e) {
      System.out.println("Error caught");
    }

  }


  /**
   * Basic tests to check that when results are displayed for a query the arrow keys can be used to
   * select one of the suggestions.
   */
  @Test
  public void verifyThatCanSelectSuggestion() {
//        Assume.assumeTrue("Check if internet have internet access by pinging google",checkConnection.CheckInternet());
//        Assume.assumeTrue("Check if api required for tests is available", checkConnection.CheckResource());
    FxRobot robot = new FxRobot();
    String selectedSuggestion = "Restoril";

    robot.clickOn(comboBox).type(KeyCode.R);
    WaitForAsyncUtils.waitForFxEvents();
    sleep(2, SECONDS);
    robot.clickOn(comboBox).type(KeyCode.E).type(KeyCode.S);
    WaitForAsyncUtils.waitForFxEvents();
    robot.type(KeyCode.DOWN).type(KeyCode.DOWN).type(KeyCode.DOWN).type(KeyCode.DOWN)
        .type(KeyCode.ENTER);
    WaitForAsyncUtils.waitForFxEvents();
    assertTrue(comboBox.getSelectionModel().getSelectedItem().equals(selectedSuggestion));


  }


  /**
   * Tear down the window after test
   *
   * @throws TimeoutException When the tests runs for too long
   */
  @After
  public void tearDown() throws TimeoutException {

    FxToolkit.hideStage();
    release(new KeyCode[]{});
    release(new MouseButton[]{});
  }

}

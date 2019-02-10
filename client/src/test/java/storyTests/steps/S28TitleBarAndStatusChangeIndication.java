package storyTests.steps;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.testfx.api.FxToolkit.registerPrimaryStage;
import static org.testfx.api.FxToolkit.setupApplication;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.testfx.framework.junit.ApplicationTest;
import seng302.App;


/**
 * This class contains a set of Cucumber steps for testing story 28 (title bar and status change
 * indication).
 */
public class S28TitleBarAndStatusChangeIndication extends ApplicationTest {


  /**
   * Runs the application.
   *
   * @throws Throwable If an exception occurs opening the GUI.
   */
  @Given("^the user interface is open$")
  public void theUserInterfaceIsOpen() throws Throwable {

    registerPrimaryStage();
    setupApplication(App.class);

  }


  /**
   * This method currently does nothing. It should tests to see if each page has an appropriate
   * title in accordance with Story 28's ACs..
   *
   * @throws Throwable If an exception occurs while checking each title.
   */
  @Then("^each page should have an appropriate title$")
  public void eachPageShouldHaveAnAppropriateTitle() throws Throwable {
    //Just look at the fist page
    Stage currentStage = App.getWindow();
    assertEquals("SapioCulture", currentStage.getTitle());

  }


  /**
   * Performs an action on the donor account ABC1234 and clicks the done button. This is performed
   * on a checkbox, so the change will always occur.
   *
   * @throws Throwable If an exception occurs modifying ABC1234.
   */
  @When("^I perform an action$")
  public void iPerformAnAction() throws Throwable {

    clickOn("#usernameTextField").write("ABC1234");
    clickOn("#loginButton");
    clickOn("#Edit");
    clickOn("#editLivedInUKFrance");
    clickOn("#Done");

  }


  /**
   * Checks the status bar to see if message appropriate to the action performed in
   * 'iPerformAnAction' appears in the status bar.
   *
   * @throws Throwable If an exception occurs accessing the status bar.
   */
  @Then("^the result of that action is displayed in the status bar$")
  public void theResultOfThatActionIsDisplayedInTheStatusBar() throws Throwable {

    Label actionLabel = lookup("#actionLabel").query();
    assertEquals("Sweeny Todd (NHI: ABC1234) modified.", actionLabel.getText());

  }


  /**
   * Checks that an asterisk is present in the title bar.
   *
   * @throws Throwable If an exception occurs while attempting to access the title bar.
   */
  @Then("^an asterisk should appear in the title bar$")
  public void anAsteriskShouldAppearInTheTitleBar() throws Throwable {

    String title = App.getWindow().getTitle();
    assertEquals('*', title.charAt(title.length() - 1));

  }


  /**
   * Saves the application and confirms that the unsaved indicator (an asterisk) has been removed
   * from title bar.
   *
   * @throws Throwable If an exception occurs during the step.
   */
  @Then("^the asterisk should disappear when I save$")
  public void theAsteriskShouldDisappearWhenISave() throws Throwable {

    clickOn("#file").clickOn("#save").clickOn("#saveConfirm");
    String title = App.getWindow().getTitle();
    assertNotEquals('*', title.charAt(title.length() - 1));

  }


}

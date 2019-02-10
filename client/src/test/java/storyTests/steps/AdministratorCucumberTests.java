package storyTests.steps;

import static org.junit.Assert.assertEquals;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.junit.BeforeClass;
import org.testfx.framework.junit.ApplicationTest;
import seng302.App;
import seng302.model.AccountManager;
import seng302.model.GitlabGUITestSetup;
import seng302.model.person.Administrator;

public class AdministratorCucumberTests extends ApplicationTest {

  private App mainGUI;

  public static AccountManager accountManager = App.getDatabase();

  @Override
  /**
   * Sets the GUI stage
   */
  public void start(Stage stage) throws Exception {
    mainGUI = new App();
    mainGUI.start(stage);
    accountManager.addDefaultAdminIfNoneExists();
  }

  @BeforeClass
  public static void headless() {
    GitlabGUITestSetup.headless();
  }


  @Given("^The user interface is open and I am on the login page\\.$")
  public void theUserInterfaceIsOpenAndIAmOnTheLoginPage() throws TimeoutException {

//        registerPrimaryStage();
//        setupApplication(App.class);
  }

  @When("^I log in as the default administrator with the given details: username \"([^\"]*)\", password \"([^\"]*)\"\\.$")
  public void iLogInAsTheDefaultAdministratorWithTheGivenDetailsUsernamePassword(String username,
      String password) {
    clickOn("#adminButton");
    clickOn("#usernameTextField").write(username);
    clickOn("#passwordField").write(password);
    clickOn("#loginButton");
  }

  @Then("^I should be in the Administrator window and can see the buttons: buttonOne \"([^\"]*)\", buttonTwo \"([^\"]*)\"\\.$")
  public void iShouldBeInTheAdministratorWindowAndCanSeeTheButtonsButtonButton(String commandString,
      String adminString) {
    Button command = lookup("#commandLineButton").query();
    assertEquals(commandString, command.getText());
    Button admin = lookup("#viewAdministratorButton").query();
    assertEquals(adminString, admin.getText());
  }

  @Given("^There exits an admin user account in the database with details: username \"([^\"]*)\", password \"([^\"]*)\"\\.$")
  public void thereExitsAnAdminUserAccountInTheDatabaseWithDetailsUsernamePassword(String username,
      String password) {
    App.getDatabase().getAdministrators()
        .put(username, new Administrator("Command", "Prompt", "Overwrite",
            null, username, password, null, new ArrayList<>(), 1));
  }

  @When("^I have logged in as an administrator with the given details: username \"([^\"]*)\", password \"([^\"]*)\"\\.$")
  public void iHaveLoggedInAsAnAdministratorWithTheGivenDetailsUsernamePassword(String username,
      String password) {
    clickOn("#adminButton");
    clickOn("#usernameTextField").write(username);
    clickOn("#passwordField").write(password);
    clickOn("#loginButton");
  }

  @When("^From the admin main menu I click on the Edit button.$")
  public void fromTheAdminMainMenuISelectButton() throws Throwable {
    clickOn("#editButton");
  }

  @Then("^I can see my details in editable text boxes: firstName \"([^\"]*)\", middleName \"([^\"]*)\", lastName \"([^\"]*)\"\\.$")
  public void iCanSeeMyDetailsInEditableBoxesFirstNameMiddleNameLastName(String firstString,
      String secondString, String lastString) {
    TextField first = lookup("#givenNameTextField").query();
    assertEquals(firstString, first.getText());
    TextField second = lookup("#otherNameTextField").query();
    assertEquals(secondString, second.getText());
    TextField last = lookup("#lastNameTextField").query();
    assertEquals(lastString, last.getText());
  }

  @Then("^I can edit my details and change: firstName \"([^\"]*)\"\\.$")
  public void iCanEditMyDetailsAndChangeFirstName(String firstName) {
    clickOn("#givenNameTextField").write(firstName);
  }

  @Then("^After pressing \"([^\"]*)\" I should receive the confirmation message \"([^\"]*)\"\\.$")
  public void afterPressingIShouldReceiveTheConfirmationMessage(String done, String message) {
    Button doneButton = lookup("#editButton").query();
    assertEquals(done, doneButton.getText());
    clickOn(doneButton);
    Label label = lookup("#errorLabel").query();
    assertEquals(message, label.getText());

  }
}

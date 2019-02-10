package storyTests;


import cucumber.api.CucumberOptions;
import cucumber.api.SnippetType;
import cucumber.api.junit.Cucumber;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import seng302.controllers.GitlabGUITestSetup;


/**
 * Runner for cucumber tests.
 */
@RunWith(Cucumber.class)
@CucumberOptions(
    features = "src/test/java/storyTests/features",
    format = {"pretty", "html:target/site/cucumber-pretty", "json:target/cucumber.json"},
    snippets = SnippetType.CAMELCASE,
    glue = "storyTests/steps"
)
public class CucumberRunnerTest {


  /**
   * Configures TestFX for headless running. This allows it to function in Git's continuous
   * integration environment.
   */
  @BeforeClass
  public static void headless() {

    GitlabGUITestSetup.headless();

  }


}
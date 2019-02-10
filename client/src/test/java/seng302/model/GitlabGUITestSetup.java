package seng302.model;

/**
 * Should be called in the before function of GUI tests so that they can properly be run on the
 * CI-runner. Disabling of these on you local machine, by setting 'gitlab-ci' to false in your local
 * pom.xml will allow you to inspect the tests as they are run.
 */
public class GitlabGUITestSetup {

  public static void headless() {
    {
      System.setProperty("prism.verbose", "true"); // optional
      System.setProperty("java.awt.headless", "true");
      System.setProperty("testfx.robot", "glass");
      System.setProperty("testfx.headless", "true");
      System.setProperty("glass.platform", "Monocle");
      System.setProperty("monocle.platform", "Headless");
      System.setProperty("prism.order", "sw");
      System.setProperty("prism.text", "t2k");
      System.setProperty("testfx.setup.timeout", "2500");
    }
  }
}




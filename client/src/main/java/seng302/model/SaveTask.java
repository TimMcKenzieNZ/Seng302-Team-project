package seng302.model;


import javafx.application.Platform;
import javafx.concurrent.Task;
import seng302.App;
import seng302.model.person.LogEntry;


/**
 * Used to save the Users to files off the main GUI thread as this is a long process and it causes
 * the GUI to become unresponsive.
 */
public class SaveTask extends Task<String> {

  /**
   * AccountManager that contains the users to save.
   */
  private AccountManager database;


  /**
   * Constructor for SaveTask.
   *
   * @param database The AccounterManager containing the users.
   */
  public SaveTask(AccountManager database) {
    this.database = database;

  }


  /**
   * Start the task.
   *
   * @return empty string
   */
  @Override
  protected String call() {
    Platform.runLater(() -> {
      LogEntry logEntry = new LogEntry(database.getCurrentUser(), database.getCurrentUser(),
          "save", "saving", "Administrators");
      App.getDatabase().getSystemLog().add(logEntry);
    });

    database.exportAdmins();
    Platform.runLater(() -> {
      LogEntry logEntry = new LogEntry(database.getCurrentUser(), database.getCurrentUser(),
          "save", "successfully saved", "Administrators");
      App.getDatabase().getSystemLog().add(logEntry);
    });

    Platform.runLater(() -> {
      LogEntry logEntry = new LogEntry(database.getCurrentUser(), database.getCurrentUser(),
          "save", "saving", "Clinicians");
      App.getDatabase().getSystemLog().add(logEntry);
    });
    database.exportClinicians();
    Platform.runLater(() -> {
      LogEntry logEntry = new LogEntry(database.getCurrentUser(), database.getCurrentUser(),
          "save", "successfully saved", "Clinicians");
      App.getDatabase().getSystemLog().add(logEntry);
    });
    Platform.runLater(() -> {
      LogEntry logEntry = new LogEntry(database.getCurrentUser(), database.getCurrentUser(),
          "save", "saving", "Donor/Receivers");
      App.getDatabase().getSystemLog().add(logEntry);
    });
    database.exportAccounts();
    Platform.runLater(() -> {
      LogEntry logEntry = new LogEntry(database.getCurrentUser(), database.getCurrentUser(),
          "save", "successfully saved", "Donor/Receivers");
      App.getDatabase().getSystemLog().add(logEntry);
    });
    Platform.runLater(() -> {
      LogEntry logEntry = new LogEntry(database.getCurrentUser(), database.getCurrentUser(),
          "save", "save", "successful");
      App.getDatabase().getSystemLog().add(logEntry);
    });

    return "";
  }

  @Override
  protected void cancelled() {
    super.cancelled();
    updateMessage("The task was cancelled.");
  }

  @Override
  protected void failed() {
    super.failed();
    updateMessage("The task failed.");
  }

  @Override
  public void succeeded() {
    super.succeeded();
    updateMessage("The task finished successfully.");
  }
}

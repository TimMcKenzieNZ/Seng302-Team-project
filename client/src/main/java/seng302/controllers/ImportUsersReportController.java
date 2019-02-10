package seng302.controllers;

import java.util.List;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import seng302.model.ImportSummary;
import seng302.model.Marshal;
import seng302.model.person.UserAccountStatus;
import seng302.model.person.UserValidationReport;

public class ImportUsersReportController {

  private Marshal marshal;

  private ImportSummary importSummary;

  private int numberOfDuplicates = 0;

  @FXML
  public Button okButton;

  @FXML
  private TableView<UserValidationReport> successfulImportsTable;

  @FXML
  private TableView<UserValidationReport> failedImportsTable;

  @FXML
  private Label importAttempts;

  @FXML
  private Label fileRepairs;

  @FXML
  private Label fileOverwrites;

  @FXML
  private Label filesAdded;

  @FXML
  private TableColumn<UserValidationReport, String> successfulNameColumn;

  @FXML
  private TableColumn<UserValidationReport, String> successfulStatusColumn;

  @FXML
  private TableColumn<UserValidationReport, String> successfulReasonColumn;

  @FXML
  private TableColumn<UserValidationReport, String> failedNameColumn;

  @FXML
  private TableColumn<UserValidationReport, String> failedStatusColumn;

  @FXML
  private TableColumn<UserValidationReport, String> failedReasonColumn;

  /**
   * The observable list of all successful reports
   */
  private ObservableList<UserValidationReport> observableSuccessfulReports = FXCollections
      .observableArrayList();


  /**
   * The observable list of all failed reports
   */
  private ObservableList<UserValidationReport> observableFailedReports = FXCollections
      .observableArrayList();


  public void setDuplicates(int numberOfDuplicates) {
    this.numberOfDuplicates = numberOfDuplicates;
  }


  public void setMarshal(Marshal marshal) {
    initializeColumns();
    this.marshal = marshal;
    marshal.generateReportsFromExceptions();
    observableSuccessfulReports.addAll(marshal.getSuccessfulImports());
    successfulImportsTable.setItems(observableSuccessfulReports);
    observableFailedReports.addAll(marshal.getFailedImports());
    failedImportsTable.setItems(observableFailedReports);
    generateReportStatistics();
  }

  /**
   * Helper method for 'initialize()' which sets the cell factory of each column in the TableView
   * object.
   */
  private void initializeColumns() {

    // Successful Imports
    // Successful name column
    successfulNameColumn.setCellValueFactory(record -> new ReadOnlyStringWrapper(
        record.getValue().getUsername()
    ));

    // Successful status column
    successfulStatusColumn.setCellValueFactory(record -> new ReadOnlyStringWrapper(
        record.getValue().getAccountStatus().toString()
    ));
    // Successful reason column
    successfulReasonColumn.setCellValueFactory(record -> new
        ReadOnlyStringWrapper(
        record.getValue().getIssues().toString().replaceAll("\\[", "").replaceAll("\\]", "")
    ));

    // Failed Imports
    // Failed name column
    failedNameColumn.setCellValueFactory(record -> new ReadOnlyStringWrapper(
        record.getValue().getUsername()
    ));

    // Failed status column
    failedStatusColumn.setCellValueFactory(record -> new ReadOnlyStringWrapper(
        record.getValue().getAccountStatus().toString()
    ));
    // Failed reason column
    failedReasonColumn.setCellValueFactory(record -> new
        ReadOnlyStringWrapper(
        record.getValue().getIssues().toString().replaceAll("\\[", "").replaceAll("\\]", "")));
  }

  @FXML
  public void handleOkButton(ActionEvent event) {
    Stage stage = (Stage) okButton.getScene().getWindow();
    stage.close();
  }

  public void generateReportStatistics() {
    int numberOfImportAttempts = 0;
    int numberOfExistingUsersMerged = 0;
    int numberOfNewUsersAdded = 0;
    int numberOfUsersRepaired = 0;
    numberOfImportAttempts = observableFailedReports.size() + observableSuccessfulReports.size();
    numberOfExistingUsersMerged = numberOfDuplicates;
    for (UserValidationReport userReport : observableSuccessfulReports) {
      UserAccountStatus userStatus = userReport.getAccountStatus();
      if (userStatus.equals(UserAccountStatus.REPAIRED)) {
        numberOfUsersRepaired += 1;
        numberOfNewUsersAdded += 1;
      } else if (userStatus.equals(UserAccountStatus.POOR) || userStatus
          .equals(UserAccountStatus.VALID)) {
        numberOfNewUsersAdded += 1;
      }
    }
    importAttempts.setText(importAttempts.getText() + " " + numberOfImportAttempts);
    fileRepairs.setText(fileRepairs.getText() + " " + numberOfUsersRepaired);
    fileOverwrites.setText(fileOverwrites.getText() + " " + numberOfExistingUsersMerged);
    filesAdded.setText(filesAdded.getText() + " " + numberOfNewUsersAdded);

  }

  public void setImportSummary(ImportSummary importSummary) {
    initializeColumns();
    this.importSummary = importSummary;

    int numDuplicates = 0;

    List<UserValidationReport> successfulImports = importSummary.getSuccessfulImports();
    List<UserValidationReport> rejectedImports = importSummary.getRejectedImports();

    for(UserValidationReport report: successfulImports) {
      for(Object issue: report.getIssues()) {
        if(issue.equals("Duplicate account exists")){
          numDuplicates++;
        }
      }
    }

    observableSuccessfulReports.addAll(successfulImports);
    successfulImportsTable.setItems(observableSuccessfulReports);

    observableFailedReports.addAll(rejectedImports);
    failedImportsTable.setItems(observableFailedReports);

    setDuplicates(numDuplicates);

    generateReportStatistics();
  }
}

package seng302.model;

import java.util.ArrayList;
import java.util.List;
import seng302.model.person.UserValidationReport;

public class ImportSummary {
  private List<UserValidationReport> successfulImports = new ArrayList<>();
  private List<UserValidationReport> rejectedImports = new ArrayList<>();

  public void addSuccessfulImport(UserValidationReport report) {
    successfulImports.add(report);
  }

  public void addRejectedImport(UserValidationReport report) {
    rejectedImports.add(report);
  }

  public List<UserValidationReport> getSuccessfulImports() {
    return successfulImports;
  }

  public List<UserValidationReport> getRejectedImports() {
    return rejectedImports;
  }

  public String toString() {
    String goodImports = "";
    for(UserValidationReport successfulImport:successfulImports) {
      goodImports += successfulImport.toString() + "\n";
    }
    String badImports = "";
    for(UserValidationReport rejectedImport:rejectedImports) {
      badImports += rejectedImports.toString() + "\n";
    }

    return "Good Imports: \n" + goodImports + "=========="
        + "\nBad Imports: \n" + badImports + "==========";
  }
}

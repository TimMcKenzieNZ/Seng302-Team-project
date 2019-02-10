package seng302.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import seng302.model.CLICommandHandler;
import java.util.ArrayList;
import java.util.List;


/**
 * Controller for command line gui
 */
public class CliTabController {

  /**
   * TextField for the command line input
   */
  @FXML
  TextField cliInput;

  /**
   * ListView where the command line out put will be displayed
   */
  @FXML
  ListView cliOutput;

  ObservableList commandOutput = FXCollections.observableArrayList();

  /**
   * List of previous command by the clinician
   */
  private List previousCommands;

  /**
   * Current index of the command shown in cliInput
   */
  private int currentIndex;

  private CLICommandHandler commandHandler = new CLICommandHandler();


  /**
   * Initialize the CLI tab
   */
  @FXML
  private void initialize() {
    previousCommands = new ArrayList();
    currentIndex = -1;
    initializeKeyPressEvent();
    initializeCellFactory();
    cliOutput.setItems(commandOutput);
  }


  /**
   * Initialises and sets the cell factory used to represent items in the CLI output. This ensures
   * that String objects passed to the ListView are wrapped.
   * Also sets the font to properly show the table on a select statement.
   * Please use a monospaced font otherwise the formatting will be broken
   */
  private void initializeCellFactory() {
    assert cliOutput != null;
    cliOutput.setCellFactory(listView -> new ListCell<String>() {

      @Override
      public void updateItem(String item, boolean empty) {
        super.updateItem(item, empty);
        if (item != null) {
          Text itemWrapper = new Text(item);
          itemWrapper.setFont(Font.font("Consolas"));
          // itemWrapper.setWrappingWidth(cliOutput.getWidth() - END_OFFSET); // Enable text wrapping
          setGraphic(itemWrapper);
        }
      }

    });
  }


  /**
   * Initialises an key press event handler applied to the CLI input field. This manages UP and DOWN
   * key presses, which controls command line history.
   */
  private void initializeKeyPressEvent() {
    assert cliInput != null;
    cliInput.setOnKeyPressed(event -> {
      if (event.getCode() == KeyCode.UP) {
        goBackward();
      } else if (event.getCode() == KeyCode.DOWN) {
        goForward();
      }
    });
  }


  /**
   * Occurs when the user selects up, will show the last command entered
   */
  private void goBackward() {
    if (this.currentIndex > 0) {
      currentIndex--;
      cliInput.setText((String) previousCommands.get(currentIndex));
    }
  }

  /**
   * Occurs when the user selects down, will show the next command after the current
   */
  private void goForward() {
    if (this.currentIndex + 1 < previousCommands.size()) {
      currentIndex++;
      cliInput.setText((String) previousCommands.get(currentIndex));
    } else {
      cliInput.setText("");
    }
  }


  /**
   * Takes command and runs in the cli.
   *
   * We clear out the lists in a very 'unusual' manner since normally clearing the lists doesn't work.
   * This is due to java caching part of the ListView and retaining it so that even after calling clear, it still retains it
   * This leads to weird 'duplicated command outputs' where the original command has been removed from the listview because of 'clear', a duplicate one has been created
   * In order to fix this, we reassign commandOutput to be a new observableArrayList and reset its contents every time we run a command.
   * This should theoretically cause java to clean up the cached memory since the object referencing it has been reassigned.
   * Refer to https://eng-git.canterbury.ac.nz/seng302-2018/team-600/issues/43 for more information
   */
  @FXML
  private void commandEnter() {
    String command = cliInput.getText();
    List<String> tempCommands = new ArrayList<>(previousCommands);
    if (command.equalsIgnoreCase("clear")) {
      commandOutput = FXCollections.observableArrayList();
      initialize();
    } else {
      ArrayList<String> messages = commandHandler.commandControl(command);
      ArrayList<String> tempOutput = new ArrayList<>(commandOutput);
      commandOutput = FXCollections.observableArrayList(tempOutput);
      initialize();
      commandOutput.addAll(0,messages);
      }
    previousCommands = tempCommands;
    previousCommands.add(command);
    cliInput.setText("");
    currentIndex = previousCommands.size();
  }
}

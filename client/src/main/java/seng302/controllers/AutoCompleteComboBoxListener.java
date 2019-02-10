package seng302.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import seng302.model.MedicationAutoCompleteResult;
import seng302.model.MedicationService;

/**
 * Listener for editable combobox which implements autocompletion of drug search autocomplete using
 * MedicationService
 *
 * @param <T> Type used in combobox listener
 */
public class AutoCompleteComboBoxListener<T> implements EventHandler<KeyEvent> {

  private ComboBox comboBox;
  private ObservableList<T> data;
  private boolean moveCaretToPos = false;
  private int caretPos;
  private String previousQuery;
  private int queryId = 0;
  private int currentId = 0;

  /**
   * Constructor for AutoCompleteComboBoxListener
   *
   * @param comboBox Combobox to bind to
   */
  public AutoCompleteComboBoxListener(final ComboBox comboBox) {
    this.comboBox = comboBox;
    this.data = comboBox.getItems();
    this.previousQuery = "";
    this.comboBox.setEditable(true);
    this.comboBox.setOnKeyPressed(new EventHandler<KeyEvent>() {

      @Override
      public void handle(KeyEvent t) {
        comboBox.hide();
      }
    });
    this.comboBox.setOnKeyReleased(AutoCompleteComboBoxListener.this);
  }


  /**
   * Event handler for AutoCompleteComboBoxListener
   *
   * @param event Event to be handled
   */
  @Override
  public void handle(KeyEvent event) {
    // int to prevent slow queries over writing more recent results
    int localID = queryId;
    // increment counter
    queryId++;

    // Handle special keys
    if (event.getCode() == KeyCode.UP) {
      caretPos = -1;
      moveCaret(comboBox.getEditor().getText().length());
      return;
    } else if (event.getCode() == KeyCode.DOWN) {
      if (!comboBox.isShowing()) {
        comboBox.show();
      }
      caretPos = -1;
      moveCaret(comboBox.getEditor().getText().length());
      return;
    } else if ((event.getCode() == KeyCode.BACK_SPACE) || (event.getCode() == KeyCode.DELETE)) {
      moveCaretToPos = true;
      caretPos = comboBox.getEditor().getCaretPosition();
    }

    if (event.getCode() == KeyCode.RIGHT || event.getCode() == KeyCode.LEFT
        || event.isControlDown() || event.getCode() == KeyCode.HOME
        || event.getCode() == KeyCode.END || event.getCode() == KeyCode.TAB) {
      return;
    }

    // Create Task instance
    Task<Void> task = new Task<Void>() {
      // Implement required call() method
      @Override
      protected Void call() throws Exception {

        ObservableList list = FXCollections.observableArrayList();
        data = comboBox.getItems();

        // If the previous query starts with the current query, then the results of the current query are a subset of the
        // previous query and we cant avoid a slow api call and search the previous query instead.
        if ((previousQuery.length() > 0) && (AutoCompleteComboBoxListener.this.comboBox
            .getEditor().getText().toLowerCase().startsWith(previousQuery.toLowerCase()))) {


          previousQuery = AutoCompleteComboBoxListener.this.comboBox.getEditor().getText();
          for (int i = 0; i < data.size(); i++) {
            if (data.get(i).toString().toLowerCase().startsWith(
                AutoCompleteComboBoxListener.this.comboBox
                    .getEditor().getText().toLowerCase())) {
              list.add(data.get(i));
            }
          }
        }

        // Not a subset of previous results, get results from api
        else if (AutoCompleteComboBoxListener.this.comboBox
            .getEditor().getText().length() > 0) {

          MedicationAutoCompleteResult apiResult;
          apiResult = MedicationService.drugAutoComplete(AutoCompleteComboBoxListener.this.comboBox
              .getEditor().getText());
          list.setAll(apiResult.getSuggestions());
          previousQuery = apiResult.getQuery();
        }

        Platform.runLater(() -> updateCombobox(list, localID));

        return null;
      }
    };
    // Run task in new thread
    new Thread(task).start();

  }


  /**
   * Used to move the caret position to right of the text when the text in combobox is updated.
   *
   * @param textLength The length of the text
   */
  private void moveCaret(int textLength) {
    if (caretPos == -1) {
      comboBox.getEditor().positionCaret(textLength);
    } else {
      comboBox.getEditor().positionCaret(caretPos);
    }
    moveCaretToPos = false;
  }


  /**
   * Updates the combobox to contain the strings in the given list.
   *
   * @param list list of strings to add to the combobox
   */
  private void updateCombobox(ObservableList list, int localID) {
    if (localID >= currentId) {
      currentId = localID;
      String t = comboBox.getEditor().getText();

      comboBox.setItems(list);
      comboBox.getEditor().setText(t);
      if (!moveCaretToPos) {
        caretPos = -1;
      }
      moveCaret(t.length());
      if (!list.isEmpty()) {
        comboBox.show();
      }
    }
  }
}
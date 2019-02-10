package seng302.controllers;

import javafx.scene.control.TableView;
import javafx.scene.layout.Pane;

/**
 * HeaderlessTable is an extension of TableView which creates a table with no visible header. This
 * code is based on a solution to a StackOverflow question posted by Don Wills
 * (https://stackoverflow.com/a/44534520).
 */
public class HeaderlessTable extends TableView {


  /**
   * Overrides the standard resize method so that the header row of the table is hidden from view.
   *
   * @param width The new with as a double.
   * @param height The new height as a double.
   */
  @Override
  public void resize(double width, double height) {

    // Perform standard resize operation.
    super.resize(width, height);

    // Retrieve and remove header from table.
    Pane tableHeaderRow = (Pane) lookup("TableHeaderRow");
    tableHeaderRow.setMinHeight(0);
    tableHeaderRow.setPrefHeight(0);
    tableHeaderRow.setMaxHeight(0);
    tableHeaderRow.setVisible(false);

  }


}

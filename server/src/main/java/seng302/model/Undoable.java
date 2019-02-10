package seng302.model;

/**
 * An event which can be reverted in the GUI
 */
public interface Undoable {

  /**
   * Procedure when selected undo
   */
  void undo();

  /**
   * Procedure when selected redo
   */
  void redo();

  String getUndoRedoName();
}

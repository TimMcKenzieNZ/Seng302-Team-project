package seng302.model;

import java.util.Stack;

/**
 * A stack to store Undoables to be used when the user selects undo/redo in the gui
 */
public class CommandStack {

  private final Stack<Undoable> undo;
  private final Stack<Undoable> redo;

  public CommandStack() {
    undo = new Stack<>();
    redo = new Stack<>();
  }

  public void addCommand(Undoable undoable) {
    if (undoable != null) {
      undo.add(undoable);
      redo.clear();
    }
  }

  public void undo() {
    if (!undo.isEmpty()) {
      Undoable undoable = undo.pop();
      undoable.undo();
      redo.push(undoable);
    }

  }


  public void redo() {
    if (!redo.isEmpty()) {
      Undoable undoable = redo.pop();
      undoable.redo();
      undo.push(undoable);
    }
  }

  public Stack<Undoable> getRedo() {
    return redo;
  }

  public Stack<Undoable> getUndo() {
    return undo;
  }

  public void save() {
    undo.clear();
    redo.clear();
  }
}

package seng302.model;

import java.io.IOException;
import java.io.InputStream;
import jline.console.ConsoleReader;

public class CommandLine {

  ConsoleReader consoleReader;

  public CommandLine() {
    try {
      consoleReader = new ConsoleReader(System.in, System.out);
    } catch (IOException exception) {
      System.out.println(
          "ERROR: The console reader could not be created. The application was forced to close.");
      System.exit(1);
    }
  }


  /**
   * Reads the next line and returns it as a string.
   *
   * @return The next line as a String.
   */
  public String readInput() {
    String input = "";
    try {
      input = consoleReader.readLine();
    } catch (IOException exception) {
      System.out.println("ERROR: The last user input could not be retrieved.");
    }
    return input;
  }


  /**
   * Returns the input stream of the console reader.
   *
   * @return The console reader's input stream.
   */
  public InputStream getStream() {
    return consoleReader.getInput();
  }


}
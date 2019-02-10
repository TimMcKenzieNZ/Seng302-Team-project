package seng302.model.Database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Parses a given SQL command string for forbidden keywords given in the SQLBlackList and throws an IllegalArgumentException.
 */
public class CLIToSQLCommandSanitizer {

    private static final List<String> SQLBlackList = new ArrayList<>(Arrays.asList("drop table", "delete", "alter table", "create table", "insert", "update", "truncate table", "sleep", "wait"));

    /**
     * A static method that parses the given command for key words given in the SQLBlacklist
     * If any are present then the object will throw an IllegalArgumentException with the given forbidden keyword.
     *
     * @param SQL A string SQL command. The only acceptable command is a 'SELECT' command.
     * @throws IllegalArgumentException Thrown when the given command contains a forbidden SQL keyword given in the SQLBlackList.
     */
    public void validateSQL(String SQL) throws IllegalArgumentException {
        for (String phrase : SQLBlackList) {
            if (SQL.toLowerCase().contains(phrase)) {
                throw new IllegalArgumentException("SQL query should not contain '" + phrase + "'!");
            }
        }
    }

}

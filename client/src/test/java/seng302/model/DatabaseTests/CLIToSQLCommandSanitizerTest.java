package seng302.model.DatabaseTests;

import static junit.framework.TestCase.assertEquals;
import org.junit.Test;
import org.junit.Before;
import seng302.model.Database.CLIToSQLCommandSanitizer;
import java.util.Random;


import java.util.ArrayList;
import java.util.Arrays;


/**
 * A test class to test the CLIToSQLCommandSanitizer class.
 */
public class CLIToSQLCommandSanitizerTest {

    private CLIToSQLCommandSanitizer sanitizer = new CLIToSQLCommandSanitizer();

    private String commandTestString;

    private Random randomNumberGenerator;

    @Before
    public void setUp() {
        randomNumberGenerator = new Random();

        commandTestString = "the quick brown fox jumped over the lazy LYCAN.";
    }


    /**
     * Tests the CLIToSQLCommandSanitizer by checking every blacklisted keyword. For each keyword A command string is created and
     * that keyword is randomly inserted into the string. The string is then checked by the CLIToSQLCommandSanitizer and an error should be thrown.
     * We check that an error is thrown for each keyword, and that the error message matches that keyword.
     */
    @Test
    public void testCommandForAllBlackListedSQLKeyWords () {
        ArrayList<String> errorMessages = new ArrayList<String>();

        ArrayList<String> keywordList = new ArrayList<String>(Arrays.asList("alter table", "create table", "delete", "drop table", "insert", "truncate table", "update"));

        int errorCount = 0;

        for (String word: keywordList) {
            int insertionIndex = randomNumberGenerator.nextInt(commandTestString.length() -1);
            StringBuilder testString = new StringBuilder(commandTestString);
                    testString.insert(insertionIndex, word).toString();
            try {

                sanitizer.validateSQL(testString.toString());
            } catch (IllegalArgumentException e) {
                errorCount ++;
                errorMessages.add(e.getMessage());
            }
        }

        assertEquals("errorCount does not match what is expected.", keywordList.size(), errorCount);

        errorMessages.sort(String::compareToIgnoreCase);
        keywordList.sort(String::compareToIgnoreCase);

        for (int i = 0; i < keywordList.size(); i ++) {
            String errorMessage = errorMessages.get(i);
            String word = keywordList.get(i);
            String expectedErrorMessage = "SQL query should not contain '" + word + "'!";
            assertEquals("Error message '" + errorMessage + "' does not match expected error message: " + expectedErrorMessage,
                    expectedErrorMessage, errorMessage);
        }
    }


    /**
     * Tests that a string with no blacklisted keywords does not cause the CLIToSQLCommandSanitizer to throw an exception.
     */
    @Test
    public void testBlueSkyScenarioForValidator() {
        boolean thrown = false;
        try {
            sanitizer.validateSQL(commandTestString);
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        assertEquals("CLIToSQLCommandSanitizer incorrectly threw exception for the string " + commandTestString, false, thrown);
    }
}

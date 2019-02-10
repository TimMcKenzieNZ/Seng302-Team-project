package seng302.model.databaseTests.testingUtilities;

import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.SQLException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import seng302.model.database.DBCConnection;


/**
 * Utility class containing methods for running sql scripts to reset and resample the database for testing purposes.
 */
public class DatabaseUtilities {

    private DatabaseUtilities() {
        throw new IllegalStateException("Utility class");
    }

    static DBCConnection connection;

    private static final String testURL = "jdbc:mariadb://mysql2.csse.canterbury.ac.nz/seng302-2018-team600-test";

    public final static String RESET_DEATH_DETAILS_TEST_TABLE = "/ResetDeathDetailsTestTable.sql";

    public final static String POST_DEATH_DETAILS_TEST_DATA = "/PostDeathDetailsTestData.sql";

    public final static String DELETE_DEATH_DETAILS_TEST_DATA = "/DeleteDeathDetailsTestData.sql";


    /**
     * Runs the given sql file on test database.
     *
     * @param fileName name of sql to run.
     * @throws SQLException if unable to connect to database.
     */
    public static void runSqlScript(String fileName) throws SQLException {
        String url = testURL;
        connection = new DBCConnection();
        connection.setConnection(url);
        runSqlFile(connection.getConnection(), fileName);
        connection.terminate();
    }


    static private void runSqlFile(Connection connection, String SQLFileName) {
        Resource resource = new ClassPathResource(SQLFileName);
        EncodedResource encodedResource = new EncodedResource(resource, Charset.forName("UTF-8"));
        ScriptUtils.executeSqlScript(connection, encodedResource);
    }
}
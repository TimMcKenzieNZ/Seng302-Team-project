package seng302.model.databaseTests.sprintBootTests;

import static org.junit.Assert.assertEquals;
import static seng302.model.databaseTests.testingUtilities.EndpointInteractionUtilities.createHeaders;
import static seng302.model.databaseTests.testingUtilities.EndpointInteractionUtilities.login;

import org.json.JSONException;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import seng302.model.database.DBCConnection;
import seng302.model.databaseTests.testingUtilities.DatabaseUtilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * This class contains a series of tests for delete death details endpoint and
 * uses Spring Boot's testing utilities alongside JUnit.
 */
@Ignore //Ignored as This endpoint should not be used. 
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DeleteDeathDetailsTest {

    // Class attributes.
    @Autowired
    private TestRestTemplate template;
    private static final String DEFAULT_ADMIN_USERNAME = "Sudo";
    private static final String DEFAULT_CLINICIAN_USERNAME = "5";
    private static final String DEFAULT_PASSWORD = "password";
    private static final String DONOR_1 = "DDD1111";
    private static final String DONOR_2 = "DDD1112";
    private static final String DONOR_3 = "DDD1113";
    private static final String DONOR_4 = "DDD1114";
    private static final String DONOR_5 = "DDD1115";
    private static final String DONOR_6 = "DDD1116";
    static private DBCConnection dbcConnection;


    /**
     * Configures DBCConnection to use the test database instead of production, resets the DeathDetails table and
     * populates it with some test data. This is called before the server is run.
     */
    @BeforeClass
    public static void setUp() throws SQLException {
        dbcConnection = new DBCConnection();
        dbcConnection.setConnection("jdbc:mariadb://mysql2.csse.canterbury.ac.nz/seng302-2018-team600" +
                "-test?allowMultiQueries=true");
        DBCConnection.setTestDatabase(true);
        DatabaseUtilities.runSqlScript(DatabaseUtilities.RESET_DEATH_DETAILS_TEST_TABLE);
        DatabaseUtilities.runSqlScript(DatabaseUtilities.DELETE_DEATH_DETAILS_TEST_DATA);
        ;
    }


    /**
     * Configures DBCConnection to use its default database, which some tests
     * expect and would break without and resets the DeathDetails table.
     */
    @AfterClass
    public static void tearDown() throws SQLException {
        DBCConnection.setTestDatabase(false);
        DatabaseUtilities.runSqlScript(DatabaseUtilities.RESET_DEATH_DETAILS_TEST_TABLE);
    }


    /**
     * Tests that DeathDetails can be successfully deleted by an authenticated admin.
     * Checks response status code is accepted (202).
     */
    @Test
    public void deleteDeathDetailsAsAdmin() throws JSONException {
        String token = login(DEFAULT_ADMIN_USERNAME, DEFAULT_PASSWORD, template);
        assertEquals(true, checkDatabaseForDeathDetails(DONOR_1));
        ResponseEntity responseEntity = deleteDeathDetails(token, DONOR_1);
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        assertEquals(false, checkDatabaseForDeathDetails(DONOR_1));
    }


    /**
     * Tests that DeathDetails can be successfully deleted by an authenticated admin.
     * Checks response status code is accepted (202).
     */
    @Test
    public void deleteDeathDetailsAsClinician() throws JSONException {
        String token = login(DEFAULT_CLINICIAN_USERNAME, DEFAULT_PASSWORD, template);
        assertEquals(true, checkDatabaseForDeathDetails(DONOR_2));
        ResponseEntity responseEntity = deleteDeathDetails(token, DONOR_2);
        assertEquals(HttpStatus.ACCEPTED, responseEntity.getStatusCode());
        assertEquals(false, checkDatabaseForDeathDetails(DONOR_2));
    }


    /**
     * Tests that donor cannot delete their own DeathDetails.
     * Checks response status code is forbidden (403).
     */
    @Test
    public void deleteOwnDeathDetailsAsDonor() throws JSONException {
        String token = login(DONOR_3, DEFAULT_PASSWORD, template);
        assertEquals(true, checkDatabaseForDeathDetails(DONOR_3));
        ResponseEntity responseEntity = deleteDeathDetails(token, DONOR_3);
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        assertEquals(true, checkDatabaseForDeathDetails(DONOR_3));
    }


    /**
     * Tests that donor cannot delete another donors DeathDetails.
     * Checks response status code is forbidden (403).
     */
    @Test
    public void deleteDeathDetailsAsDonor() throws JSONException {
        String token = login(DONOR_1, DEFAULT_PASSWORD, template);
        assertEquals(true, checkDatabaseForDeathDetails(DONOR_4));
        ResponseEntity responseEntity = deleteDeathDetails(token, DONOR_4);
        assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode());
        assertEquals(true, checkDatabaseForDeathDetails(DONOR_4));
    }


    /**
     * Tests that anonymous/unauthenticated user cannot delete DeathDetails.
     * Checks response status code is forbidden (403).
     */
    @Test
    public void deleteDeathDetailsAsAnonymous() throws JSONException {
        HttpEntity<Object> request = new HttpEntity<Object>("");
        assertEquals(true, checkDatabaseForDeathDetails(DONOR_5));
        ResponseEntity response = template.exchange("/api/v1/donors/" + DONOR_5 + "/deathDetails", HttpMethod.DELETE, request, String.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(true, checkDatabaseForDeathDetails(DONOR_5));
    }


    /**
     * Tests that correct response is given when attempting to delete non-existent death details.
     * Checks response status code is bad request (400).
     */
    @Test
    public void deleteNonExistantDeathDetails() throws JSONException {
        String token = login(DEFAULT_ADMIN_USERNAME, DEFAULT_PASSWORD, template);
        assertEquals(false, checkDatabaseForDeathDetails(DONOR_6));
        ResponseEntity responseEntity = deleteDeathDetails(token, DONOR_6);
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        assertEquals(false, checkDatabaseForDeathDetails(DONOR_6));
    }


    /**
     * Helper method for making delete death details request.
     *
     * @param token auth token
     * @param nhi of donor that has died
     * @return ResponseEntity
     */
    private ResponseEntity deleteDeathDetails(String token, String nhi) {

        HttpEntity<Object> request = new HttpEntity<Object>("", createHeaders(token));
        ResponseEntity response = template.exchange("/api/v1/donors/" + nhi + "/deathDetails", HttpMethod.DELETE, request, String.class);
        return response;

    }


    /**
     * Checks the DeathDetails table in the test database directly for an entry with the given nhi.
     *
     * @param nhi nhi of the user whose death details to check
     * @return True if death details exist, false otherwise.
     */
    private boolean checkDatabaseForDeathDetails(String nhi){
        Connection connection = dbcConnection.getConnection();
        String sql = "SELECT * FROM `DeathDetails` WHERE `ddUsername`= ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nhi);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return true;
                } else {
                   return false;
                }
            } catch (SQLException e1) {
                return false;
            }
        } catch (SQLException e1) {
            return false;
        }
    }
}









package seng302.model.databaseTests;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import seng302.controller.AdminController;
import seng302.controller.ClinicianController;
import seng302.controller.LoginController;
import seng302.model.database.DBCConnection;
import seng302.model.database.UserService;
import seng302.model.person.*;
import seng302.model.security.AuthenticationTokenStore;
import seng302.model.security.LoginAttempt;
import seng302.model.security.LoginResult;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static junit.framework.TestCase.assertTrue;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;


/**
 * A class to test the User endpoints of the UserServiceAPI class (for both admins and clinicians)
 */
@Ignore
public class UserAPITest {
    private static final String url = "jdbc:mariadb://mysql2.csse.canterbury.ac.nz/seng302-2018-team600-test";

    private AdminController controller;
    private LoginController loginController;

    private DBCConnection connection;
    private User user;
    private LogEntry entry;
    private UserCreator params;
    private UserCreator params2;
    private UserCreator params3;
    private ArrayList<LogEntry> entries;

    private static final String logString = "User Being Modified: Marik Ishtar (Administrator, username: Winged Dragon of Ra), " +
            "Changed by User: Marik Ishtar (Administrator, username: Winged Dragon of Ra), Dance moves changed from 'break' to 'sassy'";


    @Before
    public void setUp() {

        connection = mock(DBCConnection.class);
        //connection.setConnection(url);
        System.out.println(connection.getConnection());
        System.out.println(connection.isConnected());
        AuthenticationTokenStore authenticationTokenStore = new AuthenticationTokenStore();
        controller = new AdminController(authenticationTokenStore);
        loginController = new LoginController(new AuthenticationTokenStore());
        user = new Administrator( "Marik", "", "Ishtar","Winged Dragon of Ra" , "Shadow Realm");
        user.getContactDetails().setMobileNum("027 345 6789");
        user.getContactDetails().setHomeNum("123 4567");
        user.getContactDetails().setEmail("blah@blah.com");
        Address add = new Address("123", "happy street", "happyburb",
                "happytown", "happyregion", "2345", "ZZ");
        user.getContactDetails().setAddress(add);
        entry = new LogEntry(user, user, "Dance moves", "sassy", "break");
        LogEntry entry2 = new LogEntry(user, user, "Dance moves2", "what", "break");
        LogEntry entry3 = new LogEntry(user, user, "Dance moves3", "no", "break");

        entries = new ArrayList<>();
        entries.add(entry);
        entries.add(entry2);
        entries.add(entry3);
        user.getModifications().add(entry);
        params = new UserCreator("Winged Dragon of Ra", "Marik", "", "Ishtar", "Shadow Realm", null, entry, null, "0800838383");
        params2 = new UserCreator("Winged Dragon of Ra", "Marik", "", "Ishtar", "Shadow Realm", "Canterbury", entry, null, "0800838383");
        params3 = new UserCreator("Winged Dragon of Ra", "Marik", "bob", "Ishtar", "Shadow Realm", "Canterbury", entry, entries, "0800838383");


        //entry = new LogEntry(user, user, "Dance moves", "break", "sassy");
        //user.getModifications().add(entry);
        LoginAttempt loginAttempt = new LoginAttempt();
        loginAttempt.setUsername("Sudo");
        loginAttempt.setPassword("password");
        ResponseEntity responseEntity = loginController.login(loginAttempt);

    }


    @Test
    public void postAndDeleteUserBlueSky() {
        ResponseEntity result = controller.addAdmin(params);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        ResponseEntity res = controller.deleteAdmin("Winged Dragon of Ra");
        assertEquals(HttpStatus.ACCEPTED, res.getStatusCode());
    }

    @Test
    public void deleteUserFail() { ;
        ResponseEntity res = controller.deleteAdmin("Winged Dragon of Ra");
        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }


    @Test
    public void postUserFailedUsername() {
        boolean thrown = false;
        try {

            params.setUsername(null);
            controller.addAdmin(params);
        } catch (AssertionError e) {
            thrown = true;
        }
        assertTrue(thrown);
    }


    @Test
    public void postUserDuplicateFail() {
        controller.addAdmin(params);
        ResponseEntity result = controller.addAdmin(params);
        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
        ResponseEntity res = controller.deleteAdmin("Winged Dragon of Ra");
        assertEquals(HttpStatus.ACCEPTED, res.getStatusCode());
    }


    @Test
    public void postAndDeleteClinicianBlueSky() {
        AuthenticationTokenStore authenticationTokenStore = new AuthenticationTokenStore();
        ClinicianController cont = new ClinicianController(authenticationTokenStore);
        params2.setUsername("234");
        System.out.println(params2.getUsername());
        ResponseEntity result = cont.addClinician(params2);
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        ResponseEntity res = controller.deleteAdmin("234");
        assertEquals(HttpStatus.ACCEPTED, res.getStatusCode());
    }


    @Test
    public void GetUserBlueSkyForClinician() {
        AuthenticationTokenStore authenticationTokenStore = new AuthenticationTokenStore();
        ClinicianController cont = new ClinicianController(authenticationTokenStore);
        ResponseEntity result = cont.getClinician("5");
        User clinician = (User) result.getBody();
        System.out.println(result);
        cont.deleteClinician("Winged Dragon of Ra");
        assertEquals("Winged Dragon of Ra", clinician.getUserName());
        assertEquals("Marik", clinician.getFirstName());
        assertEquals("", clinician.getMiddleName());
        assertEquals("Ishtar", clinician.getLastName());
        assertEquals("Shadow Realm", clinician.getPassword());
        assertEquals(1, clinician.getVersion());
        assertEquals(true, clinician.isActive());
        assertEquals("Canterbury", clinician.getContactDetails().getAddress().getRegion());
        fail("Could not get User");
    }


    @Test
    public void GetUserBlueSkyForAdmin() {

            ResponseEntity result = controller.getAdmin("The Tester");
            User admin = (User) result.getBody();
            assertEquals("The Tester", admin.getUserName());
            assertEquals("Marik", admin.getFirstName());
            assertEquals("", admin.getMiddleName());
            assertEquals("Ishtar", admin.getLastName());
            assertEquals("Shadow Realm", admin.getPassword());
            assertEquals(1, admin.getVersion());
            assertEquals(true, admin.isActive());
            assertEquals("027 345 6789", admin.getContactDetails().getMobileNum());
            assertEquals(2, admin.getModifications().size());
            // Last assert checks if the first logentry matches what is expected.
            assertEquals(logString, admin.getModifications().get(0).toString().substring(0, 203));

    }

    @Test
    public void GetUserFailBadUsername() {
        ResponseEntity result = controller.getAdmin("Dragony McDragonFace");
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }


//    @Test
//    public void patchAdminBlueSkyAllAttributes() {
//        controller.addAdmin(params);
//        ResponseEntity result = controller.updateAdmin(params3);
//        ResponseEntity res = controller.getAdmin("Winged Dragon of Ra");
//        User admin = (User) res.getBody();
//        controller.deleteAdmin("Winged Dragon of Ra");
//        assertEquals("number of entry logs should be three", 3, admin.getModifications().size());
//        assertEquals("bob", admin.getMiddleName());
//        assertEquals(HttpStatus.ACCEPTED, result.getStatusCode());
//
//    }
//
//
//    @Test
//    public void patchFailAdminNotFound() {
//        ResponseEntity result = controller.updateAdmin(params);
//        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
//
//    }

//    @After
//    public void tearDown() {
//        D
//    }
}

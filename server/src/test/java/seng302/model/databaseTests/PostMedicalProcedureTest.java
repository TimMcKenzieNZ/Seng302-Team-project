package seng302.model.databaseTests;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import seng302.controller.DonorReceiverController;
import seng302.model.MedicalProcedure;
import seng302.model.ProcedureDelete;
import seng302.model.person.DonorReceiverCreator;
import seng302.model.security.AuthenticationTokenStore;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class PostMedicalProcedureTest extends APITest {

    private static DonorReceiverController donorReceiverController;

    @BeforeClass
    public static void setUp() {
        connectToDatabase();
        adminLogin("test");
        donorReceiverController = new DonorReceiverController(authenticationTokenStore);
    }

    @Test
    public void addProcedurePass() {
        String summary = "This is a test summary";
        String description = "This is a test description";
        LocalDate now = LocalDate.now();
        ArrayList<String> affectedOrgans = new ArrayList<String>();
        affectedOrgans.add("Liver");
        affectedOrgans.add("Heart");
        MedicalProcedure medicalProcedure = new MedicalProcedure(summary,description, now, affectedOrgans);
        if (canRunTests) {
            ResponseEntity responseEntity = donorReceiverController.addDonorProcedure("ABC1234", medicalProcedure);
            assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
            ProcedureDelete procedureDelete = new ProcedureDelete();
            procedureDelete.setSummary(medicalProcedure.getSummary());
            donorReceiverController.deleteDonorProcedure("ABC1234", procedureDelete);
        } else {
            System.out.println("Test cannot be run");
        }
    }

    @Test
    public void badProcedure() {
        String summary = "This is a second test summary";
        String description = "This is a second test description";
        LocalDate now = LocalDate.now();
        ArrayList<String> affectedOrgans = new ArrayList<String>();
        MedicalProcedure medicalProcedure = new MedicalProcedure(summary,description, now, affectedOrgans);
        if (canRunTests) {
            ResponseEntity responseEntity = donorReceiverController.addDonorProcedure("ABC1234", medicalProcedure);
            assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
            responseEntity = donorReceiverController.addDonorProcedure("ABC1234", medicalProcedure);
            assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
            ProcedureDelete procedureDelete = new ProcedureDelete();
            procedureDelete.setSummary(medicalProcedure.getSummary());
            donorReceiverController.deleteDonorProcedure("ABC1234", procedureDelete);
        } else {
            System.out.println("Test cannot be run");
        }
    }

}

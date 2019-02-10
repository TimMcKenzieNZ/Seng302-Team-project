package seng302.model.databaseTests;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import seng302.model.database.DBCConnection;

import java.sql.SQLException;
import java.util.List;

public class DBCConnectionTest {

  /**
   * Connects to the test database and attempts to retrieve the user Sweeny Todd.
   */
  @Test
  @Ignore
  public void connectToDatabaseTest() throws SQLException {

    DBCConnection connection = new DBCConnection("jdbc:mariadb://mysql2.csse.canterbury.ac.nz/seng302-2018-team600-test", "seng302-team600", "TailspinElla4435");
    List<String> result = connection.executeSanatizedStatement("SELECT * FROM Users WHERE username='ABC1234'");
    Assert.assertEquals("| ABC1234  | Sweeny    | null       | Todd     | password | 1      | 2018-05-16 23:31:22.0 | 1       |", result.get(0));
  }

}

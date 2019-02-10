package seng302.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@APIController
public class TestController extends BaseController {

  @GetMapping(value = "status")
  public ResponseEntity testStatus() {
    return new ResponseEntity<>("Server is running", HttpStatus.OK);
  }

  /* TODO: Add some security for this endpoint (i.e a custom header/body value
     that must be in the request so nobody accidentally queries this endpoint */
  @PostMapping(value = "reset")
  public ResponseEntity resetServer() {
    try {
      File dir = new File("~/../../home/gitlab-runner/builds/9a494248/0/seng302-2018/team-600");
      String[] environment = {"PATH=/home/sengstudent/bin:/home/sengstudent/.local/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games:/snap/bin:/usr/lib/jvm/java-8-oracle/bin:/usr/lib/jvm/java-8-oracle/db/bin:/usr/lib/jvm/java-8-oracle/jre/bin"};
      Runtime.getRuntime().exec("docker stop team600_web_1 && "
          + "docker container prune -f && "
          + "docker-compose up -d", environment, dir);
      return new ResponseEntity<>(System.getProperty("user.dir"), HttpStatus.OK);
    }
    catch (IOException e) {
      StringWriter writer = new StringWriter();
      e.printStackTrace(new PrintWriter(writer));
      return new ResponseEntity<>(writer.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}

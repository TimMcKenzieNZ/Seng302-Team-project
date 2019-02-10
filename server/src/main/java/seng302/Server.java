package seng302;


import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.authentication.AuthenticationProvider;
import seng302.model.database.DBCConnection;
import seng302.model.security.AuthenticationProviderImpl;

@SpringBootApplication
public class Server {

  public static void main(String[] args) throws Exception {
    DBCConnection.setTestDatabase(false);
//  DBCConnection.setTestDatabase(true);
    SpringApplication.run(Server.class, args);
  }

  @Bean
  public AuthenticationProvider initializeAuthenticationProvider() {
    return new AuthenticationProviderImpl();
  }

  @Bean
  public TaskScheduler taskScheduler() {
    //org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler
    return new ThreadPoolTaskScheduler();
  }

}



package seng302.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ResponseBody;
import seng302.model.database.LoginService;
import seng302.model.security.*;

import java.sql.SQLException;


@APIController
public class LoginController extends BaseController {

    private LoginService loginService;
    private final AuthenticationTokenStore authenticationTokenStore;

    @Autowired
    public LoginController(AuthenticationTokenStore authenticationTokenStore) {
        this.authenticationTokenStore = authenticationTokenStore;
        this.loginService = new LoginService();

    }


    @PostMapping(value = "login", headers="Accept=application/json")
    @ResponseBody
    public ResponseEntity login(@RequestBody LoginAttempt loginAttempt) {
        LoginResult loginResult;
        try{
             loginResult = loginService.login(loginAttempt, dbcConnection.getConnection(), authenticationTokenStore);
        } catch (SQLException e){
            return new ResponseEntity<>("Invalid username and password supplied.",HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(loginResult, HttpStatus.OK);
    }


    @AnyDonorCredentials
    @PostMapping(value = "logout")
    @ResponseBody
    public ResponseEntity logout(@RequestHeader(value="x-auth-token") String token){
        if(loginService.logout(token, authenticationTokenStore)){
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}

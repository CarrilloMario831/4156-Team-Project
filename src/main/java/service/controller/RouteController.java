package service.controller;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import service.handler.UsersTableSqlHelper;
import service.models.User;
import service.util.UserRoles;

/** This class contains all the API routes for the service. */
@RestController
public class RouteController {

  @Autowired public UsersTableSqlHelper usersTableSqlHelper;

  @PostMapping(value = "/create-user", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> createUser(@RequestParam(value = "username") String username) {
    try {
      User newUser =
          User.builder().userId(UUID.randomUUID()).username(username).role(UserRoles.USER).build();
      usersTableSqlHelper.insert(newUser);
      return new ResponseEntity<>(newUser.toString(), HttpStatus.OK);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}

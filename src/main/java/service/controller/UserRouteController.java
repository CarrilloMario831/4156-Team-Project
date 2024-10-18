package service.controller;

import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.handler.UsersTableSqlHelper;
import service.models.User;
import service.util.UserRoles;

/** This class contains all the API endpoints for user-related requests. */
@RestController
@RequestMapping("/api/users")
public class UserRouteController {

  @Autowired public UsersTableSqlHelper usersTableSqlHelper;

  @PostMapping(value = "/createUser", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> createUser(@RequestParam(value = "username") String username) {
    try {
      User newUser =
          User.builder()
              .userId(UUID.randomUUID())
              .username(username)
              .role(UserRoles.USER)
              .lastAccess(LocalDateTime.now())
              .build();
      usersTableSqlHelper.insert(newUser);
      return new ResponseEntity<>(newUser.toString(), HttpStatus.OK);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @PatchMapping(value = "/updateUsername", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> updateUsername(
      @RequestParam(value = "userID") String userID,
      @RequestParam(value = "newUsername") String newUsername) {
    try {

      if (newUsername == null || newUsername.isEmpty()) {
        return new ResponseEntity<>("New username cannot be empty", HttpStatus.BAD_REQUEST);
      }

      User user = usersTableSqlHelper.select(userID);

      if (user == null) {
        return new ResponseEntity<>(
            "User with userID: " + userID + " was not found", HttpStatus.NOT_FOUND);
      }
      String oldUsername = user.getUsername();

      if (oldUsername.equals(newUsername)) {
        return new ResponseEntity<>("Username is already " + oldUsername, HttpStatus.BAD_REQUEST);
      }
      boolean updateSuccess = usersTableSqlHelper.update(userID, newUsername);

      if (!updateSuccess) {
        return new ResponseEntity<>(
            "User with \nuserID: "
                + userID
                + " \nusername: "
                + oldUsername
                + "\ncould not be updated.",
            HttpStatus.INTERNAL_SERVER_ERROR);
      }

      return new ResponseEntity<>(
          "Username was successfully changed from: \n" + oldUsername + " --> " + newUsername,
          HttpStatus.OK);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}

package service.controller;

import static service.util.DateTimeUtils.FORMATTER;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import service.handler.UsersTableSqlHelper;
import service.models.User;
import service.util.UserRoles;

/** This class contains all the API endpoints for user-related requests. */
@RestController
@RequestMapping("/api/users")
public class UserRouteController {

  /** The Users table sql helper. */
  @Autowired public UsersTableSqlHelper usersTableSqlHelper;

  /**
   * Gets username.
   *
   * @param userId the user id
   * @return the username
   */
  @GetMapping(value = "/getUsername", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getUsername(@RequestParam(value = "userId") String userId) {
    if (userId == null || userId.isEmpty()) {
      return new ResponseEntity<>("userId needed to get username.", HttpStatus.BAD_REQUEST);
    }
    try {
      User user = usersTableSqlHelper.getUserWithUserId(userId);
      if (user == null) {
        return new ResponseEntity<>(
            "User with userId: " + userId + " was not found", HttpStatus.NOT_FOUND);
      }
      String username = user.getUsername();
      if (username == null || username.isEmpty()) {
        return new ResponseEntity<>("User " + userId + " has no username.", HttpStatus.NO_CONTENT);
      }
      return new ResponseEntity<>(username, HttpStatus.OK);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Gets role.
   *
   * @param userId the user id
   * @return the role
   */
  @GetMapping(value = "/getRole", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getRole(@RequestParam(value = "userId") String userId) {
    if (userId == null || userId.isEmpty()) {
      return new ResponseEntity<>("userId needed to get role.", HttpStatus.BAD_REQUEST);
    }
    try {
      User user = usersTableSqlHelper.getUserWithUserId(userId);
      if (user == null) {
        return new ResponseEntity<>(
            "User with userId: " + userId + " was not found", HttpStatus.NOT_FOUND);
      }
      String username = user.getUsername();
      Enum<UserRoles> userRole = user.getRole();
      if (userRole == null) {
        return new ResponseEntity<>(
            "User " + username + " has no role assigned.", HttpStatus.NO_CONTENT);
      }
      return new ResponseEntity<>(userRole.toString(), HttpStatus.OK);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Gets last access.
   *
   * @param userId the user id
   * @return the last access
   */
  @GetMapping(value = "/getLastAccess", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getLastAccess(@RequestParam(value = "userId") String userId) {
    if (userId == null || userId.isEmpty()) {
      return new ResponseEntity<>(
          "userId needed to get time of last access.", HttpStatus.BAD_REQUEST);
    }
    try {
      User user = usersTableSqlHelper.getUserWithUserId(userId);
      if (user == null) {
        return new ResponseEntity<>(
            "User with userId: " + userId + " was not found", HttpStatus.NOT_FOUND);
      }
      LocalDateTime lastAccess = user.getLastAccess();
      String username = user.getUsername();
      if (lastAccess == null) {
        return new ResponseEntity<>(
            "User " + username + " has not accessed the service yet.", HttpStatus.NO_CONTENT);
      }
      return new ResponseEntity<>(lastAccess.format(FORMATTER), HttpStatus.OK);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Create user response entity.
   *
   * @param username the username
   * @return the response entity
   */
  @PostMapping(value = "/createUser", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> createUser(@RequestParam(value = "username") String username) {
    if (username == null || username.isEmpty()) {
      return new ResponseEntity<>("Username needed to create user.", HttpStatus.BAD_REQUEST);
    }
    try {
      if (usersTableSqlHelper.getUserWithUsername(username) != null) {
        return new ResponseEntity<>(
            "Username " + username + " already taken. Try a different username.",
            HttpStatus.CONFLICT);
      }
      User newUser =
          User.builder()
              .userId(UUID.randomUUID())
              .username(username)
              .role(UserRoles.USER)
              .lastAccess(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
              .build();
      usersTableSqlHelper.insertUser(newUser);
      return new ResponseEntity<>(
          username + " was successfully created. \n UserID: " + newUser.getUserId(), HttpStatus.OK);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Update username response entity.
   *
   * @param userId the user id
   * @param currentUsername the current username
   * @param newUsername the new username
   * @return the response entity
   */
  @PatchMapping(value = "/updateUsername", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> updateUsername(
      @RequestParam(value = "userId") String userId,
      @RequestParam(value = "currentUsername") String currentUsername,
      @RequestParam(value = "newUsername") String newUsername) {

    if (userId == null || userId.isEmpty()) {
      return new ResponseEntity<>("UserID cannot be empty.", HttpStatus.BAD_REQUEST);
    }
    if (currentUsername == null || currentUsername.isEmpty()) {
      return new ResponseEntity<>("Current username cannot be empty.", HttpStatus.BAD_REQUEST);
    }
    if (newUsername == null || newUsername.isEmpty()) {
      return new ResponseEntity<>("New username cannot be empty.", HttpStatus.BAD_REQUEST);
    }
    if (currentUsername.equals(newUsername)) {
      return new ResponseEntity<>(
          "New username is the same as current username. Please enter a different username.",
          HttpStatus.BAD_REQUEST);
    }
    try {
      if (usersTableSqlHelper.getUserWithUsername(newUsername) != null) {
        return new ResponseEntity<>(
            "Username " + newUsername + " already taken. \nTry a different username.",
            HttpStatus.CONFLICT);
      }
      User user = usersTableSqlHelper.getUserWithUserId(userId);

      if (user == null) {
        return new ResponseEntity<>(
            "User with userId: " + userId + " was not found", HttpStatus.NOT_FOUND);
      }

      if (!user.getUsername().equals(currentUsername)) {
        return new ResponseEntity<>(
            "Current username is wrong for userID: "
                + userId
                + "\nPlease enter the correct current username.",
            HttpStatus.BAD_REQUEST);
      }

      boolean updateSuccess = usersTableSqlHelper.updateUsername(userId, newUsername);

      if (!updateSuccess) {
        return new ResponseEntity<>(
            "User with \nuserId: "
                + userId
                + " \nusername: "
                + currentUsername
                + "\ncould not be updated.",
            HttpStatus.INTERNAL_SERVER_ERROR);
      }

      return new ResponseEntity<>(
          "Username for userID: "
              + userId
              + " successfully changed from: \n"
              + currentUsername
              + " --> "
              + newUsername,
          HttpStatus.OK);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Update role response entity.
   *
   * @param userId the user id
   * @param newRole the new role
   * @return the response entity
   */
  @PatchMapping(value = "/updateRole", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> updateRole(
      @RequestParam(value = "userId") String userId,
      @RequestParam(value = "newRole") String newRole) {
    if (userId == null || userId.isEmpty()) {
      return new ResponseEntity<>("UserID cannot be empty.", HttpStatus.BAD_REQUEST);
    }

    if (newRole == null
        || newRole.isEmpty()
        || !(newRole.equals(UserRoles.ADMIN.toString())
            || newRole.equals(UserRoles.USER.toString()))) {
      return new ResponseEntity<>(
          "Role is invalid. Must be ADMIN or USER.", HttpStatus.BAD_REQUEST);
    }
    try {
      User user = usersTableSqlHelper.getUserWithUserId(userId);
      if (user == null) {
        return new ResponseEntity<>(
            "User with userId: " + userId + " was not found.", HttpStatus.NOT_FOUND);
      }
      UserRoles oldRole = user.getRole();
      String username = user.getUsername();
      if (oldRole.toString().equals(newRole)) {
        return new ResponseEntity<>(
            "User " + username + " is already " + newRole, HttpStatus.BAD_REQUEST);
      }

      boolean updateSuccess = usersTableSqlHelper.updateRole(userId, newRole);

      if (!updateSuccess) {
        return new ResponseEntity<>(
            "User with \nuserId: "
                + userId
                + " \nusername: "
                + user.getUsername()
                + "\ncould not be updated.",
            HttpStatus.INTERNAL_SERVER_ERROR);
      }

      return new ResponseEntity<>(
          "Role of "
              + username
              + " was successfully changed from: \n"
              + oldRole
              + " --> "
              + newRole,
          HttpStatus.OK);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}

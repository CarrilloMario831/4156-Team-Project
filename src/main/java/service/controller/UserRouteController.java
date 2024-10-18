package service.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

  @Autowired public UsersTableSqlHelper usersTableSqlHelper;
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

  /** Sample javadoc to pass checkstyle. */
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
        return new ResponseEntity<>("User " + userId + " has no username. ", HttpStatus.NO_CONTENT);
      }
      return new ResponseEntity<>(username, HttpStatus.OK);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /** Sample javadoc to pass checkstyle. */
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
            "User " + username + " has no role assigned. ", HttpStatus.NO_CONTENT);
      }
      return new ResponseEntity<>(userRole.toString(), HttpStatus.OK);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /** Sample javadoc to pass checkstyle. */
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
            "User " + username + " has not accessed the service yet. ", HttpStatus.NO_CONTENT);
      }
      return new ResponseEntity<>(lastAccess.format(formatter), HttpStatus.OK);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /** Sample javadoc to pass checkstyle. */
  @PostMapping(value = "/createUser", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> createUser(@RequestParam(value = "username") String username) {
    if (username == null || username.isEmpty()) {
      return new ResponseEntity<>("Username needed to create user.", HttpStatus.BAD_REQUEST);
    }
    try {
      if (usersTableSqlHelper.getUserWithUsername(username) != null) {
        return new ResponseEntity<>(
            "Username " + username + "already taken. Try a different username.",
            HttpStatus.CONFLICT);
      }
      User newUser =
          User.builder()
              .userId(UUID.randomUUID())
              .username(username)
              .role(UserRoles.USER)
              .lastAccess(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
              .build();
      usersTableSqlHelper.insert(newUser);
      return new ResponseEntity<>(
          username + " was successfully created. \n UserID: " + newUser.getUserId(), HttpStatus.OK);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /** Sample javadoc to pass checkstyle. */
  @PatchMapping(value = "/updateUsername", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> updateUsername(
      @RequestParam(value = "userId") String userId,
      @RequestParam(value = "newUsername") String newUsername) {

    // TODO: Add oldUsername as a param and check if the userId and username match

    if (newUsername == null || newUsername.isEmpty()) {
      return new ResponseEntity<>("New username cannot be empty", HttpStatus.BAD_REQUEST);
    }
    try {
      if (usersTableSqlHelper.getUserWithUserId(userId) != null) {
        return new ResponseEntity<>(
            "Username " + newUsername + "already taken. Try a different username.",
            HttpStatus.CONFLICT);
      }
      User user = usersTableSqlHelper.getUserWithUserId(userId);

      if (user == null) {
        return new ResponseEntity<>(
            "User with userId: " + userId + " was not found", HttpStatus.NOT_FOUND);
      }
      String oldUsername = user.getUsername();

      if (oldUsername.equals(newUsername)) {
        return new ResponseEntity<>("Username is already " + oldUsername, HttpStatus.BAD_REQUEST);
      }
      boolean updateSuccess = usersTableSqlHelper.updateUsername(userId, newUsername);

      if (!updateSuccess) {
        return new ResponseEntity<>(
            "User with \nuserId: "
                + userId
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

  /** Sample javadoc to pass checkstyle. */
  @PatchMapping(value = "/updateRole", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> updateRole(
      @RequestParam(value = "userId") String userId,
      @RequestParam(value = "newRole") String newRole) {
    try {

      if (newRole == null
          || !(newRole.equals(UserRoles.ADMIN.toString())
              || newRole.equals(UserRoles.USER.toString()))) {
        return new ResponseEntity<>(
            "Role is invalid. Must be ADMIN or USER.", HttpStatus.BAD_REQUEST);
      }
      User user = usersTableSqlHelper.getUserWithUserId(userId);

      if (user == null) {
        return new ResponseEntity<>(
            "User with userId: " + userId + " was not found", HttpStatus.NOT_FOUND);
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

package service.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static service.util.DateTimeUtils.FORMATTER;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import service.handler.UsersTableSqlHelper;
import service.models.User;
import service.util.UserRoles;

/** Unit tests for the UserRouteController class. */
@SpringBootTest
public class UserRouteControllerTests {

  @InjectMocks private UserRouteController userRouteController;

  @Mock private UsersTableSqlHelper usersTableSqlHelper;

  private final LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

  private User testUser;

  /** Sample javadoc to pass checkstyle. */
  @BeforeEach
  public void setup() {
    testUser =
        User.builder()
            .username("sjimenez814")
            .userId(UUID.fromString("9cdd2cec-d003-4964-b55c-cb336c51b809"))
            .lastAccess(now)
            .role(UserRoles.USER)
            .build();
  }

  @Test
  public void testGetUsernameRoute() {
    String expectedUsername = testUser.getUsername();
    String userId = testUser.getUserId().toString();

    // Test Success
    when(usersTableSqlHelper.getUserWithUserId(userId)).thenReturn(testUser);
    ResponseEntity<String> getUsernameResponse = userRouteController.getUsername(userId);
    assertEquals(expectedUsername, getUsernameResponse.getBody());
    assertEquals(HttpStatus.OK, getUsernameResponse.getStatusCode());

    // Test null userId
    getUsernameResponse = userRouteController.getUsername(null);
    assertEquals("userId needed to get username.", getUsernameResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, getUsernameResponse.getStatusCode());

    // Test empty userId
    getUsernameResponse = userRouteController.getUsername("");
    assertEquals("userId needed to get username.", getUsernameResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, getUsernameResponse.getStatusCode());

    // Test null username
    testUser.setUsername(null);
    getUsernameResponse = userRouteController.getUsername(userId);
    assertEquals(
        "User 9cdd2cec-d003-4964-b55c-cb336c51b809 has no username.",
        getUsernameResponse.getBody());
    assertEquals(HttpStatus.NO_CONTENT, getUsernameResponse.getStatusCode());

    // Test empty username
    testUser.setUsername("");
    getUsernameResponse = userRouteController.getUsername(userId);
    assertEquals(
        "User 9cdd2cec-d003-4964-b55c-cb336c51b809 has no username.",
        getUsernameResponse.getBody());
    assertEquals(HttpStatus.NO_CONTENT, getUsernameResponse.getStatusCode());

    // Test user not found
    when(usersTableSqlHelper.getUserWithUserId(userId)).thenReturn(null);
    getUsernameResponse = userRouteController.getUsername(userId);
    assertEquals(
        "User with userId: 9cdd2cec-d003-4964-b55c-cb336c51b809 was not found",
        getUsernameResponse.getBody());
    assertEquals(HttpStatus.NOT_FOUND, getUsernameResponse.getStatusCode());

    // Test Internal Error
    when(usersTableSqlHelper.getUserWithUserId(userId)).thenThrow(RuntimeException.class);
    getUsernameResponse = userRouteController.getUsername(userId);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, getUsernameResponse.getStatusCode());
  }

  @Test
  public void testGetRole() {
    UserRoles expectedRole = testUser.getRole();
    String userId = testUser.getUserId().toString();

    // Test Success
    when(usersTableSqlHelper.getUserWithUserId(userId)).thenReturn(testUser);
    ResponseEntity<String> getRoleResponse = userRouteController.getRole(userId);
    assertEquals(expectedRole.toString(), getRoleResponse.getBody());
    assertEquals(HttpStatus.OK, getRoleResponse.getStatusCode());

    // Test null userId
    getRoleResponse = userRouteController.getRole(null);
    assertEquals("userId needed to get role.", getRoleResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, getRoleResponse.getStatusCode());

    // Test empty userId
    getRoleResponse = userRouteController.getRole("");
    assertEquals("userId needed to get role.", getRoleResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, getRoleResponse.getStatusCode());

    // Test null userRole
    testUser.setRole(null);
    getRoleResponse = userRouteController.getRole(userId);
    assertEquals(
        "User " + testUser.getUsername() + " has no role assigned.", getRoleResponse.getBody());
    assertEquals(HttpStatus.NO_CONTENT, getRoleResponse.getStatusCode());

    // Test user not found
    when(usersTableSqlHelper.getUserWithUserId(userId)).thenReturn(null);
    getRoleResponse = userRouteController.getRole(userId);
    assertEquals(
        "User with userId: " + testUser.getUserId() + " was not found", getRoleResponse.getBody());
    assertEquals(HttpStatus.NOT_FOUND, getRoleResponse.getStatusCode());

    // Test Internal Error
    when(usersTableSqlHelper.getUserWithUserId(userId)).thenThrow(RuntimeException.class);
    getRoleResponse = userRouteController.getRole(userId);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, getRoleResponse.getStatusCode());
  }

  @Test
  public void testGetLastAccess() {
    String expectedLastAccess = testUser.getLastAccess().format(FORMATTER);
    String userId = testUser.getUserId().toString();

    // Test Success
    when(usersTableSqlHelper.getUserWithUserId(userId)).thenReturn(testUser);
    ResponseEntity<String> getLastAccessResponse = userRouteController.getLastAccess(userId);
    assertEquals(expectedLastAccess, getLastAccessResponse.getBody());
    assertEquals(HttpStatus.OK, getLastAccessResponse.getStatusCode());

    // Test null userId
    getLastAccessResponse = userRouteController.getLastAccess(null);
    assertEquals("userId needed to get time of last access.", getLastAccessResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, getLastAccessResponse.getStatusCode());

    // Test empty userId
    getLastAccessResponse = userRouteController.getLastAccess("");
    assertEquals("userId needed to get time of last access.", getLastAccessResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, getLastAccessResponse.getStatusCode());

    // Test null lastAccess
    testUser.setLastAccess(null);
    getLastAccessResponse = userRouteController.getLastAccess(userId);
    assertEquals(
        "User " + testUser.getUsername() + " has not accessed the service yet.",
        getLastAccessResponse.getBody());
    assertEquals(HttpStatus.NO_CONTENT, getLastAccessResponse.getStatusCode());

    // Test user not found
    when(usersTableSqlHelper.getUserWithUserId(userId)).thenReturn(null);
    getLastAccessResponse = userRouteController.getLastAccess(userId);
    assertEquals(
        "User with userId: " + testUser.getUserId() + " was not found",
        getLastAccessResponse.getBody());
    assertEquals(HttpStatus.NOT_FOUND, getLastAccessResponse.getStatusCode());

    // Test Internal Error
    when(usersTableSqlHelper.getUserWithUserId(userId)).thenThrow(RuntimeException.class);
    getLastAccessResponse = userRouteController.getLastAccess(userId);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, getLastAccessResponse.getStatusCode());
  }

  @Test
  public void testCreateUser() {
    String testUsername = "user1";

    // Test Success
    when(usersTableSqlHelper.getUserWithUsername(any())).thenReturn(null);
    when(usersTableSqlHelper.insertUser(any())).thenReturn(true);
    ResponseEntity<?> createUserResponse = userRouteController.createUser(testUsername);
    assertEquals(HttpStatus.OK, createUserResponse.getStatusCode());

    // Test null username
    createUserResponse = userRouteController.createUser(null);
    assertEquals("Username needed to create user.", createUserResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, createUserResponse.getStatusCode());

    // Test empty username
    createUserResponse = userRouteController.createUser("");
    assertEquals("Username needed to create user.", createUserResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, createUserResponse.getStatusCode());

    // Test taken username
    when(usersTableSqlHelper.getUserWithUsername(any())).thenReturn(testUser);
    createUserResponse = userRouteController.createUser(testUsername);
    assertEquals(
        "Username user1 already taken. Try a different username.", createUserResponse.getBody());
    assertEquals(HttpStatus.CONFLICT, createUserResponse.getStatusCode());

    // Test Internal Error
    when(usersTableSqlHelper.getUserWithUsername(any())).thenReturn(null);
    doThrow(new RuntimeException()).when(usersTableSqlHelper).insertUser(any());
    createUserResponse = userRouteController.createUser(testUsername);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, createUserResponse.getStatusCode());
  }

  @Test
  public void testUpdateUsername() {
    // Test null and empty fields
    ResponseEntity<?> updateUsernameResponse = userRouteController.updateUsername(null, "", "");
    assertEquals("UserID cannot be empty.", updateUsernameResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, updateUsernameResponse.getStatusCode());
    updateUsernameResponse = userRouteController.updateUsername("", "", "");
    assertEquals("UserID cannot be empty.", updateUsernameResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, updateUsernameResponse.getStatusCode());
    updateUsernameResponse =
        userRouteController.updateUsername(String.valueOf(testUser.getUserId()), "", "");
    assertEquals("Current username cannot be empty.", updateUsernameResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, updateUsernameResponse.getStatusCode());
    updateUsernameResponse =
        userRouteController.updateUsername(String.valueOf(testUser.getUserId()), null, "");
    assertEquals("Current username cannot be empty.", updateUsernameResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, updateUsernameResponse.getStatusCode());
    updateUsernameResponse =
        userRouteController.updateUsername(
            String.valueOf(testUser.getUserId()), testUser.getUsername(), "");
    assertEquals("New username cannot be empty.", updateUsernameResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, updateUsernameResponse.getStatusCode());
    updateUsernameResponse =
        userRouteController.updateUsername(
            String.valueOf(testUser.getUserId()), testUser.getUsername(), null);
    assertEquals("New username cannot be empty.", updateUsernameResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, updateUsernameResponse.getStatusCode());

    // Test current and new username same
    updateUsernameResponse =
        userRouteController.updateUsername(
            String.valueOf(testUser.getUserId()), testUser.getUsername(), testUser.getUsername());
    assertEquals(
        "New username is the same as current username. Please enter a different username.",
        updateUsernameResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, updateUsernameResponse.getStatusCode());

    // Test taken username
    String newUsername = "iloveswe23";
    when(usersTableSqlHelper.getUserWithUsername(any())).thenReturn(testUser);
    updateUsernameResponse =
        userRouteController.updateUsername(
            String.valueOf(testUser.getUserId()), testUser.getUsername(), newUsername);
    assertEquals(
        "Username " + newUsername + " already taken. \nTry a different username.",
        updateUsernameResponse.getBody());
    assertEquals(HttpStatus.CONFLICT, updateUsernameResponse.getStatusCode());

    // Test User not found
    when(usersTableSqlHelper.getUserWithUsername(any())).thenReturn(null);
    when(usersTableSqlHelper.getUserWithUserId(any())).thenReturn(null);
    updateUsernameResponse =
        userRouteController.updateUsername(
            String.valueOf(testUser.getUserId()), testUser.getUsername(), newUsername);
    assertEquals(
        "User with userId: " + testUser.getUserId() + " was not found",
        updateUsernameResponse.getBody());
    assertEquals(HttpStatus.NOT_FOUND, updateUsernameResponse.getStatusCode());

    // Test current username wrong
    when(usersTableSqlHelper.getUserWithUserId(any())).thenReturn(testUser);
    updateUsernameResponse =
        userRouteController.updateUsername(
            String.valueOf(testUser.getUserId()), "wrongUsername", newUsername);
    assertEquals(
        "Current username is wrong for userID: "
            + testUser.getUserId()
            + "\nPlease enter the correct current username.",
        updateUsernameResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, updateUsernameResponse.getStatusCode());

    // Test unsuccessful update
    when(usersTableSqlHelper.updateUsername(anyString(), anyString())).thenReturn(false);
    updateUsernameResponse =
        userRouteController.updateUsername(
            String.valueOf(testUser.getUserId()), testUser.getUsername(), newUsername);
    assertEquals(
        "User with \nuserId: "
            + testUser.getUserId()
            + " \nusername: "
            + testUser.getUsername()
            + "\ncould not be updated.",
        updateUsernameResponse.getBody());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, updateUsernameResponse.getStatusCode());

    // Test successful update
    when(usersTableSqlHelper.updateUsername(anyString(), anyString())).thenReturn(true);
    updateUsernameResponse =
        userRouteController.updateUsername(
            String.valueOf(testUser.getUserId()), testUser.getUsername(), newUsername);
    assertEquals(
        "Username for userID: "
            + testUser.getUserId()
            + " successfully changed from: \n"
            + testUser.getUsername()
            + " --> "
            + newUsername,
        updateUsernameResponse.getBody());
    assertEquals(HttpStatus.OK, updateUsernameResponse.getStatusCode());

    // Test internal error
    when(usersTableSqlHelper.updateUsername(anyString(), anyString()))
        .thenThrow(RuntimeException.class);
    updateUsernameResponse =
        userRouteController.updateUsername(
            String.valueOf(testUser.getUserId()), testUser.getUsername(), newUsername);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, updateUsernameResponse.getStatusCode());
  }

  @Test
  public void updateRole() {

    // Test null and empty fields
    ResponseEntity<?> updateRoleResponse = userRouteController.updateRole(null, "");
    assertEquals("UserID cannot be empty.", updateRoleResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, updateRoleResponse.getStatusCode());
    updateRoleResponse = userRouteController.updateRole("", "");
    assertEquals("UserID cannot be empty.", updateRoleResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, updateRoleResponse.getStatusCode());
    updateRoleResponse = userRouteController.updateRole(String.valueOf(testUser.getUserId()), null);
    assertEquals("Role is invalid. Must be ADMIN or USER.", updateRoleResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, updateRoleResponse.getStatusCode());
    updateRoleResponse = userRouteController.updateRole(String.valueOf(testUser.getUserId()), "");
    assertEquals("Role is invalid. Must be ADMIN or USER.", updateRoleResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, updateRoleResponse.getStatusCode());

    // Test a role that is neither ADMIN nor USER
    updateRoleResponse =
        userRouteController.updateRole(String.valueOf(testUser.getUserId()), "invalid");
    assertEquals("Role is invalid. Must be ADMIN or USER.", updateRoleResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, updateRoleResponse.getStatusCode());

    // Test user not found
    when(usersTableSqlHelper.getUserWithUserId(any())).thenReturn(null);
    updateRoleResponse =
        userRouteController.updateRole(
            String.valueOf(testUser.getUserId()), UserRoles.ADMIN.toString());
    assertEquals(
        "User with userId: " + testUser.getUserId() + " was not found.",
        updateRoleResponse.getBody());
    assertEquals(HttpStatus.NOT_FOUND, updateRoleResponse.getStatusCode());

    // Test same role
    when(usersTableSqlHelper.getUserWithUserId(any())).thenReturn(testUser);
    updateRoleResponse =
        userRouteController.updateRole(
            String.valueOf(testUser.getUserId()), testUser.getRole().toString());
    assertEquals(
        "User " + testUser.getUsername() + " is already " + testUser.getRole(),
        updateRoleResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, updateRoleResponse.getStatusCode());

    // Test unsuccessful udpate
    when(usersTableSqlHelper.updateRole(anyString(), anyString())).thenReturn(false);
    updateRoleResponse =
        userRouteController.updateRole(String.valueOf(testUser.getUserId()), "ADMIN");
    assertEquals(
        "User with \nuserId: "
            + testUser.getUserId()
            + " \nusername: "
            + testUser.getUsername()
            + "\ncould not be updated.",
        updateRoleResponse.getBody());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, updateRoleResponse.getStatusCode());

    // Test successful update
    when(usersTableSqlHelper.updateRole(anyString(), anyString())).thenReturn(true);
    updateRoleResponse =
        userRouteController.updateRole(String.valueOf(testUser.getUserId()), "ADMIN");
    assertEquals(
        "Role of "
            + testUser.getUsername()
            + " was successfully changed from: \n"
            + testUser.getRole()
            + " --> "
            + UserRoles.ADMIN,
        updateRoleResponse.getBody());
    assertEquals(HttpStatus.OK, updateRoleResponse.getStatusCode());

    // Test internal error
    when(usersTableSqlHelper.updateRole(anyString(), anyString()))
        .thenThrow(RuntimeException.class);
    updateRoleResponse =
        userRouteController.updateRole(String.valueOf(testUser.getUserId()), "ADMIN");
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, updateRoleResponse.getStatusCode());
  }
}

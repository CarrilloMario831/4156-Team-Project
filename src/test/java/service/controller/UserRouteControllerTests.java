package service.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
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
    doNothing().when(usersTableSqlHelper).insert(any());
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
        "Username user1already taken. Try a different username.", createUserResponse.getBody());
    assertEquals(HttpStatus.CONFLICT, createUserResponse.getStatusCode());

    // Test Internal Error
    when(usersTableSqlHelper.getUserWithUsername(any())).thenReturn(null);
    doThrow(new RuntimeException()).when(usersTableSqlHelper).insert(any());
    createUserResponse = userRouteController.createUser(testUsername);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, createUserResponse.getStatusCode());
  }
}

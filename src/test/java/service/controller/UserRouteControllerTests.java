package service.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
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

  @Test
  public void testGetUsernameRoute() {
    String expectedUsername = "sjimenez81402";
    String userId = "9cdd2cec-d003-4964-b55c-cb336c51b809";
    User expectedUser =
        User.builder()
            .username(expectedUsername)
            .userId(UUID.fromString(userId))
            .lastAccess(now)
            .role(UserRoles.USER)
            .build();

    // Test Success
    when(usersTableSqlHelper.getUserWithUserId(userId)).thenReturn(expectedUser);
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
    expectedUser.setUsername(null);
    getUsernameResponse = userRouteController.getUsername(userId);
    assertEquals(
        "User 9cdd2cec-d003-4964-b55c-cb336c51b809 has no username.",
        getUsernameResponse.getBody());
    assertEquals(HttpStatus.NO_CONTENT, getUsernameResponse.getStatusCode());

    // Test empty username
    expectedUser.setUsername("");
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
}

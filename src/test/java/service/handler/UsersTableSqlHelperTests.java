package service.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import service.models.User;
import service.util.UserRoles;

/** Unit tests for the UsersTableSqlHelper class. */
@SpringBootTest
public class UsersTableSqlHelperTests {

  @Mock private JdbcTemplate jdbcTemplate;

  @InjectMocks private UsersTableSqlHelper usersTableSqlHelper;

  private final LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

  private User testUser;

  /** Sets up a test user before each test. */
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

  /** Tests inserting a user into the database. */
  @Test
  public void testInsertUser() {
    // Test successful insert
    when(jdbcTemplate.update(any(), any(), any(), any(), any())).thenReturn(1);
    assertTrue(
        usersTableSqlHelper.insertUser(testUser), "Insert should return true when successful.");

    // Test unsuccessful insert
    when(jdbcTemplate.update(any(), any(), any(), any(), any())).thenReturn(0);
    assertFalse(
        usersTableSqlHelper.insertUser(testUser),
        "Insert should return false when no rows are affected.");

    // Test exception thrown
    when(jdbcTemplate.update(any(), any(), any(), any(), any())).thenThrow(RuntimeException.class);
    assertThrows(
        RuntimeException.class,
        () -> usersTableSqlHelper.insertUser(testUser),
        "Insert should propagate exceptions.");
  }

  /** Tests retrieving all users from the database. */
  @Test
  public void testGetAllUsers() {
    // Test successful retrieval
    ArrayList<User> users = new ArrayList<>();
    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(users);
    assertEquals(users, usersTableSqlHelper.getAllUsers(), "Should return the list of all users.");

    // Test exception thrown
    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenThrow(RuntimeException.class);
    assertThrows(
        RuntimeException.class,
        () -> usersTableSqlHelper.getAllUsers(),
        "Should propagate exceptions.");
  }

  /** Tests retrieving a user by user ID. */
  @Test
  public void testGetUserWithUserId() {
    // Test empty list
    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(new ArrayList<>());
    assertEquals(
        null,
        usersTableSqlHelper.getUserWithUserId(testUser.getUserId().toString()),
        "Should return null when user is not found.");

    // Test more than one user
    ArrayList<User> users = new ArrayList<>();
    users.add(testUser);
    users.add(testUser);
    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(users);
    assertThrows(
        IllegalStateException.class,
        () -> usersTableSqlHelper.getUserWithUserId(testUser.getUserId().toString()),
        "Should throw IllegalStateException when multiple users are found.");

    // Test successful retrieval
    users.remove(0);
    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(users);
    User actualUser = usersTableSqlHelper.getUserWithUserId(testUser.getUserId().toString());
    assertEquals(testUser, actualUser, "Should return the correct user.");
  }

  /** Tests retrieving a user by username. */
  @Test
  public void testGetUserWithUsername() {
    // Test empty list
    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(new ArrayList<>());
    assertEquals(
        null,
        usersTableSqlHelper.getUserWithUsername(testUser.getUsername()),
        "Should return null when user is not found.");

    // Test more than one user
    ArrayList<User> users = new ArrayList<>();
    users.add(testUser);
    users.add(testUser);
    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(users);
    assertThrows(
        IllegalStateException.class,
        () -> usersTableSqlHelper.getUserWithUsername(testUser.getUsername()),
        "Should throw IllegalStateException when multiple users are found.");

    // Test successful retrieval
    users.remove(0);
    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(users);
    User actualUser = usersTableSqlHelper.getUserWithUsername(testUser.getUsername());
    assertEquals(testUser, actualUser, "Should return the correct user.");
  }

  /** Tests updating a user's username. */
  @Test
  public void testUpdateUsername() {
    // Test successful update
    when(jdbcTemplate.update(any(), anyString(), anyString())).thenReturn(1);
    assertTrue(
        usersTableSqlHelper.updateUsername(testUser.getUserId().toString(), testUser.getUsername()),
        "Update should return true when successful.");

    // Test unsuccessful update
    when(jdbcTemplate.update(any(), anyString(), anyString())).thenReturn(0);
    assertFalse(
        usersTableSqlHelper.updateUsername(testUser.getUserId().toString(), testUser.getUsername()),
        "Update should return false when no rows are affected.");

    // Test exception thrown
    when(jdbcTemplate.update(any(), anyString(), anyString())).thenThrow(RuntimeException.class);
    assertThrows(
        RuntimeException.class,
        () ->
            usersTableSqlHelper.updateUsername(
                testUser.getUserId().toString(), testUser.getUsername()),
        "Update should propagate exceptions.");
  }

  /** Tests updating a user's role. */
  @Test
  public void testUpdateRole() {
    // Test successful update
    when(jdbcTemplate.update(any(), anyString(), anyString())).thenReturn(1);
    assertTrue(
        usersTableSqlHelper.updateRole(
            testUser.getUserId().toString(), testUser.getRole().toString()),
        "Update should return true when successful.");

    // Test unsuccessful update
    when(jdbcTemplate.update(any(), anyString(), anyString())).thenReturn(0);
    assertFalse(
        usersTableSqlHelper.updateRole(
            testUser.getUserId().toString(), testUser.getRole().toString()),
        "Update should return false when no rows are affected.");

    // Test exception thrown
    when(jdbcTemplate.update(any(), anyString(), anyString())).thenThrow(RuntimeException.class);
    assertThrows(
        RuntimeException.class,
        () ->
            usersTableSqlHelper.updateRole(
                testUser.getUserId().toString(), testUser.getRole().toString()),
        "Update should propagate exceptions.");
  }

  /** Tests updating a user's last access time. */
  @Test
  public void testUpdateLastAccess() {
    // Test successful update
    when(jdbcTemplate.update(any(), any(), anyString())).thenReturn(1);
    assertTrue(
        usersTableSqlHelper.updateLastAccess(
            testUser.getUserId().toString(), testUser.getLastAccess()),
        "Update should return true when successful.");

    // Test unsuccessful update
    when(jdbcTemplate.update(any(), any(), anyString())).thenReturn(0);
    assertFalse(
        usersTableSqlHelper.updateLastAccess(
            testUser.getUserId().toString(), testUser.getLastAccess()),
        "Update should return false when no rows are affected.");

    // Test exception thrown
    when(jdbcTemplate.update(any(), any(), anyString())).thenThrow(RuntimeException.class);
    assertThrows(
        RuntimeException.class,
        () ->
            usersTableSqlHelper.updateLastAccess(
                testUser.getUserId().toString(), testUser.getLastAccess()),
        "Update should propagate exceptions.");
  }

  /** Tests deleting a user from the database. */
  @Test
  public void testDelete() {
    // Test successful delete
    when(jdbcTemplate.update(any(), anyString())).thenReturn(1);
    assertTrue(
        usersTableSqlHelper.delete(testUser.getUserId().toString()),
        "Delete should return true when successful.");

    // Test unsuccessful delete
    when(jdbcTemplate.update(any(), anyString())).thenReturn(0);
    assertFalse(
        usersTableSqlHelper.delete(testUser.getUserId().toString()),
        "Delete should return false when no rows are affected.");

    // Test exception thrown
    when(jdbcTemplate.update(any(), anyString())).thenThrow(RuntimeException.class);
    assertThrows(
        RuntimeException.class,
        () -> usersTableSqlHelper.delete(testUser.getUserId().toString()),
        "Delete should propagate exceptions.");
  }
}

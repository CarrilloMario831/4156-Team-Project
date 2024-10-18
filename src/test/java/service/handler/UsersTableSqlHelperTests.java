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

/** Sample javadoc to pass checkstyle. */
@SpringBootTest
public class UsersTableSqlHelperTests {

  @Mock private JdbcTemplate jdbcTemplate;

  @InjectMocks private UsersTableSqlHelper usersTableSqlHelper;

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
  public void testInsertUser() {
    // Test successful insert
    when(jdbcTemplate.update(any(), any(), any(), any(), any(), any())).thenReturn(1);
    assertTrue(usersTableSqlHelper.insertUser(testUser));

    // Test unsuccessful insert
    when(jdbcTemplate.update(any(), any(), any(), any(), any(), any())).thenReturn(0);
    assertFalse(usersTableSqlHelper.insertUser(testUser));

    // Test error thrown
    when(jdbcTemplate.update(any(), any(), any(), any(), any(), any()))
        .thenThrow(RuntimeException.class);
    assertThrows(RuntimeException.class, () -> usersTableSqlHelper.insertUser(testUser));
  }

  @Test
  public void testGetAllUsers() {
    // Test successful
    ArrayList<User> users = new ArrayList<>();
    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(users);
    assertEquals(users, usersTableSqlHelper.getAllUsers());

    // Test error thrown
    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenThrow(RuntimeException.class);
    assertThrows(RuntimeException.class, () -> usersTableSqlHelper.getAllUsers());
  }

  @Test
  public void testGetUserWithUserId() {
    // Test empty list
    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(new ArrayList<>());
    assertEquals(null, usersTableSqlHelper
            .getUserWithUserId(String.valueOf(testUser.getUserId())));

    // Test more than one user
    ArrayList<User> users = new ArrayList<>();
    users.add(testUser);
    users.add(testUser);
    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(users);
    assertThrows(
        IllegalStateException.class,
        () -> usersTableSqlHelper.getUserWithUserId(String.valueOf(testUser.getUserId())));

    // Test successful retrieve user
    users.remove(0);
    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(users);
    User actualUser = usersTableSqlHelper.getUserWithUserId(String.valueOf(testUser.getUserId()));
    assertEquals(testUser, actualUser);
  }

  @Test
  public void testGetUserWithUsername() {
    // Test empty list
    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(new ArrayList<>());
    assertEquals(null, usersTableSqlHelper.getUserWithUsername(testUser.getUsername()));

    // Test more than one user
    ArrayList<User> users = new ArrayList<>();
    users.add(testUser);
    users.add(testUser);
    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(users);
    assertThrows(
        IllegalStateException.class,
        () -> usersTableSqlHelper.getUserWithUsername(testUser.getUsername()));

    // Test successful retrieve user
    users.remove(0);
    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(users);
    User actualUser =
        usersTableSqlHelper.getUserWithUsername(String.valueOf(testUser.getUsername()));
    assertEquals(testUser, actualUser);
  }

  @Test
  public void testUpdateUsername() {
    // Test successful username update
    when(jdbcTemplate.update(any(), anyString(), anyString())).thenReturn(1);
    assertTrue(
        usersTableSqlHelper.updateUsername(
            String.valueOf(testUser.getUserId()), testUser.getUsername()));

    // Test unsuccessful username update
    when(jdbcTemplate.update(any(), anyString(), anyString())).thenReturn(0);
    assertFalse(
        usersTableSqlHelper.updateUsername(
            String.valueOf(testUser.getUserId()), testUser.getUsername()));

    // Test error thrown
    when(jdbcTemplate.update(any(), anyString(), anyString())).thenThrow(RuntimeException.class);
    assertThrows(
        RuntimeException.class,
        () ->
            usersTableSqlHelper.updateUsername(
                String.valueOf(testUser.getUserId()), testUser.getUsername()));
  }

  @Test
  public void testUpdateRole() {
    // Test successful role update
    when(jdbcTemplate.update(any(), anyString(), anyString())).thenReturn(1);
    assertTrue(
        usersTableSqlHelper.updateRole(
            String.valueOf(testUser.getUserId()), String.valueOf(testUser.getRole())));

    // Test unsuccessful role update
    when(jdbcTemplate.update(any(), anyString(), anyString())).thenReturn(0);
    assertFalse(
        usersTableSqlHelper.updateRole(
            String.valueOf(testUser.getUserId()), String.valueOf(testUser.getRole())));

    // Test error thrown
    when(jdbcTemplate.update(any(), anyString(), anyString())).thenThrow(RuntimeException.class);
    assertThrows(
        RuntimeException.class,
        () ->
            usersTableSqlHelper.updateRole(
                String.valueOf(testUser.getUserId()), String.valueOf(testUser.getRole())));
  }

  @Test
  public void testUpdateLastAccess() {
    // Test successful last access update
    when(jdbcTemplate.update(any(), any(), anyString())).thenReturn(1);
    assertTrue(
        usersTableSqlHelper.updateLastAccess(
            String.valueOf(testUser.getUserId()), testUser.getLastAccess()));

    // Test unsuccessful last access update
    when(jdbcTemplate.update(any(), any(), anyString())).thenReturn(0);
    assertFalse(
        usersTableSqlHelper.updateLastAccess(
            String.valueOf(testUser.getUserId()), testUser.getLastAccess()));

    // Test error thrown
    when(jdbcTemplate.update(any(), any(), anyString())).thenThrow(RuntimeException.class);
    assertThrows(
        RuntimeException.class,
        () ->
            usersTableSqlHelper.updateLastAccess(
                String.valueOf(testUser.getUserId()), testUser.getLastAccess()));
  }

  @Test
  public void testUpdateInventoryAccess() {
    // Test successful last access update
    when(jdbcTemplate.update(any(), any(), anyString())).thenReturn(1);
    assertTrue(
        usersTableSqlHelper.updateInventoryAccess(
            String.valueOf(testUser.getUserId()), String.valueOf(testUser.getInventoryAccess())));

    // Test unsuccessful last access update
    when(jdbcTemplate.update(any(), any(), anyString())).thenReturn(0);
    assertFalse(
        usersTableSqlHelper.updateInventoryAccess(
            String.valueOf(testUser.getUserId()), String.valueOf(testUser.getInventoryAccess())));

    // Test error thrown
    when(jdbcTemplate.update(any(), any(), anyString())).thenThrow(RuntimeException.class);
    assertThrows(
        RuntimeException.class,
        () ->
            usersTableSqlHelper.updateInventoryAccess(
                String.valueOf(testUser.getUserId()),
                String.valueOf(testUser.getInventoryAccess())));
  }

  @Test
  public void testDelete() {
    // Test successful last access update
    when(jdbcTemplate.update(any(), anyString())).thenReturn(1);
    assertTrue(usersTableSqlHelper.delete(String.valueOf(testUser.getUserId())));

    // Test unsuccessful last access update
    when(jdbcTemplate.update(any(), anyString())).thenReturn(0);
    assertFalse(usersTableSqlHelper.delete(String.valueOf(testUser.getUserId())));

    // Test error thrown
    when(jdbcTemplate.update(any(), anyString())).thenThrow(RuntimeException.class);
    assertThrows(
        RuntimeException.class,
        () -> usersTableSqlHelper.delete(String.valueOf(testUser.getUserId())));
  }
}

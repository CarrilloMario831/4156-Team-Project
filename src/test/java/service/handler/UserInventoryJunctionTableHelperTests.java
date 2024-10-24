package service.handler;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

/** Unit tests for the UserInventoryJunctionTableHelper class. */
@SpringBootTest
public class UserInventoryJunctionTableHelperTests {
  @Mock private JdbcTemplate jdbcTemplate;

  @InjectMocks private UserInventoryJunctionTableHelper userInventoryJunctionTableHelper;

  private String testUserId;
  private String testUsername;
  private String testInventoryId;
  private String testInventoryName;

  /** Init. */
  @BeforeEach
  public void init() {
    testUserId = UUID.randomUUID().toString();
    testUsername = "testUsername";
    testInventoryId = UUID.randomUUID().toString();
    testInventoryName = "testInventory";
  }

  /** Test get inventory ids by user id. */
  @Test
  public void testGetInventoryIdsByUserId() {
    // Test error during SQL Query
    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenThrow(new RuntimeException());
    assertThrows(
        RuntimeException.class,
        () -> userInventoryJunctionTableHelper.getInventoryIdsByUserId(testUserId));

    // Test successful SQL Query
    ArrayList<String> testList = new ArrayList<>();
    testList.add(testUserId);
    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(testList);
    assertDoesNotThrow(() -> userInventoryJunctionTableHelper.getInventoryIdsByUserId(testUserId));
    assertEquals(testList, userInventoryJunctionTableHelper.getInventoryIdsByUserId(testUserId));
  }

  /** Test get user ids by inventory id. */
  @Test
  public void testGetUserIdsByInventoryId() {
    // Test error during SQL Query
    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenThrow(new RuntimeException());
    assertThrows(
        RuntimeException.class, () -> userInventoryJunctionTableHelper.getUserIdsByInventoryId(""));

    // Test successful SQL Query
    ArrayList<String> testList = new ArrayList<>();
    testList.add(testUserId);
    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(testList);
    assertDoesNotThrow(() -> userInventoryJunctionTableHelper.getUserIdsByInventoryId(testUserId));
    assertEquals(testList, userInventoryJunctionTableHelper.getUserIdsByInventoryId(testUserId));
  }

  /** Test add user inventory access. */
  @Test
  public void testAddUserInventoryAccess() {
    // Test more than 1 row updated
    when(jdbcTemplate.update(anyString(), anyString(), anyString(), anyString(), anyString()))
        .thenReturn(2);
    assertFalse(
        userInventoryJunctionTableHelper.addUserInventoryAccess(
            testUserId, testUsername, testInventoryId, testInventoryName));

    // Test less than 1 row updated
    when(jdbcTemplate.update(anyString(), anyString(), anyString(), anyString(), anyString()))
        .thenReturn(0);
    assertFalse(
        userInventoryJunctionTableHelper.addUserInventoryAccess(
            testUserId, testUsername, testInventoryId, testInventoryName));

    // Test 1 row updated
    when(jdbcTemplate.update(anyString(), anyString(), anyString(), anyString(), anyString()))
        .thenReturn(1);
    assertTrue(
        userInventoryJunctionTableHelper.addUserInventoryAccess(
            testUserId, testUsername, testInventoryId, testInventoryName));

    // Test error during SQL Query
    when(jdbcTemplate.update(anyString(), anyString(), anyString(), anyString(), anyString()))
        .thenThrow(new RuntimeException());
    assertThrows(
        RuntimeException.class,
        () ->
            userInventoryJunctionTableHelper.addUserInventoryAccess(
                testUserId, testUsername, testInventoryId, testInventoryName));
  }

  /** Test remove user inventory access. */
  @Test
  public void testRemoveUserInventoryAccess() {
    // Test more than 1 row updated
    when(jdbcTemplate.update(anyString(), anyString(), anyString())).thenReturn(2);
    assertFalse(
        userInventoryJunctionTableHelper.removeUserInventoryAccess(testUserId, testInventoryId));

    // Test less than 1 row updated
    when(jdbcTemplate.update(anyString(), anyString(), anyString())).thenReturn(0);
    assertFalse(
        userInventoryJunctionTableHelper.removeUserInventoryAccess(testUserId, testInventoryId));

    // Test 1 row updated
    when(jdbcTemplate.update(anyString(), anyString(), anyString())).thenReturn(1);
    assertTrue(
        userInventoryJunctionTableHelper.removeUserInventoryAccess(testUserId, testInventoryId));

    // Test error during SQL Query
    when(jdbcTemplate.update(anyString(), anyString(), anyString()))
        .thenThrow(new RuntimeException());
    assertThrows(
        RuntimeException.class,
        () ->
            userInventoryJunctionTableHelper.removeUserInventoryAccess(
                testUserId, testInventoryId));
  }
}

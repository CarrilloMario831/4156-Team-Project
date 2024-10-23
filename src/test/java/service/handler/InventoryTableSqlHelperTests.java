package service.handler;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import service.models.Inventory;

/** Unit tests for the InventoryTableSqlHelper class. */
@SpringBootTest
public class InventoryTableSqlHelperTests {

  @Mock private JdbcTemplate jdbcTemplate;

  @InjectMocks private InventoryTableSqlHelper inventoryTableSqlHelper;

  private Inventory testInventory;
  private String userKey;

  /** Sets up a test inventory before each test. */
  @BeforeEach
  public void setup() {
    testInventory =
        Inventory.builder()
            .inventoryId(UUID.fromString("bf456378-a8b3-40b6-b1a1-654bc9de5f02"))
            .inventoryName("Test Inventory")
            .adminId(UUID.fromString("9cdd2cec-d003-4964-b55c-cb336c51b809"))
            .build();

    userKey = testInventory.getAdminId().toString();
  }

  /** Tests the insert method of InventoryTableSqlHelper. */
  @Test
  public void testInsert() {
    // Test successful insert
    when(jdbcTemplate.update(anyString(), anyString(), anyString(), anyString())).thenReturn(1);
    assertDoesNotThrow(
        () -> inventoryTableSqlHelper.insert(testInventory, userKey),
        "Insert should not throw an exception when successful.");

    // Test exception thrown
    when(jdbcTemplate.update(anyString(), anyString(), anyString(), anyString()))
        .thenThrow(RuntimeException.class);
    assertThrows(
        RuntimeException.class,
        () -> inventoryTableSqlHelper.insert(testInventory, userKey),
        "Insert should propagate exceptions.");
  }

  /** Tests selecting all inventories from the database. */
  @Test
  public void testSelectAll() {
    // Prepare a list of inventories
    List<Inventory> inventories = new ArrayList<>();
    inventories.add(testInventory);

    // Mock the jdbcTemplate to return the inventories list
    when(jdbcTemplate.query(anyString(), any(InventoryRowMapper.class))).thenReturn(inventories);

    // Call the method and assert the result
    List<Inventory> result = inventoryTableSqlHelper.select();
    assertEquals(inventories, result, "Should return the list of all inventories.");

    // Test empty list
    when(jdbcTemplate.query(anyString(), any(InventoryRowMapper.class)))
        .thenReturn(new ArrayList<>());
    result = inventoryTableSqlHelper.select();
    assertTrue(result.isEmpty(), "Should return an empty list when no inventories are found.");

    // Test exception thrown
    when(jdbcTemplate.query(anyString(), any(InventoryRowMapper.class)))
        .thenThrow(RuntimeException.class);
    assertThrows(
        RuntimeException.class,
        () -> inventoryTableSqlHelper.select(),
        "Should propagate exceptions.");
  }

  /** Tests selecting an inventory by its ID. */
  @Test
  public void testSelectById() {
    String inventoryId = testInventory.getInventoryId().toString();

    // Prepare a list of inventories
    List<Inventory> inventories = new ArrayList<>();
    inventories.add(testInventory);

    // Mock the jdbcTemplate to return the inventories list when the specific SQL is called
    when(jdbcTemplate.query(contains("where inventory_id"), any(InventoryRowMapper.class)))
        .thenReturn(inventories);

    // Call the method and assert the result
    List<Inventory> result = inventoryTableSqlHelper.select(inventoryId);
    assertEquals(inventories, result, "Should return the list of inventories with the given ID.");

    // Test inventory not found
    when(jdbcTemplate.query(contains("where inventory_id"), any(InventoryRowMapper.class)))
        .thenReturn(new ArrayList<>());
    result = inventoryTableSqlHelper.select(inventoryId);
    assertTrue(result.isEmpty(), "Should return an empty list when inventory is not found.");

    // Test exception thrown
    when(jdbcTemplate.query(contains("where inventory_id"), any(InventoryRowMapper.class)))
        .thenThrow(RuntimeException.class);
    assertThrows(
        RuntimeException.class,
        () -> inventoryTableSqlHelper.select(inventoryId),
        "Should propagate exceptions.");
  }

  /** Tests updating the inventory name. */
  @Test
  public void testUpdateInventoryName() {
    String inventoryId = testInventory.getInventoryId().toString();
    String newInventoryName = "Updated Inventory Name";

    // Test successful update
    when(jdbcTemplate.update(anyString(), eq(newInventoryName), eq(inventoryId))).thenReturn(1);
    assertTrue(
        inventoryTableSqlHelper.update(inventoryId, newInventoryName),
        "Update should return true when one or more rows are affected.");

    // Test unsuccessful update
    when(jdbcTemplate.update(anyString(), eq(newInventoryName), eq(inventoryId))).thenReturn(0);
    assertFalse(
        inventoryTableSqlHelper.update(inventoryId, newInventoryName),
        "Update should return false when no rows are affected.");

    // Test exception thrown
    when(jdbcTemplate.update(anyString(), eq(newInventoryName), eq(inventoryId)))
        .thenThrow(RuntimeException.class);
    assertThrows(
        RuntimeException.class,
        () -> inventoryTableSqlHelper.update(inventoryId, newInventoryName),
        "Should propagate exceptions.");
  }

  /** Tests updating the admin ID of an inventory. */
  @Test
  public void testUpdateAdmin() {
    String inventoryId = testInventory.getInventoryId().toString();
    String newAdminId = UUID.randomUUID().toString();

    // Test successful update
    when(jdbcTemplate.update(anyString(), eq(newAdminId), eq(inventoryId))).thenReturn(1);
    assertTrue(
        inventoryTableSqlHelper.updateAdmin(inventoryId, newAdminId),
        "Update should return true when one or more rows are affected.");

    // Test unsuccessful update
    when(jdbcTemplate.update(anyString(), eq(newAdminId), eq(inventoryId))).thenReturn(0);
    assertFalse(
        inventoryTableSqlHelper.updateAdmin(inventoryId, newAdminId),
        "Update should return false when no rows are affected.");

    // Test exception thrown
    when(jdbcTemplate.update(anyString(), eq(newAdminId), eq(inventoryId)))
        .thenThrow(RuntimeException.class);
    assertThrows(
        RuntimeException.class,
        () -> inventoryTableSqlHelper.updateAdmin(inventoryId, newAdminId),
        "Should propagate exceptions.");
  }

  /** Tests deleting an inventory. */
  @Test
  public void testDelete() {
    String inventoryId = testInventory.getInventoryId().toString();

    // Test successful delete
    when(jdbcTemplate.update(anyString(), eq(inventoryId))).thenReturn(1);
    assertTrue(
        inventoryTableSqlHelper.delete(inventoryId),
        "Delete should return true when one or more rows are affected.");

    // Test unsuccessful delete
    when(jdbcTemplate.update(anyString(), eq(inventoryId))).thenReturn(0);
    assertFalse(
        inventoryTableSqlHelper.delete(inventoryId),
        "Delete should return false when no rows are affected.");

    // Test exception thrown
    when(jdbcTemplate.update(anyString(), eq(inventoryId))).thenThrow(RuntimeException.class);
    assertThrows(
        RuntimeException.class,
        () -> inventoryTableSqlHelper.delete(inventoryId),
        "Should propagate exceptions.");
  }

  /**
   * Tests the InventoryRowMapper's mapRow method.
   *
   * @throws SQLException if a database access error occurs.
   */
  @Test
  public void testInventoryRowMapper() throws SQLException {
    ResultSet resultSet = mock(ResultSet.class);

    when(resultSet.getString("inventory_id")).thenReturn(testInventory.getInventoryId().toString());
    when(resultSet.getString("inventory_name")).thenReturn(testInventory.getInventoryName());
    when(resultSet.getString("user_id")).thenReturn(testInventory.getAdminId().toString());

    InventoryRowMapper rowMapper = new InventoryRowMapper();
    Inventory mappedInventory = rowMapper.mapRow(resultSet, 1);

    assertEquals(
        testInventory.getInventoryId(),
        mappedInventory.getInventoryId(),
        "Inventory ID should match.");
    assertEquals(
        testInventory.getInventoryName(),
        mappedInventory.getInventoryName(),
        "Inventory name should match.");
    assertEquals(
        testInventory.getAdminId(), mappedInventory.getAdminId(), "Admin ID should match.");
  }
}

package service.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import service.models.Inventory;

/** Unit tests for the InventoryTableSqlHelper class. */
@SpringBootTest
public class InventoryTableSqlHelperTests {

  /**
   * This variable is needed because it prevents the test queries from actually being executed on
   * DB.
   */
  @Mock private JdbcTemplate jdbcTemplate;

  // Here we define that the previously mocked variable is to be utilized within the class instance.
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
            .build();
  }

  /** Tests the insert method of InventoryTableSqlHelper. */
  @Test
  public void testInsert() {
    // I think a better test would be to check the returned value from insert method call
    // instead of checking if it threw an exception or not. Also, testing if the method
    // returns 1 or 0 allows for full branch coverage.
    //    // Test successful insert
    //    when(jdbcTemplate.update(anyString(), anyString(), anyString())).thenReturn(1);
    //    assertDoesNotThrow(
    //        () -> inventoryTableSqlHelper.insert(testInventory),
    //        "Insert should not throw an exception when successful.");

    // Test successful insert return value of 1
    when(jdbcTemplate.update(anyString(), anyString(), anyString())).thenReturn(1);
    assertTrue(
        inventoryTableSqlHelper.insertInventory(testInventory),
        "Insert should return true when successful.");

    // Mocking update to return 0, simulating an insert that didn't add any rows aka it didn't work.
    when(jdbcTemplate.update(anyString(), anyString(), anyString())).thenReturn(0);
    assertFalse(
        inventoryTableSqlHelper.insertInventory(testInventory),
        "Insert should return false when unsuccessful.");

    // Test exception thrown
    when(jdbcTemplate.update(anyString(), anyString(), anyString()))
        .thenThrow(RuntimeException.class);
    assertThrows(
        RuntimeException.class,
        () -> inventoryTableSqlHelper.insertInventory(testInventory),
        "Insert should propagate exceptions.");
  }

  /** Tests selecting all inventories from the database. */
  @Test
  public void testSelectAll() {
    // Prepare a list of inventories
    List<Inventory> inventories = new ArrayList<>();
    inventories.add(testInventory);

    // Mock the jdbcTemplate to return the inventories list
    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(inventories);

    // Call the method and assert the result
    List<Inventory> result = inventoryTableSqlHelper.getAllInventories();
    assertEquals(inventories, result, "Should return the list of all inventories.");

    // Test empty list
    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(new ArrayList<>());
    result = inventoryTableSqlHelper.getAllInventories();
    assertTrue(result.isEmpty(), "Should return an empty list when no inventories are found.");

    // Test exception thrown
    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenThrow(RuntimeException.class);
    assertThrows(
        RuntimeException.class,
        () -> inventoryTableSqlHelper.getAllInventories(),
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
    when(jdbcTemplate.query(contains("where inventory_id"), any(RowMapper.class)))
        .thenReturn(inventories);

    // Call the method and assert the result
    Inventory result = inventoryTableSqlHelper.getInventoryWithInventoryId(inventoryId);
    assertEquals(inventories.get(0), result, "Should return the inventory with the given ID.");

    // Test inventory not found
    when(jdbcTemplate.query(contains("where inventory_id"), any(RowMapper.class)))
        .thenReturn(new ArrayList<>());
    result = inventoryTableSqlHelper.getInventoryWithInventoryId(inventoryId);
    assertNull(result, "Should return null if there's no inventory found with the given ID.");

    // Test exception thrown from more than one result returned by the query
    inventories.add(testInventory);
    when(jdbcTemplate.query(contains("where inventory_id"), any(RowMapper.class)))
        .thenReturn(inventories);
    assertThrows(
        IllegalStateException.class,
        () -> inventoryTableSqlHelper.getInventoryWithInventoryId(inventoryId),
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
}

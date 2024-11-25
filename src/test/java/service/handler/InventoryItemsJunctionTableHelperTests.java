package service.handler;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

/** The type Inventory items junction table helper tests. */
@SpringBootTest
public class InventoryItemsJunctionTableHelperTests {
  @Mock private JdbcTemplate jdbcTemplate;

  @InjectMocks private InventoryItemsJunctionTableHelper inventoryItemsJunctionTableHelper;

  private String testItemId;
  private String testItemName;
  private String testInventoryId;

  /** Init. */
  @BeforeEach
  public void init() {
    testItemId = UUID.randomUUID().toString();
    testItemName = "testItemName";
    testInventoryId = UUID.randomUUID().toString();
  }

  /** Test get item ids by inventory id. */
  @Test
  void testGetItemIdsByInventoryId() {
    // Test error during SQL Query
    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenThrow(new RuntimeException());
    assertThrows(
        RuntimeException.class,
        () -> inventoryItemsJunctionTableHelper.getItemIdsByInventoryId(testInventoryId));

    // Test successful SQL Query
    ArrayList<String> testList = new ArrayList<>();
    testList.add(testItemId);
    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(testList);
    assertDoesNotThrow(
        () -> inventoryItemsJunctionTableHelper.getItemIdsByInventoryId(testInventoryId));
    assertEquals(
        testList, inventoryItemsJunctionTableHelper.getItemIdsByInventoryId(testInventoryId));
  }

  /** Test get item names by inventory id. */
  @Test
  void testGetItemNamesByInventoryId() {
    // Test error during SQL Query
    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenThrow(new RuntimeException());
    assertThrows(
        RuntimeException.class,
        () -> inventoryItemsJunctionTableHelper.getItemNamesByInventoryId(testInventoryId));

    // Test successful SQL Query
    ArrayList<String> testList = new ArrayList<>();
    testList.add(testItemName);
    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(testList);
    assertDoesNotThrow(
        () -> inventoryItemsJunctionTableHelper.getItemNamesByInventoryId(testInventoryId));
    assertEquals(
        testList, inventoryItemsJunctionTableHelper.getItemNamesByInventoryId(testInventoryId));
  }
}

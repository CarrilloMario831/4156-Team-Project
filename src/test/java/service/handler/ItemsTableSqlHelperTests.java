package service.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
import service.models.Item;

/** Unit tests for the ItemsTableSqlHelper class. */
@SpringBootTest
public class ItemsTableSqlHelperTests {

  @Mock private JdbcTemplate jdbcTemplate;

  @InjectMocks private ItemsTableSqlHelper itemsTableSqlHelper;

  private Item testItem;

  /** Sets up a test item before each test. */
  @BeforeEach
  public void setup() {
    testItem =
        Item.builder()
            .itemId(UUID.fromString("9cdd2cec-d003-4964-b55c-cb336c51b809"))
            .itemName("Test Item")
            .timeOfAddition(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .location("Test Location")
            .quantity(10)
            .reservationStatus(false)
            .reservationDurationInMillis(0)
            .price(19.99)
            .nextRestockDateTime(null)
            .build();
  }

  /** Tests the insertion of an item into the database. */
  @Test
  public void testInsertItem() {
    // Test successful insert
    when(jdbcTemplate.update(
            anyString(),
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
            any()))
        .thenReturn(1);
    assertTrue(
        itemsTableSqlHelper.insertItem(testItem),
        "Insert should return true when one row is affected.");

    // Test unsuccessful insert
    when(jdbcTemplate.update(
            anyString(),
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
            any()))
        .thenReturn(0);
    assertFalse(
        itemsTableSqlHelper.insertItem(testItem),
        "Insert should return false when no rows are affected.");

    // Test exception thrown
    when(jdbcTemplate.update(
            anyString(),
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
            any()))
        .thenThrow(RuntimeException.class);
    assertThrows(
        RuntimeException.class,
        () -> itemsTableSqlHelper.insertItem(testItem),
        "Insert should propagate exceptions.");
  }

  /** Tests retrieving all items from the database. */
  @Test
  public void testGetAllItems() {
    // Test successful query
    List<Item> items = new ArrayList<>();
    items.add(testItem);
    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(items);
    assertEquals(items, itemsTableSqlHelper.getAllItems(), "Should return the list of all items.");

    // Test empty list
    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(new ArrayList<>());
    assertTrue(
        itemsTableSqlHelper.getAllItems().isEmpty(),
        "Should return an empty list when no items are found.");

    // Test exception thrown
    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenThrow(RuntimeException.class);
    assertThrows(
        RuntimeException.class,
        () -> itemsTableSqlHelper.getAllItems(),
        "Should propagate exceptions.");
  }

  /** Tests retrieving an item by its ID. */
  @Test
  public void testGetItem() {
    String itemId = testItem.getItemId().toString();

    // Test successful retrieval
    List<Item> items = new ArrayList<>();
    items.add(testItem);
    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(items);
    assertEquals(
        items,
        itemsTableSqlHelper.getItem(itemId),
        "Should return the list of items with the given ID.");

    // Test item not found
    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenReturn(new ArrayList<>());
    assertTrue(
        itemsTableSqlHelper.getItem(itemId).isEmpty(),
        "Should return an empty list when item is not found.");

    // Test exception thrown
    when(jdbcTemplate.query(anyString(), any(RowMapper.class))).thenThrow(RuntimeException.class);
    assertThrows(
        RuntimeException.class,
        () -> itemsTableSqlHelper.getItem(itemId),
        "Should propagate exceptions.");
  }

  /** Tests updating the location of an item. */
  @Test
  public void testUpdateItemLocation() {
    String itemId = testItem.getItemId().toString();
    String newLocation = "New Location";

    // Test successful update
    when(jdbcTemplate.update(anyString(), eq(newLocation), eq(itemId))).thenReturn(1);
    assertTrue(
        itemsTableSqlHelper.updateItemLocation(itemId, newLocation),
        "Update should return true when one row is affected.");

    // Test unsuccessful update
    when(jdbcTemplate.update(anyString(), eq(newLocation), eq(itemId))).thenReturn(0);
    assertFalse(
        itemsTableSqlHelper.updateItemLocation(itemId, newLocation),
        "Update should return false when no rows are affected.");

    // Test exception thrown
    when(jdbcTemplate.update(anyString(), eq(newLocation), eq(itemId)))
        .thenThrow(RuntimeException.class);
    assertThrows(
        RuntimeException.class,
        () -> itemsTableSqlHelper.updateItemLocation(itemId, newLocation),
        "Should propagate exceptions.");
  }

  /** Tests updating the price of an item. */
  @Test
  public void testUpdateItemPrice() {
    String itemId = testItem.getItemId().toString();
    double newPrice = 29.99;

    // Test successful update
    when(jdbcTemplate.update(anyString(), eq(newPrice), eq(itemId))).thenReturn(1);
    assertTrue(
        itemsTableSqlHelper.updateItemPrice(itemId, newPrice),
        "Update should return true when one row is affected.");

    // Test unsuccessful update
    when(jdbcTemplate.update(anyString(), eq(newPrice), eq(itemId))).thenReturn(0);
    assertFalse(
        itemsTableSqlHelper.updateItemPrice(itemId, newPrice),
        "Update should return false when no rows are affected.");

    // Test exception thrown
    when(jdbcTemplate.update(anyString(), eq(newPrice), eq(itemId)))
        .thenThrow(RuntimeException.class);
    assertThrows(
        RuntimeException.class,
        () -> itemsTableSqlHelper.updateItemPrice(itemId, newPrice),
        "Should propagate exceptions.");
  }

  /** Tests updating the quantity of an item. */
  @Test
  public void testUpdateItemQuantity() {
    String itemId = testItem.getItemId().toString();
    int newQuantity = 20;

    // Test successful update
    when(jdbcTemplate.update(anyString(), eq(newQuantity), eq(itemId))).thenReturn(1);
    assertTrue(
        itemsTableSqlHelper.updateItemQuantity(itemId, newQuantity),
        "Update should return true when one row is affected.");

    // Test unsuccessful update
    when(jdbcTemplate.update(anyString(), eq(newQuantity), eq(itemId))).thenReturn(0);
    assertFalse(
        itemsTableSqlHelper.updateItemQuantity(itemId, newQuantity),
        "Update should return false when no rows are affected.");

    // Test exception thrown
    when(jdbcTemplate.update(anyString(), eq(newQuantity), eq(itemId)))
        .thenThrow(RuntimeException.class);
    assertThrows(
        RuntimeException.class,
        () -> itemsTableSqlHelper.updateItemQuantity(itemId, newQuantity),
        "Should propagate exceptions.");
  }

  /** Tests deleting an item from the database. */
  @Test
  public void testDeleteItem() {
    String itemId = testItem.getItemId().toString();

    // Test successful delete
    when(jdbcTemplate.update(anyString(), eq(itemId))).thenReturn(1);
    assertTrue(
        itemsTableSqlHelper.deleteItem(itemId),
        "Delete should return true when one row is affected.");

    // Test unsuccessful delete
    when(jdbcTemplate.update(anyString(), eq(itemId))).thenReturn(0);
    assertFalse(
        itemsTableSqlHelper.deleteItem(itemId),
        "Delete should return false when no rows are affected.");

    // Test exception thrown
    when(jdbcTemplate.update(anyString(), eq(itemId))).thenThrow(RuntimeException.class);
    assertThrows(
        RuntimeException.class,
        () -> itemsTableSqlHelper.deleteItem(itemId),
        "Should propagate exceptions.");
  }

  /** Tests updating the name of an item. */
  @Test
  public void testUpdateItemName() {
    String itemId = testItem.getItemId().toString();
    String newItemName = "New Item Name";

    // Test successful update
    when(jdbcTemplate.update(anyString(), eq(newItemName), eq(itemId))).thenReturn(1);
    assertTrue(
        itemsTableSqlHelper.updateItemName(itemId, newItemName),
        "Update should return true when one row is affected.");

    // Test unsuccessful update
    when(jdbcTemplate.update(anyString(), eq(newItemName), eq(itemId))).thenReturn(0);
    assertFalse(
        itemsTableSqlHelper.updateItemName(itemId, newItemName),
        "Update should return false when no rows are affected.");

    // Test exception thrown
    when(jdbcTemplate.update(anyString(), eq(newItemName), eq(itemId)))
        .thenThrow(RuntimeException.class);
    assertThrows(
        RuntimeException.class,
        () -> itemsTableSqlHelper.updateItemName(itemId, newItemName),
        "Should propagate exceptions.");
  }
}

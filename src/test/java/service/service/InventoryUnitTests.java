package service.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.models.Inventory;
import service.models.Item;

/** Unit tests for the Inventory class. */
public class InventoryUnitTests {

  private Inventory inventory;
  private UUID adminId;

  /** Sets up a new {@link Inventory} instance before each test. */
  @BeforeEach
  public void setUp() {
    adminId = UUID.randomUUID();
    inventory =
        Inventory.builder()
            .adminId(adminId)
            .inventoryName("Test Inventory")
            .items(new HashMap<>())
            .build();
  }

  /** Tests the constructor of the Inventory class. */
  @Test
  public void testConstructor() {
    assertNotNull(inventory.getInventoryId(), "Inventory ID should not be null.");
    assertEquals(
        "Test Inventory", inventory.getInventoryName(), "Inventory name should be set correctly.");
    assertEquals(
        adminId.toString(), inventory.getAdminId().toString(), "Admin ID should be set correctly.");
    assertTrue(inventory.getAllItems().isEmpty(), "Items map should be initialized and empty.");
  }

  /** Test createItem with valid input. */
  @Test
  public void testCreateItem() {
    inventory.createItem("Laptop", 10, "Warehouse A", 1000.0);
    assertEquals(1, inventory.getAllItems().size());
    Item item = inventory.searchItemsByName("Laptop").get(0);
    assertEquals("Laptop", item.getItemName());
    assertEquals(10, item.getQuantity());
    assertEquals(1000.0, item.getPrice(), 0.01);
  }

  /** Test createItem with invalid input (empty name, negative quantity/price). */
  @Test
  public void testCreateItemInvalid() {
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              inventory.createItem("", -1, "Warehouse A", -100.0);
            });
    assertEquals("Invalid item data.", exception.getMessage());
  }

  /** Tests retrieving the inventory ID. */
  @Test
  public void testGetId() {
    UUID inventoryId = inventory.getInventoryId();
    assertNotNull(inventoryId, "Inventory ID should not be null.");
  }

  /** Test addItem with valid input. */
  @Test
  public void testAddItem() {
    Item item =
        Item.builder()
            .itemName("Chicken Nugget")
            .quantity(5)
            .location("Warehouse B")
            .price(200.0)
            .build();
    assertTrue(inventory.addItem(item)); // Item should be added successfully
    assertEquals(1, inventory.getAllItems().size());
    assertEquals("Chicken Nugget", inventory.getItem(item.getItemId()).getItemName());
  }

  /** Test addItem with duplicate item. */
  @Test
  public void testAddItemDuplicate() {
    Item item =
        Item.builder().itemName("Monkey").quantity(5).location("Warehouse B").price(200.0).build();
    assertTrue(inventory.addItem(item)); // First add should succeed
    assertFalse(inventory.addItem(item)); // Second add should return false
  }

  /** Test removeItem with valid item. */
  @Test
  public void testRemoveItem() {
    Item item =
        Item.builder()
            .itemName("Pretzel")
            .quantity(3)
            .location("Warehouse C")
            .price(5000.0)
            .build();
    inventory.addItem(item);
    assertTrue(inventory.removeItem(item.getItemId())); // Should return true
    assertNull(inventory.getItem(item.getItemId())); // Should be null after removal
  }

  /** Test removeItem for non-existent item. */
  @Test
  public void testRemoveItemNonExistent() {
    boolean removed = inventory.removeItem(UUID.randomUUID());
    assertFalse(removed);
    assertEquals(
        0,
        inventory.getAllItems().size(),
        "Inventory should remain unchanged after attempting to remove a non-existent item.");
  }

  /** Tests retrieving an item by its UUID. */
  @Test
  public void testGetItem() {
    Item item =
        Item.builder()
            .itemName("Coconut")
            .quantity(15)
            .location("Warehouse C")
            .price(199.99)
            .build();
    inventory.addItem(item);
    UUID itemId = item.getItemId();

    Item retrievedItem = inventory.getItem(itemId);

    assertNotNull(retrievedItem, "Retrieved item should not be null.");
    assertEquals(
        itemId,
        retrievedItem.getItemId(),
        "Retrieved item should have the same ID as the added item.");
  }

  /** Tests retrieving an item that does not exist in the inventory. */
  @Test
  public void testGetNonExistentItem() {
    UUID randomId = UUID.randomUUID();
    Item item = inventory.getItem(randomId);

    assertNull(item, "Retrieving a non-existent item should return null.");
  }

  /** Tests retrieving all items from the inventory. */
  @Test
  public void testGetAllItems() {
    Item item1 =
        Item.builder()
            .itemName("Eyeball")
            .quantity(7)
            .location("Warehouse D")
            .price(299.99)
            .build();
    Item item2 =
        Item.builder()
            .itemName("Chocolate Chip Cookie")
            .quantity(12)
            .location("Warehouse E")
            .price(49.99)
            .build();

    inventory.addItem(item1);
    inventory.addItem(item2);

    Collection<Item> allItems = inventory.getAllItems();

    assertEquals(2, allItems.size(), "Inventory should have two items.");
    assertTrue(allItems.contains(item1), "Inventory should contain the first added item.");
    assertTrue(allItems.contains(item2), "Inventory should contain the second added item.");
  }

  /** Tests getting the inventory name. */
  @Test
  public void testGetInventoryName() {
    assertEquals(
        "Test Inventory",
        inventory.getInventoryName(),
        "Inventory name should be 'Test Inventory'.");
  }

  /** Tests setting a new inventory name. */
  @Test
  public void testSetInventoryName() {
    inventory.setInventoryName("Updated Inventory Name");
    assertEquals(
        "Updated Inventory Name",
        inventory.getInventoryName(),
        "Inventory name should be updated.");
  }

  /** Tests adding multiple items with the same name but different IDs. */
  @Test
  public void testMultipleItemsWithSameName() {
    Item item1 =
        Item.builder().itemName("Chair").quantity(10).location("Warehouse H").price(59.99).build();
    Item item2 =
        Item.builder().itemName("Chair").quantity(5).location("Warehouse I").price(49.99).build();

    inventory.addItem(item1);
    inventory.addItem(item2);

    assertEquals(
        2,
        inventory.getAllItems().size(),
        "Inventory should handle items with the same name but different IDs.");
  }

  /** Tests retrieving the admin ID associated with the inventory. */
  @Test
  public void testGetAdminId() {
    assertEquals(
        adminId.toString(),
        inventory.getAdminId().toString(),
        "Admin ID should match the one provided during initialization.");
  }

  /** Test updateItemPrice with valid input. */
  @Test
  public void testUpdateItemPrice() {
    Item item =
        Item.builder().itemName("Phone").quantity(7).location("Warehouse D").price(800.0).build();
    inventory.addItem(item);
    inventory.updateItemPrice(item.getItemId(), 850.0);
    assertEquals(850.0, inventory.getItem(item.getItemId()).getPrice(), 0.01);
  }

  /** Test updateItemQuantity with valid input. */
  @Test
  public void testUpdateItemQuantity() {
    Item item =
        Item.builder().itemName("Tablet").quantity(4).location("Warehouse E").price(300.0).build();
    inventory.addItem(item);
    inventory.updateItemQuantity(item.getItemId(), 10);
    assertEquals(10, inventory.getItem(item.getItemId()).getQuantity());
  }

  /** Test updateItemQuantity with invalid input (negative quantity). */
  @Test
  public void testUpdateItemQuantityInvalid() {
    Item item =
        Item.builder().itemName("Tablet").quantity(4).location("Warehouse E").price(300.0).build();
    inventory.addItem(item);
    IllegalArgumentException exception =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              inventory.updateItemQuantity(item.getItemId(), -5); // Invalid quantity
            });
    assertEquals("Invalid quantity or item not found.", exception.getMessage());
  }

  /** Test searchItemsByName (partial match). */
  @Test
  public void testSearchItemsByNamePartial() {
    inventory.createItem("Sack", 10, "Warehouse A", 5.0);
    inventory.createItem("Safe", 5, "Warehouse B", 10.0);
    inventory.createItem("Salmon", 15, "Warehouse C", 12.0);

    List<Item> result = inventory.searchItemsByName("Sa");
    assertEquals(3, result.size()); // All items should match partial "Sa"
  }

  /** Test searchItemsByName with no matches. */
  @Test
  public void testSearchItemsByNameNoMatch() {
    inventory.createItem("Laptop", 10, "Warehouse A", 1000.0);
    List<Item> result = inventory.searchItemsByName("Phone");
    assertTrue(result.isEmpty());
  }

  /** Test reserveItem with valid input. */
  @Test
  public void testReserveItem() {
    Item item =
        Item.builder()
            .itemName("Projector")
            .quantity(2)
            .location("Warehouse F")
            .price(500.0)
            .build();
    inventory.addItem(item);
    inventory.reserveItem(item.getItemId(), Duration.ofHours(2));
    assertTrue(inventory.getItem(item.getItemId()).isReservationStatus());
  }

  /** Test unreserveItem with valid input. */
  @Test
  public void testUnreserveItem() {
    Item item =
        Item.builder()
            .itemName("Nintendo Switch")
            .quantity(2)
            .location("Warehouse F")
            .price(500.0)
            .build();
    inventory.addItem(item);
    inventory.reserveItem(item.getItemId(), Duration.ofHours(2));
    inventory.unreserveItem(item.getItemId());
    assertFalse(inventory.getItem(item.getItemId()).isReservationStatus());
  }

  /** Tests the {@code toString} method of the {@link Inventory} class. */
  @Test
  public void testToString() {
    Item item =
        Item.builder().itemName("Tablet").quantity(8).location("Warehouse G").price(399.99).build();
    inventory.addItem(item);

    String inventoryString = inventory.toString();

    assertTrue(
        inventoryString.contains("Test Inventory"), "toString should contain inventory name.");
    assertTrue(inventoryString.contains(item.getItemName()), "toString should contain item name.");
    assertTrue(
        inventoryString.contains(String.valueOf(item.getQuantity())),
        "toString should contain item quantity.");
  }
}

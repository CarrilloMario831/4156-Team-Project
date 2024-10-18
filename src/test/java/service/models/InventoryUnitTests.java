package service.models;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/** Unit tests for the Inventory class. */
@SpringBootTest
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
            .inventoryId(UUID.randomUUID())
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

  /** Tests creating a new item and adding it to the inventory. */
  @Test
  public void testCreateItem() {
    inventory.createItem("Mouse", 20, "Warehouse F", 29.99);

    assertEquals(
        1, inventory.getAllItems().size(), "Inventory should have one item after creation.");
    Item item = inventory.getAllItems().iterator().next();
    assertEquals("Mouse", item.getItemName(), "Item name should be 'Mouse'.");
    assertEquals(20, item.getQuantity(), "Item quantity should be 20.");
    assertEquals("Warehouse F", item.getLocation(), "Item location should be 'Warehouse F'.");
    assertEquals(29.99, item.getPrice(), 0.001, "Item price should be 29.99.");
  }

  /** Tests retrieving the inventory ID. */
  @Test
  public void testGetId() {
    UUID inventoryId = inventory.getInventoryId();
    assertNotNull(inventoryId, "Inventory ID should not be null.");
  }

  /** Tests adding an item to the inventory. */
  @Test
  public void testAddItem() {
    Item item =
        Item.builder()
            .itemName("Laptop")
            .quantity(10)
            .location("Warehouse A")
            .price(999.99)
            .build();
    inventory.addItem(item);

    assertEquals(
        1, inventory.getAllItems().size(), "Inventory should have one item after addition.");
    assertTrue(inventory.getAllItems().contains(item), "Inventory should contain the added item.");
  }

  /** Tests removing an item from the inventory. */
  @Test
  public void testRemoveItem() {
    Item item =
        Item.builder()
            .itemName("Smartphone")
            .quantity(5)
            .location("Warehouse B")
            .price(499.99)
            .build();
    inventory.addItem(item);
    UUID itemId = item.getItemId();

    inventory.removeItem(itemId);

    assertEquals(
        0, inventory.getAllItems().size(), "Inventory should be empty after item removal.");
    assertFalse(
        inventory.getAllItems().contains(item), "Inventory should not contain the removed item.");
  }

  /** Tests retrieving an item by its UUID. */
  @Test
  public void testGetItem() {
    Item item =
        Item.builder()
            .itemName("Headphones")
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

  /** Tests retrieving all items from the inventory. */
  @Test
  public void testGetAllItems() {
    Item item1 =
        Item.builder()
            .itemName("Monitor")
            .quantity(7)
            .location("Warehouse D")
            .price(299.99)
            .build();
    Item item2 =
        Item.builder()
            .itemName("Keyboard")
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

  /** Tests retrieving the admin ID associated with the inventory. */
  @Test
  public void testGetAdminId() {
    assertEquals(
        adminId.toString(),
        inventory.getAdminId().toString(),
        "Admin ID should match the one provided during initialization.");
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

  /** Tests removing an item that does not exist in the inventory. */
  @Test
  public void testRemoveNonExistentItem() {
    UUID randomId = UUID.randomUUID();
    inventory.removeItem(randomId);

    assertEquals(
        0,
        inventory.getAllItems().size(),
        "Inventory should remain unchanged after attempting to remove a non-existent item.");
  }

  /** Tests retrieving an item that does not exist in the inventory. */
  @Test
  public void testGetNonExistentItem() {
    UUID randomId = UUID.randomUUID();
    Item item = inventory.getItem(randomId);

    assertNull(item, "Retrieving a non-existent item should return null.");
  }

  /** Tests adding the same item twice to the inventory. */
  @Test
  public void testAddDuplicateItem() {
    Item item =
        Item.builder().itemName("Tablet").quantity(8).location("Warehouse G").price(399.99).build();
    inventory.addItem(item);
    inventory.addItem(item); // Attempt to add the same item again

    assertEquals(
        1, inventory.getAllItems().size(), "Inventory should not contain duplicate items.");
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
}

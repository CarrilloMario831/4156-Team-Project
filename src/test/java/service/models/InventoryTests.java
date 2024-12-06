package service.models;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * The type Inventory tests.
 */
@SpringBootTest
public class InventoryTests {

  /**
   * Test builder and getters.
   */
  @Test
  public void testBuilderAndGetters() {
    UUID inventoryId = UUID.randomUUID();
    Inventory inventory =
        Inventory.builder()
            .inventoryId(inventoryId)
            .inventoryName("Test Inventory")
            .items(null)
            .build();

    assertThat(inventory.getInventoryId()).isEqualTo(inventoryId);
    assertThat(inventory.getInventoryName()).isEqualTo("Test Inventory");
    assertThat(inventory.getItems()).isNull();
  }

  /**
   * Test setters.
   */
  @Test
  public void testSetters() {
    UUID inventoryId = UUID.randomUUID();
    Inventory inventory =
        Inventory.builder()
            .inventoryId(UUID.randomUUID())
            .inventoryName("Old Name")
            .items(null)
            .build();

    inventory.setInventoryId(inventoryId);
    inventory.setInventoryName("Updated Inventory");

    assertThat(inventory.getInventoryId()).isEqualTo(inventoryId);
    assertThat(inventory.getInventoryName()).isEqualTo("Updated Inventory");
  }

  /**
   * Test to string with null items.
   */
  @Test
  public void testToStringWithNullItems() {
    Inventory inventory =
        Inventory.builder()
            .inventoryId(UUID.randomUUID())
            .inventoryName("Null Items Inventory")
            .items(null)
            .build();

    String toStringOutput = inventory.toString();

    assertThat(toStringOutput)
        .isEqualTo("Null Items Inventory (" + inventory.getInventoryId() + "):\n");
  }

  /**
   * Test to string with empty items.
   */
  @Test
  public void testToStringWithEmptyItems() {
    Inventory inventory =
        Inventory.builder()
            .inventoryId(UUID.randomUUID())
            .inventoryName("Empty Inventory")
            .items(Collections.emptyMap())
            .build();

    String toStringOutput = inventory.toString();

    assertThat(toStringOutput).isEqualTo("Empty Inventory (" + inventory.getInventoryId() + "):\n");
  }

  /**
   * Test to string with items.
   */
  @Test
  public void testToStringWithItems() {
    UUID inventoryId = UUID.randomUUID();
    UUID itemId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    Item item =
        Item.builder()
            .itemId(itemId)
            .itemName("Sample Item")
            .timeOfAddition(now)
            .location("A1-B2")
            .inventoryId(inventoryId)
            .quantity(10)
            .reservationStatus(false)
            .price(19.99)
            .build();

    Map<UUID, Item> items = new HashMap<>();
    items.put(itemId, item);

    Inventory inventory =
        Inventory.builder()
            .inventoryId(inventoryId)
            .inventoryName("Test Inventory With Items")
            .items(items)
            .build();

    String toStringOutput = inventory.toString();

    assertThat(toStringOutput).contains("Test Inventory With Items (" + inventoryId + "):\n");
    assertThat(toStringOutput).contains("Item Name: Sample Item");
    assertThat(toStringOutput).contains("Quantity: 10");
  }

  /**
   * Test to string with multiple items.
   */
  @Test
  public void testToStringWithMultipleItems() {
    UUID inventoryId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    Item item1 =
        Item.builder()
            .itemId(UUID.randomUUID())
            .itemName("Item 1")
            .timeOfAddition(now)
            .location("A1-B2")
            .inventoryId(inventoryId)
            .quantity(5)
            .reservationStatus(false)
            .price(10.99)
            .build();

    Item item2 =
        Item.builder()
            .itemId(UUID.randomUUID())
            .itemName("Item 2")
            .timeOfAddition(now)
            .location("B1-C3")
            .inventoryId(inventoryId)
            .quantity(15)
            .reservationStatus(true)
            .price(25.49)
            .build();

    Map<UUID, Item> items = new HashMap<>();
    items.put(item1.getItemId(), item1);
    items.put(item2.getItemId(), item2);

    Inventory inventory =
        Inventory.builder()
            .inventoryId(inventoryId)
            .inventoryName("Multiple Items Inventory")
            .items(items)
            .build();

    String toStringOutput = inventory.toString();

    assertThat(toStringOutput).contains("Multiple Items Inventory (" + inventoryId + "):\n");
    assertThat(toStringOutput).contains("Item Name: Item 1");
    assertThat(toStringOutput).contains("Quantity: 5");
    assertThat(toStringOutput).contains("Item Name: Item 2");
    assertThat(toStringOutput).contains("Quantity: 15");
  }
}

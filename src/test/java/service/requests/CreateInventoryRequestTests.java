package service.requests;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import service.models.Item;

/**
 * The type Create inventory request tests.
 */
@SpringBootTest
public class CreateInventoryRequestTests {

  /**
   * Test builder and getters.
   */
  @Test
  public void testBuilderAndGetters() {
    UUID inventoryId = UUID.randomUUID();
    String inventoryName = "Test Inventory";

    Map<UUID, Item> items = new HashMap<>();
    items.put(
        UUID.randomUUID(),
        Item.builder()
            .itemId(UUID.randomUUID())
            .itemName("Item1")
            .location("Aisle 1")
            .timeOfAddition(LocalDateTime.now())
            .inventoryId(inventoryId)
            .quantity(10)
            .price(20.0)
            .build());

    CreateInventoryRequest request =
        CreateInventoryRequest.builder()
            .inventoryId(inventoryId)
            .inventoryName(inventoryName)
            .items(items)
            .build();

    assertThat(request.getInventoryId()).isEqualTo(inventoryId);
    assertThat(request.getInventoryName()).isEqualTo(inventoryName);
    assertThat(request.getItems()).isEqualTo(items);
  }

  /**
   * Test setter for inventory name.
   */
  @Test
  public void testSetterForInventoryName() {
    CreateInventoryRequest request =
        CreateInventoryRequest.builder()
            .inventoryId(UUID.randomUUID())
            .inventoryName("Old Name")
            .items(Collections.emptyMap())
            .build();

    request.setInventoryName("New Name");
    assertThat(request.getInventoryName()).isEqualTo("New Name");
  }

  /**
   * Test builder with null items.
   */
  @Test
  public void testBuilderWithNullItems() {
    UUID inventoryId = UUID.randomUUID();
    String inventoryName = "Empty Inventory";

    CreateInventoryRequest request =
        CreateInventoryRequest.builder()
            .inventoryId(inventoryId)
            .inventoryName(inventoryName)
            .items(null)
            .build();

    assertThat(request.getInventoryId()).isEqualTo(inventoryId);
    assertThat(request.getInventoryName()).isEqualTo(inventoryName);
    assertThat(request.getItems()).isNull();
  }

  /**
   * Test empty items map.
   */
  @Test
  public void testEmptyItemsMap() {
    UUID inventoryId = UUID.randomUUID();
    String inventoryName = "Empty Items";

    CreateInventoryRequest request =
        CreateInventoryRequest.builder()
            .inventoryId(inventoryId)
            .inventoryName(inventoryName)
            .items(Collections.emptyMap())
            .build();

    assertThat(request.getInventoryId()).isEqualTo(inventoryId);
    assertThat(request.getInventoryName()).isEqualTo(inventoryName);
    assertThat(request.getItems()).isEmpty();
  }

  /**
   * Test edge cases.
   */
  @Test
  public void testEdgeCases() {
    UUID inventoryId = UUID.randomUUID();
    String inventoryName = ""; // Empty inventory name

    CreateInventoryRequest request =
        CreateInventoryRequest.builder()
            .inventoryId(inventoryId)
            .inventoryName(inventoryName)
            .items(null)
            .build();

    assertThat(request.getInventoryId()).isEqualTo(inventoryId);
    assertThat(request.getInventoryName()).isEmpty();
    assertThat(request.getItems()).isNull();
  }
}

package service.models;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/** Represents an inventory that holds a collection of items. */
@Getter
@Builder
public class Inventory {

  @NonNull @Setter private UUID inventoryId = UUID.randomUUID();

  @NonNull @Setter private String inventoryName;

  @NonNull private Map<UUID, Item> items;

  @NonNull @Setter private UUID adminId;

  /**
   * Creates a new item and adds it to the inventory.
   *
   * @param itemName the name of the item
   * @param quantity the quantity of the item
   * @param location the location of the item
   * @param price the price of the item
   */
  public void createItem(String itemName, int quantity, String location, double price) {
    Item item =
        Item.builder()
            .itemName(itemName)
            .quantity(quantity)
            .location(location)
            .price(price)
            .build();
    addItem(item);
  }

  /**
   * Adds a new item to the inventory.
   *
   * @param item the item to be added
   */
  public void addItem(Item item) {
    items.put(item.getItemId(), item);
  }

  /**
   * Removes an item from the inventory by its UUID.
   *
   * @param itemId the UUID of the item to be removed
   */
  public void removeItem(UUID itemId) {
    items.remove(itemId);
  }

  /**
   * Retrieves an item from the inventory by its UUID.
   *
   * @param itemId the UUID of the item to retrieve
   * @return the item with the specified UUID, or null if not found
   */
  public Item getItem(UUID itemId) {
    return items.get(itemId);
  }

  /**
   * Returns a collection of all items in the inventory.
   *
   * @return a collection of all items
   */
  public Collection<Item> getAllItems() {
    return items.values();
  }

  /**
   * Returns a string representation of the inventory.
   *
   * @return a string representation of the inventory
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(inventoryName).append(" (").append(inventoryId).append("):\n");
    for (Map.Entry<UUID, Item> entry : items.entrySet()) {
      Item value = entry.getValue();
      sb.append("Item Name: ")
          .append(value.getItemName())
          .append(" Quantity: ")
          .append(value.getQuantity())
          .append("\n");
    }
    return sb.toString();
  }
}

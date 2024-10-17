package service.models;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

/**
 * Represents an inventory that holds a collection of items.
 */
public class Inventory {

  private final UUID uuid;

  private String inventoryName;
  private Map<UUID, Item> items;
  private final String adminId;

  /**
   * Constructs a new Inventory with the specified administrator ID, inventory name, and items.
   *
   * @param adminId       the UUID of the administrator
   * @param inventoryName the name of the inventory
   * @param items         a map of items in the inventory
   */
  public Inventory(UUID adminId, String inventoryName, Map<UUID, Item> items) {
    this.uuid = UUID.randomUUID();
    this.inventoryName = inventoryName;
    this.items = items;
    this.adminId = adminId.toString();
  }

  /**
   * Creates a new item and adds it to the inventory.
   *
   * @param itemName the name of the item
   * @param quantity the quantity of the item
   * @param location the location of the item
   * @param price    the price of the item
   */
  public void createItem(String itemName, int quantity, String location, double price) {
    Item item = Item.builder()
            .itemName(itemName)
            .quantity(quantity)
            .location(location)
            .price(price).build();
    addItem(item);
  }

  /**
   * Returns the UUID of the inventory.
   *
   * @return the UUID of the inventory
   */
  public UUID getId() {
    return uuid;
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
   * Returns the name of the inventory.
   *
   * @return the inventory name
   */
  public String getInventoryName() {
    return inventoryName;
  }

  /**
   * Sets the name of the inventory.
   *
   * @param inventoryName the new inventory name
   */
  public void setInventoryName(String inventoryName) {
    this.inventoryName = inventoryName;
  }

  /**
   * Returns the administrator ID associated with the inventory.
   *
   * @return the administrator ID
   */
  public String getAdminId() {
    return adminId;
  }

  /**
   * Returns a string representation of the inventory.
   *
   * @return a string representing of the inventory
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(inventoryName).append(" (").append(uuid).append("):\n");
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

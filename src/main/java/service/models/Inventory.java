package service.models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/** Represents an inventory that holds a collection of items. */
@Getter
@Builder
public class Inventory {

  @NonNull
  private final UUID inventoryId = UUID.randomUUID();

  @NonNull
  @Setter
  private String inventoryName;

  @NonNull
  private Map<UUID, Item> items;

  @NonNull
  private final UUID adminId;

  /**
   * Creates a new item and adds it to the inventory.
   *
   * @param itemName the name of the item
   * @param quantity the quantity of the item
   * @param location the location of the item
   * @param price    the price of the item
   */
  public void createItem(String itemName, int quantity, String location, double price) {
    // Check if item name is empty or quantity is negative
    if (itemName == null || itemName.isEmpty() || quantity <= 0 || price < 0) {
      throw new IllegalArgumentException("Invalid item data.");
    }
    Item item = Item.builder()
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
    // if (items.containsKey(item.getItemId())) {
    // throw new IllegalArgumentException("Item already exists in the inventory.");
    // }
    items.put(item.getItemId(), item);
  }

  /**
   * Removes an item from the inventory by its UUID.
   *
   * @param itemId the UUID of the item to be removed
   */
  public void removeItem(UUID itemId) {
    // if (!items.containsKey(itemId)) {
    // throw new NoSuchElementException("Item not found.");
    // }
    items.remove(itemId);
  }

  /**
   * Retrieves an item from the inventory by its UUID.
   *
   * @param itemId the UUID of the item to retrieve
   * @return the item with the specified UUID, or null if not found
   */
  public Item getItem(UUID itemId) {
    if (items.containsKey(itemId)) {
      return items.get(itemId);
    }
    return null;
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
   * Updates the price of an item in the inventory.
   *
   * @param itemId   the UUID of the item
   * @param newPrice the new price to set
   */
  public void updateItemPrice(UUID itemId, double newPrice) {
    Item item = getItem(itemId);
    if (item != null && newPrice >= 0) {
      item.setPrice(newPrice);
    } else {
      throw new IllegalArgumentException("Invalid price or item not found.");
    }
  }

  /**
   * Updates the location of an item in the inventory.
   *
   * @param itemId      the UUID of the item
   * @param newLocation the new location of the item
   */
  public void updateItemLocation(UUID itemId, String newLocation) {
    Item item = getItem(itemId);
    if (item != null && newLocation != null && !newLocation.isEmpty()) {
      item.setLocation(newLocation);
    } else {
      throw new IllegalArgumentException("Invalid location or item not found.");
    }
  }

  /**
   * Updates an item's quantity in the inventory.
   *
   * @param itemId      the UUID of the item
   * @param newQuantity the new quantity to set
   */
  public void updateItemQuantity(UUID itemId, int newQuantity) {
    Item item = getItem(itemId);
    if (item != null && newQuantity >= 0) {
      item.setItemQuantity(newQuantity);
    } else {
      throw new IllegalArgumentException("Invalid quantity or item not found.");
    }
  }

  /**
   * Deletes an item from the inventory if it exists.
   *
   * @param itemId the UUID of the item to be deleted
   * @return true if the item was found and deleted, false otherwise
   */
  public boolean deleteItem(UUID itemId) {
    if (items.containsKey(itemId)) {
      items.remove(itemId);
      return true;
    }
    return false;
  }

  /**
   * Checks if an item is in stock.
   *
   * @param itemId the UUID of the item
   * @return true if the item is in stock, false otherwise
   */
  public boolean isInStock(UUID itemId) {
    Item item = getItem(itemId);
    return item != null && item.getQuantity() > 0;
  }

  /**
   * Adds stock to an item in the inventory.
   *
   * @param itemId             the UUID of the item
   * @param additionalQuantity the amount of stock to add
   */
  public void restockItem(UUID itemId, int additionalQuantity) {
    Item item = getItem(itemId);
    if (item != null && additionalQuantity > 0) {
      item.addItemQuantity(additionalQuantity);
    } else {
      throw new IllegalArgumentException("Invalid quantity or item not found.");
    }
  }

  /**
   * Reserves an item for a specific duration if it's available.
   *
   * @param itemId              the UUID of the item
   * @param reservationDuration the duration for the reservation
   */
  public void reserveItem(UUID itemId, Duration reservationDuration) {
    Item item = getItem(itemId);
    if (item != null && item.getQuantity() > 0) {
      item.setReservationStatus(true);
      item.setReservationDuration(reservationDuration);
      item.setReservationTime(LocalDateTime.now());
    } else {
      throw new IllegalStateException("Item is out of stock or not found.");
    }
  }

  /**
   * Unreserves an item, making it available again.
   *
   * @param itemId the UUID of the item
   */
  public void unreserveItem(UUID itemId) {
    Item item = getItem(itemId);
    if (item != null) {
      item.setReservationStatus(false);
      item.setReservationDuration(Duration.ZERO);
      item.setReservationTime(null);
    } else {
      throw new NoSuchElementException("Item not found.");
    }
  }

  /**
   * Checks if an item is reserved.
   *
   * @param itemId the UUID of the item
   * @return true if the item is reserved, false otherwise
   */
  public boolean isItemReserved(UUID itemId) {
    Item item = getItem(itemId);
    return item != null && item.isReservationStatus();
  }

  /**
   * Searches for items by a partial name match.
   *
   * @param partialName the partial name of the item to search for
   * @return a list of items whose names contain the partialName
   *         (case-insensitive)
   */
  public List<Item> searchItemsByName(String partialName) {
    List<Item> matchingItems = new ArrayList<>();
    for (Item item : items.values()) {
      if (item.getItemName().toLowerCase().contains(partialName.toLowerCase())) {
        matchingItems.add(item);
      }
    }
    return matchingItems;
  }

  /**
   * Returns a string representation of the inventory.
   *
   * @return a string representation of all items
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

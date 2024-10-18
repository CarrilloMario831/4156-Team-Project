package service.models;

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
  
  @NonNull @Setter private UUID inventoryId;

  @NonNull @Setter private String inventoryName;

  private Map<UUID, Item> items;

  @NonNull @Setter private UUID adminId;

  /**
   * Returns a string representation of the inventory.
   *
   * @return a string representation of the inventory
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append(inventoryName).append(" (").append(inventoryId).append("):\n");
    if (this.items == null) {
      return sb.toString();
    }
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

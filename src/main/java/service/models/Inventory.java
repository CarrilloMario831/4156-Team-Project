package service.models;

import java.util.UUID;
import lombok.Getter;

/** Sample javadoc for Inventory class (implemented later). */
@Getter
public class Inventory {
  private final UUID inventoryId;

  /** Inventory constructor. */
  public Inventory(UUID inventoryId) {
    this.inventoryId = inventoryId;

    // TODO: Add logic to retreive Inventory from DB
  }
}

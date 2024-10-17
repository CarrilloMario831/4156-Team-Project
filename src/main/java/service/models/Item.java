package service.models;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/** Represents an Item in the Reservation/Management Service. */
@Getter
@Builder
public class Item {

  @NonNull private final UUID itemId;

  @NonNull @Setter private String itemName;

  @NonNull @Setter private LocalDateTime timeOfAddition;

  @NonNull @Setter private String location;

  private int quantity;

  @Setter private boolean reservationStatus;
  @Setter private Duration reservationDuration;
  @Setter private LocalDateTime reservationTime;
  @Setter private double price;
  @Setter private LocalDateTime nextRestockDateTime;

  /** Item builder. */
  public static class ItemBuilder {
    /** Item builder. */
    public ItemBuilder() {
      this.itemId = UUID.randomUUID(); // Automatically generate itemId
      this.timeOfAddition = LocalDateTime.now(); // Automatically set time of addition
      this.reservationStatus = false; // Default reservation status
      this.reservationDuration = Duration.ZERO; // Default reservation duration
    }
  }

  /**
   * Adjusts the quantity of the item by the specified amount. Positive values increase the
   * quantity, negative values decrease it.
   *
   * @param adjustment the amount to adjust the quantity by
   */
  public void adjustQuantity(int adjustment) {
    this.quantity += adjustment;
    if (this.quantity < 0) {
      this.quantity = 0; // Ensures quantity doesn't go negative
    }
  }

  /**
   * Adds the specified number of items to the current quantity.
   *
   * @param numberOfItems the number of items to add
   */
  public void addItemQuantity(int numberOfItems) {
    adjustQuantity(numberOfItems);
  }

  /**
   * Sets the quantity of the item.
   *
   * @param quantity the new quantity; if negative, quantity is set to 0
   */
  public void setItemQuantity(int quantity) {
    this.quantity = Math.max(quantity, 0);
  }

  /**
   * Removes the specified number of items from the current quantity.
   *
   * @param numberOfItems the number of items to remove
   */
  public void removeItems(int numberOfItems) {
    adjustQuantity(-numberOfItems);
  }

  /**
   * Returns a string representation of the item.
   *
   * @return a string representation of the item
   */
  @Override
  public String toString() {
    return "\nItem: "
        + itemName
        + "\nLocation: "
        + location
        + "\nQuantity: "
        + quantity
        + "\nReservation Status: "
        + reservationStatus;
  }
}

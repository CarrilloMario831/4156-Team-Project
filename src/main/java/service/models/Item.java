package service.models;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/** Represents an Item in the Reservation/Management Service. */
@Getter
@Setter
@Builder
public class Item {

  @NonNull private UUID itemId;

  @NonNull private String itemName;

  @NonNull private LocalDateTime timeOfAddition;

  @NonNull private String location;

  @NonNull private UUID inventoryId;

  private int quantity;

  private boolean reservationStatus;
  private long reservationDurationInMillis;
  private LocalDateTime reservationTime;
  private double price;
  private LocalDateTime nextRestockDateTime;

  /**
   * Returns a string representation of the item.
   *
   * @return a string representation of the item
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Item Details:\n");
    sb.append("UUID: ").append(itemId.toString()).append("\n");
    sb.append("Name: ").append(itemName).append("\n");
    sb.append("Location: ").append(location).append("\n");
    sb.append("Quantity: ").append(quantity).append("\n");
    sb.append("Price: $").append(price).append("\n");
    sb.append("Reservation Status: ")
        .append(reservationStatus ? "Reserved" : "Available")
        .append("\n");

    if (reservationStatus) {
      sb.append("Reservation Duration: ")
          .append(reservationDurationInMillis)
          .append(" milliseconds\n");
      sb.append("Reservation Time: ").append(reservationTime.toString()).append("\n");
    }

    if (nextRestockDateTime != null) {
      sb.append("Next Restock Date: ").append(nextRestockDateTime).append("\n");
    }

    return sb.toString();
  }
}

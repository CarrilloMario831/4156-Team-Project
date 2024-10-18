package service.requests;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

/** Represents an Item in the Reservation/Management Service. */
@Getter
@Builder
public class CreateItemRequest {

  @NonNull private String itemName;

  @NonNull private String location;

  private UUID inventoryId;

  private int quantity;

  private boolean reservationStatus;
  private long reservationDurationInMillis;
  private LocalDateTime reservationTime;
  private double price;
  private LocalDateTime nextRestockDateTime;
}

package service.requests;

import java.util.Map;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import service.models.Item;

/** Represents an Inventory in the Reservation/Management Service. */
@Getter
@Builder
public class CreateInventoryRequest {

  private UUID inventoryId;

  @NonNull @Setter private String inventoryName;

  private Map<UUID, Item> items;

  @NonNull @Setter private UUID adminId;
}

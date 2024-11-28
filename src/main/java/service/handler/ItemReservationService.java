package service.handler;

import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import service.exceptions.ReservationException;
import service.models.Item;

@Service
@Getter
public class ItemReservationService {

  private final ItemsTableSqlHelper itemsTableSqlHelper;
  private final ScheduledExecutorService scheduler;

  @Autowired
  public ItemReservationService(ItemsTableSqlHelper itemsTableSqlHelper) {
    this.itemsTableSqlHelper = itemsTableSqlHelper;
    this.scheduler = Executors.newScheduledThreadPool(1);
  }

  /**
   * Reserve an item or a specific quantity of items.
   *
   * @param itemId The ID of the item to reserve
   * @param quantity The quantity to reserve (defaults to 1)
   * @param durationInMillis How long the reservation should last
   * @return The updated Item object
   * @throws ReservationException if the reservation cannot be made
   */
  public Item reserveItem(String itemId, int quantity, long durationInMillis)
      throws ReservationException {
    var items = itemsTableSqlHelper.getItem(itemId);
    if (items.isEmpty()) {
      throw new ReservationException("Item not found: " + itemId);
    }

    Item item = items.get(0);

    if (item.isReservationStatus() && item.getQuantity() < quantity) {
      throw new ReservationException("Item is not available in requested quantity");
    }

    LocalDateTime reservationTime = LocalDateTime.now();
    item.setReservationStatus(true);
    item.setReservationTime(reservationTime);
    item.setReservationDurationInMillis(durationInMillis);
    item.setQuantity(item.getQuantity() - quantity);

    boolean updated = itemsTableSqlHelper.updateItemReservation(itemId, durationInMillis);
    if (!updated) {
      throw new ReservationException("Failed to update reservation status");
    }

    scheduleReservationExpiration(itemId, quantity, durationInMillis);

    return item;
  }

  /**
   * Extend an existing reservation.
   *
   * @param itemId The ID of the reserved item
   * @param additionalDurationInMillis How much longer the reservation should last
   * @return The updated Item object
   * @throws ReservationException if the extension cannot be made
   */
  public Item extendReservation(String itemId, long additionalDurationInMillis)
      throws ReservationException {
    var items = itemsTableSqlHelper.getItem(itemId);
    if (items.isEmpty()) {
      throw new ReservationException("Item not found: " + itemId);
    }

    Item item = items.get(0);

    if (!item.isReservationStatus()) {
      throw new ReservationException("Item is not currently reserved");
    }

    long newDuration = item.getReservationDurationInMillis() + additionalDurationInMillis;

    boolean updated = itemsTableSqlHelper.updateItemReservation(itemId, newDuration);
    if (!updated) {
      throw new ReservationException("Failed to extend reservation");
    }

    scheduleReservationExpiration(itemId, 1, newDuration);

    item.setReservationDurationInMillis(newDuration);
    return item;
  }

  /**
   * Cancel a reservation before it expires.
   *
   * @param itemId The ID of the reserved item
   * @param quantity The quantity to return to available stock
   * @return The updated Item object
   * @throws ReservationException if the cancellation cannot be completed
   */
  public Item cancelReservation(String itemId, int quantity) throws ReservationException {
    var items = itemsTableSqlHelper.getItem(itemId);
    if (items.isEmpty()) {
      throw new ReservationException("Item not found: " + itemId);
    }

    Item item = items.get(0);

    if (!item.isReservationStatus()) {
      throw new ReservationException("Item is not currently reserved");
    }

    item.setQuantity(item.getQuantity() + quantity);

    boolean quantityUpdated = itemsTableSqlHelper.updateItemQuantity(itemId, item.getQuantity());
    if (!quantityUpdated) {
      throw new ReservationException("Failed to update item quantity");
    }

    boolean cancelled = itemsTableSqlHelper.cancelItemReservation(itemId);
    if (!cancelled) {
      throw new ReservationException("Failed to cancel reservation");
    }

    item.setReservationStatus(false);
    item.setReservationTime(null);
    item.setReservationDurationInMillis(0);

    return item;
  }

  private void scheduleReservationExpiration(String itemId, int quantity, long durationInMillis) {
    scheduler.schedule(
        () -> {
          try {
            var items = itemsTableSqlHelper.getItem(itemId);
            if (!items.isEmpty() && items.get(0).isReservationStatus()) {
              cancelReservation(itemId, quantity);
            }
          } catch (ReservationException e) {
            System.err.println("Failed to expire reservation: " + e.getMessage());
          }
        },
        durationInMillis,
        TimeUnit.MILLISECONDS);
  }
}

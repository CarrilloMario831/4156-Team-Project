package service.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.exceptions.ReservationException;
import service.handler.ItemReservationService;
import service.models.Item;

@RestController
@RequestMapping("/api/reservations")
public class ReservationRouteController {

  @Autowired private ItemReservationService reservationService;

  /**
   * Create a new reservation for specified quantity of an item.
   *
   * @param itemId The ID of the item to reserve
   * @param quantity The quantity to reserve
   * @param durationInMillis Duration of the reservation in milliseconds
   * @return ResponseEntity containing the reservation result
   */
  @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> createReservation(
      @RequestParam(value = "itemId") String itemId,
      @RequestParam(value = "quantity") int quantity,
      @RequestParam(value = "durationInMillis") long durationInMillis) {

    if (itemId == null || itemId.isEmpty()) {
      return new ResponseEntity<>("itemId is required", HttpStatus.BAD_REQUEST);
    }

    if (quantity <= 0) {
      return new ResponseEntity<>("Quantity must be greater than 0", HttpStatus.BAD_REQUEST);
    }

    if (durationInMillis <= 0) {
      return new ResponseEntity<>("Duration must be greater than 0", HttpStatus.BAD_REQUEST);
    }

    try {
      Item item = reservationService.reserveItem(itemId, quantity, durationInMillis);

      return new ResponseEntity<>(
          String.format(
              "Successfully reserved %d units of %s. Remaining quantity: %d",
              quantity, item.getItemName(), item.getQuantity()),
          HttpStatus.OK);

    } catch (ReservationException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      return new ResponseEntity<>(
          "Failed to create reservation: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Extend an existing reservation.
   *
   * @param itemId The ID of the reserved item
   * @param additionalDurationInMillis Additional duration in milliseconds
   * @return ResponseEntity containing the extension result
   */
  @PatchMapping(value = "/extend", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> extendReservation(
      @RequestParam(value = "itemId") String itemId,
      @RequestParam(value = "additionalDurationInMillis") long additionalDurationInMillis) {

    if (itemId == null || itemId.isEmpty()) {
      return new ResponseEntity<>("itemId is required", HttpStatus.BAD_REQUEST);
    }

    if (additionalDurationInMillis <= 0) {
      return new ResponseEntity<>(
          "Additional duration must be greater than 0", HttpStatus.BAD_REQUEST);
    }

    try {
      Item item = reservationService.extendReservation(itemId, additionalDurationInMillis);

      return new ResponseEntity<>(
          String.format(
              "Successfully extended reservation for %s. New duration: %d ms",
              item.getItemName(), item.getReservationDurationInMillis()),
          HttpStatus.OK);

    } catch (ReservationException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      return new ResponseEntity<>(
          "Failed to extend reservation: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Cancel a reservation and return items to available inventory.
   *
   * @param itemId The ID of the reserved item
   * @param quantity The quantity to return to inventory
   * @return ResponseEntity containing the cancellation result
   */
  @DeleteMapping(value = "/cancel", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> cancelReservation(
      @RequestParam(value = "itemId") String itemId,
      @RequestParam(value = "quantity") int quantity) {

    if (itemId == null || itemId.isEmpty()) {
      return new ResponseEntity<>("itemId is required", HttpStatus.BAD_REQUEST);
    }

    if (quantity <= 0) {
      return new ResponseEntity<>("Quantity must be greater than 0", HttpStatus.BAD_REQUEST);
    }

    try {
      Item item = reservationService.cancelReservation(itemId, quantity);

      return new ResponseEntity<>(
          String.format(
              "Successfully cancelled reservation for %d units of %s. Available quantity: %d",
              quantity, item.getItemName(), item.getQuantity()),
          HttpStatus.OK);

    } catch (ReservationException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      return new ResponseEntity<>(
          "Failed to cancel reservation: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Get detailed reservation status of an item.
   *
   * @param itemId The ID of the item to check
   * @return ResponseEntity containing the reservation status
   */
  @GetMapping(value = "/status", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getReservationStatus(
      @RequestParam(value = "itemId") String itemId) {

    if (itemId == null || itemId.isEmpty()) {
      return new ResponseEntity<>("itemId is required", HttpStatus.BAD_REQUEST);
    }

    try {
      List<Item> items = reservationService.getItemsTableSqlHelper().getItem(itemId);
      if (items.isEmpty()) {
        return new ResponseEntity<>("Item not found", HttpStatus.NOT_FOUND);
      }

      Item item = items.get(0);
      String status =
          String.format(
              "Item: %s\n"
                  + "Total Quantity: %d\n"
                  + "Reserved: %s\n"
                  + "Reservation Time: %s\n"
                  + "Reservation Duration: %d ms",
              item.getItemName(),
              item.getQuantity(),
              item.isReservationStatus(),
              item.getReservationTime() != null ? item.getReservationTime().toString() : "N/A",
              item.getReservationDurationInMillis());

      return new ResponseEntity<>(status, HttpStatus.OK);

    } catch (Exception e) {
      return new ResponseEntity<>(
          "Failed to get reservation status: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}

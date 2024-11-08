package service.controller;

import static service.util.DateTimeUtils.FORMATTER;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import service.handler.ItemsTableSqlHelper;
import service.models.Item;
import service.requests.CreateItemRequest;

/** This class contains all the API endpoints for user-related requests. */
@RestController
@RequestMapping("/api/items")
public class ItemsRouteController {

  /** The Items table sql helper. */
  @Autowired public ItemsTableSqlHelper itemsTableSqlHelper;

  /**
   * Create item response entity.
   *
   * @param createItemRequest the create item request
   * @return the response entity
   */
  @PostMapping(value = "/createItem", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> createItem(@RequestBody CreateItemRequest createItemRequest) {
    if (createItemRequest == null) {
      return new ResponseEntity<>("Empty request", HttpStatus.BAD_REQUEST);
    }
    try {
      Item newItem =
          Item.builder()
              .itemId(UUID.randomUUID())
              .itemName(createItemRequest.getItemName())
              .timeOfAddition(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
              .location(createItemRequest.getLocation())
              .inventoryId(createItemRequest.getInventoryId())
              .quantity(createItemRequest.getQuantity())
              .price(createItemRequest.getPrice())
              .nextRestockDateTime(createItemRequest.getNextRestockDateTime())
              .reservationStatus(createItemRequest.isReservationStatus())
              .reservationTime(createItemRequest.getReservationTime())
              .reservationDurationInMillis(createItemRequest.getReservationDurationInMillis())
              .build();

      boolean isSuccessful = itemsTableSqlHelper.insertItem(newItem);

      if (!isSuccessful) {
        return new ResponseEntity<>("Failed to create item", HttpStatus.INTERNAL_SERVER_ERROR);
      }

      return new ResponseEntity<>(
          "Successfully created item: " + newItem.getItemId() + "\n" + newItem.getItemName(),
          HttpStatus.CREATED);

    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Gets item name.
   *
   * @param itemId the item id
   * @return the item name
   */
  @GetMapping(value = "/getItemName", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getItemName(@RequestParam(value = "itemId") String itemId) {
    if (itemId == null || itemId.isEmpty()) {
      return new ResponseEntity<>("itemId needed to get item name.", HttpStatus.BAD_REQUEST);
    }
    try {
      List<Item> item = itemsTableSqlHelper.getItem(itemId);
      if (item == null || item.isEmpty()) {
        return new ResponseEntity<>(
            "Item with itemId: " + itemId + " was not found.", HttpStatus.NOT_FOUND);
      }
      String itemName = item.get(0).getItemName();
      if (itemName.isEmpty()) {
        return new ResponseEntity<>("Item " + itemId + " has no item name.", HttpStatus.NO_CONTENT);
      }
      return new ResponseEntity<>(itemName, HttpStatus.OK);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Gets item time of addition.
   *
   * @param itemId the item id
   * @return the item time of addition
   */
  @GetMapping(value = "/getItemTimeOfAddition", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getItemTimeOfAddition(
      @RequestParam(value = "itemId") String itemId) {
    if (itemId == null || itemId.isEmpty()) {
      return new ResponseEntity<>("itemId needed to get item name.", HttpStatus.BAD_REQUEST);
    }
    try {
      List<Item> item = itemsTableSqlHelper.getItem(itemId);
      if (item == null || item.isEmpty()) {
        return new ResponseEntity<>(
            "Item with itemId: " + itemId + " was not found", HttpStatus.NOT_FOUND);
      }
      LocalDateTime timeOfAddition = item.get(0).getTimeOfAddition();
      return new ResponseEntity<>(timeOfAddition.format(FORMATTER), HttpStatus.OK);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Gets item quantity.
   *
   * @param itemId the item id
   * @return the item quantity
   */
  @GetMapping(value = "/getItemQuantity", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getItemQuantity(@RequestParam(value = "itemId") String itemId) {
    if (itemId == null || itemId.isEmpty()) {
      return new ResponseEntity<>("itemId needed to get item name.", HttpStatus.BAD_REQUEST);
    }
    try {
      List<Item> item = itemsTableSqlHelper.getItem(itemId);
      if (item == null || item.isEmpty()) {
        return new ResponseEntity<>(
            "Item with itemId: " + itemId + " was not found", HttpStatus.NOT_FOUND);
      }
      int itemQuantity = item.get(0).getQuantity();
      return new ResponseEntity<>(itemQuantity, HttpStatus.OK);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Is item reserved response entity.
   *
   * @param itemId the item id
   * @return the response entity
   */
  @GetMapping(value = "/isItemReserved", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> isItemReserved(@RequestParam(value = "itemId") String itemId) {
    if (itemId == null || itemId.isEmpty()) {
      return new ResponseEntity<>("itemId needed to get item name.", HttpStatus.BAD_REQUEST);
    }
    try {
      List<Item> item = itemsTableSqlHelper.getItem(itemId);
      if (item == null || item.isEmpty()) {
        return new ResponseEntity<>(
            "Item with itemId: " + itemId + " was not found", HttpStatus.NOT_FOUND);
      }
      return new ResponseEntity<>(item.get(0).isReservationStatus(), HttpStatus.OK);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Gets item reservation duration.
   *
   * @param itemId the item id
   * @return the item reservation duration
   */
  @GetMapping(value = "/getItemReservationDuration", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getItemReservationDuration(
      @RequestParam(value = "itemId") String itemId) {
    if (itemId == null || itemId.isEmpty()) {
      return new ResponseEntity<>("itemId needed to get item name.", HttpStatus.BAD_REQUEST);
    }
    try {
      List<Item> item = itemsTableSqlHelper.getItem(itemId);
      if (item == null || item.isEmpty()) {
        return new ResponseEntity<>(
            "Item with itemId: " + itemId + " was not found", HttpStatus.NOT_FOUND);
      }
      return new ResponseEntity<>(item.get(0).getReservationDurationInMillis(), HttpStatus.OK);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Gets item reservation time.
   *
   * @param itemId the item id
   * @return the item reservation time
   */
  @GetMapping(value = "/getItemReservationTime", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getItemReservationTime(
      @RequestParam(value = "itemId") String itemId) {
    if (itemId == null || itemId.isEmpty()) {
      return new ResponseEntity<>("itemId needed to get item name.", HttpStatus.BAD_REQUEST);
    }
    try {
      List<Item> item = itemsTableSqlHelper.getItem(itemId);
      if (item == null || item.isEmpty()) {
        return new ResponseEntity<>(
            "Item with itemId: " + itemId + " was not found", HttpStatus.NOT_FOUND);
      }
      LocalDateTime timeOfReservation = item.get(0).getReservationTime();
      return new ResponseEntity<>(timeOfReservation.format(FORMATTER), HttpStatus.OK);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Gets item location.
   *
   * @param itemId the item id
   * @return the item location
   */
  @GetMapping(value = "/getItemLocation", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getItemLocation(@RequestParam(value = "itemId") String itemId) {
    if (itemId == null || itemId.isEmpty()) {
      return new ResponseEntity<>("itemId needed to get item name.", HttpStatus.BAD_REQUEST);
    }
    try {
      List<Item> item = itemsTableSqlHelper.getItem(itemId);
      if (item == null || item.isEmpty()) {
        return new ResponseEntity<>(
            "Item with itemId: " + itemId + " was not found", HttpStatus.NOT_FOUND);
      }
      return new ResponseEntity<>(item.get(0).getLocation(), HttpStatus.OK);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Gets item price.
   *
   * @param itemId the item id
   * @return the item price
   */
  @GetMapping(value = "/getItemPrice", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> getItemPrice(@RequestParam(value = "itemId") String itemId) {
    if (itemId == null || itemId.isEmpty()) {
      return new ResponseEntity<>("itemId needed to get item name.", HttpStatus.BAD_REQUEST);
    }
    try {
      List<Item> item = itemsTableSqlHelper.getItem(itemId);
      if (item == null || item.isEmpty()) {
        return new ResponseEntity<>(
            "Item with itemId: " + itemId + " was not found", HttpStatus.NOT_FOUND);
      }
      return new ResponseEntity<>(item.get(0).getPrice(), HttpStatus.OK);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Gets next restock time.
   *
   * @param itemId the item id
   * @return the next restock time
   */
  @GetMapping(value = "/getNextRestockTime", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getNextRestockTime(@RequestParam(value = "itemId") String itemId) {
    if (itemId == null || itemId.isEmpty()) {
      return new ResponseEntity<>("itemId needed to get item name.", HttpStatus.BAD_REQUEST);
    }
    try {
      List<Item> itemList = itemsTableSqlHelper.getItem(itemId);
      if (itemList == null || itemList.isEmpty()) {
        return new ResponseEntity<>(
            "Item with itemId: " + itemId + " was not found", HttpStatus.NOT_FOUND);
      }
      Item item = itemList.get(0);
      LocalDateTime nextRestockTime = item.getNextRestockDateTime();
      if (nextRestockTime == null) {
        return new ResponseEntity<>(
            "No restock time available for item: " + item.getItemName(), HttpStatus.NO_CONTENT);
      }
      return new ResponseEntity<>(nextRestockTime.format(FORMATTER), HttpStatus.OK);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Gets inventory id from item id.
   *
   * @param itemId the item id
   * @return the inventory id from item id
   */
  @GetMapping(value = "/getInventoryIdFromItemId", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getInventoryIdFromItemId(
      @RequestParam(value = "itemId") String itemId) {
    if (itemId == null || itemId.isEmpty()) {
      return new ResponseEntity<>("itemId needed to get item name.", HttpStatus.BAD_REQUEST);
    }
    try {
      List<Item> itemList = itemsTableSqlHelper.getItem(itemId);
      if (itemList == null || itemList.isEmpty()) {
        return new ResponseEntity<>(
            "Item with itemId: " + itemId + " was not found", HttpStatus.NOT_FOUND);
      }
      Item item = itemList.get(0);
      UUID inventoryId = item.getInventoryId();
      if (inventoryId == null) {
        return new ResponseEntity<>(
            "No inventory id found for item: " + item.getItemName(), HttpStatus.NOT_FOUND);
      }
      return new ResponseEntity<>(inventoryId.toString(), HttpStatus.OK);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Update item name response entity.
   *
   * @param itemId the item id
   * @param newItemName the new item name
   * @return the response entity
   */
  @PatchMapping(value = "/updateItemName", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> updateItemName(
      @RequestParam(value = "itemId") String itemId,
      @RequestParam(value = "newItemName") String newItemName) {
    if (itemId == null || itemId.isEmpty()) {
      return new ResponseEntity<>("itemId is needed to update item name.", HttpStatus.BAD_REQUEST);
    }

    if (newItemName == null || newItemName.isEmpty()) {
      return new ResponseEntity<>("Item name cannot be empty.", HttpStatus.BAD_REQUEST);
    }
    List<Item> itemList;
    try {
      itemList = itemsTableSqlHelper.getItem(itemId);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    if (itemList == null || itemList.isEmpty()) {
      return new ResponseEntity<>(
          "Item with itemId: " + itemId + " was not found.", HttpStatus.NOT_FOUND);
    }

    if (itemList.size() > 1) {
      return new ResponseEntity<>(
          "There are multiple items with itemID: " + itemId, HttpStatus.CONFLICT);
    }

    Item item = itemList.get(0);
    if (item == null) {
      return new ResponseEntity<>("No item found for itemID: " + itemId, HttpStatus.NOT_FOUND);
    }

    if (item.getItemName().equals(newItemName)) {
      return new ResponseEntity<>(
          "Item itemID: " + itemId + "\nalready has the name: " + newItemName,
          HttpStatus.BAD_REQUEST);
    }
    boolean isSuccessful;
    try {
      isSuccessful = itemsTableSqlHelper.updateItemName(itemId, newItemName);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    if (isSuccessful) {
      return new ResponseEntity<>(
          "Item: "
              + item.getItemId()
              + "\nwas successfully renamed. \n"
              + item.getItemName()
              + " --> "
              + newItemName,
          HttpStatus.OK);
    }

    return new ResponseEntity<>(
        "Item with itemID: " + item.getItemId() + " could not be updated.",
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Delete item response entity.
   *
   * @param itemId the item id
   * @return the response entity
   */
  @DeleteMapping(value = "/deleteItem", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> deleteItem(@RequestParam(value = "itemId") String itemId) {
    if (itemId == null || itemId.isEmpty()) {
      return new ResponseEntity<>("itemId needed to get item name.", HttpStatus.BAD_REQUEST);
    }
    List<Item> itemList;
    try {
      itemList = itemsTableSqlHelper.getItem(itemId);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    if (itemList == null || itemList.isEmpty()) {
      return new ResponseEntity<>(
          "Item with item id: " + itemId + " does not exist.", HttpStatus.NOT_FOUND);
    }

    if (itemList.size() > 1) {
      return new ResponseEntity<>(
          "There are multiple items with itemID: " + itemId, HttpStatus.CONFLICT);
    }

    Item item = itemList.get(0);
    if (item == null) {
      return new ResponseEntity<>("No item found for itemID: " + itemId, HttpStatus.NOT_FOUND);
    }
    boolean isSuccessful;
    try {
      isSuccessful = itemsTableSqlHelper.deleteItem(itemId);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    if (isSuccessful) {
      return new ResponseEntity<>(
          "ItemID: "
              + item.getItemId()
              + "\n\""
              + item.getItemName()
              + "\" was successfully deleted.",
          HttpStatus.OK);
    }

    return new ResponseEntity<>(
        "Item with itemID: " + item.getItemId() + " could not be deleted.",
        HttpStatus.INTERNAL_SERVER_ERROR);
  }

  /**
   * Update item quantity response entity.
   *
   * @param itemId the item id
   * @param newQuantity the new quantity
   * @return the response entity
   */
  @PatchMapping(value = "/updateItemQuantity", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> updateItemQuantity(
      @RequestParam(value = "itemId") String itemId,
      @RequestParam(value = "newQuantity") int newQuantity) {
    if (itemId == null || itemId.isEmpty()) {
      return new ResponseEntity<>("itemId needed to update quantity.", HttpStatus.BAD_REQUEST);
    }
    if (newQuantity < 0) {
      return new ResponseEntity<>(
          "Item quantity cannot be a negative number.", HttpStatus.BAD_REQUEST);
    }
    try {
      List<Item> itemList = itemsTableSqlHelper.getItem(itemId);
      if (itemList == null || itemList.isEmpty()) {
        return new ResponseEntity<>(
            "Item with itemId: " + itemId + " was not found", HttpStatus.NOT_FOUND);
      }
      Item item = itemList.get(0);

      if (item == null) {
        return new ResponseEntity<>("No item found for itemID: " + itemId, HttpStatus.NOT_FOUND);
      }
      int oldQuantity = item.getQuantity();
      if (oldQuantity == newQuantity) {
        return new ResponseEntity<>(
            "Item \"" + item.getItemName() + "\" already has a quantity of: " + oldQuantity,
            HttpStatus.CONFLICT);
      }
      boolean isSuccess = itemsTableSqlHelper.updateItemQuantity(itemId, newQuantity);

      if (!isSuccess) {
        return new ResponseEntity<>(
            "Could not update quantity for item: \"" + item.getItemName() + "\"",
            HttpStatus.INTERNAL_SERVER_ERROR);
      }

      return new ResponseEntity<>(
          "Item: "
              + item.getItemId()
              + "\nName: "
              + item.getItemName()
              + "\nQuantity was successfully updated. \n"
              + oldQuantity
              + " --> "
              + newQuantity,
          HttpStatus.OK);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Update item location response entity.
   *
   * @param itemId the item id
   * @param newLocation the new location
   * @return the response entity
   */
  @PatchMapping(value = "/updateItemLocation", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> updateItemLocation(
      @RequestParam(value = "itemId") String itemId,
      @RequestParam(value = "newLocation") String newLocation) {
    if (itemId == null || itemId.isEmpty()) {
      return new ResponseEntity<>("itemId needed to update quantity.", HttpStatus.BAD_REQUEST);
    }
    if (newLocation == null || newLocation.isEmpty()) {
      return new ResponseEntity<>("Location cannot be empty.", HttpStatus.BAD_REQUEST);
    }
    try {
      List<Item> itemList = itemsTableSqlHelper.getItem(itemId);
      if (itemList == null || itemList.isEmpty()) {
        return new ResponseEntity<>(
            "Item with itemId: " + itemId + " was not found", HttpStatus.NOT_FOUND);
      }
      Item item = itemList.get(0);
      if (item == null) {
        return new ResponseEntity<>("No item found for itemID: " + itemId, HttpStatus.NOT_FOUND);
      }
      String oldLocation = item.getLocation();
      if (oldLocation.equals(newLocation)) {
        return new ResponseEntity<>(
            "Item \""
                + item.getItemName()
                + "\" already has a location of: \""
                + oldLocation
                + "\"",
            HttpStatus.CONFLICT);
      }
      boolean isSuccess = itemsTableSqlHelper.updateItemLocation(itemId, newLocation);

      if (!isSuccess) {
        return new ResponseEntity<>(
            "Could not update location for item: \"" + item.getItemName() + "\"",
            HttpStatus.INTERNAL_SERVER_ERROR);
      }

      return new ResponseEntity<>(
          "Item: "
              + item.getItemId()
              + "\nName: "
              + item.getItemName()
              + "\nLocation was successfully updated. \n\""
              + oldLocation
              + "\" --> \""
              + newLocation
              + "\"",
          HttpStatus.OK);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Update item price response entity.
   *
   * @param itemId the item id
   * @param newPrice the new price
   * @return the response entity
   */
  @PatchMapping(value = "/updateItemPrice", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> updateItemPrice(
      @RequestParam(value = "itemId") String itemId,
      @RequestParam(value = "newPrice") double newPrice) {
    if (itemId == null || itemId.isEmpty()) {
      return new ResponseEntity<>("itemId needed to update price.", HttpStatus.BAD_REQUEST);
    }
    if (newPrice < 0) {
      return new ResponseEntity<>("Item price cannot be negative.", HttpStatus.BAD_REQUEST);
    }
    try {
      List<Item> itemList = itemsTableSqlHelper.getItem(itemId);
      if (itemList == null || itemList.isEmpty()) {
        return new ResponseEntity<>(
            "Item with itemId: " + itemId + " was not found", HttpStatus.NOT_FOUND);
      }
      Item item = itemList.get(0);
      if (item == null) {
        return new ResponseEntity<>("No item found for itemID: " + itemId, HttpStatus.NOT_FOUND);
      }
      double oldPrice = item.getPrice();
      if (oldPrice == newPrice) {
        return new ResponseEntity<>(
            "Item \"" + item.getItemName() + "\" already has a price of: " + oldPrice,
            HttpStatus.CONFLICT);
      }
      boolean isSuccess = itemsTableSqlHelper.updateItemPrice(itemId, newPrice);

      if (!isSuccess) {
        return new ResponseEntity<>(
            "Could not update price for item: \"" + item.getItemName() + "\"",
            HttpStatus.INTERNAL_SERVER_ERROR);
      }

      return new ResponseEntity<>(
          "Item: "
              + item.getItemId()
              + "\nName: "
              + item.getItemName()
              + "\nPrice was successfully updated. \n"
              + oldPrice
              + " --> "
              + newPrice,
          HttpStatus.OK);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Updates the reservation duration for the specified item. @param itemId the item id
   *
   * @param itemId the item id
   * @param reservationDurationInMillis the reservation duration in millis
   * @return the response entity
   */
  @PatchMapping(value = "/updateItemReservation", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> updateItemReservation(
      @RequestParam(value = "itemId") String itemId,
      @RequestParam(value = "reservationDurationInMillis") long reservationDurationInMillis) {
    if (itemId == null || itemId.isEmpty()) {
      return new ResponseEntity<>("itemId needed to update reservation.", HttpStatus.BAD_REQUEST);
    }
    if (reservationDurationInMillis < 0) {
      return new ResponseEntity<>(
          "Reservation duration cannot be negative.", HttpStatus.BAD_REQUEST);
    }

    try {
      List<Item> itemList = itemsTableSqlHelper.getItem(itemId);
      if (itemList == null || itemList.isEmpty()) {
        return new ResponseEntity<>(
            "Item with itemId: " + itemId + " was not found", HttpStatus.NOT_FOUND);
      }
      Item item = itemList.get(0);

      if (item == null) {
        return new ResponseEntity<>("No item found for itemID: " + itemId, HttpStatus.NOT_FOUND);
      }

      boolean isSuccess =
          itemsTableSqlHelper.updateItemReservation(itemId, reservationDurationInMillis);

      if (!isSuccess) {
        return new ResponseEntity<>(
            "Could not update reservation for item: " + item.getItemName(),
            HttpStatus.INTERNAL_SERVER_ERROR);
      }

      return new ResponseEntity<>(
          "Reservation for item: " + item.getItemName() + " updated successfully.", HttpStatus.OK);

    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Cancels the reservation for the specified item. @param itemId the item id
   *
   * @param itemId the item id
   * @return the response entity
   */
  @PatchMapping(value = "/cancelItemReservation", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> cancelItemReservation(
      @RequestParam(value = "itemId") String itemId) {
    if (itemId == null || itemId.isEmpty()) {
      return new ResponseEntity<>("itemId needed to cancel reservation.", HttpStatus.BAD_REQUEST);
    }

    try {
      List<Item> itemList = itemsTableSqlHelper.getItem(itemId);
      if (itemList == null || itemList.isEmpty()) {
        return new ResponseEntity<>(
            "Item with itemId: " + itemId + " was not found", HttpStatus.NOT_FOUND);
      }
      Item item = itemList.get(0);

      if (item == null) {
        return new ResponseEntity<>("No item found for itemID: " + itemId, HttpStatus.NOT_FOUND);
      }

      boolean isSuccess = itemsTableSqlHelper.cancelItemReservation(itemId);

      if (!isSuccess) {
        return new ResponseEntity<>(
            "Could not cancel reservation for item: " + item.getItemName(),
            HttpStatus.INTERNAL_SERVER_ERROR);
      }

      return new ResponseEntity<>(
          "Reservation for item: " + item.getItemName() + " has been canceled.", HttpStatus.OK);

    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}

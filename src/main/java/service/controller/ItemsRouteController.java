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
import org.springframework.web.bind.annotation.GetMapping;
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

  @Autowired public ItemsTableSqlHelper itemsTableSqlHelper;

  /** Sample javadoc to pass checkstyle. */
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

  /** Sample javadoc to pass checkstyle. */
  @GetMapping(value = "/getItemName", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getItemName(@RequestParam(value = "itemId") String itemId) {
    if (itemId == null || itemId.isEmpty()) {
      return new ResponseEntity<>("itemId needed to get item name.", HttpStatus.BAD_REQUEST);
    }
    try {
      List<Item> item = itemsTableSqlHelper.getItem(itemId);
      if (item == null || item.isEmpty()) {
        return new ResponseEntity<>(
            "Item with itemId: " + itemId + " was not found", HttpStatus.NOT_FOUND);
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

  /** Sample javadoc to pass checkstyle. */
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

  /** Sample javadoc to pass checkstyle. */
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

  /** Sample javadoc to pass checkstyle. */
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

  /** Sample javadoc to pass checkstyle. */
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

  /** Sample javadoc to pass checkstyle. */
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

  /** Sample javadoc to pass checkstyle. */
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

  /** Sample javadoc to pass checkstyle. */
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

  /** Sample javadoc to pass checkstyle. */
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

  /** Sample javadoc to pass checkstyle. */
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
}

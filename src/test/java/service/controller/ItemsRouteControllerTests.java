package service.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import service.handler.ItemsTableSqlHelper;
import service.models.Item;

/** Unit tests for the ItemsRouteController class. */
@SpringBootTest
public class ItemsRouteControllerTests {

  @InjectMocks private ItemsRouteController itemsRouteController;

  @Mock private ItemsTableSqlHelper itemsTableSqlHelper;

  private final LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

  private Item testItem;

  /** Sets . */
  @BeforeEach
  public void setup() {
    testItem =
        Item.builder()
            .itemId(UUID.fromString("505234a2-da43-416c-a579-b9235c9be738"))
            .itemName("Socks")
            .timeOfAddition(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .location("Ya Motha's House")
            .inventoryId(UUID.fromString("dd18911c-a3fd-4f08-819e-a917f2baad18"))
            .quantity(3000)
            .price(17.38)
            .nextRestockDateTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .reservationStatus(true)
            .reservationTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .reservationDurationInMillis(1000 * 60 * 60 * 24)
            .build();
  }

  /** Test get item name. */
  @Test
  public void testGetItemName() {
    // Test empty and null itemId
    ResponseEntity<?> getItemNameResponse = itemsRouteController.getItemName(null);
    assertEquals("itemId needed to get item name.", getItemNameResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, getItemNameResponse.getStatusCode());
    getItemNameResponse = itemsRouteController.getItemName("");
    assertEquals("itemId needed to get item name.", getItemNameResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, getItemNameResponse.getStatusCode());

    // Test null and empty getItem()
    String testItemId = String.valueOf(testItem.getItemId());
    when(itemsTableSqlHelper.getItem(anyString())).thenReturn(null);
    getItemNameResponse = itemsRouteController.getItemName(testItemId);
    assertEquals(
        "Item with itemId: " + testItemId + " was not found.", getItemNameResponse.getBody());
    assertEquals(HttpStatus.NOT_FOUND, getItemNameResponse.getStatusCode());
    when(itemsTableSqlHelper.getItem(anyString())).thenReturn(new ArrayList<>());
    getItemNameResponse = itemsRouteController.getItemName(testItemId);
    assertEquals(
        "Item with itemId: " + testItemId + " was not found.", getItemNameResponse.getBody());
    assertEquals(HttpStatus.NOT_FOUND, getItemNameResponse.getStatusCode());

    // Test successful getName
    List<Item> testItems = new ArrayList<>();
    testItems.add(testItem);
    when(itemsTableSqlHelper.getItem(anyString())).thenReturn(testItems);
    getItemNameResponse = itemsRouteController.getItemName(testItemId);
    assertEquals(testItem.getItemName(), getItemNameResponse.getBody());
    assertEquals(HttpStatus.OK, getItemNameResponse.getStatusCode());

    // Test no name
    testItem.setItemName("");
    testItems.clear();
    testItems.add(testItem);
    getItemNameResponse = itemsRouteController.getItemName(testItemId);
    assertEquals("Item " + testItemId + " has no item name.", getItemNameResponse.getBody());
    assertEquals(HttpStatus.NO_CONTENT, getItemNameResponse.getStatusCode());

    // Test internal error
    when(itemsTableSqlHelper.getItem(anyString())).thenThrow(RuntimeException.class);
    getItemNameResponse = itemsRouteController.getItemName(testItemId);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, getItemNameResponse.getStatusCode());
  }

  /** Test update item name. */
  @Test
  public void testUpdateItemName() {
    String testItemName = testItem.getItemName();
    // Test empty and null itemId & newItemName
    ResponseEntity<?> updateItemNameResponse =
        itemsRouteController.updateItemName(null, testItemName);
    assertEquals("itemId is needed to update item name.", updateItemNameResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, updateItemNameResponse.getStatusCode());
    updateItemNameResponse = itemsRouteController.updateItemName("", testItemName);
    assertEquals("itemId is needed to update item name.", updateItemNameResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, updateItemNameResponse.getStatusCode());
    String testItemId = String.valueOf(testItem.getItemId());
    updateItemNameResponse = itemsRouteController.updateItemName(testItemId, null);
    assertEquals("Item name cannot be empty.", updateItemNameResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, updateItemNameResponse.getStatusCode());
    updateItemNameResponse = itemsRouteController.updateItemName(testItemId, "");
    assertEquals("Item name cannot be empty.", updateItemNameResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, updateItemNameResponse.getStatusCode());

    // Test null and empty getItem()
    when(itemsTableSqlHelper.getItem(anyString())).thenReturn(null);
    updateItemNameResponse = itemsRouteController.updateItemName(testItemId, testItemName);
    assertEquals(
        "Item with itemId: " + testItemId + " was not found.", updateItemNameResponse.getBody());
    assertEquals(HttpStatus.NOT_FOUND, updateItemNameResponse.getStatusCode());
    when(itemsTableSqlHelper.getItem(anyString())).thenReturn(new ArrayList<>());
    updateItemNameResponse = itemsRouteController.updateItemName(testItemId, testItemName);
    assertEquals(
        "Item with itemId: " + testItemId + " was not found.", updateItemNameResponse.getBody());
    assertEquals(HttpStatus.NOT_FOUND, updateItemNameResponse.getStatusCode());

    // Test multiple Items in list
    List<Item> testItems = new ArrayList<>();
    testItems.add(testItem);
    testItems.add(testItem);
    when(itemsTableSqlHelper.getItem(anyString())).thenReturn(testItems);
    updateItemNameResponse = itemsRouteController.updateItemName(testItemId, testItemName);
    assertEquals(
        "There are multiple items with itemID: " + testItemId, updateItemNameResponse.getBody());
    assertEquals(HttpStatus.CONFLICT, updateItemNameResponse.getStatusCode());

    // Test same name
    testItems.clear();
    testItems.add(testItem);
    updateItemNameResponse = itemsRouteController.updateItemName(testItemId, testItemName);
    assertEquals(
        "Item itemID: " + testItemId + "\nalready has the name: " + testItemName,
        updateItemNameResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, updateItemNameResponse.getStatusCode());

    // Test successful update
    testItems.clear();
    testItems.add(testItem);
    when(itemsTableSqlHelper.updateItemName(anyString(), anyString())).thenReturn(true);
    updateItemNameResponse = itemsRouteController.updateItemName(testItemId, "newName");
    assertEquals(
        "Item: "
            + testItemId
            + "\nwas successfully renamed. \n"
            + testItemName
            + " --> "
            + "newName",
        updateItemNameResponse.getBody());
    assertEquals(HttpStatus.OK, updateItemNameResponse.getStatusCode());

    // Test successful update
    when(itemsTableSqlHelper.updateItemName(anyString(), anyString())).thenReturn(false);
    updateItemNameResponse = itemsRouteController.updateItemName(testItemId, "newName");
    assertEquals(
        "Item with itemID: " + testItemId + " could not be updated.",
        updateItemNameResponse.getBody());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, updateItemNameResponse.getStatusCode());

    // Test internal error
    when(itemsTableSqlHelper.getItem(anyString())).thenThrow(RuntimeException.class);
    updateItemNameResponse = itemsRouteController.updateItemName(testItemId, testItemName);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, updateItemNameResponse.getStatusCode());
  }

  /** Test the getItemQuantity endpoint. */
  @Test
  public void testGetItemQuantity() {

    // Test null and empty itemId
    ResponseEntity<?> response = itemsRouteController.getItemQuantity(null);
    assertEquals("itemId needed to get item name.", response.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    response = itemsRouteController.getItemQuantity("");
    assertEquals("itemId needed to get item name.", response.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    // Test when item is not found
    String testItemId = String.valueOf(testItem.getItemId());
    when(itemsTableSqlHelper.getItem(anyString())).thenReturn(null);
    response = itemsRouteController.getItemQuantity(testItemId);
    assertEquals("Item with itemId: " + testItemId + " was not found", response.getBody());
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    // Test successful response
    List<Item> testItems = new ArrayList<>();
    testItems.add(testItem);
    when(itemsTableSqlHelper.getItem(anyString())).thenReturn(testItems);
    response = itemsRouteController.getItemQuantity(testItemId);
    assertEquals(testItem.getQuantity(), response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());

    // Test internal error
    when(itemsTableSqlHelper.getItem(anyString())).thenThrow(RuntimeException.class);
    response = itemsRouteController.getItemQuantity(testItemId);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
  }

  /** Test the getItemPrice endpoint. */
  @Test
  public void testGetItemPrice() {

    // Test null and empty itemId
    ResponseEntity<?> response = itemsRouteController.getItemPrice(null);
    assertEquals("itemId needed to get item name.", response.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    response = itemsRouteController.getItemPrice("");
    assertEquals("itemId needed to get item name.", response.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    // Test when item is not found
    String testItemId = String.valueOf(testItem.getItemId());
    when(itemsTableSqlHelper.getItem(anyString())).thenReturn(null);
    response = itemsRouteController.getItemPrice(testItemId);
    assertEquals("Item with itemId: " + testItemId + " was not found", response.getBody());
    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    // Test successful response
    List<Item> testItems = new ArrayList<>();
    testItems.add(testItem);
    when(itemsTableSqlHelper.getItem(anyString())).thenReturn(testItems);
    response = itemsRouteController.getItemPrice(testItemId);
    assertEquals(testItem.getPrice(), response.getBody());
    assertEquals(HttpStatus.OK, response.getStatusCode());

    // Test internal error
    when(itemsTableSqlHelper.getItem(anyString())).thenThrow(RuntimeException.class);
    response = itemsRouteController.getItemPrice(testItemId);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
  }

  /** Test the updateItemReservation endpoint. */
  @Test
  public void testUpdateItemReservation() {
    int oneDayMillis = 24 * 60 * 60 * 1000;

    // Test with null or empty itemId
    ResponseEntity<String> updateItemReservationResponse =
        itemsRouteController.updateItemReservation(null, oneDayMillis);
    assertEquals("itemId needed to update reservation.", updateItemReservationResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, updateItemReservationResponse.getStatusCode());

    updateItemReservationResponse = itemsRouteController.updateItemReservation("", oneDayMillis);
    assertEquals("itemId needed to update reservation.", updateItemReservationResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, updateItemReservationResponse.getStatusCode());

    // Test with negative reservation duration
    String testItemId = String.valueOf(testItem.getItemId());
    updateItemReservationResponse = itemsRouteController.updateItemReservation(testItemId, -1000);
    assertEquals(
        "Reservation duration cannot be negative.", updateItemReservationResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, updateItemReservationResponse.getStatusCode());

    // Test when the item is not found
    when(itemsTableSqlHelper.getItem(anyString())).thenReturn(null);
    updateItemReservationResponse =
        itemsRouteController.updateItemReservation(testItemId, oneDayMillis);
    assertEquals(
        "Item with itemId: " + testItemId + " was not found",
        updateItemReservationResponse.getBody());
    assertEquals(HttpStatus.NOT_FOUND, updateItemReservationResponse.getStatusCode());

    // Test successful reservation update
    List<Item> testItems = new ArrayList<>();
    testItems.add(testItem);
    when(itemsTableSqlHelper.getItem(anyString())).thenReturn(testItems);
    when(itemsTableSqlHelper.updateItemReservation(anyString(), anyLong())).thenReturn(true);
    updateItemReservationResponse =
        itemsRouteController.updateItemReservation(testItemId, oneDayMillis);
    assertEquals(
        "Reservation for item: " + testItem.getItemName() + " updated successfully.",
        updateItemReservationResponse.getBody());
    assertEquals(HttpStatus.OK, updateItemReservationResponse.getStatusCode());

    // Test failed reservation update
    when(itemsTableSqlHelper.updateItemReservation(anyString(), anyLong())).thenReturn(false);
    updateItemReservationResponse =
        itemsRouteController.updateItemReservation(testItemId, oneDayMillis);
    assertEquals(
        "Could not update reservation for item: " + testItem.getItemName(),
        updateItemReservationResponse.getBody());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, updateItemReservationResponse.getStatusCode());

    // Test internal server error
    when(itemsTableSqlHelper.getItem(anyString())).thenThrow(RuntimeException.class);
    updateItemReservationResponse =
        itemsRouteController.updateItemReservation(testItemId, oneDayMillis);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, updateItemReservationResponse.getStatusCode());
  }

  /** Test the cancelItemReservation endpoint. */
  @Test
  public void testCancelItemReservation() {
    // Test with null or empty itemId
    ResponseEntity<String> cancelItemReservationResponse =
        itemsRouteController.cancelItemReservation(null);
    assertEquals("itemId needed to cancel reservation.", cancelItemReservationResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, cancelItemReservationResponse.getStatusCode());

    cancelItemReservationResponse = itemsRouteController.cancelItemReservation("");
    assertEquals("itemId needed to cancel reservation.", cancelItemReservationResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, cancelItemReservationResponse.getStatusCode());

    // Test when the item is not found
    String testItemId = String.valueOf(testItem.getItemId());
    when(itemsTableSqlHelper.getItem(anyString())).thenReturn(null);
    cancelItemReservationResponse = itemsRouteController.cancelItemReservation(testItemId);
    assertEquals(
        "Item with itemId: " + testItemId + " was not found",
        cancelItemReservationResponse.getBody());
    assertEquals(HttpStatus.NOT_FOUND, cancelItemReservationResponse.getStatusCode());

    // Test successful reservation cancellation
    List<Item> testItems = new ArrayList<>();
    testItems.add(testItem);
    when(itemsTableSqlHelper.getItem(anyString())).thenReturn(testItems);
    when(itemsTableSqlHelper.cancelItemReservation(anyString())).thenReturn(true);
    cancelItemReservationResponse = itemsRouteController.cancelItemReservation(testItemId);
    assertEquals(
        "Reservation for item: " + testItem.getItemName() + " has been canceled.",
        cancelItemReservationResponse.getBody());
    assertEquals(HttpStatus.OK, cancelItemReservationResponse.getStatusCode());

    // Test failed reservation cancellation
    when(itemsTableSqlHelper.cancelItemReservation(anyString())).thenReturn(false);
    cancelItemReservationResponse = itemsRouteController.cancelItemReservation(testItemId);
    assertEquals(
        "Could not cancel reservation for item: " + testItem.getItemName(),
        cancelItemReservationResponse.getBody());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, cancelItemReservationResponse.getStatusCode());

    // Test internal server error
    when(itemsTableSqlHelper.getItem(anyString())).thenThrow(RuntimeException.class);
    cancelItemReservationResponse = itemsRouteController.cancelItemReservation(testItemId);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, cancelItemReservationResponse.getStatusCode());
  }

  @Test
  public void testUpdateItemLocation() {
    // Test null or empty field
    ResponseEntity<String> updateItemLocationResponse =
        itemsRouteController.updateItemLocation(null, "");
    assertEquals("itemId needed to update quantity.", updateItemLocationResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, updateItemLocationResponse.getStatusCode());
    updateItemLocationResponse = itemsRouteController.updateItemLocation("", "");
    assertEquals("itemId needed to update quantity.", updateItemLocationResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, updateItemLocationResponse.getStatusCode());
    updateItemLocationResponse =
        itemsRouteController.updateItemLocation(testItem.getItemId().toString(), null);
    assertEquals("Location cannot be empty.", updateItemLocationResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, updateItemLocationResponse.getStatusCode());
    updateItemLocationResponse =
        itemsRouteController.updateItemLocation(testItem.getItemId().toString(), "");
    assertEquals("Location cannot be empty.", updateItemLocationResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, updateItemLocationResponse.getStatusCode());

    // Test when the item is not found
    String testItemId = String.valueOf(testItem.getItemId());
    when(itemsTableSqlHelper.getItem(anyString())).thenReturn(null);
    updateItemLocationResponse =
        itemsRouteController.updateItemLocation(
            testItem.getItemId().toString(), testItem.getLocation());
    assertEquals(
        "Item with itemId: " + testItemId + " was not found", updateItemLocationResponse.getBody());
    assertEquals(HttpStatus.NOT_FOUND, updateItemLocationResponse.getStatusCode());
    when(itemsTableSqlHelper.getItem(anyString())).thenReturn(new ArrayList<>());
    updateItemLocationResponse =
        itemsRouteController.updateItemLocation(
            testItem.getItemId().toString(), testItem.getLocation());
    assertEquals(
        "Item with itemId: " + testItemId + " was not found", updateItemLocationResponse.getBody());
    assertEquals(HttpStatus.NOT_FOUND, updateItemLocationResponse.getStatusCode());

    // Test a list with null item
    List<Item> testItems = new ArrayList<>();
    testItems.add(null);
    when(itemsTableSqlHelper.getItem(anyString())).thenReturn(testItems);
    updateItemLocationResponse =
        itemsRouteController.updateItemLocation(
            testItem.getItemId().toString(), testItem.getLocation());
    assertEquals("No item found for itemID: " + testItemId, updateItemLocationResponse.getBody());
    assertEquals(HttpStatus.NOT_FOUND, updateItemLocationResponse.getStatusCode());

    // Test matching location
    testItems.clear();
    testItems.add(testItem);
    when(itemsTableSqlHelper.getItem(anyString())).thenReturn(testItems);
    updateItemLocationResponse =
        itemsRouteController.updateItemLocation(
            testItem.getItemId().toString(), testItem.getLocation());
    assertEquals(
        "Item \""
            + testItem.getItemName()
            + "\" already has a location of: \""
            + testItem.getLocation()
            + "\"",
        updateItemLocationResponse.getBody());
    assertEquals(HttpStatus.CONFLICT, updateItemLocationResponse.getStatusCode());

    // Test unsuccessful update
    when(itemsTableSqlHelper.updateItemLocation(anyString(), anyString())).thenReturn(false);
    updateItemLocationResponse =
        itemsRouteController.updateItemLocation(testItem.getItemId().toString(), "newLocation");
    assertEquals(
        "Could not update location for item: \"" + testItem.getItemName() + "\"",
        updateItemLocationResponse.getBody());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, updateItemLocationResponse.getStatusCode());

    // Test successful update
    when(itemsTableSqlHelper.updateItemLocation(anyString(), anyString())).thenReturn(true);
    updateItemLocationResponse =
        itemsRouteController.updateItemLocation(testItem.getItemId().toString(), "newLocation");
    assertEquals(
        "Item: "
            + testItem.getItemId()
            + "\nName: "
            + testItem.getItemName()
            + "\nLocation was successfully updated. \n\""
            + testItem.getLocation()
            + "\" --> \""
            + "newLocation"
            + "\"",
        updateItemLocationResponse.getBody());
    assertEquals(HttpStatus.OK, updateItemLocationResponse.getStatusCode());

    // Test internal server error
    when(itemsTableSqlHelper.getItem(anyString())).thenThrow(RuntimeException.class);
    updateItemLocationResponse =
        itemsRouteController.updateItemLocation(testItem.getItemId().toString(), "newLocation");
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, updateItemLocationResponse.getStatusCode());
  }

  @Test
  public void testUpdateItemPrice() {
    // Test null or invalid field
    ResponseEntity<String> updateItemPriceResponse = itemsRouteController.updateItemPrice(null, -1);
    assertEquals("itemId needed to update quantity.", updateItemPriceResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, updateItemPriceResponse.getStatusCode());
    updateItemPriceResponse = itemsRouteController.updateItemPrice("", -1);
    assertEquals("itemId needed to update quantity.", updateItemPriceResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, updateItemPriceResponse.getStatusCode());
    updateItemPriceResponse =
        itemsRouteController.updateItemPrice(testItem.getItemId().toString(), -1);
    assertEquals("Item price cannot be negative.", updateItemPriceResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, updateItemPriceResponse.getStatusCode());

    // Test when the item is not found
    String testItemId = String.valueOf(testItem.getItemId());
    when(itemsTableSqlHelper.getItem(anyString())).thenReturn(null);
    updateItemPriceResponse =
        itemsRouteController.updateItemPrice(testItem.getItemId().toString(), 1);
    assertEquals(
        "Item with itemId: " + testItemId + " was not found", updateItemPriceResponse.getBody());
    assertEquals(HttpStatus.NOT_FOUND, updateItemPriceResponse.getStatusCode());
    when(itemsTableSqlHelper.getItem(anyString())).thenReturn(new ArrayList<>());
    updateItemPriceResponse =
        itemsRouteController.updateItemPrice(testItem.getItemId().toString(), 1);
    assertEquals(
        "Item with itemId: " + testItemId + " was not found", updateItemPriceResponse.getBody());
    assertEquals(HttpStatus.NOT_FOUND, updateItemPriceResponse.getStatusCode());

    // Test a list with null item
    List<Item> testItems = new ArrayList<>();
    testItems.add(null);
    when(itemsTableSqlHelper.getItem(anyString())).thenReturn(testItems);
    updateItemPriceResponse =
        itemsRouteController.updateItemPrice(testItem.getItemId().toString(), 1);
    assertEquals("No item found for itemID: " + testItemId, updateItemPriceResponse.getBody());
    assertEquals(HttpStatus.NOT_FOUND, updateItemPriceResponse.getStatusCode());

    // Test matching price
    testItems.clear();
    testItems.add(testItem);
    when(itemsTableSqlHelper.getItem(anyString())).thenReturn(testItems);
    updateItemPriceResponse =
        itemsRouteController.updateItemPrice(testItem.getItemId().toString(), testItem.getPrice());
    assertEquals(
        "Item \"" + testItem.getItemName() + "\" already has a price of: " + testItem.getPrice(),
        updateItemPriceResponse.getBody());
    assertEquals(HttpStatus.CONFLICT, updateItemPriceResponse.getStatusCode());

    // Test unsuccessful update
    when(itemsTableSqlHelper.updateItemPrice(anyString(), anyDouble())).thenReturn(false);
    updateItemPriceResponse =
        itemsRouteController.updateItemPrice(
            testItem.getItemId().toString(), testItem.getPrice() + 1);
    assertEquals(
        "Could not update price for item: \"" + testItem.getItemName() + "\"",
        updateItemPriceResponse.getBody());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, updateItemPriceResponse.getStatusCode());

    // Test successful update
    when(itemsTableSqlHelper.updateItemPrice(anyString(), anyDouble())).thenReturn(true);
    updateItemPriceResponse =
        itemsRouteController.updateItemPrice(
            testItem.getItemId().toString(), testItem.getPrice() + 1);
    assertEquals(
        "Item: "
            + testItem.getItemId()
            + "\nName: "
            + testItem.getItemName()
            + "\nPrice was successfully updated. \n"
            + testItem.getPrice()
            + " --> "
            + (testItem.getPrice() + 1),
        updateItemPriceResponse.getBody());
    assertEquals(HttpStatus.OK, updateItemPriceResponse.getStatusCode());

    // Test internal server error
    when(itemsTableSqlHelper.getItem(anyString())).thenThrow(RuntimeException.class);
    updateItemPriceResponse =
        itemsRouteController.updateItemPrice(testItem.getItemId().toString(), 1);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, updateItemPriceResponse.getStatusCode());
  }
}

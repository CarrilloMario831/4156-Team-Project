package service.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static service.util.DateTimeUtils.FORMATTER;

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
import service.requests.CreateItemRequest;

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

  /** Test creating a new item. */
  @Test
  public void testCreateItem() {

    // populate the itemRequest object to be passed into the controller
    CreateItemRequest testItemRequest =
        CreateItemRequest.builder()
            .itemName(testItem.getItemName())
            .location(testItem.getLocation())
            .inventoryId(testItem.getInventoryId())
            .quantity(testItem.getQuantity())
            .reservationStatus(testItem.isReservationStatus())
            .reservationDurationInMillis(testItem.getReservationDurationInMillis())
            .reservationTime(testItem.getReservationTime())
            .price(testItem.getPrice())
            .nextRestockDateTime(testItem.getNextRestockDateTime())
            .build();

    // Test successful creation.
    when(itemsTableSqlHelper.insertItem(any())).thenReturn(true);
    ResponseEntity<?> createItemResponse = itemsRouteController.createItem(testItemRequest);
    assertEquals(HttpStatus.CREATED, createItemResponse.getStatusCode());

    // Test null passed in for createItemRequest
    createItemResponse = itemsRouteController.createItem(null);
    assertEquals(HttpStatus.BAD_REQUEST, createItemResponse.getStatusCode());
    assertEquals("Empty request", createItemResponse.getBody());

    // Test unsuccessful creation.
    when(itemsTableSqlHelper.insertItem(any())).thenReturn(false);
    createItemResponse = itemsRouteController.createItem(testItemRequest);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, createItemResponse.getStatusCode());
    assertEquals("Failed to create item", createItemResponse.getBody());

    // Test Internal error caused by thrown exception.
    doThrow(new RuntimeException()).when(itemsTableSqlHelper).insertItem(any());
    createItemResponse = itemsRouteController.createItem(testItemRequest);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, createItemResponse.getStatusCode());
  }

  /** Test get the next restock time for an item. */
  @Test
  public void testGetNextRestockTime() {
    List<Item> testItemList = new ArrayList<>();
    testItemList.add(testItem);

    // Test successful GET.
    when(itemsTableSqlHelper.getItem(any())).thenReturn(testItemList);
    ResponseEntity<?> getItemResponse =
        itemsRouteController.getNextRestockTime(testItem.getItemId().toString());
    assertEquals(HttpStatus.OK, getItemResponse.getStatusCode());
    assertEquals(testItem.getNextRestockDateTime().format(FORMATTER), getItemResponse.getBody());

    // Test null passed into controller
    getItemResponse = itemsRouteController.getNextRestockTime(null);
    assertEquals(HttpStatus.BAD_REQUEST, getItemResponse.getStatusCode());
    assertEquals("itemId needed to get item name.", getItemResponse.getBody());

    // Test empty string passed into controller
    getItemResponse = itemsRouteController.getNextRestockTime("");
    assertEquals(HttpStatus.BAD_REQUEST, getItemResponse.getStatusCode());
    assertEquals("itemId needed to get item name.", getItemResponse.getBody());

    // Test unsuccessful GET with a null fetched from mocked DB.
    when(itemsTableSqlHelper.getItem(any())).thenReturn(null);
    getItemResponse = itemsRouteController.getNextRestockTime(testItem.getItemId().toString());
    assertEquals(HttpStatus.NOT_FOUND, getItemResponse.getStatusCode());
    assertEquals(
        "Item with itemId: " + testItem.getItemId() + " was not found", getItemResponse.getBody());

    // Test unsuccessful GET with an empty list returned.
    when(itemsTableSqlHelper.getItem(any())).thenReturn(new ArrayList<>());
    getItemResponse = itemsRouteController.getNextRestockTime(testItem.getItemId().toString());
    assertEquals(HttpStatus.NOT_FOUND, getItemResponse.getStatusCode());
    assertEquals(
        "Item with itemId: " + testItem.getItemId() + " was not found", getItemResponse.getBody());

    // Test when there is no available next restock date
    testItemList.remove(0);
    testItem.setNextRestockDateTime(null);
    testItemList.add(testItem);
    when(itemsTableSqlHelper.getItem(any())).thenReturn(testItemList);
    getItemResponse = itemsRouteController.getNextRestockTime(testItem.getItemId().toString());
    assertEquals(HttpStatus.NO_CONTENT, getItemResponse.getStatusCode());
    assertEquals(
        "No restock time available for item: " + testItem.getItemName(), getItemResponse.getBody());

    // Test thrown exception.
    doThrow(new RuntimeException()).when(itemsTableSqlHelper).getItem(any());
    getItemResponse = itemsRouteController.getNextRestockTime(testItem.getItemId().toString());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, getItemResponse.getStatusCode());
  }

  /** Test getting the item's inventory utilizing the itemId. */
  @Test
  public void testGetInventoryIdFromItemId() {
    List<Item> testItemList = new ArrayList<>();
    testItemList.add(testItem);

    // Test successful GET.
    when(itemsTableSqlHelper.getItem(any())).thenReturn(testItemList);
    ResponseEntity<?> getItemInventoryResponse =
        itemsRouteController.getInventoryIdFromItemId(testItem.getItemId().toString());
    assertEquals(HttpStatus.OK, getItemInventoryResponse.getStatusCode());
    assertEquals(testItem.getInventoryId().toString(), getItemInventoryResponse.getBody());

    // Test null passed into controller
    getItemInventoryResponse = itemsRouteController.getInventoryIdFromItemId(null);
    assertEquals(HttpStatus.BAD_REQUEST, getItemInventoryResponse.getStatusCode());
    assertEquals("itemId needed to get item name.", getItemInventoryResponse.getBody());

    // Test empty string passed into controller
    getItemInventoryResponse = itemsRouteController.getInventoryIdFromItemId("");
    assertEquals(HttpStatus.BAD_REQUEST, getItemInventoryResponse.getStatusCode());
    assertEquals("itemId needed to get item name.", getItemInventoryResponse.getBody());

    // Test unsuccessful GET with a null fetched from mocked DB.
    when(itemsTableSqlHelper.getItem(any())).thenReturn(null);
    getItemInventoryResponse =
        itemsRouteController.getInventoryIdFromItemId(testItem.getItemId().toString());
    assertEquals(HttpStatus.NOT_FOUND, getItemInventoryResponse.getStatusCode());
    assertEquals(
        "Item with itemId: " + testItem.getItemId() + " was not found",
        getItemInventoryResponse.getBody());

    // Test unsuccessful GET with an empty list returned.
    when(itemsTableSqlHelper.getItem(any())).thenReturn(new ArrayList<>());
    getItemInventoryResponse =
        itemsRouteController.getInventoryIdFromItemId(testItem.getItemId().toString());
    assertEquals(HttpStatus.NOT_FOUND, getItemInventoryResponse.getStatusCode());
    assertEquals(
        "Item with itemId: " + testItem.getItemId() + " was not found",
        getItemInventoryResponse.getBody());

    // Test thrown exception.
    doThrow(new RuntimeException()).when(itemsTableSqlHelper).getItem(any());
    getItemInventoryResponse =
        itemsRouteController.getInventoryIdFromItemId(testItem.getItemId().toString());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, getItemInventoryResponse.getStatusCode());
  }

  /** Test getting an Item's location. */
  @Test
  public void testGetItemLocation() {
    List<Item> testItemList = new ArrayList<>();
    testItemList.add(testItem);

    // Test successful GET.
    when(itemsTableSqlHelper.getItem(any())).thenReturn(testItemList);
    ResponseEntity<?> getItemLocationResponse =
        itemsRouteController.getItemLocation(testItem.getItemId().toString());
    assertEquals(HttpStatus.OK, getItemLocationResponse.getStatusCode());
    assertEquals(testItem.getLocation(), getItemLocationResponse.getBody());

    // Test null passed into controller
    getItemLocationResponse = itemsRouteController.getItemLocation(null);
    assertEquals(HttpStatus.BAD_REQUEST, getItemLocationResponse.getStatusCode());
    assertEquals("itemId needed to get item name.", getItemLocationResponse.getBody());

    // Test empty string passed into controller
    getItemLocationResponse = itemsRouteController.getItemLocation("");
    assertEquals(HttpStatus.BAD_REQUEST, getItemLocationResponse.getStatusCode());
    assertEquals("itemId needed to get item name.", getItemLocationResponse.getBody());

    // Test unsuccessful GET with a null fetched from mocked DB.
    when(itemsTableSqlHelper.getItem(any())).thenReturn(null);
    getItemLocationResponse = itemsRouteController.getItemLocation(testItem.getItemId().toString());
    assertEquals(HttpStatus.NOT_FOUND, getItemLocationResponse.getStatusCode());
    assertEquals(
        "Item with itemId: " + testItem.getItemId() + " was not found",
        getItemLocationResponse.getBody());

    // Test unsuccessful GET with an empty list returned.
    when(itemsTableSqlHelper.getItem(any())).thenReturn(new ArrayList<>());
    getItemLocationResponse = itemsRouteController.getItemLocation(testItem.getItemId().toString());
    assertEquals(HttpStatus.NOT_FOUND, getItemLocationResponse.getStatusCode());
    assertEquals(
        "Item with itemId: " + testItem.getItemId() + " was not found",
        getItemLocationResponse.getBody());

    // Test thrown exception.
    doThrow(new RuntimeException()).when(itemsTableSqlHelper).getItem(any());
    getItemLocationResponse = itemsRouteController.getItemLocation(testItem.getItemId().toString());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, getItemLocationResponse.getStatusCode());
  }

  /** Test getting an Item's reservation time. */
  @Test
  public void testGetItemReservationTime() {
    List<Item> testItemList = new ArrayList<>();
    testItemList.add(testItem);

    // Test successful GET.
    when(itemsTableSqlHelper.getItem(any())).thenReturn(testItemList);
    ResponseEntity<?> getItemReservationTimeResponse =
        itemsRouteController.getItemReservationTime(testItem.getItemId().toString());
    assertEquals(HttpStatus.OK, getItemReservationTimeResponse.getStatusCode());
    assertEquals(
        testItem.getReservationTime().format(FORMATTER), getItemReservationTimeResponse.getBody());

    // Test null passed into controller
    getItemReservationTimeResponse = itemsRouteController.getItemReservationTime(null);
    assertEquals(HttpStatus.BAD_REQUEST, getItemReservationTimeResponse.getStatusCode());
    assertEquals("itemId needed to get item name.", getItemReservationTimeResponse.getBody());

    // Test empty string passed into controller
    getItemReservationTimeResponse = itemsRouteController.getItemReservationTime("");
    assertEquals(HttpStatus.BAD_REQUEST, getItemReservationTimeResponse.getStatusCode());
    assertEquals("itemId needed to get item name.", getItemReservationTimeResponse.getBody());

    // Test unsuccessful GET with a null fetched from mocked DB.
    when(itemsTableSqlHelper.getItem(any())).thenReturn(null);
    getItemReservationTimeResponse =
        itemsRouteController.getItemReservationTime(testItem.getItemId().toString());
    assertEquals(HttpStatus.NOT_FOUND, getItemReservationTimeResponse.getStatusCode());
    assertEquals(
        "Item with itemId: " + testItem.getItemId() + " was not found",
        getItemReservationTimeResponse.getBody());

    // Test unsuccessful GET with an empty list returned.
    when(itemsTableSqlHelper.getItem(any())).thenReturn(new ArrayList<>());
    getItemReservationTimeResponse =
        itemsRouteController.getItemReservationTime(testItem.getItemId().toString());
    assertEquals(HttpStatus.NOT_FOUND, getItemReservationTimeResponse.getStatusCode());
    assertEquals(
        "Item with itemId: " + testItem.getItemId() + " was not found",
        getItemReservationTimeResponse.getBody());

    // Test thrown exception.
    doThrow(new RuntimeException()).when(itemsTableSqlHelper).getItem(any());
    getItemReservationTimeResponse =
        itemsRouteController.getItemReservationTime(testItem.getItemId().toString());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, getItemReservationTimeResponse.getStatusCode());
  }

  /** Test getting an Item's reservation duration. */
  @Test
  public void testGetItemReservationDuration() {
    List<Item> testItemList = new ArrayList<>();
    testItemList.add(testItem);

    // Test successful GET.
    when(itemsTableSqlHelper.getItem(any())).thenReturn(testItemList);
    ResponseEntity<?> getItemReservationDurationResponse =
        itemsRouteController.getItemReservationDuration(testItem.getItemId().toString());
    assertEquals(HttpStatus.OK, getItemReservationDurationResponse.getStatusCode());
    assertEquals(
        testItem.getReservationDurationInMillis(), getItemReservationDurationResponse.getBody());

    // Test null passed into controller
    getItemReservationDurationResponse = itemsRouteController.getItemReservationDuration(null);
    assertEquals(HttpStatus.BAD_REQUEST, getItemReservationDurationResponse.getStatusCode());
    assertEquals("itemId needed to get item name.", getItemReservationDurationResponse.getBody());

    // Test empty string passed into controller
    getItemReservationDurationResponse = itemsRouteController.getItemReservationDuration("");
    assertEquals(HttpStatus.BAD_REQUEST, getItemReservationDurationResponse.getStatusCode());
    assertEquals("itemId needed to get item name.", getItemReservationDurationResponse.getBody());

    // Test unsuccessful GET with a null fetched from mocked DB.
    when(itemsTableSqlHelper.getItem(any())).thenReturn(null);
    getItemReservationDurationResponse =
        itemsRouteController.getItemReservationDuration(testItem.getItemId().toString());
    assertEquals(HttpStatus.NOT_FOUND, getItemReservationDurationResponse.getStatusCode());
    assertEquals(
        "Item with itemId: " + testItem.getItemId() + " was not found",
        getItemReservationDurationResponse.getBody());

    // Test unsuccessful GET with an empty list returned.
    when(itemsTableSqlHelper.getItem(any())).thenReturn(new ArrayList<>());
    getItemReservationDurationResponse =
        itemsRouteController.getItemReservationDuration(testItem.getItemId().toString());
    assertEquals(HttpStatus.NOT_FOUND, getItemReservationDurationResponse.getStatusCode());
    assertEquals(
        "Item with itemId: " + testItem.getItemId() + " was not found",
        getItemReservationDurationResponse.getBody());

    // Test thrown exception.
    doThrow(new RuntimeException()).when(itemsTableSqlHelper).getItem(any());
    getItemReservationDurationResponse =
        itemsRouteController.getItemReservationDuration(testItem.getItemId().toString());
    assertEquals(
        HttpStatus.INTERNAL_SERVER_ERROR, getItemReservationDurationResponse.getStatusCode());
  }

  /** Test getting an Item's reservation status. */
  @Test
  public void testIsItemReserved() {
    List<Item> testItemList = new ArrayList<>();
    testItemList.add(testItem);

    // Test successful GET.
    when(itemsTableSqlHelper.getItem(any())).thenReturn(testItemList);
    ResponseEntity<?> isItemReservedResponse =
        itemsRouteController.isItemReserved(testItem.getItemId().toString());
    assertEquals(HttpStatus.OK, isItemReservedResponse.getStatusCode());
    assertEquals(testItem.isReservationStatus(), isItemReservedResponse.getBody());

    // Test null passed into controller
    isItemReservedResponse = itemsRouteController.isItemReserved(null);
    assertEquals(HttpStatus.BAD_REQUEST, isItemReservedResponse.getStatusCode());
    assertEquals("itemId needed to get item name.", isItemReservedResponse.getBody());

    // Test empty string passed into controller
    isItemReservedResponse = itemsRouteController.isItemReserved("");
    assertEquals(HttpStatus.BAD_REQUEST, isItemReservedResponse.getStatusCode());
    assertEquals("itemId needed to get item name.", isItemReservedResponse.getBody());

    // Test unsuccessful GET with a null fetched from mocked DB.
    when(itemsTableSqlHelper.getItem(any())).thenReturn(null);
    isItemReservedResponse = itemsRouteController.isItemReserved(testItem.getItemId().toString());
    assertEquals(HttpStatus.NOT_FOUND, isItemReservedResponse.getStatusCode());
    assertEquals(
        "Item with itemId: " + testItem.getItemId() + " was not found",
        isItemReservedResponse.getBody());

    // Test unsuccessful GET with an empty list returned.
    when(itemsTableSqlHelper.getItem(any())).thenReturn(new ArrayList<>());
    isItemReservedResponse = itemsRouteController.isItemReserved(testItem.getItemId().toString());
    assertEquals(HttpStatus.NOT_FOUND, isItemReservedResponse.getStatusCode());
    assertEquals(
        "Item with itemId: " + testItem.getItemId() + " was not found",
        isItemReservedResponse.getBody());

    // Test thrown exception.
    doThrow(new RuntimeException()).when(itemsTableSqlHelper).getItem(any());
    isItemReservedResponse = itemsRouteController.isItemReserved(testItem.getItemId().toString());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, isItemReservedResponse.getStatusCode());
  }

  /** Test getting an Item's time of addition. */
  @Test
  public void testGetTimeItemOfAddition() {
    List<Item> testItemList = new ArrayList<>();
    testItemList.add(testItem);

    // Test successful GET.
    when(itemsTableSqlHelper.getItem(any())).thenReturn(testItemList);
    ResponseEntity<?> getTimeOfAdditionResponse =
        itemsRouteController.getItemTimeOfAddition(testItem.getItemId().toString());
    assertEquals(HttpStatus.OK, getTimeOfAdditionResponse.getStatusCode());
    assertEquals(
        testItem.getTimeOfAddition().format(FORMATTER), getTimeOfAdditionResponse.getBody());

    // Test null passed into controller
    getTimeOfAdditionResponse = itemsRouteController.getItemTimeOfAddition(null);
    assertEquals(HttpStatus.BAD_REQUEST, getTimeOfAdditionResponse.getStatusCode());
    assertEquals("itemId needed to get item name.", getTimeOfAdditionResponse.getBody());

    // Test empty string passed into controller
    getTimeOfAdditionResponse = itemsRouteController.getItemTimeOfAddition("");
    assertEquals(HttpStatus.BAD_REQUEST, getTimeOfAdditionResponse.getStatusCode());
    assertEquals("itemId needed to get item name.", getTimeOfAdditionResponse.getBody());

    // Test unsuccessful GET with a null fetched from mocked DB.
    when(itemsTableSqlHelper.getItem(any())).thenReturn(null);
    getTimeOfAdditionResponse =
        itemsRouteController.getItemTimeOfAddition(testItem.getItemId().toString());
    assertEquals(HttpStatus.NOT_FOUND, getTimeOfAdditionResponse.getStatusCode());
    assertEquals(
        "Item with itemId: " + testItem.getItemId() + " was not found",
        getTimeOfAdditionResponse.getBody());

    // Test unsuccessful GET with an empty list returned.
    when(itemsTableSqlHelper.getItem(any())).thenReturn(new ArrayList<>());
    getTimeOfAdditionResponse =
        itemsRouteController.getItemTimeOfAddition(testItem.getItemId().toString());
    assertEquals(HttpStatus.NOT_FOUND, getTimeOfAdditionResponse.getStatusCode());
    assertEquals(
        "Item with itemId: " + testItem.getItemId() + " was not found",
        getTimeOfAdditionResponse.getBody());

    // Test thrown exception.
    doThrow(new RuntimeException()).when(itemsTableSqlHelper).getItem(any());
    getTimeOfAdditionResponse =
        itemsRouteController.getItemTimeOfAddition(testItem.getItemId().toString());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, getTimeOfAdditionResponse.getStatusCode());
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

    // Test when item is not found and empty list returned
    when(itemsTableSqlHelper.getItem(anyString())).thenReturn(new ArrayList<>());
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

    // Test when empty list returned
    when(itemsTableSqlHelper.getItem(anyString())).thenReturn(new ArrayList<>());
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
    assertEquals("itemId needed to update price.", updateItemPriceResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, updateItemPriceResponse.getStatusCode());
    updateItemPriceResponse = itemsRouteController.updateItemPrice("", -1);
    assertEquals("itemId needed to update price.", updateItemPriceResponse.getBody());
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

  @Test
  public void testUpdateItemQuantity() {
    // Test null or invalid field
    ResponseEntity<String> updateItemQuantityResponse =
        itemsRouteController.updateItemQuantity(null, -1);
    assertEquals("itemId needed to update quantity.", updateItemQuantityResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, updateItemQuantityResponse.getStatusCode());
    updateItemQuantityResponse = itemsRouteController.updateItemQuantity("", -1);
    assertEquals("itemId needed to update quantity.", updateItemQuantityResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, updateItemQuantityResponse.getStatusCode());
    updateItemQuantityResponse =
        itemsRouteController.updateItemQuantity(testItem.getItemId().toString(), -1);
    assertEquals(
        "Item quantity cannot be a negative number.", updateItemQuantityResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, updateItemQuantityResponse.getStatusCode());

    // Test when the item is not found
    String testItemId = String.valueOf(testItem.getItemId());
    when(itemsTableSqlHelper.getItem(anyString())).thenReturn(null);
    updateItemQuantityResponse =
        itemsRouteController.updateItemQuantity(testItem.getItemId().toString(), 1);
    assertEquals(
        "Item with itemId: " + testItemId + " was not found", updateItemQuantityResponse.getBody());
    assertEquals(HttpStatus.NOT_FOUND, updateItemQuantityResponse.getStatusCode());
    when(itemsTableSqlHelper.getItem(anyString())).thenReturn(new ArrayList<>());
    updateItemQuantityResponse =
        itemsRouteController.updateItemQuantity(testItem.getItemId().toString(), 1);
    assertEquals(
        "Item with itemId: " + testItemId + " was not found", updateItemQuantityResponse.getBody());
    assertEquals(HttpStatus.NOT_FOUND, updateItemQuantityResponse.getStatusCode());

    // Test a list with null item
    List<Item> testItems = new ArrayList<>();
    testItems.add(null);
    when(itemsTableSqlHelper.getItem(anyString())).thenReturn(testItems);
    updateItemQuantityResponse =
        itemsRouteController.updateItemQuantity(testItem.getItemId().toString(), 1);
    assertEquals("No item found for itemID: " + testItemId, updateItemQuantityResponse.getBody());
    assertEquals(HttpStatus.NOT_FOUND, updateItemQuantityResponse.getStatusCode());

    // Test matching price
    testItems.clear();
    testItems.add(testItem);
    when(itemsTableSqlHelper.getItem(anyString())).thenReturn(testItems);
    updateItemQuantityResponse =
        itemsRouteController.updateItemQuantity(
            testItem.getItemId().toString(), testItem.getQuantity());
    assertEquals(
        "Item \""
            + testItem.getItemName()
            + "\" already has a quantity of: "
            + testItem.getQuantity(),
        updateItemQuantityResponse.getBody());
    assertEquals(HttpStatus.CONFLICT, updateItemQuantityResponse.getStatusCode());

    // Test unsuccessful update
    when(itemsTableSqlHelper.updateItemPrice(anyString(), anyDouble())).thenReturn(false);
    updateItemQuantityResponse =
        itemsRouteController.updateItemQuantity(
            testItem.getItemId().toString(), testItem.getQuantity() + 1);
    assertEquals(
        "Could not update quantity for item: \"" + testItem.getItemName() + "\"",
        updateItemQuantityResponse.getBody());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, updateItemQuantityResponse.getStatusCode());

    // Test successful update
    when(itemsTableSqlHelper.updateItemQuantity(anyString(), anyInt())).thenReturn(true);
    updateItemQuantityResponse =
        itemsRouteController.updateItemQuantity(
            testItem.getItemId().toString(), testItem.getQuantity() + 1);
    assertEquals(
        "Item: "
            + testItem.getItemId()
            + "\nName: "
            + testItem.getItemName()
            + "\nQuantity was successfully updated. \n"
            + testItem.getQuantity()
            + " --> "
            + (testItem.getQuantity() + 1),
        updateItemQuantityResponse.getBody());
    assertEquals(HttpStatus.OK, updateItemQuantityResponse.getStatusCode());

    // Test internal server error
    when(itemsTableSqlHelper.getItem(anyString())).thenThrow(RuntimeException.class);
    updateItemQuantityResponse =
        itemsRouteController.updateItemQuantity(testItem.getItemId().toString(), 1);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, updateItemQuantityResponse.getStatusCode());
  }

  @Test
  public void testDeleteItem() {
    // Test null or invalid field
    ResponseEntity<String> deleteItemResponse = itemsRouteController.deleteItem(null);
    assertEquals("itemId needed to delete item.", deleteItemResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, deleteItemResponse.getStatusCode());
    deleteItemResponse = itemsRouteController.deleteItem("");
    assertEquals("itemId needed to delete item.", deleteItemResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, deleteItemResponse.getStatusCode());

    // Test when the item is not found
    String testItemId = String.valueOf(testItem.getItemId());
    when(itemsTableSqlHelper.getItem(anyString())).thenReturn(null);
    deleteItemResponse = itemsRouteController.deleteItem(testItem.getItemId().toString());
    assertEquals(
        "Item with itemId: " + testItemId + " was not found.", deleteItemResponse.getBody());
    assertEquals(HttpStatus.NOT_FOUND, deleteItemResponse.getStatusCode());
    when(itemsTableSqlHelper.getItem(anyString())).thenReturn(new ArrayList<>());
    deleteItemResponse = itemsRouteController.deleteItem(testItem.getItemId().toString());
    assertEquals(
        "Item with itemId: " + testItemId + " was not found.", deleteItemResponse.getBody());
    assertEquals(HttpStatus.NOT_FOUND, deleteItemResponse.getStatusCode());

    // Test a list with null item
    List<Item> testItems = new ArrayList<>();
    testItems.add(null);
    when(itemsTableSqlHelper.getItem(anyString())).thenReturn(testItems);
    deleteItemResponse = itemsRouteController.deleteItem(testItem.getItemId().toString());
    assertEquals("No item found for itemID: " + testItemId, deleteItemResponse.getBody());
    assertEquals(HttpStatus.NOT_FOUND, deleteItemResponse.getStatusCode());

    // Test a list with multiple items
    testItems.clear();
    testItems.add(testItem);
    testItems.add(null);
    when(itemsTableSqlHelper.getItem(anyString())).thenReturn(testItems);
    deleteItemResponse = itemsRouteController.deleteItem(testItem.getItemId().toString());
    assertEquals(
        "There are multiple items with itemID: " + testItemId, deleteItemResponse.getBody());
    assertEquals(HttpStatus.CONFLICT, deleteItemResponse.getStatusCode());

    // Test unsuccessful update
    testItems.clear();
    testItems.add(testItem);
    when(itemsTableSqlHelper.updateItemPrice(anyString(), anyDouble())).thenReturn(false);
    deleteItemResponse = itemsRouteController.deleteItem(testItem.getItemId().toString());
    assertEquals(
        "Item with itemID: " + testItem.getItemId() + " could not be deleted.",
        deleteItemResponse.getBody());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, deleteItemResponse.getStatusCode());

    // Test successful update
    when(itemsTableSqlHelper.deleteItem(anyString())).thenReturn(true);
    deleteItemResponse = itemsRouteController.deleteItem(testItem.getItemId().toString());
    assertEquals(
        "Item: "
            + testItem.getItemId()
            + "\n\""
            + testItem.getItemName()
            + "\"was successfully deleted.",
        deleteItemResponse.getBody());
    assertEquals(HttpStatus.OK, deleteItemResponse.getStatusCode());

    // Test internal server error
    doThrow(new RuntimeException()).when(itemsTableSqlHelper).deleteItem(any());
    deleteItemResponse = itemsRouteController.deleteItem(testItem.getItemId().toString());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, deleteItemResponse.getStatusCode());
  }
}

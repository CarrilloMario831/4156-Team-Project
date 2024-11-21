package service.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import service.handler.InventoryTableSqlHelper;
import service.models.Inventory;
import service.models.Item;
import service.requests.CreateInventoryRequest;

/** Unit tests for the ItemsRouteController class. */
@SpringBootTest
public class InventoryRouteControllerTests {

  @InjectMocks private InventoryRouteController inventoryRouteController;

  @Mock private InventoryTableSqlHelper inventoryTableSqlHelper;

  private Inventory testInventory;
  private Item testItem;

  /** Create a sample inventory that holds an example item. */
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

    // put a sample test item within the items this inventory is mapped to
    Map<UUID, Item> testMap = Map.of(testItem.getItemId(), testItem);
    testInventory =
        Inventory.builder()
            .inventoryId(UUID.fromString("505234a2-da43-416c-a579-b9235c9be738"))
            .inventoryName("Test Inventory")
            .items(testMap)
            .build();
  }

  /** Test creating a new inventory. */
  @Test
  public void testCreateInventory() {

    // create the request that gets passed into the endpoint
    CreateInventoryRequest testInventoryRequest =
        CreateInventoryRequest.builder()
            .inventoryId(testInventory.getInventoryId())
            .inventoryName(testInventory.getInventoryName())
            .items(testInventory.getItems())
            .build();

    // Test successful creation.
    when(inventoryTableSqlHelper.insertInventory(any())).thenReturn(true);
    ResponseEntity<?> createInventoryResponse =
        inventoryRouteController.createInventory(testInventoryRequest);
    assertEquals(HttpStatus.CREATED, createInventoryResponse.getStatusCode());

    // Test null passed in for CreateInventoryRequest.
    createInventoryResponse = inventoryRouteController.createInventory(null);
    assertEquals("Empty request", createInventoryResponse.getBody());
    assertEquals(HttpStatus.BAD_REQUEST, createInventoryResponse.getStatusCode());

    // Test unsuccessful creation.
    when(inventoryTableSqlHelper.insertInventory(any())).thenReturn(false);
    createInventoryResponse = inventoryRouteController.createInventory(testInventoryRequest);
    assertEquals("Failed to create inventory", createInventoryResponse.getBody());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, createInventoryResponse.getStatusCode());

    // Test Internal Error caused by thrown exception.
    doThrow(new RuntimeException()).when(inventoryTableSqlHelper).insertInventory(any());
    createInventoryResponse = inventoryRouteController.createInventory(testInventoryRequest);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, createInventoryResponse.getStatusCode());
  }

  /** Test accessing an inventories name. */
  @Test
  public void testGetInventoryName() {

    String inventoryId = testInventory.getInventoryId().toString();

    // Test successful get
    when(inventoryTableSqlHelper.getInventoryWithInventoryId(any())).thenReturn(testInventory);
    ResponseEntity<?> getInventoryNameResponse =
        inventoryRouteController.getInventoryName(inventoryId);
    assertEquals(HttpStatus.OK, getInventoryNameResponse.getStatusCode());
    assertEquals(testInventory.getInventoryName(), getInventoryNameResponse.getBody());

    // Test null inventoryId passed in
    getInventoryNameResponse = inventoryRouteController.getInventoryName(null);
    assertEquals(HttpStatus.BAD_REQUEST, getInventoryNameResponse.getStatusCode());
    assertEquals("inventoryId needed to get inventories.", getInventoryNameResponse.getBody());

    // Test empty inventoryId passed in
    getInventoryNameResponse = inventoryRouteController.getInventoryName("");
    assertEquals(HttpStatus.BAD_REQUEST, getInventoryNameResponse.getStatusCode());
    assertEquals("inventoryId needed to get inventories.", getInventoryNameResponse.getBody());

    // Test empty inventoryName
    testInventory.setInventoryName("");
    getInventoryNameResponse = inventoryRouteController.getInventoryName(inventoryId);
    assertEquals(HttpStatus.NO_CONTENT, getInventoryNameResponse.getStatusCode());
    assertEquals(
        "Inventory with inventoryId: " + inventoryId + " has no name.",
        getInventoryNameResponse.getBody());

    // Test inventory that couldn't be found
    when(inventoryTableSqlHelper.getInventoryWithInventoryId(any())).thenReturn(null);
    getInventoryNameResponse = inventoryRouteController.getInventoryName(inventoryId);
    assertEquals(HttpStatus.NOT_FOUND, getInventoryNameResponse.getStatusCode());
    assertEquals(
        "Inventory with inventoryId: " + inventoryId + " has not been found.",
        getInventoryNameResponse.getBody());

    // Test Internal Error caused by thrown exception.
    when(inventoryTableSqlHelper.getInventoryWithInventoryId(any()))
        .thenThrow(RuntimeException.class);
    getInventoryNameResponse = inventoryRouteController.getInventoryName(inventoryId);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, getInventoryNameResponse.getStatusCode());
  }
  
  /** Test accessing items belonging to a specific inventory. */
  @Test
  public void testGetInventoryItemIds() {
    
//    String inventoryId = testInventory.getInventoryId().toString();
//
//    // Test successful get
//    when(inventoryTableSqlHelper.getInventoryWithInventoryId(any())).thenReturn(testInventory);
//    ResponseEntity<?> getInventoryItemsResponse =
//        inventoryRouteController.getInventoryName(inventoryId);
//    assertEquals(HttpStatus.OK, getInventoryItemsResponse.getStatusCode());
//    assertEquals(testInventory.getInventoryName(), getInventoryItemsResponse.getBody());
//
//    // Test null inventoryId passed in
//    getInventoryItemsResponse = inventoryRouteController.getInventoryName(null);
//    assertEquals(HttpStatus.BAD_REQUEST, getInventoryItemsResponse.getStatusCode());
//    assertEquals("inventoryId needed to get inventories.", getInventoryItemsResponse.getBody());
//
//    // Test empty inventoryId passed in
//    getInventoryItemsResponse = inventoryRouteController.getInventoryName("");
//    assertEquals(HttpStatus.BAD_REQUEST, getInventoryItemsResponse.getStatusCode());
//    assertEquals("inventoryId needed to get inventories.", getInventoryItemsResponse.getBody());
//
//    // Test empty inventoryName
//    testInventory.setInventoryName("");
//    getInventoryItemsResponse = inventoryRouteController.getInventoryName(inventoryId);
//    assertEquals(HttpStatus.NO_CONTENT, getInventoryItemsResponse.getStatusCode());
//    assertEquals(
//        "Inventory with inventoryId: " + inventoryId + " has no name.",
//        getInventoryItemsResponse.getBody());
//
//    // Test inventory that couldn't be found
//    when(inventoryTableSqlHelper.getInventoryWithInventoryId(any())).thenReturn(null);
//    getInventoryItemsResponse = inventoryRouteController.getInventoryName(inventoryId);
//    assertEquals(HttpStatus.NOT_FOUND, getInventoryItemsResponse.getStatusCode());
//    assertEquals(
//        "Inventory with inventoryId: " + inventoryId + " has not been found.",
//        getInventoryItemsResponse.getBody());
//
//    // Test Internal Error caused by thrown exception.
//    when(inventoryTableSqlHelper.getInventoryWithInventoryId(any()))
//        .thenThrow(RuntimeException.class);
//    getInventoryItemsResponse = inventoryRouteController.getInventoryName(inventoryId);
//    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, getInventoryItemsResponse.getStatusCode());
  }
}

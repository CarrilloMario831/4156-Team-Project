package service.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

  /** Sample javadoc to pass checkstyle. */
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
}

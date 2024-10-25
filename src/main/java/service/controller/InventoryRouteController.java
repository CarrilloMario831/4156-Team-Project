package service.controller;

import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import service.handler.InventoryTableSqlHelper;
import service.handler.ItemsTableSqlHelper;
import service.models.Inventory;
import service.models.Item;
import service.requests.CreateInventoryRequest;

/** This class contains all the API endpoints for inventory-related requests. */
@RestController
@RequestMapping("/api/inventories")
public class InventoryRouteController {

  /** The Inventory table sql helper. */
  @Autowired public InventoryTableSqlHelper inventoryTableSqlHelper;

  @Autowired private ItemsTableSqlHelper itemsTableSqlHelper;

  /**
   * Allow for inventories to be created under a specific user's id. @param createInventoryRequest
   * the create inventory request
   *
   * @param createInventoryRequest the create inventory request
   * @return the response entity
   */
  @PostMapping(value = "/createInventory", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> createInventory(
      @RequestBody CreateInventoryRequest createInventoryRequest) {
    if (createInventoryRequest == null) {
      return new ResponseEntity<>("Empty request", HttpStatus.BAD_REQUEST);
    }
    try {
      Inventory newInventory =
          Inventory.builder()
              .inventoryId(UUID.randomUUID())
              .inventoryName(createInventoryRequest.getInventoryName())
              .build();
      boolean isSuccessful = inventoryTableSqlHelper.insert(newInventory);
      if (!isSuccessful) {
        return new ResponseEntity<>("Failed to create inventory", HttpStatus.INTERNAL_SERVER_ERROR);
      }
      return new ResponseEntity<>(
          "Successfully created inventory: "
              + newInventory.getInventoryId()
              + "\n"
              + newInventory.getInventoryName(),
          HttpStatus.CREATED);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Returns a string representation of the requested inventory.
   *
   * @param inventoryId Unique identifier for the inventory the client would like to access.
   * @return a string representation of the inventory.
   */
  @GetMapping(value = "/getInventoryName", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getInventoryName(
      @RequestParam(value = "inventoryId") String inventoryId) {
    if (inventoryId == null || inventoryId.isEmpty()) {
      return new ResponseEntity<>("inventoryId needed to get inventories.", HttpStatus.BAD_REQUEST);
    }
    try {
      List<Inventory> inventoryList = inventoryTableSqlHelper.select(inventoryId);
      if (inventoryList == null) {
        return new ResponseEntity<>(
            "Inventory with inventoryId: " + inventoryId + " has not been found.",
            HttpStatus.NOT_FOUND);
      }
      String inventoryName = inventoryList.get(0).getInventoryName();
      return new ResponseEntity<>(inventoryName, HttpStatus.OK);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Returns a string representation of the requested inventory's items.
   *
   * @param inventoryId Unique identifier for the inventory the client would like to access.
   * @return a string representation of the inventory's items
   */
  @GetMapping(value = "/getInventoryItems", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> getInventoryItems(
      @RequestParam(value = "inventoryId") String inventoryId) {

    // TODO: Use the junction table to make this request faster

    if (inventoryId == null || inventoryId.isEmpty()) {
      return new ResponseEntity<>("inventoryId needed to get inventories.", HttpStatus.BAD_REQUEST);
    }
    try {
      List<Inventory> inventoryList = inventoryTableSqlHelper.select(inventoryId);
      if (inventoryList == null) {
        return new ResponseEntity<>(
            "Inventory with inventoryId: " + inventoryId + " has not been found.",
            HttpStatus.NOT_FOUND);
      }
      List<Item> itemList = itemsTableSqlHelper.getAllItems();
      String inventoryName = inventoryList.get(0).getInventoryName();
      StringBuilder returnList = new StringBuilder();
      if (itemList.isEmpty()) {
        return new ResponseEntity<>(inventoryName + " is empty.", HttpStatus.OK);
      } else {
        returnList.append(inventoryName + " contains:\n");

        for (Item item : itemList) {
          if (item.getInventoryId() != null) {
            String itemToInventoryReference = item.getInventoryId().toString();
            if (itemToInventoryReference.equals(inventoryId)) {
              returnList
                  .append(item.getQuantity())
                  .append(" ")
                  .append(item.getItemName())
                  .append("(s) \n");
            }
          }
        }
        return new ResponseEntity<>(returnList.toString(), HttpStatus.OK);
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Returns confirmation regarding the change of the inventory's name.
   *
   * @param newInventoryName the new inventory name
   * @param inventoryId Unique identifier for the inventory the client would like to access.
   * @return a string representation of the inventory's new name.
   */
  @PatchMapping(value = "/updateInventoryName", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> updateInventoryName(
      @RequestParam(value = "newInventoryName") String newInventoryName,
      @RequestParam(value = "inventoryId") String inventoryId) {
    if (inventoryId == null || inventoryId.isEmpty()) {
      return new ResponseEntity<>("inventoryId needed to get inventories.", HttpStatus.BAD_REQUEST);
    }
    try {
      boolean updateSuccess = inventoryTableSqlHelper.update(inventoryId, newInventoryName);
      if (updateSuccess) {
        return new ResponseEntity<>("Successfully changed the inventory's name.\n", HttpStatus.OK);
      } else {
        return new ResponseEntity<>(
            "Unsuccessful inventory name change.\n", HttpStatus.INTERNAL_SERVER_ERROR);
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}

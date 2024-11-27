package service.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.handler.InventoryItemsJunctionTableHelper;
import service.handler.InventoryTableSqlHelper;
import service.models.Inventory;
import service.requests.CreateInventoryRequest;

/** This class contains all the API endpoints for inventory-related requests. */
@RestController
@RequestMapping("/api/inventories")
public class InventoryRouteController {

  /** The Inventory table sql helper. */
  @Autowired public InventoryTableSqlHelper inventoryTableSqlHelper;

  @Autowired private InventoryItemsJunctionTableHelper inventoryItemsJunctionTableHelper;

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
      boolean isSuccessful = inventoryTableSqlHelper.insertInventory(newInventory);
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
      Inventory inventory = inventoryTableSqlHelper.getInventoryWithInventoryId(inventoryId);
      if (inventory == null) {
        return new ResponseEntity<>(
            "Inventory with inventoryId: " + inventoryId + " has not been found.",
            HttpStatus.NOT_FOUND);
      }
      String inventoryName = inventory.getInventoryName();
      if (inventoryName.isEmpty()) {
        return new ResponseEntity<>(
            "Inventory with inventoryId: " + inventoryId + " has no name.", HttpStatus.NO_CONTENT);
      }
      return new ResponseEntity<>(inventoryName, HttpStatus.OK);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Returns a list of strings representing the UUID of the items belonging to this inventory.
   *
   * @param inventoryId Unique identifier for the inventory the client would like to access.
   * @return a list of strings representing the UUID of the items belonging to this inventory.
   */
  @GetMapping(value = "/getInventoryItemIds", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<String>> getInventoryItemIds(
      @RequestParam(value = "inventoryId") String inventoryId) {

    if (inventoryId == null || inventoryId.isEmpty()) {
      return new ResponseEntity<>(
          List.of("inventoryId needed to get inventories."), HttpStatus.BAD_REQUEST);
    }
    try {
      Inventory inventory = inventoryTableSqlHelper.getInventoryWithInventoryId(inventoryId);
      if (inventory == null) {
        return new ResponseEntity<>(
            List.of("Inventory with inventoryId: " + inventoryId + " has not been found."),
            HttpStatus.NOT_FOUND);
      }
      List<String> itemIds = inventoryItemsJunctionTableHelper.getItemIdsByInventoryId(inventoryId);

      if (itemIds.isEmpty()) {
        return new ResponseEntity<>(
            List.of("No items found within inventory: " + inventoryId), HttpStatus.NO_CONTENT);
      } else {
        return new ResponseEntity<>(itemIds, HttpStatus.OK);
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
      List<String> errorMessage = new ArrayList<>();
      errorMessage.add(e.getMessage());
      return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Returns a list of strings representing the names of the items belonging to this inventory.
   *
   * @param inventoryId Unique identifier for the inventory the client would like to access.
   * @return a list of strings representing the names of the items belonging to this inventory.
   */
  @GetMapping(value = "/getInventoryItemNames", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<String>> getInventoryItemNames(
      @RequestParam(value = "inventoryId") String inventoryId) {

    if (inventoryId == null || inventoryId.isEmpty()) {
      return new ResponseEntity<>(
          List.of("inventoryId needed to get inventories."), HttpStatus.BAD_REQUEST);
    }
    try {
      Inventory inventory = inventoryTableSqlHelper.getInventoryWithInventoryId(inventoryId);
      if (inventory == null) {
        return new ResponseEntity<>(
            List.of("Inventory with inventoryId: " + inventoryId + " has not been found."),
            HttpStatus.NOT_FOUND);
      }
      List<String> itemNames =
          inventoryItemsJunctionTableHelper.getItemNamesByInventoryId(inventoryId);

      if (itemNames.isEmpty()) {
        return new ResponseEntity<>(
            List.of("No items found within inventory: " + inventoryId), HttpStatus.NO_CONTENT);
      } else {
        return new ResponseEntity<>(itemNames, HttpStatus.OK);
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
      List<String> errorMessage = new ArrayList<>();
      errorMessage.add(e.getMessage());
      return new ResponseEntity<>(errorMessage, HttpStatus.INTERNAL_SERVER_ERROR);
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

    if (newInventoryName == null || newInventoryName.isEmpty()) {
      return new ResponseEntity<>(
          "New inventory name is needed for the update.", HttpStatus.BAD_REQUEST);
    }
    try {
      boolean updateSuccess = inventoryTableSqlHelper.update(inventoryId, newInventoryName);
      if (updateSuccess) {
        return new ResponseEntity<>("Successfully changed the inventory's name.", HttpStatus.OK);
      } else {
        return new ResponseEntity<>(
            "Unsuccessful inventory name change.", HttpStatus.INTERNAL_SERVER_ERROR);
      }
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
  
  /**
   * Delete an adminstrator's chosen inventory from the DB.
   *
   * @param inventoryId unique identifier for an inventory within the DB.
   * @return response entity
   */
  @DeleteMapping(value = "/deleteInventory", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<?> deleteInventory(@RequestParam(value = "inventoryId") String inventoryId) {
    
    if (inventoryId == null || inventoryId.isEmpty()) {
      return new ResponseEntity<>("inventoryId needed to delete inventories.",
          HttpStatus.BAD_REQUEST);
    }
    
    try{
      boolean deleteSuccess = inventoryTableSqlHelper.delete(inventoryId);
      if (!deleteSuccess) {
        return new ResponseEntity<>(
            "Unable to delete inventory with inventoryId: " + inventoryId,
            HttpStatus.FORBIDDEN);
      } else {
        return new ResponseEntity<>(
            "Successfully deleted inventory with inventoryId: " + inventoryId, HttpStatus.OK);
      }
      
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
  }
}

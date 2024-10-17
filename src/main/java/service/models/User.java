package service.models;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Getter;

/** Sample javadoc for User mini class. */
@Getter
public class User {
  private final UUID userId;
  private String username; // Change to FINAL once initialized via DB read
  private List<Inventory> inventoryAccessList;
  private LocalDateTime lastAccess;

  /** User constructor. */
  public User(UUID userId) {
    this.userId = userId;
    populateUserData();
  }

  /** get a specific inventory the user can read. */
  public Inventory getInventory(UUID inventoryId) {
    // TODO: Logic for retrieving a specific Inventory from the DB
    System.out.println("Retrieving inventory with ID: " + inventoryId);
    return null;
  }

  private void populateUserData() {
    // TODO: Add logic to populate other fields based on whats in the DB
    // fetch inventory list
    // fetch lastAccess
    // fetch username
  }

  public void refreshData() {
    populateUserData();
  }
}

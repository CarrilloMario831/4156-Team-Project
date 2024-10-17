package service.models;

import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/** Sample javadoc for Admin mini class. */
@Getter
@Setter
public class Admin extends User {
  private List<Item> itemsCreated;
  private List<Item> itemsEdited;
  private List<Item> itemsDeleted;
  private List<Inventory> inventoriesCreated;

  /** Admin constructor. */
  public Admin(UUID id) {
    super(id);

    // TODO: Add logic to populate Admin fields based on whats in the DB
    // populateAdminData();
  }

  private void populateAdminData() {
    // TODO: Add logic to populate other fields based on whats in the DB
    // fetch items created/edited/deleted
    // fetch inventories created
  }

  @Override
  public void refreshData() {
    super.refreshData();
    populateAdminData();
  }
}

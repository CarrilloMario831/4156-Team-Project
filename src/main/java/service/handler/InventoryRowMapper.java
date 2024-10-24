package service.handler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import service.models.Inventory;

/**
 * We need this class extending the rowMapper function so we can return different types of objects
 * after SQL searches.
 */
public class InventoryRowMapper extends BaseRowMapper<Inventory> {

  /**
   * This method will format the SQL response into an Inventory object that gets returned.
   *
   * @param rs Data coming out of the DB.
   * @param rowNum Number of rows to be grabbed from the search.
   * @return Inventory object representing the searched for inventories.
   * @throws SQLException This is a SQLException we'll have to deal with in the future.
   */
  @Override
  public Inventory mapRow(ResultSet rs, int rowNum) throws SQLException {
    return Inventory.builder()
        .inventoryId(UUID.fromString(rs.getString("inventory_id")))
        .inventoryName(rs.getString("inventory_name"))
        //        .adminId(UUID.fromString(rs.getString("user_id")))
        .build();
  }
}

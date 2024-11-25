package service.handler;

import java.util.List;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * This class handles the translation from java objects to SQL queries into the local MySQL database
 * titled reservation_management. This is only for testing and providing some boilerplate code on
 * how to write JDBC template based queries
 */
@Getter
@Repository
public class InventoryItemsJunctionTableHelper {

  @Autowired private JdbcTemplate jdbcTemplate;

  /**
   * Gets item ids by inventory id.
   *
   * @param inventoryId the inventory id
   * @return the item ids by inventory id
   */
  public List<String> getItemIdsByInventoryId(String inventoryId) {
    String sql = "select * from Inventory_Items where inventory_id = " + "'" + inventoryId + "'";
    return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("item_id"));
  }

  /**
   * Gets item names by inventory id.
   *
   * @param inventoryId the inventory id
   * @return the item names by inventory id
   */
  public List<String> getItemNamesByInventoryId(String inventoryId) {
    String sql = "select * from Inventory_Items where inventory_id = " + "'" + inventoryId + "'";
    return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("item_name"));
  }
}

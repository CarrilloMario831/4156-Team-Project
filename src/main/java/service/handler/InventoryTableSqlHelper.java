package service.handler;

import java.util.List;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import service.models.Inventory;

/**
 * This class handles the translation from java objects to SQL queries into the local MySQL database
 * titled reservation_management.
 */
@Getter
@Repository
public class InventoryTableSqlHelper {

  @Autowired private JdbcTemplate jdbcTemplate;

  /**
   * This is a test insert class for providing an insight into what it looks like to insert
   * inventories into the DB.
   *
   * @param inventory Inventory object that you'd like to store within DB.
   * @return the boolean
   */
  public boolean insert(Inventory inventory) {
    String sql = "insert into Inventories (inventory_id, inventory_name) values (?,?)";
    int rows =
        jdbcTemplate.update(
            sql, inventory.getInventoryId().toString(), inventory.getInventoryName());
    System.out.println(rows + "Inventory row/s inserted.");
    return rows == 1;
  }

  /**
   * This is a test select method for providing insight into what it looks like to read inventories
   * from the DB.
   *
   * @return the list
   */
  public List<Inventory> select() {
    String sql = "select * from Inventories";
    RowMapper<Inventory> rowMapper = new InventoryRowMapper();
    return jdbcTemplate.query(sql, rowMapper);
  }

  /**
   * This is a test select method for providing insight into what it looks like to read inventories
   * from the DB.
   *
   * @param inventoryId Unique identifier for the inventory you'd like to search for in the DB.
   * @return the list
   */
  public List<Inventory> select(String inventoryId) {
    String sql = "select * from Inventories where inventory_id = " + "'" + inventoryId + "'";
    RowMapper<Inventory> rowMapper = new InventoryRowMapper();
    return jdbcTemplate.query(sql, rowMapper);
  }

  /**
   * This method will change the location column for an inventory and returns a boolean representing
   * the success of the query.
   *
   * @param inventoryId Unique identifier for the inventory within the DB.
   * @param inventoryName String representation of the new inventory name
   * @return boolean boolean
   */
  public boolean update(String inventoryId, String inventoryName) {
    String sql = "update Inventories set inventory_name = ? where inventory_id = ?";
    int rows = jdbcTemplate.update(sql, inventoryName, inventoryId);
    System.out.println(rows + " inventory row/s updated");
    return rows > 0;
  }

  /**
   * This method will simply delete.
   *
   * @param inventoryId Unique identifier for the inventory within the DB we'd like to delete.
   * @return boolean representing the number of rows deleted.
   */
  public boolean delete(String inventoryId) {
    String sql = "delete from Inventories where inventory_id = ?";
    int rows = jdbcTemplate.update(sql, inventoryId);
    System.out.println(rows + " row/s deleted");
    return rows > 0;
  }
}

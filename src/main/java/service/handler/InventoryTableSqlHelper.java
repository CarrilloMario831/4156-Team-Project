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

  private JdbcTemplate jdbcTemplate;

  /**
   * This method allows for Spring Boot to auto-manage the beans needed to connect to the SQL DB.
   */
  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * This is a test insert class for providing an insight into what it looks like to insert
   * inventories into the DB.
   *
   * @param inventory Inventory object that you'd like to store within DB.
   * @param userKey This references the user to which this inventory belongs to.
   */
  public void insert(Inventory inventory, String userKey) {
    // Create your insert SQL query with "?" as a placeholder for variable values

    // Inventories is the table within the MySQL DB
    String sql = "insert into Inventories (inventory_id, inventory_name, user_key) values (?,?,?)";

    // JDBC template provides many methods and query() is synonymous with select
    // update() is for the SQL insert, update, deletes
    int rows =
        jdbcTemplate.update(sql, inventory.getInventoryId(), inventory.getInventoryName(), userKey);
    System.out.println(rows + "Inventory row/s inserted.");
  }

  /**
   * This is a test select method for providing insight into what it looks like to read inventories
   * from the DB.
   */
  public List<Inventory> select() {

    // define the sql query
    String sql = "select * from Inventories";

    // This stores the select query results from the DB
    // Edit the way these fields are populating the Inventory object from the InventoryRowMapper
    // class
    RowMapper<Inventory> rowMapper = new InventoryRowMapper();

    // Store the results within an indexable array
    return jdbcTemplate.query(sql, rowMapper);
  }

  /**
   * This is a test select method for providing insight into what it looks like to read inventories
   * from the DB.
   *
   * @param inventoryId Unique identifier for the inventory you'd like to search for in the DB.
   */
  public List<Inventory> select(String inventoryId) {

    // define the sql query
    String sql = "select * from Inventories where inventory_id = " + "'" + inventoryId + "'";

    // This stores the select query results from the DB
    RowMapper<Inventory> rowMapper = new InventoryRowMapper();

    // Store the results within an indexable array
    return jdbcTemplate.query(sql, rowMapper);
  }

  /**
   * This method will change the location column for an inventory and returns a boolean representing
   * the success of the query.
   *
   * @param inventoryId Unique identifier for the inventory within the DB.
   * @param inventoryName String representation of the new inventory name
   * @return boolean
   */
  public boolean update(String inventoryId, String inventoryName) {

    // Example inventoryId to check if this method works 'bf456378-a8b3-40b6-b1a1-654bc9de5f02'
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

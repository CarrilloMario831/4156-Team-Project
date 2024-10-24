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
public class UserInventoryJunctionTableHelper {
  @Autowired private JdbcTemplate jdbcTemplate;

  // Get all inventory IDs accessible to a user
  public List<String> getInventoryIdsByUserId(String userId) {
    // return all the inventories that a User can access
    String sql = "select * from User_Inventories where user_id = " + "'" + userId + "'";
    return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("inventory_id"));
  }

  // Get all users that can access a certain inventory
  public List<String> getUserIdsByInventoryId(String inventoryId) {
    // return all the inventories that a User can access
    String sql = "select * from User_Inventories where inventory_id = " + "'" + inventoryId + "'";
    return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("user_id"));
  }

  // insert userID and inventoryID
  public boolean addUserInventoryAccess(
      String userId, String username, String inventoryId, String inventory_name) {
    String sql =
        "insert into User_Inventories (user_id, username, inventory_id, inventory_name) values (?,?,?,?)";
    int rows = jdbcTemplate.update(sql, userId, username, inventoryId, inventory_name);
    System.out.println(rows + "row/s inserted.");
    return rows == 1;
  }

  // remove userID and inventoryID
  public boolean removeUserInventoryAccess(String userId, String inventoryId) {
    String sql = "delete from User_Inventories where user_id = ? and inventory_id = ?";
    int rows = jdbcTemplate.update(sql, userId, inventoryId);
    System.out.println(rows + "row/s deleted.");
    return rows == 1;
  }
}

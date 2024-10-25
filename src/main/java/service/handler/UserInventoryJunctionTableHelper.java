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

  /**
   * Gets inventory ids by user id.
   *
   * @param userId the user id
   * @return inventory ids by user id
   */
  public List<String> getInventoryIdsByUserId(String userId) {
    String sql = "select * from User_Inventories where user_id = " + "'" + userId + "'";
    return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("inventory_id"));
  }

  /**
   * Gets user ids by inventory id.
   *
   * @param inventoryId the inventory id
   * @return the user ids by inventory id
   */
  public List<String> getUserIdsByInventoryId(String inventoryId) {
    String sql = "select * from User_Inventories where inventory_id = " + "'" + inventoryId + "'";
    return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("user_id"));
  }

  /**
   * Add user inventory access boolean.
   *
   * @param userId the user id
   * @param username the username
   * @param inventoryId the inventory id
   * @param inventoryName the inventory name
   * @return the boolean
   */
  public boolean addUserInventoryAccess(
      String userId, String username, String inventoryId, String inventoryName) {
    String sql =
        "insert into User_Inventories (user_id, username, inventory_id, inventory_name)"
            + " values (?,?,?,?)";
    int rows = jdbcTemplate.update(sql, userId, username, inventoryId, inventoryName);
    System.out.println(rows + "row/s inserted.");
    return rows == 1;
  }

  /**
   * Remove user inventory access boolean.
   *
   * @param userId the user id
   * @param inventoryId the inventory id
   * @return the boolean
   */
  public boolean removeUserInventoryAccess(String userId, String inventoryId) {
    String sql = "delete from User_Inventories where user_id = ? and inventory_id = ?";
    int rows = jdbcTemplate.update(sql, userId, inventoryId);
    System.out.println(rows + "row/s deleted.");
    return rows == 1;
  }
}

package service.handler;

import static service.util.DateTimeUtils.FORMATTER;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import service.models.User;
import service.util.UserRoles;

/**
 * This class handles the translation from java objects to SQL queries into the local MySQL database
 * titled reservation_management. This is only for testing and providing some boilerplate code on
 * how to write JDBC template based queries
 */
@Getter
@Repository
public class UsersTableSqlHelper {

  private JdbcTemplate jdbcTemplate;

  /**
   * This method allows for Spring Boot to auto-manage the beans needed to connect to the SQL DB.
   */
  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * This is a test insert class for providing an insight into what it looks like to insert users
   * into the DB.
   *
   * @param user User object that you'd like to store within DB.
   */
  public boolean insertUser(User user) {
    String sql =
        "insert into Users ("
            + "user_id, username, role, "
            + "last_access, inventory_access) "
            + "values (?,?,?,?,?)";
    int rows =
        jdbcTemplate.update(
            sql,
            user.getUserId().toString(),
            user.getUsername(),
            user.getRole().toString(),
            user.getLastAccess(),
            user.getInventoryAccess() != null ? user.getInventoryAccess().toString() : null);
    System.out.println(rows + "row/s inserted.");
    return rows == 1;
  }

  /**
   * This is a test select method for providing insight into what it looks like to read users from
   * the DB.
   */
  public List<User> getAllUsers() {
    String sql = "select * from Users";
    return jdbcTemplate.query(sql, getRowMapper());
  }

  /**
   * This is a test select method for providing insight into what it looks like to read users from
   * the DB.
   *
   * @param userId Unique identifier for the user you'd like to search for in the DB.
   */
  public User getUserWithUserId(String userId) {
    String sql = "select * from Users where user_id = " + "'" + userId + "'";
    List<User> results = jdbcTemplate.query(sql, getRowMapper());
    if (results.isEmpty()) {
      return null;
    } else if (results.size() > 1) {
      // throw error multiple users of the same userID
      throw new IllegalStateException("More than one user found for user id: " + userId);
    } else {
      return results.get(0);
    }
  }

  /**
   * This is a test select method for providing insight into what it looks like to read users from
   * the DB.
   *
   * @param username Unique identifier for the user you'd like to search for in the DB.
   */
  public User getUserWithUsername(String username) {
    String sql = "select * from Users where username = " + "'" + username + "'";
    List<User> results = jdbcTemplate.query(sql, getRowMapper());
    if (results.isEmpty()) {
      return null;
    } else if (results.size() > 1) {
      throw new IllegalStateException("More than one user found for username: " + username);
    } else {
      return results.get(0);
    }
  }

  /**
   * This method will change the username column for a user and returns a boolean representing the
   * success of the query.
   *
   * @param userId Unique identifier for the user within the DB.
   * @param username username of user.
   * @return boolean
   */
  public boolean updateUsername(String userId, String username) {
    String sql = "update Users set username = ? where user_id = ?";
    int rows = jdbcTemplate.update(sql, username, userId);
    System.out.println(rows + " row/s updated");
    return rows == 1;
  }

  /**
   * This method will change the role column for a user and returns a boolean representing the
   * success of the query.
   *
   * @param userId Unique identifier for the user within the DB.
   * @param userRole role of user.
   * @return boolean
   */
  public boolean updateRole(String userId, String userRole) {
    String sql = "update Users set role = ? where user_id = ?";
    int rows = jdbcTemplate.update(sql, userRole, userId);
    System.out.println(rows + " row/s updated");
    return rows == 1;
  }

  /**
   * This method will change the last access column for a user and returns a boolean representing
   * the success of the query.
   *
   * @param userId Unique identifier for the user within the DB.
   * @param lastAccess lastAccess of user.
   * @return boolean
   */
  public boolean updateLastAccess(String userId, LocalDateTime lastAccess) {
    String sql = "update Users set last_access = ? where user_id = ?";
    int rows = jdbcTemplate.update(sql, lastAccess, userId);
    System.out.println(rows + " row/s updated");
    return rows == 1;
  }

  /**
   * This method will change the inventory access column for a user and returns a boolean
   * representing the success of the query.
   *
   * @param userId Unique identifier for the user within the DB.
   * @param inventoryAccess lastAccess of user.
   * @return boolean
   */
  public boolean updateInventoryAccess(String userId, String inventoryAccess) {
    String sql = "update Users set inventory_access = ? where user_id = ?";
    int rows = jdbcTemplate.update(sql, inventoryAccess, userId);
    System.out.println(rows + " row/s updated");
    return rows == 1;
  }

  /**
   * This method will simply delete user from the table.
   *
   * @param userId Unique identifier for the user within the DB we'd like to delete
   * @return boolean representing the number of rows deleted.
   */
  public boolean delete(String userId) {
    String sql = "delete from Users where user_id = ?";
    int rows = jdbcTemplate.update(sql, userId);
    System.out.println(rows + " row/s deleted");
    return rows == 1;
  }

  private RowMapper<User> getRowMapper() {
    return (rs, rowNum) ->
        User.builder()
            .userId(UUID.fromString(rs.getString("user_id")))
            .username(rs.getString("username"))
            .role(UserRoles.valueOf(rs.getString("role")))
            .lastAccess(LocalDateTime.parse(rs.getString("last_access"), FORMATTER))
            .inventoryAccess(
                rs.getString("inventory_access") != null
                    ? UUID.fromString(rs.getString("inventory_access"))
                    : null)
            .build();
  }
}

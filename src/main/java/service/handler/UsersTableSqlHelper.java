package service.handler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
  private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
  public void insert(User user) {
    // Create your insert SQL query with "?" as a placeholder for variable
    // values

    // Users is the table within the MySQL DB
    String sql =
        "insert into Users ("
            + "user_id, username, role, "
            + "last_access, inventory_access) "
            + "values (?,?,?,?,?)";

    // JDBC template provides many methods and query() is synomous with select
    // update() is for the SQL insert, update, deletes
    int rows =
        jdbcTemplate.update(
            sql,
            user.getUserId().toString(),
            user.getUsername(),
            user.getRole().toString(),
            user.getLastAccess(),
            user.getInventoryAccess() != null ? user.getInventoryAccess().toString() : null);
    System.out.println(rows + "row/s inserted.");
  }

  /**
   * This is a test select method for providing insight into what it looks like to read users from
   * the DB.
   */
  public List<User> select() {

    // define the sql query
    String sql = "select * from Users";

    // This stores the select query results from the DB
    RowMapper<User> rowMapper =
        (rs, rowNum) ->
            User.builder()
                .userId(UUID.fromString(rs.getString("user_id")))
                .username(rs.getString("username"))
                .role(UserRoles.valueOf(rs.getString("role")))
                .lastAccess(LocalDateTime.parse(rs.getString("last_access"), formatter))
                .inventoryAccess(
                    rs.getString("inventory_access") != null
                        ? UUID.fromString(rs.getString("inventory_access"))
                        : null)
                .build();

    // Store the results within an indexable array
    return jdbcTemplate.query(sql, rowMapper);
  }

  /**
   * This is a test select method for providing insight into what it looks like to read users from
   * the DB.
   *
   * @param userId Unique identifier for the user you'd like to search for in the DB.
   */
  public User select(String userId) {

    // define the sql query
    String sql = "select * from Users where user_id = " + "'" + userId + "'";

    // This stores the select query results from the DB
    RowMapper<User> rowMapper =
        (rs, rowNum) -> {
          return User.builder()
              .userId(UUID.fromString(rs.getString("user_id")))
              .username(rs.getString("username"))
              .role(UserRoles.valueOf(rs.getString("role")))
              .lastAccess(LocalDateTime.parse(rs.getString("last_access"), formatter))
              .inventoryAccess(
                  rs.getString("inventory_access") != null
                      ? UUID.fromString(rs.getString("inventory_access"))
                      : null)
              .build();
        };

    // Store the results within an indexable array
    List<User> results = jdbcTemplate.query(sql, rowMapper);

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
   * This method will change the inventory access column for a user and returns a boolean
   * representing the success of the query.
   *
   * @param userId Unique identifier for the user within the DB.
   * @param inventoryId Unique identifier for the Inventory that the user can access.
   * @return boolean
   */
  public boolean update(String userId, String inventoryId) {
    String sql = "update Users set inventory_access = ? where user_id = ?";
    int rows = jdbcTemplate.update(sql, inventoryId, userId);

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
}

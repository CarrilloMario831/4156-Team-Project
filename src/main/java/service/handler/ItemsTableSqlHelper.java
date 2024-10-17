package service.handler;

import java.util.List;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import service.models.Item;

/**
 * This class handles the translation from java objects to SQL queries into the local MySQL database
 * titled reservation_management. This is only for testing and providing some boilerplate code on
 * how to write JDBC template based queries.
 */
@Getter
@Repository
public class ItemsTableSqlHelper {

  private JdbcTemplate jdbcTemplate;

  /**
   * This method allows for Spring Boot to auto-manage the beans needed to connect to the SQL DB.
   */
  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  /**
   * This is a test insert class for providing an insight into what it looks like to insert items
   * into the DB.
   *
   * @param item Item object that you'd like to store within DB.
   */
  public void insert(Item item) {
    // Create your insert SQL query with "?" as a placeholder for variable
    // values

    // Items is the table within the MySQL DB
    String sql =
        "insert into Items ("
            + "item_name, time_of_addition, quantity, "
            + "reserved_status, reservation_time, reservation_duration, "
            + "location, price, next_restock) "
            + "values (?,?,?,?,?,?,?,?,?)";

    // JDBC template provides many methods and query() is synonymous with select
    // update() is for the SQL insert, update, deletes
    int rows =
        jdbcTemplate.update(
            sql,
            item.getItemName(),
            item.getTimeOfAddition(),
            item.getQuantity(),
            item.isReservationStatus(),
            item.getReservationTime(),
            item.getReservationDurationInMillis(),
            item.getLocation(),
            item.getPrice(),
            item.getNextRestockDateTime());
    System.out.println(rows + "row/s inserted.");
  }

  /**
   * This is a test select method for providing insight into what it looks like to read items from
   * the DB.
   */
  public List<Item> select() {

    // define the sql query
    String sql = "select * from Items";

    // This stores the select query results from the DB
    RowMapper<Item> rowMapper = new ItemRowMapper();

    // Store the results within an indexable array
    return jdbcTemplate.query(sql, rowMapper);
  }

  /**
   * This is a test select method for providing insight into what it looks like to read items from
   * the DB.
   *
   * @param uuid Unique identifier for the item you'd like to search for in the DB.
   */
  public List<Item> select(String uuid) {

    // define the sql query
    String sql = "select * from Items where uuid = " + "'" + uuid + "'";

    // This stores the select query results from the DB
    RowMapper<Item> rowMapper = new ItemRowMapper();

    // Store the results within an indexable array
    return jdbcTemplate.query(sql, rowMapper);
  }

  /**
   * This method will change the location column for an item and returns a boolean representing the
   * success of the query.
   *
   * @param uuid Unique identifier for the item within the DB.
   * @param location String representation of the new location where the item is stored
   * @return Return true or false whether the update was done.
   */
  public boolean update(String uuid, String location) {

    // UPDATE Items SET item_name = 'Cheese Nuggets' where uuid =
    // '0b1ee0b0-8bf0-11ef-9fd0-343c922917f9'
    String sql = "update Items set location = ? where uuid = ?";
    int rows = jdbcTemplate.update(sql, location, uuid);

    System.out.println(rows + " row/s updated");
    return rows > 0;
  }

  /**
   * This method will simply delete.
   *
   * @param uuid Unique identifier for the item within the DB we'd like to delete
   * @return boolean representing the number of rows deleted.
   */
  public boolean delete(String uuid) {
    String sql = "delete from Items where uuid = ?";

    int rows = jdbcTemplate.update(sql, uuid);
    System.out.println(rows + " row/s deleted");

    return rows > 0;
  }
}

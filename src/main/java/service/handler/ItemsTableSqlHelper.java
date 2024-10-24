package service.handler;

import static service.util.DateTimeUtils.FORMATTER;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
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
   *
   * @param jdbcTemplate the jdbc template
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
   * @return the boolean
   */
  public boolean insertItem(Item item) {
    String sql =
        "insert into Items ("
            + "item_id, item_name, time_of_addition, quantity, "
            + "reserved_status, reservation_time, reservation_duration, "
            + "location, price, next_restock, inventory_id) "
            + "values (?,?,?,?,?,?,?,?,?,?, ?)";
    int rows =
        jdbcTemplate.update(
            sql,
            item.getItemId().toString(),
            item.getItemName(),
            item.getTimeOfAddition(),
            item.getQuantity(),
            item.isReservationStatus(),
            item.getReservationTime(),
            item.getReservationDurationInMillis(),
            item.getLocation(),
            item.getPrice(),
            item.getNextRestockDateTime(),
            item.getInventoryId() != null ? item.getInventoryId().toString() : null);
    return rows == 1;
  }

  /**
   * This is a test select method for providing insight into what it looks like to read items from
   * the DB.
   *
   * @return the all items
   */
  public List<Item> getAllItems() {
    String sql = "select * from Items";
    RowMapper<Item> rowMapper = (rs, rowNum) -> getItemFromTable(rs);
    return jdbcTemplate.query(sql, rowMapper);
  }

  /**
   * This is a test select method for providing insight into what it looks like to read items from
   * the DB.
   *
   * @param itemId Unique identifier for the item you'd like to search for in the DB.
   * @return the item
   */
  public List<Item> getItem(String itemId) {
    String sql = "select * from Items where item_id = " + "'" + itemId + "'";
    RowMapper<Item> rowMapper = (rs, rowNum) -> getItemFromTable(rs);
    return jdbcTemplate.query(sql, rowMapper);
  }

  private Item getItemFromTable(ResultSet rs) throws SQLException {
    return Item.builder()
        .itemId(UUID.fromString(rs.getString("item_id")))
        .timeOfAddition(LocalDateTime.parse(rs.getString("time_of_addition"), FORMATTER))
        .itemName(rs.getString("item_name"))
        .quantity(rs.getInt("quantity"))
        .location(rs.getString("location"))
        .price(rs.getDouble("price"))
        .reservationDurationInMillis(rs.getLong("reservation_duration"))
        .reservationStatus(rs.getBoolean("reserved_status"))
        .reservationTime(
            (rs.getString("reservation_time") != null
                ? LocalDateTime.parse(rs.getString("reservation_time"), FORMATTER)
                : null))
        .nextRestockDateTime(
            rs.getString("next_restock") != null
                ? LocalDateTime.parse(rs.getString("next_restock"), FORMATTER)
                : null)
        .build();
  }

  /**
   * This method will change the location column for an item and returns a boolean representing the
   * success of the query.
   *
   * @param itemId Unique identifier for the item within the DB.
   * @param location String representation of the new location where the item is stored
   * @return Return true or false whether the update was done.
   */
  public boolean updateItemLocation(String itemId, String location) {
    String sql = "update Items set location = ? where item_id = ?";
    int rows = jdbcTemplate.update(sql, location, itemId);
    System.out.println(rows + " row/s updated");
    return rows == 1;
  }

  /**
   * This method will change the location column for an item and returns a boolean representing the
   * success of the query.
   *
   * @param itemId Unique identifier for the item within the DB.
   * @param newPrice the new price of the item
   * @return Return true or false whether the update was done.
   */
  public boolean updateItemPrice(String itemId, double newPrice) {
    String sql = "update Items set price = ? where item_id = ?";
    int rows = jdbcTemplate.update(sql, newPrice, itemId);
    System.out.println(rows + " row/s updated");
    return rows == 1;
  }

  /**
   * This method will change the quantity column for an item and returns a boolean representing the
   * success of the query.
   *
   * @param itemId Unique identifier for the item within the DB.
   * @param newQuantity int of new quantity to change
   * @return Return true or false whether the update was done.
   */
  public boolean updateItemQuantity(String itemId, int newQuantity) {
    String sql = "update Items set quantity = ? where item_id = ?";
    int rows = jdbcTemplate.update(sql, newQuantity, itemId);

    System.out.println(rows + " row/s updated");
    return rows == 1;
  }

  /**
   * This method will simply delete item from the DB.
   *
   * @param itemId Unique identifier for the item within the DB we'd like to delete
   * @return boolean representing the number of rows deleted.
   */
  public boolean deleteItem(String itemId) {
    String sql = "delete from Items where item_id = ?";

    int rows = jdbcTemplate.update(sql, itemId);
    System.out.println(rows + " row/s deleted");

    return rows == 1;
  }

  /**
   * pass checkstyle. @param itemId the item id
   *
   * @param itemId the item id
   * @param newItemName the new item name
   * @return the boolean
   */
  public boolean updateItemName(String itemId, String newItemName) {
    String sql = "update Items set item_name = ? where item_id = ?";
    int rows = jdbcTemplate.update(sql, newItemName, itemId);
    System.out.println(rows + " row/s updated");
    return rows == 1;
  }

  /**
   * This method will update the reservation duration for an item.
   *
   * @param itemId Unique identifier for the item within the DB.
   * @param reservationDurationInMillis New reservation duration in milliseconds.
   * @return Return true or false whether the update was done.
   */
  public boolean updateItemReservation(String itemId, long reservationDurationInMillis) {
    String sql =
        "update Items set reservation_duration = ?, reserved_status = true where item_id" + " = ?";
    int rows = jdbcTemplate.update(sql, reservationDurationInMillis, itemId);
    System.out.println(rows + " row/s updated.");
    return rows == 1;
  }

  /**
   * This method will cancel the reservation of an item by clearing the reservation fields.
   *
   * @param itemId Unique identifier for the item within the DB.
   * @return Return true or false whether the update was done.
   */
  public boolean cancelItemReservation(String itemId) {
    String sql =
        "update Items set reserved_status = false, "
            + "reservation_time = null, "
            + "reservation_duration = 0 "
            + "where item_id = ?";
    int rows = jdbcTemplate.update(sql, itemId);
    System.out.println(rows + " row/s updated.");
    return rows == 1;
  }
}

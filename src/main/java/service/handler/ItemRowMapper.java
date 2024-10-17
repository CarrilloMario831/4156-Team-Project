package service.handler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import service.models.Item;

/**
 * We need this class extending the rowMapper function so we can return different types of objects
 * after SQL searches.
 */
public class ItemRowMapper extends BaseRowMapper<Item> {

  private final DateTimeFormatter formatter =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");

  /**
   * This method will format the SQL response into an Item object that gets returned.
   *
   * @param rs Data coming out of DB.
   * @param rowNum Number of rows to be grabbed from the search.
   * @return Item this is an object representing the searched for Items.
   * @throws SQLException This is a SQLException we'll have to deal with in the future.
   */
  @Override
  public Item mapRow(ResultSet rs, int rowNum) throws SQLException {
    return Item.builder()
        .itemId(UUID.fromString(rs.getString("uuid")))
        .timeOfAddition(LocalDateTime.parse(rs.getString("time_of_addition"), formatter))
        .itemName(rs.getString("item_name"))
        .quantity(rs.getInt("quantity"))
        .location(rs.getString("location"))
        .price(rs.getDouble("price"))
        .reservationDurationInMillis(rs.getLong("reservation_duration"))
        .reservationStatus(rs.getBoolean("reserved_status"))
        .reservationTime(LocalDateTime.parse(rs.getString("reservation_time"), formatter))
        .nextRestockDateTime(LocalDateTime.parse(rs.getString("next_restock"), formatter))
        .build();
  }
}

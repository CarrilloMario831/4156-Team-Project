package service.service;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.awt.event.ItemEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles the translation from java objects to SQL queries into
 * the local MySQL database titled reservation_management.
 * This is only for testing and providing some boilerplate code on how to
 * write JDBC template based queries
 */
@Getter
@Repository
public class sql_test_item_repo {
  
  private JdbcTemplate jdbcTemplate;
  
  /**
   * This method allows for Spring Boot to auto-manage the beans
   * needed to connect to the SQL DB.
   */
  @Autowired
  public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }
  
  /**
   * This is a test insert class for providing an insight into what
   * it looks like to insert items into the DB.
   */
  public void insert(sql_test_item item){
    // Create your insert SQL query with "?" as a placeholder for variable
    // values
    
    //Items is the table within the MySQL DB
    String sql = "insert into Items ("
                 + "item_name, time_of_addition, quantity, "
                 + "reserved_status, reservation_time, "
                 + "location, price, next_restock) "
                 + "values (?,?,?,?,?,?,?,?)";
    
    // JDBC template provides many methods and query() is synomous with select
    // update() is for the SQL insert, update, deletes
    int rows = jdbcTemplate.update(
        sql,
        item.getItemName(),
        item.getTimeOfAddition(),
        item.getQuantity(),
        item.isReservationStatus(),
        item.getReservationTime(),
        item.getLocation(),
        item.getPrice(),
        item.getNextRestockDateTime()
        );
    
    System.out.println(rows + "row/s affected");
  }
  
}

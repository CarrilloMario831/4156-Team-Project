package service.handler;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;

/**
 * This class simply allows for us to use the mapRow method with different classes.
 *
 * @param <T> This is the type of object the mapRow method will be working with and return.
 */
public abstract class BaseRowMapper<T> implements RowMapper<T> {

  /**
   * This method will format the SQL response into an abstract object of type T.
   *
   * @param rs Data coming out of the DB.
   * @param rowNum Number of rows to be grabbed from the search.
   * @return abstract object of type T representing the searched for T objects.
   * @throws SQLException This is a SQLException we'll have to deal with in the future.
   */
  @Override
  public abstract T mapRow(ResultSet rs, int rowNum) throws SQLException;
}

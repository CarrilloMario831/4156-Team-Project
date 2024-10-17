package service.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/** Sample JavaDoc to pass checkstyle. */
@SpringBootApplication
public class ServiceApplication {

  public static void main(String[] args) {
    ApplicationContext context = SpringApplication.run(ServiceApplication.class, args);

    // create a sql_test_item object to test inserting & updating within the DB
    sql_test_item item = new sql_test_item("Cheese", 2, "Home", 2.99);
    sql_test_item item2 = new sql_test_item("Chicken Feet", 3000, "Ya Motha's House", 17.38);
    item.setReservationStatus(true);
    item2.setReservationStatus(true);

    // reference to the repository defining how to query our DB
    sql_test_item_repo repo = context.getBean(sql_test_item_repo.class);
    repo.insert(item);
    repo.insert(item2);

    System.out.println(repo.select());
    System.out.println(repo.select("c56a4180-65aa-42ec-a945-5fd21dec0538"));
    repo.update("c56a4180-65aa-42ec-a945-5fd21dec0538", "NYC");
    repo.delete("c56a4180-65aa-42ec-a945-5fd21dec0538");
  }
}

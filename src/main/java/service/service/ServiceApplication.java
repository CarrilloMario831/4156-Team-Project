package service.service;

import java.time.Duration;
import java.time.LocalDateTime;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import service.handler.ItemsTableSQLHelper;
import service.models.Item;

/** Sample JavaDoc to pass checkstyle. */
@SpringBootApplication(scanBasePackages = {"service", "service.handler"})
public class ServiceApplication {

  public static void main(String[] args) {
    ApplicationContext context = SpringApplication.run(ServiceApplication.class, args);

    // create a sql_test_item object to test inserting & updating within the DB
    Item item1 =
        Item.builder()
            .price(2.99)
            .timeOfAddition(LocalDateTime.now())
            .location("Ya Grandma's House")
            .quantity(5)
            .itemName("Socks")
            .reservationTime(LocalDateTime.now())
            .reservationStatus(true)
            .reservationDurationInMillis(Duration.ofMinutes(45).toMillis())
            .nextRestockDateTime(LocalDateTime.now())
            .build();

    Item item2 =
        Item.builder()
            .price(17.38)
            .timeOfAddition(LocalDateTime.now())
            .location("Ya Motha's House")
            .quantity(3000)
            .itemName("Chicken feet")
            .reservationTime(LocalDateTime.now())
            .reservationDurationInMillis(Duration.ofHours(3).toMillis())
            .nextRestockDateTime(LocalDateTime.now())
            .reservationStatus(true)
            .build();

    // reference to the repository defining how to query our DB
    ItemsTableSQLHelper repo = context.getBean(ItemsTableSQLHelper.class);
    repo.insert(item1);
    repo.insert(item2);

    System.out.println(repo.select());
    System.out.println(repo.select("c56a4180-65aa-42ec-a945-5fd21dec0538"));
    repo.update("c56a4180-65aa-42ec-a945-5fd21dec0538", "NYC");
    repo.delete("c56a4180-65aa-42ec-a945-5fd21dec0538");
  }
}

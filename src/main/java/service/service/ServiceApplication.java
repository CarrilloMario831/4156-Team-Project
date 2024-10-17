package service.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import service.handler.ItemsTableSqlHelper;
import service.handler.UsersTableSqlHelper;
import service.models.Item;
import service.models.User;
import service.util.UserRoles;

/** Sample JavaDoc to pass checkstyle. */
@SpringBootApplication(scanBasePackages = {"service", "service.handler"})
public class ServiceApplication {

  /** Sample javadoc to pass checkstyle. */
  public static void main(String[] args) {
    ApplicationContext context = SpringApplication.run(ServiceApplication.class, args);

    LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    // create a sql_test_item object to test inserting & updating within the DB
    Item item1 =
        Item.builder()
            .price(2.99)
            .timeOfAddition(now)
            .location("Ya Grandma's House")
            .quantity(5)
            .itemName("Socks")
            .reservationTime(now)
            .reservationStatus(true)
            .reservationDurationInMillis(Duration.ofMinutes(45).toMillis())
            .nextRestockDateTime(now)
            .build();

    Item item2 =
        Item.builder()
            .price(17.38)
            .timeOfAddition(now)
            .location("Ya Motha's House")
            .quantity(3000)
            .itemName("Chicken feet")
            .reservationTime(now)
            .reservationDurationInMillis(Duration.ofHours(3).toMillis())
            .nextRestockDateTime(now)
            .reservationStatus(true)
            .build();

    // reference to the repository defining how to query our DB
    ItemsTableSqlHelper itemsTableSqlHelper = context.getBean(ItemsTableSqlHelper.class);
    itemsTableSqlHelper.insert(item1);
    itemsTableSqlHelper.insert(item2);

    System.out.println(itemsTableSqlHelper.select());
    System.out.println(itemsTableSqlHelper.select("c56a4180-65aa-42ec-a945-5fd21dec0538"));
    itemsTableSqlHelper.update("c56a4180-65aa-42ec-a945-5fd21dec0538", "NYC");
    itemsTableSqlHelper.delete("c56a4180-65aa-42ec-a945-5fd21dec0538");

    // reference to the helper to test User operations
    UsersTableSqlHelper usersTableSqlHelper = context.getBean(UsersTableSqlHelper.class);

    UUID userId1 = UUID.randomUUID();
    UUID userId2 = UUID.randomUUID();
    User user1 =
        User.builder()
            .userId(userId1)
            .username("sjimenez814")
            .role(UserRoles.ADMIN)
            .lastAccess(now)
            .build();
    User user2 =
        User.builder()
            .userId(userId2)
            .username("superMario35")
            .role(UserRoles.USER)
            .lastAccess(now)
            .build();
    usersTableSqlHelper.insert(user1);
    usersTableSqlHelper.insert(user2);

    System.out.println(usersTableSqlHelper.select());
    System.out.println(usersTableSqlHelper.select(userId1.toString()));
    usersTableSqlHelper.update(userId1.toString(), "c56a4180-65aa-42ec-a945-5fd21dec0538");
    usersTableSqlHelper.delete(userId2.toString());
  }
}

package service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import service.handler.InventoryTableSqlHelper;
import service.handler.ItemsTableSqlHelper;
import service.handler.UsersTableSqlHelper;
import service.models.Inventory;
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
            .itemId(UUID.randomUUID())
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
            .itemId(UUID.randomUUID())
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
    itemsTableSqlHelper.insertItem(item1);
    itemsTableSqlHelper.insertItem(item2);

    System.out.println(itemsTableSqlHelper.getAllItems());
    System.out.println(itemsTableSqlHelper.getItem("c56a4180-65aa-42ec-a945-5fd21dec0538"));
    itemsTableSqlHelper.updateItemLocation("c56a4180-65aa-42ec-a945-5fd21dec0538", "NYC");
    itemsTableSqlHelper.deleteItem("c56a4180-65aa-42ec-a945-5fd21dec0538");

    List<Item> itemsList = itemsTableSqlHelper.getItem("c56a4180-65aa-42ec-a945-5fd21dec0538");
    // Populating the hashmap some with some sample items
    Map<UUID, Item> itemsMap = new HashMap<>();
    for (Item item : itemsList) {
      itemsMap.put(item.getItemId(), item);
    }
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
    usersTableSqlHelper.insertUser(user1);
    usersTableSqlHelper.insertUser(user2);

    System.out.println(usersTableSqlHelper.getAllUsers());
    System.out.println(usersTableSqlHelper.getUserWithUserId(userId1.toString()));
    usersTableSqlHelper.updateUsername(userId1.toString(), "daKing827");
    usersTableSqlHelper.delete(userId2.toString());

    // converting admin1's ID to a UUID
    String admin1Id = "f1234567-abcd-4d5e-9999-abcdef012345";
    UUID admin1Uuid = UUID.fromString(admin1Id);
    UUID inventoryUuid = UUID.randomUUID();

    // Testing the inventory
    InventoryTableSqlHelper inventoryRepo = context.getBean(InventoryTableSqlHelper.class);
    Inventory production =
        Inventory.builder()
            .inventoryId(inventoryUuid)
            .inventoryName("prod inventory")
            .items(itemsMap)
            .adminId(admin1Uuid)
            .build();

    // give admin1 one more inventory that they can add items to
    inventoryRepo.insert(production, admin1Id);
    System.out.println(inventoryRepo.select());
    System.out.println(inventoryRepo.select(inventoryUuid.toString()));
    inventoryRepo.update("bf456378-a8b3-40b6-b1a1-654bc9de5f02", "Seba Dance Inventory");
    inventoryRepo.delete("bd123478-12ab-45f6-abc8-4456ac987654");
  }
}

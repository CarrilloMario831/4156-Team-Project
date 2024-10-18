package service.service;

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
import service.models.Inventory;
import service.models.Item;

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

    //  System.out.println(itemsTableSqlHelper.select());
    System.out.println(itemsTableSqlHelper.select("c56a4180-65aa-42ec-a945-5fd21dec0538"));
    itemsTableSqlHelper.update("c56a4180-65aa-42ec-a945-5fd21dec0538", "NYC");
    itemsTableSqlHelper.delete("c56a4180-65aa-42ec-a945-5fd21dec0538");

    List<Item> itemsList = itemsTableSqlHelper.select("c56a4180-65aa-42ec-a945-5fd21dec0538");
    // Populating the hashmap some with some sample items
    Map<UUID, Item> itemsMap = new HashMap<>();
    for (Item item : itemsList) {
      itemsMap.put(item.getItemId(), item);
    }
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
  }
}

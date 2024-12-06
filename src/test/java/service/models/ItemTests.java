package service.models;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/** The type Item tests. */
@SpringBootTest
public class ItemTests {

  /** Test builder and getters. */
  @Test
  public void testBuilderAndGetters() {
    UUID itemId = UUID.randomUUID();
    UUID inventoryId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    Item item =
        Item.builder()
            .itemId(itemId)
            .itemName("Test Item")
            .timeOfAddition(now)
            .location("A1-B2")
            .inventoryId(inventoryId)
            .quantity(10)
            .reservationStatus(false)
            .price(19.99)
            .nextRestockDateTime(now.plusDays(7))
            .build();

    assertThat(item.getItemId()).isEqualTo(itemId);
    assertThat(item.getItemName()).isEqualTo("Test Item");
    assertThat(item.getTimeOfAddition()).isEqualTo(now);
    assertThat(item.getLocation()).isEqualTo("A1-B2");
    assertThat(item.getInventoryId()).isEqualTo(inventoryId);
    assertThat(item.getQuantity()).isEqualTo(10);
    assertThat(item.isReservationStatus()).isFalse();
    assertThat(item.getPrice()).isEqualTo(19.99);
    assertThat(item.getNextRestockDateTime()).isEqualTo(now.plusDays(7));
  }

  /** Test setters. */
  @Test
  public void testSetters() {
    Item item =
        Item.builder()
            .itemId(UUID.randomUUID())
            .itemName("Minimal Item")
            .timeOfAddition(LocalDateTime.now())
            .location("A1")
            .inventoryId(UUID.randomUUID())
            .quantity(0)
            .reservationStatus(false)
            .price(0.0)
            .build();
    UUID itemId = UUID.randomUUID();
    UUID inventoryId = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    item.setItemId(itemId);
    item.setItemName("Updated Item");
    item.setTimeOfAddition(now);
    item.setLocation("C3-D4");
    item.setInventoryId(inventoryId);
    item.setQuantity(20);
    item.setReservationStatus(true);
    item.setReservationDurationInMillis(7200000);
    item.setReservationTime(now.minusHours(2));
    item.setPrice(29.99);
    item.setNextRestockDateTime(now.plusDays(3));

    assertThat(item.getItemId()).isEqualTo(itemId);
    assertThat(item.getItemName()).isEqualTo("Updated Item");
    assertThat(item.getTimeOfAddition()).isEqualTo(now);
    assertThat(item.getLocation()).isEqualTo("C3-D4");
    assertThat(item.getInventoryId()).isEqualTo(inventoryId);
    assertThat(item.getQuantity()).isEqualTo(20);
    assertThat(item.isReservationStatus()).isTrue();
    assertThat(item.getReservationDurationInMillis()).isEqualTo(7200000);
    assertThat(item.getReservationTime()).isEqualTo(now.minusHours(2));
    assertThat(item.getPrice()).isEqualTo(29.99);
    assertThat(item.getNextRestockDateTime()).isEqualTo(now.plusDays(3));
  }

  /** Test to string without optional fields. */
  @Test
  void testToStringWithoutOptionalFields() {
    Item item =
        Item.builder()
            .itemId(UUID.randomUUID())
            .itemName("Minimal Item")
            .timeOfAddition(LocalDateTime.now())
            .location("A1")
            .inventoryId(UUID.randomUUID())
            .quantity(0)
            .reservationStatus(false)
            .price(0.0)
            .build();

    String toStringOutput = item.toString();

    assertThat(toStringOutput).contains("Item Details:");
    assertThat(toStringOutput).contains("Name: Minimal Item");
    assertThat(toStringOutput).contains("Quantity: 0");
    assertThat(toStringOutput).contains("Reservation Status: Available");
    assertThat(toStringOutput).doesNotContain("Reservation Duration");
    assertThat(toStringOutput).doesNotContain("Reservation Time");
    assertThat(toStringOutput).doesNotContain("Next Restock Date");
  }

  /** Test to string with all fields. */
  @Test
  void testToStringWithAllFields() {
    LocalDateTime now = LocalDateTime.now();
    Item item =
        Item.builder()
            .itemId(UUID.randomUUID())
            .itemName("Full Item")
            .timeOfAddition(now)
            .location("B2")
            .inventoryId(UUID.randomUUID())
            .quantity(5)
            .reservationStatus(true)
            .reservationDurationInMillis(1800000)
            .reservationTime(now.minusMinutes(30))
            .price(15.75)
            .nextRestockDateTime(now.plusDays(2))
            .build();

    String toStringOutput = item.toString();

    assertThat(toStringOutput).contains("Item Details:");
    assertThat(toStringOutput).contains("Name: Full Item");
    assertThat(toStringOutput).contains("Reservation Status: Reserved");
    assertThat(toStringOutput).contains("Reservation Duration: 1800000 milliseconds");
    assertThat(toStringOutput).contains("Reservation Time: " + now.minusMinutes(30));
    assertThat(toStringOutput).contains("Next Restock Date: " + now.plusDays(2));
  }

  /** Test to string with null restock date time. */
  @Test
  void testToStringWithNullRestockDateTime() {
    Item item =
        Item.builder()
            .itemId(UUID.randomUUID())
            .itemName("No Restock Item")
            .timeOfAddition(LocalDateTime.now())
            .location("A3")
            .inventoryId(UUID.randomUUID())
            .quantity(3)
            .reservationStatus(false)
            .price(12.50)
            .nextRestockDateTime(null)
            .build();

    String toStringOutput = item.toString();

    assertThat(toStringOutput).doesNotContain("Next Restock Date");
  }
}

package service.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the Item class.
 */
@SpringBootTest
public class ItemUnitTests {

  private Item testItem;

  /**
   * Sets up a test item before each test method.
   */
  @BeforeEach
  public void setUpItemForTesting() {
    testItem = new Item("Toyota Forklift", 2, "Garage", 999.99);
  }

  /**
   * Tests the constructor of the Item class.
   */
  @Test
  public void testItemConstructor() {
    assertNotNull(testItem.getUuid(), "UUID should not be null");
    assertEquals("Toyota Forklift", testItem.getItemName(), "Item name should match");
    assertEquals(2, testItem.getQuantity(), "Quantity should be initialized to 2");
    assertEquals("Garage", testItem.getLocation(), "Location should match");
    assertEquals(999.99, testItem.getPrice(), 0.01, "Price should match");
    assertNotNull(testItem.getTimeOfAddition(), "Time of addition should not be null");
    assertFalse(testItem.isReservationStatus(), "Reservation status should be false by default");
    assertEquals(Duration.ZERO, testItem.getReservationDuration(),
            "Reservation duration should be zero by default");
    assertNull(testItem.getReservationTime(), "Reservation time should be null by default");
    assertNull(testItem.getNextRestockDateTime(), "Next restock date should be null by default");
  }

  /**
   * Tests adjusting the quantity of the item by a positive number.
   */
  @Test
  public void testAdjustQuantityPositive() {
    int initialQuantity = testItem.getQuantity();
    testItem.adjustQuantity(3);
    assertEquals(initialQuantity + 3, testItem.getQuantity(), "Quantity should increase by 3");
  }

  /**
   * Tests adjusting the quantity of the item by a negative number.
   */
  @Test
  public void testAdjustQuantityNegative() {
    int initialQuantity = testItem.getQuantity();
    testItem.adjustQuantity(-1);
    assertEquals(initialQuantity - 1, testItem.getQuantity(), "Quantity should decrease by 1");
  }

  /**
   * Tests that the quantity cannot go below zero when adjusted.
   */
  @Test
  public void testAdjustQuantityCannotGoNegative() {
    testItem.adjustQuantity(-100);
    assertEquals(0, testItem.getQuantity(), "Quantity should not go below zero");
  }

  /**
   * Tests adding items to the current quantity.
   */
  @Test
  public void testAddItemQuantity() {
    int initialQuantity = testItem.getQuantity();
    testItem.addItemQuantity(5);
    assertEquals(initialQuantity + 5, testItem.getQuantity(), "Quantity should increase by 5");
  }

  /**
   * Tests setting the item's quantity to a specific value.
   */
  @Test
  public void testSetItemQuantity() {
    testItem.setItemQuantity(10);
    assertEquals(10, testItem.getQuantity(), "Quantity should be set to 10");
  }

  /**
   * Tests that setting a negative quantity results in zero.
   */
  @Test
  public void testSetItemQuantityNegative() {
    testItem.setItemQuantity(-5);
    assertEquals(0, testItem.getQuantity(), "Quantity should not be negative");
  }

  /**
   * Tests removing items from the current quantity.
   */
  @Test
  public void testRemoveItems() {
    testItem.setItemQuantity(10);
    testItem.removeItems(3);
    assertEquals(7, testItem.getQuantity(), "Quantity should decrease by 3");
  }

  /**
   * Tests that removing more items than available sets the quantity to zero.
   */
  @Test
  public void testRemoveItemsCannotGoNegative() {
    testItem.setItemQuantity(2);
    testItem.removeItems(5);
    assertEquals(0, testItem.getQuantity(), "Quantity should not go below zero");
  }

  /**
   * Tests setting and getting the reservation status of the item.
   */
  @Test
  public void testReservationStatus() {
    testItem.setReservationStatus(true);
    assertTrue(testItem.isReservationStatus(), "Reservation status should be true");
    testItem.setReservationStatus(false);
    assertFalse(testItem.isReservationStatus(), "Reservation status should be false");
  }

  /**
   * Tests setting and getting the reservation duration.
   */
  @Test
  public void testReservationDuration() {
    Duration duration = Duration.ofHours(2);
    testItem.setReservationDuration(duration);
    assertEquals(duration, testItem.getReservationDuration(),
            "Reservation duration should be set to 2 hours");
  }

  /**
   * Tests setting and getting the reservation time.
   */
  @Test
  public void testReservationTime() {
    LocalDateTime now = LocalDateTime.now();
    testItem.setReservationTime(now);
    assertEquals(now, testItem.getReservationTime(),
            "Reservation time should match the set time");
  }

  /**
   * Tests setting and getting the location of the item.
   */
  @Test
  public void testSetLocation() {
    testItem.setLocation("Warehouse A");
    assertEquals("Warehouse A", testItem.getLocation(),
            "Location should be updated to 'Warehouse A'");
  }

  /**
   * Tests setting and getting the price of the item.
   */
  @Test
  public void testSetPrice() {
    testItem.setPrice(1200.50);
    assertEquals(1200.50, testItem.getPrice(), 0.01, "Price should be updated to 1200.50");
  }

  /**
   * Tests setting and getting the next restock date and time.
   */
  @Test
  public void testSetNextRestockDateTime() {
    LocalDateTime futureDate = LocalDateTime.now().plusDays(5);
    testItem.setNextRestockDateTime(futureDate);
    assertEquals(futureDate, testItem.getNextRestockDateTime(),
            "Next restock date should be set correctly");
  }

  /**
   * Tests setting and getting the time of addition of the item.
   */
  @Test
  public void testSetTimeOfAddition() {
    LocalDateTime pastDate = LocalDateTime.now().minusDays(1);
    testItem.setTimeOfAddition(pastDate);
    assertEquals(pastDate, testItem.getTimeOfAddition(), "Time of addition should be updated");
  }

  /**
   * Tests setting and getting the item name.
   */
  @Test
  public void testSetItemName() {
    testItem.setItemName("Honda Forklift");
    assertEquals("Honda Forklift", testItem.getItemName(),
            "Item name should be updated to 'Honda Forklift'");
  }

  /**
   * Tests the toString method of the Item class.
   */
  @Test
  public void testToStringMethod() {
    String expectedString = "Item Details:\n"
            + "UUID: " + testItem.getUuid() + "\n"
            + "Name: Toyota Forklift\n"
            + "Location: Garage\n"
            + "Quantity: 2\n"
            + "Price: $999.99\n"
            + "Reservation Status: Available\n";
    assertEquals(expectedString, testItem.toString(), "toString() method output should match");
  }
}

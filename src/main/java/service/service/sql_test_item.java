package service.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

public class sql_test_item{
  
  // Unique identifier (primary key within SQL DB)
  private final UUID uuid;
  
  // Item details
  private String itemName;
  private LocalDateTime timeOfAddition;
  private int quantity;
  private boolean reservationStatus;
  private Duration reservationDuration;
  private LocalDateTime reservationTime;
  private String location;         // General location or specific address
  private double price;
  private LocalDateTime nextRestockDateTime;
  
  public sql_test_item(String itemName, int quantity, String location, double price) {
    this.uuid = UUID.randomUUID();
    this.itemName = itemName;
    this.timeOfAddition = LocalDateTime.now();
    this.quantity = quantity;
    this.reservationStatus = false;
    this.reservationDuration = Duration.ZERO;
    this.reservationTime = null;
    this.location = location;
    this.price = price;
    this.nextRestockDateTime = null;
  }
  
  // Getters and setters (except for unique identifiers like UUID)
  
  /**
   * Returns the name of the item.
   *
   * @return the name of the item
   */
  public String getItemName() {
    return itemName;
  }
  
  /**
   * Sets the name of the item.
   *
   * @param itemName the new name of the item
   */
  public void setItemName(String itemName) {
    this.itemName = itemName;
  }
  
  /**
   * Returns the time when the item was added.
   *
   * @return the time of addition
   */
  public LocalDateTime getTimeOfAddition() {
    return timeOfAddition;
  }
  
  /**
   * Sets the time when the item was added.
   *
   * @param timeOfAddition the time of addition
   */
  public void setTimeOfAddition(LocalDateTime timeOfAddition) {
    this.timeOfAddition = timeOfAddition;
  }
  
  /**
   * Returns the current quantity of the item.
   *
   * @return the quantity of the item
   */
  public int getQuantity() {
    return quantity;
  }
  
  /**
   * Adjusts the quantity of the item by the specified amount.
   * Positive values increase the quantity, negative values decrease it.
   *
   * @param adjustment the amount to adjust the quantity by
   */
  public void adjustQuantity(int adjustment) {
    this.quantity += adjustment;
    if (this.quantity < 0) {
      this.quantity = 0; // Ensures quantity doesn't go negative
    }
  }
  
  /**
   * Adds the specified number of items to the current quantity.
   *
   * @param numberOfItems the number of items to add
   */
  public void addItemQuantity(int numberOfItems) {
    adjustQuantity(numberOfItems);
  }
  
  /**
   * Sets the quantity of the item.
   *
   * @param quantity the new quantity; if negative, quantity is set to 0
   */
  public void setItemQuantity(int quantity) {
    this.quantity = Math.max(quantity, 0);
  }
  
  /**
   * Removes the specified number of items from the current quantity.
   *
   * @param numberOfItems the number of items to remove
   */
  public void removeItems(int numberOfItems) {
    adjustQuantity(-numberOfItems);
  }
  
  /**
   * Returns the reservation status of the item.
   *
   * @return {@code true} if the item is reserved; {@code false} otherwise
   */
  public boolean isReservationStatus() {
    return reservationStatus;
  }
  
  /**
   * Sets the reservation status of the item.
   *
   * @param reservationStatus {@code true} to reserve the item; {@code false} to unreserve
   */
  public void setReservationStatus(boolean reservationStatus) {
    this.reservationStatus = reservationStatus;
  }
  
  /**
   * Returns the duration of the reservation.
   *
   * @return the reservation duration
   */
  public Duration getReservationDuration() {
    return reservationDuration;
  }
  
  /**
   * Sets the duration for which the item is reserved.
   *
   * @param reservationDuration the duration of the reservation
   */
  public void setReservationDuration(Duration reservationDuration) {
    this.reservationDuration = reservationDuration;
  }
  
  /**
   * Returns the time when the item was reserved.
   *
   * @return the reservation time
   */
  public LocalDateTime getReservationTime() {
    return reservationTime;
  }
  
  /**
   * Sets the time when the item was reserved.
   *
   * @param reservationTime the reservation time
   */
  public void setReservationTime(LocalDateTime reservationTime) {
    this.reservationTime = reservationTime;
  }
  
  /**
   * Returns the general location of the item.
   *
   * @return the location
   */
  public String getLocation() {
    return location;
  }
  
  /**
   * Sets the general location of the item.
   *
   * @param location the new location
   */
  public void setLocation(String location) {
    this.location = location;
  }
  
  /**
   * Returns the price of the item.
   *
   * @return the price
   */
  public double getPrice() {
    return price;
  }
  
  /**
   * Sets the price of the item.
   *
   * @param price the new price
   */
  public void setPrice(double price) {
    this.price = price;
  }
  
  /**
   * Returns the next restock date and time.
   *
   * @return the next restock date and time
   */
  public LocalDateTime getNextRestockDateTime() {
    return nextRestockDateTime;
  }
  
  /**
   * Sets the next restock date and time.
   *
   * @param nextRestockDateTime the next restock date and time
   */
  public void setNextRestockDateTime(LocalDateTime nextRestockDateTime) {
    this.nextRestockDateTime = nextRestockDateTime;
  }
  
  /**
   * Returns the unique identifier (UUID) of the item.
   *
   * @return the UUID of the item
   */
  public UUID getUuid() {
    return uuid;
  }
  
  /**
   * Returns a string representation of the item.
   *
   * @return a string representation of the item
   */
  @Override
  public String toString() {
    return "\nItem: "
           + itemName
           + "\nLocation: "
           + location
           + "\nQuantity: "
           + quantity
           + "\nReservation Status: "
           + reservationStatus;
  }
  
  // Additional methods can be added as needed
  
}

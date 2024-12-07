package service.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import service.exceptions.ReservationException;
import service.handler.ItemReservationService;
import service.handler.ItemsTableSqlHelper;
import service.models.Item;

/** Unit tests for the reservation logic of the service. */
@SpringBootTest
public class ReservationRouteControllerTest {

  @InjectMocks private ReservationRouteController reservationRouteController;

  @Mock private ItemReservationService itemReservationService;

  @Mock private ItemsTableSqlHelper itemsTableSqlHelper;

  private Item testItem;

  /** Sets . */
  @BeforeEach
  public void setup() {
    testItem =
        Item.builder()
            .itemId(UUID.fromString("505234a2-da43-416c-a579-b9235c9be738"))
            .itemName("Socks")
            .timeOfAddition(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .location("Ya Motha's House")
            .inventoryId(UUID.fromString("dd18911c-a3fd-4f08-819e-a917f2baad18"))
            .quantity(3000)
            .price(17.38)
            .nextRestockDateTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .reservationStatus(true)
            .reservationTime(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS))
            .reservationDurationInMillis(1000 * 60 * 60 * 24)
            .build();

    // Mock the items wrapper for the routes that need it
    when(itemReservationService.getItemsTableSqlHelper()).thenReturn(itemsTableSqlHelper);
  }

  /** Testing the creating of a reservation. */
  @Test
  public void testCreateReservation() throws ReservationException {

    // Test successful creation.
    when(itemReservationService.reserveItem(anyString(), anyInt(), anyLong())).thenReturn(testItem);
    ResponseEntity<?> createReservationResponse =
        reservationRouteController.createReservation(
            testItem.getItemId().toString(),
            testItem.getQuantity(),
            testItem.getReservationDurationInMillis());
    assertEquals(HttpStatus.OK, createReservationResponse.getStatusCode());

    // Test null passed into the itemId
    createReservationResponse =
        reservationRouteController.createReservation(
            null, testItem.getQuantity(), testItem.getReservationDurationInMillis());
    assertEquals(HttpStatus.BAD_REQUEST, createReservationResponse.getStatusCode());

    // Test quantity passed in as 0
    createReservationResponse =
        reservationRouteController.createReservation(
            testItem.getItemId().toString(), 0, testItem.getReservationDurationInMillis());
    assertEquals(HttpStatus.BAD_REQUEST, createReservationResponse.getStatusCode());

    // Test quantity passed in as 0
    createReservationResponse =
        reservationRouteController.createReservation(
            testItem.getItemId().toString(), testItem.getQuantity(), 0);
    assertEquals(HttpStatus.BAD_REQUEST, createReservationResponse.getStatusCode());

    // Testing the exception thrown from the route
    doThrow(new RuntimeException())
        .when(itemReservationService)
        .reserveItem(anyString(), anyInt(), anyLong());
    createReservationResponse =
        reservationRouteController.createReservation(
            testItem.getItemId().toString(),
            testItem.getQuantity(),
            testItem.getReservationDurationInMillis());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, createReservationResponse.getStatusCode());
  }

  /** Testing the extension of a reservation. */
  @Test
  public void testExtendReservation() throws ReservationException {

    // Test successful extension.
    when(itemReservationService.extendReservation(anyString(), anyLong())).thenReturn(testItem);
    ResponseEntity<?> extendReservationResponse =
        reservationRouteController.extendReservation(
            testItem.getItemId().toString(), testItem.getReservationDurationInMillis());
    assertEquals(HttpStatus.OK, extendReservationResponse.getStatusCode());

    // Test null passed into the itemId
    extendReservationResponse =
        reservationRouteController.extendReservation(
            null, testItem.getReservationDurationInMillis());
    assertEquals(HttpStatus.BAD_REQUEST, extendReservationResponse.getStatusCode());

    // Test duration passed in as 0
    extendReservationResponse =
        reservationRouteController.extendReservation(testItem.getItemId().toString(), 0);
    assertEquals(HttpStatus.BAD_REQUEST, extendReservationResponse.getStatusCode());

    // Testing the exception thrown from the route
    doThrow(new RuntimeException())
        .when(itemReservationService)
        .extendReservation(anyString(), anyLong());
    extendReservationResponse =
        reservationRouteController.extendReservation(
            testItem.getItemId().toString(), testItem.getReservationDurationInMillis());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, extendReservationResponse.getStatusCode());
  }

  /** Testing the cancellation of a reservation. */
  @Test
  public void testCancelReservation() throws ReservationException {

    // Test successful cancel.
    when(itemReservationService.cancelReservation(anyString(), anyInt())).thenReturn(testItem);
    ResponseEntity<?> cancelReservationResponse =
        reservationRouteController.cancelReservation(
            testItem.getItemId().toString(), testItem.getQuantity());
    assertEquals(HttpStatus.OK, cancelReservationResponse.getStatusCode());

    // Test null passed into the itemId
    cancelReservationResponse =
        reservationRouteController.cancelReservation(null, testItem.getQuantity());
    assertEquals(HttpStatus.BAD_REQUEST, cancelReservationResponse.getStatusCode());

    // Test duration passed in as 0
    cancelReservationResponse =
        reservationRouteController.extendReservation(testItem.getItemId().toString(), 0);
    assertEquals(HttpStatus.BAD_REQUEST, cancelReservationResponse.getStatusCode());

    // Testing the exception thrown from the route
    doThrow(new RuntimeException())
        .when(itemReservationService)
        .extendReservation(anyString(), anyLong());
    cancelReservationResponse =
        reservationRouteController.extendReservation(
            testItem.getItemId().toString(), testItem.getReservationDurationInMillis());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, cancelReservationResponse.getStatusCode());
  }

  /** Testing getting the status of a reservation. */
  @Test
  public void testGetReservationStatus() throws ReservationException {

    List<Item> items = new ArrayList<>();
    items.add(testItem);

    // Test successful get.
    when(itemsTableSqlHelper.getItem(anyString())).thenReturn(items);
    ResponseEntity<?> getReservationStatusResponse =
        reservationRouteController.getReservationStatus(testItem.getItemId().toString());
    assertEquals(HttpStatus.OK, getReservationStatusResponse.getStatusCode());

    // Test null passed into the itemId
    getReservationStatusResponse = reservationRouteController.getReservationStatus(null);
    assertEquals(HttpStatus.BAD_REQUEST, getReservationStatusResponse.getStatusCode());

    // Test item not found
    when(itemsTableSqlHelper.getItem(anyString())).thenReturn(new ArrayList<>());
    getReservationStatusResponse =
        reservationRouteController.getReservationStatus(testItem.getItemId().toString());
    assertEquals(HttpStatus.NOT_FOUND, getReservationStatusResponse.getStatusCode());

    // Testing the exception thrown from the route
    doThrow(new RuntimeException()).when(itemsTableSqlHelper).getItem(anyString());
    getReservationStatusResponse =
        reservationRouteController.getReservationStatus(testItem.getItemId().toString());
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, getReservationStatusResponse.getStatusCode());
  }
}

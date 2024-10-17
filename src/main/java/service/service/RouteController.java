package service.service;

// import java.util.UUID;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PatchMapping;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** This class contains all the API routes for the service. */
@RestController
public class RouteController {

  // @PostMapping(value = "/addItemToInventory", produces =
  // MediaType.APPLICATION_JSON_VALUE)
  // public ResponseEntity<?> addItemToInventory(
  // @RequestParam("adminId") String adminId, @RequestParam("inventoryId") String
  // inventoryId,
  // @RequestParam("itemName") String itemName, @RequestParam("quantity") int
  // quantity,
  // @RequestParam("location") String location, @RequestParam("price") double
  // price) {
  // try{
  // // Check the Inventory Class for the Specific Admin ID to see if
  // // the Item Exists within the Inventory Id
  // new Item()
  // if (0) {
  // return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
  // } else {
  //
  // }
  // } catch (Exception e) {
  // System.out.println(e.getMessage());
  // return new ResponseEntity<>("An Error has occurred",
  // HttpStatus.INTERNAL_SERVER_ERROR);
  // }
  // }
  //
  // @PatchMapping(value = "/setItemQuantity", produces =
  // MediaType.APPLICATION_JSON_VALUE)
  // public ResponseEntity<?> setItemQuantity(
  // @RequestParam("adminId") String adminId, @RequestParam("inventoryId") String
  // inventoryId,
  // @RequestParam("itemId") UUID itemId, @RequestParam("quantity") int quantity)
  // {
  // try {
  //
  // } catch (Exception e) {
  // handleException(e);
  // }
  // }
  //
  // private ResponseEntity<?> handleException(Exception e) {
  // System.out.println(e.getMessage());
  // return new ResponseEntity<>("An error has occurred",
  // HttpStatus.INTERNAL_SERVER_ERROR);
  // }

}

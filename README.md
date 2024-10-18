# COMS 4156 NoConflicts Team Project

# Setup and Installation

This section provides step-by-step instructions to set up the codebase on your local machine, run the application, and locate the outputs. The service was developed using **Java 17** and **Maven 3.9.5**, so you will need to have those installed along with an IDE.

## Prerequisites

Before setting up the project, ensure that your local machine meets the following requirements:

- **Java Development Kit (JDK) 17**
    - **Download:** [OpenJDK 17](https://openjdk.java.net/projects/jdk/17/)
    - **Installation Verification:**
      ```bash
      java -version
      ```
      You should see output similar to:
      ```
      java version "17.0.2" 2022-01-18 LTS
      Java(TM) SE Runtime Environment (build 17.0.2+8-LTS-86)
      Java HotSpot(TM) 64-Bit Server VM (build 17.0.2+8-LTS-86, mixed mode, sharing)
      ```

- **Apache Maven 3.9.5**
    - **Download:** [Maven Download Page](https://maven.apache.org/download.cgi)
    - **Installation Verification:**
      ```bash
      mvn -version
      ```
      You should see output similar to:
      ```
      Apache Maven 3.9.5 (f8a0c37d9c3af58d163c9bd08aebc2b1c2a9b6b1)
      Maven home: /usr/local/apache-maven/apache-maven-3.9.5
      Java version: 17.0.2, vendor: Oracle Corporation, runtime: /usr/local/java/jdk-17.0.2
      Default locale: en_US, platform encoding: UTF-8
      OS name: "mac os x", version: "11.6.8", arch: "x86_64", family: "mac"
      ```

- **Git**
    - **Download:** [Git Download Page](https://git-scm.com/downloads)
    - **Installation Verification:**
      ```bash
      git --version
      ```
      You should see output similar to:
      ```
      git version 2.39
      ```

- **Integrated Development Environment (IDE)**
    - **Recommended:** [IntelliJ IDEA](https://www.jetbrains.com/idea/)
    - **Alternative Options:** Eclipse, Visual Studio Code, etc.

## Cloning the Repository

1. **Open Terminal (MacOS) or Command Prompt (Windows).**

2. **Navigate to the Directory Where You Want to Clone the Project:**
   ```bash
   cd /path/to/your/desired/directory
   ```

3. **Clone the Repository Using Git:**
   ```bash
   git clone https://github.com/your-username/your-repository.git
   ```
   *Replace `https://github.com/your-username/your-repository.git` with the actual repository URL.*

4. **Navigate into the Project Directory:**
   ```bash
   cd your-repository
   ```

## Setting Up the Environment

1. **Install Dependencies:**
   Maven will automatically handle project dependencies specified in the `pom.xml` file. No additional steps are required.
## MySQL Database Setup
1. These steps are borrowed from Prof. Ferguson's [HW0 for W4111 Intro to DB](https://github.com/donald-f-ferguson/W4111-Intro-to-Databases-Base/blob/main/docs/Homework/HW0/HW0.md#mysql-server-community-edition).
2. Install MySQL (Server) Community edition if you don't already have it.
3. Write down and remember the root user ID and password.
4. Choose Legacy Authentication method.
5. Here are some [useful commands](https://dev.mysql.com/doc/mysql-getting-started/en/#mysql-getting-started-installing) to check if the MySQL daemon process is running in the background.
6. Download any free trial of DataGrip or trying logging in with your school email
7. Connect DataGrip to your MySQL server (you'll need the root user ID and password); [here's a guide for this](https://www.youtube.com/watch?v=szK5sWGHgbw).
8. Set username as <b>root</b> and password as <b>password</b>
9. Open the schema.sql file, select all the text and hit the green execute button to create a database called
   reservation management with 4 tables inside of it: Inventories, Inventory_Items, Items and Users.
10. Now, you should be able to open any of the tables and find some data in them to start playing
    around with.

## Running the Application

### For Java on MacOS and Windows

```bash
mvn spring-boot:run
```

**Explanation of the Command:**

- `mvn spring-boot:run`: Tells Maven to run the Spring Boot application.

**Note:** Ensure that you run the command in the project's root directory where the `pom.xml` file is located: `Root/`

## Accessing the Application

Once the application is running, you can access it via:

- **API Endpoints:**
  Open your browser or Postman and navigate to:
  ```
  http://127.0.0.1:8080/endpoint?arg1=value&arg2=value&...
  ```
  *Replace `/endpoint?arg=value` with the actual endpoint and parameters you wish to test.*

- **Application Logs:**
  The console where you ran the Maven command will display logs, including startup information, incoming requests, and any error messages.

## Locating Outputs

- **Console Output:**
  All logs, including informational messages and errors, will be displayed in the terminal or command prompt where you executed the Maven command.

## Common Commands

Here are some additional Maven commands that might be useful:

- **Build the Project:**
  ```bash
  mvn clean install
  ```
  This command cleans any previous builds and installs the project dependencies, compiling the code and packaging it as specified in the `pom.xml`.

- **Run Tests:**
  ```bash
  mvn test
  ```
  Executes the test suites defined in the project.

- **Package the Application:**
  ```bash
  mvn package
  ```
  Compiles the code and packages it into a JAR or WAR file as configured.

## Troubleshooting

- **Maven Not Found:**
  Ensure that Maven is correctly installed and that the `MAVEN_HOME` environment variable is set. Also, add Maven’s `bin` directory to your system's `PATH`.

- **Java Version Issues:**
  Ensure that the JDK version matches the project's requirements. You can set the Java version in your `pom.xml` if needed.

- **Dependency Issues:**
  Run the following command to force Maven to update dependencies:
  ```bash
  mvn clean install -U
  ```

- **Application Errors:**
  Check the console logs for stack traces or error messages. Ensure all required environment variables and configurations are correctly set.

## Additional Tips

- **Using an IDE:**
  For a better development experience, consider importing the project into an Integrated Development Environment (IDE) like [IntelliJ IDEA](https://www.jetbrains.com/idea/), [Eclipse](https://www.eclipse.org/downloads/), or [Visual Studio Code](https://code.visualstudio.com/). These IDEs offer features like code completion, debugging, and easy navigation.
---

## Endpoints

This section provides detailed documentation for the API endpoints. Each endpoint is described with its HTTP method, URL path, expected input parameters, output, and the possible responses upon success or failure.

---

### GET `/api/inventories/getInventoryName`

- **Expected Input Parameters:**
    - `inventoryId` (String): Unique identifier for the inventory.

- **Expected Output:** The name of the inventory as a plain text string.

**Description:**
Returns the name of the specified inventory.

**Upon Success:**
- **Status Code:** `HTTP 200 OK`
- **Response Body:**
  ```
  [Inventory Name]
  ```

**Upon Failure:**
- **Status Codes & Responses:**
    - `HTTP 400 Bad Request`: `"inventoryId needed to get inventories."`
    - `HTTP 404 Not Found`: `"Inventory with inventoryId: [inventoryId] has not been found."`
    - `HTTP 500 Internal Server Error`: `"[Error message]"`

---

### GET `/api/inventories/getInventoryAdmin`

- **Expected Input Parameters:**
    - `inventoryId` (String): Unique identifier for the inventory.

- **Expected Output:** The admin of the inventory as a plain text string.

**Description:**
Returns the admin of the specified inventory.

**Upon Success:**
- **Status Code:** `HTTP 200 OK`
- **Response Body:**
  ```
  Inventory: [Inventory Name] has admin: [Admin Name]
  ```

**Upon Failure:**
- **Status Codes & Responses:**
    - `HTTP 400 Bad Request`: `"inventoryId needed to get inventories."`
    - `HTTP 404 Not Found`: `"Inventory with inventoryId: [inventoryId] has not been found."`
    - `HTTP 500 Internal Server Error`: `"[Error message]"`

---

### GET `/api/inventories/getInventoryItems`

- **Expected Input Parameters:**
    - `inventoryId` (String): Unique identifier for the inventory.

- **Expected Output:** A list of items in the inventory as a plain text string.

**Description:**
Retrieves the items contained in the specified inventory.

**Upon Success:**
- **Status Code:** `HTTP 200 OK`
- **Response Body:**
  ```
  Inventory contains:
  [Quantity] [Item Name](s)
  ...
  ```

**Upon Failure:**
- **Status Codes & Responses:**
    - `HTTP 400 Bad Request`: `"inventoryId needed to get inventories."`
    - `HTTP 404 Not Found`: `"Inventory with inventoryId: [inventoryId] has not been found."`
    - `HTTP 500 Internal Server Error`: `"[Error message]"`

---

### PATCH `/api/inventories/updateInventoryName`

- **Expected Input Parameters:**
    - `inventoryId` (String): Unique identifier for the inventory.
    - `newInventoryName` (String): The new name for the inventory.

- **Expected Output:** Confirmation message as a plain text string.

**Description:**
Updates the name of the specified inventory.

**Upon Success:**
- **Status Code:** `HTTP 200 OK`
- **Response Body:**
  ```
  Successfully changed the inventory's name.
  ```

**Upon Failure:**
- **Status Codes & Responses:**
    - `HTTP 400 Bad Request`: `"inventoryId needed to get inventories."`
    - `HTTP 500 Internal Server Error`: `"Unsuccessful inventory name change."` or `"[Error message]"`

---

### PATCH `/api/inventories/updateInventoryAdmin`

- **Expected Input Parameters:**
    - `inventoryId` (String): Unique identifier for the inventory.
    - `adminId` (String): Unique identifier for the new admin.

- **Expected Output:** Confirmation message as a plain text string.

**Description:**
Updates the admin of the specified inventory.

**Upon Success:**
- **Status Code:** `HTTP 200 OK`
- **Response Body:**
  ```
  Successfully changed the inventory's admin to: [Admin Name]
  ```

**Upon Failure:**
- **Status Codes & Responses:**
    - `HTTP 400 Bad Request`: `"inventoryId needed to get inventories."` or `"adminId is required."`
    - `HTTP 500 Internal Server Error`:
        - If the new admin is the same as the current admin: `"[Admin Name] is already the admin for this inventory."`
        - `"Unsuccessful inventory admin change."`
        - `"[Error message]"`

---

### POST `/api/items/createItem`

- **Expected Input Parameters:**
    - **Request Body (JSON):**
        - `itemName` (String): Name of the item.
        - `location` (String): Location of the item.
        - `inventoryId` (UUID): Identifier of the inventory.
        - `quantity` (Integer): Quantity of the item.
        - `price` (Double): Price of the item.
        - `nextRestockDateTime` (String): Next restock date and time in ISO format.
        - `reservationStatus` (Boolean): Reservation status of the item.
        - `reservationTime` (String): Reservation time in ISO format.
        - `reservationDurationInMillis` (Long): Reservation duration in milliseconds.

- **Expected Output:** Confirmation message as a plain text string.

**Description:**
Creates a new item in the inventory system.

**Upon Success:**
- **Status Code:** `HTTP 201 Created`
- **Response Body:**
  ```
  Successfully created item: [Item ID]
  [Item Name]
  ```

**Upon Failure:**
- **Status Codes & Responses:**
    - `HTTP 400 Bad Request`: `"Empty request"`
    - `HTTP 500 Internal Server Error`: `"Failed to create item"` or `"[Error message]"`

---

### GET `/api/items/getItemName`

- **Expected Input Parameters:**
    - `itemId` (String): Unique identifier for the item.

- **Expected Output:** The name of the item as a plain text string.

**Description:**
Retrieves the name of the specified item.

**Upon Success:**
- **Status Code:** `HTTP 200 OK`
- **Response Body:**
  ```
  [Item Name]
  ```

**Upon Failure:**
- **Status Codes & Responses:**
    - `HTTP 400 Bad Request`: `"itemId needed to get item name."`
    - `HTTP 404 Not Found`: `"Item with itemId: [itemId] was not found."`
    - `HTTP 204 No Content`: `"Item [itemId] has no item name."`
    - `HTTP 500 Internal Server Error`: `"[Error message]"`

---

### GET `/api/items/getItemTimeOfAddition`

- **Expected Input Parameters:**
    - `itemId` (String): Unique identifier for the item.

- **Expected Output:** The time of addition of the item as a string.

**Description:**
Retrieves the time when the specified item was added.

**Upon Success:**
- **Status Code:** `HTTP 200 OK`
- **Response Body:**
  ```
  [Time of Addition]
  ```

**Upon Failure:**
- **Status Codes & Responses:**
    - `HTTP 400 Bad Request`: `"itemId needed to get item time of addition."`
    - `HTTP 404 Not Found`: `"Item with itemId: [itemId] was not found"`
    - `HTTP 500 Internal Server Error`: `"[Error message]"`

---

### GET `/api/items/getItemQuantity`

- **Expected Input Parameters:**
    - `itemId` (String): Unique identifier for the item.

- **Expected Output:** The quantity of the item as an integer.

**Description:**
Retrieves the quantity of the specified item.

**Upon Success:**
- **Status Code:** `HTTP 200 OK`
- **Response Body:**
  ```
  [Item Quantity]
  ```

**Upon Failure:**
- **Status Codes & Responses:**
    - `HTTP 400 Bad Request`: `"itemId needed to get item quantity."`
    - `HTTP 404 Not Found`: `"Item with itemId: [itemId] was not found"`
    - `HTTP 500 Internal Server Error`: `"[Error message]"`

---

### GET `/api/items/isItemReserved`

- **Expected Input Parameters:**
    - `itemId` (String): Unique identifier for the item.

- **Expected Output:** A boolean value indicating if the item is reserved.

**Description:**
Checks if the specified item is reserved.

**Upon Success:**
- **Status Code:** `HTTP 200 OK`
- **Response Body:**
  ```
  true
  ```
  or
  ```
  false
  ```

**Upon Failure:**
- **Status Codes & Responses:**
    - `HTTP 400 Bad Request`: `"itemId needed to check reservation status."`
    - `HTTP 404 Not Found`: `"Item with itemId: [itemId] was not found"`
    - `HTTP 500 Internal Server Error`: `"[Error message]"`

---

### GET `/api/items/getItemReservationDuration`

- **Expected Input Parameters:**
    - `itemId` (String): Unique identifier for the item.

- **Expected Output:** The reservation duration in milliseconds as a number.

**Description:**
Retrieves the reservation duration for the specified item.

**Upon Success:**
- **Status Code:** `HTTP 200 OK`
- **Response Body:**
  ```
  [Reservation Duration in Milliseconds]
  ```

**Upon Failure:**
- **Status Codes & Responses:**
    - `HTTP 400 Bad Request`: `"itemId needed to get reservation duration."`
    - `HTTP 404 Not Found`: `"Item with itemId: [itemId] was not found"`
    - `HTTP 500 Internal Server Error`: `"[Error message]"`

---

### GET `/api/items/getItemReservationTime`

- **Expected Input Parameters:**
    - `itemId` (String): Unique identifier for the item.

- **Expected Output:** The reservation time as a string.

**Description:**
Retrieves the reservation time of the specified item.

**Upon Success:**
- **Status Code:** `HTTP 200 OK`
- **Response Body:**
  ```
  [Reservation Time]
  ```

**Upon Failure:**
- **Status Codes & Responses:**
    - `HTTP 400 Bad Request`: `"itemId needed to get reservation time."`
    - `HTTP 404 Not Found`: `"Item with itemId: [itemId] was not found"`
    - `HTTP 500 Internal Server Error`: `"[Error message]"`

---

### GET `/api/items/getItemLocation`

- **Expected Input Parameters:**
    - `itemId` (String): Unique identifier for the item.

- **Expected Output:** The location of the item as a plain text string.

**Description:**
Retrieves the location of the specified item.

**Upon Success:**
- **Status Code:** `HTTP 200 OK`
- **Response Body:**
  ```
  [Item Location]
  ```

**Upon Failure:**
- **Status Codes & Responses:**
    - `HTTP 400 Bad Request`: `"itemId needed to get item location."`
    - `HTTP 404 Not Found`: `"Item with itemId: [itemId] was not found"`
    - `HTTP 500 Internal Server Error`: `"[Error message]"`

---

### GET `/api/items/getItemPrice`

- **Expected Input Parameters:**
    - `itemId` (String): Unique identifier for the item.

- **Expected Output:** The price of the item as a number.

**Description:**
Retrieves the price of the specified item.

**Upon Success:**
- **Status Code:** `HTTP 200 OK`
- **Response Body:**
  ```
  [Item Price]
  ```

**Upon Failure:**
- **Status Codes & Responses:**
    - `HTTP 400 Bad Request`: `"itemId needed to get item price."`
    - `HTTP 404 Not Found`: `"Item with itemId: [itemId] was not found"`
    - `HTTP 500 Internal Server Error`: `"[Error message]"`

---

### GET `/api/items/getNextRestockTime`

- **Expected Input Parameters:**
    - `itemId` (String): Unique identifier for the item.

- **Expected Output:** The next restock date and time as a string.

**Description:**
Retrieves the next restock date and time for the specified item.

**Upon Success:**
- **Status Code:** `HTTP 200 OK`
- **Response Body:**
  ```
  [Next Restock Time]
  ```

**Upon Failure:**
- **Status Codes & Responses:**
    - `HTTP 400 Bad Request`: `"itemId needed to get next restock time."`
    - `HTTP 404 Not Found`: `"Item with itemId: [itemId] was not found"`
    - `HTTP 204 No Content`: `"No restock time available for item: [Item Name]"`
    - `HTTP 500 Internal Server Error`: `"[Error message]"`

---

### GET `/api/items/getInventoryIdFromItemId`

- **Expected Input Parameters:**
    - `itemId` (String): Unique identifier for the item.

- **Expected Output:** The inventory ID associated with the item as a string.

**Description:**
Retrieves the inventory ID for the specified item.

**Upon Success:**
- **Status Code:** `HTTP 200 OK`
- **Response Body:**
  ```
  [Inventory ID]
  ```

**Upon Failure:**
- **Status Codes & Responses:**
    - `HTTP 400 Bad Request`: `"itemId needed to get inventory ID."`
    - `HTTP 404 Not Found`: `"Item with itemId: [itemId] was not found"`
    - `HTTP 404 Not Found`: `"No inventory id found for item: [Item Name]"`
    - `HTTP 500 Internal Server Error`: `"[Error message]"`

---

### PATCH `/api/items/updateItemName`

- **Expected Input Parameters:**
    - `itemId` (String): Unique identifier for the item.
    - `newItemName` (String): The new name for the item.

- **Expected Output:** Confirmation message as a plain text string.

**Description:**
Updates the name of the specified item.

**Upon Success:**
- **Status Code:** `HTTP 200 OK`
- **Response Body:**
  ```
  Item: [Item ID]
  was successfully renamed. 
  [Old Item Name] --> [New Item Name]
  ```

**Upon Failure:**
- **Status Codes & Responses:**
    - `HTTP 400 Bad Request`:
        - `"itemId is needed to update item name."`
        - `"Item name cannot be empty."`
        - `"Item [itemId] already has the name: [newItemName]"`
    - `HTTP 404 Not Found`: `"Item with itemID: [itemId] was not found."`
    - `HTTP 409 Conflict`: `"There are multiple items with itemID: [itemId]"`
    - `HTTP 500 Internal Server Error`:
        - `"Item with itemID: [itemId] could not be updated."`
        - `"[Error message]"`

---

### DELETE `/api/items/deleteItem`

- **Expected Input Parameters:**
    - `itemId` (String): Unique identifier for the item.

- **Expected Output:** Confirmation message as a plain text string.

**Description:**
Deletes the specified item.

**Upon Success:**
- **Status Code:** `HTTP 200 OK`
- **Response Body:**
  ```
  ItemID: [Item ID]
  "[Item Name]" was successfully deleted.
  ```

**Upon Failure:**
- **Status Codes & Responses:**
    - `HTTP 400 Bad Request`: `"itemId needed to delete item."`
    - `HTTP 404 Not Found`:
        - `"Item with item id: [itemId] does not exist."`
        - `"No item found for itemID: [itemId]"`
    - `HTTP 409 Conflict`: `"There are multiple items with itemID: [itemId]"`
    - `HTTP 500 Internal Server Error`:
        - `"Item with itemID: [itemId] could not be deleted."`
        - `"[Error message]"`

---

### PATCH `/api/items/updateItemQuantity`

- **Expected Input Parameters:**
    - `itemId` (String): Unique identifier for the item.
    - `newQuantity` (Integer): The new quantity for the item.

- **Expected Output:** Confirmation message as a plain text string.

**Description:**
Updates the quantity of the specified item.

**Upon Success:**
- **Status Code:** `HTTP 200 OK`
- **Response Body:**
  ```
  Item: [Item ID]
  Name: [Item Name]
  Quantity was successfully updated. 
  [Old Quantity] --> [New Quantity]
  ```

**Upon Failure:**
- **Status Codes & Responses:**
    - `HTTP 400 Bad Request`:
        - `"itemId needed to update quantity."`
        - `"Quantity cannot be a negative number."`
        - `"Item "[Item Name]" already has a quantity of [Quantity]"`
    - `HTTP 404 Not Found`: `"Item with itemId: [itemId] was not found"`
    - `HTTP 500 Internal Server Error`:
        - `"Could not update quantity for item: [itemId]"`
        - `"[Error message]"`

---

### PATCH `/api/items/updateItemLocation`

- **Expected Input Parameters:**
    - `itemId` (String): Unique identifier for the item.
    - `newLocation` (String): The new location for the item.

- **Expected Output:** Confirmation message as a plain text string.

**Description:**
Updates the location of the specified item.

**Upon Success:**
- **Status Code:** `HTTP 200 OK`
- **Response Body:**
  ```
  Item: [Item ID]
  Name: [Item Name]
  Location was successfully updated. 
  "[Old Location]" --> "[New Location]"
  ```

**Upon Failure:**
- **Status Codes & Responses:**
    - `HTTP 400 Bad Request`:
        - `"itemId needed to update location."`
        - `"Location cannot be empty."`
        - `"Item "[Item Name]" already has a location of: "[Old Location]"`
    - `HTTP 404 Not Found`: `"Item with itemId: [itemId] was not found"`
    - `HTTP 500 Internal Server Error`:
        - `"Could not update location for item: "[Item Name]""`
        - `"[Error message]"`

---

### PATCH `/api/items/updateItemPrice`

- **Expected Input Parameters:**
    - `itemId` (String): Unique identifier for the item.
    - `newPrice` (Double): The new price for the item.

- **Expected Output:** Confirmation message as a plain text string.

**Description:**
Updates the price of the specified item.

**Upon Success:**
- **Status Code:** `HTTP 200 OK`
- **Response Body:**
  ```
  Item: [Item ID]
  Name: [Item Name]
  Price was successfully updated. 
  [Old Price] --> [New Price]
  ```

**Upon Failure:**
- **Status Codes & Responses:**
    - `HTTP 400 Bad Request`:
        - `"itemId needed to update price."`
        - `"Item price cannot be negative."`
        - `"Item "[Item Name]" already has a price of: [Old Price]"`
    - `HTTP 404 Not Found`: `"Item with itemId: [itemId] was not found"`
    - `HTTP 500 Internal Server Error`:
        - `"Could not update price for item: "[Item Name]""`
        - `"[Error message]"`

---

### GET `/api/users/getUsername`

- **Expected Input Parameters:**
    - `userId` (String): Unique identifier for the user.

- **Expected Output:** The username as a plain text string.

**Description:**
Retrieves the username of the specified user.

**Upon Success:**
- **Status Code:** `HTTP 200 OK`
- **Response Body:**
  ```
  [Username]
  ```

**Upon Failure:**
- **Status Codes & Responses:**
    - `HTTP 400 Bad Request`: `"userId needed to get username."`
    - `HTTP 404 Not Found`: `"User with userId: [userId] was not found"`
    - `HTTP 204 No Content`: `"User [userId] has no username."`
    - `HTTP 500 Internal Server Error`: `"[Error message]"`

---

### GET `/api/users/getRole`

- **Expected Input Parameters:**
    - `userId` (String): Unique identifier for the user.

- **Expected Output:** The role of the user as a plain text string.

**Description:**
Retrieves the role of the specified user.

**Upon Success:**
- **Status Code:** `HTTP 200 OK`
- **Response Body:**
  ```
  [User Role]
  ```

**Upon Failure:**
- **Status Codes & Responses:**
    - `HTTP 400 Bad Request`: `"userId needed to get role."`
    - `HTTP 404 Not Found`: `"User with userId: [userId] was not found"`
    - `HTTP 204 No Content`: `"User [Username] has no role assigned."`
    - `HTTP 500 Internal Server Error`: `"[Error message]"`

---

### GET `/api/users/getLastAccess`

- **Expected Input Parameters:**
    - `userId` (String): Unique identifier for the user.

- **Expected Output:** The last access time of the user as a string.

**Description:**
Retrieves the last access time of the specified user.

**Upon Success:**
- **Status Code:** `HTTP 200 OK`
- **Response Body:**
  ```
  [Last Access Time]
  ```

**Upon Failure:**
- **Status Codes & Responses:**
    - `HTTP 400 Bad Request`: `"userId needed to get time of last access."`
    - `HTTP 404 Not Found`: `"User with userId: [userId] was not found"`
    - `HTTP 204 No Content`: `"User [Username] has not accessed the service yet."`
    - `HTTP 500 Internal Server Error`: `"[Error message]"`

---

### POST `/api/users/createUser`

- **Expected Input Parameters:**
    - `username` (String): The desired username for the new user.

- **Expected Output:** Confirmation message as a plain text string.

**Description:**
Creates a new user with the specified username.

**Upon Success:**
- **Status Code:** `HTTP 200 OK`
- **Response Body:**
  ```
  [Username] was successfully created. 
  UserID: [User ID]
  ```

**Upon Failure:**
- **Status Codes & Responses:**
    - `HTTP 400 Bad Request`: `"Username needed to create user."`
    - `HTTP 409 Conflict`: `"Username [username] already taken. Try a different username."`
    - `HTTP 500 Internal Server Error`: `"[Error message]"`

---

### PATCH `/api/users/updateUsername`

- **Expected Input Parameters:**
    - `userId` (String): Unique identifier for the user.
    - `currentUsername` (String): The current username.
    - `newUsername` (String): The new desired username.

- **Expected Output:** Confirmation message as a plain text string.

**Description:**
Updates the username of the specified user.

**Upon Success:**
- **Status Code:** `HTTP 200 OK`
- **Response Body:**
  ```
  Username for userID: [User ID] successfully changed from: 
  [Current Username] --> [New Username]
  ```

**Upon Failure:**
- **Status Codes & Responses:**
    - `HTTP 400 Bad Request`:
        - `"UserID cannot be empty."`
        - `"Current username cannot be empty."`
        - `"New username cannot be empty."`
        - `"New username is the same as current username. Please enter a different username."`
        - `"Current username is wrong for userID: [userId]. Please enter the correct current username."`
    - `HTTP 404 Not Found`: `"User with userId: [userId] was not found"`
    - `HTTP 409 Conflict`: `"Username [newUsername] already taken. Try a different username."`
    - `HTTP 500 Internal Server Error`:
        - `"User with userId: [userId] could not be updated."`
        - `"[Error message]"`

---

### PATCH `/api/users/updateRole`

- **Expected Input Parameters:**
    - `userId` (String): Unique identifier for the user.
    - `newRole` (String): The new role to assign (must be "ADMIN" or "USER").

- **Expected Output:** Confirmation message as a plain text string.

**Description:**
Updates the role of the specified user.

**Upon Success:**
- **Status Code:** `HTTP 200 OK`
- **Response Body:**
  ```
  Role of [Username] was successfully changed from: 
  [Old Role] --> [New Role]
  ```

**Upon Failure:**
- **Status Codes & Responses:**
    - `HTTP 400 Bad Request`:
        - `"UserID cannot be empty."`
        - `"Role is invalid. Must be ADMIN or USER."`
        - `"User [Username] is already [newRole]"`
    - `HTTP 404 Not Found`: `"User with userId: [userId] was not found."`
    - `HTTP 500 Internal Server Error`:
        - `"User with userId: [userId] could not be updated."`
        - `"[Error message]"`

---

## Error Handling

All endpoints may return a `HTTP 500 Internal Server Error` with its specific exception message 
in cases of unexpected failures or exceptions. Ensure that input parameters are correctly formatted and valid to avoid `HTTP 400 Bad Request` responses.

---

## Tools Used 
* Lombok
* Spotless
* PMD

---

-- Create the database if it doesn't exist
DROP DATABASE IF EXISTS `reservation_management`;
CREATE DATABASE IF NOT EXISTS `reservation_management`;
USE `reservation_management`;

-- Create Inventory Table
CREATE TABLE IF NOT EXISTS Inventories (
                                           inventory_id CHAR(36) PRIMARY KEY,  -- UUID for the inventory
                                           inventory_name VARCHAR(255) NOT NULL  -- Name of the inventory
);

-- Create Item Table
CREATE TABLE IF NOT EXISTS Items (
    -- UUID being randomly generated by the UUID() function
    item_id CHAR(36) PRIMARY KEY, -- Unique identifier for each item (UUID)
    item_name VARCHAR(255) NOT NULL,  -- Name of the item
    time_of_addition VARCHAR(255) NOT NULL,  -- Time item was added
    quantity INT NOT NULL,  -- Current quantity of the item
    reserved_status BOOLEAN DEFAULT FALSE,  -- Whether the item is reserved
    reservation_duration LONG NULL,  -- Duration of the reservation
    reservation_time VARCHAR(255) NULL,  -- Time when the item was reserved
    location VARCHAR(255) NULL,  -- Warehouse or location of the item
    price DECIMAL(10,2) NOT NULL,  -- Price of the item
    next_restock VARCHAR(255) NULL,  -- Next restock date and time
    inventory_id CHAR(36) NOT NULL, -- Points to the inventory the item belongs to
    FOREIGN KEY (inventory_id) REFERENCES Inventories(inventory_id) ON DELETE CASCADE
);
-- Create Users Table
CREATE TABLE IF NOT EXISTS Users (
                                     user_id CHAR(36) PRIMARY KEY,  -- Unique user key (UUID)
                                     username VARCHAR(255) NOT NULL UNIQUE,  -- Name of the user
                                     password VARCHAR(255) NOT NULL, -- Password for the user to log in with
                                     role ENUM('ADMIN', 'USER') NOT NULL DEFAULT 'USER',  -- User roles
                                     last_access VARCHAR(255) NOT NULL  -- Time of last access
);

-- Junction Table to Link Users and Inventories (Many-to-Many)
CREATE TABLE IF NOT EXISTS User_Inventories (
                                                user_id CHAR(36),  -- FK to Users
                                                username VARCHAR(255) NOT NULL,  -- Name of the user
                                                inventory_id CHAR(36),  -- FK to Inventories
                                                inventory_name VARCHAR(255) NOT NULL,  -- Name of the inventory
                                                PRIMARY KEY (user_id, inventory_id),
                                                FOREIGN KEY (user_id) REFERENCES Users(user_id) ON DELETE CASCADE,
                                                FOREIGN KEY (inventory_id) REFERENCES Inventories(inventory_id) ON DELETE CASCADE
);

-- Create Inventory Items Junction Table (for Inventory to Item relationship)
CREATE TABLE IF NOT EXISTS Inventory_Items (
    inventory_id CHAR(36),  -- FK to Inventory
    inventory_name VARCHAR(255) NOT NULL,  -- Name of the inventory
    item_id CHAR(36),  -- FK to Item
    item_name VARCHAR(255) NOT NULL,  -- Name of the item
    PRIMARY KEY (inventory_id, item_id),
    FOREIGN KEY (inventory_id) REFERENCES Inventories(inventory_id) ON DELETE CASCADE,
    FOREIGN KEY (item_id) REFERENCES Items(item_id) ON DELETE CASCADE
);

-- Trigger automatically creates a default inventory for any user created
CREATE TRIGGER create_default_inventory
    AFTER INSERT ON Users
    FOR EACH ROW
BEGIN
    DECLARE new_inventory_id CHAR(36);
    DECLARE new_inventory_name VARCHAR(255);
    SET new_inventory_id = UUID();
    SET new_inventory_name = CONCAT(NEW.username, "'s Default Inventory");
    -- Create a default inventory for the user
    INSERT INTO Inventories (inventory_id, inventory_name)
    VALUES (new_inventory_id, new_inventory_name);

    -- Link the user to their new default inventory in User_Inventories junction table
    INSERT INTO User_Inventories (user_id, username, inventory_id, inventory_name)
    VALUES (NEW.user_id, NEW.username, new_inventory_id, new_inventory_name);
END;

CREATE TRIGGER add_new_item_to_junction_table
    AFTER INSERT ON Items
    FOR EACH ROW
BEGIN
    -- Link the new item to the inventory
    INSERT INTO Inventory_Items (inventory_id, inventory_name , item_id, item_name)
    VALUES (
        NEW.inventory_id,
        (SELECT inventory_name FROM Inventories WHERE inventory_id = NEW.inventory_id),
        NEW.item_id,
        NEW.item_name
    );
END;

CREATE TRIGGER update_inventory_items_junction_when_inventory_id_changes
    AFTER UPDATE ON Items
    FOR EACH ROW
BEGIN
    -- Check if the inventory_id has changed
    IF OLD.inventory_id != NEW.inventory_id THEN
        -- Remove the old entry in Inventory_Items
        DELETE FROM Inventory_Items
        WHERE inventory_id = OLD.inventory_id AND item_id = OLD.item_id;

        -- Add the new pair with the updated inventory_id and inventory_name
        INSERT INTO Inventory_Items (inventory_id, inventory_name, item_id, item_name)
        VALUES (
                   NEW.inventory_id,
                   (SELECT inventory_name FROM Inventories WHERE inventory_id = NEW.inventory_id),
                   NEW.item_id,
                   NEW.item_name
               );
    END IF;
END;

CREATE TRIGGER delete_default_inventory
    AFTER DELETE ON Users
    FOR EACH ROW
BEGIN
    DECLARE inventory_name_to_delete VARCHAR(255);

    -- Construct the name of the inventory to delete
    SET inventory_name_to_delete = CONCAT(OLD.username, "'s Default Inventory");

    -- Delete the inventory with the constructed name
    DELETE FROM Inventories
    WHERE inventory_name = inventory_name_to_delete;
END;


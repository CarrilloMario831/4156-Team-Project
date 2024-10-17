-- Create the database if it doesn't exist
DROP DATABASE IF EXISTS `reservation_management`;
CREATE DATABASE IF NOT EXISTS `reservation_management`;
USE `reservation_management`;

-- Create Item Table
CREATE TABLE IF NOT EXISTS Items (

    uuid CHAR(36) PRIMARY KEY NOT NULL DEFAULT (UUID()), -- Unique identifier for each item (UUID)
        -- UUID being randomly generated by the UUID() function

    item_name VARCHAR(255) NOT NULL,  -- Name of the item
    time_of_addition VARCHAR(255) NOT NULL ,  -- Time item was added
    quantity INT NOT NULL,  -- Current quantity of the item
    reserved_status BOOLEAN DEFAULT FALSE,  -- Whether the item is reserved
    reservation_duration LONG NOT NULL,  -- Duration of the reservation
    reservation_time VARCHAR(255) NULL,  -- Time when the item was reserved
    location VARCHAR(255) NULL,  -- Warehouse or location of the item
    price DECIMAL(10,2) NOT NULL,  -- Price of the item
    next_restock VARCHAR(255) NULL,  -- Next restock date and time
    inventory_id CHAR(36) DEFAULT NULL -- Points to the inventory the item belongs to
);

-- Create Inventory Table
CREATE TABLE IF NOT EXISTS Inventories (
    inventory_id CHAR(36) PRIMARY KEY,  -- UUID for the inventory
    inventory_name VARCHAR(255) NOT NULL,  -- Name of the inventory
    user_key CHAR(36) NOT NULL  -- User's unique key (foreign key to users)
);

-- Create Inventory Items Junction Table (for Inventory to Item relationship)
CREATE TABLE IF NOT EXISTS Inventory_Items (
    inventory_id CHAR(36),  -- FK to Inventory
    item_uuid CHAR(36),  -- FK to Item
    PRIMARY KEY (inventory_id, item_uuid),
    FOREIGN KEY (inventory_id) REFERENCES Inventories(inventory_id) ON DELETE CASCADE
);

-- Create Users Table
CREATE TABLE IF NOT EXISTS Users (
    user_key CHAR(36) PRIMARY KEY,  -- Unique user key (UUID)
    username VARCHAR(255) NOT NULL UNIQUE,  -- Name of the user
    role ENUM('Admin', 'Secretary', 'User') NOT NULL DEFAULT 'User',  -- User roles
    last_access TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- Time of last access
    item_action VARCHAR(255),  -- Item created/modified/deleted by the user
    inventory_access CHAR(36),  -- FK to inventory user has access to
    FOREIGN KEY (inventory_access) REFERENCES Inventories(inventory_id) ON DELETE SET NULL
);

-- Junction Table to Link Users and Inventories (Many-to-Many)
CREATE TABLE IF NOT EXISTS User_Inventories (
    user_key CHAR(36),  -- FK to Users
    inventory_id CHAR(36),  -- FK to Inventories
    PRIMARY KEY (user_key, inventory_id),
    FOREIGN KEY (user_key) REFERENCES Users(user_key) ON DELETE CASCADE,
    FOREIGN KEY (inventory_id) REFERENCES Inventories(inventory_id) ON DELETE CASCADE
);

-- Here we add the foreign key to connect the user to the inventories they have access to
ALTER TABLE Inventories
ADD CONSTRAINT fk_user_inventory
    FOREIGN KEY (user_key)
    REFERENCES Users (user_key)
    ON DELETE CASCADE;

-- Here we add the foreign key to connect the items to the Inventories they belong to
ALTER TABLE Items
    ADD CONSTRAINT fk_item_inventory
    FOREIGN KEY (inventory_id)
    REFERENCES Inventories (inventory_id)
    ON DELETE CASCADE;

-- Trigger automatically creates a default inventory for any user created
CREATE TRIGGER create_default_inventory
    AFTER INSERT ON Users
    FOR EACH ROW
BEGIN
    DECLARE new_inventory_id CHAR(36);
    SET new_inventory_id = UUID();
    -- Create a default inventory for the user
    INSERT INTO Inventories (inventory_id, inventory_name, user_key)
    VALUES (new_inventory_id, CONCAT(NEW.username, "'s Default Inventory"), NEW.user_key);
END;

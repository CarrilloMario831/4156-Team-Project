USE reservation_management;

-- Sample insert for Users
INSERT INTO Users (user_id, username, role, last_access)
VALUES
    ('f1234567-abcd-4d5e-9999-abcdef012345', 'admin1', 'ADMIN', '2024-10-17 12:03:33'),
    ('f89f6789-abcd-4d5e-8888-abcdef876543', 'secretary1', 'USER', '2024-10-17 12:03:33');

-- Sample inserts for Inventories
INSERT INTO Inventories (inventory_id, inventory_name)
VALUES
    ('bf456378-a8b3-40b6-b1a1-654bc9de5f02', 'Electronics Inventory'),
    ('bd123478-12ab-45f6-abc8-4456ac987654', 'Office Supplies Inventory');

-- Sample inserts for Items
INSERT INTO Items (item_id, item_name, time_of_addition, reservation_time, quantity, reserved_status, price, location, inventory_id)
VALUES
    ('c56a4180-65aa-42ec-a945-5fd21dec0538', 'Laptop', '2024-10-17 12:03:33', '2024-10-17 12:03:33', 50, FALSE, 1200.00, 'CheeseCake Factory','bf456378-a8b3-40b6-b1a1-654bc9de5f02'),
    ('a123b789-123b-48ec-923f-d9b8f06ba7aa', 'Mouse', '2024-10-17 12:03:33', '2024-10-17 12:03:33', 200, TRUE, 25.99, 'Crumbl Cookie','bf456378-a8b3-40b6-b1a1-654bc9de5f02');
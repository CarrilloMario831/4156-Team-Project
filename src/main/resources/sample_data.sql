USE reservation_management;

-- Sample insert for Users
INSERT INTO Users (user_key, username, role)
VALUES
    ('u1234567-abcd-4d5e-9999-abcdef012345', 'admin1', 'Admin'),
    ('u89f6789-abcd-4d5e-8888-abcdef876543', 'secretary1', 'Secretary');


-- Sample inserts for Items
INSERT INTO Items (uuid, item_name, quantity, reserved_status, price)
VALUES
    ('c56a4180-65aa-42ec-a945-5fd21dec0538', 'Laptop', 50, FALSE, 1200.00),
    ('a123b789-123b-48ec-923f-d9b8f06ba7aa', 'Mouse', 200, TRUE, 25.99);

-- Sample inserts for Inventories
INSERT INTO Inventories (inventory_id, inventory_name, user_key)
VALUES
    ('bf456378-a8b3-40b6-b1a1-654bc9de5f02', 'Electronics Inventory', 'u1234567-abcd-4d5e-9999-abcdef012345'),
    ('bd123478-12ab-45f6-abc8-4456ac987654', 'Office Supplies Inventory', 'u89f6789-abcd-4d5e-8888-abcdef876543');

-- Sample insert into Inventory_Items (linking inventories to items)
INSERT INTO Inventory_Items (inventory_id, item_uuid)
VALUES
    ('bf456378-a8b3-40b6-b1a1-654bc9de5f02', 'c56a4180-65aa-42ec-a945-5fd21dec0538'),
    ('bd123478-12ab-45f6-abc8-4456ac987654', 'a123b789-123b-48ec-923f-d9b8f06ba7aa');

-- Create Database
DROP DATABASE IF EXISTS lostfounddb;
CREATE DATABASE lostfounddb CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE lostfounddb;

-- Create Table: user
CREATE TABLE user (
    User_ID INT NOT NULL AUTO_INCREMENT,
    Name VARCHAR(255) NOT NULL,
    Email VARCHAR(255) NOT NULL,
    Password VARCHAR(255) NOT NULL,
    Role ENUM('User', 'Admin') NOT NULL DEFAULT 'User',
    Contact VARCHAR(50),
    PRIMARY KEY (User_ID),
    UNIQUE KEY uk_email (Email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create Table: admin
CREATE TABLE admin (
    Admin_ID INT NOT NULL AUTO_INCREMENT,
    User_ID INT NOT NULL,
    Admin_Role ENUM('SuperAdmin', 'Moderator') NOT NULL DEFAULT 'Moderator',
    PRIMARY KEY (Admin_ID),
    UNIQUE KEY uk_user_id (User_ID),
    FOREIGN KEY (User_ID) REFERENCES user(User_ID) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create Table: item
CREATE TABLE item (
    Item_ID INT NOT NULL AUTO_INCREMENT,
    Name VARCHAR(255) NOT NULL,
    Description TEXT,
    Category VARCHAR(100),
    User_ID INT NOT NULL,
    Status ENUM('Lost', 'Found') NOT NULL,
    Date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (Item_ID),
    FOREIGN KEY (User_ID) REFERENCES user(User_ID) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create Table: reports
CREATE TABLE reports (
    Report_ID INT NOT NULL AUTO_INCREMENT,
    User_ID INT NOT NULL,
    Item_ID INT NOT NULL,
    Report_Type ENUM('Lost', 'Found') NOT NULL,
    Report_Date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (Report_ID),
    FOREIGN KEY (User_ID) REFERENCES user(User_ID) ON DELETE CASCADE,
    FOREIGN KEY (Item_ID) REFERENCES item(Item_ID) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create Table: lost_item
CREATE TABLE lost_item (
    Lost_Item_ID INT NOT NULL AUTO_INCREMENT,
    Item_ID INT NOT NULL,
    Last_Seen_Location VARCHAR(255),
    Last_Seen_Date DATE,
    Additional_Details TEXT,
    image_path VARCHAR(255),
    PRIMARY KEY (Lost_Item_ID),
    UNIQUE KEY uk_item_id (Item_ID),
    FOREIGN KEY (Item_ID) REFERENCES item(Item_ID) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Create Table: found_item
CREATE TABLE found_item (
    Found_Item_ID INT NOT NULL AUTO_INCREMENT,
    Item_ID INT NOT NULL,
    Found_Location VARCHAR(255),
    Found_Date DATE,
    Storage_Location VARCHAR(255),
    Additional_Details TEXT,
    image_path VARCHAR(255),
    PRIMARY KEY (Found_Item_ID),
    UNIQUE KEY uk_item_id (Item_ID),
    FOREIGN KEY (Item_ID) REFERENCES item(Item_ID) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Insert Users (5 Admins, 5 Non-Admins)
INSERT INTO user (User_ID, Name, Email, Password, Role, Contact)
VALUES
    (1, 'Alice Smith', 'alice@example.com', 'password123', 'Admin', '123-456-7890'),
    (2, 'Bob Johnson', 'bob@example.com', 'password123', 'Admin', '234-567-8901'),
    (3, 'Carol Williams', 'carol@example.com', 'password123', 'Admin', '345-678-9012'),
    (4, 'Dave Brown', 'dave@example.com', 'password123', 'Admin', '456-789-0123'),
    (5, 'Eve Davis', 'eve@example.com', 'password123', 'Admin', '567-890-1234'),
    (6, 'Frank Wilson', 'frank@example.com', 'password123', 'User', '678-901-2345'),
    (7, 'Grace Taylor', 'grace@example.com', 'password123', 'User', '789-012-3456'),
    (8, 'Hank Anderson', 'hank@example.com', 'password123', 'User', '890-123-4567'),
    (9, 'Ivy Martinez', 'ivy@example.com', 'password123', 'User', '901-234-5678'),
    (10, 'Jack Lee', 'jack@example.com', 'password123', 'User', '012-345-6789');

-- Insert Admins
INSERT INTO admin (Admin_ID, User_ID, Admin_Role)
VALUES
    (NULL, 1, 'SuperAdmin'),
    (NULL, 2, 'Moderator'),
    (NULL, 3, 'SuperAdmin'),
    (NULL, 4, 'Moderator'),
    (NULL, 5, 'SuperAdmin');

-- Insert Items (20 Lost, 20 Found)
INSERT INTO item (Item_ID, Name, Description, Category, User_ID, Status, Date)
VALUES
    -- Lost Items (Item_ID 1–10)
    (1, 'Lost Wallet', 'Black leather wallet with ID', 'lost', 1, 'Lost', '2025-04-18 08:32:00'),
    (2, 'Lost Phone', 'iPhone 12, blue case', 'lost', 2, 'Lost', '2025-04-18 08:32:00'),
    (3, 'Lost Keys', 'Car keys with red keychain', 'lost', 3, 'Lost', '2025-04-18 08:32:00'),
    (4, 'Lost Laptop', 'MacBook Air, silver', 'lost', 4, 'Lost', '2025-04-18 08:32:00'),
    (5, 'Lost Watch', 'Silver wristwatch', 'lost', 5, 'Lost', '2025-04-18 08:32:00'),
    (6, 'Lost Backpack', 'Black JanSport backpack', 'lost', 6, 'Lost', '2025-04-18 08:32:00'),
    (7, 'Lost Sunglasses', 'Ray-Ban sunglasses', 'lost', 7, 'Lost', '2025-04-18 08:32:00'),
    (8, 'Lost Umbrella', 'Blue folding umbrella', 'lost', 8, 'Lost', '2025-04-18 08:32:00'),
    (9, 'Lost Book', 'Hardcover novel', 'lost', 9, 'Lost', '2025-04-18 08:32:00'),
    (10, 'Lost Headphones', 'Wireless earbuds', 'lost', 10, 'Lost', '2025-04-18 08:32:00'),
    -- Found Items (Item_ID 11–20)
    (11, 'Found Wallet', 'Brown leather wallet', 'found', 6, 'Found', '2025-04-18 08:32:00'),
    (12, 'Found Phone', 'Samsung Galaxy, black case', 'found', 7, 'Found', '2025-04-18 08:32:00'),
    (13, 'Found Keys', 'House keys with blue tag', 'found', 8, 'Found', '2025-04-18 08:32:00'),
    (14, 'Found Laptop', 'Dell XPS, black', 'found', 9, 'Found', '2025-04-18 08:32:00'),
    (15, 'Found Watch', 'Gold wristwatch', 'found', 10, 'Found', '2025-04-18 08:32:00'),
    (16, 'Found Backpack', 'Grey Nike backpack', 'found', 1, 'Found', '2025-04-18 08:32:00'),
    (17, 'Found Sunglasses', 'Oakley sunglasses', 'found', 2, 'Found', '2025-04-18 08:32:00'),
    (18, 'Found Umbrella', 'Red umbrella', 'found', 3, 'Found', '2025-04-18 08:32:00'),
    (19, 'Found Book', 'Paperback mystery novel', 'found', 4, 'Found', '2025-04-18 08:32:00'),
    (20, 'Found Headphones', 'Bose over-ear headphones', 'found', 5, 'Found', '2025-04-18 08:32:00'),
    -- Lost Items (Item_ID 21–30)
    (21, 'Lost Wallet', 'Black leather wallet with ID', 'lost', 1, 'Lost', '2025-04-17 10:00:00'),
    (22, 'Lost Phone', 'iPhone 12, blue case', 'lost', 2, 'Lost', '2025-04-16 12:00:00'),
    (23, 'Lost Keys', 'Car keys with red keychain', 'lost', 3, 'Lost', '2025-04-15 14:00:00'),
    (24, 'Lost Laptop', 'MacBook Air, silver', 'lost', 4, 'Lost', '2025-04-14 16:00:00'),
    (25, 'Lost Watch', 'Silver wristwatch', 'lost', 5, 'Lost', '2025-04-13 18:00:00'),
    (26, 'Lost Backpack', 'Black JanSport backpack', 'lost', 6, 'Lost', '2025-04-12 20:00:00'),
    (27, 'Lost Sunglasses', 'Ray-Ban sunglasses', 'lost', 7, 'Lost', '2025-04-11 22:00:00'),
    (28, 'Lost Umbrella', 'Blue folding umbrella', 'lost', 8, 'Lost', '2025-04-10 09:00:00'),
    (29, 'Lost Book', 'Hardcover novel', 'lost', 9, 'Lost', '2025-04-09 11:00:00'),
    (30, 'Lost Headphones', 'Wireless earbuds', 'lost', 10, 'Lost', '2025-04-08 13:00:00'),
    -- Found Items (Item_ID 31–40)
    (31, 'Found Wallet', 'Brown leather wallet', 'found', 6, 'Found', '2025-04-17 10:30:00'),
    (32, 'Found Phone', 'Samsung Galaxy, black case', 'found', 7, 'Found', '2025-04-16 12:30:00'),
    (33, 'Found Keys', 'House keys with blue tag', 'found', 8, 'Found', '2025-04-15 14:30:00'),
    (34, 'Found Laptop', 'Dell XPS, black', 'found', 9, 'Found', '2025-04-14 16:30:00'),
    (35, 'Found Watch', 'Gold wristwatch', 'found', 10, 'Found', '2025-04-13 18:30:00'),
    (36, 'Found Backpack', 'Grey Nike backpack', 'found', 1, 'Found', '2025-04-12 20:30:00'),
    (37, 'Found Sunglasses', 'Oakley sunglasses', 'found', 2, 'Found', '2025-04-11 22:30:00'),
    (38, 'Found Umbrella', 'Red umbrella', 'found', 3, 'Found', '2025-04-10 09:30:00'),
    (39, 'Found Book', 'Paperback mystery novel', 'found', 4, 'Found', '2025-04-09 11:30:00'),
    (40, 'Found Headphones', 'Bose over-ear headphones', 'found', 5, 'Found', '2025-04-08 13:30:00');

-- Insert 20 Reports (10 Lost for Item_ID 21–30, 10 Found for Item_ID 31–40)
INSERT INTO reports (User_ID, Item_ID, Report_Type, Report_Date)
VALUES
    -- 10 Lost Reports (Item_ID 21–30)
    (1, 21, 'Lost', '2025-04-17 10:05:00'),
    (2, 22, 'Lost', '2025-04-16 12:05:00'),
    (3, 23, 'Lost', '2025-04-15 14:05:00'),
    (4, 24, 'Lost', '2025-04-14 16:05:00'),
    (5, 25, 'Lost', '2025-04-13 18:05:00'),
    (6, 26, 'Lost', '2025-04-12 20:05:00'),
    (7, 27, 'Lost', '2025-04-11 22:05:00'),
    (8, 28, 'Lost', '2025-04-10 09:05:00'),
    (9, 29, 'Lost', '2025-04-09 11:05:00'),
    (10, 30, 'Lost', '2025-04-08 13:05:00'),
    -- 10 Found Reports (Item_ID 31–40)
    (6, 31, 'Found', '2025-04-17 10:35:00'),
    (7, 32, 'Found', '2025-04-16 12:35:00'),
    (8, 33, 'Found', '2025-04-15 14:35:00'),
    (9, 34, 'Found', '2025-04-14 16:35:00'),
    (10, 35, 'Found', '2025-04-13 18:35:00'),
    (1, 36, 'Found', '2025-04-12 20:35:00'),
    (2, 37, 'Found', '2025-04-11 22:35:00'),
    (3, 38, 'Found', '2025-04-10 09:35:00'),
    (4, 39, 'Found', '2025-04-09 11:35:00'),
    (5, 40, 'Found', '2025-04-08 13:35:00');

-- Insert 10 Lost Item Records (Item_ID 21–30)
INSERT INTO lost_item (Item_ID, Last_Seen_Location, Last_Seen_Date, Additional_Details, image_path)
VALUES
    (21, 'Central Park', '2025-04-15', 'Contains $50', 'images/lost/wallet.jpg'),
    (22, 'Subway Station', '2025-04-14', 'Screen cracked', 'images/lost/phone.jpg'),
    (23, 'Coffee Shop', '2025-04-13', 'Toyota key', 'images/lost/keys.jpg'),
    (24, 'Library', '2025-04-12', 'Charger included', 'images/lost/laptop.jpg'),
    (25, 'Gym', '2025-04-11', 'Engraved', 'images/lost/watch.jpg'),
    (26, 'Bus Stop', '2025-04-10', 'Contains books', 'images/lost/backpack.jpg'),
    (27, 'Beach', '2025-04-09', 'Prescription lenses', 'images/lost/sunglasses.jpg'),
    (28, 'Office', '2025-04-08', 'Automatic open', 'images/lost/umbrella.jpg'),
    (29, 'Park', '2025-04-07', 'Signed copy', 'images/lost/book.jpg'),
    (30, 'Mall', '2025-04-06', 'White case', 'images/lost/headphones.jpg');

-- Insert 10 Found Item Records (Item_ID 31–40)
INSERT INTO found_item (Item_ID, Found_Location, Found_Date, Storage_Location, Additional_Details, image_path)
VALUES
    (31, 'Train Station', '2025-04-15', 'Lost and Found Office', 'Contains cards', 'images/found/wallet.jpg'),
    (32, 'Park Bench', '2025-04-14', 'Security Desk', 'Locked screen', 'images/found/phone.jpg'),
    (33, 'Restaurant', '2025-04-13', 'Front Desk', 'Multiple keys', 'images/found/keys.jpg'),
    (34, 'Conference Room', '2025-04-12', 'IT Department', 'No charger', 'images/found/laptop.jpg'),
    (35, 'Parking Lot', '2025-04-11', 'Lost and Found Office', 'Scratched', 'images/found/watch.jpg'),
    (36, 'Library', '2025-04-10', 'Main Desk', 'Contains laptop', 'images/found/backpack.jpg'),
    (37, 'Beach', '2025-04-09', 'Lifeguard Station', 'No case', 'images/found/sunglasses.jpg'),
    (38, 'Bus Stop', '2025-04-08', 'Transit Office', 'Manual open', 'images/found/umbrella.jpg'),
    (39, 'Cafe', '2025-04-07', 'Bookstore', 'Dog-eared', 'images/found/book.jpg'),
    (40, 'Gym', '2025-04-06', 'Front Desk', 'No case', 'images/found/headphones.jpg');

-- Dump Users with All Details
SELECT 
    u.User_ID, 
    u.Name, 
    u.Email, 
    u.Password, 
    u.Role, 
    u.Contact,
    a.Admin_ID, 
    a.Admin_Role
FROM 
    user u
LEFT JOIN 
    admin a ON u.User_ID = a.User_ID
ORDER BY 
    u.Role DESC, u.Name;

-- Dump Items
SELECT 
    Item_ID, 
    Name, 
    Description, 
    Category, 
    User_ID, 
    Status, 
    Date
FROM 
    item
ORDER BY 
    Item_ID;

-- Dump Lost Items
SELECT 
    Lost_Item_ID, 
    Item_ID, 
    Last_Seen_Location, 
    Last_Seen_Date, 
    Additional_Details, 
    image_path
FROM 
    lost_item
ORDER BY 
    Item_ID;

-- Dump Found Items
SELECT 
    Found_Item_ID, 
    Item_ID, 
    Found_Location, 
    Found_Date, 
    Storage_Location, 
    Additional_Details, 
    image_path
FROM 
    found_item
ORDER BY 
    Item_ID;

-- Dump Reports with All Details
SELECT 
    r.Report_ID, 
    r.User_ID, 
    u.Name AS User_Name, 
    r.Item_ID, 
    i.Name AS Item_Name, 
    i.Description, 
    i.Category, 
    i.Status, 
    r.Report_Type, 
    r.Report_Date,
    li.Last_Seen_Location, 
    li.Last_Seen_Date, 
    li.Additional_Details AS Lost_Details, 
    li.image_path AS Lost_Image,
    fi.Found_Location, 
    fi.Found_Date, 
    fi.Storage_Location, 
    fi.Additional_Details AS Found_Details, 
    fi.image_path AS Found_Image
FROM 
    reports r
JOIN 
    user u ON r.User_ID = u.User_ID
JOIN 
    item i ON r.Item_ID = i.Item_ID
LEFT JOIN 
    lost_item li ON r.Item_ID = li.Item_ID AND r.Report_Type = 'Lost'
LEFT JOIN 
    found_item fi ON r.Item_ID = fi.Item_ID AND r.Report_Type = 'Found'
ORDER BY 
    r.Report_Type, r.Report_ID;
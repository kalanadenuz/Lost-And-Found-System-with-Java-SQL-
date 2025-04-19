## 📦 Lost and Found Management System
Welcome to the Lost and Found Management System, a sophisticated Java-based desktop application designed to streamline the management of lost and found items in organizations, campuses, or public spaces. Crafted with Java Swing for an intuitive, dark-themed user interface and powered by MySQL for robust data persistence, this system empowers users to register, report, track, and manage items with ease. Whether you're a user searching for a lost item or an admin overseeing operations, this application offers a seamless, secure, and visually appealing experience.

Built with Object-Oriented Programming (OOP) principles at its core, the system ensures modularity, scalability, and maintainability. From user authentication to item tracking and analytics, every feature is designed to be efficient and user-friendly, making lost and found management effortless.

### 🌟 Features
- 🔐 User Registration and Secure Login: Create accounts and log in securely with role-based access (user/admin).
- 🧾 Add, Edit, and View Lost/Found Items: Report lost or found items with detailed descriptions and images, and view reports effortlessly.
- 🧑‍💼 Admin Panel for Comprehensive Management: Admins can manage users, promote/demote admins, and oversee all reports.
- 📊 Dashboard with Reports and Analytics: Visualize lost and found trends with interactive reports and summaries.
- 🖼️ Image Support for Items: Upload and display images for lost and found items to aid identification.
- 📁 Organized Codebase Using OOP Principles: Modular design leveraging encapsulation, inheritance, polymorphism, and abstraction.
- 💾 Persistent Data Storage with MySQL: Reliable storage and retrieval of user, item, and report data.
- 🎨 Modern, Dark-Themed UI: Clean, responsive interface with interactive tables, styled buttons, and rounded edges for a premium user experience.

### 🛠 Technologies Used
- Java: Swing for UI, JDBC for database connectivity.
- MySQL: Robust relational database for data persistence.
- NetBeans IDE: Maven-based project for streamlined development.
-OOP Design Patterns: MVC (Model-View-Controller), DAO (Data Access Object), and Service Layer patterns for clean architecture.

### 🏛️ OOP Principles Applied
The system is a showcase of Object-Oriented Programming (OOP) principles, ensuring a maintainable and scalable codebase:

- Encapsulation: Data and methods are bundled into classes (e.g., User, Item), with private fields and public getters/setters to protect data integrity.
- Inheritance: Shared behavior is abstracted into base classes or interfaces (e.g., Item as a base for LostItem and FoundItem), promoting code reuse.
- Polymorphism: Dynamic method dispatch allows flexible handling of objects (e.g., ReportService processes both LostItem and FoundItem polymorphically).
- Abstraction: Interfaces and abstract classes (e.g., in dao and service packages) define contracts, hiding implementation details.
- Modularity: The MVC pattern separates concerns, with model for data, ui for views, and service/dao for logic, enhancing maintainability.

### 📁 Project Structure
```
Lost and Found System/
├───images/
│   ├───found/        # Images for found items
│   └───lost/         # Images for lost items
│
├───src/
│   └───main/
│       ├───java/
│       │   └───com/
│       │       └───lostfound/
│       │           ├───app/       # Application entry point
│       │           │   └── MainApp.java
│       │           │
│       │           ├───config/    # Configuration classes
│       │           │   ├── DBConfig.java
│       │           │   └── AppConfig.java
│       │           │
│       │           ├───dao/       # Data Access Objects
│       │           │   ├── UserDAO.java
│       │           │   ├── ItemDAO.java
│       │           │   ├── LostItemDAO.java
│       │           │   ├── FoundItemDAO.java
│       │           │   ├── ReportDAO.java
│       │           │   └── AdminDAO.java
│       │           │
│       │           ├───model/     # Data models
│       │           │   ├── User.java
│       │           │   ├── Item.java
│       │           │   ├── LostItem.java
│       │           │   ├── FoundItem.java
│       │           │   ├── Report.java
│       │           │   ├── Admin.java
│       │           │   └── ReportDetails.java
│       │           │
│       │           ├───service/   # Business logic
│       │           │   ├── UserService.java
│       │           │   ├── ItemService.java
│       │           │   ├── ReportService.java
│       │           │   ├── LostItemService.java
│       │           │   ├── FoundItemService.java
│       │           │   └── AdminService.java
│       │           │
│       │           └───ui/        # Swing UI components
│       │               ├── auth/
│       │               │   ├── LoginUI.java
│       │               │   └── RegisterUI.java
│       │               ├── admin/
│       │               │   ├── AdminPanelUI.java
│       │               │   ├── ManageReportsUI.java
│       │               │   └── ManageUsersUI.java
│       │               ├── user/
│       │               │   ├── HomeUI.java
│       │               │   ├── CreateReportUI.java
│       │               │   └── ManageMyReportsUI.java
│       │               └── shared/
│       │                   ├── ReportUI.java
│       │                   └── UserPanelUI.java
│       │
│       └───resources/
│           ├── logging.properties
│           └── application.properties
│
├───nbactions.xml      # NetBeans configuration
├───lostfounddb.sql    # MySQL database schema and data
└───pom.xml            # Maven project configuration
```

# 🚀 How to Run on Windows

Welcome to the **Lost and Found Management System**! Follow this step-by-step guide to set up and run the application on your Windows machine. This guide is designed to be beginner-friendly, ensuring you can launch the login screen and explore the system with ease. Let’s get started! 🌟

---

## 📢 Important Notices

Before you begin, please read these notices carefully to ensure a smooth setup:

- **Read This Guide Thoroughly**: Follow each step in order to avoid issues.  
- **Do Not Modify Code Unless Instructed**: Changing notmentioned code may break the application. If you accidentally modify something, press `Ctrl+Z` to undo.  
- **Follow Steps Sequentially**: Each step builds on the previous one, so complete them in order.  
- **Contact for Support**: If you encounter errors, email me at **kalanadenuz2002@gmail.com** for assistance.

---

## 🛠 Prerequisites

Ensure you have the following installed and configured on your Windows machine:

- **JDK 22**: Installed and added to your system’s PATH. [Download JDK 22](https://www.oracle.com/java/technologies/javase/jdk22-archive-downloads.html)  
- **NetBeans IDE**: Version 12 or later, configured for Maven projects. [Download NetBeans](https://netbeans.apache.org/)  
- **MySQL Community Server**: Version 8.0 or later, with a user account and password set up. [Download MySQL](https://dev.mysql.com/downloads/mysql/)

---

## 📋 Step-by-Step Guide

### 1. Download and Extract the Repository

- Download the repository as a ZIP file from GitHub.  
  Click **Code > Download ZIP**
- Extract to:  
  `C:\Lost and Found System\`
- Verify all files are present including image files.

---

### 2. Set Up the MySQL Database

- Open the SQL file:  
  `C:\Lost and Found System\lostfounddb.sql`
- Launch MySQL CLI:

```bash
mysql -u your_username -p

```

- Paste and run the SQL script in MySQL CLI.
  
```sql
SHOW TABLES;
```

- Expected Output:
found_item, item, lost_item, report, user
- Check data:

```sql
SELECT * FROM user;
```
### 3. Create a New NetBeans Project

- Launch NetBeans IDE

- Create a project:
**File > New Project > Java with Maven > Java Application**

- Set:

- Project Name: LostandFoundSystem

- Location: `C:\Users\YourUsername\Documents\NetBeansProjects\`

### 4. Copy Repository Files to Project

- From:
`C:\Lost and Found System\`

- To:
`C:\Users\YourUsername\Documents\NetBeansProjects\LostandFoundSystem\`

- Overwrite when prompted.

- In NetBeans:
**Right-click project > Clean and Build**

### 5. Install MySQL Connector/J

- Download: MySQL Connector/J 8.0.30

- Extract to:
`C:\mysql-connector-java-8.0.30\`

- Open pom.xml:
```xml
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.30</version>
</dependency>
```
- verify this code in that xml file
  
- Update version if using a different one
  
- Save and Clean and Build

### 6. Create a Database Connection in NetBeans

**Open Services tab > Databases > Right-click > New Connection**

- Driver: MySQL (add JAR if not listed)

- Database: lostfounddb

- Username: your MySQL username

- Password: your MySQL password

- Test Connection

- Finish and connect
 
### 7. Update Database Credentials

- Open DBConnection.java: Location: 

**Projects > LostandFoundSystem > Source Packages > com.lostfound.config**

- Update password:

```java
private static final String PASSWORD = "your_password";
```
- Update URL:

```java  
private static final String URL = "jdbc:mysql://localhost:3306/lostfounddb";
```
- Remove any additional URL parameters
  
- Save file

### 8. Run the Application

- Open LostandFoundSystem.java: com.lostfound.app

- Right-click and select Run File

- OR right-click the project and choose Run

#### Login Accounts

##### Admin Account
- Email: `alice@example.com`
- Password: `password123`

##### User Account
- Email: `hank@example.com`
- Password: `password123`

## 🎉 You’re Done!

#### Congratulations! 🎊
##### You’ve successfully set up and run the LostandFoundSystem. You can now:

- Log in as admin or user

- Register users

- Report lost/found items

- View and manage reports

#### Enjoy! 😊

## 🛠 Troubleshooting

- Database Connection Failed: Ensure MySQL Server is running and Check credentials in DBConnection.java

- Missing Files: Recopy files from ZIP if needed

  

---


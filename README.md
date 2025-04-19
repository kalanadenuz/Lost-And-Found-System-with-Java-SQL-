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

### 🚀 How to Run
1. Clone the repository.
2. Import the project into **NetBeans**.
3. Set up the **MySQL database** using the provided schema.
4. Update database credentials in the config package.
5. Run the application from the main UI class.

---


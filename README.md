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
Lost and FOund System
├───images
│   ├───found        # Images for found items (referenced in database)
│   └───lost         # Images for lost items (referenced in database)
└───src
    └───main
        ├───java
        │   └───com
        │       └───lostfound
        │           ├───app       # Application entry point (e.g., LostandFoundSystem.java)
        │           ├───config    # Database connection setup (e.g., DBConnection.java)
        │           ├───dao       # Data Access Objects for CRUD operations (e.g., UserDAO, ReportDAO)
        │           ├───model     # Data models (e.g., User, Item, LostItem, FoundItem, Report)
        │           ├───service   # Business logic and service interfaces (e.g., UserService, ReportService)
        │           └───ui        # Swing-based UI screens (e.g., RegisterUI, ReportUI, UserPanelUI)
        └───resources
            ├───icons             # UI icons (e.g., found.png, lost.png)
            └───logging.properties # Logging configuration for java.util.logging
├───sql
    └───schema.sql           # MySQL schema and AI-generated data
```

### 🚀 How to Run
1. Clone the repository.
2. Import the project into **NetBeans**.
3. Set up the **MySQL database** using the provided schema.
4. Update database credentials in the config package.
5. Run the application from the main UI class.

---


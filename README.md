# MID E-Learning Platform

This project is an internal e-learning platform developed for interns at MID. It provides a comprehensive solution for course management, assignments, attendance, and student progress tracking.

## ✨ Features

- **User Authentication:** Secure login and registration for students, mentors, and admins.
- **Role-Based Access Control:** Different dashboards and functionalities for Admin, Mentor, and Student roles.
- **Course Management:** Admins and Mentors can create and manage courses.
- **Assignment Handling:** Create assignments, allow students to submit their work, and track submissions.
- **Attendance Tracking:** Monitor and manage student attendance.
- **Student Progress:** View detailed progress reports and analytics for students.
- **Announcements & Notifications:** Keep users informed about important updates.

## 🛠️ Technology Stack

- **Backend:** Java 21, Spring Boot 3.5.6, Spring Security, Spring Data JPA
- **Frontend:** Thymeleaf, Tailwind CSS
- **Database:** MySQL 8.0
- **Build Tools:** Maven (for backend), npm (for frontend dependencies)

##  Prerequisites

Before you begin, ensure you have the following installed on your system:

- [Git](https://git-scm.com/)
- [Java Development Kit (JDK) 21](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
- [Apache Maven](https://maven.apache.org/download.cgi)
- [Node.js and npm](https://nodejs.org/en/download/)
- [MySQL Server](https://dev.mysql.com/downloads/mysql/)

---

## 🚀 Getting Started

Follow these steps to run the application directly on your machine.

1.  **Clone the Repository:**
    ```bash
    git clone <repository-url>
    cd mid_elearning-local
    ```

2.  **Set up the Database:**
    - Make sure you have a MySQL server running on your machine.
    - Create a new database named `e_learning`.
    - Manually import the schema and initial data by executing the `init.sql` script located in the root of the project. You can use a tool like MySQL Workbench or the command line for this.
      ```bash
      mysql -u your_username -p e_learning < init.sql
      ```

3.  **Configure the Application:**
    - Open the file `src/main/resources/application.properties`.
    - Update the database connection details to match your local MySQL setup.
    ```properties
    # ... other properties
    spring.datasource.url=jdbc:mysql://localhost:3306/e_learning
    spring.datasource.username=your_mysql_user
    spring.datasource.password=your_mysql_password
    # ... other properties
    ```

4.  **Install Frontend Dependencies & Build CSS:**
    - Open a terminal in the project root and run `npm install` to install Tailwind CSS.
    - Run the build script to compile the CSS. It's recommended to keep this running in a separate terminal to watch for changes during development.
    ```bash
    npm install
    npm run build:css
    ```

5.  **Run the Spring Boot Application:**
    - Open another terminal in the project root.
    - Use the Maven wrapper to build and run the application:
    ```bash
    ./mvnw spring-boot:run
    ```
    *Note for Windows users: Use `mvnw.cmd spring-boot:run`.*

6.  **Access the Application:**
    Once the application has started, you can access it in your web browser at:
    [http://localhost:8080](http://localhost:8080)

---

## ⚙️ Configuration

The main application configuration is managed in the `src/main/resources/application.properties` file. This includes settings for the database connection, server port, and other Spring Boot properties.

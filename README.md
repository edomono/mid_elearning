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
- **Containerization:** Docker, Docker Compose

##  Prerequisites

Before you begin, ensure you have the following installed on your system:

- [Git](https://git-scm.com/)
- [Java Development Kit (JDK) 21](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
- [Apache Maven](https://maven.apache.org/download.cgi)
- [Node.js and npm](https://nodejs.org/en/download/)
- [Docker and Docker Compose](https://www.docker.com/products/docker-desktop/)

---

## 🚀 Getting Started

There are two primary ways to run this project: using Docker (recommended for ease of use) or setting it up manually on your local machine.

### Method 1: Running with Docker (Recommended)

This is the simplest way to get the application running, as it handles all dependencies and services automatically.

1.  **Clone the repository:**
    ```bash
    git clone <repository-url>
    cd mid_elearning-local
    ```

2.  **Build and Run the Application:**
    From the root of the project directory, run the following command:
    ```bash
    docker-compose up --build
    ```
    This command will:
    - Build the Docker image for the Spring Boot application.
    - Start the MySQL database container.
    - Start the Spring Boot application container.
    - The `init.sql` file will automatically set up the `e_learning` database schema.

3.  **Access the Application:**
    Once the containers are up and running, you can access the application in your web browser at:
    [http://localhost:8080](http://localhost:8080)

### Method 2: Manual Local Setup

Follow these steps if you prefer to run the application directly on your machine without Docker.

1.  **Clone the Repository:**
    ```bash
    git clone <repository-url>
    cd mid_elearning-local
    ```

2.  **Set up the Database:**
    - Make sure you have a MySQL server running on your machine.
    - Create a new database named `e_learning`.
    - Manually import the schema and initial data by executing the `init.sql` script located in the root of the project.

3.  **Configure Environment Variables:**
    - Create a copy of the `.env.example` file and name it `.env`.
    - Open the `.env` file and update the database connection details to match your local MySQL setup. **Important:** Change `DB_HOST` from `springboot_db` to `localhost` or `127.0.0.1`.
    ```properties
    APP_PORT=8080

    DB_HOST=localhost
    DB_PORT=3306
    DB_NAME=e_learning
    DB_USER=your_mysql_user
    DB_PASSWORD=your_mysql_password

    MYSQL_ROOT_PASSWORD=your_mysql_root_password
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

The application uses environment variables for configuration. These can be set in a `.env` file in the project root or directly in the `docker-compose.yml` file.

| Variable              | Description                               | Default (in Docker) |
| --------------------- | ----------------------------------------- | ------------------- |
| `APP_PORT`            | Port the application will run on.         | `8080`              |
| `DB_HOST`             | Hostname of the database server.          | `springboot_db`     |
| `DB_PORT`             | Port of the database server.              | `3306`              |
| `DB_NAME`             | The name of the database.                 | `e_learning`        |
| `DB_USER`             | Username for the database connection.     | `root`              |
| `DB_PASSWORD`         | Password for the database connection.     | `root`              |
| `MYSQL_ROOT_PASSWORD` | Root password for the MySQL container.    | `root`              |

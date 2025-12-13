# Bookstore Management System

## Team Members
Patrik Granström  
Gerli Hirv  
Taru Nipuli  
Veeti Vänttinen

## Overview
The Bookstore Management System is a desktop application that allows users to browse, purchase, and
manage books. Regular users can register, log in, add books to their cart, buy books and view their user details and order
history on a profile page. Admin users have access to a management page where they can add, edit, and remove
books from the store.

The application features multilingual support, allowing users to switch between English, Japanese, and Arabic.

## Features
- User registration and authentication
- User profile page with user details and order history
- Book catalog with search option by book name or author, and filtering by genre
- Admin management page for adding, updating, and deleting books
- Shopping cart functionality
- Checkout functionality

## Technology Stack
Java  
MariaDB  
JavaFX  
Maven  
Jenkins  
Docker  
JUnit  
Mockito  
TestFx  
Xming  
SonarQube  
SonarScanner  
JMeter

## Development Method
Agile  

## Activity Diagram
![UML activity diagram of the project](https://github.com/TheColourYellow/Autumn-2025-SDP-1/blob/main/Documents/UML_diagrams/activity_diagram.png)

## Localization
The Bookstore Management System allows users to switch between multiple languages.
Localization is implemented using Java’s ResourceBundle with all translations stored in the resources/ResourceBundles.

Users can select their preferred language from the language dropdown menu on the landing page.
After selecting the language, it is saved through the SessionManager, and all views are shown in the chosen language.
The Application also supports both LTR (Left-to-Right) and RTL (Right-to-Left) text directions.

The application supports following languages:
- English
- Japanese
- Arabic

The database uses seperate translation tables for genres, authors and book names, so that management
of the book catalogue is possible in each supported language.  
User selected langauge settings are saved to the user table through row-based localisation.  

## Set Up and Installation  
### Prerequisites
Before installing and running this project, make sure the following software is installed:
- **Docker** (Docker Desktop recommended)
- **Docker Compose** (included with Docker Desktop)
- **X11 Server for GUI support (JavaFX)**:
  - **Windows**: [Xming](https://sourceforge.net/projects/xming/)
  - **macOS**: [XQuartz](https://www.xquartz.org/)
  - **Linux**: Ensure X11 is installed and running

Verify installations:
```bash
docker --version
docker compose version
git --version
```
---
### Getting the project
Clone the project repository using Git (command line or IDE):
```bash
git clone <repository-url>
cd <project-root>
```
---
### Configuration
Ensure the following files exist in the project root:
- 'docker-compose.yml'
- '.env'

Example '.env'
```env
# Database configuration
DATABASE_URL = jdbc:mariadb://mariadb:3306/bookstore_db
DATABASE_USER = bookstore_user
DATABASE_PASSWORD = <insert db password>
DATABASE_ROOT_PASSWORD = <insert root db password>

# Admin user credentials
ADMIN_USERNAME = admin
ADMIN_EMAIL = default@admin.com
ADMIN_PASSWORD = <insert admin password>
```
> **Warning**: Never commit '.env' files containing secrets to version control.
---
### GUI Requirements (JavaFX)
Since the application uses JavaFX, an X11 server is required to display the GUI when running the application via Docker container.
- **Windows**: Start Xming before running the application.
- **macOS**: Start XQuartz before running the application.
- **Linux**: Ensure X11 is running.
---
### Running the Application
The project consists of two Docker containers:
- **JavaFX Bookstore Application**
- **MariaDB Database**

From the project root, start the containers:
#### Using the prebuilt image:
```bash
docker compose up -d
```
#### Building the image locally:
```bash
docker compose up --build -d
```
> **Note**: To build locally, comment out the 'image:' line and uncomment the 'build:' line under javafx-app in docker-compose.yml.
---
### Verifying installation
Check that both containers are running:
```bash
docker compose ps
```
If using Docker Desktop, container status can also be checked via the **Containers** tab.

---
### Stopping and Removing the application
To stop and remove the containers:
```bash
docker compose down
```
---
### Database Access (Optional)
To connect to the running MariaDB container from command line:
```bash
docker exec -it bookstore-db mariadb -u bookstore_user -p
```
Enter the password specified in the '.env' file when prompted.

## SonarQube Code Analysis
The project uses SonarQube for comprehensive static code analysis, focusing on security, 
reliability, and maintainability.

Current Quality Gate Rating:
    - Security: A
    - Reliability: A
    - Maintainability: A
    - Hotspots Reviewed: A

How to Run SonarQube Analysis
1. Start SonarQube Server (if running locally):
    - Navigate to the `bin` directory of your SonarQube installation.
    - Run the appropriate script:
      On windows: `StartSonar.bat`
      On macOS/Linux: `./sonar.sh start`
    - Access the server at `http://localhost:9000` in your web browser.
2. Configure Your Project:
    - Create a `sonar-project.properties` file in the root directory of your project with the following content:
      ```
      sonar.projectKey=your_project_key
      sonar.projectName=YourProjectName
      sonar.projectVersion=1.0
      sonar.host.url=http://localhost:9000
      sonar.sources=src
      sonar.java.binaries=target/classes
      sonar.java.libraries=target/classes
      sonar.tokens=your_sonar_token
      
      ```
3. Run Sonar Scanne:
    - Open a terminal in your project directory.
    - Execute the command:
      `sonar-scanner`
4. View Results:
    - Open your web browser and navigate to `http://localhost:9000`
    - Navigate to your project to view the analysis results.

## Non-functional Tests  
### Heuristic Evaluation    
Application has been heuristically evaluated according to Nielsen's Heuristics. Majority of the findings were severity ranking 2.  
Two findings were ranked with a 3 and one finding was ranked with 4.  
Rank 4 finding was deemed important enough to be fixed following the evaluation.  

### User Acceptance Testing  
User Acceptance Test Plan was completed during development, focusing on user registration, user log in and capability to browse books and utilise shopping cart.  

### Performance Tests  
![Performance test results](https://github.com/TheColourYellow/Autumn-2025-SDP-1/blob/main/Documents/reports/performance_test_results.png)  
Performance tests were conducted as part of CI / CD pipeline applied through Jenkins

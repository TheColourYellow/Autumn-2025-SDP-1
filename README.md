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

## Development Method
Agile

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
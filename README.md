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

The application supports following languages:
- English
- Japanese
- Arabic

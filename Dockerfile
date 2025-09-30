
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy your built JAR into the container
COPY target/bookstore-1.0-SNAPSHOT.jar app.jar

# Run the JavaFX application
CMD ["java", "--module-path", "/usr/share/openjfx/lib", "--add-modules", "javafx.controls,javafx.fxml", "-jar", "YourApp.jar"]

# Build stage
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Copy the entire project
COPY . .

# Install Maven
RUN apt-get update && \
    apt-get install -y maven && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Install dependencies for GUI
RUN apt-get update && \
    apt-get install -y wget unzip libgtk-3-0 libglu1-mesa libgl1 libx11-6 libxcb1 libxtst6 libxrender1 xauth x11-apps xvfb && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Download JavaFX SDK
RUN wget https://download2.gluonhq.com/openjfx/21.0.2/openjfx-21.0.2_linux-x64_bin-sdk.zip -O javafx-sdk.zip && \
    unzip javafx-sdk.zip -d /opt/ && \
    rm javafx-sdk.zip

ENV PATH="/opt/javafx-sdk-21.0.2/lib:$PATH"
ENV DISPLAY=host.docker.internal:0.0

# Copy your built JAR into the container
COPY --from=builder /app/target/bookstore-1.0-SNAPSHOT.jar app.jar

# Run the JavaFX application
CMD ["java", "--module-path", "/opt/javafx-sdk-21.0.2/lib", "--add-modules", "javafx.controls,javafx.fxml", "-jar", "app.jar"]

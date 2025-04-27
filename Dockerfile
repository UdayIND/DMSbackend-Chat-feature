# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:17-jdk

# Set the working directory
WORKDIR /app

# Copy the Maven wrapper and project files
COPY . .

# Make the Maven wrapper executable
RUN chmod +x mvnw

# Build the application
RUN ./mvnw clean package -DskipTests

# Run the application
CMD ["java", "-jar", "target/DMSbackend-0.0.1-SNAPSHOT.jar"]

# Use an official OpenJDK runtime as a parent image
FROM eclipse-temurin:17-jdk

# Set the working directory
WORKDIR /app

# Copy the Maven wrapper and project files
# Copy Maven wrapper and related files first
COPY .mvn/ .mvn/
COPY mvnw .
COPY mvnw.cmd .
COPY pom.xml .

# Then copy the rest of the source code
COPY src/ src/

RUN chmod +x mvnw

RUN ./mvnw clean package -DskipTests

# Run the application
CMD ["java", "-jar", "target/DMSbackend-0.0.1-SNAPSHOT.jar"]

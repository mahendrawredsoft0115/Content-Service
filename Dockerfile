# Use official Eclipse Temurin JDK 21 as base image
FROM eclipse-temurin:21-jdk

# Set working directory in container
WORKDIR /app

# Copy the built jar file into container
COPY build/libs/content-feed-0.0.1-SNAPSHOT.jar  app.jar

# Expose port (match your Spring Boot server.port if different)
EXPOSE 8082

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]

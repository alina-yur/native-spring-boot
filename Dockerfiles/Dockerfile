#docker build -f Dockerfiles/Dockerfile -t native-spring-boot-jdk:latest .

# Build
FROM eclipse-temurin:23 AS build

# Set the working dir
WORKDIR /build

# Copy the source code for building
COPY . .

# Build the application
RUN ./mvnw package -DskipTests

# Run time stage
FROM eclipse-temurin:23

# Set the working dir
WORKDIR /app

# Copy the jar
COPY --from=build /build/target/demo-0.0.1-SNAPSHOT.jar /app/demo-0.0.1-SNAPSHOT.jar

# Expose the application port
EXPOSE 8080

# Run
CMD ["java", "-jar", "demo-0.0.1-SNAPSHOT.jar"]
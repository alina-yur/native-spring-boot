# docker build -f Dockerfiles/Dockerfile -t native-spring-boot-jdk:latest .
# docker run -p 8080:8080 localhost/native-spring-boot-jdk:latest

# Build with GraalVM 25.2
FROM container-registry.oracle.com/graalvm/jdk:25i2 AS build

# Set the working dir
WORKDIR /build

# Copy the source code for building
COPY . .

# Build the application
RUN ./mvnw package -DskipTests

# Run-time stage
FROM container-registry.oracle.com/graalvm/jdk:25i2

# Set the working dir
WORKDIR /app

# Copy the jar
COPY --from=build /build/target/demo-0.0.1-SNAPSHOT.jar /app/app.jar

# Expose the application port
EXPOSE 8080

# Run
CMD ["java", "-jar", "app.jar"]

#build: docker build -f Dockerfiles/Dockerfile -t native-spring-boot-jdk:latest .

# Using Oracle GraalVM for JDK 23
FROM openjdk:23-slim // do temurin

# Set the working directory to /home/app
WORKDIR /build

# Copy the source code into the image for building
COPY . .

# Build
RUN ./mvnw package -DskipTests

WORKDIR /app
RUN cp /build/target/demo-0.0.1-SNAPSHOT.jar /app/demo-0.0.1-SNAPSHOT.jar

EXPOSE 8080
CMD ["java", "-jar", "demo-0.0.1-SNAPSHOT.jar"]
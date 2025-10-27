# Build stage
FROM --platform=linux/amd64 eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY pom.xml mvnw ./
COPY .mvn .mvn
RUN chmod +x ./mvnw
RUN ./mvnw dependency:go-offline
COPY src src
RUN ./mvnw package -DskipTests

# Runtime stage
FROM --platform=linux/amd64 eclipse-temurin:21-jre-alpine
RUN apk --no-cache add curl
VOLUME /tmp
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

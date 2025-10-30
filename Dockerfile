# Build stage
FROM --platform=linux/amd64 maven:3.9-amazoncorretto-21-alpine AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src src
RUN mvn package -DskipTests

# Runtime stage
FROM --platform=linux/amd64 amazoncorretto:21-alpine
RUN apk --no-cache add curl
VOLUME /tmp
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]

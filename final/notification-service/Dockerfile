# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY api-gateway/pom.xml api-gateway/
COPY cart-service/pom.xml cart-service/
COPY catalog-service/pom.xml catalog-service/
COPY inventory-service/pom.xml inventory-service/
COPY notification-service/pom.xml notification-service/
COPY order-service/pom.xml order-service/
COPY payment-service/pom.xml payment-service/

RUN mvn dependency:go-offline -B
COPY . .
RUN mvn clean package -DskipTests -pl notification-service -am
RUN cp notification-service/target/notification-service-*.jar app.jar

# Stage 2: Run
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/app.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]

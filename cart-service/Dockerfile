FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY . .
RUN ./gradlew :cart-service:bootJar --no-daemon -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/cart-service/build/libs/*.jar app.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]

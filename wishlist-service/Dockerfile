FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY . .
RUN ./gradlew :wishlist-service:bootJar --no-daemon -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/wishlist-service/build/libs/*.jar app.jar
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "app.jar"]

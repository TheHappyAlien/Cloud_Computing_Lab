FROM maven:3.6.3-openjdk-17 AS build
WORKDIR /tictactoe
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src src
RUN mvn package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /tictactoe
COPY --from=build /tictactoe/target/tictactoe-0.0.1-SNAPSHOT.jar ./tictactoe.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "tictactoe.jar"]
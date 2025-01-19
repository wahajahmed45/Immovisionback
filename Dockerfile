FROM openjdk:17-jdk-slim
WORKDIR /app
COPY . .
RUN ./gradlew build -x test
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "build/libs/immovision-0.0.1-SNAPSHOT.jar"]
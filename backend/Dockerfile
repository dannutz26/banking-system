FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY backend/.mvn/ .mvn/
COPY backend/mvnw backend/pom.xml ./
RUN chmod +x mvnw && ./mvnw dependency:go-offline
COPY backend/src/ ./src/
RUN ./mvnw package -DskipTests
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "target/backend-0.0.1-SNAPSHOT.jar"]
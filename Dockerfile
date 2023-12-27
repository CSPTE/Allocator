FROM maven:3.9.5-eclipse-temurin-17-focal

WORKDIR /app

COPY . .

RUN mvn clean install -DskipTests

EXPOSE 8080

WORKDIR /app/bootstrap

CMD ["mvn", "spring-boot:run"]
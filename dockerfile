FROM maven:3.8.7-eclipse-temurin-17-focal AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn -e -B dependency:resolve
COPY src ./src
RUN mvn -e -B install spring-boot:repackage

FROM eclipse-temurin:17.0.6_10-jre
COPY --from=builder /app/target/stock-api-1.0.jar .
CMD ["java","-jar","/stock-api-1.0.jar"]
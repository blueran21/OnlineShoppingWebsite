FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

COPY pom.xml ./
COPY account-service/pom.xml account-service/pom.xml
COPY item-service/pom.xml item-service/pom.xml
COPY order-service/pom.xml order-service/pom.xml

RUN mvn -q -DskipTests -pl order-service -am dependency:go-offline

COPY order-service/src order-service/src

RUN mvn -q -DskipTests -pl order-service -am package

FROM eclipse-temurin:17-jre
WORKDIR /app
ENV SPRING_PROFILES_ACTIVE=docker
COPY --from=build /workspace/order-service/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["sh","-c","java ${JAVA_OPTS:-} -jar app.jar"]

# syntax=docker/dockerfile:1
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace

# 先拷贝所有 pom（父 + 各模块），保证能解析 parent
COPY pom.xml ./
COPY account-service/pom.xml account-service/pom.xml
COPY item-service/pom.xml item-service/pom.xml
COPY order-service/pom.xml order-service/pom.xml

# 预拉依赖（只针对本模块及需要的模块）
RUN mvn -q -DskipTests -pl account-service -am dependency:go-offline

# 再拷贝源码（仅本模块）
COPY account-service/src account-service/src

# 只构建本模块
RUN mvn -q -DskipTests -pl account-service -am package

FROM eclipse-temurin:17-jre
WORKDIR /app
ENV SPRING_PROFILES_ACTIVE=docker
COPY --from=build /workspace/account-service/target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["sh","-c","java ${JAVA_OPTS:-} -jar app.jar"]

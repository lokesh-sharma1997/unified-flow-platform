FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /build
COPY pom.xml /build/pom.xml
COPY shared/ufp-common /build/shared/ufp-common
COPY services/ufp-pipeline /build/services/ufp-pipeline
RUN mvn -f /build/pom.xml clean package -pl shared/ufp-common,services/ufp-pipeline -am -DskipTests --no-transfer-progress

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
RUN addgroup -S ufp && adduser -S ufp -G ufp
COPY --from=build /build/services/ufp-pipeline/target/*.jar app.jar
USER ufp
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]

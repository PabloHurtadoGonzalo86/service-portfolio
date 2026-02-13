# NOTE: Requires pre-built JAR. Run ./gradlew bootJar before docker build.
# The CI/CD pipeline (cd.yml) handles this automatically.
FROM eclipse-temurin:24-jre-alpine
RUN addgroup -S spring && adduser -S spring -G spring
RUN apk add --no-cache curl
WORKDIR /app
COPY build/libs/*.jar app.jar
RUN chown spring:spring app.jar
USER spring:spring
EXPOSE 8080
ENV JAVA_OPTS="-Xms256m -Xmx512m"
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]

FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN apt-get update && apt-get install -y maven && mvn -q -DskipTests package

FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/dashboard-service-0.1.0.jar app.jar

# OpenRASP Java agent is attached at deploy time via -javaagent, not baked into
# the image, so the same image can run with or without the agent for the
# Phase 4 adversarial test's agent-off / agent-on comparison. See
# docs/writeup.md "What Could Go Wrong" for why that comparison matters.
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]

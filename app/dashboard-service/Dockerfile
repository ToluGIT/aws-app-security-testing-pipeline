FROM eclipse-temurin:11-jdk-jammy AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN apt-get update && apt-get install -y maven && mvn -q -DskipTests package

FROM eclipse-temurin:11-jre-jammy
WORKDIR /app
COPY --from=build /app/target/dashboard-service-0.1.0.jar app.jar

# OpenRASP Java agent, v1.3.7, the last tagged release. Downloaded and
# extracted here, but NOT wired into ENTRYPOINT. Whether the agent 
# attaches is controlled per-pod via the Kubernetes deployment's `command`
# override, not baked into the image, so the same image serves both the
# agent-off and agent-on comparison.
RUN apt-get update && apt-get install -y wget && \
    wget -q https://github.com/baidu/openrasp/releases/download/v1.3.7/rasp-java.tar.gz && \
    tar -xzf rasp-java.tar.gz && \
    mv rasp-* /opt/rasp && \
    rm rasp-java.tar.gz && \
    apt-get remove -y wget && apt-get autoremove -y

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
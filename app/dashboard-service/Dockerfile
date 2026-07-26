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
    rm rasp-java.tar.gz && \
    mv rasp-* /opt/rasp && \
    apt-get remove -y wget && apt-get autoremove -y

# OpenRASP's official plugin ships with meta.all_log = true, an
# "observation mode" switch that force-downgrades every algorithm's
# declared `action: 'block'` to `action: 'log'` (see official.js,
# ~line 43 and ~line 761). Confirmed via a live test: detection worked
# correctly (90% confidence, exact attack identified, query logged in
# alarm.log) but nothing was ever blocked, because of this default, not
# because detection failed. Flipped to false so declared block actions
# actually block, which is the whole point of an in-process runtime
# defense layer for this project.
RUN sed -i 's/all_log: true/all_log: false/' /opt/rasp/rasp/plugins/official.js

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
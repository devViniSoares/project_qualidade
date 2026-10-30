FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw
COPY src src
RUN ./mvnw -B verify
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN mkdir /app/data && chown -R 10001:10001 /app
COPY --from=build /workspace/target/organizacao-recursos-1.0.0-SNAPSHOT.jar app.jar
USER 10001:10001
ENV BIND_ADDRESS=0.0.0.0
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]

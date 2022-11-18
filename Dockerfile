FROM amazoncorretto:17 as builder
WORKDIR /cme
COPY . .
RUN ./mvnw -DskipTests package

FROM amazoncorretto:17
WORKDIR /cme
COPY --from=builder /cme/target/cme-0.0.1-SNAPSHOT.jar ./cme.jar
EXPOSE 8080
EXPOSE 4000
ENTRYPOINT ["java", "-jar", "/cme/cme.jar"]
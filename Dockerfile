FROM amazoncorretto:17
WORKDIR /cme
COPY target/cme-0.0.1-SNAPSHOT.jar cme.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/cme/cme.jar"]
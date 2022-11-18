FROM adoptopenjdk/openjdk11
WORKDIR /app
COPY target/ReadingIsGood-0.0.1-SNAPSHOT.jar ReadingIsGood-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java","-jar","/app/ReadingIsGood-0.0.1-SNAPSHOT.jar"]
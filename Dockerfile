FROM adoptopenjdk/openjdk11:ubi
ADD target/demo-api-gateway-0.0.1-SNAPSHOT.jar /app.jar
ENTRYPOINT ["java","-jar", "-Dspring.profiles.active=docker","/app.jar"]
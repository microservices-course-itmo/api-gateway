FROM adoptopenjdk/openjdk11:ubi
ADD target/api-gateway-0.1.0-SNAPSHOT.jar /app.jar
ENTRYPOINT ["java","-jar", "-Dspring.profiles.active=docker","/app.jar"]
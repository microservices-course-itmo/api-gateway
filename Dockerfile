FROM adoptopenjdk/openjdk11:ubi
ADD target/api-gateway-0.2.0-SNAPSHOT.jar /app.jar
ENTRYPOINT ["java","-jar", "-Dspring.profiles.active=docker","/app.jar"]
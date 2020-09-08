FROM adoptopenjdk/openjdk11:ubi
ADD target/api-gateway.jar /app.jar
ENTRYPOINT ["java","-jar", "-Dspring.profiles.active=docker","/app.jar"]
FROM openjdk:8-jdk-alpine
VOLUME /tmp
ADD build/libs/*.war /app.war
ENTRYPOINT ["java","-Xms256m", "-Xmx1024m", "-Djava.security.egd=file:/dev/./urandom","-jar","/app.war"]

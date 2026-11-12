
FROM tomcat:10.1-jdk17-temurin

RUN rm -rf /usr/local/tomcat/webapps/*

COPY target/bank.war /usr/local/tomcat/webapps/ROOT.war

EXPOSE 8080

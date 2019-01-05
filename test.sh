mvn assembly:assembly && java -jar target/tracesonar-0.1-SNAPSHOT.jar -f target/Tomcat7.0.jar -q "org.apache.catalina.connector.InputBuffer#*"

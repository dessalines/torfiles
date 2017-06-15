pkill -f target/torfiles.jar
mvn clean install -DskipTests
nohup java -jar target/torfiles.jar $@ >> log.out &

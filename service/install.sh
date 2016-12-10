pkill -f target/torshare.jar
mvn clean install -DskipTests
nohup java -jar target/torshare.jar $@ >> log.out &

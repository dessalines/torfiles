pkill -9 target/torshare.jar
mvn clean install
nohup java -jar target/torshare.jar $@ >> log.out &

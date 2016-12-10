pkill -f target/torshare.jar
mvn clean install
nohup java -Xmx1g -jar target/torshare.jar $@ >> log.out &

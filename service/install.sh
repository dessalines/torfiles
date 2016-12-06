pkill -9 target/torshare.jar
mvn clean install
java -jar target/torshare.jar "$@"

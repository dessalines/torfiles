cd ~/git/torshare/service
pgrep -f torshare.jar || nohup java -jar target/torshare.jar -p2pspider /home/tyler/git/p2pspider/ -loglevel debug> log.out

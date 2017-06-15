cd ~/git/torfiles/service
pgrep -f torfiles.jar || nohup java -jar target/torfiles.jar -p2pspider /home/tyler/git/p2pspider/ -loglevel debug> log.out

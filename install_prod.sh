# Building the front end
pkill -f torshare.jar
cd ui
yarn
ng build -prod -aot
cd ..

# Building the back end
cd service
sh install.sh -torrents_dir ../../p2pspider/torrents

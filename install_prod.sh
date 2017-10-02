# Building the front end
pkill -f torfiles.jar
cd ui
yarn
ng build -prod -aot
cd ..

# Building the back end
cd service
sh install.sh

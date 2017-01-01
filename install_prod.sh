# Building the front end
cd ui
yarn
ng build -prod
cd ..

# Building the back end
cd service
sh install.sh -ssl ~/keystore.jks -peer_scanner

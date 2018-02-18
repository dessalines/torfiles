# Building the front end
cd ui
yarn
ng build -dev
cd ..

# Building the back end
cd service
sh install.sh -scanTPB -crawl

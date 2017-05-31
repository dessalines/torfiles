[TorShare](http://torshare.ml) &mdash; An open-source torrent search service. 
==========
![](http://img.shields.io/version/0.0.1.png?color=green)
[![Build Status](https://travis-ci.org/dessalines/torshare.svg?branch=master)](https://travis-ci.org/dessalines/torshare)

<!---
	Torshare: an open-source torrent caching service.
-->

[TorShare](http://torshare.ml) is an open-source, self-hostable service for storing, sharing, and searching for torrents.

It features:
- A clean search interface, that searches for all torrent files. 
- Uses [p2pspider](https://github.com/fanpei91/p2pspider) to crawl the DHT network, to build a library of torrents and their peer counts. 
- Torrent batch uploader, for uploading of all your torrents at once.
- A details page with the torrent metadata.


Tech used:
- [Java Spark](https://github.com/perwendel/spark), [Bootstrap v4](https://github.com/twbs/bootstrap), [Angular4](https://github.com/angular/angular), [Angular-cli](https://github.com/angular/angular-cli), [ng2-bootstrap](http://valor-software.com/ng2-bootstrap/), [ActiveJDBC](http://javalite.io/activejdbc), [Liquibase](http://www.liquibase.org/), [Postgres](https://www.postgresql.org/), 


<!-- Join the subreddit: [/r/flowchat](https://www.reddit.com/r/flowchat/) -->


## Installation 

### Requirements
- Java 8 + Maven
- Node + npm, [nvm](https://github.com/creationix/nvm) is the preferred installation method.
- Postgres 9.3 or higher

### Download TorShare
`git clone https://github.com/dessalines/torshare`

### Setup a postgres database
`psql -c 'create user torshare with password "xxxx" superuser;" -U postgres`
`psql -c 'create database torshare;' -U postgres`

[Here](https://www.digitalocean.com/community/tutorials/how-to-install-and-use-postgresql-on-ubuntu-16-04) are some instructions to get your DB up and running.

#### Edit your `pom.xml` file to point to your database

```sh
cd torshare
vim service/pom.xml
```

Edit it to point to your own database:
```xml
<!--The Database location and login, here's a sample-->
<jdbc.url>jdbc:postgresql://127.0.0.1/torshare</jdbc.url>
<jdbc.username>torshare</jdbc.username>
<jdbc.password>xxxx</jdbc.password
```

### Install Torshare

Local testing:

`./install_dev.sh` and goto `http://localhost:4567/`

for a production environment:

- edit `ui/config/environment.prod.ts` to point to your hostname
- Create a (.jks) file with your ssl certs, put it in your home directory, @ `keystore.jks`
- `./install_prod.sh -ssl KEYSTORE.JKS -p2pspider P2P_SPIDER_FOLDER`

You can redirect ports in linux to route from port 443, or 80 to this port:

`sudo iptables -t nat -I PREROUTING -p tcp --dport 443 -j REDIRECT --to-ports 4567`

### Install p2pSpider

```sh
git clone https://github.com/dessalines/p2pspider
cd p2pspider
npm i
node index.js
```

This will start running `p2pspider`, which crawls the DHT to download torrents and peers. 

==========

## Bugs and feature requests
Have a bug or a feature request? If your issue isn't [already listed](https://github.com/dessalines/torshare/issues/), then open a [new issue here](https://github.com/dessalines/torshare/issues/new).

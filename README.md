[TorFiles](http://torfiles.ml) &mdash; An open-source search engine for finding files within torrents.
==========
![](http://img.shields.io/version/0.0.1.png?color=green)
[![Build Status](https://travis-ci.org/dessalines/torfiles.svg?branch=master)](https://travis-ci.org/dessalines/torfiles)

<!---
	TorFiles: An open-source search engine for finding files within torrents.
-->

[TorFiles](http://torfiles.ml) is an open-source, self-hostable service for storing, sharing, and searching not just torrents, but **all files within the torrents**. 

You can now search for:

- Specific songs within discography torrents: *beck sunday sun*.
- Specific episodes of shows: *Seinfeld s04e11*.

It features:
- A clean search interface, that searches every file of every torrent, sorting automatically by peers and size.
- Uses the DHT crawler [p2pspider](https://github.com/dessalines/p2pspider) to build a library of torrents and their peer counts.

Tech used:
- [Java Spark](https://github.com/perwendel/spark), [Bootstrap v4](https://github.com/twbs/bootstrap), [Angular4](https://github.com/angular/angular), [Angular-cli](https://github.com/angular/angular-cli), [ng2-bootstrap](http://valor-software.com/ng2-bootstrap/), [ActiveJDBC](http://javalite.io/activejdbc), [Liquibase](http://www.liquibase.org/), [Postgres](https://www.postgresql.org/), 


<!-- Join the subreddit: [/r/flowchat](https://www.reddit.com/r/flowchat/) -->


## Installation 

### Requirements
- Java 8 + Maven
- Node + npm, [nvm](https://github.com/creationix/nvm) is the preferred installation method.
- Postgres 9.3 or higher

### Download TorFiles
`git clone https://github.com/dessalines/torfiles`

### Setup a postgres database
```sh
psql -c "create user torfiles with password 'asdf' superuser;" -U postgres
psql -c "create database torfiles with owner torfiles;" -U postgres
```

[Or here](https://www.digitalocean.com/community/tutorials/how-to-install-and-use-postgresql-on-ubuntu-16-04) are some instructions to get your DB up and running.

#### Edit your `pom.xml` file to point to your database

```sh
cd torfiles
vim service/pom.xml
```

Edit it to point to your own database:
```xml
<!--The Database location and login, here's a sample-->
<jdbc.url>jdbc:postgresql://127.0.0.1/torfiles</jdbc.url>
<jdbc.username>torfiles</jdbc.username>
<jdbc.password>asdf</jdbc.password
```

### Install TorFiles

Local testing:

`./install_dev.sh` and goto `http://localhost:4567/`

for a production environment:

- edit `ui/config/environment.prod.ts` to point to your hostname
- Create a (.jks) file with your ssl certs, put it in your home directory, @ `keystore.jks`
- `./install_prod.sh -ssl KEYSTORE.JKS`

You can redirect ports in linux to route from port 443, or 80 to this port:

`sudo iptables -t nat -I PREROUTING -p tcp --dport 443 -j REDIRECT --to-ports 4567`

### Install p2pSpider

```sh
git clone https://github.com/dessalines/p2pspider
cd p2pspider
vim index.js
```
Edit the client to point to your DB:

```javascript
const client = new Client({
  user: 'torfiles',
  host: 'localhost',
  database: 'torfiles',
  password: 'asdf',
});
```

Run p2pSpider:
```sh
npm i
node index.js
```

Or use `pm2` to start multiple instances of it, to crawl faster:
```sh
npm i -g pm2
pm2 start index.js -i 5
```

This will start running `p2pspider`, which crawls the DHT to download torrents and peers, and saves them to your database. **It will take a few days for your database to fill up.**

---

## Bugs and feature requests
Have a bug or a feature request? If your issue isn't [already listed](https://github.com/dessalines/torfiles/issues/), then open a [new issue here](https://github.com/dessalines/torfiles/issues/new).

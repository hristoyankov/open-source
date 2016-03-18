#!/usr/bin/env bash

add-apt-repository ppa:openjdk-r/ppa
apt-get -y update
apt-get install -y openjdk-8-jdk

curl -sL https://deb.nodesource.com/setup | bash -
apt-get install -y nodejs

apt-get install -y git

npm install npm -g

npm cache clean -f > /dev/null 2>&1
npm install -g n
n stable > /dev/null 2>&1

npm install -g bower

su vagrant -c "cd /vagrant/resources/sass && bower install bower.json"

exit 0

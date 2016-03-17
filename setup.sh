#!/bin/bash

add-apt-repository ppa:openjdk-r/ppa
apt-get -y update
apt-get install -y openjdk-8-jdk

apt-get install -y nodejs-legacy
apt-get install -y npm
npm install -g bower
bower install /vagrant/resources/sass/bower.json

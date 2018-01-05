#!/bin/bash

FULLPATH="$( cd "$(dirname "$0")" ; pwd -P )"

sudo echo "Building Exenium Bot"
mvn clean install
sudo rm /etc/init.d/exenium
sudo ln -s $FULLPATH/target/ExeniumBot-1.0.jar /etc/init.d/exenium
sudo /etc/init.d/exenium restart
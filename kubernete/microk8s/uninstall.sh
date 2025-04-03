#!/bin/bash
source .env

rm -rf  $KUBERNETES_VOL_DIR/dbdata/
rm -rf  $KUBERNETES_VOL_DIR/geowebcache-data/
rm -rf  $KUBERNETES_VOL_DIR/rabbitmq-data/

microk8s kubectl delete -R -f ./workdir
rm -rf ./workdir

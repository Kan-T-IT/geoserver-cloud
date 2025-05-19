#!/bin/bash
source .env

echo "Deleting Kubernetes resources"

microk8s kubectl delete -R -f ./workdir

# Delete namespace
echo "Deleting namespace: $KUBERNETES_NAMESPACE"

microk8s kubectl delete namespace $KUBERNETES_NAMESPACE

# Delete volumes
echo "Deleting volume directories"

rm -rf $KUBERNETES_VOL_DIR/dbdata/
rm -rf $KUBERNETES_VOL_DIR/geowebcache-data/
rm -rf $KUBERNETES_VOL_DIR/rabbitmq-data/

# Disable MetalLB
#echo "Disabling MetalLB"
#microk8s disable metallb

# Clean up workdir
echo "Cleaning up workdir"

rm -rf ./workdir

echo "Uninstallation complete."

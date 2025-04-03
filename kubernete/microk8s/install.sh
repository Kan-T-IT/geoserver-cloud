#!/bin/bash
source .env

echo "Applying .env config"

cp -r ./templates workdir

grep -rl KUBERNETES-SITE-URL ./workdir | xargs sed -i 's|KUBERNETES-SITE-URL|'"$KUBERNETES_SITE_URL"'|g'
grep -rl KUBERNETES-NODE-NAME ./workdir | xargs sed -i 's|KUBERNETES-NODE-NAME|'"$KUBERNETES_NODE_NAME"'|g'
grep -rl KUBERNETES-VOL-DIR ./workdir | xargs sed -i 's|KUBERNETES-VOL-DIR|'"$KUBERNETES_VOL_DIR"'|g'
grep -rl CLUSTER-ISSUER-NAME ./workdir | xargs sed -i 's|CLUSTER-ISSUER-NAME|'"$CLUSTER_ISSUER_NAME"'|g'
grep -rl KUBERNETES-NAMESPACE ./workdir | xargs sed -i 's|KUBERNETES-NAMESPACE|'"$KUBERNETES_NAMESPACE"'|g'
grep -rl ACL-PASSWORD ./workdir | xargs sed -i 's|ACL-PASSWORD|'"$ACL_PASSWORD"'|g'
grep -rl GEOSERVER-PASSWORD ./workdir | xargs sed -i 's|GEOSERVER-PASSWORD|'"$GEOSERVER_PASSWORD"'|g'
sed -i 's|SERVER-PUBLIC-IP|'"$SERVER_PUBLIC_IP"'|g' ./workdir/configs/metallb-configmap.yaml 

mkdir  $KUBERNETES_VOL_DIR/dbdata/
mkdir  $KUBERNETES_VOL_DIR/geowebcache-data/
mkdir  $KUBERNETES_VOL_DIR/rabbitmq-data/

microk8s enable metallb:$SERVER_PUBLIC_IP-$SERVER_PUBLIC_IP
microk8s kubectl apply -R -f workdir/configs
microk8s kubectl apply -R -f workdir/database

echo "Waiting for database to start"

tempgndb=`microk8s kubectl get pod -n $KUBERNETES_NAMESPACE -l component=database --no-headers`
gnbd=( $tempgndb )
gnbdstatus=${gnbd[2]}

while [ "$gnbdstatus" != "Running" ]
do
    tempgndb=`microk8s kubectl get pod -n $KUBERNETES_NAMESPACE -l component=database --no-headers`
    gnbd=( $tempgndb )
    gnbdstatus=${gnbd[2]}
done
sleep 5

echo "Applying initial database configuration"

database=${gnbd[0]}
microk8s kubectl exec -n $KUBERNETES_NAMESPACE -it $database -- psql -U postgres postgres -c "CREATE ROLE pgconfig WITH PASSWORD '"$ACL_PASSWORD"';"
microk8s kubectl exec -n $KUBERNETES_NAMESPACE -it $database -- psql -U postgres postgres -c 'ALTER ROLE pgconfig WITH login;'
microk8s kubectl exec -n $KUBERNETES_NAMESPACE -it $database -- psql -U postgres postgres -c 'ALTER ROLE pgconfig WITH superuser;'
microk8s kubectl exec -n $KUBERNETES_NAMESPACE -it $database -- psql -U postgres postgres -c 'CREATE DATABASE pgconfig OWNER pgconfig;'
microk8s kubectl exec -n $KUBERNETES_NAMESPACE -it $database -- psql -U postgres pgconfig -c 'CREATE EXTENSION postgis;'

microk8s kubectl exec -n $KUBERNETES_NAMESPACE -it $database -- psql -U postgres postgres -c "CREATE ROLE "$DATABASE_USER" WITH PASSWORD '"$DATABASE_PASS"';"
microk8s kubectl exec -n $KUBERNETES_NAMESPACE -it $database -- psql -U postgres postgres -c 'ALTER ROLE '"$DATABASE_USER"' WITH login;'
microk8s kubectl exec -n $KUBERNETES_NAMESPACE -it $database -- psql -U postgres postgres -c 'ALTER ROLE '"$DATABASE_USER"' WITH superuser;'
microk8s kubectl exec -n $KUBERNETES_NAMESPACE -it $database -- psql -U postgres postgres -c 'CREATE DATABASE '"$DATABASE_NAME"' OWNER '"$DATABASE_USER"';'
microk8s kubectl exec -n $KUBERNETES_NAMESPACE -it $database -- psql -U postgres $DATABASE_NAME -c 'CREATE EXTENSION postgis;'



echo "Starting remaining services"

microk8s kubectl apply -R -f workdir/geoserver-cloud

tempwebui=`microk8s kubectl get pod -n $KUBERNETES_NAMESPACE -l component=webui --no-headers`
webui=( $tempwebui )
webuistatus=${webui[2]}

while [ "$webuistatus" != "Running" ]
do
    tempwebui=`microk8s kubectl get pod -n $KUBERNETES_NAMESPACE -l component=webui --no-headers`
    webui=( $tempwebui )
    webuistatus=${webui[2]}
done

echo "Done."

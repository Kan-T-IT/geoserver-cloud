#!/bin/bash
source .env

echo "Applying .env config"

echo "Copy templates dir"

cp -r ./templates workdir

echo "Check environment variable"
echo "--------------------------------------"

: "${KUBERNETES_SITE_URL:?Variable not defined in .env}"
: "${KUBERNETES_NODE_NAME:?Variable not defined in .env}"
: "${KUBERNETES_VOL_DIR:?Variable not defined in .env}"
: "${CLUSTER_ISSUER_NAME:?Variable not defined in .env}"
: "${KUBERNETES_NAMESPACE:?Variable not defined in .env}"
: "${ACL_PASSWORD:?Variable not defined in .env}"
: "${GEOSERVER_PASSWORD:?Variable not defined in .env}"
: "${SERVER_PUBLIC_IP:?Variable not defined in .env}"

echo "Remplace variable in the files"
echo "--------------------------------------"

grep -rl KUBERNETES-SITE-URL ./workdir | xargs sed -i 's|KUBERNETES-SITE-URL|'"$KUBERNETES_SITE_URL"'|g'
grep -rl KUBERNETES-NODE-NAME ./workdir | xargs sed -i 's|KUBERNETES-NODE-NAME|'"$KUBERNETES_NODE_NAME"'|g'
grep -rl KUBERNETES-VOL-DIR ./workdir | xargs sed -i 's|KUBERNETES-VOL-DIR|'"$KUBERNETES_VOL_DIR"'|g'
grep -rl CLUSTER-ISSUER-NAME ./workdir | xargs sed -i 's|CLUSTER-ISSUER-NAME|'"$CLUSTER_ISSUER_NAME"'|g'
grep -rl KUBERNETES-NAMESPACE ./workdir | xargs sed -i 's|KUBERNETES-NAMESPACE|'"$KUBERNETES_NAMESPACE"'|g'
grep -rl ACL-PASSWORD ./workdir | xargs sed -i 's|ACL-PASSWORD|'"$ACL_PASSWORD"'|g'
grep -rl GEOSERVER-PASSWORD ./workdir | xargs sed -i 's|GEOSERVER-PASSWORD|'"$GEOSERVER_PASSWORD"'|g'
sed -i 's|SERVER-PUBLIC-IP|'"$SERVER_PUBLIC_IP"'|g' ./workdir/configs/metallb-configmap.yaml 

echo "create volume dir"
echo "--------------------------------------"

mkdir -p $KUBERNETES_VOL_DIR/dbdata/
mkdir -p $KUBERNETES_VOL_DIR/geowebcache-data/
mkdir -p $KUBERNETES_VOL_DIR/rabbitmq-data/

echo "Create namespace"
echo "--------------------------------------"

microk8s kubectl create namespace $KUBERNETES_NAMESPACE


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
# If not exist role, creation
microk8s kubectl exec -n $KUBERNETES_NAMESPACE -it $database -- psql -U postgres postgres -c "DO \$\$ BEGIN IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = 'pgconfig') THEN CREATE ROLE pgconfig WITH PASSWORD '"$ACL_PASSWORD"'; END IF; END \$\$;"
microk8s kubectl exec -n $KUBERNETES_NAMESPACE -it $database -- psql -U postgres postgres -c 'ALTER ROLE pgconfig WITH login;'
microk8s kubectl exec -n $KUBERNETES_NAMESPACE -it $database -- psql -U postgres postgres -c 'ALTER ROLE pgconfig WITH superuser;'

# If not exist database, creation
#microk8s kubectl exec -n $KUBERNETES_NAMESPACE -it $database -- psql -U postgres postgres -c "SELECT 'CREATE DATABASE pgconfig OWNER pgconfig' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'pgconfig')\\gexec"
microk8s kubectl exec -n $KUBERNETES_NAMESPACE -i $database -- \
psql -U postgres postgres <<EOF
SELECT 'CREATE DATABASE pgconfig OWNER pgconfig'
WHERE NOT EXISTS (
  SELECT FROM pg_database WHERE datname = 'pgconfig'
)\gexec
EOF

microk8s kubectl exec -n $KUBERNETES_NAMESPACE -it $database -- psql -U postgres pgconfig -c 'CREATE EXTENSION IF NOT EXISTS postgis;'

# repeat for database user
microk8s kubectl exec -n $KUBERNETES_NAMESPACE -it $database -- psql -U postgres postgres -c "DO \$\$ BEGIN IF NOT EXISTS (SELECT FROM pg_roles WHERE rolname = '"$DATABASE_USER"') THEN CREATE ROLE "$DATABASE_USER" WITH PASSWORD '"$DATABASE_PASS"'; END IF; END \$\$;"
microk8s kubectl exec -n $KUBERNETES_NAMESPACE -it $database -- psql -U postgres postgres -c 'ALTER ROLE '"$DATABASE_USER"' WITH login;'
microk8s kubectl exec -n $KUBERNETES_NAMESPACE -it $database -- psql -U postgres postgres -c 'ALTER ROLE '"$DATABASE_USER"' WITH superuser;'

#microk8s kubectl exec -n $KUBERNETES_NAMESPACE -it $database -- psql -U postgres postgres -c "SELECT 'CREATE DATABASE "$DATABASE_NAME" OWNER "$DATABASE_USER"' WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = '"$DATABASE_NAME"')\\gexec"

microk8s kubectl exec -n "$KUBERNETES_NAMESPACE" -i "$database" -- \
psql -U postgres postgres <<EOF
SELECT 'CREATE DATABASE $DATABASE_NAME OWNER $DATABASE_USER'
WHERE NOT EXISTS (
  SELECT FROM pg_database WHERE datname = '$DATABASE_NAME'
)\gexec
EOF

microk8s kubectl exec -n $KUBERNETES_NAMESPACE -it $database -- psql -U postgres $DATABASE_NAME -c 'CREATE EXTENSION IF NOT EXISTS postgis;'



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

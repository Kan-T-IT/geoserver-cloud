
# Kubernete fo GeoServer Cloud

Quick instructions for deploying GeoServer Cloud.

## Deployment

Docker images for all the services are available on DockerHub.

You can find production-suitable deployment files in the folders:
* [microk8s](microk8s): for deployment on Micro K8S
* [eks](eks): for deployment on AWS


For the deployment of GeoServer Cloud we can deploy it on different Kubernete platforms, here are the details of the deployment on MickoK8S 

## Deployment on MicroK8S

### Pre Requisites

* MicroK8S.
    * Ingress module.
    * DNS module.
    * Cert-manager module.


1. Use snap to install microk8s
```bash
sudo snap install microk8s --classic
```

2. Enable necesary micro8s modules

```bash
microk8s enable ingress
microk8s enable cert-manager
```

3. Create certmanager config to enable letsencrypt using your own email
```bash
microk8s kubectl apply -f - <<EOF
---
apiVersion: cert-manager.io/v1
kind: ClusterIssuer
metadata:
  name: letsencrypt
spec:
  acme:
    email: YOUREMAIL@DOMAIN.com
    server: https://acme-v02.api.letsencrypt.org/directory
    privateKeySecretRef:
      # Secret resource that will be used to store the account's private key.
      name: letsencrypt-account-key
    # Add a single challenge solver, HTTP01 using nginx
    solvers:
    - http01:
        ingress:
          class: public
EOF
```

4. Clone this repository

It clone repository


```bash
cd geoserver-cloud/kubernete
```

5. Access to directory for microk8s

```bash
cd microk8s
```

6. Edit all fields in .env file with the necesary information.
```env
KUBERNETES_SITE_URL=GEONODE_CLOUD_FINAL_URL    # i.e.: cloud.mygeonode.com
KUBERNETES_NODE_NAME=YOUR_CLUSTER_NAME_NAME    # usually host machine name
KUBERNETES_VOL_DIR=YOUR_DESIRED_LOCATION       # this path shold exist
CLUSTER_ISSUER_NAME=YOUR_CLUSTER_ISSUER_NAME   # created earlier in this guide
SERVER_PUBLIC_IP=YOU.RPU.BLI.CIP               # the public ipv4 of the server                 
GEOSERVER_PASSWORD=geoserver                   # password for geoserver admin user
```

7. Run `./install.sh` and enjoy.


---

## Deployment on EKS

### Pre requisites


Ensure that the EKS cluster is up and running and configured with the following:

1. **OIDC Provider and IAM**: Configure the OIDC provider for the EKS cluster.
2. **IAM Service Account for AWS Load Balancer Controller**: Create the IAM service account and attach the necessary policies.
3. **Necessary Addons**: Install AWS Load Balancer Controller and EBS CSI Driver.


### Deploy of Kubernetes Resources


To deploy the necessary resources on EKS, follow this order:

- **Cluster and StorageClass**
  - `cluster.yaml` in `clusterEksctl` (if the cluster is not already created).
  - `local-storageclass.yaml` in `configs/storageclass` (to set up the StorageClass before creating volumes).

- **Database**
  - ConfigMap: `gndatabase-configmap.yaml` in `database/configmaps`.
  - PVC: `dbdata-pvc.yaml` in `database/volumes`.
  - Deployment: `gndatabase-deployment.yaml` in `database/deployments`.
  - Service: `gndatabase-service.yaml` in `database/services`.


- **gs-cloud Components**
  - ConfigMaps in `gs-cloud/configmaps` (to have all configurations ready).
  - PVCs: `geowebcache-data-persistentvolumeclaim.yaml` and `rabbitmq-data-persistentvolumeclaim.yaml` in `gs-cloud/volumes`.
  - Deployments: Deploy `acl`, `gateway`, `gwc`, `rabbitmq`, `rest`, `wcs`, `webui`, `wfs`, and `wms` in `gs-cloud/deployments`.
  - Services in `gs-cloud/services`.

- **Ingress**
  - Finally, apply `geonode-ingress.yaml` in `configs/ingress` to expose services to the outside.


After following these steps, verify the status of your pods and services.


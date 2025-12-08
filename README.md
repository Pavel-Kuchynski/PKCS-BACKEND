## PKCS-DS
This service is responsible for working with MongoDb database.

### MongoDB Deployment
To run this application, you will need to set up the required infrastructure.

#### Prerequisites
* A running Kubernetes cluster.
* `kubectl` command-line tool configured to communicate with your cluster.

1. Create the required Kubernetes namespace for MongoDB:
   ```shell
   kubectl apply -f mongo-namespace.yaml
2. Populate sensitive information in Kubernetes secrets:
    ```shell
    kubectl apply -f mongo-secret.yaml
3. Deploy MongoDB using the provided StatefulSet configuration:
    ```shell
    kubectl apply -f mongo-pvc.yaml
    kubectl apply -f mongo-deployment.yaml
    kubectl apply -f mongo-service.yaml

### PKCS-DS Backend Deployment
To deploy the PKCS backend service, follow these steps:

1. Run the deployment configuration for the PKCS backend service:
   ```shell
   kubectl apply -f pkcs-ds-deployment.yaml
2. Expose the PKCS backend service:
   ```shell
   kubectl apply -f pkcs-ds-service.yaml

### Verification
To verify that the deployments were successful, you can check the status of the pods in the respective
namespaces:

1. verify that the deployments were successful, you can check the status of the pods in the respective
    ```shell
    minikube kubectl -- get pods -A
2. verify that the services are running and accessible:
    ```shell
    minikube kubectl -- get svc -A

3. Access the PKCS backend service:
    ```shell
    minikube service pkcs-ds-service

## PKCS-BACKEND
    This service is responcibel to work with frontend application.
### PKCS-BACKEND Deployment
To deploy the PKCS backend service, follow these steps:
1. Populate sensitive information in Kubernetes secrets:
    ```shell
   kubectl apply -f pkcs-core-secret.yaml
2. Run the deployment configuration for the PKCS backend service:
   ```shell
   kubectl apply -f pkcs-backend-deployment.yaml
3. Expose the PKCS backend service:
   ```shell
    kubectl apply -f pkcs-backend-service.yaml

### Create new topics in Kafka
    ```shell
    kubectl -n kafka exec -it <kafka-pod-name> -- \
    /opt/kafka/bin/kafka-topics.sh --create \
      --bootstrap-server localhost:9092 \
      --replication-factor 1 \
      --partitions 4 \
      --topic game.saved

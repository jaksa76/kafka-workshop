set NAMESPACE=kafka-workshop

echo IMPORTANT: make sure you have access to a kubernetes cluster
kubectl get pods -A

echo
echo setting up kafka
helm repo add bitnami https://charts.bitnami.com/bitnami
helm repo update
helm install workshop bitnami/kafka -n kafka --create-namespace -f kafka-values.yaml

echo
echo setting up kowl (kafka monitoring)
kubectl run kowl --image quay.io/cloudhut/kowl:master -n kafka --env="KAFKA_BROKERS=workshop-kafka.kafka.svc.cluster.local:9092"
kubectl expose pod kowl -n kafka --port=8080

helm repo add metrics-server https://kubernetes-sigs.github.io/metrics-server/
helm repo update
helm upgrade --install metrics-server metrics-server/metrics-server --namespace kube-system

@REM echo
@REM echo setting up chaos mesh
@REM helm repo add chaos-mesh https://charts.chaos-mesh.org
@REM helm install chaos-mesh chaos-mesh/chaos-mesh -n=chaos-testing
@REM helm upgrade chaos-mesh chaos-mesh/chaos-mesh --namespace=chaos-testing --set dashboard.securityMode=false

echo starting kafka-client pod
kubectl run kafka-client --image docker.io/bitnami/kafka:2.8.0-debian-10-r30 -n kafka --command -- sleep infinity


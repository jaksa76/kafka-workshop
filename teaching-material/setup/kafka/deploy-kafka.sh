#!/bin/bash

NAMESPACE=kafka-workshop

echo IMPORTANT: make sure you have access to a kubernetes cluster
kubectl get pods -A

echo
echo setting up kafka
helm repo add bitnami https://charts.bitnami.com/bitnami
helm install workshop bitnami/kafka -n kafka --create-namespace -f kafka-values.yaml

echo
echo setting up kowl (kafka monitoring)
kubectl run kowl --restart='Never' --image quay.io/cloudhut/kowl:master -n kafka --env="KAFKA_BROKERS=workshop-kafka.kafka.svc.cluster.local:9092"
kubectl expose pod kowl -n kafka --port=8080

echo
echo setting up chaos mesh
helm repo add chaos-mesh https://charts.chaos-mesh.org
helm install chaos-mesh chaos-mesh/chaos-mesh -n=chaos-testing
helm upgrade chaos-mesh chaos-mesh/chaos-mesh --namespace=chaos-testing --set dashboard.securityMode=false

echo starting kafka-client pod
kubectl run kafka-client --restart='Never' --image docker.io/bitnami/kafka:2.8.0-debian-10-r30 -n kafka --command -- sleep infinity

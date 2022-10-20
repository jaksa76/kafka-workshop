helm install dev-machines . --wait
kubectl get pods -o name | foreach { kubectl exec $_ -- git clone https://github.com/jaksa76/kafka-workshop.git /root/kafka-workshop }
kubectl get svc
kubectl get pods -o name | foreach { echo $_; kubectl exec $_ -- cat /root/.config/code-server/config.yaml }

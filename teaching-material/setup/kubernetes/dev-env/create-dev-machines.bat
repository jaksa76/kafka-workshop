helm install dev-machines .
kubernetes get svc
kubectl get pods -o name | foreach { echo $_; kubectl exec $_ -- cat /root/.config/code-server/config.yaml }

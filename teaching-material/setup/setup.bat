eksctl create cluster --name kafka-workshop
aws eks update-kubeconfig --name kafka-workshop

cd kafka
./deploy-kafka.bat
cd ..

cd kubernetes/dev-env
./create-dev-machines.bat
cd ../..

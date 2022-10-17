# Prerequisites

Make sure you have the following installed:
- aws cli tool
- eksctl
- kubectl
- helm
and that aws cli is configured (run `aws configure`)

# Setup

1. Edit the teaching-material\setup\kubernetes\dev-env\values.yaml to specify the attendees.
2. Run the setup.bat in teaching-material\setup\

This will create an EKS cluster and deploy 3 kafka nodes on it. 
In addition it will create a pod with vs-code server for each user.
It will also print the credentials to access the vscode remote UI.

# Tear down

Make sure you delete all the kubernetes resources and tear down the eks cluster when the workshop is complete.
#!/bin/bash
# Install PostgreSQL in the service-portfolio namespace via Bitnami Helm chart
# Run from a machine with kubectl access to the cluster

set -euo pipefail

echo "=== Service Portfolio - PostgreSQL Setup ==="

# 1. Create namespace
echo "Creating namespace..."
kubectl apply -f 01-api-dev/00-namespace.yaml

# 2. Install PostgreSQL via Helm
echo "Installing PostgreSQL..."
helm install postgresql bitnami/postgresql -n service-portfolio \
  --set auth.username=serviceportfolio \
  --set auth.password=<YOUR_PASSWORD> \
  --set auth.database=serviceportfolio \
  --set primary.persistence.storageClass=longhorn \
  --set primary.persistence.size=10Gi \
  --set primary.resources.requests.cpu=100m \
  --set primary.resources.requests.memory=256Mi \
  --set primary.resources.limits.cpu=500m \
  --set primary.resources.limits.memory=512Mi

echo ""
echo "PostgreSQL installed."
echo "Internal URL: jdbc:postgresql://postgresql.service-portfolio.svc.cluster.local:5432/serviceportfolio"
echo ""
echo "=== Next steps ==="
echo "1. Fill in real values in k8s/01-api-dev/01-secret.yaml and k8s/02-api-prod/01-secret.yaml"
echo "2. Apply secrets:  kubectl apply -f k8s/01-api-dev/01-secret.yaml"
echo "3. Apply dev:      kubectl apply -f k8s/01-api-dev/"
echo "4. Apply prod:     kubectl apply -f k8s/02-api-prod/"

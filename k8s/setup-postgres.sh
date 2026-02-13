#!/bin/bash
# Instalar PostgreSQL en el namespace service-portfolio via Bitnami Helm chart
# Ejecutar desde una maquina con acceso al cluster (kubectl configurado)

# 1. Crear namespace
kubectl apply -f namespace.yml

# 2. Instalar PostgreSQL
helm install postgresql bitnami/postgresql -n service-portfolio \
  --set auth.username=serviceportfolio \
  --set auth.password=REPLACE_ME_WITH_SECURE_PASSWORD \
  --set auth.database=serviceportfolio \
  --set primary.persistence.storageClass=longhorn \
  --set primary.persistence.size=10Gi \
  --set primary.resources.requests.cpu=100m \
  --set primary.resources.requests.memory=256Mi \
  --set primary.resources.limits.cpu=500m \
  --set primary.resources.limits.memory=512Mi

echo "PostgreSQL instalado. URL interna: jdbc:postgresql://postgresql.service-portfolio.svc.cluster.local:5432/serviceportfolio"

# 3. Aplicar secrets (editar secrets.yml primero con valores reales)
# kubectl apply -f secrets.yml

# 4. Desplegar la app
# kubectl apply -f deployment.yml
# kubectl apply -f service.yml
# kubectl apply -f ingress.yml

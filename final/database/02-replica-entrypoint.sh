#!/bin/bash
set -e

# Variable para el password de replicación que usará pg_basebackup
export PGPASSWORD=$POSTGRES_REPLICATION_PASSWORD

echo "Iniciando configuración de réplica..."

# 1. Esperar a que el primario esté disponible y aceptando conexiones
until pg_isready -h postgres-primary -p 5432 -U admin; do
  echo "Esperando al nodo primario (postgres-primary)..."
  sleep 2
done

# 2. Verificar si ya existen datos en el directorio de PostgreSQL
if [ -z "$(ls -A $PGDATA)" ]; then
    echo "Directorio de datos vacío. Iniciando pg_basebackup desde el primario..."
    
    # pg_basebackup:
    # -h: host primario
    # -D: directorio destino
    # -U: usuario de replicación
    # -vP: verbose y progreso
    # -R: crea automáticamente el archivo signal y la configuración de conexión al primario
    pg_basebackup -h postgres-primary -D $PGDATA -U $POSTGRES_REPLICATION_USER -vP -R
    
    echo "Backup base completado con éxito."
    
    # Ajustar permisos (PostgreSQL es estricto con los permisos de la carpeta de datos)
    chmod 700 $PGDATA
else
    echo "El directorio de datos ya contiene información. Omitiendo pg_basebackup."
fi

# 3. Ejecutar el entrypoint original de la imagen oficial de PostgreSQL
echo "Lanzando PostgreSQL en modo réplica..."
exec docker-entrypoint.sh postgres

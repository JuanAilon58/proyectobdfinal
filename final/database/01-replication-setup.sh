#!/bin/bash
set -e

echo "Configurando replicación en el nodo primario..."

# 1. Configurar pg_hba.conf para permitir conexiones de replicación
# Permitimos replicación desde cualquier host en la red interna de Docker usando md5
echo "host replication ${POSTGRES_REPLICATION_USER} 0.0.0.0/0 md5" >> "$PGDATA/pg_hba.conf"

# 2. Ajustar parámetros en postgresql.conf
# Usamos cat para añadir las configuraciones necesarias al final del archivo
cat >> "$PGDATA/postgresql.conf" <<EOF
wal_level = replica
max_wal_senders = 10
max_replication_slots = 10
hot_standby = on
archive_mode = on
archive_command = 'test ! -f /var/lib/postgresql/data/archive/%f && cp %p /var/lib/postgresql/data/archive/%f'
EOF

# Crear directorio de archivado si no existe
mkdir -p "$PGDATA/archive"
chown postgres:postgres "$PGDATA/archive"

echo "Configuración de replicación completada."

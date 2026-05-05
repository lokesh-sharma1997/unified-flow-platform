#!/bin/bash
set -e

for db in ufp_gateway ufp_workflow ufp_pipeline ufp_mlops ufp_agent ufp_governance; do
  psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE DATABASE $db;
    GRANT ALL PRIVILEGES ON DATABASE $db TO $POSTGRES_USER;
EOSQL
  echo "Created database: $db"
done

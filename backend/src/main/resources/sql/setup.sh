#!/bin/bash

# UPDATE THIS PATH to match your installed version
PSQL_PATH="/c/Program Files/PostgreSQL/17/bin/psql.exe"

DB_NAME="banking_system"
DB_USER="user_test"

# Fix permissions
"$PSQL_PATH" -U postgres -d $DB_NAME -c "ALTER SYSTEM SET default_transaction_read_only = off;"
"$PSQL_PATH" -U postgres -d $DB_NAME -c "SELECT pg_reload_conf();"
"$PSQL_PATH" -U postgres -d $DB_NAME -c "GRANT ALL ON SCHEMA public TO $DB_USER;"
"$PSQL_PATH" -U postgres -d $DB_NAME -c "ALTER SCHEMA public OWNER TO $DB_USER;"

# Run scripts
"$PSQL_PATH" -U $DB_USER -d $DB_NAME -f createUsersTable.sql
"$PSQL_PATH" -U $DB_USER -d $DB_NAME -f createAccountsTable.sql
"$PSQL_PATH" -U $DB_USER -d $DB_NAME -f createUserSettingsTable.sql
"$PSQL_PATH" -U $DB_USER -d $DB_NAME -f createAuditLogsTable.sql
"$PSQL_PATH" -U $DB_USER -d $DB_NAME -f createExchangeRatesTable.sql
"$PSQL_PATH" -U $DB_USER -d $DB_NAME -f createTransactionsTable.sql
"$PSQL_PATH" -U $DB_USER -d $DB_NAME -f insertExchangeRates.sql
"$PSQL_PATH" -U $DB_USER -d $DB_NAME -f permissions.sql
#!/usr/bin/env sh
set -eu

MYSQL_HOST="${MYSQL_HOST:-127.0.0.1}"
MYSQL_PORT="${MYSQL_PORT:-3306}"
MYSQL_USER="${MYSQL_USER:-root}"
MYSQL_PASSWORD="${MYSQL_PASSWORD:-verysecret}"
MYSQL_DATABASE="${MYSQL_DATABASE:-alphaseries}"
SQL_FILE="${1:-}"

mysql_cmd() {
  mariadb -h "$MYSQL_HOST" -P "$MYSQL_PORT" -u "$MYSQL_USER" "-p$MYSQL_PASSWORD" "$@"
}

mysql_cmd -e "CREATE DATABASE IF NOT EXISTS \`$MYSQL_DATABASE\` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

if [ -n "$SQL_FILE" ]; then
  if [ ! -f "$SQL_FILE" ]; then
    echo "SQL file not found: $SQL_FILE" >&2
    exit 1
  fi
  mysql_cmd "$MYSQL_DATABASE" < "$SQL_FILE"
  echo "Imported $SQL_FILE into $MYSQL_DATABASE"
else
  echo "Created database $MYSQL_DATABASE. Pass a matching AlphaSeries .sql file to import schema/data."
fi

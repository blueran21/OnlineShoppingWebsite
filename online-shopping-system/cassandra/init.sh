#!/bin/sh
set -e

echo 'Waiting for Cassandra CQL service...'
# 等 CQL 可用（最多 ~3 分钟）
for n in 1 2 3 4 5 6 7 8 9 10 \
         11 12 13 14 15 16 17 18 19 20 \
         21 22 23 24 25 26 27 28 29 30 \
         31 32 33 34 35 36 37 38 39 40 \
         41 42 43 44 45 46 47 48 49 50 \
         51 52 53 54 55 56 57 58 59 60; do
  if cqlsh cassandra1 9042 -e "SELECT release_version FROM system.local" >/dev/null 2>&1; then
    echo 'CQL is ready'; break
  fi
  echo "still waiting... $n"; sleep 3
done

echo 'Applying schema from /init/init.cql...'
cqlsh cassandra1 9042 -f /init/init.cql

echo 'Verifying keyspace...'
if cqlsh cassandra1 9042 -e "DESCRIBE KEYSPACE orders_keyspace" >/dev/null 2>&1; then
  echo 'orders_keyspace exists'
  exit 0
else
  echo 'orders_keyspace verification failed'
  exit 2
fi

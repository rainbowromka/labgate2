#!/usr/bin/env bash
if [ ! -d /home/perminovrp/projects/labgatej_dev/database/tmp ]; then
  mkdir -p /home/perminovrp/projects/labgatej_dev/database/tmp;
fi
/usr/lib/postgresql/9.5/bin/postgres -D /home/perminovrp/projects/labgatej_dev/database/db -p 5543 -i
# Перед тем как запустить базу данных, убедитесь что в postgresql.conf прописан
# путь к unix_socket_directories
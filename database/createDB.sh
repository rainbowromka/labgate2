#!/usr/bin/env bash
if [ ! -d /home/perminovrp/projects/labgatej_dev/database/db ]; then
  mkdir -p /home/perminovrp/projects/labgatej_dev/database/db;
fi
/usr/lib/postgresql/9.5/bin/initdb --encoding UTF8 -D /home/perminovrp/projects/labgatej_dev/database/db

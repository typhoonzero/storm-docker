#!/bin/sh
if [ ! -z $NIMBUS_SERVICE_HOST ]; then
  echo "found nimbus service: $NIMBUS_SERVICE_HOST"
else
  echo "no nimbus host found in service env"
  exit 1
fi

/storm-nimbus-daemon $@ --nimbus.host $NIMBUS_SERVICE_HOST

exec bin/storm ui

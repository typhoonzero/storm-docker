#!/bin/sh

/storm-nimbus-daemon $@

exec bin/storm supervisor

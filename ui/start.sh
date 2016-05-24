#!/bin/sh

/storm-nimbus-daemon $2

exec bin/storm ui

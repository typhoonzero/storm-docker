#coding: utf8

import time
import os
import datetime

def kill_topology_yestoday(topo_name):
    yesterday = (datetime.date.today() - datetime.timedelta(days=1)).strftime('%Y-%m-%d')
    yesterday_topology = '%s%s'%(topo_name, yesterday)
    cmd = "PATH=/opt/java/bin /opt/sys/storm/bin/storm kill -w 5 %s"%yesterday_topology
    os.system(cmd)

kill_topology_yestoday("stormdemo_")

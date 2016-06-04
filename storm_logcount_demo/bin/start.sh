source /etc/profile
cd /opt/sys/topology/stormdemo
/opt/sys/apache-storm-0.9.4/bin/storm jar stormdemo-1.0-jar-with-dependencies.jar storm.real_time_info.StromDemo 1>/tmp/stormdemo.log 2>/tmp/stormdemo.err


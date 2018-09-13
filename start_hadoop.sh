# Start hadoop
$HADOOP_HOME/sbin/hadoop-daemon.sh start namenode
$HADOOP_HOME/sbin/hadoop-daemon.sh start datanode

# Start the ssh server
sudo systemctl start sshd
$HBASE_HOME/bin/start-hbase.sh

FROM ha-hdfs:0.1
#FROM centos:7
#FROM ubuntu:latest

# https://issues.apache.org/jira/browse/HDFS-9427
HEALTHCHECK CMD curl -f http://localhost:9870/ || exit 1

ARG CLUSTER_NAME
ARG ACTIVE_NAME_NODE
ARG STANDBY_NAME_NODE
ARG JOURNAL_NODE_1
ARG JOURNAL_NODE_2
ARG JOURNAL_NODE_3
ARG ZK_NODE_1
ARG ZK_NODE_2
ARG ZK_NODE_3

ENV HDFS_CONF_dfs_namenode_name_dir=/hadoop/data/namenode
RUN mkdir -p /hadoop/data/namenode
#RUN chown -R hadoop /hadoop/data/namenode
#VOLUME /hadoop/dfs/name

ADD /conf/conf-setup.sh /conf-setup.sh
ADD /namenode/run.sh /run.sh
RUN chmod a+x /conf-setup.sh
RUN chmod a+x /run.sh

RUN /conf-setup.sh

#ENV USER=root

EXPOSE 9870
EXPOSE 8020
EXPOSE 9000

#ENV HADOOP_PREFIX=/hadoop/hadoop-3.2.0
#ENV HADOOP_CONF_DIR=/hadoop/hadoop-3.2.0/etc/hadoop
#ENV JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk/

CMD ["/run.sh"]
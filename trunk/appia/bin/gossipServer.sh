#!/bin/bash

#java -classpath $CLASSPATH org.continuent.appia.demo.ExecuteXML ./config/gossipServer.xml

java -classpath $CLASSPATH org.continuent.appia.gossip.GossipServer "$@"
#!/bin/bash

#java net.sf.appia.demo.ExecuteXML ./config/gossipServer.xml

java -cp .:classes:lib/log4j-1.2.14.jar net.sf.appia.gossip.GossipServer "$@"
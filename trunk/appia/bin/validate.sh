#!/bin/bash

echo "Starting group member 1"
java -classpath $CLASSPATH org.continuent.appia.demo.ExecuteXML ./config/perf.xml &
echo "Starting group member 2"
java -classpath $CLASSPATH org.continuent.appia.demo.ExecuteXML ./config/perf.xml
echo "Test finished"

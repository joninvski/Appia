#!/bin/bash

echo "Starting group member 1"
java org.continuent.appia.demo.ExecuteXML ./config/demo/perf.xml &
echo "Starting group member 2"
java org.continuent.appia.demo.ExecuteXML ./config/demo/perf.xml
echo "Test finished"

#!/bin/bash

echo "Starting group member 1"
java net.sf.appia.demo.ExecuteXML ./config/demo/perf.xml &
echo "Starting group member 2"
java net.sf.appia.demo.ExecuteXML ./config/demo/perf.xml
echo "Test finished"

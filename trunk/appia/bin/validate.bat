@echo off

echo Starting group member 1
start java net.sf.appia.demo.ExecuteXML config\demo\perf.xml
echo Starting group member 2
start java net.sf.appia.demo.ExecuteXML config\demo\perf.xml
echo Test finished

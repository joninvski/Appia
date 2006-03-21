This directory contains the main function of all the available
demonstrations.

To use them, the CLASSPATH environment variable must point to this
directory and to the directory where the Appia kernel and protocols
bytecode is available. For a typical configuration type:

bash> export CLASSPATH=.:..

p2pUdp:   Point-to-point communication over UDP sockets. Text based.
p2pTcp:   Point-to-point communication over TCP sockets. Text based.
Appl:     Group communication protocols. Requires the use of a Gossip
server. Text based.
ApplMult: Group communication protocols allowing multiple endpoints
over one group endpoint. Text based.

The classes are well commented and can be used as "learn by example"
tutorials. 

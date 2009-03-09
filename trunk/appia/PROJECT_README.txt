=== Global Notes ===
    1. Stub Events are exchanged between clients->server and vice-versa
    2. Proxy Events are exchanged between servers.
    3. Vs stands for View Synchrony
    4. When a server sends a proxy event, it always also sends it to itself too.

=== Protocol Rationale ===

Servers Servers create a group with View Synchrony which is the only methods for communication between servers. All communication between servers and client<->servers guarantee FIFO property.

Clients use the VsStubLayer, which transforms true Virtual Sinchrony commands and Group Sendable events into stub events to send to the servers. It also does the opposite, transforming messages sent from the server into true View Synchrony commands.

To handle the entering/leaving of clients, a server sends to all others servers an event stating this information. All servers then request their attached clients to block (NewClientProxyEvent/LeaveClientProxyEvent). When they have received a blockOk from all clients, the server sends to the VS channel a BlockOkProxy.
When a server receives BlockOks from all servers it checks if it is the leader of the view, and if such is the case, it sends a DecideProxy Event to all other servers with the information of the next view for the clients.
Note that all this happens at a group scale, meaning that a client leaving a view in a group does not impact in any way. The explanation omitted this fact for clarity reasons.

There are also two list. FutureDeadList and FutureLiveList. Whenever a server receives a NewClientProxyEvent or a LeaveClientProxyEvent it puts the client in the respective list. They serve as temporary information views, until the lider has all the conditions to decide.

To handle the enter/leave of servers, whenever a new view is received, they say to all their clients to shut up. When all their clients are shutten up they lock themselfs from accepting new clients or leaves and send through the VS channel their clients views through an UpdateProxyMessage.
When a server receives all UpdateProxyMessages and the node is the leader, it sends a UpdateDecideMessage to all servers.

=== File Descrition ===
src
`-- net
 `-- sf
   `-- appia
     `-- project
       |-- debug -> Debug tools used during the development of the project.
       `-- group -> Contains the files developed for the project
            |
            |-- client -> Classes used in the clients
            |-- exampleApp -> This is the application used in the clients. Only serves
            |                   to proof that the project work. Does not know it is
            |                   not on a true VS.
            |
            |-- event
            |   |-- proxy -> Events exchanged between servers
            |   `-- stub -> Events exchanged between the server and the clients
            |
            `-- server -> Classes used in the servers
config
`-- project
     |-- clients -> The Config files for the clients
     `-- servers -> The config files for the servers

=== Protocol definition ===

===== Client (implemented by class VsProxySession.java) =====

    #### Comming from the upper application ####
    upon receive GroupInit() do:
        sendToServer(groupInitStubEvent)

    upon receive BlockOk
        sendToServer(blockOkStubEvent)

    upon receive GroupSendableEvent do:
        sendToServer(groupSendableStubEvent) #It encapsulates the GSE

    upon receive LeaveEvent do:
        sendToServer(leaveStubEvent)

    #### Comming from the server #####
    upon receive ShutUpStubEvent() do:
        #It basically request the upper application to shut up
        sendToUpperApplication(blockOk, Direction.DOWN)

    upon receive viewStubEvent() do:
        sendToUpperApplication(view)

    upon receive groupSendableStubEvent() do:
        #It basically desencapsulates the GroupSendableEvent
        sendToUpperApplication(groupSendableEvent)

    ### Periodically ###
    upon delta time:
        sendToServer(pongEvent)

===== Server (implemented by class VsStubSession.java) =====

    ### Coming from the client ###
    upon receive groupInitStub() do:
        sendToAllServers(NewClientProxyEvent)

    upon receive leaveStubEvent() do:
        sendToAllServers(LeaveClientProxyEvent)

    upon receive GroupSendableEventStub() do:
        sendToAllServers(GrupSendableProxyEvent)

    upon receive BlockOkStubEvent() do:
        set_client_as_muted

        if(allMyClientsAreMuted)
            sendToAllServers(blockOkProxyEvent)

    upon receive PongEvent() do:
        putResetPongTimerClient() #The client is alive

    ### Coming from other servers ###
    upon receive groupSendableProxyEvent()  do:
        futureLiveClientList.add(leaveClient)
        sendToMyAttachedClients(encapsulatededGSEvent)

    upon receive NewClientProxyEvent() do:
        lock (controlMessages)
        if(iHaveClientsInThatGroup) do:
            sendToAllMyClients(shutUpStubEvent)

        else
            sendToAllClient(BlockOkProxyEvent) #Say all my clients are blocked

    upon receive LeaveClientProxyEvent() do:
        lock (controlMessages)
        futureDeadList.add(leaveClient)
        if(iHaveClientsInThatGroup) do:
            sendToAllMyClients(shutUpStubEvent)

        else
            sendToAllClient(BlockOkProxyEvent) #Say all my clients are blocked

    upon receive BlockOkProxyEvent() do:
        if(event.version == old)
            discard(event)

        store_the_server_as_having_all_clients_muted()

        if allServersHaveClientsMuted && iAmTheLeader
            myClientView.add(futureLiveList)
            myClientView.remove(futureDeadList)

            #I now have the view updated
            sendToAllServers(DecidedProxyEvent(myClientView))

    upon receive DecidedProxyEvent(newView) do:
        myClientView = newView #I update my view with what was decided by the leader

        #And I may update my temporary lists to reflect this new view TODO - I'm not doing this
        futureLiveList.removePresentIn(newView) #The ones already added I may remove
        futureDeadList.remove(newView) #The ones already added I may remove

        sendToMyAttachedClients(myClientView)

    ### Coming from the View
    upon receive BlockOk() do:
        lock (controlMessages)
        flushMode = true

        if(iHaveClientsAttached)
            askClientsToShutUp
        else
            blockOk.go(Direction.DOWN)

    upon receive View() do:
        update serverView
        serverAlreadyStableList.clear()

        sendToAllServers(UpdateProxy(myClientsView))

    upon receive UpdateProxyEvent(otherServerView)
        serverAlreadyStableList.add(servetThatSentMe)

        temporaryView = temporaryView.Merge(otherServerView)

        if(serverAlreadyStableList containsAll(serversAlive) && iAmTheLeader)
            recentlyDeadClients = myClientsView.notPresentIn(temporaryView)
            myClientsView = temporaryView
            sendToAllServers(updateDecideProxyEvent(myClientsView)) # I have decided

    upon receive UpdateDecideProxyEvent(viewDecidedByLeader)
        myClientsView = viewDecidedByLeader
        flushMode = false

        sendToMyClients(ViewStubEvent(myClientsView))
        unlock (controlMessages)

=== Configuration files ===
 ==== Client ====
    The clients config files can be found at:
        * config/project/clients

    The parameters are:
        <parameter name="serverhost">192.168.1.19</parameter> -> The host of the server to connect to
        <parameter name="serverport">4000</parameter> -> The port number of the server to connect to

        <parameter name="username">CLIENT_0</parameter> -> The name for the endpoint of the client
        <parameter name="localport">3000</parameter> -> The localport of the client

 ==== Server ====
    The clients config files can be found at:
        * config/project/server

    The parameters are:
            <parameter name="localport">4000</parameter> - The localport for the server to use. The server uses the defined port and the imediate next one also. Both must be available. For example, in this case 4000 and 4001 port numbers are used by a single server
            <parameter name="servername">MANEL</parameter> - The servername user by the server in the endpt.
            <parameter name="gossiphost">192.168.1.19</parameter> - The hostname of the gossip server
            <parameter name="gossipport">5000</parameter> - The port number of the gossip port

=== Problems ===
    * The synchronization is done in an conservative way. This ensures no errors but has a performance penalty cost. In the future there could be a refactorization to allow a more tuned synchronization.
    * The example application that was done only to test the project contains some bugs. Honestly I seize these bugs to get clients dying in unexpected times. (Note: The "real" project works well, it is only the example application that sometimes crashes)
    * With FIFO properties, the messages sometimes take a long time to be delivered. With a low discovery time, clients are easily considered dead.

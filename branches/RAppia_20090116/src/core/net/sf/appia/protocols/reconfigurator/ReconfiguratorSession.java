package net.sf.appia.protocols.reconfigurator;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import net.sf.appia.adaptationmanager.Action;
import net.sf.appia.adaptationmanager.ActionResponse;
import net.sf.appia.adaptationmanager.AdaptationEvent;
import net.sf.appia.adaptationmanager.AdaptationResponseEvent;
import net.sf.appia.adaptationmanager.events.ContextAnswerEvent;
import net.sf.appia.adaptationmanager.events.ContextQueryEvent;
import net.sf.appia.core.Appia;
import net.sf.appia.core.AppiaEventException;
import net.sf.appia.core.Channel;
import net.sf.appia.core.Direction;
import net.sf.appia.core.Event;
import net.sf.appia.core.Layer;
import net.sf.appia.core.Session;
import net.sf.appia.core.TimeProvider;
import net.sf.appia.core.events.channel.ChannelClose;
import net.sf.appia.core.events.channel.ChannelInit;
import net.sf.appia.core.events.reconfiguration.GetServiceStateEvent;
import net.sf.appia.core.events.reconfiguration.RecActionEvent;
import net.sf.appia.core.events.reconfiguration.RecActionResponseEvent;
import net.sf.appia.core.events.reconfiguration.SetServiceStateEvent;
import net.sf.appia.core.events.reconfiguration.SetValueEvent;
import net.sf.appia.core.events.reconfiguration.StartServiceEvent;
import net.sf.appia.core.events.reconfiguration.StopServiceEvent;
import net.sf.appia.protocols.common.RegisterSocketEvent;
import net.sf.appia.xml.interfaces.InitializableSession;
import net.sf.appia.xml.utils.SessionProperties;

/**
 * 
 * This class defines a ReconfiguratorSession
 * 
 * @author <a href="mailto:cfonseca@gsd.inesc-id.pt">Cristina Fonseca</a>
 * @version 1.0
 */
public class ReconfiguratorSession extends Session implements InitializableSession{

    private TimeProvider time;

    //canal que faz a interface com o nó
    private Channel channel;
    private InetSocketAddress local;
    private InetSocketAddress remote;
    private int localPort = -1;

    //canal de controlo ligado ao AManager
    private Channel channelM;
    private InetSocketAddress localM;
    private InetSocketAddress remoteM;
    private int localPortM = -1;
   
    private final Hashtable<String, Class> reconfigActions = new Hashtable();
    private HashMap<String, List<String>> actionsId = new HashMap<String, List<String>>();
    private HashMap<String, List<ActionResponse>> receivedResponses = new HashMap<String, List<ActionResponse>>();


    public ReconfiguratorSession(Layer layer) {
        super(layer);
        reconfigActions.put("setValue", SetValueEvent.class);
        reconfigActions.put("getServiceState", GetServiceStateEvent.class);
        reconfigActions.put("setServiceState", SetServiceStateEvent.class);
        reconfigActions.put("startService", StartServiceEvent.class);
        reconfigActions.put("stopService", StopServiceEvent.class);

    }

    public void init(SessionProperties params) {

        this.localPortM = Integer.parseInt(params.getProperty("localport"));
        final String remoteHost = params.getProperty("remotehost");
        final int remotePort = Integer.parseInt(params.getProperty("remoteport"));
        try {
            this.remoteM = 
                new InetSocketAddress(InetAddress.getByName(remoteHost),remotePort);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }


    /**
     * Initialization method to be used directly by a static 
     * initialization process
     * @param localPort the local port
     * @param remote the remote address
     */
    public void init(int localPort, InetSocketAddress remote){
        this.localPortM = localPort;
        this.remoteM = remote;
    }

    /**
     * Main event handler.
     * @param ev the event to handle.
     * 
     * @see net.sf.appia.core.Session#handle(net.sf.appia.core.Event)
     */
    public void handle(Event ev) {
        if (ev instanceof ChannelInit)
            handleChannelInit((ChannelInit) ev);
        else if (ev instanceof ChannelClose)
            handleChannelClose((ChannelClose) ev);
        else if (ev instanceof AdaptationEvent)
            handleAdaptationEvent((AdaptationEvent) ev);
        else if(ev instanceof RecActionResponseEvent)
            handleReconfigurationResponseEvent((RecActionResponseEvent) ev);
        else if(ev instanceof ContextAnswerEvent)
            handleStackCompositionEvent((ContextAnswerEvent) ev);
        else if (ev instanceof RegisterSocketEvent)
            handleRSE((RegisterSocketEvent) ev);
        else if(ev instanceof ContextQueryEvent)
            handleStackInfoRequestEvent((ContextQueryEvent) ev);
        else
            try {
                ev.go();
            } catch (AppiaEventException e) {
                e.printStackTrace();
            }
    }

    //método não usado por agora - em standby, é do monitor
    private void handleStackInfoRequestEvent(ContextQueryEvent ev) {
      
        String sessionId = ev.getMessage().popString();
        Collection<Session> c = Appia.getSessionList().values();
        
                
        Iterator<Session> it= c.iterator();
        
               
        while(it.hasNext()){
            Session s = (Session) it.next();
                    
            if(s.getId().equals(sessionId)){
                System.out.println("Encontrei a session!!!!!!!!!!!1");        
            }
        }
        
       
    }

    //método não usado por agora - em standby, é do monitor
    private void handleStackCompositionEvent(ContextAnswerEvent ev) {
      
        System.out.println("Recebi StackCompositionEvent na ReconfigurationSession");
        ev.setChannel(channelM);
        ev.setDir(Direction.DOWN);
        ev.setSourceSession(this);
       
        ev.source = localM;
        ev.dest = remoteM;

        List<String> channels = new ArrayList<String>();
        List<String> sessions = new ArrayList<String>();
        
        Iterator it = ev.getChannelList().entrySet().iterator();
        
        
        while (it.hasNext()) {
            Entry<String, Channel> o = (Entry<String, Channel>) it.next();
            String s = o.getValue().getChannelID();
            channels.add(s);
        }
        
        Iterator it2 = ev.getSessionList().entrySet().iterator();
        
        while (it2.hasNext()) {
            Entry<String, Session> o = (Entry<String, Session>) it2.next();
            String s = o.getValue().getId();
            sessions.add(s);
        }
        
        
        ev.getMessage().pushObject(channels);
        ev.getMessage().pushObject(sessions);
        
        
        try {
            ev.init();
            ev.go();
        } catch (AppiaEventException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        
    }

    /*
     * ChannelInit
     */
    private void handleChannelInit(ChannelInit init) {

        try {

            if(channel == null){
                channel = init.getChannel();
                time = channel.getTimeProvider();
                init.go();
            }
            else{
                channelM = init.getChannel();
                init.go();
                new RegisterSocketEvent(channelM,Direction.DOWN,this,localPortM).go();


            }

        } catch (AppiaEventException e) {
            e.printStackTrace();
        }       
    }

    /*
     * RegisterSocketEvent
     */
    private void handleRSE(RegisterSocketEvent event) {

        if(event.getDir() == Direction.UP){
            if(event.error){
                System.err.println("Error on the RegisterSocketEvent!!! " + event.getErrorDescription());
                System.exit(-1);
            }



            if(event.getChannel().equals(channelM)){
                localM = new InetSocketAddress(event.localHost,event.port);
            }else{
                local = new InetSocketAddress(event.localHost,event.port);
            }
        }

        try {
            event.go();
        } catch (AppiaEventException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /*
     * ChannelClose
     */
    private void handleChannelClose(ChannelClose close) {
        try {
            close.go();
        } catch (AppiaEventException e) {
            e.printStackTrace();
        }
    }

    
    

    /*
     * AdaptationEvent
     */
    private void handleAdaptationEvent(AdaptationEvent ae) {
     
        if (ae.getDir() == Direction.DOWN) {
            // Event is going DOWN
            System.out.println("[RECONFIGURATOR] AdaptationEvent : down");
        }
        else {
            // Event is going UP
            System.out.println("\n[RECONFIGURATOR] A receber AdaptationEvent : " + ae.getSeqNum());
            
            String seqNum = (String) ae.getMessage().getHeader("adaptationManagerSeqNum");
            List<Action> popObject = (List<Action>) ae.getMessage().popObject();
         //   System.out.println("Recebi AdatationEvent " + seqNum + " com uma lista de " + popObject.size() + " acções");

            //mete na lista o Id do AdatpationEvent recebido e a lista de Reconfigs events a ele associados
            insertIntoMap(seqNum, String.valueOf(popObject.size()));

            for(Action a: popObject){

                String actionId = a.getActionId();
                String actionName = a.getName();
                String actionObjectId = a.getObjectId();
                Class actionObjectType = a.getObjectType();
                Object[] actionParameters = a.getParameters();

                System.out.println("");
                System.out.println("[RECONFIGURATOR] A criar Action " +  actionId + " - " + actionName);

                //get the class for the new event                  
                Class eventType = reconfigActions.get(actionName);

                //reflection to construct the right event
                RecActionEvent reconfigEvent = null;
                try {
                    Class c = Class.forName(eventType.getName());
                    reconfigEvent = (RecActionEvent) c.newInstance();               

                } catch (ClassNotFoundException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                } catch (InstantiationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                reconfigEvent.setActionId(actionId);
                reconfigEvent.setReconfigId(String.valueOf(ae.getSeqNum()));
                reconfigEvent.setReconfigName(actionName);
                reconfigEvent.setChannel(ae.getChannel());
                reconfigEvent.setDir(Direction.UP);
                reconfigEvent.setSourceSession(ae.getSourceSession());

                if(reconfigEvent instanceof SetValueEvent){
                    ((SetValueEvent) reconfigEvent).setServiceType(0, ae.getChannel().getChannelID(),actionObjectType);
                    reconfigEvent.setParameters(actionParameters);
                }

                try {
                    reconfigEvent.init();
                    reconfigEvent.go();
                } catch (AppiaEventException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

            try {
                ae.go();
            } catch (AppiaEventException e) {
                e.printStackTrace();
            }



        }
    }

    private void handleReconfigurationResponseEvent(
            RecActionResponseEvent ev) {

        System.out.println("[RECONFIGURATOR] A receber RecActionResponseEvent : " + ev.getActionId() + " " + ev.getReturnType());

        //ver se já recebi todas as actions, senão adicionar ao AdaptationResponseEvent
        //  String managerRequestId = actionsId.
        String managerRequestId = searchIntoMap(ev.getActionId());
        ActionResponse ar = new ActionResponse(ev.getActionId(), ev.getReturnType());
        List<ActionResponse> resp = null;

        //add to receivedResponses list
        if(receivedResponses.containsKey(managerRequestId)){
            receivedResponses.remove(managerRequestId);
            
        }else{
            resp = new ArrayList<ActionResponse>();
            
        }
        resp.add(ar);
        receivedResponses.put(managerRequestId, resp);

        //se recebi todas as respostas
        if(actionsId.get(managerRequestId).size() == receivedResponses.get(managerRequestId).size()){
            System.out.println("[RECONFIGURATOR] A enviar AdaptationResponseResponseEvent : " + managerRequestId + "\n");

            //Criar o AdaptationResponseEvent
            AdaptationResponseEvent are = new AdaptationResponseEvent();
            are.setChannel(channelM);
            are.setDir(Direction.DOWN);        
            are.setSourceSession(this);
            are.setSeqNum(Integer.parseInt(managerRequestId));
            are.source = localM;
            are.dest = remoteM;
            are.getMessage().pushString(managerRequestId); 
            are.getMessage().pushObject(resp);

            try {
                are.init();
                are.go();
            } catch (AppiaEventException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

        }

        try {
            ev.go();
        } catch (AppiaEventException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }



    private void insertIntoMap(String actionId, String reconfigEventId){

        if ( ! actionsId.containsKey( actionId ) ) {
            List<String> list = new ArrayList<String>( );
            list.add(reconfigEventId);
            actionsId.put( actionId, list);
        }
        else {
            List<String> list = (List<String>) actionsId.get(actionId);
            list.add( reconfigEventId );
        }

    }

    //pressupõe que não há actions com Id repetido
    private String searchIntoMap(String reconfigId){
        java.util.Set<Entry<String, List<String>>> h = actionsId.entrySet();
        Iterator<Entry<String, List<String>>> it = h.iterator();

        while(it.hasNext()){
            Entry<String, List<String>> entry = it.next();
            List list = entry.getValue();
            if(list.contains(reconfigId))
                return entry.getKey();

        }

        return "-1";
    }

}

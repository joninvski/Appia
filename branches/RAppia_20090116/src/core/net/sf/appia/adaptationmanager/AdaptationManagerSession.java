package net.sf.appia.adaptationmanager;

import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

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
import net.sf.appia.core.message.Message;
import net.sf.appia.protocols.common.RegisterSocketEvent;
import net.sf.appia.test.xml.ecco.EccoLayer;
import net.sf.appia.xml.interfaces.InitializableSession;
import net.sf.appia.xml.utils.SessionProperties;

/**
 * 
 * This class defines an AdaptationManagerSession which is responsible for sending the AdaptationEvents 
 * to the reconfigurator and receive the responses. 
 * 
 * @author <a href="mailto:cfonseca@gsd.inesc-id.pt">Cristina Fonseca</a>
 * @version 1.0
 */
//manager de teste. Envia mensagens de reconfiguração para o reconfigurador quando se escreve algo na consola local.
public class AdaptationManagerSession extends Session implements InitializableSession {

    private Channel channel;
    private TimeProvider time;

    private InetSocketAddress local;
    private InetSocketAddress remote;
    private int localPort = -1;

    private AdaptationShell shell;
    private static int seqNum = 0;

    protected List<String> channelList;
    protected List<String> sessionList;

    //AdaptationMonitor frame;

    public AdaptationManagerSession(Layer layer) {
        super(layer);       
    }

    public void init(SessionProperties params) {

        this.localPort = Integer.parseInt(params.getProperty("localport"));
        final String remoteHost = params.getProperty("remotehost");
        final int remotePort = Integer.parseInt(params.getProperty("remoteport"));
        try {
            this.remote = 
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
        this.localPort = localPort;
        this.remote = remote;
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
        else if(ev instanceof AdaptationResponseEvent)
            handleAdaptationResponseEvent((AdaptationResponseEvent) ev);
        else if (ev instanceof RegisterSocketEvent)
            handleRSE((RegisterSocketEvent) ev);
        else if(ev instanceof ContextAnswerEvent)
            handleStackCompositionEvent((ContextAnswerEvent) ev);
        else if(ev instanceof ContextQueryEvent)
            handlestackInfoRequest((ContextQueryEvent) ev);
        else
            try {
                ev.go();
            } catch (AppiaEventException e) {
                e.printStackTrace();
            }
    }


    private void handlestackInfoRequest(ContextQueryEvent stackInfoRequestEvent) {

        try {
            ContextQueryEvent e = new ContextQueryEvent(channel, Direction.DOWN, this);
            e.dest = remote;
            e.source = local;
            e.getMessage().pushString(stackInfoRequestEvent.getId());

            e.init();
            e.go();

        } catch (AppiaEventException e) {
            e.printStackTrace();
        }

    }

    private void handleStackCompositionEvent(ContextAnswerEvent ev) {

        sessionList =  (List<String>) ev.getMessage().popObject();
        channelList =  (List<String>) ev.getMessage().popObject();

        System.out.println("SessionList " + sessionList.toString());
        System.out.println("ChannelList " + channelList.toString());

        try {
            ev.go();
        } catch (AppiaEventException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //final Thread t = ev.getChannel().getThreadFactory().newThread(frame);
        //t.setName("Monitor shell");
        //t.start();

        //Iterator<String> it = sessionList.iterator();
        //frame.printStack(it);


        //Envia um evento a pedir a composição da pilha ao nó
      /*  ContextQueryEvent e;
        try {
            e = new ContextQueryEvent(channel, Direction.DOWN, this);
            e.dest = remote;
            e.source = local;
            e.getMessage().pushString("ecco_s");

            e.init();
            e.go();
            System.out.println("ENVIEI STACK INFO REQUEST");
        } catch (AppiaEventException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
       */
    }

    /*
     * ChannelInit
     */
    private void handleChannelInit(ChannelInit init) {
        channel = init.getChannel();
        time = channel.getTimeProvider();
        try {
            init.go();
        } catch (AppiaEventException e) {
            e.printStackTrace();
        }

        /*
         * This event is used to register a socket on the layer that is used 
         * to interface Appia with sockets.
         */
        try {
            new RegisterSocketEvent(channel,Direction.DOWN,this,localPort).go();
        } catch (AppiaEventException e1) {
            e1.printStackTrace();
        }

        //Initializes the debug monitor
        /*  frame = new AdaptationMonitor(this,channel); 

        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setSize( 400, 500 ); // configura o tamanho do frame
        frame.setVisible( true ); // exibe o frame
         */

    }

    /*
     * RegisterSocketEvent
     */
    private void handleRSE(RegisterSocketEvent event) {
        if(event.error){
            System.err.println("Error on the RegisterSocketEvent!!!");
            System.exit(-1);
        }

        local = new InetSocketAddress(event.localHost,event.port);

        shell = new AdaptationShell(channel);
        final Thread t = event.getChannel().getThreadFactory().newThread(shell);
        t.setName("Rec shell");
        t.start();
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

            System.out.println("[AMANAGER] A enviar AdaptationEvent : " + seqNum);
            Action a = new Action("1", RecOperations.SET_VALUE.toString(), net.sf.appia.test.xml.ecco.EccoLayer.class, "21", new Object[]{"localPort",4001});
            
            ae.addAction(a);
            ae.setSeqNum(seqNum);
            ae.getMessage().pushObject(ae.getActions());
            ae.getMessage().addHeader("adaptationManagerSeqNum", String.valueOf(seqNum));

            seqNum++;
            ae.source = local;
            ae.dest = remote;
            
            try {
                ae.setSourceSession(this);
                ae.init();
                ae.go();
            } catch (AppiaEventException e) {
                e.printStackTrace();
            }
        }
        else {
            // Event is going UP
            System.out.print("[AMANAGER] AdaptationEvent : up");
        }
    }


    private void handleAdaptationResponseEvent(AdaptationResponseEvent ev) {

        System.out.println("[AMANAGER] A receber AdaptationResponseEvent : " + ev.getSeqNum() + "\n");

        List<ActionResponse> l =  ((List<ActionResponse>) ev.getMessage().popObject());
        System.out.println(l.get(0).getActionId() + " " + l.get(0).getReturnValue());
        
        try {
            ev.go();
        } catch (AppiaEventException e) {
            e.printStackTrace();
        }

    }


}

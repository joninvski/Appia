package net.sf.appia.core;

import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Map.Entry;

import net.sf.appia.adaptationmanager.events.ContextAnswerEvent;
import net.sf.appia.core.events.reconfiguration.AddServiceEvent;
import net.sf.appia.core.events.reconfiguration.BecomeQuiscentEvent;
import net.sf.appia.core.events.reconfiguration.CreateChannelEvent;
import net.sf.appia.core.events.reconfiguration.GetServiceStateEvent;
import net.sf.appia.core.events.reconfiguration.RecActionEvent;
import net.sf.appia.core.events.reconfiguration.RecActionResponseEvent;
import net.sf.appia.core.events.reconfiguration.RemoveServiceEvent;
import net.sf.appia.core.events.reconfiguration.ResumeChannelEvent;
import net.sf.appia.core.events.reconfiguration.SetServiceStateEvent;
import net.sf.appia.core.events.reconfiguration.SetValueEvent;
import net.sf.appia.core.events.reconfiguration.StartBufferingEvent;
import net.sf.appia.core.events.reconfiguration.StartChannelEvent;
import net.sf.appia.core.events.reconfiguration.StartServiceEvent;
import net.sf.appia.core.events.reconfiguration.StopBufferingEvent;
import net.sf.appia.core.events.reconfiguration.StopChannelEvent;
import net.sf.appia.core.events.reconfiguration.StopServiceEvent;
import net.sf.appia.core.reconfigurator.ReconfigureSession;
import net.sf.appia.core.reconfigurator.utils.Attribute;
import net.sf.appia.core.reconfigurator.utils.How;
import net.sf.appia.core.reconfigurator.utils.ValuedAttribute;
import net.sf.appia.core.reconfigurator.utils.Where;
import net.sf.appia.core.type.ChannelType;
import net.sf.appia.core.type.ServiceType;

//o X ou channel manager da lili
public class Interpreter{

    private Channel managerChannel;
    protected Hashtable<String, Channel> channelList = new Hashtable<String, Channel>();
    protected Hashtable<String, Session> sessionList = new Hashtable<String, Session>();

    public Interpreter() {
        super();
        // tem de ser ser feito o preenchimento da sessionList de acordo com as
        // QoS dos canais existentes
    }

     public void init() {

    }

    public void updateChannelList() {

        Iterator it = channelList.entrySet().iterator();

        while (it.hasNext()) {
            Entry<String, Channel> o = (Entry<String, Channel>) it.next();
            Channel ch = o.getValue();

            ChannelCursor c = ch.getCursor();
            c.top();
            Session s;
            try {
                                 
                                
                for (int i = 0; i < ch.getQoS().getLayers().length; i++) {
                    s = c.getSession();
                    sessionList.put(s.getLayer().getClass().getName(), s);
                    c.down();
                }

            } catch (AppiaCursorException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
      
        //Faz parte do monitor, para já não se usa
        /*ContextAnswerEvent sce = new ContextAnswerEvent();
        sce.setChannel(managerChannel);
        sce.setDir(Direction.DOWN);
        sce.setSourceSession(null);
        
        sce.setChannelList(channelList);
        sce.setSessionList(sessionList);
        
        //System.out.println("channelList " + channelList + " SessionList " + sessionList);
        
        //sce.getMessage().pushObject(channelList);
       // sce.getMessage().pushObject(sessionList);
        
        
        
        try {
            sce.init();
            sce.go();
        } catch (AppiaEventException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
       
        */

    }

    public void updateChannelList(Channel ch) {
        channelList.put(ch.getChannelID(), ch);

        if(ch.getChannelID().equals("manager_c"))
            managerChannel = ch;
  
    }

    private Hashtable<String, Channel> getChannelByType(ChannelType t) {
        return null;
    }

    private void updateSessionList() {

    }

    private Hashtable<String, Channel> getSessionByType(ServiceType t) {
        return null;
    }

    /**
     * 
     * @param event
     */
    public void handle(RecActionEvent event) {
        updateChannelList();
       
        /*
         * SetValueEvent
         */
        if (event instanceof SetValueEvent) {
            System.out.println("[INTERPRETER] A receber SetValueEvent : " + event.getActionId());
            handleSetValueEvent((SetValueEvent) event);
        }
        
        /*
         * AddServiceEvent
         */
        if (event instanceof AddServiceEvent) {

        }

        /*
         * RemoveServiceEvent
         */
        if (event instanceof RemoveServiceEvent) {

        }

        /*
         * StartBufferingEvent
         */
        if (event instanceof StartBufferingEvent) {

        }

        /*
         * StopBufferingEvent
         */
        if (event instanceof StopBufferingEvent) {

        }

        /*
         * BecomeQuiscentEvent
         */
        if (event instanceof BecomeQuiscentEvent) {

        }

        /*
         * ResumeChannelEvent
         */
        if (event instanceof ResumeChannelEvent) {

        }

        /*
         * GetServiceStateEvent
         */
        if (event instanceof GetServiceStateEvent) {

        }

        /*
         * SetServiceStateEvent
         */
        if (event instanceof SetServiceStateEvent) {

        }

        /*
         * StartServiceEvent
         */
        if (event instanceof StartServiceEvent) {

        }

        /*
         * StopServiceEvent
         */
        if (event instanceof StopServiceEvent) {

        }

        /*
         * StartChannelEvent
         */
        if (event instanceof StartChannelEvent) {

        }

        /*
         * StopChannelEvent
         */
        if (event instanceof StopChannelEvent) {

        }

        /*
         * CreateChannelEvent
         */
        if (event instanceof CreateChannelEvent) {

        }
    }

    /**
     * Sets the value of a specific parameter in a session
     * @param event
     */
    private void handleSetValueEvent(SetValueEvent event) {
        
        // Retirar os parâmetros do Evento
        Session reconfigSession = sessionList.get(event.getWhere()
                .getServiceType().getName());

        // Response Event
        RecActionResponseEvent rre = new RecActionResponseEvent();
        rre.setChannel(event.getChannel());
        rre.setSourceSession(event.getSourceSession());
        rre.setDir(Direction.DOWN);
        rre.setActionId(event.getActionId());
        rre.setReconfigId(event.getReconfigId());

        try {
          
            Object[] params = event.getParameters();
           // System.out.println("Event parameters " + params[0] + params[1]);
           
            //o atributo contém o nome e o valor da variável a alterar
            ValuedAttribute t = new ValuedAttribute(new Attribute(
                    (String) params[0], params[1].getClass()), params[1]);
        
            //Ver se o valor está na lista de parâmetros adaptáveis da session
            Attribute[] adaptableParams = ((ReconfigurableSession) reconfigSession).getLayer().addaptableParams;
            
            if(adaptableParams.length == 0){
                //System.out.println("O parâmetro não é adaptável!!");
                rre.setReturnType(false);
                sendRecActionResponseEvent(rre);
                return;
            }
               
            for(Attribute a : adaptableParams){
                if(a.getName().equals(params[0])){
                   // System.out.println("O parâmetro " + a.getName() + " é adaptável!!");
                }
                else{
                    rre.setReturnType(false);
                    sendRecActionResponseEvent(rre);
                   // System.out.println("O parâmetro " + a.getName() + " não é adaptável!!");
                }
            }
            
            try {
                Field f = reconfigSession.getClass().getDeclaredField(
                        t.getAttribute().getName());
                f.setAccessible(true);
                f.set(reconfigSession, t.getValue());
                rre.setReturnType(true);

            } catch (SecurityException e) {
                rre.setReturnType(false);
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                rre.setReturnType(false);
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                rre.setReturnType(false);
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                rre.setReturnType(false);
                e.printStackTrace();
            }

            System.out.println("[INTERPRETER] A enviar RecActionResponseEvent : " + rre.getReturnType());

            sendRecActionResponseEvent(rre);
            
        } catch (SecurityException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void sendRecActionResponseEvent(RecActionResponseEvent rre){
        try {
            rre.init();
            rre.go();
        } catch (AppiaEventException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    
   
}

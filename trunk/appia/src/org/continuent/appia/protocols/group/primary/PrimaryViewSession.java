package org.continuent.appia.protocols.group.primary;

import org.apache.log4j.Logger;
import org.continuent.appia.core.AppiaEventException;
import org.continuent.appia.core.Channel;
import org.continuent.appia.core.Direction;
import org.continuent.appia.core.Event;
import org.continuent.appia.core.Layer;
import org.continuent.appia.core.Session;
import org.continuent.appia.protocols.group.Endpt;
import org.continuent.appia.protocols.group.LocalState;
import org.continuent.appia.protocols.group.ViewState;
import org.continuent.appia.protocols.group.intra.View;
import org.continuent.appia.protocols.group.leave.LeaveEvent;
import org.continuent.appia.protocols.group.sync.BlockOk;
import org.continuent.appia.xml.interfaces.InitializableSession;
import org.continuent.appia.xml.utils.SessionProperties;

/**
 * 
 * This session implements a Primary View protocol. Processes only receive
 * views when in a primary partition. To bootstrap the primary partition construction
 * one process must be defined has <i>primary</i>. From that point forward, new
 * processes can be added to a primary partition. Primary views are defined by a
 * majority of members from the previous <b>primary</b> view.
 * 
 * @author José Mocito</a>
 * @version 1.0
 */
public class PrimaryViewSession extends Session implements InitializableSession {

    private static Logger log = Logger.getLogger(PrimaryViewSession.class);
    
    private boolean blocked;
    private View view, lastPrimaryView;
    private ViewState vs, vs_old;
    private LocalState ls;
    private boolean primaryProcess;
    private boolean isPrimary;
    private boolean wasPrimary;
    private long ackCount;
    int primaryCounter;
    int[] newMembers;
    boolean newMembersState[];
    
    public PrimaryViewSession(Layer layer) {
        super(layer);
    }

    public void init(SessionProperties params) {
        if (params.containsKey("primary")) {
            if (params.getBoolean("primary"))
                primaryProcess = true;
        }
    }
    public void handle(Event event) {
        if (event instanceof View)
            handleView((View) event);
        else if (event instanceof BlockOk)
            handleBlockOk((BlockOk) event);
        else if (event instanceof ProbeEvent)
            handleProbeEvent((ProbeEvent) event);
        else if (event instanceof DeliverViewEvent)
            handleDeliverViewEvent((DeliverViewEvent) event);
        else if (event instanceof KickEvent)
            handleKickEvent((KickEvent) event);
        else {
            if (log.isDebugEnabled())
                log.error("Received unexpected event: "+event);
            
            try {
                event.go();
            } catch (AppiaEventException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleView(View view) {
        blocked = false;
        this.view = view;
        if (vs == null) {
            vs = view.vs;
            ls = view.ls;

            if (log.isInfoEnabled()) {
                String viewStr = "Received first View:\n";
                for (int i = 0; i < view.vs.view.length; i++)
                    viewStr += view.vs.view[i] + "\n";
                log.info(viewStr);
            }

            if (primaryProcess) {
                isPrimary = true;
               deliverView();
            }
        }
        else {
            ackCount = 0;
            
            vs_old = vs;
            vs = view.vs;
            ls = view.ls;

            if (log.isInfoEnabled()) {
                String viewStr = "Received new View:\n";
                for (int i = 0; i < view.vs.view.length; i++)
                    viewStr += view.vs.view[i] + "\n";
                log.info(viewStr);
            }

            if (isPrimary) {
                log.info("My last view was primary");
                Endpt[] survivingMembers = vs.getSurvivingMembers(vs_old);
                if (survivingMembers.length >= vs_old.view.length / 2 + 1) {
                    // Is primary view = Has majority of members from previous view
                    log.info("I'm still on a primary view");
                    if (survivingMembers.length < vs.view.length) {
                        // There are new members, hold view
                        Endpt[] newMembersEndpts = vs.getNewMembers(vs_old);
                        newMembers = new int[newMembersEndpts.length];
                        for (int i = 0; i < newMembers.length; i++)
                            newMembers[i] = vs.getRank(newMembersEndpts[i]);
                        newMembersState = new boolean[newMembers.length];
                        log.info("There are "+newMembers.length+" new members");
                    }
                    else {
                        // Deliver view
                        deliverView();
                    }                 
                }
                else {
                    // Left the primary partition...
                    log.info("Left the primary partition");
                    isPrimary = false;
                    wasPrimary = true;
                }
            }
            else if (!isPrimary || wasPrimary) {
                // Hold view and send Probe
                try {
                    ProbeEvent event = new ProbeEvent(view.getChannel(), Direction.DOWN, this, vs.group, vs.id);
                    event.getMessage().pushInt(primaryCounter);
                    event.getMessage().pushBoolean(wasPrimary);
                    event.go();
                } catch (AppiaEventException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private void handleBlockOk(BlockOk ok) {
        log.info("Received BlockOk");
        blocked = true;
        try {
            ok.go();
        } catch (AppiaEventException e) {
            e.printStackTrace();
        }
    }
    private void handleProbeEvent(ProbeEvent event) {
        log.info("Received ProbeEvent");
        if (isPrimary) {
            // Last view was primary
            if (event.getMessage().popBoolean()) {
                // Peer was in a primary partition at some point
                if (ls.my_rank == vs.getRank(vs.getSurvivingMembers(vs_old)[0])) {
                    // Process has the lowest rank from the surviving members. Kick peer! 
                    kick(event.getChannel(), event.orig);
                }
                
            }
            else if (++ackCount == newMembers.length) {
                // New peers were never in a primary partition and all new members probed
                if (ls.my_rank == vs.getRank(vs.getSurvivingMembers(vs_old)[0])) {
                    // Process has the lowest rank from the surviving members. Order view delivery!
                    try {
                        DeliverViewEvent deliver = new DeliverViewEvent(event.getChannel(), Direction.DOWN, this, vs.group, vs.id);
                        deliver.dest = newMembers;
                        deliver.getMessage().pushInt(primaryCounter);
                        deliver.go();
                    } catch (AppiaEventException e) {
                        e.printStackTrace();
                    }
                }
                // All primary processes can deliver their views
                deliverView();
            }   
        }
        else {
            // Last view was not primary
            if (wasPrimary) {
                // Process was in a primary partition at some point
                if (event.getMessage().popBoolean()) {
                    // Peer was also in a primary partition at some point
                    int peerPrimaryCounter = event.getMessage().popInt();
                    // Check primary view counter
                    if (peerPrimaryCounter > primaryCounter)
                        leave(event.getChannel());
                    else if (peerPrimaryCounter < primaryCounter)
                        ackCount--;
                }
                else {
                    // Peer was never in a primary partition. New peers can only join
                    // primary partitions. KICK!
                    kick(event.getChannel(), event.orig);
                }
            }
            
            if (++ackCount == vs.view.length && hasMajority(view, lastPrimaryView))
                deliverView();
        }
    }

    private void handleKickEvent(KickEvent kick) {
        log.info("Received KickEvent");
        leave(kick.getChannel());
    }
    
    private void handleDeliverViewEvent(DeliverViewEvent deliver) {
        log.info("Received DeliverViewEvent");
        primaryCounter = deliver.getMessage().popInt();
        deliverView();
    }
    
    private boolean hasMajority(View v1, View v2) {
        if (v1.vs.getSurvivingMembers(v2.vs).length >= v2.vs.view.length / 2 + 1)
            return true;
        else
            return false;
    }
    
    private void deliverView() {
        log.info("Delivering Primary View");
        lastPrimaryView = this.view;
        isPrimary = true;
        wasPrimary = false;
        try {
            this.view.go();
        } catch (AppiaEventException e) {
            e.printStackTrace();
        }
        primaryCounter++;
    }
    
    private void kick(Channel ch, int dest) {
        log.info("Kicking process "+dest+"...");
        try {
            KickEvent kick = new KickEvent(ch, Direction.DOWN, this, vs.group, vs.id);
            kick.dest = new int[] {dest};
            kick.go();
        } catch (AppiaEventException e) {
            e.printStackTrace();
        }
    }
    
    private void leave(Channel ch) {
        log.info("Leaving group...");
        try {
            LeaveEvent leave = new LeaveEvent(ch, Direction.DOWN, this, vs.group, vs.id);
            leave.go();
        } catch (AppiaEventException e) {
            e.printStackTrace();
        }
    }
}

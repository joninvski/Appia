package net.sf.appia.project.group.exampleApp;

import java.net.InetSocketAddress;
import java.util.Dictionary;
import java.util.Hashtable;

import net.sf.appia.core.AppiaEventException;
import net.sf.appia.core.Channel;
import net.sf.appia.core.Direction;
import net.sf.appia.core.Event;
import net.sf.appia.core.Session;
import net.sf.appia.core.events.channel.ChannelInit;
import net.sf.appia.protocols.common.RegisterSocketEvent;
import net.sf.appia.protocols.group.AppiaGroupException;
import net.sf.appia.protocols.group.Endpt;
import net.sf.appia.protocols.group.Group;
import net.sf.appia.protocols.group.ViewID;
import net.sf.appia.protocols.group.ViewState;
import net.sf.appia.protocols.group.events.GroupInit;
import net.sf.appia.protocols.group.intra.View;
import net.sf.appia.protocols.group.leave.LeaveEvent;
import net.sf.appia.protocols.group.sync.BlockOk;
import net.sf.appia.xml.interfaces.InitializableSession;
import net.sf.appia.xml.utils.SessionProperties;

public class ExampleClientSession extends Session implements InitializableSession {
	public Channel clientChannel;

	private InetSocketAddress localAddress;
	private int localPort;
	
	//More for demo purposes
	String username;

	//Where we store our groups
	Dictionary<String, JoinedGroup> myGroups = new Hashtable<String, JoinedGroup>();
	
	/**
	 * Creates a new ExampleClientSession.
	 * @param l
	 */
	public ExampleClientSession(ExampleClientLayer l) {
		super(l);
	}

	public void init(SessionProperties params) {
		this.username = params.getProperty("username");
		this.localPort = Integer.parseInt(params.getProperty("localport"));
		System.out.println("username: " + username);
	}

	/**
	 * Main event handler.
	 * @param ev the event to handle.
	 * 
	 * @see net.sf.appia.core.Session#handle(net.sf.appia.core.Event)
	 */
	public void handle(Event ev) {
		System.out.println("Example client session - "+ ev.getDir() 
				+" - Received an event type - " + ev.getClass());

		try {
			if (ev instanceof ChannelInit)
				handleChannelInit((ChannelInit) ev);
			else if (ev instanceof RegisterSocketEvent)
				handleRSE((RegisterSocketEvent) ev);
			else if (ev instanceof View)
				handleView((View) ev);
			else if (ev instanceof TextEvent)
				handleTextEvent((TextEvent) ev);
			else if (ev instanceof BlockOk)
				handleBlockOk((BlockOk) ev);
			else{
				System.out.println("ExampleClientSession Session: Event not treaded - " + ev.getClass());
				ev.go();
			}
		} 
		catch (AppiaEventException e) {
			e.printStackTrace();
		}
	}

	private void handleBlockOk(BlockOk ok) throws AppiaEventException {
		System.out.println("The client is being asked to shut up on group: " + ok.group.id);

		JoinedGroup group = myGroups.get(ok.group.id);
		group.setMute(true);
		
		ok.go();
	}

	private void handleTextEvent(TextEvent event) throws AppiaEventException {
		if(event.getDir() == Direction.UP){
			//event.loadMessage(); //TODO -Strangely I do not need to load here :S
						
			System.out.println("[" + event.getUsername() + "] has sent me: " + event.getUserMessage());
		}
		
		event.go();
	}

	private void handleView(View view) {
		for (int i = 0; i < view.vs.addresses.length; i++)
			System.out.println(
					"{"
					+ ((InetSocketAddress)view.vs.addresses[i]).getAddress().getHostAddress()
					+ ":"
					+ ((InetSocketAddress)view.vs.addresses[i]).getPort()
					+ "} ");
		JoinedGroup myGroup = myGroups.get(view.group.id);
		if(view == null){
			System.out.println("Received a view and I shouldnt receive");
		}
		//TODO - Something in this example client needs to be fixed
		if(myGroup == null){
			return;
		}
			
				
		myGroup.setPresentViewState(view.vs);
	}

	private void handleChannelInit(ChannelInit init) throws AppiaEventException {
		clientChannel = init.getChannel();

		init.go();
		new RegisterSocketEvent(clientChannel,Direction.DOWN,this,localPort).go();
	}


	private void handleRSE(RegisterSocketEvent event) throws AppiaEventException {
		if(event.getDir() == Direction.DOWN){
			System.out.println("No one should ask me to register sockets");
			System.exit(-1);
		}

		if(event.error){
			System.err.println("Error on the RegisterSocketEvent!!! ");
			System.err.println("Reason: " + event.getErrorDescription());
			System.exit(-1);
		}

		//Cool the socket was created
		localAddress = new InetSocketAddress(event.localHost,event.port);
		System.out.println("My local address is: " +  localAddress);

		event.go();

		//And I can now launch the shell to accept user commands
		startShell();
	}

	private void startShell() {
		GroupShell shell = new GroupShell(clientChannel, this);
		final Thread t = clientChannel.getThreadFactory().newThread(shell);
		t.setName("Client shell");
		t.start();
	}

	protected void sendGroupInit(String groupName) {
		try {
			System.out.println("SendGroupInit");
			
			//Check if I'm already in the group
			if(myGroups.get(groupName) != null) {
				System.out.println("I'm already on the group: " + groupName);
				return;
			}
				
			//Prepare the various fields to create the group init event
			Group myGroup = new Group(groupName);
			Endpt myEndpt=new Endpt(username+"@"+localAddress.toString()); 

			Endpt[] view=null;
			InetSocketAddress[] addrs=null;

			addrs=new InetSocketAddress[1];
			addrs[0]=localAddress;
			view=new Endpt[1];
			view[0]=myEndpt;

			ViewState currentView = new ViewState("1", myGroup, new ViewID(0,view[0]), new ViewID[0], view, addrs); //TODO -also

			//Finally we can create the group init event
			GroupInit gi =
				new GroupInit(currentView,myEndpt,null, null,clientChannel,Direction.DOWN,this);
			gi.asyncGo(clientChannel, Direction.DOWN);
			
			JoinedGroup joinedGroup = new JoinedGroup(myGroup, myEndpt, currentView);
			myGroups.put(groupName, joinedGroup);
			
		} catch (AppiaEventException ex) {
			System.err.println("EventException while launching GroupInit");
			System.err.println(ex);
			System.exit(-1);
		} catch (NullPointerException ex) {
			System.err.println("EventException while launching GroupInit");
			System.err.println(ex);
			System.exit(-1);
		} catch (AppiaGroupException ex) {
			System.err.println("EventException while launching GroupInit");
			System.exit(-1);
			System.err.println(ex);
		} 
	}

	public void sendLeaveEvent(String groupName) throws AppiaEventException {
		JoinedGroup group = myGroups.get(groupName);
		
		if(group == null){
			System.out.println("I'm not in the group: " + groupName);
			return;
		}
		
		myGroups.remove(groupName);
		
		LeaveEvent leave = new LeaveEvent(this.clientChannel, Direction.DOWN, this,
				group.getGroup(), group.getPresentViewState().id);
	
		leave.asyncGo(clientChannel, Direction.DOWN);
	}

	public void viewCurrentView(String groupName) {
		JoinedGroup group = myGroups.get(groupName);
		
		if(group == null){
			System.out.println("I'm not in the group: " + groupName);
			return;
		}		

		System.out.println("Current View \n: " + group.getPresentViewState());
	}


	public void sendText(String text, String groupId)  {
		TextEvent textEvent;
		try {
			JoinedGroup jgroup = myGroups.get(groupId);
			
			if(jgroup == null){
				System.out.println("I'm not in the group: " + groupId);
				return;
			}
						
			textEvent = new TextEvent(clientChannel, Direction.DOWN, this, jgroup.group);
		} catch (AppiaEventException e) {
			e.printStackTrace();
			return;
		}
		
		textEvent.setUserMessage(text);
		textEvent.setUsername(username);
		
		textEvent.storeMessage();
		
		try {
			textEvent.asyncGo(clientChannel, Direction.DOWN);
		} catch (AppiaEventException e) {
			e.printStackTrace();
			return;
		}
	}
	
	public class JoinedGroup {
		private Group group;
		private Endpt myEndpt;
		private ViewState presentViewState;

		private boolean mute = false;

		public JoinedGroup(Group group, Endpt myEndpt, ViewState presentViewState){
			this.group = group;
			this.myEndpt = myEndpt;
			this.presentViewState = presentViewState;
		}
		
		public Group getGroup() {
			return group;
		}
		public void setGroup(Group group) {
			this.group = group;
		}
		public Endpt getMyEndpt() {
			return myEndpt;
		}
		public void setMyEndpt(Endpt myEndpt) {
			this.myEndpt = myEndpt;
		}
		public ViewState getPresentViewState() {
			return presentViewState;
		}
		public void setPresentViewState(ViewState presentView) {
			this.presentViewState = presentView;
		}

		public boolean getMute() {
			return mute;
		}
		public void setMute(boolean mute) {
			this.mute = mute;
		}
	}
}

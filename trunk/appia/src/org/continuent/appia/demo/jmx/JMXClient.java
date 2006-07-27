package org.continuent.appia.demo.jmx;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.continuent.appia.core.Channel;
import org.continuent.appia.management.jmx.ChannelManagerMBean;
import org.continuent.appia.protocols.group.suspect.SuspectSession;

/**
 * This example shows the simplest way to connect to a JSR 160 connector server in Appia.
 * To connect, the user needs the address of the Appia connector server. 
 * This address is generated by Appia, and must be known to this client.
 * When using JSR 160's RMI connector server, this information is often in form of a
 * JNDI name where the RMI stub has been registered; in this case the client needs
 * to know the host and port of the JNDI server and the JNDI path where the stub is
 * registered.
 *
 * @version 1.0
 * @author <a href="mailto:nunomrc@di.fc.ul.pt">Nuno Carvalho</a>
 */    
public class JMXClient {
    
    private JMXClient(){}
    
    public static void main(String[] args) throws Exception {
        
        // The RMI server's host: this is actually ignored by JSR 160
        // since this information is stored in the RMI stub.
        final String serverHost = "host";
        // The host, port and path where the rmiregistry runs.
        final String namingHost = "localhost";
        final int namingPort = 1099;
        final JMXServiceURL url = new JMXServiceURL("service:jmx:rmi://" + serverHost + "/jndi/rmi://" 
                + namingHost + ":" + namingPort + "/appia");
        
        // Connect a JSR 160 JMXConnector to the server side
        final JMXConnector connector = JMXConnectorFactory.connect(url);        
        // Retrieve an MBeanServerConnection that represent the MBeanServer the remote
        // connector server is bound to
        final MBeanServerConnection connection = connector.getMBeanServerConnection();
        
        final ObjectName delegateName = ObjectName.getInstance(Channel.class.getName()+":"+"name=Perf Channel");
        final Object proxy = MBeanServerInvocationHandler.newProxyInstance(connection, delegateName, ChannelManagerMBean.class, true);
        final ChannelManagerMBean bean = (ChannelManagerMBean) proxy;
        final String sessionID = SuspectSession.class.getName()+":Perf Channel";
        
        // Make some example calls to the MBean
        final Long previous_time = new Long(bean.getParameter("suspect_sweep",sessionID));
        System.out.println("suspect_sweep = " + previous_time.longValue());
        System.out.println("Changing suspect time...");
        bean.setParameter("suspect_sweep",""+(previous_time.longValue()+1000),SuspectSession.class.getName()+":Perf Channel");
        System.out.println("suspect_sweep = " + bean.getParameter("suspect_sweep",sessionID));
    }
}
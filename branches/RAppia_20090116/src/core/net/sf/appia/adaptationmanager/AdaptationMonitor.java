package net.sf.appia.adaptationmanager;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import net.sf.appia.adaptationmanager.events.ContextQueryEvent;
import net.sf.appia.core.AppiaEventException;
import net.sf.appia.core.Channel;
import net.sf.appia.core.Direction;
import net.sf.appia.core.Layer;
import net.sf.appia.core.Session;

/**
 * 
 * This class defines a AdaptationMonitor
 * 
 * @author <a href="mailto:cfonseca@gsd.inesc-id.pt">Cristina Fonseca</a>
 * @version 1.0
 */
public class AdaptationMonitor extends JFrame implements KeyListener {

    //the session that calls the adaptation monitor. important to send events requesting info about the stack
    private AdaptationManagerSession amsession;
    Channel channel;

    private String pressedKey = "";
    private JTextArea textArea;
    private JFrame frame;
    private String sessions;
    private int sessionsCount;
    String[] alpha = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
    private List<String> sessionsList;

    public AdaptationMonitor(AdaptationManagerSession s, Channel ch){
        frame = new JFrame("Adaptation Manager Monitor" );
        channel = ch;
        amsession = s;

        textArea = new JTextArea( 25, 20 ); // configura JTextArea
        textArea.setText( "Press any key on the keyboard..." );
        textArea.setEnabled( false ); // desativa textarea
        textArea.setDisabledTextColor( Color.BLACK ); // configura cor de texto
        add( textArea ); // adiciona textarea ao JFrame

        addKeyListener(this); // permite que o frame processe os eventos de teclado

    }

    public void keyPressed(KeyEvent event) {

        pressedKey = String.format( "%s", 
                event.getKeyText( event.getKeyCode() )); // gera saída de tecla pressionada
        processPressedKey( event ); // configura a saída das linhas dois e três 

    }


    private void processPressedKey( KeyEvent event )
    {
        String str = "PressedKey: " + pressedKey;  
        textArea.setText(textArea.getText() + "\n" + str);


        if(pressedKey.compareToIgnoreCase(alpha[sessionsCount]) > 0){
            textArea.setDisabledTextColor( Color.BLUE );
            textArea.setText(textArea.getText() + " \n" + "The id does not correspond to a session!!");
        }else{
            textArea.setDisabledTextColor( Color.BLACK );
            textArea.setText(textArea.getText() + " \n" + "I will show you info about the service...!!");

            System.out.println(pressedKey.toLowerCase());
            int i=0;
            while(!(alpha[i].toLowerCase().equals(pressedKey.toLowerCase()))){
                i++;
                System.out.println("______ " + alpha[i].toLowerCase());
            }

            ContextQueryEvent e = new ContextQueryEvent();
            e.setId(sessionsList.get(i));
            e.setDir(Direction.DOWN);
            e.setChannel(channel);
            e.getMessage().pushString(e.getId());
            e.setSourceSession(amsession);
            try {
                e.asyncGo(channel, Direction.DOWN);
            } catch (AppiaEventException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            //channel.handle(e);
        }
    } 


    public void printStack(Iterator<String> it){
        String welcome = new String("Press the layer id to see more info... \n\n");
        sessions = new String();
        sessionsList = new ArrayList<String>();
        String aux= new String();
        int i = 0;


        while(it.hasNext()){
            aux = it.next();
            sessionsList.add(aux);
            sessions = sessions + "\n" +alpha[i] + " - " + aux; 
            i++;
        }

        sessionsCount = i;
        System.out.println("SESSIONS " + sessions);
        textArea.setText(welcome+sessions);
        // textArea.setText(welcome);
    }

    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub     
    }

    public void keyTyped(KeyEvent e) {
        // TODO Auto-generated method stub
    }


}

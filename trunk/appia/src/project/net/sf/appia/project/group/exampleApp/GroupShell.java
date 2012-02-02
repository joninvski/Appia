/**
 * Appia: Group communication and protocol composition framework library
 * Copyright 2006 University of Lisbon
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 *
 * Initial developer(s): Alexandre Pinto and Hugo Miranda.
 * Contributor(s): See Appia web page for a list of contributors.
 */
/*
 * Created on Mar 16, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package net.sf.appia.project.group.exampleApp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import net.sf.appia.core.AppiaException;
import net.sf.appia.core.Channel;
import net.sf.appia.core.Direction;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class defines a GroupShell for the example app
 * to use.
 * 
 * @author João Trindade
 * @version 1.0
 */
public class GroupShell implements Runnable {

    private Channel channel;
    private ExampleClientSession session;

    /**
     * Creates a new MyShell.
     * @param ch
     */
    public GroupShell(Channel upperChannel, ExampleClientSession session) {
        this.channel = upperChannel;
        this.session = session;
    }

    /**
     * Execution of the thread.
     * @see java.lang.Runnable#run()
     */
    public void run() {
        boolean dontExit = true;
        while(dontExit) {
            System.out.print("> ");

            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

            String str = "";
            try {
                str = in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Dictionary<String, Pattern> options = defineOptions();

            try{

                if (matches(options.get("help"), str)){
                    printsHelp(options);
                }

                else if (matches(options.get("group"), str)){
                    System.out.println("Group cmd choosen");
                    String groupName = getGroupName(str);
                    session.sendGroupInit(groupName);
                }

                else if (matches(options.get("send"), str)){
                    System.out.println("Send cmd choosen");
                    TextEvent event = new TextEvent();

                    String text = getText(str);
                    event.setUserMessage(text);
                    event.getMessage().pushString(text);

                    event.asyncGo(channel,Direction.DOWN);
                }

                else if (matches(options.get("leave"), str)){
                    System.out.println("Leave cmd choosen");
                    String groupName = getGroupLeaveName(str);
                    session.sendLeaveEvent(groupName);
                }
                
                else if (matches(options.get("view"), str)){
                    System.out.println("View cmd choosen");
                    String groupName = getGroupView(str);
                    session.viewCurrentView(groupName);
                }
                
                else if (matches(options.get("text"), str)){
                    System.out.println("Text cmd choosen");
                    String groupId = getTextGroupId(str);
                    String text = getText(str);
                    session.sendText(text, groupId);
                }
                
                else {
                    System.out.println("No command match found");
                    printsHelp(options);
                }
            }
            catch (AppiaException e){
                e.printStackTrace();
            }

        }
    }

    private String getTextGroupId(String str) {
            return str.split(" ")[1];
    }

	private String getGroupView(String str) {
        return str.replaceFirst("(view|v) ", "");	
    }
    
    private String getGroupName(String str) {
        return str.replaceFirst("(group|g) ", "");	
    }
    
    private String getGroupLeaveName(String str) {
        return str.replaceFirst("(leave|l) ", "");	
    }

    private String getText(String str) {
        return str.replaceFirst("(send|s) ", "");	
    }

    private void printsHelp(Dictionary<String, Pattern> options) {
        System.out.println("Help cmd choosen");
        System.out.println("Other options are:");
        System.out.println(options.get("group").pattern() +  " - to join a group");
        System.out.println(options.get("send").pattern() +  " - to send a group");
        System.out.println(options.get("leave").pattern() +  " - to leave a group");
        System.out.println(options.get("view").pattern() +  " - to view the view state of a group");
        System.out.println(options.get("text").pattern() +  " - to send a text message");
    }

    private boolean matches(Pattern pattern, String str) {
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    private Dictionary<String, Pattern> defineOptions(){
        Dictionary<String, Pattern> options = new Hashtable<String, Pattern>();

        String rgString = "(h|help)";
        Pattern patternHelp = Pattern.compile(rgString);
        options.put("help", patternHelp);

        rgString = "(join|group|change|g) ([a-z]|[A-Z])+";
        Pattern patternGroup = Pattern.compile(rgString);
        options.put("group", patternGroup);

        rgString = "(s|send) ([a-zA-Z] ?)*";
        Pattern patternSend = Pattern.compile(rgString);
        options.put("send", patternSend);
        
        rgString = "(l|leave) ([a-zA-Z] ?)*";
        Pattern patternLeave = Pattern.compile(rgString);
        options.put("leave", patternLeave);

        rgString = "(v|view) ([a-z]|[A-Z])+";
        Pattern patternView = Pattern.compile(rgString);
        options.put("view", patternView);
  
        rgString = "(t|text) ([a-z]|[A-Z])+ ([a-zA-Z] ?)*";
        Pattern patternText = Pattern.compile(rgString);
        options.put("text", patternText);
        
        return options;
    }
}
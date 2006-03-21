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
 package org.continuent.appia.protocols.fifo;

//////////////////////////////////////////////////////////////////////
//                                                                  //
// Appia: protocol development and composition framework            //
//                                                                  //
// Version: 1.0/J                                                   //
//                                                                  //
// Copyright, 2000, Universidade de Lisboa                          //
// All rights reserved                                              //
// See license.txt for further information                          //
//                                                                  //
// Class: Header                                                    //
//                                                                  //
// Author: Nuno Carvalho, 11/2001                                   //
//                                                                  //
// Change Log:                                                      //
//////////////////////////////////////////////////////////////////////

import org.continuent.appia.core.events.SendableEvent;
import org.continuent.appia.core.message.*;



/**
 * Class that implements the header of a message.
 * @see SendableEvent
 * @see FifoSession
 * @author Nuno Carvalho
 */
public class MulticastHeader {
    
    int numElements;
    private MsgBuffer[] msgBuf;
    private int indexHeader;

    public MulticastHeader(SendableEvent e, int myId) {
	Message msg = e.getMessage();
	MsgBuffer mbuf = new MsgBuffer();
	mbuf.len = 4;
	/* get number of elements */
	msg.pop(mbuf);
	numElements = getInt(mbuf);
	msgBuf = new MsgBuffer[numElements];
	indexHeader = -1;
	/* get all headers */
	for (int i=0; i<numElements; i++) {
	    msgBuf[i] = new MsgBuffer();
	    msgBuf[i].len = 12;
	    msg.pop(msgBuf[i]);
	    /* get index of my header */
	    if(getInt(msgBuf[i]) == myId)
		indexHeader = i;
	}
    }

    public MsgBuffer myHeader() {
	if(indexHeader == -1)
	    return null;
	else {
	    msgBuf[indexHeader].off+=4;
	    return msgBuf[indexHeader];
	}
    }

    public int getNumElems(){
	return numElements;
    }

    private int getInt(MsgBuffer mbuf) {
	int i=0;
	i |= (((int)mbuf.data[mbuf.off+0]) & 0xFF) << 24;
	i |= (((int)mbuf.data[mbuf.off+1]) & 0xFF) << 16;
	i |= (((int)mbuf.data[mbuf.off+2]) & 0xFF) <<  8;
	i |= (((int)mbuf.data[mbuf.off+3]) & 0xFF) <<  0;
	return i;
    }    
}

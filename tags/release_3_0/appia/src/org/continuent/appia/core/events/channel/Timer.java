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
 
/**
 * Title:        Appia<p>
 * Description:  Protocol development and composition framework<p>
 * Copyright:    Copyright (c) Alexandre Pinto & Hugo Miranda & Luis Rodrigues<p>
 * Company:      F.C.U.L.<p>
 * @author Alexandre Pinto & Hugo Miranda & Luis Rodrigues
 * @version 1.0
 */
package org.continuent.appia.core.events.channel;

import org.continuent.appia.core.*;

public class Timer extends ChannelEvent {

  /**
   * The timer unique Identification.
   */
  public String timerID;
  /**
   * <b>ATENTION: Changed sematic. 
   * It now stands for the time untill reception of the returning event</b>
   */
  protected long when;

  /**
   * Creates a uninitialized Timer Event.
   */
  public Timer() {
	  this.setPriority(200);
  }

  /**
   * Creates a initialized Timer Event.
   * Use {@linkplain Timer#Timer(long, String, Channel, int, Session, int)} instead.
   * 
   * @deprecated
   * @see Timer#Timer(long, String, Channel, int, Session, int)
   */
  public Timer(String timerID, long when,
               Channel channel, int dir, Session source,
               int qualifier)
    throws AppiaEventException, AppiaException {

    super(channel,dir,source,qualifier);

    this.timerID=timerID;
    this.setPriority(200);

    if ( when < 0 )
       throw new AppiaException("Timer: when is negative");

    this.when = when - channel.getTimeProvider().currentTimeMillis();
  }

  /**
   * Creates a initialized Timer Event.
   * 
   * @param when delta between now and the time that the timer will expire.
   * @param timerID ID of the timer
   * @param channel channel of the timer
   * @param dir Direction of the timer
   * @param source Session that creates the timer
   * @param qualifier Qualifier of the timer
   * @throws AppiaEventException
   * @throws AppiaException
   */
  public Timer(long when, String timerID,
               Channel channel, int dir, Session source,
               int qualifier)
    throws AppiaEventException, AppiaException {

    super(channel,dir,source,qualifier);

    this.timerID=timerID;
    this.setPriority(200);
    
    if ( when < 0 )
       throw new AppiaException("Timer: when is negative");

    this.when=when;
  }

  /**
   * Sets the time when the Timer event will be returned.
   *
   * @param when The time in milliseconds.
   */
  public void setTimeout(long when) throws AppiaException {
    if ( when < 0 )
       throw new AppiaException("Timer: when is negative");

    this.when=when;
  }

  /**
   * Gets the time when the Timer event will be returned.
   *
   * @return when The time in milliseconds.
   */
  public long getTimeout() {
    return when;
  }
  
  /**
   * Redefenition of Event.cloneEvent().
   */
  public Event cloneEvent() throws CloneNotSupportedException {
    return super.cloneEvent();
  }

}
/*
 * SpaceListener.java
 *
 * Created on 9 février 2002, 12:11
 */

package org.bmt.graviton.event;

import java.util.EventListener;

/**
 *
 * @author  bmt
 * @version
 */
public interface SpaceListener
extends
EventListener
{
    public void particlesMoved(SpaceEvent evt);
}

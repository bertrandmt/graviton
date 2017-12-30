/*
 * Space.java
 *
 * Created on 9 février 2002, 11:59
 */

package org.bmt.graviton;

import org.bmt.graviton.event.SpaceEvent;
import org.bmt.graviton.event.SpaceListener;

import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Iterator;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;

import javax.swing.event.EventListenerList;

/**
 *
 * @author  bmt
 * @version
 */
public class SpaceModel// implements Runnable
{
    Set particles;
    double elapsed;
    
    /** Creates new Space */
    public SpaceModel()
    {
        particles = new HashSet();
        elapsed = 0;
    }
    
    public void addParticle(ParticleModel p)
    {
        particles.add(p);
    }
    
    public void removeParticle(ParticleModel p)
    {
        particles.remove(p);
    }
    
    public void step(double dt, double width, double height)
    {
        elapsed += dt;
        
        for (Iterator i = particles.iterator();i.hasNext();)
        {
            ((ParticleModel)i.next()).accelerate(particles);
        }
        
        for (Iterator i = particles.iterator();i.hasNext();)
        {
            ((ParticleModel)i.next()).step(dt, width, height);
        }
    }
    
    public String toString()
    {
        StringBuffer b = new StringBuffer();
        
        b.append(elapsed)
         .append(",");
        
        for (Iterator i = particles.iterator(); i.hasNext(); ) b.append(i.next().toString()).append(",");
        
        return b.toString();
    }
}

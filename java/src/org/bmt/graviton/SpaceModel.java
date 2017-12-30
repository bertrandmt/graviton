/*
 * Space.java
 *
 * Created on 9 fevrier 2002, 11:59
 */

package org.bmt.graviton;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

/**
 *
 * @author  bmt
 * @version
 */
public class SpaceModel// implements Runnable
{
    Set<MassModel> particles;
    double elapsed;
    
    /** Creates new Space */
    public SpaceModel()
    {
        particles = new HashSet<MassModel>();
        elapsed = 0;
    }
    
    public void addMass(MassModel p)
    {
        particles.add(p);
    }
    
    public void removeMass(MassModel p)
    {
        particles.remove(p);
    }
    
    public void step(double dt, double width, double height)
    {
        elapsed += dt;
        
        for (Iterator i = particles.iterator();i.hasNext();)
        {
            ((MassModel)i.next()).accelerate(particles);
        }
        
        for (Iterator i = particles.iterator();i.hasNext();)
        {
            ((MassModel)i.next()).step(dt, width, height);
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

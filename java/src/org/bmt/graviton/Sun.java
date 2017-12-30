/*
 * Sun.java
 *
 * Created on 9 février 2002, 21:02
 */

package org.bmt.graviton;

import java.util.Set;
import java.awt.Color;

/**
 *
 * @author  bmt
 * @version
 */
public class Sun
extends
ParticleModel
{
    /** Creates new Sun */
    public Sun(double m, double r, double x, double y)
    {
        super(m, r, x, y);
    }
    
    public void accelerate(Set particles)
    {
    }
    
    public void step(double dt, double width, double height)
    {
    }
}

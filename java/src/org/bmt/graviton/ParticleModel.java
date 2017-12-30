/*
 * Particle.java
 *
 * Created on 9 février 2002, 21:03
 */
package org.bmt.graviton;

import java.util.Set;
import java.util.Map;

/**
 *
 * @author  bmt
 * @version
 */
public abstract class ParticleModel
{
    double x;
    double y;
    
    double vx;
    double vy;
    
    double ax;
    double ay;
    
    final double m;
    final double r;
    
    public static final double GRAVITATION_CONSTANT = 6.67259e-11;
    public static final double REPULSOR_MASS = -2000;
    
    public ParticleModel(double m, double r)
    {
        this(m, r, 0, 0, 0, 0);
    }
    
    public ParticleModel(double m, double r, double x, double y)
    {
        this(m, r, x, y, 0, 0);
    }
    
    public ParticleModel(double m, double r, double x, double y, double vx, double vy)
    {
        this.m = m;
        this.r = Math.max(r, 0.4/Space.SCALE);
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.ax = 0;
        this.ay = 0;
    }
    
    public abstract void accelerate(Set particles);
    public abstract void step(double dt, double width, double height);
    
    public String toString()
    {
        StringBuffer b = new StringBuffer();
        
        b.append(this.x)
         .append(",")
         .append(this.y)
         .append(",")
         .append(this.vx)
         .append(",")
         .append(this.vy)
         .append(",")
         .append(this.ax)
         .append(",")
         .append(this.ay);
        
        return b.toString();
    }
}

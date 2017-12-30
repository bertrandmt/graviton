/*
 * Mass.java
 *
 * Created on 9 fevrier 2002, 21:03
 */
package org.bmt.graviton;

import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author  bmt
 * @version
 */
public class MassModel
{
    double x;
    double y;
    
    double vx;
    double vy;
    
    double ax;
    double ay;
    
    final double m;
    final double r;
    
    static final boolean repulse_on = false;

    public static final double GRAVITATION_CONSTANT = 6.67259e-11;
    public static final double REPULSOR_MASS = -2000;
    
    public MassModel(double m, double r)
    {
        this(m, r, 0, 0, 0, 0);
    }
    
    public MassModel(double m, double r, double x, double y)
    {
        this(m, r, x, y, 0, 0);
    }
    
    public MassModel(double m, double r, double x, double y, double vx, double vy)
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
    
    static final double GRAV_ORDER = 2;
    static final double BOUNCE_ENERGY_GAIN = 0;
    
    public void accelerate(Set<MassModel> particles)
    {
        this.ax = this.ay = 0;
        for (Iterator i = particles.iterator(); i.hasNext();)
        {
            MassModel p = (MassModel)i.next();
            if (p==this) continue;
            
            double dx = p.x - this.x;
            double dy = p.y - this.y;
            double r2 = dx * dx + dy * dy;
            double r1 = Math.sqrt(r2);
            double r3 = Math.pow(r2, (1+GRAV_ORDER)/2);
            /*
            if (r1<(p.r+this.r))
            {
                // a) bounce : v = -(v.n)n + (v.t)t
                double v_dot_n =  vx*dx + vy*dy;
                double v_dot_t = -vx*dy + vy*dx;
                
                vx = (-v_dot_n*dx - v_dot_t*dy)/r2*(1+BOUNCE_ENERGY_GAIN);
                vy = (-v_dot_n*dy + v_dot_t*dx)/r2*(1+BOUNCE_ENERGY_GAIN);
                
                // b) reset r3
                r3 = r1 * Math.pow(p.r+this.r, GRAV_ORDER);
            }
            */
            // accelerate
            double m_over_r3 = p.m / r3 * GRAVITATION_CONSTANT;
            this.ax += m_over_r3 * dx;
            this.ay += m_over_r3 * dy;
        }
    }
    
    public void step(double dt, double width, double height)
    {
        if (repulse_on)
        {
            repulse(width, height);
        }
        
        this.vx += this.ax * dt;
        this.vy += this.ay * dt;
        
        this.x += this.vx * dt;
        this.y += this.vy * dt;
        /*
        if (this.x < -REPULSOR_DISTANCE / 2)
        {
            this.x = -REPULSOR_DISTANCE / 2;
            this.vx = -this.vx / 10;
        }
        if (this.x > 6400/*width* + REPULSOR_DISTANCE / 2)
        {
            this.x = 6400/*width* + REPULSOR_DISTANCE / 2;
            this.vx = -this.vx / 10;
        }
        if (this.y < -REPULSOR_DISTANCE / 2)
        {
            this.y = -REPULSOR_DISTANCE / 2;
            this.vy = -this.vy / 10;
        }
        if (this.y > 4800/*height* + REPULSOR_DISTANCE / 2)
        {
            this.y = 4800/*height*+ REPULSOR_DISTANCE / 2;
            this.vy = -this.vy / 10;
        }
        */
    }
    
    private static final double REPULSOR_DISTANCE = /*6*/0;
    
    private void repulse(double width, double height)
    {
        attract(REPULSOR_MASS , -REPULSOR_DISTANCE, y, 1);
        attract(REPULSOR_MASS , width + REPULSOR_DISTANCE, y, 1);
        attract(REPULSOR_MASS , x, -REPULSOR_DISTANCE, 1);
        attract(REPULSOR_MASS , x, height + REPULSOR_DISTANCE, 1);
    }
    
    private void attract(double m, double x, double y, double coef)
    {
        double delta_x = x - this.x;
        double delta_y = y - this.y;
        double r_cube = Math.pow(delta_x * delta_x + delta_y * delta_y, coef + 0.5);
        if (r_cube != 0)
        {
            double mass_over_r_cube = m / r_cube * GRAVITATION_CONSTANT;
            this.ax += mass_over_r_cube * delta_x;
            this.ay += mass_over_r_cube * delta_y;
        }
    }
    
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

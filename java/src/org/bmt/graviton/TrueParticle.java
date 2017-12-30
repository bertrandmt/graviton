/*
 * Particle.java
 *
 * Created on 9 février 2002, 11:38
 */

package org.bmt.graviton;

import java.util.Set;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author  bmt
 * @version
 */
public class TrueParticle
extends
ParticleModel
{
    static final boolean repulse_on = false;
    
    public TrueParticle(double m, double r)
    {
        super(m, r);
    }
    
    public TrueParticle(double m, double r, double x, double y)
    {
        super(m, r, x, y);
    }
    
    public TrueParticle(double m, double r, double x, double y, double vx, double vy)
    {
        super(m, r, x, y, vx, vy);
    }
    
    static final double GRAV_ORDER = 2;
    static final double BOUNCE_ENERGY_GAIN = 0;
    
    public void accelerate(Set particles)
    {
        this.ax = this.ay = 0;
        for (Iterator i = particles.iterator(); i.hasNext();)
        {
            ParticleModel p = (ParticleModel)i.next();
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
}
